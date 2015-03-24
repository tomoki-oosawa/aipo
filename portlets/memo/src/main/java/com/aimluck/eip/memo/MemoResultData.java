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
package com.aimluck.eip.memo;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.util.ALEipUtils;

/**
 * メモ帳のResultDataです。 <BR>
 * 
 */
public class MemoResultData extends MemoLiteResultData {

  /** メモ */
  private ALStringField note;

  /** 作成日 */
  private Date create_date;

  /** 更新日 */
  private Date update_date;

  /**
   *
   *
   */
  @Override
  public void initField() {
    super.initField();
    note = new ALStringField();
    note.setTrim(false);
    create_date = new Date();
    update_date = new Date();
  }

  /**
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * @return
   */
  public String getNoteStr() {
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
    ALDateTimeField date = new ALDateTimeField("yyyy年M月d日");
    date.setValue(create_date);
    return date;
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
    if (date == null) {
      return;
    }
    create_date = date;
  }

  /**
   * @param string
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    update_date = date;
  }

}
