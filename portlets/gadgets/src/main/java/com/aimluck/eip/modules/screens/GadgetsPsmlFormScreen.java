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

import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.gadgets.AdvancedGagetsPsmlFormData;
import com.aimluck.eip.gadgets.util.GadgetsUtils;

/**
 *
 */
public class GadgetsPsmlFormScreen extends ALVelocityScreen {

  /**
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    AdvancedGagetsPsmlFormData formData = new AdvancedGagetsPsmlFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);

    boolean maximized = false;
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    String jspeid = (String) jdata.getUser().getTemp("js_peid");
    if (jspeid != null) {
      maximized = true;
    }
    if (maximized) {
      context.put("maximized", "true");
    } else {
      context.put("maximized", "false");
    }

    String template = "portlets/html/gadgets-psml.vm";
    setTemplate(rundata, context, template);
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return GadgetsUtils.GADGETS_ADMIN_PORTLET_NAME;
  }

}