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

package org.apache.jetspeed.om.profile.psml;

import org.apache.jetspeed.om.profile.Role;

/**
 * Bean like implementation of the Role interface suitable for 
 * Castor serialization.
 * 
 * @see org.apache.jetspeed.om.registry.Security
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PsmlRole.java,v 1.3 2004/02/23 03:02:54 jford Exp $
 */
public class PsmlRole implements Role, java.io.Serializable
{

    private String name = null;

    public PsmlRole()
    {}
    
    /** @return the role name that is required for accessing this entry */
    public String getName()
    {
        return this.name;
    }
                                
    /** Sets the role name required for accessing this entry
     * @param name the required role name
     */
    public void setName( String name )
    {
        this.name = name;
    }

}