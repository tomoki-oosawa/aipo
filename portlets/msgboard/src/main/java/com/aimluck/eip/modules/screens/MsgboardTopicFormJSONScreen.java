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

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.msgboard.MsgboardTopicFormData;
import com.aimluck.eip.msgboard.MsgboardTopicMultiDelete;
import com.aimluck.eip.msgboard.MsgboardTopicReplyFormData;

/**
 * 掲示板トピックをJSONデータとして出力するクラスです。 
 * 
 */
public class MsgboardTopicFormJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MsgboardTopicFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {
        //
        MsgboardTopicFormData formData = new MsgboardTopicFormData();
        formData.initField();
        formData.loadCategoryList(rundata, context);
        if (formData.doInsert(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

      } else if (ALEipConstants.MODE_UPDATE.equals(mode)) {

        MsgboardTopicFormData formData = new MsgboardTopicFormData();
        formData.initField();
        formData.loadCategoryList(rundata, context);
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {

        MsgboardTopicFormData formData = new MsgboardTopicFormData();
        formData.initField();
        formData.loadCategoryList(rundata, context);
        if (formData.doDelete(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_MULTI_DELETE.equals(mode)) {
        MsgboardTopicMultiDelete delete = new MsgboardTopicMultiDelete();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("topic_reply".equals(mode)) {
        MsgboardTopicReplyFormData formData = new MsgboardTopicReplyFormData();
        formData.initField();
        if (formData.doInsert(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if ("delete_reply".equals(mode)) {
        MsgboardTopicReplyFormData formData = new MsgboardTopicReplyFormData();
        formData.initField();
        if (formData.doDelete(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      }

      /*
       * else if ("multi_complete".equals(mode)) {
       * 
       * ToDoMultiStateUpdate delete = new ToDoMultiStateUpdate(); if
       * (delete.doMultiAction(this, rundata, context)) { } else { ArrayList
       * list = (ArrayList) context .get(ALEipConstants.ERROR_MESSAGE_LIST);
       * JSONArray json = JSONArray.fromArray(list.toArray()); result =
       * json.toString(); } }
       */

    } catch (Exception e) {
      logger.error("[MsgboardTopicFormJSONScreen]", e);
    }

    return result;
  }
}
