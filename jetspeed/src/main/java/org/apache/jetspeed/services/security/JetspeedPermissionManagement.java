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


// Turbine
import org.apache.turbine.services.TurbineServices;


import org.apache.jetspeed.om.security.Permission;

/**
 * <p> The <code>PermissionManagement</code> interface describes contract between 
 * the portal and security provider required for Jetspeed Permission Management.
 * This interface enables an application to be independent of the underlying 
 * permission management technology.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: JetspeedPermissionManagement.java,v 1.4 2004/02/23 03:58:11 jford Exp $
 */

public abstract class JetspeedPermissionManagement
{
    public String SERVICE_NAME = "PermissionManagement";

    /*
     * Utility method for accessing the service
     * implementation
     *
     * @return a PermissionManagement implementation instance
     */
    protected static PermissionManagement getService()
    {
        return (PermissionManagement)TurbineServices
        .getInstance().getService(PermissionManagement.SERVICE_NAME);
    }

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
    public static Iterator getPermissions(String rolename)
        throws JetspeedSecurityException
    {
        return getService().getPermissions(rolename);
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
    public static Iterator getPermissions()
        throws JetspeedSecurityException
    {
        return getService().getPermissions();
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
    public static void addPermission(Permission permission)
        throws JetspeedSecurityException
    {
        getService().addPermission(permission);
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
    public static void savePermission(Permission permission)
        throws JetspeedSecurityException
    {
        getService().savePermission(permission);
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
    public static void removePermission(String permissionName)
        throws JetspeedSecurityException
    {
        getService().removePermission(permissionName);
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
    public static void grantPermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
        getService().grantPermission(roleName, permissionName);
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
    public static void revokePermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
        getService().revokePermission(roleName,permissionName);
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
    public static boolean hasPermission(String roleName, String permissionName)
        throws JetspeedSecurityException
    {
        return getService().hasPermission(roleName,permissionName);
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
    public static Permission getPermission(String permissionName)
        throws JetspeedSecurityException
    {
        return getService().getPermission(permissionName);
    }

}











