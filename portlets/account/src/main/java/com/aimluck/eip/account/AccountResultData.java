/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * ユーザーアカウントのResultDataです。 <BR>
 *
 */
public class AccountResultData implements ALData {

  /** ユーザーID */
  private ALNumberField user_id;

  /** 有効/無効 */
  private ALStringField disabled;

  /** ユーザー名 */
  private ALStringField user_name;

  /** 名前 */
  private ALStringField name;

  /** フリガナ（名前） */
  private ALStringField name_kana;

  /** メールアドレス */
  private ALStringField email;

  /** 電話番号（外線） */
  private ALStringField out_telephone;

  /** 電話番号（内線） */
  private ALStringField in_telephone;

  /** 電話番号（携帯） */
  private ALStringField cellular_phone;

  /** 携帯メールアドレス */
  private ALStringField cellular_mail;

  /** 部署名 */
  private List<String> post_name_list;

  /** 役職名 */
  private ALStringField position_name;

  private boolean has_photo;

  private boolean is_admin;

  /**
   *
   *
   */
  @Override
  public void initField() {
    user_id = new ALNumberField();
    user_name = new ALStringField();
    name = new ALStringField();
    name_kana = new ALStringField();
    email = new ALStringField();
    out_telephone = new ALStringField();
    in_telephone = new ALStringField();
    cellular_phone = new ALStringField();
    cellular_mail = new ALStringField();
    post_name_list = new ArrayList<String>();
    position_name = new ALStringField();
    disabled = new ALStringField();

    has_photo = false;
    is_admin = false;
  }

  /**
   * @return
   */
  public ALStringField getName() {
    return name;
  }

  /**
   * @return
   */
  public ALStringField getUserName() {
    return user_name;
  }

  /**
   * @return
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * @return
   */
  public ALStringField getDisabled() {
    return disabled;
  }

  /**
   * @param string
   */
  public void setName(String string) {
    name.setValue(string);
  }

  /**
   * @param string
   */
  public void setUserName(String string) {
    user_name.setValue(string);
  }

  /**
   * @param i
   */
  public void setUserId(int i) {
    user_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setDisabled(String string) {
    disabled.setValue(string);
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
  public List<String> getPostNameList() {
    return post_name_list;
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
  public void setPostNameList(List<String> list) {
    post_name_list.addAll(list);
  }

  /**
   * @return
   */
  public ALStringField getCellularMail() {
    return cellular_mail;
  }

  /**
   * @return
   */
  public ALStringField getCellularPhone() {
    return cellular_phone;
  }

  /**
   * @return
   */
  public ALStringField getEmail() {
    return email;
  }

  /**
   * @return
   */
  public ALStringField getInTelephone() {
    return in_telephone;
  }

  /**
   * @return
   */
  public ALStringField getOutTelephone() {
    return out_telephone;
  }

  /**
   * @param string
   */
  public void setCellularMail(String string) {
    cellular_mail.setValue(string);
  }

  /**
   * @param string
   */
  public void setCellularPhone(String string) {
    cellular_phone.setValue(string);
  }

  /**
   * @param string
   */
  public void setEmail(String string) {
    email.setValue(string);
  }

  /**
   * @param string
   */
  public void setInTelephone(String string) {
    in_telephone.setValue(string);
  }

  /**
   * @param field
   */
  public void setOutTelephone(String string) {
    out_telephone.setValue(string);
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
  public void setNameKana(String string) {
    name_kana.setValue(string);
  }

  public boolean hasPhoto() {
    return has_photo;
  }

  public void setHasPhoto(boolean bool) {
    has_photo = bool;
  }

  /**
   * @return is_admin
   */
  public boolean isAdmin() {
    return is_admin;
  }

  /**
   * @param is_admin
   */
  public void setIsAdmin(boolean is_admin) {
    this.is_admin = is_admin;
  }

}
