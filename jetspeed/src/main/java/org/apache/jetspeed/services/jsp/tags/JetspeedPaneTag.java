package org.apache.jetspeed.services.jsp.tags;

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

//import java.util.*;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

// Turbine Classes 
import org.apache.turbine.services.jsp.JspService;

import org.apache.ecs.ConcreteElement;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedTool;

/**
 * Supporting class for the pane tag.
 * Builds the output of a PSML config file and insert it within the 
 * current JSP page
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: JetspeedPaneTag.java,v 1.6 2004/02/23 03:59:40 jford Exp $
 */
public class JetspeedPaneTag extends TagSupport 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedPaneTag.class.getName());
    
    private String name = null;

    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
   
    /**
     * Method called when the tag is encountered to send attributes to the
     * output stream
     *
     * @return SKIP_BODY, as it is intended to be a single tag.
     */
    public int doStartTag() throws JspException                   
    {
        JetspeedRunData data = (JetspeedRunData) pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);
        
        // retrieve the name attribute val
        if (this.name == null)
        {
            this.name = JetspeedResources.getString("screen.homepage");
        }
            
        try
        {  
            pageContext.getOut().flush();

            ConcreteElement result = new ConcreteElement();

            if (data != null && data.getUser() != null)
            {
                JetspeedTool jt = new JetspeedTool(data);
                String jspeid = (String) data.getUser().getTemp("js_peid");
                if (jspeid != null)
                {
                    data.setMode(JetspeedRunData.MAXIMIZE);
                    result = jt.getPortletById(jspeid);
                }
                else 
                {
                    result = jt.getPane(this.name);
                }
            }
        
            // Check whether this is an "old" screen (that returns a ConcreteElement)
            // or a "new" one that returns null.
            if (result != null)
            {
                //The ECS element must serialize in the character encoding
                // of the response
                result.setCodeSet(data.getResponse().getCharacterEncoding());

                result.output(data.getResponse().getWriter());
            }

        }
        catch (Exception e)
        {
            String message = "Error processing name '" + name + "'.";
            logger.error(message, e);
            try
            {
                data.getOut().print("Error processing ecs screen '" + name + "'. See log for more information.");
            }
            catch (java.io.IOException ioe) 
            {
            }    
        }
        return SKIP_BODY;
    }
}
