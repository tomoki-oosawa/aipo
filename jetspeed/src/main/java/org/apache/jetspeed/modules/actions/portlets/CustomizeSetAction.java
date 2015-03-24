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
 
package org.apache.jetspeed.modules.actions.portlets;

// Jetspeed imports
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletSkin;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletSetController;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.om.BaseSecurityReference;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.PortletInfoEntry;
import org.apache.jetspeed.om.registry.base.BaseCategory;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.psml.PsmlPortlets;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.psml.PsmlEntry;
import org.apache.jetspeed.om.profile.Reference;
import org.apache.jetspeed.om.profile.psml.PsmlReference;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.om.profile.psml.PsmlSkin;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.psml.PsmlMetaInfo;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.psml.PsmlController;
import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.psml.PsmlControl;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.customlocalization.CustomLocalization;
import org.apache.jetspeed.services.idgenerator.JetspeedIdGenerator;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.util.AutoProfile;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.services.resources.JetspeedResources;

// Turbine stuff
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;


// Velocity Stuff
import org.apache.velocity.context.Context;

// Java imports
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import java.text.MessageFormat;

/**
 * This action implements the default portletset behavior customizer 
 * 
 * <p>Don't call it from the URL, the Portlet and the Action are automatically
 * associated through the registry PortletName
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: CustomizeSetAction.java,v 1.51 2004/02/23 02:56:58 jford Exp $
 */
public class CustomizeSetAction extends VelocityPortletAction
{

    private static final String USER_SELECTIONS = "session.portlets.user.selections";
    private static final String UI_PORTLETS_SELECTED = "portletsSelected";
    private static final String PORTLET_LIST = "session.portlets.list";
    private static final String ALL_PORTLET_LIST = "session.all.portlets.list";
    private static final String PORTLET_LIST_PAGE_SIZE = "session.portlets.page.size";
    private static final String HIDE_EMPTY_CATEGORIES = "customizer.hide.empty.categories";
    
    public static final String FILTER_FIELDS = "filter_fields";
    public static final String FILTER_VALUES = "filter_values";

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(CustomizeSetAction.class.getName());    
    
