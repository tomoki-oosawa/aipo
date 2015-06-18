/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.services.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.Permission;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
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

import com.aimluck.eip.cayenne.om.security.TurbinePermission;
import com.aimluck.eip.cayenne.om.security.TurbineRolePermission;
import com.aimluck.eip.orm.Database;

/**
 * パーミッションを管理するクラスです。 <br />
 * 
 */
public class ALPermissionManagement extends TurbineBaseService implements
    PermissionManagement {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALPermissionManagement.class.getName());

  private JetspeedRunDataService runDataService = null;

  private final static String CASCADE_DELETE = "programmatic.cascade.delete";

  private final static boolean DEFAULT_CASCADE_DELETE = true;

  private final static String CONFIG_SYSTEM_PERMISSIONS = "system.permissions";

  private boolean cascadeDelete;

  private final static String CACHING_ENABLE = "caching.enable";

  private boolean cachingEnable = true;

  private Vector<?> systemPermissions = null;

  /**
   *
   */
  public Iterator<?> getPermissions(String rolename)
      throws JetspeedSecurityException {
    Role role = null;
    try {
      if (cachingEnable) {
        Iterator<?> iterator = JetspeedSecurityCache.getPermissions(rolename);
        if (iterator != null) {
          return iterator;
        }
      }
      role = JetspeedSecurity.getRole(rolename);
    } catch (JetspeedSecurityException e) {
      logger.error("Failed to Retrieve Role: ", e);
      throw new PermissionException("Failed to Retrieve Role: ", e);
    }

    List<TurbineRolePermission> rels;
    HashMap<String, Permission> perms;

    try {
      Expression exp =
        ExpressionFactory.matchDbExp(
          TurbineRolePermission.ROLE_ID_PK_COLUMN,
          Integer.valueOf(role.getId()));
      rels = Database.query(TurbineRolePermission.class, exp).fetchList();

      if (rels.size() > 0) {
        perms = new HashMap<String, Permission>(rels.size());
      } else {
        perms = new HashMap<String, Permission>();
      }

      for (int ix = 0; ix < rels.size(); ix++) {
        TurbineRolePermission rel = rels.get(ix);
        Permission perm = rel.getTurbinePermission();
        perms.put(perm.getName(), perm);
      }
    } catch (Exception e) {
      logger.error("Failed to retrieve permissions ", e);
      throw new PermissionException("Failed to retrieve permissions ", e);
    }
    return perms.values().iterator();
  }

  /**
   *
   */
  public Iterator<TurbinePermission> getPermissions()
      throws JetspeedSecurityException {
    List<TurbinePermission> permissions;
    try {
      permissions = Database.query(TurbinePermission.class).fetchList();

    } catch (Exception e) {
      logger.error("Failed to retrieve permissions ", e);
      throw new PermissionException("Failed to retrieve permissions ", e);
    }
    return permissions.iterator();
  }

  /**
   *
   */
  public void addPermission(Permission permission)
      throws JetspeedSecurityException {
    if (permissionExists(permission.getName())) {
      throw new PermissionException("The permission '"
        + permission.getName()
        + "' already exists");
    }

    try {
      // 新規オブジェクトモデル
      TurbinePermission tpermission = Database.create(TurbinePermission.class);
      tpermission.setName(permission.getName());
      tpermission.setOBJECTDATA(null);
      Database.commit();

    } catch (Exception e) {
      Database.rollback();
      String message =
        "Failed to create permission '" + permission.getName() + "'";
      logger.error(message, e);
      throw new PermissionException(message, e);
    }
  }

  /**
   *
   */
  public void savePermission(Permission permission)
      throws JetspeedSecurityException {

    if (!permissionExists(permission.getName())) {
      throw new PermissionException("The permission '"
        + permission.getName()
        + "' doesn't exists");
    }

    try {

      if (permission instanceof TurbinePermission) {
        Database.commit();
      } else {
        throw new PermissionException(
          "TurbinePermissionManagment: Permission is not a Turbine permission, cannot update");
      }

    } catch (Exception e) {
      Database.rollback();
      String message =
        "Failed to create permission '" + permission.getName() + "'";
      logger.error(message, e);
      throw new PermissionException(message, e);
    }

  }

  /**
   *
   */
  public void removePermission(String permissionName)
      throws JetspeedSecurityException {
    try {

      if (systemPermissions.contains(permissionName)) {
        throw new PermissionException("["
          + permissionName
          + "] is a system permission and cannot be removed");
      }
      Permission permission = this.getPermission(permissionName);

      if (cascadeDelete) {
        Expression exp =
          ExpressionFactory.matchDbExp(
            TurbineRolePermission.PERMISSION_ID_PK_COLUMN,
            Integer.valueOf(permission.getId()));
        Database.query(TurbineRolePermission.class, exp).deleteAll();
      }
      Database.delete((TurbineRolePermission) permission);
      Database.commit();

      if (cachingEnable) {
        JetspeedSecurityCache.removeAllPermissions(permissionName);
      }
    } catch (Exception e) {

      String message = "Failed to remove permission '" + permissionName + "'";
      logger.error(message, e);
      throw new PermissionException(message, e);
    } finally {

    }

  }

  /**
   *
   */
  public void grantPermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    try {
      Role role = JetspeedSecurity.getRole(roleName);
      Permission permission = this.getPermission(permissionName);

      // 新規オブジェクトモデル
      TurbineRolePermission role_permission =
        Database.create(TurbineRolePermission.class);
      role_permission.setRoleId(Integer.parseInt(role.getId()));
      role_permission.setPermissionId(Integer.parseInt(permission.getId()));
      Database.commit();

    } catch (Exception e) {
      String message =
        "Grant permission '"
          + permissionName
          + "' to role '"
          + roleName
          + "' failed: ";
      logger.error(message, e);
      throw new PermissionException(message, e);
    }
  }

  /**
   *
   */
  public void revokePermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    try {
      Role role = JetspeedSecurity.getRole(roleName);
      Permission permission = this.getPermission(permissionName);

      Expression exp1 =
        ExpressionFactory.matchDbExp(
          TurbineRolePermission.ROLE_ID_PK_COLUMN,
          Integer.valueOf(role.getId()));
      Expression exp2 =
        ExpressionFactory.matchDbExp(
          TurbineRolePermission.PERMISSION_ID_PK_COLUMN,
          Integer.valueOf(permission.getId()));

      Database
        .query(TurbineRolePermission.class, exp1)
        .andQualifier(exp2)
        .deleteAll();

      if (cachingEnable) {
        JetspeedSecurityCache.removePermission(roleName, permissionName);
      }
    } catch (Exception e) {
      Database.rollback();
      String message =
        "Revoke permission '"
          + permissionName
          + "' to role '"
          + roleName
          + "' failed: ";
      logger.error(message, e);
      throw new PermissionException(message, e);
    }

  }

  /**
   *
   */
  public boolean hasPermission(String roleName, String permissionName)
      throws JetspeedSecurityException {
    List<TurbineRolePermission> permissions;

    try {
      if (cachingEnable) {
        return JetspeedSecurityCache.hasPermission(roleName, permissionName);
      }

      Role role = JetspeedSecurity.getRole(roleName);
      Permission permission = this.getPermission(permissionName);

      Expression exp1 =
        ExpressionFactory.matchDbExp(
          TurbineRolePermission.ROLE_ID_PK_COLUMN,
          Integer.valueOf(role.getId()));
      Expression exp2 =
        ExpressionFactory.matchDbExp(
          TurbineRolePermission.PERMISSION_ID_PK_COLUMN,
          Integer.valueOf(permission.getId()));

      permissions =
        Database
          .query(TurbineRolePermission.class, exp1)
          .andQualifier(exp2)
          .fetchList();

    } catch (Exception e) {
      String message = "Failed to check permission '" + permissionName + "'";
      logger.error(message, e);
      throw new PermissionException(message, e);
    }
    return (permissions.size() > 0);
  }

  /**
   *
   */
  public Permission getPermission(String permissionName)
      throws JetspeedSecurityException {
    List<TurbinePermission> permissions;

    try {

      permissions = Database.query(TurbinePermission.class).fetchList();

    } catch (Exception e) {
      String message = "Failed to retrieve permission '" + permissionName + "'";
      logger.error(message, e);
      throw new PermissionException(message, e);
    }
    if (permissions.size() > 1) {
      throw new PermissionException(
        "Multiple Permissions with same permissionname '"
          + permissionName
          + "'");
    }
    if (permissions.size() == 1) {
      TurbinePermission permission = permissions.get(0);
      return permission;
    }
    throw new PermissionException("Unknown permission '" + permissionName + "'");

  }

  protected JetspeedRunData getRunData() {
    JetspeedRunData rundata = null;
    if (this.runDataService != null) {
      rundata = this.runDataService.getCurrentRunData();
    }
    return rundata;
  }

  /**
   * 
   * @param permissionName
   * @return
   * @throws PermissionException
   */
  protected boolean permissionExists(String permissionName)
      throws PermissionException {
    List<TurbinePermission> permissions;
    try {
      Expression exp =
        ExpressionFactory.matchExp(
          TurbinePermission.PERMISSION_NAME_PROPERTY,
          permissionName);
      permissions = Database.query(TurbinePermission.class, exp).fetchList();
    } catch (Exception e) {
      logger.error("Failed to check account's presence", e);
      throw new PermissionException("Failed to check account's presence", e);
    }
    if (permissions.size() < 1) {
      return false;
    }
    return true;
  }

  /**
   *
   */
  @Override
  public synchronized void init(ServletConfig conf)
      throws InitializationException {
    if (getInit()) {
      return;
    }

    super.init(conf);

    ResourceService serviceConf =
      ((TurbineServices) TurbineServices.getInstance())
        .getResources(JetspeedSecurityService.SERVICE_NAME);

    this.runDataService =
      (JetspeedRunDataService) TurbineServices.getInstance().getService(
        RunDataService.SERVICE_NAME);

    cascadeDelete =
      serviceConf.getBoolean(CASCADE_DELETE, DEFAULT_CASCADE_DELETE);
    cachingEnable = serviceConf.getBoolean(CACHING_ENABLE, cachingEnable);
    systemPermissions =
      serviceConf.getVector(CONFIG_SYSTEM_PERMISSIONS, new Vector<Object>());
    setInit(true);
  }

}
