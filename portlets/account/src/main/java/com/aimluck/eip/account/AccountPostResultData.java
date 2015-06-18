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
package com.aimluck.eip.account;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 部署のResultDataです。 <br />
 */
public class AccountPostResultData implements ALData {

  /** 部署ID */
  private ALNumberField post_id;

  /** 会社ID */
  private ALNumberField company_id;

  /** 部署名 */
  private ALStringField post_name;

  /** 郵便番号 */
  private ALStringField zipcode;

  /** 住所 */
  private ALStringField address;

  /** 電話番号（外線） */
  private ALStringField in_telephone;

  /** 電話番号（内線） */
  private ALStringField out_telephone;

  /** FAX 番号 */
  private ALStringField fax_number;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /** グループ名 */
  private ALStringField group_name;

  /**
   *
   *
   */
  @Override
  public void initField() {
    post_id = new ALNumberField();
    company_id = new ALNumberField();
    post_name = new ALStringField();
    zipcode = new ALStringField();
    address = new ALStringField();
    in_telephone = new ALStringField();
    out_telephone = new ALStringField();
    fax_number = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
    group_name = new ALStringField();
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
  public ALNumberField getCompany_id() {
    return company_id;
  }

  /**
   * @return
   */
  public ALStringField getCreate_date() {
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
  public ALNumberField getPostId() {
    return post_id;
  }

  /**
   * @return
   */
  public ALStringField getPostName() {
    return post_name;
  }

  /**
   * 電話番号（外線）を取得します。
   * 
   * @return
   */
  public ALStringField getOutTelephone() {
    return out_telephone;
  }

  /**
   * 電話番号（内線）を取得します。
   * 
   * @return
   */
  public ALStringField getInTelephone() {
    return in_telephone;
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
  public ALStringField getZipcode() {
    return zipcode;
  }

  /**
   * @param string
   */
  public void setAddress(String string) {
    address.setValue(string);
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
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setFaxNumber(String string) {
    fax_number.setValue(string);
  }

  /**
   * @param id
   */
  public void setPostId(int id) {
    post_id.setValue(id);
  }

  /**
   * @param string
   */
  public void setPostName(String string) {
    post_name.setValue(string);
  }

  /**
   * @param string
   */
  public void setOutTelephone(String string) {
    out_telephone.setValue(string);
  }

  /**
   * @param string
   */
  public void setInTelephone(String string) {
    in_telephone.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setZipcode(String string) {
    zipcode.setValue(string);
  }

  /**
   * グループ名を取得します
   * 
   * @return
   */
  public ALStringField getGroupName() {
    return group_name;
  }

  /**
   * グループ名をセットします
   * 
   * @param string
   */
  public void setGroupName(String string) {
    group_name.setValue(string);
  }
}
