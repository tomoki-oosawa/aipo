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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.JetspeedUserFactory;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.JetspeedPortalAccessController;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.JetspeedUserManagement;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;

/**
 * <p>This is an implementation of the <code>JetspeedSecurityService</code> interface.
 *
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
 * @version $Id: JetspeedDBSecurityService.java,v 1.25 2004/03/31 04:49:10 morciuch Exp $
 */

public class JetspeedDBSecurityService extends TurbineBaseService
                                       implements JetspeedSecurityService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedDBSecurityService.class.getName());
    
    private final static String CONFIG_CASEINSENSITIVE_USERNAME = "caseinsensitive.username";
    private final static String CONFIG_CASEINSENSITIVE_PASSWORD = "caseinsensitive.password";
    private final static String CONFIG_CASEINSENSITIVE_UPPER = "caseinsensitive.upper";
    private final static String CONFIG_LOGON_STRIKE_COUNT = "logon.strike.count";
    private final static String CONFIG_LOGON_STRIKE_MAX = "logon.strike.max";
    private final static String CONFIG_LOGON_STRIKE_INTERVAL = "logon.strike.interval";
    private final static String CONFIG_LOGON_AUTO_DISABLE = "logon.auto.disable";
    private final static String CONFIG_ACTIONS_ANON_DISABLE = "actions.anon.disable";
    private final static String CONFIG_ACTIONS_ALLUSERS_DISABLE = "actions.allusers.disable";
	private final static String CONFIG_ACTIONS_ADMIN_ROLES = "admin.roles";

    private final static String CONFIG_NEWUSER_ROLES     = "newuser.roles";
    private final static String CONFIG_DEFAULT_PERMISSION_LOGGEDIN     = "permission.default.loggedin";
    private final static String CONFIG_DEFAULT_PERMISSION_ANONYMOUS     = "permission.default.anonymous";
    private final static String CONFIG_ANONYMOUS_USER = "user.anonymous";
    private final static String [] DEFAULT_PERMISSIONS = {""};
    private final static String [] DEFAULT_CONFIG_NEWUSER_ROLES = 
    { "user" };
	private final static String [] DEFAULT_ADMIN_ROLES = 
	{ "admin" };

    String roles[] = null;
    boolean caseInsensitiveUsername = false;
    boolean caseInsensitivePassword = false;
    boolean caseInsensitiveUpper = true;
    boolean actionsAnonDisable = true;
    boolean actionsAllUsersDisable = false;
    String anonymousUser = "anon";
	String[] adminRoles = null;

    int strikeCount = 3;             // 3 within the interval
    int strikeMax = 20;              // 20 total failures 
    long strikeInterval = 300;  // five minutes

    boolean autoLogonDisable = false;

    private static HashMap users = new HashMap();

    private static Object sem = new Object();

    /**
     * This is the early initialization method called by the 
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public synchronized void init(ServletConfig conf) throws InitializationException 
    {
        // already initialized
        if (getInit()) return;

        super.init(conf);

        // get configuration parameters from Jetspeed Resources
        ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                     .getResources(JetspeedSecurityService.SERVICE_NAME);
        
        try
        {
            roles = serviceConf.getStringArray(CONFIG_NEWUSER_ROLES);
			adminRoles = serviceConf.getStringArray(CONFIG_ACTIONS_ADMIN_ROLES);
        }
        catch (Exception e)
        {}
            
        if (null == roles || roles.length == 0)
        {
            roles = DEFAULT_CONFIG_NEWUSER_ROLES;
        }

		if (null == adminRoles || adminRoles.length == 0)
		{
			adminRoles = DEFAULT_ADMIN_ROLES;
		}

        caseInsensitiveUsername = serviceConf.getBoolean(CONFIG_CASEINSENSITIVE_USERNAME, caseInsensitiveUsername);
        caseInsensitivePassword = serviceConf.getBoolean(CONFIG_CASEINSENSITIVE_PASSWORD, caseInsensitivePassword);
        caseInsensitiveUpper = serviceConf.getBoolean(CONFIG_CASEINSENSITIVE_UPPER, caseInsensitiveUpper);

        strikeCount = serviceConf.getInt(CONFIG_LOGON_STRIKE_COUNT, strikeCount);
        strikeInterval = serviceConf.getLong(CONFIG_LOGON_STRIKE_INTERVAL, strikeInterval);
        strikeMax = serviceConf.getInt(CONFIG_LOGON_STRIKE_MAX, strikeMax);

        autoLogonDisable = serviceConf.getBoolean(CONFIG_LOGON_AUTO_DISABLE, autoLogonDisable);
        actionsAnonDisable = serviceConf.getBoolean(CONFIG_ACTIONS_ANON_DISABLE, actionsAnonDisable);
        actionsAllUsersDisable = serviceConf.getBoolean(CONFIG_ACTIONS_ALLUSERS_DISABLE, actionsAllUsersDisable);

        anonymousUser = serviceConf.getString(CONFIG_ANONYMOUS_USER, anonymousUser);

        // initialization done
        setInit(true);
     }


    //////////////////////////////////////////////////////////////////////////
    //
    // Required JetspeedSecurity Functions
    //
    // Required Features provided by default JetspeedSecurity
    //
    //////////////////////////////////////////////////////////////////////////

    /*
     * Factory to create a new JetspeedUser, using JetspeedUserFactory.
     * The class that is created by the default JetspeedUserFactory is configured
     * in the JetspeedSecurity properties:
     *
     *    services.JetspeedSecurity.user.class=
     *        org.apache.jetspeed.om.security.BaseJetspeedUser
     *
     * @return JetspeedUser a newly created user that implements JetspeedUser.
     */
    public JetspeedUser getUserInstance()
    {
        try
        {
            return JetspeedUserFactory.getInstance();
        }
        catch (UserException e)
        {
            return null;
        }
    }

    //////////////////////////////////////////////////////////////////////////
    //
    // Optional JetspeedSecurity Features 
    //
    // Features are not required to be implemented by Security Provider
    //
    //////////////////////////////////////////////////////////////////////////

    /*
     * During logon, the username can be case sensitive or case insensitive.
     *
     * Given a username, converts the username to either lower or upper case.
     * This optional feature is configurable from the JetspeedSecurity.properties:
     *
     *     <code>services.JetspeedSecurity.caseinsensitive.username = true/false</code>
     *     <code>services.JetspeedSecurity.caseinsensitive.upper = true/false</code>
     *
     * If <code>caseinsensitive.username</code> is true,  
     * then conversion is enabled and the username will be converted before 
     * being sent to the Authentication provider.
     *
     * @param username The username to be converted depending on configuration.
     * @return The converted username.
     *
     */
    public String convertUserName(String username)
    {
        if (caseInsensitiveUsername)
        { 
            username = (caseInsensitiveUpper) ? username.toUpperCase() : username.toLowerCase(); 
        } 
        return username;
    }

    /*
     * During logon, the password can be case sensitive or case insensitive.
     *
     * Given a password, converts the password to either lower or upper case.
     * This optional feature is configurable from the JetspeedSecurity.properties:
     *
     *     <code>services.JetspeedSecurity.caseinsensitive.password = true/false</code>
     *     <code>services.JetspeedSecurity.caseinsensitive.upper = true/false</code>
     *
     * If <code>caseinsensitive.password</code> is true,  
     * then conversion is enabled and the password will be converted before 
     * being sent to the Authentication provider.
     *
     * @param password The password to be converted depending on configuration.
     * @return The converted password.
     *
     */
    public String convertPassword(String password)
    {
        if (caseInsensitivePassword)
        { 
            password = (caseInsensitiveUpper) ? password.toUpperCase() : password.toLowerCase(); 
        } 
        return password;
    }

    /*
     * Logon Failure / Account Disabling Feature
     *
     * Checks and tracks failed user-logon attempts.
     * If the user fails to logon after a configurable number of logon attempts,
     * then the user's account will be disabled.
     *
     * This optional feature is configurable from the JetspeedSecurity.properties:
     *
     *     <code>services.JetspeedSecurity.logon.auto.disable=false</code>
     *
     * The example setting below allows for 3 logon strikes per 300 seconds.
     * When the strike.count is exceeded over the strike.interval, the account
     * is disabled. The strike.max is the cumulative maximum.
     *
     *     <code>services.JetspeedSecurity.logon.strike.count=3</code>
     *     <code>services.JetspeedSecurity.logon.strike.interval=300</code>
     *     <code>services.JetspeedSecurity.logon.strike.max=10</code>
     *
     * These settings are not persisted, and in a distributed environment are 
     * only tracked per node.
     *
     * @param username The username to be checked.
     * @return True if the strike count reached the maximum threshold and the
     *         user's account was disabled, otherwise False.
     *
     */
    public boolean checkDisableAccount(String username)
    {
        username = convertUserName(username);
 
        // TODO: make this work across a cluster of servers
        UserLogonStats stat = (UserLogonStats)users.get(username);
        if (stat == null)
        {
            stat = new UserLogonStats(username);
            synchronized (sem)
            {
                users.put(username, stat);
            }
        }
        boolean disabled = stat.failCheck(strikeCount, strikeInterval, strikeMax);

        if (disabled)
        {
            try
            {
                // disable the account
                JetspeedUser user = (JetspeedUser)JetspeedSecurity.getUser(username);
                if (user != null)
                {
                    user.setDisabled("T");
                    JetspeedSecurity.saveUser(user);
                }
            }
            catch (Exception e)
            {
                 logger.error("Could not disable user: " + username, e);
            }
        }
        return disabled;
    }

    /*
     * Logon Failure / Account Disabling Feature
     *    
     * Returns state of the the logon failure / account disabling feature.
     * 
     * If the user fails to logon after a configurable number of logon attempts,
     * then the user's account will be disabled.
     *
     * @see JetspeedSecurityService#checkLogonFailures
     *
     * @return True if the feature is enabled, false if the feature is disabled.
     *
     */
    public boolean isDisableAccountCheckEnabled()
    {
        return autoLogonDisable;
    }

    
    /*
     * Logon Failure / Account Disabling Feature
     *    
     * Resets counters for the logon failure / account disabling feature.
     * 
     * If the user fails to logon after a configurable number of logon attempts,
     * then the user's account will be disabled.
     *
     * @see JetspeedSecurityService#checkLogonFailures
     *
     * @param username The username to reset the logon failure counters.
     *
     */
    public void resetDisableAccountCheck(String username)
    {
        // TODO: make this work across a cluster of servers
        username = convertUserName(username);
        UserLogonStats stat = (UserLogonStats)users.get(username);
        if (stat == null)           
        {
            stat = new UserLogonStats(username);
            synchronized (sem)
            {
                users.put(username, stat);
            }
        }
        stat.reset();
    }
    

    //////////////////////////////////////////////////////////////////////////
    //
    // Optional JetspeedSecurity Helpers
    //
    //////////////////////////////////////////////////////////////////////////

    /**
     * Helper to UserManagement.
     * Retrieves a <code>JetspeedUser</code> given the primary principle username.
     * The principal can be any valid Jetspeed Security Principal:
     *   <code>org.apache.jetspeed.om.security.UserNamePrincipal</code>
     *   <code>org.apache.jetspeed.om.security.UserIdPrincipal</code>
     *   
     * The security service may optionally check the current user context
     * to determine if the requestor has permission to perform this action.
     *
     * @param username The username principal.
     * @return a <code>JetspeedUser</code> associated to the principal identity.
     * @exception UserException when the security provider has a general failure retrieving a user.
     * @exception UnknownUserException when the security provider cannot match
     *            the principal identity to a user.
     * @exception InsufficientPrivilegeException when the requestor is denied due to insufficient privilege 
     */

    public JetspeedUser getUser(String username) 
        throws JetspeedSecurityException
    {
        return JetspeedUserManagement.getUser(new UserNamePrincipal(username));
    }


    /**
     * Helper to PortalAuthorization.
     * Gets a <code>JetspeedUser</code> from rundata, authorize user to perform the secured action on
     * the given <code>Portlet</code> resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param rundata request that the user is taken from rundatas
     * @param action the secured action to be performed on the resource by the user.     
     * @param portlet the portlet resource.
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedRunData runData, String action, Portlet portlet)
    {
        return JetspeedPortalAccessController.checkPermission(runData.getJetspeedUser(),
                                                       portlet,
                                                       action);
    }

    /**
     * Helper to PortalAuthorization.
     * Gets a <code>JetspeedUser</code> from rundata, authorize user to perform the secured action on
     * the given <code>Entry</code> resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param rundata request that the user is taken from rundatas
     * @param action the secured action to be performed on the resource by the user.     
     * @param entry the portal entry resource.
     * @return boolean true if the user has sufficient privilege.
    public boolean checkPermission(JetspeedRunData runData, String action, RegistryEntry entry)
    {
        return JetspeedPortalAccessController.checkPermission(runData.getJetspeedUser(),
                                                       entry,
                                                       action);
    }
     */

    /*
     * Security configuration setting to disable all action buttons for the Anon user
     * This setting is readonly and is edited in the JetspeedSecurity deployment
     *    
     *
     * @return True if the feature actions are disabled for the anon user
     *
     */
    public boolean areActionsDisabledForAnon()
    {
        return actionsAnonDisable;
    }

    /*
     * Security configuration setting to disable all action buttons for all users
     * This setting is readonly and is edited in the JetspeedSecurity deployment
     *    
     *
     * @return True if the feature actions are disabled for the all users
     *
     */
    public boolean areActionsDisabledForAllUsers()
    {
        return actionsAllUsersDisable;
    }

   /*
     * Gets the name of the anonymous user account if applicable
     *    
     *
     * @return String the name of the anonymous user account
     *
     */
    public String getAnonymousUserName()
    {
        return anonymousUser;
    }

	/*
	 * Gets the list of administrative roles
	 *    
	 * @return list of admin roles
	 */
	 public List getAdminRoles()
	 {
	 	List result = new ArrayList();
	 	for (int i = 0; i < adminRoles.length; i++)
	 	{
	 		result.add(adminRoles[i]);
	 	}
	 	
		return result;
	 }

	/**
	 * Returns true if user has administrative role
	 * 
	 * @param user
	 * @return true if user has administrative role
	 */
	public boolean hasAdminRole(User user)
	{
		String username = user.getUserName();
		try
		{
			List adminRoles = getAdminRoles();
			for (Iterator it = adminRoles.iterator(); it.hasNext();)
			{
				if (JetspeedSecurity.hasRole(username, (String)it.next()))
				{
					return true;
				}
			}
		}
		catch (Exception e)
		{	
			logger.error(e);		
		}
		
		return false;
	}

}

