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
 * A Jetspeed basic Role.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: BaseJetspeedRole.java,v 1.3 2004/02/23 03:14:12 jford Exp $
 */
public class BaseJetspeedRole implements Role
{
    protected String name;
    protected String id = null;

    protected boolean isNew = true;

    public BaseJetspeedRole(String id)
    {
        this.id = id;
        isNew = true;
    }

    public BaseJetspeedRole()
    {
        isNew = true;
    }

    /**
     * Get the name of the Role
     *
     * @return the name of the role.
     */
    public String getName()
    {
        return name;
    }
 
    /**
     * Set the name of the Role
     *
     * @param roleName the name of the Role.
     */
    public void setName(String roleName)
    {
        name = roleName;
    }

    /**
     * Get the id of the Role
     *
     * @return the id of the role.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Set the id of the Role
     *
     * @param id the new id for the role
     */
    public void setId(String id)
    {      
        if (this.id == null)
        {
            this.id = id;
        }
    }

    public boolean isNew()
    {
        return isNew;
    }

    void setNew(boolean isNew)
    {
        this.isNew = isNew;
    }

}