    /** 
     * Subclasses must override this method to provide default behavior 
     * for the portlet action
     */
    protected void buildNormalContext(VelocityPortlet portlet, 
                                      Context context,
                                      RunData rundata) throws Exception
    {
        JetspeedRunData jdata = (JetspeedRunData) rundata;
        SessionState customizationState = jdata.getPageSessionState();
        Profile profile = jdata.getCustomizedProfile();
        String mediaType = profile.getMediaType ();

        // set velocity variable of mediatype (displayed in the customizer menu)
        context.put("mtype", profile.getMediaType());

        // make the list of already used panes/portlets available through the 'runs' reference
        context.put("runs", AutoProfile.getPortletList(rundata));
        
        // we should first retrieve the portlet to customize
        PortletSet set = (PortletSet) (jdata).getCustomized();

        //identify the portlet submode and build the appropriate subt-template path
        String mode = rundata.getParameters().getString("mode");
        if (mode == null)
        {
          mode = (String) customizationState.getAttribute("customize-mode");
          if ((mode == null) || (mode.equalsIgnoreCase("addset")) || (mode.equalsIgnoreCase("general")))
          {
            mode = "layout";
          }
          
        }
        else
        {
          if ((mediaType.equalsIgnoreCase("wml")) && (!mode.equalsIgnoreCase("add")))
          {
            mode = "layout";
          }

          customizationState.setAttribute("customize-mode", mode);
        }

        String template = (String) context.get("template");

        if (template != null)
        {
            int idx = template.lastIndexOf(".");
            
            if (idx > 0)
            {
                template = template.substring(0, idx);
            }
            
            StringBuffer buffer = new StringBuffer(template);
            buffer.append("-").append(mode).append(".vm");
            
            template = TemplateLocator.locatePortletTemplate(rundata, buffer.toString());
            context.put("feature", template);

        }
    
        if (set == null)
        {
            return;
        }

        // get the customization state for this page
        String customizedPaneName = (String) customizationState.getAttribute("customize-paneName");
        if (customizedPaneName == null) 
        {
            customizedPaneName = "*";
        }

        // generic context stuff
        context.put("panename", customizedPaneName);
        context.put("skin", set.getPortletConfig().getPortletSkin());
        context.put("set", set);
        context.put("action", "portlets.CustomizeSetAction");        
        context.put("controllers", buildInfoList(rundata, Registry.PORTLET_CONTROLLER, mediaType));
        //context.put("skins", buildList(rundata, Registry.SKIN));
        //context.put("securitys", buildList(rundata, Registry.SECURITY));
        context.put("customizer", portlet);
    
        String controllerName = set.getController().getConfig().getName();
        context.put("currentController", controllerName);

        context.put("currentSecurityRef", set.getPortletConfig().getSecurityRef());

       /** 
        * Special handling for wml profiles
        * no skins, no properties menuentry, no panes 
        * --------------------------------------------------------------------------
        * last modified: 12/10/01
        * Andreas Kempf, Siemens ICM S CP OP, Munich
        * mailto: A.Kempf@web.de
        */

        if (mediaType.equalsIgnoreCase("wml"))
        {
          context.put("currentSkin", "Not for wml!");
          context.put("allowproperties", "false");
        }
        else
        {
          if (set.getPortletConfig().getSkin() != null)
          {
            context.put("currentSkin", set.getPortletConfig().getPortletSkin().getName());
          }
          context.put("allowproperties", "true");
        }


        context.put("allowpane", "false");
        
        // do not allow panes for wml profiles
        if ((!mediaType.equalsIgnoreCase("wml")) && (set.getController() instanceof PortletSetController))
        {
          if (customizedPaneName != null)
          {
            context.put("allowpane", "true");
          }
        }
        else
        {
            context.put("allowportlet", "true");
        }
        // --------------------------------------------------------------------------
            


        if ("add".equals(mode)) // build context for add mode
        {
            int start = rundata.getParameters().getInt("start", -1);
            if (start < 0)
            {
                //System.out.println("Clearing session variables");
                start = 0;
                PortletSessionState.clearAttribute(rundata, USER_SELECTIONS);
                PortletSessionState.clearAttribute(rundata, PORTLET_LIST);
            }
            
            ArrayList allPortlets = new ArrayList();
            List portlets  = buildPortletList(rundata, set, mediaType, allPortlets);
            Map userSelections = getUserSelections(rundata); 
            // Build a list of categories from the available portlets
            List categories = buildCategoryList(rundata, mediaType, allPortlets);
            context.put("categories", categories);
            
            context.put("parents", PortletFilter.buildParentList(allPortlets));
            addFiltersToContext(rundata, context);
            
            int size = getSize(portlet);               
            int end = Math.min(start + size, portlets.size());
                
            if (start > 0)
            {
                context.put("prev", String.valueOf(Math.max(start - size, 0)));
            }
                
            if (start + size < portlets.size())
            {
                context.put("next", String.valueOf(start + size));
            }
                
            context.put("browser", portlets.subList(start, end));
            context.put("size", new Integer(size));
            context.put(UI_PORTLETS_SELECTED, userSelections);
            
			context.put("portlets", portlets);
        }
        else if ("addref".equals(mode))
        {
            Iterator psmlIterator = null;
            psmlIterator = Profiler.query(new QueryLocator(QueryLocator.QUERY_ALL));
            
            // Set Start and End
            int start = rundata.getParameters().getInt("start", 0);                
            int size = getSize(portlet);
 

            // Only include entries in compatibale with the Media-type/Country/Language
            List psmlList = new LinkedList();
            Profile refProfile = null;
            int profileCounter = 0;
            while (psmlIterator.hasNext())
            {
                refProfile = (Profile) psmlIterator.next();
                
                if (refProfile.getMediaType() != null)
                {
                    if (profile.getMediaType().equals(refProfile.getMediaType()) == false)
                    {
                        continue;
                    }
                }
                
                if (profile.getLanguage() != null)
                {
                    if (refProfile.getLanguage() != null)
                    {
                        if (profile.getLanguage().equals(refProfile.getLanguage()) == true)
                        {
                            if (profile.getCountry() != null)
                            {
                                if (refProfile.getCountry() != null)
                                {
                                    if (profile.getCountry().equals(refProfile.getCountry()) == false)
                                    {
                                        // Profile and Ref are different countries
                                        continue;
                                    }
                                }
                            }
                            else
                            {
                                if (refProfile.getCountry() != null)
                                {
                                    // Profile has no country and Ref has a country
                                    continue;
                                }
                            }
                        }
                        else
                        {
                            // Profile and Ref are different languages
                            continue;
                        }
                    }
                }
                else
                {
                    if (refProfile.getLanguage() != null)
                    {
                        // Profile has no Language and Ref has a country
                        continue;
                    }
                }
                
                if (profile.getPath().equals(refProfile.getPath()) == true)
                {
                    // Do not allow Profile to reference it self
                    continue;
                }
                
                // Only add profiles to list that will be displayed
                if (profileCounter >= (start + size))
                {
                    break;
                }
                if (profileCounter >= start)
                {
                    psmlList.add(refProfile);
                }
                profileCounter++;
            }
               
            // Add Start to context
            if (start > 0)
            {
                context.put("prev", String.valueOf(Math.max(start - size, 0)));
            }
                
            // Set end to context
            if ((size == psmlList.size()) && (psmlIterator.hasNext()))
            {
                context.put("next", String.valueOf(start + size));
            }
            
            context.put("psml", psmlList.iterator());
        }
        else // build context for layout mode
        {
            // nothing specific to do
        }

    }

