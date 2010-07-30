/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.webmail.WebMailAccountFormData;

/**
 * 管理者用メールアカウントの詳細画面を処理するクラスです。 <br />
 *
 */
public class WebMailAdminFormJSONScreen extends ALJSONScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebMailAdminFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {
        //
        WebMailAccountFormData formData = new WebMailAccountFormData();
        rundata.getRequest().setAttribute("account_name",
            DatabaseOrmService.getInstance().getAlias() + "システムメールアカウント");
        formData.initField();
        if (formData.doInsert(this, rundata, context)) {
        } else {
          ArrayList list = (ArrayList) context
              .get(ALEipConstants.ERROR_MESSAGE_LIST);
          JSONArray json = JSONArray.fromObject(list);
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_UPDATE.equals(mode)) {
        WebMailAccountFormData formData = new WebMailAccountFormData();
        formData.initField();
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          ArrayList list = (ArrayList) context
              .get(ALEipConstants.ERROR_MESSAGE_LIST);
          JSONArray json = JSONArray.fromObject(list);
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {
        WebMailAccountFormData formData = new WebMailAccountFormData();
        formData.initField();
        if (formData.doDelete(this, rundata, context)) {
        } else {
          ArrayList list = (ArrayList) context
              .get(ALEipConstants.ERROR_MESSAGE_LIST);
          JSONArray json = JSONArray.fromObject(list);
          result = json.toString();
        }
      }
    } catch (Exception e) {
      logger.error("[WebMailAdminFormJSONScreen]", e);
    }

    return result;
  }
}
