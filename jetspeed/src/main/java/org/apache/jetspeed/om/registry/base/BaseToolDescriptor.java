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

import org.apache.jetspeed.om.registry.ToolDescriptor;

/**
 * Bean-like implementation of the ToolDescriptor interface
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @version $Id: BaseToolDescriptor.java,v 1.2 2004/02/23 03:08:26 jford Exp $
 */
public class BaseToolDescriptor implements ToolDescriptor, java.io.Serializable
{
    private String name = null;
    private String scope = null;
    private String classname = null;

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

        BaseToolDescriptor obj = (BaseToolDescriptor)object;

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

        if (scope!=null)
        {
            if(!scope.equals(obj.getScope()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getScope()!=null)
            {
                return false;
            }
        }

        if (classname!=null)
        {
            if(!classname.equals(obj.getClassname()))
            {
                return false;
            }
        }
        else
        {
            if (obj.getClassname()!=null)
            {
                return false;
            }
        }

        return true;
    }

    /** @return the name of the tool */
    public String getName()
    {
        return this.name;
    }

    /** Sets the name for this tool
     * @param title the new name of the tool
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /** @return the scope of this tool */
    public String getScope()
    {
        return this.scope;
    }

    /** Sets the scope of this tool.
     * The currently recognized scope are "request", "session", "persistent", "global"
     * @param scope the new scope of this tool
     */
    public void setScope( String scope )
    {
        this.scope = scope;
    }

    /** @return the clasname of this tool */
    public String getClassname()
    {
        return this.classname;
    }

    /** Sets the classname of this tool
     * @param classname the new classname of this tool
     */
    public void setClassname( String classname )
    {
        this.classname = classname;
    }
}
