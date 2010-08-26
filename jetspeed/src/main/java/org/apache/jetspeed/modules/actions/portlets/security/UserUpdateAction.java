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

package org.apache.jetspeed.modules.actions.portlets.security;

// java util
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.jetspeed.modules.actions.portlets.SecureVelocityPortletAction;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.security.NotUniqueUserException;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;
import org.apache.velocity.context.Context;

// Email Stuff
import org.apache.commons.mail.SimpleEmail;

// Lang Stuff
import org.apache.commons.lang.StringEscapeUtils;

/**
 * This action sets up the template context for editing users in the Turbine database.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:kimptoc_mail@yahoo.com">Chris Kimpton</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: UserUpdateAction.java,v 1.17 2004/03/31 04:49:10 morciuch Exp $
 */
public class UserUpdateAction extends SecureVelocityPortletAction
{
    private static final String TEMP_USER = "tempUser";
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(UserUpdateAction.class.getName());     
    
    /**
     * Build the maximized state content for this portlet. (Same as normal state).
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildMaximizedContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {
        buildNormalContext( portlet, context, rundata);
    }

    /**
     * Build the configure state content for this portlet.
     * TODO: we could configure this portlet with configurable skins, etc..
     *
     * @param portlet The velocity-based portlet that is being built.
     * @param context The velocity context for this request.
     * @param rundata The turbine rundata context for this request.
     */
    protected void buildConfigureContext( VelocityPortlet portlet,
                                          Context context,
                                          RunData rundata )
    {

        buildNormalContext( portlet, context, rundata);
        setTemplate(rundata, "user-form.vm");
    }

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
            JetspeedUser user = null;
            /*
             * Grab the mode for the user form.
             */
            String mode = rundata.getParameters().getString(SecurityConstants.PARAM_MODE);

