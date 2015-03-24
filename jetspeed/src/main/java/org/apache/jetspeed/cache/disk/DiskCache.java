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

import java.io.*;

/**
<p>
A way to store remote documents locally.  This can increase performance by 
eliminating TCP connection latency.
</p>

<p>
All implementations of a DiskCache should implement this interface.
</p>

@author <A HREF="mailto:burton@apache.org">Kevin A. Burton</A>
@author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
@version $Id: DiskCache.java,v 1.15 2004/02/23 02:45:29 jford Exp $
*/
public interface DiskCache {
    
    /**
    <p>
    Take the given remove URL and place it in the disk cache.  Additionaly 
    operations include building a DiskCacheEntry and then returning it.
    </p>
    
    <p>getEntry may attempt to pull down the URL if it is not in the disk cache.
    Most implementations may provide support for changing this behavior so that
    you have to explicitly call add.  This is done for performance reasons
    so that HTTP clients don't end up triggering a URL connection to fetch 
    the given URL.</p>
    
    */
    public DiskCacheEntry getEntry( String url ) throws IOException;

    /**
    <p>Get an Entry from the from the cache but force this URL to be fetched and
    then cached no matter what configuration options Jetspeed provides.
    
    @see #getEntry( String url )
    */
    public DiskCacheEntry getEntry( String url,
                                    boolean force ) throws IOException;
    
    /**
    Get a entry based on a URL but you should already have the content.  This is
    usually used to specify an alternative Reader (maybe StringReader).

    <p>getEntry may attempt to pull down the URL if it is not in the disk cache.
    Most implementations may provide support for changing this behavior so that
    you have to explicitly call add.  This is done for performance reasons
    so that HTTP clients don't end up triggering a URL connection to fetch 
    the given URL.</p>
    
    */
    public DiskCacheEntry getEntry( String url, Reader is ) throws IOException;
    
    /**
    Get a list of all the documents within the cache...
    
    */
    public DiskCacheEntry[] getEntries();

    /**
    Add this URL to the disk cache
    
    */
    public void add( String url ) throws IOException;
    
    /**
    Remove this URL from the disk cache.
    
    */
    public void remove( String url ) throws IOException;
    

    /**
    Get the URL from the Internet and then place it in the File dest.
    
    */
    public String fetch( String url, String destination ) throws IOException ;

    /**
    Return the root of this DiskCache.
    
    */
    public String getRoot();

    /**
    Tell the DiskCache that this URL should be refreshed.  This will do this in 
    a threaded and asynchronous manner.
    
    */
    public void refresh( String url );

    /**
    Ask if a url is in the DiskCache  
    
    */
    public boolean isCached( String url );

}
