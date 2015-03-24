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

package org.apache.jetspeed.services.portletcache;

import java.io.IOException;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.GlobalCacheService;
import org.apache.turbine.services.cache.CachedObject;
import org.apache.turbine.services.cache.ObjectExpiredException;


/**
 * This class is a static wrapper around the Turbine GlobalCacheService
 * which doesn't provide its own wrapper.
 * 
 * @see org.apache.turbine.services.cache.GlobalCacheService
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: GlobalCache.java,v 1.7 2004/02/23 03:34:54 jford Exp $
 */
public class GlobalCache {
        
    /**
     * Add an object to the cache
     *
     * @param id key of the object used by the cache to store and retrieve
     * it. Must not be null.
     * @param o the object to cache
     */
    public static void addObject( String id, CachedObject o ) {

        GlobalCacheService gcs = (GlobalCacheService)TurbineServices
            .getInstance()
            .getService( GlobalCacheService.SERVICE_NAME );
            
        gcs.addObject( id, o );
    }
    
    /**
     * Gets a cached object given its id (a String).
     *
     * @param id The String id for the object.
     * @return A CachedObject.
     * @exception ObjectExpiredException The object has expired in
     * the cache.
     */
    public static CachedObject getObject(String id)
        throws ObjectExpiredException {

        GlobalCacheService gcs = (GlobalCacheService)TurbineServices
            .getInstance()
            .getService( GlobalCacheService.SERVICE_NAME );
            
        return gcs.getObject( id );
    }
    
    /**
     * Gets size, in bytes, of cache
     *
     * @return int Size, in byte, of cache
     * @exception IOException Exception passed from
     */
    public static int getCacheSize()
    throws IOException {
        GlobalCacheService gcs = (GlobalCacheService)TurbineServices
            .getInstance()
            .getService( GlobalCacheService.SERVICE_NAME );
        
        return gcs.getCacheSize();
    }
    
    /**
     * Gets size, in bytes, of cache
     *
     * @return int, Size, in byte, of cache
     */
    public static int getNumberOfObjects() {
        int tempInt = 0;
        GlobalCacheService gcs = (GlobalCacheService)TurbineServices
            .getInstance()
            .getService( GlobalCacheService.SERVICE_NAME );
        
        tempInt = gcs.getNumberOfObjects();
        return tempInt;
    }
    
    /**
     * Flush the cache of <B>ALL</B> objects
     */
    public void flushCache() {
        GlobalCacheService gcs = (GlobalCacheService)TurbineServices
            .getInstance()
            .getService( GlobalCacheService.SERVICE_NAME );
        
        gcs.flushCache();
    }
}
