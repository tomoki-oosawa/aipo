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
package com.aimluck.eip.fileio;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.fileio.util.FileIOScheduleCsvUtils;
import com.aimluck.eip.schedule.ScheduleDetailResultData;
import com.aimluck.eip.util.ALLocalizationUtils;

public class FileIOScheduleCsvData extends ScheduleDetailResultData {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOScheduleCsvData.class.getName());

  /** データのCSVファイル上での位置(行数) */
  private int line_count;

  /** <code>note</code> 内容 */
  private ALStringField note;

  private boolean is_error;

  private ALStringField userNameString;

  private ALStringField loginNameString;

  /**
   * データのCSVファイル上での位置(行数)を取得します <BR>
   * 
   * @return
   */
  public int getLineCount() {
    return line_count;

  }

  public String getLineCounttest() {
    return ALLocalizationUtils.getl10nFormat(
      "FILEIO_ERROR_NUMBER",
      getLineCount());
  }

  @Override
  public void initField() {
    super.initField();
    // 内容
    note = new ALStringField();
    note.setTrim(false);
    is_error = false;

    userNameString = new ALStringField();
    loginNameString = new ALStringField();
  }

  /**
   * データのCSVファイル上での位置(行数)を入力します <BR>
   * 
   * @param i
   */
  public void setLineCount(int i) {
    line_count = i;
  }

  /**
   * 開始時間より終了時間が早くなっていた場合にはtrue <BR>
   * 
   * @return
   */
  public boolean isTimeError() {
    ALDateTimeField start_date = this.getStartDate();
    ALDateTimeField end_date = this.getEndDate();
    if (!start_date.toString().equals("") && !end_date.toString().equals("")) {
      if (end_date.getValue().before(start_date.getValue())) {
        return true;
      }
    }
    return false;
  }

  /**
   * 日付を跨らない様にする為に終了日と開始日が同じ日付になっているかどうかを示す関数 <BR>
   * 
   * @return
   */
  public boolean isDateError() {
    if (!FileIOScheduleCsvUtils.checkDateAcross(this.getStartDate(), this
      .getEndDate())) {
      return true;
    }
    return false;
  }

  /**
   * 内容を取得します。
   * 
   * @return
   */
  public ALStringField getNoteField() {
    return note;
  }

  /**
   * 内容を取得します。
   * 
   * @param string
   */
  @Override
  public void setNote(String string) {
    super.setNote(string);
    note.setValue(string);
  }

  public boolean getIsError() {
    return is_error;
  }

  public void setIsError(boolean flg) {
    is_error = flg;
  }

  @Override
  public boolean isSpan() {
    return FileIOScheduleCsvUtils
      .isSpan(this.getStartDate(), this.getEndDate());
  }

  public void setUserNameString(String userNameString) {
    this.userNameString.setValue(userNameString);
  }

  public ALStringField getUserNameString() {
    return userNameString;
  }

  public void setLoginNameString(String loginNameString) {
    this.loginNameString.setValue(loginNameString);
  }

  public ALStringField getLoginNameString() {
    return loginNameString;
  }

}
