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
package com.aimluck.eip.message;

import java.io.Serializable;
import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.message.util.MessageUtils;

/**
 *
 */
public class MessageRoomResultData implements ALData, Serializable {

  private static final long serialVersionUID = 1884204884214262528L;

  private ALNumberField roomId;

  private ALNumberField userId;

  private ALStringField name;

  private ALStringField roomType;

  private ALNumberField unreadCount;

  private ALStringField lastMessage;

  private ALDateTimeField updateDate;

  private ALDateTimeField updateDateTime;

  private ALDateTimeField updateYear;

  private ALDateTimeField updateDateYear;

  private Long photoModified;

  private boolean hasPhoto;

  private boolean isAutoName;

  /**
   *
   */
  @Override
  public void initField() {
    roomId = new ALNumberField();
    userId = new ALNumberField();
    name = new ALStringField();
    roomType = new ALStringField();
    unreadCount = new ALNumberField();
    lastMessage = new ALStringField();
    updateDate = new ALDateTimeField("M月d日");
    updateDateTime = new ALDateTimeField("H:mm");
    updateYear = new ALDateTimeField("yyyy年");
    updateDateYear = new ALDateTimeField("yyyy年M月d日");
  }

  /**
   * @param roomId
   *          セットする roomId
   */
  public void setRoomId(long roomId) {
    this.roomId.setValue(roomId);
  }

  /**
   * @return roomId
   */
  public ALNumberField getRoomId() {
    return roomId;
  }

  /**
   * @param name
   *          セットする name
   */
  public void setName(String name) {
    this.name.setValue(name);
  }

  /**
   * @return name
   */
  public ALStringField getName() {
    return name;
  }

  public ALStringField getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType.setValue(roomType);
  }

  public ALNumberField getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(long unreadCount) {
    this.unreadCount.setValue(unreadCount);
  }

  public ALStringField getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(String lastMessage) {
    this.lastMessage.setValue(lastMessage);
  }

  public void setUpdateDate(Date updateDate) {
    this.updateYear.setValue(updateDate);
    this.updateDateYear.setValue(updateDate);
    this.updateDate.setValue(updateDate);
    this.updateDateTime.setValue(updateDate);
  }

  public ALDateTimeField getUpdateDate() {
    ALDateTimeField today = new ALDateTimeField("M月d日");
    ALDateTimeField thisYear = new ALDateTimeField("yyyy年");
    today.setValue(new Date());
    thisYear.setValue(new Date());
    if (updateDate.toString().equals(today.toString())
      && updateYear.toString().equals(thisYear.toString())) {
      return updateDateTime;
    } else if (!updateDate.toString().equals(today.toString())
      && updateYear.toString().equals(thisYear.toString())) {
      return updateDate;
    } else {
      return updateDateYear;
    }
  }

  public boolean isAutoName() {
    return isAutoName;
  }

  public void setAutoName(boolean isAutoName) {
    this.isAutoName = isAutoName;
  }

  /**
   * @param hasPhoto
   *          セットする hasPhoto
   */
  public void setHasPhoto(boolean hasPhoto) {
    this.hasPhoto = hasPhoto;
  }

  /**
   * @return hasPhoto
   */
  public boolean isHasPhoto() {
    return hasPhoto;
  }

  /**
   * @param photoModified
   *          セットする photoModified
   */
  public void setPhotoModified(Long photoModified) {
    this.photoModified = photoModified;
  }

  /**
   * @return photoModified
   */
  public Long getPhotoModified() {
    return photoModified;
  }

  /**
   * @param userId
   *          セットする userId
   */
  public void setUserId(long userId) {
    this.userId.setValue(userId);
  }

  /**
   * @return userId
   */
  public ALNumberField getUserId() {
    return userId;
  }

  /**
   * @return authority
   */
  public String getAuthority(int userId) {
    EipTMessageRoom room = MessageUtils.getRoom(roomId.getValueWithInt());
    if (room == null) {
      return "M";
    }
    return MessageUtils.hasAuthorityRoom(room, userId) ? "A" : "M";
  }

  /**
   * @return desktopNotification
   */
  public String getDesktopNotification(int userId) {
    EipTMessageRoom room = MessageUtils.getRoom(roomId.getValueWithInt());
    if (room == null) {
      return "F";
    }
    return MessageUtils.isDesktopNotification(room, userId) ? "A" : "F";
  }

  /**
   * @return mobileNotification
   */
  public String getMobileNotification(int userId) {
    EipTMessageRoom room = MessageUtils.getRoom(roomId.getValueWithInt());
    if (room == null) {
      return "M";
    }
    return MessageUtils.isMobileNotification(room, userId) ? "A" : "F";
  }

}
