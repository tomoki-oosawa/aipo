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

import org.apache.turbine.util.RunData;
import org.apache.turbine.modules.Action;
import org.apache.turbine.TurbineConstants;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.security.nosecurity.FakeJetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;

/**
    Calls the profiler to load the requested PSML resource based on request params
    Its necessary to load the profile from this action, not the SessionValidator
    in order to get the cached ACL list from logon
  
@author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
@version $Id: JetspeedAccessController.java,v 1.10 2004/02/23 02:59:06 jford Exp $
*/

public class JetspeedAccessController extends Action
{
   
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedAccessController.class.getName());
    
    public void doPerform( RunData data ) throws Exception
    {
        JetspeedUser user = (JetspeedUser)data.getUser();

        getACL(data);
        JetspeedRunData jdata = null;
        
        try
        {
            jdata = (JetspeedRunData)data;
        }
        catch (ClassCastException e)
        {
            logger.error("The RunData object does not implement the expected interface, "
                       + "please verify the RunData factory settings", e);
            return;
        }

        Profile newProfile = null;
        Profile currentProfile = null;

        try
        {
            // get the profile and store it in the RunData
            newProfile = Profiler.getProfile(jdata);
            currentProfile = jdata.getProfile();
        }
        catch (Throwable other)
        {
            data.setScreenTemplate(JetspeedResources.getString(TurbineConstants.TEMPLATE_ERROR));
            String message = other.getMessage() != null ? other.getMessage() : other.toString();
            data.setMessage(message);
            data.setStackTrace(org.apache.turbine.util.StringUtils.stackTrace(other), other);

            if (currentProfile == null)
            {
                currentProfile = Profiler.createProfile();
            }
            if (newProfile == null)
            {
                newProfile = Profiler.createProfile();
            }
            if (data.getUser() == null)
            {
                JetspeedUser juser = new FakeJetspeedUser(JetspeedSecurity.getAnonymousUserName(), false);
                data.setUser(juser);
            }
        }


        if ((currentProfile == null)
         || (!currentProfile.equals(newProfile)))
        {
            // the profile changed due to the request parameters,
            // change it in the RunData
            jdata.setProfile(newProfile);
        }
 
    }

    protected void getACL(RunData data)
    {
        data.setACL(null);
/*    
        if ( data.getUser() != null && data.getUser().hasLoggedIn() )
        {
            AccessControlList acl = (AccessControlList)
                data.getSession().getValue(AccessControlList.SESSION_KEY);
            if ( acl == null )
            {
                //acl = TurbineSecurity.getACL( data.getUser() );
                acl = null;
                data.getSession().putValue( AccessControlList.SESSION_KEY,
                                            (Object)acl );
            }
            data.setACL(acl);
        }
*/
    }

}