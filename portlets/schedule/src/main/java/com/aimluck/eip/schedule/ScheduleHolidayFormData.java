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

package com.aimluck.eip.schedule;

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.schedule.util.ScheduleHolidayUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * ワークフローカテゴリのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class ScheduleHolidayFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleHolidayFormData.class.getName());

  /** 休日名 */
  private ALStringField holiday_title;

  /** メモ */
  private ALDateTimeField holiday_date;

  private Integer holiday_id;

  private ALNumberField create_user_id;

  private ALNumberField update_user_id;

  /** 休日一覧 */
  private List<ScheduleHolidayResultData> holidayList;

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

    String holidayid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    if (holidayid != null && Integer.valueOf(holidayid) != null) {
      holiday_id = Integer.valueOf(holidayid);
    }
  }

  /**
   *
   *
   */
  @Override
  public void initField() {
    // 休日名
    holiday_title = new ALStringField();
    holiday_title.setFieldName(ALLocalizationUtils
      .getl10n("SCHEDULE_COMPANY_HOLIDAY"));
    holiday_title.setTrim(true);
  }

  /**
   * ワークフローカテゴリの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
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
  // @Override
  // protected boolean loadFormData(RunData rundata, Context context,
  // List<String> msgList) {
  // try {
  // // オブジェクトモデルを取得
  // EipMUserHoliday category =
  // WorkflowUtils.getEipTWorkflowCategory(rundata, context);
  // if (category == null) {
  // return false;
  // }
  // // カテゴリ名
  // holiday_title.setValue(category.getCategoryName());
  // // テンプレート
  // ordertemplate.setValue(category.getTemplate());
  // // メモ
  // note.setValue(category.getNote());
  // // 申請経路ID
  // if (category.getEipTWorkflowRoute() == null) {
  // route_id.setValue(0);
  // } else {
  // route_id.setValue(category
  // .getEipTWorkflowRoute()
  // .getRouteId()
  // .longValue());
  // }
  // // 申請経路
  // if (category.getEipTWorkflowRoute() != null) {
  // route.setValue(category.getEipTWorkflowRoute().getRoute());
  // }
  // } catch (Exception ex) {
  // logger.error("workflow", ex);
  // return false;
  // }
  // return true;
  // }
  //
  // /**
  // * ワークフローカテゴリをデータベースに格納します。 <BR>
  // *
  // * @param rundata
  // * @param context
  // * @param msgList
  // * @return
  // */
  // @Override
  // protected boolean insertFormData(RunData rundata, Context context,
  // List<String> msgList) {
  // try {
  // EipTWorkflowRoute route =
  // WorkflowUtils.getEipTWorkflowRoute(Long.valueOf(route_id.getValue()));
  // EipTWorkflowCategory category =
  // Database.create(EipTWorkflowCategory.class);
  // category.setCategoryName(category_name.getValue());
  // category.setNote(note.getValue());
  // category.setUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
  // category.setCreateDate(Calendar.getInstance().getTime());
  // category.setUpdateDate(Calendar.getInstance().getTime());
  // category.setTemplate(ordertemplate.getValue());
  // category.setEipTWorkflowRoute(route);
  // Database.commit();
  // // イベントログに保存
  // ALEventlogFactoryService.getInstance().getEventlogHandler().log(
  // category.getCategoryId(),
  // ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY,
  // category.getCategoryName());
  // } catch (Exception ex) {
  // Database.rollback();
  // logger.error("workflow", ex);
  // return false;
  // }
  // return true;
  // }
  //
  // /**
  // * データベースに格納されているワークフローカテゴリを更新します。 <BR>
  // *
  // * @param rundata
  // * @param context
  // * @param msgList
  // * @return
  // */
  // @Override
  // protected boolean updateFormData(RunData rundata, Context context,
  // List<String> msgList) {
  // try {
  // // オブジェクトモデルを取得
  // EipTWorkflowRoute route =
  // WorkflowUtils.getEipTWorkflowRoute(Long.valueOf(route_id.getValue()));
  // EipTWorkflowCategory category =
  // WorkflowUtils.getEipTWorkflowCategory(rundata, context);
  // if (category == null) {
  // return false;
  // }
  // // カテゴリ名
  // category.setCategoryName(category_name.getValue());
  // // テンプレート
  // category.setTemplate(ordertemplate.getValue());
  // // メモ
  // category.setNote(note.getValue());
  // // ユーザーID
  // category.setUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
  // // 更新日
  // category.setUpdateDate(Calendar.getInstance().getTime());
  // // 申請経路ID
  // category.setEipTWorkflowRoute(route);
  //
  // // カテゴリを更新
  // Database.commit();
  // // イベントログに保存
  // ALEventlogFactoryService.getInstance().getEventlogHandler().log(
  // category.getCategoryId(),
  // ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY,
  // category.getCategoryName());
  // } catch (Exception ex) {
  // Database.rollback();
  // logger.error("workflow", ex);
  // return false;
  // }
  // return true;
  // }
  //
  // /**
  // * ワークフローカテゴリを削除します。 <BR>
  // *
  // * @param rundata
  // * @param context
  // * @param msgList
  // * @return
  // */
  // @Override
  // protected boolean deleteFormData(RunData rundata, Context context,
  // List<String> msgList) {
  // try {
  // // オブジェクトモデルを取得
  // EipTWorkflowCategory category =
  // WorkflowUtils.getEipTWorkflowCategory(rundata, context);
  // if (category == null) {
  // return false;
  // }
  //
  // if (category.getCategoryId().intValue() == 1) {
  // // カテゴリ「その他」は削除不可
  // msgList.add(ALLocalizationUtils
  // .getl10nFormat("WORKFLOW_ALERT_CATEGORY_DELETE_OTHER"));
  // return false;
  // }
  //
  // // ワーフクローカテゴリを削除
  // Database.delete(category);
  //
  // // このカテゴリに含まれる依頼をカテゴリ「未分類」に移す。
  // SelectQuery<EipTWorkflowRequest> query =
  // Database.query(EipTWorkflowRequest.class);
  // Expression exp1 =
  // ExpressionFactory.matchDbExp(
  // EipTWorkflowRequest.EIP_TWORKFLOW_CATEGORY_PROPERTY
  // + "."
  // + EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN,
  // category.getCategoryId());
  // query.setQualifier(exp1);
  // List<EipTWorkflowRequest> requests = query.fetchList();
  // if (requests != null && requests.size() > 0) {
  // EipTWorkflowRequest request = null;
  // EipTWorkflowCategory defaultCategory =
  // WorkflowUtils.getEipTWorkflowCategory(Long.valueOf(1));
  // int size = requests.size();
  // for (int i = 0; i < size; i++) {
  // request = requests.get(i);
  // request.setEipTWorkflowCategory(defaultCategory);
  // }
  // }
  //
  // Database.commit();
  //
  // // イベントログに保存
  // ALEventlogFactoryService.getInstance().getEventlogHandler().log(
  // category.getCategoryId(),
  // ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY,
  // category.getCategoryName());
  // } catch (Exception ex) {
  // Database.rollback();
  // logger.error("workflow", ex);
  // return false;
  // }
  // return true;
  // }
  //
  // /**
  // * テンプレートを渡す
  // *
  // * @param num
  // * @return
  // */
  // public String routeTemplate(int num) {
  // for (WorkflowRouteResultData o : routeList) {
  // WorkflowRouteResultData tmp = o;
  // if (tmp.getRouteId().getValue() == num) {
  // return tmp.getRouteH();
  // }
  // }
  //
  // return "";
  // }
  //
  // /**
  // * 休日名を取得します。 <BR>
  // *
  // * @return
  // */
  // public ALStringField getHolidayTitle() {
  // return holiday_title;
  // }
  //
  // /**
  // * 申請経路IDを取得します。 <BR>
  // *
  // * @return
  // */
  // public ALNumberField getRouteId() {
  // return route_id;
  // }
  //
  // public String getRouteH() {
  // StringBuffer routeun = new StringBuffer();
  // String username;
  //
  // if (route.getValue() != null && !"".equals(route.getValue())) {
  // StringTokenizer st = new StringTokenizer(route.getValue(), ",");
  // while (st.hasMoreTokens()) {
  // username = WorkflowUtils.getName(st.nextToken());
  // routeun.append(username);
  // routeun.append(" -> ");
  // }
  // routeun.append(ALLocalizationUtils.getl10n("WORKFLOW_COMPLETION"));
  //
  // return routeun.toString();
  // }
  // return "";
  // }

  /**
   * 休日一覧を取得します。 <BR>
   * 
   * @return
   */
  public List<ScheduleHolidayResultData> getHolidayList() {
    return holidayList;
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadHolidayList(RunData rundata, Context context) {
    holidayList = ScheduleHolidayUtils.loadHolidayList(rundata, context);
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

}
