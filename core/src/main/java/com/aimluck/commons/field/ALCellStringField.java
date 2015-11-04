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
import java.util.List;

import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 携帯電話用の入力フィールドを表すクラス（文字列用）です。 <br />
 * 
 */
public class ALCellStringField extends ALStringField {

  /**
   *
   */
  private static final long serialVersionUID = -5043436998024783834L;

  /**
   * コンストラクタ
   * 
   */
  public ALCellStringField() {
    super();
  }

  /**
   * コンストラクタ
   * 
   * @param str
   */
  public ALCellStringField(String str) {
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
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_NAME",
          fieldName));
        return false;
      }
    } else {
      if (!isValidCharacterType()) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_CHAR_TYPE_BY_NAME",
          fieldName,
          getCharTypeByName()));
        return false;
      } else {
        if (isLimitLength()) {
          int len = valueByteLength();
          if (len < getMinLength()) {
            msgList.add(ALLocalizationUtils.getl10nFormat(
              "COMMONS_FIELD_INPUT_LENGTH_CAUTION_LESS",
              fieldName,
              getMinLength()));
            return false;
          }
          if (len > getMaxLength()) {
            msgList.add(ALLocalizationUtils.getl10nFormat(
              "COMMONS_FIELD_INPUT_LENGTH_CAUTION_OVER",
              fieldName,
              getMaxLength()));
            return false;
          }
        }
      }
    }
    return true;
  }
}
