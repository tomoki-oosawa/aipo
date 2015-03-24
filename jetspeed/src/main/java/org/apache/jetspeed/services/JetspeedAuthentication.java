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

// Turbine
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.security.AccountExpiredException;
import org.apache.jetspeed.services.security.CredentialExpiredException;
import org.apache.jetspeed.services.security.FailedLoginException;
import org.apache.jetspeed.services.security.LoginException;
import org.apache.jetspeed.services.security.PortalAuthentication;
import org.apache.turbine.services.TurbineServices;

/**
 * Static accessor for the JetspeedAuthentication service
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 */
public abstract class JetspeedAuthentication {
  /**
   * Given a public credential(username) and private credential(password),
   * perform authentication. If authentication succeeds, a
   * <code>JetspeedUser</code> is returned representing the authenticated
   * subject.
   * 
   * @param username
   *            a public credential of the subject to be authenticated.
   * @param password
   *            a private credentialof the subject to be authenticated.
   * @return a <code>JetspeedUser</code> object representing the authenticated
   *         subject.
   * @exception LoginException
   *                when general security provider failure.
   * @exception FailedLoginException
   *                when the authentication failed.
   * @exception AccountExpiredException
   *                when the subject's account is expired.
   * @exception CredentialExpiredException
   *                when the subject's credential is expired.
   */
  public static JetspeedUser login(String username, String password)
      throws LoginException {
    return getService().login(username, password);
  }

  /**
   * Automatically authenticates and retrieves the portal anonymous user.
   * 
   * @return a <code>JetspeedUser</code> object representing the authenticated
   *         subject.
   * @exception LoginException
   *                if the authentication fails.
   */
  public static JetspeedUser getAnonymousUser() throws LoginException {
    return getService().getAnonymousUser();
  }

  /**
   * Logout the <code>JetspeedUser</code>.
   * 
   * The logout procedure my may include removing/destroying
   * <code>Principal</code> and <code>Credential</code> information if
   * relevant to the security provider.
   * 
   * @exception LoginException
   *                if the logout fails.
   */
  public static void logout() throws LoginException {
    getService().logout();
  }

  /*
   * Utility method for accessing the service implementation
   * 
   * @return a UniqueIdService implementation instance
   */
  protected static PortalAuthentication getService() {
    return (PortalAuthentication) TurbineServices.getInstance().getService(
        PortalAuthentication.SERVICE_NAME);
  }

}
