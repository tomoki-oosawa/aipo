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
package com.aimluck.eip.addressbook;

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.addressbookuser.beans.AddressBookUserGroupLiteBean;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳のリザルトデータクラスです。
 */
public class AddressBookResultData implements ALData {

  /** アドレスID */
  private ALNumberField address_id;

  /** 名前 */
  private ALStringField name;

  /** 名前（フリガナ） */
  private ALStringField name_kana;

  /** メールアドレス */
  private ALStringField email;

  /** 電話番号（外線） */
  private ALStringField telephone;

  /** 電話番号（内線） */
  private ALStringField in_telephone;

  /** 電話番号（携帯） */
  private ALStringField cellular_phone;

  /** 携帯メールアドレス */
  private ALStringField cellular_mail;

  /** 会社名 */
  private ALStringField company_name;

  /** 会社ID */
  private ALStringField company_id;

  /** 部署名 */
  private ALStringField post_name;

  /** 部署リスト */
  private List<AddressBookUserGroupLiteBean> post_list;

  /** 役職名 */
  private ALStringField position_name;

  /** 備考 */
  private ALStringField note;

  /** 公開フラグ */
  private ALStringField public_flag;

  /** 会社名（フリガナ） */
  private ALStringField company_name_kana;

  /** 会社郵便番号 */
  private ALStringField zipcode;

  /** 会社住所 */
  private ALStringField company_address;

  /** 会社部署電話番号(外線) */
  private ALStringField company_telephone;

  /** 会社部署電話番号(内線)社員時のみ使用 */
  private ALStringField post_in_telephone;

  /** 会社FAX番号 */
  private ALStringField company_fax_number;

  /** 会社URL */
  private ALStringField company_url;

  /** グループ名 */
  // private ALStringField group_name;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /** 登録者 */
  private ALStringField created_user;

  /** 登録者のID */
  private ALNumberField created_user_id;

  /** 更新者 */
  private ALStringField updated_user;

  /** 更新者のID */
  private ALNumberField updated_user_id;

  /** 索引 */
  private ALStringField index;

  /**
   *
   */
  @Override
  public void initField() {
    address_id = new ALNumberField();
    name = new ALStringField();
    name_kana = new ALStringField();
    email = new ALStringField();
    telephone = new ALStringField();
    in_telephone = new ALStringField();
    cellular_phone = new ALStringField();
    cellular_mail = new ALStringField();
    company_name = new ALStringField();
    company_id = new ALStringField();
    post_name = new ALStringField();
    post_list = new ArrayList<AddressBookUserGroupLiteBean>();
    position_name = new ALStringField();
    note = new ALStringField();
    public_flag = new ALStringField();
    company_name_kana = new ALStringField();
    zipcode = new ALStringField();
    company_address = new ALStringField();
    company_telephone = new ALStringField();
    post_in_telephone = new ALStringField();
    company_fax_number = new ALStringField();
    company_url = new ALStringField();
    // group_name = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
    created_user = new ALStringField();
    created_user_id = new ALNumberField();
    updated_user = new ALStringField();
    updated_user_id = new ALNumberField();
    index = new ALStringField();
  }

