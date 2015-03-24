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

import org.apache.jetspeed.services.security.RoleException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;

/**
 * Factory class for creating Jetspeed Roles.
 * The role class is configured in the JR.p
 *
 */
public class JetspeedRoleFactory
{
    private static final String CONFIG_ROLE_CLASSNAME = "role.class";

    private static String roleClassName = null;
    private static Class roleClass = null;
    
    /**
     * Factory method to create JetspeedRole instances.  
     *
     *
     * @throws UnknownEntityException when the role instance cant be created.
     * @return Role a new created role.
     */
    public static Role getInstance()
        throws RoleException
    {
        return getInstance(true);
    }

    public static Role getInstance(boolean isNew)
        throws RoleException
    {
        Role role = null;

        if (null == roleClassName)
        {
            try
            {
                ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                         .getResources(JetspeedSecurityService.SERVICE_NAME);
                roleClassName = serviceConf.getString(CONFIG_ROLE_CLASSNAME);                                                             
                roleClass = Class.forName(roleClassName);
            }
            catch(Exception e)
            {
                throw new RoleException(
                    "RoleFactory: Failed to create a Class object for Role implementation: " + e.toString());
            }
        }

        try
        {
            role = (Role)roleClass.newInstance();
            if (role instanceof BaseJetspeedRole)
            {
                ((BaseJetspeedRole)role).setNew(isNew);
            }            
        }
        catch(Exception e)
        {
            throw new RoleException("Failed instantiate an Role implementation object: " + e.toString());
        }

        return role;
    }
    

}


