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

package org.apache.jetspeed.modules.actions.portlets;

// Jetspeed Stuff
import org.apache.jetspeed.modules.actions.portlets.CustomizeSetAction;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletSkin;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.om.BaseSecurityReference;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.om.profile.psml.PsmlSkin;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.base.BaseParameter;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.util.MetaData;
import org.apache.jetspeed.services.statemanager.SessionState;

// Turbine stuff
import org.apache.turbine.util.RunData;
import org.apache.turbine.modules.ActionLoader;

// Velocity Stuff
import org.apache.velocity.context.Context;

import java.util.Vector;
import java.util.Iterator;

/**
 * This action implements the default portlet behavior customizer
 *
 * <p>Don't call it from the URL, the Portlet and the Action are automatically
 * associated through the registry PortletName
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 */
public class CustomizeAction extends VelocityPortletAction
{

    public static final String PARAM_NAMESPACE = "_param_";

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(CustomizeAction.class.getName());    
    
    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     *
     * <table>
     * <tr><th>Context      </th><th> Description</th></tr>
     * <!-- ---------  ------------------------- -->
     * <tr><td>action       </td><td> Action to use</td></tr>
     * <tr><td>current_skin </td><td> Current skin for this portlet INSTANCE</td></tr>
     * <tr><td>params       </td><td> List of configurable parameters from the REGISTRY entry.</td></tr>
     * <tr><td>portlet      </td><td> Portlet, not the Portlet Instance!</td></tr>
     * <tr><td>skins        </td><td> List of skins</td></tr>
     * <tr><td>security     </td><td> List of security ref</td></tr>
     * <tr><td>security_ref </td><td> Current securityRef for this portlet INSTANCE</td></tr>
     * </table>
     */
    protected void buildNormalContext( VelocityPortlet portlet,
                                       Context context,
                                       RunData rundata )
    {

        // generic context stuff
        context.put("skins", CustomizeSetAction.buildList(rundata, Registry.SKIN));
        context.put("securitys", CustomizeSetAction.buildList(rundata, Registry.SECURITY));

        // we should first retrieve the portlet to customize
        Portlet p = ((JetspeedRunData)rundata).getCustomized();

        context.put("action", "portlets.CustomizeAction");

        PortletInstance instance = PersistenceManager.getInstance(p, rundata);
        context.put("portlet_instance", PersistenceManager.getInstance(p, rundata));

        if (p==null) return;

        // retrieve the portlet parameters
        PortletEntry entry = (PortletEntry)Registry.getEntry(Registry.PORTLET,p.getName());
        // save the entry in the session
        Vector params = new Vector();
        Iterator i = entry.getParameterNames();

        //System.out.println("==========================================");
        while(i.hasNext())
        {
            String name = (String)i.next();
            Parameter param = entry.getParameter(name);

            // filter some "system" and hidden parameters
            if (  (!param.isHidden()) && (name.charAt(0)!='_') )
            {
                // check the user role
                if (JetspeedSecurity.checkPermission((JetspeedUser)rundata.getUser(), new PortalResource( entry, param), JetspeedSecurity.PERMISSION_CUSTOMIZE))
                {
                    // Implementation of clone() is missing so we have do it "by hand"
                    Parameter clone = new BaseParameter();
                    clone.setName(param.getName());
                    clone.setTitle(param.getTitle());
                    clone.setDescription(param.getDescription());
                    clone.setType(param.getType());
                    if (instance.getAttribute(name, null) != null)
                    {
                        clone.setValue(instance.getAttribute(name));
                        //System.out.println("Adding value from instance [" + name + "] = [" + clone.getValue() + "]");
                    }
                    else if (p.getPortletConfig().getInitParameter(name) != null)
                    {
                        clone.setValue(p.getPortletConfig().getInitParameter(name));
                        //System.out.println("Adding value from init [" + name + "] = [" + clone.getValue() + "]");
                    }
                    else
                    {
                        clone.setValue(param.getValue());
                        //System.out.println("Adding value from registry [" + name + "] = [" + clone.getValue() + "]");
                    }
                    params.add(clone);
                }
            }
        }

        // get the customization state for this page
        SessionState customizationState = ((JetspeedRunData)rundata).getPageSessionState();
        customizationState.setAttribute("customize-parameters", params);

        // populate the customizer context
        context.put("parameters", params);
        context.put("portlet", p);
        context.put("customizer", portlet);

        if (p.getPortletConfig().getSecurityRef() != null)
          context.put("security_ref", p.getPortletConfig().getSecurityRef().getParent());
        if (p.getPortletConfig().getSkin() != null)
            context.put("current_skin", p.getPortletConfig().getPortletSkin().getName());

        Profile profile = ((JetspeedRunData)rundata).getCustomizedProfile();
        String currentTitle = profile.getDocument().getEntryById(p.getID()).getTitle();
        if (currentTitle == null && p.getPortletConfig().getMetainfo() != null)
        {
            currentTitle = p.getPortletConfig().getMetainfo().getTitle();
        }
        context.put("current_title", currentTitle);

    }

