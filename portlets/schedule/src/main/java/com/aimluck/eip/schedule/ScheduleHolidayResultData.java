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

package com.aimluck.eip.schedule;

import java.util.Date;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 設備のResultDataです。 <BR>
 * 
 */
public class ScheduleHolidayResultData implements ALData {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleHolidayResultData.class.getName());

  /** holiday ID */
  private ALNumberField holiday_id;

  /** 休日 */
  private ALDateTimeField holiday_date;

  /** タイトル */
  private ALStringField holiday_title;

  /** 登録者ID */
  private ALNumberField create_user_id;

  /** 更新者ID */
  private ALNumberField update_user_id;

  // /** 設備グループリスト */
  // private List<ScheduleHolidayResultData> ScheduleHolidayList;

  /**
   *
   *
   */
  @Override
  public void initField() {
    holiday_id = new ALNumberField();
    holiday_date = new ALDateTimeField();
    holiday_title = new ALStringField();
    create_user_id = new ALNumberField();
    update_user_id = new ALNumberField();
    // ScheduleHolidayList = new ArrayList<ScheduleHolidayResultData>();
  }

  /**
   * @return
   */
  public ALNumberField getHolidayId() {
    return holiday_id;
  }

  /**
   * @return
   */
  public ALDateTimeField getHolidayDate() {
    return holiday_date;
  }

  /**
   * @return
   */
  public ALStringField getHolidayTitle() {
    return holiday_title;
  }

  /**
   * @return
   */
  public String getStringHolidayTitle() {
    return holiday_title.getValue();
  }

  /**
   * @param i
   */
  public void setHolidayId(long i) {
    holiday_id.setValue(i);
  }

  public void setHolidayDate(Date data) {
    holiday_date.setValue(data);
  }

  /**
   * @param string
   */
  public void setHolidayTitle(String string) {
    holiday_title.setValue(string);
  }

  /**
   * @return
   */
  public ALNumberField getCreateUserID() {
    return create_user_id;
  }

  /**
   * @return
   */
  public ALNumberField getUpdateUserID() {
    return update_user_id;
  }

  /**
   * @param string
   */
  public void setCreateUserID(long i) {
    create_user_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setUpdateUserID(long i) {
    update_user_id.setValue(i);
  }
}
