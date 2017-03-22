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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.message.MessageListSelectData;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageListScreen extends ALVelocityScreen {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MessageListScreen.class.getName());

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
      String isRead = "";

      // メッセージのタブを開いているときには既読にする(非表示のときは既読にしない)
      try {
        isRead = rundata.getParameters().getString("is_read");
      } catch (Throwable ignore) {
        // ignore
      }

      try {
        targetUserId = rundata.getParameters().getInteger("u");
      } catch (Throwable ignore) {
        // ignore
      }
      int userId = ALEipUtils.getUserId(rundata);
      if (targetUserId != null && targetUserId > 0) {
        room = MessageUtils.getRoom(userId, targetUserId);
        if (room != null) {
          // roomId = room.getRoomId();
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
        if (roomId == null) {
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }

        room = MessageUtils.getRoom(roomId);
        if (room == null) {
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      }
      if (!isNewRoom) {
        if (!MessageUtils.isJoinRoom(room, userId)) {
          ALEipUtils.redirectPageNotFound(rundata);
          return;
        }
      }
      MessageListSelectData listData = new MessageListSelectData();
      if (isNewRoom) {
        listData.setTargetUserId((int) targetUser.getUserId().getValue());
      } else {
        listData.setRoom(room);
      }
      listData.initField();
      listData.doViewList(this, rundata, context);

      if (listData.getList().size() > 0) {
        if (!"F".equals(isRead)) {
          try {
            MessageUtils.read(room, listData.getUserId(), listData
              .getLastMessageId());
          } catch (Throwable ignore) {
            // ignore
            logger.error("MessageListScreen.MessageUtils.read", ignore);
          }
        }
      }

      String layout_template = "portlets/html/ajax-message-list.vm";
      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("MessageListScreen.doOutput", ex);
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
