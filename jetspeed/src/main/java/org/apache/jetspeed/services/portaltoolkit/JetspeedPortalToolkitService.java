/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

package org.apache.jetspeed.services.portaltoolkit;

//jetspeed stuff
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletSkin;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.BasePortletConfig;
import org.apache.jetspeed.portal.PortletControlConfig;
import org.apache.jetspeed.portal.BasePortletControlConfig;
import org.apache.jetspeed.portal.PortletControllerConfig;
import org.apache.jetspeed.portal.BasePortletControllerConfig;
import org.apache.jetspeed.portal.BasePortletSkin;
import org.apache.jetspeed.portal.BasePortletSet;
import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;

import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.PortletControlEntry;
import org.apache.jetspeed.om.registry.PortletControllerEntry;
import org.apache.jetspeed.om.registry.SkinEntry;
import org.apache.jetspeed.util.MetaData;
import org.apache.jetspeed.util.JetspeedException;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.jetspeed.om.BaseSecurityReference;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.registry.SecurityEntry;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.rundata.RunDataService;

import java.util.Iterator;

import java.util.Hashtable;
import java.util.Map;
import javax.servlet.ServletConfig;

/**
 * Simple implementation of the PortalFactoryService.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 *
 * @version $Id: JetspeedPortalToolkitService.java,v 1.33 2004/03/29 21:02:29 taylor Exp $
 */
