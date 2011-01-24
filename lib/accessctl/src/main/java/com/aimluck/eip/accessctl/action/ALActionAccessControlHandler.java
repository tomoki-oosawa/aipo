/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.accessctl.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.account.EipTAclPortletFeature;
import com.aimluck.eip.cayenne.om.account.EipTAclRole;
import com.aimluck.eip.cayenne.om.account.EipTAclUserRoleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALUserContextLocator;

public class ALActionAccessControlHandler extends ALAccessControlHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALActionAccessControlHandler.class.getName());

  @SuppressWarnings("unchecked")
  @Override
  public boolean hasAuthority(int userId, String featureName, int aclType) {

    Object acl_obj =
      ALUserContextLocator.getAttribute(ALUserContextLocator.KEY_ACL);

    EipTAclRole role = null;

    Map<String, EipTAclRole> roleMap;

    if (acl_obj == null) {

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTAclRole.EIP_TACL_USER_ROLE_MAPS_PROPERTY
            + "."
            + EipTAclUserRoleMap.TURBINE_USER_PROPERTY
            + "."
            + TurbineUser.USER_ID_PK_COLUMN,
          userId);

      List<EipTAclRole> roleList =
        Database.query(EipTAclRole.class, exp).fetchList();

      List<EipTAclPortletFeature> featureList =
        Database.query(EipTAclPortletFeature.class).fetchList();

      Map<Integer, String> _map = new HashMap<Integer, String>();

      for (EipTAclPortletFeature feature : featureList) {
        _map.put(feature.getFeatureId(), feature.getFeatureName());
      }

      roleMap = new HashMap<String, EipTAclRole>();
      String _featureName;
      for (EipTAclRole _role : roleList) {
        _featureName = _map.get(_role.getFeatureId().intValue());
        if (_featureName.equals(featureName)) {
          role = _role;
        }
        roleMap.put(_featureName, _role);
      }
      ALUserContextLocator.setAttribute(ALUserContextLocator.KEY_ACL, roleMap);
    } else {
      roleMap = (HashMap<String, EipTAclRole>) acl_obj;
      role = roleMap.get(featureName);
    }

    if (role == null) {
      return false;
    }

    int dbAclType = role.getAclType().intValue();

    return ((dbAclType & aclType) == aclType);
  }

  @Override
  public List<Integer> getAcceptUserIdsExceptLoginUser(int uid, String feat,
      int acl_type) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM turbine_user WHERE (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM turbine_user_group_role WHERE ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" FROM eip_t_acl_role WHERE ((");
    sb.append(EipTAclRole.ACL_TYPE_COLUMN);
    sb.append(" & ");
    sb.append(Integer.toString(acl_type));
    sb.append(") = ");
    sb.append(Integer.toString(acl_type));
    sb.append(") AND (");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" FROM eip_t_acl_portlet_feature WHERE ");
    sb.append(EipTAclPortletFeature.FEATURE_NAME_COLUMN);
    sb.append("='");
    sb.append(feat.trim());
    sb.append("'))))) AND (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" != ");
    sb.append(Integer.toString(uid));
    sb.append(")");

    List<Integer> userIds = new ArrayList<Integer>();
    String sqlString = sb.toString();

    List<TurbineUser> list =
      Database.sql(TurbineUser.class, sqlString).fetchList();

    for (TurbineUser tuser : list) {
      userIds.add(tuser.getUserId());
    }
    return userIds;
  }

  @Override
  public List<Integer> getAcceptUserIdsInListExceptLoginUser(int uid,
      String feat, int acl_type, List<ALEipUser> ulist) {
    List<Integer> userIds = new ArrayList<Integer>();
    int u_size;
    if ((ulist == null) || (u_size = ulist.size()) < 1) {
      return userIds;
    }

    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM turbine_user WHERE (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM turbine_user_group_role WHERE ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" FROM eip_t_acl_role WHERE ((");
    sb.append(EipTAclRole.ACL_TYPE_COLUMN);
    sb.append(" & ");
    sb.append(Integer.toString(acl_type));
    sb.append(") = ");
    sb.append(Integer.toString(acl_type));
    sb.append(") AND (");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" FROM eip_t_acl_portlet_feature WHERE ");
    sb.append(EipTAclPortletFeature.FEATURE_NAME_COLUMN);
    sb.append("='");
    sb.append(feat.trim());
    sb.append("'))))) AND (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" in (");
    for (int i = 0; i < u_size; i++) {
      ALEipUser member = ulist.get(i);
      sb.append(Long.toString(member.getUserId().getValue()));
      if (i + 1 < u_size) {
        sb.append(",");
      }
    }
    sb.append(")) AND (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" != ");
    sb.append(Integer.toString(uid));
    sb.append(")");

    String sqlString = sb.toString();

    List<TurbineUser> list =
      Database.sql(TurbineUser.class, sqlString).fetchList();

    for (TurbineUser tuser : list) {
      userIds.add(tuser.getUserId());
    }
    return userIds;
  }

  @Override
  public List<TurbineUser> getAuthorityUsersFromGroup(RunData rundata,
      String feat, String groupname, boolean includeLoginuser) {

    int listNumber = ALAccessControlConstants.VALUE_ACL_LIST;
    int detailNumber = ALAccessControlConstants.VALUE_ACL_DETAIL;
    int updateNumber = ALAccessControlConstants.VALUE_ACL_UPDATE;

    int aclNumber = listNumber | detailNumber | updateNumber;

    StringBuffer sb = new StringBuffer();

    sb.append("(SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM turbine_user_group_role WHERE ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" FROM eip_t_acl_role WHERE ((");
    sb.append(EipTAclRole.ACL_TYPE_COLUMN);
    sb.append(" & ");
    sb.append(Integer.toString(aclNumber));
    sb.append(") = ");
    sb.append(Integer.toString(aclNumber));
    sb.append(") AND (");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" FROM eip_t_acl_portlet_feature WHERE ");
    sb.append(EipTAclPortletFeature.FEATURE_NAME_COLUMN);
    sb.append("='");
    sb.append(feat.trim());
    sb.append("'))))");

    StringBuffer statement = new StringBuffer();

    statement.append("SELECT DISTINCT ");
    statement
      .append("B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, D.POSITION ");
    statement.append("FROM turbine_user_group_role as A ");
    statement.append("LEFT JOIN turbine_user as B ");
    statement.append("on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN turbine_group as C ");
    statement.append("on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN eip_m_user_position as D ");
    statement.append("on A.USER_ID = D.USER_ID ");
    statement.append("WHERE B.USER_ID IN ");
    statement.append(sb);
    statement.append(" AND B.DISABLED = 'F'");
    statement.append(" AND C.GROUP_NAME = #bind($groupname) ");
    statement.append("ORDER BY D.POSITION");

    List<TurbineUser> list =
      Database.sql(TurbineUser.class, statement.toString()).param(
        "groupname",
        groupname).fetchList();
    return list;
  }

  /**
   * ACLの登録（ここではコミット処理はしない）
   * 
   */
  @Override
  public void insertDefaultRole(int uid) throws Exception {
    // 現在のロール27種
    int role = ALAccessControlConstants.ROLE_NUM;
    TurbineUser tuser = Database.get(TurbineUser.class, Integer.valueOf(uid));
    List<Integer> integerList = new ArrayList<Integer>(role);
    for (int i = 0; i < role; i++) {
      integerList.add(Integer.valueOf(i + 1));
    }
    Expression exp =
      ExpressionFactory.inDbExp(EipTAclRole.ROLE_ID_PK_COLUMN, integerList);
    List<EipTAclRole> list = Database.query(EipTAclRole.class, exp).fetchList();
    for (EipTAclRole role2 : list) {
      EipTAclUserRoleMap map = Database.create(EipTAclUserRoleMap.class);
      map.setEipTAclRole(role2);
      map.setTurbineUser(tuser);
    }
  }

}
