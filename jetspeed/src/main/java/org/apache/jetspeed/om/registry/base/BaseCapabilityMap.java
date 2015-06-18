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

import org.apache.jetspeed.om.registry.CapabilityMap;
import java.util.Vector;
import java.util.Iterator;

/**
 * Simple bean-like implementation of the CapabilityMap
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseCapabilityMap.java,v 1.3 2004/02/23 03:08:26 jford Exp $
 */
public class BaseCapabilityMap implements CapabilityMap, java.io.Serializable
{
    private Vector caps = new Vector();

    public BaseCapabilityMap()
    {
    }

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

        BaseCapabilityMap obj = (BaseCapabilityMap)object;

        Iterator i = caps.iterator();
        Iterator i2 = obj.caps.iterator();
        while(i.hasNext())
        {
            String c1 = (String)i.next();
            String c2 = null;

            if (i2.hasNext())
            {
                c2 = (String)i2.next();
            }
            else
            {
                return false;
            }

            if (!c1.equals(c2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        return true;
    }

    public Iterator getCapabilities()
    {
        return caps.iterator();
    }

    public void addCapability(String name)
    {
        if (!caps.contains(name))
        {
            caps.add(name);
        }
    }

    public void removeCapability(String name)
    {
        caps.remove(name);
    }

    /**
     * Checks if the argument capability is included in this map
     *
     * @param capabiltiy a capability descriptor
     * @return true if the capability is supported
     */
    public boolean contains(String capability)
    {
        return caps.contains(capability);
    }

    /**
     * Checks if the all the elements of argument capability map
     * are included in the current one
     *
     * @param map a CapabilityMap implementation to test
     * @return true is all the elements the argument map are included in the
     * current map.
     */
    public boolean containsAll(CapabilityMap map)
    {
        Iterator i = map.getCapabilities();

        while(i.hasNext())
        {
            String capability = (String)i.next();
            if (!contains(capability))
            {
                return false;
            }
        }

        return true;
    }

    // castor related method definitions

    public Vector getCaps()
    {
        return caps;
    }

}
