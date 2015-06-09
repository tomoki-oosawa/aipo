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
package com.aimluck.eip.workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRoute;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフロー申請経路のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class WorkflowRouteFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowRouteFormData.class.getName());

  /** 申請経路名 */
  private ALStringField route_name;

  /** メモ */
  private ALStringField note;

  private ALStringField route;

  private Integer route_id;

  /** <code>memberList</code> メンバーリスト */
  private List<ALEipUser> memberList;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    String routeid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    if (routeid != null && Integer.valueOf(routeid) != null) {
      route_id = Integer.valueOf(routeid);
    }
  }

  /**
   *
   *
   */
  @Override
  public void initField() {
    // カテゴリ名
    route_name = new ALStringField();
    route_name.setFieldName(ALLocalizationUtils
      .getl10n("WORKFLOW_APPLICATION_ROUTE_NAME"));
    route_name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils.getl10n("WORKFLOW_MEMO"));
    note.setTrim(true);
    // 申請経路
    route = new ALStringField();
    route.setFieldName(ALLocalizationUtils
      .getl10n("WORKFLOW_APPLICATION_ROUTE"));
    route.setTrim(true);

    memberList = new ArrayList<ALEipUser>();
  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      try {
        String userNames[] = rundata.getParameters().getStrings("positions");
        if (userNames != null && userNames.length > 0) {

          SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
          Expression exp1 =
            ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, userNames);
          Expression exp2 =
            ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");
          query.setQualifier(exp1);
          query.andQualifier(exp2);

          memberList.addAll(ALEipUtils.getUsersFromSelectQuery(query));

          List<TurbineUser> list = query.fetchList();

          TurbineUser record = null;
          int length = userNames.length;
          StringBuilder builder = new StringBuilder();
          for (int i = 0; i < length; i++) {
            record = getEipUserRecord(list, userNames[i]);
            builder.append(record.getUserId());
            if (i != userNames.length - 1) {
              builder.append(",");
            }
          }
          String stCsv = builder.toString();
          route.setValue(stCsv);
        }
      } catch (Exception ex) {
        logger.error("[WorkflowRouteFormData]", ex);
      }
    }
    return res;
  }

  /**
   * ワークフロー申請経路の各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // カテゴリ名必須項目
    route_name.setNotNull(true);
    // カテゴリ名文字数制限
    route_name.limitMaxLength(50);
    // 申請経路文字数制限
    route.limitMaxLength(1000);
    // メモ文字数制限
    note.limitMaxLength(10000);
  }

  /**
   * ワークフロー申請経路のフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      SelectQuery<EipTWorkflowRoute> query =
        Database.query(EipTWorkflowRoute.class);
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTWorkflowRoute.ROUTE_NAME_PROPERTY,
          route_name.getValue());
      query.setQualifier(exp1);
      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(
            EipTWorkflowRoute.ROUTE_ID_PK_COLUMN,
            route_id);
        query.andQualifier(exp2);
      }
      if (query.fetchList().size() != 0) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "WORKFLOW_ALERT_ROUTE_ALREADY_CREATED",
          route_name.toString()));
      }
      if (route.getValue() == null) {
        msgList.add(ALLocalizationUtils.getl10nFormat("WORKFLOW_ALERT_TO"));
      }
    } catch (Exception ex) {
      logger.error("workflow", ex);
      return false;
    }
    // カテゴリ名
    route_name.validate(msgList);
    // メモ
    note.validate(msgList);
    // 申請経路
    route.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * ワークフロー申請経路をデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTWorkflowRoute routeobj =
        WorkflowUtils.getEipTWorkflowRoute(rundata, context);
      if (route == null) {
        return false;
      }
      // カテゴリ名
      route_name.setValue(routeobj.getRouteName());
      // 申請経路
      route.setValue(routeobj.getRoute());
      // メモ
      note.setValue(routeobj.getNote());

      String users = routeobj.getRoute();

      StringTokenizer st = new StringTokenizer(users, ",");
      int size = st.countTokens();

      String[] userarray = new String[size];

      for (int i = 0; i < size; i++) {
        userarray[i] = st.nextToken();
      }

      if (size > 0) {
        SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
        Expression exp =
          ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, userarray);
        query.setQualifier(exp);
        // memberList.addAll(ALEipUtils.getUsersFromSelectQuery(query));

        // 取得したクエリがソートされてしまうので、元の順序に並び替えて配列に入れる
        List<ALEipUser> memberListTmp =
          ALEipUtils.getUsersFromSelectQuery(query);
        for (int i = 0; i < userarray.length; i++) {
          for (int j = 0; j < memberListTmp.size(); j++) {
            ALEipUser usertmp = memberListTmp.get(j);
            if (userarray[i].compareTo(usertmp.getUserId().toString()) == 0) {
              memberList.add(usertmp);
            }
          }
        }

      }
    } catch (Exception ex) {
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

  /**
   * ワークフロー申請経路をデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      EipTWorkflowRoute routeobj = Database.create(EipTWorkflowRoute.class);
      routeobj.setRouteName(route_name.getValue());
      routeobj.setNote(note.getValue());
      routeobj.setCreateDate(Calendar.getInstance().getTime());
      routeobj.setUpdateDate(Calendar.getInstance().getTime());
      routeobj.setRoute(route.getValue());
      Database.commit();
      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        routeobj.getRouteId(),
        ALEventlogConstants.PORTLET_TYPE_WORKFLOW_ROUTE,
        routeobj.getRouteName());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているワークフロー申請経路を更新します。 <BR>
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
      // オブジェクトモデルを取得
      EipTWorkflowRoute routeobj =
        WorkflowUtils.getEipTWorkflowRoute(rundata, context);
      if (route == null) {
        return false;
      }
      // カテゴリ名
      routeobj.setRouteName(route_name.getValue());
      // 申請経路
      routeobj.setRoute(route.getValue());
      // メモ
      routeobj.setNote(note.getValue());
      // 更新日
      routeobj.setUpdateDate(Calendar.getInstance().getTime());

      // カテゴリを更新
      Database.commit();
      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        routeobj.getRouteId(),
        ALEventlogConstants.PORTLET_TYPE_WORKFLOW_ROUTE,
        routeobj.getRouteName());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

  /**
   * ワークフロー申請経路を削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTWorkflowRoute routeobj =
        WorkflowUtils.getEipTWorkflowRoute(rundata, context);
      if (routeobj == null) {
        return false;
      }

      List<EipTWorkflowRequest> requests =
        WorkflowUtils.getEipTWorkflowRequest(routeobj);
      for (EipTWorkflowRequest request : requests) {
        request.setEipTWorkflowRoute(null);
      }

      List<EipTWorkflowCategory> categories =
        WorkflowUtils.getEipTworkflowCategory(routeobj);
      for (EipTWorkflowCategory category : categories) {
        category.setEipTWorkflowRoute(null);
      }

      // ワーフクロー申請経路を削除
      Database.delete(routeobj);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        routeobj.getRouteId(),
        ALEventlogConstants.PORTLET_TYPE_WORKFLOW_ROUTE,
        routeobj.getRouteName());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

  /**
   * 申請経路名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getRouteName() {
    return route_name;
  }

  /**
   * 申請経路を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getRoute() {
    return route;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  /**
   * 指定したユーザ名のオブジェクトを取得する．
   * 
   * @param userList
   * @param userName
   * @return
   */
  private TurbineUser getEipUserRecord(List<TurbineUser> userList,
      String userName) {
    int size = userList.size();
    for (int i = 0; i < size; i++) {
      TurbineUser record = userList.get(i);
      if (record.getLoginName().equals(userName)) {
        return record;
      }
    }
    return null;
  }

}
