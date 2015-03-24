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

import java.util.ArrayList;
import java.util.List;

import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 携帯電話用の入力フィールドを表すクラス（数字用）です。 <br />
 * 
 */
public class ALCellNumberField extends ALNumberField {

  /**
   *
   */
  private static final long serialVersionUID = -6340064223039830226L;

  /**
   * コンストラクタ
   * 
   */
  public ALCellNumberField() {
    super();
  }

  /**
   * コンストラクタ
   * 
   * @param value
   */
  public ALCellNumberField(long value) {
    super(value);
  }

  /**
   * コンストラクタ
   * 
   * @param str
   */
  public ALCellNumberField(String str) {
    super(str);
  }

  /**
   * 入力フィールド値を検証します。
   * 
   * @param msgList
   * @return
   */
  @Override
  public boolean validate(List<String> msgList) {
    if (msgList == null) {
      msgList = new ArrayList<String>();
    }

    if (!isNotNullValue()) {
      if (isNotNull()) {
        // 必須入力属性で値が設定されていない場合
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_NAME",
          fieldName));
        return false;
      }
    } else {
      if (!isNumberValue()) {
        // 有効な数値が設定されていない場合
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_CORRECT_NUMBER_CAUTION",
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
}
