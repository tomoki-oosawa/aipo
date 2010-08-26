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
import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 * Display portlet in print friendly format
 * 
 * @version $Id: Print.java,v 1.3 2004/02/23 02:50:53 jford Exp $ 
 * @author <a href="mailto:mark_orciuch@ngsltd.com">Mark Orciuch</a>
 */
public class Print extends Action
{
                                                           
    /**
     * Default template to use with print friendly format
     */
    private static final String DEFAULT_TEMPLATE = "content";

    /**
     * Default template to use with print friendly format
     */
    private static final String ACTION_PRINT_TEMPLATE_KEY = "action.print.template";

    /**
     * Performs the action
     * 
     * @param rundata
     * @exception Exception
     */
    public void doPerform( RunData rundata ) throws Exception
    {

        String peid = rundata.getParameters().getString("js_peid");
        if ( peid == null )
        {
            rundata.setScreenTemplate("Ecs");
            return;
        }

         JetspeedRunData jdata = (JetspeedRunData)rundata;
         jdata.setJs_peid(peid);

        // retrieve the print friendly format action template
         String template = JetspeedResources.getString(ACTION_PRINT_TEMPLATE_KEY, DEFAULT_TEMPLATE);

        // redirect to the content template
        rundata.setScreenTemplate(template);
    }
}
