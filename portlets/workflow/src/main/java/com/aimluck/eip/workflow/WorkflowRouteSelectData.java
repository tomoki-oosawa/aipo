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

import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTWorkflowRoute;
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
 * ワークフロー申請経路検索データを管理するクラスです。 <BR>
 * 
 */
public class WorkflowRouteSelectData extends
    ALAbstractSelectData<EipTWorkflowRoute, EipTWorkflowRoute> implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowRouteSelectData.class.getName());

  /** 申請経路の総数 */
  private int routeSum;

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
  protected ResultList<EipTWorkflowRoute> selectList(RunData rundata,
      Context context) {
    try {

      SelectQuery<EipTWorkflowRoute> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTWorkflowRoute> list = query.getResultList();
      // 件数をセットする．
      routeSum = list.getTotalCount();
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
  private SelectQuery<EipTWorkflowRoute> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTWorkflowRoute> query =
      Database.query(EipTWorkflowRoute.class);

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
  protected EipTWorkflowRoute selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return WorkflowUtils.getEipTWorkflowRoute(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTWorkflowRoute record) {
    WorkflowRouteResultData rd = new WorkflowRouteResultData();
    rd.initField();
    rd.setRouteId(record.getRouteId().longValue());
    rd.setRouteName(ALCommonUtils.compressString(
      record.getRouteName(),
      getStrLength()));
    rd
      .setRoute(ALCommonUtils.compressString(record.getRoute(), getStrLength()));
    return rd;
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTWorkflowRoute record) {
    WorkflowRouteDetailResultData rd = new WorkflowRouteDetailResultData();
    rd.initField();
    rd.setRouteId(record.getRouteId().longValue());
    rd.setRouteName(record.getRouteName());
    rd.setNote(record.getNote());
    rd.setRoute(record.getRoute());
    rd.setCreateDate(ALDateUtil.format(
      record.getCreateDate(),
      ALLocalizationUtils.getl10n("WORKFLOW_YEAR_MONTH_DAY")));
    rd.setUpdateDate(ALDateUtil.format(
      record.getUpdateDate(),
      ALLocalizationUtils.getl10n("WORKFLOW_YEAR_MONTH_DAY")));
    return rd;
  }

  public String getViewtype() {
    return "route";
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("route_name", EipTWorkflowRoute.ROUTE_NAME_PROPERTY);
    map.putValue("route", EipTWorkflowRoute.ROUTE_PROPERTY);
    return map;
  }

  public int getRouteSum() {
    return routeSum;
  }

  public String getStringCR(String str) {
    return ALCommonUtils.replaceToAutoCR(str);
  }

}
