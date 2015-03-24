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
package org.apache.jetspeed.portal.portlets.viewprocessor;

// Ecs
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;

// Jetspeed portal
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.portlets.GenericMVCContext;
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.util.ServiceUtil;

// Turbine stuff
import org.apache.turbine.services.jsp.JspService;

// Turbine util
import org.apache.turbine.util.RunData;

//java stuff
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.RequestDispatcher;

/**
 * <b>JspViewProcessor</b> - MVC processor for serving jsp files.
 * <p>
 * The .jsp file location may be specified in two different ways:
 * <li><b>using the "template" parameter</b> - the JspTemplateService will search portlets and then screens 
 * folder to locate the appropriate template. The template must be specifed in the "template" 
 * portlet parameter.
 * <li><b>using relative url</b> - the .jsp template will be served directly bypassing the 
 * JspTemplateService. The template must be specifed in the portlet url property. 
 * Example: /html/welcome.jsp.
 * <P>
 * 
 * @author <a href="mailto:tkuebler@cisco.com">Tod Kuebler</a>
 * @author <a href="mailto:weaver@apache.org">Scott Weaver</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: $
 */
public class JSPViewProcessor
implements ViewProcessor
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JSPViewProcessor.class.getName());
    
    /** Creates a new instance of JSPViewProcessor */
    public JSPViewProcessor()
    {
    }

    public Object processView(GenericMVCContext context)
    {

        Portlet portlet = (Portlet) context.get("portlet");
        RunData data = (RunData) context.get("data");
        HttpServletRequest request = data.getRequest();
        String template = (String) context.get("template");
        logger.info("JSPViewProcessor - processing template " + template);

        try
        {

            // Allow access to portlet from .jsp template
            request.setAttribute("portlet", portlet);

            // put context in attribute so you can get to it from .jsp template
            request.setAttribute("context", context);

            // Add js_peid out of convenience
            request.setAttribute("js_peid", portlet.getID());

            // Add rundata out of convenience (JspService.RUNDATA differs from GenericMVCPortlet.RUNDATA)
            request.setAttribute(JspService.RUNDATA, data);

            // Retrieve the URL. For backward compatibility, use the URL first 
            // and then fallback to "template" parameter
            PortletEntry pe = (PortletEntry) Registry.getEntry(Registry.PORTLET, portlet.getName());

            // Files referenced from default templates folder will be processed
            // using JspService. Otherwise, they will be loaded using EcsServletElement
            // from where ever they came from.
            if (pe.getURL() == null || pe.getURL().trim().length() == 0)
            {

                if (template != null && -1 == template.indexOf(".jsp"))
                {
                    template = template + ".jsp";
                }

                logger.info("JSPViewProcessor - locating template - " + data.toString() 
                         + " - " + template);

                //we use the template locator to translate the template
                String locatedTemplate = TemplateLocator.locatePortletTemplate(data, template);
                logger.info("JSPViewProcessor - located template: " + locatedTemplate);

                /*if (locatedTemplate == null)
                {
                    locatedTemplate = TemplateLocator.locateScreenTemplate(data, template);
                    if (locatedTemplate != null)
                    {
                        locatedTemplate = "/screens" + locatedTemplate;
                    }
                    logger.debug("JSPViewProcessor - located screen template: " + locatedTemplate);
                } */

                JspService service = (JspService) ServiceUtil.getServiceByName(JspService.SERVICE_NAME);

                // this is only necessary if we don't run in a JSP page environment
                // but better be safe than sorry...
                service.addDefaultObjects(data);

                // handle request
                service.handleRequest(data, locatedTemplate);

            }
            else
            {
                // Build parameter list to be passed with the jsp
                Iterator names = portlet.getPortletConfig().getInitParameterNames();
                while (names.hasNext()) 
                {
                    String name = (String) names.next();
                    String value = (String) portlet.getPortletConfig().getInitParameter(name);
                    data.getParameters().setString(name, value);
                }

                template = pe.getURL();

                if (logger.isDebugEnabled())
                {
                    logger.debug("JSPViewProcessor - serving jsp directly using: " + template);
                }

                // get the RequestDispatcher for the JSP
                RequestDispatcher dispatcher = data.getServletContext().getRequestDispatcher(template);
                data.getOut().flush();
                dispatcher.include(data.getRequest(), data.getResponse());
            }

        }
        catch (Exception e)
        {

            String message = "JSPViewProcessor: Could not include the following JSP Page:  [" + template + "] :\n\t" 
                             + e.getMessage();
            logger.error(message, e);

            return new StringElement(message);
        }

        return new ElementContainer();
    }

    /** Process the template passed in the context
     * (context.get("template")).  Invoked by the GenericMVCPortlet
     * after action handling to process the template type
     * in question.
     *
     */
    public void init(Portlet portlet)
    {
    }
}
