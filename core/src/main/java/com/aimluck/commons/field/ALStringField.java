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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 入力フィールドを表すクラス（文字列用）です。 <br />
 * 
 */
public class ALStringField extends ALAbstractField {

  /**
   *
   */
  private static final long serialVersionUID = -5978839673623376818L;

  /** 文字の種類（全て） */
  public static final int TYPE_ALL = 0;

  /** 文字の種類（半角英字） */
  public static final int TYPE_ALPHABET = 1;

  /** 文字の種類（半角数字） */
  public static final int TYPE_NUMBER = 2;

  /** 文字の種類（半角英数字） */
  public static final int TYPE_ALPHABET_NUMBER = TYPE_ALPHABET | TYPE_NUMBER;

  /** 文字の種類（半角カナ文字） */
  public static final int TYPE_HANKAKUKANA = 4;

  /** 文字の種類（半角英数字カナ文字） */
  public static final int TYPE_ALPHABET_NUMBER_HANKAKUKANA =
    TYPE_ALPHABET_NUMBER | TYPE_HANKAKUKANA;

  /** 文字の種類（全角文字） */
  public static final int TYPE_MULTIBYTE = 8;

  /** 文字の種類（半角記号） */
  public static final int TYPE_SYMBOL = 16;

  /** 文字の種類（半角英数字記号） */
  public static final int TYPE_ASCII = TYPE_ALPHABET_NUMBER | TYPE_SYMBOL;

  public static final Pattern PATTERN = Pattern.compile("[\\u0000-\\uFFFF]*");

  /** 文字の種類 */
  protected int characterType = TYPE_ALL;

  /** 文字列の長さ制限フラグ */
  protected boolean limitLength = false;

  /** 文字列の長さ（最小値） */
  protected int minLength = 0;

  /** 文字列の長さ（最大値） */
  protected int maxLength = Integer.MAX_VALUE;

  /** 入力フィールド値（文字列） */
  protected String value = null;

  /** 入力フィールド値の左右の空白を削除するかのフラグ */
  protected boolean isTrimValue = true;

  /**
   * コンストラクタ
   * 
   */
  public ALStringField() {
    this(null);
  }

  /**
   * コンストラクタ
   * 
   * @param str
   */
  public ALStringField(String str) {
    setValue(str);
  }

  /**
   * 文字列の種類を取得します。
   * 
   * @return
   */
  public int getCharacterType() {
    return characterType;
  }

  /**
   * 文字列の長さ（最小値）を取得します。
   * 
   * @return
   */
  public int getMinLength() {
    return minLength;
  }

  /**
   * 文字列の長さ（最大値）を取得します。
   * 
   * @return
   */
  public int getMaxLength() {
    return maxLength;
  }

  /**
   * 入力フィールド値を取得します。
   * 
   * @return
   */
  public String getValue() {
    return value;
  }

  public String getURLEncodedValue() {
    if (value == null) {
      return "";
    } else {
      try {
        return URLEncoder.encode(value, "utf-8");
      } catch (UnsupportedEncodingException e) {
        return "file";
      }
    }
  }

  /**
   * 文字の種類を設定します。
   * 
   * @param i
   */
  public void setCharacterType(int i) {
    characterType = i;
  }

  /**
   * 文字列の長さ制限の有無を判定します。
   * 
   * @return
   */
  public boolean isLimitLength() {
    return limitLength;
  }

  /**
   * 入力フィールド値を設定します。
   * 
   * @param str
   */
  @Override
  public void setValue(String str) {
    if (str != null && isTrimValue) {
      value = removeSpace(str);
    } else {
      value = str;
    }
  }

  /**
   * 文字列の長さ制限（最小値と最大値）を設定します。
   * 
   * @param min
   * @param max
   */
  public void limitLength(int min, int max) {
    if (max < min) {
      throw new IllegalArgumentException();
    }

    minLength = min;
    maxLength = max;
    limitLength = true;
  }

  /**
   * 文字列の長さ制限（最小値）を設定します。
   * 
   * @param min
   */
  public void limitMinLength(int min) {
    if (maxLength < min) {
      throw new IllegalArgumentException();
    }

    minLength = min;
    limitLength = true;
  }

  /**
   * 文字列の長さ制限（最大値）を設定します。
   * 
   * @param max
   */
  public void limitMaxLength(int max) {
    if (max < minLength) {
      throw new IllegalArgumentException();
    }

    maxLength = max;
    limitLength = true;
  }

  /**
   * 入力フィールド値の左右の空白を取り除くかのフラグを設定します。
   * 
   * @param bool
   */
  public void setTrim(boolean bool) {
    isTrimValue = bool;
  }

