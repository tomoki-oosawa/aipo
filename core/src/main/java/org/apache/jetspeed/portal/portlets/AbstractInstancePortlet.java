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

// package
package org.apache.jetspeed.portal.portlets;

// imports
import org.apache.jetspeed.portal.PortletConfig;

/**
* Extend AbstractPortlet to re-define the handle used for portlet caching:
* This handle uses the portlet's unique id and the portal page's id to form
* a portlet instance id for caching.
* @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
* @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
*/
public class AbstractInstancePortlet
    extends AbstractPortlet
{
    /**
    * Construct the handle used for caching.
    * @param config The config object, expected to be a PortletConfig.
    */
    public static Object getHandle(Object config)
    {
        //this implementation expects a PortletConfig object as its
        // configuration 
        PortletConfig pc = null;

        if (!(config instanceof PortletConfig))
        {
            return null;            
        }

        // form the key from the current request's portal page profile
        // and the portlet id in the config
        pc = (PortletConfig)config;

        StringBuffer handle = new StringBuffer(256);
        handle.append(pc.getPageId());
        handle.append('/');
        handle.append(pc.getPortletId());

        return handle.toString();   

    }   // getHandle

}   // AbstractInstancePortlet

