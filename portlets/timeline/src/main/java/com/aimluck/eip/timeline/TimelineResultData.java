/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.timeline;

import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムライントピックのResultData <BR>
 * 
 */
public class TimelineResultData implements ALData {

  /** トピック ID */
  private ALNumberField timeline_id;

  /** 親トピック ID */
  private ALNumberField parent_id;

  /** 所有者 ID */
  private ALNumberField owner_id;

  /** 所有者名 */
  private ALStringField owner_name;

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

  /** 登録日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** 子トピックオブジェクト */
  private List<TimelineResultData> coTopicList;

  /** 顔写真の有無 */
  private boolean has_photo;

  /**
   *
   *
   */
  @Override
  public void initField() {
    timeline_id = new ALNumberField();
    parent_id = new ALNumberField();
    owner_id = new ALNumberField();
    owner_name = new ALStringField();

    note = new ALStringField();
    note.setTrim(false);
    reply_count = new ALNumberField();
    create_user = new ALStringField();
    create_date = new ALDateTimeField();
    update_date = new ALDateTimeField();

    is_public = true;
    new_topic = false;
    has_photo = false;
  }

  /**
   * @return
   */
  public ALNumberField getTimelineId() {
    return timeline_id;
  }

  /**
   * @param i
   */
  public void setTimelineId(long i) {
    timeline_id.setValue(i);
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
   * @return coTopicList
   */
  public List<TimelineResultData> getCoTopicList() {
    return coTopicList;
  }

  /**
   * @param coTopicList
   *          セットする coTopicList
   */
  public void setCoTopicList(List<TimelineResultData> coTopicList) {
    this.coTopicList = coTopicList;
  }

  public Integer getReplyCountInteger() {
    return Integer.valueOf((int) reply_count.getValue());
  }

  /**
   * 
   * @param bool
   */
  public void setHasPhoto(boolean bool) {
    has_photo = bool;
  }

  public boolean hasPhoto() {
    return has_photo;
  }

}