    /** Clean up the customization state */
    public void doCancel(RunData rundata, Context context)
    {
        ((JetspeedRunData)rundata).setCustomized(null);
        if (((JetspeedRunData)rundata).getCustomized()==null)
        {
            try
            {
                ActionLoader.getInstance().exec( rundata, "controls.EndCustomize" );
            }
            catch (Exception e)
            {
                logger.error("Unable to load action controls.EndCustomize ",e);
            }
        }
    }

    /**
     * Resets the portlet settings to default
     * 
     * @param rundata
     * @param context
     */
    public void doDefault(RunData rundata, Context context)
    {                    
        // we should first retrieve the portlet to customize and its parameters
        // definition
        Portlet p = ((JetspeedRunData) rundata).getCustomized();

        // Update paramaters
        try
        {
            PortletInstance instance = PersistenceManager.getInstance(p, rundata);

            instance.removeAllAttributes();

            try
            {
                ((JetspeedRunData) rundata).getCustomizedProfile().store();
            }
            catch (Exception e)
            {
                logger.error("Unable to save profile ",e);
            }

            //FIXME: this hack is due to the corrupted lifecycle of the portlet in the
            //current API when caching is activated
            try
            {
                org.apache.jetspeed.util.PortletSessionState.setPortletConfigChanged(p, rundata);
                p.init();
            }
            catch (PortletException e)
            {
                logger.error("Customizer failed to reinitialize the portlet "+p.getName(), e);
            }

            // we're done, make sure clean up the
            // session
            doCancel(rundata, context);
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }


    /** Updates the customized portlet entry */
    public void doUpdate(RunData rundata, Context context)
    {
        // get the customization state for this page
        SessionState customizationState = ((JetspeedRunData)rundata).getPageSessionState();

        // we should first retrieve the portlet to customize and its parameters
        // definition
        Portlet p = ((JetspeedRunData)rundata).getCustomized();
        Vector params = (Vector) customizationState.getAttribute("customize-parameters");
        String newSecurityParent = rundata.getParameters().getString("_security_ref");
        String newSkinName = (String) rundata.getParameters().getString("_skin");
        String newTitle = (String) rundata.getParameters().getString("current_title");

        boolean changeRequested = ( (params != null) || (newSkinName != null) || (newSecurityParent != null) || (newTitle != null));
        boolean madePsChange = false;
        boolean madePcChange = false;

        if ((p==null) || (changeRequested == false ))
        {
            doCancel(rundata, context);
            return;
        }
        PortletConfig pc = p.getPortletConfig();
        Profile profile = ((JetspeedRunData)rundata).getCustomizedProfile();
        Entry entry = profile.getDocument().getEntryById(p.getID());

        // Only update the security ref if the parent changed
        if ((newSecurityParent != null))
        {
            boolean securityChanged = false;
            SecurityReference currentSecurityRef = pc.getSecurityRef();
            if (currentSecurityRef != null)
            {
                securityChanged = (newSecurityParent.equals(currentSecurityRef.getParent()) == false);
            }
            else
            {
                securityChanged = (newSecurityParent.trim().length() > 0);
            }
            if (securityChanged == true)
            {
                SecurityReference securityRef = null;
                if ((newSecurityParent.trim().length() > 0))
                {
                    securityRef = new BaseSecurityReference();
                    securityRef.setParent( newSecurityParent);
                }
                // Note: setting the portlet's config may not be a good idea -
                // it might be used as the Portlet for other PSMLDocument Entries that
                // have a different idea of security - and the caching of Portlets does
                // NOT include security -ggolden.
                pc.setSecurityRef(securityRef);
                entry.setSecurityRef(securityRef);
                madePcChange = true;
            }
        }

        // Only update the skin if the name changed
        if (newSkinName != null)
        {
            boolean skinChanged = false;
            String currentSkinName = null;

            if  (pc.getSkin() != null)
                currentSkinName = pc.getPortletSkin().getName();

            if (currentSkinName != null)
            {
                skinChanged = (newSkinName.equals(currentSkinName) == false);
            }
            else
            {
                skinChanged = (newSkinName.trim().length() > 0);
            }

            if (skinChanged == true)
            {
                PortletSkin skin = null;
                if ((newSkinName.trim().length() > 0))
                {
                    skin = PortalToolkit.getSkin(newSkinName);
                    if (skin != null)
                    {
                        // Note: setting the portlet's config may not be a good idea -
                        // it might be used as the Portlet for other PSMLDocument Entries that
                        // have a different idea of skin - and the caching of Portlets does
                        // NOT include skin -ggolden.
                        pc.setPortletSkin(skin);

                        Skin psmlSkin = entry.getSkin();
                        if (psmlSkin == null)
                        {
                            entry.setSkin(new PsmlSkin());
                        }
                        entry.getSkin().setName(newSkinName);
                    }
                    else
                    {
                        logger.warn( "Unable to update skin for portlet entry " + entry.getId() + " because skin " + skin + " does not exist.");
                    }
                }
                else
                {
                    // Note: setting the portlet's config may not be a good idea -
                    // it might be used as the Portlet for other PSMLDocument Entries that
                    // have a different idea of skin - and the caching of Portlets does
                    // NOT include skin -ggolden.
                    pc.setPortletSkin( null);
                    entry.setSkin(null);
                }
                madePcChange = true;
            }
        }

        // Only update the title if the title changed
        if (newTitle != null)
        {
            boolean titleChanged = false;
            String currentTitle = entry.getTitle();

            MetaData md = pc.getMetainfo();
            if  (currentTitle == null && md != null && md.getTitle() != null)
                currentTitle = md.getTitle();

            if (currentTitle != null)
            {
                titleChanged = (newTitle.equals(currentTitle) == false);
            }
            else
            {
                titleChanged = (newTitle.trim().length() > 0);
            }

            if (titleChanged == true)
            {

                if ((newTitle.trim().length() > 0))
                {
                    // Note: setting the portlet's config may not be a good idea -
                    // it might be used as the Portlet for other PSMLDocument Entries that
                    // have a different idea of title - and the caching of Portlets does
                    // NOT include title -ggolden.
                    if (md == null) {
                        md = new MetaData();
                        pc.setMetainfo(md);
                    }
                    md.setTitle(newTitle);
                    entry.setTitle(newTitle);
                    madePcChange = true;
                }
            }
        }

        // Update paramaters
        try
        {
            PortletInstance instance = PersistenceManager.getInstance(p, rundata);
            PortletEntry regEntry = (PortletEntry) Registry.getEntry(Registry.PORTLET, p.getName());

            Iterator i = params.iterator();

            //System.out.println("==========================================");
            while(i.hasNext())
            {
                Parameter param = (Parameter)i.next();
                String name = param.getName();
                String newValue = null;
                String[] testArray = rundata.getParameters().getStrings(name);
                if (testArray != null && testArray.length > 1)
                {
                    newValue = org.apache.jetspeed.util.StringUtils.arrayToString(testArray, ",");
                }
                else 
                {
                    newValue = rundata.getParameters().getString(name);
                    if (newValue == null)
                    {
                        newValue = "";
                    }
                }
                String regValue = regEntry.getParameter(name).getValue(); //param.getValue();
                String psmlValue = instance.getAttribute(name);

                //System.out.println(name + "= [" + psmlValue + "] in psml");
                //System.out.println(name + "= [" + regValue + "] in registry");

                // New value for this parameter exists
                if (newValue != null)
                {
                    //System.out.println(name + "= [" + newValue + "] in request");
                    // New value differs from registry - record it in psml
                    if (!regValue.equals(newValue) || !psmlValue.equals(newValue))
                    {
                        instance.setAttribute(name, newValue);
                        psmlValue = newValue;
                        //System.out.println("setting attribute for [" + name + "] to [" + newValue + "]");
                    }
                    madePsChange = true;
                }
                // Remove duplicate parameters from psml
                if (psmlValue != null && psmlValue.equals(regValue))
                {
                    //System.out.println("removing attribute for [" + name + "]");
                    instance.removeAttribute(name);
                    madePsChange = true;
                }

            }

            // save all the changes
            if ((madePsChange == true) || (madePcChange == true))
            {
                try
                {
                    JetspeedRunData jdata = (JetspeedRunData) rundata;
                    profile.store();
                    //FIXME: this hack is due to the corrupted lifecycle of the portlet in the
                    //current API when caching is activated
                    p.init();
                    org.apache.jetspeed.util.PortletSessionState.setPortletConfigChanged(p, rundata);
                }
                catch (PortletException e)
                {
                    logger.error("Customizer failed to reinitialize the portlet "+p.getName(), e);
                }
                catch (Exception e)
                {
                    logger.error("Unable to save profile ",e);
                }
            }

            // we're done, make sure clean up the
            // session
            doCancel(rundata, context);
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
    }
}
