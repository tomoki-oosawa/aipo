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
package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.workflow.WorkflowAllSelectData;
import com.aimluck.eip.workflow.WorkflowSelectData;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * Webメールの詳細画面を処理するクラスです。 <br />
 * 
 */
public class WorkflowScreenPrint extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowScreenPrint.class.getName());

  /** コンテントタイプ */
  private static final String CONTENT_TYPE = "text/html;charset="
    + ALEipConstants.DEF_CONTENT_ENCODING;

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    try {
      String tabParam = rundata.getParameters().getString("tab");
      String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
      if ((WorkflowAllSelectData.TAB_ALLDISPLAY).equals(tabParam)
        || (WorkflowAllSelectData.TAB_ALLDISPLAY).equals(currentTab)) {
        WorkflowAllSelectData detailData = new WorkflowAllSelectData();
        detailData.initField();
        detailData.doViewDetail(this, rundata, context);
      } else {
        WorkflowSelectData detailData = new WorkflowSelectData();
        detailData.initField();
        detailData.doViewDetail(this, rundata, context);
      }
      String mailIndex =
        rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
      // context.put("currentTab", ALEipUtils.getTemp(rundata, context, "tab"));
      context.put(ALEipConstants.ENTITY_ID, mailIndex);
      setTemplate(
        rundata,
        context,
        "portlets/html/ajax-workflow-detail-print.vm");
    } catch (Exception ex) {
      logger.error("[WorkflowDetailScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  @Override
  protected String getContentType(RunData rundata) {
    return CONTENT_TYPE;
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return WorkflowUtils.WORKFLOW_PORTLET_NAME;
  }

}
