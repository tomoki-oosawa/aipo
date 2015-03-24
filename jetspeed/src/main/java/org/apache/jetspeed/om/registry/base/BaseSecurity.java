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
 * Bean like implementation of the Security interface suitable for
 * Castor serialization.
 *
 * @see org.apache.jetspeed.om.registry.Security
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseSecurity.java,v 1.4 2004/02/23 03:08:26 jford Exp $
 */
public class BaseSecurity implements Security,java.io.Serializable
{

    private String role;

    public BaseSecurity()
    {}

    public BaseSecurity(String role)
    {
        this.role = role;
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

        BaseSecurity obj = (BaseSecurity)object;

        if (role!=null)
        {
            return role.equals(obj.getRole());
        }
        else
        {
            if (obj.getRole()!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @return the role name that is required for accessing this entry */
    public String getRole()
    {
        return this.role;
    }

    /** Sets the role name required for accessing this entry
     * @param role the required role name
     */
    public void setRole( String role )
    {
        this.role = role;
    }

}
