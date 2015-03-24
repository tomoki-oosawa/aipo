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
package com.aimluck.eip.userfacility.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.DataRow;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.account.VEipMUserGroupList;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.userfacility.beans.UserAllLiteBean;
import com.aimluck.eip.userfacility.beans.UserAllLiteBean.Type;

/**
 *
 */
public class UserAllUtils {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(UserAllUtils.class.getName());

  public static List<UserAllLiteBean> getUserAllLiteBeans(RunData rundata,
      int filterId, Type filterType, boolean groupOnly) {
    List<VEipMUserGroupList> list =
      getUserAllList(rundata, filterId, filterType, -1, -1, groupOnly);
    List<UserAllLiteBean> results = new ArrayList<UserAllLiteBean>();
    for (VEipMUserGroupList model : list) {
      UserAllLiteBean bean = new UserAllLiteBean();
      bean.initField();
      bean.setId(model.getId());
      bean.setType(Type.valueOf(model.getType()));
      bean.setName(model.getName());
      results.add(bean);
    }

    return results;

  }

  public static ResultList<VEipMUserGroupList> getUserAllList(RunData rundata,
      int filterId, Type filterType) {
    return getUserAllList(rundata, filterId, filterType, -1, -1, false);
  }

  public static ResultList<VEipMUserGroupList> getUserAllList(RunData rundata,
      int filterId, Type filterType, int page, int limit, boolean groupOnly) {

    StringBuilder select = new StringBuilder();
    boolean isFirst = true;
    if (Type.fg.equals(filterType) && filterId > 0) {

    } else {
      select
        .append(" SELECT 10 * t0.group_id + 1 AS uid, t0.group_id AS id, t0.group_alias_name AS name, 'ug' AS type, t0.group_id AS sort, 1 as pos FROM turbine_group t0");
      select.append("  WHERE t0.owner_id = 1 ");
      isFirst = false;
    }
    if (Type.ug.equals(filterType) && filterId > 0) {
      select.append("  AND t0.group_id = #bind($filterId) ");
    } else {
      if (!groupOnly) {
        if (!isFirst) {
          select.append(" UNION ALL");
        }
        select
          .append(" SELECT 10 * t0.group_id + 2 AS uid, t0.group_id AS id, t0.group_name AS name, 'fg' AS type, t0.group_id AS sort, 2 as pos FROM eip_m_facility_group t0");
        if (Type.fg.equals(filterType) && filterId > 0) {
          select.append("  WHERE t0.group_id = #bind($filterId) ");
        }
      }
    }
    if (Type.fg.equals(filterType) && filterId > 0) {

    } else {
      select.append(" UNION ALL");

      if (Database.isJdbcPostgreSQL()) {
        select
          .append(" SELECT DISTINCT 10 * t1.user_id + 3 AS uid, t1.user_id AS id, t1.last_name || ' ' || t1.first_name AS name, 'u' AS type, t3.position AS sort, 3 as pos");
        select.append(" FROM turbine_user_group_role AS t0");
      } else {
        select
          .append(" SELECT DISTINCT 10 * t1.user_id + 3 AS uid, t1.user_id AS id, CONCAT(t1.last_name,' ',t1.first_name) AS name, 'u' AS type, t3.position AS sort, 3 as pos");
        select.append(" FROM turbine_user_group_role AS t0");
      }
      select.append(" LEFT JOIN turbine_user AS t1 ON t0.user_id = t1.user_id");
      select
        .append(" LEFT JOIN turbine_group AS t2 ON t0.group_id = t2.group_id");
      select
        .append(" LEFT JOIN eip_m_user_position AS t3 ON t1.user_id = t3.user_id WHERE ((t1.user_id > 3) AND (t1.company_id = 1) AND (t1.disabled <> 'T'))");
    }
    if (Type.ug.equals(filterType) && filterId > 0) {
      select.append("  AND t0.group_id = #bind($filterId) ");
    } else {
      if (!groupOnly) {
        select.append(" UNION ALL");
        select
          .append(" SELECT 10 * t0.facility_id + 4 AS uid, t0.facility_id AS id, t0.facility_name AS name, 'f' AS type, t0.sort AS sort, 4 as pos");
        select.append(" FROM eip_m_facility AS t0");
        select
          .append(" LEFT JOIN eip_m_facility_group_map AS t1 ON t0.facility_id = t1.facility_id");
        select
          .append(" LEFT JOIN eip_m_facility_group AS t2 ON t1.group_id = t2.group_id");
        if (Type.fg.equals(filterType) && filterId > 0) {
          select.append("  WHERE t1.group_id = #bind($filterId) ");
        }
      }
    }

    StringBuilder last = new StringBuilder();
    last.append(" ORDER BY pos, sort");

    SQLTemplate<VEipMUserGroupList> countQuery =
      Database.sql(VEipMUserGroupList.class, "SELECT COUNT(*) as c FROM ("
        + select.toString()
        + ") as uCount");
    if (filterType != null && filterId > 0) {
      countQuery.param("filterId", filterId);
    }

    int countValue = 0;
    if (page > 0 && limit > 0) {
      List<DataRow> fetchCount = countQuery.fetchListAsDataRow();

      for (DataRow row : fetchCount) {
        countValue = ((Long) row.get("c")).intValue();
      }

      int offset = 0;
      if (limit > 0) {
        int num = ((int) (Math.ceil(countValue / (double) limit)));
        if ((num > 0) && (num < page)) {
          page = num;
        }
        offset = limit * (page - 1);
      } else {
        page = 1;
      }

      last.append(" LIMIT ");
      last.append(limit);
      last.append(" OFFSET ");
      last.append(offset);
    }

    SQLTemplate<VEipMUserGroupList> query =
      Database.sql(VEipMUserGroupList.class, select.toString()
        + last.toString());
    if (filterType != null && filterId > 0) {
      query.param("filterId", filterId);
    }
    List<VEipMUserGroupList> list = query.fetchList();

    if (page > 0 && limit > 0) {
      return new ResultList<VEipMUserGroupList>(list, page, limit, countValue);
    } else {
      return new ResultList<VEipMUserGroupList>(list, -1, -1, list.size());
    }
  }

}
