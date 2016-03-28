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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.project.ProjectTaskSimpleSelectData;
import com.aimluck.eip.project.util.ProjectUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ProjectScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProjectScreen.class.getName());

  /**
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    try {

      clearSession(rundata, context);

      ParameterParser parser = rundata.getParameters();
      ALEipUtils.passPSML(rundata, context, "p12f-filters", parser
        .getString(ALEipConstants.LIST_FILTER));
      ALEipUtils.passPSML(rundata, context, "p12g-filtertypes", parser
        .getString(ALEipConstants.LIST_FILTER_TYPE));

      ProjectTaskSimpleSelectData listData = new ProjectTaskSimpleSelectData();
      listData.initField();
      listData.setRowsNum(Integer.parseInt(ALEipUtils.getPortlet(
        rundata,
        context).getPortletConfig().getInitParameter("p1a-rows")));
      listData.setTopView(true);
      listData.doViewList(this, rundata, context);

      String layout_template = "portlets/html/ajax-project.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[ProjectScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * ポートレット名取得
   *
   * @return ポートレット名
   */
  @Override
  protected String getPortletName() {
    return ProjectUtils.PROJECT_PORTLET_NAME;
  }

  /**
   * セッション情報をクリアする
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  private void clearSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("target_keyword");
    list.add("target_user_id");
    list.add("target_tracker");
    list.add("target_priority");
    list.add("target_status");
    list.add("target_progress_rate_from");
    list.add("target_progress_rate_to");
    list.add("target_delay");
    ALEipUtils.removeTemp(rundata, context, list);
  }

}
