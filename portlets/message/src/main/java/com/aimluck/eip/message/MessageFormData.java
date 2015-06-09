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
package com.aimluck.eip.message;

import static com.aimluck.eip.util.ALLocalizationUtils.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTMessage;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageFile;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRead;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoomMember;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.fileupload.util.FileuploadUtils.ShrinkImageSet;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.push.ALPushService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MessageFormData.class.getName());

  private String orgId;

  private ALNumberField roomId;

  private ALNumberField userId;

  private ALStringField message;

  private ALEipUser login_user;

  private EipTMessageRoom room;

  private ALEipUser targetUser;

  private List<FileuploadLiteBean> fileuploadList;

  private String folderName = null;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    login_user = ALEipUtils.getALEipUser(rundata);
    orgId = Database.getDomainName();
    folderName = rundata.getParameters().getString("folderName");

  }

  /**
   *
   */
  @Override
  public void initField() {
    message = new ALStringField();
    message.setFieldName(getl10n("MESSAGE_CAPTION_MESSAGE"));
    message.setTrim(false);
    roomId = new ALNumberField();
    userId = new ALNumberField();
    fileuploadList = new ArrayList<FileuploadLiteBean>();

  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      try {
        fileuploadList = MessageUtils.getFileuploadList(rundata);
      } catch (Exception ex) {
        logger.error("message", ex);
      }
    }
    return res;
  }

  /**
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
    message.setNotNull(true);
    message.limitMaxLength(10000);
  }

  /**
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    if (userId.getValue() > 0) {
      targetUser = ALEipUtils.getALEipUser((int) userId.getValue());
    } else {
      room = Database.get(EipTMessageRoom.class, roomId.getValue());
      if (room == null) {
        msgList.add(getl10n("MESSAGE_VALIDATE_ROOM_NOT_FOUND"));
      }
      if (room != null
        && !MessageUtils.isJoinRoom(room, (int) login_user
          .getUserId()
          .getValue())) {
        msgList.add(getl10n("MESSAGE_VALIDATE_ROOM_ACCESS_DENIED"));
      }
    }
    message.validate(msgList);

    return msgList.size() == 0;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {

      Date now = new Date();

      if (room == null && targetUser != null) {
        int userId = (int) login_user.getUserId().getValue();
        int targetUserId = (int) targetUser.getUserId().getValue();
        room = MessageUtils.getRoom(userId, targetUserId);
        if (room == null) {
          room = Database.create(EipTMessageRoom.class);

          EipTMessageRoomMember map1 =
            Database.create(EipTMessageRoomMember.class);
          map1.setEipTMessageRoom(room);
          map1.setUserId((int) login_user.getUserId().getValue());
          map1.setTargetUserId((int) targetUser.getUserId().getValue());
          map1.setLoginName(login_user.getName().getValue());

          EipTMessageRoomMember map2 =
            Database.create(EipTMessageRoomMember.class);
          map2.setEipTMessageRoom(room);
          map2.setTargetUserId((int) login_user.getUserId().getValue());
          map2.setUserId((int) targetUser.getUserId().getValue());
          map2.setLoginName(targetUser.getName().getValue());

          room.setAutoName("T");
          room.setRoomType("O");
          room.setLastUpdateDate(now);
          room.setCreateDate(now);
          room.setCreateUserId((int) login_user.getUserId().getValue());
          room.setUpdateDate(now);

          Database.commit();
        }
      }
      if (room == null) {
        throw new IllegalArgumentException("room may not be null. ");
      }
      @SuppressWarnings("unchecked")
      List<EipTMessageRoomMember> members = room.getEipTMessageRoomMember();

      EipTMessage model = Database.create(EipTMessage.class);
      model.setEipTMessageRoom(room);
      model.setMessage(message.getValue());
      model.setCreateDate(now);
      model.setUpdateDate(now);
      model.setMemberCount(members.size());
      model.setUserId((int) login_user.getUserId().getValue());

      List<String> recipients = new ArrayList<String>();
      for (EipTMessageRoomMember member : members) {
        if (member.getUserId().intValue() != (int) login_user
          .getUserId()
          .getValue()) {
          EipTMessageRead record = Database.create(EipTMessageRead.class);
          record.setEipTMessage(model);
          record.setIsRead("F");
          record.setUserId(member.getUserId());
          record.setRoomId(room.getRoomId());
          recipients.add(member.getLoginName());
        }
      }

      room
        .setLastMessage(ALCommonUtils.compressString(message.getValue(), 100));
      room.setLastUpdateDate(now);

      insertAttachmentFiles(fileuploadList, folderName, (int) login_user
        .getUserId()
        .getValue(), model, msgList);

      Database.commit();

      roomId.setValue(room.getRoomId());

      Map<String, String> params = new HashMap<String, String>();
      params.put("roomId", String.valueOf(room.getRoomId()));
      params.put("messageId", String.valueOf(model.getMessageId()));

      ALPushService.pushAsync("messagev2", params, recipients);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("MessageFormData.insertFormData", ex);
      return false;
    }

    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  public ALStringField getMessage() {
    return message;
  }

  private boolean insertAttachmentFiles(
      List<FileuploadLiteBean> fileuploadList, String folderName, int uid,
      EipTMessage entry, List<String> msgList) {

    if (fileuploadList == null || fileuploadList.size() <= 0) {
      return true;
    }

    try {
      int length = fileuploadList.size();
      ArrayList<FileuploadLiteBean> newfilebeans =
        new ArrayList<FileuploadLiteBean>();
      FileuploadLiteBean filebean = null;
      for (int i = 0; i < length; i++) {
        filebean = fileuploadList.get(i);
        if (filebean.isNewFile()) {
          newfilebeans.add(filebean);
        }
      }
      int newfilebeansSize = newfilebeans.size();
      if (newfilebeansSize > 0) {
        FileuploadLiteBean newfilebean = null;
        for (int j = 0; j < length; j++) {
          newfilebean = newfilebeans.get(j);
          String[] acceptExts = ImageIO.getWriterFormatNames();
          ShrinkImageSet shrinkImageSet =
            FileuploadUtils.getBytesShrinkFilebean(
              orgId,
              folderName,
              uid,
              newfilebean,
              acceptExts,
              FileuploadUtils.DEF_THUMBNAIL_WIDTH,
              FileuploadUtils.DEF_THUMBNAIL_HEIGHT,
              msgList,
              true);

          String filename = j + "_" + String.valueOf(System.nanoTime());

          EipTMessageFile file = Database.create(EipTMessageFile.class);
          file.setOwnerId(Integer.valueOf(uid));
          file.setFileName(newfilebean.getFileName());
          file.setFilePath(MessageUtils.getRelativePath(filename));
          if (shrinkImageSet != null && shrinkImageSet.getShrinkImage() != null) {
            file.setFileThumbnail(shrinkImageSet.getShrinkImage());
          }
          file.setEipTMessage(entry);
          file.setRoomId(room.getRoomId());
          file.setCreateDate(Calendar.getInstance().getTime());
          file.setUpdateDate(Calendar.getInstance().getTime());

          ALStorageService.copyTmpFile(
            uid,
            folderName,
            String.valueOf(newfilebean.getFileId()),
            MessageUtils.FOLDER_FILEDIR_MESSAGE,
            MessageUtils.CATEGORY_KEY + ALStorageService.separator() + uid,
            filename);

          if (shrinkImageSet != null && shrinkImageSet.getFixImage() != null) {
            ALStorageService.createNewFile(
              new ByteArrayInputStream(shrinkImageSet.getFixImage()),
              MessageUtils.FOLDER_FILEDIR_MESSAGE
                + ALStorageService.separator()
                + Database.getDomainName()
                + ALStorageService.separator()
                + MessageUtils.CATEGORY_KEY
                + ALStorageService.separator()
                + uid
                + ALStorageService.separator()
                + filename);
          } else {
            ALStorageService.copyTmpFile(
              uid,
              folderName,
              String.valueOf(newfilebean.getFileId()),
              MessageUtils.FOLDER_FILEDIR_MESSAGE,
              MessageUtils.CATEGORY_KEY + ALStorageService.separator() + uid,
              filename);
          }
        }

        ALStorageService.deleteTmpFolder(uid, folderName);
      }

    } catch (Exception e) {
      logger.error(e);
    }
    return true;
  }

  public int getRoomId() {
    return (int) roomId.getValue();
  }
}
