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

// Jetspeed stuff
import org.apache.jetspeed.portal.Portlet;

// Turbine stuff

import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * An abstract action class to build JspPortlet actions.
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
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
 *
 * @version $Id: JspPortletAction.java,v 1.7 2004/02/23 02:56:58 jford Exp $
 */
public abstract class JspPortletAction extends GenericMVCAction
{

    /**
    * @see org.apache.jetspeed.portal.portlets.mvc.PortletAction#buildConfigureContext(Portlet, Context, RunData)     
    */
    protected void buildConfigureContext(Portlet portlet, Context context, RunData rundata)
        throws Exception
    {

        buildConfigureContext(portlet, rundata);
        if (rundata.getRequest().getAttribute("_" + portlet.getID() + "_noConfigureContext")
            != null)
        {
            super.buildConfigureContext(portlet, context, rundata);
        }
    }

    /** 
      * Kept for backward compatibility.  New classes should use 
     * the method signatures build*(Portlet, Context, RunData)
     * If you override this method <b>DO NOT</b> call super.buildConfigureContext().
     * <br>
     * Subclasses should override this method if they wish to
     * provide their own customization behavior.
     * Default is to use Portal base customizer action
     */
    protected void buildConfigureContext(Portlet portlet, RunData rundata) throws Exception
    {

        // STW: backward compatibility bootstrap flag
        rundata.getRequest().setAttribute("_" + portlet.getID() + "_noConfigureContext", " ");
    }

    /**
     * @see org.apache.jetspeed.portal.portlets.mvc.PortletAction#buildMaximizedContext(Portlet, Context, RunData)
     */
    protected void buildMaximizedContext(Portlet portlet, Context context, RunData rundata)
        throws Exception
    {
        buildMaximizedContext(portlet, rundata);
        if (rundata.getRequest().getAttribute("_" + portlet.getID() + "_noMaximizedContext")
            != null)
        {
            super.buildMaximizedContext(portlet, context, rundata);
        }
    }

    /** 
     * Kept for backward compatibility.  New classes should use 
     * the method signatures build*(Portlet, Context, RunData)
     * If you override this method <b>DO NOT</b> call super.buildMaximizedContext().
     * <br>
     * Subclasses should override this method if they wish to
     * build specific content when maximized. Default behavior is
     * to do the same as normal content.<br>     
     */
    protected void buildMaximizedContext(Portlet portlet, RunData rundata) throws Exception
    {
        // STW: backward compatibility bootstrap flag
        rundata.getRequest().setAttribute("_" + portlet.getID() + "_noMaximizedContext", " ");
    }

    /**
     * @see org.apache.jetspeed.portal.portlets.mvc.PortletAction#buildNormalContext(Portlet, Context, RunData)
     */
    protected void buildNormalContext(Portlet portlet, Context context, RunData data)
        throws Exception
    {
        buildNormalContext(portlet, data);
    }

    /** 
     * Subclasses must override this method to provide default behavior 
     * for the portlet action
     */
    protected abstract void buildNormalContext(Portlet portlet, RunData rundata) throws Exception;
	
	/**
	 * You should use one of PortletAction.setTemplate() methods
	 * @deprecated
	 */
    public void setTemplate(RunData data, Portlet portlet, String template)
    
    {
    	if(template != null)
    	{
        	super.setTemplate(data, template, true);
    	}
    	else
    	{
    		super.resetTemplate(data);
    	}
    }

}
