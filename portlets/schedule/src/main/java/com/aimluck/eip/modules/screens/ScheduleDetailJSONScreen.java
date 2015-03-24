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

import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.schedule.ScheduleDetailResultData;
import com.aimluck.eip.schedule.ScheduleTooltipSelectData;

/**
 * カレンダーを処理するクラスです。 <br />
 * 
 */
public class ScheduleDetailJSONScreen extends ALJSONScreen {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleDetailJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    JSONObject json = new JSONObject();

    String entityid = rundata.getParameters().get("scheduleid");
    if (entityid == null || "".equals(entityid)) {
      return json.toString();
    }

    ScheduleTooltipSelectData detailData = new ScheduleTooltipSelectData();
    detailData.initField();

    JSONObject scheduleJson = new JSONObject();
    if (detailData.doViewDetail(this, rundata, context)) {
      ScheduleDetailResultData schedule =
        (ScheduleDetailResultData) detailData.getDetail();
      scheduleJson.put("id", schedule.getScheduleId().getValue());
      scheduleJson.put("name", ALStringUtil.sanitizing(schedule
        .getName()
        .getValue()));
      scheduleJson.put("date", schedule.getDate2());
      scheduleJson.put("place", ALStringUtil.sanitizing(schedule
        .getPlace()
        .getValue()));
      scheduleJson.put("isSpan", schedule.isSpan());
      scheduleJson.put("memberList", detailData.getMemberList());
      scheduleJson.put("facilityList", detailData.getFacilityList());
    }
    return scheduleJson.toString();
  }
}
