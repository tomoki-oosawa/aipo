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


// java.util
import java.util.Date;

import org.apache.jetspeed.om.security.JetspeedUser;

// Jetspeed modules
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;

// turbine.modules
import org.apache.turbine.modules.Action;
import org.apache.turbine.modules.ActionLoader;

// resources
import org.apache.turbine.services.localization.Localization;
import org.apache.jetspeed.services.resources.JetspeedResources;

// templates
import org.apache.turbine.services.template.TurbineTemplate;

// turbine.util
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.GenerateUniqueId;
import org.apache.turbine.util.StringUtils;

// security
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.security.JetspeedSecurityException;

/**
    This action validates the form input from the NewAccount Screen.
    If it is valid, then it will check to make sure that the user account
    does not already exist. If it does, then it will show the NewAccount
    screen again. If it doesn't alread exist, then it will create the new
    user and set the CONFIRM_VALUE to be the users session id. This part should
    probably be re-done to get a better less hackable CONFIRM_VALUE, but this
    should work for now. If everything goes well, this action will send the user
    a confirmation email and then show the ConfirmRegistration screen.

    @author Jon S. Stevens <a href="mailto:jon@clearink.com">jon@clearink.com</a>
    @author David S. Taylor <a href="mailto:david@bluesunrise.com">david@bluesunrise.com</a>
    @author Tom Adams <a href="mailto:tom@PIsoftware.com">tom@PIsoftware.com</a>

*/
public class CreateNewUserAndConfirm extends Action
{
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(CreateNewUserAndConfirm.class.getName());    
    
