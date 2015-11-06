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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageReadCountListJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MessageReadCountListJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONObject().toString();
    try {

      int userId = ALEipUtils.getUserId(rundata);

      Integer roomId = null;
      try {
        roomId = rundata.getParameters().getInt("r");
      } catch (Throwable ignore) {
        // ignore
      }
      Integer maxMessageId = null;
      try {
        maxMessageId = rundata.getParameters().getInt("max");
      } catch (Throwable ignore) {
        // ignore
      }
      Integer minMessageId = null;
      try {
        minMessageId = rundata.getParameters().getInt("min");
      } catch (Throwable ignore) {
        // ignore
      }
      if (roomId != null && minMessageId != null && maxMessageId != null) {
        Map<Integer, Long> maps =
          MessageUtils.getReadCountList(
            roomId,
            userId,
            minMessageId,
            maxMessageId);
        JSONObject json = new JSONObject();
        Iterator<Entry<Integer, Long>> iterator = maps.entrySet().iterator();
        while (iterator.hasNext()) {
          Entry<Integer, Long> next = iterator.next();
          json.put(next.getKey(), next.getValue());
        }
        result = json.toString();
      }
    } catch (Exception e) {
      logger.error("MessageReadCountListJSONScreen.getJSONString", e);
    }

    return result;
  }
}
