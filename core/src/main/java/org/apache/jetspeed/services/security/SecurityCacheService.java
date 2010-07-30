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
import org.apache.turbine.services.Service;

/**
 * The Security Cache Service caches roles and permissions (ACLs)
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SecurityCacheService.java,v 1.8 2004/02/23 03:58:11 jford Exp $
 */


public interface SecurityCacheService extends Service
{
   /** The name of this service */
   public String SERVICE_NAME = "SecurityCache";

   /*
    * Loads the security cache for the given user.
    *
    * @param username the user to cache all role and permission information for.
    */
    public void load(String username)
        throws JetspeedSecurityException;

    /*
     * UnLoads the security cache for the given user.
     *
     * @param username the user to cache all role and permission information for.
     */
    public void unload(String username);


    public void loadRolePermissions();

    /**
     *  Retrieves a role from the cache for the given username.
     *
     * @param username The name key of the user.
     * @param roleName The name of the role.
     */ 
    public Role getRole(String username, String roleName);
    
	public Role getRole(String username, String roleName, String groupName);
	
    /**
     *  Retrieves a role from the cache for the given username.
     *
     */ 
    public void addRole(Role role);

    public void addRole(String username, Role role);
    
	public void addRole(String username, Role role, Group group);

    public boolean hasRole(String username, String roleName);
    
	public boolean hasRole(String username, String roleName, String groupName);

    public void removeRole(String username, String roleName);
    
	public void removeRole(String username, String roleName, String groupName);

    public Iterator getRoles(String username);

    public CachedAcl getAcl(String username);

    public Permission getPermission(String roleName, String permissionName);
    
    public void addPermission(String roleName, Permission permission);

    public boolean hasPermission(String roleName, String permissionName);

    public void removePermission(String roleName, String permissionName);

    public Iterator getPermissions(String roleName);

    public void removeAllRoles(String rolename);

    public void removeAllPermissions(String permissionName);

}



