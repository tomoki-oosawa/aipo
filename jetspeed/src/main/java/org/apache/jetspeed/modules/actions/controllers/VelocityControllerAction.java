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
 
package org.apache.jetspeed.modules.actions.controllers;

// Jetspeed stuff
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.statemanager.SessionState;

// Turbine stuff
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.modules.actions.VelocityAction;
import org.apache.turbine.services.localization.Localization;

// Velocity Stuff
import org.apache.velocity.context.Context;

/**
 * An abstract action class to build VelocityPortlet actions.
 * 
 * <p>Don't call it from the URL, the Portlet and the Action are automatically
 * associated through the registry PortletName
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco</a>
 */
public abstract class VelocityControllerAction extends VelocityAction
{

    /**
     * This overrides the default Action.perform() to execute the
     * doEvent() method.  If that fails, then it will execute the
     * doPerform() method instead.
     *
     * @param data A Turbine RunData object.
     * @exception Exception, a generic exception.
     */
    protected void perform( RunData rundata )
        throws Exception
    {
        // first try to see if there are some events registered for this
        // action...
        Context context = getContext(rundata);
        if (context != null)
        {
            // if context is already defined, events have already been 
            // processed, call doPerform
            doPerform(rundata);
        }
        else
        {
            context = TurbineVelocity.getContext();
            rundata.getTemplateInfo().setTemplateContext("VelocityActionContext",context);
            try
            {
                executeEvents(rundata, context );
            }                    
            catch (NoSuchMethodException e)
            {
                // no event selected
                doPerform(rundata);
            }
        }
    }

    /**
     * This method is used when you want to short circuit an Action
     * and change the template that will be executed next.
     *
     * @param data Turbine information.
     * @param template The template that will be executed next.
     */
    public void setTemplate(RunData data,
                            String template)
    {
        getContext(data).put( "template" , template );
    }

    /**
     * Return the Context needed by Velocity.
     *
     * @param RunData data
     * @return Context, a context for web pages.
     */
    protected Context getContext(RunData data)
    {
        return (Context)data.getTemplateInfo()
                            .getTemplateContext( "VelocityControllerContext" );
    }

    public void doPerform( RunData rundata, Context context )
    {
        PortletController controller = (PortletController)context.get( "controller" );

        // if we're in customization mode for the given set, handle 
        // customization
        if (((JetspeedRunData)rundata).getMode()==JetspeedRunData.CUSTOMIZE)
        {
            buildCustomizeContext( controller, context, rundata);
            return;
        }

        buildNormalContext( controller, context, rundata);
    }

    /** 
     * Subclasses must override this method to provide default behavior 
     * for the portlet action
     */
    protected void buildCustomizeContext( PortletController controller, 
                                          Context context,
                                          RunData rundata )
    {
        String name = controller.getPortlets().getName();            
        String template = (String)context.get("template");

        int dotIdx = template.lastIndexOf('.');
        if (dotIdx > -1)
        {
            template = template.substring(0,dotIdx)
                       + "-customize.vm";
        }
        else
        {
            template = template+"-customize";
        }
        
        setTemplate(rundata, template);
        
        context.put( "action", controller.getConfig().getInitParameter("action"));

        // We want the save button to say different things based on whether we're about to save to persistent storage
        // (Save and Apply) or just go the next screen (Apply).
        JetspeedRunData jdata = (JetspeedRunData) rundata;

        // get the customization state for this page
        SessionState customizationState = jdata.getPageSessionState();

        String saveLabel = null;
        if (((String) customizationState.getAttribute("customize-paneName")).equalsIgnoreCase("*"))
        {
            saveLabel = Localization.getString(rundata, "CUSTOMIZER_SAVEAPPLY");    
        }
        else
        {
            saveLabel = Localization.getString(rundata, "CUSTOMIZER_APPLY");
        }
        context.put("saveLabel", saveLabel);

    }

    /** 
     * Subclasses must override this method to provide default behavior 
     * for the portlet action
     */
    protected abstract void buildNormalContext( PortletController controller, 
                                                Context context,
                                                RunData rundata );


    /** Switch out of customize mode
     */
    public void doCancel(RunData data, Context context)
    {
        // nothing to do
    }
}
