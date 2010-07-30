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
package org.apache.jetspeed.services;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.Service;

/**
 * ServiceHelper
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ServiceHelper.java,v 1.2 2004/02/23 04:00:57 jford Exp $
 */
public class ServiceHelper
{
    private static final JetspeedLogger log = JetspeedLogFactoryService.getLogger(ServiceHelper.class.getName());
    
    /**
     * Load an implementation class from the configuration.
     * 
     * @param configurationName
     * @return
     * @throws CPSInitializationException
     */
    static public Class loadModelClass(Service service, String configurationName)
    throws InitializationException
    {
        String className = service.getConfiguration().getString(configurationName, null);
        if (null == className)
        {
            throw new InitializationException(configurationName + " implementation configuration not found.");
        }
        try
        {
            return Class.forName(className);
            
        }
        catch (ClassNotFoundException e)
        {
            throw new InitializationException("Could not preload " + className + " implementation class.", e);
        }            
    }

    /**
     * Creates objects given the class. 
     * Throws exceptions if the class is not found in the default class path, 
     * or the class is not an instance of CmsObject.
     * 
     * @param classe the class of object
     * @return the newly created object
     * @throws ContentManagementException
     */    
    public static Object createObject(Class classe)
    {
        Object object = null;
        try
        {
            object = classe.newInstance();
        }
        catch (Exception e)
        {
            log.error("Factory failed to create object: " + classe.getName(), e);            
        }
        
        return object;        
    }
    
}
