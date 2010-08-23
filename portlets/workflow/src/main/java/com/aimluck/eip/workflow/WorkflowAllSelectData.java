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

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequest;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRequestMap;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフロー検索データを管理するクラスです。 <BR>
 * 
 */
public class WorkflowAllSelectData extends
    ALAbstractSelectData<EipTWorkflowRequest, EipTWorkflowRequest> implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowAllSelectData.class.getName());

  //
  /** サブメニュー（作成分）のタブ（未完了） */
  public static final String TAB_UNFINISHED = "unfinished";

  /** サブメニュー（作成分）のタブ（完了） */
  public static final String TAB_FINISHED = "finished";

  /** 現在選択されているタブ */
  private String currentTab;

  /** カテゴリ一覧 */
  private List<WorkflowCategoryResultData> categoryList;

  /** 申請経路一覧 */
  private List<WorkflowRouteResultData> routeList;

  /** 依頼総数 */
  private int requestSum;

  private ALEipUser login_user;

  /** ACL用の変数 */
  private String aclPortletFeature;

  /**  */
  private ALNumberField previous_id;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    String sorttype = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p2a-sort"));
    }

    if ("create_date".equals(ALEipUtils
      .getTemp(rundata, context, LIST_SORT_STR))
      && (sorttype == null || "".equals(sorttype))) {
      ALEipUtils.setTemp(
        rundata,
        context,
        LIST_SORT_TYPE_STR,
        ALEipConstants.LIST_SORT_TYPE_DESC);
    }

    String tabParam = rundata.getParameters().getString("alltab");
    currentTab = ALEipUtils.getTemp(rundata, context, "alltab");
    if (tabParam == null && currentTab == null) {
      ALEipUtils.setTemp(rundata, context, "alltab", TAB_UNFINISHED);
      currentTab = TAB_UNFINISHED;
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "alltab", tabParam);
      currentTab = tabParam;
    }

    login_user = ALEipUtils.getALEipUser(rundata);

    super.init(action, rundata, context);

    String entityId_Str =
      rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    if (entityId_Str == null || "".equals(entityId_Str)) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_WORKFLOW_REQUEST_OTHER;
    } else {
      Integer entityId = Integer.parseInt(entityId_Str);
      int aimUserId = getUserId(rundata, context, entityId);
      int uid = ALEipUtils.getUserId(rundata);
      if (aimUserId != uid) {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_WORKFLOW_REQUEST_OTHER;
      } else {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_WORKFLOW_REQUEST_SELF;
      }
    }

    try {
      previous_id = new ALNumberField();
      String previd = rundata.getParameters().getString("prvid");
      previous_id.setValue(previd);
    } catch (Exception e) {
      previous_id = null;
    }

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
  @Override
  public ResultList<EipTWorkflowRequest> selectList(RunData rundata,
      Context context) {
    try {

      SelectQuery<EipTWorkflowRequest> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTWorkflowRequest> list = query.getResultList();
      // リクエストの総数をセットする．
      requestSum = list.getTotalCount();
      return list;
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
    SelectQuery<EipTWorkflowRequest> query =
      Database.query(EipTWorkflowRequest.class);

    if (TAB_UNFINISHED.equals(currentTab)) {
      Expression exp1 =
        ExpressionFactory.noMatchExp(
          EipTWorkflowRequest.PROGRESS_PROPERTY,
          WorkflowUtils.DB_PROGRESS_ACCEPT);
      query.setQualifier(exp1);
    } else if (TAB_FINISHED.equals(currentTab)) {
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTWorkflowRequest.PROGRESS_PROPERTY,
          WorkflowUtils.DB_PROGRESS_ACCEPT);
      query.setQualifier(exp1);
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
  @Override
  protected Object getResultData(EipTWorkflowRequest record) {
    try {
      WorkflowResultData rd = new WorkflowResultData();
      rd.initField();
      rd.setRequestId(record.getRequestId().intValue());
      rd.setCategoryId(record
        .getEipTWorkflowCategory()
        .getCategoryId()
        .longValue());
      rd.setCategoryName(ALCommonUtils.compressString(record
        .getEipTWorkflowCategory()
        .getCategoryName(), getStrLength()));
      rd.setRequestName(ALCommonUtils.compressString(
        record.getRequestName(),
        getStrLength()));
      rd.setPriority(record.getPriority().intValue());
      rd.setPriorityImage(WorkflowUtils.getPriorityImage(record
        .getPriority()
        .intValue()));
      rd.setPriorityString(WorkflowUtils.getPriorityString(record
        .getPriority()
        .intValue()));
      rd.setProgress(record.getProgress());
      rd.setPrice(record.getPrice().longValue());

      String lastUpdateUser = null;
      EipTWorkflowRequestMap map = null;
      int order = 0;
      List<EipTWorkflowRequestMap> maps =
        WorkflowUtils.getEipTWorkflowRequestMap(record);
      int size = maps.size();

      if (WorkflowUtils.DB_PROGRESS_ACCEPT.equals(record.getProgress())) {
        // すべて承認済みの場合、最終承認者をセットする
        map = maps.get(size - 1);
        ALEipUser user = ALEipUtils.getALEipUser(map.getUserId().intValue());
        lastUpdateUser = user.getAliasName().getValue();
        order = map.getOrderIndex().intValue();
      } else {
        for (int i = 0; i < size; i++) {
          map = maps.get(i);
          if (WorkflowUtils.DB_STATUS_CONFIRM.equals(map.getStatus())) {
            // 最終閲覧者を取得する
            ALEipUser user =
              ALEipUtils.getALEipUser(map.getUserId().intValue());
            lastUpdateUser = user.getAliasName().getValue();
            order = map.getOrderIndex().intValue() - 1;
            break;
          }
        }
      }

      rd.setStateString(order + "/" + (size - 1));

      rd.setLastUpdateUser(lastUpdateUser);
      rd.setCreateDate(WorkflowUtils.translateDate(
        record.getCreateDate(),
        "yyyy年M月d日H時m分"));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  private int getUserId(RunData rundata, Context context, Integer entityId) {
    Expression exp =
      ExpressionFactory.matchDbExp(
        EipTWorkflowRequest.REQUEST_ID_PK_COLUMN,
        entityId);
    SelectQuery<EipTWorkflowRequest> query =
      Database.query(EipTWorkflowRequest.class, exp);
    List<EipTWorkflowRequest> record = query.fetchList();
    if (record.size() > 0) {
      return (record.get(0)).getUserId().intValue();
    } else {
      return -1;
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
  @Override
  public EipTWorkflowRequest selectDetail(RunData rundata, Context context) {
    return WorkflowUtils.getEipTWorkflowRequestAll(rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(EipTWorkflowRequest obj) {
    return WorkflowUtils.getResultDataDetail(obj, login_user);
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
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("request_name", EipTWorkflowRequest.REQUEST_NAME_PROPERTY);
    map.putValue("priority", EipTWorkflowRequest.PRIORITY_PROPERTY);
    map.putValue("price", EipTWorkflowRequest.PRICE_PROPERTY);
    map.putValue("create_date", EipTWorkflowRequest.CREATE_DATE_PROPERTY);
    // map.putValue("category_name", EipTTodo.EIP_TTODO_CATEGORY_PROPERTY + "."
    // + EipTTodoCategory.CATEGORY_NAME_PROPERTY);
    map.putValue("category", EipTWorkflowCategory.CATEGORY_ID_PK_COLUMN);
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
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

  /**
   * アクセス権限用メソッド。<br />
   * アクセス権限の有無を返します。
   * 
   * @return
   */
  public boolean hasAuthorityOther() {
    return hasAuthority;
  }
}
