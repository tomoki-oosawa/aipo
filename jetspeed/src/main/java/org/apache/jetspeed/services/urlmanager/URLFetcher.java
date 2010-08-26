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

package org.apache.jetspeed.services.urlmanager;

//standard Java stuff
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;

//turbine stuff
import org.apache.jetspeed.services.resources.JetspeedResources;

//jetspeed stuff
import org.apache.jetspeed.cache.disk.DiskCacheEntry;
import org.apache.jetspeed.cache.disk.DiskCacheUtils;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
<p>
Handles fetching URLs and if for some reason anything happens add it to the
BadURLManager.  There are also some util methods for downloading URLs that don't
use the Disk Cache.
</p>



@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
@version $Id: URLFetcher.java,v 1.14 2004/02/23 03:30:47 jford Exp $
*/
public class URLFetcher 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(URLFetcher.class.getName());
    
    /**
    URLs that Jetspeed is currently trying to fetch in real time.
    */
    private static Hashtable realtime_urls = new Hashtable();

    /**
     *
     */
    static final boolean shouldFetchNow = 
        JetspeedResources.getBoolean( JetspeedResources.CACHE_REQUIRE_CACHED_KEY );
    
    static {
        //Looking for redirected channels...
        java.net.HttpURLConnection.setFollowRedirects(true);
    }

    public static final Reader fetch( String url ) throws IOException {
        return fetch ( url, false );
    }

    
    /**
    Try and fetch a URL as and get the content as a String and possibly add
    the URL to the BadURLManager if anything goes wrong.
    
    @param url The URL to fetch
    @param force if set to true then do not use force this entry to be in the cache...
                 IE do not use CACHE_REQUIRE_CACHED
    */
    public static final Reader fetch( String url,
                                           boolean force ) throws IOException {

        if ( ! URLManager.isOK( url ) ) {
            throw new URLNotAvailableException( url );
        }

        //SGP
        if( force == false && DiskCacheUtils.isCached( url ) == true) 
        {
            logger.info( "The url " + 
                      url + 
                      " is fetched from the Cache" );
            return JetspeedDiskCache.getInstance().getEntry( url ).getReader();
        }
        
        //do cache required checking
        if ( shouldFetchNow && 
             DiskCacheUtils.isCached( url ) == false && 
             isRealtimeURL( url ) == false &&
             force == false ) {

            logger.info( "The url " + 
                      url + 
                      " is not in the cache and will be fetched now because you have configured -> " + 
                      JetspeedResources.CACHE_REQUIRE_CACHED_KEY );
                 
            //it is possible that two thread request the same URL.
            //The refresh call in JetspeedDiskCache takes care of this.
            JetspeedDiskCache.getInstance().refresh( url );
            
            //thow an Exception that this isn't in the cache.
            throw new ContentNotAvailableException( url );
        }

        if( isRealtimeURL( url ) == true ) {
            addRealtimeURL( url );
          synchronized(url.intern())
          {
             try
             {
               //We wait for other thread to load
                url.intern().wait();
             } catch (InterruptedException e)
             {
               logger.info("Wait Interrupted");
             } finally
             {
                  removeRealtimeURL( url );
               }
          }
            // We try again
          return URLFetcher.fetch( url, force );
        } else {
            addRealtimeURL( url );
        }
        try {
            
            URL content;

	    // Determine the URL's protocol
            String protocol = url.substring(0, url.indexOf(":/"));
	    
	    // Check if a proxy is set. If no port is set, use the default port (-1)
            String proxyHost = URLManager.getProxyHost( protocol );
            if (proxyHost != null)
            {
                // Open the URL using a proxy
                content = new URL(protocol,
                                  proxyHost,
                                  URLManager.getProxyPort( protocol ),
                                  url);
            }
            else
            {
                content = new URL( url );
            }

            URLConnection conn = content.openConnection();
            return getReader( conn );
            
        } catch ( Throwable t ) {
            
            String reason = "";
            
            if ( t instanceof MalformedURLException ) {
                reason = "The URL is Malformed.";
            } else {
                reason = t.toString();
            }
            
            //if the URL couldn't be fetched because it is remote AND
            //it is not in the cache, add it to the bad URL list.
            if ( DiskCacheUtils.isCached( url ) == false ) {
                //Reported up there...
                //logger.error( t );
                URLManager.register( url, URLManagerService.STATUS_BAD, reason );
            } else {
            //it is in the cache, remove it (could be broken in cache).
            //next time we could be luckier.
                JetspeedDiskCache.getInstance().remove(url);
            }


            throw new URLNotAvailableException( reason, url );

        } finally {
            removeRealtimeURL( url );
        }

    }


    /**
    Try and fetch a URL if the copy in the cache has expired and add
    the URL to the BadURLManager if anything goes wrong.
    
    @param url The URL to fetch
    @param force if set to true then do not use force this entry to be in the cache...
                 IE do not use CACHE_REQUIRE_CACHED
    */
    public static final boolean refresh( String url) throws IOException {
        
        if ( ! URLManager.isOK( url ) ) {
            if( DiskCacheUtils.isCached(url) ) 
                JetspeedDiskCache.getInstance().remove(url);
            throw new URLNotAvailableException( url );
        }
        
        if(isRealtimeURL(url)) {
            return false;
        }

            
         DiskCacheEntry dce = null;
         if( DiskCacheUtils.isCached(url) ) {
             try {
                 dce = JetspeedDiskCache.getInstance().getEntry( url );
                 if(!dce.hasExpired())
                 {
                         return false;
                 }
                 addRealtimeURL( url );

                 //only update this if the URL on which it is based is newer 
                 //than the one on disk.
                 URL sock;
                   
                 // Determine the URL's protocol
                 String protocol = url.substring(0, url.indexOf(":/"));
           
                 // Check if a proxy is set. If no port is set, use the default port (-1)
                 String proxyHost = URLManager.getProxyHost( protocol );
                 if (proxyHost != null)
                 {
                     // Open the URL using a proxy
                     sock = new URL(protocol,
                                    proxyHost,
                                    URLManager.getProxyPort( protocol ),
                                    url);
                 }
                 else
                 {	
                     sock = new URL( url );
                 }

                 URLConnection conn = null;
                 conn = sock.openConnection();

                 File file = dce.getFile();
                 long mod = dce.getLastModified();
                 long filesize = 0;
                 if(file != null)
                 {
                     filesize = file.length();
                 }

                 if(mod > 0 || filesize > 0)
                     conn.setIfModifiedSince(mod);
                   
                 conn.connect();
                 long last = conn.getLastModified();
                 long expires = conn.getExpiration();
                 int clength = conn.getContentLength();
                 int respCode = 200;
                 if(conn instanceof HttpURLConnection) {
                     respCode = ( ( HttpURLConnection )conn ).getResponseCode();
                 }
                   
                 if (respCode != 304 /*NOT MODIFIED*/ && 
                     (clength == -1 || clength > 0) && 
                     (  last == 0 || 
                       last > dce.getLastModified()) ) {

                     logger.info( "URLFetcher: Found updated URL: " + 
                               url +
                               " Modified " + last + " Expires: " + expires +
                               " CLength: " + clength ); 
                
                     //force this URL to update.

                     JetspeedDiskCache.getInstance().getEntry( url, getReader( conn ) );
                     //Trying to deal with a problem under FreeBSD
                     conn.getInputStream().close();

                     //Set the last modified and expiration times for entry
                     //FIXME: 0 is used in FileWatcher to mean not initialized...
                     if(last > 0)
                         dce.setLastModified(last);    
                     else
                         dce.setLastModified( System.currentTimeMillis() );
                     dce.setExpirationTime(expires);


                     //removeRealtimeURL( url ); (done in finally)
                     return true;
                     //now make sure that the entry that depends on this HREF
                     //is updated in the PortletFactory.
                 } else {

                     if(last > 0)
                         dce.setLastModified(last);    
                     else
                         dce.setLastModified( System.currentTimeMillis() );
                     dce.setExpirationTime(expires);
                           
                       
                     logger.info( "DiskCacheDaemon: URL still valid: " + url +
                               " Modified " + last + " Expires: " + expires +
                               " CLength: " + clength); 
                     //removeRealtimeURL( url ); (done in finally)
                     return false;
                 }
             } catch (Throwable e) {
                 //Add as a Bad URL
                 logger.error("Throwable",  e);
                 URLManager.register( url,
                                      URLManagerService.STATUS_BAD,
                                      e.toString() );
             } finally {
                 removeRealtimeURL( url );
             }
                   
         } else {
             logger.info( "URLFetcher: Cache miss during validation! Forcing url: " + url ); 
             removeRealtimeURL( url );
             JetspeedDiskCache.getInstance().getEntry( url, true );
             return true;
         }
           return false;
                    
    }


    /**
     *
     * Return a Reader for a given HTTP connection.
     * If the connection first line contains a XML declaration
     * with encoding, honor this encoding.
     * If not, use the encoding from the HTTP connection,
     * taking ISO-8859-1 as default.
     *
    */
    static final Reader getReader( URLConnection conn )
        throws IOException, UnsupportedEncodingException {
        String enc = conn.getContentEncoding();
        if( enc == null ) {
            enc = "ISO-8859-1";
        }
        // Some XML files come with a encoding attribute inside,
        // different than the HTTP encoding. We will have
        // to start reading the Reader, read the attribute and rewind 
        // the stream, generating a new reader with the "true" encoding
        BufferedInputStream is = new BufferedInputStream( conn.getInputStream() );
        //If document is XML, find the encoding and give it priority over
        //the one returned by the connection

        //we mark for resetting later. We need a big number to ensure
        // stack of streams don't read it to fill buffers.
        is.mark( 20480 );
        BufferedReader asciiReader = new BufferedReader( new InputStreamReader( is, "ASCII" ) );
        String decl = asciiReader.readLine();
        //System.err.println( "Line: " + decl );
        String key = "encoding=\"";
        //decl nul means that the connection got reset...
        if( decl != null ) {
            int off = decl.indexOf( key );
            if( off > 0 ) {
                enc = decl.substring( off + key.length(), 
                                      decl.indexOf( '"' , off + key.length()) );
            }
        }
        logger.info("URLFetcher: found URL with encoding -> " + enc );
        //Reset the bytes read
        is.reset();
        Reader rdr = new InputStreamReader( is,
                                            enc );
        return rdr;
    }


    
    /**
    Add a URL that is downloading in realtime
    */
    static final void addRealtimeURL( String url ) {
        synchronized( realtime_urls )
        {
            Vector threads = (Vector) realtime_urls.get( url);
            if(threads != null)
               {
                if(!threads.contains(Thread.currentThread()))
                   {
                     threads.addElement(Thread.currentThread() );
                   }
               } else {
                threads = new Vector();
                threads.addElement(Thread.currentThread());
                realtime_urls.put( url, threads  );
               }
        }
        
    }
    
    /**
    Remove a URL because it isn't downloading anymore.
    */
    static final void removeRealtimeURL( String url ) {
        synchronized( realtime_urls )
        {
           Vector threads = (Vector) realtime_urls.get( url);
           if(threads != null)
               synchronized( threads  )
                   {
                    Thread realLoader = (Thread) threads.firstElement();
                    if(realLoader == Thread.currentThread())
                    {
                      synchronized(url.intern())
                     {
                      realtime_urls.remove(url);
                      url.intern().notifyAll();
                      }          
                     } else {
                     threads.removeElement(Thread.currentThread());
                     }
                    }
        }
        
    }

    /**
    Return true if this URL isn't downloading in realtime.
    */
    static final boolean isRealtimeURL( String url ) {

        synchronized( realtime_urls ) {
            return realtime_urls.get( url ) != null;
        }
            
    }

    /**
    Return the list of realtime URLs for debug
    */
    public static final Hashtable getRealtimeURLs() {
        synchronized(realtime_urls) {
            return realtime_urls;
        }
    }
    
}
