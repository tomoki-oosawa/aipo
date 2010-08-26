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

package org.apache.jetspeed.cache.disk;

//standard java stuff
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

//turbine
import org.apache.turbine.services.servlet.TurbineServlet;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.URIEncoder;

/**
   <p>
   Misc utils for managing the disk cache.
   </p>

   <p>
   This tries to separate URLs into three categories:
       <ul>
           <li>
           Virtual: URLs which are contructored such as: /test/test.xml
           </li>

           <li>
           Local: URLs which are contructored such as: http://localhost/test/test.xml
           </li>

           <li>
           Remote: URLs which are contructored such as: http://REMOTE.SERVER/test/test.xml
           </li>
           
       </ul>
   </p>
   
   @see DiskCache
   @author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
   @version $Id: DiskCacheUtils.java,v 1.20 2004/02/23 02:45:29 jford Exp $
*/
public class DiskCacheUtils {

    /**
    Used to determine if a given URL should be cached.  This prevents people 
    from trying to cache documents that aren't supported.  http and ftp should
    fit almost any situation.
    */
    public final static String[] VALID_PROTOCOLS = { "http", "ftp" };
    
    /**
    Stores the protocols which sould be recognized as local
    */
    private static Vector localprotocols = JetspeedResources.getVector("diskcache.localprotocols");

    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(DiskCacheUtils.class.getName()); 
    
    private static String hostName = "localhost";
    static {
        try {
            hostName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Throwable t) {}
        if (localprotocols.size()==0) { // default values, if key is not defined
            localprotocols.add("file");
        }
    }
    
    /**
    Give an full url:  http://www.cnn.com/test
    
    just return the virutal portion:  /test
    */
    public static String getVirtual( String url ) {
        
        //strip off the begining of the URL if necessary:
        
        int begin = 0;
        
        if ( url.indexOf(":/") != -1 ) {
            begin = url.indexOf( ":/" ) + 2;
        }
        
        if ( begin > 0 ) {
            url = url.substring( begin, url.length() );
        }
        
        url = url.substring( url.indexOf("/"), url.length() );
        
        
        return url;
    }
    
    /**
       Given a virtual URL, resolve it to a local URL:
    
       Ex: /test.xml  -> http://localhost:80/test.xml
    */
    public static String getLocalURL( String virtual ) {

     //if this virtual URL is actually a local, return it directly.
        if ( virtual != null &&
             isLocal( virtual ) &&
             virtual.indexOf("/") != 0 ) {
            return virtual;
        }

        if ( isVirtual( virtual ) == false ) {
            throw new IllegalArgumentException( "The URL specifies is not a virtual URL: " + virtual );
        }
                
        String url = TurbineServlet.getResource( virtual ).toString();
        
        return url;
    }

    /**
       Return true if this URL is virtual.

       EX: /tmp/test.xml
     */
    public static boolean isVirtual( String url ) {

        if ( url.indexOf( "/" ) == 0 ) {
            return true;
        } 

        return false;
    }
    
    /**
    Return true if this URL is on the local server.
    */
    public static boolean isLocal( String url ) {


        /*
          If the URL is virtual, return true:

          EX: /test/test.xml
              
         */
        
        if ( url != null && url.length() > 1 ) {

            if ( ( url.indexOf( ":/" ) == -1 ) ) {
                //this must be a local URL because it is virtual "/test/test.xml"
                return true;
            }


        /*
          ok... perform two more tests.  if the URL is on the local server.. or on
          localhost then return true
          
          EX: http://localhost
              http://server.domain
         */
            if ( ( url.indexOf( "http://localhost" ) == 0 ) || 
                 ( url.indexOf( "http://127.0.0.1" ) == 0 ) || 
                 ( url.indexOf( "http://" + hostName ) == 0 ) ||

           // RL: using EngineContext doesn't work because the serverName
           // and serverPort is a request-based information.
           // We should either fix EngineContext to use a config property
           // or remove it altogether and find something that always works !
        
             /* ( url.indexOf( EngineContext.getInstance().getServerScheme() +
                            "://" + 
                            EngineContext.getInstance().getServerName() ) == 0 ) */
                    ( false )
                 ) {
                return true;
            }
            
            /* SH Testing local protocols also */
            if (localprotocols!=null) {
                Enumeration en = localprotocols.elements();
                while(en.hasMoreElements()) {
                    String protocol = (String)en.nextElement()+":";
                    if ( url.indexOf(protocol) != -1 ) 
                    {
                        return true;
                    }
                }
            }

        }
        return false;
    }
    
    /**
    Return true if this URL is NOT on the local server.
    */
    public static boolean isRemote( String url ) {
        return ! isLocal( url );
    }
    
    /**
    Return true if this url is in the cache.
    @see DiskCache#isCached( String )
    */
    public static boolean isCached( DiskCache instance, 
                                    String url ) {
     
    /*         if ( isLocal( url ) ) {
               //SGP: I think we should not cache local urls
               return false;
               }*/
                
    //         return DiskCacheUtils.getFile( instance, url ).exists();
         return instance.isCached(url);
    }

    /**
    @see DiskCacheUtils#isCached( DiskCache, String )
    */
    public static boolean isCached( String url ) {
        return isCached( JetspeedDiskCache.getInstance(), url );
    }
    
    /**
    Return true if the given URL should be cached or not.
    */
    public static boolean isCacheable( String url ) {

        for (int i = 0; i < VALID_PROTOCOLS.length;++i) {

            String uri = VALID_PROTOCOLS[i] + ":/";

            if (url.length() >= uri.length() &&
        url.substring(0, uri.length() ).equals( uri ) ) {
                return isRemote( url ); //SGP was true
            }

        }
        return false;
    }
    
    /**
       Given a URL, determine what the filename would be within the cache.  Note 
       that this doesn't return a URL just a path to where it would be stored 
       locally.
    */
    public static File getFile( DiskCache instance, 
                                String url ) {

      String file = URIEncoder.encode( url );

      file = instance.getRoot() + "/" + file;

      return new File( file );
      
    }

    /**
       Given a url and an disk cache instance, determine what the correct URL for this
       cache entry for the remote URL would be.
     */
    public static String getFileURL( DiskCache instance,
                                     String url ) {

        URL fileURL = null;
        
        try {
            fileURL = DiskCacheUtils.getFile( instance, url ).toURL();
        } catch (MalformedURLException e) {
            // what can we do in this case ?
            logger.error("Exception getting URL", e);
            return null;
        }
        
        return fileURL.toString();
        
    }
    
}

