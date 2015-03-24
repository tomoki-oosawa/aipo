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
package org.apache.jetspeed.portal.portlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.ecs.ConcreteElement;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.portlets.viewprocessor.ViewProcessor;
import org.apache.jetspeed.portal.portlets.viewprocessor.ViewProcessorFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedTemplateLink;
import org.apache.jetspeed.util.template.JspTemplate;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.util.RunData;

/**
 * Provides the basic MVC Portlet functionality independant of any
 * specific view technology (ie jsp, velocity, xslt, etc).  It handles
 * the views via a ViewProcessor, which is a pluggable, factory
 * created, run time module for which ever view technology your portlet
 * uses.
 * 
 * There is no need to extend this portlet class, just define your porlet
 * entry in the registy as a child of this class and provide your template
 * and action class (extened from GenericMVCAction of course) and you
 * are good to go.
 * 
 * Example .xreg entry:
 * 
 *  <portlet-entry name="GenericMVCPortlet" hidden="false"
 *       type="abstract" application="false">
 *       <security-ref parent="default"/>
 *       <classname>com.cisco.it.psf.portal.portlets.GenericMVCPortlet</classname>
 *      <media-type ref="html"/>
 *      <url cachedOnURL="true"/>
 *  </portlet-entry>
 *  <portlet-entry name="VelocityMVCExample" hidden="false" type="ref"
 *      parent="GenericMVCPortlet" application="false">
 *      <meta-info>
 *          <title>Velocity MVC Portlet</title>
 *          <description>Velocity Generic MVC Portlet</description>
 *      </meta-info>
 *      <classname>com.cisco.it.psf.portal.portlets.GenericMVCPortlet</classname>
 *      <parameter name="template" value="mvc-example" hidden="true"
 *          cachedOnName="true" cachedOnValue="true"/>
 *      <parameter name="viewtype" value="Velocity" hidden="true"
 *          cachedOnName="true" cachedOnValue="true"/>
 *      <parameter name="action"
 *          value="portlets.ExampleGenericMVCAction" hidden="true"
 *          cachedOnName="true" cachedOnValue="true"/>
 *      <url cachedOnURL="true"/>
 *  </portlet-entry>
 * 
 * See the Velocity and JSP MVC Portlet examples for template and action class clues.
 * 
 * To add new view processor types, simply implement the ViewProcessor
 * interface and add your type into the <b>viewprocessor.properties</b> file as
 * shown in the example below:
 * 
 * mvc.viewprocessor.Velocity = org.apache.jetspeedportlets.viewprocessors.VelocityViewProcessor
 * mvc.viewprocessor.JSP = org.apache.jetspeedportlets.viewprocessors.JSPViewProcessor
 * mvc.viewprocessor.XSL = org.apache.jetspeedportlets.viewprocessors.XSLViewProcessor
 * 
 * @stereotype role
 * @author tkuebler
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: GenericMVCPortlet.java,v 1.11 2003/02/11 23:09:18 tkuebler Exp $
 */
public class GenericMVCPortlet extends AbstractInstancePortlet
{
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(GenericMVCPortlet.class.getName());    
    
    // STW: Context keys
    public static final String PORTLET = "portlet";
    public static final String TEMPLATE = "template";
    public static final String RUNDATA = "data";
    public static final String PORTLET_CONFIG = "conf";
    public static final String SKIN = "skin";
    public static final String VIEW_TYPE = "viewType";
    public static final String IS_CACHEABLE = "_IsCacheable";

    // need cache timer, etc
    private String viewType = "ERROR: not set in config";
    private String actionName = "ERROR: not set in config";
    private String template = "ERROR: not set in config";
    private String configureTemplate;
    private String maximizedTemplate;
    private ViewProcessor processor = null;
    private boolean providesCustomization;

    public static final String RENDERING_DELAYED = "renderingDelayed";
    public static final String SIMULATE_DELAY = "simulateDelay";
    public static final String PORTLET_ID = "__portletId";
    public static final String DOC_URL = "__docUrl";
  
