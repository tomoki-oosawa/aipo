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
package com.aimluck.eip.todo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.ALAdminMailContext;
import com.aimluck.eip.mail.ALAdminMailMessage;
import com.aimluck.eip.mail.ALMailService;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoの状態を更新するクラスです。 <BR>
 * 
 */
public class ToDoStateUpdateData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoStateUpdateData.class.getName());

  /** 状態 */
  private ALNumberField state;

  /** ACL用の変数 * */
  private String aclPortletFeature;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    int login_user_id = ALEipUtils.getUserId(rundata);
    String todoId = rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    String userId = rundata.getParameters().getString("user_id");

    if (todoId == null || todoId.equals("new")) {
      if (userId != null && !userId.equals(String.valueOf(login_user_id))) {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER;
      } else {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
      }
    } else {
      EipTTodo todo = ToDoUtils.getEipTTodo(rundata, context, true);
      if ((todo != null && todo.getTurbineUser().getUserId() != login_user_id)) {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER;
      } else {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
      }
    }
  }

  /**
   * フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // 状態
    state = new ALNumberField();
    state.setFieldName("進捗");
  }

  /**
   * 状態フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // 0 から 100 まで
    state.limitValue(0, 100);
    // 必須項目
    state.setNotNull(true);
  }

  /**
   * 入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    int value = (int) state.getValue();
    // 0以上100以下で、10の倍数
    return (value % 10 == 0 && state.validate(msgList));
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 状態を更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {

      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

      EipTTodo todo = ToDoUtils.getEipTTodo(rundata, context, false);
      if (todo == null) {
        return false;
      }
      todo.setState(Short.valueOf((short) state.getValue()));
      todo.setUpdateDate(Calendar.getInstance().getTime());

      // Todoを更新
      Database.commit();

      // メール送信

      try {
        List<ALEipUser> memberList = new ArrayList<ALEipUser>();
        memberList.add(ALEipUtils.getALEipUser(todo.getUserId()));
        int msgType = ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_TODO);
        if (msgType > 0) {
          // パソコンへメールを送信
          List<ALEipUserAddr> destMemberList =
            ALMailUtils.getALEipUserAddrs(memberList, ALEipUtils
              .getUserId(rundata), false);
          String subject = "[" + ALOrgUtilsService.getAlias() + "]ToDo";
          String orgId = Database.getDomainName();

          List<ALAdminMailMessage> messageList =
            new ArrayList<ALAdminMailMessage>();
          for (ALEipUserAddr destMember : destMemberList) {
            ALAdminMailMessage message = new ALAdminMailMessage(destMember);
            message.setPcSubject(subject);
            message.setCellularSubject(subject);
            message.setPcBody(ToDoUtils.createMsgForPc(
              rundata,
              todo,
              memberList,
              false));
            message.setCellularBody(ToDoUtils.createMsgForPc(
              rundata,
              todo,
              memberList,
              false));
            messageList.add(message);
          }
          ALMailService.sendAdminMailAsync(new ALAdminMailContext(
            orgId,
            ALEipUtils.getUserId(rundata),
            messageList,
            ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_TODO)));
        }
      } catch (Exception ex) {
        msgList.add("メールを送信できませんでした。");
        logger.error("todo", ex);
        return false;
      }

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[ToDoStateUpdateData]", t);
      return false;
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 状態を取得します。 <BR>
   * 
   * @return
   */
  public ALNumberField getState() {
    return state;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }
}
