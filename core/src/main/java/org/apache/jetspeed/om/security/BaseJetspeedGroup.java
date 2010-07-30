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
 * A Jetspeed basic Group.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: BaseJetspeedGroup.java,v 1.3 2004/02/23 03:14:12 jford Exp $
 */
public class BaseJetspeedGroup implements Group
{
    protected String name;
    protected String id = null;

    protected boolean isNew = true;

    public BaseJetspeedGroup(String id)
    {
        this.id = id;
        isNew = true;
    }

    public BaseJetspeedGroup()
    {
        isNew = true;
    }

    /**
     * Get the name of the Group
     *
     * @return the name of the group.
     */
    public String getName()
    {
        return name;
    }
 
    /**
     * Set the name of the Group
     *
     * @param groupName the name of the Group.
     */
    public void setName(String groupName)
    {
        name = groupName;
    }

    /**
     * Get the id of the Group
     *
     * @return the id of the group.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Set the id of the Group
     *
     * @param id the new id for the group
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



