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
package org.apache.jetspeed.util.template;

import org.apache.log4j.Logger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * JspTemplate
 *
 * @author <a href="mailto:junyang@cisco.com">Jun Yang</a>
 * @version $Id: JspTemplate.java,v 1.2 2004/02/23 03:20:46 jford Exp $
 */
public class JspTemplate 
{
    protected RunData runData;
    protected String templateName;
    private static Logger logger = Logger.getLogger(JspTemplate.class);    
    public JspTemplate(RunData runData, String templateName)
    {
        this.runData = runData;
        this.templateName = templateName;
    }

    public String getContent()
    {
        JspService jsp = (JspService) TurbineServices.getInstance().getService(JspService.SERVICE_NAME);
        try
        {
            jsp.handleRequest(this.runData, this.templateName);
        }
        catch (TurbineException te1)
        {
            if (!this.templateName.endsWith(".jsp"))
            {
                try
                {
                    jsp.handleRequest(this.runData, this.templateName + ".jsp");
                }
                catch (TurbineException te2)
                {
                    logger.error("failed to invoke JSP Template '" + 
                                    this.templateName + "' and '" + this.templateName + ".jsp'", te2);
                }
            }
            else
            {
                logger.error("failed to invoke JSP Template '" + this.templateName + "'", te1);            
            }
        }
        
        return "";
    }
}
