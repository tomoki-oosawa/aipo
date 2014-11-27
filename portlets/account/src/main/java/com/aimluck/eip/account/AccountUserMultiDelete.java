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

package com.aimluck.eip.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDeleteFileUtil;
import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.portlet.EipTBlog;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogEntry;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFile;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFootmarkMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineFile;
import com.aimluck.eip.cayenne.om.portlet.EipTTimelineLike;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.datasync.ALDataSyncFactoryService;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * ユーザアカウントを複数削除するためのクラス． <BR>
 * 
 */
public class AccountUserMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserMultiDelete.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {

    try {
      // WebAPIのDBへ接続できるか確認
      if (!ALDataSyncFactoryService
        .getInstance()
        .getDataSyncHandler()
        .checkConnect()) {
        msgList.add(ALLocalizationUtils
          .getl10nFormat("ACCOUNT_ALERT_CONNECT_DB_FAILED"));
        return false;
      }

      Expression exp =
        ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, values);
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class, exp);
      List<TurbineUser> ulist = query.fetchList();
      if (ulist == null || ulist.size() == 0) {
        return false;
      }

      int size = ulist.size();
      String[] user_name_list = new String[size];

      // 予めバリデーション
      int admin_count = 0;
      for (TurbineUser user : ulist) {
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
        TurbineUser record = ulist.get(i);
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
        String userId = record.getUserId().toString();

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

        // ToDoを削除する
        String sql4 = "DELETE FROM eip_t_todo WHERE USER_ID = '" + userId + "'";
        Database.sql(EipTTodo.class, sql4);

        String sql5 =
          "DELETE FROM eip_t_todo_category WHERE USER_ID = '" + userId + "'";
        Database.sql(EipTTodoCategory.class, sql5);

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
              .where(
                Operations.in(EipTBlogEntry.EIP_TBLOG_PROPERTY, EipBlogList))
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
        String sql6 =
          "DELETE FROM eip_t_blog_footmark_map WHERE USER_ID = '"
            + userId
            + "'";
        Database.sql(EipTBlogFootmarkMap.class, sql6);

        // ソーシャルアプリ関連データ削除
        ALApplicationService.deleteUserData(user_name);

        // ワークフロー自動承認
        AccountUtils.acceptWorkflow(record.getUserId());

        // タイムライン削除
        Expression exp01 =
          ExpressionFactory.matchDbExp(EipTTimeline.OWNER_ID_COLUMN, record
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
            Integer timelineId = timeline.getTimelineId();
            if (timelineId != null) {
              timelineIdList.add(timelineId);
            }
          }
          if (!timelineIdList.isEmpty()) {

            SelectQuery<EipTTimeline> EipTTimelineSQL2 =
              Database.query(EipTTimeline.class).andQualifier(
                ExpressionFactory.inDbExp(
                  EipTTimeline.PARENT_ID_COLUMN,
                  timelineIdList));
            List<EipTTimeline> timelineCommentList =
              EipTTimelineSQL2.fetchList();
            List<Integer> timelineAllIdList =
              new ArrayList<Integer>(timelineIdList);
            if (timelineCommentList != null && !timelineCommentList.isEmpty()) {
              timelineList.addAll(timelineCommentList);
              for (EipTTimeline timeline : timelineCommentList) {
                Integer timelineId = timeline.getTimelineId();
                if (timelineId != null) {
                  timelineAllIdList.add(timelineId);
                }

              }
            }
            SelectQuery<EipTTimelineLike> EipTTimelineLikeSQL =
              Database.query(EipTTimelineLike.class);
            EipTTimelineLikeSQL.andQualifier(ExpressionFactory.inDbExp(
              EipTTimelineLike.EIP_TTIMELINE_PROPERTY,
              timelineAllIdList));
            List<EipTTimelineLike> likes = EipTTimelineLikeSQL.fetchList();
            Database.deleteAll(likes);

            for (EipTTimeline entry : timelineList) {
              List<String> fpaths = new ArrayList<String>();
              List<?> files = entry.getEipTTimelineFile();
              if (files != null && files.size() > 0) {
                int fileSize = files.size();
                for (int j = 0; j < fileSize; j++) {
                  fpaths.add(((EipTTimelineFile) files.get(j)).getFilePath());
                }

                Integer timelineId = entry.getTimelineId();
                if (timelineId != null) {
                  ALDeleteFileUtil.deleteFiles(
                    timelineId,
                    EipTTimelineFile.EIP_TTIMELINE_PROPERTY,
                    AccountUtils.getSaveDirPath(
                      orgId,
                      entry.getOwnerId(),
                      "timeline"),
                    fpaths,
                    EipTTimelineFile.class);
                }
              }
            }

            EipTTimelineSQL2.deleteAll();
            EipTTimelineSQL.deleteAll();
          }
        }

        // メッセージ
        /*-
        List<EipTMessageFile> messageFileList =
          Database
            .query(EipTMessageFile.class)
            .where(
              Operations.eq(EipTMessageFile.OWNER_ID_PROPERTY, record
                .getUserId()))
            .fetchList();

        ALDeleteFileUtil.deleteFiles(AccountUtils.getSaveDirPath(orgId, record
          .getUserId(), "message"), messageFileList);

        String messageDeleteSql1 =
          "delete from eip_t_message where user_id = #bind($user_id)";
        String messageDeleteSql2 =
          "delete from eip_t_message_room_member where user_id = #bind($user_id)";

        Database.sql(EipTMessage.class, messageDeleteSql1).param(
          "user_id",
          record.getUserId()).execute();
        Database.sql(EipTMessageRoomMember.class, messageDeleteSql2).param(
          "user_id",
          record.getUserId()).execute();
         */

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
    } catch (RuntimeException e) {
      Database.rollback();
      logger.error("AccountUserMultiDelete.action", e);
      return false;
    } catch (Exception e) {
      Database.rollback();
      logger.error("AccountUserMultiDelete.action", e);
      return false;
    }
  }
}
