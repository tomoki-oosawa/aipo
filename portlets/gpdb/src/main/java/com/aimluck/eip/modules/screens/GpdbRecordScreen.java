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

import com.aimluck.eip.gpdb.GpdbRecordSelectData;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class GpdbRecordScreen extends GpdbScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbRecordScreen.class.getName());

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

      GpdbRecordSelectData listData = new GpdbRecordSelectData();
      listData.initField();
      listData.setGpdbId(context, rundata);
      listData.setRowsNum(Integer.parseInt(ALEipUtils.getPortlet(
        rundata,
        context).getPortletConfig().getInitParameter("p1b-rows")));
      listData.doViewList(this, rundata, context);

      String layout_template = "portlets/html/ajax-gpdb-record.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[GpdbRecordScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

}
