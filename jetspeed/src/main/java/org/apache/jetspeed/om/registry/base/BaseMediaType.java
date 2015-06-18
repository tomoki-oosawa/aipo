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

/**
 * Utility object for serializing with Castor a MediaTypeEntry reference
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseMediaType.java,v 1.3 2004/02/23 03:08:26 jford Exp $
 */
public class BaseMediaType implements java.io.Serializable
{

    private String name;

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

        BaseMediaType obj = (BaseMediaType)object;

        if (name!=null)
        {
            return name.equals(obj.getRef());
        }
        else
        {
            if (obj.getRef()!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @return the media type name that is referenced */
    public String getRef()
    {
        return this.name;
    }

    /** Sets the media type name referenced by this object
     * @param name the required media type name
     */
    public void setRef( String name )
    {
        this.name = name;
    }

}
