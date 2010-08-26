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

 
// Java Core Classes
import java.util.Properties;
import java.util.Locale;
import java.io.StringWriter;

import javax.servlet.http.Cookie;

// Turbine Modules
import org.apache.velocity.context.Context;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

// Jetspeed modules
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.nosecurity.FakeJetspeedUser;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.LoginException;
import org.apache.jetspeed.services.security.FailedLoginException;
import org.apache.jetspeed.services.security.CredentialExpiredException;
import org.apache.jetspeed.services.security.AccountExpiredException;

// Email Stuff
import org.apache.commons.mail.SimpleEmail;

// Lang Stuff
import org.apache.commons.lang.StringEscapeUtils;

/**
    This class is responsible for logging a user into the system. It is also
    responsible for making sure that the user has been marked as confirmed. 
    If the user is not marked as confirmed, then it will show them the 
    
*/
public class JLoginUser extends ActionEvent
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JLoginUser.class.getName());    
    
    /**
    * called when the password reminder button is pressed.
    * sends a user their password
    **/
    public void doReminder( RunData rundata ) throws Exception
    {
        JetspeedRunData data = (JetspeedRunData)rundata;

        try {
            String username = data.getParameters().getString("username", "");

            JetspeedUser user = null;

            try {
                user = JetspeedSecurity.getUser(username);
            } catch (JetspeedSecurityException ignored) {
            }

            if (user == null)
            {
                data.setScreenTemplate("LoginHelp");
                data.setMessage(Localization.getString("JLOGINUSER_PASSWORDREMINDER_INVALIDUSER"));
                if (logger.isDebugEnabled())
                    logger.debug(Localization.getString(rundata, "JLOGINUSER_PASSWORDREMINDER_INVALIDUSER"));
                return;
            }

            user.setHasLoggedIn( Boolean.FALSE);
            data.setUser(user);
 
            DynamicURI url = new DynamicURI(data);

            //build body via template
            StringWriter email_body = new StringWriter();

            Context context = TurbineVelocity.getContext(data);
            context.put( "data", data );
            context.put( "user", user );
            context.put("userurl",url);
            context.put("config",new JetspeedResources());

            //determine the language to be used for the notification email
            String lang = (String)user.getPerm("language");
            String ctry = (String)user.getPerm("country");
            Locale loc = null;
            if (lang != null && ctry != null)
            {
                loc = new Locale(lang,ctry); 
            }

            String templatePath = TemplateLocator.locateEmailTemplate(data, JetspeedResources.getString("password.reminder.template"), loc);

            SimpleEmail se = new SimpleEmail();

            String charset = JetspeedResources.getString("newuser.notification.charset","iso-8859-1");
            se.setCharset(charset);
            context.put( "firstname", StringEscapeUtils.unescapeHtml(user.getFirstName()) );
            context.put( "lastname", StringEscapeUtils.unescapeHtml(user.getLastName()) );
            context.put( "username", StringEscapeUtils.unescapeHtml(user.getUserName()) );
            context.put("email",se);

            TurbineVelocity.handleRequest(context, templatePath, email_body);

            se.setMsg(email_body.toString());

            Properties props = System.getProperties();
            String mailServerMachine = JetspeedResources.getString( "mail.server" );
            props.put("mail.host", mailServerMachine );
            props.put("mail.smtp.host", mailServerMachine);

            se.send();

            data.setMessage (Localization.getString(rundata, "JLOGINUSER_PASSWORDREMINDER_SENT"));
            logger.info( "Password for user " + user.getUserName() + " was sent to " + user.getEmail());
            logger.info(Localization.getString(rundata, "JLOGINUSER_PASSWORDREMINDER_SENT"));
            data.setScreenTemplate("Login");
        } catch ( Exception e ) {
            data.setScreenTemplate("LoginHelp");
            String errorTitle = Localization.getString(rundata, "JLOGINUSER_PASSWORDREMINDER_ERROR") ;
            String errorMessage = errorTitle + e.toString();

            logger.warn( errorMessage, e );
            data.setMessage ( errorMessage );
        }
    }


    public void doPerform( RunData rundata ) throws Exception
    {
        JetspeedRunData data = (JetspeedRunData)rundata;
        
        String username = data.getParameters().getString("username", "");
        String password = data.getParameters().getString("password", "");

        boolean newUserApproval = JetspeedResources.getBoolean("newuser.approval.enable", false);
        String secretkey = (String) data.getParameters().getString("secretkey", null);
        if ( secretkey != null )
        {

            // its the first logon - we are verifying the secretkey

            // handle the buttons on the ConfirmRegistration page
            String button1 = data.getParameters().getString ( "submit1", null );
            if ( button1 != null && button1.equalsIgnoreCase("Cancel") )
            {
                data.setScreenTemplate(TurbineTemplate.getDefaultScreen());
                return;
            }
            
            // check to make sure the user entered the right confirmation key
            // if not, then send them to the ConfirmRegistration screen            
            JetspeedUser user = JetspeedSecurity.getUser(username);

            if (user == null)
            {
                logger.warn("JLogin User: Unexpected condition : user is NULL");
                return;   
            }
            String confirm_value = user.getConfirmed();
            if ( ! secretkey.equals ( confirm_value ) && ! confirm_value.equals ( JetspeedResources.CONFIRM_VALUE ) )
            {
                if ( newUserApproval )
                {
                    data.setMessage(Localization.getString(rundata, "JLOGINUSER_KEYNOTVALID"));
                    data.setScreenTemplate("NewUserAwaitingAcceptance");
                    return;
                }
                else
                {
                  if ( user.getConfirmed().equals(JetspeedResources.CONFIRM_VALUE_REJECTED))
                  {
                    data.setMessage(Localization.getString(rundata, "JLOGINUSER_KEYNOTVALID"));
                    data.setScreenTemplate("NewUserRejected");
                    return;
                  }
                  else
                  {
                    data.setMessage(Localization.getString(rundata, "JLOGINUSER_KEYNOTVALID"));
                    data.setScreenTemplate("ConfirmRegistration");
                    return;
                  }
                }
            }
             
            user.setConfirmed( JetspeedResources.CONFIRM_VALUE );
            data.setMessage (Localization.getString(rundata, "JLOGINUSER_WELCOME"));
            JetspeedSecurity.saveUser(user);
        }
        
        JetspeedUser user = null;
        try
        {
            user = JetspeedSecurity.login(username, password);
            JetspeedSecurity.saveUser(user);
        }
        catch (LoginException e)
        {            
            data.setScreenTemplate(JetspeedResources.getString(TurbineConstants.TEMPLATE_LOGIN));
            String message = e.getMessage() != null ? e.getMessage() : e.toString();
            data.setMessage(message);
            data.setUser(JetspeedSecurity.getAnonymousUser());
            data.getUser().setHasLoggedIn(new Boolean (false) );            

            if (e instanceof FailedLoginException)
            {
                if (!disableCheck(data))
                {                
                    logger.info("JLoginUser: Credential Failure on login for user: " + username);
                    data.setMessage(Localization.getString(rundata, "PASSWORDFORM_FAILED_MSG"));
                }
            }
            else if (e instanceof AccountExpiredException)
            {
                logger.info("JLoginUser: Account Expired for user " + username);
            } 
            else if (e instanceof CredentialExpiredException)
            {
                logger.info("JLoginUser: Credentials expired for user: " + username);
                data.setScreenTemplate(
                    JetspeedResources.getString(JetspeedResources.CHANGE_PASSWORD_TEMPLATE, "ChangePassword")
                    );
                data.setMessage(Localization.getString(rundata, "PASSWORDFORM_EXPIRED_MSG"));
                data.getParameters().setString("username", username);
            } 

            return;
        }
        catch (Throwable other)
        {
            data.setScreenTemplate(JetspeedResources.getString(TurbineConstants.TEMPLATE_ERROR));
            String message = other.getMessage() != null ? other.getMessage() : other.toString();
            data.setMessage(message);
            data.setStackTrace(org.apache.turbine.util.StringUtils.stackTrace(other), other);
            JetspeedUser juser = new FakeJetspeedUser(JetspeedSecurity.getAnonymousUserName(), false);
            data.setUser(juser);
            return;
        }
        if ("T".equals(user.getDisabled()))
        {
            data.setMessage(Localization.getString(rundata, "JLOGINUSER_ACCOUNT_DISABLED"));
            data.setScreenTemplate(JetspeedResources.getString("logon.disabled.form"));
            data.getUser().setHasLoggedIn(new Boolean (false) );
            return;
        }

        // check for being confirmed before allowing someone to finish logging in
        if ( data.getUser().hasLoggedIn())
        {
            if  (JetspeedSecurity.isDisableAccountCheckEnabled())
            {
                // dst: this needs some refactoring. I don't believe this api is necessary
                JetspeedSecurity.resetDisableAccountCheck(data.getParameters().getString("username", ""));
            }        

            String confirmed = data.getUser().getConfirmed();
            if (confirmed == null || !confirmed.equals(JetspeedResources.CONFIRM_VALUE ))
            {
                if (confirmed != null && confirmed.equals(JetspeedResources.CONFIRM_VALUE_REJECTED))
                {
                  data.setMessage(Localization.getString(rundata, "JLOGINUSER_KEYNOTVALID"));
                  data.setScreenTemplate("NewUserRejected");
                  data.getUser().setHasLoggedIn(new Boolean (false) );
                  return;
                }
                else
                {
                  data.setMessage(Localization.getString(rundata, "JLOGINUSER_CONFIRMFIRST"));
                  data.setScreenTemplate("ConfirmRegistration");
                  data.getUser().setHasLoggedIn(new Boolean (false) );
                  return;
                }
            }

            // user has logged in successfully at this point
  
            boolean automaticLogonEnabled = JetspeedResources.getBoolean("automatic.logon.enable", false);
            if (automaticLogonEnabled)
            {
              //Does the user want to use this facility?
              boolean userRequestsRememberMe = data.getParameters().getBoolean("rememberme",false);
              if (userRequestsRememberMe)
              {
                //save cookies on the users machine.
                int maxage = JetspeedResources.getInt("automatic.logon.cookie.maxage",-1);
                String comment = JetspeedResources.getString("automatic.logon.cookie.comment","");
                String domain = JetspeedResources.getString("automatic.logon.cookie.domain");
                String path = JetspeedResources.getString("automatic.logon.cookie.path","/");

                if (domain == null)
                {
                  String server = data.getServerName();
                  domain = "." + server;
                }

                String loginCookieValue = null;

                if ( JetspeedResources.getString("automatic.logon.cookie.generation","everylogon").equals("everylogon") )
                {
                  loginCookieValue = ""+Math.random();
                  data.getUser().setPerm("logincookie",loginCookieValue);
                  JetspeedSecurity.saveUser( data.getJetspeedUser() );
                } 
                else 
                {
                  loginCookieValue = (String)data.getUser().getPerm("logincookie");
                  if (loginCookieValue == null || loginCookieValue.length() == 0)
                  {
                    loginCookieValue = ""+Math.random();
                    data.getUser().setPerm("logincookie",loginCookieValue);
                    JetspeedSecurity.saveUser( data.getJetspeedUser() );
                  }
                }

                Cookie userName = new Cookie("username",data.getUser().getUserName());
                Cookie loginCookie = new Cookie("logincookie",loginCookieValue);

                userName.setMaxAge(maxage);
                userName.setComment(comment);
                userName.setDomain(domain);
                userName.setPath(path);

                loginCookie.setMaxAge(maxage);
                loginCookie.setComment(comment);
                loginCookie.setDomain(domain);
                loginCookie.setPath(path);

                data.getResponse().addCookie(userName);
                data.getResponse().addCookie(loginCookie);

              }
                    
            }

        }
        else
        {
            disableCheck(data);
        }

    }

    private boolean disableCheck(JetspeedRunData data)
    {
        boolean disabled = false;
        // disable user after a configurable number of strikes
        if  (JetspeedSecurity.isDisableAccountCheckEnabled())
        {
            disabled = JetspeedSecurity.checkDisableAccount(data.getParameters().getString("username", ""));
            
            if (disabled)
            {
                data.setMessage(Localization.getString(data, "JLOGINUSER_ACCOUNT_DISABLED"));
                data.setScreenTemplate(JetspeedResources.getString("logon.disabled.form"));
                data.getUser().setHasLoggedIn(new Boolean (false) );
            }
        }
        return disabled;
    }
}
