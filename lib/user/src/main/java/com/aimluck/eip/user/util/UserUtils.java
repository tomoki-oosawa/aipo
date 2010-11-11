/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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
import java.util.Hashtable;
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
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.user.beans.UserEmailLiteBean;
import com.aimluck.eip.user.beans.UserGroupLiteBean;
import com.aimluck.eip.user.beans.UserLiteBean;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーのユーティリティクラスです。 <br />
 * 
 */
public class UserUtils {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(UserUtils.class.getName());

  /** userLiteBeanキャッシュ用の変数 */
  private static Hashtable<String, ArrayList<UserLiteBean>> userLiteBeans =
    new Hashtable<String, ArrayList<UserLiteBean>>();

  /** userEmailLiteBeanキャッシュ用の変数 */
  private static Hashtable<String, ArrayList<UserEmailLiteBean>> userEmailLiteBeans =
    new Hashtable<String, ArrayList<UserEmailLiteBean>>();

  /**
   * 
   * @param rundata
   * @return
   */
  public static synchronized List<UserLiteBean> getUserLiteBeansFromGroup(
      RunData rundata, String groupname, boolean includeLoginuser) {
    int login_user_id = null != rundata ? ALEipUtils.getUserId(rundata) : 0;
    String org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
    if (userLiteBeans.containsKey(org_id + "_" + groupname)) {
      /** キャッシュを出力する */
      @SuppressWarnings("unchecked")
      List<UserLiteBean> res =
        (List<UserLiteBean>) userLiteBeans
          .get(org_id + "_" + groupname)
          .clone();
      if (!includeLoginuser && login_user_id > 3) {
        /** ログインユーザを返り値から除く */
        UserLiteBean user;
        for (int i = 0; i < res.size(); i++) {
          user = res.get(i);
          if (Integer.valueOf(user.getUserId()) == login_user_id) {
            res.remove(i);
            break;
          }
        }
      }
      return res;
    } else {
      /** SQLを構築してデータベース検索 */
      ArrayList<UserLiteBean> list = new ArrayList<UserLiteBean>();
      ArrayList<UserLiteBean> cache_list = new ArrayList<UserLiteBean>();// キャッシュに保存する用のリスト(返り値用のリストは値が変更される可能性があるので使えない)
      StringBuffer statement = new StringBuffer();
      statement.append("SELECT DISTINCT ");
      statement
        .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, D.POSITION ");
      statement.append("FROM TURBINE_USER_GROUP_ROLE as A ");
      statement.append("LEFT JOIN TURBINE_USER as B ");
      statement.append("  on A.USER_ID = B.USER_ID ");
      statement.append("LEFT JOIN TURBINE_GROUP as C ");
      statement.append("  on A.GROUP_ID = C.GROUP_ID ");
      statement.append("LEFT JOIN EIP_M_USER_POSITION as D ");
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
        cache_list.add(user);
      }

      /** リストをキャッシュする */
      userLiteBeans.put(org_id + "_" + groupname, cache_list);

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

  /**
   * 
   * @param rundata
   * @return
   */
  public static synchronized List<UserEmailLiteBean> getUserEmailLiteBeansFromGroup(
      RunData rundata, String groupname, boolean includeLoginuser) {
    int login_user_id = null != rundata ? ALEipUtils.getUserId(rundata) : 0;
    String org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
    ArrayList<UserEmailLiteBean> list = new ArrayList<UserEmailLiteBean>();
    ArrayList<UserEmailLiteBean> cache_list =
      new ArrayList<UserEmailLiteBean>();// キャッシュに保存する用のリスト(返り値用のリストは値が変更される可能性があるので使えない)
    if (userEmailLiteBeans.containsKey(org_id + "_" + groupname)) {
      /** キャッシュを出力する */
      @SuppressWarnings("unchecked")
      List<UserEmailLiteBean> res =
        (ArrayList<UserEmailLiteBean>) (userEmailLiteBeans.get(org_id
          + "_"
          + groupname)).clone();
      if (!includeLoginuser && login_user_id > 3) {
        /** ログインユーザを返り値から除く */
        UserEmailLiteBean user;
        for (int i = 0; i < res.size(); i++) {
          user = res.get(i);
          if (Integer.valueOf(user.getUserId()) == login_user_id) {
            res.remove(i);
            break;
          }
        }
      }
      return res;
    } else {
      // SQLの作成
      StringBuffer statement = new StringBuffer();
      statement.append("SELECT DISTINCT ");
      statement
        .append("  B.USER_ID, B.LOGIN_NAME, B.FIRST_NAME, B.LAST_NAME, B.EMAIL, D.POSITION ");
      statement.append("FROM TURBINE_USER_GROUP_ROLE as A ");
      statement.append("LEFT JOIN TURBINE_USER as B ");
      statement.append("  on A.USER_ID = B.USER_ID ");
      statement.append("LEFT JOIN TURBINE_GROUP as C ");
      statement.append("  on A.GROUP_ID = C.GROUP_ID ");
      statement.append("LEFT JOIN EIP_M_USER_POSITION as D ");
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
        cache_list.add(user);
      }

      /** リストをキャッシュする */
      userEmailLiteBeans.put(org_id + "_" + groupname, cache_list);

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

  /**
   * 
   * @param rundata
   * @return
   */
  public static List<UserGroupLiteBean> getUserGroupLiteBeans(RunData rundata,
      boolean isMygroup, boolean isPost) {
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
    } catch (Exception e) {
      logger.error("[UserUtils]", e);
    }
    return list;
  }

  /**
   * キャッシュしたリストをクリアします。
   */
  public static void clearCache() {
    userLiteBeans = new Hashtable<String, ArrayList<UserLiteBean>>();
    userEmailLiteBeans = new Hashtable<String, ArrayList<UserEmailLiteBean>>();
  }
}
