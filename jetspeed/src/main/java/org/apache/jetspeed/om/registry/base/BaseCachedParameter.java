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

import org.apache.jetspeed.om.registry.*;

/**
 * Bean like implementation of the Parameter interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.Parameter
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: BaseCachedParameter.java,v 1.5 2004/02/23 03:08:26 jford Exp $
 */
public class BaseCachedParameter extends BaseParameter
    implements CachedParameter, java.io.Serializable
{
    private boolean cachedOnName = true;
    private boolean cachedOnValue = true;

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

        BaseCachedParameter obj = (BaseCachedParameter)object;

        if (cachedOnName != obj.getCachedOnName())
        {
            return false;
        }

        if (cachedOnValue != obj.getCachedOnValue())
        {
            return false;
        }

        return super.equals(object);
    }

    public boolean isCachedOnName()
    {
        return cachedOnName;
    }

    public boolean isCachedOnValue()
    {
        return cachedOnValue;
    }

    public void setCachedOnName(boolean cached)
    {
        this.cachedOnName = cached;
    }

    public void setCachedOnValue(boolean cached)
    {
        this.cachedOnValue = cached;
    }

    // castor
    public boolean getCachedOnName()
    {
        return cachedOnName;
    }

    public boolean getCachedOnValue()
    {
        return cachedOnValue;
    }

}