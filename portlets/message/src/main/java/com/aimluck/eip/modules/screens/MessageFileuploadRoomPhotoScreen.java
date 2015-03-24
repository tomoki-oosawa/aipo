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

import java.util.Date;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageFileuploadRoomPhotoScreen extends FileuploadThumbnailScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MessageFileuploadRoomPhotoScreen.class.getName());

  private static final String KEY_ROOM_ID = "rid";

  /**
   * 
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    try {
      Integer rid = rundata.getParameters().getInt(KEY_ROOM_ID);
      if (rid == null || rid <= 0) {
        return;
      }

      EipTMessageRoom room = MessageUtils.getRoom(rid);
      if (room == null) {
        return;
      }

      if (!MessageUtils.isJoinRoom(room, ALEipUtils.getUserId(rundata))) {
        return;
      }

      byte[] photo;
      if (!ALCellularUtils.isSmartPhone(rundata)) {
        photo = room.getPhoto();
        Date date = room.getPhotoModified();
        if (date != null) {
          super.setLastModified(date);
        }
      } else {
        photo = room.getPhotoSmartphone();

        Date date = room.getPhotoModified();
        if (date != null) {
          super.setLastModified(date);
        }
      }
      if (photo == null) {
        return;
      }

      super.setFile(photo);
      super.setFileName("photo.jpg");
      super.doOutput(rundata);
    } catch (Exception e) {
      logger.error("MessageFileuploadRoomPhotoScreen.doOutput", e);
    }
  }
}