    public void doPerform( RunData data ) throws Exception
    {
        try
        {

            String submit = "submit1";
            String str = (String) data.getUser().getTemp ( submit, "asdfasdf" );
            if ( str != null && str
                .equalsIgnoreCase( data.getParameters().getString(submit, "")) )
            {
                data.getUser().removeTemp(submit);
                data.setScreenTemplate( TurbineTemplate.getDefaultScreen() );
                return;
            }

            String pass1 = data.getParameters().getString("password", "");
            String pass2 = data.getParameters().getString("password_confirm", "");

            // make sure the passwords are not empty
            if ( (pass1.length() == 0 || pass2.length() == 0 )
                || ! pass1.equals ( pass2 ) )
            {
                data.setMessage(Localization.getString(data, "CREATENEWUSERANDCONFIRM_PWNOTMATCH"));
                data.setScreenTemplate("NewAccount");
                return;
            }

            String username = data.getParameters().getString("username", "");

            // convert case if configured
            username = JetspeedSecurity.convertUserName(username);
            pass1 = JetspeedSecurity.convertPassword(pass1);
            pass2 = JetspeedSecurity.convertPassword(pass2);

            // make sure the username exists
            if ( username.length() == 0 )
            {
                data.setMessage(Localization.getString(data, "CREATENEWUSERANDCONFIRM_NOUSERNAME"));
                data.setScreenTemplate("NewAccount");
                return;
            }

            String email = data.getParameters().getString("email", "");
            // make sure the email exists
            if ( email.length() == 0 )
            {
                data.setMessage(Localization.getString(data, "CREATENEWUSERANDCONFIRM_NOEMAIL"));
                data.setScreenTemplate("NewAccount");
                return;
            }

            String CHNAME = Localization.getString(data, "CREATENEWUSERANDCONFIRM_DUPLICATEMSG");

            boolean accountExists = true;
            try
            {
                JetspeedSecurity.getUser(username);
            }
            catch(JetspeedSecurityException e)
            {
                accountExists = false;
            }

            if (!accountExists)
            {
                Date now = new Date();

                JetspeedUser user = JetspeedSecurity.getUserInstance();

                user.setUserName( username );
                user.setCreateDate(now);
                user.setLastLogin(new Date(0));
                user.setFirstName( data.getParameters().getString("firstname") );
                user.setLastName( data.getParameters().getString("lastname") );
                user.setEmail( data.getParameters().getString("email") );

                createUser(user, data);

                // create a unique confirmation string for the new user
                String confirmValue = GenerateUniqueId.getIdentifier();

                // allow for disabling of email for configurations without a mail server
                boolean newUserNotification = JetspeedResources.getBoolean("newuser.notification.enable", false);
                boolean newUserApproval = JetspeedResources.getBoolean("newuser.approval.enable", false);
                boolean enableMail = JetspeedResources.getBoolean("newuser.confirm.enable", false);
                if (false == enableMail)
                    confirmValue = JetspeedResources.CONFIRM_VALUE;

                if (true == newUserApproval)
                    confirmValue = JetspeedResources.CONFIRM_VALUE_PENDING;

                user.setConfirmed( confirmValue );

                // Store the user object.
                data.setUser(user);

                user.setPassword(pass1);
                JetspeedSecurity.addUser(user);
                if (!enableMail && !newUserApproval)
                {
                  user.setHasLoggedIn(new Boolean (true));
                  user.setLastLogin(new Date(0));
                }
                data.setMessage(Localization.getString(data, "CREATENEWUSERANDCONFIRM_CREATE"));
                if (enableMail || newUserNotification || newUserApproval)
                {
                    data.setUser(JetspeedSecurity.getAnonymousUser());
                    data.getParameters().add("username", username);
                    data.getParameters().add("password", pass1);
                    if ( ! newUserApproval )
                    {
                        ActionLoader.getInstance().exec(data, "SendConfirmationEmail");
                        data.setScreenTemplate("ConfirmRegistration");
                    }
                    else
                    {
                        data.setScreenTemplate("NewUserAwaitingAcceptance");
                    }
                    // FIXME: Should notification be set when request is made, or when
                    //        user is accepted?
                    if ( newUserNotification )
                    {
                        ActionLoader.getInstance().exec(data, "SendNewUserNotificationEmail");
                    }
                }
                else
                {
                    bypassConfirmMail(data, username, pass1);
                }

            }
            else // username exists. show the screen again.
            {
                data.setMessage(Localization.getString(data, "CREATENEWUSERANDCONFIRM_CHOOSENEWNAME"));
                data.setScreenTemplate("NewAccount");
                // set the username to be the CHNAME string so that it is
                // clear that this needs to be replaced
                data.getParameters().add("username", CHNAME);
            }
        }
        catch (Exception e)
        {
          logger.error("CreateNewUserAndConfirm",e);
          data.setMessage(e.toString());
          data.setStackTrace(StringUtils.stackTrace(e), e);
          data.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
    }

    /**
     * createUser creates a new user.
     * Subclasses can override this method - adding additional user property settings as needed.
     * The default implementation does nothing.
     *
     * @param user the new user that has been created
     * @param data the current RunData instance
     *
     * @throws Exception passed up from JetspeedSecurity
     */
    protected void createUser(JetspeedUser user, RunData data) throws Exception
    {
    }

    /**
     * bypassConfirmMail allows configurations to bypass sending the confirmation email
     * The new user is logged on and then redirected to the home page
     *
     * @param data Turbine information.
     * @param username The user's username.
     * @param password The user's password.
     */
    private void bypassConfirmMail(RunData data, String username, String password)
    {
        JetspeedUser usr = null;
        try
        {
          // Authenticate the user and get the object.
          usr = JetspeedSecurity.login( username, password );

          // bring logged on user to homepage via redirect
          JetspeedLink jslink = JetspeedLinkFactory.getInstance(data);
          data.setRedirectURI(jslink.getHomePage().toString());
          JetspeedLinkFactory.putInstance(jslink);
        }
        catch ( Exception e )
        {
            logger.error("Exception", e);
            data.setMessage(e.toString());
            data.setStackTrace(StringUtils.stackTrace(e), e);
            data.setScreenTemplate(JetspeedResources.getString("template.error","Error"));
        }
      }

}
