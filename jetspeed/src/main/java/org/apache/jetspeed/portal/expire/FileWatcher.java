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

package org.apache.jetspeed.portal.expire;

//jetspeed stuff
import org.apache.jetspeed.cache.disk.DiskCacheEntry;
import org.apache.jetspeed.cache.disk.DiskCacheUtils;
import org.apache.jetspeed.cache.disk.JetspeedDiskCache;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

//java stuff
import java.io.IOException;
import java.io.Serializable;


/**
A generic class for watching a file and determining if it has changed.

@author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
@version $Id: FileWatcher.java,v 1.16 2004/02/23 03:24:40 jford Exp $
*/
public class FileWatcher implements Serializable
{

    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(FileWatcher.class.getName());    
    
    //BEGIN Exception index

    public static final String EXCEPTION_URL_NOT_NULL = 
        "URL can NOT be null here.";
    
    public static final String EXCEPTION_URL_NOT_IN_CACHE = 
        "The URL you specified within the disk cache does not exist: ";
    
    
    //END Exception index    
    
    /**
    The URL on which this FileWatchExpire is based.
    */
    private String url = "";

    /**
    The last time this files URL has been modified on disk.
    */
    private long lastModified = 0;

    /**
    The object that relies on this FileWatcher
    */
    private String parent = "";

    /**
    Create a FileWatcher with no parent info.
    
    @see #FileWatcher( String, String )
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: FileWatcher.java,v 1.16 2004/02/23 03:24:40 jford Exp $
    */
    public FileWatcher( String url ) throws IOException {
        this( url, null );
    }
                            
    /**
    Create a new FileWatcher to watch the given URL.
    
    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: FileWatcher.java,v 1.16 2004/02/23 03:24:40 jford Exp $
    */
    public FileWatcher( String url,
                        String parent ) throws IOException {
        
        if ( url == null ) {
            throw new IOException( EXCEPTION_URL_NOT_NULL );
        }

        if ( DiskCacheUtils.isRemote( url ) &&
             DiskCacheUtils.isCached( url ) == false ) {
            
            throw new IOException( EXCEPTION_URL_NOT_IN_CACHE + url );
        }

        //Try to set last modified when creating FileWatcher objet
        try {
            this.lastModified = JetspeedDiskCache.getInstance().getEntry( url ).
                                                            getLastModified();
        } catch (Throwable e) 
        {
            logger.error( "Unable to set last modified on url " + url, e );
        }
        
        this.url = url;
        this.parent = parent;
    }
    
    /**
    Return true if the URL on which this is based has changed.

    @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
    @version $Id: FileWatcher.java,v 1.16 2004/02/23 03:24:40 jford Exp $
    */
    public boolean hasChanged() {

        try {

            //initially set the lastModified data
            if ( this.lastModified == 0 ) {
            
                DiskCacheEntry entry = JetspeedDiskCache.getInstance().getEntry( url );
                
                this.lastModified = entry.getLastModified();

                return false;
            
            }

            //the recent modification... if there was one. otherwise it will 
            // == this.lastModified
            long recent = JetspeedDiskCache.getInstance()
                .getEntry( url ).getLastModified();

            // 0 means always modified
            if ( recent == 0 || 
                this.lastModified < recent ) {
                   
                if ( logger.isInfoEnabled() )
                {
                    String message = "";

                    if ( this.parent != null ) {
                        message = this.parent + ":  ";
                    }

                    message += "REFRESH: Expiring Portlet because it's URL has been modified on disk -> " + url;

                    logger.info( message );
                }
                return true;
            } 
            
        } catch ( IOException e ) {
            logger.error("Exception",  e );
            return false;
        }
        
        //default should be to not expire.  This is set if the URL is null
        return false;        
        
    }
    
}
