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

package com.aimluck.eip.holiday;

import java.util.Calendar;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * HolidayのResultDataです。 <BR>
 * 
 */
public class HolidayResultData implements ALData {

  /** Holiday ID */
  private ALNumberField holiday_id;

  /** 祝日の日付 */
  private ALStringField holiday_date;

  /** 祝日の名前 */
  private ALStringField holiday_name;

  /** デフォルトの祝日かどうかを示すフラグ */
  private ALStringField default_flag;

  /** インスタンスが生成された年 */
  private final static int createdYear = Calendar.getInstance().get(
    Calendar.YEAR);

  /** インスタンスが生成された月 */
  private final static int createdMonth = Calendar.getInstance().get(
    Calendar.MONTH);

  /** インスタンスが生成された日 */
  private final static int createdDay = Calendar.getInstance().get(
    Calendar.DAY_OF_MONTH);

  /**
   *
   *
   */
  @Override
  public void initField() {
    holiday_id = new ALNumberField();
    holiday_date = new ALStringField();
    holiday_name = new ALStringField();
    default_flag = new ALStringField();
  }

  /**
   * @return holiday_id
   */
  public ALNumberField getHolidayId() {
    return holiday_id;
  }

  /**
   * @return holiday_date
   */
  public ALStringField getHolidayDate() {
    return holiday_date;
  }

  /**
   * @return holiday_name
   */
  public ALStringField getHolidayName() {
    return holiday_name;
  }

  /**
   * @return default_flag
   */
  public ALStringField getDefaultFlag() {
    return default_flag;
  }

  /**
   * インスタンスが生成された年を返す． <BR>
   * 
   * @return
   */
  public static int getCreatedYear() {
    return createdYear;
  }

  /**
   * インスタンスが生成された月を返す． <BR>
   * 
   * @return
   */
  public static int getCreatedMonth() {
    return createdMonth;
  }

  /**
   * インスタンスが生成された日を返す． <BR>
   * 
   * @return
   */
  public static int getCreatedDay() {
    return createdDay;
  }

  /**
   * @param holiday_id
   *          セットする holiday_id
   */
  public void setHolidayId(int holiday_id) {
    this.holiday_id.setValue(holiday_id);
  }

  /**
   * @param holiday_date
   *          セットする holiday_date
   */
  public void setHolidayDate(String holiday_date) {
    this.holiday_date.setValue(holiday_date);
  }

  /**
   * @param holiday_name
   *          セットする holiday_name
   */
  public void setHolidayName(String holiday_name) {
    this.holiday_name.setValue(holiday_name);
  }

  /**
   * @param default_flag
   *          セットする default_flag
   */
  public void setDefaultFlag(String default_flag) {
    this.default_flag.setValue(default_flag);
  }
}
