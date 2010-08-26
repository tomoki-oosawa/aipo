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
/*
 * PortletAction.java
 *
 *
 * $Id: PortletAction.java,v 1.8.2.1 2003/02/24 18:45:42 tkuebler Exp $
 *
 * Created on January 29, 2003, 2:52 PM
 */
package org.apache.jetspeed.modules.actions.portlets;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.portlets.GenericMVCPortlet;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;




/**
 *
 * Abstract holder for the portlet specific methods required above and
 * beyond the standard turbine action.
 * 
 * Extends the PortletActionEvent, which
 * encapsulates the event handling feature.
 * 
 * @author  tkuebler
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: PortletAction.java,v 1.8.2.1 2003/02/24 18:45:42 tkuebler Exp $
 * @stereotype moment-interval
 * 
 */


/*
 * Note:
 * assumes a templated portlet, maybe should break this up into two classes
 * the portletaction would only have the build*Context methods
 * the templatedportletaction would have the rest
 * skipping this complexity for now since non-templated portlet actions can just
 * extend ActionEvent and forget about the context probably
 */
public abstract class PortletAction
    extends PortletActionEvent
  {

    /** Creates a new instance of PortletAction */
    public PortletAction()
      {
      }

    public void doPerform(RunData data)
                   throws Exception
      {

        // assumes a context for the portlet...
        // should check for null and create one
        doPerform(data, getContext(data));
      }

    /**
     * You SHOULD override this method and implement it in your
     * action.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    public abstract void doPerform(RunData data, Context context)
                            throws Exception;

    /**
     * Return the Context
     *
     * @param RunData data
     * @return Context, a context for web pages.
     */
    protected Context getContext(RunData data)
      {

        return (Context) data.getTemplateInfo().getTemplateContext("VelocityPortletContext");
      }

    /**
     * This method is used when you want to short circuit an Action
     * and change the template that will be executed next.  The TTL
     * for this is a single request.
     *
     * @param data Turbine information.
     * @param template The template that will be executed next.
     */
    public void setTemplate(RunData data, String template)
      {
        setTemplate(data, template, false);
      }
      
    /**
     * This method is used when you want to short circuit an Action
     * and change the template that will be executed next. If
     * the <code>persistent</code> attribute is set to <i>true</i>
     * the template value is stored in the portlet's session state
     * and will be used until a new value has been set, either within 
     * portlet session or within the context.  Regardless of the
     * value of <code>persistent</code>, the context will ALWAYS
     * have the correct "template" attribute set within.
     *
     * @param data Turbine information.
     * @param template The template that will be executed next.
     * @param persistent whether or not to make the template set 
     * persistent for the extent of the portlet session
     * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
	public void setTemplate(RunData data, String template, boolean persistent)
	{
		Portlet portlet = getPortlet(getContext(data));

		if (template != null)
		{
			if (persistent)
			{
				PortletSessionState.setAttribute(
					portlet,
					data,
					GenericMVCPortlet.TEMPLATE,
					template);
			}
			else
			{
				// Make sure there is no ssession residue ;)
				resetTemplate(data);
			}

			// Always make the current template is available within the
			// context.
			getContext(data).put("template", template);
		}

	}
	
	/**
	 * Clears the PortletSessionState of the <code>template</code>
	 * attribute.
	 */
	protected void resetTemplate(RunData data)
	{
		Portlet portlet = getPortlet(getContext(data));
		PortletSessionState.clearAttribute(portlet, data, GenericMVCPortlet.PORTLET);
	}
      
  
      
    public Portlet getPortlet(Context context)
    {
    	return (Portlet) context.get(GenericMVCPortlet.PORTLET);
    }
    /**
     * Retrieves the template for this PortletAction's Portlet.
	 * The Portlet <code>init()</code> will have already initialized
	 * the template value within the current context in this order:<br/>
	 * 1. From the PortletSessionState's "template" attribute <br />
	 * 2. From the PortletConfig's "template" parameter.<br /><br />
	 * However, the action may have overriden this value using
	 * any of the <code>setTemplate()</code> methods.
     * @param Context context the context for this action's portlet.
     * @return String  Current view template for this action's portlet.
     * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
	public String getTemplate(Context context)
	{		

		return (String) context.get(GenericMVCPortlet.TEMPLATE);
	}

    protected abstract void buildConfigureContext(Portlet portlet, Context context, RunData data)
                                           throws Exception;

    protected abstract void buildMaximizedContext(Portlet portlet, Context context, RunData data)
                                           throws Exception;

    protected abstract void buildNormalContext(Portlet portlet, Context context, RunData data)
                                        throws Exception;
  }
