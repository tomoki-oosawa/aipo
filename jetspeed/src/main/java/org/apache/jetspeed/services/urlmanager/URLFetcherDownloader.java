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

package org.apache.jetspeed.services.urlmanager;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
Used for fetching URLs and adding them to the disk cache when Jetspeed is 
operating in CACHE_REQUIRED mode.

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: URLFetcherDownloader.java,v 1.5 2004/02/23 03:30:47 jford Exp $
*/
public class URLFetcherDownloader implements Runnable 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(URLFetcherDownloader.class.getName());
    
    private String url = null;
    
    public URLFetcherDownloader( String url ) {
        super();
        this.url = url;
        
    }
    
    public void run() {

        URLFetcher.addRealtimeURL( url );
        try {
            logger.info( "URLFetcherDownloader -> now downloading URL: " + this.url );
            URLFetcher.fetch( url, true ); //SGP I think it should be forced
            // URLFetcher will put forced entries in the cache
        } catch ( Throwable t ) {
            logger.error( "URLFetcherDownloader couldn't pull down url." + this.url, t );
        }
        
        URLFetcher.removeRealtimeURL( url );
    }
    
    
}
