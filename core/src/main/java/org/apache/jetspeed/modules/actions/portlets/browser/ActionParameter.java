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

package org.apache.jetspeed.modules.actions.portlets.browser;

import java.io.Serializable;

/**
 * Action Parameter
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ActionParameter.java,v 1.4 2004/02/23 02:51:19 jford Exp $
 *
*/
public class ActionParameter implements Serializable
{
    String name;
    String action;
    String type;
    String page;

    public ActionParameter(String name, String action, String type)
    {
        this.name = name;
        if(type.equalsIgnoreCase("psml"))
        {
            int index = action.indexOf("/");
            this.page= action.substring(0, index);
            this.action= action.substring(index+1);
        }
        else
        {
            this.action= action;
        }
        this.type = type;
    }

    public String getName()
    {
        return this.name;
    }
    public String getPage()
    {
        return this.page;
    }

    public String getAction()
    {
        return this.action;
    }
    public String getType()
    {
        return this.type;
    }
    public void setName(String name)
    {
        this.name =  name;
    }

    public void setAction(String action)
    {
        this.action = action;
    }
    public void setType(String type)
    {
        this.type = type;
    }
}


