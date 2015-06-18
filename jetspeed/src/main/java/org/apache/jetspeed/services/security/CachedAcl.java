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

import org.apache.jetspeed.om.security.BaseJetspeedGroup;
import org.apache.jetspeed.om.security.BaseJetspeedGroupRole;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.GroupRole;
import org.apache.jetspeed.om.security.Role;

/**
 * Cached ACL - default implementation cached ACL containing role/permission.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: CachedAcl.java,v 1.5 2004/02/23 03:58:11 jford Exp $
 */
public class CachedAcl
{
	protected HashMap groupRoles = new HashMap();
	protected String userName;

	/**
	 * @param userName
	 */
	public CachedAcl(String userName)
	{
		this.userName = userName;
	}

	/**
	 * @param role
	 */
	public void addRole(Role role)
	{
		Group group = new BaseJetspeedGroup();
		group.setName(GroupManagement.DEFAULT_GROUP_NAME);
		addRole(role, group);
	}

	/**
	 * @param role
	 * @param group
	 */
	public void addRole(Role role, Group group)
	{
		GroupRole gr = new BaseJetspeedGroupRole();
		gr.setRole(role);
		gr.setGroup(group);
		String key = getGroupRoleKey(group.getName(), role.getName());
		groupRoles.put(key, gr);
	}

	/**
	 * @param roleName
	 * @return
	 */
	public Role getRole(String roleName)
	{
		return getRole(roleName, GroupManagement.DEFAULT_GROUP_NAME);
	}

	/**
	 * @param roleName
	 * @param groupName
	 * @return
	 */
	public Role getRole(String roleName, String groupName)
	{
		GroupRole gr = (GroupRole) groupRoles.get(getGroupRoleKey(groupName, roleName));
		return gr != null ? gr.getRole() : null;
	}

	/**
	 * @return
	 */
	public Iterator getRoles()
	{
		return groupRoles.values().iterator();
	}

	/**
	 * @return
	 */
	public String getUserName()
	{
		return this.userName;
	}

	/**
	 * @param roleName
	 * @return
	 */
	public boolean hasRole(String roleName)
	{
		return hasRole(roleName, GroupManagement.DEFAULT_GROUP_NAME);
	}

	/**
	 * @param roleName
	 * @param groupName
	 * @return
	 */
	public boolean hasRole(String roleName, String groupName)
	{
		return groupRoles.containsKey(getGroupRoleKey(groupName, roleName));
	}

	/**
	 * @param roleName
	 */
	public void removeRole(String roleName)
	{
		removeRole(roleName, GroupManagement.DEFAULT_GROUP_NAME);
	}
	
	/**
	 * @param roleName
	 * @param groupName
	 */
	public void removeRole(String roleName, String groupName)
	{
		groupRoles.remove(getGroupRoleKey(groupName, roleName));
	}

	/**
	 * @param grouproles
	 */
	public void setRoles(Iterator grouproles)
	{
		while (grouproles.hasNext())
		{
			GroupRole grouprole = (GroupRole) grouproles.next();
			String key = getGroupRoleKey(grouprole.getGroup().getName(), grouprole.getRole().getName());
			this.groupRoles.put(key, grouprole);
		}
	}

	/**
	 * @param groupName
	 * @param roleName
	 * @return
	 */
	private String getGroupRoleKey(String groupName, String roleName)
	{
		StringBuffer key = new StringBuffer();
		key.append(groupName);
		key.append(roleName);
		
		return key.toString();
	}

}
