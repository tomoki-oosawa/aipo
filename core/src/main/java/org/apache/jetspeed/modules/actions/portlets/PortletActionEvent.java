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
 * PortletActionEvent.java
 *
 * Created on January 29, 2003, 4:20 PM
 */
package org.apache.jetspeed.modules.actions.portlets;

import java.lang.reflect.Method;

import java.util.Enumeration;
import java.util.HashMap;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.portlets.GenericMVCPortlet;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;


/**
 * Provides form based action handling via the eventSubmit_do[action] pattern.
 * Works just like the mechanism described for the velocity portlet.  Extends
 * this convienent functionality to all GenericMVCPortlets
 * 
 * @author  tkuebler
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: PortletActionEvent.java,v 1.3.2.1 2003/02/24 18:45:42 tkuebler Exp $
 * @stereotype moment-interval
 */
public abstract class PortletActionEvent
    extends ActionEvent
  {
  	
  	/**
  	 * Cache ActionEvent methods to avoid repeated replection
  	 * method lookups.
  	 */
  	private static final HashMap eventMethods = new HashMap();

    /**
     * You need to implement this in your classes that extend this
     * class.
     *
     * @param data A Turbine RunData object.
     * @exception Exception, a generic exception.
     */
    public abstract void doPerform(RunData data)
                            throws Exception;

    /**
     * This overrides the default Action.perform() to execute the
     * doEvent() method.  If that fails, then it will execute the
     * doPerform() method instead.
     *
     * @param data A Turbine RunData object.
     * @exception Exception, a generic exception.
     */
    protected void perform(RunData data)
                    throws Exception
      {

        try
          {
            executeEvents(data, TurbineVelocity.getContext(data));
          }
        catch (NoSuchMethodException e)
          {
            doPerform(data);
          }
      }

    /**
     * This method should be called to execute the event based system.
     *
     * @param data A Turbine RunData object.
     * @param context context information.
     * @exception Exception, a generic exception.
     */
    public void executeEvents(RunData data, Context context)
                       throws Exception
      {

        // Name of the button.
        String theButton = null;
        
        // Portlet whom this action is a target of
        Portlet portlet = (Portlet) context.get(GenericMVCPortlet.PORTLET);

        // ParameterParser.
        ParameterParser pp = data.getParameters();
        String button = pp.convert(BUTTON);

        // Loop through and find the button.
        for (Enumeration e = pp.keys(); e.hasMoreElements();)
          {

            String key = (String) e.nextElement();

            if (key.startsWith(button))
              {
                theButton = formatString(key);

                break;
              }
          }

        if (theButton == null )
          {
            throw new NoSuchMethodException("ActionEvent: The button was null");
          }

 

		if (!fireEvent(data, Context.class, context, theButton) && PortletSessionState.isMyRequest(data, portlet))
		{
			// Old JSP actions use Portlet instead of Context
			// as their event method's 2nd parameter
			if (!fireEvent(data, Portlet.class,
				portlet,
				theButton))
			{
				// Attempt to execut things the old way..
				super.executeEvents(data);
			}
		}            

      }
      
    /**
     * Convenience method for firing portlet events.
	 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
     */
	protected boolean fireEvent(RunData data, Class deltaClass, Object deltaValue, String theButton)		
	{
		try
		{
			// The arguments to the method to find.
			Class[] classes = new Class[2];
			classes[0] = RunData.class;
			classes[1] = deltaClass;
			
			// The arguments to pass to the method to execute.
			Object[] args = new Object[2];
			
			String methodKey = getClass().getName()+":"
			                   +theButton+":"+classes[0].getName()
			                   +":"+classes[1].getName();
			
			Method method = (Method)eventMethods.get(methodKey);
			if(method == null)
			{
				method = getClass().getMethod(theButton, classes);
				eventMethods.put(methodKey, method);
			}
			args[0] = data;
			args[1] = deltaValue;
			method.invoke(this, args);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
		
	}
      
     
  }
