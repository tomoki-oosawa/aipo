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
 * VelocityViewProcessor.java
 *
 *
 * Created on January 27, 2003, 8:54 PM
 */
package org.apache.jetspeed.portal.portlets.viewprocessor;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.portlets.*;
import org.apache.jetspeed.portal.portlets.GenericMVCContext;
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.util.JetspeedClearElement;

import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;


/**
 *
 * @author  tkuebler
 */
public class VelocityViewProcessor implements ViewProcessor
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(VelocityViewProcessor.class.getName());
    
    /** Creates a new instance of VelocityViewProcessor */
    public VelocityViewProcessor()
      {
      }

    public void init(Portlet portlet)
              throws PortletException
      {
      }

    /** Process the template passed in the context
     * (context.get("template")).  Invoked by the GenericMVCPortlet
     * after action handling to process the template type
     * in question.
     *
     */
    public Object processView(GenericMVCContext context)
      {

        // generate the content
        JetspeedClearElement element = null;
        String template = (String) context.get("template");
        logger.info("VelocityViewProcessor - processing " + template);

        try
          {

            if (-1 == template.indexOf(".vm"))
              {
                template = template + ".vm";
              }

            logger.info("VelocityViewProcessor - locating template - " + 
                     ((RunData) context.get("data")).toString() + template);

            String templatePath = TemplateLocator.locatePortletTemplate(
                                          (RunData) context.get("data"), 
                                          template);

            // need to add cache support
            Portlet portlet = (Portlet) context.get("portlet");
            RunData rundata = (RunData) context.get("data");
            long cachePeriod = -1;
            AbstractPortlet abstractPortlet = null;
            // STW: Safety net ;)
            if(portlet instanceof AbstractPortlet)
            {
            	abstractPortlet =(AbstractPortlet) portlet;
            	if(abstractPortlet.getExpirationMillis() != null)
            	{
            		cachePeriod = abstractPortlet.getExpirationMillis().longValue();
            	}
            }
           
			if (cachePeriod > 0 && abstractPortlet != null)
			{
				String s = TurbineVelocity.handleRequest(context, templatePath);
				abstractPortlet.setExpirationMillis(
					cachePeriod + System.currentTimeMillis());
				element = new JetspeedClearElement(s);

			}
			else
			{
				TurbineVelocity.handleRequest(
					context, templatePath, rundata.getOut());
			}
            
            
          }
        catch (Exception e)
          {
            element = new JetspeedClearElement(e.toString());
            logger.error("VelocityViewProcessor - had problems handling request - " + e);
            e.printStackTrace();
          }

        TurbineVelocity.requestFinished(context);

        if (element == null)
          {
            element = new JetspeedClearElement("");
          }

        return element;
      }
  }
