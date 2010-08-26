package org.apache.jetspeed.portal.portlets;

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

import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.portal.PortletInstance;

 /** 
  * Bogus Portlet. Use to test portlet instance persistence
  * 
  * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
  */
public class BogusPortlet extends AbstractInstancePortlet 
{
    
    public org.apache.ecs.ConcreteElement getContent(org.apache.turbine.util.RunData data) 
    {
        String s1 = "Config ID: " + getPortletConfig().getPortletId();
        String s2 = ", Portlet ID: " + getID();
        String s3 = ", Page ID: " + getPortletConfig().getPageId();
        PortletInstance instance = PersistenceManager.getInstance(this,data);
        String s4 = instance.getAttribute("country");
        return new org.apache.jetspeed.util.JetspeedClearElement( s1 + s2 + s3 + ", " + s4);        
    }

}

