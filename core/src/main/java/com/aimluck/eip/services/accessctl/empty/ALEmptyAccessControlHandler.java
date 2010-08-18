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
package com.aimluck.eip.services.accessctl.empty;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アクセス権限を管理するクラスです。 <br />
 * 
 */
public class ALEmptyAccessControlHandler extends ALAccessControlHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEmptyAccessControlHandler.class.getName());

  @Override
  public boolean hasAuthority(int userId, String featerName, int aclType) {
    if (ALAccessControlConstants.POERTLET_FEATURE_SCHEDULE_OTHER
      .equals(featerName)) {
      boolean updatable = (aclType & ALAccessControlConstants.VALUE_ACL_UPDATE) == ALAccessControlConstants.VALUE_ACL_UPDATE;
      boolean deletable = (aclType & ALAccessControlConstants.VALUE_ACL_DELETE) == ALAccessControlConstants.VALUE_ACL_DELETE;
      return (!updatable && !deletable);
    }
    return true;
  }

  @Override
  public List<Integer> getAcceptUserIdsExceptLoginUser(DataContext dataContext,
      int uid, String feat, int acl_type) {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" FROM TURBINE_USER WHERE ");
    sb.append(TurbineUser.USER_ID_PK_COLUMN);
    sb.append(" != ");
    sb.append(Integer.toString(uid));

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
      DataRow raw = (DataRow) list.get(i);
      userIds.add((Integer) ALEipUtils.getObjFromDataRow(raw,
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
    if ((list == null) || ((size = list.size()) < 1)) {
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
    statement.append("WHERE B.DISABLED = 'F'");
    statement.append(" AND C.GROUP_NAME = '").append(groupname).append("' ");
    statement.append("ORDER BY D.POSITION");

    // String merged1 = sb.toString();
    String merged2 = statement.toString();
    SQLTemplate stemp = new SQLTemplate(TurbineUser.class, merged2);
    stemp.setFetchingDataRows(true);
    List<?> ulist = dataContext.performQuery(stemp);
    return ulist;
  }

  @Override
  public void insertDefaultRole(int uid) throws Exception {

  }

}
