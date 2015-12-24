/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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

import javax.servlet.ServletConfig;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunDataService;
import org.apache.jetspeed.services.security.GroupException;
import org.apache.jetspeed.services.security.GroupManagement;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.JetspeedSecurityService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.rundata.RunDataService;

import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineRole;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * グループを管理するクラスです。 <br />
 * 
 */
public class ALGroupManagement extends TurbineBaseService implements
    GroupManagement {

  private JetspeedRunDataService runDataService = null;

  private final static String CONFIG_DEFAULT_ROLE = "role.default";

  private final static String CASCADE_DELETE = "programmatic.cascade.delete";

  private final static boolean DEFAULT_CASCADE_DELETE = true;

  private boolean cascadeDelete;

  protected String defaultRole = "user";

  /**
   * グループを追加します。
   * 
   * @param group
   */
  @Override
  public void addGroup(Group group) throws JetspeedSecurityException {
    if (groupExists(group.getName())) {
      throw new GroupException("The group '"
        + group.getName()
        + "' already exists");
    }

    try {
      Database.commit();
    } catch (Exception e) {
      throw new GroupException("Failed to create group '"
        + group.getName()
        + "'", e);
    }

    try {
      addDefaultGroupPSML(group);
    } catch (Exception e) {
      try {
        removeGroup(group.getName());
      } catch (Exception e2) {
      }
      throw new GroupException(
        "failed to add default PSML for Group resource",
        e);
    }
  }

  /**
   *
   */
  @Override
  public Iterator<Group> getGroups(String username)
      throws JetspeedSecurityException {
    JetspeedUser user = null;
    try {
      user = JetspeedSecurity.getUser(new UserNamePrincipal(username));
    } catch (JetspeedSecurityException e) {
      throw new GroupException("Failed to Retrieve User: ", e);
    }

    Expression exp =
      ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, user
        .getUserId());
    SelectQuery<TurbineUserGroupRole> query =
      Database.query(TurbineUserGroupRole.class, exp);

    List<TurbineUserGroupRole> rels;
    HashMap<String, Group> groups;

    try {
      rels = query.fetchList();

      if (rels.size() > 0) {
        groups = new HashMap<String, Group>(rels.size());
      } else {
        groups = new HashMap<String, Group>();
      }

      for (int ix = 0; ix < rels.size(); ix++) {
        TurbineUserGroupRole rel = rels.get(ix);
        Group group = rel.getTurbineGroup();
        groups.put(group.getName(), group);
      }
    } catch (Exception e) {
      throw new GroupException("Failed to retrieve groups ", e);
    }
    return groups.values().iterator();
  }

  /**
   *
   */
  @Override
  public Iterator<TurbineGroup> getGroups() throws JetspeedSecurityException {
    List<TurbineGroup> groups;
    try {
      SelectQuery<TurbineGroup> query = Database.query(TurbineGroup.class);
      groups = query.fetchList();
    } catch (Exception e) {
      throw new GroupException("Failed to retrieve groups ", e);
    }
    return groups.iterator();

  }

  /**
   * 
   * @param group
   * @throws GroupException
   */
  protected void addDefaultGroupPSML(Group group) throws GroupException {
    try {
      String orgId = Database.getDomainName();

      JetspeedRunDataService runDataService =
        (JetspeedRunDataService) TurbineServices.getInstance().getService(
          RunDataService.SERVICE_NAME);
      JetspeedRunData rundata = runDataService.getCurrentRunData();
      Profile profile = Profiler.createProfile();
      profile.setGroup(group);
      profile.setMediaType("html");
      profile.setOrgName(orgId);
      Profiler.createProfile(rundata, profile);
    } catch (ProfileException e) {
      try {
        removeGroup(group.getName());
      } catch (Exception e2) {
      }
      throw new GroupException("Failed to create Group PSML", e);
    }
  }

  /**
   *
   */
  @Override
  public void saveGroup(Group group) throws JetspeedSecurityException {
    if (!groupExists(group.getName())) {
      throw new GroupException("The group '"
        + group.getName()
        + "' doesn't exists");
    }

    try {

      if (group instanceof TurbineGroup) {
        Database.commit();
      } else {
        throw new GroupException(
          "TurbineGroupManagment: Group is not a Turbine group, cannot update");
      }

    } catch (Exception e) {
      throw new GroupException("Failed to create group '"
        + group.getName()
        + "'", e);
    }

  }

  /**
   *
   */
  @Override
  public void removeGroup(String groupname) throws JetspeedSecurityException {
    try {
      Group group = this.getGroup(groupname);

      if (cascadeDelete) {
        Expression exp =
          ExpressionFactory.matchDbExp(TurbineGroup.GROUP_ID_PK_COLUMN, Integer
            .valueOf(group.getId()));
        SelectQuery<TurbineUserGroupRole> query =
          Database.query(TurbineUserGroupRole.class, exp);

        query.deleteAll();

      }
      Database.delete((TurbineGroup) group);

      PsmlManager.removeGroupDocuments(group);

      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      throw new GroupException("Failed to remove group '" + groupname + "'", e);
    } finally {

    }

  }

  /**
   *
   */
  @Override
  public void joinGroup(String username, String groupname)
      throws JetspeedSecurityException {
    joinGroup(username, groupname, defaultRole);
  }

  /**
   *
   */
  @Override
  public void joinGroup(String username, String groupname, String rolename)
      throws JetspeedSecurityException {
    try {
      JetspeedUser user = JetspeedSecurity.getUser(username);
      Group group = this.getGroup(groupname);
      Role role = JetspeedSecurity.getRole(rolename);

      // 新規オブジェクトモデル
      TurbineUserGroupRole user_group_role =
        Database.create(TurbineUserGroupRole.class);

      TurbineUser tuser =
        ALEipUtils.getTurbineUser(Integer.valueOf(user.getUserId()));

      user_group_role.setTurbineUser(tuser);
      user_group_role.setTurbineGroup((TurbineGroup) group);
      user_group_role.setTurbineRole((TurbineRole) role);
      Database.commit();

    } catch (Exception e) {
      Database.rollback();
      throw new GroupException("Join group '"
        + groupname
        + "' to user '"
        + username
        + "' failed: ", e);
    }
  }

  /**
   *
   */
  @Override
  public void unjoinGroup(String username, String groupname)
      throws JetspeedSecurityException {
    unjoinGroup(username, groupname, defaultRole);
  }

  /**
   *
   */
  @Override
  public void unjoinGroup(String username, String groupname, String rolename)
      throws JetspeedSecurityException {
    try {
      JetspeedUser user = JetspeedSecurity.getUser(username);
      Group group = this.getGroup(groupname);
      Role role = JetspeedSecurity.getRole(rolename);

      Expression exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(user.getUserId()));
      Expression exp2 =
        ExpressionFactory.matchDbExp(TurbineGroup.GROUP_ID_PK_COLUMN, Integer
          .valueOf(group.getId()));
      Expression exp3 =
        ExpressionFactory.matchDbExp(TurbineRole.ROLE_ID_PK_COLUMN, Integer
          .valueOf(role.getId()));
      SelectQuery<TurbineUserGroupRole> query =
        Database.query(TurbineUserGroupRole.class);
      query.setQualifier(exp1);
      query.andQualifier(exp2);
      query.andQualifier(exp3);

      query.deleteAll();

      Database.commit();

    } catch (Exception e) {
      throw new GroupException("Unjoin group '"
        + groupname
        + "' to user '"
        + username
        + "' failed: ", e);
    }
  }

  /**
   *
   */
  @Override
  public boolean inGroup(String username, String groupname)
      throws JetspeedSecurityException {
    List<TurbineUserGroupRole> groups;

    try {
      JetspeedUser user = JetspeedSecurity.getUser(username);
      Group group = this.getGroup(groupname);

      Expression exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, user
          .getUserId());
      Expression exp2 =
        ExpressionFactory.matchDbExp(TurbineGroup.GROUP_ID_PK_COLUMN, group
          .getId());

      SelectQuery<TurbineUserGroupRole> query =
        Database.query(TurbineUserGroupRole.class);
      query.setQualifier(exp1);
      query.andQualifier(exp2);

      groups = query.fetchList();
    } catch (Exception e) {
      throw new GroupException("Failed to check group '" + groupname + "'", e);
    }
    return (groups.size() > 0);
  }

  /**
   *
   */
  @Override
  public Group getGroup(String groupname) throws JetspeedSecurityException {
    List<TurbineGroup> groups;
    try {
      Expression exp =
        ExpressionFactory.matchExp(TurbineGroup.GROUP_NAME_PROPERTY, groupname);

      SelectQuery<TurbineGroup> query = Database.query(TurbineGroup.class, exp);

      groups = query.fetchList();
    } catch (Exception e) {
      throw new GroupException(
        "Failed to retrieve group '" + groupname + "'",
        e);
    }
    if (groups.size() > 1) {
      throw new GroupException("Multiple Groups with same groupname '"
        + groupname
        + "'");
    }
    if (groups.size() == 1) {
      TurbineGroup group = groups.get(0);
      return group;
    }
    throw new GroupException("Unknown group '" + groupname + "'");

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
   * @param groupName
   * @return
   * @throws GroupException
   */
  protected boolean groupExists(String groupName) throws GroupException {
    List<TurbineGroup> groups;
    try {
      Expression exp =
        ExpressionFactory.matchExp(TurbineGroup.GROUP_NAME_PROPERTY, groupName);

      groups = Database.query(TurbineGroup.class, exp).fetchList();
    } catch (Exception e) {
      throw new GroupException("Failed to check account's presence", e);
    }
    if (groups.size() < 1) {
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

    defaultRole = serviceConf.getString(CONFIG_DEFAULT_ROLE, defaultRole);
    cascadeDelete =
      serviceConf.getBoolean(CASCADE_DELETE, DEFAULT_CASCADE_DELETE);

    setInit(true);
  }
}