    public int getSize(VelocityPortlet portlet)
    {
        int size = 15;
        try
        {
            size = Integer.parseInt(portlet.getPortletConfig()
                                           .getInitParameter("size"));
        }
        catch (Exception e)
        {
            logger.debug("CustomizeSetAction: Init param 'size' not parsed");
        }
        return size;
    }

    /** Clean up the customization state */
    public void doCancel(RunData rundata, Context context)
    {
        //((JetspeedRunData)rundata).setCustomized(null);
        //rundata.setScreenTemplate("Home");
        SessionState customizationState = ((JetspeedRunData) rundata).getPageSessionState();
        customizationState.setAttribute("customize-mode", "layout");
    }

    /** Save the general informations for this set */
    public void doSave(RunData rundata, Context context)
    {
        doMetainfo(rundata, context);
        doSkin(rundata, context);
        doLayout(rundata, context);
        doSecurity(rundata, context);
        
        Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
        try
        {
            String mtype = rundata.getParameters().getString("mtype");

            if (mtype != null)
            {
                profile.setMediaType(mtype);
            }
            profile.store();
        }
        catch (Exception e)
        {
            logger.error("Exception occured while saving PSML", e);
        }

    }
    
    /** Save customizations and get out of customization state */
    public void doApply(RunData rundata, Context context)
    {
        doSave(rundata, context);
    }
    
