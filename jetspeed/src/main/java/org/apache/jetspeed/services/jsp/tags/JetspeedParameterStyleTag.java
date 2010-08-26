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

package org.apache.jetspeed.services.jsp.tags;

// java classes
import java.util.Hashtable;
import java.util.StringTokenizer;

// jsp api
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

// Turbine Classes 
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.jsp.JspService;

// jetspeed
import org.apache.jetspeed.modules.ParameterLoader;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.Registry;

/**
 * Supporting class for the parameter style tag.
 * Sends a parameter rendered using specific style to the output stream.
 * 
 * The following tag attributes are supported:
 * 
 * <UL>
 * <LI><code>name</code>: parameter name (required).</li>
 * <LI><code>style</code>: parameter style name (required)</LI>
 * <LI><code>value</code>: parameter current value</LI>
 * <LI><code>portlet</code>: portlet name to check security against</LI>
 * </UL>
 * <p>Note: tag body may also contain parameter style options in format: option1=value1;optionN=valueN. Check
 * documentation for individual parameter style to see what options are supported</p>
 * <p>Note: Use care when specifying style options in the tag body - the body is not cleansed to remove
 * embedded carriage returns and tabs.</p>
 * Examples:
 * <UL>
 * <LI><code>&lt;jetspeed:parameterStyle name="portlet-list" style="RegistryEntryListBox"/&gt;</CODE>
 * <LI><code>&lt;jetspeed:parameterStyle name="skin-list" style="RegistryEntryListBox"&gt;registry=Skin&lt;/jetspeed:parameterStyle/&gt;</CODE>
 * <LI><code>&lt;jetspeed:parameterStyle name="control-list" style="RegistryEntryListBox" value="TabControl"&gt;registry=PortletControl&lt;/jetspeed:parameterStyle/&gt;</CODE>
 * </UL>
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JetspeedParameterStyleTag.java,v 1.4 2004/02/23 03:59:40 jford Exp $
 */
public class JetspeedParameterStyleTag extends BodyTagSupport
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedParameterStyleTag.class.getName());
    
    /**
     * name parameter defines parameter name
     */
    private String name = null;

    /**
     * name parameter defines parameter style
     */
    private String style = null;

    /**
     * name parameter defines current parameter style value
     */
    private String value = null;

    /**
     * name parameter defines portlet name to check security against
     */
    private String portlet = null;

    /**
     * The setter for name parameter
     * 
     * @param value
     */
    public void setName(String value)
    {
        this.name = value;
    }

    /**
     * The setter for value parameter
     * 
     * @param value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * The setter for syle parameter
     * 
     * @param value
     */
    public void setStyle(String value)
    {
        this.style = value;
    }

    /**
     * The setter for value parameter
     * 
     * @param value
     */
    public void setPortlet(String value)
    {
        this.portlet = value;
    }

    /**
     * 
     * @return code
     * @exception JspException
     */
    public int doStartTag() throws JspException 
    {
        return EVAL_BODY_TAG;
    }

    /**
     * 
     * @return code
     * @exception JspException
     */
    public int doEndTag() throws JspException 
    {
        RunData data = (RunData) pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);    
        String result = null;

        try
        {

            // See if body contains any parameter options
            String body = this.getBodyContent() == null ? null : this.getBodyContent().getString();
            Hashtable options = new Hashtable();

            if (body != null && !body.trim().equalsIgnoreCase(""))
            {
                StringTokenizer st = new StringTokenizer(body, ";");
                String prefix = this.name + ".style.";
                while (st.hasMoreTokens())
                {
                    StringTokenizer pair = new StringTokenizer(st.nextToken(), "=");
                    if (pair.countTokens() == 2)
                    {
                        options.put(prefix + pair.nextToken().trim(), pair.nextToken().trim());
                    }
                }
            }

            boolean canAccess = true;

            // If portlet name is specified, it will be used to check security for the parameter
            if (this.portlet != null)
            {
                // Retrieve registry entry and its parameter
                PortletEntry entry = (PortletEntry) Registry.getEntry(Registry.PORTLET, this.portlet);
                Parameter param = entry.getParameter(this.name);

                // Verify security for the parameter
                canAccess = JetspeedSecurity.checkPermission((JetspeedUser) data.getUser(), 
                                                             new PortalResource(entry, param), 
                                                             JetspeedSecurity.PERMISSION_CUSTOMIZE);
            }

            if (canAccess)
            {
                result = ParameterLoader.getInstance().eval(data, 
                                                            this.style, 
                                                            this.name, 
                                                            this.value, 
                                                            options);                
            }

            pageContext.getOut().print(result);


        }
        catch (Exception e)
        {
            result = "<input type=\"text\" name=\"" + this.name + "\" value=\"" + this.value + "\"";

            String message = "Error processing portlet (PortletTag): [" + name + "]";
            logger.error(message, e);
            try
            {
                pageContext.getOut().print(result);
            }
            catch (java.io.IOException ioe)
            {
            }
        }

        return EVAL_PAGE;
    }

}