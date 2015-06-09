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
package com.aimluck.commons.utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 入力フィールドで取り扱う日付に対するユーティリティクラスです。 <br />
 * 
 */
public class ALDateUtil {

  /** 日付の表示フォーマット */
  protected static final String DEFAULT_DATE_FORMAT = "yyyy'/'M'/'d";

  /**
   * 日付の文字列をもとに、Calendar型に変換します。
   * 
   * @param strDate
   * @return
   */
  public static Calendar dateToCalendar(String strDate) {
    return dateToCalendar(
      strDate.substring(0, 4),
      strDate.substring(4, 6),
      strDate.substring(6, 8),
      false);
  }

  /**
   * 年月日の文字列をもとに、Calendar型に変換します。
   * 
   * @param strYear
   * @param strMonth
   * @param strDay
   * @param b
   * @return
   */
  public static Calendar dateToCalendar(String strYear, String strMonth,
      String strDay, boolean b) {
    int year, month, day;
    try {
      year = Integer.parseInt(strYear);
      month = Integer.parseInt(strMonth) - 1;
      day = Integer.parseInt(strDay);
    } catch (StringIndexOutOfBoundsException e) {
      return null;
    } catch (NumberFormatException e) {
      return null;
    }
    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
    cal.setLenient(b);
    cal.set(year, month, day, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal;
  }

  /**
   * 年月日の文字列表現を取得します。
   * 
   * @param year
   * @param month
   * @param day
   * @return
   */
  public static String formatDate(int year, int month, int day) {
    return formatDate(String.valueOf(year), String.valueOf(month), String
      .valueOf(day));
  }

  /**
   * 年月日の文字列表現を取得します。
   * 
   * @param strYear
   * @param strMonth
   * @param strDay
   * @return
   */
  public static String formatDate(String strYear, String strMonth, String strDay) {
    Calendar cal = dateToCalendar(strYear, strMonth, strDay, false);
    if (cal == null) {
      return null;
    }
    try {
      cal.getTime();
    } catch (IllegalArgumentException e) {
      return null;
    }
    return formatCalendarDate(cal);
  }

  /**
   * Calendar型を文字列の日付に変換します。
   * 
   * @param cal
   * @return
   */
  public static String formatCalendarDate(Calendar cal) {
    return getStringYear(cal) + getStringMonth(cal) + getStringDay(cal);
  }

  /**
   * 年の文字列表現を取得します。
   * 
   * @param cal
   * @return
   */
  public static String getStringYear(Calendar cal) {
    Object[] args = new Object[1];
    args[0] = Integer.valueOf(cal.get(Calendar.YEAR));
    return MessageFormat.format("{0,number,0000}", args);
  }

  /**
   * 月の文字列表現を取得します。
   * 
   * @param cal
   * @return
   */
  public static String getStringMonth(Calendar cal) {
    return getStringMonth(cal.get(Calendar.MONTH));
  }

  /**
   * 月の文字列表現を取得します。
   * 
   * @param month
   * @return
   */
  public static String getStringMonth(int month) {
    switch (month) {
      case Calendar.JANUARY:
        return "01";
      case Calendar.FEBRUARY:
        return "02";
      case Calendar.MARCH:
        return "03";
      case Calendar.APRIL:
        return "04";
      case Calendar.MAY:
        return "05";
      case Calendar.JUNE:
        return "06";
      case Calendar.JULY:
        return "07";
      case Calendar.AUGUST:
        return "08";
      case Calendar.SEPTEMBER:
        return "09";
      case Calendar.OCTOBER:
        return "10";
      case Calendar.NOVEMBER:
        return "11";
      case Calendar.DECEMBER:
        return "12";
      default:
        return null;
    }
  }

  /**
   * 日の文字列表現を取得します。
   * 
   * @param cal
   * @return
   */
  public static String getStringDay(Calendar cal) {
    Object[] args = new Object[1];
    args[0] = Integer.valueOf(cal.get(Calendar.DATE));
    return MessageFormat.format("{0,number,00}", args);
  }

  /**
   * 曜日の文字列表現を取得します。
   * 
   * @param cal
   * @return
   */
  public static String getDayOfWeek(Calendar cal) {
    String res = "";

    switch (cal.get(Calendar.DAY_OF_WEEK)) {
      case Calendar.SUNDAY:
        res = ALLocalizationUtils.getl10n("COMMONS_UTILS_SUNDAY_CELL");
        break;
      case Calendar.MONDAY:
        res = ALLocalizationUtils.getl10n("COMMONS_UTILS_MONDAY_CELL");
        break;
      case Calendar.TUESDAY:
        res = ALLocalizationUtils.getl10n("COMMONS_UTILS_TUSEDAY_CELL");
        break;
      case Calendar.WEDNESDAY:
        res = ALLocalizationUtils.getl10n("COMMONS_UTILS_WEDNESDAY_CELL");
        break;
      case Calendar.THURSDAY:
        res = ALLocalizationUtils.getl10n("COMMONS_UTILS_THURSDAY_CELL");
        break;
      case Calendar.FRIDAY:
        res = ALLocalizationUtils.getl10n("COMMONS_UTILS_FRIDAY_CELL");
        break;
      case Calendar.SATURDAY:
        res = ALLocalizationUtils.getl10n("COMMONS_UTILS_SATURDAY_CELL");
        break;
      default:
        return null;
    }
    return res;
  }

  /**
   * 日付の文字列表現を取得します。
   * 
   * @param date
   * @return
   */
  public static String format(Date date) {
    return format(date, null);
  }

  /**
   * 日付の文字列表現を取得します。
   * 
   * @param date
   * @param strFormat
   * @return
   */
  public static String format(Date date, String strFormat) {
    String formatStr = null;
    int year;
    int month;
    int day;

    if (date == null) {
      return "";
    }
    if (strFormat == null) {
      strFormat = DEFAULT_DATE_FORMAT;
    }

    try {
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      year = calendar.get(Calendar.YEAR);
      month = calendar.get(Calendar.MONTH) + 1;
      day = calendar.get(Calendar.DATE);
    } catch (Throwable ex) {
      year = 0;
      month = 0;
      day = 0;
    }
    SimpleDateFormat formatter = new SimpleDateFormat(strFormat);
    GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);

    try {
      calendar.setLenient(false);
      formatStr = formatter.format(calendar.getTime());
    } catch (Exception ex) {
      formatStr = "";
    }
    return formatStr;
  }

}
