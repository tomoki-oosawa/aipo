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

package org.apache.jetspeed.modules.pages;


// Jetspeed classes
import org.apache.jetspeed.util.template.JetspeedTemplateNavigation;

// Turbine Utility Classes
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.velocity.context.Context;

/**
 * Same as turbine's JspPage, only that it extends JetspeedTemplatePage.
 * Adds some convenience objects to the request.
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 * @version $Revision: 1.7 $      
 */
public class JetspeedJspPage extends JetspeedTemplatePage
{ 

    /**
     * Same method as in turbine's JspPage
     * Stuffs some useful objects into the request so that 
     * it is available to the Action module and the Screen module
     */  
    protected void doBuildBeforeAction(RunData data) throws Exception
    {
        super.doBuildBeforeAction( data);
        JspService jsp = (JspService)TurbineServices.getInstance()
            .getService(JspService.SERVICE_NAME);
                
        jsp.addDefaultObjects(data);

        data.getResponse().setBufferSize(jsp.getDefaultBufferSize());

        // FIXME: this tools are now in TR.p file, as standard request tools instantiated by turbine.
        // All tools but jnavigation are there, jnavigation need to be instantiated here
        // because TemplateNavigation in which depends does have a no args constructor..and
        // turbine need one to be able to instantiate such class as request tool
        Context context = TurbineVelocity.getContext(data);
        TurbineVelocity.getContext(data).put("jnavigation", new JetspeedTemplateNavigation(data));
        data.getTemplateInfo().setTemplateContext(VelocityService.CONTEXT, context);

    }

}
