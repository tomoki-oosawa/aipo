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

package org.apache.jetspeed.services.security.nosecurity;

import java.util.Iterator;
import java.util.Vector;

// Jetspeed Security
import org.apache.jetspeed.services.security.RoleManagement;

import org.apache.jetspeed.om.security.Role;

import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.om.security.BaseJetspeedRole;

// Jetspeed Security Exceptions
import org.apache.jetspeed.services.security.JetspeedSecurityException;

// Turbine
import org.apache.turbine.services.TurbineBaseService;

/**
 * <p> The <code>NoRoleManagement</code> class is a Jetspeed
 * security provider, implementing the <code>RoleManagement</code> interface.
 * It provides no role management - only the "user" role exists for any user, no roles are
 * listed or saved, any role requested is supplied with a temp. Role object.
 *
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 * @version $Id: NoRoleManagement.java,v 1.3 2004/02/23 03:53:24 jford Exp $
 */
public class NoRoleManagement
    extends TurbineBaseService
   implements RoleManagement
{
    /**
     * Retrieves all <code>Role</code>s for a given username principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param username a user principal identity to be retrieved.
     * @return Iterator over all roles associated to the user principal.
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Iterator getRoles(String username)
        throws JetspeedSecurityException
    {
        // give everyone the "user" role
        Vector v = new Vector(1);
        BaseJetspeedRole r = new BaseJetspeedRole();
        //r.setNew(false);
        r.setName(JetspeedSecurity.JETSPEED_ROLE_USER);
        r.setId(JetspeedSecurity.JETSPEED_ROLE_USER);
        v.add(r);
        return v.iterator();
    }

    /**
     * Retrieves all <code>Role</code>s.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @return Iterator over all roles.
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Iterator getRoles()
        throws JetspeedSecurityException
    {
        return new Vector().iterator();
    }

    /**
     * Adds a <code>Role</code> into permanent storage.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void addRole(Role role)
        throws JetspeedSecurityException
    {
    }

    /**
     * Saves a <code>Role</code> into permanent storage.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void saveRole(Role role)
        throws JetspeedSecurityException
    {
    }

    /**
     * Removes a <code>Role</code> from the permanent store.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param rolename the principal identity of the role to be retrieved.
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void removeRole(String rolename)
        throws JetspeedSecurityException
    {
    }

    /**
     * Grants a role to a user.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure retrieving roles.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void grantRole(String username, String rolename)
        throws JetspeedSecurityException
    {
    }

	public void grantRole(String username, String rolename, String groupname)
		throws JetspeedSecurityException
	{
	}

    /**
     * Grants a role to a user for a specific group.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure retrieving roles.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void grantGroupRole(String username, String groupname, String rolename)
        throws JetspeedSecurityException
    {
    }

    /**
     * Revokes a role from a user.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure retrieving roles.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void revokeRole(String username, String rolename)
        throws JetspeedSecurityException
    {
    }

    /**
     * Revokes a role from a user for a specific group.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure retrieving roles.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public void revokeRole(String username, String rolename, String groupname)
        throws JetspeedSecurityException
    {
    }

    /**
     * Checks for the relationship of user has a role. Returns true when the user has the given role.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception RoleException when the security provider has a general failure retrieving roles.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public boolean hasRole(String username, String rolename)
        throws JetspeedSecurityException
    {
        // give everyone the "user" role
        if (rolename.equals(JetspeedSecurity.JETSPEED_ROLE_USER)) return true;

        return false;
    }

	public boolean hasRole(String username, String rolename, String groupname)
		throws JetspeedSecurityException
	{
		// give everyone the "user" role
		if (rolename.equals(JetspeedSecurity.JETSPEED_ROLE_USER) &&	
			groupname.equals(JetspeedSecurity.JETSPEED_GROUP)) return true;

		return false;
	}

    /**
     * Retrieves a single <code>Role</code> for a given rolename principal.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param rolename a role principal identity to be retrieved.
     * @return Role the role record retrieved.
     * @exception RoleException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
     */
    public Role getRole(String rolename)
        throws JetspeedSecurityException
    {
        BaseJetspeedRole r = new BaseJetspeedRole();
        //r.setNew(false);
        r.setName(rolename);
        r.setId(rolename);
        return r;
    }
}

