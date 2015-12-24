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
package com.aimluck.eip.addressbook;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * アドレス帳会社情報のリザルトデータクラスです。
 */
public class AddressBookCompanyResultData implements ALData {

  /** 会社ID */
  private ALNumberField company_id;

  /** 会社名 */
  private ALStringField company_name;

  /** 会社名(フリガナ) */
  private ALStringField company_name_kana;

  private ALStringField post_name;

  private ALStringField zipcode;

  private ALStringField address;

  private ALStringField telephone;

  private ALStringField fax_number;

  private ALStringField url;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /**
   *
   */
  @Override
  public void initField() {
    company_id = new ALNumberField();
    company_name = new ALStringField();
    company_name_kana = new ALStringField();
    post_name = new ALStringField();
    zipcode = new ALStringField();
    address = new ALStringField();
    telephone = new ALStringField();
    fax_number = new ALStringField();
    url = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * @return
   */
  public ALStringField getAddress() {
    return address;
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
   * @return
   */
  public ALStringField getCompanyNameKana() {
    return company_name_kana;
  }

  /**
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALStringField getFaxNumber() {
    return fax_number;
  }

  /**
   * @return
   */
  public ALStringField getPostName() {
    return post_name;
  }

  /**
   * @return
   */
  public ALStringField getTelephone() {
    return telephone;
  }

  /**
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * @return
   */
  public ALStringField getUrl() {
    return url;
  }

  /**
   * @return
   */
  public ALStringField getZipcode() {
    return zipcode;
  }

  /**
   * @param field
   */
  public void setAddress(String field) {
    address.setValue(field);
  }

  /**
   * @param field
   */
  public void setCompanyId(long id) {
    company_id.setValue(id);
  }

  /**
   * @param field
   */
  public void setCompanyName(String field) {
    company_name.setValue(field);
  }

  /**
   * @param field
   */
  public void setCompanyNameKana(String field) {
    company_name_kana.setValue(field);
  }

  /**
   * @param field
   */
  public void setCreateDate(String field) {
    create_date.setValue(field);
  }

  /**
   * @param field
   */
  public void setFaxNumber(String field) {
    fax_number.setValue(field);
  }

  /**
   * @param field
   */
  public void setPostName(String field) {
    post_name.setValue(field);
  }

  /**
   * @param field
   */
  public void setTelephone(String field) {
    telephone.setValue(field);
  }

  /**
   * @param field
   */
  public void setUpdateDate(String field) {
    update_date.setValue(field);
  }

  /**
   * @param field
   */
  public void setUrl(String field) {
    url.setValue(field);
  }

  /**
   * @param field
   */
  public void setZipcode(String field) {
    zipcode.setValue(field);
  }

}
