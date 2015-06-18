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
package com.aimluck.commons.field;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日付を表すクラス（年月日用）です。 <br />
 * 
 */
public class ALDateContainer implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -4471272059151133185L;

  /** 日付 */
  protected Date date = null;

  /** 年 */
  protected String year = null;

  /** 月 */
  protected String month = null;

  /** 日 */
  protected String day = null;

  /**
   * コンストラクタ
   * 
   */
  public ALDateContainer() {
  }

  /**
   * 日付がNullではないかどうかを判定します。
   * 
   * @return
   */
  public boolean isNotNullValue() {
    return (isNullYear() && isNullMonth() && isNullDay());
  }

  /**
   * 年の値を設定します。
   * 
   * @param str
   */
  public void setYear(String str) {
    year = str;
  }

  /**
   * 年の値を設定します。
   * 
   * @param i
   */
  public void setYear(int i) {
    year = Integer.toString(i);
  }

  /**
   * 月の値を設定します。
   * 
   * @param str
   */
  public void setMonth(String str) {
    month = str;
  }

  /**
   * 月の値を設定します。
   * 
   * @param i
   */
  public void setMonth(int i) {
    month = Integer.toString(i);
  }

  /**
   * 日の値を設定します。
   * 
   * @param str
   */
  public void setDay(String str) {
    day = str;
  }

  /**
   * 日の値を設定します。
   * 
   * @param i
   */
  public void setDay(int i) {
    day = Integer.toString(i);
  }

  /**
   * 日付の値を設定します。
   * 
   * @param date
   */
  public void setDate(Date date) {
    if (date != null) {
      this.date = (Date) date.clone();
    }

    try {
      Calendar calendar = new GregorianCalendar();
      if (date == null) {
        year = null;
        month = null;
        day = null;
      } else {
        calendar.setTime(date);
        year = Integer.toString(calendar.get(Calendar.YEAR));
        month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
        day = Integer.toString(calendar.get(Calendar.DATE));
      }
    } catch (Throwable ex) {
      year = null;
      month = null;
      day = null;
    }
  }

  /**
   * 年の文字列表現を取得します。
   * 
   * @return
   */
  public String toStringYear() {
    return year;
  }

  /**
   * 月の文字列表現を取得します。
   * 
   * @return
   */
  public String toStringMonth() {
    return month;
  }

  /**
   * 日の文字列表現を取得します。
   * 
   * @return
   */
  public String toStringDay() {
    return day;
  }

  /**
   * 年がNullではないかどうかを判定します。
   * 
   * @return
   */
  public boolean isNullYear() {
    if (date != null) {
      return false;
    }

    if ((year != null) && (year.trim().length() != 0)) {
      return false;
    }

    return true;
  }

  /**
   * 月がNullではないかどうかを判定します。
   * 
   * @return
   */
  public boolean isNullMonth() {
    if (date != null) {
      return false;
    }

    if ((month != null) && (month.trim().length() != 0)) {
      return false;
    }

    return true;
  }

  /**
   * 日がNullではないかどうかを判定します。
   * 
   * @return
   */
  public boolean isNullDay() {
    if (date != null) {
      return false;
    }

    if ((day != null) && (day.trim().length() != 0)) {
      return false;
    }

    return true;
  }

  /**
   * 年の値を取得します。
   * 
   * @return
   * @throws NumberFormatException
   * @throws ALIllegalDateException
   */
  public int getYear() throws NumberFormatException, ALIllegalDateException {
    if (!isNarrow(year)) {
      throw new ALIllegalDateException("Year = " + year);
    }

    int yearNum = Integer.parseInt(year);
    if (yearNum < 0) {
      throw new ALIllegalDateException("Year = " + year);
    }
    return yearNum;
  }

  /**
   * 月の値を取得します。
   * 
   * @return
   * @throws NumberFormatException
   * @throws ALIllegalDateException
   */
  public int getMonth() throws NumberFormatException, ALIllegalDateException {
    if (!isNarrow(month)) {
      throw new ALIllegalDateException("Month = " + month);
    }

    int monthNum = Integer.parseInt(month);
    if (monthNum < 1 || monthNum > 12) {
      throw new ALIllegalDateException("Month = " + month);
    }
    return monthNum;
  }

  /**
   * 日の値を取得します。
   * 
   * @return
   * @throws NumberFormatException
   * @throws ALIllegalDateException
   */
  public int getDay() throws NumberFormatException, ALIllegalDateException {
    if (!isNarrow(day)) {
      throw new ALIllegalDateException("Day = " + day);
    }

    int dayNum = Integer.parseInt(day);
    if (dayNum < 1 || dayNum > 31) {
      throw new ALIllegalDateException("Day = " + day);
    }
    return dayNum;
  }

  /**
   * 日付の値を取得します。
   * 
   * @return
   * @throws NumberFormatException
   * @throws ALIllegalDateException
   */
  public Date getDate() throws NumberFormatException, ALIllegalDateException {
    if (date != null) {
      return (Date) date.clone();
    }

    int yearNum = getYear();
    int monthNum = getMonth() - 1;
    int dayNum = getDay();

    try {
      Calendar calendar = new GregorianCalendar(yearNum, monthNum, dayNum);

      calendar.setLenient(false);
      date = calendar.getTime();
      return (Date) date.clone();
    } catch (IllegalArgumentException ex) {
      throw new ALIllegalDateException("Year = "
        + year
        + ", Month = "
        + month
        + ", Day = "
        + day);
    }
  }

  /**
   * 全角文字が含まれているかどうかを判定します。
   * 
   * @param str
   * @return
   */
  protected boolean isNarrow(String str) {
    if (str == null) {
      return false;
    }

    byte[] chars;
    int length = str.length();
    try {
      for (int i = 0; i < length; i++) {
        chars =
          (Character.valueOf(str.charAt(i)).toString())
            .getBytes(ALAbstractField.ENCORDE_CONFIRM_CHARTYPE);

        if (chars.length > 1) {
          return false;
        }
      }
    } catch (UnsupportedEncodingException ex) {
      return false;
    }

    return true;
  }

  /**
   * 入力フィールド値の文字列表現を取得します。
   * 
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    try {
      sb
        .append(toStringYear())
        .append("/")
        .append(toStringMonth())
        .append("/")
        .append(toStringDay());
    } catch (Throwable ex) {
      return "";
    }
    return sb.toString();
  }
}
