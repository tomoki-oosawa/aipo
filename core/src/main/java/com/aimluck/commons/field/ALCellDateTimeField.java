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
 * 携帯電話用の入力フィールドを表すクラス（年月日時分用）です。 <br />
 * 
 */
public class ALCellDateTimeField extends ALDateTimeField {

  /**
   *
   */
  private static final long serialVersionUID = -7178634856186162538L;

  /**
   * コンストラクタ
   * 
   */
  public ALCellDateTimeField() {
    super();
  }

  /**
   * コンストラクタ
   * 
   * @param dateFormat
   */
  public ALCellDateTimeField(String dateFormat) {
    super(dateFormat);
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
      String dateStr = translateDate(calendar.getTime(), format);
      if ("Unknown".equals(dateStr) || "".equals(dateStr)) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "COMMONS_FIELD_DATE_TYPE_CAUTION",
          fieldName));
        return false;
      }
    }
    return true;
  }
}
