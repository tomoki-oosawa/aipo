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

package org.apache.jetspeed.util.template;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.template.TemplateLink;
import org.apache.turbine.services.pull.ApplicationTool;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PanedPortletController;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;

import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 * <p>A customized version of the TemplateLink which can handle portlet
 * references.</p>
 *
 * <p>It is inserted into the template context by Turbine, via request tools.</p>
 *
 * <p>Each portlet must call setPortlet(this) on it before entering the template
 * rendering code. This is done currently in VelocityPortlet.</p>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @version $Id: JetspeedTemplateLink.java,v 1.14 2004/02/23 03:20:46 jford Exp $
 *
 * @deprecated Use JetspeedBaseLink
 */
public class JetspeedTemplateLink
    extends TemplateLink implements ApplicationTool
{

    // parameter names for the Jetspeed framework elements
    public static final String PORTLET_KEY = "portlet";
    public static final String ACTION_KEY = "action";
    public static final String SCREEN_KEY = "screen";
    public static final String TEMPLATE_KEY = "template";
    public static final String PANEL_KEY = "select-panel";

    /**
     *<p>The name of the portlet for which a URL will be generated.</p>
     */
    private String portletName = null;

    /**
     *<p>Request to which we refer.</p>
     */
    private JetspeedRunData data = null;

    /**
     *<p>The portlet that will be used to build the reference.</p>
     */
    protected Portlet activePortlet = null;



    /**
     * Empty constructor.for introspection
     */
    public JetspeedTemplateLink()
    {
    }

    /**
     * Constructor.
     *
     * @param data A Jetspeed RunData object.
     */
    public JetspeedTemplateLink(RunData data)
    {
        super(data);
        this.data = (JetspeedRunData)data;

        String tmpl = this.data.getRequestedTemplate();
        if (tmpl!=null)
        {
            setPage(tmpl);
        }
    }

    /**
     * This will initialise a JetspeedTool object that was
     * constructed with the default constructor (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData object
     */
    public void init(Object data)
    {
        super.init(data);
        this.data = (JetspeedRunData)data;

        String tmpl = this.data.getRequestedTemplate();
        if (tmpl!=null)
        {
            setPage(tmpl);
        }
    }

    /**
     * <p> Set the portlet giving context to this Link object.</p>
     *
     */
    public void setPortlet(Portlet portlet)
    {
        this.activePortlet=portlet;
    }



    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        super.refresh();

        String tmpl = this.data.getRequestedTemplate();
        if (tmpl!=null)
        {
            setPage(tmpl);
        }
    }

    /**
     * Return a URI that refers to the named portlet.
     *
     * @param portlet the name of the portlet to link to
     * @return a DynamicURI referencing the named portlet for easy link construction in template
     */
    public DynamicURI forPortlet(String portlet)
    {
        this.portletName = portlet;
        removePathInfo(getPortletKey());
        removeQueryData(getPortletKey());
        return addPathInfo(getPortletKey(), portlet);
    }

    /**
     * Add a portlet reference in the link.
     *
     * @param portlet the name of the portlet to link to
     * @deprecated the name is confusing. Use @see(#forPortlet()) instead.
     * @return a DynamicURI referencing the named portlet for easy link construction in template
     */
    public DynamicURI setPortlet(String portlet)
    {
        return this.forPortlet( portlet );
    }

    /**
     * @return the portlet parameter value
     */
    public String getPortlet()
    {
        return this.portletName;
    }

    /**
     * @return the portlet parameter name
     */
    public String getPortletKey()
    {
        return PORTLET_KEY;
    }

    /**
     * @return the action parameter name
     */
    public String getActionKey()
    {
        return ACTION_KEY;
    }

    /**
     * @return the screen parameter name
     */
    public String getScreenKey()
    {
        return SCREEN_KEY;
    }

    /**
     * @return the template parameter name
     */
    public String getPageKey()
    {
        return TEMPLATE_KEY;
    }

    /**
     * Add a select-panel reference in the link
     *
     * @param portlet the name of the portlet to link to
     * @return a self reference for easy link construction in template
     */
    public DynamicURI setPanel(String panel)
    {
        removePathInfo(JetspeedResources.PATH_PANEID_KEY);
        removeQueryData(JetspeedResources.PATH_PANEID_KEY);
        PortletController controller = activePortlet.getPortletConfig()
                                                        .getPortletSet()
                                                        .getController();

       String id = null;

        if (controller instanceof PanedPortletController)
        {
            PortletSet set = controller.getPortlets();
            Portlet portlet = null;
            if (null != set)
            {
                portlet = set.getPortletByName(panel);
            }
            if (portlet != null)
                id = portlet.getID();
        }
        else
        {
           Portlets entry = data.getProfile().getDocument().getPortlets(panel);
           if (entry != null)
               id = entry.getId();
         }
        return addPathInfo(JetspeedResources.PATH_PANEID_KEY, id);
    }

    /**
     * <p>Use the activePortlet to get the current Panel name (key).</p>
     *
     * @return the panel parameter name
     */
    public String getPanelKey()
    {
        String panelName = PANEL_KEY;
        try
        {
            PortletController controller = activePortlet.getPortletConfig()
                                                        .getPortletSet()
                                                        .getController();

            if (controller instanceof PanedPortletController)
            {
                panelName=((PanedPortletController)controller).getParameterName();
            }

        }
        catch (Exception e)
        {
            panelName = PANEL_KEY;
        }

        return panelName;
    }

    /**
     *
     *
     */
    public String toString()
    {
        String tmpl = this.data.getRequestedTemplate();
        if (tmpl!=null)
        {
            setPage(tmpl);
        }

        String buf = super.toString();
        return buf;
    }

    public DynamicURI getPortletByName(String portletName)
    {
        String id = null;
        Entry entry = data.getProfile().getDocument().getEntry(portletName);
        //Portlets pEntry =  data.getProfile().getDocument().getPortletsById(entry.getId());
        if (entry != null)
        {
            id = entry.getId();
        }
        System.out.println("js_peid:"+id);
        return addPathInfo("js_peid", id);

    }

    public void clear()
    {
        removePathInfo();
        removeQueryData();
    }
}
