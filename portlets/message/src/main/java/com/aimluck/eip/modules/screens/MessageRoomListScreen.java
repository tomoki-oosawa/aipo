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

import java.util.Date;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.message.MessageRoomListSelectData;
import com.aimluck.eip.message.MessageRoomResultData;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageRoomListScreen extends ALVelocityScreen {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MessageRoomListScreen.class.getName());

  /**
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      Integer targetUserId = null;
      Integer roomId = null;
      boolean isNewRoom = false;
      EipTMessageRoom room = null;
      ALEipUser targetUser = null;
      String keyword = null;
      try {
        targetUserId = rundata.getParameters().getInteger("u");
      } catch (Throwable ignore) {
        // ignore
      }
      try {
        // keyword = rundata.getParameters().getString("k");
      } catch (Throwable ignore) {
        // ignore
      }
      if (targetUserId != null && targetUserId > 0) {
        int userId = ALEipUtils.getUserId(rundata);
        room = MessageUtils.getRoom(userId, targetUserId);
        if (room != null) {
          roomId = room.getRoomId();
        } else {
          targetUser = ALEipUtils.getALEipUser(targetUserId);
          isNewRoom = true;
        }
      } else {
        try {
          roomId = rundata.getParameters().getInteger("r");
        } catch (Throwable ignore) {
          // ignore
        }
      }
      context.put("currentRoom", roomId);

      MessageRoomListSelectData listData = new MessageRoomListSelectData();
      listData.initField();
      if (!StringUtils.isEmpty(keyword)) {
        listData.setKeyword(keyword);
        context.put("isSearch", true);
      } else {
        context.put("isSearch", false);
      }
      listData.doViewList(this, rundata, context);

      if (isNewRoom) {
        MessageRoomResultData rd = new MessageRoomResultData();
        rd.initField();
        rd.setAutoName(true);
        rd.setLastMessage("");
        rd.setUserId(targetUser.getUserId().getValue());
        rd.setName(targetUser.getAliasName().getValue());
        rd.setHasPhoto(targetUser.hasPhoto());
        rd.setPhotoModified(targetUser.getPhotoModified());
        rd.setRoomId(0);
        rd.setRoomType("O");
        rd.setUnreadCount(0);
        rd.setUpdateDate(new Date());

        listData.getList().add(0, rd);
        context.put("currentRoom", 0);
      }
      String layout_template = "portlets/html/ajax-message-room-list.vm";
      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("MessageRoomListScreen.doOutput", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return MessageUtils.MESSAGE_PORTLET_NAME;
  }

}
