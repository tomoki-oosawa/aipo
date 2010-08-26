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

package org.apache.jetspeed.om.registry.base;

// Java imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.jetspeed.om.registry.SecurityAccess;
import org.apache.jetspeed.om.registry.SecurityAllow;
import org.apache.jetspeed.om.registry.SecurityEntry;
import org.apache.jetspeed.services.security.GroupManagement;
import org.apache.jetspeed.services.security.RoleManagement;

/**
 * Interface for manipulatin the Security Entry on the registry entries
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: BaseSecurityEntry.java,v 1.15 2004/03/23 21:15:24 jford Exp $
 */
public class BaseSecurityEntry extends BaseRegistryEntry implements SecurityEntry, java.io.Serializable
{

    /** Holds value of property accesses. */
    private Vector accesses = new Vector();

    private transient Map accessMap = null;

    public static final String ALL_ACTIONS = "*";

    public static final String ALL_ROLES = "*";

	public static final String ALL_GROUPS = "*";    

	public static final String ALL_GROUP_ROLES = "*";	

    public static final String ALL_USERS = "*";

    private static final String OWNER_MAP = "owner";

    private static final String ROLE_MAP = "role";

	private static final String GROUP_MAP = "group";    
	
	private static final String GROUP_ROLE_MAP = "grouprole";	

    private static final String USER_MAP = "user";

    private static transient Object accessMapSync = new Object();

