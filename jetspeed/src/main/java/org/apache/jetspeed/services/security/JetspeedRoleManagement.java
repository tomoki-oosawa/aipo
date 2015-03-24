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

import java.util.Iterator;

import org.apache.jetspeed.om.security.Role;
import org.apache.turbine.services.TurbineServices;

/**
 * <p> The <code>RoleManagement</code> interface describes contract between
 * the portal and security provider required for Jetspeed Role Management.
 * This interface enables an application to be independent of the underlying
 * role management technology.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JetspeedRoleManagement.java,v 1.5 2004/02/23 03:58:11 jford Exp $
 */

public abstract class JetspeedRoleManagement
{
  public String SERVICE_NAME = "RoleManagement";

  /*
   * Utility method for accessing the service
   * implementation
   *
   * @return a RoleService implementation instance
   */
  protected static RoleManagement getService()
  {
    return (RoleManagement) TurbineServices
        .getInstance().getService(RoleManagement.SERVICE_NAME);
  }

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
  public static Iterator getRoles(String username) throws JetspeedSecurityException
  {
    return getService().getRoles(username);
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
  public static Iterator getRoles() throws JetspeedSecurityException
  {
    return getService().getRoles();
  }

  /**
   * Adds a <code>Role</code> into permanent storage.
   *
   *
   * @exception RoleException when the security provider has a general failure.
   * @exception NotUniqueEntityException when the public credentials fail to meet
   *                                   the security provider-specific unique constraints.
   * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
   */
  public static void addRole(Role role) throws JetspeedSecurityException
  {
    getService().addRole(role);
  }

  /**
   * Save a <code>Role</code> into permanent storage.
   *
   *
   * @exception RoleException when the security provider has a general failure.
   * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
   */
  public static void saveRole(Role role) throws JetspeedSecurityException
  {
    getService().saveRole(role);
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
  public static void removeRole(String rolename) throws JetspeedSecurityException
  {
    getService().removeRole(rolename);
  }

  /**
   * Grants a role to a user.
   *
   * The security service may optionally check the current user context
   * to determine if the requestor has permission to perform this action.
   *
   * @exception RoleException when the security provider has a general failure retrieving users.
   * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
   */
  public static void grantRole(String username, String rolename) throws JetspeedSecurityException
  {
    getService().grantRole(username, rolename);
  }

  /**
   * Grants a role to a user for a given group.
   *
   * The security service may optionally check the current user context
   * to determine if the requestor has permission to perform this action.
   *
   * @exception RoleException when the security provider has a general failure retrieving users.
   * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
   */
  public static void grantRole(String username, String rolename, String groupname) throws JetspeedSecurityException
  {
    getService().grantRole(username, rolename, groupname);
  }

  /**
   * Revokes a role from a user.
   *
   * The security service may optionally check the current user context
   * to determine if the requestor has permission to perform this action.
   *
   * @exception RoleException when the security provider has a general failure retrieving users.
   * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
   */
  public static void revokeRole(String username, String rolename) throws JetspeedSecurityException
  {
    getService().revokeRole(username, rolename);
  }

  /**
   * Revokes a role from a user for a specific group.
   *
   * The security service may optionally check the current user context
   * to determine if the requestor has permission to perform this action.
   *
   * @exception RoleException when the security provider has a general failure retrieving users.
   * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
   */
  public static void revokeRole(String username, String rolename, String groupname) throws JetspeedSecurityException
  {
    getService().revokeRole(username, rolename, groupname);
  }


  /**
   * Checks for the relationship of user has a role. Returns true when the user has the given role.
   *
   * The security service may optionally check the current user context
   * to determine if the requestor has permission to perform this action.
   *
   * @exception RoleException when the security provider has a general failure retrieving users.
   * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege
   */
  public static boolean hasRole(String username, String rolename) throws JetspeedSecurityException
  {
    return getService().hasRole(username, rolename);
  }

  public static boolean hasRole(String username, String rolename, String groupname) throws JetspeedSecurityException
  {
	return getService().hasRole(username, rolename, groupname);
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
  public static Role getRole(String rolename) throws JetspeedSecurityException
  {
    return getService().getRole(rolename);
  }

}
