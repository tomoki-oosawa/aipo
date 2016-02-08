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
package com.aimluck.eip.modules.actions.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.workflow.WorkflowAllSelectData;
import com.aimluck.eip.workflow.WorkflowSelectData;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフローのアクションクラスです。 <BR>
 *
 */
public class WorkflowAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    // セッション情報をクリア
    clearWorkflowSession(rundata, context);
    ALEipUtils.setTemp(rundata, context, "Workflow_Maximize", "false");

    WorkflowSelectData listData = new WorkflowSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setStrLength(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "workflow");
  }

  /**
   * 最大化表示の際の処理を記述します。 <BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    ALEipUtils.setTemp(rundata, context, "Workflow_Maximize", "true");
    try {
      if (ALEipConstants.MODE_LIST.equals(mode)) {
        doWorkflow_list(rundata, context);
      }

      String currentTab = ALEipUtils.getTemp(rundata, context, "tab");

      if (currentTab != null && currentTab.endsWith("alldisplay")) {
        doWorkflow_list_all(rundata, context);
      } else {
        doWorkflow_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("workflow", ex);
    }

  }

  /**
   * 依頼を一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWorkflow_list(RunData rundata, Context context)
      throws Exception {
    WorkflowSelectData listData = new WorkflowSelectData();
    listData.initField();
    listData.loadCategoryList(rundata, context);
    listData.loadRouteList(rundata, context);
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    context.put("all_flg", false);
    setTemplate(rundata, "workflow-list");
  }

  /**
   * 全依頼を一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWorkflow_list_all(RunData rundata, Context context)
      throws Exception {
    WorkflowAllSelectData listData = new WorkflowAllSelectData();
    listData.initField();
    listData.loadCategoryList(rundata, context);
    listData.loadRouteList(rundata, context);
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    context.put("all_flg", true);
    setTemplate(rundata, "workflow-list-admin");
  }

  private void clearWorkflowSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("submenu");
    list.add("tab");
    list.add("alltab");
    list.add("WorkflowSelectDatasort");
    list.add("WorkflowAllSelectDatasort");
    list.add("WorkflowSelectDatafiltertype");
    list.add("WorkflowSelectDatafilter");
    list.add(WorkflowUtils.TARGET_KEYWORD);
    ALEipUtils.removeTemp(rundata, context, list);
  }
}
