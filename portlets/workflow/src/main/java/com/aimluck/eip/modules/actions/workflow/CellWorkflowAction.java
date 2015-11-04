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
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.workflow.WorkflowAllSelectData;
import com.aimluck.eip.workflow.WorkflowConfirmFormData;
import com.aimluck.eip.workflow.WorkflowFormData;
import com.aimluck.eip.workflow.WorkflowSelectData;

/**
 * ワークフローの携帯用アクションクラスです。 <BR>
 * 
 */
public class CellWorkflowAction extends WorkflowAction {

  public static final String MODE_MENU = "menu";

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellWorkflowAction.class.getName());

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
    try {
      if (MODE_MENU.equals(mode)) {
        doWorkflow_menu(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doWorkflow_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doWorkflow_list(rundata, context);
      }

      if (getMode() == null) {
        doWorkflow_menu(rundata, context);
        // doWorkflow_list(rundata, context);
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
  @Override
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
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "workflow-list");
  }

  public void doWorkflow_menu(RunData rundata, Context context) {
    putData(rundata, context);
    setTemplate(rundata, "workflow-menu");
  }

  /**
   * 全依頼を一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
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
    listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p3a-strlen")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "workflow-list-admin");
  }

  /**
   * 依頼を詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWorkflow_detail(RunData rundata, Context context)
      throws Exception {
    WorkflowSelectData detailData = new WorkflowSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "workflow-detail");
    } else {
      doWorkflow_list(rundata, context);
    }
  }

  /**
   * 差し戻された依頼を削除する
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWorkflow_delete(RunData rundata, Context context)
      throws Exception {
    WorkflowFormData formData = new WorkflowFormData();
    formData.initField();

    formData.loadCategoryList(rundata, context);
    formData.loadRouteList(rundata, context);
    if (formData.doDelete(this, rundata, context)) {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doWorkflow_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    } else {
      doWorkflow_list(rundata, context);
    }

  }

  /**
   * 依頼を否認する
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWorkflow_denial(RunData rundata, Context context)
      throws Exception {
    WorkflowConfirmFormData formData = new WorkflowConfirmFormData();
    formData.initField();
    formData.setAcceptFlg(false);
    if (formData.doUpdate(this, rundata, context)) {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doWorkflow_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    }

  }

  /**
   * 依頼を承認する
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWorkflow_accept(RunData rundata, Context context)
      throws Exception {
    WorkflowConfirmFormData formData = new WorkflowConfirmFormData();
    formData.initField();
    formData.setAcceptFlg(true);
    if (formData.doUpdate(this, rundata, context)) {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      rundata.setRedirectURI(jsLink.getPortletById(
        ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
        "eventSubmit_doWorkflow_list",
        "1").toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    }
  }

  @SuppressWarnings("unused")
  private void clearCellWorkflowSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("submenu");
    list.add("tab");
    list.add("alltab");
    list.add("com.aimluck.eip.workflow.WorkflowSelectDatasort");
    list.add("com.aimluck.eip.workflow.WorkflowAllSelectDatasort");
    list.add("com.aimluck.eip.workflow.WorkflowSelectDatafiltertype");
    list.add("com.aimluck.eip.workflow.WorkflowSelectDatafilter");
    ALEipUtils.removeTemp(rundata, context, list);
  }

}
