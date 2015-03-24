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

package org.apache.jetspeed.services;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Permission;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.CredentialsManagement;
import org.apache.jetspeed.services.security.GroupManagement;
import org.apache.jetspeed.services.security.JetspeedGroupManagement;
import org.apache.jetspeed.services.security.JetspeedPermissionManagement;
import org.apache.jetspeed.services.security.JetspeedRoleManagement;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.jetspeed.services.security.LoginException;
import org.apache.jetspeed.services.security.PermissionManagement;
import org.apache.jetspeed.services.security.PortalAuthentication;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.services.security.RoleManagement;
import org.apache.jetspeed.services.security.UserManagement;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

/**
 * <P>
 * This is a commodity static accessor class around the
 * <code>JetspeedSecurityService</code>
 * </P>
 * 
 * @see org.apache.jetspeed.services.security.JetspeedSecurityService
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor </a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 */

abstract public class JetspeedSecurity /* extends TurbineSecurity */
{
  public static final String PERMISSION_VIEW = "view";

  public static final String PERMISSION_CUSTOMIZE = "customize";

  public static final String PERMISSION_MAXIMIZE = "maximize";

  public static final String PERMISSION_MINIMIZE = "minimize";

  public static final String PERMISSION_PERSONALIZE = "personalize";

  public static final String PERMISSION_DETACH = "detach";

  public static final String PERMISSION_CLOSE = "close";

  public static final String PERMISSION_INFO = "info";

  public static final String PERMISSION_PRINT_FRIENDLY = "print_friendly";

  // Jetspeed security only has multiple groups.
  // Access Control checks cab be role-based or group-role-based.
  // If a user has the specified group-role for the resource, then the user can
  // access that resource
  public static final String JETSPEED_GROUP = "Jetspeed";

  public static final String JETSPEED_GROUP_ID = "1";

  public static final String JETSPEED_ROLE_USER = "user";

  public static final String JETSPEED_ROLE_ADMIN = "admin";

  /**
   * Alphabet consisting of upper and lowercase letters A-Z and the digits 0-9
   * Used to make a random password.
   */
  public static final char[] NUMBERS_AND_LETTERS_ALPHABET = { 'A', 'B', 'C',
      'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
      'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e',
      'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
      't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
      '7', '8', '9', };

  /**
   * Alphabet consisting of lowercase letters a-z and the digits 0-9 Used to
   * make a random password.
   */
  public static final char[] LC_NUMBERS_AND_LETTERS_ALPHABET = { 'a', 'b', 'c',
      'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
      'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
      '5', '6', '7', '8', '9', };

  /**
   * Commodity method for getting a reference to the service singleton
   */
  public static JetspeedSecurityService getService() {
    return (JetspeedSecurityService) TurbineServices.getInstance().getService(
        JetspeedSecurityService.SERVICE_NAME);
  }

  // ////////////////////////////////////////////////////////////////////////
  // PortalAuthentication
  // ///////////////////////////////////////////////////////////////////////

  /**
   * @see PortalAuthentication#login
   */
  public static JetspeedUser login(String username, String password)
      throws LoginException {
    return JetspeedAuthentication.login(username, password);
  }

  /**
   * @see PortalAuthentication#getAnonymousUser
   */
  public static JetspeedUser getAnonymousUser() throws LoginException {
    return JetspeedAuthentication.getAnonymousUser();
  }

  /**
   * @see PortalAuthentication#logout
   */
  public static void logout() throws LoginException {
    JetspeedAuthentication.logout();
  }

  // ////////////////////////////////////////////////////////////////////////
  // PortalAuthorization
  // ///////////////////////////////////////////////////////////////////////

  /**
   * @see JetspeedPortalAccessController#checkPermission
   */
  public static boolean checkPermission(JetspeedUser user, Entry entry,
      String action) {
    return JetspeedPortalAccessController.checkPermission(user, entry, action);
  }

  /**
   * @see JetspeedPortalAccessController#checkPermission
   */
  public static boolean checkPermission(JetspeedUser user, Portlet portlet,
      String action) {
    return JetspeedPortalAccessController
        .checkPermission(user, portlet, action);
  }

  /**
   * @see JetspeedPortalAccessController#checkPermission
   */
  public static boolean checkPermission(JetspeedUser user,
      PortalResource resource, String action) {
    return JetspeedPortalAccessController.checkPermission(user, resource,
        action);
  }

