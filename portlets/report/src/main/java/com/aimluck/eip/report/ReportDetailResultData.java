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
package com.aimluck.eip.report;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 報告書のResultDataです。 <BR>
 * 
 */
public class ReportDetailResultData extends ReportResultData {

  /** ユーザーID */
  private ALNumberField user_id;

  /** メモ */
  private ALStringField note;

  /** 更新日 */
  private ALDateTimeField update_date;

  /**
   *
   *
   */
  @Override
  public void initField() {
    super.initField();
    user_id = new ALNumberField();
    note = new ALStringField();
    note.setTrim(false);
    update_date = new ALDateTimeField();
  }

  /**
   * @return
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @param i
   */
  public void setUserId(long i) {
    user_id.setValue(i);
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
  public ALDateTimeField getUpdateDate() {
    return update_date;
  }

  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    this.update_date.setValue(date);
  }

  public boolean hasPhoto() {
    ALEipUser user =
      ALEipManager.getInstance().getUser(
        Integer.valueOf((int) this.user_id.getValue()));
    if (user != null) {
      return user.hasPhoto();
    } else {
      return false;
    }

  }

}
