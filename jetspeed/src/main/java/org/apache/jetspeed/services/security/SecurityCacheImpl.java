/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.Permission;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;

/**
 * The Security Cache Service caches roles and permissions (ACLs)
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: SecurityCacheImpl.java,v 1.10 2004/02/23 03:58:11 jford Exp $
 */


public class SecurityCacheImpl  extends TurbineBaseService
                                implements SecurityCacheService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(SecurityCacheImpl.class.getName());
    
    protected Map acls = new HashMap();
    protected Map perms = new HashMap();

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
    * Load the security cache for the given user's roles and permissions.
    *
    * @param JetspeedUser the user to cache all role and permission information for.
    */
    public void load(String username)
        throws JetspeedSecurityException
    {                
        CachedAcl acl = new CachedAcl(username);
        acl.setRoles(JetspeedSecurity.getRoles(username));
        acls.put(username, acl);
        if (perms.size() == 0)
        {
            loadRolePermissions();
        }
    }

    public void unload(String username)
    {
        acls.remove(username);
    }

    public Role getRole(String roleName)
    {
        return (Role)perms.get(roleName);
    }

    public Role getRole(String username, String roleName)
    {
        CachedAcl acl = (CachedAcl)acls.get(username);
        if (acl == null)
        {
            return null;
        }
        return acl.getRole(roleName);
    }
    
	public Role getRole(String username, String roleName, String groupName)
	{
		CachedAcl acl = (CachedAcl) acls.get(username);
		if (acl == null)
		{
			return null;
		}
		return acl.getRole(roleName, groupName);
	}
    
    public void addRole(Role role)
    {
        if (!perms.containsKey(role.getName()))
        {
            perms.put(role.getName(), new HashMap());
        }        
    }

    public void addRole(String username, Role role)
    {
        CachedAcl acl = (CachedAcl)acls.get(username);
        if (null != acl)
        {
            acl.addRole(role);
        }
        if (!perms.containsKey(role.getName()))
        {
            perms.put(role.getName(), new HashMap());
        }        
    }

	public void addRole(String username, Role role, Group group)
	{
		CachedAcl acl = (CachedAcl) acls.get(username);
		if (null != acl)
		{
			acl.addRole(role, group);
		}
		if (!perms.containsKey(role.getName()))
		{
			perms.put(role.getName(), new HashMap());
		}
	}

	public boolean hasRole(String username, String roleName)
	{
		return hasRole(username, roleName, GroupManagement.DEFAULT_GROUP_NAME);
	}

	public boolean hasRole(String username, String roleName, String groupName)
	{
		CachedAcl acl = (CachedAcl) acls.get(username);
		if (null != acl)
		{
			return acl.hasRole(roleName, groupName);
		}
		return false;
	}

	public void removeRole(String username, String roleName)
	{
		removeRole(username, roleName, GroupManagement.DEFAULT_GROUP_NAME);
	}

	public void removeRole(String username, String roleName, String groupName)
	{
		CachedAcl acl = (CachedAcl) acls.get(username);
		if (null != acl)
		{
			acl.removeRole(roleName, groupName);
		}
		// TODO: Why do this?
		perms.remove(roleName);
	}

    public CachedAcl getAcl(String username)
    {
        return (CachedAcl)acls.get(username);
    }


    public Iterator getRoles(String username)
    {
        CachedAcl acl = (CachedAcl)acls.get(username);
        if (null != acl)
        {
            return acl.getRoles();
        }
        return null;        
    }

    public Permission getPermission(String roleName, String permissionName)
    {        
        Map map = (Map)perms.get(roleName);
        if (null != map)
        {
            return (Permission)map.get(permissionName);
        }
        return null;        
    }
    
    public void addPermission(String roleName, Permission permission)
    {
        Map map = (Map)perms.get(roleName);
        if (null != map)
        {
            map.put(permission.getName(), permission);
        }
    }

    public boolean hasPermission(String roleName, String permissionName)
    {
        Map map = (Map)perms.get(roleName);
        if (null != map)
        {
            return map.containsKey(permissionName);
        }
        return false;
    }

    public void removePermission(String roleName, String permissionName)
    {
        Map map = (Map)perms.get(roleName);
        if (null != map)
        {
            map.remove(permissionName);
        }
    }

    public Iterator getPermissions(String roleName)
    {
        Map map = (Map)perms.get(roleName);
        if (map != null)
        {
            return map.values().iterator();
        }
        return null;
    }


    public void removeAllRoles(String rolename)
    {
        Iterator iterator = acls.values().iterator();
        while (iterator.hasNext())
        {
            CachedAcl acl = (CachedAcl)iterator.next();
            acl.removeRole(rolename);
        }
        perms.remove(rolename);
    }

    public void removeAllPermissions(String permissionName)
    {
        Iterator iterator = perms.values().iterator();
        while (iterator.hasNext())
        {
            Map map = (Map)iterator.next();
            map.remove(permissionName);
        }
    }

    public void loadRolePermissions()
    {
        try
        {
            Iterator roles = JetspeedSecurity.getRoles();
            while (roles.hasNext())
            {
                Role role = (Role)roles.next();
                Map map = new HashMap();
                Iterator prms = JetspeedSecurity.getPermissions(role.getName());
                while (prms.hasNext())
                {
                    Permission perm = (Permission)prms.next();
                    map.put(perm.getName(), perm);
                }
                perms.put(role.getName(), map);
            }
        }
        catch (JetspeedSecurityException e)
        {
            logger.error("Exception", e);
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // Service Init
    ///////////////////////////////////////////////////////////////////////////


    /**
     * This is the early initialization method called by the 
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public synchronized void init(ServletConfig conf) 
        throws InitializationException 
    {
        if (getInit()) return;

        super.init(conf);

        setInit(true);
     }

}


