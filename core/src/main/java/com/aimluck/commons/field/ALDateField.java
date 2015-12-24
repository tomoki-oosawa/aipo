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
package com.aimluck.commons.field;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 入力フィールドを表すクラス（年月日用）です。 <br />
 * 
 */
public class ALDateField extends ALAbstractField {

  /**
   *
   */
  private static final long serialVersionUID = 5926176023655878545L;

  /** 日付 */
  protected ALDateContainer value = null;

  /**
   * コンストラクタ
   * 
   */
  public ALDateField() {
  }

  /**
   * コンストラクタ
   * 
   * @param container
   */
  public ALDateField(ALDateContainer container) {
    setValue(container);
  }

  /**
   * 入力フィールド値を設定します。
   * 
   * @param container
   */
  public void setValue(ALDateContainer container) {
    value = container;
  }

  /**
   * 入力フィールド値を取得します。
   * 
   * @return
   */
  public ALDateContainer getValue() {
    return value;
  }

  /**
   * 入力フィールド値を検証します。
   * 
   * @param msgList
   * @return
   */
  public boolean validate(List<String> msgList) {
    if (msgList == null) {
      msgList = new ArrayList<String>();
    }

    if (!isNotNullValue()) {
      if (isNotNull()) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_NAME_SPAN",
          fieldName));
        return false;
      }
    } else {
      try {
        value.getDate();

        // 1582年以前の年の指定を弾く
        // (理由:Javaでは1582年より前の年にユリウス暦を採用しているのに対して、
        // PostgreSQLは暦として常にグレゴリオ暦が採用されているため)
        int year = value.getYear();
        if (year <= 1582) {
          msgList.add(ALLocalizationUtils.getl10nFormat(
            "COMMONS_FIELD_ERROR_DATE",
            fieldName));
          return false;
        }

      } catch (NumberFormatException ex) {

        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_ERROR_SPAN",
          fieldName));
        return false;
      } catch (ALIllegalDateException ex) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_ERROR_SPAN",
          fieldName));
        return false;
      }
    }
    return true;
  }

  /**
   * 年の文字列表現を取得します。
   * 
   * @return
   */
  public String toStringYear() {
    if (value == null) {
      return ALStringUtil.sanitizing(null);
    } else {
      return ALStringUtil.sanitizing(value.toStringYear());
    }
  }

  /**
   * 月の文字列表現を取得します。
   * 
   * @return
   */
  public String toStringMonth() {
    if (value == null) {
      return ALStringUtil.sanitizing(null);
    } else {
      return ALStringUtil.sanitizing(value.toStringMonth());
    }
  }

  /**
   * 日の文字列表現を取得します。
   * 
   * @return
   */
  public String toStringDay() {
    if (value == null) {
      return ALStringUtil.sanitizing(null);
    } else {
      return ALStringUtil.sanitizing(value.toStringDay());
    }
  }

  /**
   * 入力フィールド値（文字列）を設定します。<br />
   * 入力書式：YYYY-mm-DD 例) 2004-5-01
   * 
   */
  @Override
  public void setValue(String str) {
    if (str == null) {
      value = null;
      return;
    }

    String year = null;
    String month = null;
    String day = null;

    StringTokenizer st = new StringTokenizer(str.trim(), "-");
    if (st.hasMoreTokens()) {
      year = st.nextToken();
    } else {
      value = null;
      return;
    }
    if (st.hasMoreTokens()) {
      month = st.nextToken();
    } else {
      value = null;
      return;
    }
    if (st.hasMoreTokens()) {
      day = st.nextToken();
    } else {
      value = null;
      return;
    }

    value = new ALDateContainer();
    value.setYear(year);
    value.setMonth(month);
    value.setDay(day);
  }

  /**
   * 入力フィールド値（日付）を設定します。
   * 
   * @param date
   */
  public void setValue(Date date) {
    String year = null;
    String month = null;
    String day = null;

    try {
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(date);
      year = Integer.toString(calendar.get(Calendar.YEAR));
      month = Integer.toString(calendar.get(Calendar.MONTH) + 1);
      day = Integer.toString(calendar.get(Calendar.DATE));
    } catch (Throwable ex) {
      value = null;
    }

    value = new ALDateContainer();
    value.setYear(year);
    value.setMonth(month);
    value.setDay(day);
  }

  /**
   * 年の値を取得します。
   * 
   * @return
   */
  public String getYear() {
    return toStringYear();
  }

  /**
   * 月の値を取得します。
   * 
   * @return
   */
  public String getMonth() {
    return toStringMonth();
  }

  /**
   * 日の値を取得します。
   * 
   * @return
   */
  public String getDay() {
    return toStringDay();
  }

  /**
   * 入力フィールド値がNullではないかどうかを判定します。
   * 
   * @return
   */
  protected boolean isNotNullValue() {
    if (value == null
      || (value.isNullYear() && value.isNullMonth() && value.isNullDay())) {
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
    if (value == null) {
      return ALStringUtil.sanitizing(null);
    } else {
      return ALStringUtil.sanitizing(value.toStringYear()
        + '/'
        + value.toStringMonth()
        + '/'
        + value.toStringDay());
    }
  }
}
