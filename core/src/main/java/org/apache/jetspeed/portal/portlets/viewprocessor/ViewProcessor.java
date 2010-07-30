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
 * ViewProcessor.java
 *
 * Created on January 27, 2003, 5:18 PM
 */
package org.apache.jetspeed.portal.portlets.viewprocessor;

import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.portlets.*;

/**
 * Interface that defines a ViewProcessor.  This is what the
 * GenericMVCPortlet uses via the ViewProcessorFactory to
 * do the template processing portion of the MVC design pattern.
 * 
 * @author  tkuebler
 * @version 1.0
 * @stereotype plug-in point
 */
public interface ViewProcessor
  {

    /**
     * Process the template passed in the context
     * (context.get("template")).  Invoked by the GenericMVCPortlet
     * after action handling to process the template type
     * in question.
     */
    void init(Portlet portlet)
              throws PortletException;

    Object processView(GenericMVCContext context);
  }
