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

import org.apache.turbine.services.TurbineServices;

/**
 * This class is a static accessor for the PortletCache service
 * 
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: PortletCache.java,v 1.4 2004/02/23 03:34:54 jford Exp $
 */
public class PortletCache {
        
    /**
     * Add an object to the cache
     *
     * @see PortletCacheService#addCacheable
     */
    public static void addCacheable( Cacheable item ) {

        PortletCacheService gcs = (PortletCacheService)TurbineServices
            .getInstance()
            .getService( PortletCacheService.SERVICE_NAME );
            
        gcs.addCacheable( item );
    }
    
    /**
     * Removes an object from the cache based on its handle
     *
     * @see PortletCacheService#removeCacheable
     */
    public static void removeCacheable( String handle ) {

        PortletCacheService gcs = (PortletCacheService)TurbineServices
            .getInstance()
            .getService( PortletCacheService.SERVICE_NAME );
            
        gcs.removeCacheable( handle );

    }
    
    /**
     * Retrieves an object from the cache
     *
     * @see PortletCacheService#getCacheable
     */
    public static Cacheable getCacheable( String handle ) {

        PortletCacheService gcs = (PortletCacheService)TurbineServices
            .getInstance()
            .getService( PortletCacheService.SERVICE_NAME );
            
        return gcs.getCacheable( handle );
    }
    
}
