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

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.timeline.TimelineUrlBeans;
import com.aimluck.eip.timeline.util.TimelineUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムライントピックの一覧を処理するクラスです。
 * 
 */
public class TimelineUrlScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineUrlScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      String url = ALEipUtils.getParameter(rundata, context, "url");
      TimelineUrlBeans listdata = TimelineUtils.perseFromUrl(url);
      if (listdata != null) {
        setResultData(listdata);
        putData(rundata, context);
      } else {
        try {
          BufferedWriter writer;
          ServletOutputStream out = null;
          HttpServletResponse response = rundata.getResponse();
          out = response.getOutputStream();
          writer =
            new BufferedWriter(new OutputStreamWriter(
              out,
              ALEipConstants.DEF_CONTENT_ENCODING));
          writer.write("error");
          writer.flush();
          writer.close();
          return;
        } catch (Exception e) {
          logger.error("[ALVelocityScreen]", e);
        }
      }
      String layout_template = "portlets/html/ajax-timeline-url.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[TimelineScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return TimelineUtils.TIMELIME_PORTLET_NAME;
  }
}
