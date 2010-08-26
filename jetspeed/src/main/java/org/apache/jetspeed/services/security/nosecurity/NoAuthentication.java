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

import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.InitializationException;

import org.apache.jetspeed.services.security.PortalAuthentication;
import org.apache.jetspeed.services.security.LoginException;

import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.om.security.JetspeedUser;

import org.apache.jetspeed.services.security.FailedLoginException;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.nosecurity.FakeJetspeedUser;
import org.apache.turbine.services.rundata.RunDataService;

/**
 * <p> The <code>NoAuthentication</code> class is a Jetspeed
 * security provider, implementing the <code>PortalAuthentication</code> interface.
 * It provides no authentication - all login attempts are allowed.
 *
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 * @version $Id: NoAuthentication.java,v 1.3 2004/02/23 03:53:24 jford Exp $
 */
public class NoAuthentication
    extends TurbineBaseService
    implements PortalAuthentication
{
    /** The JetspeedRunData Service. */
    private JetspeedRunDataService m_runDataService = null;

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
    public JetspeedUser login(String username, String password)
        throws LoginException
    {
        // we let anyone in!
        if (false) throw new FailedLoginException("Invalid user id or password");

        // create a user object with this username for Jetspeed use
        FakeJetspeedUser user = new FakeJetspeedUser(username, true);

        // make it the logged in user for Jetspeed
        putUserIntoContext(user);

        return user;
        
    }   // login

    /**
     * Automatically authenticates and retrieves the portal anonymous user.
     *
     * @return a <code>JetspeedUser</code> object representing the authenticated subject.
     * @exception LoginException if the authentication fails.
     */
    public JetspeedUser getAnonymousUser()
        throws LoginException
    {
        // create a user object with this username for Jetspeed use
        FakeJetspeedUser user = new FakeJetspeedUser(JetspeedSecurity.getAnonymousUserName(), false);

        // make it the logged in user for Jetspeed
        putUserIntoContext(user);

        return user;

    }   // getAnonymousUser

    /**
     * Logout the <code>JetspeedUser</code>.
     *
     * The logout procedure my may include removing/destroying
     * <code>Principal</code> and <code>Credential</code> information
     * if relevant to the security provider.
     *
     * @exception LoginException if the logout fails.
     */
    public void logout()
        throws LoginException
    {
        // revert to the anon. user as the current user
        getAnonymousUser();

    }   // logout

    /**
    * Performs late initialization.  Called just before the first use of the service.
    *
    * If your class relies on early initialization, and the object it
    * expects was not received, you can use late initialization to
    * throw an exception and complain.
    *
    * @exception InitializationException, if initialization of this class was not successful.
    */
    public synchronized void init() 
        throws InitializationException 
    {
        super.init();

        m_runDataService =
            (JetspeedRunDataService)TurbineServices.getInstance()
                .getService(RunDataService.SERVICE_NAME);

     }  // init

    ////////////////////////////////////////////////////////////////////////////

    protected JetspeedRunData getRunData()
    {
        JetspeedRunData rundata = null;
        if (m_runDataService != null)
        {
            rundata = m_runDataService.getCurrentRunData();
        }
        return rundata;
    }

    protected JetspeedUser getUserFromContext()
    {
        JetspeedRunData rundata = getRunData();
        JetspeedUser user = null;
        if (rundata != null)
        {
            user = (JetspeedUser)rundata.getUser();
        }
        return user;
    }

    protected JetspeedRunData putUserIntoContext(JetspeedUser user)
    {
        JetspeedRunData rundata = getRunData();
        if (rundata != null)
        {
            rundata.setUser(user);
            rundata.save();
        }
        return rundata;
    }
}

