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
import org.apache.turbine.services.jsp.JspService;

import org.apache.ecs.ConcreteElement;

// Jetspeed classes
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.services.Profiler;

/**
 * Supporting class for the portlet tag.
 * Builds the output of a portlet (with conrol) and insert it within the
 * current JSP page
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: JetspeedPortletTag.java,v 1.10 2004/02/23 03:59:40 jford Exp $
 */
public class JetspeedPortletTag extends TagSupport
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedPortletTag.class.getName());
    
    private String name = null;
    private String psml = null;

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

    /**
     * Method called when the tag is encountered to send attributes to the
     * output stream
     *
     * @return SKIP_BODY, as it is intended to be a single tag.
     */
    public int doStartTag() throws JspException
    {
        JetspeedRunData data = (JetspeedRunData)pageContext.getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);

        // if called without arguments, use the default request parameter
        if (this.name == null)
        {
            this.name = data.getPortlet();
        }

        try
        {
            pageContext.getOut().flush();

            ConcreteElement result = new ConcreteElement();
            Entry entry = null;

            if (this.psml != null)
            {
                ProfileLocator baseLocator = Profiler.createLocator();
                baseLocator.createFromPath(this.psml);
                Profile baseProfile = Profiler.getProfile(baseLocator);
                if (baseProfile != null)
                {
                    entry = baseProfile.getDocument().getEntry(name);
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug("JetspeedPortletTag: retrieved [" + entry + "] from psml [" + this.psml);
                    }
                }
            }
            else
            {
                entry  = data.getProfile().getDocument().getEntry(name);
                if ( logger.isDebugEnabled() )
                {
                    logger.debug("JetspeedPortletTag: retrieved [" + entry + "] from current psml");
                }
            }

            if (entry != null)
            {
                result = PortletFactory.getPortlet(entry).getContent(data);
            }

            // Check whether this is an "old" screen (that returns a ConcreteElement)
            // or a "new" one that returns null.
            if ( result != null )
            {
                //The ECS element must serialize in the character encoding
                // of the response
                result.setCodeSet( data.getResponse().getCharacterEncoding() );

                result.output( data.getResponse().getWriter() );
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
            catch(java.io.IOException ioe) {}
        }
        return SKIP_BODY;
    }
}
