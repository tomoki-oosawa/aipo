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
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.WebMailFormData;
import com.aimluck.eip.webmail.WebMailMultiDelete;
import com.aimluck.eip.webmail.WebMailMultiMove;
import com.aimluck.eip.webmail.WebMailMultiRead;
import com.aimluck.eip.webmail.WebMailMultiUnread;

/**
 * WebメールをJSONデータとして出力するクラスです。 <br />
 *
 */
public class WebMailFormJSONScreen extends ALJSONScreen {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {
        //
        WebMailFormData formData = new WebMailFormData();
        formData.initField();
        if (formData.doInsert(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {
        WebMailFormData formData = new WebMailFormData();
        formData.initField();
        if (formData.doDelete(this, rundata, context)) {
          ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

        // 一括削除
      } else if ("multi_delete".equals(mode)) {
        WebMailMultiDelete delete = new WebMailMultiDelete();
        if (delete.doMultiAction(this, rundata, context)) {
          this.setMode(ALEipConstants.MODE_DELETE);
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

        // 一括フォルダ移動
      } else if ("multi_move".equals(mode)) {
        WebMailMultiMove move = new WebMailMultiMove();
        if (move.doMultiAction(this, rundata, context)) {
          this.setMode("multi_move");
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

        // 一括既読化
      } else if ("multi_read".equals(mode)) {
        WebMailMultiRead read = new WebMailMultiRead();
        if (read.doMultiAction(this, rundata, context)) {
          this.setMode("multi_read");
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }

        // 一括未読化
      } else if ("multi_unread".equals(mode)) {
        WebMailMultiUnread unread = new WebMailMultiUnread();
        if (unread.doMultiAction(this, rundata, context)) {
          this.setMode("multi_unread");
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      }
    } catch (Exception e) {
      logger.error("[WebMailFormJSONScreen]", e);
    }

    return result;
  }
}
