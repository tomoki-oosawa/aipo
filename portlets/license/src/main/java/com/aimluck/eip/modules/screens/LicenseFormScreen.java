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

import com.aimluck.eip.license.LicenseFormData;
import com.aimluck.eip.license.util.LicenseUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * LicenseFormScreen
 * 
 */
public class LicenseFormScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(LicenseFormScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      doSystem_form_license(rundata, context);
    } catch (Exception ex) {
      logger.error("[LicenseFormScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * ライセンス情報を登録するフォームを表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doSystem_form_license(RunData rundata, Context context)
      throws Exception {
    LicenseFormData formData = new LicenseFormData();
    formData.initField();
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, context, "portlets/html/ajax-license-form.vm");
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return LicenseUtils.LICENSE_PORTLET_NAME;
  }
}
