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

// Turbine stuff
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.services.resources.ResourceService;

// Velocity Stuff
import org.apache.velocity.context.Context;

//Java
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.StringTokenizer;

//Jetspeed
import org.apache.commons.lang.SerializationUtils;
import org.apache.jetspeed.modules.actions.portlets.security.SecurityConstants;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.psmlmanager.PsmlManagerService;
import org.apache.jetspeed.om.profile.BasePSMLDocument;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.Group;

//castor support
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.xml.sax.InputSource;

// serialization support
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

/**
 * This action enables to update the psml entries
 *
 * @author <a href="mailto:david@apache.org">David Sean Taylor</a>
 * @version $Id: PsmlUpdateAction.java,v 1.18 2004/03/31 04:49:10 morciuch Exp $
 */
public class PsmlUpdateAction extends SecureVelocityPortletAction
{

    protected static final String PSML_REFRESH_FLAG = "psmlRefreshFlag";
    protected static final String TRUE = "true";
    protected static final String FALSE = "false";
    protected static final String CATEGORY_NAME = "categoryName";
    protected static final String CATEGORY_VALUE = "categoryValue";
    protected static final String COPY_FROM = "copyFrom";
    protected static final String COPY_TO = "copyTo";
    protected static final String TEMP_LOCATOR = "tempLocator";
    protected static final String PSML_UPDATE_PANE = "PsmlForm";
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PsmlUpdateAction.class.getName());    
    
    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     */
    /**
     * Build the normal state content for this portlet.
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildNormalContext( VelocityPortlet portlet,
                                       Context context,
                                       RunData rundata )
    {
        try
        {
            //
            // if there was an error, display the message
            //
            String msgid = rundata.getParameters().getString(SecurityConstants.PARAM_MSGID);
            if (msgid != null)
            {
                int id = Integer.parseInt(msgid);
                if (id < SecurityConstants.MESSAGES.length)
                    context.put(SecurityConstants.PARAM_MSG, SecurityConstants.MESSAGES[id]);

                // get the bad entered data and put it back for convenient update
                 ProfileLocator locator = (ProfileLocator)rundata.getUser().getTemp(TEMP_LOCATOR);
                if (locator != null)
                    context.put("profile", Profiler.createProfile(locator));
            }

            String mode = rundata.getParameters().getString(SecurityConstants.PARAM_MODE);
            context.put(SecurityConstants.PARAM_MODE, mode);
            String path = rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID);

            if(mode != null && mode.equals(SecurityConstants.PARAM_MODE_DELETE))
            {
                ProfileLocator locator = Profiler.createLocator();
                locator.createFromPath(path);
                Profile profile = Profiler.getProfile(locator);
               if (profile != null)
               {
                   rundata.getUser().setTemp(TEMP_LOCATOR, locator);
                   context.put("profile", profile);
               }
               else
                   logger.error("Profile for Path:"+path+" Not Found!");
            }

            if(mode != null && mode.equals(SecurityConstants.PARAM_MODE_INSERT))
            {
                org.apache.jetspeed.om.registry.Registry mediaTypes = Registry.get(Registry.MEDIA_TYPE);
                context.put("mediaTypes", mediaTypes.listEntryNames());
                if (msgid == null)
                {
                    if(path == null)
                    {
                        context.put(CATEGORY_NAME, "user");
                        context.put("categoryValue", "anon");
                        context.put("copyFrom", "user/anon/media-type/html/page/default.psml");
                    }
                    else
                    {
                        ProfileLocator tmpLocator = Profiler.createLocator();
                        tmpLocator.createFromPath(path);
                        Profile profile = Profiler.getProfile(tmpLocator);
                        if (profile != null)
                        {
                            rundata.getUser().setTemp(TEMP_LOCATOR, tmpLocator);
                            context.put("profile", profile);
                        }
                        String categoryName = "group";
                        String categoryValue = tmpLocator.getGroupName();
                        if (categoryValue == null)
                        {
                            categoryName = "role";
                            categoryValue = tmpLocator.getRoleName();
                            if (categoryValue == null)
                            {
                                categoryName = "user";
                                categoryValue = tmpLocator.getUserName();
                                if (categoryValue == null)
                                {
                                    categoryName = "user";
                                    categoryValue = "anon";
                                }
                            }

                        }
                        context.put(CATEGORY_NAME, categoryName);
                        context.put("categoryValue", categoryValue);
                        context.put("copyFrom", path);
                    }
                }
                else
                {
                    context.put(CATEGORY_NAME, rundata.getUser().getTemp(CATEGORY_NAME));
                    context.put(CATEGORY_VALUE, rundata.getUser().getTemp(CATEGORY_VALUE));
                    context.put(COPY_FROM, rundata.getUser().getTemp(COPY_FROM));
                }
            }

            if(mode != null && mode.equals("export"))
            {
                if (msgid == null)
                {
                    String tmpPath = JetspeedResources.getString(JetspeedResources.TEMP_DIRECTORY_KEY, "/tmp");
                    String exportPath = JetspeedResources.getString("psml.export.default.path",
                                                                    TurbineServlet.getRealPath(tmpPath));
                    if(path == null)
                    {
                        context.put(COPY_TO, exportPath);
                        context.put(COPY_FROM,
                                    Profiler.PARAM_USER +
                                    File.separator +
                                    Profiler.PARAM_ANON +
                                    File.separator +
                                    Profiler.PARAM_MEDIA_TYPE +
                                    File.separator +
                                    "html" +
                                    File.separator +
                                    Profiler.PARAM_PAGE +
                                    File.separator +
                                    Profiler.FULL_DEFAULT_PROFILE);
                    }
                    else
                    {
                        ProfileLocator tmpLocator = Profiler.createLocator();
                        tmpLocator.createFromPath(path);
                        Profile profile = Profiler.getProfile(tmpLocator);
                        if (profile != null)
                        {
                            rundata.getUser().setTemp(TEMP_LOCATOR, tmpLocator);
                            context.put("profile", profile);
                        }

                        String categoryName = Profiler.PARAM_GROUP;
                        String categoryValue = tmpLocator.getGroupName();
                        if (categoryValue == null)
                        {
                            categoryName = Profiler.PARAM_ROLE;
                            categoryValue = tmpLocator.getRoleName();
                            if (categoryValue == null)
                            {
                                categoryName = Profiler.PARAM_USER;
                                categoryValue = tmpLocator.getUserName();
                                if (categoryValue == null)
                                {
                                    categoryName = Profiler.PARAM_USER;
                                    categoryValue = Profiler.PARAM_ANON;
                                }
                            }

                        }

                        context.put(COPY_TO, exportPath + File.separator + tmpLocator.getName());
                        context.put(COPY_FROM, path);
                    }
                }
                else
                {
                    context.put(COPY_TO, rundata.getUser().getTemp(COPY_TO));
                    context.put(COPY_FROM, rundata.getUser().getTemp(COPY_FROM));
                }
            }

            if(mode != null && mode.equals("export_all"))
            {
                if (msgid == null)
                {
                    // get the PSML Root Directory
                    ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                                 .getResources(PsmlManagerService.SERVICE_NAME);
                    String root = serviceConf.getString("root", "/WEB-INF/psml");
                    context.put(COPY_TO, TurbineServlet.getRealPath(root));
                }
                else
                {
                    context.put(COPY_TO, rundata.getUser().getTemp(COPY_TO));
                }
            }

            if(mode != null && mode.equals("import"))
            {
                org.apache.jetspeed.om.registry.Registry mediaTypes = Registry.get(Registry.MEDIA_TYPE);
                context.put("mediaTypes", mediaTypes.listEntryNames());
                if (msgid == null)
                {
                    // get the PSML Root Directory
                    ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                                 .getResources(PsmlManagerService.SERVICE_NAME);
                    String root = serviceConf.getString("root", "/WEB-INF/psml");
                    root = TurbineServlet.getRealPath(root);

                    if(path == null)
                    {
                        context.put(CATEGORY_NAME, Profiler.PARAM_USER);
                        context.put("categoryValue", Profiler.PARAM_ANON);
                        context.put("copyFrom",
                                    root +
                                    File.separator +
                                    Profiler.PARAM_USER +
                                    File.separator +
                                    Profiler.PARAM_ANON +
                                    File.separator +
                                    Profiler.PARAM_MEDIA_TYPE +
                                    File.separator +
                                    "html" +
                                    File.separator +
                                    Profiler.PARAM_PAGE +
                                    File.separator +
                                    Profiler.FULL_DEFAULT_PROFILE);
                    }
                    else
                    {
                        ProfileLocator tmpLocator = Profiler.createLocator();
                        tmpLocator.createFromPath(path);
                        Profile profile = Profiler.getProfile(tmpLocator);
                        if (profile != null)
                        {
                            rundata.getUser().setTemp(TEMP_LOCATOR, tmpLocator);
                            context.put("profile", profile);
                        }
                        String categoryName = Profiler.PARAM_GROUP;
                        String categoryValue = tmpLocator.getGroupName();
                        if (categoryValue == null)
                        {
                            categoryName = Profiler.PARAM_ROLE;
                            categoryValue = tmpLocator.getRoleName();
                            if (categoryValue == null)
                            {
                                categoryName = Profiler.PARAM_USER;
                                categoryValue = tmpLocator.getUserName();
                                if (categoryValue == null)
                                {
                                    categoryName = Profiler.PARAM_USER;
                                    categoryValue = Profiler.PARAM_ANON;
                                }
                            }

                        }
                        context.put(CATEGORY_NAME, categoryName);
                        context.put("categoryValue", categoryValue);
                        String filePath = this.mapLocatorToFile(tmpLocator);
                        context.put("copyFrom", root + File.separator + filePath.toString());
                    }
                }
                else
                {
                    context.put(CATEGORY_NAME, rundata.getUser().getTemp(CATEGORY_NAME));
                    context.put(CATEGORY_VALUE, rundata.getUser().getTemp(CATEGORY_VALUE));
                    context.put(COPY_FROM, rundata.getUser().getTemp(COPY_FROM));
                }
            }

            if(mode != null && mode.equals("import_all"))
            {
                if (msgid == null)
                {
                    // get the PSML Root Directory
                    ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                                 .getResources(PsmlManagerService.SERVICE_NAME);
                    String root = serviceConf.getString("root", "/WEB-INF/psml");
                    context.put(COPY_FROM, TurbineServlet.getRealPath(root));
                }
                else
                {
                    context.put(COPY_FROM, rundata.getUser().getTemp(COPY_FROM));
                }
            }

        }
        catch (Exception e)
        {
            logger.error("Exception", e);
            rundata.setMessage("Error in PsmlUpdateAction: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }

    /**
     * Database Insert Action for Psml.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doInsert(RunData rundata, Context context)
        throws Exception
    {
        Profile profile = null;
        ProfileLocator locator = null;
        String categoryName = null;
        String categoryValue = null;
        String copyFrom = null;
        String name = null;

        try
        {
            categoryName = rundata.getParameters().getString("CategoryName");
            categoryValue = rundata.getParameters().getString("CategoryValue");
            copyFrom = rundata.getParameters().getString("CopyFrom");
            name = rundata.getParameters().getString("name");
            //
            //create a new locator and set its values according to users input
            //
            locator = Profiler.createLocator();
            if (categoryName.equalsIgnoreCase(Profiler.PARAM_GROUP))
            {
                locator.setGroupByName(categoryValue);
            }
            else if (categoryName.equalsIgnoreCase(Profiler.PARAM_ROLE))
            {
                locator.setRoleByName(categoryValue);
            }
            else if (categoryName.equalsIgnoreCase(Profiler.PARAM_USER))
            {
                locator.setUser(JetspeedSecurity.getUser(categoryValue));
            }
            else
            {
                locator.setAnonymous(true);
            }

            String tempVar = rundata.getParameters().getString("psml_mediatype");
            if (tempVar != null && tempVar.trim().length() > 0)
            {
                locator.setMediaType(tempVar);
            }

            tempVar = rundata.getParameters().getString("psml_language");
            if (tempVar != null && tempVar.trim().length() > 0)
            {
                locator.setLanguage(tempVar);
            }

            tempVar = rundata.getParameters().getString("psml_country");
            if (tempVar != null && tempVar.trim().length() > 0)
            {
                locator.setCountry(tempVar);
            }

            locator.setName(name);

            //check if profile to be created already exists
            if (PsmlManager.getDocument(locator) != null )
                throw new EntityExistsException("Profile:"+locator.getPath()+" Already Exists!");
            //
            // validate that its not an 'blank' profile -- not allowed
            //
            if (name == null || name.trim().length() == 0)
            {
                JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
                DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                      .addPathInfo(SecurityConstants.PARAM_MODE,
                                                   SecurityConstants.PARAM_MODE_INSERT)
                                      .addPathInfo(SecurityConstants.PARAM_MSGID,
                                                   SecurityConstants.MID_INVALID_ENTITY_NAME);
                JetspeedLinkFactory.putInstance(link);
                rundata.setRedirectURI(duri.toString());

                //save user entered values
                if (locator != null)
                    rundata.getUser().setTemp(TEMP_LOCATOR, locator);
                if (categoryName != null)
                    rundata.getUser().setTemp(CATEGORY_NAME, categoryName);
                if (categoryValue != null)
                    rundata.getUser().setTemp(CATEGORY_VALUE, categoryValue);
                if (copyFrom != null)
                    rundata.getUser().setTemp(COPY_FROM, copyFrom);
                return;
            }

            //
            // retrieve the profile to clone
            //
            ProfileLocator baseLocator = Profiler.createLocator();
            baseLocator.createFromPath(copyFrom);
            Profile baseProfile = Profiler.getProfile(baseLocator);

            //
            // create a new profile
            //
            if(baseProfile != null)
            {
                PSMLDocument doc = baseProfile.getDocument();
                if(doc != null)
                {
                    Portlets portlets = doc.getPortlets();
                    
                    Portlets clonedPortlets = (Portlets) SerializationUtils.clone(portlets);
                    org.apache.jetspeed.util.PortletUtils.regenerateIds(clonedPortlets);
                    profile = Profiler.createProfile(locator, clonedPortlets);
                }
                else
                {
                    profile = Profiler.createProfile(locator, null);
                }
                setRefreshPsmlFlag(rundata, TRUE);
            }
            else
            {
                logger.error("Profile listed in Copy From Not Found!");
            }
        }
        catch (EntityExistsException e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                  .addPathInfo(SecurityConstants.PARAM_MODE,
                                               SecurityConstants.PARAM_MODE_INSERT)
                                  .addPathInfo(SecurityConstants.PARAM_MSGID,
                                               SecurityConstants.MID_ENTITY_ALREADY_EXISTS);
            JetspeedLinkFactory.putInstance(link);
            rundata.setRedirectURI(duri.toString());
        }
        catch (Exception e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                  .addPathInfo(SecurityConstants.PARAM_MODE,
                                               SecurityConstants.PARAM_MODE_INSERT)
                                  .addPathInfo(SecurityConstants.PARAM_MSGID,
                                               SecurityConstants.MID_UPDATE_FAILED);
            JetspeedLinkFactory.putInstance(link);
            rundata.setRedirectURI(duri.toString());
        }
        // save values that user just entered so they don't have to re-enter
        if (locator != null)
           rundata.getUser().setTemp(TEMP_LOCATOR, locator);
        if (categoryName != null)
            rundata.getUser().setTemp(CATEGORY_NAME, categoryName);
        if (categoryValue != null)
            rundata.getUser().setTemp(CATEGORY_VALUE, categoryValue);
        if (copyFrom != null)
            rundata.getUser().setTemp(COPY_FROM, copyFrom);

    }

    /**
     * Delete Psml entry
     */
    public void doDelete(RunData rundata, Context context) throws Exception
    {
        try
        {
            ProfileLocator locator = (ProfileLocator)rundata.getUser().getTemp(TEMP_LOCATOR);
            if (locator != null)
            {
                Profiler.removeProfile(locator);
                setRefreshPsmlFlag(rundata, TRUE);
            }
            else
            {
                logger.error("ProfileLocator not found!");
            }
        }
        catch(Exception e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                  .addPathInfo(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_DELETE)
                                  .addPathInfo(SecurityConstants.PARAM_MSGID,
                                               SecurityConstants.MID_DELETE_FAILED);
            JetspeedLinkFactory.putInstance(link);
            rundata.setRedirectURI(duri.toString());
        }

    }

    public void setRefreshPsmlFlag(RunData rundata, String value)
    {
        rundata.getUser().setTemp(PSML_REFRESH_FLAG, TRUE);
    }

    /**
     * File Export Action for Psml.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doExport(RunData rundata, Context context)
        throws Exception
    {
        Profile profile = null;
        ProfileLocator locator = null;
        String copyTo = null;
        String copyFrom = null;

        try
        {
            copyFrom = rundata.getParameters().getString("CopyFrom");
            copyTo = rundata.getParameters().getString("CopyTo");

            //
            // retrieve the profile to clone
            //
            ProfileLocator baseLocator = Profiler.createLocator();
            baseLocator.createFromPath(copyFrom);
            Profile baseProfile = Profiler.getProfile(baseLocator);

            //
            // Export profile
            //
            if(baseProfile != null)
            {
                PSMLDocument doc = baseProfile.getDocument();
                if(doc != null)
                {
                    if (!this.saveDocument(copyTo,doc))
                        throw new Exception("Failed to save PSML document");
                    rundata.addMessage("Profile [" + copyFrom + "] has been saved to disk in [" + copyTo + "]<br>");
                }
            }
            else
            {
                logger.error("Profile listed in Copy From Not Found!");
            }
        }
        catch (Exception e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                  .addPathInfo(SecurityConstants.PARAM_MODE,
                                               "export")
                                  .addPathInfo(SecurityConstants.PARAM_MSGID,
                                               SecurityConstants.MID_UPDATE_FAILED);
            JetspeedLinkFactory.putInstance(link);
            rundata.setRedirectURI(duri.toString());
        }
        // save values that user just entered so they don't have to re-enter
        if (copyTo != null)
            rundata.getUser().setTemp(COPY_TO, copyTo);
        if (copyFrom != null)
            rundata.getUser().setTemp(COPY_FROM, copyFrom);

    }

    /**
     * File Export All Action for Psml.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doExportall(RunData rundata, Context context)
        throws Exception
    {
        String copyTo = null;

        logger.info("PsmlUpdateAction: Starting export all operation");

        try
        {
            copyTo = rundata.getParameters().getString("CopyTo");

            //
            // retrieve the profiles to export
            //
            Iterator i = Profiler.query(new QueryLocator(QueryLocator.QUERY_ALL));
            while (i.hasNext())
            {
                Profile profile = (Profile) i.next();
                PSMLDocument doc = profile.getDocument();
                if(doc != null)
                {
                    // Build the fully qualified file name
                    StringBuffer copyToFile = new StringBuffer(copyTo);
                    copyToFile.append(File.separator);
                    if (profile.getGroupName() != null)
                    {
                        copyToFile.append("group");
                        copyToFile.append(File.separator);
                        copyToFile.append(profile.getGroupName());
                        copyToFile.append(File.separator);
                    }
                    else if (profile.getRoleName() != null)
                    {
                        copyToFile.append("role");
                        copyToFile.append(File.separator);
                        copyToFile.append(profile.getRoleName());
                        copyToFile.append(File.separator);
                    }
                    else if (profile.getUserName() != null)
                    {
                        copyToFile.append("user");
                        copyToFile.append(File.separator);
                        copyToFile.append(profile.getUserName());
                        copyToFile.append(File.separator);
                    }
                    if (profile.getMediaType() != null)
                    {
                        copyToFile.append(profile.getMediaType());
                        copyToFile.append(File.separator);
                    }
                    if (profile.getLanguage() != null)
                    {
                        copyToFile.append(profile.getLanguage());
                        copyToFile.append(File.separator);
                    }
                    if (profile.getCountry() != null)
                    {
                        copyToFile.append(profile.getCountry());
                        copyToFile.append(File.separator);
                    }
                    copyToFile.append(profile.getName());

                    if (!this.saveDocument(copyToFile.toString(), doc)) {
                        logger.error("Failed to save PSML document for [" + profile.getPath());
                    } else {
                        String msg = "Profile [" + profile.getPath() + "] has been saved to disk in [" + copyToFile.toString() + "]<br>";
                        logger.info(msg);
                        rundata.addMessage(msg);
                    }
                }
            }

        }
        catch (Exception e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                  .addPathInfo(SecurityConstants.PARAM_MODE,
                                               "export_all")
                                  .addPathInfo(SecurityConstants.PARAM_MSGID,
                                               SecurityConstants.MID_UPDATE_FAILED);
            JetspeedLinkFactory.putInstance(link);
            rundata.setRedirectURI(duri.toString());
        }
        // save values that user just entered so they don't have to re-enter
        if (copyTo != null) {
            rundata.getUser().setTemp(COPY_TO, copyTo);
        }

        logger.info("PsmlUpdateAction: Ending export all operation");
    }

    /** Save the PSML document on disk to the specififed fileOrUrl
     *
     * @param fileOrUrl a String representing either an absolute URL
     * or an absolute filepath
     * @param doc the document to save
     */
    private boolean saveDocument(String fileOrUrl, PSMLDocument doc)
    {
        boolean success = false;

        if (doc == null) return false;
        File f = new File(fileOrUrl);
        File d = new File(f.getParent());
        d.mkdirs();

        FileWriter writer = null;

        try
        {
            writer = new FileWriter(f);
            // create the serializer output format
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(4);
            Serializer serializer = new XMLSerializer(writer,format);
            Marshaller marshaller = new Marshaller(serializer.asDocumentHandler());
            marshaller.setMapping(this.loadMapping());
            marshaller.marshal(doc.getPortlets());

            success = true;
        }
        catch (MarshalException e)
        {
            logger.error("PSMLUpdateAction: Could not marshal the file " + f.getAbsolutePath(), e);
        }
        catch (MappingException e)
        {
            logger.error("PSMLUpdateAction: Could not marshal the file " + f.getAbsolutePath(), e);
        }
        catch (ValidationException e)
        {
            logger.error("PSMLUpdateAction: document "+f.getAbsolutePath() + " is not valid", e);
        }
        catch (IOException e)
        {
            logger.error("PSMLUpdateAction: Could not save the file " + f.getAbsolutePath(), e);
        }
        catch (Exception e)
        {
            logger.error("PSMLUpdateAction: Error while saving  " + f.getAbsolutePath(), e);
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch (IOException e)
            {
            }
        }

        return success;
    }

    /**
     * Loads psml mapping file
     *
     * @exception Exception
     */
    private Mapping loadMapping()
        throws Exception
    {
        // get configuration parameters from Jetspeed Resources
        ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                     .getResources(PsmlManagerService.SERVICE_NAME);

        // test the mapping file and create the mapping object
        Mapping mapping = null;
        String mapFile = serviceConf.getString("mapping","${webappRoot}/WEB-INF/conf/psml-mapping.xml");
        mapFile = TurbineServlet.getRealPath( mapFile );
        if (mapFile != null)
        {
            File map = new File(mapFile);
            if ( logger.isDebugEnabled() )
            {
                logger.debug( "Loading psml mapping file " + mapFile );
            }
            if (map.exists() && map.isFile() && map.canRead())
            {
                try
                {
                    mapping = new Mapping();
                    InputSource is = new InputSource( new FileReader(map) );
                    is.setSystemId( mapFile );
                    mapping.loadMapping( is );
                }
                catch (Exception e)
                {
                    logger.error("Error in psml mapping creation", e);
                    throw new Exception("Error in mapping");
                }
            }
            else
            {
                throw new Exception("PSML Mapping not found or not a file or unreadable: " + mapFile);
            }
        }

        return mapping;
    }

    /**
     * File Import Action for Psml.
     *
     * TODO: Implement file upload.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doImport(RunData rundata, Context context)
        throws Exception
    {
        Profile profile = null;
        ProfileLocator locator = null;
        String categoryName = null;
        String categoryValue = null;
        String copyFrom = null;
        String name = null;

        try
        {
            categoryName = rundata.getParameters().getString("CategoryName");
            categoryValue = rundata.getParameters().getString("CategoryValue");
            copyFrom = rundata.getParameters().getString("CopyFrom");
            name = rundata.getParameters().getString("name");
            //
            //create a new locator and set its values according to users input
            //
            locator = Profiler.createLocator();
            if (categoryName.equalsIgnoreCase(Profiler.PARAM_GROUP))
            {
                locator.setGroupByName(categoryValue);
            }
            else if (categoryName.equalsIgnoreCase(Profiler.PARAM_ROLE))
            {
                locator.setRoleByName(categoryValue);
            }
            else if (categoryName.equalsIgnoreCase(Profiler.PARAM_USER))
            {
                locator.setUser(JetspeedSecurity.getUser(categoryValue));
            }
            else
            {
                locator.setAnonymous(true);
            }

            String tempVar = rundata.getParameters().getString("psml_mediatype");
            if (tempVar != null && tempVar.trim().length() > 0)
            {
                locator.setMediaType(tempVar);
            }

            tempVar = rundata.getParameters().getString("psml_language");
            if (tempVar != null && tempVar.trim().length() > 0)
            {
                locator.setLanguage(tempVar);
            }

            tempVar = rundata.getParameters().getString("psml_country");
            if (tempVar != null && tempVar.trim().length() > 0)
            {
                locator.setCountry(tempVar);
            }

            locator.setName(name);

            //
            // validate that its not an 'blank' profile -- not allowed
            //
            if (name == null || name.trim().length() == 0)
            {
                JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
                DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                      .addPathInfo(SecurityConstants.PARAM_MODE,
                                                   "import")
                                      .addPathInfo(SecurityConstants.PARAM_MSGID,
                                                   SecurityConstants.MID_INVALID_ENTITY_NAME);
                JetspeedLinkFactory.putInstance(link);
                rundata.setRedirectURI(duri.toString());

                //save user entered values
                if (locator != null)
                {
                    rundata.getUser().setTemp(TEMP_LOCATOR, locator);
                }
                if (categoryName != null)
                {
                    rundata.getUser().setTemp(CATEGORY_NAME, categoryName);
                }
                if (categoryValue != null)
                {
                    rundata.getUser().setTemp(CATEGORY_VALUE, categoryValue);
                }
                if (copyFrom != null)
                {
                    rundata.getUser().setTemp(COPY_FROM, copyFrom);
                }
                return;
            }

            //
            // Retrieve the document to import
            //
            PSMLDocument doc = this.loadDocument(copyFrom);

            //
            // create a new profile
            //
            if(doc != null)
            {
                Portlets portlets = doc.getPortlets();
                //
                // Profiler does not provide update capability - must remove before replacing
                //
                if (PsmlManager.getDocument(locator) != null)
                {
                    Profiler.removeProfile(locator);
                }
                
                Portlets clonedPortlets = (Portlets) SerializationUtils.clone(portlets);
                org.apache.jetspeed.util.PortletUtils.regenerateIds(clonedPortlets);
                
                profile = Profiler.createProfile(locator, clonedPortlets);
            }
            else
            {
                throw new Exception("Failed to load PSML document from disk");
            }
            rundata.addMessage("Profile for [" + locator.getPath() + "] has been imported from file [" + copyFrom + "]<br>");
            setRefreshPsmlFlag(rundata, TRUE);

        }
        catch (Exception e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                  .addPathInfo(SecurityConstants.PARAM_MODE,
                                               "import")
                                  .addPathInfo(SecurityConstants.PARAM_MSGID,
                                               SecurityConstants.MID_UPDATE_FAILED);
            JetspeedLinkFactory.putInstance(link);
            rundata.setRedirectURI(duri.toString());
        }
        // save values that user just entered so they don't have to re-enter
        if (locator != null)
        {
           rundata.getUser().setTemp(TEMP_LOCATOR, locator);
        }
        if (categoryName != null)
        {
            rundata.getUser().setTemp(CATEGORY_NAME, categoryName);
        }
        if (categoryValue != null)
        {
            rundata.getUser().setTemp(CATEGORY_VALUE, categoryValue);
        }
        if (copyFrom != null)
        {
            rundata.getUser().setTemp(COPY_FROM, copyFrom);
        }

    }

    /**
     * File Import All Action for Psml.
     *
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doImportall(RunData rundata, Context context)
        throws Exception
    {
        String copyFrom = null;

        try
        {
            copyFrom = rundata.getParameters().getString("CopyFrom");

            //
            // Collect all .psml files from the root specified
            //
            Vector files = new Vector();
            this.collectPsml(files, copyFrom);

            //
            // Process each file
            //
            for (Iterator it = files.iterator(); it.hasNext(); )
            {
                // If error occurs processing one entry, continue on with the others
                String path = null;
                try
                {
                    String psml = ((File) it.next()).getPath();
                    path = psml.substring(copyFrom.length() + 1);
                    ProfileLocator locator = this.mapFileToLocator(path);

                    PSMLDocument doc = this.loadDocument(psml);

                    //
                    // create a new profile
                    //
                    if(doc != null)
                    {
                        Portlets portlets = doc.getPortlets();
                        //
                        // Profiler does not provide update capability - must remove before replacing
                        //
                        if (PsmlManager.getDocument(locator) != null)
                        {
                            Profiler.removeProfile(locator);
                        }
                        Profiler.createProfile(locator, portlets);
                    }
                    else
                    {
                        throw new Exception("Failed to load PSML document [" + psml + "] from disk");
                    }
                    rundata.addMessage("Profile for [" + locator.getPath() + "] has been imported from file [" + psml + "]<br>");
                    setRefreshPsmlFlag(rundata, TRUE);
                }
                catch (Exception ouch)
                {
                    logger.error("Exception", ouch);
                    rundata.addMessage("ERROR importing file [" + path + "]: " + ouch.toString() + "<br>");
                }
            }

        }
        catch (Exception e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
            DynamicURI duri = link.getPaneByName(PSML_UPDATE_PANE)
                                  .addPathInfo(SecurityConstants.PARAM_MODE,
                                               "import_all")
                                  .addPathInfo(SecurityConstants.PARAM_MSGID,
                                               SecurityConstants.MID_UPDATE_FAILED);
            JetspeedLinkFactory.putInstance(link);
            rundata.setRedirectURI(duri.toString());
        }
        // save values that user just entered so they don't have to re-enter
        if (copyFrom != null)
        {
            rundata.getUser().setTemp(COPY_FROM, copyFrom);
        }

    }

    /**
     * This method recursively collect all .psml documents starting at the given root
     *
     * @param v      Vector to put the file into
     * @param root   Root directory for import
     */
    private void collectPsml(Vector v, String root)
    {

        File dir = new File(root);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
                collectPsml(v, files[i].getPath());
            }
            else if (files[i].isFile() && files[i].getPath().endsWith(".psml"))
            {
                v.add(files[i]);
            }
        }

    }

    /**
     * Creates profile locator from a given path in the format:
     *
     *   user/<name>/<mediaType>/<language>/<country>/<page>/
     *
     *   group/ ""
     *   role/  ""
     *
     * @param path The formatted profiler path string.
     * @param path   fully qualified .psml file name
     * @return profile locator
     */
    private ProfileLocator mapFileToLocator(String path)
    throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("PsmlUpdateAction.createFromPath: processing path = " + path);
        }
        ProfileLocator result = Profiler.createLocator();

        // Tokenize the file path into elements
        StringTokenizer tok = new StringTokenizer(path, File.separator);

        // Load path elements into a vector for random access
        Vector tokens = new Vector();
        while (tok.hasMoreTokens())
        {
            tokens.add(tok.nextToken());
        }

        // Assume that 1st element is the profile type (user|role|group) and 2nd is the name
        if (tokens.size() > 1)
        {
            String type = (String) tokens.elementAt(0);
            String name = (String) tokens.elementAt(1);
            if (type.equals(Profiler.PARAM_USER))
            {
                result.setUser(JetspeedSecurity.getUser(name));
            }
            else if (type.equals(Profiler.PARAM_GROUP))
            {
                result.setGroup(JetspeedSecurity.getGroup(name));
            }
            else if (type.equals(Profiler.PARAM_ROLE))
            {
                result.setRole(JetspeedSecurity.getRole(name));
            }
        }

        // Assume that the last element is the page name
        if (tokens.size() > 0)
        {
            result.setName((String) tokens.lastElement());
        }

        // Based on the number of path elements set the other profile attributes
        switch (tokens.size())
        {
        case 3: // user|role|group/name/page.psml
            break;
        case 4: // user|role|group/name/media-type/page.psml
            result.setMediaType((String) tokens.elementAt(2));
            break;
        case 5: // user|role|group/name/media-type/language/page.psml
            result.setMediaType((String) tokens.elementAt(2));
            result.setLanguage((String) tokens.elementAt(3));
            break;
        case 6: // user|role|group/name/media-type/language/country/page.psml
            result.setMediaType((String) tokens.elementAt(2));
            result.setLanguage((String) tokens.elementAt(3));
            result.setCountry((String) tokens.elementAt(4));
            break;
        default:
            throw new Exception("Path must contain 3 to 6 elements: [" + path + "], and the size was: " + tokens.size());
        }

        return result;
    }

    /**
     * Maps a ProfileLocator to a file.
     *
     * @param locator The profile locator describing the PSML resource to be found.
     * @return the String path of the file.
     */
    private String mapLocatorToFile(ProfileLocator locator)
    {
        StringBuffer path = new StringBuffer();

        // move the base dir is either user or role is specified
        Role role = locator.getRole();
        Group group = locator.getGroup();
        JetspeedUser user = locator.getUser();

        if (user != null)
        {
            path.append(Profiler.PARAM_USER);
            String name = user.getUserName();
            if (null != name && name.length() > 0)
            {
                path.append(File.separator)
                    .append(name);
            }
        }
        else if (group != null)
        {
            path.append(Profiler.PARAM_GROUP);
            String name = group.getName();
            if (null != name && name.length() > 0)
            {
                path.append(File.separator)
                    .append(name);
            }
        }
        else if (null != role)
        {
            path.append(Profiler.PARAM_ROLE);
            String name = role.getName();
            if (null != name && name.length() > 0)
            {
                path.append(File.separator)
                    .append(name);
            }
        }

        // Media
        if (null != locator.getMediaType())
        {
            path.append(File.separator)
                .append(locator.getMediaType());
        }
        // Language
        if (null != locator.getLanguage())
        {
            path.append(File.separator)
                .append(locator.getLanguage());
        }
        // Country
        if (null != locator.getCountry())
        {
            path.append(File.separator)
                .append(locator.getCountry());
        }
        // Resource Name
        if (null != locator.getName())
        {
            if (!(locator.getName().endsWith(Profiler.DEFAULT_EXTENSION)))
            {
                path.append(File.separator)
                    .append(locator.getName()).append(Profiler.DEFAULT_EXTENSION);
            }
            else
            {
                path.append(File.separator)
                    .append(locator.getName());
            }
        }
        else
        {
            path.append(File.separator)
                .append(Profiler.FULL_DEFAULT_PROFILE);
        }

        return  path.toString();
    }

    /**
     * Load a PSMLDOcument from disk
     *
     * @param fileOrUrl a String representing either an absolute URL or an
     * absolute filepath
     */
    private PSMLDocument loadDocument(String fileOrUrl)
    {
        PSMLDocument doc = null;

        if (fileOrUrl != null)
        {

            // we'll assume the name is the the location of the file
            File f = null;

            f = new File(fileOrUrl);

            if (!f.exists())
            {
                return null;
            }

            doc = new BasePSMLDocument();
            doc.setName(fileOrUrl);

            // now that we have a file reference, try to load the serialized PSML
            Portlets portlets = null;
            FileReader reader = null;
            try
            {
                reader = new FileReader(f);

                Unmarshaller unmarshaller = new Unmarshaller(this.loadMapping());
                portlets = (Portlets) unmarshaller.unmarshal(reader);

                doc.setPortlets(portlets);

            }
            catch (IOException e)
            {
                logger.error("PSMLUpdateAction: Could not load the file " + f.getAbsolutePath(), e);
            }
            catch (MarshalException e)
            {
                logger.error("PSMLUpdateAction: Could not unmarshal the file " + f.getAbsolutePath(), e);
            }
            catch (MappingException e)
            {
                logger.error("PSMLUpdateAction: Could not unmarshal the file " + f.getAbsolutePath(), e);
            }
            catch (ValidationException e)
            {
                logger.error("PSMLUpdateAction: document " + f.getAbsolutePath() + " is not valid", e);
            }
            catch (Exception e)
            {
                logger.error("PSMLUpdateAction: Error while loading  " + f.getAbsolutePath(), e);
            }
            finally
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                }
            }
        }

        return doc;
    }

}


