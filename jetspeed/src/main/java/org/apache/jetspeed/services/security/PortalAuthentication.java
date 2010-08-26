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

import org.apache.turbine.services.Service;

import org.apache.jetspeed.om.security.JetspeedUser;

/**
 * <p> The <code>PortalAuthentication</code> interface defines contract between 
 * the portal and security provider required for authentication a Jetspeed User.
 * This interface enables an application to be independent of the underlying 
 * authentication technology.
 *
 * <p> If the <code>login</code> method returns without
 * throwing an exception, then the overall authentication succeeded.
 *
 * <p> To logout the caller simply needs to invoke the <code>logout</code> method.  
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: PortalAuthentication.java,v 1.3 2004/02/23 03:58:11 jford Exp $
 */

public interface PortalAuthentication extends Service
{
    public String SERVICE_NAME = "PortalAuthentication";

    /**
     * Given a public credential(username) and private credential(password), 
     * perform authentication. If authentication succeeds, a <code>JetspeedUser</code> 
     * is returned representing the authenticated subject.
     *
     * @param username a public credential of the subject to be authenticated.
     * @param password a private credentialof the subject to be authenticated.
     * @return a <code>JetspeedUser</code> object representing the authenticated subject.
     * @exception LoginException when general security provider failure.
     * @exception FailedLoginException when the authentication failed.
     * @exception AccountExpiredException when the subject's account is expired.
     * @exception CredentialExpiredException when the subject's credential is expired.
     */
    JetspeedUser login(String username, String password)
        throws LoginException;

    /**
     * Automatically authenticates and retrieves the portal anonymous user.
     *
     * @return a <code>JetspeedUser</code> object representing the authenticated subject.
     * @exception LoginException if the authentication fails.
     */
    JetspeedUser getAnonymousUser()
        throws LoginException;

    /**
     * Logout the <code>JetspeedUser</code>.
     *
     * The logout procedure my may include removing/destroying
     * <code>Principal</code> and <code>Credential</code> information
     * if relevant to the security provider.
     *
     * @exception LoginException if the logout fails.
     */
    void logout()
        throws LoginException;
}
