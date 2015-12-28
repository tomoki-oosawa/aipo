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
package com.aimluck.eip.account;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 『役職』のResultDataです。 <br />
 */
public class AccountPositionResultData implements ALData {

  /** 役職ID */
  private ALNumberField position_id;

  /** 役職名 */
  private ALStringField position_name;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /**
   *
   *
   */
  @Override
  public void initField() {
    position_id = new ALNumberField();
    position_name = new ALStringField();
    create_date = new ALDateTimeField();
    update_date = new ALDateTimeField();
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALNumberField getPositionId() {
    return position_id;
  }

  /**
   * @return
   */
  public ALStringField getPositionName() {
    return position_name;
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return update_date;
  }

  /**
   * @param string
   */
  public void setCreateDate(Date date) {
    create_date.setValue(date);
  }

  /**
   * @param id
   */
  public void setPositionId(int id) {
    position_id.setValue(id);
  }

  /**
   * @param string
   */
  public void setPositionName(String string) {
    position_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdate_date(Date date) {
    update_date.setValue(date);
  }

}
