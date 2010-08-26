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

package org.apache.jetspeed.daemon.impl.util;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
Util class for joining a ThreadGroup and joining all of its Threads and waiting
for completion.

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@version $Id: ThreadGroupJoin.java,v 1.10 2004/02/23 02:47:43 jford Exp $
*/
public class ThreadGroupJoin 
{    
    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ThreadGroupJoin.class.getName());
    
    /**
    Join a ThreadGroup and wait until it finishes
    */
    public static void join( ThreadGroup tg ) 
    {
        //join the threadgroup but don't have a source.
        join( tg, null, 0 );
    }

    /**
    Join a ThreadGroup bug specify a source and use a default interval.
    */
    public static void join( ThreadGroup tg,
                             String source ) 
    {
        join( tg, source, 100 );
    }
    
    /**
    Join a ThreadGroup except also log when interval number of Threads have 
    finished.
    */
    public static void join( ThreadGroup tg, 
                             String source, 
                             int interval ) 
    {

        Thread[] threads = new Thread[ tg.activeCount() ];
        
        tg.enumerate( threads );

        //keep waiting until all the DownloadThreads have stopped.

        long begin = System.currentTimeMillis();        
        
        for ( int i = 0; i < threads.length; ++i ) {

            if ( !threads[i].interrupted() ) 
            {
                try 
                {
                    if ( threads[i] != null ) 
                    {
                        threads[i].join();
                        
                        //if this is an even MOD of this interval and a source
                        //is defined then log it
                        if ( i != 0 && 
                            i % interval == 0 && 
                            source != null ) 
                        {
                            long seconds = ( System.currentTimeMillis() - begin ) / 1000;
                            begin = System.currentTimeMillis();
                            
                            if ( logger.isInfoEnabled() )
                            {
                                logger.info( source + ": has completed " + i + " threads in " + seconds + " second(s)" );
                            }
                            
                        }
                        
                    }

                } 
                catch (InterruptedException e) 
                {
                    logger.info( "Thread: " + threads[i].getName() + " -> DONE");
                    //noop.  this is standard.
                }
           }

        }
        
        
    }
    
}
    
  
