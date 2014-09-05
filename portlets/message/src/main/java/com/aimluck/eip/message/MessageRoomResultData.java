/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2014 Aimluck,Inc.
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
import com.aimluck.eip.common.ALData;

/**
 *
 */
public class MessageRoomResultData implements ALData, Serializable {

  private static final long serialVersionUID = 1884204884214262528L;

  private ALNumberField roomId;

  private ALStringField name;

  private ALStringField roomType;

  private ALNumberField unreadCount;

  private ALStringField lastMessage;

  private ALDateTimeField updateDate;

  private ALDateTimeField updateDateTime;

  private ALDateTimeField updateYear;

  private ALDateTimeField updateDateYear;

  private boolean isAutoName;

  /**
   *
   */
  @Override
  public void initField() {
    roomId = new ALNumberField();
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
}
