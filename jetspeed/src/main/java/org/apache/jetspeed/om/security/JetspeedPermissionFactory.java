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

import org.apache.jetspeed.services.security.PermissionException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;

/**
 * Factory class for creating Jetspeed Permissions.
 * The permission class is configured in the JR.p
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedPermissionFactory.java,v 1.4 2004/02/23 03:14:12 jford Exp $  
 */
public class JetspeedPermissionFactory
{
    private static final String CONFIG_GROUP_CLASSNAME = "permission.class";

    private static String permissionClassName = null;
    private static Class permissionClass = null;
    
    /**
     * Factory method to create JetspeedPermission instances.  
     *
     *
     * @throws UnknownEntityException when the permission instance cant be created.
     * @return Permission a new created permission.
     */
    public static Permission getInstance()
        throws PermissionException
    {
        return getInstance(true);
    }

    public static Permission getInstance(boolean isNew)
        throws PermissionException
    {
        Permission permission = null;

        if (null == permissionClassName)
        {
            try
            {
                ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                         .getResources(JetspeedSecurityService.SERVICE_NAME);
                permissionClassName = serviceConf.getString(CONFIG_GROUP_CLASSNAME);                                                             
                permissionClass = Class.forName(permissionClassName);
            }
            catch(Exception e)
            {
                throw new PermissionException(
                    "PermissionFactory: Failed to create a Class object for Permission implementation: " + e.toString());
            }
        }

        try
        {
            permission = (Permission)permissionClass.newInstance();
            if (permission instanceof BaseJetspeedPermission)
            {
                ((BaseJetspeedPermission)permission).setNew(isNew);
            }            
        }
        catch(Exception e)
        {
            throw new PermissionException("Failed instantiate an Permission implementation object: " + e.toString());
        }

        return permission;
    }
    

}




