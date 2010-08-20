/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.commons.field;

import java.util.ArrayList;
import java.util.List;

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
        msgList.add("『 " + fieldName + " 』を入力してください。");
        return false;
      }
    } else {
      if (!isValidCharacterType()) {
        msgList.add("『 "
          + fieldName
          + " 』は"
          + getCharTypeByName()
          + "で入力してください。");
        return false;
      } else {
        if (isLimitLength()) {
          int len = valueByteLength();
          if (len < getMinLength()) {
            msgList.add("『 "
              + fieldName
              + " 』は"
              + getMinLength()
              + "文字以上で入力してください。");
            return false;
          }
          if (len > getMaxLength()) {
            msgList.add("『 "
              + fieldName
              + " 』は"
              + getMaxLength()
              + "文字以下で入力してください。");
            return false;
          }
        }
      }
    }
    return true;
  }
}
