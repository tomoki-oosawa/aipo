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

// Jetspeed
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.Profiler;

// Turbine
import org.apache.turbine.modules.Action;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.util.RunData;
import org.apache.turbine.TurbineConstants;

/**
 * Performs change password action
 * 
 * @author <a href="morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: ChangePassword.java,v 1.7 2004/02/23 02:59:06 jford Exp $
 */
public class ChangePassword extends Action
{
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ChangePassword.class.getName());
    
    public void doPerform(RunData rundata) throws Exception
    {

        String cancelBtn = rundata.getParameters().getString(Localization.getString(rundata, "PASSWORDFORM_CANCEL"));
        String username  = rundata.getParameters().getString("username" , "");
        String oldPassword  = JetspeedSecurity.convertPassword(rundata.getParameters().getString("old_password" , ""));
        String password  = JetspeedSecurity.convertPassword(rundata.getParameters().getString("password", ""));
        String password2 = JetspeedSecurity.convertPassword(rundata.getParameters().getString("password_confirm", ""));

        // CANCEL BUTTON
        //
        // check to see if the Cancel button was pressed.
        // if so, return to default portal page
        if (cancelBtn != null && cancelBtn.equalsIgnoreCase(Localization.getString(rundata, "PASSWORDFORM_CANCEL")))
        {
            return;
        }

        String returnTemplate = JetspeedResources.getString(JetspeedResources.CHANGE_PASSWORD_TEMPLATE, "ChangePassword");

        try 
        {

            JetspeedUser user = JetspeedSecurity.getUser(username);

            if (!password.equals(password2))
            {
                rundata.setMessage(Localization.getString(rundata, "UPDATEACCOUNT_PWNOTMATCH"));
                rundata.setScreenTemplate(returnTemplate);
                return;
            }

            if (password.equals(oldPassword))
            {
                rundata.setMessage(Localization.getString(rundata, "PASSWORDFORM_THESAME_MSG"));
                rundata.setScreenTemplate(returnTemplate);
                return;
            }

            // Change the password
            JetspeedSecurity.changePassword(user, oldPassword, password);
            rundata.setMessage(Localization.getString(rundata, "PASSWORDFORM_DONE"));

            // Login again
            rundata.getParameters().setString("username", username);
            rundata.getParameters().setString("password", password);
            String userRequestsRememberMe = rundata.getParameters().getString("rememberme");
            rundata.getParameters().setString("rememberme", userRequestsRememberMe);
            ActionLoader.getInstance().getInstance(
                JetspeedResources.getString(TurbineConstants.ACTION_LOGIN)
                ).doPerform(rundata);

            // Update the profile in rundata - not sure why this is not happening automatically?
            JetspeedRunData jdata = (JetspeedRunData) rundata;
            jdata.setProfile(Profiler.getProfile(jdata));

        }
        catch (Exception e) 
        {
            logger.error("Exception", e);
            rundata.setMessage(e.getMessage() != null ? e.getMessage() : e.toString());
            rundata.setScreenTemplate(returnTemplate);
        }
    }

}
