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

package org.apache.jetspeed.services;

import java.security.Principal;
import java.util.Iterator;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.UserManagement;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

/**
 * Static accessor for the PortalAccessController service
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 */
public abstract class JetspeedUserManagement {

  /*
   * Utility method for accessing the service implementation
   * 
   * @return a UserManagement implementation instance
   */
  protected static UserManagement getService() {
    return (UserManagement) TurbineServices.getInstance().getService(
        UserManagement.SERVICE_NAME);
  }

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#getUser
   */
  public static JetspeedUser getUser(Principal principal)
      throws JetspeedSecurityException {
    return getService().getUser(principal);
  }

  /**
   * @see UserManagement#getUser
   */
  public static JetspeedUser getUser(RunData rundata, Principal principal)
      throws JetspeedSecurityException {
    return getService().getUser(rundata, principal);
  }

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#getUsers
   */
  public static Iterator getUsers() throws JetspeedSecurityException {
    return getService().getUsers();
  }

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#getUsers
   */
  public static Iterator getUsers(String filter)
      throws JetspeedSecurityException {
    return getService().getUsers(filter);
  }

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#saveUser
   */
  public static void saveUser(JetspeedUser user)
      throws JetspeedSecurityException {
    getService().saveUser(user);
  }

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#addUser
   */
  public static void addUser(JetspeedUser user)
      throws JetspeedSecurityException {
    getService().addUser(user);
  }

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#removeUser
   */
  public static void removeUser(Principal principal)
      throws JetspeedSecurityException {
    getService().removeUser(principal);
  }

  // /////////////////////////////////////////////////////////////////////
  // Credentials Management
  // ////////////////////////////////////////////////////////////////////

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#changePassword
   */
  public static void changePassword(JetspeedUser user, String oldPassword,
      String newPassword) throws JetspeedSecurityException {
    getService().changePassword(user, oldPassword, newPassword);
  }

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#forcePassword
   */
  public static void forcePassword(JetspeedUser user, String password)
      throws JetspeedSecurityException {
    getService().forcePassword(user, password);
  }

  /**
   * @see org.apache.jetspeed.services.security.UserManagement#encryptPassword
   */
  public static String encryptPassword(String password)
      throws JetspeedSecurityException {
    return getService().encryptPassword(password);
  }

}
