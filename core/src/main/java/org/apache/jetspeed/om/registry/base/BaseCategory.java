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

import org.apache.jetspeed.om.registry.Category;

/**
 * Bean like implementation of the Category interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.Security
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: BaseCategory.java,v 1.4 2004/02/23 03:08:26 jford Exp $
 */
public class BaseCategory implements Category, java.io.Serializable
{
    private String name;
    private String group = "Jetspeed";

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

        BaseCategory obj = (BaseCategory)object;

        if (name!=null)
        {
            if (!name.equals(obj.getName()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getName()!=null)
            {
                return false;
            }
        }

        if (group!=null)
        {
            if(!group.equals(obj.getGroup()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getGroup()!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @return the string Name */
    public String getName()
    {
        return name;
    }

    /** Sets the string Name
     *
     * @param value the new Name value
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /** @return the string Group */
    public String getGroup()
    {
        return group;
    }

    /** Sets the string Group
     *
     * @param value the new Group value
     */
    public void setGroup(String group)
    {
        this.group = group;
    }

}