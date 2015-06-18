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

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.schedule.ScheduleWeeklyJSONFormData;

/**
 * カレンダーを処理するクラスです。 <br />
 * 
 */
public class ScheduleWeeklyJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleWeeklyJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    List<String> msgList = new ArrayList<String>();
    String mode = this.getMode();

    ScheduleWeeklyJSONFormData formData = new ScheduleWeeklyJSONFormData();
    try {
      if (ALEipConstants.MODE_INSERT.equals(mode)) {
        formData.init(this, rundata, context);
        formData.loadParameters(rundata, context, msgList);
        if (!(msgList.size() > 0) && !formData.getIsViewList()) {
          if (formData.doInsert(this, rundata, context)) {
          } else {
            msgList = formData.getMsgList();
          }
        }
      } else if (ALEipConstants.MODE_UPDATE.equals(mode)) {
        formData.init(this, rundata, context);
        formData.loadParameters(rundata, context, msgList);
        if (!(msgList.size() > 0) && !formData.getIsViewList()) {
          if (formData.doUpdate(this, rundata, context)) {
          } else {
            msgList = formData.getMsgList();
          }
        }
      }
    } catch (Exception e) {
      logger.error("[ScheduleWeeklyJSONScreen]", e);
    }

    formData.initField();
    return formData.doViewList(this, rundata, context, msgList);
  }
}
