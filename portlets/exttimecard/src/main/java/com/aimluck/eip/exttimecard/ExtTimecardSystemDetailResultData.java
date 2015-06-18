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
package com.aimluck.eip.exttimecard;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * タイムカード管理の勤務形態の情報を保持する。
 * 
 * 
 */
public class ExtTimecardSystemDetailResultData extends
    ExtTimecardSystemResultData {

  private ALNumberField user_id;

  private ALNumberField start_hour;

  private ALNumberField start_minute;

  private ALNumberField end_hour;

  private ALNumberField end_minute;

  private ALNumberField start_day;

  private ALNumberField worktime_in;

  private ALNumberField resttime_in;

  private ALNumberField worktime_out;

  private ALNumberField resttime_out;

  private ALNumberField change_hour;

  private ALStringField outgoing_add_flag;

  private ALStringField create_date;

  private ALStringField update_date;

  /**
   *
   *
   */
  @Override
  public void initField() {
    super.initField();
    user_id = new ALNumberField();
    start_hour = new ALNumberField();
    start_minute = new ALNumberField();
    end_hour = new ALNumberField();
    end_minute = new ALNumberField();
    start_day = new ALNumberField();
    worktime_in = new ALNumberField();
    resttime_in = new ALNumberField();
    worktime_out = new ALNumberField();
    resttime_out = new ALNumberField();
    change_hour = new ALNumberField();
    outgoing_add_flag = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  public String getUserId() {
    return user_id.getValueAsString();
  }

  public String getStartHour() {
    return start_hour.getValueAsString();
  }

  public String getStartMinute() {
    return start_minute.getValueAsString();
  }

  public String getEndHour() {
    return end_hour.getValueAsString();
  }

  public String getEndMinute() {
    return end_minute.getValueAsString();
  }

  public String getSpan() {
    return ALLocalizationUtils.getl10nFormat(
      "EXTTIMECARD_HOUR_MINUTE_FORMAT_SPAN",
      start_hour.toString(),
      ALStringUtil.toTwoDigitString(start_minute),
      end_hour.toString(),
      ALStringUtil.toTwoDigitString(end_minute));
  }

  public String getWorkTimeIn() {
    return worktime_in.getValueAsString();
  }

  public String getRestTimeIn() {
    return resttime_in.getValueAsString();
  }

  public String getWorkTimeInRestTimeInText() {
    return ALLocalizationUtils.getl10nFormat(
      "EXTTIMECARD_RESTTIME_FOR_WORKTIME",
      worktime_in.toString(),
      resttime_in.toString());
  }

  public String getWorkTimeOut() {
    return worktime_out.getValueAsString();
  }

  public String getRestTimeOut() {
    return resttime_out.getValueAsString();
  }

  public String getWorkTimeOutRestTimeOutText() {
    return ALLocalizationUtils.getl10nFormat(
      "EXTTIMECARD_RESTTIME_FOR_WORKTIME",
      worktime_out.toString(),
      resttime_out.toString());
  }

  public String getChangeHour() {
    return change_hour.getValueAsString();
  }

  public String getOutgoingAddFlag() {
    return outgoing_add_flag.getValue();
  }

  public ALStringField getCreateDate() {
    return create_date;
  }

  public ALStringField getUpdateDate() {
    return update_date;
  }

  public ALNumberField getStartDay() {
    return start_day;
  }

  public void setUserId(int i) {
    user_id.setValue(i);
  }

  public void setStartHour(int i) {
    start_hour.setValue(i);
  }

  public void setStartMinute(int i) {
    start_minute.setValue(i);
  }

  public void setEndHour(int i) {
    end_hour.setValue(i);
  }

  public void setEndMinute(int i) {
    end_minute.setValue(i);
  }

  public void setWorkTimeIn(int i) {
    worktime_in.setValue(i);
  }

  public void setRestTimeIn(int i) {
    resttime_in.setValue(i);
  }

  public void setWorkTimeOut(int i) {
    worktime_out.setValue(i);
  }

  public void setRestTimeOut(int i) {
    resttime_out.setValue(i);
  }

  public void setChangeHour(int i) {
    change_hour.setValue(i);
  }

  public void setOutgoingAddFlag(String str) {
    outgoing_add_flag.setValue(str);
  }

  public void setCreateDate(String str) {
    create_date.setValue(str);
  }

  public void setUpdateDate(String str) {
    update_date.setValue(str);
  }

  public void setStartDay(int i) {
    start_day.setValue(i);
  }

}