  /**
   * 入力フィールド値の左右の空白を取り除くかどうかを判定します。
   * 
   * @return
   */
  public boolean isTrim() {
    return isTrimValue;
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
      if (!isValidCharacterType()) {
        // 設定されている文字セット以外の文字を含む場合
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_CHAR_TYPE_BY_NAME_SPAN",
          fieldName,
          getCharTypeByName()));
        return false;
      } else {
        if (isLimitLength()) {
          // 文字列長制限がある場合
          int len = value.length();
          if (len < getMinLength()) {
            // 文字列長が最小値を下回る場合
            msgList.add(ALLocalizationUtils.getl10nFormat(
              "COMMONS_FIELD_INPUT_LENGTH_CAUTION_LESS_SPAN",
              fieldName,
              getMinLength()));
            return false;
          }
          if (len > getMaxLength()) {
            // 文字列長が最大値を上回る場合
            msgList.add(ALLocalizationUtils.getl10nFormat(
              "COMMONS_FIELD_INPUT_LENGTH_CAUTION_OVER_SPAN",
              fieldName,
              getMaxLength()));
            return false;

          }
        }
        if (!isUTF8()) {
          // utf-8を超える範囲の文字が含まれている場合
          msgList.add(ALLocalizationUtils.getl10nFormat(
            "COMMONS_FIELD_INPUT_NOT_UTF_EIGHT",
            fieldName));
          return false;
        }
      }
    }

    return true;
  }

  /**
   * 文字の種類が正しいかを判定します。
   * 
   * @return
   */
  protected boolean isValidCharacterType() {
    if (getCharacterType() == TYPE_ALL) {
      return true;
    }

    if (!isNotNullValue()) {
      return true;
    }

    int length = value.length();
    for (int i1 = 0; i1 < length; i1++) {
      if ((getType(value.charAt(i1)) & getCharacterType()) == 0) {
        // 指定されたタイプ以外の文字を含んでいる
        return false;
      }
    }
    return true;
  }

  /**
   * 指定したchar型文字の種類を取得します。
   * 
   * @param ch
   * @return
   */
  protected int getType(char ch) {
    byte[] chars;

    try {
      chars =
        (Character.valueOf(ch).toString()).getBytes(ENCORDE_CONFIRM_CHARTYPE);
    } catch (UnsupportedEncodingException ex) {
      return TYPE_ALL;
    }

    if (chars.length == 2) {
      // 全角文字
      return TYPE_MULTIBYTE;
    }

    if (Character.isDigit(ch)) {
      // 半角数字
      return TYPE_NUMBER;
    }

    if (Character.isLetter(ch)) {
      // 半角英字
      return TYPE_ALPHABET;
    }

    // 半角記号
    return TYPE_SYMBOL;
  }

  /**
   * 入力フィールド値の文字列の長さを取得します。
   * 
   * @return
   */
  protected int valueByteLength() {
    int len = 0;
    if (value == null) {
      return len;
    }

    try {
      len = (value.getBytes(ENCORDE_CONFIRM_CHARTYPE)).length;
    } catch (UnsupportedEncodingException ex) {
      len = 0;
    }

    return len;
  }

  /**
   * 入力フィールド値がNullではないかどうかを判定します。
   * 
   * @return
   */
  protected boolean isNotNullValue() {
    if (value == null || value.length() <= 0) {
      return false;
    }

    return true;
  }

  protected boolean isUTF8() {
    Matcher m = PATTERN.matcher(value);
    return m.matches();
  }

  /**
   * 文字の種類の表示名を取得します。
   * 
   * @return
   */
  protected String getCharTypeByName() {
    switch (characterType) {
      case TYPE_ALPHABET:
        return ALLocalizationUtils.getl10n("COMMONS_FIELD_TYPE_ALPHABET");
      case TYPE_NUMBER:
        return ALLocalizationUtils.getl10n("COMMONS_FIELD_TYPE_NUMBER");
      case TYPE_HANKAKUKANA:
        return ALLocalizationUtils.getl10n("COMMONS_FIELD_TYPE_HANKAKUKANA");
      case TYPE_MULTIBYTE:
        return ALLocalizationUtils.getl10n("COMMONS_FIELD_TYPE_MULTIBYTE");
      case TYPE_ALPHABET_NUMBER:
        return ALLocalizationUtils
          .getl10n("COMMONS_FIELD_TYPE_ALPHABET_NUMBER");
      case TYPE_ALPHABET_NUMBER_HANKAKUKANA:
        return ALLocalizationUtils
          .getl10n("COMMONS_FIELD_TYPE_ALPHABET_NUMBER_HANKAKUKANA");
      case TYPE_SYMBOL:
        return ALLocalizationUtils.getl10n("COMMONS_FIELD_TYPE_SYMBOL");
      case TYPE_ASCII:
        return ALLocalizationUtils.getl10n("COMMONS_FIELD_TYPE_ASCII");
    }
    // 文字種別無指定
    return "";
  }

  /**
   * 入力フィールド値の左右の全角スペースを削除します。
   * 
   * @param str
   * @return
   */
  private static String removeSpace(String str) {
    int len = str.length();
    int st = 0;
    char[] val = str.toCharArray();

    while ((st < len) && (val[st] <= ' ' || val[st] == 0x3000)) {
      st++;
    }
    while ((st < len) && (val[len - 1] <= ' ' || val[len - 1] == 0x3000)) {
      len--;
    }
    return ((st > 0) || (len < str.length())) ? str.substring(st, len) : str;
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
