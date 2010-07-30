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


// Java
import java.util.Hashtable;
import javax.servlet.http.Cookie;

// Jetspeed
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.rundata.JetspeedRunData;

// Turbine
import org.apache.turbine.modules.Action;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.GenerateUniqueId;

/**
 *
 *   Updates an Account in the User and save the User object to backing store.
 *   You must have been logged in in order
 *   to update the account.
 */
public class UpdateAccount extends Action
{
    public void doPerform( RunData rundata ) throws Exception
    {
        JetspeedRunData data = (JetspeedRunData)rundata;

        // check to make sure the user has logged in before accessing this screen
        if ( ! data.getUser().hasLoggedIn() )
        {
            data.setScreenTemplate( JetspeedResources.getString( "services.JspService.screen.error.NotLoggedIn", "Error" ) );
            return;
        }

        String cancelBtn = data.getParameters().getString( "CancelBtn" , "" );
        String username  = data.getParameters().getString( "username" , "" );
        String oldPassword  = JetspeedSecurity.convertPassword(data.getParameters().getString( "old_password" , "" ));
        String password  = JetspeedSecurity.convertPassword(data.getParameters().getString( "password" , "" ));
        String password2 = JetspeedSecurity.convertPassword(data.getParameters().getString( "password_confirm" , "" ));
        String firstname = data.getParameters().getString( "firstname", "" );
        String lastname  = data.getParameters().getString( "lastname" , "" );
        String email     = data.getParameters().getString( "email" , "" );
        boolean userRequestsRememberMe = data.getParameters().getBoolean( "rememberme" , false );

        // Save user input in case there is an error and 
        // we have to go back to the EditAccount screen
        Hashtable screenData = new Hashtable();
        screenData.put( "username",  username );
        screenData.put( "firstname", firstname );
        screenData.put( "lastname",  lastname );
        screenData.put( "email",     email );
        data.getRequest().setAttribute( "ScreenDataEditAccount", screenData );

        // CANCEL BUTTON
        //
        // check to see if the Cancel button was pressed.
        // if so, return to the screen we were previously on
        // defined by nextscreen in the EditAccount screen
        if ( ! cancelBtn.equalsIgnoreCase( "" ) )
        {
            return;
        }

        // PASSWORD
        //
        // if the fields are empty, then don't do anything to the passwords
        boolean changepass = false;
        if ( password.trim().length() > 0 && password2.trim().length() > 0 )
        {
            changepass = true;
        }
        
        if ( changepass == true && ! password.equals( password2 ) )
        {
            data.setMessage(Localization.getString(rundata, "UPDATEACCOUNT_PWNOTMATCH"));
            backToEditAccount( data, screenData );
            return;
        }
        
        if ( changepass == true && password.equals( oldPassword ) )
        {
            // old password = new passwod, so do not change.
            changepass = false;
        }

            // FIRSTNAME
        //
        // make sure the firstname exists
        if ( firstname.length() == 0 )
        {
            data.setMessage(Localization.getString(rundata, "UPDATEACCOUNT_NOFIRSTNAME"));
            backToEditAccount( data, screenData );
            return;
        }

        // LASTNAME
        //
        // make sure the lastname exists
        if ( lastname.length() == 0 )
        {
            data.setMessage(Localization.getString(rundata, "UPDATEACCOUNT_NOLASTNAME"));
            backToEditAccount( data, screenData );
            return;
        }

        // AUTOMATIC LOGIN
        //
        // if automatic login is enabled, then handle the remember me checkbox
        if ( JetspeedResources.getBoolean("automatic.logon.enable", false) )
        {
          if ( ! userRequestsRememberMe ) 
          {
            if ( data.getRequest().getCookies() != null &&
                 data.getCookies().getString("username") != null &&
                 data.getCookies().getString("logincookie") != null )
            {
              // remove cookies by re-adding them with zero MaxAge, which deletes them
              Cookie userName = new Cookie("username","");
              Cookie loginCookie = new Cookie("logincookie","");

              String comment = JetspeedResources.getString("automatic.logon.cookie.comment","");
              String domain = JetspeedResources.getString("automatic.logon.cookie.domain");
              String path = JetspeedResources.getString("automatic.logon.cookie.path","/");

              if (domain == null)
              {
                String server = data.getServerName();
                domain = "." + server;
              }

              userName.setMaxAge(0);
              userName.setComment(comment);
              userName.setDomain(domain);
              userName.setPath(path);

              loginCookie.setMaxAge(0);
              loginCookie.setComment(comment);
              loginCookie.setDomain(domain);
              loginCookie.setPath(path);

              data.getResponse().addCookie(userName);
              data.getResponse().addCookie(loginCookie);

              data.getCookies().remove("username");
              data.getCookies().remove("logincookie");
            }
          } 
          else 
          {
            if ( data.getRequest().getCookies() == null ||
                 !data.getCookies().getString("username","").equals(data.getUser().getUserName()) ||
                 !data.getCookies().getString("logincookie","").equals(data.getUser().getPerm("logincookie")) )
            {
              String loginCookieValue = (String)data.getUser().getPerm("logincookie");
              if (loginCookieValue == null || loginCookieValue.length() == 0)
              {
                loginCookieValue = ""+Math.random();
                data.getUser().setPerm("logincookie",loginCookieValue);
                JetspeedSecurity.saveUser( data.getJetspeedUser() );
              }

              Cookie userName = new Cookie("username",data.getUser().getUserName());
              Cookie loginCookie = new Cookie("logincookie",loginCookieValue);

              int maxage = JetspeedResources.getInt("automatic.logon.cookie.maxage",-1);
              String comment = JetspeedResources.getString("automatic.logon.cookie.comment","");
              String domain = JetspeedResources.getString("automatic.logon.cookie.domain");
              String path = JetspeedResources.getString("automatic.logon.cookie.path","/");

              if (domain == null)
              {
                String server = data.getServerName();
                domain = "." + server;
              }

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

              data.getCookies().add("username",data.getUser().getUserName());
              data.getCookies().add("logincookie",loginCookieValue);
            }
          }
        }

        // EMAIL
        //
        // make sure the email exists
        if ( email.length() == 0 )
        {
            data.setMessage(Localization.getString(rundata, "UPDATEACCOUNT_NOEMAIL"));
            backToEditAccount( data, screenData );
            return;
        }

        boolean enableMail = JetspeedResources.getBoolean("newuser.confirm.enable", false);

        String currentEmail = (String) data.getUser().getEmail();
        if ( enableMail && ( currentEmail == null || ! currentEmail.equalsIgnoreCase(email) ) )
            {
                //Send confirmation email if different than current
                data.getUser().setEmail( email );
                data.getUser().setConfirmed( GenerateUniqueId.getIdentifier() );
                JetspeedSecurity.saveUser( data.getJetspeedUser() );
                ActionLoader.getInstance().exec(data, "SendConfirmationEmail");
                // add in the username to the parameters because ConfirmRegistration needs it
                data.getParameters().add("username", data.getUser().getUserName() );
                data.setMessage(Localization.getString(rundata, "UPDATEACCOUNT_NEWEMAILCONFIRM"));
                data.setScreenTemplate("ConfirmRegistration");
            }
        else
            {       
                JetspeedSecurity.saveUser( data.getJetspeedUser() );
            }
            
        // update currently logged in information that might have changed
        data.getUser().setFirstName( firstname );
        data.getUser().setLastName( lastname );
        data.getUser().setEmail( email );
        if ( changepass )
        {
            try
            {
              JetspeedSecurity.changePassword(data.getJetspeedUser(),oldPassword, password);
            } catch (JetspeedSecurityException e)
            {
                data.setMessage(e.getMessage());
                backToEditAccount( data, screenData );
                return;
            }
        }

        //allow sub-classes to update additional information
        updateUser(data);

        JetspeedSecurity.saveUser( data.getJetspeedUser() );
        data.setMessage (Localization.getString(rundata, "UPDATEACCOUNT_DONE"));
        
    }

    /**
     * updateUser updates the user object.
     * Subclasses can extend this class and override this method - adding additional custom settings as needed.
     * Note the default implementation does nothing - so no need to call the super version.
     *
     * @param data Turbine request/session information.
     */
    protected void updateUser(RunData data)
    {
        //default version does nothing
    }

    private void backToEditAccount( RunData rundata, Hashtable screenData )
    {   
        rundata.getRequest().setAttribute( "ScreenDataEditAccount",
                                           screenData );
        rundata.setScreenTemplate("EditAccount");
    }

}
