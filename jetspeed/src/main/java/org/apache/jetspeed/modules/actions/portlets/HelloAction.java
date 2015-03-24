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
 
package org.apache.jetspeed.modules.actions.portlets;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;

// Turbine stuff
import org.apache.turbine.util.RunData;

// Velocity Stuff
import org.apache.velocity.context.Context;


/**
 * An abstract action class to build VelocityPortlet actions.
 * 
 * <p>Don't call it from the URL, the Portlet and the Action are automatically
 * associated through the registry PortletName
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 */
public class HelloAction extends VelocityPortletAction
{

    /** 
     * Subclasses should override this method if they wish to
     * build specific content when maximized. Default behavior is
     * to do the same as normal content.
     */
    protected void buildMaximizedContext( VelocityPortlet portlet, 
                                          Context context,
                                          RunData rundata )
    {
        buildNormalContext( portlet, context, rundata);
        
        String text = (String)context.get("text");

        if (text == null)
        {
            text = "Hello World ";
        }

        context.put("text", text+" (Maximized !)");
    }

    /** 
     * Subclasses should override this method if they wish to
     * provide their own customization behavior.
     * Default is to use Portal base customizer action
     */
    protected void buildConfigureContext( VelocityPortlet portlet, 
                                          Context context,
                                          RunData rundata )
    {

        buildNormalContext( portlet, context, rundata);
        
        setTemplate(rundata, "hello-customize");
        
    }

    /** 
     * Subclasses must override this method to provide default behavior 
     * for the portlet action
     */
    protected void buildNormalContext( VelocityPortlet portlet, 
                                       Context context,
                                       RunData rundata )
    {
        context.put("text",portlet.getPortletConfig().getInitParameter("text"));
    }

    public void doUpdate(RunData data, Context context)
    {
        String text = data.getParameters().getString("text");
        
        if (text!=null)
        {
            VelocityPortlet portlet = (VelocityPortlet)context.get("portlet");
            portlet.setAttribute("text",text,data);
            context.put("text",text);
            context.put("message", "Text successfully updated");
        }
        else
        {
            context.put("message", "You must specify a new text");
        }
    }
}
