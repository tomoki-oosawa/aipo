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

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.todo.ToDoSelectData;
import com.aimluck.eip.todo.ToDoStateUpdateData;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoの一覧を処理するクラスです。 <br />
 * 
 */
public class ToDoScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {

      if ("update".equals(mode)) {
        updateState(rundata, context, portlet);
      }
      ParameterParser parser = rundata.getParameters();

      ToDoUtils.passPSML(rundata, context, "p12f-filters", parser
        .getString(ALEipConstants.LIST_FILTER));
      ToDoUtils.passPSML(rundata, context, "p12g-filtertypes", parser
        .getString(ALEipConstants.LIST_FILTER_TYPE));

      // 選択しているタブ情報の削除
      ALEipUtils.removeTemp(rundata, context, "tab");

      ToDoSelectData listData = new ToDoSelectData();
      listData.initField();
      listData.loadCategoryList(rundata);
      listData.setRowsNum(Integer.parseInt(portlet
        .getPortletConfig()
        .getInitParameter("p1a-rows")));
      listData.setTableColumNum(Integer.parseInt(portlet
        .getPortletConfig()
        .getInitParameter("p0e-rows")));
      listData.setStrLength(0);
      listData.doViewList(this, rundata, context);

      String layout_template = "portlets/html/ajax-todo.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[ToDoScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  protected void updateState(RunData rundata, Context context,
      VelocityPortlet portlet) {
    ToDoStateUpdateData data = new ToDoStateUpdateData();
    data.initField();
    data.doUpdate(this, rundata, context);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return ToDoUtils.TODO_PORTLET_NAME;
  }

}
