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

import java.util.List;

import net.sf.json.JSONArray;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.wiki.WikiFormData;
import com.aimluck.eip.wiki.WikiMultiDelete;

/**
 * Wikiを処理するクラスです。 <br />
 * 
 */
public class WikiFormJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    String mode = this.getMode();
    try {

      if (ALEipConstants.MODE_INSERT.equals(mode)) {
        //
        WikiFormData formData = new WikiFormData();
        formData.initField();
        formData.loadTopWikiList(rundata, context);
        if (formData.doInsert(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_UPDATE.equals(mode)) {
        //
        WikiFormData formData = new WikiFormData();
        formData.initField();
        formData.loadTopWikiList(rundata, context);
        if (formData.doUpdate(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
          @SuppressWarnings("unchecked")
          List<String> msgList =
            (List<String>) context.get(ALEipConstants.ERROR_MESSAGE_LIST);
          if (msgList.contains(ALLocalizationUtils
            .getl10n("WIKI_CONFLICT_ERROR"))) {
            if (msgList.size() == 1) {
              ALEipUtils.removeTemp(rundata, context, "update_date");
            }
          }
        }
      } else if (ALEipConstants.MODE_DELETE.equals(mode)) {
        //
        WikiFormData formData = new WikiFormData();
        formData.initField();
        formData.loadTopWikiList(rundata, context);
        if (formData.doDelete(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      } else if (ALEipConstants.MODE_MULTI_DELETE.equals(mode)) {
        WikiMultiDelete delete = new WikiMultiDelete();
        if (delete.doMultiAction(this, rundata, context)) {
        } else {
          JSONArray json =
            JSONArray
              .fromObject(context.get(ALEipConstants.ERROR_MESSAGE_LIST));
          result = json.toString();
        }
      }

    } catch (Exception e) {
      logger.error("[WikiFormJSONScreen]", e);
    }

    return result;
  }

}
