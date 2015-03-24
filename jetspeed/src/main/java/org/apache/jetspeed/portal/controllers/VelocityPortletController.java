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

package org.apache.jetspeed.portal.controllers;

// Turbine stuff
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.services.pull.TurbinePull;
import org.apache.turbine.util.RunData;

// Jetspeed stuff
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

// Ecs stuff
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

// Velocity Stuff
import org.apache.velocity.context.Context;

/**
 * A Velocity based portlet controller implementation
 * 
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 *
 * @version $Id: VelocityPortletController.java,v 1.12 2004/02/23 03:25:06 jford Exp $
 */
public class VelocityPortletController extends AbstractPortletController
{
    
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(VelocityPortletController.class.getName());    
    
    public ConcreteElement getContent( RunData rundata )
    {
        // create a blank context and with all the global application
        // Pull Tools inside
        Context context = TurbineVelocity.getContext();
        
        context.put( "data", rundata );
        context.put( "controller", this );
        context.put( "portlets", this.getPortlets().toArray() );
        context.put( "config", this.getConfig() );
        context.put( "skin", this.getPortlets().getPortletConfig().getPortletSkin() );
        context.put( "template", getConfig().getInitParameter("template") );
        
        // Put the request and session based contexts
        TurbinePull.populateContext(context, rundata);
        
        // allow subclass to insert specific objects in the context
        buildContext(rundata, context);
        
        String actionName = getConfig().getInitParameter("action");
        
        if (actionName != null)
        {
            // store the context so that the action can retrieve it
            rundata.getTemplateInfo().setTemplateContext( "VelocityControllerContext", context );

            // if there is an action with the same name in modules/actions/portlets exec it
            try
            {
                ActionLoader.getInstance().exec( rundata, actionName );
            }
            catch( Exception e)
            {
               logger.error("Exception",  e);
            }
        }
 
        // either the action selected the template, or use the default template 
        // defined in the registry
        String template = (String)context.get( "template" );
        
        // generate the content
        String s = "";

        try
        {
            if (-1 == template.indexOf(".vm"))
            {
                template = template + ".vm";
            }
            
            String templatePath = TemplateLocator.locateControllerTemplate(rundata, template);
            TurbineVelocity.handleRequest(context, templatePath, rundata.getOut());
        }
        catch( Exception e)
        {
            logger.error( "Error generating content: ", e );
            s= e.toString();
        }
        
        TurbineVelocity.requestFinished(context);

        return new StringElement( s );
    }

    public void buildContext(RunData data, Context context)
    {
        // nothing special
    }
}