            if (mode != null && (mode.equals(SecurityConstants.PARAM_MODE_UPDATE) ||
                                 mode.equals(SecurityConstants.PARAM_MODE_DELETE)))
            {
                // get the primary key and put the object in the context
                String username = rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID);
                user = JetspeedSecurity.getUser(username);
                context.put(SecurityConstants.CONTEXT_USER, user);
            }

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
                JetspeedUser tempUser = (JetspeedUser)rundata.getUser().getTemp(TEMP_USER);
                if (tempUser != null)
                    context.put(SecurityConstants.CONTEXT_USER, tempUser);

            }

            context.put(SecurityConstants.PARAM_MODE, mode);

        }
        catch (Exception e)
        {
            logger.error("Error in Jetspeed User Security", e);
            rundata.setMessage("Error in Jetspeed User Security: " + e.toString());
            rundata.setStackTrace(StringUtils.stackTrace(e), e);
            rundata.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }

    /**
     * Database Insert Action for Users. Performs inserts into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doInsert(RunData rundata, Context context)
        throws Exception
    {
        JetspeedUser user = null;
        try
        {
            //
            // validate that its not an 'blank' rolename -- not allowed
            //
            String name = rundata.getParameters().getString("username");
            if (name == null || name.trim().length() == 0)
            {
                DynamicURI duri = new DynamicURI (rundata);
                duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_USER_UPDATE);
                duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_INVALID_ENTITY_NAME);
                rundata.setRedirectURI(duri.toString());
                // save values that user just entered so they don't have to re-enter
                if (user != null)
                   rundata.getUser().setTemp(TEMP_USER, user);
                return;
            }


            //
            // create a new user
            //
            user = JetspeedSecurity.getUserInstance();
            rundata.getParameters().setProperties(user);

            String password = rundata.getParameters().getString("password");
            if (password == null)
                password = "";

            user.setUserName(JetspeedSecurity.convertUserName(user.getUserName()));

            Date now = new Date();
            user.setCreateDate(now);
            user.setLastLogin(now);
            user.setConfirmed(JetspeedResources.CONFIRM_VALUE);

            String disabled = rundata.getParameters().getString("disabled");
            user.setDisabled( disabled );

            //
            // add the user
            ///
            user.setPassword(password);
            JetspeedSecurity.addUser(user);
        }
        catch (NotUniqueUserException e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // dup key found - display error message - bring back to same screen
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_USER_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_ENTITY_ALREADY_EXISTS);
            rundata.setRedirectURI(duri.toString());
            // save values that user just entered so they don't have to re-enter
            if (user != null)
               rundata.getUser().setTemp(TEMP_USER, user);
        }

    }

    /**
     * Database Update Action for Users. Performs accepting of new users into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doAccept(RunData rundata, Context context)
    throws Exception
    {
        JetspeedUser user = null;
        try
        {
            //
            // get the user object from the selected entry in the browser
            //
            user = (JetspeedUser)JetspeedSecurity.getUser(
                                           rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID));

            user.setConfirmed(JetspeedResources.CONFIRM_VALUE);

            //
            // update the user in the database
            //
            JetspeedSecurity.saveUser(user);


            //
            // tell the user that they can now use jetspeed
            //

            DynamicURI url = new DynamicURI(rundata);

            //build body via template
            StringWriter email_body = new StringWriter();
            SimpleEmail se = new SimpleEmail();
            String charset = JetspeedResources.getString("newuser.notification.charset","iso-8859-1");
            se.setCharset(charset);
            Context emailContext = TurbineVelocity.getContext();
            emailContext.put( "firstname", StringEscapeUtils.unescapeHtml(user.getFirstName()) );
            emailContext.put( "lastname", StringEscapeUtils.unescapeHtml(user.getLastName()) );
            emailContext.put( "username", StringEscapeUtils.unescapeHtml(user.getUserName()) );
            emailContext.put( "data", rundata );
            emailContext.put( "user", user );
            emailContext.put( "config",new JetspeedResources());
            emailContext.put( "urltojetspeed",url);
            emailContext.put( "email",se);

            //determine the language to be used for the notification email
            String language = (String)user.getPerm("language",TurbineResources.getString("locale.default.language", "en"));
            String country = (String)user.getPerm("country",TurbineResources.getString("locale.default.country", "US"));
            Locale locale = new Locale(language,country);

            String templateFile = JetspeedResources.getString("newuser.approval.accept.template");
            String templatePath = TemplateLocator.locateEmailTemplate(rundata, templateFile, locale );
            TurbineVelocity.handleRequest(emailContext, templatePath, email_body);

            se.setMsg( email_body.toString() );

            Properties props = System.getProperties();
            String mailServerMachine = JetspeedResources.getString( "mail.server" );
            props.put ( "mail.host", mailServerMachine );
            props.put("mail.smtp.host", mailServerMachine);

            se.send();


        } catch (Exception e)
        {
            // log the error msg
            logger.error("Exception", e);

            //
            // error on update - display error message
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_USER_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_UPDATE_FAILED);
            if (user != null)
                duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, user.getUserName());
            duri.addQueryData(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_UPDATE);
            rundata.setRedirectURI(duri.toString());
            // save values that user just entered so they don't have to re-enter
            if (user != null)
                rundata.getUser().setTemp(TEMP_USER, user);
        }
     }

    /**
     * Database Update Action for Users. Performs accepting of new users into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
     public void doReject(RunData rundata, Context context)
    throws Exception
    {
        JetspeedUser user = null;
        try
        {
            //
            // get the user object from the selected entry in the browser
            //
            user = (JetspeedUser)JetspeedSecurity.getUser(
                                           rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID));

            user.setConfirmed(JetspeedResources.CONFIRM_VALUE_REJECTED);

            //
            // update the user in the database
            //
            JetspeedSecurity.saveUser(user);



            //
            // tell the user that they can now use jetspeed
            //

            DynamicURI url = new DynamicURI(rundata);

            //build body via template
            StringWriter email_body = new StringWriter();
            SimpleEmail se = new SimpleEmail();
            String charset = JetspeedResources.getString("newuser.notification.charset","iso-8859-1");
            se.setCharset(charset);
            Context emailContext = TurbineVelocity.getContext();
            emailContext.put( "firstname", StringEscapeUtils.unescapeHtml(user.getFirstName()) );
            emailContext.put( "lastname", StringEscapeUtils.unescapeHtml(user.getLastName()) );
            emailContext.put( "username", StringEscapeUtils.unescapeHtml(user.getUserName()) );
            emailContext.put( "data", rundata );
            emailContext.put( "user", user );
            emailContext.put( "config",new JetspeedResources());
            emailContext.put( "urltojetspeed",url);
            emailContext.put( "email",se);

            //determine the language to be used for the notification email
            String language = (String)user.getPerm("language",TurbineResources.getString("locale.default.language", "en"));
            String country = (String)user.getPerm("country",TurbineResources.getString("locale.default.country", "US"));
            Locale locale = new Locale(language,country);

            String templateFile = JetspeedResources.getString("newuser.approval.reject.template");
            String templatePath = TemplateLocator.locateEmailTemplate(rundata, templateFile, locale );
            TurbineVelocity.handleRequest(emailContext, templatePath, email_body);

            se.setMsg( email_body.toString() );

            Properties props = System.getProperties();
            String mailServerMachine = JetspeedResources.getString( "mail.server" );
            props.put ( "mail.host", mailServerMachine );
            props.put("mail.smtp.host", mailServerMachine);

            se.send();

        } catch (Exception e)
        {
            // log the error msg
              logger.error("Exception", e);

            //
            // error on update - display error message
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_USER_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_UPDATE_FAILED);
            if (user != null)
                duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, user.getUserName());
            duri.addQueryData(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_UPDATE);
            rundata.setRedirectURI(duri.toString());
            // save values that user just entered so they don't have to re-enter
            if (user != null)
                rundata.getUser().setTemp(TEMP_USER, user);
        }
     }

    /**
     * Database Update Action for Users. Performs updates into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doUpdate(RunData rundata, Context context)
        throws Exception
    {
        JetspeedUser user = null;
        try
        {
            //
            // get the user object from the selected entry in the browser
            //
            user = (JetspeedUser)JetspeedSecurity.getUser(
                            rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID));

            String name = rundata.getParameters().getString("username");
            if (name == null || name.trim().length() == 0)
            {
                DynamicURI duri = new DynamicURI (rundata);
                duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_USER_UPDATE);
                duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_INVALID_ENTITY_NAME);
                if (user != null)
                    duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, user.getUserName());
                duri.addQueryData(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_UPDATE);
                rundata.setRedirectURI(duri.toString());
                // save values that user just entered so they don't have to re-enter
                if (user != null)
                   rundata.getUser().setTemp(TEMP_USER, user);
                return;
            }

            //
            // pull the values off the form and into the user object
            //
            String oldDisabled = user.getDisabled();
            rundata.getParameters().setProperties(user);
            user.setLastAccessDate();

            JetspeedSecurity.forcePassword(user,rundata.getParameters().getString("password"));

            String strDisabled = rundata.getParameters().getString("disabled");
            boolean disabled = (strDisabled != null && "T".equals(strDisabled));
            user.setDisabled(strDisabled);

            if  (!disabled && "T".equals(oldDisabled) && JetspeedSecurity.isDisableAccountCheckEnabled())
            {
                JetspeedSecurity.resetDisableAccountCheck(name);
            }

            //
            // update the user in the database
            //
            JetspeedSecurity.saveUser(user);

            JetspeedUser currentUser = (JetspeedUser)rundata.getUser();
            if (currentUser.getUserName().equals(user.getUserName()))
            {
                // same user as admin -- need to update in memory
                currentUser.setPassword(user.getPassword()); // Contains Encrypted password
                currentUser.setFirstName(user.getFirstName());
                currentUser.setLastName(user.getLastName());
                currentUser.setEmail(user.getEmail());
            }

        }
        catch (Exception e)
        {
           // log the error msg
            logger.error("Exception", e);

            //
            // error on update - display error message
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_USER_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_UPDATE_FAILED);
            if (user != null)
                duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, user.getUserName());
            duri.addQueryData(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_UPDATE);
            rundata.setRedirectURI(duri.toString());
            // save values that user just entered so they don't have to re-enter
            if (user != null)
               rundata.getUser().setTemp(TEMP_USER, user);
        }

    }

    /**
     * Database Delete Action for Users. Performs deletes into security database.
     *
     * @param rundata The turbine rundata context for this request.
     * @param context The velocity context for this request.
     */
    public void doDelete(RunData rundata, Context context)
        throws Exception
    {
        JetspeedUser user = null;
        try
        {
            //
            // get the user object from the selected entry in the browser
            //
            user = (JetspeedUser)JetspeedSecurity.getUser(
                       rundata.getParameters().getString(SecurityConstants.PARAM_ENTITY_ID));

            if (rundata.getUser().getUserName().equals(user.getUserName()))
            {
                DynamicURI duri = new DynamicURI (rundata);
                duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_USER_UPDATE);
                duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_CANT_DELETE_CURRENT);
                if (user != null)
                    duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, user.getUserName());
                duri.addQueryData(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_DELETE);
                rundata.setRedirectURI(duri.toString());
                // save values that user just entered so they don't have to re-enter
                if (user != null)
                   rundata.getUser().setTemp(TEMP_USER, user);
                return;
            }

            //
            // remove the user
            //
            JetspeedSecurity.removeUser(user.getUserName());

        }
        catch (Exception e)
        {
           // log the error msg
            logger.error("Exception", e);

            //
            // error on delete - display error message
            //
            DynamicURI duri = new DynamicURI (rundata);
            duri.addPathInfo(SecurityConstants.PANE_NAME, SecurityConstants.PANEID_USER_UPDATE);
            duri.addPathInfo(SecurityConstants.PARAM_MSGID, SecurityConstants.MID_DELETE_FAILED);
            if (user != null)
                duri.addPathInfo(SecurityConstants.PARAM_ENTITY_ID, user.getUserName());
            duri.addQueryData(SecurityConstants.PARAM_MODE, SecurityConstants.PARAM_MODE_DELETE);
            rundata.setRedirectURI(duri.toString());

            // save values that user just entered so they don't have to re-enter
           if (user != null)
               rundata.getUser().setTemp(TEMP_USER, user);

        }
    }

}