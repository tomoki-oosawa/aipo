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

package org.apache.jetspeed.om.security;


/**
 * A Principal based on the user id.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: UserIdPrincipal.java,v 1.3 2004/02/23 03:14:12 jford Exp $
 */
public class UserIdPrincipal implements java.security.Principal
{
    private final String userId;

    public UserIdPrincipal(String userId)
    {
        this.userId = userId;   
    }

    /**
     * Compares this principal to the specified object.  Returns true
     * if the object passed in matches the principal represented by
     * the implementation of this interface.
     *
     * @param another principal to compare with.
     *
     * @return true if the principal passed in is the same as that
     * encapsulated by this principal, and false otherwise.

     */
    public boolean equals(Object another)
    {
        if (!(another instanceof UserIdPrincipal))
            return false;
        UserIdPrincipal principal = (UserIdPrincipal)another;
        return this.getName().equals(principal.getName());
    }

    /**
     * Returns a string representation of this principal.
     *
     * @return a string representation of this principal.
     */
    public String toString()
    {
        return this.userId;
    }


    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    public String getName()
    {
        return this.userId;
    }

}


