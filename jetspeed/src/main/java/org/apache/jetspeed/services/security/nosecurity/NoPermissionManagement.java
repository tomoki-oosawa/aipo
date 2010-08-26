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

package org.apache.jetspeed.services.security.nosecurity;

import java.util.Iterator;
import java.util.Vector;

// Jetspeed Security
import org.apache.jetspeed.services.security.PermissionManagement;

import org.apache.jetspeed.om.security.Permission;

import org.apache.jetspeed.om.security.BaseJetspeedPermission;

// Jetspeed Security Exceptions
import org.apache.jetspeed.services.security.JetspeedSecurityException;

// Turbine
import org.apache.turbine.services.TurbineBaseService;

/**
 * <p> The <code>NoPermissionManagement</code> class is a Jetspeed
 * security provider, implementing the <code>PermissionManagement</code> interface.
 * It provides no permission management - no roles have permissions, no permissions are
 * saved, and requests for any permission is satisfied with a temp. Permission object.
 *
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 * @version $Id: NoPermissionManagement.java,v 1.3 2004/02/23 03:53:24 jford Exp $
 */
public class NoPermissionManagement
    extends TurbineBaseService
   implements PermissionManagement
{
   /**
     * Retrieves all <code>Permission</code>s for a given rolename principal.
     *   
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param rolename a role name identity to be retrieved.
     * @return Iterator over all permissions associated to the role principal.
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public Iterator getPermissions(String rolename)
        throws JetspeedSecurityException
    {
        return new Vector().iterator();
    }

    /**
     * Retrieves all <code>Permission</code>s.
     *   
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @return Iterator over all permissions.
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public Iterator getPermissions()
        throws JetspeedSecurityException
    {
        return new Vector().iterator();
    }

    /**
     * Adds a <code>Permission</code> into permanent storage. 
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public void addPermission(Permission permission)
        throws JetspeedSecurityException
    {
    }

    /**
     * Saves a <code>Permission</code> into permanent storage. 
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public void savePermission(Permission permission)
        throws JetspeedSecurityException
    {
    }

    /**
     * Removes a <code>Permission</code> from the permanent store.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param permissionName the principal identity of the permission to be retrieved.
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public void removePermission(String permissionName)
        throws JetspeedSecurityException
    {
    }

    /**
     * Grants a permission to a role. 
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param roleName grant a permission to this role.
     * @param permissionName the permission to grant to the role.
     * @exception PermissionException when the security provider has a general failure retrieving permissions.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public void grantPermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
    }

    /**
     * Revokes a permission from a role. 
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param roleName grant a permission to this role.
     * @param permissionName the permission to grant to the role.     
     * @exception PermissionException when the security provider has a general failure retrieving permissions.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public void revokePermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
    }

    /**
     * Checks for the relationship of role has a permission. Returns true when the role has the given permission.
     *
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param roleName grant a permission to this role.
     * @param permissionName the permission to grant to the role.    
     * @exception PermissionException when the security provider has a general failure retrieving permissions.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public boolean hasPermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
        return false;
    }

    /**
     * Retrieves a single <code>Permission</code> for a given permissionName principal.
     *   
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param permissionName a permission principal identity to be retrieved.
     * @return Permission the permission record retrieved.
     * @exception PermissionException when the security provider has a general failure.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    public Permission getPermission(String permissionName)
        throws JetspeedSecurityException
    {
        BaseJetspeedPermission r = new BaseJetspeedPermission();
        //r.setNew(false);
        r.setName(permissionName);
        r.setId(permissionName);
        return r;
    }
}

