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
package com.aimluck.eip.msgboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 掲示板トピックのResultData <BR>
 *
 */
public class MsgboardTopicResultData implements ALData {

  /** トピック ID */
  private ALNumberField topic_id;

  /** トピック名 */
  private ALStringField topic_name;

  /** 親トピック ID */
  private ALNumberField parent_id;

  /** 所有者 ID */
  private ALNumberField owner_id;

  /** 所有者名 */
  private ALStringField owner_name;

  /** カテゴリ ID */
  private ALNumberField category_id;

  /** カテゴリ名 */
  private ALStringField category_name;

  /** 公開/非公開フラグ */
  private boolean is_public;

  /** 新着フラグ */
  private boolean new_topic;

  /** メモ */
  private ALStringField note;

  /** 返信数 */
  private ALNumberField reply_count;

  /** 登録者名 */
  private ALStringField create_user;

  /** 更新者名 */
  private ALStringField update_user;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /** 顔写真フラグ */
  private boolean has_photo;

  /** ログインユーザーID */
  private ALNumberField login_user_id;

  /**
   *
   *
   */
  @Override
  public void initField() {
    topic_id = new ALNumberField();
    topic_name = new ALStringField();
    parent_id = new ALNumberField();
    owner_id = new ALNumberField();
    owner_name = new ALStringField();
    category_id = new ALNumberField();
    category_name = new ALStringField();

    note = new ALStringField();
    note.setTrim(false);
    reply_count = new ALNumberField();
    create_user = new ALStringField();
    update_user = new ALStringField();
    create_date = new ALDateTimeField();
    update_date = new ALDateTimeField();

    attachmentFileList = new ArrayList<FileuploadBean>();

    is_public = true;
    new_topic = false;

    has_photo = false;

    login_user_id = new ALNumberField();
  }

  /**
   * @return
   */
  public ALNumberField getTopicId() {
    return topic_id;
  }

  /**
   * @return
   */
  public String getTopicName() {
    return topic_name.toString();
  }

  public String getTopicNameHtml() {
    return ALCommonUtils.replaceToAutoCR(topic_name.toString());
  }

  /**
   * @param i
   */
  public void setTopicId(long i) {
    topic_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setTopicName(String string) {
    topic_name.setValue(string);
  }

  /**
   * @param i
   */
  public void setParentId(long i) {
    parent_id.setValue(i);
  }

  /**
   * @param i
   */
  public ALNumberField getParentId() {
    return parent_id;
  }

  /**
   * @param i
   */
  public void setCategoryId(long i) {
    category_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setCategoryName(String string) {
    category_name.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getCategoryId() {
    return category_id;
  }

  /**
   * @return
   */
  public String getCategoryName() {
    return category_name.toString();
  }

  public String getCategoryNameHtml() {
    return ALCommonUtils.replaceToAutoCR(category_name.toString());
  }

  /**
   * @param i
   */
  public void setOwnerId(long i) {
    owner_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getOwnerId() {
    return owner_id;
  }

  public void setOwnerName(String string) {
    owner_name.setValue(string);
  }

  public ALStringField getOwnerName() {
    return owner_name;
  }

  public String getOwnerNameHtml() {
    return ALCommonUtils.replaceToAutoCR(owner_name.toString());
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return ALEipUtils.getFormattedTime(create_date);
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * @param string
   */
  public void setCreateDate(Date date) {
    create_date.setValue(date);
  }

  /**
   * @param string
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    this.update_date.setValue(date);
  }

  public ALStringField getCreateUser() {
    return create_user;
  }

  public void setCreateUser(String str) {
    create_user.setValue(str);
  }

  public ALStringField getUpdateUser() {
    return update_user;
  }

  public void setUpdateUser(String str) {
    update_user.setValue(str);
  }

  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  public void setAttachmentFiles(List<FileuploadBean> list) {
    attachmentFileList = list;
  }

  /**
   * 公開/非公開フラグ．
   *
   * @return
   */
  public boolean isPublic() {
    return is_public;
  }

  /**
   * @return
   */
  public void setPublicFlag(boolean bool) {
    is_public = bool;
  }

  public boolean isNew() {
    return new_topic;
  }

  public void setNewTopicFlag(boolean bool) {
    new_topic = bool;
  }

  public void setReplyCount(long count) {
    reply_count.setValue(count);
  }

  public ALNumberField getReplyCount() {
    return reply_count;
  }

  /**
   * @return has_photo
   */
  public boolean isOwnerHasPhoto() {
    return has_photo;
  }

  /**
   * @param hasPhoto
   */
  public void setOwnerHasPhoto(boolean hasPhoto) {
    this.has_photo = hasPhoto;
  }

  public void setLoginUserId(int i) {
    login_user_id.setValue(i);
  }

  public ALNumberField getLoginUserId() {
    return login_user_id;
  }

}
