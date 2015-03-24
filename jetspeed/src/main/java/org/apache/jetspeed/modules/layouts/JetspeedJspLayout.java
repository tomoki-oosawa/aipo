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
package org.apache.jetspeed.modules.layouts;

// Jetspeed imports
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

//Turbine imports
import org.apache.turbine.modules.Layout;
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.jsp.TurbineJspService;

/*
 * This Layout module allows JSP templates to be used as layouts.
 * Same as turbine's JspLayout, except that:-
 * - The Mimetype is not fixed text/html but the one set in JetspeedTemplatePage
 * - Navigations are handled by the layout Jsp only
 * - Not the result of the screen is placed in the request, just the screen name and relative path.
 *   The Ecs.jsp will handle ecs-screen processing.
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: JetspeedJspLayout.java,v 1.21 2004/02/23 02:59:30 jford Exp $
 */
public class JetspeedJspLayout extends Layout
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedJspLayout.class.getName());     
    
    /** 
     * Method called by LayoutLoader.
     *
     * @param RunData
     */
    public void doBuild(RunData data) throws Exception
    {
        String screenPath  = null;
        
        // FIXME:  using TurbineJspService instead of the interface JspService 
        //         because getRelativeTemplateName() is not defined in the 
        //         interface.  A patch has been submitted to Turbine.

        TurbineJspService jsp = (TurbineJspService)TurbineServices.getInstance().getService(JspService.SERVICE_NAME);

        // set the content type (including charset)
        data.getResponse().setContentType(data.getContentType());
        if (logger.isInfoEnabled() )
        {
            logger.info("JetspeedJspLayout: set response content type to " + data.getContentType());
        }

        // tell turbine that the response is handled by the JSP system.
        data.declareDirectResponse();

        // Put the path to the screen template into the request.
        String path = TemplateLocator.locateScreenTemplate(data, data.getScreenTemplate());
        if (path != null)
            screenPath = jsp.getRelativeTemplateName("/screens" + path);
        data.getRequest().setAttribute("screenJsp", screenPath);
        if (logger.isInfoEnabled() )
        {        
            logger.info("JetspeedJspLayout: set 'screenJSP' to: " + screenPath );
        }

        // Grab the layout template set in the JetspeedTemplatePage.  
        String templateName = data.getLayoutTemplate();

        // Finally, generate the layout template and output to the response
        if (logger.isInfoEnabled() )
        {
            logger.info("JetspeedJspLayout: forward request to: " +  "/layouts" + templateName);
        }
        jsp.handleRequest(data, "/layouts" + templateName, false);
    }
    
}
