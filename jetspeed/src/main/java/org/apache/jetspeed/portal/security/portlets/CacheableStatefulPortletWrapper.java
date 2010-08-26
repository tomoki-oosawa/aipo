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

package org.apache.jetspeed.portal.security.portlets;

//jetspeed
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.expire.Expire;
import org.apache.jetspeed.services.portletcache.Cacheable;

import org.apache.turbine.services.cache.CachedObject;


/**
<p>
This object is used to wrap a Portlet, ensuring that access control rules are enforced.
</p>

@author <A HREF="mailto:sgala@apache.org">Santiago Gala</A>
@version $Id: CacheableStatefulPortletWrapper.java,v 1.4 2004/02/23 03:27:46 jford Exp $
*/
public class CacheableStatefulPortletWrapper extends StatefulPortletWrapper implements /*FIXME*/Cacheable
{

    /*
     * The cacheable associated with us
     */
    private Cacheable wrappedCacheable = null;

    
    public CacheableStatefulPortletWrapper( Portlet inner )
    {
        super( inner );
        if( inner instanceof Cacheable )
        {
            wrappedCacheable = (Cacheable) inner;
        }
        else
        {
            //Log error or throw exception
        }
            
    }

    //Cacheable interface

    /**
    */
    public boolean isCacheable()
    {
        return wrappedCacheable.isCacheable();
    }

    /**
    */
    public void setCacheable(boolean cacheable)
    {
        wrappedCacheable.setCacheable( cacheable );
    }


    /**
    */
    public Expire getExpire()
    {
        return wrappedCacheable.getExpire();
    }

    /**
    */
    public final String getHandle()
    {
        return wrappedCacheable.getHandle();
    }

    /**
    */
    public final void setHandle( String handle )
    {
        wrappedCacheable.setHandle( handle );
    }

    /**
    @see Cacheable#getExpirationMillis
    */
    public Long getExpirationMillis()
    {
      return wrappedCacheable.getExpirationMillis();
    }
    

    /**
    @see Cacheable#setExpirationMillis
    */
    public void setExpirationMillis( long expirationMillis)
    {
      wrappedCacheable.setExpirationMillis( expirationMillis );
    }

    /**
     * This allows the associated CachedObject to be
     * known.  One use of the <CODE>cachedObject</CODE> is to
     * set the expiration time
     *
     * @param cachedObject Handle to the CachedObject
     */
    public void setCachedObject(CachedObject cachedObject)
    {
      wrappedCacheable.setCachedObject( cachedObject );
    }
    
}
