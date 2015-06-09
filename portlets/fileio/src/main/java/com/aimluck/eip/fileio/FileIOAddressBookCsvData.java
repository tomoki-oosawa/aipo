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
package com.aimluck.eip.fileio;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.addressbook.AddressBookResultData;

public class FileIOAddressBookCsvData extends AddressBookResultData {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAddressBookCsvData.class.getName());

  /** データのCSVファイル上での位置(行数) */
  private int line_count;

  /** 同じ会社名がデータベースに存在するかどうか */
  private boolean same_company;

  /** エラー発生かどうか */
  private boolean is_error;

  /** 電話番号（外線） */
  private ALStringField telephone;

  /** 電話番号（携帯） */
  private ALStringField cellular_phone;

  /** メールアドレス */
  private ALStringField email;

  /** 携帯メールアドレス */
  private ALStringField cellular_mail;

  /** 会社郵便番号 */
  private ALStringField zipcode;

  /** 会社住所 */
  private ALStringField company_address;

  /** 会社部署電話番号(外線) */
  private ALStringField company_telephone;

  /** 会社FAX番号 */
  private ALStringField company_fax_number;

  /** 会社URL */
  private FileIOStringField company_url;

  /**
   * フィールドの初期化
   */
  @Override
  public void initField() {
    line_count = 0;
    same_company = false;
    is_error = false;

    telephone = new ALStringField();
    cellular_phone = new ALStringField();
    email = new ALStringField();
    cellular_mail = new ALStringField();

    zipcode = new ALStringField();
    company_address = new ALStringField();
    company_telephone = new ALStringField();
    company_fax_number = new ALStringField();
    company_url = new FileIOStringField();

    super.initField();
  }

  /**
   * データのCSVファイル上での位置(行数)を取得します <BR>
   * 
   * @param i
   */
  public int getLineCount() {
    return line_count;
  }

  /**
   * 同じ会社名がデータベースに存在するかどうかを取得します <BR>
   * 
   * @return
   */
  public boolean getSameCompany() {
    return same_company;
  }

  public boolean getIsError() {
    return is_error;
  }

  /**
   * データのCSVファイル上での位置(行数)を入力します <BR>
   * 
   * @param i
   */
  public void setLineCount(int i) {
    line_count = i;
  }

  /**
   * 同じ会社名がデータベースに存在するかどうかを入力します <BR>
   * 
   * @param flg
   */
  public void setSameCompany(boolean flg) {
    same_company = flg;
  }

  public void setIsError(boolean flg) {
    is_error = flg;
  }

  /**
   * @param field
   */
  public void setTelephone(ALStringField string) {
    telephone = string;
  }

  /**
   * @return
   */
  @Override
  public ALStringField getTelephone() {
    return telephone;
  }

  /**
   * @param field
   */
  public void setCellularPhone(ALStringField string) {
    cellular_phone = string;
  }

  /**
   * @return
   */
  @Override
  public ALStringField getCellularPhone() {
    return cellular_phone;
  }

  /**
   * @param field
   */
  public void setEmail(ALStringField string) {
    email = string;
  }

  /**
   * @return
   */
  @Override
  public ALStringField getEmail() {
    return email;
  }

  /**
   * @param field
   */
  public void setCellularMail(ALStringField string) {
    cellular_mail = string;
  }

  /**
   * @return
   */
  @Override
  public ALStringField getCellularMail() {
    return cellular_mail;
  }

  /**
   * @param field
   */
  public void setZipcode(ALStringField string) {
    zipcode = string;
  }

  /**
   * @return
   */
  @Override
  public ALStringField getZipcode() {
    return zipcode;
  }

  /**
   * @param field
   */
  public void setCompanyAddress(ALStringField string) {
    company_address = string;
  }

  /**
   * @return
   */
  @Override
  public ALStringField getCompanyAddress() {
    return company_address;
  }

  /**
   * @param field
   */
  public void setCompanyTelephone(ALStringField string) {
    company_telephone = string;
  }

  /**
   * @return
   */
  @Override
  public ALStringField getCompanyTelephone() {
    return company_telephone;
  }

  /**
   * @param field
   */
  public void setCompanyFaxNumber(ALStringField string) {
    company_fax_number = string;
  }

  /**
   * @return
   */
  @Override
  public ALStringField getCompanyFaxNumber() {
    return company_fax_number;
  }

  /**
   * @param field
   */
  public void setCompanyUrl(FileIOStringField string) {
    company_url = string;
  }

  /**
   * @return
   */
  @Override
  public FileIOStringField getCompanyUrl() {
    return company_url;
  }
}
