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
package com.aimluck.eip.blog;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ブログコメントのResultDataです。 <BR>
 * 
 */
public class BlogCommentResultData implements ALData {

  /** Comment ID */
  private ALNumberField comment_id;

  /** Owner ID */
  private ALNumberField owner_id;

  /** Owner 名 */
  private ALStringField owner_name;

  /** メモ */
  private ALStringField comment;

  /** Entry ID */
  private ALNumberField entry_id;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /** 更新日(DATE) */
  private ALDateTimeField update_date_alternative;

  private ALEipUser owner;

  /**
   *
   *
   */
  @Override
  public void initField() {
    comment_id = new ALNumberField();
    owner_id = new ALNumberField();
    owner_name = new ALStringField();
    comment = new ALStringField();
    entry_id = new ALNumberField();
    create_date = new ALDateTimeField();
    update_date = new ALStringField();
    update_date_alternative = new ALDateTimeField();
  }

  /**
   * @return
   */
  public ALNumberField getCommentId() {
    return comment_id;
  }

  /**
   * @return
   */
  public ALNumberField getOwnerId() {
    return owner_id;
  }

  /**
   * @return
   */
  public ALStringField getOwnerName() {
    return owner_name;
  }

  /**
   * @return
   */
  public String getComment() {
    return ALEipUtils.getMessageList(comment.getValue());
  }

  /**
   * @return
   */
  public ALNumberField getEntryId() {
    return entry_id;
  }

  /**
   * @param i
   */
  public void setCommentId(long i) {
    comment_id.setValue(i);
  }

  /**
   * @param i
   */
  public void setOwnerId(long i) {
    owner_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setOwnerName(String string) {
    owner_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setComment(String string) {
    comment.setValue(string);
  }

  /**
   * @param i
   */
  public void setEntryId(long i) {
    entry_id.setValue(i);
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
  public ALStringField getUpdateDate() {
    return update_date;
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
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

  /**
   * @ return
   */
  public ALDateTimeField getUpdateDateAlternative() {
    return ALEipUtils.getFormattedTime(update_date_alternative);
  }

  /**
   * @param Date
   */
  public void setUpdateDateAlternative(Date date) {
    update_date_alternative.setValue(date);
  }

  /**
   * @return owner
   */
  public ALEipUser getOwner() {
    return owner;
  }

  /**
   * @param owner
   *          セットする owner
   */
  public void setOwner(ALEipUser owner) {
    this.owner = owner;
  }
}
