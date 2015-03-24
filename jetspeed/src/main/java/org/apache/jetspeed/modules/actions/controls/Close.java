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
 
package org.apache.jetspeed.modules.actions.controls;

// Turbine stuff
import org.apache.turbine.modules.Action;
import org.apache.turbine.util.RunData;

// Jetspeed stuff
import org.apache.jetspeed.services.rundata.JetspeedRunData;

/**
 * Change the internal state of a portlet from normal to closed
 * 
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 */
public class Close extends Action
{
    /**
     * @param rundata The RunData object for the current request
     */    
    public void doPerform( RunData rundata ) throws Exception
    {

        // Only logged in users can minmize
        if( rundata.getUser() == null)
        {
            return;
        }

        // Get jsp_peid parmameter.  If it does not exist, then do nothing
        String peid = rundata.getParameters().getString("js_peid");
        if ( peid == null )
        {
            return;
        }
        
        JetspeedRunData jdata = (JetspeedRunData)rundata;

        // Remove the Portlet using the PEID
        jdata.getProfile().getDocument().removeEntryById(peid);
    }
}
