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

import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowCategory;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフローカテゴリ検索データを管理するクラスです。 <BR>
 * 
 */
public class WorkflowCategorySelectData extends
    ALAbstractSelectData<EipTWorkflowCategory, EipTWorkflowCategory> implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowCategorySelectData.class.getName());

  /** カテゴリの総数 */
  private int categorySum;

  /** 申請経路一覧 */
  private List<WorkflowRouteResultData> routeList;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
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
    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTWorkflowCategory> selectList(RunData rundata,
      Context context) {
    try {

      SelectQuery<EipTWorkflowCategory> query =
        getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTWorkflowCategory> list = query.getResultList();
      // 件数をセットする．
      categorySum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("workflow", ex);
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
  private SelectQuery<EipTWorkflowCategory> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTWorkflowCategory> query =
      Database.query(EipTWorkflowCategory.class);

    return query;
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipTWorkflowCategory selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return WorkflowUtils.getEipTWorkflowCategory(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTWorkflowCategory record) {
    WorkflowCategoryResultData rd = new WorkflowCategoryResultData();
    rd.initField();
    rd.setCategoryId(record.getCategoryId().longValue());
    rd.setCategoryName(ALCommonUtils.compressString(
      record.getCategoryName(),
      getStrLength()));
    if (record.getEipTWorkflowRoute() != null) {
      rd.setRoute(record.getEipTWorkflowRoute().getRoute());
      rd.setRouteName(record.getEipTWorkflowRoute().getRouteName());
    }
    return rd;
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTWorkflowCategory record) {
    WorkflowCategoryDetailResultData rd =
      new WorkflowCategoryDetailResultData();
    rd.initField();
    rd.setCategoryId(record.getCategoryId().longValue());
    rd.setCategoryName(record.getCategoryName());
    rd.setNote(record.getNote());
    rd.setTemplate(record.getTemplate());
    rd.setCreateDate(ALDateUtil.format(
      record.getCreateDate(),
      ALLocalizationUtils.getl10n("WORKFLOW_YEAR_MONTH_DAY")));
    rd.setUpdateDate(ALDateUtil.format(
      record.getUpdateDate(),
      ALLocalizationUtils.getl10n("WORKFLOW_YEAR_MONTH_DAY")));
    if (record.getEipTWorkflowRoute() != null) {
      rd.setRoute(record.getEipTWorkflowRoute().getRoute());
      rd.setRouteName(record.getEipTWorkflowRoute().getRouteName());
    }
    return rd;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("category_name", EipTWorkflowCategory.CATEGORY_NAME_PROPERTY);
    map.putValue(
      "route_name",
      EipTWorkflowCategory.EIP_TWORKFLOW_ROUTE_PROPERTY);
    return map;
  }

  public int getCategorySum() {
    return categorySum;
  }

  public String getViewtype() {
    return "category";
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadRouteList(RunData rundata, Context context) {
    routeList = WorkflowUtils.loadRouteList(rundata, context);
  }

  public List<WorkflowRouteResultData> getRouteList() {
    return routeList;
  }
}
