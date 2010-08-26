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

import java.util.List;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.Service;

/**
 * The Security Service manages Users, Groups Roles and Permissions in the 
 * system. The Jetspeed Security Service extends the interface of the Turbine
 * Security Service, adding on the Jetspeed specific interface: AccessControl
 * for controlling access to portal resources (portlets, panes).
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: JetspeedSecurityService.java,v 1.12 2004/03/31 04:49:10 morciuch Exp $
 */


public interface JetspeedSecurityService extends Service
{
   /** The name of this service */
   public String SERVICE_NAME = "JetspeedSecurity";

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
   public JetspeedUser getUserInstance();


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
    public String convertUserName(String username);

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
    public String convertPassword(String password);

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
    public boolean checkDisableAccount(String username);

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
    public boolean isDisableAccountCheckEnabled();

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
    public void resetDisableAccountCheck(String username);


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
        throws JetspeedSecurityException;


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
    public boolean checkPermission(JetspeedRunData runData, String action, Portlet portlet);

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
     */
    //public boolean checkPermission(JetspeedRunData runData, String action, RegistryEntry entry);

   /*
     * Security configuration setting to disable all action buttons for the Anon user
     * This setting is readonly and is edited in the JetspeedSecurity deployment
     *    
     *
     * @return True if the feature actions are disabled for the anon user
     *
     */
    public boolean areActionsDisabledForAnon();

    /*
     * Security configuration setting to disable all action buttons for all users
     * This setting is readonly and is edited in the JetspeedSecurity deployment
     *    
     *
     * @return True if the feature actions are disabled for the all users
     *
     */
    public boolean areActionsDisabledForAllUsers();


   /*
     * Gets the name of the anonymous user account if applicable
     *    
     *
     * @return String the name of the anonymous user account
     *
     */
    public String getAnonymousUserName();

	/*
	 * Gets the list of administrative roles
	 *    
	 * @return list of admin roles
	 */
	 public List getAdminRoles();

	/*
	 * Returns true if user has adminstrative role
	 *    
	 * @return
	 */
	 public boolean hasAdminRole(User user);

}

