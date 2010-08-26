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
 * <p> The <code>UserManagement</code> interface describes a contract between 
 * the portal and security provider required for Jetspeed Credentials Management.
 * This interface enables an application to be independent of the underlying 
 * user management technology.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: CredentialsManagement.java,v 1.3 2004/02/23 03:58:11 jford Exp $
 */

public interface CredentialsManagement extends Service  
{
    public String SERVICE_NAME = "CredentialsManagement";

    /**
     * Allows for a user to change their own password.
     *
     * @param user the user to change the password for.
     * @param oldPassword the current password supplied by the user.
     * @param newPassword the current password requested by the user.
     * @exception UserException when the security provider has a general failure retrieving a user.
     * @exception UnknownUserException when the security provider cannot match
     *            the principal identity to a user.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    void changePassword( JetspeedUser user,
                         String oldPassword, 
                         String newPassword )
        throws JetspeedSecurityException;

    /**
     * Forcibly sets new password for a User.
     *
     * Provides an administrator the ability to change the forgotten or
     * compromised passwords. Certain implementatations of this feature
     * would require administrative level access to the authenticating
     * server / program.
     *     
     * @param user the user to change the password for.
     * @param password the new password.   
     * @exception UserException when the security provider has a general failure retrieving a user.
     * @exception UnknownUserException when the security provider cannot match
     *            the principal identity to a user.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */
    void forcePassword( JetspeedUser user, String password )
        throws JetspeedSecurityException;


    /**
     * This method provides client-side encryption of passwords.
     *
     * If <code>secure.passwords</code> are enabled in JetspeedSecurity properties,
     * the password will be encrypted, if not, it will be returned unchanged.
     * The <code>secure.passwords.algorithm</code> property can be used
     * to chose which digest algorithm should be used for performing the
     * encryption. <code>SHA</code> is used by default.
     *
     * @param password the password to process
     * @return processed password
     */
    String encryptPassword( String password )
        throws JetspeedSecurityException;

}