    public void init() throws PortletException
    {
        
        //STW: check custimization attribute
        String provConf = getPortletConfig().getInitParameter("provides.customization", "false");
        providesCustomization = new Boolean(provConf).booleanValue();
        
        // pull the important info out of the portlet config
        actionName = getPortletConfig().getInitParameter("action");
        // STW: Allow subclasses to set viewtype for backward compatibillity
        if (getPortletConfig().getInitParameter("viewtype") != null)
        {
            viewType = getPortletConfig().getInitParameter("viewtype");
        }

        template = getPortletConfig().getInitParameter("template");

        // get viewprocessor from factory
        logger.info(
            "GenericMVCPortlet - creating view processor for viewtype = "
                + viewType
                + ", template = "
                + template);
        processor = ViewProcessorFactory.getViewProcessor(viewType);

        // initialize view processor with portlet info
        processor.init(this);

        // should make this config file driven
        // STW removed this so subclasses can decide this for
        // themselves.
        // setCacheable(false);
    }

    /**
     * By default MVCPortlets are cacheable. This can be overriden by specifying
     * "_IsCacheable" parameter.
     * 
     * @return 
     */
    public boolean isCacheable()
    {
        return getPortletConfig().getInitParameter(IS_CACHEABLE, "true").equalsIgnoreCase("true");
    }

/**
 * Whether or not this portlet provides it's own customizer.
 * This can be set at the registry level by adding a 
 * boolean paramter "provides.customization"
 * Defaults to "false"
 */
    public boolean providesCustomization()
    {
        return providesCustomization;
    }

    public ConcreteElement getContent(RunData rundata)
    {
        if (useDelayedRendering(rundata))
        {
            Profile profile = ((JetspeedRunData) rundata).getProfile();
            String path = profile.getPath();
            String portletId = getID();
            
            // FIXME: please use JetspeedLink to create Portal URLs           
            String docUrl = "portal/" + path + "/js_peid/" + portletId + "?action=controls.Print";
            docUrl = URLEncoder.encode(docUrl); //, "UTF-8");
            // END FIXME:
            
            HttpServletRequest request = rundata.getRequest();
            request.setAttribute(PORTLET_ID, portletId);
            request.setAttribute(DOC_URL, docUrl);

            // render content that pulls
            return renderJspTemplate(rundata, "delayedContent.jsp");          
        }
        
        simulateDelay();             
            
        //if caching is turned off or no expiration time set, generate and return the content
        if (!isCacheable() || null == getExpirationMillis())
        {
            return buildContent(rundata);
        }

        //is the cached content still valid, if not, generate and return the content
        if (getExpirationMillis().longValue() <= System.currentTimeMillis())
        {
            return buildContent(rundata);
        }

        //else, the cached content is fine to be returned
        return getContent(rundata, null, true);

    }

