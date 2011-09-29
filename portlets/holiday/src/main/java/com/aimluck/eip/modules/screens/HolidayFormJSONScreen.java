/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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
import com.aimluck.eip.holiday.HolidayDefaultFormat;
import com.aimluck.eip.holiday.HolidayFormData;
import com.aimluck.eip.holiday.HolidayMultiDelete;
import com.aimluck.eip.holiday.HolidaySelectData;

/**
 * HolidayをJSONデータとして出力するクラスです。 <br />
 * 
 */
public class HolidayFormJSONScreen extends ALJSONScreen {

  /** デフォルトに戻すモード */
  private static final String MODE_DEFAULT_FORMAT = "default_format";

  /** 表示年を次の年にするモード */
  private static final String MODE_INCREMENT_YEAR = "increment_year";

  /** 表示年を次の年にするモード */
  private static final String MODE_DECREMENT_YEAR = "decrement_year";

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(HolidayFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {
        //
        HolidayFormData formData = new HolidayFormData();
        formData.initField();
        if (formData.doInsert(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if (ALEipConstants.MODE_UPDATE.equals(mode)) {

        HolidayFormData formData = new HolidayFormData();
        formData.initField();
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {

        HolidayFormData formData = new HolidayFormData();
        formData.initField();
        if (formData.doDelete(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("multi_delete".equals(mode)) {

        HolidayMultiDelete delete = new HolidayMultiDelete();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (MODE_DEFAULT_FORMAT.equals(mode)) {
        HolidayDefaultFormat format = new HolidayDefaultFormat();
        format.action(rundata, context, HolidaySelectData
          .getViewedHolidayYear());
      } else if (MODE_INCREMENT_YEAR.equals(mode)) {
        HolidaySelectData.incrementViewedHolidayYear();
      } else if (MODE_DECREMENT_YEAR.equals(mode)) {
        HolidaySelectData.decrementViewedHolidayYear();
      }

    } catch (Exception e) {
      logger.error("[HolidayFormJSONScreen]", e);
    }

    return result;
  }
}
