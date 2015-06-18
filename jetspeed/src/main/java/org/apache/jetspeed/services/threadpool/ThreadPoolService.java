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

import org.apache.turbine.services.Service;

/**
 * <p>base interface for a simple ThreadPool</p>
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: ThreadPoolService.java,v 1.5 2004/02/23 03:51:31 jford Exp $
 */
public interface ThreadPoolService extends Service {
    
    public static final String SERVICE_NAME = "ThreadPool";

    /**
     * Put the Runnable object on the process queue of the threadpool.
     * This process will be executed at defaut priority as soon as a 
     * thread is available
     *
     * @param runnable the Runnable object to process
     */
    public void process( Runnable runnable );

    /**
     * Put the Runnable object on the process queue of the threadpool.
     * This process will be executed at the specified priority as soon as a 
     * thread is available
     *
     * @param runnable the Runnable object to process
     * @param priority the priority the process should run with
     */
    public void process( Runnable runnable, int priority );
       
}