  // ////////////////////////////////////////////////////////////////////////
  // UserManagement
  // ///////////////////////////////////////////////////////////////////////

  /**
   * @see UserManagement#getUser
   */
  public static JetspeedUser getUser(Principal principal)
      throws JetspeedSecurityException {
    return JetspeedUserManagement.getUser(principal);
  }

  /**
   * @see UserManagement#getUsers
   */
  public static Iterator getUsers() throws JetspeedSecurityException {
    return JetspeedUserManagement.getUsers();
  }

  /**
   * @see UserManagement#saveUser
   */
  public static void saveUser(JetspeedUser user)
      throws JetspeedSecurityException {
    JetspeedUserManagement.saveUser(user);
  }

  /**
   * @see UserManagement#addUser
   */
  public static void addUser(JetspeedUser user)
      throws JetspeedSecurityException {
    JetspeedUserManagement.addUser(user);
  }

  /**
   * @see UserManagement#getUsers(String)
   */
  public static Iterator getUsers(String filter)
      throws JetspeedSecurityException {
    return JetspeedUserManagement.getUsers(filter);
  }

  /**
   * @see UserManagement#removeUser
   */
  public static void removeUser(Principal principal)
      throws JetspeedSecurityException {
    JetspeedUserManagement.removeUser(principal);
  }

  /**
   * @see UserManagement#getUser
   */
  public static JetspeedUser getUser(String username)
      throws JetspeedSecurityException {
    return JetspeedUserManagement.getUser(new UserNamePrincipal(username));
  }

  /**
   * @see UserManagement#getUser
   */
  public static JetspeedUser getUser(RunData rundata, String username)
      throws JetspeedSecurityException {
    return JetspeedUserManagement.getUser(rundata, new UserNamePrincipal(
        username));
  }

  /**
   * @see UserManagement#removeUser
   */
  public static void removeUser(String username)
      throws JetspeedSecurityException {
    JetspeedUserManagement.removeUser(new UserNamePrincipal(username));
  }

  // ////////////////////////////////////////////////////////////////////////
  // CredentialsManagement
  // ///////////////////////////////////////////////////////////////////////

  /**
   * @see CredentialsManagement#changePassword
   */
  public static void changePassword(JetspeedUser user, String oldPassword,
      String newPassword) throws JetspeedSecurityException {
    JetspeedUserManagement.changePassword(user, oldPassword, newPassword);

  }

  /**
   * @see CredentialsManagement#forcePassword
   */
  public static void forcePassword(JetspeedUser user, String password)
      throws JetspeedSecurityException {
    JetspeedUserManagement.forcePassword(user, password);
  }

  /**
   * @see CredentialsManagement#encryptPassword
   */
  public static String encryptPassword(String password)
      throws JetspeedSecurityException {
    return JetspeedUserManagement.encryptPassword(password);
  }

  // ////////////////////////////////////////////////////////////////////////
  // Role Management
  // ///////////////////////////////////////////////////////////////////////

  /**
   * @see RoleManagement#getRoles(String)
   */
  public static Iterator getRoles(String username)
      throws JetspeedSecurityException {
    return JetspeedRoleManagement.getRoles(username);
  }

  /**
   * @see RoleManagement#getRoles
   */
  public static Iterator getRoles() throws JetspeedSecurityException {
    return JetspeedRoleManagement.getRoles();
  }

  /**
   * @see RoleManagement#addRole
   */
  public static void addRole(Role role) throws JetspeedSecurityException {
    JetspeedRoleManagement.addRole(role);
  }

  /**
   * @see RoleManagement#saveRole
   */
  public static void saveRole(Role role) throws JetspeedSecurityException {
    JetspeedRoleManagement.saveRole(role);
  }

  /**
   * @see RoleManagement#removeRole
   */
  public static void removeRole(String rolename)
      throws JetspeedSecurityException {
    JetspeedRoleManagement.removeRole(rolename);
  }

  /**
   * @see RoleManagement#grantRole
   */
  public static void grantRole(String username, String rolename)
      throws JetspeedSecurityException {
    JetspeedRoleManagement.grantRole(username, rolename);
  }

