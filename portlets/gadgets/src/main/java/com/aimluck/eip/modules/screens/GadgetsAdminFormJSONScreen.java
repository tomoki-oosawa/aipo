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

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.gadgets.GadgetsAdminFormData;
import com.aimluck.eip.gadgets.GagetsAdminMultiDelete;
import com.aimluck.eip.gadgets.GagetsAdminMultiDisable;
import com.aimluck.eip.gadgets.GagetsAdminMultiEnable;

/**
 *
 */
public class GadgetsAdminFormJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GadgetsAdminFormJSONScreen.class.getName());

  /**
   * @param rundata
   * @param context
   * @return
   * @throws Exception
   */
  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {

    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {

        GadgetsAdminFormData formData = new GadgetsAdminFormData();
        formData.initField();
        if (formData.doInsert(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_UPDATE.equals(mode)) {
        GadgetsAdminFormData formData = new GadgetsAdminFormData();
        formData.initField();
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {

        GadgetsAdminFormData formData = new GadgetsAdminFormData();
        formData.initField();
        if (formData.doDelete(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if ("disable".equals(mode)) {
        GadgetsAdminFormData formData = new GadgetsAdminFormData();
        formData.initField();
        formData.init(this, rundata, context);
        formData.disableFormData(rundata, context, null);

      } else if ("enable".equals(mode)) {
        GadgetsAdminFormData formData = new GadgetsAdminFormData();
        formData.initField();
        formData.init(this, rundata, context);
        formData.enableFormData(rundata, context, null);

      } else if ("multi_delete".equals(mode)) {

        GagetsAdminMultiDelete delete = new GagetsAdminMultiDelete();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("multi_enable".equals(mode)) {

        GagetsAdminMultiEnable delete = new GagetsAdminMultiEnable();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if ("multi_disable".equals(mode)) {

        GagetsAdminMultiDisable delete = new GagetsAdminMultiDisable();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      }
    } catch (RuntimeException e) {
      logger.error("[GadgetsAdminFormJSONScreen]", e);
    } catch (Exception e) {
      logger.error("[GadgetsAdminFormJSONScreen]", e);
    }

    return result;
  }

}
