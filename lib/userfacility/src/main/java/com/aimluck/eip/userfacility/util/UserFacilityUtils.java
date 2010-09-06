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
package com.aimluck.eip.userfacility.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.user.beans.UserLiteBean;
import com.aimluck.eip.user.util.UserUtils;
import com.aimluck.eip.userfacility.beans.UserFacilityLiteBean;

/**
 * ユーザーのユーティリティクラスです。 <br />
 * 
 */
public class UserFacilityUtils {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(UserFacilityUtils.class.getName());

  /**
   * 
   * @param rundata
   * @return
   */
  public static List<UserFacilityLiteBean> getUserFacilityLiteBeansFromGroup(
      RunData rundata, String groupname) {

    List<UserLiteBean> tmp_u_list =
      UserUtils.getUserLiteBeansFromGroup(rundata, groupname, true);
    int t_size = tmp_u_list.size();

    List<UserFacilityLiteBean> list = new ArrayList<UserFacilityLiteBean>();
    UserLiteBean t_user;
    UserFacilityLiteBean user;
    for (int i = 0; i < t_size; i++) {
      t_user = tmp_u_list.get(i);
      user = new UserFacilityLiteBean();
      user.initField();
      user.setUserFacilityId(Integer.parseInt(t_user.getUserId()));
      user.setName(t_user.getUserId());
      user.setAliasName(t_user.getAliasName());
      user.setUserFacilityType("U");
      list.add(user);
    }

    if (!("LoginUser".equals(groupname))) {
      // SQLの作成
      StringBuffer statement = new StringBuffer();
      statement.append("SELECT DISTINCT ");
      statement.append("  B.FACILITY_ID, B.FACILITY_NAME ");
      statement.append("FROM EIP_FACILITY_GROUP as A ");
      statement.append("LEFT JOIN EIP_M_FACILITY as B ");
      statement.append("  on A.FACILITY_ID = B.FACILITY_ID ");
      statement.append("LEFT JOIN TURBINE_GROUP as C ");
      statement.append("  on A.GROUP_ID = C.GROUP_ID ");
      statement.append("WHERE C.GROUP_NAME = '").append(groupname).append("'");

      String query = statement.toString();

      List<EipMFacility> list2 =
        Database.sql(EipMFacility.class, query).fetchList();

      // ユーザデータを作成し、返却リストへ格納
      for (EipMFacility record : list2) {
        user = new UserFacilityLiteBean();
        user.initField();
        user.setUserFacilityId(record.getFacilityId());
        user.setName("f" + user.getUserFacilityId());
        user.setAliasName(record.getFacilityName());
        user.setUserFacilityType("F");
        list.add(user);
      }
    } else {
      List<EipMFacility> aList = Database.query(EipMFacility.class).fetchList();
      for (EipMFacility record : aList) {
        user = new UserFacilityLiteBean();
        user.initField();
        user.setUserFacilityId(record.getFacilityId().intValue());
        user.setName("f" + user.getUserFacilityId());
        user.setAliasName(record.getFacilityName());
        user.setUserFacilityType("F");
        list.add(user);
      }
    }

    return list;
  }

  public static UserFacilityLiteBean getUserFacilityLiteBean(RunData rundata) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    UserFacilityLiteBean user = new UserFacilityLiteBean();
    user.initField();
    user.setUserFacilityId(Integer
      .parseInt(jdata.getJetspeedUser().getUserId()));
    user.setName(jdata.getJetspeedUser().getUserName());
    user.setAliasName(jdata.getJetspeedUser().getFirstName(), jdata
      .getJetspeedUser()
      .getLastName());
    user.setUserFacilityType("U");
    return user;
  }
}
