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
import org.apache.jetspeed.cache.disk.DiskCacheUtils;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.urlmanager.URLManager;
import org.apache.jetspeed.services.urlmanager.URLFetcher;

//java stuff
import java.io.IOException;
import java.util.Iterator;

/**
Manages pulling URLs from the BadURLManager, and if they are available, removing
them from the BadURLManager and placing them in the DiskCache.

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@version $Id: BadURLManagerDaemon.java,v 1.14 2004/02/23 02:48:05 jford Exp $
*/
public class BadURLManagerDaemon implements Daemon {
    
    private int status = Daemon.STATUS_NOT_PROCESSED;
    private int result = Daemon.RESULT_UNKNOWN;
    private DaemonConfig config = null;
    private DaemonEntry entry = null;

    /**
     * Static initilization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(BadURLManagerDaemon.class.getName());
    
    /**
    Go over all the documents on the system and if refresh them if necessary.
    */
    public void run() {
        
        logger.info("Checking for valid URLs within the URLManager");

        this.setResult( Daemon.RESULT_PROCESSING );
        
        Iterator i = URLManager.list().iterator();
        
        while ( i.hasNext() ) {

            String url = (String)i.next();
            
            // we only want to process bad URLs...
            if ( URLManager.isOK( url ) ) continue;
            
            try {
    
                URLManager.unregister(url);

                logger.info("Removing " + url + " from BadURL list" );
                this.save();

                //FIXME: It should check refresh of the portlets, like DiskCache...
                URLFetcher.refresh(url);

                
            } catch ( Throwable t ) {
                //don't do anything here because the URL for this has a good
                //chance of being invalid anyway.
                logger.error("Invalid URL?", t);
                if ( DiskCacheUtils.isCached( url ) ) {
                    try {
                        //To avoid interference with the Disk Cache refreshing
                        JetspeedDiskCache.getInstance().remove( url );
                    } catch (IOException ioe) {}
                }
                logger.info("Failed to load: " + url + " from BadURL list");
            }
            
        }
        
        this.setResult( Daemon.RESULT_SUCCESS );
    }

    /**
    */
    public void init( DaemonConfig config, 
                      DaemonEntry entry ) {
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

    /**
    Require that the BadURLManager save its configuration here.
    */
    public void save() {

        // RL: What should be persisted here ?
        // BadURLManager.getInstance().save();

    }

    public void restore() { /* noop */ }

    
    
}

