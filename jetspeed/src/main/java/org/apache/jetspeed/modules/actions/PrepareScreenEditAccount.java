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

package org.apache.jetspeed.modules.actions;


// JDK Stuff
import java.util.*;

// External Stuff
import org.apache.turbine.modules.Action;
import org.apache.turbine.services.resources.TurbineResources;

import org.apache.turbine.util.RunData;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;

public class PrepareScreenEditAccount extends Action
{
    public void doPerform( RunData rundata ) throws Exception
    {
        // check to make sure the user has logged in before accessing this screen
        if ( ! rundata.getUser().hasLoggedIn() )
        {
            rundata.setScreenTemplate( TurbineResources.getString( "services.JspService.screen.error.NotLoggedIn","Error") );
            return;
        }
        
        // fill in the blanks in the form
        String username  = rundata.getUser().getUserName();
        String firstname = null;
        String lastname  = null;
        String email     = null;

        // for security, get information about the user from the database
        // instead of what we already have cached.
        try
        {
            JetspeedUser user = JetspeedSecurity.getUser(rundata.getUser().getUserName());
            firstname = (String) user.getFirstName();
            lastname  = (String) user.getLastName();
            email     = (String) user.getEmail();
        
            if ( firstname == null )
                firstname = "";
            if ( lastname == null )
                lastname = "";
            if ( email == null )
                email = "";

            Hashtable screenData = new Hashtable();
            screenData.put( "username",  username );
            screenData.put( "firstname", firstname );
            screenData.put( "lastname",  lastname );
            screenData.put( "email",     email );
            rundata.getRequest().setAttribute( "ScreenDataEditAccount", screenData );
    
            return;
        }
        catch(Exception e)
        {
            rundata.setScreenTemplate( TurbineResources.getString( "services.JspService.screen.error.NotLoggedIn","Error") );
            return;
        }
    }
}
