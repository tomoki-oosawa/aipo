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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageResultData implements ALData, Serializable {

  private static final long serialVersionUID = 1884204884214262528L;

  private ALNumberField messageId;

  private ALNumberField roomId;

  private ALNumberField userId;

  private ALStringField lastName;

  private ALStringField firstName;

  private ALNumberField unreadCount;

  private ALNumberField memberCount;

  private ALStringField message;

  private ALStringField keyword;

  private ALDateTimeField createDate;

  private Long photoModified;

  private boolean hasPhoto = false;

  private boolean isOwner = false;

  private transient List<FileuploadBean> attachmentFileList;

  /**
   *
   */
  @Override
  public void initField() {
    messageId = new ALNumberField();
    roomId = new ALNumberField();
    userId = new ALNumberField();
    lastName = new ALStringField();
    firstName = new ALStringField();
    unreadCount = new ALNumberField();
    memberCount = new ALNumberField();
    message = new ALStringField();
    message.setTrim(false);
    createDate = new ALDateTimeField();
    attachmentFileList = new ArrayList<FileuploadBean>();
    keyword = new ALStringField();
  }

  /**
   * @param messageId
   *          セットする messageId
   */
  public void setMessageId(long messageId) {
    this.messageId.setValue(messageId);
  }

  /**
   * @return roomId
   */
  public ALNumberField getMessageId() {
    return messageId;
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

  public ALNumberField getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(long unreadCount) {
    this.unreadCount.setValue(unreadCount);
  }

  public void setCreateDate(Date createDate) {
    this.createDate.setValue(createDate);
  }

  public ALDateTimeField getCreateDate() {
    return ALEipUtils.getFormattedTimeDetail(createDate);
  }

  public ALDateTimeField getCreateDateLine() {
    ALDateTimeField date = new ALDateTimeField("yyyy年M月d日（E）");
    date.setValue(createDate.getValue());
    return date;
  }

  public ALDateTimeField getCreateDateKey() {
    ALDateTimeField date = new ALDateTimeField("yyyy-MM-dd");
    date.setValue(createDate.getValue());
    return date;
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
   * @param lastName
   *          セットする lastName
   */
  public void setLastName(String lastName) {
    this.lastName.setValue(lastName);
  }

  /**
   * @return lastName
   */
  public ALStringField getLastName() {
    return lastName;
  }

  /**
   * @param firstName
   *          セットする firstName
   */
  public void setFirstName(String firstName) {
    this.firstName.setValue(firstName);
  }

  /**
   * @return firstName
   */
  public ALStringField getFirstName() {
    return firstName;
  }

  /**
   * @param memberCount
   *          セットする memberCount
   */
  public void setMemberCount(long memberCount) {
    this.memberCount.setValue(memberCount);
  }

  /**
   * @return memberCount
   */
  public ALNumberField getMemberCount() {
    return memberCount;
  }

  /**
   * @return isOneMember
   */
  public boolean isOneMember() {
    return memberCount.getValue() == 1;
  }

  /**
   * @param message
   *          セットする message
   */
  public void setMessage(String message) {
    this.message.setValue(message);
  }

  /**
   * @return message
   */
  public ALStringField getMessage() {
    return message;
  }

  public String getMessageHtml() {
    return ALEipUtils.getMessageList(message.getValue(), keyword.getValue());
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

  public boolean isAllRead() {
    return 0 == this.unreadCount.getValue();
  }

  public int getReadCount() {
    return (int) (this.memberCount.getValue() - this.unreadCount.getValue() - 1);
  }

  /**
   * @param isOwner
   *          セットする isOwner
   */
  public void setOwner(boolean isOwner) {
    this.isOwner = isOwner;
  }

  /**
   * @return isOwner
   */
  public boolean isOwner() {
    return isOwner;
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

  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  /**
   * @return keyword
   */
  public ALStringField getKeyword() {
    return keyword;
  }

  /**
   * @param keyword
   *          セットする keyword
   */
  public void setKeyword(String keyword) {
    this.keyword.setValue(keyword);
  }

  /**
   * @param attachmentFileList
   *          セットする attachmentFileList
   */
  public void setAttachmentFileList(List<FileuploadBean> attachmentFileList) {
    this.attachmentFileList =
      attachmentFileList == null
        ? new ArrayList<FileuploadBean>(0)
        : attachmentFileList;
  }

  public ALStringField getDisplayName() {
    ALStringField displayName = new ALStringField();

    if (userId.getValue() < 4) {
      displayName.setValue(ALOrgUtilsService.getAlias());
    } else {
      displayName.setValue(lastName.getValue() + " " + firstName.getValue());
    }
    return displayName;
  }

  public boolean isSystemUser() {
    return userId.getValue() < 4;
  }
}
