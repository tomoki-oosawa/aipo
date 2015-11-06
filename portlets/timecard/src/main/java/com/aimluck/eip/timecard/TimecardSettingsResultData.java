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
package com.aimluck.eip.timecard;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * タイムカード集計の設定情報を保持する。
 * 
 * 
 */
public class TimecardSettingsResultData implements ALData {

  private ALNumberField timecard_settings_id;

  private ALStringField start_time;

  private ALStringField end_time;

  private ALNumberField worktime_in;

  private ALNumberField resttime_in;

  private ALNumberField worktime_out;

  private ALNumberField resttime_out;

  /**
   *
   */
  public void initField() {
    timecard_settings_id = new ALNumberField();
    start_time = new ALStringField();
    end_time = new ALStringField();
    worktime_in = new ALNumberField();
    resttime_in = new ALNumberField();
    worktime_out = new ALNumberField();
    resttime_out = new ALNumberField();
  }

  public ALNumberField getTimecardSettingsId() {
    return timecard_settings_id;
  }

  public ALStringField getStartTime() {
    return start_time;
  }

  public ALStringField getEndTime() {
    return end_time;
  }

  public ALNumberField getWorktimeIn() {
    return worktime_in;
  }

  public ALNumberField getResttimeIn() {
    return resttime_in;
  }

  public ALNumberField getWorktimeOut() {
    return worktime_out;
  }

  public ALNumberField getResttimeOut() {
    return resttime_out;
  }

  public void setTimecardSettingsId(int i) {
    timecard_settings_id.setValue(i);
  }

  public void setStartTime(String str) {
    start_time.setValue(str);
  }

  public void setEndTime(String str) {
    end_time.setValue(str);
  }

  public void setWorktimeIn(int i) {
    worktime_in.setValue(i);
  }

  public void setResttimeIn(int i) {
    resttime_in.setValue(i);
  }

  public void setWorktimeOut(int i) {
    worktime_out.setValue(i);
  }

  public void setResttimeOut(int i) {
    resttime_out.setValue(i);
  }

}
