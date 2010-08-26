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

package org.apache.jetspeed.portal.security.portlets;

// Jetspeed
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletState;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.portletcache.Cacheable;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.services.PortletStats;

// Turbine imports
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.localization.Localization;

// ECS
import org.apache.ecs.ConcreteElement;
import org.apache.jetspeed.util.JetspeedClearElement;



/**
<p>
This object is used to wrap a Portlet, ensuring that access control rules are enforced.
</p>

@author <A HREF="mailto:sgala@apache.org">Santiago Gala</A>
@version $Id: PortletWrapper.java,v 1.25 2004/03/29 21:38:43 taylor Exp $
*/
public class PortletWrapper implements Portlet
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PortletWrapper.class.getName());
    
    /*
     * The portlet we are wrapping
     */
    private Portlet wrappedPortlet = null;
    private PortalResource portalResource = null;
    private ConcreteElement content = null;

    public PortletWrapper(Portlet inner)
    {
        wrappedPortlet = inner;
        portalResource = new PortalResource(wrappedPortlet);
    }

    /**
    */
    public final String getName()
    {
        //This means name is accessible for every Portlet
        return wrappedPortlet.getName();
    }

    /**
    */
    public final void setName(String name)
    {
        //if we want to secure this, we need a context for the check
        wrappedPortlet.setName(name);
    }

    /**
    */
    public final PortletConfig getPortletConfig()
    {
        return wrappedPortlet.getPortletConfig();
    }

    /**
    */
    public final void setPortletConfig(PortletConfig pc)
    {
        //if we want to secure this, we need a context for the check
        wrappedPortlet.setPortletConfig(pc);
    }
    
    /**
    */
    public ConcreteElement getContent(RunData rundata)
    {
 
        if (checkPermission(rundata, JetspeedSecurity.PERMISSION_VIEW))
        {
            if (PortletStats.isEnabled())
            {
            	long start = System.currentTimeMillis();
            	content = wrappedPortlet.getContent(rundata);
            	long time = System.currentTimeMillis() - start; // time it took to get the content for this portlet
                PortletStats.logAccess(rundata, this, PortletStats.ACCESS_OK, time);
            } else {
            	content = wrappedPortlet.getContent(rundata);
            }
            
            return content;
        }
        else 
        {
            if (PortletStats.isEnabled())
            {
                PortletStats.logAccess(rundata, this, PortletStats.ACCESS_DENIED);
            }
            return new JetspeedClearElement(Localization.getString(rundata, "SECURITY_NO_ACCESS_TO_PORTLET"));
        }
    }

    /**
    Provide a description within PML if the user has specified one.

    @return a null entry if the user hasn't defined anything
    */
    public String getDescription()
    {
        return wrappedPortlet.getDescription();
    }

    public String getDescription(String instanceDescription)
    {
        return wrappedPortlet.getDescription(instanceDescription);
    }

    /**
    */
    public void setDescription(String description)
    {
        wrappedPortlet.setDescription(description);
    }

    /**
     * @see Portlet#getImage
     */
    public String getImage(String instanceImage)
    {
        return wrappedPortlet.getImage(instanceImage);
    }
    
    /**
     * @see Portlet#setImge
     */
    public void setImage(String image)
    {
        wrappedPortlet.setImage(image);
    }
    /**
     * @see Portlet#getTitle
     */
    public String getTitle()
    {
        /* FIXME, no rundata here if( !checkPermission(rundata, 
                                         JetspeedSecurity.PERMISSION_VIEW ) )
                                              { */
        return wrappedPortlet.getTitle();
        /* } */

    }

    /**
     * @see Portlet#getImage
     */
    public String getTitle(String instanceTitle)
    {
        return wrappedPortlet.getTitle(instanceTitle);
    }
    
    /**
    Set the title for this Portlet
    */
    public void setTitle(String title)
    {
        /* FIXME, no rundata here if( !checkPermission(rundata, 
                                              JetspeedSecurity.PERMISSION_CUSTOMIZE ) )
                                              { */
        wrappedPortlet.setTitle(title);
        /* } */
    }


    /**
    */
    public boolean getAllowEdit(RunData rundata)
    {
        return checkPermission(rundata, JetspeedSecurity.PERMISSION_CUSTOMIZE);
    }

    /**
     */
     public boolean getAllowView(RunData rundata)
     {

         if (checkPermission(rundata, JetspeedSecurity.PERMISSION_VIEW))
         {
             return wrappedPortlet.getAllowView(rundata);
         }
         return false;
     }
    
    /**
    */
    public boolean getAllowMaximize(RunData rundata)
    {
        return checkPermission(rundata, JetspeedSecurity.PERMISSION_MAXIMIZE);
    }

    /**
    By default don't provide any initialization
    */
    public void init() throws PortletException 
    {
        /* FIXME, no rundata here if( !checkPermission(rundata, 
                                              JetspeedSecurity.PERMISSION_CUSTOMIZE) )
                                              { */
        wrappedPortlet.init();
        /* } */
    }

    /**
    @see Portlet#getCreationTime
    */
    public long getCreationTime()
    {
        /* FIXME, no rundata here if( !checkPermission(rundata, 
                                              JetspeedSecurity.PERMISSION_VIEW) )
                                              { */
        return wrappedPortlet.getCreationTime();
        /* } */
    }
    
    /**
    @see Portlet#setCreationTime
    */
    public void setCreationTime(long creationTime)
    {
        /* FIXME, no rundata here if( !checkPermission(rundata, 
                                              JetspeedSecurity.PERMISSION_CUSTOMIZE) )
                                              { */
        wrappedPortlet.setCreationTime(creationTime);
    }
    
    /**
    @see Portlet#supportsType
    */
    public boolean supportsType(MimeType mimeType)
    {
        /* FIXME, no rundata here if( !checkPermission(rundata, 
                                              JetspeedSecurity.PERMISSION_VIEW) )
                                              { */
            return wrappedPortlet.supportsType(mimeType);
            /* } */
    }

    /**
     * Utility method for checking Permissions on myself.
     * @param rundata A rundata Object
     * @param permissionName String the name of the Permission requested
     * @return boolean is it granted?
     */
    protected boolean checkPermission(RunData rundata, String permissionName)
    {
        try
        {
            JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
            portalResource.setOwner(jsLink.getUserName());
            JetspeedLinkFactory.putInstance(jsLink);
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage(), e);
            portalResource.setOwner(null);
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("checking for Portlet permission: "
                      + permissionName 
                      + " for portlet: "
                      + wrappedPortlet.getName() 
                      + " Owner = " 
                    + portalResource.getOwner());
        }
        
        return JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),
                                                 portalResource,
                                                 permissionName);
    }

    // utility methods

    /**
    Returns TRUE if the title bar in should be displayed. The title bar includes
    the portlet title and action buttons. 
     
    NOTE(FIXME) Not in Portlet interface. Called a la Bean from Velocity.
    @param rundata A RunData object
    */
    public boolean isShowTitleBar(RunData rundata)
    {
        if (wrappedPortlet.getPortletConfig() != null) 
        {
            // Parameter can exist in PSML or <portlet-entry>
            return Boolean.valueOf(wrappedPortlet.getPortletConfig().getInitParameter("_showtitlebar", "true")).booleanValue();
        }
        return getAttribute("_showtitlebar", "true", rundata).equals("true");
    }

    
    /**
    Retrieve a portlet attribute from persistent storage

    @param attrName The attribute to retrieve
    @parm attrDefValue The value if the attr doesn't exists
    @param rundata A RunData object
    @return The attribute value
    */
    public String getAttribute(String attrName, String attrDefValue, RunData rundata)
    {
        if (checkPermission(rundata, JetspeedSecurity.PERMISSION_VIEW))
        {
            return wrappedPortlet.getAttribute(attrName, attrDefValue, rundata);
        }
        else
        {
            //FIXME: for the moment we will allow this call to succeed...
            //throw new TurbineRuntimeException( "Security check failed" );
            return wrappedPortlet.getAttribute(attrName, attrDefValue, rundata);
        }

    }

    /**
     * Sets a portlet attribute to persistent storage
     *
     * @param attrName The attribute to retrieve
     * @param attrValue The value 
     * @param rundata A RunData object
     */
    public void setAttribute(String attrName, String attrValue, RunData rundata)
    {
        if (checkPermission(rundata, JetspeedSecurity.PERMISSION_VIEW))
        {
            wrappedPortlet.setAttribute(attrName, attrValue, rundata);
        }
        else
        {
            //FIXME: for the moment we will allow this call to succeed...
            //throw new TurbineRuntimeException( "Security check failed" );
            wrappedPortlet.setAttribute(attrName, attrValue, rundata);
        }

    }

    /**
     * Gets the portlet instance associated with this portlet.
     *
     * @param rundata A RunData object
     * @return PortletInstance
     */
    public PortletInstance getInstance(RunData rundata)
    {
        return wrappedPortlet.getInstance(rundata);
    }

    /**
     * <p>Return an instance of one of the classes in this package
     * making tests before calling the wrapped portlet</p>
     * <p>Different wrapper classes must be used with the current API
     * depending on the interfaces implemented by the portlet. :-(</p>
     *
     */
    public static Portlet wrap(Portlet aPortlet)
    {
        //SGP Security test
        if (aPortlet instanceof PortletState)
        {
            if (aPortlet instanceof Cacheable)
            {
                return new CacheableStatefulPortletWrapper(aPortlet);
            }
            return new StatefulPortletWrapper(aPortlet);
        }
        if (aPortlet instanceof Cacheable)
        {
            return new CacheablePortletWrapper(aPortlet);
        }
        return new PortletWrapper(aPortlet);
        
    }
 

    public String getID()
    {
        return wrappedPortlet.getID();
    }

    public void setID(String id)
    {
        wrappedPortlet.setID(id);
    }

    /**
    * @return true if the portlet does its own customization
    */
    public boolean providesCustomization()
    {
        return wrappedPortlet.providesCustomization();
    } 

    public Portlet getPortlet()
    {
        return wrappedPortlet;
    }      
}
