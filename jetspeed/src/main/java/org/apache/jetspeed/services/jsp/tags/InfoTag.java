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

import java.util.Date;
import java.text.DateFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

// Turbine Classes 
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.jsp.JspService;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Supporting class for the info tag.
 * Sends the screens ecs element's content to the output stream.  
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 */
public class InfoTag extends TagSupport 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(InfoTag.class.getName());
    
    /**
     * requestedInfo parameter defines type of Info that is being requested
     */
    private String requestedInfo;

    /** 
     * The setter for requestedInfo parameter
     */
    public void setRequestedInfo(String requestedInfo)
    {
        this.requestedInfo = requestedInfo;
    }


    public int doStartTag() throws JspException 
    {
        RunData data = (RunData)pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);    
        
        try
        {           
            String result = "jetspeed-InfoTag: unknown parameter >" + requestedInfo +"<";

            /* Server Date */
            if (requestedInfo.equalsIgnoreCase("ServerDate")) {
                result = DateFormat.getDateTimeInstance().format( new Date());
            }  

            /* User Name */
            if (requestedInfo.equalsIgnoreCase("UserName")) {
              result = data.getUser().getUserName();
            }  

            /* First Name */
            if (requestedInfo.equalsIgnoreCase("FirstName")) {
              result = data.getUser().getFirstName();
            }  

            /* Last Name */
            if (requestedInfo.equalsIgnoreCase("LastName")) {
              result = data.getUser().getLastName();
            }  

            /* EMail */
            if (requestedInfo.equalsIgnoreCase("EMail")) {
              result = data.getUser().getEmail();
            }  
            pageContext.getOut().print(result);
        }
        catch (Exception e)
        {
            String message = "Error processing info-tag, parameter: "+ requestedInfo;
            logger.error(message, e);
            try
            {
                data.getOut().print("Error processing info-tag, parameter: "+ requestedInfo);
            }
            catch(java.io.IOException ioe) {}    
        }
        return EVAL_BODY_INCLUDE;
    }
}
