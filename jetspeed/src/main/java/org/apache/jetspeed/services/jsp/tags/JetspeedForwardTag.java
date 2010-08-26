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

// Servlet API
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

// Turbine Classes
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.services.jsp.JspService;

// ECS support
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

// Jetspeed support
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.forward.ForwardService;
import org.apache.jetspeed.util.ServiceUtil;

/**
 * Supporting class for the forward tag. Enables forwarding navigation to other pages or panes
 * in the portal. If "name" and "target" are both specified, the "name" is assumed to be a portlet.
 * If only "name" is specified, the name is assumed to be a logical forward name.
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JetspeedForwardTag.java,v 1.3 2004/02/23 03:59:40 jford Exp $
 * @see org.apache.jetspeed.services.forward.ForwardService#forward(RunData rundata, String forwardName)
 * @see org.apache.jetspeed.services.forward.ForwardService#forward(RunData rundata, String portlet, String target)
 * @since 1.4b4
 */
public class JetspeedForwardTag extends TagSupport
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedForwardTag.class.getName());
    
    private String name = null;
    private String target = null;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getTarget()
    {
        return this.target;
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
        
        try
        {
            ConcreteElement result = null;
            DynamicURI uri = null;
            ForwardService service = (ForwardService) ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);

            if (this.name != null && this.target != null)
            {
                uri = service.forward(data, this.name, this.target);
            }
            else if (this.getName() != null)
            {
                uri = service.forward(data, this.name);
            }
            if (uri != null)
            {
                result = new StringElement(uri.toString());
            }

            // Output the result
            if (result != null) 
            {
                pageContext.getOut().print(result);
            }

        }
        catch (Exception e)
        {
            String message = "Error processing name '" + name + "'.";
            logger.error(message, e);
            try
            {
                data.getOut().print("Error processing forward name '" + name + "'. See log for more information.");
            }
            catch (java.io.IOException ioe) 
            {
            }
        }
        return EVAL_BODY_INCLUDE;
    }
}
