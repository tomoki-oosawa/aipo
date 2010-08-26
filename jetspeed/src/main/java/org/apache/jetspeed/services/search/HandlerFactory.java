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

package org.apache.jetspeed.services.search;

// Turbine APIs
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.TurbineServices;

/**
 * Search object handler factory
 *
 * @author <a href="morciuch@apache.org">Mark Orciuch</a>
 * 
 * @version $Id: HandlerFactory.java,v 1.4 2004/02/23 03:48:47 jford Exp $
 */
public class HandlerFactory
{
    private static final Map handlerCache = Collections.synchronizedMap(new HashMap());
    
    /**
     * Returns parsed object handler for specific object
     * 
     * @param obj
     * @return 
     */
    public static ObjectHandler getHandler(Object obj) throws Exception
    {
        return getHandler(obj.getClass().getName());

    }
    
    /**
    * Returns parsed object handler for specific object
    * 
    * @param obj
    * @return 
    */
    public static ObjectHandler getHandler(String className) throws Exception
    {
        ObjectHandler handler = null;
        
        if(handlerCache.containsKey(className))
        {
            handler = (ObjectHandler)handlerCache.get(className);
        }
        else
        {
            ResourceService serviceConf = ((TurbineServices) TurbineServices.getInstance())
                                              .getResources(SearchService.SERVICE_NAME);
            String handlerClass = serviceConf.getString("document." + className);
    
            if (handlerClass == null)
            {
                throw new Exception("No handler was found for document type: " + className);
            }
    
            handler = (ObjectHandler) Class.forName(handlerClass).newInstance();
            handlerCache.put(className, handler);
        }
        //System.out.println("HandlerFactory: returning handler " + handler + " for " + obj);

        return handler;
    }
}
