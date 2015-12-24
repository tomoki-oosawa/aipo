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
package com.aimluck.eip.workflow;

import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRoute;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
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
 * ワークフローカテゴリのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class WorkflowCategoryFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowCategoryFormData.class.getName());

  /** カテゴリ名 */
  private ALStringField category_name;

  /** メモ */
  private ALStringField note;

  private ALStringField ordertemplate;

  private Integer category_id;

  /** 申請経路ID */
  private ALNumberField route_id;

  private ALStringField route;

  /** 申請経路一覧 */
  private List<WorkflowRouteResultData> routeList;

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

    String categoryid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    if (categoryid != null && Integer.valueOf(categoryid) != null) {
      category_id = Integer.valueOf(categoryid);
    }
  }

  /**
   *
   *
   */
  @Override
  public void initField() {
    // カテゴリ名
    category_name = new ALStringField();
    category_name.setFieldName(ALLocalizationUtils
      .getl10n("WORKFLOW_CLASSIFICATION"));
    category_name.setTrim(true);
    // メモ
    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils.getl10n("WORKFLOW_MEMO"));
    note.setTrim(true);
    // テンプレート
    ordertemplate = new ALStringField();
    ordertemplate.setFieldName(ALLocalizationUtils
      .getl10n("WORKFLOW_APPLICATION_CONTENTS_TEMPLATE"));
    ordertemplate.setTrim(true);
    // 申請経路ID
    route_id = new ALNumberField();
    route = new ALStringField();
    route_id.setFieldName(ALLocalizationUtils
      .getl10n("WORKFLOW_APPLICATION_ROUTE"));
  }

  /**
   * ワークフローカテゴリの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // カテゴリ名必須項目
    category_name.setNotNull(true);
    // カテゴリ名文字数制限
    category_name.limitMaxLength(50);
    // テンプレート文字数制限
    ordertemplate.limitMaxLength(1000);
    // メモ文字数制限
    note.limitMaxLength(10000);
  }

  /**
   * ワークフローカテゴリのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      SelectQuery<EipTWorkflowCategory> query =
        Database.query(EipTWorkflowCategory.class);
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTWorkflowCategory.CATEGORY_NAME_PROPERTY,
          category_name.getValue());
      query.setQualifier(exp1);
      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(
            EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN,
            category_id);
        query.andQualifier(exp2);
      }
      if (query.fetchList().size() != 0) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "WORKFLOW_ALERT_CATEGORY_ALREADY_CREATED",
          category_name.toString()));
      }
    } catch (Exception ex) {
      logger.error("workflow", ex);
      return false;
    }
    // カテゴリ名
    category_name.validate(msgList);
    // メモ
    note.validate(msgList);
    // テンプレート
    ordertemplate.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * ワークフローカテゴリをデータベースから読み出します。 <BR>
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
      EipTWorkflowCategory category =
        WorkflowUtils.getEipTWorkflowCategory(rundata, context);
      if (category == null) {
        return false;
      }
      // カテゴリ名
      category_name.setValue(category.getCategoryName());
      // テンプレート
      ordertemplate.setValue(category.getTemplate());
      // メモ
      note.setValue(category.getNote());
      // 申請経路ID
      if (category.getEipTWorkflowRoute() == null) {
        route_id.setValue(0);
      } else {
        route_id.setValue(category
          .getEipTWorkflowRoute()
          .getRouteId()
          .longValue());
      }
      // 申請経路
      if (category.getEipTWorkflowRoute() != null) {
        route.setValue(category.getEipTWorkflowRoute().getRoute());
      }
    } catch (Exception ex) {
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

  /**
   * ワークフローカテゴリをデータベースに格納します。 <BR>
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
      EipTWorkflowRoute route =
        WorkflowUtils.getEipTWorkflowRoute(Long.valueOf(route_id.getValue()));
      EipTWorkflowCategory category =
        Database.create(EipTWorkflowCategory.class);
      category.setCategoryName(category_name.getValue());
      category.setNote(note.getValue());
      category.setUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      category.setCreateDate(Calendar.getInstance().getTime());
      category.setUpdateDate(Calendar.getInstance().getTime());
      category.setTemplate(ordertemplate.getValue());
      category.setEipTWorkflowRoute(route);
      Database.commit();
      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        category.getCategoryId(),
        ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY,
        category.getCategoryName());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているワークフローカテゴリを更新します。 <BR>
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
      EipTWorkflowRoute route =
        WorkflowUtils.getEipTWorkflowRoute(Long.valueOf(route_id.getValue()));
      EipTWorkflowCategory category =
        WorkflowUtils.getEipTWorkflowCategory(rundata, context);
      if (category == null) {
        return false;
      }
      // カテゴリ名
      category.setCategoryName(category_name.getValue());
      // テンプレート
      category.setTemplate(ordertemplate.getValue());
      // メモ
      category.setNote(note.getValue());
      // ユーザーID
      category.setUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      // 更新日
      category.setUpdateDate(Calendar.getInstance().getTime());
      // 申請経路ID
      category.setEipTWorkflowRoute(route);

      // カテゴリを更新
      Database.commit();
      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        category.getCategoryId(),
        ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY,
        category.getCategoryName());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

  /**
   * ワークフローカテゴリを削除します。 <BR>
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
      EipTWorkflowCategory category =
        WorkflowUtils.getEipTWorkflowCategory(rundata, context);
      if (category == null) {
        return false;
      }

      if (category.getCategoryId().intValue() == 1) {
        // カテゴリ「その他」は削除不可
        msgList.add(ALLocalizationUtils
          .getl10nFormat("WORKFLOW_ALERT_CATEGORY_DELETE_OTHER"));
        return false;
      }

      // ワーフクローカテゴリを削除
      Database.delete(category);

      // このカテゴリに含まれる依頼をカテゴリ「未分類」に移す。
      SelectQuery<EipTWorkflowRequest> query =
        Database.query(EipTWorkflowRequest.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTWorkflowRequest.EIP_TWORKFLOW_CATEGORY_PROPERTY
            + "."
            + EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN,
          category.getCategoryId());
      query.setQualifier(exp1);
      List<EipTWorkflowRequest> requests = query.fetchList();
      if (requests != null && requests.size() > 0) {
        EipTWorkflowRequest request = null;
        EipTWorkflowCategory defaultCategory =
          WorkflowUtils.getEipTWorkflowCategory(Long.valueOf(1));
        int size = requests.size();
        for (int i = 0; i < size; i++) {
          request = requests.get(i);
          request.setEipTWorkflowCategory(defaultCategory);
        }
      }

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        category.getCategoryId(),
        ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY,
        category.getCategoryName());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("workflow", ex);
      return false;
    }
    return true;
  }

  /**
   * テンプレートを渡す
   * 
   * @param num
   * @return
   */
  public String routeTemplate(int num) {
    for (WorkflowRouteResultData o : routeList) {
      WorkflowRouteResultData tmp = o;
      if (tmp.getRouteId().getValue() == num) {
        return tmp.getRouteH();
      }
    }

    return "";
  }

  /**
   * カテゴリ名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCategoryName() {
    return category_name;
  }

  /**
   * テンプレートを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getTemplate() {
    return ordertemplate;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * 申請経路IDを取得します。 <BR>
   * 
   * @return
   */
  public ALNumberField getRouteId() {
    return route_id;
  }

  public ALStringField getRoute() {
    return route;
  }

  public String getRouteH() {
    StringBuffer routeun = new StringBuffer();
    String username;

    if (route.getValue() != null && !"".equals(route.getValue())) {
      StringTokenizer st = new StringTokenizer(route.getValue(), ",");
      while (st.hasMoreTokens()) {
        username = WorkflowUtils.getName(st.nextToken());
        routeun.append(username);
        routeun.append(" -> ");
      }
      routeun.append(ALLocalizationUtils.getl10n("WORKFLOW_COMPLETION"));

      return routeun.toString();
    }
    return "";
  }

  /**
   * カテゴリ一覧を取得します。 <BR>
   * 
   * @return
   */
  public List<WorkflowRouteResultData> getRouteList() {
    return routeList;
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadRouteList(RunData rundata, Context context) {
    routeList = WorkflowUtils.loadRouteList(rundata, context);
  }

}