  /**
   * @see RoleManagement#grantRole
   */
  public static void grantRole(String username, String rolename,
      String groupname) throws JetspeedSecurityException {
    JetspeedRoleManagement.grantRole(username, rolename, groupname);
  }

  /**
   * @see RoleManagement#revokeRole
   */
  public static void revokeRole(String username, String rolename)
      throws JetspeedSecurityException {
    JetspeedRoleManagement.revokeRole(username, rolename);
  }

  /**
   * @see RoleManagement#revokeRole()
   */
  public static void revokeRole(String username, String rolename,
      String groupname) throws JetspeedSecurityException {
    JetspeedRoleManagement.revokeRole(username, rolename, groupname);
  }

  /**
   * @see RoleManagement#hasRole
   */
  public static boolean hasRole(String username, String rolename)
      throws JetspeedSecurityException {
    return JetspeedRoleManagement.hasRole(username, rolename);
  }

  public static boolean hasRole(String username, String rolename,
      String groupname) throws JetspeedSecurityException {
    return JetspeedRoleManagement.hasRole(username, rolename, groupname);
  }

  /**
   * @see RoleManagement#getRole
   */
  public static Role getRole(String rolename) throws JetspeedSecurityException {
    return JetspeedRoleManagement.getRole(rolename);
  }

  // ////////////////////////////////////////////////////////////////////////
  // Group Management
  // ///////////////////////////////////////////////////////////////////////

  /**
   * @see GroupManagement#getGroups(String)
   */
  public static Iterator getGroups(String username)
      throws JetspeedSecurityException {
    return JetspeedGroupManagement.getGroups(username);
  }

  /**
   * @see GroupManagement#getGroups
   */
  public static Iterator getGroups() throws JetspeedSecurityException {
    return JetspeedGroupManagement.getGroups();
  }

  /**
   * @see GroupManagement#addGroup
   */
  public static void addGroup(Group group) throws JetspeedSecurityException {
    JetspeedGroupManagement.addGroup(group);
  }

  /**
   * @see GroupManagement#saveGroup
   */
  public static void saveGroup(Group group) throws JetspeedSecurityException {
    JetspeedGroupManagement.saveGroup(group);
  }

  /**
   * @see GroupManagement#removeGroup
   */
  public static void removeGroup(String groupname)
      throws JetspeedSecurityException {
    JetspeedGroupManagement.removeGroup(groupname);
  }

  /**
   * @see GroupManagement#joinGroup
   */
  public static void joinGroup(String username, String groupname)
      throws JetspeedSecurityException {
    JetspeedGroupManagement.joinGroup(username, groupname);
  }

  /**
   * @see GroupManagement#joinGroup(String username, String groupname, String
   *      rolename)
   */
  public static void joinGroup(String username, String groupname,
      String rolename) throws JetspeedSecurityException {
    JetspeedGroupManagement.joinGroup(username, groupname, rolename);
  }

  /**
   * @see GroupManagement#revokeGroup
   */
  public static void unjoinGroup(String username, String groupname)
      throws JetspeedSecurityException {
    JetspeedGroupManagement.unjoinGroup(username, groupname);
  }

  /**
   * @see GroupManagement#revokeGroup(String username, String groupname, String
   *      rolename)
   */
  public static void unjoinGroup(String username, String groupname,
      String rolename) throws JetspeedSecurityException {
    JetspeedGroupManagement.unjoinGroup(username, groupname, rolename);
  }

  /**
   * @see GroupManagement#inGroup
   */
  public static boolean inGroup(String username, String groupname)
      throws JetspeedSecurityException {
    return JetspeedGroupManagement.inGroup(username, groupname);
  }

  /**
   * @see GroupManagement#getGroup
   */
  public static Group getGroup(String groupname)
      throws JetspeedSecurityException {
    return JetspeedGroupManagement.getGroup(groupname);
  }

  // ////////////////////////////////////////////////////////////////////////
  //
  // Required JetspeedSecurity Functions
  //
  // Required Features provided by default JetspeedSecurity
  //
  // ////////////////////////////////////////////////////////////////////////

  /**
   * @see JetspeedSecurityService#getUserInstance
   */
  public static JetspeedUser getUserInstance() {
    return ((JetspeedSecurityService) getService()).getUserInstance();
  }

