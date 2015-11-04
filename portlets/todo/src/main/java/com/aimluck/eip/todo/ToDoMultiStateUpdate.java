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
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.mail.ALAdminMailContext;
import com.aimluck.eip.mail.ALAdminMailMessage;
import com.aimluck.eip.mail.ALMailService;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 複数のToDoの状態を更新するクラスです
 *
 */
public class ToDoMultiStateUpdate extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoMultiStateUpdate.class.getName());

  private String aclPortletFeature;

  /**
   * @param rundata
   * @param context
   * @param defineAclType
   * @return
   * @throws ALPermissionException
   */
  @Override
  protected boolean doCheckAclPermission(RunData rundata, Context context,
      int defineAclType) throws ALPermissionException {
    List<String> values = new ArrayList<String>();
    Object[] objs = rundata.getParameters().getKeys();
    int length = objs.length;
    for (int i = 0; i < length; i++) {
      if (objs[i].toString().startsWith("check")) {
        String str = rundata.getParameters().getString(objs[i].toString());
        values.add(str);
      }
    }

    Expression exp1 =
      ExpressionFactory.noMatchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    Expression exp2 =
      ExpressionFactory.inDbExp(EipTTodo.TODO_ID_PK_COLUMN, values);

    if (Database
      .query(EipTTodo.class)
      .andQualifier(exp1)
      .andQualifier(exp2)
      .getCount() > 0) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
    }

    return super.doCheckAclPermission(rundata, context, defineAclType);
  }

  /**
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

      Expression exp1 =
        ExpressionFactory.inDbExp(EipTTodo.TODO_ID_PK_COLUMN, values);

      List<EipTTodo> todoList =
        Database.query(EipTTodo.class).andQualifier(exp1).fetchList();

      if (todoList == null || todoList.size() == 0) {
        return false;
      }

      for (EipTTodo todo : todoList) {
        todo.setState(Short.valueOf((short) 100));
      }

      Database.commit();

      for (EipTTodo todo : todoList) {
        // メール送信
        if (aclPortletFeature
          .equals(ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER)) {
          try {
            List<ALEipUser> memberList = new ArrayList<ALEipUser>();
            memberList.add(ALEipUtils.getALEipUser(todo.getUserId()));
            int msgType =
              ALMailUtils.getSendDestType(ALMailUtils.KEY_MSGTYPE_TODO);
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
        }
      }
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[ToDoMultiStateUpdate]", t);
      return false;
    }
    return true;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限を返します。
   *
   * @return
   */
  @Override
  protected int getDefineAclType() {
    return ALAccessControlConstants.VALUE_ACL_UPDATE;
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
