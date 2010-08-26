package org.apache.jetspeed.portal.portlets;

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

// jetspeed
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;

// ecs
import org.apache.jetspeed.util.JetspeedClearElement;
import org.apache.ecs.ConcreteElement;

// turbine
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.velocity.context.Context;

/**
 * Preview portlet. Displays content of potlet
 * defined in parameter "previewedPortletName".
 * 
 * @version $Id: PreviewPortlet.java,v 1.5 2004/02/23 04:03:33 jford Exp $ 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a> 
 * @author <a href="mailto:mark_orciuch@ngsltd.com">Mark Orciuch</a>
 */
public class PreviewPortlet extends AbstractPortlet
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(PreviewPortlet.class.getName());
    
    /**
     * @param rundata The RunData object for the current request
     */
    public ConcreteElement getContent(RunData rundata)
    {

        Context context = TurbineVelocity.getContext(rundata);
        context.put( "data", rundata);
        TurbineVelocity.getContext(rundata).put("jetspeed", new org.apache.jetspeed.util.template.JetspeedTool(rundata));

        ConcreteElement result = null;
        String controlName = rundata.getParameters().getString("previewedControlName");
        String portletName = rundata.getParameters().getString("previewedPortletName");
        if ( portletName == null )
        {
            JetspeedLink jsLink = null;

            try
            {
                jsLink = JetspeedLinkFactory.getInstance(rundata);
                rundata.setRedirectURI(
                    jsLink.setUser(
                        rundata.getUser().getUserName(), "default.psml")
                    .addPathInfo("template", "Customize")
                    .addPathInfo("media-type", "html")
                    .toString()
                    );
                JetspeedLinkFactory.putInstance(jsLink);
                jsLink = null;
                return new JetspeedClearElement("");
            } catch (Exception e)
            {
                logger.error("Exception",  e);
                rundata.setScreenTemplate("Home");
                return new JetspeedClearElement("");
            }
            //return new JetspeedClearElement("You must specify portlet to preview using [previewedPortletName] parameter");
        }
        Portlet portlet = null;

        try
        {
            portlet = PortletFactory.getPortlet(portletName, "PreviewPortlet");
            PortletControl control = controlName == null ? PortalToolkit.getControl((String)null) :
                                     PortalToolkit.getControl(controlName);
            control = null;
            if ( control != null )
            {
                JetspeedRunData jdata = (JetspeedRunData)rundata;
                // Use the profile's skin
                //portlet.getPortletConfig().setSkin(PortalToolkit.getSkin(jdata.getProfile().getDocument().getPortlets().getSkin()));
                control.setPortlet(portlet);
                control.init();
                result = control.getContent(rundata);
            } 
            else if ( portlet != null )
            {
                result = portlet.getContent(rundata);
            }

            if ( result != null && !result.toString().equals("") )
            {
                /*String html =  result.toString();
                String currentURL = rundata.getRequest().getRequestURI();
                Log.debug("PreviewPortlet: currentURL = " + currentURL + " reader value = " + html);
                PreviewRewriter rewriter = new PreviewRewriter(currentURL, currentURL , currentURL, portletName);
                result = new JetspeedClearElement(rewriter.rewrite(new StringReader(html)));*/
            } 
            else
            {
                // The portlet already streamed its content - return a stub
                result = new JetspeedClearElement("");
            }

        } 
        catch ( Exception e )
        {
            logger.error("Exception",  e);
            result = new JetspeedClearElement("This resource is temporarily unavailable");
        }

        rundata.setLayout("preview");
        return result;
    }

}