  // ////////////////////////////////////////////////////////////////////////
  //
  // Optional JetspeedSecurity Features
  //
  // Features are not required to be implemented by Security Provider
  //
  // ////////////////////////////////////////////////////////////////////////

  /**
   * @see JetspeedSecurityService#convertUserName
   */
  public static String convertUserName(String username) {
    return ((JetspeedSecurityService) getService()).convertUserName(username);
  }

  /**
   * @see JetspeedSecurityService#convertPassword
   */
  public static String convertPassword(String password) {
    return ((JetspeedSecurityService) getService()).convertPassword(password);
  }

  /**
   * @see JetspeedSecurityService#checkDisableAcccount
   */
  public static boolean checkDisableAccount(String username) {
    return ((JetspeedSecurityService) getService())
        .checkDisableAccount(username);
  }

  /**
   * @see JetspeedSecurityService#isDisableCountCheckEnabled
   */
  public static boolean isDisableAccountCheckEnabled() {
    return ((JetspeedSecurityService) getService())
        .isDisableAccountCheckEnabled();
  }

  /**
   * @see JetspeedSecurityService#resetDisableAccountCheck
   */
  public static void resetDisableAccountCheck(String username) {
    ((JetspeedSecurityService) getService()).resetDisableAccountCheck(username);
  }

  /**
   * @see JetspeedSecurityService#areActionsDisabledForAnon
   */
  public static boolean areActionsDisabledForAnon() {
    return ((JetspeedSecurityService) getService()).areActionsDisabledForAnon();
  }

  /**
   * @see JetspeedSecurityService#areActionsDisabledForAllUsers
   */
  public static boolean areActionsDisabledForAllUsers() {
    return ((JetspeedSecurityService) getService())
        .areActionsDisabledForAllUsers();
  }

  /*
   * @see JetspeedSecurityService#getAnonymousUserName
   */
  public static String getAnonymousUserName() {
    return ((JetspeedSecurityService) getService()).getAnonymousUserName();
  }

  /*
   * @see JetspeedSecurityService#getAdminRoles
   */
  public static List getAdminRoles() {
    return ((JetspeedSecurityService) getService()).getAdminRoles();
  }

  /*
   * @see JetspeedSecurityService#hasAdminRole
   */
  public static boolean hasAdminRole(User user) {
    return ((JetspeedSecurityService) getService()).hasAdminRole(user);
  }

  // ////////////////////////////////////////////////////////////////////////
  //
  // PortalAuthorization - Helpers
  //
  // ///////////////////////////////////////////////////////////////////////

  /**
   * @see JetspeedSecurityService#checkPermission
   */
  public static boolean checkPermission(JetspeedRunData runData, String action,
      Portlet portlet) {
    return checkPermission(runData.getJetspeedUser(), portlet, action);
  }

  /**
   * @see JetspeedSecurityService#checkPermission
   */
  public static boolean checkPermission(JetspeedRunData runData, String action,
      RegistryEntry entry) {
    return checkPermission(runData.getJetspeedUser(),
        new PortalResource(entry), action);
  }

  // ////////////////////////////////////////////////////////////////////////
  // Permission Management
  // ///////////////////////////////////////////////////////////////////////

  /**
   * @see PermissionManagement#getPermissions(String)
   */
  public static Iterator getPermissions(String rolename)
      throws JetspeedSecurityException {
    return JetspeedPermissionManagement.getPermissions(rolename);
  }

  /**
   * @see PermissionManagement#getPermissions
   */
  public static Iterator getPermissions() throws JetspeedSecurityException {
    return JetspeedPermissionManagement.getPermissions();
  }

  /**
   * @see PermissionManagement#addPermission
   */
  public static void addPermission(Permission permission)
      throws JetspeedSecurityException {
    JetspeedPermissionManagement.addPermission(permission);
  }

  /**
   * @see PermissionManagement#savePermission
   */
  public static void savePermission(Permission permission)
      throws JetspeedSecurityException {
    JetspeedPermissionManagement.savePermission(permission);
  }

  /**
   * @see PermissionManagement#removePermission
   */
  public static void removePermission(String permissionName)
      throws JetspeedSecurityException {
    JetspeedPermissionManagement.removePermission(permissionName);
  }

