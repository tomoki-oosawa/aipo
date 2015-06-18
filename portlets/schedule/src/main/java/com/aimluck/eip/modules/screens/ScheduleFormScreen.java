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

import com.aimluck.eip.schedule.ScheduleFormData;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュールフォームデータを管理するためのクラスです。 <br />
 * 
 */
public class ScheduleFormScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleFormScreen.class.getName());

  private final static String IGNORE_VIEWDATE = "ignore_viewdate";

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {

      doSchedule_form(rundata, context);

    } catch (Exception ex) {
      logger.error("[ScheduleFormScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  protected void doSchedule_form(RunData rundata, Context context) {
    ScheduleFormData formData = new ScheduleFormData();
    formData.loadParameters(rundata, context);
    formData.initField();
    formData.doViewForm(this, rundata, context);

    String ignore_viewdate = rundata.getParameters().getString(IGNORE_VIEWDATE);
    if (ignore_viewdate != null && ignore_viewdate.equals("true")) {
      rundata.getParameters().remove(IGNORE_VIEWDATE);
      context.put(IGNORE_VIEWDATE, "true");
    } else {
      context.put(IGNORE_VIEWDATE, "false");
    }
    String layout_template = "portlets/html/ajax-schedule-form.vm";
    setTemplate(rundata, context, layout_template);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return ScheduleUtils.SCHEDULE_PORTLET_NAME;
  }
}
