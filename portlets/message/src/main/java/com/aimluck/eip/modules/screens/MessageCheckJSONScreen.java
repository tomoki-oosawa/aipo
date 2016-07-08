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

import net.sf.json.JSONObject;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMessage;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

public class MessageCheckJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MessageCheckJSONScreen.class.getName());

  @Override
  protected String getPrefix() {
    return "";
  }

  @Override
  protected String getSuffix() {
    return "";
  }

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONObject json = new JSONObject();

    try {
      Integer messageId = null;
      try {
        messageId = rundata.getParameters().getInt("messageId");
      } catch (Throwable t) {

      }
      if (messageId == null) {
        return json.toString();
      }
      EipTMessage message = MessageUtils.getMessage(messageId);
      if (message == null) {
        return json.toString();
      }
      if (MessageUtils.isJoinRoom(message, ALEipUtils.getUserId(rundata))) {
        EipTMessageRoom room = message.getEipTMessageRoom();
        ALEipUser login_user = ALEipUtils.getALEipUser(rundata);

        boolean isDesktopNotification =
          MessageUtils.isDesktopNotification(room, login_user
            .getUserId()
            .getValueWithInt());

        Integer userId = message.getUserId();
        ALEipUser user = ALEipUtils.getALEipUser(userId);
        json.put("messageId", messageId);
        json.put("userId", userId);
        if (isDesktopNotification) {
          String displayName = "";
          if (userId < 4) {
            displayName = ALOrgUtilsService.getAlias();
          } else {
            displayName = user.getAliasName().getValue();
          }
          json.put("displayName", displayName);
          json.put("text", ALCommonUtils.compressString(
            message.getMessage(),
            100));
        }
        json.put("hasPhoto", user.hasPhoto());
        json.put("photoModified", user.getPhotoModified());
      }
      result = json.toString();
    } catch (Exception e) {
      logger.error("MessageCheckJSONScreen.getJSONString", e);
    }

    return result;
  }
}
