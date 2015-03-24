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

import java.security.Principal;
import java.util.Iterator;
import java.util.Vector;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.security.CredentialsManagement;
import org.apache.jetspeed.services.security.InsufficientPrivilegeException;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.NotUniqueUserException;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.jetspeed.services.security.UserException;
import org.apache.jetspeed.services.security.UserManagement;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.RunData;

/**
 * <p>
 * The <code>NoUserManagement</code> class is a Jetspeed security provider,
 * implementing the <code>UserManagement</code> and
 * <code>CredentialsManagement</code> interfaces. It does not manage any users -
 * no users are listed, no users are saved, any request for a user is satisfied
 * with a temp. User object.
 * 
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden </a>
 */
public class NoUserManagement extends TurbineBaseService implements
    UserManagement, CredentialsManagement {
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
  public JetspeedUser getUser(Principal principal)
      throws JetspeedSecurityException {
    // create a user object with this username for Jetspeed use
    FakeJetspeedUser user = new FakeJetspeedUser(principal.getName(), false);
    return user;
  }

  public JetspeedUser getUser(RunData rundata, Principal principal)
      throws JetspeedSecurityException {
    // create a user object with this username for Jetspeed use
    FakeJetspeedUser user = new FakeJetspeedUser(principal.getName(), false);
    return user;
  }

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
  public Iterator getUsers() throws JetspeedSecurityException {
    return new Vector().iterator();
  }

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
  public Iterator getUsers(String filter) throws JetspeedSecurityException {
    return new Vector().iterator();
  }

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
  public void saveUser(JetspeedUser user) throws JetspeedSecurityException {
  }

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
  public void addUser(JetspeedUser user) throws JetspeedSecurityException {
  }

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
  public void removeUser(Principal principal) throws JetspeedSecurityException {
  }

  /**
   * Allows for a user to change their own password.
   * 
   * @param user
   *            the user to change the password for.
   * @param oldPassword
   *            the current password supplied by the user.
   * @param newPassword
   *            the current password requested by the user.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                a user.
   * @exception UnknownUserException
   *                when the security provider cannot match the principal
   *                identity to a user.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void changePassword(JetspeedUser user, String oldPassword,
      String newPassword) throws JetspeedSecurityException {
  }

  /**
   * Forcibly sets new password for a User.
   * 
   * Provides an administrator the ability to change the forgotten or
   * compromised passwords. Certain implementatations of this feature would
   * require administrative level access to the authenticating server / program.
   * 
   * @param user
   *            the user to change the password for.
   * @param password
   *            the new password.
   * @exception UserException
   *                when the security provider has a general failure retrieving
   *                a user.
   * @exception UnknownUserException
   *                when the security provider cannot match the principal
   *                identity to a user.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void forcePassword(JetspeedUser user, String password)
      throws JetspeedSecurityException {
  }

  /**
   * This method provides client-side encryption of passwords.
   * 
   * If <code>secure.passwords</code> are enabled in JetspeedSecurity
   * properties, the password will be encrypted, if not, it will be returned
   * unchanged. The <code>secure.passwords.algorithm</code> property can be
   * used to chose which digest algorithm should be used for performing the
   * encryption. <code>SHA</code> is used by default.
   * 
   * @param password
   *            the password to process
   * @return processed password
   */
  public String encryptPassword(String password)
      throws JetspeedSecurityException {
    return password;
  }
}