    /** Add a new portlets element in the customized set */
    public void doAddset(RunData rundata, Context context)
    {
        PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
        String title = rundata.getParameters().getString("title", "My Pane");
        
        if (set != null)
        {
            Portlets portlets = ((JetspeedRunData) rundata).getCustomizedProfile()
                                                           .getDocument()
                                                           .getPortletsById(set.getID());
            
            if (portlets != null)
            {
                Portlets p = new PsmlPortlets();
                p.setMetaInfo(new PsmlMetaInfo());
                p.getMetaInfo().setTitle(title);
                p.setId(JetspeedIdGenerator.getNextPeid());
                SecurityReference defaultRef = PortalToolkit.getDefaultSecurityRef(
                    ((JetspeedRunData) rundata).getCustomizedProfile());
                if (defaultRef != null)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("CustomizeSetAction: setting default portlet set security to [" + defaultRef.getParent() + "]");
                    }
                    p.setSecurityRef(defaultRef);
                }
                portlets.addPortlets(p);
            }
        }

        SessionState customizationState = ((JetspeedRunData) rundata).getPageSessionState();
        customizationState.setAttribute("customize-mode", "layout");
    }
    
   public void doPrevious(RunData rundata, Context context) throws Exception
   {
       int queryStart = rundata.getParameters().getInt("previous", 0);
       String mtype = rundata.getParameters().getString("mtype", null);
       maintainUserSelections(rundata);
       JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
       DynamicURI duri = null;
       if (mtype == null)
       {
           duri = link.setTemplate("Customize").addQueryData("start", String.valueOf(queryStart));
       }
       else
       {
           duri = link.setTemplate("Customize").
               addQueryData("start", String.valueOf(queryStart)).
               addQueryData("mtype", mtype);
       }
       JetspeedLinkFactory.putInstance(link);
       rundata.setRedirectURI(duri.toString());
       return;
   }

   public void doNext(RunData rundata, Context context) throws Exception
   {
       int queryStart = rundata.getParameters().getInt("next", 0);
       String mtype = rundata.getParameters().getString("mtype", null);
       maintainUserSelections(rundata);
       JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
       DynamicURI duri = null;
       if (mtype == null)
       {
           duri = link.setTemplate("Customize").addQueryData("start", String.valueOf(queryStart));
       }
       else
       {
           duri = link.setTemplate("Customize").
               addQueryData("start", String.valueOf(queryStart)).
               addQueryData("mtype", mtype);
       }
       JetspeedLinkFactory.putInstance(link);
       rundata.setRedirectURI(duri.toString());
       return;
   }

   protected void maintainUserSelections(RunData rundata) throws Exception
   {
       int size = rundata.getParameters().getInt("size", 0);
       int previous = rundata.getParameters().getInt("previous", -1);
       int start = 0;
       if (previous >= 0)
       {
           start = previous + size;
       }

       String[] pnames = rundata.getParameters().getStrings("pname");
       //System.out.println("start = "+start+" size = "+size);
       //System.out.println("pnames = "+rundata.getParameters());
       Map userSelections = getUserSelections(rundata);
       List portlets = (List) PortletSessionState.getAttribute(rundata, PORTLET_LIST, null);
       if (portlets != null)
       {
           int end = Math.min(start + size, portlets.size());
           int pnamesIndex = 0;
           //Go through all the portlets on this page and figure out which ones have been 
           //checked and which ones unchecked and accordingly update the userSelectionMap
           for (int portletIndex = start; portletIndex < end; portletIndex++)
           {
               PortletEntry entry = (PortletEntry) portlets.get(portletIndex);
               if (pnames != null 
                   && pnamesIndex < pnames.length 
                   && pnames[pnamesIndex].equals(entry.getName()))
               {
                    userSelections.put(entry.getName(), entry);
                    pnamesIndex++;
               }
               else
               {
                    userSelections.remove(entry.getName());
               }
           }
           PortletSessionState.setAttribute(rundata, USER_SELECTIONS, userSelections);
           /*
           Iterator it = userSelections.keySet().iterator();           
           System.out.print("User Selections: ");
           while (it.hasNext())
           {
               System.out.print(", "+it.next());
           }
           System.out.println("\n");
           */
       }
       else
       {
           throw new Exception("Master Portlet List is null!");
       }

   }

    /** Add new portlets in the customized set */
    public void doAdd(RunData rundata, Context context) throws Exception
    {
        /** 
         * Special handling for wml profiles
         * no skins, no properties menuentry, no panes 
         * --------------------------------------------------------------------------
         * last modified: 10/31/01
         * Andreas Kempf, Siemens ICM S CP PE, Munich
         * mailto: A.Kempf@web.de
         */
        //boolean isWML = AutoProfile.doIt (rundata, true).getMediaType().equalsIgnoreCase("wml");
        PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
        
        maintainUserSelections(rundata);
        Map userSelections = getUserSelections(rundata);
        String[] pnames = new String[userSelections.size()];
        userSelections.keySet().toArray(pnames);
        //String[] pnames = rundata.getParameters().getStrings("pname");

        // Create a ClearPortletControl
        Control ctrl = new PsmlControl();
        ctrl.setName ("ClearPortletControl");

        
        if ((pnames != null) && (set != null))
        {
            Portlets portlets = ((JetspeedRunData) rundata).getCustomizedProfile()
                                                           .getDocument()
                                                           .getPortletsById(set.getID());

            List usedPortlets = AutoProfile.getPortletList(rundata);
            boolean addIt;
            int cc;
            Entry usedEntry;
                                          
            for (int i = 0; i < pnames.length; i++)
            {
                PortletEntry entry = (PortletEntry) Registry.getEntry(Registry.PORTLET, pnames[i]);

                // add only new portlets!
                if ((entry != null) && (portlets != null))
                {
                
                    addIt = true;
 /*
                    for (cc=0; cc<usedPortlets.size(); cc++)
                    {
                      usedEntry = (Entry) usedPortlets.get(cc);
                      if (entry.getName().equals(usedEntry.getParent()))
                      {
                        addIt = false;
                        break;
                      }
                      
                    }
*/
                    if (addIt)
                    {
                      Entry p = new PsmlEntry();
                      // add the ClearPortletControl to wml entries      
                      //if (isWML)
                      //  p.setControl (ctrl);
                    
                      p.setParent(pnames[i]);
                      p.setId(JetspeedIdGenerator.getNextPeid());
//                      SecurityReference defaultRef = PortalToolkit.getDefaultSecurityRef(
//                          ((JetspeedRunData) rundata).getCustomizedProfile());
//                      if (defaultRef != null)
//                      {
//                          if (Log.getLogger().isDebugEnabled())
//                          {
//                              Log.debug("CustomizeSetAction: setting default portlet security to [" + defaultRef.getParent() + "]");
//                          }
//                          p.setSecurityRef(defaultRef);
//                      }
                      portlets.addEntry(p);
                    }
                }
            }
        }
        // --------------------------------------------------------------------------

        SessionState customizationState = ((JetspeedRunData) rundata).getPageSessionState();
        customizationState.setAttribute("customize-mode", "layout");

        /** 
         * Save changed wml profile
         * --------------------------------------------------------------------------
         * last modified: 10/31/01
         * Andreas Kempf, Siemens ICM S CP PE, Munich
         * mailto: A.Kempf@web.de
        if (isWML)
        {
          ((JetspeedRunData)rundata).getCustomizedProfile().store();
          //rundata.save();
        }
         */
        
    }

    /** Add new Reference in the customized set */
    public void doAddref(RunData rundata, Context context) throws Exception
    {
        PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
        String[] refNames = rundata.getParameters().getStrings("refname");
        
        // Create a ClearPortletControl
        Control ctrl = new PsmlControl();
        ctrl.setName ("ClearPortletControl");

        
        if ((refNames != null) && (set != null))
        {
            Portlets portlets = ((JetspeedRunData) rundata).getCustomizedProfile()
                                                           .getDocument()
                                                           .getPortletsById(set.getID());

            for (int i = 0; i < refNames.length; i++)
            {
                SecurityReference sref = getSecurityReference(rundata, refNames[i]);
                if (sref != null)
                {
                    Reference ref = new PsmlReference();
                    ref.setPath(refNames[i]);              
                    ref.setSecurityRef(sref);
                    portlets.addReference(ref);
                }
                else
                {
                    String tmpl = CustomLocalization.getString("CUSTOMIZER_ADD_REF_ERROR", rundata);
                    Object[] args = {
                        refNames[i]
                    };
                    String message = MessageFormat.format(tmpl, args).toString();

                    rundata.addMessage(message.concat("<br>"));
                    if (logger.isWarnEnabled())
                    {
                        logger.warn(message);
                    }
                }
            }
        }
        SessionState customizationState = ((JetspeedRunData) rundata).getPageSessionState();
        customizationState.setAttribute("customize-mode", "layout");
    }

    /**
     * Get the security reference from the outer portlet set
     *
     * @param path the psml locator path
     * @return the security reference of the referenced resource     
     */
    protected SecurityReference getSecurityReference(RunData rundata, String path)
    {
        try
        {
            ProfileLocator locator = Profiler.createLocator();
            locator.createFromPath(path);
            Profile profile = Profiler.getProfile(locator);
            if (profile != null)
            {
                PSMLDocument doc = profile.getDocument();
                if (doc != null)
                {
                    Portlets rootSet = doc.getPortlets();
                    /*
                    There is no way to do a check on a Portlets element, only a Entry element.
                    This can easily be added, but Im just under a release right now and it 
                    could be perceived as too destabilizing -- david
                    
                    if (JetspeedSecurity.checkPermission((JetspeedUser) rundata.getUser(),
                                                         rootSet,
                                                         JetspeedSecurity.PERMISSION_VIEW))
                    {
                    */
                        return rootSet.getSecurityRef();    
        //            }
                }
            }
        }
        catch (ProfileException e)
        {
            logger.error("Exception", e);
        }
        return null;
    }
    
    /** Sets the metainfo for this entry */
    public void doMetainfo(RunData rundata, Context context)
    {
        PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
        String title = rundata.getParameters().getString("title");
        String description = rundata.getParameters().getString("description");

        if (set != null)
        {
            Portlets portlets = ((JetspeedRunData) rundata).getCustomizedProfile()
                                                           .getDocument()
                                                           .getPortletsById(set.getID());
            
            if (portlets != null)
            {
                MetaInfo meta = portlets.getMetaInfo();
                if (meta == null)
                {
                    meta = new PsmlMetaInfo();
                    portlets.setMetaInfo(meta);
                }
            
                if (title != null)
                {
                    meta.setTitle(title);
                    set.setTitle(title);
                }

                if(description != null)
                {
                    meta.setDescription(description);
                    set.setDescription(description);
                }
            }
        }
    }

    /** Updates the customized portlet entry */
    public void doLayout(RunData rundata, Context context)
    {
        // we should first retrieve the portlet to customize and its parameters
        // definition
        PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
        
        try
        {            
            String controller = rundata.getParameters().getString("controller");
            
            if (controller != null)
            {
                Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
                PortletController pc = PortalToolkit.getController(controller);

                if (pc != null)
                {
                    set.setController(pc);

                    Portlets portlets = profile.getDocument().getPortletsById(set.getID());

                    Controller c = portlets.getController();
                    if (c == null)
                    {
                        c = new PsmlController();
                        portlets.setController(c);
                    }
                    c.setName(controller);
                    
                    String linkedControl = pc.getConfig().getInitParameter("control");

                    if (linkedControl != null)
                    {
                        Control ctl = new PsmlControl();
                        ctl.setName(linkedControl);
                        portlets.setControl(ctl);
                    }
                    else
                    {
                        portlets.setControl(null);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }
        
    }

    /**
     * Set the skin in the PSML and the current PortletConfig
     * using the HTML parameter "skin".  If the parmeter is
     * missing or 'blank', then the skin is set to null.
     *
     */

    /**
     * Set the skin in the PSML and the current PortletConfig
     * using the HTML parameter "skin".  If the parmeter is
     * missing or 'blank', then the skin is set to null.
     *
     */
    public void doSkin(RunData rundata, Context context)
    {
        // we should first retrieve the portlet to customize and its parameters
        // definition
        PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
        
        try
        {            
            String skin = rundata.getParameters().getString("skin");
            Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
            Portlets portlets = profile.getDocument().getPortletsById(set.getID());
            
            // skin is neither null nor zero-length
            if ((skin != null) && (skin.trim().length() > 0))
            {
                PortletSkin s = PortalToolkit.getSkin(skin);

                if (s != null)
                {
                    set.getPortletConfig().setPortletSkin(s);

                    Skin psmlSkin = portlets.getSkin();
                    if (psmlSkin == null)
                    {
                        portlets.setSkin(new PsmlSkin());
                    }
                    portlets.getSkin().setName(skin);
                }
                else
                {
                    logger.warn("Unable to update skin for portlet set " 
                             + set.getID() + " because skin " + skin 
                             + " does not exist.");
                    return;
                }
            }
            else
            {
              // skin is either null or zero-length
              String custPortletSetID = portlets.getId();
              String rootPortletSetID = profile.getRootSet().getID();
              
              // set system default skin for root PSML element
              if (custPortletSetID != null && rootPortletSetID != null &&
                  custPortletSetID.equals(rootPortletSetID))
              {
                // get system default skin
                String defaultSkinName = JetspeedResources.getString("services.PortalToolkit.default.skin");
                PortletSkin defaultSkin = PortalToolkit.getSkin(defaultSkinName);
                
                if (defaultSkin != null)
                {
                  set.getPortletConfig().setPortletSkin((PortletSkin) defaultSkin);
                  Skin psmlSkin = portlets.getSkin();
                  
                  if (psmlSkin == null)
                  {
                    portlets.setSkin(new PsmlSkin());
                  }
                
                  portlets.getSkin().setName(defaultSkin.getName());
                }
                else
                {
                    logger.warn("Unable to set default skin for root portlet set " 
                             + set.getID() + " because skin " + skin 
                             + " does not exist.");
                    return;
                }
              }
              else
              {
                // By setting the skin to null, the parent's skin will be used.
                set.getPortletConfig().setPortletSkin((PortletSkin) null);
                portlets.setSkin(null);
              }
            }
        }
        catch (Exception e)
        {
            logger.error(e);
        }        
    }

    /**
     * Set the SecuirtyRef in the PSML and the current PortletConfig
     * using the HTML parameter "securityRef".  If the parmeter is
     * missing or 'blank', then the SecuriyReference is set to null.
     *
     */
    public void doSecurity(RunData rundata, Context context)
    {
        // we should first retrieve the portlet to customize and its parameters
        // definition
        PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
        
        try
        {            
            String securityRefName = rundata.getParameters().getString("securityRef");
            SecurityReference securityRef = null;
            Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
            Portlets portlets = profile.getDocument().getPortletsById(set.getID());

            if ((securityRefName != null) && (securityRefName.trim().length() > 0))
            {
                securityRef = new BaseSecurityReference();
                securityRef.setParent(securityRefName);
            }
            set.getPortletConfig().setSecurityRef(securityRef);
            portlets.setSecurityRef(securityRef);
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }        
    }
    

    // Create a list of all available portlets
    public static List buildPortletList(RunData data, PortletSet set, String mediaType, List allPortlets)
    {
        List list = new ArrayList();                                                                 
        Iterator i = Registry.get(Registry.PORTLET).listEntryNames();
        
        while (i.hasNext())
        {
            PortletEntry entry = (PortletEntry) Registry.getEntry(Registry.PORTLET,
                                                                 (String) i.next());
            
            Iterator medias;
            //Make a master portlet list, we will eventually us this to build a category list
            allPortlets.add(entry);
            // MODIFIED: Selection now takes care of the specified mediatype!
            if (JetspeedSecurity.checkPermission((JetspeedUser) data.getUser(), 
                                                 new PortalResource(entry), 
                                                 JetspeedSecurity.PERMISSION_VIEW)
              && ((!entry.isHidden()) 
                && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT))
                && entry.hasMediaType(mediaType)))
            {
                list.add(entry);
            }
        }
        
        String[] filterFields = (String[]) PortletSessionState.getAttribute(data, FILTER_FIELDS);
        String[] filterValues = (String[]) PortletSessionState.getAttribute(data, FILTER_VALUES);
        list = PortletFilter.filterPortlets(list, filterFields, filterValues);
        
        Collections.sort(list,
                new Comparator() {
                    public int compare(Object o1, Object o2)
                    {
                        String t1 = (((PortletEntry) o1).getTitle() != null)
                            ? ((PortletEntry) o1).getTitle().toLowerCase()
                            : ((PortletEntry) o1).getName().toLowerCase();
                        String t2 = (((PortletEntry) o2).getTitle() != null)
                            ? ((PortletEntry) o2).getTitle().toLowerCase()
                            : ((PortletEntry) o2).getName().toLowerCase();
                                                   
                        return t1.compareTo(t2);
                    }
                });
        //this is used only by maintainUserSelection - which does not need the 
        //portlet list to be regenrated       
        PortletSessionState.setAttribute(data, PORTLET_LIST, list);
        return list;
    }

    public static Map getUserSelections(RunData data)
    {
        Map userSelections = (Map) PortletSessionState.getAttribute(data, USER_SELECTIONS, null);
        if (userSelections == null)
        {
            userSelections = new HashMap();
            PortletSessionState.setAttribute(data, USER_SELECTIONS, userSelections);
        }
        return userSelections;
    }
    
    public static List buildInfoList(RunData data, String regName, String mediaType)
    {
        List list = new ArrayList();
        
        String mime = ((JetspeedRunData) data).getCapability()
                                              .getPreferredType()
                                              .toString();
                                             
        Iterator m = Registry.get(Registry.MEDIA_TYPE).listEntryNames();
//        String mediaName = "html";
//        
//        while(m.hasNext())
//        {
//            MediaTypeEntry me = (MediaTypeEntry)
//                Registry.getEntry(Registry.MEDIA_TYPE,(String)m.next());
//            
//            if (me!=null)
//            {
//                if (mime.equals(me.getMimeType()))
//                {
//                    mediaName = me.getName();
//                    break;
//                }
//            }
//        }
                    
        Iterator i = Registry.get(regName).listEntryNames();
        
        while (i.hasNext())
        {
            PortletInfoEntry entry = (PortletInfoEntry) Registry.getEntry(regName,
                                                                 (String) i.next());
            
            // MODIFIED: Selection now takes care of the specified mediatype!
            if (JetspeedSecurity.checkPermission((JetspeedUser) data.getUser(), 
                                                  new PortalResource(entry), 
                                                  JetspeedSecurity.PERMISSION_CUSTOMIZE)
               && ((!entry.isHidden())
                 && entry.hasMediaType(mediaType)))
            {
                list.add(entry);
            }
        }
        
        Collections.sort(list,
                new Comparator() {
                    public int compare(Object o1, Object o2)
                    {
                        String t1 = (((RegistryEntry) o1).getTitle() != null)
                            ? ((RegistryEntry) o1).getTitle()
                            : ((RegistryEntry) o1).getName();
                        String t2 = (((RegistryEntry) o2).getTitle() != null)
                            ? ((RegistryEntry) o2).getTitle()
                            : ((RegistryEntry) o2).getName();
                        
                        return t1.compareTo(t2);
                    }
                });
        
        return list;
    }
    
    public static List buildList(RunData data, String regName)
    {
        List list = new ArrayList();
        
        Iterator i = Registry.get(regName).listEntryNames();        
        while (i.hasNext())
        {
            RegistryEntry entry = Registry.getEntry(regName, (String) i.next());
            
            if (JetspeedSecurity.checkPermission((JetspeedUser) data.getUser(),
                                                 new PortalResource(entry),
                                                 JetspeedSecurity.PERMISSION_CUSTOMIZE)
               && (!entry.isHidden()))
            {
                list.add(entry);
            }
        }
        
        Collections.sort(list,
                new Comparator() {
                    public int compare(Object o1, Object o2)
                    {
                        String t1 = (((RegistryEntry) o1).getTitle() != null)
                            ? ((RegistryEntry) o1).getTitle()
                            : ((RegistryEntry) o1).getName();
                        String t2 = (((RegistryEntry) o2).getTitle() != null)
                            ? ((RegistryEntry) o2).getTitle()
                            : ((RegistryEntry) o2).getName();
                        
                        return t1.compareTo(t2);
                    }
                });
        
        return list;
    }
    
    /**
     * Builds a list of all portlet categories
     * @param RunData current requests RunData object
     * @param List portlets All available portlets
     */    
    public static List buildCategoryList(RunData data, String mediaType, List portlets)
    {
        boolean hideEmpties = JetspeedResources.getBoolean(HIDE_EMPTY_CATEGORIES, true);
        TreeMap catMap = new TreeMap();
        Iterator pItr = portlets.iterator();
        while (pItr.hasNext())
        {
            PortletEntry entry =  (PortletEntry) pItr.next();
            if (hideEmpties)
            {
                if (JetspeedSecurity.checkPermission((JetspeedUser) data.getUser(), 
                                         new PortalResource(entry), 
                                         JetspeedSecurity.PERMISSION_VIEW)
                    && ((!entry.isHidden()) 
                    && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT))
                    && entry.hasMediaType(mediaType)))
                {
                    Iterator cItr = entry.listCategories();
                    while (cItr.hasNext())
                    {
                        BaseCategory cat = (BaseCategory) cItr.next();
                        catMap.put(cat.getName(), cat);
                    }            
                }
            }
            else
            {
                Iterator cItr = entry.listCategories();
                while (cItr.hasNext())
                {
                    BaseCategory cat = (BaseCategory) cItr.next();
                    catMap.put(cat.getName(), cat);
                }            
            }
        }
        
        //BaseCategory allCat = new BaseCategory();
          //      allCat.setName("All Portlets");
            //    catMap.put(allCat.getName(), allCat);
        return new ArrayList(catMap.values());
        
    }
    
    /**
     * Adds a filter over the available portlets list based on category
     */
    public void doFiltercategory(RunData rundata, Context context) throws Exception
    {
        String filterCat = rundata.getParameters().getString("filter_category", "All Portlets");
        PortletSessionState.setAttribute(rundata, "filter_category", filterCat);
        maintainUserSelections(rundata);
        
        String mtype = rundata.getParameters().getString("mtype", null);
        JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
        DynamicURI duri = null;
        if (mtype == null)
        {
            duri = link.setTemplate("Customize").addQueryData("start", "0");
        }
        else
        {
            duri = link.setTemplate("Customize").addQueryData("start", "0").addQueryData("mtype", mtype);
        }
        JetspeedLinkFactory.putInstance(link);
        rundata.setRedirectURI(duri.toString());
        return;
    }
    
    /**
     * Adds a filter over the available portlets list based on category
     */
    public void doFilter(RunData rundata, Context context) throws Exception
    {
        String[] filterFields = rundata.getParameters().getStrings("filter_field");
        String[] filterValues = new String[filterFields.length];
        for(int i=0; i<filterFields.length; i++)
        {
            String filterField = filterFields[i];
            if(filterField != null)
            {
                String filterValue = rundata.getParameters().getString(filterField + ":filter_value");
                filterValues[i] = filterValue;
            }
         }
        
        PortletSessionState.setAttribute(rundata, FILTER_FIELDS, filterFields);
        PortletSessionState.setAttribute(rundata, FILTER_VALUES, filterValues);
         
        maintainUserSelections(rundata);
    
        String mtype = rundata.getParameters().getString("mtype", null);
        JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
        DynamicURI duri = null;
        if (mtype == null)
        {
            duri = link.setTemplate("Customize").addQueryData("start", "0");
        }
        else
        {
            duri = link.setTemplate("Customize").addQueryData("start", "0").addQueryData("mtype", mtype);
        }
        JetspeedLinkFactory.putInstance(link);
        rundata.setRedirectURI(duri.toString());
        return;
    }
    
    private void addFiltersToContext(RunData data, Context context)
    {
        String[] filterFields = (String[]) PortletSessionState.getAttribute(data, FILTER_FIELDS);
        String[] filterValues = (String[]) PortletSessionState.getAttribute(data, FILTER_VALUES);
        if(filterFields != null && filterValues != null && filterFields.length == filterValues.length)
        {
            for(int i=0; i<filterFields.length; i++)
            {
                String field = filterFields[i];
                String value = filterValues[i];
        
                context.put(field + "_filter_value", value);
            }
        }
    }
        
}
