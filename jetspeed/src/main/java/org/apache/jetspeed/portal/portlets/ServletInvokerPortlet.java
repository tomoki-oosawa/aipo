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


package org.apache.jetspeed.portal.portlets;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.portal.portlets.AbstractPortlet;
import org.apache.jetspeed.util.servlet.EcsServletElement;

import org.apache.turbine.util.RunData;

/**
 * The ServletInvokerPortlet invokes a servlet or JSP and displays the result.
 * 
 * @author Thomas Schaeck (schaeck@de.ibm.com)
 */
public class ServletInvokerPortlet extends AbstractPortlet 
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ServletInvokerPortlet.class.getName());    

  /**
   * Returns an ECS concrete element that includes the servlet/JSP.
   *
   * The servlet/JSP will be invoked when the ECS tree is written 
   * to the servlet output stream and add its output to the stream.
   */
  public ConcreteElement getContent(RunData rundata) {
	// !!! Need to check this - is this the right rundata object ? !!!
	PortletConfig pc = this.getPortletConfig();

	String servletURL = null;
	try {
	  servletURL = (String) this.getPortletConfig().getInitParameter("url");
	  return new EcsServletElement(rundata, servletURL);
	} catch (Exception e) {
	  String message = "ServletInvokerPortlet: Error invoking " 
					   + servletURL + ": " + e.getMessage();
	  logger.error(message, e);
	  return new StringElement(message);
	}
  }    }