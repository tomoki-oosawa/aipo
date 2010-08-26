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
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;

/**
 * Supporting class for the portlet link tag.
 * Builds a link to displaying a portlet in specified view and insert it within the
 * current JSP page. If js_peid is specified it is used as is. Otherwise, the js_peid
 * is derived from the first portlet in profile matching specified portlet name.
 *
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JetspeedPortletLinkTag.java,v 1.4 2004/02/23 03:59:40 jford Exp $
 */
public class JetspeedPortletLinkTag extends TagSupport
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedPortletLinkTag.class.getName());
    
    private String name = null;
    private String jspeid = null;
    private String psml = null;
    private String action = null;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setPsml(String psml)
    {
        this.psml = psml;
    }

    public String getPsml()
    {
        return this.psml;
    }

    public void setJspeid(String value)
    {
        this.jspeid = value;
    }

    public String getJspeid()
    {
        return this.jspeid;
    }

    public void setAction(String value)
    {
        this.action = value;
    }

    public String getAction()
    {
        return this.action;
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
            Profile baseProfile = null;
            ProfileLocator baseLocator = Profiler.createLocator();
            int rootType = JetspeedLink.DEFAULT;
            String rootValue = null;
            int elementType = JetspeedLink.DEFAULT;
            String elementValue = null;

            // Create locator to retrieve profile settings
            if (this.psml != null)
            {
               baseLocator.createFromPath(this.psml);
               if (baseLocator.getUser() != null)
               {
                   rootType = JetspeedLink.USER;
                   rootValue = baseLocator.getUserName();
               } 
               else if (baseLocator.getRole() != null)
               {
                   rootType = JetspeedLink.ROLE;
                   rootValue = baseLocator.getRoleName();
               }
               else if (baseLocator.getGroup() != null)
               {
                   rootType = JetspeedLink.GROUP;
                   rootValue = baseLocator.getGroupName();
               }
            }
            else
            {
               rootType = JetspeedLink.CURRENT;
               rootValue = "";
               baseProfile = data.getProfile();
               baseLocator.createFromPath(baseProfile.getPath());
            }

            //  Determine search method
            if (baseLocator != null)
            {                
                // search by portlet name
                if (this.name != null)
                {
                    elementType = JetspeedLink.PORTLET_ID_QUERY;
                    elementValue = this.name;
                }
                else if (this.jspeid != null)
                {
                    elementType = JetspeedLink.PORTLET_ID;
                    elementValue = this.jspeid;
                }
                // Build the link
                JetspeedLink link = JetspeedLinkFactory.getInstance(data);
                DynamicURI uri = link.getLink(rootType,
                                              rootValue,
                                              baseLocator.getName(),
                                              elementType,
                                              elementValue,
                                              this.action == null ? "controls.Maximize" : this.action,
                                              null,
                                              baseLocator.getMediaType(),
                                              baseLocator.getLanguage(),
                                              baseLocator.getCountry());
                result = new StringElement(uri.toString());
                JetspeedLinkFactory.putInstance(link);
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
                data.getOut().print("Error processing portlet '" + name + "'. See log for more information.");
            }
            catch (java.io.IOException ioe) 
            {
            }
        }
        return EVAL_BODY_INCLUDE;
    }
}