  /**
   * @see PermissionManagement#grantPermission
   */
  public static void grantPermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    JetspeedPermissionManagement.grantPermission(roleName, permissionName);
  }

  /**
   * @see PermissionManagement#revokePermission
   */
  public static void revokePermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    JetspeedPermissionManagement.revokePermission(roleName, permissionName);
  }

  /**
   * @see PermissionManagement#hasPermission
   */
  public static boolean hasPermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    return JetspeedPermissionManagement.hasPermission(roleName, permissionName);
  }

  /**
   * @see PermissionManagement#getPermission
   */
  public static Permission getPermission(String permissionName)
      throws JetspeedSecurityException {
    return JetspeedPermissionManagement.getPermission(permissionName);
  }

  /**
   * A utility method that will generate a password consisting of random numbers
   * and letters of length N from a specified character array
   * 
   * @param length
   * @param characters
   * @return String
   * @throws JetspeedSecurityException
   * @author <a href="mailto:ben.woodward@bbc.co.uk">Ben Woodward </a>
   */
  private static String generatePassword(int length, char[] characters)
      throws JetspeedSecurityException {
    String password = "";
    int randomNumber = 0;
    for (int ia = 0; ia < length; ia++) {
      randomNumber = (int) (Math.random() * NUMBERS_AND_LETTERS_ALPHABET.length);
      password += characters[randomNumber];
    }
    return password;
  }

  /**
   * A utility method that will generate a password consisting of random numbers
   * and letters of length N
   * 
   * @param length
   * @return String
   * @throws JetspeedSecurityException
   * @author <a href="mailto:ben.woodward@bbc.co.uk">Ben Woodward </a>
   */
  public static String generateMixedCasePassword(int length)
      throws JetspeedSecurityException {
    return generatePassword(length, NUMBERS_AND_LETTERS_ALPHABET);
  }

  /**
   * A utility method that will generate a lowercase password consisting of
   * random numbers and letters of length N
   * 
   * @param length
   * @return String
   * @throws JetspeedSecurityException
   * @author <a href="mailto:ben.woodward@bbc.co.uk">Ben Woodward </a>
   */
  public static String generateLowerCasePassword(int length)
      throws JetspeedSecurityException {
    return generatePassword(length, LC_NUMBERS_AND_LETTERS_ALPHABET)
        .toLowerCase();
  }

  /**
   * A utility method that will generate an uppercase password consisting of
   * random numbers and letters of length N
   * 
   * @param length
   * @return String
   * @throws JetspeedSecurityException
   */
  public static String generateUpperCasePassword(int length)
      throws JetspeedSecurityException {
    return generatePassword(length, LC_NUMBERS_AND_LETTERS_ALPHABET)
        .toUpperCase();
  }

  /**
   * Utility method for retreiving the correct security reference based on
   * profile and registry information.
   */
  public static SecurityReference getSecurityReference(Entry entry,
      JetspeedRunData rundata) {
    PortletEntry pEntry = null;
    if (entry != null) {
      pEntry = (PortletEntry) Registry.getEntry(Registry.PORTLET, entry
          .getParent());
    }
    SecurityReference securityRef = null;
    // First, check the profile level security
    if (entry != null) {
      securityRef = entry.getSecurityRef();
    }

    // If no profile level security has been assigned, use the registry
    if (securityRef == null && pEntry != null) {
      securityRef = pEntry.getSecurityRef();
    }

    // still no security? go with the default.
    if (securityRef == null && rundata != null) {
      securityRef = PortalToolkit.getDefaultSecurityRef(rundata.getProfile());
    }

    return securityRef;
  }

  /**
   * Checks where the security of this Entry is actually defined.
   * 
   * @return int
   *         <ul>
   *         <li><b>0 </b> if there is security assigned at the profile level.
   *         </li>
   *         <li><b>1 </b> if there is security assigned at the registry level.
   *         </li>
   *         <li><b>2 </b> if the 2 previous assertion are false (inheriting)
   *         </li>
   *         </ul>
   */
  public static int getSecuritySource(Entry entry, JetspeedRunData rundata) {
    PortletEntry pEntry = (PortletEntry) Registry.getEntry(Registry.PORTLET,
        entry.getParent());
    if (entry.getSecurityRef() != null) {
      return 0;
    }

    if (pEntry != null && pEntry.getSecurityRef() != null) {
      return 1;
    }

    return 2;
  }

}
