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

package org.apache.jetspeed.services.threadpool;

import org.apache.turbine.services.TurbineServices;

/**
 * This is a simple static accessor class to the access the common     
 * methods of JetspeedThreadPoolService 
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: ThreadPool.java,v 1.4 2004/02/23 03:51:31 jford Exp $
 */
public class ThreadPool
{
    
    /**
     * @see org.apache.jetspeed.services.threadpool.JetspeedThreadPoolService#process( Runnable runnable )
     */
    public static void process( Runnable runnable )  {
        
        JetspeedThreadPoolService service = 
            (JetspeedThreadPoolService) TurbineServices
            .getInstance()
            .getService( ThreadPoolService.SERVICE_NAME );
            
        service.process( runnable );
        
    }
    
    /**
     * @see org.apache.jetspeed.services.threadpool.JetspeedThreadPoolService#process( Runnable runnable, int priority )
     */
    public static void process( Runnable runnable, int priority ) {
        
        JetspeedThreadPoolService service = 
            (JetspeedThreadPoolService) TurbineServices
            .getInstance()
            .getService( ThreadPoolService.SERVICE_NAME );
            
        service.process( runnable, priority );
        
    }

}
