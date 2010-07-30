package org.apache.jetspeed.services.jsp.tags;

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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;


// JetSpeed Classes
import org.apache.jetspeed.services.TemplateLocator;

// Turbine Classes
import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.TurbineServices;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Supporting class for the navigation tag.
 * Includes a navigation JSP. If the respective tag parameter is set,
 * different JSPs will be choosen, depending on whether the user has 
 * already logged in or not.
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 */
public class NavigationTag extends TagSupport 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(NavigationTag.class.getName());
    
    /**
     * defaultTemplate parameter defines the template whose contents will replace
     * this tag in the layout if none of the special states are detected.
     */
    private String defaultTemplate;

    /** 
     * The setter for DefaultTemplate parameter
     */
    public void setDefaultTemplate(String defaultTemplate) 
    {
        this.defaultTemplate = defaultTemplate;
    }

    /**
     * loggedInTemplate parameter defines the template whose contents will replace
     * this tag in the layout if the user is already logged into the system.
     */
    private String loggedInTemplate;

    /** 
     * The setter for loggedInTemplate parameter
     */
    public void setLoggedInTemplate(String loggedInTemplate) 
    {
        this.loggedInTemplate = loggedInTemplate;
    }


    /**
     * Method called when the tag is encountered to send the navigation
     * template's contents to the output stream
     *
     * @return SKIP_BODY, as it is intended to be a single tag.
     */
    public int doStartTag() throws JspException 
    {
        String template = defaultTemplate;
        String module   = null;

        RunData data = (RunData)pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);
        try
        {  
            /* LOGGED_IN */
            if ( (data != null) && (data.getUser() != null) && data.getUser().hasLoggedIn() && (loggedInTemplate != null) ) 
              template = loggedInTemplate;

            data.getTemplateInfo().setNavigationTemplate(
                        TemplateLocator.locateNavigationTemplate(data,template));

            pageContext.getOut().flush();
            module = ((TemplateService)TurbineServices.getInstance().getService(
            TemplateService.SERVICE_NAME)).getNavigationName(template);
            NavigationLoader.getInstance().exec(data, module);
        }
        catch (Exception e)
        {
            String message = "Error processing navigation template:" + template + " using module: " + module;
            logger.error(message, e);
            try
            {
                data.getOut().print("Error processing navigation template: " + template + " using module: " + module);
            }
            catch(java.io.IOException ioe) {}    
        }
        return SKIP_BODY;
    }
}
