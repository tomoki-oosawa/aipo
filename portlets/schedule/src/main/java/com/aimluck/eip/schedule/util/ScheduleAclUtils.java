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
package com.aimluck.eip.schedule.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.cayenne.DataRow;

import com.aimluck.eip.cayenne.om.account.VEipMUserGroupList;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facility.beans.FacilityLiteBean;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.user.beans.UserLiteBean;
import com.aimluck.eip.userfacility.beans.UserFacilityLiteBean;

/**
 *
 */
public class ScheduleAclUtils {

  public static final String ACL_CACHE_KEY =
    "com.aimluck.eip.schedule.util.ScheduleAclUtils.cache";

  public static int ACL_READONLY = 1;

  public static int ACL_EDITABLE = 1;

  public static boolean hasAclAcceptUser(int targetId, int userId, int level) {
    return hasAclAccept(targetId, userId, level, false);
  }

  public static boolean hasAclAcceptFacility(int targetId, int userId, int level) {
    return hasAclAccept(targetId, userId, level, true);
  }

  public static boolean hasAclAccept(int targetId, int userId, int level,
      boolean isFacility) {
    List<Integer> list = null;
    if (isFacility) {
      list = getAclAcceptFacilityFilter(Arrays.asList(targetId), userId, level);
    } else {
      list = getAclAcceptUserFilter(Arrays.asList(targetId), userId, level);
    }
    return list != null && list.size() > 0;
  }

  @SuppressWarnings("unchecked")
  public static List<Integer> getAclAcceptUserList(int userId, int level) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    String key = ACL_CACHE_KEY + "users:" + userId + ":" + level;
    List<Integer> cache = null;
    if (request != null) {
      try {
        cache = (List<Integer>) request.getAttribute(key);
      } catch (Throwable ignore) {
        // ignore
      }
    }

    if (cache != null) {
      return cache;
    }

    StringBuilder sql =
      new StringBuilder(
        "SELECT target_id as uid FROM eip_t_acl_map where level >= #bind($level) AND feature = 'schedule' AND target_type = 'u' AND ((type = 'ug' and id IN (select group_id from turbine_user_group_role where user_id = #bind($user_id) and role_id = 1)) OR (type = 'u' and id = #bind($user_id)))");
    sql.append(" UNION ");
    sql
      .append(" SELECT user_id as uid FROM turbine_user_group_role WHERE role_id = 1 and group_id IN (SELECT DISTINCT target_id from eip_t_acl_map where level >= #bind($level) AND feature = 'schedule' AND target_type = 'ug' AND ((type = 'ug' and id IN (select group_id from turbine_user_group_role where user_id = #bind($user_id) and role_id = 1)) OR (type = 'u' and id = #bind($user_id))))");

    SQLTemplate<VEipMUserGroupList> query =
      Database.sql(VEipMUserGroupList.class, sql.toString()).param(
        "user_id",
        userId).param("level", level);
    List<DataRow> fetchCount = query.fetchListAsDataRow();

    List<Integer> results = new ArrayList<Integer>();
    for (DataRow row : fetchCount) {
      results.add((Integer) row.get("uid"));
    }

    if (request != null) {
      request.setAttribute(key, results);
    }

