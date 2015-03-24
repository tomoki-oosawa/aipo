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
 * ViewProcessorFactory.java
 *
 * Created on January 27, 2003, 5:16 PM
 */
package org.apache.jetspeed.portal.portlets.viewprocessor;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * Creates the appropriate ViewProcessor to process the template
 *
 * @author  tkuebler
 * @version $Id: ViewProcessorFactory.java,v 1.1.2.2 2003/02/24 21:52:27 tkuebler Exp $
 * @stereotype factory
 * 
 */
public class ViewProcessorFactory
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ViewProcessorFactory.class.getName());
    
    /** Creates a new instance of ViewProcessorFactory */
    public ViewProcessorFactory()
      {

        // load the type -> processor map
        // this should probably be a turbine service...
      }

    public static ViewProcessor getViewProcessor(String viewType)
      {

        ViewProcessor viewProcessor;

        // figure out the type of portlet based on the map
        // will use properties file or xreg based map later
        // create and return appropriate processor
        // hardcoded for now, will use some config file map in future
        if (viewType.equals("Velocity"))
          {
            logger.info("ViewProcessorFactory - creating Velocity processor");
            viewProcessor = new VelocityViewProcessor();
          }
        else if (viewType.equals("JSP"))
          {
            logger.info("ViewProcessorFactory - creating JSP processor");
            viewProcessor = new JSPViewProcessor();
          }
        else if (viewType.equals("XSL"))
          {
            logger.info("ViewProcessorFactory - creating XSL processor");
            viewProcessor = new XSLViewProcessor();
          }
        else if (viewType.equals("RSS"))
          {
            logger.info("ViewProcessorFactory - creating RSS processor");
            viewProcessor = new RSSViewProcessor();
          }
        else
          {
            logger.error("ViewProcessorFactory - problem figuring out what view processor type you want - " + 
                      viewType);
            logger.error("ViewProcessorFactory - returing a JSP processor");
            viewProcessor = new JSPViewProcessor();
          }

        return viewProcessor;
      }
}
