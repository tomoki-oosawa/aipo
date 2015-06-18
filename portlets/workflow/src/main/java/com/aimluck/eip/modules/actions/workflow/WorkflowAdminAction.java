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
package com.aimluck.eip.modules.actions.workflow;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.workflow.WorkflowCategorySelectData;
import com.aimluck.eip.workflow.WorkflowRouteSelectData;

/**
 * ワークフローの管理者用アクションクラスです。 <BR>
 * 
 */
public class WorkflowAdminAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowAdminAction.class.getName());

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
    if (getMode() == null) {
      doWorkflow_category_list(rundata, context);
    }
  }

  /**
   * カテゴリを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWorkflow_category_list(RunData rundata, Context context)
      throws Exception {
    WorkflowCategorySelectData listData = new WorkflowCategorySelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "workflow-category-list");
  }

  /**
   * 申請経路を一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWorkflow_route_list(RunData rundata, Context context)
      throws Exception {
    WorkflowRouteSelectData listData = new WorkflowRouteSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "workflow-route-list");
  }

}
