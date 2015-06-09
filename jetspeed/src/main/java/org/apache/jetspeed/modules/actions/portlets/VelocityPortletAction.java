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


import java.lang.reflect.Method;

import org.apache.jetspeed.portal.Portlet;
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
 *  <p>
 *  <strong>NOTE:</strong>This supports the pre-MVC style of template based 
 *   portlet development and is supplied for backward compatibility.   It is
 *  suggested you  use a combination of 
 *  @see org.apache.jetspeed.portal.portlets.GenericMVCPortlet along with
 *  subclassing @see org.apache.jetspeed.portal.portlets.GenericMVCAction
 *  for future portlet development.
 *  </p>
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco</a>
 *
 * @version $Id: VelocityPortletAction.java,v 1.14 2004/02/23 02:56:58 jford Exp $
 */
public abstract class VelocityPortletAction extends GenericMVCAction
{



    /** 
     * Subclasses must override this method to provide default behavior 
     * for the portlet action    
     */
    protected abstract void buildNormalContext(VelocityPortlet portlet, 
                                                Context context,
                                                RunData rundata)
        throws Exception;

    /**
     * STW: Backwards compatibility so the overriden method is called specifically using a cast to VelocityPortlet
     * @see org.apache.jetspeed.portal.portlets.mvc.PortletAction#buildNormalContext(Portlet, Context, RunData)
     */
    protected void buildNormalContext(Portlet portlet, Context context, RunData data)
        throws Exception
    {
        buildNormalContext((VelocityPortlet) portlet, context, data);
    }

    /**
     * @see org.apache.jetspeed.portal.portlets.mvc.PortletAction#buildConfigureContext(Portlet, Context, RunData)
     */
    protected void buildConfigureContext(Portlet portlet, Context context, RunData data)
        throws Exception
    {
        // STW: Don't try this at home, kids.  It's about the worst reflection hack you
        // can commit.  However, it was the only to have VelocityPortletAction implement
        // GenericMVCAction an still work correctly.  The sypmtom we where experiencing
        // was that we where skipping over the overriden method in the inheriting class
        // due to the ambiguousness of the build*() method signatures (VelocityPortlet vs. Portlet)
        // This only happens when the class subclassing VelocityPortlet defines a build*()
        // method with the signature build*(VelocityPortlet, Context, RunData), which is 100%
        // of the time with any previously defined Actions subclassing VelocityPortletAction.
        // Defining build*(Portlet, Context, RunData) fixes the problem but can't expect 
        // everyone to go back and change all of there Action method signatures just to
        // fix this.  Eventually we should deprecate this class all together.
        try
        {
            Method method =
                this.getClass().getDeclaredMethod(
                    "buildConfigureContext",
                    new Class[] { VelocityPortlet.class, Context.class, RunData.class });
            method.setAccessible(true);
            method.invoke(this, new Object[] { portlet, context, data });
            method.setAccessible(false);

        }
        catch (NoSuchMethodException e)
        {
            // Subclass did not override this method
            super.buildConfigureContext(portlet, context, data);
        }
        
    }
    
    /**
     * prevents possible self-referencing loop when sub-classes invoke super.buildConfigureContext().
     * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
    protected void buildConfigureContext(VelocityPortlet portlet, Context context, RunData data)
        throws Exception
    {
    }
    
    
    protected void buildMaximizedContext(Portlet portlet, Context context, RunData data)
        throws Exception
    {
        // STW: Don't try this at home, kids.  It's about the worst reflection hack you
        // can commit.  However, it was the only to have VelocityPortletAction implement
        // GenericMVCAction an still work correctly.  The sypmtom we where experiencing
        // was that we where skipping over the overriden method in the inheriting class
        // due to the ambiguousness of the build*() method signatures (VelocityPortlet vs. Portlet)
        // This only happens when the class subclassing VelocityPortlet defines a build*()
        // method with the signature build*(VelocityPortlet, Context, RunData), which is 100%
        // of the time with any previously defined Actions subclassing VelocityPortletAction.
        // Defining build*(Portlet, Context, RunData) fixes the problem but can't expect 
        // everyone to go back and change all of there Action method signatures just to
        // fix this.  Eventually we should deprecate this class all together.
        try
        {
            Method method =
                this.getClass().getDeclaredMethod(
                    "buildMaximizedContext",
                    new Class[] { VelocityPortlet.class, Context.class, RunData.class });
            method.setAccessible(true);
            method.invoke(this, new Object[] { portlet, context, data });
            method.setAccessible(false);
        }
        catch (NoSuchMethodException e)
        {
            // Subclass did not override this method
            super.buildMaximizedContext(portlet, context, data);
        }
    }
    
    /**
     * prevents possible self-referencing loop when sub-classes invoke super.buildMaximizedContext().
     * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
    protected void buildMaximizedContext(VelocityPortlet portlet, Context context, RunData data)
        throws Exception
    {
    }
    
   
   

}
