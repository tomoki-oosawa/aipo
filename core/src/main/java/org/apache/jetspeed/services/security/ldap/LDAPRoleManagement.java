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

package org.apache.jetspeed.services.security.ldap;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.om.security.ldap.LDAPRole;
import org.apache.jetspeed.om.security.ldap.LDAPUser;
import org.apache.jetspeed.services.JetspeedLDAP;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.CachedAcl;
import org.apache.jetspeed.services.security.InsufficientPrivilegeException;
import org.apache.jetspeed.services.security.JetspeedSecurityCache;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.jetspeed.services.security.RoleException;
import org.apache.jetspeed.services.security.RoleManagement;
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
 */
public class LDAPRoleManagement extends TurbineBaseService implements
    RoleManagement {
  // Constants
  private final static String CASCADE_DELETE = "programmatic.cascade.delete";

  private final static String CACHING_ENABLE = "caching.enable";

  private final static boolean DEFAULT_CASCADE_DELETE = true;

  private final static boolean DEFAULT_CACHING_ENABLE = true;

  private final static String[] ATTRS = { "ou", "uid", "rolename",
      "rolepermissions" };

  // Instance variables
  private JetspeedRunDataService runDataService = null;

  private boolean cascadeDelete = false;

  private boolean cachingEnable = false;

  // /////////////////////////////////////////////////////////////////////////
  // Role Management Interfaces
  // /////////////////////////////////////////////////////////////////////////

  /**
   * Retrieves all <code>Role</code>s for a given username principal.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param username
   *            a user principal identity to be retrieved.
   * @return Iterator over all roles associated to the user principal.
   * @exception RoleException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Iterator getRoles(String username) throws JetspeedSecurityException {
    StringTokenizer st;
    LDAPUser user;
    try {
      if (cachingEnable) {
        Iterator result = JetspeedSecurityCache.getRoles(username);
        if (null != result) {
          return result;
        }
      }
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new RoleException("Failed to Retrieve User: ", e);
    }

    Vector roles = new Vector();

    try {
      for (Enumeration enu = user.getGroupRoles().elements(); enu
          .hasMoreElements();) {
        st = new StringTokenizer((String) enu.nextElement(), ",");
        st.nextToken();
        roles.add(new LDAPRole(st.nextToken(), false));
      }
    } catch (Exception e) {
      throw new RoleException("Failed to retrieve groups ", e);
    }
    return roles.iterator();

  }

  /**
   * Retrieves all <code>Role</code>s.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @return Iterator over all roles.
   * @exception RoleException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Iterator getRoles() throws JetspeedSecurityException {
    Vector roles = new Vector();
    Vector roleurls;

    try {
      roleurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=roles"),
          "(objectclass=jetspeedrole)", ATTRS, true);
      if (roleurls.size() > 0) {
        for (Enumeration enu = roleurls.elements(); enu.hasMoreElements();) {
          roles.add(new LDAPRole((LDAPURL) (((Vector) enu.nextElement())
              .firstElement())));
        }
      } else {
        throw new UnknownUserException("No role ");
      }
    } catch (Exception e) {
      throw new RoleException("Failed to retrieve roles ", e);
    }
    return roles.iterator();

  }

  /**
   * Adds a <code>Role</code> into permanent storage.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception RoleException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void addRole(Role role) throws JetspeedSecurityException {
    LDAPRole ldapRole = null;

    if (roleExists(role.getName())) {
      throw new RoleException("The role '" + role.getName()
          + "' already exists");
    }

    try {
      ldapRole = new LDAPRole(role.getName(), true);
      ldapRole.update(true);
    } catch (Exception e) {
      throw new RoleException("Failed to create role '" + role.getName() + "'",
          e);
    }

    if (cachingEnable) {
      JetspeedSecurityCache.addRole(ldapRole);
    }

    try {
      addDefaultRolePSML(ldapRole);
    } catch (Exception e) {
      try {
        removeRole(ldapRole.getName());
      } catch (Exception e2) {
      }
      throw new RoleException("failed to add default PSML for Role resource", e);
    }
  }

  protected void addDefaultRolePSML(Role role) throws RoleException {
    try {
      JetspeedRunDataService runDataService = (JetspeedRunDataService) TurbineServices
          .getInstance().getService(RunDataService.SERVICE_NAME);
      JetspeedRunData rundata = runDataService.getCurrentRunData();
      Profile profile = Profiler.createProfile();
      profile.setRole(role);
      profile.setMediaType("html");
      Profiler.createProfile(rundata, profile);
    } catch (ProfileException e) {
      try {
        removeRole(role.getName());
      } catch (Exception e2) {
        e.printStackTrace();
      }
      throw new RoleException("Failed to create Role PSML", e);
    }
  }

  /**
   * Saves a <code>Role</code> into permanent storage.
   * 
   * The security service can throw a <code>NotUniqueEntityException</code>
   * when the public credentials fail to meet the security provider-specific
   * unique constraints. The security service may optionally check the current
   * user context to determine if the requestor has permission to perform this
   * action.
   * 
   * @exception RoleException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void saveRole(Role role) throws JetspeedSecurityException {
    if (!roleExists(role.getName())) {
      throw new RoleException("The role '" + role.getName()
          + "' doesn't exists");
    }

    try {

    } catch (Exception e) {
      throw new RoleException("Failed to create role '" + role.getName() + "'",
          e);
    }

  }

  /**
   * Removes a <code>Role</code> from the permanent store.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param roleName
   *            the principal identity of the role to be retrieved.
   * @exception RoleException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void removeRole(String roleName) throws JetspeedSecurityException {
    try {
      LDAPRole role = new LDAPRole(roleName, false);
      JetspeedLDAP.deleteEntry(role.getldapurl());
      PsmlManager.removeRoleDocuments(role);

      if (cascadeDelete) {
      }

      if (cachingEnable) {
        JetspeedSecurityCache.removeAllRoles(roleName);
      }
    } catch (Exception e) {
      throw new RoleException("Failed to remove group '" + roleName + "'", e);
    }
  }

  /**
   * Grants a role to a user.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception RoleException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void grantRole(String username, String roleName)
      throws JetspeedSecurityException {
    grantRole(username, roleName, JetspeedSecurity.JETSPEED_GROUP);
  }

  public void grantRole(String username, String roleName, String groupName)
      throws JetspeedSecurityException {
    LDAPUser user;
    LDAPRole role;
    try {
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
      role = (LDAPRole) JetspeedSecurity.getRole(roleName);
    } catch (JetspeedSecurityException e)

    {
      throw new RoleException("Failed to Retrieve User or Role: ", e);
    }

    try {
      user.addGroupRole(groupName, roleName);
      user.update(false);

      if (cachingEnable) {
        JetspeedSecurityCache.addRole(username, role);
      }
    } catch (Exception e) {
      throw new RoleException("Failed to add role info ", e);
    }
  }

  /**
   * Revokes a role from a user.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception RoleException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void revokeRole(String username, String rolename)
      throws JetspeedSecurityException {
    revokeRole(username, rolename, JetspeedSecurity.JETSPEED_GROUP);
  }

  public void revokeRole(String username, String rolename, String groupname)
      throws JetspeedSecurityException {
    LDAPUser user;

    try {
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new RoleException("Failed to Retrieve User: ", e);
    }

    try {
      user.removeGroupRole(groupname, rolename);
      user.update(false);

      if (cachingEnable) {
        JetspeedSecurityCache.removeRole(username, rolename, groupname);
      }
    } catch (Exception e) {
      throw new RoleException("Failed to add role info ", e);
    }
  }

  /**
   * Checks for the relationship of user has a role. Returns true when the user
   * has the given role.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception RoleException
   *                when the security provider has a general failure retrieving
   *                users.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public boolean hasRole(String username, String roleName)
      throws JetspeedSecurityException {
    return hasRole(username, roleName, JetspeedSecurity.JETSPEED_GROUP);
  }

  public boolean hasRole(String username, String roleName, String groupName)
      throws JetspeedSecurityException {
    StringTokenizer st;
    LDAPUser user;
    try {
      if (cachingEnable) {
        CachedAcl acl = JetspeedSecurityCache.getAcl(username);
        if (null != acl) {
          return acl.hasRole(roleName, groupName);
        }
      }
      user = (LDAPUser) JetspeedSecurity
          .getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new RoleException("Failed to Retrieve User: ", e);
    }
    try {
      for (Enumeration enu = user.getGroupRoles().elements(); enu
          .hasMoreElements();) {
        st = new StringTokenizer((String) enu.nextElement(), ",");
        String gn = st.nextToken();
        String rn = st.nextToken();
        if (rn.equalsIgnoreCase(roleName) && gn.equalsIgnoreCase(groupName)) {
          return true;
        }
      }
    } catch (Exception e) {
      throw new RoleException("Failed to retrieve roles ", e);
    }
    return false;
  }

  /**
   * Retrieves a single <code>Role</code> for a given roleName principal.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param roleName
   *            a role principal identity to be retrieved.
   * @return Role the role record retrieved.
   * @exception RoleException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Role getRole(String roleName) throws JetspeedSecurityException {
    Vector roleurls;

    try {
      roleurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=roles"),
          "(&(uid=" + roleName + ")(objectclass=jetspeedrole))", ATTRS, true);

      if (roleurls.size() == 1) {
        return new LDAPRole((LDAPURL) ((Vector) roleurls.elementAt(0))
            .firstElement());
      } else if (roleurls.size() > 1) {
        throw new RoleException("Multiple roles with same name");
      }
    } catch (Exception e) {
      throw new RoleException("Failed to retrieve roles ", e);
    }
    throw new RoleException("Unknown role '" + roleName + "'");
  }

  // /////////////////////////////////////////////////////////////////////////
  // Internal
  // /////////////////////////////////////////////////////////////////////////

  protected JetspeedRunData getRunData() {
    JetspeedRunData rundata = null;
    if (this.runDataService != null) {
      rundata = this.runDataService.getCurrentRunData();
    }
    return rundata;
  }

  /**
   * Check whether a specified role exists.
   * 
   * The login name is used for looking up the account.
   * 
   * @param roleName
   *            the name of the role to check for existence.
   * @return true if the specified account exists
   * @throws RoleException
   *             if there was a general db access error
   * 
   */
  protected boolean roleExists(String roleName) throws RoleException {
    Vector roleurls;

    try {
      roleurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=roles"),
          "(&(uid=" + roleName + ")(objectclass=jetspeedrole))", ATTRS, true);
      if (roleurls.size() > 0) {
        return true;
      }
    } catch (Exception e) {
      throw new RoleException("Failed to retrieve roles ", e);
    }
    return false;
  }

  // /////////////////////////////////////////////////////////////////////////
  // Service Init
  // /////////////////////////////////////////////////////////////////////////

  /**
   * This is the early initialization method called by the Turbine
   * <code>Service</code> framework
   * 
   * @param conf
   *            The <code>ServletConfig</code>
   * @exception throws
   *                a <code>InitializationException</code> if the service
   *                fails to initialize
   */
  public synchronized void init(ServletConfig conf)
      throws InitializationException {
    if (getInit())
      return;

    super.init(conf);

    // get configuration parameters from Jetspeed Resources
    ResourceService serviceConf = ((TurbineServices) TurbineServices
        .getInstance()).getResources(JetspeedSecurityService.SERVICE_NAME);

    this.runDataService = (JetspeedRunDataService) TurbineServices
        .getInstance().getService(RunDataService.SERVICE_NAME);

    cascadeDelete = serviceConf.getBoolean(CASCADE_DELETE,
        DEFAULT_CASCADE_DELETE);
    cachingEnable = serviceConf.getBoolean(CACHING_ENABLE,
        DEFAULT_CACHING_ENABLE);
    setInit(true);
  }

}