public class JetspeedPortalToolkitService
    extends TurbineBaseService
    implements PortalToolkitService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedPortalToolkitService.class.getName());    

    /** The default control to use when none is specified */
    private String defaultControl = null;

    /** The default controller to use when none is specified */
    private String defaultController = null;

    /** The default skin to use when none is specified */
    private String defaultSkin = null;

    /** The default user security ref to use when none is specified */
    private String defaultUserSecurityRef = null;

    /** The default anonymous user security ref to use when none is specified */
    private String defaultAnonSecurityRef = null;

    /** The default role security ref to use when none is specified */
    private String defaultRoleSecurityRef = null;

    /** The default group security ref to use when none is specified */
    private String defaultGroupSecurityRef = null;

    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     */
    public void init(ServletConfig conf) throws InitializationException
    {

        ResourceService serviceConf =
            ((TurbineServices) TurbineServices.getInstance()).getResources(
                PortalToolkitService.SERVICE_NAME);

        this.defaultControl = serviceConf.getString("default.control");
        this.defaultController = serviceConf.getString("default.controller");
        this.defaultSkin = serviceConf.getString("default.skin");
        this.defaultUserSecurityRef = serviceConf.getString("default.user.security.ref");
        this.defaultAnonSecurityRef = serviceConf.getString("default.anon.security.ref");
        this.defaultRoleSecurityRef = serviceConf.getString("default.role.security.ref");
        this.defaultGroupSecurityRef = serviceConf.getString("default.group.security.ref");
        setInit(true);

    }

    /**
     * Instanciates a PortletControl based on a Registry entry, if available
     * or directly from a classname.
     *
     * @param name a PortletControl name available in the registry or a classname
     * @return the created PortletControl
     */
    public PortletControl getControl(String name)
    {
        PortletControl pc = null;
        PortletControlEntry entry = null;

        if (name != null)
        {
            entry = (PortletControlEntry) Registry.getEntry(Registry.PORTLET_CONTROL, name);
        }

        Map params = null;

        try
        {
            if (entry == null)
            {
                if (name != null)
                {
                    pc = (PortletControl) Class.forName(name).newInstance();
                    params = new Hashtable();
                }
            }
            else
            {
                pc = (PortletControl) Class.forName(entry.getClassname()).newInstance();
                params = entry.getParameterMap();
            }
        }
        catch (Exception e)
        {
            logger.error("Unable to instanciate control " + name + ", using default", e);
        }

        if ((pc == null) && (defaultControl != null) && (!defaultControl.equals(name)))
        {
            return getControl(defaultControl);
        }

        PortletControlConfig pcConf = new BasePortletControlConfig();
        pcConf.setName(name);
        pcConf.setInitParameters(params);
        pc.setConfig(pcConf);

        return pc;
    }

    /**
     * Instanciates a PortletControl based on a PSML Control object
     *
     * @param control the PSML control object
     * @return the created PortletControl
     */
    public PortletControl getControl(Control control)
    {
        PortletControl pc = null;

        if (control != null)
        {
            pc = getControl(control.getName());
            pc.getConfig().getInitParameters().putAll(getParameters(control));
        }
        else
        {
            if (defaultControl != null)
            {
                pc = getControl(this.defaultControl);
            }
        }

        return pc;
    }

    protected PortletControl getControl(Control control, PortletEntry entry)
    {
        PortletControl pc = null;

        if (control != null)
        {
            pc = getControl(control.getName());
            pc.getConfig().getInitParameters().putAll(getParameters(control));
        }
        else
        {
            org.apache.jetspeed.om.registry.Parameter dftPortletCtrl =
                entry.getParameter("_control");

            if (dftPortletCtrl != null)
            {
                pc = getControl(dftPortletCtrl.getValue());
            }
            else if (defaultControl != null)
            {
                pc = getControl(this.defaultControl);
            }
        }

        return pc;
    }

    /**
     * Instanciates a PortletController based on a Registry entry, if available
     * or directly from a classname.
     *
     * @param name a PortletController name available in the registry or a classname
     * @return the created PortletController
     */
    public PortletController getController(String name)
    {
        PortletController pc = null;
        PortletControllerEntry entry = null;

        if (name != null)
        {
            entry = (PortletControllerEntry) Registry.getEntry(Registry.PORTLET_CONTROLLER, name);
        }

        Map params = null;

        try
        {
            if (entry == null)
            {
                if (name != null)
                {
                    pc = (PortletController) Class.forName(name).newInstance();
                    params = new Hashtable();
                }
            }
            else
            {
                pc = (PortletController) Class.forName(entry.getClassname()).newInstance();
                params = entry.getParameterMap();
            }
        }
        catch (Exception e)
        {
            logger.error("Unable to instanciate controller " + name + ", using default");
        }

        if ((pc == null) && (defaultController != null) && (!defaultController.equals(name)))
        {
            return getController(defaultController);
        }

        PortletControllerConfig pcConf = new BasePortletControllerConfig();
        pcConf.setName(name);
        pcConf.setInitParameters(params);
        pc.setConfig(pcConf);
        pc.init();

        return pc;
    }

    /**
     * Instantiates a PortletController based on a PSML Controller object
     *
     * @param controller the PSML controller object
     * @return the created PortletController
     */
    public PortletController getController(Controller controller)
    {

        PortletController pc = null;

        if (controller != null)
        {
            pc = getController(controller.getName());
            pc.getConfig().getInitParameters().putAll(getParameters(controller));
        }
        else
        {
            if (defaultController != null)
            {
                pc = getController(this.defaultController);
            }
        }

        pc.init();

        return pc;
    }

    /**
     * Create a PortletSkin object based on a Registry skin name
     *
     * @param name the registry SkinEntry name
     * @return the new PortletSkin object
     */
    public PortletSkin getSkin(String name)
    {
        BasePortletSkin result = new BasePortletSkin();

        SkinEntry entry = null;

        if (name != null)
        {
            entry = (SkinEntry) Registry.getEntry(Registry.SKIN, name);
        }

        // either we don't have any skin defined, the skin reference is null
        // or the skin reference is invalid, in all case, retrieve the default
        // skin entry
        if (entry == null)
        {
            entry = (SkinEntry) Registry.getEntry(Registry.SKIN, this.defaultSkin);
        }

        if (entry != null)
        {
            // build the PortletSkin object
            result.setName(entry.getName());
            result.putAll(entry.getParameterMap());
        }

        // Make the skin aware of what the user agent is capable of.
        JetspeedRunDataService jrds =
            (JetspeedRunDataService) ServiceUtil.getServiceByName(RunDataService.SERVICE_NAME);
        JetspeedRunData jData = jrds.getCurrentRunData();
        if(jData != null)
        {
        	result.setCapabilityMap(jData.getCapability()); 
        }
        return result;
    }

    /**
     * Create a PortletSkin object based on PSML skin description
     *
     * @param skin the PSML Skin object
     * @return the new PortletSkin object
     */
    public PortletSkin getSkin(Skin skin)
    {
        PortletSkin result = null;
        String name = null;

        if (skin != null)
        {
            name = skin.getName();

            // create the PortletSkin corresponding to this entry
            result = getSkin(name);

            // override the values with the locally defined properties
            result.putAll(getParameters(skin));

        }

        return result;
    }

    /**
     * Creates a PortletSet from a PSML portlets description
     *
     * @param portlets the PSML portlet set description
     * @return a new instance of PortletSet
     */
    public PortletSet getSet(Portlets portlets)
    {
        VariableInteger lastID = new VariableInteger(0);
        return getSet(portlets, new VariableInteger(0));
    }

    /**
     * Creates a PortletSet from a PSML portlets description, updating
     * the portletset name based on its position within the tree
     *
     * @param portlets the PSML portlet set description
     * @param count the portletset number within the complete tree
     * @return a new instance of PortletSet
     */
    protected PortletSet getSet(Portlets portlets, VariableInteger theCount)
    {
        // Create a new BasePortletSet to handle the portlets
        BasePortletSet set = new BasePortletSet();
        PortletController controller = getController(portlets.getController());
        set.setController(controller);
        String name = portlets.getName();
        if (name != null)
        {
            set.setName(name);
        }
        else
            set.setName(String.valueOf(theCount.getValue()));

        set.setID(portlets.getId());

        theCount.setValue(theCount.getValue() + 1);

        //FIXME: this sucks ! we should either associate the portlet set
        //with its portlets peer or set the porpoerties directly on the portlet
        //set object
        //Unfortunately, this would change the API too drastically for now...
        set.setPortletConfig(getPortletConfig(portlets));

        // Add all sub portlet sets in the main set
        //        Portlets[] subsets = portlets.getPortlets();
        //        for (int i=0; i < subsets.length; i++ )

        for (Iterator it = portlets.getPortletsIterator(); it.hasNext();)
        {
            Portlets subset = (Portlets) it.next();
            // Set this subset's parent Portlets collection.          
            subset.setParentPortlets(portlets);

            Map constraints = getParameters(subset.getLayout());
            int position = getPosition(subset.getLayout());
            set.addPortlet(
                getSet(subset, theCount),
                controller.getConstraints(constraints),
                position);
        }

        // Populate the PortletSet with Portlets
        //        Entry[] entries = portlets.getEntry();
        //        for( int i = 0; i < entries.length; ++i )

        for (Iterator eit = portlets.getEntriesIterator(); eit.hasNext();)
        {
            try
            {

                Entry psmlEntry = (Entry) eit.next();
                PortletEntry entry =
                    (PortletEntry) Registry.getEntry(Registry.PORTLET, psmlEntry.getParent());

                if (entry != null)
                {
                    Portlet p = PortletFactory.getPortlet(psmlEntry);

                    if (p != null)
                    {
                        Map constraints = getParameters(psmlEntry.getLayout());
                        int position = getPosition(psmlEntry.getLayout());

                        PortletControl control = getControl(psmlEntry.getControl(), entry);

                        set.addPortlet(
                            initControl(control, p),
                            controller.getConstraints(constraints),
                            position);
                    }
                }
                else
                {
                    logger.error(
                        " The portlet "
                            + psmlEntry.getParent()
                            + " does not exist in the Registry ");
                    continue;
                }
            }
            catch (JetspeedException e)
            {
                logger.error("Exception", e);
                continue;
            }

        }

        // Decorate with a control if required and return
        if (portlets.getControl() != null)
        {
            PortletControl control = getControl(portlets.getControl());
            return initControl(control, set);
        }

        set.sortPortletSet();
        // Or return the set
        return set;
    }

    /**
     * Associates a PortletControl with an existing Portlet and
     * returns the Control
     *
     * @param pc the existing PortletControl
     * @param portlet the existing Portlet to be associated with the control
     * @return first PortletControl associated with the portlet
     */
    protected PortletControl initControl(PortletControl pc, Portlet portlet)
    {

        if (portlet == null)
        {
            throw new IllegalArgumentException("Portlet not specified");
        }

        if (pc == null)
        {
            throw new IllegalArgumentException("PortletControl not specified");
        }

        pc.init(portlet);

        return pc;

    }

    /**
    Given a PSML Portlets, get the value of what its PortletConfig would be.
    
    @param entry the Portlets containing the config
    @return the newly created PortletConfig object
    */
    protected PortletConfig getPortletConfig(Portlets portlets)
    {

        PortletConfig pc = new BasePortletConfig();

        pc.setName(portlets.getName());
        pc.setInitParameters(getParameters(portlets));

        //Invocation of new skin-locating algorithim
        pc.setPortletSkin(getSkin(findSkin(portlets)));

        pc.setSecurityRef(portlets.getSecurityRef());
        pc.setMetainfo(getMetaData(portlets));

        return pc;
    }

    /**
     * Fetches the parameters out of a PSML Portlets entry
     *
     * @param portlets the Portlets entry to check for parameters
     * @return a Map containing the parameters names/values, an empty Dictionary
     *        is returned if there are no parameters
     */
    protected static Map getParameters(Portlets portlets)
    {
        Hashtable hash = new Hashtable();

        if (portlets != null)
        {
            Parameter[] props = portlets.getParameter();

            for (int i = 0; i < props.length; ++i)
            {
                hash.put(props[i].getName(), props[i].getValue());
            }
        }

        return hash;
    }

    /**
     * Retrieves the parameters from a PSML Control object
     *
     * @param control the PSML object to explore
     * @return a Map of the existing control parameters or an empty map
     */
    protected static Map getParameters(Control control)
    {
        Hashtable hash = new Hashtable();

        if (control != null)
        {
            Parameter[] params = control.getParameter();

            for (int i = 0; i < params.length; i++)
            {
                hash.put(params[i].getName(), params[i].getValue());
            }
        }
        return hash;
    }

    /**
     * Retrieves the parameters from a PSML Controller object
     *
     * @param controller the PSML object to explore
     * @return a Map of the existing controller parameters or an empty map
     */
    protected static Map getParameters(Controller controller)
    {
        Hashtable hash = new Hashtable();

        if (controller != null)
        {
            Parameter[] params = controller.getParameter();

            for (int i = 0; i < params.length; i++)
            {
                hash.put(params[i].getName(), params[i].getValue());
            }
        }
        return hash;
    }

    /**
     * Retrieves a parameter Map from an array of PSML Layout object
     *
     * @param layout the Layout object to use
     * @return a Map containing the names/values, an empty map
     *  is returned if there are no properties
     */
    protected static Map getParameters(Layout layout)
    {
        Hashtable hash = new Hashtable();

        if (layout != null)
        {
            Parameter[] props = layout.getParameter();

            for (int i = 0; i < props.length; ++i)
            {
                hash.put(props[i].getName(), props[i].getValue());
            }
        }

        return hash;
    }

    /**
     * Retrieves a parameter Map from a PSML skin object
     *
     * @param skin the Skin object to use
     * @return a Map containing the names/values, an empty map
     *  is returned if there are no properties
     */
    protected static Map getParameters(Skin skin)
    {
        Hashtable hash = new Hashtable();

        if (skin != null)
        {
            Parameter[] props = skin.getParameter();

            for (int i = 0; i < props.length; ++i)
            {
                hash.put(props[i].getName(), props[i].getValue());
            }
        }

        return hash;
    }

    /**
    Create a MetaData object from a PSML Metainfo object
    
    @param meta the Metainfo to copy
    
    @return the new MetaData object, empty if meta is null
    */
    protected static MetaData getMetaData(Portlets portlets)
    {
        MetaData data = new MetaData();
        MetaInfo meta = portlets.getMetaInfo();

        if (meta != null)
        {
            if (meta.getTitle() != null)
            {
                data.setTitle(meta.getTitle());
            }

            if (meta.getDescription() != null)
            {
                data.setDescription(meta.getDescription());
            }

            if (meta.getImage() != null)
            {
                data.setImage(meta.getImage());
            }
        }

        return data;

    }

    /**
     * Get the position value in a Layout object
     *
     * @param layout the Layout object to use
     *
     * @return the defined position or -1 if undefined
     */
    protected static int getPosition(Layout layout)
    {
        int pos = -1;

        try
        {
            pos = (int) layout.getPosition();
        }
        catch (RuntimeException e)
        {
            // either layout is null or the position isn't an integer
            // keep the default value
        }

        return pos;
    }

    protected static class VariableInteger
    {
        int value;

        public VariableInteger(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return this.value;
        }

        public void setValue(int value)
        {
            this.value = value;
        }
    }

    /**
     * Given a locator String path, returns a Portlets collecton
     *
     * @param locatorPath ProfileLocator resource path identifier
     * @return a portlets collection from the PSML resource
     */
    public Portlets getReference(String locatorPath)
    {
        ProfileLocator locator = Profiler.createLocator();
        locator.createFromPath(locatorPath);
        String id = locator.getId();

        try
        {
            Profile profile = Profiler.getProfile(locator);
            PSMLDocument doc = profile.getDocument();
            if (doc == null)
            {
                return null;
            }
            Portlets portlets = doc.getPortlets();
            return portlets;
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
            return null;
        }
    }

    /**
     * Helps locate a skin, recursively if neccesary.
     * <ul>
     *  <li>First: return the name of the skin defined for this <code>Portlets</code>
     * collection.</li>
     * <li>  If the this <code>Portlets</code> collection has no skin defined, it's
     *  parent is checked, then it's parent's parent and so on until either a skin
     *  is found.</li>
     * <li> If the previous two attempts fail the, the system default skin is used</li>
     * @param Portlets portlets Portlets collection whose skin needs to be located.
     */
    protected String findSkin(Portlets portlets)
    {
        if (portlets.getSkin() != null)
        {
            return portlets.getSkin().getName();
        }
        else if (portlets.getParentPortlets() != null)
        {
            return findSkin(portlets.getParentPortlets());
        }
        else
        {
            return this.defaultSkin;
        }
    }

    /**
     * Gets default security ref based on the profile type (user|role|group). Returns
     * null if no default is defined.
     * 
     * @param profile
     * @return default security reference
     */
    public SecurityReference getDefaultSecurityRef(Profile profile)
    {
        String type = null;
        if (profile.getUserName() != null)
        {
            if (profile.getAnonymous())
            {
                type = Profiler.PARAM_ANON;
            }
            else
            {
                type = Profiler.PARAM_USER;
            }
        }
        else if (profile.getRoleName() != null)
        {
            type = Profiler.PARAM_ROLE;
        }
        else if (profile.getGroupName() != null)
        {
            type = Profiler.PARAM_GROUP;
        }

        return getDefaultSecurityRef(type);

    }

    /**
     * Gets default security ref based on the profile type (user|role|group). Returns
     * null if no default is defined.
     *
     * @param type of entity to return default security ref for
     * @return default security reference
     */
    public SecurityReference getDefaultSecurityRef(String type)
    {
        BaseSecurityReference result = null;

        SecurityEntry entry = null;

        String defaultRef = null;
        if (type.equals(Profiler.PARAM_USER))
        {
            defaultRef = this.defaultUserSecurityRef;
        }
        else if (type.equals(Profiler.PARAM_ANON))
        {
            defaultRef = this.defaultAnonSecurityRef;
        }
        else if (type.equals(Profiler.PARAM_ROLE))
        {
            defaultRef = this.defaultRoleSecurityRef;
        }
        else if (type.equals(Profiler.PARAM_GROUP))
        {
            defaultRef = this.defaultGroupSecurityRef;
        }

        if (defaultRef != null)
        {
            entry = (SecurityEntry) Registry.getEntry(Registry.SECURITY, defaultRef);
            if (logger.isDebugEnabled())
            {
                logger.debug(
                    "JetspeedPortalToolkit: default security for type: " + type + " is " + defaultRef);
            }
            if (entry != null)
            {
                result = new BaseSecurityReference();
                result.setParent(entry.getName());
                if (logger.isDebugEnabled())
                {
                    logger.debug(
                        "JetspeedPortalToolkit: default security for type: "
                            + type
                            + " was set to "
                            + entry.getName());
                }
            }
        }

        return result;

    }
}