  /**
   * @param i
   */
  public void setAddressId(int i) {
    address_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getAddressId() {
    return address_id;
  }

  /**
   * @param string
   */
  public void setName(String string) {
    name.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getName() {
    return name;
  }

  /**
   * @param string
   */
  public void setNameKana(String string) {
    name_kana.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getNameKana() {
    return name_kana;
  }

  /**
   * @param string
   */
  public void setEmail(String string) {
    email.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getEmail() {
    return email;
  }

  /**
   * @param string
   */
  public void setTelephone(String string) {
    telephone.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getTelephone() {
    return telephone;
  }

  /**
   * @param string
   */
  public void setInTelephone(String string) {
    in_telephone.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getInTelephone() {
    return in_telephone;
  }

  /**
   * @param string
   */
  public void setCellularPhone(String string) {
    cellular_phone.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getCellularPhone() {
    return cellular_phone;
  }

  /**
   * @param string
   */
  public void setCellularMail(String string) {
    cellular_mail.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getCellularMail() {
    return cellular_mail;
  }

  /**
   * @param string
   */
  public void setCompanyName(String string) {
    company_name.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getCompanyName() {
    return company_name;
  }

  /**
   * @param string
   */
  public void setPostName(String string) {
    post_name.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getPostName() {
    return post_name;
  }

  /**
   * @param string
   */
  public void setPositionName(String string) {
    position_name.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getPositionName() {
    return position_name;
  }

  public void setNote(String string) {
    note.setValue(string);
  }

  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  public String getNoteRaw() {
    return note.getValue();
  }

  /**
   * 公開フラグを設定します。
   *
   * @param string
   */
  public void setPublicFlag(String string) {
    public_flag.setValue(string);
  }

  /**
   * 公開フラグを取得します。
   *
   * @return
   */
  public ALStringField getPublicFlag() {
    return public_flag;
  }

  public void setCompanyNameKana(String string) {
    company_name_kana.setValue(string);
  }

  public ALStringField getCompanyNameKana() {
    return company_name_kana;
  }

  public void setZipcode(String string) {
    zipcode.setValue(string);
  }

  public ALStringField getZipcode() {
    return zipcode;
  }

  public void setCompanyAddress(String string) {
    company_address.setValue(string);
  }

  public ALStringField getCompanyAddress() {
    return company_address;
  }

  public void setCompanyTelephone(String string) {
    company_telephone.setValue(string);
  }

  public ALStringField getCompanyTelephone() {
    return company_telephone;
  }

  public void setPostInTelephone(String string) {
    post_in_telephone.setValue(string);
  }

  public ALStringField getPostInTelephone() {
    return post_in_telephone;
  }

  /**
   * @param field
   */
  public void setCompanyFaxNumber(String string) {
    company_fax_number.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getCompanyFaxNumber() {
    return company_fax_number;
  }

  /**
   * @return
   */
  public ALStringField getCompanyUrl() {
    return company_url;
  }

  /**
   * @param field
   */
  public void setCompanyUrl(String string) {
    company_url.setValue(string);
  }

  /**
   * @param field
   */
  // public void setGroupName(String string) {
  // group_name.setValue(string);
  // }

  /**
   * @return
   */
  // public ALStringField getGroupName() {
  // return group_name;
  // }

  /**
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * @param field
   */
  public void setCreatedUser(String string) {
    created_user.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getCreatedUser() {
    return created_user;
  }

  /**
   * @param field
   */
  public void setCreatedUserId(Integer i) {
    created_user_id.setValue(i.longValue());
  }

  /**
   * @return
   */
  public ALNumberField getCreatedUserId() {
    return created_user_id;
  }

  /**
   * @return
   */
  public ALStringField getUpdatedUser() {
    return updated_user;
  }

  /**
   * @param field
   */
  public void setUpdatedUser(String string) {
    updated_user.setValue(string);
  }

  /**
   * @param field
   */
  public void setUpdatedUserId(Integer i) {
    updated_user_id.setValue(i.longValue());
  }

  /**
   * @return
   */
  public ALNumberField getUpdatedUserId() {
    return updated_user_id;
  }

  /**
   * @return
   */
  public ALStringField getIndex() {
    return index;
  }

  /**
   * @param field
   */
  public void setIndex(String string) {
    index.setValue(string);
  }

  public List<AddressBookUserGroupLiteBean> getPostList() {
    return post_list;
  }

  public void setPostList(List<AddressBookUserGroupLiteBean> list) {
    post_list.addAll(list);
  }

  public ALStringField getCompanyId() {
    return company_id;
  }

  public void setCompanyId(String id) {
    company_id.setValue(id);
  }
}
