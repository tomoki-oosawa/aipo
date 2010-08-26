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

package org.apache.jetspeed.om.registry.base;

import org.apache.jetspeed.om.registry.ContentURL;

/**
 * Bean like implementation of the ContentURL interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.Security
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: BaseContentURL.java,v 1.4 2004/02/23 03:08:26 jford Exp $
 */
public class BaseContentURL implements ContentURL
{
    private String url;
    private boolean cacheKey = true;

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        BaseContentURL obj = (BaseContentURL)object;

        if (cacheKey != obj.cacheKey)
        {
            return false;
        }

        if (url!=null)
        {
            if(!url.equals(obj.url))
            {
                return false;
            }
        }
        else
        {
            if (obj.url!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @return the string URL */
    public String getURL()
    {
        return url;
    }

    /** Sets the string URL
     *
     * @param value the new URL value
     */
    public void setURL(String value)
    {
        this.url = value;
    }

    public boolean isCacheKey()
    {
        return cacheKey;
    }

    public void setCachedOnURL(boolean cacheKey)
    {
        this.cacheKey = cacheKey;
    }

    public boolean getCachedOnURL()
    {
        return this.cacheKey;
    }

}