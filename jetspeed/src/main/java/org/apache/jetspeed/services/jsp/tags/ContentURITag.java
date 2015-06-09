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
import org.apache.turbine.util.ContentURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.jsp.JspService;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Supporting class for the contentURI tag.
 * Returns the URL for the given webapp relative URI
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: ContentURITag.java,v 1.7 2004/02/23 03:59:40 jford Exp $
 */
public class ContentURITag extends TagSupport 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ContentURITag.class.getName());
    
    /**
     * type parameter defines type of URI that is requested
     */
    private String href;

    /** 
     * The setter for type parameter
     */
    public void setHref(String href)
    {
        this.href = href;
    }

    public int doStartTag() throws JspException 
    {
        RunData data = (RunData)pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);    
        
        String result = new ContentURI(data).getURI(this.href);

        try
        {
            if (result != null) {
                pageContext.getOut().print(result);
            }
        }
        catch (Exception e)
        {
            String message = "Error processing contentUri-tag, parameter: "+ href;
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
