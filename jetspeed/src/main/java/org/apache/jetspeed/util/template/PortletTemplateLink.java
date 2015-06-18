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

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PanedPortletController;
import org.apache.jetspeed.services.rundata.JetspeedRunData;


/**
 * A customized version of the JetspeedTemplateLink that reacts to the 
 * Portlet context and allows you to manipulate the lowest level pane 
 * if it exists.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @deprecated Since 2002-03-05. Use JetspeedTemplateLink with the setPortlet(Portlet) method to give
 * it the right context.
 * @version $Id: PortletTemplateLink.java,v 1.4 2004/02/23 03:20:45 jford Exp $
 */
public class PortletTemplateLink extends JetspeedTemplateLink
{
        
    private Portlet portlet = null;
    private JetspeedRunData data = null;
    
    /**
     * Empty constructor.for introspection
     */
    public PortletTemplateLink()
    {
    }

    /**
     * Constructor.
     *
     * @param data A Turbine RunData object.
     */
    public PortletTemplateLink(RunData data, Portlet portlet)
    {
        super(data);
        this.portlet = portlet;
    }

    /** 
     * Add a select-panel reference in the link
     *
     * @param portlet the name of the portlet to link to
     * @return a self reference for easy link construction in template
     */
    public DynamicURI setPanel(String panel)
    {
        removePathInfo(getPanelKey());
        removeQueryData(getPanelKey());
        return addPathInfo(getPanelKey(), panel);
    }
    
     /** 
     * @return the panel parameter name
     */
    public String getPanelKey()
    {
        String panelName = PANEL_KEY;
        try
        {
            PortletController controller = portlet.getPortletConfig()
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
}
