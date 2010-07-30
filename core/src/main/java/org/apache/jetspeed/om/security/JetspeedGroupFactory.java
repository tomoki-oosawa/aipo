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

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;

import org.apache.jetspeed.services.security.GroupException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;

/**
 * Factory class for creating Jetspeed Groups.
 * The group class is configured in the JR.p
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedGroupFactory.java,v 1.4 2004/02/23 03:14:12 jford Exp $  
 */
public class JetspeedGroupFactory
{
    private static final String CONFIG_GROUP_CLASSNAME = "group.class";

    private static String groupClassName = null;
    private static Class groupClass = null;
    
    /**
     * Factory method to create JetspeedGroup instances.  
     *
     *
     * @throws UnknownEntityException when the group instance cant be created.
     * @return Group a new created group.
     */
    public static Group getInstance()
        throws GroupException
    {
        return getInstance(true);
    }

    public static Group getInstance(boolean isNew)
        throws GroupException
    {
        Group group = null;

        if (null == groupClassName)
        {
            try
            {
                ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                         .getResources(JetspeedSecurityService.SERVICE_NAME);
                groupClassName = serviceConf.getString(CONFIG_GROUP_CLASSNAME);                                                             
                groupClass = Class.forName(groupClassName);
            }
            catch(Exception e)
            {
                throw new GroupException(
                    "GroupFactory: Failed to create a Class object for Group implementation: " + e.toString());
            }
        }

        try
        {
            group = (Group)groupClass.newInstance();
            if (group instanceof BaseJetspeedGroup)
            {
                ((BaseJetspeedGroup)group).setNew(isNew);
            }            
        }
        catch(Exception e)
        {
            throw new GroupException("Failed instantiate an Group implementation object: " + e.toString());
        }

        return group;
    }
    

}



