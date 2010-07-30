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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.om.security.Permission;
import org.apache.jetspeed.om.security.ldap.LDAPPermission;
import org.apache.jetspeed.om.security.ldap.LDAPRole;
import org.apache.jetspeed.services.JetspeedLDAP;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.ldap.LDAPURL;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.InsufficientPrivilegeException;
import org.apache.jetspeed.services.security.JetspeedSecurityCache;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.jetspeed.services.security.PermissionException;
import org.apache.jetspeed.services.security.PermissionManagement;
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
public class LDAPPermissionManagement extends TurbineBaseService implements
    PermissionManagement {
  // Constants
  private final static String CASCADE_DELETE = "programmatic.cascade.delete";

  private final static String CACHING_ENABLE = "caching.enable";

  private final static boolean DEFAULT_CASCADE_DELETE = true;

  private final static boolean DEFAULT_CACHING_ENABLE = true;

  private final static String[] ATTRS = { "ou", "uid", "permissionname" };

  // Instance variables
  private JetspeedRunDataService runDataService = null;

  private boolean cascadeDelete = false;

  private boolean cachingEnable = false;

  // /////////////////////////////////////////////////////////////////////////
  // Permission Management Interfaces
  // /////////////////////////////////////////////////////////////////////////

  /**
   * Retrieves all <code>Permission</code>s for a given roleName principal.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param roleName
   *            a role name identity to be retrieved.
   * @return Iterator over all permissions associated to the role principal.
   * @exception PermissionException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Iterator getPermissions(String roleName)
      throws JetspeedSecurityException {
    Vector perms = new Vector();
    // BasicAttributes attr = new BasicAttributes();
    LDAPRole role;
    LDAPPermission permission;
    Vector userurls;

    try {
      if (cachingEnable) {
        Iterator iterator = JetspeedSecurityCache.getPermissions(roleName);
        if (iterator != null) {
          return iterator;
        }
      }

      userurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=roles"),
          "(&(uid=" + roleName + ")(objectclass=jetspeedrole))", ATTRS, true);

      if (userurls.size() > 0) {
        role = new LDAPRole((LDAPURL) ((Vector) userurls.elementAt(0))
            .firstElement());
        for (Enumeration enu = role.getRolePermissions().elements(); enu
            .hasMoreElements();) {
          permission = new LDAPPermission((String) enu.nextElement(), false);
          perms.add(permission);
        }
      }
    } catch (Exception e) {
      throw new PermissionException("Failed to retrieve permissions ", e);
    }

    return perms.iterator();
  }

  /**
   * Retrieves all <code>Permission</code>s.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @return Iterator over all permissions.
   * @exception PermissionException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Iterator getPermissions() throws JetspeedSecurityException

  {
    // BasicAttributes attr = new BasicAttributes();
    Vector permissions = new Vector();
    Vector permissionurls;

    try {
      permissionurls = JetspeedLDAP.search(JetspeedLDAP
          .buildURL("ou=permissions"), "(objectclass=jetspeedpermission)",
          ATTRS, true);

      if (permissionurls.size() > 0) {
        for (Enumeration enu = permissionurls.elements(); enu.hasMoreElements();) {
          permissions.add(new LDAPPermission((LDAPURL) (((Vector) enu
              .nextElement()).firstElement())));
        }
      } else {
        throw new PermissionException("No permission ");
      }
    } catch (Exception e) {
      throw new PermissionException("Failed to retrieve permissions ", e);
    }
    return permissions.iterator();
  }

  /**
   * Adds a <code>Permission</code> into permanent storage.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception PermissionException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void addPermission(Permission permission)
      throws JetspeedSecurityException {
    if (permissionExists(permission.getName())) {
      throw new PermissionException("The permission '" + permission.getName()
          + "' already exists");
    }
    try {
      new LDAPPermission(permission.getName(), true).update(true);
    } catch (Exception e) {
      throw new PermissionException("Failed to create permission '"
          + permission.getName() + "'", e);
    }
  }

  /**
   * Saves a <code>Permission</code> into permanent storage.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @exception PermissionException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void savePermission(Permission permission)
      throws JetspeedSecurityException {
  }

  /**
   * Removes a <code>Permission</code> from the permanent store.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param permissionName
   *            the principal identity of the permission to be retrieved.
   * @exception PermissionException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void removePermission(String permissionName)
      throws JetspeedSecurityException {
    try {
      LDAPPermission permission = new LDAPPermission(permissionName, false);
      JetspeedLDAP.deleteEntry(permission.getldapurl());

      if (cascadeDelete) {
      }

      if (cachingEnable) {
        JetspeedSecurityCache.removeAllPermissions(permissionName);
      }
    } catch (Exception e) {
      throw new PermissionException("Failed to remove permission '"
          + permissionName + "'", e);
    }
  }

  /**
   * Grants a permission to a role.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param roleName
   *            grant a permission to this role.
   * 
   * @param permissionName
   *            the permission to grant to the role.
   * @exception PermissionException
   *                when the security provider has a general failure retrieving
   *                permissions.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void grantPermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    // BasicAttributes attr = new BasicAttributes();
    LDAPRole role;
    LDAPPermission permission;

    try {
      role = (LDAPRole) JetspeedSecurity.getRole(roleName);
      permission = (LDAPPermission) JetspeedSecurity
          .getPermission(permissionName);

      role.addRolePermissions(permissionName);
      role.update(false);

      if (cachingEnable) {
        JetspeedSecurityCache.addPermission(roleName, permission);
      }
    } catch (Exception e) {
      throw new PermissionException("Grant permission '" + permissionName
          + "' to role '" + roleName + "' failed: ", e);
    }
  }

  /**
   * Revokes a permission from a role.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param roleName
   *            grant a permission to this role.
   * @param permissionName
   *            the permission to grant to the role.
   * @exception PermissionException
   *                when the security provider has a general failure retrieving
   *                permissions.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public void revokePermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    LDAPRole role;
    Vector userurls;

    try {
      userurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=roles"),
          "(&(uid=" + roleName + ")(objectclass=jetspeedrole))", ATTRS, true);

      if (userurls.size() == 0) {
        throw new PermissionException("Role '" + roleName + "' does not exist!");
      } else {
        role = new LDAPRole((LDAPURL) ((Vector) userurls.elementAt(0))
            .firstElement());
        role.getRolePermissions().remove(permissionName);
        role.update(false);

        if (cachingEnable) {
          JetspeedSecurityCache.removePermission(roleName, permissionName);
        }
      }
    } catch (Exception e) {
      throw new PermissionException("Revoke permission '" + permissionName
          + "' to role '" + roleName + "' failed: ", e);
    }
  }

  /**
   * Checks for the relationship of role has a permission. Returns true when the
   * role has the given permission.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param roleName
   *            grant a permission to this role.
   * @param permissionName
   *            the permission to grant to the role.
   * @exception PermissionException
   *                when the security provider has a general failure retrieving
   *                permissions.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public boolean hasPermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    // BasicAttributes attr = new BasicAttributes();
    LDAPRole role;
    Vector userurls;

    try {
      if (cachingEnable) {
        return JetspeedSecurityCache.hasPermission(roleName, permissionName);
      }

      userurls = JetspeedLDAP.search(JetspeedLDAP.buildURL("ou=roles"),
          "(&(uid=" + roleName + ")(objectclass=jetspeedrole))", ATTRS, true);

      if (userurls.size() > 0) {
        role = new LDAPRole((LDAPURL) ((Vector) userurls.elementAt(0))
            .firstElement());
        return role.permissionExists(permissionName);
      }
    } catch (Exception e) {
      throw new PermissionException("Grant permission '" + permissionName
          + "' to role '" + roleName + "' failed: ", e);
    }
    return false;
  }

  /**
   * Retrieves a single <code>Permission</code> for a given permissionName
   * principal.
   * 
   * The security service may optionally check the current user context to
   * determine if the requestor has permission to perform this action.
   * 
   * @param permissionName
   *            a permission principal identity to be retrieved.
   * @return Permission the permission record retrieved.
   * @exception PermissionException
   *                when the security provider has a general failure.
   * @exception InsufficientPrivilegeException
   *                when the requestor is denied due to insufficient privilege
   */
  public Permission getPermission(String permissionName)
      throws JetspeedSecurityException {
    if (permissionExists(permissionName)) {
      return new LDAPPermission(permissionName, false);
    } else {
      throw new PermissionException("Unknown permission '" + permissionName
          + "'");
    }
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
   * Check whether a specified permission exists.
   * 
   * The login name is used for looking up the account.
   * 
   * @param permissionName
   *            the name of the permission to check for existence.
   * @return true if the specified account exists
   * @throws PermissionException
   *             if there was a general db access error
   * 
   */
  protected boolean permissionExists(String permissionName)
      throws PermissionException {
    // BasicAttributes attr = new BasicAttributes();
    Vector permissionurls;

    try {
      permissionurls = JetspeedLDAP.search(JetspeedLDAP
          .buildURL("ou=permissions"), "(&(uid=" + permissionName
          + ")(objectclass=jetspeedpermission))", ATTRS, true);

      if (permissionurls.size() > 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new PermissionException("Failed to retrieve permission ", e);
    }
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
    cachingEnable = serviceConf.getBoolean(CACHING_ENABLE, cachingEnable);
    setInit(true);
  }
}
