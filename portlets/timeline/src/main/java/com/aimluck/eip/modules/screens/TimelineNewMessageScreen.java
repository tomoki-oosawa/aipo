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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.timeline.TimelineSelectData;
import com.aimluck.eip.timeline.util.TimelineUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムライントピックの一覧を処理するクラスです。
 * 
 */
public class TimelineNewMessageScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    // VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    // String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      int scrollTop = 0;
      if (rundata.getParameters().containsKey("scrollTop")) {
        scrollTop = rundata.getParameters().getInt("scrollTop");
      }
      TimelineSelectData listData = new TimelineSelectData();
      listData.initField();
      listData.setScrollTop(scrollTop);
      listData.doViewListNew(this, rundata, context);

      if (listData.getList() != null && listData.getList().size() > 0) {
        String layout_template = "portlets/html/ajax-timeline-content.vm";
        setTemplate(rundata, context, layout_template);
      }

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
