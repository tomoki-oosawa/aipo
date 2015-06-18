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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 入力フィールドを表すクラス（数字用）です。 <br />
 * 
 */
public class ALNumberField extends ALAbstractField {

  /**
   *
   */
  private static final long serialVersionUID = 1154944827686497611L;

  /** 数字の大きさ制限フラグ */
  protected boolean limitValue = false;

  /** 数字の大きさ（最小値） */
  protected long minValue = Long.MIN_VALUE;

  /** 数字の大きさ（最大値） */
  protected long maxValue = Long.MAX_VALUE;

  /** 入力フィールド値（数字） */
  protected String value = null;

  /**
   * コンストラクタ
   * 
   */
  public ALNumberField() {
  }

  /**
   * コンストラクタ
   * 
   * @param value
   */
  public ALNumberField(long value) {
    setValue(value);
  }

  /**
   * コンストラクタ
   * 
   * @param str
   */
  public ALNumberField(String str) {
    setValue(str);
  }

  /**
   * 入力フィールド値（数字）を設定します。
   * 
   * @param value
   */
  public void setValue(long value) {
    this.value = Long.toString(value);
  }

  /**
   * 入力フィールド値（数字の文字列）を設定します。
   * 
   */
  @Override
  public void setValue(String str) {
    if (str == null) {
      value = null;
      return;
    }
    value = str.trim();
  }

  /**
   * 入力フィールド値（数字）を取得します。
   * 
   * @return
   */
  public long getValue() {
    long longValue = 0;
    if (isNumberValue()) {
      try {
        longValue = (Long.valueOf(value)).longValue();
      } catch (NumberFormatException ex) {
      }
    }
    return longValue;
  }

  /**
   * 入力フィールド値（数字）を取得します。
   * 
   * @return
   */
  public int getValueWithInt() {
    int value = 0;
    if (isNumberValue()) {
      try {
        value = Integer.parseInt(this.value);
      } catch (NumberFormatException ignore) {
      }
    }
    return value;
  }

  /**
   * 入力フィールド値の文字列表現を取得します。
   * 
   * @return
   */
  public String getValueAsString() {
    return value;
  }

  /**
   * 数字の大きさ制限の有無を判定します。
   * 
   * @return
   */
  public boolean isLimitValue() {
    return limitValue;
  }

  /**
   * 数字の大きさ制限（最小値と最大値）を設定します。
   * 
   * @param min
   * @param max
   */
  public void limitValue(long min, long max) {
    if (max < min) {
      throw new IllegalArgumentException();
    }

    minValue = min;
    maxValue = max;
    limitValue = true;
  }

  /**
   * 数字の大きさ制限（最小値）を設定します。
   * 
   * @return
   */
  public long getMinValue() {
    return minValue;
  }

  /**
   * 数字の大きさ制限（最大値）を設定します。
   * 
   * @return
   */
  public long getMaxValue() {
    return maxValue;
  }

  /**
   * 数字の大きさ制限（最小値）を設定します。
   * 
   * @param min
   */
  public void limitMinValue(long min) {
    if (getMaxValue() < min) {
      throw new IllegalArgumentException();
    }

    minValue = min;
    limitValue = true;
  }

  /**
   * 文字列の長さ制限（最大値）を設定します。
   * 
   * @param max
   */
  public void limitMaxValue(long max) {
    if (max < getMinValue()) {
      throw new IllegalArgumentException();
    }

    maxValue = max;
    limitValue = true;
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
        // 必須入力属性で値が設定されていない場合
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_NAME_SPAN",
          fieldName));
        return false;
      }
    } else {
      if (!isNumberValue()) {
        // 有効な数値が設定されていない場合
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_CORRECT_NUMBER_CAUTION_SPAN",
          fieldName));
        return false;
      } else {
        if (isLimitValue()) { // 値制限がある場合
          long longValue = getValue();
          if (longValue < getMinValue()) {
            // 設定値が最小値を下回る場合
            msgList.add(ALLocalizationUtils.getl10nFormat(
              "COMMONS_FIELD_INPUT_NUMBER_CAUTION_LESS",
              fieldName,
              getMinValue()));
            return false;
          }
          if (longValue > getMaxValue()) {
            // 設定値が最大値を上回る場合
            msgList.add(ALLocalizationUtils.getl10nFormat(
              "COMMONS_FIELD_INPUT_NUMBER_CAUTION_OVER",
              fieldName,
              getMaxValue()));
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * 入力フィールド値がNullではないかどうかを判定します。
   * 
   * @return
   */
  public boolean isNotNullValue() {
    if (value == null || value.trim().length() <= 0) {
      return false;
    }

    return true;
  }

  /**
   * 入力フィールド値が数字かどうかを判定します。
   * 
   * @return
   */
  protected boolean isNumberValue() {
    if (value == null) {
      return false;
    }

    try {
      byte[] chars;
      int len = value.length();
      for (int i = 0; i < len; i++) {
        chars =
          (Character.valueOf(value.charAt(i)).toString())
            .getBytes(ENCORDE_CONFIRM_CHARTYPE);
        if (chars.length > 1) {
          return false;
        }
      }
    } catch (UnsupportedEncodingException ex) {
      return false;
    }

    try {
      Long.valueOf(value);
    } catch (NumberFormatException ex) {
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
    return ALStringUtil.sanitizing(value);
  }
}
