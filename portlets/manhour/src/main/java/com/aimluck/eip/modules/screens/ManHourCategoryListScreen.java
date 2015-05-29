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

import com.aimluck.eip.category.CommonCategorySelectData;
import com.aimluck.eip.manhour.util.ManHourUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有カテゴリの一覧を処理するクラスです。 <br />
 * 
 */
public class ManHourCategoryListScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ManHourCategoryListScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      CommonCategorySelectData listData = new CommonCategorySelectData();
      listData.initField();
      listData.setRowsNum(Integer.parseInt(ALEipUtils.getPortlet(
        rundata,
        context).getPortletConfig().getInitParameter("p2a-rows")));
      listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(
        rundata,
        context).getPortletConfig().getInitParameter("p3a-strlen")));
      listData.doViewList(this, rundata, context);

      String layout_template = "portlets/html/ajax-manhour-category-list.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[ManHourCategoryListScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return ManHourUtils.MANHOUR_PORTLET_NAME;
  }

}
