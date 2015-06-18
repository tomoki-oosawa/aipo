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

import org.apache.jetspeed.portal.expire.Expire;
import org.apache.turbine.services.cache.CachedObject;

/**
 * <p>This is an interface for defining Jetspeed objects that may be 
 * cached in the object cache.</p>
 * <p>Each such object must be able to prodive a caching handle
 * that will uniquely identify it within the cache system</p>
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: Cacheable.java,v 1.7 2004/02/23 03:34:54 jford Exp $
 */
public interface Cacheable
{
    /**
     * Return true if this Cacheable is allowed to be cached.
     *
     * @return <CODE>TRUE</CODE> if object is cachable<br>
     * <CODE>FALSE</CODE> if object is not cacheable
     */
    public boolean isCacheable();
    
    /**
     * Set this cacheable status.
     *
     * @param cacheable Set the cacheability of the object
     */
    public void setCacheable(boolean cacheable);
    
    /**
     * Used by a Cacheable object to determine when it should expire itself from
     * the cache.
     *
     * @return Expire handle
     */
    public Expire getExpire();

    /**
     * <p>Used by the cache to get a unique reference on what you want to add
     * and then retrieve in the future from the cache</p>
     *
     * <p>Most implementations should just call the CacheHandleManager with
     * the given params within the implementation and just return this.</p>
     *
     * @deprecated cacheable classes should now implement a static getHandle(config) method
     */
    public String getHandle();

    /***
     * <p>Set the handle for this Cacheable.</p>
     *
     * <p>Note that factories should call setHandle() so that getHandle() always
     * returns a value correctly.</p>
     *
     * @deprecated cacheable classes should now implement a static getHandle(config) method
     */
    public void setHandle( String handle );
  
    /**
     * Return the expiration time in milliseconds.
     *
     * @return Expiration time in milliseconds since epoch, or null if the 
     *         expiration was not set
     */
    public Long getExpirationMillis();

    /**
     * Set the expiration time in milliseconds.
     *
     * @param expirationMillis Set expiration in millis
     */
    public void setExpirationMillis( long expirationMillis);

    /**
     * This allows the associated CachedObject to be
     * known.  One use of the <code>co</code> is to set the expiration time
     *
     * @param co Handle to the CachedObject
     */    
     public void setCachedObject(CachedObject co);
}
