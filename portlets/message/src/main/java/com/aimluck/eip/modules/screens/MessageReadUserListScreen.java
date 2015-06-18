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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMessage;
import com.aimluck.eip.message.MessageReadUserListSelectData;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageReadUserListScreen extends ALVelocityScreen {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MessageReadUserListScreen.class.getName());

  /**
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {

      Integer messageId = null;
      EipTMessage message = null;
      try {
        messageId = rundata.getParameters().getInteger("m");
      } catch (Throwable ignore) {
        // ignore
      }
      if (messageId == null) {
        ALEipUtils.redirectPageNotFound(rundata);
        return;
      }
      message = MessageUtils.getMessage(messageId);
      if (message == null) {
        ALEipUtils.redirectPageNotFound(rundata);
        return;
      }
      if (!MessageUtils.isJoinRoom(message, ALEipUtils.getUserId(rundata))) {
        ALEipUtils.redirectPermissionError(rundata);
        return;
      }

      MessageReadUserListSelectData listData =
        new MessageReadUserListSelectData();
      listData.setMessageId(messageId);
      listData.initField();
      listData.doViewList(this, rundata, context);

      String layout_template =
        "portlets/html/ajax-message-read-user-list.vm";
      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("MessageReadUserListScreen.doOutput", ex);
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
