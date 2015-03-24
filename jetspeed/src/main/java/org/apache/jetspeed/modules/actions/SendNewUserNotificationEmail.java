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

package org.apache.jetspeed.modules.actions;

// Java stuff
import java.io.StringWriter;
import java.util.Properties;
import java.util.Locale;

// Jetspeed Stuff
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.om.security.JetspeedUser;

// Turbine Stuff
import org.apache.turbine.modules.Action;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.DynamicURI;

// Velocity Stuff
import org.apache.velocity.context.Context;

// Email Stuff
import org.apache.commons.mail.SimpleEmail;

// Lang Stuff
import org.apache.commons.lang.StringEscapeUtils;

/**
    This action will send a notification email to the notification user.

TODO - does this apply to the notify emails?
    This class is used in two places, the first one is for new users.
    The second is where a user is updating their information after they
    have already created their account. If they are updating and they change
    their email address, then we want to re-confirm it to prevent people from
    screwing up their email address.
*/
public class SendNewUserNotificationEmail extends Action
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(SendNewUserNotificationEmail.class.getName());    
    
    public void doPerform( RunData data ) throws Exception
    {
        JetspeedUser user = JetspeedSecurity.getUser(data.getParameters().getString("username", ""));        
        DynamicURI url = new DynamicURI(data)
                             .addPathInfo("pane0", "Security" )
                             .addPathInfo("select-panel2", "User")
                             .addPathInfo("entityid", user.getUserName() )
                             .addQueryData("mode","update");
        try
        {
            //build body via template
            StringWriter email_body = new StringWriter();
            SimpleEmail se = new SimpleEmail();
            String charset = JetspeedResources.getString("newuser.notification.charset","iso-8859-1");
            se.setCharset(charset);
            Context emailContext = TurbineVelocity.getContext();
            emailContext.put( "firstname", StringEscapeUtils.unescapeHtml(user.getFirstName()) );
            emailContext.put( "lastname", StringEscapeUtils.unescapeHtml(user.getLastName()) );
            emailContext.put( "username", StringEscapeUtils.unescapeHtml(user.getUserName()) );
            emailContext.put( "data", data );
            emailContext.put( "user", user );
            emailContext.put( "config",new JetspeedResources());
            emailContext.put( "userurl",url);
            emailContext.put( "email",se);

            //determine the language to be used for the notification email
            String language = JetspeedResources.getString("newuser.notification.language","en");
            String country = JetspeedResources.getString("newuser.notification.country","US");
            Locale locale = new Locale(language,country);

            String templateFile = JetspeedResources.getString("newuser.notification.email.template");
            String templatePath = TemplateLocator.locateEmailTemplate(data, templateFile, locale);
            TurbineVelocity.handleRequest(emailContext, templatePath, email_body);

            se.setMsg(email_body.toString());

            Properties props = System.getProperties();
            String mailServerMachine = JetspeedResources.getString( "mail.server" );
            props.put ( "mail.host", mailServerMachine );
            props.put("mail.smtp.host", mailServerMachine);

            se.send();

            data.setMessage (Localization.getString(data, "SENDCONFIRMATIONEMAIL_SENT"));
        } catch ( Exception e )
        {
            String errorTitle = Localization.getString(data, "SENDCONFIRMATIONEMAIL_ERROR") ;
            String errorMessage = errorTitle + e.getMessage();

            logger.error( errorMessage, e );
            data.setMessage ( errorTitle + errorMessage );
        }
    }
}
