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
package com.aimluck.eip.account.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDeleteFileUtil;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.account.AccountPositionResultData;
import com.aimluck.eip.account.AccountPostResultData;
import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTBlog;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFootmarkMap;
import com.aimluck.eip.cayenne.om.portlet.EipTMessage;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoomMember;
import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineFile;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineLike;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequestMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALFileNotRemovedException;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.datasync.ALDataSyncFactoryService;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.user.beans.UserGroupLiteBean;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

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

  public static final String ACCOUNT_POSITION_PORTLET_NAME = "AccountPosition";

  /** ユーザーの絞り込みに使用する項目 */
  private static final Map<Integer, FilterRole> roleMap =
    new HashMap<Integer, FilterRole>();

  public static final Integer ROLE_ADMIN = 1;

  public static final Integer ROLE_ACTIVE = 2;

  public static final Integer ROLE_IN_ACTIVE = 3;

  public static final Integer ROLE_INVITE = 4;

  /** 検索キーワード変数の識別子 */
  public static final String TARGET_KEYWORD = "keyword";

  /** 添付ファイルを保管するディレクトリの指定 */
  public static final String FOLDER_FILEDIR_ACCOUNT = JetspeedResources
    .getString("aipo.filedir", "");

  /** メッセージの添付ファイルを保管するディレクトリの指定 */
  public static final String FOLDER_FILEDIR_MESSAGE = JetspeedResources
    .getString("aipo.filedir", "");

  public static final String CATEGORY_KEY_MESSAGE = JetspeedResources
    .getString("aipo.message.categorykey", "");

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
        userid = rundata.getUser().getUserName();
      }
      if (userid == null) {
        logger.debug("Empty ID...");
        return null;
      }
      ALBaseUser user = (ALBaseUser) JetspeedSecurity.getUser(userid);
      if (user == null) {
        return null;
      }
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
      List<EipMPost> list =
        Database.query(EipMPost.class, exp).orderAscending(
          EipMPost.SORT_PROPERTY).fetchList();
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

  public static List<AccountPositionResultData> getAccountPositionAllList() {
    List<AccountPositionResultData> AccountPositionAllList =
      new ArrayList<AccountPositionResultData>();

    try {
      List<EipMPosition> aList =
        Database.query(EipMPosition.class).orderAscending(
          EipMPosition.SORT_PROPERTY).fetchList();

      for (EipMPosition record : aList) {
        AccountPositionResultData rd = new AccountPositionResultData();
        rd.initField();
        rd.setPositionId(record.getPositionId());
        rd.setPositionName(record.getPositionName());
        AccountPositionAllList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("accountposition", ex);
    }
    return AccountPositionAllList;
  }

  public static List<AccountPositionResultData> getAccountPositionResultList(
      List<EipMPosition> result) {
    List<AccountPositionResultData> list =
      new ArrayList<AccountPositionResultData>();
    for (EipMPosition model : result) {
      list.add(getAccountPositionResultData(model));
    }
    return list;
  }

  public static AccountPositionResultData getAccountPositionResultData(
      EipMPosition model) {
    AccountPositionResultData data = new AccountPositionResultData();
    data.initField();
    data.setPositionId(model.getPositionId());
    data.setPositionName(model.getPositionName());
    data.setUpdate_date(model.getUpdateDate());
    data.setCreateDate(model.getCreateDate());
    return data;
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
   * @param rundata
   * @param userlist
   * @param msgList
   * @return
   * @throws ALDBErrorException
   * @throws ALFileNotRemovedException
   * @throws JetspeedSecurityException
   */
  public static boolean deleteUserList(RunData rundata,
      List<TurbineUser> userlist, List<String> msgList)
      throws ALDBErrorException, ALFileNotRemovedException,
      JetspeedSecurityException {
    // WebAPIのDBへ接続できるか確認
    if (!ALDataSyncFactoryService
      .getInstance()
      .getDataSyncHandler()
      .checkConnect()) {
      msgList.add(ALLocalizationUtils
        .getl10nFormat("ACCOUNT_ALERT_CONNECT_DB_FAILED"));
      return false;
    }

    int size = userlist.size();
    String[] user_name_list = new String[size];

    // 予めバリデーション
    int admin_count = 0;
    for (TurbineUser user : userlist) {
      if (user.getLoginName().equals(rundata.getUser().getUserName())) {
        msgList.add(ALLocalizationUtils
          .getl10nFormat("ACCOUNT_ALERT_DELETE_LOGINUSER"));
        return false;
      }
      if (ALEipUtils.isAdmin(user.getUserId())
        && ALEipUtils.isEnabledUser(user.getUserId())) {
        admin_count++;
      }
    }

    if (!AccountUtils.isAdminDeletable(admin_count)) {
      msgList.add(ALLocalizationUtils.getl10nFormat(
        "ACCOUNT_ALERT_NUMOFADMINS_LIMIT",
        Integer.valueOf(ALConfigService
          .get(Property.MINIMUM_ADMINISTRATOR_USER_COUNT))));
      return false;
    }

    for (int i = 0; i < size; i++) {
      TurbineUser record = userlist.get(i);
      String user_name = record.getLoginName();
      user_name_list[i] = user_name;
      if (user_name == null) {
        return false;
      }

      TurbineUser user =
        Database.get(
          TurbineUser.class,
          TurbineUser.LOGIN_NAME_COLUMN,
          user_name);

      // ユーザーを論理削除
      user.setPositionId(Integer.valueOf(0));
      user.setDisabled("T");

      // ユーザーIDを取得する
      String userId = user.getUserId().toString();

      // 対象ユーザのユーザーグループロールをすべて削除する
      SelectQuery<TurbineUserGroupRole> ugr_query =
        Database.query(TurbineUserGroupRole.class);
      Expression exp2 =
        ExpressionFactory.matchExp(
          TurbineUserGroupRole.TURBINE_USER_PROPERTY,
          userId);
      ugr_query.setQualifier(exp2);
      List<TurbineUserGroupRole> list4 = ugr_query.fetchList();
      TurbineUserGroupRole ugr = null;
      for (int j = 0; j < list4.size(); j++) {
        ugr = list4.get(j);
        Database.delete(ugr);
      }

      // TODOの削除
      Database.query(EipTTodo.class).where(
        Operations.in(EipTTodo.USER_ID_PROPERTY, userId)).deleteAll();
      Database.query(EipTTodoCategory.class).where(
        Operations.in(EipTTodoCategory.USER_ID_PROPERTY, userId)).deleteAll();

      String orgId = Database.getDomainName();

      // ブログの削除
      SelectQuery<EipTBlog> EipBlogSQL =
        Database.query(EipTBlog.class).where(
          Operations.in(EipTBlog.OWNER_ID_PROPERTY, userId));
      List<EipTBlog> EipBlogList = EipBlogSQL.fetchList();
      if (EipBlogList != null && EipBlogList.size() > 0) {
        List<EipTBlogEntry> EipTBlogEntryList =
          Database
            .query(EipTBlogEntry.class)
            .where(Operations.in(EipTBlogEntry.EIP_TBLOG_PROPERTY, EipBlogList))
            .fetchList();

        for (EipTBlogEntry entry : EipTBlogEntryList) {
          List<String> fpaths = new ArrayList<String>();
          List<?> files = entry.getEipTBlogFiles();
          if (files != null && files.size() > 0) {
            int fileSize = files.size();
            for (int j = 0; j < fileSize; j++) {
              fpaths.add(((EipTBlogFile) files.get(j)).getFilePath());
            }

            ALDeleteFileUtil.deleteFiles(
              entry.getEntryId(),
              EipTBlogFile.EIP_TBLOG_ENTRY_PROPERTY,
              AccountUtils.getSaveDirPath(orgId, entry.getOwnerId(), "blog"),
              fpaths,
              EipTBlogFile.class);

          }
        }
        Database
          .query(EipTBlogEntry.class)
          .where(Operations.in(EipTBlogEntry.EIP_TBLOG_PROPERTY, EipBlogList))
          .deleteAll();

        EipBlogSQL.deleteAll();
      }

      // ブログの足跡を削除する
      Database
        .query(EipTBlogFootmarkMap.class)
        .where(Operations.in(EipTBlogFootmarkMap.USER_ID_PROPERTY, userId))
        .deleteAll();

      // ソーシャルアプリ関連データ削除
      ALApplicationService.deleteUserData(user_name);

      // ワークフロー自動承認
      AccountUtils.acceptWorkflow(user.getUserId());

      // タイムライン削除
      Expression exp01 =
        ExpressionFactory.matchDbExp(EipTTimeline.OWNER_ID_COLUMN, user
          .getUserId());

      Expression exp02 =
        ExpressionFactory.matchDbExp(EipTTimeline.PARENT_ID_COLUMN, 0);
      Expression exp03 =
        ExpressionFactory.matchDbExp(
          "TIMELINE_TYPE",
          EipTTimeline.TIMELINE_TYPE_TIMELINE);

      SelectQuery<EipTTimeline> EipTTimelineSQL =
        Database.query(EipTTimeline.class).andQualifier(
          exp01.andExp(exp02.andExp(exp03)));
      List<EipTTimeline> timelineList = EipTTimelineSQL.fetchList();
      if (!timelineList.isEmpty()) {
        List<Integer> timelineIdList = new ArrayList<Integer>();
        for (EipTTimeline timeline : timelineList) {
          timelineIdList.add(timeline.getTimelineId());
        }
        if (!timelineIdList.isEmpty()) {
          SelectQuery<EipTTimeline> EipTTimelineSQL2 =
            Database.query(EipTTimeline.class).andQualifier(
              ExpressionFactory.inDbExp(
                EipTTimeline.PARENT_ID_COLUMN,
                timelineIdList));
          List<EipTTimeline> timelineCommentList = EipTTimelineSQL2.fetchList();
          List<Integer> timelineAllIdList =
            new ArrayList<Integer>(timelineIdList);
          if (timelineCommentList != null && !timelineCommentList.isEmpty()) {
            timelineList.addAll(timelineCommentList);
            for (EipTTimeline timeline : timelineCommentList) {
              timelineAllIdList.add(timeline.getTimelineId());
            }
          }
          SelectQuery<EipTTimelineLike> EipTTimelineLikeSQL =
            Database.query(EipTTimelineLike.class);
          EipTTimelineLikeSQL.andQualifier(ExpressionFactory.inDbExp(
            EipTTimelineLike.EIP_TTIMELINE_PROPERTY,
            timelineAllIdList));

          for (EipTTimeline entry : timelineList) {
            List<String> fpaths = new ArrayList<String>();
            List<?> files = entry.getEipTTimelineFile();
            if (files != null && files.size() > 0) {
              int fileSize = files.size();
              for (int j = 0; j < fileSize; j++) {
                fpaths.add(((EipTTimelineFile) files.get(j)).getFilePath());
              }
              ALDeleteFileUtil.deleteFiles(
                entry.getTimelineId(),
                EipTTimelineFile.EIP_TTIMELINE_PROPERTY,
                AccountUtils.getSaveDirPath(
                  orgId,
                  entry.getOwnerId(),
                  "timeline"),
                fpaths,
                EipTTimelineFile.class);
            }
          }
          EipTTimelineLikeSQL.deleteAll();
          EipTTimelineSQL2.deleteAll();
          EipTTimelineSQL.deleteAll();
        }
      }

      Expression exp011 =
        ExpressionFactory.matchDbExp(EipTTimeline.OWNER_ID_COLUMN, user
          .getUserId());

      Expression exp021 =
        ExpressionFactory.noMatchDbExp(EipTTimeline.PARENT_ID_COLUMN, 0);
      Expression exp031 =
        ExpressionFactory.matchDbExp(
          "TIMELINE_TYPE",
          EipTTimeline.TIMELINE_TYPE_TIMELINE);

      SelectQuery<EipTTimeline> EipTTimelineSQL1 =
        Database.query(EipTTimeline.class).andQualifier(
          exp011.andExp(exp021.andExp(exp031)));
      List<EipTTimeline> timelineList1 = EipTTimelineSQL1.fetchList();

      if (!timelineList1.isEmpty()) {
        SelectQuery<EipTTimelineLike> EipTTimelineLikeSQL2 =
          Database.query(EipTTimelineLike.class);
        EipTTimelineLikeSQL2.andQualifier(ExpressionFactory.inDbExp(
          EipTTimelineLike.EIP_TTIMELINE_PROPERTY,
          timelineList1));

        EipTTimelineLikeSQL2.deleteAll();
        EipTTimelineSQL1.deleteAll();

      }

      SelectQuery<EipTTimelineLike> EipTTimelineLikeSQL3 =
        Database.query(EipTTimelineLike.class);
      EipTTimelineLikeSQL3.andQualifier(ExpressionFactory.matchDbExp(
        EipTTimelineLike.OWNER_ID_COLUMN,
        user.getUserId()));
      EipTTimelineLikeSQL3.deleteAll();

      // メッセージルームの管理者権限移行
      AccountUtils.shiftMessageroomAdmin(user.getUserId());

      // メッセージ
      List<EipTMessageFile> messageFileList =
        Database
          .query(EipTMessageFile.class)
          .where(
            Operations.eq(EipTMessageFile.OWNER_ID_PROPERTY, user.getUserId()))
          .fetchList();

      ALDeleteFileUtil.deleteFiles(AccountUtils.getSaveDirPath(orgId, user
        .getUserId(), "message"), messageFileList);

      Database.query(EipTMessage.class).where(
        Operations.in(EipTMessage.USER_ID_PROPERTY, userId)).deleteAll();
      Database
        .query(EipTMessageRoomMember.class)
        .where(Operations.in(EipTMessageRoomMember.USER_ID_PROPERTY, userId))
        .deleteAll();

      // 削除対象ユーザー以外にメンバーのいないメッセージルームを削除
      StringBuilder sql = new StringBuilder();
      sql.append("select * from eip_t_message_room ");
      sql.append("where room_id in ");
      sql.append("( ");
      sql.append("select room_id from eip_t_message_room_member ");
      sql.append("where room_id in ");
      sql.append("( ");
      sql.append("select room_id from eip_t_message_room_member ");
      sql.append("group by room_id ");
      sql.append("having COUNT(*) = 1 ");
      sql.append(") ");
      sql.append("and user_id = #bind($userId) ");
      sql.append(")");

      List<EipTMessageRoom> deleteRoomList =
        Database.sql(EipTMessageRoom.class, sql.toString()).param(
          "userId",
          Integer.parseInt(userId)).fetchList();

      List<Integer> deleteRoomIdList = new ArrayList<Integer>();
      for (EipTMessageRoom room : deleteRoomList) {
        deleteRoomIdList.add(room.getRoomId());
      }

      // 削除対象ユーザー以外にメンバーのいないメッセージルームの添付ファイルを削除
      for (Integer roomId : deleteRoomIdList) {
        List<EipTMessageFile> messageRoomfiles =
          Database
            .query(EipTMessageFile.class)
            .where(Operations.eq(EipTMessageFile.ROOM_ID_PROPERTY, roomId))
            .fetchList();

        ALDeleteFileUtil.deleteFiles(
          AccountUtils.FOLDER_FILEDIR_MESSAGE,
          AccountUtils.CATEGORY_KEY_MESSAGE,
          messageRoomfiles);
      }

      if (deleteRoomIdList.size() > 0) {
        Database.query(EipTMessageRoom.class).andQualifier(
          ExpressionFactory.inDbExp(
            EipTMessageRoom.ROOM_ID_PK_COLUMN,
            deleteRoomIdList)).deleteAll();
      }

      Database.commit();

      // イベントログに保存
      String name = "";
      if (user.getLastName() != null
        && !" ".equals(user.getLastName())
        && user.getFirstName() != null
        && !" ".equals(user.getFirstName())) {
        name =
          new StringBuffer().append(user.getLastName()).append(" ").append(
            user.getFirstName()).toString();
      } else {
        name = user.getEmail();
      }
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        user.getUserId(),
        ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
        "ユーザー「" + name + "」を削除");

      // PSMLを削除
      JetspeedUser juser =
        JetspeedSecurity.getUser(new UserNamePrincipal(user_name));
      PsmlManager.removeUserDocuments(juser);

      // ユーザー名の先頭に"dummy_userid_"を追加
      String dummy_user_name =
        ALEipUtils.dummy_user_head + userId + "_" + user_name;
      user.setLoginName(dummy_user_name);
    }

    // 他のユーザの順番を変更する．
    SelectQuery<EipMUserPosition> p_query =
      Database.query(EipMUserPosition.class);
    p_query.orderAscending(EipMUserPosition.POSITION_PROPERTY);
    List<EipMUserPosition> userPositions = p_query.fetchList();
    if (userPositions != null && userPositions.size() > 0) {
      EipMUserPosition userPosition = null;
      int possize = userPositions.size();
      for (int i = 0; i < possize; i++) {
        userPosition = userPositions.get(i);
        if (userPosition.getPosition().intValue() != (i + 1)) {
          userPosition.setPosition(Integer.valueOf(i + 1));
        }
      }
    }

    Database.commit();

    // WebAPIとのDB同期
    if (!ALDataSyncFactoryService
      .getInstance()
      .getDataSyncHandler()
      .multiDeleteUser(user_name_list, size)) {
      return false;
    }

    return msgList.size() == 0;
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

  /**
   * 指定されたuserIdが使用しているメールの総容量を返します。 <BR>
   *
   * @param userId
   * @return メールの容量
   */
  public static long getStorageSizeOfUserMail(Integer userId) {
    long totalSize = 0;
    try {

      // ユーザーが持っているアカウントを取得。
      List<EipMMailAccount> accounts = ALMailUtils.getMailAccountList(userId);

      String orgId = Database.getDomainName();

      // 全アカウントに対してフォルダの容量を取得していく。
      if (accounts != null) {
        for (EipMMailAccount account : accounts) {

          Integer accountId = account.getAccountId();

          ALMailHandler handler =
            ALMailFactoryService.getInstance().getMailHandler();

          totalSize += handler.getFolderSize(orgId, userId, accountId);
        }
      }

    } catch (Throwable t) {
    }

    return totalSize;
  }

  /**
   * データ容量を単位つきで返します。 <BR>
   *
   * @param size
   * @return 文字列
   */
  public static String getSizeStr(long size) {
    if (1024 > size) {
      return size + " Byte";
    } else if (1024 * 1024 > size) {
      double dsize = size;
      dsize = dsize / 1024;
      BigDecimal bi = new BigDecimal(String.valueOf(dsize));
      double value = bi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
      return value + " KByte";
    } else {
      double dsize = size;
      dsize = dsize / 1024 / 1024;
      BigDecimal bi = new BigDecimal(String.valueOf(dsize));
      double value = bi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
      return value + " MB";
    }
  }

  /**
   * ユーザーのRole一覧を返します。
   *
   * @return
   */
  public static Map<Integer, FilterRole> getRoleMap() {
    if (roleMap.size() == 0) {
      roleMap.put(ROLE_ADMIN, new FilterRole(ROLE_ADMIN, ALLocalizationUtils
        .getl10nFormat("ACCOUNT_ROLE_ADMIN")));
      roleMap.put(ROLE_ACTIVE, new FilterRole(ROLE_ACTIVE, ALLocalizationUtils
        .getl10nFormat("ACCOUNT_ROLE_ACTIVE")));
      roleMap.put(ROLE_IN_ACTIVE, new FilterRole(
        ROLE_IN_ACTIVE,
        ALLocalizationUtils.getl10nFormat("ACCOUNT_ROLE_IN_ACTIVE")));
      // roleMap.put(ROLE_INVITE, new FilterRole(ROLE_INVITE,
      // ALLocalizationUtils
      // .getl10nFormat("ACCOUNT_ROLE_INVITE")));
    }

    return roleMap;
  }

  public static class FilterRole {
    private Integer roleId;

    private String roleName;

    public FilterRole(Integer id, String name) {
      this.setRoleId(id);
      this.setRoleName(name);
    }

    /**
     * @return roleId
     */
    public Integer getRoleId() {
      return roleId;
    }

    /**
     * @param roleId
     *          セットする roleId
     */
    public void setRoleId(Integer roleId) {
      this.roleId = roleId;
    }

    /**
     * @return roleName
     */
    public String getRoleName() {
      return roleName;
    }

    /**
     * @param roleName
     *          セットする roleName
     */
    public void setRoleName(String roleName) {
      this.roleName = roleName;
    }

  }

  /**
   * 表示切り替えで指定した検索キーワードを取得する．
   *
   * @param rundata
   * @param context
   * @return
   */
  public static String getTargetKeyword(RunData rundata, Context context) {
    String target_keyword = null;
    String keywordParam = rundata.getParameters().getString(TARGET_KEYWORD);
    target_keyword = ALEipUtils.getTemp(rundata, context, TARGET_KEYWORD);

    if (keywordParam == null && (target_keyword == null)) {
      ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, "");
      target_keyword = "";
    } else if (keywordParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, keywordParam.trim());
      target_keyword = keywordParam;
    }
    return target_keyword;
  }

  public static String getSaveDirPath(String orgId, int uid, String portletName) {
    return ALStorageService.getDocumentPath(
      FOLDER_FILEDIR_ACCOUNT,
      getCategoryKey(portletName) + ALStorageService.separator() + uid);
  }

  public static String getCategoryKey(String portletName) {
    return JetspeedResources.getString(
      "aipo." + portletName + ".categorykey",
      "");
  }

  public static String getPortletId(RunData rundata) {
    HashMap<String, String> map = ALEipUtils.getPortletFromAppIdMap(rundata);
    Iterator<java.util.Map.Entry<String, String>> iterator =
      map.entrySet().iterator();
    while (iterator.hasNext()) {
      java.util.Map.Entry<String, String> next = iterator.next();
      if (next.getKey().endsWith("AccountPerson")) {
        return next.getValue();
      }
    }
    return null;
  }

  public static List<AccountPostResultData> getAccountPostResultList(
      List<EipMPost> result) {
    List<AccountPostResultData> list = new ArrayList<AccountPostResultData>();
    for (EipMPost model : result) {
      list.add(getAccountPostResultData(model));
    }
    return list;
  }

  public static AccountPostResultData getAccountPostResultData(EipMPost model) {
    AccountPostResultData data = new AccountPostResultData();
    data.initField();
    data.setPostId(model.getPostId());
    data.setCompanyId(model.getCompanyId());
    data.setPostName(model.getPostName());
    data.setZipcode(model.getZipcode());
    data.setAddress(model.getAddress());
    data.setInTelephone(model.getInTelephone());
    data.setOutTelephone(model.getOutTelephone());
    data.setFaxNumber(model.getFaxNumber());
    data.setCreateDate(model.getCreateDate().toString());
    data.setUpdateDate(model.getUpdateDate().toString());
    data.setGroupName(model.getGroupName());
    return data;
  }

  /**
   * 指定されたユーザーが削除／無効化されたとき、メッセージルームの管理者権限を他のユーザーに移します。
   *
   * @param uid
   */
  public static boolean shiftMessageroomAdmin(int uid) {
    try {
      String userId = Integer.toString(uid);

      // user_idが自分で、管理者であるデータを取得する(チェックする必要があるルームを取得)
      SelectQuery<EipTMessageRoomMember> message_room_query =
        Database.query(EipTMessageRoomMember.class);
      Expression msgroom_exp =
        ExpressionFactory.matchExp(
          EipTMessageRoomMember.USER_ID_PROPERTY,
          userId);
      Expression msgroom_exp2 =
        ExpressionFactory.matchExp(
          EipTMessageRoomMember.AUTHORITY_PROPERTY,
          "A");
      message_room_query.setQualifier(msgroom_exp.andExp(msgroom_exp2));
      List<EipTMessageRoomMember> message_room_list =
        message_room_query.fetchList();

      // ルーム一つずつについて、自分の他のメンバーを取り出す
      for (EipTMessageRoomMember message_room : message_room_list) {
        SelectQuery<EipTMessageRoomMember> message_room_query2 =
          Database.query(EipTMessageRoomMember.class);
        Expression msgroom_exp3 =
          ExpressionFactory.matchExp(
            EipTMessageRoomMember.EIP_TMESSAGE_ROOM_PROPERTY,
            message_room.getEipTMessageRoom().getRoomId());
        Expression msgroom_exp4 =
          ExpressionFactory.noMatchExp(
            EipTMessageRoomMember.USER_ID_PROPERTY,
            message_room.getUserId());
        message_room_query2.setQualifier(msgroom_exp3.andExp(msgroom_exp4));
        List<EipTMessageRoomMember> message_room_member_list =
          message_room_query2.fetchList();

        boolean flag = false;
        // 管理者権限を持つメンバーがいたら抜ける。一人もいなかったらflagをtrueにする
        for (Iterator<EipTMessageRoomMember> iterator =
          message_room_member_list.iterator(); iterator.hasNext();) {
          EipTMessageRoomMember member = iterator.next();
          if (member.getAuthority().equals("A")) {
            break;
          }
          if (!iterator.hasNext()) {
            flag = true;
          }
        }

        // flagがtrueなら全員を管理者に設定する
        if (flag) {
          for (EipTMessageRoomMember member : message_room_member_list) {
            member.setAuthority("A");
          }
        }
      }
      return true;

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return false;
    }
  }

}
