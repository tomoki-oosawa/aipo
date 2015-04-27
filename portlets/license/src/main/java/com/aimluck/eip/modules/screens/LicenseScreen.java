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

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.license.LicenseSelectData;
import com.aimluck.eip.license.util.LicenseUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * LicenseScreen
 * 
 */
public class LicenseScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(LicenseScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {

      // ライセンスの情報を表示する
      doLicense_detail(rundata, context);

    } catch (Exception ex) {
      logger.error("[LicenseScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * ライセンス情報を表示する．
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doLicense_detail(RunData rundata, Context context)
      throws Exception {
    ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, "1");
    LicenseSelectData detailData = new LicenseSelectData();
    detailData.initField();
    detailData.doViewDetail(this, rundata, context);
    setTemplate(rundata, context, "portlets/html/ajax-license.vm");
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return LicenseUtils.LICENSE_PORTLET_NAME;
  }
}
