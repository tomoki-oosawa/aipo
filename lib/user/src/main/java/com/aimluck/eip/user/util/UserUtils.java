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
package com.aimluck.eip.user.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.user.beans.UserEmailLiteBean;
import com.aimluck.eip.user.beans.UserGroupLiteBean;
import com.aimluck.eip.user.beans.UserLiteBean;
import com.aimluck.eip.user.beans.UserPhotoLiteBean;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーのユーティリティクラスです。 <br />
 *
 */
public class UserUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(UserUtils.class.getName());

  /**
   *
   * @param rundata
   * @return
   */
  public static synchronized List<UserLiteBean> getUserLiteBeansFromGroup(
      RunData rundata, String groupname, boolean includeLoginuser) {
    int login_user_id = null != rundata ? ALEipUtils.getUserId(rundata) : 0;
    /** SQLを構築してデータベース検索 */
    ArrayList<UserLiteBean> list = new ArrayList<UserLiteBean>();
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement
      .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, D.POSITION ");
    statement.append("FROM turbine_user_group_role as A ");
    statement.append("LEFT JOIN turbine_user as B ");
    statement.append("  on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN turbine_group as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN eip_m_user_position as D ");
    statement.append("  on A.USER_ID = D.USER_ID ");
    statement.append("WHERE B.USER_ID > 3 AND B.DISABLED = 'F'");
    statement.append(" AND C.GROUP_NAME = #bind($groupname) ");
    statement.append("ORDER BY D.POSITION");

    String query = statement.toString();

    List<TurbineUser> list2 =
      Database
        .sql(TurbineUser.class, query)
        .param("groupname", groupname)
        .fetchList();

    UserLiteBean user;
    // ユーザデータを作成し、返却リストへ格納
    for (TurbineUser tuser : list2) {
      user = new UserLiteBean();
      user.initField();
      user.setUserId(tuser.getUserId());
      user.setName(tuser.getLoginName());
      user.setAliasName(tuser.getFirstName(), tuser.getLastName());
      list.add(user);
    }

    if (!includeLoginuser && login_user_id > 3) {
      /** 返り値からログインユーザを除く */
      for (int i = 0; i < list.size(); i++) {
        user = list.get(i);
        if (Integer.valueOf(user.getUserId()) == login_user_id) {
          list.remove(i);
          break;
        }
      }
    }
    return list;
  }

  /**
   *
   * @param rundata
   * @return
   */
  public static synchronized List<UserEmailLiteBean> getUserEmailLiteBeansFromGroup(
      RunData rundata, String groupname, boolean includeLoginuser) {
    int login_user_id = null != rundata ? ALEipUtils.getUserId(rundata) : 0;
    ArrayList<UserEmailLiteBean> list = new ArrayList<UserEmailLiteBean>();
    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement
      .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, B.EMAIL, D.POSITION ");
    statement.append("FROM turbine_user_group_role as A ");
    statement.append("LEFT JOIN turbine_user as B ");
    statement.append("  on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN turbine_group as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN eip_m_user_position as D ");
    statement.append("  on A.USER_ID = D.USER_ID ");
    statement.append("WHERE B.USER_ID > 3 AND B.DISABLED = 'F'");
    statement.append(" AND C.GROUP_NAME = #bind($groupname) ");
    statement.append("ORDER BY D.POSITION");
    String query = statement.toString();

    List<TurbineUser> list2 =
      Database
        .sql(TurbineUser.class, query)
        .param("groupname", groupname)
        .fetchList();

    UserEmailLiteBean user;
    // ユーザデータを作成し、返却リストへ格納
    for (TurbineUser tuser : list2) {
      user = new UserEmailLiteBean();
      user.initField();
      user.setUserId(tuser.getUserId());
      user.setName(tuser.getLoginName());
      user.setAliasName(tuser.getFirstName(), tuser.getLastName());
      user.setEmail(tuser.getEmail());
      list.add(user);
    }

    if (!includeLoginuser && login_user_id > 3) {
      /** 返り値からログインユーザを除く */
      for (int i = 0; i < list.size(); i++) {
        user = list.get(i);
        if (Integer.valueOf(user.getUserId()) == login_user_id) {
          list.remove(i);
          break;
        }
      }
    }
    return list;
  }

  /**
   *
   * @param rundata
   * @return
   */
  public static List<UserGroupLiteBean> getUserGroupLiteBeans(RunData rundata,
      boolean isMygroup, boolean isPost, boolean isFacilityGroup) {
    List<UserGroupLiteBean> list = new ArrayList<UserGroupLiteBean>();
    UserGroupLiteBean bean;
    try {
      if (isMygroup) {
        List<ALEipGroup> mygroup = ALEipUtils.getMyGroups(rundata);
        int size1 = mygroup.size();
        for (int i = 0; i < size1; i++) {
          ALEipGroup group = mygroup.get(i);
          bean = new UserGroupLiteBean();
          bean.initField();
          bean.setGroupId(group.getName().getValue());
          bean.setName(group.getAliasName().getValue());
          list.add(bean);
        }
      }
      if (isPost) {
        Map<Integer, ALEipPost> postmap =
          ALEipManager.getInstance().getPostMap();
        for (Iterator<ALEipPost> i = postmap.values().iterator(); i.hasNext();) {
          ALEipPost post = i.next();
          bean = new UserGroupLiteBean();
          bean.initField();
          bean.setGroupId(post.getGroupName().getValue());
          bean.setName(post.getPostName().getValue());
          list.add(bean);
        }
      }
      if (isFacilityGroup) {
        List<ALEipGroup> facilitygroup = ALEipUtils.getFacilityGroups();
        int size1 = facilitygroup.size();
        for (int i = 0; i < size1; i++) {
          ALEipGroup group = facilitygroup.get(i);
          bean = new UserGroupLiteBean();
          bean.initField();
          bean.setGroupId("f;" + group.getName().getValue());
          bean.setName(group.getAliasName().getValue());
          list.add(bean);
        }
      }
    } catch (Exception e) {
      logger.error("UserUtils.getUserGroupLiteBeans", e);
    }
    return list;
  }

  /**
   *
   * @param rundata
   * @param groupname
   * @param includeLoginuser
   * @param keyword
   * @return
   */
  public static synchronized List<UserPhotoLiteBean> getUserPhotoLiteBeansFromGroup(
      RunData rundata, String groupname, boolean includeLoginuser,
      String keyword) {
    return getUserPhotoLiteBeansFromGroup(
      rundata,
      groupname,
      includeLoginuser,
      keyword,
      null);
  }

  /**
   *
   * @param rundata
   * @param groupname
   * @param includeLoginuser
   * @param keyword
   * @param rid
   * @return
   */
  public static synchronized List<UserPhotoLiteBean> getUserPhotoLiteBeansFromGroup(
      RunData rundata, String groupname, boolean includeLoginuser,
      String keyword, String rid) {
    int login_user_id = null != rundata ? ALEipUtils.getUserId(rundata) : 0;
    ArrayList<UserPhotoLiteBean> list = new ArrayList<UserPhotoLiteBean>();
    // SQLの作成
    StringBuffer statement = new StringBuffer();
    statement.append("SELECT DISTINCT ");
    statement
      .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, B.HAS_PHOTO, B.PHOTO_MODIFIED, D.POSITION ");
    statement.append("FROM turbine_user_group_role as A ");
    statement.append("LEFT JOIN turbine_user as B ");
    statement.append("  on A.USER_ID = B.USER_ID ");
    statement.append("LEFT JOIN turbine_group as C ");
    statement.append("  on A.GROUP_ID = C.GROUP_ID ");
    statement.append("LEFT JOIN eip_m_user_position as D ");
    statement.append("  on A.USER_ID = D.USER_ID ");
    if (rid == null) {
      statement.append("WHERE B.USER_ID > 3 AND B.DISABLED = 'F'");
    } else {
      statement.append("LEFT JOIN eip_t_message_room_member as E ");
      statement.append("  on A.USER_ID = E.USER_ID ");
      statement
        .append("WHERE B.USER_ID > 3 AND (B.DISABLED = 'F' OR ( B.DISABLED = 'N' AND E.ROOM_ID = ");
      statement.append(" #bind($roomid) ");
      statement.append("))");
    }
    statement.append(" AND C.GROUP_NAME = #bind($groupname) ");
    if (keyword != null && !keyword.equals("")) {
      statement.append(" AND ( ");
      statement.append(" B.FIRST_NAME LIKE #bind($keyword1) ");
      statement.append(" OR B.LAST_NAME LIKE #bind($keyword2) ");
      statement.append(" OR B.FIRST_NAME_KANA LIKE #bind($keyword3) ");
      statement.append(" OR B.LAST_NAME_KANA LIKE #bind($keyword4) ");
      statement.append(" OR B.EMAIL LIKE #bind($keyword5) ");
      statement.append(" OR B.LOGIN_NAME LIKE #bind($keyword6) ");
      statement.append(" OR B.CODE LIKE #bind($keyword7) ");
      statement.append(" ) ");
    }
    statement.append("ORDER BY D.POSITION");
    String query = statement.toString();

    SQLTemplate<TurbineUser> param = Database.sql(TurbineUser.class, query);

    if (rid != null) {
      param.param("roomid", Integer.valueOf(rid));
    }
    param.param("groupname", groupname);
    if (keyword != null && !keyword.equals("")) {
      param.param("keyword1", "%" + keyword + "%");
      param.param("keyword2", "%" + keyword + "%");
      param.param("keyword3", "%" + keyword + "%");
      param.param("keyword4", "%" + keyword + "%");
      param.param("keyword5", "%" + keyword + "%");
      param.param("keyword6", "%" + keyword + "%");
      param.param("keyword7", "%" + keyword + "%");
    }

    List<TurbineUser> list2 = param.fetchList();

    UserPhotoLiteBean user;
    // ユーザデータを作成し、返却リストへ格納
    for (TurbineUser tuser : list2) {
      user = new UserPhotoLiteBean();
      user.initField();
      user.setUserId(tuser.getUserId());
      user.setName(tuser.getLoginName());
      user.setAliasName(tuser.getFirstName(), tuser.getLastName());
      user.setHasPhoto("T".equals(tuser.getHasPhoto())
        || "N".equals(tuser.getHasPhoto()));
      user.setPhotoModified(tuser.getPhotoModified().getTime());
      list.add(user);
    }

    if (!includeLoginuser && login_user_id > 3) {
      /** 返り値からログインユーザを除く */
      for (int i = 0; i < list.size(); i++) {
        user = list.get(i);
        if (Integer.valueOf(user.getUserId()) == login_user_id) {
          list.remove(i);
          break;
        }
      }
    }
    return list;
  }
}