    return results;
  }

  @SuppressWarnings("unchecked")
  public static List<Integer> getAclAcceptFacilityList(int userId, int level) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    String key = ACL_CACHE_KEY + "facilities:" + userId + ":" + level;
    List<Integer> cache = null;
    if (request != null) {
      try {
        cache = (List<Integer>) request.getAttribute(key);
      } catch (Throwable ignore) {
        // ignore
      }
    }

    if (cache != null) {
      return cache;
    }

    StringBuilder sql =
      new StringBuilder(
        "SELECT target_id as uid FROM eip_t_acl_map where level >= #bind($level) AND feature = 'schedule' AND target_type = 'f' AND ((type = 'ug' and id IN (select group_id from turbine_user_group_role where user_id = #bind($user_id) and role_id = 1)) OR (type = 'u' and id = #bind($user_id)))");
    sql.append(" UNION ");
    sql
      .append(" SELECT facility_id as uid FROM eip_m_facility_group_map WHERE group_id IN (SELECT DISTINCT target_id from eip_t_acl_map where level >= #bind($level) AND feature = 'schedule' AND target_type = 'fg' AND ((type = 'ug' and id IN (select group_id from turbine_user_group_role where user_id = #bind($user_id) and role_id = 1)) OR (type = 'u' and id = #bind($user_id))))");

    SQLTemplate<VEipMUserGroupList> query =
      Database.sql(VEipMUserGroupList.class, sql.toString()).param(
        "user_id",
        userId).param("level", level);
    List<DataRow> fetchCount = query.fetchListAsDataRow();

    List<Integer> results = new ArrayList<Integer>();
    for (DataRow row : fetchCount) {
      results.add((Integer) row.get("uid"));
    }

    if (request != null) {
      request.setAttribute(key, results);
    }

    return results;
  }

  public static <M> List<M> getAclAcceptUserFilter(List<M> users, int userId,
      int level) {
    if (users == null) {
      return null;
    }
    List<Integer> aclAcceptUserList = getAclAcceptUserList(userId, level);

    List<M> results = new ArrayList<M>(users.size());
    for (M user : users) {
      if (user instanceof ALEipUser) {
        Integer id =
          Integer.valueOf((int) ((ALEipUser) user).getUserId().getValue());
        if (aclAcceptUserList.contains(id) || userId == id.intValue()) {
          results.add(user);
        }
      } else if (user instanceof UserLiteBean) {
        Integer id = Integer.valueOf(((UserLiteBean) user).getUserId());
        if (aclAcceptUserList.contains(id) || userId == id.intValue()) {
          results.add(user);
        }
      } else if (user instanceof UserFacilityLiteBean) {
        Integer id =
          Integer.valueOf(((UserFacilityLiteBean) user).getUserFacilityId());
        if (aclAcceptUserList.contains(id) || userId == id.intValue()) {
          results.add(user);
        }
      } else if (user instanceof Integer) {
        if (aclAcceptUserList.contains(user)
          || userId == ((Integer) user).intValue()) {
          results.add(user);
        }
      }
    }
    return results;
  }

  public static <M> List<M> getAclRejectUserFilter(List<M> users, int userId,
      int level) {
    if (users == null) {
      return null;
    }
    List<Integer> aclAcceptUserList = getAclAcceptUserList(userId, level);

    List<M> results = new ArrayList<M>(users.size());
    for (M user : users) {
      if (user instanceof ALEipUser) {
        Integer id =
          Integer.valueOf((int) ((ALEipUser) user).getUserId().getValue());
        if (!aclAcceptUserList.contains(id) && userId != id.intValue()) {
          results.add(user);
        }
      } else if (user instanceof UserLiteBean) {
        Integer id = Integer.valueOf(((UserLiteBean) user).getUserId());
        if (!aclAcceptUserList.contains(id) && userId != id.intValue()) {
          results.add(user);
        }
      } else if (user instanceof UserFacilityLiteBean) {
        Integer id =
          Integer.valueOf(((UserFacilityLiteBean) user).getUserFacilityId());
        if (!aclAcceptUserList.contains(id) && userId != id.intValue()) {
          results.add(user);
        }
      } else if (user instanceof Integer) {
        if (!aclAcceptUserList.contains(user)
          && userId != ((Integer) user).intValue()) {
          results.add(user);
        }
      }
    }
    return results;
  }

  public static <M> List<M> getAclAcceptFacilityFilter(List<M> facilities,
      int userId, int level) {
    if (facilities == null) {
      return null;
    }
    List<Integer> aclAcceptFacilityList =
      getAclAcceptFacilityList(userId, level);

    List<M> results = new ArrayList<M>(facilities.size());
    for (M facility : facilities) {
      if (facility instanceof FacilityResultData) {
        Integer id =
          Integer.valueOf((int) ((FacilityResultData) facility)
            .getFacilityId()
            .getValue());
        if (aclAcceptFacilityList.contains(id)) {
          results.add(facility);
        }
      } else if (facility instanceof FacilityLiteBean) {
        Integer id =
          Integer.valueOf(((FacilityLiteBean) facility).getFacilityId());
        if (aclAcceptFacilityList.contains(id)) {
          results.add(facility);
        }
      } else if (facility instanceof UserFacilityLiteBean) {
        Integer id =
          Integer
            .valueOf(((UserFacilityLiteBean) facility).getUserFacilityId());
        if (aclAcceptFacilityList.contains(id)) {
          results.add(facility);
        }
      } else if (facility instanceof Integer) {
        if (aclAcceptFacilityList.contains(facility)) {
          results.add(facility);
        }
      }
    }
    return results;
  }

  public static <M> List<M> getAclRejectFacilityFilter(List<M> facilities,
      int userId, int level) {
    if (facilities == null) {
      return null;
    }
    List<Integer> aclAcceptFacilityList =
      getAclAcceptFacilityList(userId, level);

    List<M> results = new ArrayList<M>(facilities.size());
    for (M facility : facilities) {
      if (facility instanceof FacilityResultData) {
        Integer id =
          Integer.valueOf((int) ((FacilityResultData) facility)
            .getFacilityId()
            .getValue());
        if (!aclAcceptFacilityList.contains(id)) {
          results.add(facility);
        }
      } else if (facility instanceof FacilityLiteBean) {
        Integer id =
          Integer.valueOf(((FacilityLiteBean) facility).getFacilityId());
        if (!aclAcceptFacilityList.contains(id)) {
          results.add(facility);
        }
      } else if (facility instanceof UserFacilityLiteBean) {
        Integer id =
          Integer
            .valueOf(((UserFacilityLiteBean) facility).getUserFacilityId());
        if (aclAcceptFacilityList.contains(id)) {
          results.add(facility);
        }
      } else if (facility instanceof Integer) {
        if (!aclAcceptFacilityList.contains(facility)) {
          results.add(facility);
        }
      }
    }
    return results;
  }

  public static boolean hasAclAcceptUserFromScheduleId(int userId,
      int scheduleId, int level, boolean isFacility) {

    List<EipTScheduleMap> list =
      Database
        .query(EipTScheduleMap.class)
        .where(Operations.eq(EipTScheduleMap.SCHEDULE_ID_PROPERTY, scheduleId))
        .fetchList();

    List<Integer> users = new ArrayList<Integer>();
    List<Integer> facilities = new ArrayList<Integer>();
    for (EipTScheduleMap map : list) {
      if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(map.getType())) {
        users.add(map.getUserId());
      } else {
        facilities.add(map.getUserId());
      }
    }

    List<Integer> results =
      isFacility
        ? getAclAcceptFacilityFilter(facilities, userId, level)
        : getAclAcceptUserFilter(users, userId, level);

    return results != null && results.size() > 0;
  }
}
