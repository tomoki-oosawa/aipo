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

package org.apache.jetspeed.daemon.impl;

//jetspeed stuff
import org.apache.jetspeed.daemon.Daemon;
import org.apache.jetspeed.daemon.DaemonConfig;
import org.apache.jetspeed.daemon.DaemonEntry;
import org.apache.jetspeed.daemon.impl.util.diskcachedaemon.URLRefresher;
import org.apache.jetspeed.cache.disk.DiskCacheEntry;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.threadpool.ThreadPool;

//turbine stuff
import org.apache.turbine.util.RunData;

/**
A daemon that takes all instances of the DiskCacheDaemon and makes sure
that any content entries get updated on a regular basis.


@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@version $Id: DiskCacheDaemon.java,v 1.23 2004/02/23 02:48:05 jford Exp $
*/
public class DiskCacheDaemon implements Daemon {


    private int status = Daemon.STATUS_NOT_PROCESSED;
    private int result = Daemon.RESULT_UNKNOWN;
    private DaemonConfig config = null;
    private DaemonEntry entry = null;
    private RunData rundata = null;
    
    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(DiskCacheDaemon.class.getName());
    
    /**
    Go over all the documents on the system and if refresh them if necessary.
    */
    public void run() {
        
        logger.info("parsing out document store");
        this.setResult( Daemon.RESULT_PROCESSING );
        
        DiskCacheEntry urls[] = JetspeedDiskCache.getInstance().getEntries();
        
        for (int i = 0; i < urls.length; ++i) {

            String url = urls[i].getSourceURL();
            
            //SGP Note: Currently local URL have cache entries,
            //but we must not fetch them
            if(!urls[i].isLocal()) {
                ThreadPool.process( new URLRefresher( url ) );
            }

        }
        this.setResult( Daemon.RESULT_SUCCESS );
    }

    /**
    Init this Daemon from the DaemonFactory
    @see Daemon#init
    */
    public void init(DaemonConfig config, DaemonEntry entry) {
        this.config = config;
        this.entry = entry;
    }
    
    /**
    */
    public DaemonConfig getDaemonConfig() {
        return this.config;
    }

    /**
    */
    public DaemonEntry getDaemonEntry() {
        return this.entry;
    }
    
    /**
    Return the status for this Daemon

    @see Daemon#STATUS_NOT_PROCESSED
    @see Daemon#STATUS_PROCESSED
    @see Daemon#STATUS_PROCESSING
    */
    public int getStatus() {
        return this.status;
    }
    
    /**
    Set the status for this Daemon

    @see #STATUS_NOT_PROCESSED
    @see #STATUS_PROCESSED
    @see #STATUS_PROCESSING
    */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
    @see Daemon#getResult()
    */
    public int getResult() {
        return this.result;
    }

    /**
    @see Daemon#setResult(int result)
    */
    public void setResult( int result ) {
        this.result = result;
    }
    
    /**
    @see Daemon#getMessage()
    */
    public String getMessage() {
        return null;
    }

}
