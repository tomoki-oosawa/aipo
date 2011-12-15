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

package com.aimluck.eip.modules.actions.report;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.report.ReportSelectData;
import com.aimluck.eip.report.util.ReportUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 報告書のアクションクラスです。 <BR>
 * 
 */
public class ReportAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ReportAction.class.getName());

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
    ReportUtils.clearReportSession(rundata, context);

    ReportSelectData listData = new ReportSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "report");
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

    // セッション情報をクリア
    ReportUtils.clearReportSession(rundata, context);

    ReportSelectData listData = new ReportSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "report-list");
  }

}
