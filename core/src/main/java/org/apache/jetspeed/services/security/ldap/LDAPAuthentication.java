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

package org.apache.jetspeed.services.security.ldap;

import javax.naming.directory.BasicAttributes;
import javax.servlet.ServletConfig;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.JetspeedUserManagement;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.FailedLoginException;
import org.apache.jetspeed.services.security.JetspeedSecurityCache;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.jetspeed.services.security.LoginException;
import org.apache.jetspeed.services.security.PortalAuthentication;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.rundata.RunDataService;

/**
 *
 * @author <a href="mailto:ender@kilicoglu.nom.tr">Ender KILICOGLU</a>
 * @author <a href="mailto:sami.leino@netorek.fi">Sami Leino</a>
 *
 * @version $Id: LDAPAuthentication.java,v 1.8 2004/02/23 03:52:33 jford Exp $ 
 * 
 */
public class LDAPAuthentication extends TurbineBaseService
                                implements PortalAuthentication
{
    // Constants
    private final static String CONFIG_ANONYMOUS_USER = "user.anonymous";
    private final static String CACHING_ENABLE        = "caching.enable";

    // Instance variables.
    private JetspeedRunDataService runDataService     = null;
    private String anonymousUser                      = "anon";
    private boolean cachingEnable                     = true;

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

        if (username.equals(this.anonymousUser))
        {
            throw new LoginException("Anonymous user cannot login");
        }

        JetspeedUser user = null;
        BasicAttributes attr= new BasicAttributes();
        String[] attrs = {"ou", "userPassword", "uid", "mail"};
        String dN = null;

        username = JetspeedSecurity.convertUserName(username);
        password = JetspeedSecurity.convertPassword(password);

        try
        {
            user = JetspeedUserManagement.getUser(new UserNamePrincipal(username));
        }
        catch (UnknownUserException e)
        {
            throw new FailedLoginException(e.toString());
        }
        catch (JetspeedSecurityException e)
        {
            throw new LoginException(e.toString());
        }

        if(!LDAPUserManagement.passwordsMatch(user, password))
        {
            throw new FailedLoginException("Credential authentication failure");
        }

        // Mark the user as being logged in.
        user.setHasLoggedIn(new Boolean(true));

        // Store the clear-text password to session if some of the 
        // portlets need it (for example to single-signon functionality) 
        user.setTemp( "sessionPassword", password );

        // Set the last_login date in the database.
        try
        {
            user.updateLastLogin();
            putUserIntoContext(user);

            if (cachingEnable)
            {
                JetspeedSecurityCache.load(username);
            }
        }
        catch (Exception e)
        {
            putUserIntoContext(JetspeedSecurity.getAnonymousUser());
            throw new LoginException("Failed to update last login ", e);
        }

        return user;

    }
    /**
     * Automatically authenticates and retrieves the portal anonymous user.
     *
     * @return a <code>JetspeedUser</code> object representing the authenticated subject.
     * @exception LoginException if the authentication fails.
     */
    public JetspeedUser getAnonymousUser()
        throws LoginException
    {
        JetspeedUser user = null;

        try
        {
            user = JetspeedUserManagement.getUser(new UserNamePrincipal(anonymousUser));
            user.setHasLoggedIn(new Boolean(false));
            putUserIntoContext(user);
 
            if (cachingEnable)
            {
                JetspeedSecurityCache.load(user.getUserName());
            }

        }
        catch (JetspeedSecurityException e)
        {
            throw new LoginException("Failed to get anonymous user: " + e);
        }
        return user;
    }

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
        try
        {
             // if (cachingEnable)
             // {
             //     JetspeedSecurityCache.unload(getUserFromContext().getUserName());
             // }
            getAnonymousUser();
        }
        catch (Exception e)
        {
            throw new LoginException("Exception logging user out ", e );
        }
    }

    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public synchronized void init(ServletConfig conf)
        throws InitializationException
    {
        if (getInit()) return;
        super.init(conf);

        // get configuration parameters from Jetspeed Resources
        ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                     .getResources(JetspeedSecurityService.SERVICE_NAME);

        anonymousUser = serviceConf.getString(CONFIG_ANONYMOUS_USER, anonymousUser);
        cachingEnable = serviceConf.getBoolean(CACHING_ENABLE, cachingEnable);

        this.runDataService =
            (JetspeedRunDataService)TurbineServices.getInstance()
                .getService(RunDataService.SERVICE_NAME);


        setInit(true);
     }

    ////////////////////////////////////////////////////////////////////////////

    protected JetspeedRunData getRunData()
    {
        JetspeedRunData rundata = null;
        if (this.runDataService != null)
        {
            rundata = this.runDataService.getCurrentRunData();
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
