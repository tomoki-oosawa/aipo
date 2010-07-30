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
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletState;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.om.profile.Entry;

/**
* Change the internal state of a portlet from normal to minimized
*
* @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco</a>
* @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
*/
public class Minimize extends Action
{
    
    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(Minimize.class.getName());
    
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

        // Get the Portlet using the PSML document and the PEID
        Entry entry = jdata.getProfile().getDocument().getEntryById(peid);
        if ( entry == null )
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Failed to get PEID (" + peid + ") entry for User (" 
                  + rundata.getUser().getName() + ")");
            }
            return;
        }
        Portlet portlet = PortletFactory.getPortlet(entry);
        
        // Now set the portlet to minimized
        if (( portlet != null )&&( portlet instanceof PortletState ))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("peid = " + peid);
                logger.debug("portlet id = " + portlet.getID());
            }
            if (portlet.getID().equals(peid))
            {
                ((PortletState)portlet).setMinimized( true, rundata );
            }
        }
    }
}
