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

import com.aimluck.eip.cellular.CellularFormData;
import com.aimluck.eip.cellular.util.CellularUtils;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * CellularScreen
 * 
 */
public class CellularScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellularScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    String type = rundata.getParameters().getString("type", "");
    try {

      // 簡易アクセス情報を送信する
      if ("sendmail".equals(mode)) {
        ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, "1");

        CellularFormData formData = new CellularFormData();
        formData.initField();
        if (formData.doInsert(this, rundata, context)) {
          context.put("finishedSendmail", "T");
        }
        setTemplate(rundata, context, "portlets/html/ajax-cellular.vm");

      } else {

        ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, "1");

        CellularFormData formData = new CellularFormData();
        formData.initField();
        formData.doViewForm(this, rundata, context);
        if (type.equals("popup")) {
          setTemplate(
            rundata,
            context,
            "portlets/html/ajax-cellular-popup.vm");
        } else {
          setTemplate(rundata, context, "portlets/html/ajax-cellular.vm");
        }
      }

    } catch (Exception ex) {
      logger.error("[CellularScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return CellularUtils.CABINET_PORTLET_NAME;
  }

}
