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
package com.aimluck.eip.account;

import java.util.List;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.portlet.EipTBlog;
import com.aimluck.eip.cayenne.om.portlet.EipTBlogFootmarkMap;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.datasync.ALDataSyncFactoryService;
import com.aimluck.eip.util.ALEipUtils;

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
   * @see com.aimluck.eip.common.ALAbstractCheckList#action(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList,
   *      java.util.ArrayList)
   */
  protected boolean action(RunData rundata, Context context, List<String> values,
      List<String> msgList) {

    try {
      // WebAPIのDBへ接続できるか確認
      if (!ALDataSyncFactoryService.getInstance().getDataSyncHandler()
          .checkConnect()) {
        msgList.add("コントロールパネルWebAPIのデータベースの接続に失敗したため、処理は実行されませんでした。");
        return false;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      Expression exp = ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY,
          values);
      SelectQuery query = new SelectQuery(TurbineUser.class, exp);
      List<?> ulist = dataContext.performQuery(query);
      if (ulist == null || ulist.size() == 0)
        return false;

      int size = ulist.size();
      String[] user_name_list = new String[size];

      for (int i = 0; i < size; i++) {
        TurbineUser record = (TurbineUser) ulist.get(i);
        String user_name = record.getLoginName();
        user_name_list[i] = user_name;
        if (user_name == null)
          return false;

        // ユーザーを論理削除
        ObjectId oid_user = new ObjectId("TurbineUser",
            TurbineUser.LOGIN_NAME_COLUMN, user_name);
        TurbineUser user = (TurbineUser) dataContext.refetchObject(oid_user);
        user.setPositionId(Integer.valueOf(0));
        user.setDisabled("T");
        // dataContext.commitChanges();

        // ユーザーIDを取得する
        String userId = record.getUserId().toString();

        /*
         * SelectQuery getuser_query = new SelectQuery(TurbineUser.class);
         * Expression exp1 =
         * ExpressionFactory.matchExp(TurbineUser.LOGIN_NAME_PROPERTY,
         * user_name); getuser_query.setQualifier(exp1); List list3 =
         * dataContext.performQuery(getuser_query);
         *
         * int userNum = list3.size(); if (userNum != 1) return false;
         * TurbineUser deleteuser = (TurbineUser) list3.get(0); String userId;
         * userId = deleteuser.getUserId().toString();
         */

        // 対象ユーザのユーザーグループロールをすべて削除する
        SelectQuery ugr_query = new SelectQuery(TurbineUserGroupRole.class);
        Expression exp2 = ExpressionFactory.matchExp(
            TurbineUserGroupRole.TURBINE_USER_PROPERTY, userId);
        ugr_query.setQualifier(exp2);
        List<?> list4 = dataContext.performQuery(ugr_query);
        TurbineUserGroupRole ugr = null;
        for (int j = 0; j < list4.size(); j++) {
          ugr = (TurbineUserGroupRole) list4.get(j);
          dataContext.deleteObject(ugr);
        }
        // dataContext.commitChanges();

        /*
         * // ユーザーを論理削除 String sql1 = "UPDATE TURBINE_USER SET DISABLED = 'T',
         * POST_ID = 0, POSITION_ID = 0 WHERE LOGIN_NAME = '" + user_name + "'";
         * SQLTemplate rawSelect1 = new SQLTemplate(TurbineUser.class, sql1,
         * false); dataContext.performQuery(rawSelect1); //
         * 対象ユーザのユーザーグループロールをすべて削除する処理 String sql2 = "DELETE FROM
         * TURBINE_USER_GROUP_ROLE " + "WHERE USER_ID IN " + "(SELECT USER_ID
         * FROM TURBINE_USER WHERE login_name= '" + user_name + "')";
         * SQLTemplate rawSelect2 = new SQLTemplate(TurbineUser.class, sql2,
         * false); dataContext.performQuery(rawSelect2); // ユーザーIDを取得する int
         * userId = record.getUserId().intValue(); // 指定したユーザを削除する． String sql3 =
         * "DELETE FROM EIP_M_USER_POSITION WHERE USER_ID = '" + userId + "'";
         * SQLTemplate rawSelect3 = new SQLTemplate(EipMUserPosition.class,
         * sql3, false); dataContext.performQuery(rawSelect3);
         */

        // ToDoを削除する
        String sql4 = "DELETE FROM EIP_T_TODO WHERE USER_ID = '" + userId + "'";
        @SuppressWarnings("deprecation")
        SQLTemplate rawSelect4 = new SQLTemplate(EipTTodo.class, sql4, false);
        dataContext.performQuery(rawSelect4);

        String sql5 = "DELETE FROM EIP_T_TODO_CATEGORY WHERE USER_ID = '"
            + userId + "'";
        @SuppressWarnings("deprecation")
        SQLTemplate rawSelect5 = new SQLTemplate(EipTTodoCategory.class, sql5,
            false);
        dataContext.performQuery(rawSelect5);

        // ブログを削除する
        String sql6 = "DELETE FROM EIP_T_BLOG WHERE OWNER_ID = '" + userId
            + "'";
        @SuppressWarnings("deprecation")
        SQLTemplate rawSelect6 = new SQLTemplate(EipTBlog.class, sql6, false);
        dataContext.performQuery(rawSelect6);

        // ブログの足跡を削除する
        String sql7 = "DELETE FROM EIP_T_BLOG_FOOTMARK_MAP WHERE USER_ID = '"
            + userId + "'";
        @SuppressWarnings("deprecation")
        SQLTemplate rawSelect7 = new SQLTemplate(EipTBlogFootmarkMap.class,
            sql7, false);
        dataContext.performQuery(rawSelect7);

        // ワークフロー自動承認
        AccountUtils.acceptWorkflow(record.getUserId());

        /*
        SelectQuery workflow_request_map_query = new SelectQuery(EipTWorkflowRequestMap.class);
        Expression workflow_exp = ExpressionFactory.matchExp(
            EipTWorkflowRequestMap.USER_ID_PROPERTY, userId);
        Expression workflow_exp2 = ExpressionFactory.matchExp(
            EipTWorkflowRequestMap.STATUS_PROPERTY, "C");
        workflow_request_map_query.setQualifier(workflow_exp.andExp(workflow_exp2));
        List workflow_request_map_list = dataContext.performQuery(workflow_request_map_query);
        EipTWorkflowRequestMap workflow_request_map = null;
        for (int j = 0; j < list4.size(); j++) {
          workflow_request_map = (EipTWorkflowRequestMap) workflow_request_map_list.get(j);

          // 次の人がいるかどうか
          int request_number = workflow_request_map.getOrderIndex();
          SelectQuery workflow_request_map_query2 = new SelectQuery(EipTWorkflowRequestMap.class);
          Expression workflow_exp3 = ExpressionFactory.matchExp(
              EipTWorkflowRequestMap.EIP_TWORKFLOW_REQUEST_PROPERTY, workflow_request_map.getEipTWorkflowRequest());
          Expression workflow_exp4 = ExpressionFactory.matchExp(
              EipTWorkflowRequestMap.ORDER_INDEX_PROPERTY, Integer.valueOf(request_number + 1));
          workflow_request_map_query2.setQualifier(workflow_exp3.andExp(workflow_exp4));
          List workflow_request_map_list2 = dataContext.performQuery(workflow_request_map_query2);
          if (workflow_request_map_list2.size() == 1) {
            // 自動的に承認して次の人に回す
            workflow_request_map.setStatus("A");
            EipTWorkflowRequestMap workflow_request_map2 = (EipTWorkflowRequestMap) workflow_request_map_list2.get(0);
            workflow_request_map2.setStatus("C");
          }
        }
        */

        dataContext.commitChanges();

        // PSMLを削除
        JetspeedUser juser = JetspeedSecurity.getUser(new UserNamePrincipal(
            user_name));
        PsmlManager.removeUserDocuments(juser);

        // ユーザー名の先頭に"dummy_userid_"を追加
        String dummy_user_name = ALEipUtils.dummy_user_head + userId + "_"
            + user_name;
        user.setLoginName(dummy_user_name);
      }

      // 他のユーザの順番を変更する．
      SelectQuery p_query = new SelectQuery(EipMUserPosition.class);
      p_query.addOrdering(EipMUserPosition.POSITION_PROPERTY, true);
      List<?> userPositions = dataContext.performQuery(p_query);
      if (userPositions != null && userPositions.size() > 0) {
        EipMUserPosition userPosition = null;
        int possize = userPositions.size();
        for (int i = 0; i < possize; i++) {
          userPosition = (EipMUserPosition) userPositions.get(i);
          if (userPosition.getPosition().intValue() != (i + 1)) {
            userPosition.setPosition(Integer.valueOf(i + 1));
          }
        }
      }

      dataContext.commitChanges();

      // WebAPIとのDB同期
      if (!ALDataSyncFactoryService.getInstance().getDataSyncHandler()
          .multiDeleteUser(user_name_list, size)) {
        return false;
      }

      return true;
    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
  }
}
