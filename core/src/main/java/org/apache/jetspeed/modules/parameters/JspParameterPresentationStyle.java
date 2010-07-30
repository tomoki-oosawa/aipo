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

package org.apache.jetspeed.modules.parameters; 

// Turbine support
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.jsp.JspService;

// Java stuff
import java.util.Map;

// jetspeed stuff
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Generic jsp-based presentation style. The following default objects are put in the context:
 * <UL>
 * <LI>data - rundata object</LI>
 * <LI>name - name of the parameter</LI>
 * <LI>value - current value of the parameter</LI>
 * <LI>parms - map of additional style parameters</LI>
 * </UL>
 * 
 * <P>Supporting jsp templates should be placed in ${velocity-templates-root}/parameters folder.</p>
 * 
 * <P>It may be used directly with "template" as the only required parameter. This is useful when the
 * no additional objects are needed by the template.</P>
 * 
 * <P>If additional objects need to be put in the context, a new class extending JspParameterPresentationStyle
 * should be created. Override buildContext to place custom objects in the jsp context.</P>
 * 
 * <P>If "template" parameter is not specified, it is assumed that the template name is "classname.vm".</P>
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JspParameterPresentationStyle.java,v 1.4 2004/02/23 03:01:20 jford Exp $
 */

public class JspParameterPresentationStyle extends ParameterPresentationStyle
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JspParameterPresentationStyle.class.getName());     
    
    /**
     * Returns presentation control
     * 
     * @param data - rundata object
     * @param name - parameter name
     * @param value - current parameter value
     * @param parms - additional style parameters
     * @return string
     */
    public String getContent(RunData data, String name, String value, Map parms)
    {
        String result = null;

        // Get reference to jsp service
        JspService jspService = (JspService) TurbineServices.getInstance().getService(JspService.SERVICE_NAME);

        // Put basics in the context
        data.getRequest().setAttribute("data", data);
        data.getRequest().setAttribute("name", name);
        data.getRequest().setAttribute("value", value);
        data.getRequest().setAttribute("parms", parms);
        data.getRequest().setAttribute("events", this.getJavascriptEvents());

        try
        {
            // Add custom objects to the context
            this.buildContext(data, name, value, parms);

            // Build default template name (classname + .vm)
            String className = this.getClass().getName();
            int pos = className.lastIndexOf(".");
            pos = pos < 0 ? 0 : pos + 1;
            className = className.substring(pos);

            // Render the template
            String template = (String) this.getParm("template", className + ".jsp");
            String templatePath = TemplateLocator.locateParameterTemplate(data, template);
            jspService.handleRequest(data, templatePath);
            result = "";
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
            // Fallback to input text box presentation style
            result = "<input type=\"text\" name=\"" + name + "\" value=\"" + value + "\"";
        }

        return result;

    }

    /**
     * Override this method to put your own objects in the Velocity context
     * 
     * @param data
     * @param name
     * @param value
     * @param parms
     * @param context
     */
    public void buildContext(RunData data, String name, String value, Map parms)
    {

    }
}