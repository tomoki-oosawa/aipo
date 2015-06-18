/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.services.portletfactory;

//jetspeed stuff
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.BasePortletConfig;

import org.apache.jetspeed.portal.security.portlets.PortletWrapper;

import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.portletcache.PortletCache;
import org.apache.jetspeed.services.portletcache.Cacheable;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.util.MetaData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.rundata.JetspeedRunData;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.rundata.RunDataService;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.ServletConfig;

/**
 * Simple implementation of the PortalFactoryService.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: JetspeedPortletFactoryService.java,v 1.23 2004/02/23 03:36:42 jford Exp $
 */
public class JetspeedPortletFactoryService extends TurbineBaseService
    implements PortletFactoryService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedPortletFactoryService.class.getName());
    
    /** The default control to use when none is specified */
    private boolean enableCache = false;

    /** The JetspeedRunData Service. */
    private JetspeedRunDataService runDataService = null;
    
    /**
     * This is the early initialization method called by the 
     * Turbine <code>Service</code> framework
     */
    public void init( ServletConfig conf ) throws InitializationException
    {

        ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                     .getResources(PortletFactoryService.SERVICE_NAME);

        this.enableCache = serviceConf.getBoolean("enable.cache",true);
        
        // get the runData service
        this.runDataService =
            (JetspeedRunDataService)TurbineServices.getInstance()
                .getService(RunDataService.SERVICE_NAME);

        setInit(true);
    }
            
    /**
     * Given a PSML Entry return an instanciated Portlet.
     *
     * @param entry a PSML Entry describing a portlet
     * @param id the PSML entry's portlet id     
     * @return an instanciated portlet corresponding to this entry
     */
    public Portlet getPortlet( Entry entry ) throws PortletException
    {
        PortletEntry regEntry = (PortletEntry)Registry.getEntry(Registry.PORTLET, 
                                                                entry.getParent() );
        if (regEntry == null)
        {
            throw new PortletException("PortletFactory: unknown portlet entry in Registry: "+entry.getParent());
        }
        
        if (PortletEntry.TYPE_ABSTRACT.equals(regEntry.getType()))
        {
            throw new PortletException("PortletFactory: can't instanciate abstract registry entry: "+regEntry.getName());
        }
            
        PortletConfig pc = getPortletConfig(regEntry, entry.getId());

        // Set portlet config with values from PSML Entry
        pc.getInitParameters().putAll(getParameters(entry));
        pc.setPortletSkin( PortalToolkit.getSkin( entry.getSkin() ) );
        pc.setSecurityRef( getSecurityReference(entry, regEntry));

        return getPortlet( getClassname(regEntry), pc, entry.getId() );
    }

    /**
     * Given a Portlet registry entry name, instanciate it
     *
     * @param name the name of a portlet in the registry
     * @return an instanciated portlet corresponding to this entry
     */
    public Portlet getPortlet( String name, String id ) throws PortletException
    {
        PortletEntry regEntry = (PortletEntry)Registry.getEntry(Registry.PORTLET, name );

        if (regEntry == null)
        {
            throw new PortletException("PortletFactory: unknown portlet entry in Registry: "+name);
        }
        
        if (PortletEntry.TYPE_ABSTRACT.equals(regEntry.getType()))
        {
            throw new PortletException("PortletFactory: can't instanciate abstract registry entry: "+name);
        }
            
        PortletConfig pc = getPortletConfig(regEntry, id);
        
        return getPortlet( getClassname(regEntry), pc, null );
    }
    
    /** 
     * Instanciates or retrieve from memory cache a portlet corresponding to the 
     * passed parameters
     *
     * @param classname the classname of the portlet to instanciate
     * @param pc the PortletConfig object to be associated with this object
     * @param id the PSML entry's portlet id
     * @return the Portlet created or retrieve from cache
     */
    protected Portlet getPortlet( String classname, PortletConfig pc, String id )
        throws PortletException
    {

        //record the begining of the portlet creation
        long begin = System.currentTimeMillis();

        Portlet portlet = null;
        Class portletClass = null;
        String handle = null;
        
        try
        {
            portletClass = Class.forName(classname);
        }
        catch (Exception e)
        {
            throw new PortletException( "PortletFactory: Unable to load class " + classname );
        }
        
        if (enableCache)
        {
            try
            {
                // try to invoke a static getHandle() for this class
                Class[] signatureParams = { Object.class };
                Object[] methodParams = { pc };
                handle = (String)portletClass.getMethod("getHandle",signatureParams)
                                             .invoke(null,methodParams);
                // make sure the handle is differenciated by class
                handle = String.valueOf(classname.hashCode())+handle;
            }
            catch (NoSuchMethodException e)
            {
                // ignore, this simply means the portlet is not cacheable
            }
            catch (Exception e)
            {
                // invocation failed or security exception, in both case
                // log the error and treat the class as non cacheable
                logger.error("PortletFactory: failed to get cache handle",e);
            }
        }
        
        try {

            if (enableCache && (handle != null))
            {
                portlet = (Portlet)PortletCache.getCacheable( handle );

                //portlet in cache but expired, remove it from cache
                if ((portlet!=null) && ((Cacheable)portlet).getExpire().isExpired() )
                {
                    logger.info( "The portlet (" + handle + ") is expired" );
                    PortletCache.removeCacheable(handle);
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( "After removal of object(" + handle + ")." );
                    }
                    portlet = null;
                }
            }

            // we found a portlet in the cache
            if ( (portlet != null)
                 && ( portlet instanceof Cacheable )
                 && (! ((Cacheable)portlet).getExpire().isExpired()) )
            {
                // update the config for the portlet to the current one
                // Note: this is what was used to find the cached portlet.
                // Note: the init params may have changed in the psml since caching,
                //       this will update the portlet to use them.
                portlet.setPortletConfig( pc );
                portlet.setID( id );
                portlet.setName( pc.getName() );

                //FIXME: we now avoid to override metainfo when nothing is set
                //in the markup, so that cached portlets can keep their metainfo
                //This may lead to an incorrect metainfo retrieved if the first
                //instance of the portlet, which is put in the cache, has some
                //special metainfo defined in the markup 

                MetaData meta = pc.getMetainfo();
                
                if ( meta != null)
                {

                    if (! MetaData.DEFAULT_TITLE.equals( meta.getTitle() ) )
                    {
                        portlet.setTitle( meta.getTitle() );
                    }
    
                    if (! MetaData.DEFAULT_DESCRIPTION.equals( meta.getDescription() ) )
                    {
                        portlet.setDescription( meta.getDescription() );
                    }
                }

                //FIXME: Notice here we are putting the portlet without wrapper
                //in the cache, and we must wrap it on return.
                //Security implications: the portletcache should not be
                //publicly accessible.
                //Alternative: we could wrap the portlet before putting
                //it in the cache.

                //now compute the time it took to instantate and log it...
                // time in millis, sugested by Thomas Schaeck (schaeck@de.ibm.com)
                long milliseconds = ( System.currentTimeMillis() - begin );
        
                if (logger.isDebugEnabled())
                    logger.debug( "PortletFactory.getPortlet(): found in cache in "
                        + milliseconds + " ms - handle: " + handle );

                return PortletWrapper.wrap( portlet );
            }

            // if not found in the cache, instanciate a new Portlet
            portlet = (Portlet)portletClass.newInstance();

        }
        catch ( Throwable t )
        {
            logger.error("Throwable", t);
            throw new PortletException( t.getMessage() );
        }

        // save the current meta-info
        String title = null;
        String description = null;
        MetaData metainfo = pc.getMetainfo();
        
        if ( metainfo != null ) {
            title=metainfo.getTitle();
            description=metainfo.getDescription();
        }
        
        
        // init the portlet, it may override its PSML defined markup if
        // it doesn't check for it
        portlet.setID( id );
        portlet.setName( pc.getName() );
        portlet.setPortletConfig( pc );
        portlet.setCreationTime( System.currentTimeMillis() );
        portlet.init();

        //force the title and description from markup metadata
        //in case the portlet overwrote some values

        if ( metainfo != null)
        {
            if (!MetaData.DEFAULT_TITLE.equals(title) )
            {
                portlet.setTitle( title );
            }

            if (!MetaData.DEFAULT_DESCRIPTION.equals(description) )
            {
                portlet.setDescription( description );
            }
        }

        if (enableCache && (portlet instanceof Cacheable))
        {
            //place this portlet in a cache...
            ((Cacheable)portlet).setHandle( handle );
            PortletCache.addCacheable( ((Cacheable)portlet) );
            //Expiration should be added to the portlet now, so that
            //the watcher is created before file changes on disk.
            ((Cacheable)portlet).getExpire();

        }

        //now compute the time it took to instantate and log it...
        // time in millis, sugested by Thomas Schaeck (schaeck@de.ibm.com)
        long milliseconds = ( System.currentTimeMillis() - begin );

        if (logger.isDebugEnabled())
            logger.debug( "PortletFactory.getPortlet(): constructed in "
                + milliseconds + " ms - handle: " + handle );

        return PortletWrapper.wrap( portlet );

    }

    /**
     * Given a Registry Entry, get the value of what its PortletConfig would be.
     *
     * @param entry the PSML Entry containing the config
     * @param portletId the PSML entry's portlet id
     * @return the newly created PortletConfig object
     */
    protected PortletConfig getPortletConfig( PortletEntry portletEntry, String id)
    {
        Map map = new HashMap();
        map.putAll(portletEntry.getParameterMap());
        
        PortletConfig pc = new BasePortletConfig();
        pc.setName( portletEntry.getName() );
        addParentInitParameters(portletEntry, map);        
        pc.setInitParameters( map );
        pc.setMetainfo( getMetaData( portletEntry ) );
        pc.setURL( portletEntry.getURL() );
        pc.setCachedOnURL( portletEntry.isCachedOnURL() );
        //pc.setSecurityRef(portletEntry.getSecurityRef());
        pc.setSecurityRef(getSecurityReference(null, portletEntry));

        if (runDataService != null)
        {
            JetspeedRunData rundata = runDataService.getCurrentRunData();
            if (rundata != null)
            {
                Profile profile = rundata.getProfile();
                if (profile != null)
                {
                    pc.setPageId(profile.getId());
                }
            }
        }
        pc.setPortletId(id);

        return pc;
    }
    
    
    /**
     * Fetches the parameters out of a PSML Entry
     * 
     * @param entry the Entry to check for parameters
     * @return a Map containing the parameters names/values, an empty Map 
     *         is returned if there are no parameters
     */
    protected static Map getParameters( Entry entry )
    {
        Hashtable hash = new Hashtable();
        
        Parameter[] props = entry.getParameter();
        
        for(int i = 0; i < props.length; ++i)
        {
            hash.put(props[i].getName(), props[i].getValue() );
        }
        
        return hash;
    }

    /**
    Create a MetaData object from a PSML Metainfo object
    
    @param meta the Metainfo to copy

    @return the new MetaData object, empty if meta is null
    */
    protected static MetaData getMetaData(Entry entry)
    {
        MetaData data = new MetaData();
        MetaInfo meta = entry.getMetaInfo();

        if ( meta != null )
        {
            if ( meta.getTitle() != null )
                data.setTitle( meta.getTitle() );

            if ( meta.getDescription() != null )
                data.setDescription( meta.getDescription() );

            if ( meta.getImage() != null )
                data.setImage( meta.getImage() );
        }

        if ( entry.getParent() != null )
        {

            PortletEntry parent = (PortletEntry)Registry
                .getEntry( Registry.PORTLET, entry.getParent() );

            if (parent != null)
            {
                MetaData parentData = getMetaData( parent );
                parentData.merge(data);
                return parentData;
            }
            
        }

        return data;

    }

    /**
    Create a MetaData object from a registry Metainfo object
    
    @param meta the Metainfo to copy

    @return the new MetaData object, empty if meta is null
    */
    protected static MetaData getMetaData(PortletEntry entry)
    {
        MetaData data = new MetaData();

        if ( entry.getTitle() != null )
            data.setTitle( entry.getTitle() );

        if ( entry.getDescription() != null )
            data.setDescription( entry.getDescription() );
            
		if ( entry.getMetaInfo() != null && entry.getMetaInfo().getImage() != null )
			data.setImage( entry.getMetaInfo().getImage() );
            
        return data;
    }
    
    /**
     * @param Entry entry Entry whose parent we want
     * @return PortletEntry Parent of Entry
     * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
    protected static PortletEntry getParentEntry(PortletEntry entry)
    {
        PortletEntry result = null;
        String parent = entry.getParent();
        if (parent != null)
        {
            result = (PortletEntry) Registry.getEntry(Registry.PORTLET, parent);
        }

        return result;
    }
    
    /**
     * Retruns the classname defined for this PortletEntry.
     * If no classname was defined, the parent is queried
     * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
    protected String getClassname(PortletEntry entry)
    {
        String className = entry.getClassname();
        if (className == null)
        {
            PortletEntry parent = getParentEntry(entry);
            if (parent != null)
            {
            	// We must walk up the hierarchy just to be safe
                className = getClassname(parent);
            }
        }

        return className;
    }
    
    /**
     * Maps all parameters, not found within the <code>entry</code>, from
     * the <code>entry</code>'s parent into the entry
     * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
    protected void addParentInitParameters(PortletEntry entry, Map hash)
    {
        // Now map any parameters from the parent that the child does not have
        PortletEntry parent = getParentEntry(entry);
        if (parent != null)
        {
            Map parentMap = parent.getParameterMap();
            Iterator names = parent.getParameterNames();

            while (names.hasNext())
            {
                String key = (String) names.next();
                if (!hash.containsKey(key))
                {
                    hash.put(key, parentMap.get(key));                    
                }
            }
            
            // Always make sure to get the entire inheritence chain
           addParentInitParameters(parent, hash);
        }        
    }
    
    /**
     * Figures out how to produce a security reference for
     * this portlet.
     */
    protected SecurityReference getSecurityReference(Entry entry, PortletEntry pEntry)
    {
        // If something happended during init() that prevented this
        if (runDataService == null)
        {
            this.runDataService =
                (JetspeedRunDataService) TurbineServices.getInstance().getService(
                    RunDataService.SERVICE_NAME);
        }
        JetspeedRunData rundata = runDataService.getCurrentRunData();
        
        return JetspeedSecurity.getSecurityReference(entry,  rundata);
    }

}
