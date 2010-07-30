/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.jetspeed.om.registry;

// Java imports
import java.util.Vector;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.registry.MetaInfo;

/**
 * Interface for manipulatin the security entries on the registry entries
 *
 * 
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a> 
 * @version $Id: SecurityEntry.java,v 1.6 2004/02/23 03:11:39 jford Exp $
 */

public interface SecurityEntry {

    /** Getter for property accesses.
     * @return Value of property accesses.
     */
    public Vector getAccesses();
    
    /** Setter for property accesses.
     * @param accesses New value of property accesses.
     */
    public void setAccesses(Vector accesses);
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName();
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name);
    
    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription();
    
    /** Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description);
    
    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef();
    
    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef);
    
    /** Getter for property title.
     * @return Value of property title.
     */
    public String getTitle();
    
    /** Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(String title);
    
    /** Getter for property metaInfo.
     * @return Value of property metaInfo.
     */
    public MetaInfo getMetaInfo();
    
    /** Setter for property metaInfo.
     * @param metaInfo New value of property metaInfo.
     */
    public void setMetaInfo(MetaInfo metaInfo);
    
    /** Getter for property hidden.
     * @return Value of property hidden.
     */
    public boolean isHidden();
    
    /** Setter for property hidden.
     * @param hidden New value of property hidden.
     */
    public void setHidden(boolean hidden);
    
    /** Getter for property id.
     * @return Value of property id.
     */
    public long getId();
    
    /**
     * Aututhorizes action for a role
     *
     * @param role requesting action
     * @param action being requested
     * @return <CODE>true</CODE> if action is allowed for role
     */    
    public boolean allowsRole(String role, String action);

	/**
	 * Authorizes action for a group
	 *
	 * @param group requesting action
	 * @param action being requested
	 * @return <CODE>true</CODE> if action is allowed for role
	 */    
	public boolean allowsGroup(String group, String action);

	/**
	 * Authorizes action for a group role
	 *
	 * @param group requesting action
	 * @param role requesting action 
	 * @param action being requested
	 * @return <CODE>true</CODE> if action is allowed for role
	 */    
	public boolean allowsGroupRole(String group, String role, String action);
    
    /**
     * Aututhorizes action for a named user
     *
     * @param userName requesting action
     * @param action being requested
     * @return <CODE>true</CODE> if action is allowed for named user
     */    
    public boolean allowsUser(String userName, String action);
    
    /**
     * Aututhorizes action for a named user
     *
     * @param userName requesting action
     * @param action being requested
     * @param ownerUserName Onwers username 
     * @return <CODE>true</CODE> if action is allowed for named user
     */    
    boolean allowsUser(String userName, String action, String ownerUserName);
    
    /**
     * Grants access for a specific action to a specific role
     * for this SecurityEntry.  This grants specific access ignores
     * "*" action, if it exists.
     * @param String action The action we are granting access to.
     * @param String role The role that will receive access to this action.
     * @return boolean Whether or not the access was granted. Basically,
     *  a <code>false</code> means that this role already has specific access.
     */
       boolean grantRoleAccess(String action, String role);

         /**
         * Checks whether a role is specifically allowed to access the request action
         * This method ignores the "*" action and is here to play a maintenance role.
         * @param String action name of action to check
         * @param String role name of role to verify access for
         * @return boolean whether or not the <code>role</code> has access
         * to this specific action.
         */
        boolean allowsSpecificRole(String action, String role);

   /**
	* Grants access for a specific action to a specific group
	* for this SecurityEntry.  This grants specific access ignores
	* "*" action, if it exists.
	* @param String action The action we are granting access to.
	* @param String group The group that will receive access to this action.
	* @return boolean Whether or not the access was granted. Basically,
	*  a <code>false</code> means that this group already has specific access.
	*/
    boolean grantGroupAccess(String action, String group);

   /**
	* Checks whether a group is specifically allowed to access the request action
	* This method ignores the "*" action and is here to play a maintenance role.
	* @param String action name of action to check
	* @param String group name of group to verify access for
	* @return boolean whether or not the <code>group</code> has access
	* to this specific action.
	*/
	boolean allowsSpecificGroup(String action, String group);

	/**
	 * Grants access for a specific action to a specific group role
	 * for this SecurityEntry.  This grants specific access ignores
	 * "*" action, if it exists.
	 * @param String action The action we are granting access to.
	 * @param String group The group that will receive access to this action.
	 * @param String role The role that will receive access to this action. 
	 * @return boolean Whether or not the access was granted. Basically,
	 *  a <code>false</code> means that this group role already has specific access.
	 */
	 boolean grantGroupRoleAccess(String action, String group, String role);

	/**
	 * Checks whether a group role is specifically allowed to access the request action
	 * This method ignores the "*" action and is here to play a maintenance role.
	 * @param String action name of action to check
	 * @param String group name of group to verify access for
	 * @param String role name of group to verify access for
	 * @return boolean whether or not the <code>group role</code> has access
	 * to this specific action.
	 */
	 boolean allowsSpecificGroupRole(String action, String group, String role);

        /**
         * Returns the SecurityAccess object for the <code>action</code>
         * requested or null if no specific access is defined for this action.
         * The "*" does change this, if an action is not specifically defined
         * in the registry, null is returned
         * @param SecurityEntry entry SecurityEntry to check against
         * @param String action The action we want the access for.
         * @return SecurityAccess that is defined for this action or
         * <code>null</code> if one is not <strong>specifically defined</strong>
         */
        SecurityAccess getAccess(String action);

        /**
         * Checks whether a user is specifically allowed to access the request action
         * This method ignores the "*" action and is here to play a maintenance role.
         * @param String action name of action to check
         * @param String user name of user to verify access for
         * @return boolean whether or not the <code>user</code> has access
         * to this specific action.
         */
        boolean allowsSpecificUser(String action, String user);

    /**
     * Grants access for a specific action to a specific user
     * for this SecurityEntry.  This grants specific access ignores
     * "*" action, if it exists.
     * @param String action The action we are granting access to.
     * @param String user The user that will receive access to this action.
     * @return boolean Whether or not the access was granted. Basically,
     *  a <code>false</code> means that this role already has specific access.
     */
        boolean grantUserAccess(String action, String user);

        /**
         * Removes a security access for the named action.
         * This does not take into account the "*" action when
         * the "*" is not the named action.
         * @param String access name of access to remove in its entirety
         */
        void revokeAccess(String action);
        
        /**
        * Removes a user's access to a specific action.
        * @param action Action to remove access from.
        * @param role The role whose access we are revoking.
        * @return boolean Whehter or not the access existed and
        * was removed.
        */
        boolean revokeUserAccess(String action, String user);

    /**
     * Removes a role's access to a specific action.
     * @param action Action to remove access from.
     * @param role The role whose access we are revoking.
     * @return boolean Whehter or not the access existed and
     * was removed.
     */
        boolean revokeRoleAccess(String action, String role);
        
	/**
	 * Removes a group's access to a specific action.
	 * @param action Action to remove access from.
	 * @param group The group whose access we are revoking.
	 * @return boolean Whether or not the access existed and
	 * was removed.
	 */
	boolean revokeGroupAccess(String action, String group);

	/**
	 * Removes a group role's access to a specific action.
	 * @param action Action to remove access from.
	 * @param group The group whose access we are revoking.
	 * @param role The role whose access we are revoking. 
	 * @return boolean Whether or not the access existed and
	 * was removed.
	 */
	boolean revokeGroupRoleAccess(String action, String group, String role);
        
}
