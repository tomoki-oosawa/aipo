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
package com.aimluck.eip.common;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;

/**
 * セッションへ格納する会社情報を表すクラスです。 <br />
 * 
 */
public class ALEipCompany implements ALData {

  /** 会社ID */
  private ALNumberField company_id;

  /** 会社名 */
  private ALStringField company_name;

  /**
   *
   */
  public void initField() {
    company_id = new ALNumberField();
    company_name = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getCompanyId() {
    return company_id;
  }

  /**
   * @return
   */
  public ALStringField getCompanyName() {
    return company_name;
  }

  /**
   * @param id
   */
  public void setCompanyId(int id) {
    company_id.setValue(id);
  }

  /**
   * @param string
   */
  public void setCompanyName(String string) {
    company_name.setValue(string);
  }
}
