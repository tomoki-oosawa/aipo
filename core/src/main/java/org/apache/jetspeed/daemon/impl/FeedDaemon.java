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
import org.apache.jetspeed.daemon.Feed;
import org.apache.jetspeed.util.SimpleTransform;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.registry.FileRegistry;
import org.apache.jetspeed.services.registry.RegistryService;

//turbine stuff
import org.apache.turbine.services.TurbineServices;

//java stuff
import java.io.Reader;
import java.util.Vector;

/**
<p>
A daemon that parses out Jetspeed content sources.  It also handles multiple 
updating Feeds within PortletFactory.  When it encounters any RSS feeds that are
remote it will pull them locally into the JetspeedDiskCache class via the 
bulkdownloader class.
</p>

<p>
The major goals of this Daemon are:

<ul>
    <li>Parse out OCS feeds</li>
    <li>Put the new Entry into the PortletRegistry</li>
    <li>Get the URL from the Internet if it hasn't been placed in the cache.</li>    
    <li>Instantiate the Portlet if it already isn't in the cache.</li>
</ul>

</p>

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@author <A HREF="mailto:sgala@apache.org">Santiago Gala</A>
@version $Id: FeedDaemon.java,v 1.42 2004/02/23 02:48:05 jford Exp $
*/
public class FeedDaemon implements Daemon 
{
    public static final String TEMP_FILE_KEY = "FeedDaemon-debug";
    
    public static String TEMP_DIRECTORY = 
        JetspeedResources.getString( JetspeedResources.TEMP_DIRECTORY_KEY );
    
    private static boolean processed = false;

    private static FeedDaemon instance = null;



    private int status = Daemon.STATUS_NOT_PROCESSED;
    private int result = Daemon.RESULT_UNKNOWN;
    private DaemonConfig config = null;
    private DaemonEntry entry = null;
    private boolean initialized = false;

    /**
    The total number of entries found by the daemon
    */
    private static int count = 0;
    
    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(FeedDaemon.class.getName());
    
    /**
    Get the feed count.
    */
    public static int getCount() 
    {
        return FeedDaemon.count;
    }

    
    /**
    Get the feeds that are available to Jetspeed
    */
    public static Feed[] getFeeds() 
    {
        Vector v = JetspeedResources.getVector( "contentfeeds.feed.name" );
        Vector found = new Vector();
        
        for( int i = 0; i < v.size(); ++i) {
            String name = (String)v.elementAt(i);
            
            String description = JetspeedResources.getString( "contentfeeds.feed." + name + ".description" );

            String url = JetspeedResources.getString( "contentfeeds.feed." + name + ".url" );

            found.addElement( new Feed( name, 
                                        description,
                                        url ) );
        }

        //now that you have the properties file for the feeds transform them
        //into PML
        
        Feed[] feeds = new Feed[found.size()];
        found.copyInto(feeds);
        return feeds;

    }
    
    
    /**
    */
    public void run() 
    {
        try 
        {
            this.setResult( Daemon.RESULT_PROCESSING );

            logger.info( "Jetspeed:  FeedDaemon  -- BEGIN -- " );
        
            FeedDaemon.count = 0;
            Feed[] feeds = getFeeds();

            for (int i = 0; i < feeds.length; ++i ) {

                String url = feeds[i].getURL();

                String name = "feed_"+feeds[i].getName();
                
                Reader transformed;

                try {

                    logger.info( "BEGIN FEED -> " + url );

                    //get the list of PortletMarkup entries from OCS
                    transformed = getEntries( url );
                    
                    //the string transformed should now contain PML... tranform it into PortletMarkup
                    logger.info("Determining portlets...");
    
                    FileRegistry registry = (FileRegistry)TurbineServices.getInstance()
                                                .getService( RegistryService.SERVICE_NAME );

                    registry.createFragment( name, transformed , true );
    
                    logger.info( "END FEED -> " + url + " -> SUCCESS");    

                    this.setResult( Daemon.RESULT_SUCCESS );    

                } catch ( Exception e ) {
                    error( e, "FeedDaemon:  Couldn't process URL:  " + url );

                } catch ( Throwable t ) {
                    error( t, "FeedDaemon:  Couldn't process URL:  " + url );
                } 
                
            }

        } finally {
            logger.info( "Jetspeed:  FeedDaemon  --  END  -- " );
        }

    }

    /**
    Logs a message to the logging service but also sets the result for this daemon.
    */
    private void error( Throwable t, String message ) 
    {
        this.setResult( Daemon.RESULT_FAILED );
        logger.error( message, t  );
    }

    /**
    */
    private void error( String message ) 
    {
        this.error( null, message );
    }
    
    /**
    Get the PML for the given URL
    */    
    public static final Reader getEntries( String url ) throws Exception 
    {        
        //this should be the URL to the original document.  Transform
        //it into PML
 
        String stylesheet = JetspeedResources.getString( JetspeedResources.CONTENTFEEDS_STYLESHEET_URL_KEY );
 
        logger.info( "FeedDaemon:  transforming url: " + 
                  url + 
                  " with stylesheet: " + 
                  stylesheet );

         return SimpleTransform.SAXTransform( url, stylesheet, null );
    }


    
    /**
    String the DOCTYPE from the transformed document.  Castor can't handle this.
    */
    private static String strip(String target) 
    {        
        if ( target.indexOf("<!DOCTYPE") != -1 ) {
            
            int begin = target.indexOf( "\">" ) + 2;
            
            target = target.substring( begin, target.length() );
            
        }
        
        return target;
    }

    /* *** Daemon interface *** */
    
    /**
    Init this Daemon from the DaemonFactory
    */
    public void init( DaemonConfig config, DaemonEntry entry ) 
    {
        this.config = config;
        this.entry = entry;
    }
    
    /**
    */
    public DaemonConfig getDaemonConfig() 
    {
        return this.config;
    }

    /**
    */
    public DaemonEntry getDaemonEntry() 
    {
        return this.entry;
    }
    
    /**
    Return the status for this Daemon

    @see Daemon#STATUS_NOT_PROCESSED
    @see Daemon#STATUS_PROCESSED
    @see Daemon#STATUS_PROCESSING
    */
    public int getStatus() 
    {
        return this.status;
    }
    
    /**
    Set the status for this Daemon

    @see #STATUS_NOT_PROCESSED
    @see #STATUS_PROCESSED
    @see #STATUS_PROCESSING
    */
    public void setStatus(int status) 
    {
        this.status = status;
    }

    /**
    @see Daemon#getResult()
    */
    public int getResult() 
    {
        return this.result;
    }

    /**
    @see Daemon#setResult(int result)
    */
    public void setResult( int result ) 
    {
        this.result = result;
    }
    
    /**
    @see Daemon#getMessage()
    */
    public String getMessage() 
    {
        return "Total number of content feeds found: " + getCount();
    }
    
}
