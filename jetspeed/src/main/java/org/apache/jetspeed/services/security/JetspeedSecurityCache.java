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


package org.apache.jetspeed.services.security;

import java.util.Iterator;

import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.Permission;
import org.apache.jetspeed.om.security.Role;
import org.apache.turbine.services.TurbineServices;

/**
 * The Security Cache Service caches roles and permissions (ACLs)
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedSecurityCache.java,v 1.8 2004/02/23 03:58:11 jford Exp $
 */


public abstract class JetspeedSecurityCache
{
   /** The name of this service */
   public String SERVICE_NAME = "SecurityCache";


   /*
    * Utility method for accessing the service
    * implementation
    *
    * @return a SecurityCacheService implementation instance
    */
   protected static SecurityCacheService getService()
   {
       return (SecurityCacheService)TurbineServices
       .getInstance().getService(SecurityCacheService.SERVICE_NAME);
   }

   /*
    * 
    * The class that is created by the default JetspeedUserFactory is configured
    * in the JetspeedSecurity properties:
    *
    *    services.JetspeedSecurity.user.class=
    *        org.apache.jetspeed.om.security.BaseJetspeedUser
    *
    * @param JetspeedUser the user to cache all role and permission information for.
    */
    public static void load(String username)
        throws JetspeedSecurityException
    {
        getService().load(username);
    }

    public static void unload(String username)
    {
        getService().unload(username);
    }

    public static void loadRolePermissions()
    {
        getService().loadRolePermissions();
    }

    public static Role getRole(String username, String roleName)
    {
        return getService().getRole(username, roleName);
    }

	public static Role getRole(String username, String roleName, String groupName)
	{
		return getService().getRole(username, roleName, groupName);
	}
    
    public static void addRole(Role role)
    {
        getService().addRole(role);
    }

    public static void addRole(String username, Role role)
    {
        getService().addRole(username, role);
    }

	public static void addRole(String username, Role role, Group group)
	{
		getService().addRole(username, role, group);
	}

    public static boolean hasRole(String username, String roleName)
    {
        return getService().hasRole(username, roleName);
    }

	public static boolean hasRole(String username, String roleName, String groupName)
	{
		return getService().hasRole(username, roleName, groupName);
	}

    public static void removeRole(String username, String roleName)
    {
        getService().removeRole(username, roleName);
    }

	public static void removeRole(String username, String roleName, String groupName)
	{
		getService().removeRole(username, roleName, groupName);
	}

    public static Iterator getRoles(String username)
    {
        return getService().getRoles(username);
    }

    public static CachedAcl getAcl(String username)
    {
        return getService().getAcl(username);
    }

    public static void removeAllRoles(String rolename)
    {
        getService().removeAllRoles(rolename);
    }

    public static void removeAllPermissions(String permissionName)
    {
        getService().removeAllPermissions(permissionName);
    }

    public static Permission getPermission(String roleName, String permissionName)
    {
        return getService().getPermission(roleName, permissionName);
    }
    
    public static void addPermission(String roleName, Permission permission)
    {
        getService().addPermission(roleName, permission);
    }

    public static boolean hasPermission(String roleName, String permissionName)
    {
        return getService().hasPermission(roleName, permissionName);
    }

    public static void removePermission(String roleName, String permissionName)
    {
        getService().removePermission(roleName, permissionName);
    }

    public static Iterator getPermissions(String roleName)
    {
        return getService().getPermissions(roleName);
    }

}



