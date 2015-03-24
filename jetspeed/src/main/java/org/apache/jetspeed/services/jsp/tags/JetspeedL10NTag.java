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
package org.apache.jetspeed.services.jsp.tags;

// Servlet API
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

// Turbine Classes
import org.apache.turbine.services.jsp.JspService;

// ECS support
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

// Jetspeed support
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationTool;
import org.apache.jetspeed.services.customlocalization.CustomLocalization;

/**
 * Supporting class for the localization (l10n) tag.
 * Returns a localized text for string specified in the "key" parameter.
 * Use "alt" parameter to specify a default value if "key" is not translated.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JetspeedL10NTag.java,v 1.6 2004/02/23 03:59:40 jford Exp $
 */
public class JetspeedL10NTag extends TagSupport
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedL10NTag.class.getName());
    
    private CustomLocalizationTool tool = null;

    private String key = null;
    private String alt = null;

    public void setKey(String value)
    {
        this.key = value;
    }

    public String getKey()
    {
        return this.key;
    }

    public void setAlt(String value)
    {
        this.alt = value;
    }

    public String getAlt()
    {
        return this.alt;
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
            if (this.tool == null)
            {
                this.tool = new CustomLocalizationTool();
                this.tool.init(data);
            }
            ConcreteElement result = null;
            String def = this.alt != null && this.alt.trim().length() > 0 ? this.alt : this.key;

            try 
            {
                String translation = this.tool.get(key, CustomLocalization.getLocale(data));
								
                translation = translation == null ? def : translation;
                result = new StringElement(translation);            
            }
            catch (Exception oe)
            {
                result = new StringElement(def);
                logger.error("Exception", oe);
            }

            // Output the result
            if (result != null)
            {
                pageContext.getOut().print(result);
            }

        }
        catch (Exception e)
        {
            String message = "Error processing key '" + this.key + "'.";
            logger.error(message, e);
            try
            {
                data.getOut().print("Error translating key '" + this.key + "'. See log for more information.");
            }
            catch (java.io.IOException ioe)
            {
            }
        }
        return EVAL_BODY_INCLUDE;
    }
}