    protected ConcreteElement buildContent(RunData rundata)
    {
        // create a new context
        // populate it with data
        GenericMVCContext context = new GenericMVCContext(TurbinePull.getGlobalContext());
        context.put(RUNDATA, rundata);
        context.put(PORTLET, this);
        context.put(PORTLET_CONFIG, this.getPortletConfig());
        context.put(SKIN, this.getPortletConfig().getPortletSkin());
        context.put(TEMPLATE, getCurrentTemplate(rundata));
        context.put(VIEW_TYPE, viewType);
        populateRequest(rundata);

        // put references to the pull tools in the context
        TurbinePull.populateContext(context, rundata);
        // Initialize jlink and jslink tools with ourselves
        // to enable links between portlets
        Object jlink = context.get("jlink");

        if (jlink instanceof JetspeedTemplateLink)
        {
            ((JetspeedTemplateLink) jlink).setPortlet(this);
        }

        Object jslink = context.get("jslink");

        if (jslink instanceof JetspeedLink)
        {
            ((JetspeedLink) jslink).setPortlet(this);
        }

        // Handle Action
        if (actionName != null)
        {

            try
            {

                // store the context so that the action can retrieve it
                //Log.debug("VelocityPortlet found action "+actionName+" context "+context);
                // note: the name it is stored under is legacy, leaving it this way
                // because some of the routines fall back to old default behavior
                // of the velocity portlet and might depend on the name
                rundata.getTemplateInfo().setTemplateContext("VelocityPortletContext", context);

                if (logger.isDebugEnabled())
                {
                    logger.debug(
                        "GenericMVCPortlet: Executing action ["
                            + actionName
                            + "] for portlet ["
                            + this.getName()
                            + "]");
                }

                ActionLoader.getInstance().exec(rundata, actionName);
            }
            catch (Exception e)
            {
                logger.error("GenericMVCPortlet - error executing action",  e);
            }
        }

        // Process View
        // call processView method
        logger.info("GenericMVCPortlet - calling processView on processor");

        ConcreteElement result = (ConcreteElement) processor.processView(context);
        logger.info("GenericMVCPortlet - setting this portlet's content");
        clearContent();
        setContent(result); // only needed when caching is true I believe

        // return result
        return result;
    }
    /**
     * @see setViewType()
     * @return String
     */
    protected String getViewType()
    {
        return viewType;
    }

    /**
    * STW: Added for backward compatibility when using this
     * class to subclass the existing Jsp and Velocity portlets
     * so they can set their view prior to super.init();
     * @param viewType The viewType to set
     */
    protected void setViewType(String viewType)
    {
        this.viewType = viewType;
    }

    /**
     * This is called before any action execution happens.
     * This provides backward compatibility to JspPortletActions
     * who retreive  information, like Portlet, from the request
     * BEFORE the ViewProcessor.processView() is called
     * which normally populates the request with Context objects.
     * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
     */
    protected void populateRequest(RunData rundata)
    {
        HttpServletRequest request = rundata.getRequest();
        request.setAttribute("data", rundata);
        request.setAttribute("portlet", this);
        request.setAttribute("conf", this.getPortletConfig());
        request.setAttribute("skin", this.getPortletConfig().getPortletSkin());
        request.setAttribute("template", getCurrentTemplate(rundata));
        request.setAttribute("viewType", viewType);
    }
    
    /**
     * 
     */
    protected String getCurrentTemplate( RunData data)
    {
        String useTemplate = (String) PortletSessionState.getAttribute(this, data, TEMPLATE);
        if(useTemplate == null)
        {
            useTemplate = this.template;
        }

        return useTemplate;
    }
    
    protected boolean useDelayedRendering(RunData rundata)
    {
        String renderingDelayedString = getPortletConfig().getInitParameter(RENDERING_DELAYED);
        boolean renderingDelayed = false;
        if (renderingDelayedString != null)
        {
            renderingDelayed = (Boolean.valueOf(renderingDelayedString) == Boolean.TRUE);
        }

        HttpServletRequest request = rundata.getRequest();
        String action = rundata.getAction();

        return renderingDelayed && (action == null || action.length() == 0 || "controls.Restore".equals(action));
    }

    protected ConcreteElement renderJspTemplate(RunData rundata, String templateName)
    {
        JspTemplate t = new JspTemplate(rundata, "/portlets/html/" + templateName);
        PrintWriter out = null;
        try
        {
            out = rundata.getOut();
            out.println(t.getContent());
        }
        catch (IOException ioe)
        {
            logger.error(ioe);
        }

        return null;
    }
    
    private void simulateDelay()
    {        
        String simulateDelayString = getPortletConfig().getInitParameter(SIMULATE_DELAY);
        int simulateDelay = 0;  // seconds
        if (simulateDelayString != null)
        {
            simulateDelay = Integer.parseInt(simulateDelayString);
        }

        if (simulateDelay > 0)
        {
            long delayInMilliseconds = simulateDelay * 1000;
            try 
            { 
                Thread.sleep(delayInMilliseconds); 
            } 
            catch (InterruptedException e) 
            {
            }
        }
                
    }
}
