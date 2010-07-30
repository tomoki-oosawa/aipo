/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.services.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.security.BaseJetspeedGroupRole;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.GroupRole;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.CachedAcl;
import org.apache.jetspeed.services.security.GroupManagement;
import org.apache.jetspeed.services.security.JetspeedSecurityCache;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.jetspeed.services.security.RoleException;
import org.apache.jetspeed.services.security.RoleManagement;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.rundata.RunDataService;

import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineRole;
import com.aimluck.eip.cayenne.om.security.TurbineRolePermission;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.orm.DatabaseOrmService;

/**
 * ロールを管理するクラスです。 <br />
 *
 */
public class ALRoleManagement extends TurbineBaseService implements
    RoleManagement {
  private JetspeedRunDataService runDataService = null;

  private final static String CASCADE_DELETE = "programmatic.cascade.delete";

  private final static boolean DEFAULT_CASCADE_DELETE = true;

  private boolean cascadeDelete;

  private final static String CACHING_ENABLE = "caching.enable";

  private boolean cachingEnable = true;

  /**
   *
   */
  public Iterator<?> getRoles(String username) throws JetspeedSecurityException {
    JetspeedUser user = null;
    try {
      if (cachingEnable) {
        Iterator<?> result = JetspeedSecurityCache.getRoles(username);
        if (null != result) {
          return result;
        }
      }

      user = JetspeedSecurity.getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new RoleException("Failed to Retrieve User: ", e);
    }

    List<?> rels;
    HashMap<String,GroupRole> roles;

    try {
      Expression exp = ExpressionFactory.matchDbExp(
          TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(user.getUserId()));

      SelectQuery query = new SelectQuery(TurbineUserGroupRole.class, exp);

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      rels = dataContext.performQuery(query);

      if (rels.size() > 0) {
        roles = new HashMap<String,GroupRole>(rels.size());
      } else {
        roles = new HashMap<String,GroupRole>();
      }

      for (int ix = 0; ix < rels.size(); ix++) {
        TurbineUserGroupRole rel = (TurbineUserGroupRole) rels.get(ix);
        Role role = rel.getTurbineRole();
        Group group = rel.getTurbineGroup();
        GroupRole groupRole = new BaseJetspeedGroupRole();
        groupRole.setGroup(group);
        groupRole.setRole(role);
        roles.put(group.getName() + role.getName(), groupRole);
      }
    } catch (Exception e) {
      throw new RoleException("Failed to retrieve roles ", e);
    }
    return roles.values().iterator();
  }

  /**
   *
   */
  public Iterator<?> getRoles() throws JetspeedSecurityException {
    List<?> roles;
    try {
      SelectQuery query = new SelectQuery(TurbineRole.class);

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      roles = dataContext.performQuery(query);
    } catch (Exception e) {
      throw new RoleException("Failed to retrieve roles ", e);
    }
    return roles.iterator();
  }

  /**
   *
   */
  public void addRole(Role role) throws JetspeedSecurityException {

    if (roleExists(role.getName())) {
      throw new RoleException("The role '" + role.getName()
          + "' already exists");
    }

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      // 新規オブジェクトモデル
      TurbineRole trole = (TurbineRole) dataContext
          .createAndRegisterNewObject(TurbineRole.class);
      trole.setName(role.getName());
      trole.setObjectdata(null);
      dataContext.commitChanges();

    } catch (Exception e) {
      throw new RoleException("Failed to create role '" + role.getName() + "'",
          e);
    }

    if (cachingEnable) {
      JetspeedSecurityCache.addRole(role);
    }

    try {
      addDefaultRolePSML(role);
    } catch (Exception e) {
      try {
        removeRole(role.getName());
      } catch (Exception e2) {
      }
      throw new RoleException("failed to add default PSML for Role resource", e);
    }
  }

  /**
   *
   * @param role
   * @throws RoleException
   */
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
      }
      throw new RoleException("Failed to create Role PSML", e);
    }
  }

  /**
   *
   */
  public void saveRole(Role role) throws JetspeedSecurityException {
    if (!roleExists(role.getName())) {
      throw new RoleException("The role '" + role.getName()
          + "' doesn't exists");
    }

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      if (role instanceof TurbineRole) {
        dataContext.commitChanges();
      } else {
        throw new RoleException(
            "TurbineRoleManagment: Role is not a Turbine role, cannot update");
      }

    } catch (Exception e) {
      throw new RoleException("Failed to create role '" + role.getName() + "'",
          e);
    }

  }

  /**
   *
   */
  public void removeRole(String rolename) throws JetspeedSecurityException {
    try {
      Role role = this.getRole(rolename);

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      if (cascadeDelete) {
        // CASCADE TURBINE_USER_GROUP_ROLE, TURBINE_ROLE_PERMISSION
        Expression exp1 = ExpressionFactory.matchDbExp(
            TurbineRole.ROLE_ID_PK_COLUMN, Integer.valueOf(role.getId()));
        SelectQuery query1 = new SelectQuery(TurbineUserGroupRole.class, exp1);
        List<?> roles = dataContext.performQuery(query1);
        dataContext.deleteObjects(roles);

        Expression exp2 = ExpressionFactory.matchDbExp(
            TurbineRolePermission.ROLE_ID_PK_COLUMN, Integer.valueOf(role
                .getId()));
        SelectQuery query2 = new SelectQuery(TurbineRolePermission.class, exp2);
        List<?> rolepermmisions = dataContext.performQuery(query2);
        dataContext.deleteObjects(rolepermmisions);

      }

      dataContext.deleteObject((TurbineRole) role);

      PsmlManager.removeRoleDocuments(role);

      dataContext.commitChanges();

      if (cachingEnable) {
        JetspeedSecurityCache.removeAllRoles(rolename);
      }
    } catch (Exception e) {

      throw new RoleException("Failed to remove role '" + rolename + "'", e);
    } finally {

    }

  }

  /**
   *
   */
  public void grantRole(String username, String rolename)
      throws JetspeedSecurityException {
    grantRole(username, rolename, GroupManagement.DEFAULT_GROUP_NAME);
  }

  public void grantRole(String username, String rolename, String groupname)
      throws JetspeedSecurityException {
    try {
      JetspeedUser user = JetspeedSecurity.getUser(username);
      Role role = this.getRole(rolename);
      Group group = JetspeedSecurity.getGroup(groupname);

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      // 新規オブジェクトモデル
      TurbineUserGroupRole user_group_role = (TurbineUserGroupRole) dataContext
          .createAndRegisterNewObject(TurbineUserGroupRole.class);
      TurbineUser tuser = (TurbineUser) DataObjectUtils.objectForPK(
          dataContext, TurbineUser.class, Integer.valueOf(user.getUserId()));
      user_group_role.setTurbineUser(tuser);
      user_group_role.setTurbineGroup((TurbineGroup) group);
      user_group_role.setTurbineRole((TurbineRole) role);
      dataContext.commitChanges();

      if (cachingEnable) {
        JetspeedSecurityCache.addRole(username, role, group);
      }
    } catch (Exception e) {
      throw new RoleException("Grant role '" + rolename + "' to user '"
          + username + "' failed: ", e);
    }
  }

  /**
   *
   */
  public void revokeRole(String username, String rolename)
      throws JetspeedSecurityException {
    revokeRole(username, rolename, GroupManagement.DEFAULT_GROUP_NAME);
  }

  public void revokeRole(String username, String rolename, String groupname)
      throws JetspeedSecurityException {
    try {
      JetspeedUser user = JetspeedSecurity.getUser(username);
      Role role = this.getRole(rolename);
      Group group = JetspeedSecurity.getGroup(groupname);

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      Expression exp1 = ExpressionFactory.matchDbExp(
          TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(user.getUserId()));
      Expression exp2 = ExpressionFactory.matchDbExp(
          TurbineGroup.GROUP_ID_PK_COLUMN, Integer.valueOf(group.getId()));
      Expression exp3 = ExpressionFactory.matchDbExp(
          TurbineRole.ROLE_ID_PK_COLUMN, Integer.valueOf(role.getId()));
      SelectQuery query = new SelectQuery(TurbineUserGroupRole.class);
      query.setQualifier(exp1);
      query.andQualifier(exp2);
      query.andQualifier(exp3);
      List<?> list = dataContext.performQuery(query);

      dataContext.deleteObjects(list);
      dataContext.commitChanges();

      if (cachingEnable) {
        JetspeedSecurityCache.removeRole(username, rolename, groupname);
      }

    } catch (Exception e) {
      throw new RoleException("Revoke role '" + rolename + "' to user '"
          + username + "' failed: ", e);
    }

  }

  /**
   *
   */
  public boolean hasRole(String username, String rolename)
      throws JetspeedSecurityException {
    return hasRole(username, rolename, GroupManagement.DEFAULT_GROUP_NAME);
  }

  public boolean hasRole(String username, String rolename, String groupname)
      throws JetspeedSecurityException {
    List<?> roles;

    try {
      if (cachingEnable) {
        CachedAcl acl = JetspeedSecurityCache.getAcl(username);
        if (null != acl) {
          return acl.hasRole(rolename, groupname);
        }
      }
      JetspeedUser user = JetspeedSecurity.getUser(username);
      Role role = this.getRole(rolename);
      Group group = JetspeedSecurity.getGroup(groupname);

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      Expression exp1 = ExpressionFactory.matchDbExp(
          TurbineUser.USER_ID_PK_COLUMN, Integer.valueOf(user.getUserId()));
      Expression exp2 = ExpressionFactory.matchDbExp(
          TurbineGroup.GROUP_ID_PK_COLUMN, Integer.valueOf(group.getId()));
      Expression exp3 = ExpressionFactory.matchDbExp(
          TurbineRole.ROLE_ID_PK_COLUMN, Integer.valueOf(role.getId()));

      SelectQuery query = new SelectQuery(TurbineUserGroupRole.class);
      query.setQualifier(exp1);
      query.andQualifier(exp2);
      query.andQualifier(exp3);
      roles = dataContext.performQuery(query);

    } catch (Exception e) {
      throw new RoleException("Failed to check role '" + rolename + "'", e);
    }
    return (roles.size() > 0);
  }

  /**
   *
   */
  public Role getRole(String rolename) throws JetspeedSecurityException {
    List<?> roles;

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      Expression exp = ExpressionFactory.matchExp(
          TurbineRole.ROLE_NAME_PROPERTY, rolename);
      SelectQuery query = new SelectQuery(TurbineRole.class, exp);
      roles = dataContext.performQuery(query);

    } catch (Exception e) {
      throw new RoleException("Failed to retrieve role '" + rolename + "'", e);
    }
    if (roles.size() > 1) {
      throw new RoleException("Multiple Roles with same rolename '" + rolename
          + "'");
    }
    if (roles.size() == 1) {
      TurbineRole role = (TurbineRole) roles.get(0);
      return (Role) role;
    }
    throw new RoleException("Unknown role '" + rolename + "'");

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
   * @param roleName
   * @return
   * @throws RoleException
   */
  protected boolean roleExists(String roleName) throws RoleException {
    List<?> roles;

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      Expression exp = ExpressionFactory.matchExp(
          TurbineRole.ROLE_NAME_PROPERTY, roleName);
      SelectQuery query = new SelectQuery(TurbineRole.class, exp);
      roles = dataContext.performQuery(query);

    } catch (Exception e) {
      throw new RoleException("Failed to check account's presence", e);
    }
    if (roles.size() < 1) {
      return false;
    }
    return true;
  }

  /**
   *
   */
  public synchronized void init(ServletConfig conf)
      throws InitializationException {
    if (getInit()) {
      return;
    }

    super.init(conf);

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
