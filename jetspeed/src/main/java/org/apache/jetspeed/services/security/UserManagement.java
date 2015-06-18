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

import java.security.Principal;
import java.util.Iterator;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;

/**
 * <p>
 * The <code>UserManagement</code> interface describes contract between the
 * portal and security provider required for Jetspeed User Management. This
 * interface enables an application to be independent of the underlying user
 * management technology.
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor </a>
 */

public interface UserManagement extends Service, CredentialsManagement {
  public String SERVICE_NAME = "UserManagement";

  /**
   * Retrieves a <code>JetspeedUser</code> given the primary principle. The
   * principal can be any valid Jetspeed Security Principal:
   * <code>org.apache.jetspeed.om.security.UserNamePrincipal</code>
   *   <code>org.apache.jetspeed.om.security.UserIdPrincipal</code>
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param principal
   *            a principal identity to be retrieved.
   * @return a <code>JetspeedUser</code> associated to the principal identity.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                a user.
   * @exception UnknownUserException
   *                when the security provider cannot match the principal
   *                identity to a user.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  JetspeedUser getUser(Principal principal) throws JetspeedSecurityException;

  JetspeedUser getUser(RunData rundata, Principal principal)
      throws JetspeedSecurityException;

  /**
   * Retrieves a collection of all <code>JetspeedUser</code>s. The security
   * service may optionally check the current user context to determine if the
   * requestor has permission to perform this action.
   * 
   * @return a collection of <code>JetspeedUser</code> entities.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  Iterator getUsers() throws JetspeedSecurityException;

  /**
   * Retrieves a collection of <code>JetspeedUser</code> s filtered by a
   * security provider-specific query string. For example SQL, OQL, JDOQL. The
   * security service may optionally check the current user context to determine
   * if the requestor has permission to perform this action.
   * 
   * @return a collection of <code>JetspeedUser</code> entities.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  Iterator getUsers(String filter) throws JetspeedSecurityException;

  /**
   * Saves a <code>JetspeedUser</code>'s attributes into permanent storage.
   * The user's account is required to exist in the storage. The security
   * service may optionally check the current user context to determine if the
   * requestor has permission to perform this action.
   * 
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  void saveUser(JetspeedUser user) throws JetspeedSecurityException;

  /**
   * Adds a <code>JetspeedUser</code> into permanent storage. The security
   * service can throw a <code>NotUniqueUserException</code> when the public
   * credentials fail to meet the security provider-specific unique constraints.
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception NotUniqueUserException
   *                when the public credentials fail to meet the security
   *                provider-specific unique constraints.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  void addUser(JetspeedUser user) throws JetspeedSecurityException;

  /**
   * Removes a <code>JetspeedUser</code> from the permanent store. The
   * security service may optionally check the current user context to determine
   * if the requestor has permission to perform this action.
   * 
   * @param principal
   *            the principal identity to be retrieved.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                a user.
   * @exception UnknownUserException
   *                when the security provider cannot match the principal
   *                identity to a user.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  void removeUser(Principal principal) throws JetspeedSecurityException;

}
