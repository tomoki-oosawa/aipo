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
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseParameter.java,v 1.4 2004/02/23 03:08:26 jford Exp $
 */
public class BaseParameter extends BaseRegistryEntry
    implements Parameter, java.io.Serializable
{

    private String value = null;
    private String type = null;

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

        BaseParameter obj = (BaseParameter)object;

        if (value!=null)
        {
            if (!value.equals(obj.getValue()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getValue()!=null)
            {
                return false;
            }
        }

        if (type!=null)
        {
            if(!type.equals(obj.getType()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getType()!=null)
            {
                return false;
            }
        }

        return super.equals(object);
    }

    /** @return the value for this parameter */
    public String getValue()
    {
        return this.value;
    }

    /** Sets the value of this parameter.
     *
     * @param value the new parameter value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /** @return the parameter's type */
    public String getType()
    {
        return this.type;
    }

    /** Sets the type of this parameter.value.
     *
     * @param type the new parameter type
     */
    public void setType(String type)
    {
        this.type = type;
    }
}