    public BaseSecurityEntry()
    { }

    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object == null)
        {
            return false;
        }

        BaseSecurityEntry obj = (BaseSecurityEntry) object;

        Iterator i = accesses.iterator();
        Iterator i2 = obj.accesses.iterator();
        while (i.hasNext())
        {
            BaseSecurityAccess c1 = (BaseSecurityAccess) i.next();
            BaseSecurityAccess c2 = null;

            if (i2.hasNext())
            {
                c2 = (BaseSecurityAccess) i2.next();
            }
            else
            {
                return false;
            }

            if (!c1.equals(c2))
            {
                return false;
            }
        }

        if (i2.hasNext())
        {
            return false;
        }

        return super.equals(object);
    }

    /** Getter for property accesses.
     * @return Value of property accesses.
     */
    public Vector getAccesses()
    {
        return accesses;
    }

    /** Setter for property accesses.
     * @param accesses New value of property accesses.
     */
    public void setAccesses(Vector accesses)
    {
        this.accesses = accesses;
        buildAccessMap();
    }

    /**
     * Aututhorizes action for a role.
     *
     * o If the requested action and the action ALL_ACTIONS
     *   do not exist, then return false.
     *
     * o If the requesting role and ALL_ROLES does not exist for the
     *   the action, then return false.
     *
     * @param role requesting action
     * @param action being requested
     * @return <CODE>true</CODE> if action is allowed for role
     */
    public boolean allowsRole(String role, String action)
    {
        Map allowMap = null;
        boolean allow = false;

        if (accessMap == null)
        {
            buildAccessMap();
        }

        // Checked action
        allowMap = (Map) accessMap.get(action);
        allow = isInAllowMap(allowMap, ROLE_MAP, role, ALL_ROLES);
        if (allow == true)
        {
            return allow;
        }

        // Checked all actions
        allowMap = (Map) accessMap.get(ALL_ACTIONS);
        allow = isInAllowMap(allowMap, ROLE_MAP, role, ALL_ROLES);

        // Not allowed
        return allow;
    }

	/**
	 * Aututhorizes action for a group.
	 *
	 * o If the requested action and the action ALL_ACTIONS
	 *   do not exist, then return false.
	 *
	 * o If the requesting role and ALL_GROUP does not exist for the
	 *   the action, then return false.
	 *
	 * @param group requesting action
	 * @param action being requested
	 * @return <CODE>true</CODE> if action is allowed for group
	 */
	public boolean allowsGroup(String group, String action)
	{
		Map allowMap = null;
		boolean allow = false;

		if (accessMap == null)
		{
			buildAccessMap();
		}

		// Checked action
		allowMap = (Map) accessMap.get(action);
		allow = isInAllowMap(allowMap, GROUP_MAP, group, ALL_GROUPS);
		if (allow == true)
		{
			return allow;
		}

		// Checked all actions
		allowMap = (Map) accessMap.get(ALL_ACTIONS);
		allow = isInAllowMap(allowMap, GROUP_MAP, group, ALL_GROUPS);

		// Not allowed
		return allow;
	}

	/**
	 * Authorizes action for a group role.
	 *
	 * o If the requested action and the action ALL_ACTIONS
	 *   do not exist, then return false.
	 *
	 * o If the requesting group role and ALL_GROUPS_ROLES does not exist for the
	 *   the action, then return false.
	 *
	 * @param group requesting action
	 * @param role requesting action 
	 * @param action being requested
	 * @return <CODE>true</CODE> if action is allowed for group role
	 */
	public boolean allowsGroupRole(String group, String role, String action)
	{
		Map allowMap = null;
		boolean allow = false;

		if (accessMap == null)
		{
			buildAccessMap();
		}

		// Checked action
		allowMap = (Map) accessMap.get(action);
		allow = isInAllowMap(allowMap, GROUP_ROLE_MAP, group+role, ALL_GROUP_ROLES);
		if (allow == true)
		{
			return allow;
		}

		// Checked all actions
		allowMap = (Map) accessMap.get(ALL_ACTIONS);
		allow = isInAllowMap(allowMap, GROUP_ROLE_MAP, group+role, ALL_GROUP_ROLES);

		// Not allowed
		return allow;
	}

    /**
     * Aututhorizes action for a named user
     *
     * @param userName requesting action
     * @param action being requested
     * @return <CODE>true</CODE> if action is allowed for named user
     */
    public boolean allowsUser(String userName, String action)
    {
        return allowsUser(userName, action, null);
    }
    /**
     * Aututhorizes action for a named user
     *
     * @param userName requesting action
     * @param action being requested
     * @param owner User
     * @return <CODE>true</CODE> if action is allowed for named user
     */
    public boolean allowsUser(String userName, String action, String owner)
    {
        Map allowMap = null;
        boolean allow = false;

        if (accessMap == null)
        {
            buildAccessMap();
        }
        if ((owner != null) && (owner.equals(userName)))
        {
            // Checked action
            allowMap = (Map) accessMap.get(action);
            allow = isInAllowMap(allowMap, OWNER_MAP, null, null);
            if (allow == true)
            {
                return allow;
            }

            // Checked action
            allowMap = (Map) accessMap.get(ALL_ACTIONS);
            allow = isInAllowMap(allowMap, OWNER_MAP, null, null);
            if (allow == true)
            {
                return allow;
            }
        }

        // Checked action
        allowMap = (Map) accessMap.get(action);
        allow = isInAllowMap(allowMap, USER_MAP, userName, ALL_USERS);
        if (allow == true)
        {
            return allow;
        }

        // Checked all actions
        allowMap = (Map) accessMap.get(ALL_ACTIONS);
        allow = isInAllowMap(allowMap, USER_MAP, userName, ALL_USERS);

        // Not allowed
        return allow;

    }
    
    /**
     * Checks whether a role is specifically allowed to access the request action
     * This method ignores the "*" action and is here to play a maintenance role.
     */
    public boolean allowsSpecificRole( String action, String role)
    {
        SecurityAccess access = (SecurityAccess) getAccess(action);
        if (access.getAllAllows() != null)
        {
            Iterator allAllows = access.getAllows().iterator();
            while (allAllows.hasNext())
            {
                SecurityAllow allow = (SecurityAllow) allAllows.next();
                if (allow.getRole() != null && allow.getRole().equals(role))
                {
                    return true;
                }
            }
        }
        return false;
    }

	/**
	 * Checks whether a group is specifically allowed to access the request action
	 * This method ignores the "*" action and is here to play a maintenance role.
	 */
	public boolean allowsSpecificGroup(String action, String group)
	{
		SecurityAccess access = (SecurityAccess) getAccess(action);
		if (access.getAllAllows() != null)
		{
			Iterator allAllows = access.getAllows().iterator();
			while (allAllows.hasNext())
			{
				SecurityAllow allow = (SecurityAllow) allAllows.next();
				if (allow.getGroup() != null && allow.getGroup().equals(group))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether a group role is specifically allowed to access the request action
	 * This method ignores the "*" action and is here to play a maintenance role.
	 */
	public boolean allowsSpecificGroupRole(String action, String group, String role)
	{
		SecurityAccess access = (SecurityAccess) getAccess(action);
		if (access.getAllAllows() != null)
		{
			Iterator allAllows = access.getAllows().iterator();
			while (allAllows.hasNext())
			{
				SecurityAllow allow = (SecurityAllow) allAllows.next();
				if (allow.getGroup() != null && 
					allow.getGroup().equals(group) &&
					allow.getRole() != null &&
					allow.getRole().equals(role))
				{
					return true;
				}
			}
		}
		return false;
	}
    
        /**
        * Checks whether a role is specifically allowed to access the request action
        * This method ignores the "*" action and is here to play a maintenance role.
        * @param String action name of action to check
        * @param String role name of role to verify access for
        * @return boolean whether or not the <code>role</code> has access
        * to this specific action.
        */
    public boolean allowsSpecificUser(String action, String user)
    {
        BaseSecurityAccess access = (BaseSecurityAccess) getAccess(action);
        if (access.getAllAllows() != null)
        {
            Iterator allAllows = access.getAllows().iterator();
            while (allAllows.hasNext())
            {
                BaseSecurityAllow allow = (BaseSecurityAllow) allAllows.next();
                if (allow.getUser() != null && allow.getUser().equals(user))
                {
                    return true;
                }
            }
        }
        return false;
    }

    
    
    
    /**
     * Returns the SecurityAccess object for the <code>action</code>
     * requested or null if no specific access is defined for this action.
     * The "*" does change this, if an action is not specifically defined
     * in the registry, null is returned
     * @param SecurityEntry entry SecurityEntry to check against
     * @param String action The action we want the access for.
     * @return SecurityAccess that is defined for this action or
     * <code>null</code> if one is not <strong>specifically defined</strong>
     */
    public SecurityAccess getAccess(String action)
    {
        Iterator itr = getAccesses().iterator();
        while (itr.hasNext())
        {
            BaseSecurityAccess access = (BaseSecurityAccess) itr.next();
            if (access.getAction().equals(action))
            {
                return access;
            }
        }

        return null;
    }
    
    /**
     * Grants access for a specific action to a specific role
     * for this SecurityEntry.  This grants specific access ignores
     * "*" action, if it exists.
     * @param String action The action we are granting access to.
     * @param String role The role that will receive access to this action.
     * @return boolean Whether or not the access was granted. Basically,
     *  a <code>false</code> means that this role already has specific access.
     */
    public boolean grantRoleAccess(String action, String role)
    {
        if (!allowsSpecificRole(action, role))
        {
            SecurityAccess access = getAccess(action);
            List allows = access.getAllows();
            if (allows == null)
            {
                allows = new Vector();
            }

            BaseSecurityAllow allow = new BaseSecurityAllow();
            allow.setRole(role);
            allows.add(allow);
            
            buildAccessMap();
            
            return true;
        }

        return false;
    }

	/**
	 * Grants access for a specific action to a specific group
	 * for this SecurityEntry.  This grants specific access ignores
	 * "*" action, if it exists.
	 * @param String action The action we are granting access to.
	 * @param String group The group that will receive access to this action.
	 * @return boolean Whether or not the access was granted. Basically,
	 *  a <code>false</code> means that this group already has specific access.
	 */
	public boolean grantGroupAccess(String action, String group)
	{
		if (!allowsSpecificGroup(action, role))
		{
			SecurityAccess access = getAccess(action);
			List allows = access.getAllows();
			if (allows == null)
			{
				allows = new Vector();
			}

			BaseSecurityAllow allow = new BaseSecurityAllow();
			allow.setGroup(group);
			allows.add(allow);
            
			buildAccessMap();
            
			return true;
		}

		return false;
	}

	/**
	 * Grants access for a specific action to a specific group fole
	 * for this SecurityEntry.  This grants specific access ignores
	 * "*" action, if it exists.
	 * @param String action The action we are granting access to.
	 * @param String group The group that will receive access to this action.
	 * @param String role The role that will receive access to this action. 
	 * @return boolean Whether or not the access was granted. Basically,
	 *  a <code>false</code> means that this group role already has specific access.
	 */
	public boolean grantGroupRoleAccess(String action, String group, String role)
	{
		if (!allowsSpecificGroupRole(action, group, role))
		{
			SecurityAccess access = getAccess(action);
			List allows = access.getAllows();
			if (allows == null)
			{
				allows = new Vector();
			}

			BaseSecurityAllow allow = new BaseSecurityAllow();
			allow.setGroup(group);
			allow.setRole(role);
			allows.add(allow);
            
			buildAccessMap();
            
			return true;
		}

		return false;
	}
    
    /**
     * Grants access for a specific action to a specific user
     * for this SecurityEntry.  This grants specific access ignores
     * "*" action, if it exists.
     * @param String action The action we are granting access to.
     * @param String user The user that will receive access to this action.
     * @return boolean Whether or not the access was granted. Basically,
     *  a <code>false</code> means that this role already has specific access.
     */
    public boolean grantUserAccess(String action, String user)
    {
        if (!allowsSpecificUser(action, user))
        {
            SecurityAccess access = getAccess(action);
            List allows = access.getAllows();
            if (allows == null)
            {
                allows = new Vector();
            }

            BaseSecurityAllow allow = new BaseSecurityAllow();
            allow.setUser(user);
            allows.add(allow);
            
            buildAccessMap();
            
            return true;
        }

        return false;
    }


    /**
     * Removes a role's access to a specific action.
     * @param action Action to remove access from.
     * @param role The role whose access we are revoking.
     * @return boolean Whehter or not the access existed and
     * was removed.
     */
    public boolean revokeRoleAccess(String action, String role)
    {
        if (allowsSpecificRole(action, role))
        {
            SecurityAccess access = getAccess(action);
            List allows = access.getAllows();
            if (allows == null || allows.isEmpty())
            {
                revokeAccess(action);
                return false;
            }

            for (int i = 0; i < allows.size(); i++)
            {
                BaseSecurityAllow allow = (BaseSecurityAllow) allows.get(i);
                if (allow.getRole() != null && allow.getRole().equals(role))
                {
                    allows.remove(i);
                    if (allows.isEmpty() && access.getOwnerAllows().isEmpty())
                    {
                        revokeAccess(action);
                    }

                    return true;
                }
            }
        }
        return false;
    }
    
	/**
	 * Removes a group's access to a specific action.
	 * @param action Action to remove access from.
	 * @param group The group whose access we are revoking.
	 * @return boolean Whehter or not the access existed and
	 * was removed.
	 */
	public boolean revokeGroupAccess(String action, String group)
	{
		if (allowsSpecificGroup(action, group))
		{
			SecurityAccess access = getAccess(action);
			List allows = access.getAllows();
			if (allows == null || allows.isEmpty())
			{
				revokeAccess(action);
				return false;
			}

			for (int i = 0; i < allows.size(); i++)
			{
				BaseSecurityAllow allow = (BaseSecurityAllow) allows.get(i);
				if (allow.getGroup() != null && allow.getGroup().equals(group))
				{
					allows.remove(i);
					if (allows.isEmpty() && access.getOwnerAllows().isEmpty())
					{
						revokeAccess(action);
					}

					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes a group role's access to a specific action.
	 * @param action Action to remove access from.
	 * @param group The group whose access we are revoking.
	 * @param role The role whose access we are revoking. 
	 * @return boolean Whether or not the access existed and
	 * was removed.
	 */
	public boolean revokeGroupRoleAccess(String action, String group, String role)
	{
		if (allowsSpecificGroupRole(action, group, role))
		{
			SecurityAccess access = getAccess(action);
			List allows = access.getAllows();
			if (allows == null || allows.isEmpty())
			{
				revokeAccess(action);
				return false;
			}

			for (int i = 0; i < allows.size(); i++)
			{
				BaseSecurityAllow allow = (BaseSecurityAllow) allows.get(i);
				if (allow.getGroup() != null && 
					allow.getGroup().equals(group) &&
					allow.getRole() != null &&
					allow.getRole().equals(role))
				{
					allows.remove(i);
					if (allows.isEmpty() && access.getOwnerAllows().isEmpty())
					{
						revokeAccess(action);
					}

					return true;
				}
			}
		}
		return false;
	}
    
    /**
    * Removes a user's access to a specific action.
    * @param action Action to remove access from.
    * @param role The role whose access we are revoking.
    * @return boolean Whehter or not the access existed and
    * was removed.
    */
    public boolean revokeUserAccess(String action, String user)
    {
        if (allowsSpecificUser(action, user))
        {
            SecurityAccess access = getAccess(action);
            List allows = access.getAllows();
            if (allows == null || allows.isEmpty())
            {
                revokeAccess(action);
                return false;
            }

            for (int i = 0; i < allows.size(); i++)
            {
                BaseSecurityAllow allow = (BaseSecurityAllow) allows.get(i);
                if (allow.getUser() != null && allow.getUser().equals(user))
                {
                    allows.remove(i);
                    if (allows.isEmpty() && access.getOwnerAllows().isEmpty())
                    {
                        revokeAccess(action);
                    }

                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Removes a security access for the named action.
     * This does not take into account the "*" action when
     * the "*" is not the named action.
     * @param String access name of access to remove in its entirety
     */
    public void revokeAccess(String action)
    {
        List list = getAccesses();
        for (int i = 0; i < list.size(); i++)
        {
            BaseSecurityAccess access = (BaseSecurityAccess) list.get(i);
            if (access.getAction().equals(action))
            {
                list.remove(i);
                return;
            }
        }
    }
    
    
    
    
    

    private void buildAccessMap()
    {
        Map actionMap = null;
        SecurityAccess accessElement = null;

        synchronized (accessMapSync)
        {
            if (accessMap == null)
            {
                accessMap = new HashMap();
            }

            accessMap.clear();
        }
        // Build allow map
        for (Iterator accessIterator = getAccesses().iterator(); accessIterator.hasNext();)
        {
            accessElement = (SecurityAccess) accessIterator.next();

            // Get action map of the action.  Create one if none exists
            String action = accessElement.getAction();

            if (action == null)
            {
                action = ALL_ACTIONS;
            }

            actionMap = (Map) accessMap.get(action);
            if (actionMap == null)
            {
                actionMap = new HashMap();
                accessMap.put(action, actionMap);
            }
            addAllows(actionMap, accessElement);
        }
    }

    /**
     * Add access elements to the access map.  The elements will be
     * appened to the appropiate map.
     *
     * @param accessMap to receive accessElements
     * @param accessElement to copy to access map
     */
    private void addAllows(Map accessMap, SecurityAccess accessElement)
    {
        SecurityAllow allowElement = null;
        String role = null;
        String group = null;
        Map ownerMap = null; // Map of owner allowed
        Map roleMap = null;  // Map of roles allowed
		Map groupMap = null;  // Map of groups allowed        
		Map groupRoleMap = null;  // Map of group role allowed		
        Map userMap = null;  // Map of users allowed
        String userName = null;

        if (accessElement.getAllAllows() == null)
        {
            return;
        }

        // Add allows to the action Map
        for (Iterator allowIterator = accessElement.getAllAllows().iterator(); allowIterator.hasNext();)
        {
            allowElement = (SecurityAllow) allowIterator.next();
            role = null;
            userName = null;
			group = null;

            // Add Owner
            if (allowElement.isOwner() == true)
            {
                ownerMap = (Map) accessMap.get(OWNER_MAP);
                if (ownerMap == null)
                {
                    ownerMap = new HashMap();
                    accessMap.put(OWNER_MAP, ownerMap);
                }
                ownerMap.put(null, null);
            }

            // Add Role
            role = allowElement.getRole();
            if (role != null)
            {
            	// Role map
                roleMap = (Map) accessMap.get(ROLE_MAP);
                if (roleMap == null)
                {
                    roleMap = new HashMap();
                    accessMap.put(ROLE_MAP, roleMap);
                }
                roleMap.put(role, null);
				
				// Group role map                
				groupRoleMap = (Map) accessMap.get(GROUP_ROLE_MAP);
				if (groupRoleMap == null)
				{
					groupRoleMap = new HashMap();
					accessMap.put(GROUP_ROLE_MAP, groupRoleMap);
				}
				if (group == null)
				{
					group = GroupManagement.DEFAULT_GROUP_NAME;
				}
				groupRoleMap.put(group+role, null);
                
            }

			// Add Group
			group = allowElement.getGroup();
			if (group != null)
			{
				// Group map
				groupMap = (Map) accessMap.get(GROUP_MAP);
				if (groupMap == null)
				{
					groupMap = new HashMap();
					accessMap.put(GROUP_MAP, groupMap);
				}
				groupMap.put(group, null);
				
				// Group role map                
				groupRoleMap = (Map) accessMap.get(GROUP_ROLE_MAP);
				if (groupRoleMap == null)
				{
					groupRoleMap = new HashMap();
					accessMap.put(GROUP_ROLE_MAP, groupRoleMap);
				}
				if (role == null)
				{
					role = RoleManagement.DEFAULT_ROLE_NAME;
				}
				groupRoleMap.put(group+role, null);
				
			}

            // Add User
            userName = allowElement.getUser();
            if (userName != null)
            {
                userMap = (Map) accessMap.get(USER_MAP);
                if (userMap == null)
                {
                    userMap = new HashMap();
                    accessMap.put(USER_MAP, userMap);
                }
                userMap.put(userName, null);
            }
        }
    }

    /**
     * Search allow map of user/role or "all user/role"
     *
     * @param allowMap Map of allow-if
     * @param mapType ROLE_MAP or USER_MAP or GROUP_MAP or GROUP_ROLE_MAP
     * @param mapKey role or user to test
     * @param allKey ALL_ROLE or ALL_USER or ALL_GROUP or ALL_GROUP_ROLE
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    private boolean isInAllowMap(Map allowMap, String mapType, String mapKey, String allKey)
    {
        boolean allow = false;
        if (allowMap != null)
        {
            Map allowTypeMap = (Map) allowMap.get(mapType);
            if (allowTypeMap == null)
            {
                return allowMap.isEmpty(); // If action exist and no allows, then grant permission
            }
            allow = allowTypeMap.containsKey(mapKey);
            if (allow == false)
            {
              allow = allowTypeMap.containsKey(allKey);
            }
            return allow;
        }

        // Not allowed
        return allow;
    }
 }

