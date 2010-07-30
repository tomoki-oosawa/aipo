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

// Turbine Classes 
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateLink;
import org.apache.turbine.services.jsp.JspService;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Supporting class for the DynamicURI tag.
 * Basically routes the call to DynamicUri
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 * @version $Id: DynamicURITag.java,v 1.11 2004/02/23 03:59:40 jford Exp $
 */
public class DynamicURITag extends TagSupport 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(DynamicURITag.class.getName());
    
    /**
     * screen parameter defines the screen to set
     */
    private String screen;

    /** 
     * The setter for screen parameter
     */
    public void setScreen(String screen)
    {
        this.screen = screen;
    }

    /**
     * screen parameter defines the screen to set
     */
    private String template;

    /** 
     * The setter for screen parameter
     */
    public void setTemplate(String template)
    {
        this.template = template;
    }

    /**
     * action parameter defines the action to set
     */
    private String action;

    /** 
     * The setter for screen parameter
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    public int doStartTag() throws JspException 
    {
        RunData data = (RunData)pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);    
        
        TemplateLink uri = new TemplateLink( data );
        if ( template != null ) uri.setPage( template );
        if ( screen != null ) uri.setScreen( screen );
        if ( action != null ) uri.setAction( action );

        try
        {
            if (uri != null) {
                pageContext.getOut().print(uri.toString());
            }
        }
        catch (Exception e)
        {
            String message = "Error processing DynamicUriTag, parameter: screen='"+ screen + "', action='" +action +"'";
            logger.error(message, e);
            try
            {
                data.getOut().print( message );
            }
            catch(java.io.IOException ioe) {}    
        }
       
        return EVAL_BODY_INCLUDE;
    }

}
