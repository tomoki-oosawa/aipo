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

import com.aimluck.eip.system.SystemWebMailAccountFormData;
import com.aimluck.eip.system.util.SystemWebMailUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 管理者用メールアカウントの詳細画面を処理するクラスです。 <br />
 * 
 */
public class SystemWebMailAdminFormScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemWebMailAdminFormScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    try {
      SystemWebMailAccountFormData formData =
        new SystemWebMailAccountFormData();
      formData.initField();
      formData.doViewForm(this, rundata, context);
      if (formData.getAccountName().getValue() == null) {
        context.put("mode_form", "new_form");
      } else {
        context.put("mode_form", "edit_form");
      }

      setTemplate(
        rundata,
        context,
        "portlets/html/ajax-system-webmail-account-form-admin.vm");

    } catch (Exception ex) {
      logger.error("[WebMailAdminFormScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return SystemWebMailUtils.WEBMAIL_ADMIN_PORTLET_NAME;
  }
}
