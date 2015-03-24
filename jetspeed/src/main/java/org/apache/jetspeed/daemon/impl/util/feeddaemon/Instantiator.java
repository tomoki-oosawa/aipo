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

package org.apache.jetspeed.daemon.impl.util.feeddaemon;

//jetspeed stuff
import org.apache.jetspeed.cache.disk.DiskCacheUtils;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.urlmanager.URLManager;
import org.apache.jetspeed.services.urlmanager.URLManagerService;
import org.apache.jetspeed.services.urlmanager.URLFetcher;
import org.apache.jetspeed.services.resources.JetspeedResources;

//Java stuff
import java.io.IOException;

/**
<p>
Given an PortletEntry use the PortletFactory to instantiate this Portlet and
then place it in the cache.  
</p>

<p>
If the URL isn't 

</p>

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@version $Id: Instantiator.java,v 1.26 2004/02/23 02:47:27 jford Exp $
*/
public class Instantiator implements Runnable {

    /**
    The maximum number of seconds to wait before warning that the URL took
    too long to download
    */
    public static final int MAX_WARN_SECONDS = 3;
    
    /**
    Specify the interval to log when Portlets are instantiated
    */
    public static final int LOG_INTERVAL = 100;
    
    private PortletEntry   entry = null;
    private int            id = 0;

    private boolean        forcePortet = false;
    
    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(Instantiator.class.getName());
    
    /**
    Create a Instantiator with info on what to instantiate
    */
    public Instantiator( PortletEntry entry ) { 

        this.entry = entry;
        
    }
    
    /**
    @see #Instantiator( PortletEntry )
    */
    public Instantiator( int id,
                         PortletEntry entry ) {

        this(entry);                     
        this.id = id;
        
    }

    /**
    Get the url from the net and put it on disk
    */
    public void getURL( String url ) throws IOException {

        //if the user wants to download the URL and it isn't in the cache then go for it
        if ( JetspeedResources.getBoolean( JetspeedResources.CONTENTFEEDS_FETCHALL_KEY ) && 
             DiskCacheUtils.isCached( url ) == false ) {
            
            long download_begin = System.currentTimeMillis();        
            try {

                //JetspeedDiskCache.getInstance().getEntry( url, true );
               //SGP
                JetspeedDiskCache.getInstance().getEntry(
                                                         url, 
                                                         URLFetcher.fetch(url, true));
            
                long total = ( System.currentTimeMillis() - download_begin ) / 1000;
            
                if ( total >= MAX_WARN_SECONDS ) {
                    logger.warn( this.getClass().getName() + "  The following URL took too long (" + 
                    total + 
                    " second(s)) to download: " + url );
                }
            
            } catch (IOException e) {
            
                //Not necessary to print a stack trace here because this will 
                //generate too much output
            
                logger.error( "The following URL couldn't be downloaded " + 
                           url + 
                           " and took " + 
                           ( System.currentTimeMillis() - download_begin ) / 1000 +
                           " seconds to download. " );
                throw new IOException( e.getMessage() );
            }
        
        }
        
    }
    
    /**
    Do work necessary to instantiate the current Entry but only do this if it is 
    NOT already in the cache.
    */
    public void run() {

        try {
            
            if(this.entry == null)
                {
                    logger.error("Instantiator: Null Entry");
                    return;
                }

            if(this.entry.getURL() == null)
                {
                    logger.error("Instantiator: Null URL");
                    return;
                }

            this.getURL( this.entry.getURL() );
            
        } catch ( IOException e ) {
            //the real IOException is logged in getURL
            return;
        } catch ( Throwable t) {
            //t.printStackTrace();
            logger.error( "Instantiator: Throwable", t);
        }

        org.apache.jetspeed.om.registry.Registry registry = 
            Registry.get(Registry.PORTLET);

        try {
            if(!registry.hasEntry(this.entry.getName()))
                {
                    registry.addEntry( this.entry );

                    if ( JetspeedResources.getBoolean( JetspeedResources.AUTOCREATE_PORTLETS_KEY ) )
                        {
                  
                            PortletFactory.getPortlet( this.entry.getName(), "0" );

                        }

                }
                                                     
        } catch ( Exception e ) {
            logger.error( "InstantiatorThread: Couldn't create Portlet: ", e );

            //SGP We add the URL to the BadList
            URLManager.register( this.entry.getURL(), URLManagerService.STATUS_BAD, e.toString() );

            //remove this entry because it threw a PortletException so users 
            //should be prevented from seeing this again.
            registry.removeEntry( this.entry.getName() );
        }
        
        //log how many portlets we have instantiated.
        if ( id != 0 &&
             id % LOG_INTERVAL == 0 ) {
            logger.info( "Instantiator:  instanted " + id + " portlet(s)" );
        }
        
    }
    
}

