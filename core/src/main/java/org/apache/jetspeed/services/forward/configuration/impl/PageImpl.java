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

package org.apache.jetspeed.services.forward.configuration.impl;

import org.apache.jetspeed.services.forward.configuration.Page;

/**
 * Page Implementation for Forward
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PageImpl.java,v 1.2 2004/02/23 03:50:10 jford Exp $
 */
public class PageImpl implements Page, java.io.Serializable
{

    private String name;
    private String user;
    private String role;
    private String group;


    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUser()
    {
        return this.user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getRole()
    {
        return this.role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public String getGroup()
    {
        return this.group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }


}



