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
package org.apache.jetspeed.util;

import org.apache.turbine.services.TurbineServices;

/**
 * Convenience class for accessing Turbine / Fulcrum services.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 *  
 */
public abstract class ServiceUtil
{
    /**
     * @param String name of the service.  For Turbine and Fulcrum
     * services this is easily accessible from the Service's interface
     * via <code>SERVICE_NAME</code> field.
     * @return Object a service
     */
    public static Object getServiceByName(String name)
    {
        return TurbineServices.getInstance().getService(name);
    }
}
