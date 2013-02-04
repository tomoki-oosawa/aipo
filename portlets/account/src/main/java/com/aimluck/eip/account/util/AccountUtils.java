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

package com.aimluck.eip.account.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequestMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.user.beans.UserGroupLiteBean;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーアカウントのユーティリティクラスです
 */
public class AccountUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUtils.class.getName());

  /** CSVファイル名 */
  public static final String FOLDER_TMP_FOR_USERINFO_CSV_FILENAME =
    "user_info.csv";

  public static final int CSV_FILE_COL_COUNT = 11;

  /** ユーザー名として使用可能な記号群 */
  public static final String[] USER_NAME_SYMBOLS = { ".", "-", "_" };

  public static final String ACCOUNT_PORTLET_NAME = "Account";

  public static final String COMPANY_PORTLET_NAME = "Company";

  public static final String ACCOUNT_PERSON_PORTLET_NAME = "AccountPerson";

  public static final String ACCOUNT_LOGIN_PORTLET_NAME = "AccountLogin";

  /**
   * セッション中のエンティティIDで示されるユーザ情報を取得する。 論理削除されたユーザを取得した場合はnullを返す。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static ALBaseUser getBaseUser(RunData rundata, Context context) {
    String userid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (userid == null) {
        logger.debug("Empty ID...");
        return null;
      }
      ALBaseUser user = (ALBaseUser) JetspeedSecurity.getUser(userid);
      // 削除済みユーザの取得は行わない。
      // By Haruo Kaneko
      if ("T".equals(user.getDisabled())) {
        return null;
      } else {
        return (ALBaseUser) JetspeedSecurity.getUser(userid);
      }
    } catch (UnknownUserException uex) {
      logger.error("UnknownUserException : UserID = " + userid);
      return null;
    } catch (Exception ex) {
      logger.error("AccountUtils.getBaseUser", ex);
      return null;
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMCompany getEipMCompany(RunData rundata, Context context) {
    return getEipMCompany(ALEipUtils.getTemp(
      rundata,
      context,
      ALEipConstants.ENTITY_ID));
  }

  public static EipMCompany getEipMCompany(String id) {
    EipMCompany result = null;
    try {
      if (id == null || Integer.valueOf(id) == null) {
        logger.debug("Empty ID...");
        return result;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipMCompany.COMPANY_ID_PK_COLUMN, Integer
          .valueOf(id));
      List<EipMCompany> list =
        Database.query(EipMCompany.class, exp).fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return result;
      }
      result = list.get(0);
    } catch (Exception ex) {
      logger.error("AccountUtils.getEipMCompany", ex);
    }
    return result;
  }

  /**
   * セッションに格納されているIDを用いて、部署情報を取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMPost getEipMPost(RunData rundata, Context context) {
    return getEipMPost(ALEipUtils.getTemp(
      rundata,
      context,
      ALEipConstants.ENTITY_ID));
  }

  public static EipMPost getEipMPost(String id) {
    EipMPost result = null;
    try {
      if (id == null || Integer.valueOf(id) == null) {
        logger.debug("Empty ID...");
        return result;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipMPost.POST_ID_PK_COLUMN, Integer
          .valueOf(id));
      List<EipMPost> list = Database.query(EipMPost.class, exp).fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return result;
      }
      result = list.get(0);
    } catch (Exception ex) {
      logger.error("AccountUtils.getEipMPost", ex);
    }
    return result;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMPosition getEipMPosition(RunData rundata, Context context) {
    EipMPosition result = null;
    String id = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (id == null || Integer.valueOf(id) == null) {
        logger.debug("Empty ID...");
        return result;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipMPosition.POSITION_ID_PK_COLUMN,
          Integer.valueOf(id));
      List<EipMPosition> list =
        Database.query(EipMPosition.class, exp).fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return result;
      }
      result = list.get(0);
    } catch (Exception ex) {
      logger.error("AccountUtils.getEipMPosition", ex);
    }
    return result;
  }

  public static String[] getCsvSplitStrings(String line) {
    if (line == null || line.equals("")) {
      return null;
    }

    try {
      List<String> list = new ArrayList<String>();
      int count_comma = 0;
      char c;
      StringBuffer token = new StringBuffer("");
      int len = line.length();
      for (int i = 0; i < len; i++) {
        c = line.charAt(i);
        if (c != ',' && i == len - 1) {
          token.append(c);
          list.add(token.toString());
        } else if (c == ',') {
          list.add(token.toString());
          token = new StringBuffer("");
          count_comma++;
          continue;
        } else {
          token.append(c);
        }
        if (count_comma > AccountUtils.CSV_FILE_COL_COUNT) {
          break;
        }
      }

      if (line.endsWith(",")) {
        list.add("");
      }

      String[] strings = new String[list.size()];
      strings = list.toArray(strings);
      return strings;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * ユーザーの所属する部署の一覧を取得します。
   * 
   * @param uid
   *          ユーザーID
   * @return 所属する部署リスト
   */
  public static List<UserGroupLiteBean> getPostBeanList(int uid) {
    SelectQuery<TurbineUserGroupRole> query =
      Database.query(TurbineUserGroupRole.class);
    Expression exp1 =
      ExpressionFactory.matchExp(
        TurbineUserGroupRole.TURBINE_USER_PROPERTY,
        Integer.valueOf(uid));
    Expression exp2 =
      ExpressionFactory.greaterExp(
        TurbineUserGroupRole.TURBINE_GROUP_PROPERTY,
        Integer.valueOf(3));
    Expression exp3 =
      ExpressionFactory.matchExp(TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
        + "."
        + TurbineGroup.OWNER_ID_PROPERTY, Integer.valueOf(1));
    query.setQualifier(exp1);
    query.andQualifier(exp2);
    query.andQualifier(exp3);
    List<TurbineUserGroupRole> list = query.fetchList();

    if (list == null || list.size() < 0) {
      return null;
    }

    List<UserGroupLiteBean> resultList = new ArrayList<UserGroupLiteBean>();

    TurbineGroup group = null;
    UserGroupLiteBean bean = null;
    for (TurbineUserGroupRole ugr : list) {
      group = ugr.getTurbineGroup();
      bean = new UserGroupLiteBean();
      bean.initField();
      bean.setGroupId(group.getName());
      bean.setName(group.getGroupAliasName());
      resultList.add(bean);
    }

    return resultList;
  }

  /**
   * 指定した ID のユーザが削除済みかどうかを調べる。
   * 
   * @param userId
   * @return
   */
  public static boolean getUserIsDisabledOrDeleted(String userId) {
    if (userId == null || userId.equals("")) {
      return true;
    }
    String disabled;
    try {
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(userId));
      query.setQualifier(exp);
      List<TurbineUser> destUserList = query.fetchList();
      if (destUserList == null || destUserList.size() <= 0) {
        return true;
      }
      disabled = (destUserList.get(0)).getDisabled();
      return ("T".equals(disabled) || "N".equals(disabled));
    } catch (Exception ex) {
      logger.error("AccountUtils.getUserIsDisabledOrDeleted", ex);
      return true;
    }
  }

  /**
   * 指定されたユーザーが削除／無効化されたとき、申請が来ているワークフローを全て承認します。
   * 
   * @param uid
   */
  public static boolean acceptWorkflow(int uid) {
    try {
      String userId = Integer.toString(uid);

      // 申請が来ているワークフローを取得する
      SelectQuery<EipTWorkflowRequestMap> workflow_request_map_query =
        Database.query(EipTWorkflowRequestMap.class);
      Expression workflow_exp =
        ExpressionFactory.matchExp(
          EipTWorkflowRequestMap.USER_ID_PROPERTY,
          userId);
      Expression workflow_exp2 =
        ExpressionFactory.matchExp(EipTWorkflowRequestMap.STATUS_PROPERTY, "C");
      workflow_request_map_query.setQualifier(workflow_exp
        .andExp(workflow_exp2));
      List<EipTWorkflowRequestMap> workflow_request_map_list =
        workflow_request_map_query.fetchList();
      EipTWorkflowRequestMap workflow_request_map = null;
      int list_size = workflow_request_map_list.size();

      // 申請が来ているワークフローの数だけ繰り返す
      for (int j = 0; j < list_size; j++) {
        workflow_request_map = workflow_request_map_list.get(j);

        // ワークフローを最後の人まで見ていく
        int request_number = workflow_request_map.getOrderIndex();
        while (true) {
          // 次の人がいるかどうか
          SelectQuery<EipTWorkflowRequestMap> workflow_request_map_query2 =
            Database.query(EipTWorkflowRequestMap.class);
          Expression workflow_exp3 =
            ExpressionFactory.matchExp(
              EipTWorkflowRequestMap.EIP_TWORKFLOW_REQUEST_PROPERTY,
              workflow_request_map.getEipTWorkflowRequest());
          Expression workflow_exp4 =
            ExpressionFactory.matchExp(
              EipTWorkflowRequestMap.ORDER_INDEX_PROPERTY,
              Integer.valueOf(request_number + 1));
          workflow_request_map_query2.setQualifier(workflow_exp3
            .andExp(workflow_exp4));
          List<EipTWorkflowRequestMap> workflow_request_map_list2 =
            workflow_request_map_query2.fetchList();

          // 自身を自動承認状態にする
          workflow_request_map.setStatus("T");
          if (workflow_request_map_list2.size() == 1) {
            // 次の人が見つかった
            EipTWorkflowRequestMap workflow_request_map2 =
              workflow_request_map_list2.get(0);
            if (getUserIsDisabledOrDeleted(workflow_request_map2
              .getUserId()
              .toString())) {
              // 次の人が削除済み、もしくは無効化されていたら自動承認した上で次の人に回す
              workflow_request_map2.setStatus("T");
              request_number += 1;
            } else {
              // 次の人を確認状態にして終了
              workflow_request_map2.setStatus("C");
              break;
            }
          } else {
            // 次の人が見つからなければ、最後まで行ったことになるので終了
            if (workflow_request_map.getEipTWorkflowRequest() != null) {
              workflow_request_map.getEipTWorkflowRequest().setProgress("A");
            }
            break;
          }
        }
      }
      return true;
    } catch (Exception e) {
      logger.error("AccountUtils.acceptWorkflow", e);
      return false;
    }
  }

  /**
   * 管理者権限を持ったユーザを一人、管理者権限剥奪・無効化・削除しても<br/>
   * 最低限必要な管理者権限を持ったユーザ数を割らないかどうかを返します。
   * 
   * @return
   */
  public static boolean isAdminDeletable() {
    return isAdminDeletable(1);
  }

  /**
   * 管理者権限を持ったユーザを指定人数、管理者権限剥奪・無効化・削除しても<br/>
   * 最低限必要な管理者権限を持ったユーザ数を割らないかどうかを返します。
   * 
   * @param admin_count
   * @return
   */
  public static boolean isAdminDeletable(int admin_count) {
    try {
      int minimum_admin =
        Integer.valueOf(ALConfigService
          .get(Property.MINIMUM_ADMINISTRATOR_USER_COUNT));
      Group group = JetspeedSecurity.getGroup("LoginUser");
      Role adminrole = JetspeedSecurity.getRole("admin");
      int current_admin_count =
        Database.query(TurbineUserGroupRole.class).where(
          Operations.eq(TurbineUserGroupRole.TURBINE_ROLE_PROPERTY, adminrole
            .getId()),
          Operations.eq(TurbineUserGroupRole.TURBINE_GROUP_PROPERTY, group
            .getId()),
          Operations.ne(TurbineUserGroupRole.TURBINE_USER_PROPERTY, 1),
          Operations.eq(TurbineUserGroupRole.TURBINE_USER_PROPERTY
            + "."
            + TurbineUser.DISABLED_PROPERTY, "F")).distinct(true).getCount();
      int admin_count_will = current_admin_count - admin_count;
      if (admin_count_will < 0) {
        admin_count_will = 0;
      }
      return minimum_admin <= admin_count_will;
    } catch (JetspeedSecurityException e) {
      logger.error("AccountUtils.isAdminDeletable", e);
      return false;
    }
  }

  /**
   * 与えられたユーザー名に使われている記号が、使用できるものかを確認します。
   * 
   * @return
   */
  public static boolean isValidSymbolUserName(String name) {
    List<String> symbols = Arrays.asList(USER_NAME_SYMBOLS);
    for (char c : name.toCharArray()) {
      if (ALStringUtil.isSymbol(c) && !symbols.contains(String.valueOf(c))) {
        return false;
      }
    }
    return true;
  }
}
