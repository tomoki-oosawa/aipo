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

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.timeline.TimelineSelectData;
import com.aimluck.eip.timeline.util.TimelineUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログの詳細画面を処理するクラスです。 <br />
 * 
 */
public class TimelineDetailScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimelineDetailScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    try {
      TimelineSelectData detailData = new TimelineSelectData();
      detailData.initField();
      detailData.doViewDetail(this, rundata, context);

      String entityid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      context.put(ALEipConstants.ENTITY_ID, entityid);

      String layout_template = "portlets/html/ajax-timeline-detail.vm";

      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("[TimelineDetailScreen] Exception.", ex);
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
