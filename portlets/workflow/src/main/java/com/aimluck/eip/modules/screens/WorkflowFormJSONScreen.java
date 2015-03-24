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

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.workflow.WorkflowConfirmFormData;
import com.aimluck.eip.workflow.WorkflowFormData;
import com.aimluck.eip.workflow.util.WorkflowUtils;

/**
 * ワークフローをJSONデータとして出力するクラスです。 <br />
 *
 */
public class WorkflowFormJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WorkflowFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {
        WorkflowFormData formData = new WorkflowFormData();
        formData.initField();
        formData.loadCategoryList(rundata, context);
        formData.loadRouteList(rundata, context);
        if (formData.doInsert(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      }
      if (ALEipConstants.MODE_UPDATE.equals(mode)) {
        WorkflowFormData formData = new WorkflowFormData();
        formData.initField();
        formData.loadCategoryList(rundata, context);
        formData.loadRouteList(rundata, context);
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("accept".equals(mode)) {
        // 現在のステータスが "A"でないなら下の走らせないないようにする。
        WorkflowConfirmFormData formData = new WorkflowConfirmFormData();
        formData.initField();
        formData.setAcceptFlg(true);
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("denial".equals(mode)) {
        WorkflowConfirmFormData formData = new WorkflowConfirmFormData();
        formData.initField();
        formData.setAcceptFlg(false);
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {
        WorkflowFormData formData = new WorkflowFormData();
        formData.initField();

        formData.loadCategoryList(rundata, context);
        formData.loadRouteList(rundata, context);
        if (formData.doDelete(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("categorytemplate".equals(mode)) {
        int value =
          Integer.valueOf(rundata.getParameters().getStrings("value")[0]);
        WorkflowFormData formData = new WorkflowFormData();
        Map<String, String> map = new HashMap<String, String>();

        formData.initField();
        formData.loadCategoryList(rundata, context);
        formData.loadRouteList(rundata, context);
        map.put("template", formData.categoryTemplate(value));
        Integer value2 = WorkflowUtils.getRouteIdFromCategoryId(value);
        if (value2 != null) {
          map.put("route_id", value2.toString());
          map.put("route_h", formData.getRouteHTemplate(value2));
          map.put("route", formData.getRouteMap(value2));
        } else {
          map.put("route_id", "");
          map.put("route_h", "");
          map.put("route", "");
        }
        JSONArray json = JSONArray.fromObject(map);
        result = json.toString();
      } else if ("routetemplate".equals(mode)) {
        Integer value =
          Integer.valueOf(rundata.getParameters().getStrings("value")[0]);
        WorkflowFormData formData = new WorkflowFormData();
        Map<String, String> map = new HashMap<String, String>();

        formData.initField();
        formData.loadCategoryList(rundata, context);
        formData.loadRouteList(rundata, context);
        if (value != 0) {
          map.put("route_h", formData.getRouteHTemplate(value));
          map.put("route", formData.getRouteMap(value));
        } else {
          map.put("route_h", "");
          map.put("route", "");
        }
        JSONArray json = JSONArray.fromObject(map);
        result = json.toString();
      }
    } catch (Exception e) {
      logger.error("[WorkflowFormJSONScreen]", e);
    }

    return result;
  }
}
