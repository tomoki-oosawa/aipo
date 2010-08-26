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
 * GenericMVCAction.java
 *
 * Created on January 29, 2003, 2:56 PM
 */
package org.apache.jetspeed.modules.actions.portlets;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.portal.portlets.GenericMVCContext;
import org.apache.jetspeed.portal.portlets.GenericMVCPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.persistence.PortalPersistenceException;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;


/**
 * Provides standard portal and MVC related action functionality.  Developers
 * extend this class for thier own actions and provide implementations of the
 * build*Context methods apropos to thier portlet needs.
 *
 * @author  tkuebler
 * @version $Id: GenericMVCAction.java,v 1.8 2003/02/11 23:09:17 tkuebler Exp $
 * @stereotype moment-interval
 */
public class GenericMVCAction
    extends PortletAction
  {

    /**
     * Static initialization of the logger for this class
     */    
    protected static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(GenericMVCAction.class.getName());      
      
    /** Creates a new instance of GenericMVCAction */
    public GenericMVCAction()
      {

        // remove if empty at end of design phase
      }

    // override what you need to here
    protected void perform(RunData rundata)
                    throws Exception
      {

        Context context = getContext(rundata);

        if ((context != null) && (rundata.getParameters().getString("action") != null))
          {

            // if context is already defined and Actions defined, events
            // have already been processed, call doPerform
            logger.debug("Action detected with action + context");
            doPerform(rundata, context);
          }
        else
          {

            // if context is null, create a new one
            if (context == null)
              {
                logger.debug("Action: building action context");
                context = new GenericMVCContext();
                rundata.getTemplateInfo().setTemplateContext("VelocityActionContext", context);
              }

            try
              {

                // process implicit ActionEvent invocation
                logger.debug("Action: try executing events");

                GenericMVCPortlet portlet = (GenericMVCPortlet) context.get("portlet");

                if (portlet != null)
                  {

                    // verify this portlet is the one requested by checking the
                    // js_peid request var.  If there is no js_peid
                    // do not worry a about verifing.  helps with backward compat.
                    if (rundata.getParameters().getString("js_peid") == null || PortletSessionState.isMyRequest(rundata, portlet))
                      {
                        executeEvents(rundata, context);
                      }
                    else
                      {
                        logger.debug("Action: calling doPerform");
                        doPerform(rundata, context);
                      }
                  }
                else
                  {
                    executeEvents(rundata, context);
                  }
              }
            catch (NoSuchMethodException e)
              {

                // no event selected, process normal context generation
                logger.debug("Action: calling doPerform");
                
              }
              
			doPerform(rundata, context);
          }
      }

    public void doPerform(RunData rundata, Context context)
                   throws Exception
      {

        GenericMVCPortlet portlet = null;
        JetspeedRunData jdata = (JetspeedRunData) rundata;
        logger.debug("GenericMVCAction: retrieved context: " + context);

        if (context != null)
          {
            portlet = (GenericMVCPortlet) context.get("portlet");
          }

        logger.debug("GenericMVCAction: retrieved portlet: " + portlet);

        if (portlet != null)
          {

            //System.out.println("class = " + this.getClass().getName());
            //rundata.getUser().setTemp(this.getClass().getName(), portlet.getID());
            // we're bein configured
            if ((jdata.getMode() == JetspeedRunData.CUSTOMIZE) && (portlet.getName().equals(jdata.getCustomized().getName())))
              {
                logger.debug("GenericMVCAction: building customize");
                buildConfigureContext(portlet, context, rundata);

                return;
              }

            // we're maximized
            if (jdata.getMode() == JetspeedRunData.MAXIMIZE)
              {
                logger.debug("GenericMVCAction: building maximize");
                buildMaximizedContext(portlet, context, rundata);

                return;
              }

            logger.debug("GenericMVCAction: building normal");
            buildNormalContext(portlet, context, rundata);
          }
      }

    /**
     * Subclasses should override this method if they wish to
     * build specific content when maximized. Default behavior is
     * to do the same as normal content.
     */
    protected void buildMaximizedContext(Portlet portlet, Context context, RunData rundata)
                                  throws Exception
      {
        buildNormalContext(portlet, context, rundata);
      }

    /**
     * Subclasses should override this method if they wish to
     * provide their own customization behavior.
     * Default is to use Portal base customizer action
     */
    protected void buildConfigureContext(Portlet portlet, Context context, RunData rundata)
                                  throws Exception
      {

        // code goes here. :)
      }

    /**
     * Subclasses must override this method to provide default behavior
     * for the portlet action
     */
    protected void buildNormalContext(Portlet portlet, Context context, RunData rundata)
                               throws Exception
      {
      }

    /**
     * Convenience method for retreiving this action's PortletInstance
     * @param Context context Current context object.
     * @return Portlet the Portlet for this action
     * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
     */
    public PortletInstance getPortletInstance(Context context)
    {
        return getPortlet(context).getInstance((RunData) context.get("data"));
    }
    
    /**
    * Convenience method for retreiving this action's PortletInstance
    * attribute.
    * @param String Attribute Name
    * @param Context context Current context object.
    * @return String portlet attribute
    * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
    */   
    public String getAttribute(String attrName, Context context)
    {
        return getPortletInstance(context).getAttribute(attrName);
    }
    
    /**
    * Convenience method for retreiving this action's PortletInstance
    * attribute.
    * @param String Attribute Name
    * @param String Default to return if no attribute is found
    * @param Context context Current context object.
    * @return String portlet attribute
    * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
    */
    public String getAttribute(String attrName, String defaultValue, Context context)
    {
        return getPortletInstance(context).getAttribute(attrName, defaultValue);
    }
    /**
    * Convenience method for setting this action's PortletInstance
    * attribute.
    * @param String Attribute Name
    * @param String Attribute Value
    * @param Context context Current context object.
    * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
    */
    public void setAttribute(String attrName, String value, Context context)
        throws PortalPersistenceException
    {
        PortletInstance instance = getPortletInstance(context);
        instance.setAttribute(attrName, value);
        PersistenceManager.store(instance);
    }
    
    /**
     * Throws an exception if user attempts to perform unathorized action.
     * 
     * @param data
     * @throws SecurityException
     */
    public void checkAdministrativeAction(RunData data) throws SecurityException
    {
		if (!JetspeedSecurity.hasAdminRole(data.getUser()))
		{
			if (logger.isWarnEnabled())
			{
				logger.warn(
					"User ["
						+ data.getUser().getUserName()
						+ "] attempted to perform administrative action");
			}
			throw new SecurityException(
				"User ["
					+ data.getUser().getUserName()
					+ "] must be an administrator to perform this action");
		}    	
    }
  }
