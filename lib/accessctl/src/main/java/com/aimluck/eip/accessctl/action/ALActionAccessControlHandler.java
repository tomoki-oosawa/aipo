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
package com.aimluck.eip.accessctl.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.account.EipTAclPortletFeature;
import com.aimluck.eip.cayenne.om.account.EipTAclRole;
import com.aimluck.eip.cayenne.om.account.EipTAclUserRoleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

public class ALActionAccessControlHandler extends ALAccessControlHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALActionAccessControlHandler.class.getName());

  @Override
  public boolean hasAuthority(int userId, String featerName, int aclType) {

    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query = new SelectQuery(EipTAclRole.class);
    Expression exp11 = ExpressionFactory.matchDbExp(
      EipTAclRole.EIP_TACL_USER_ROLE_MAPS_PROPERTY + "."
        + EipTAclUserRoleMap.TURBINE_USER_PROPERTY + "."
        + TurbineUser.USER_ID_PK_COLUMN, userId);
    Expression exp12 = ExpressionFactory.matchExp(
      EipTAclRole.EIP_TACL_PORTLET_FEATURE_PROPERTY + "."
        + EipTAclPortletFeature.FEATURE_NAME_PROPERTY, featerName);
    query.setQualifier(exp11.andExp(exp12));
    query.setDistinct(true);

    List<?> roles = dataContext.performQuery(query);
    if (roles == null || roles.size() == 0) {
      return false;
    }

    EipTAclRole role = (EipTAclRole) roles.get(0);
    int dbAclType = role.getAclType().intValue();

    return ((dbAclType & aclType) == aclType);
  }

  @Override
  public List<Integer> getAcceptUserIdsExceptLoginUser(DataContext dataContext,
      int uid, String feat, int acl_type) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM TURBINE_USER WHERE (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM EIP_T_ACL_USER_ROLE_MAP WHERE ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" FROM EIP_T_ACL_ROLE WHERE ((");
    sb.append(EipTAclRole.ACL_TYPE_COLUMN);
    sb.append(" & ");
    sb.append(Integer.toString(acl_type));
    sb.append(") = ");
    sb.append(Integer.toString(acl_type));
    sb.append(") AND (");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" FROM EIP_T_ACL_PORTLET_FEATURE WHERE ");
    sb.append(EipTAclPortletFeature.FEATURE_NAME_COLUMN);
    sb.append("='");
    sb.append(feat.trim());
    sb.append("'))))) AND (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" != ");
    sb.append(Integer.toString(uid));
    sb.append(")");

    String sqlString = sb.toString();

    @SuppressWarnings("deprecation")
    SQLTemplate query = new SQLTemplate(TurbineUser.class, sqlString, true);
    query.setFetchingDataRows(true);

    List<?> list = dataContext.performQuery(query);
    List<Integer> userIds = new ArrayList<Integer>();
    int size;
    if ((list == null) || ((size = list.size()) < 1)) {
      return userIds;
    }

    for (int i = 0; i < size; i++) {
      DataRow row = (DataRow) list.get(i);
      userIds.add((Integer) ALEipUtils.getObjFromDataRow(row,
        TurbineUser.USER_ID_PK_COLUMN));
    }
    return userIds;
  }

  @Override
  public List<Integer> getAcceptUserIdsInListExceptLoginUser(
      DataContext dataContext, int uid, String feat, int acl_type, List<?> ulist) {
    List<Integer> userIds = new ArrayList<Integer>();
    int u_size;
    if ((ulist == null) || (u_size = ulist.size()) < 1) {
      return userIds;
    }

    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM TURBINE_USER WHERE (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM EIP_T_ACL_USER_ROLE_MAP WHERE ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" FROM EIP_T_ACL_ROLE WHERE ((");
    sb.append(EipTAclRole.ACL_TYPE_COLUMN);
    sb.append(" & ");
    sb.append(Integer.toString(acl_type));
    sb.append(") = ");
    sb.append(Integer.toString(acl_type));
    sb.append(") AND (");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" FROM EIP_T_ACL_PORTLET_FEATURE WHERE ");
    sb.append(EipTAclPortletFeature.FEATURE_NAME_COLUMN);
    sb.append("='");
    sb.append(feat.trim());
    sb.append("'))))) AND (");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" in (");
    for (int i = 0; i < u_size; i++) {
      ALEipUser member = (ALEipUser) ulist.get(i);
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

    @SuppressWarnings("deprecation")
    SQLTemplate query = new SQLTemplate(TurbineUser.class, sqlString, true);
    query.setFetchingDataRows(true);

    List<?> list = dataContext.performQuery(query);
    int size;
    if (list == null || (size = list.size()) < 1) {
      return userIds;
    }

    for (int i = 0; i < size; i++) {
      DataRow raw = (DataRow) list.get(i);
      userIds.add((Integer) ALEipUtils.getObjFromDataRow(raw,
        TurbineUser.USER_ID_PK_COLUMN));
    }
    return userIds;
  }

  @Override
  public List<?> getAuthorityUsersFromGroup(RunData rundata, String feat,
      String groupname, boolean includeLoginuser) {

    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();

    int listNumber = ALAccessControlConstants.VALUE_ACL_LIST;
    int detailNumber = ALAccessControlConstants.VALUE_ACL_DETAIL;
    int updateNumber = ALAccessControlConstants.VALUE_ACL_UPDATE;

    int aclNumber = listNumber | detailNumber | updateNumber;

    StringBuffer sb = new StringBuffer();

    sb.append("(SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM EIP_T_ACL_USER_ROLE_MAP WHERE ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclUserRoleMap.ROLE_ID_COLUMN);
    sb.append(" FROM EIP_T_ACL_ROLE WHERE ((");
    sb.append(EipTAclRole.ACL_TYPE_COLUMN);
    sb.append(" & ");
    sb.append(Integer.toString(aclNumber));
    sb.append(") = ");
    sb.append(Integer.toString(aclNumber));
    sb.append(") AND (");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" IN (SELECT ");
    sb.append(EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);
    sb.append(" FROM EIP_T_ACL_PORTLET_FEATURE WHERE ");
    sb.append(EipTAclPortletFeature.FEATURE_NAME_COLUMN);
    sb.append("='");
    sb.append(feat.trim());
    sb.append("'))))");

    StringBuffer statement = new StringBuffer();

    statement.append("SELECT DISTINCT ");
    statement
      .append("B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, D.POSITION ");
    statement.append("FROM TURBINE_USER_GROUP_ROLE as A ");
    statement.append("LEFT JOIN TURBINE_USER as B ");
    statement.append("on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN TURBINE_GROUP as C ");
    statement.append("on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN EIP_M_USER_POSITION as D ");
    statement.append("on A.USER_ID = D.USER_ID ");
    statement.append("WHERE B.USER_ID IN ");
    statement.append(sb);
    statement.append(" AND B.DISABLED = 'F'");
    statement.append(" AND C.GROUP_NAME = '").append(groupname).append("' ");
    statement.append("ORDER BY D.POSITION");

    // String merged1 = sb.toString();
    String merged2 = statement.toString();
    SQLTemplate stemp = new SQLTemplate(TurbineUser.class, merged2);
    stemp.setFetchingDataRows(true);
    List<?> ulist = dataContext.performQuery(stemp);
    return ulist;
  }

  /**
   * ACLの登録（ここではコミット処理はしない）
   * 
   */
  @Override
  public void insertDefaultRole(int uid) throws Exception {
    // 現在のロール27種
    int role = ALAccessControlConstants.ROLE_NUM;
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    TurbineUser tuser = (TurbineUser) DataObjectUtils.objectForPK(dataContext,
      TurbineUser.class, Integer.valueOf(uid));
    SelectQuery query = new SelectQuery(EipTAclRole.class);
    List<Integer> integerList = new ArrayList<Integer>(role);
    for (int i = 0; i < role; i++) {
      integerList.add(Integer.valueOf(i + 1));
    }
    Expression exp = ExpressionFactory.inDbExp(EipTAclRole.ROLE_ID_PK_COLUMN,
      integerList);
    query.setQualifier(exp);
    List<?> list = dataContext.performQuery(query);
    int size = list.size();
    for (int i = 0; i < size; i++) {
      EipTAclUserRoleMap map = (EipTAclUserRoleMap) dataContext
        .createAndRegisterNewObject(EipTAclUserRoleMap.class);
      map.setEipTAclRole((EipTAclRole) list.get(i));
      map.setTurbineUser(tuser);
    }
  }

}
