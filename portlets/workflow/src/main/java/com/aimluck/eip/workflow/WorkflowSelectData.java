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
package com.aimluck.eip.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowFile;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequestMap;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.util.WhatsNewUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフロー検索データを管理するクラスです。 <BR>
 *
 */
public class WorkflowSelectData extends
    ALAbstractSelectData<EipTWorkflowRequest> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WorkflowSelectData.class.getName());

  /** サブメニュー（確認依頼） */
  public static final String SUBMENU_REQUESTED = "requested";

  /** サブメニュー（作成分） */
  public static final String SUBMENU_CREATED = "created";

  /** サブメニュー（確認依頼）のタブ（未確認） */
  public static final String TAB_UNCONFIRMED = "unconfirmed";

  /** サブメニュー（確認依頼）のタブ（確認済） */
  public static final String TAB_CONFIRMED = "confirmed";

  /** サブメニュー（確認依頼）のタブ（完了） */
  public static final String TAB_COMPLETED = "completed";

  /** サブメニュー（作成分）のタブ（未完了） */
  public static final String TAB_UNFINISHED = "unfinished";

  /** サブメニュー（作成分）のタブ（完了） */
  public static final String TAB_FINISHED = "finished";

  /** 現在選択されているサブメニュー */
  private String currentSubMenu;

  /** 現在選択されているタブ */
  private String currentTab;

  /** カテゴリ一覧 */
  private List<WorkflowCategoryResultData> categoryList;

  /** 申請経路一覧 */
  private List<WorkflowRouteResultData> routeList;

  /** リクエストの総数 */
  private int requestSum;

  private ALEipUser login_user;

  private ALNumberField previous_id;

  /** 他ユーザーのワークフローの閲覧権限 */
  private boolean hasAuthorityOther;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    String sorttype = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR,
          ALEipUtils.getPortlet(rundata, context).getPortletConfig()
              .getInitParameter("p2a-sort"));
    }

    if ("create_date".equals(ALEipUtils
        .getTemp(rundata, context, LIST_SORT_STR))
        && (sorttype == null || "".equals(sorttype))) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_TYPE_STR,
          ALEipConstants.LIST_SORT_TYPE_DESC);
    }

    String subMenuParam = rundata.getParameters().getString("submenu");
    currentSubMenu = ALEipUtils.getTemp(rundata, context, "submenu");
    if (subMenuParam == null && currentSubMenu == null) {
      ALEipUtils.setTemp(rundata, context, "submenu", SUBMENU_REQUESTED);
      currentSubMenu = SUBMENU_REQUESTED;
    } else if (subMenuParam != null) {
      ALEipUtils.setTemp(rundata, context, "submenu", subMenuParam);
      currentSubMenu = subMenuParam;
    }

    String tabParam = rundata.getParameters().getString("tab");
    currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tabParam == null && currentTab == null) {
      if (SUBMENU_REQUESTED.equals(currentSubMenu)) {
        tabParam = TAB_UNCONFIRMED;
      } else {
        tabParam = TAB_UNFINISHED;
      }
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    }
    if (SUBMENU_REQUESTED.equals(currentSubMenu)) {
      if (TAB_UNFINISHED.equals(currentTab) || TAB_FINISHED.equals(currentTab)) {
        currentTab = TAB_UNCONFIRMED;
      }
    } else {
      if (TAB_UNCONFIRMED.equals(currentTab)
          || TAB_CONFIRMED.equals(currentTab)
          || TAB_COMPLETED.equals(currentTab)) {
        currentTab = TAB_UNFINISHED;
      }
    }

    try {
      previous_id = new ALNumberField();
      String previd = rundata.getParameters().getString("prvid");
      previous_id.setValue(previd);
    } catch (Exception e) {
      previous_id = null;
    }

    login_user = ALEipUtils.getALEipUser(rundata);

    // アクセス権限
    ALAccessControlFactoryService aclservice = (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAuthorityOther = aclhandler.hasAuthority(ALEipUtils.getUserId(rundata),
        ALAccessControlConstants.POERTLET_FEATURE_WORKFLOW_REQUEST_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);

    super.init(action, rundata, context);
  }

  /**
   *
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata, Context context) {
    categoryList = WorkflowUtils.loadCategoryList(rundata, context);
  }

  /**
   *
   * @param rundata
   * @param context
   */
  public void loadRouteList(RunData rundata, Context context) {
    routeList = WorkflowUtils.loadRouteList(rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractListData#selectData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  public List<EipTWorkflowRequest> selectList(RunData rundata, Context context) {
    try {

      SelectQuery<EipTWorkflowRequest> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List<EipTWorkflowRequest> list = query.perform();
      // リクエストの総数をセットする．
      requestSum = list.size();
      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTWorkflowRequest> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTWorkflowRequest> query = new SelectQuery<EipTWorkflowRequest>(
        EipTWorkflowRequest.class);

    Integer login_user_id = Integer.valueOf((int) login_user.getUserId()
        .getValue());

    if (SUBMENU_REQUESTED.equals(currentSubMenu)) {
      // 確認依頼

      Expression exp1 = ExpressionFactory.matchExp(
          EipTWorkflowRequest.EIP_TWORKFLOW_REQUEST_MAP_PROPERTY + "."
              + EipTWorkflowRequestMap.USER_ID_PROPERTY, login_user_id);
      query.setQualifier(exp1);

      if (TAB_UNCONFIRMED.equals(currentTab)) {
        Expression exp21 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.PROGRESS_PROPERTY,
            WorkflowUtils.DB_PROGRESS_WAIT);
        Expression exp22 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.EIP_TWORKFLOW_REQUEST_MAP_PROPERTY + "."
                + EipTWorkflowRequestMap.STATUS_PROPERTY,
            WorkflowUtils.DB_STATUS_CONFIRM);
        Expression exp31 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.EIP_TWORKFLOW_REQUEST_MAP_PROPERTY + "."
                + EipTWorkflowRequestMap.STATUS_PROPERTY,
            WorkflowUtils.DB_STATUS_REQUEST);
        Expression exp32 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.PROGRESS_PROPERTY,
            WorkflowUtils.DB_PROGRESS_DENAIL);

        query.andQualifier((exp21.andExp(exp22)).orExp(exp31.andExp(exp32)));
      } else if (TAB_CONFIRMED.equals(currentTab)) {
        Expression exp2 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.PROGRESS_PROPERTY,
            WorkflowUtils.DB_PROGRESS_WAIT);
        query.andQualifier(exp2);

        Expression exp3 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.EIP_TWORKFLOW_REQUEST_MAP_PROPERTY + "."
                + EipTWorkflowRequestMap.STATUS_PROPERTY,
            WorkflowUtils.DB_STATUS_ACCEPT);
        query.andQualifier(exp3);
      } else {
        Expression exp2 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.PROGRESS_PROPERTY,
            WorkflowUtils.DB_PROGRESS_ACCEPT);
        query.andQualifier(exp2);

        Expression exp3 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.EIP_TWORKFLOW_REQUEST_MAP_PROPERTY + "."
                + EipTWorkflowRequestMap.STATUS_PROPERTY,
            WorkflowUtils.DB_STATUS_ACCEPT);
        query.andQualifier(exp3);
      }
    } else {
      // 作成分
      Expression exp1 = ExpressionFactory.matchExp(
          EipTWorkflowRequest.USER_ID_PROPERTY, login_user_id);
      query.setQualifier(exp1);

      if (TAB_UNFINISHED.equals(currentTab)) {
        List<String> progressList = new ArrayList<String>();
        progressList.add(WorkflowUtils.DB_PROGRESS_WAIT);
        progressList.add(WorkflowUtils.DB_PROGRESS_DENAIL);
        Expression exp2 = ExpressionFactory.inExp(
            EipTWorkflowRequest.PROGRESS_PROPERTY, progressList);
        query.andQualifier(exp2);

        // C（確認）/ W（待ち状態）/ D（否認）があれば、未完了の状態
        List<String> stausList = new ArrayList<String>();
        stausList.add(WorkflowUtils.DB_STATUS_CONFIRM);
        stausList.add(WorkflowUtils.DB_STATUS_WAIT);
        stausList.add(WorkflowUtils.DB_STATUS_DENIAL);
        Expression exp3 = ExpressionFactory.inExp(
            EipTWorkflowRequest.EIP_TWORKFLOW_REQUEST_MAP_PROPERTY + "."
                + EipTWorkflowRequestMap.STATUS_PROPERTY, stausList);
        query.andQualifier(exp3);
      } else {
        Expression exp2 = ExpressionFactory.matchExp(
            EipTWorkflowRequest.PROGRESS_PROPERTY,
            WorkflowUtils.DB_PROGRESS_ACCEPT);
        query.andQualifier(exp2);
      }
    }

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getListData(java.lang.Object)
   */
  protected Object getResultData(EipTWorkflowRequest record) {
    try {
      WorkflowResultData rd = new WorkflowResultData();
      rd.initField();
      rd.setRequestId(record.getRequestId().intValue());
      rd.setCategoryId(record.getEipTWorkflowCategory().getCategoryId()
          .longValue());
      rd.setCategoryName(ALCommonUtils.compressString(record
          .getEipTWorkflowCategory().getCategoryName(), getStrLength()));
      rd.setRequestName(ALCommonUtils.compressString(record.getRequestName(),
          getStrLength()));
      rd.setPriority(record.getPriority().intValue());
      rd.setPriorityImage(WorkflowUtils.getPriorityImage(record.getPriority()
          .intValue()));
      rd.setPriorityString(WorkflowUtils.getPriorityString(record.getPriority()
          .intValue()));
      rd.setProgress(record.getProgress());
      rd.setPrice(record.getPrice().longValue());

      String lastUpdateUser = null;
      EipTWorkflowRequestMap map = null;
      int order = 0;
      List<?> maps = WorkflowUtils.getEipTWorkflowRequestMap(record);
      int size = maps.size();

      if (WorkflowUtils.DB_PROGRESS_ACCEPT.equals(record.getProgress())) {
        // すべて承認済みの場合、最終承認者をセットする
        map = (EipTWorkflowRequestMap) maps.get(size - 1);
        ALEipUser user = ALEipUtils.getALEipUser(map.getUserId().intValue());
        lastUpdateUser = user.getAliasName().getValue();
        order = map.getOrderIndex().intValue();
      } else {
        for (int i = 0; i < size; i++) {
          map = (EipTWorkflowRequestMap) maps.get(i);
          if (WorkflowUtils.DB_STATUS_CONFIRM.equals(map.getStatus())) {
            // 最終閲覧者を取得する
            ALEipUser user = ALEipUtils
                .getALEipUser(map.getUserId().intValue());
            lastUpdateUser = user.getAliasName().getValue();
            order = map.getOrderIndex().intValue() - 1;
            break;
          }
        }
      }

      rd.setStateString(order + "/" + (size - 1));

      rd.setLastUpdateUser(lastUpdateUser);
      rd.setCreateDate(WorkflowUtils.translateDate(record.getCreateDate(),
          "yyyy年M月d日H時m分"));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  public EipTWorkflowRequest selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {
    try {

      EipTWorkflowRequest request = WorkflowUtils.getEipTWorkflowRequest(
          rundata, context, false);

      /**
       * 新着ポートレット既読処理
       */
      String entryid = ALEipUtils.getTemp(rundata, context,
          ALEipConstants.ENTITY_ID);
      WhatsNewUtils.shiftWhatsNewReadFlag(
          WhatsNewUtils.WHATS_NEW_TYPE_WORKFLOW_REQUEST,
          Integer.parseInt(entryid), (int) login_user.getUserId().getValue());
      /**
       *
       */

      return request;
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(EipTWorkflowRequest obj) {
    return WorkflowUtils.getResultDataDetail(obj, login_user);
  }

  /**
   * ファイル検索のクエリを返します
   *
   * @return
   */
  @SuppressWarnings("unused")
  private SelectQuery<EipTWorkflowFile> getSelectQueryForFiles(int requestid) {
    SelectQuery<EipTWorkflowFile> query = new SelectQuery<EipTWorkflowFile>(
        EipTWorkflowFile.class);
    Expression exp = ExpressionFactory.matchDbExp(
        EipTWorkflowRequest.REQUEST_ID_PK_COLUMN, Integer.valueOf(requestid));
    query.setQualifier(exp);
    return query;
  }

  /**
   *
   * @return
   */
  public List<WorkflowCategoryResultData> getCategoryList() {
    return categoryList;
  }

  public List<WorkflowRouteResultData> getRouteList() {
    return routeList;
  }

  /**
   * 現在選択されているサブメニューを取得します。 <BR>
   *
   * @return
   */
  public String getCurrentSubMenu() {
    return this.currentSubMenu;
  }

  /**
   * 現在選択されているタブを取得します。 <BR>
   *
   * @return
   */
  public String getCurrentTab() {
    return currentTab;
  }

  /**
   * リクエストの総数を返す． <BR>
   *
   * @return
   */
  public int getRequestSum() {
    return requestSum;
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("request_name", EipTWorkflowRequest.REQUEST_NAME_PROPERTY);
    map.putValue("priority", EipTWorkflowRequest.PRIORITY_PROPERTY);
    map.putValue("price", EipTWorkflowRequest.PRICE_PROPERTY);
    map.putValue("create_date", EipTWorkflowRequest.CREATE_DATE_PROPERTY);
    map.putValue("category",
        EipTWorkflowRequest.EIP_TWORKFLOW_CATEGORY_PROPERTY + "."
            + EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN);
    return map;
  }

  public ALEipUser getLoginUser() {
    return login_user;
  }

  /**
   *
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  public ALNumberField getPreviousID() {
    return previous_id;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_WORKFLOW_REQUEST_SELF;
  }

  public boolean hasAuthorityOther() {
    return hasAuthorityOther;
  }
}
