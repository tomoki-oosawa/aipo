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
 * 携帯電話用の入力フィールドを表すクラス（年月日用）です。 <br />
 * 
 */
public class ALCellDateField extends ALDateField {

  /**
   *
   */
  private static final long serialVersionUID = -8240954278261905509L;

  /**
   * コンストラクタ
   * 
   */
  public ALCellDateField() {
    super();
  }

  /**
   * コンストラクタ
   * 
   * @param container
   */
  public ALCellDateField(ALDateContainer container) {
    super(container);
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
          "COMMONS_FIELD_INPUT_ERROR",
          fieldName));
        return false;
      } catch (ALIllegalDateException ex) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_INPUT_ERROR",
          fieldName));
        return false;
      }

    }
    return true;
  }
}
