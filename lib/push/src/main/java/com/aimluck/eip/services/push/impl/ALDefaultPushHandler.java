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
package com.aimluck.eip.services.push.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.http.ALCometServlet;
import com.aimluck.eip.http.ALCometServlet.MessageSender;
import com.aimluck.eip.http.ServletContextLocator;
import com.aimluck.eip.services.push.ALPushHandler;

/**
 *
 */
public class ALDefaultPushHandler extends ALPushHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALDefaultPushHandler.class.getName());

  private static ALPushHandler instance;

  public static ALPushHandler getInstance() {
    if (instance == null) {
      instance = new ALDefaultPushHandler();
    }

    return instance;
  }

  /**
   * @param type
   * @param recipients
   */
  @Override
  public void pushAsync(String type, Map<String, String> params,
      List<String> recipients) {
    ServletContext servletContext = ServletContextLocator.get();
    if (servletContext != null) {
      JSONObject object = new JSONObject();
      object.put("type", type);
      if (params != null) {
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
          Entry<String, String> next = iterator.next();
          object.put(next.getKey(), next.getValue());
        }
      }

      MessageSender messageSender =
        (MessageSender) servletContext
          .getAttribute(ALCometServlet.KEY_MESSAGE_SENDER);

      messageSender.sendMessage(recipients, object.toString());

    }
  }

}
