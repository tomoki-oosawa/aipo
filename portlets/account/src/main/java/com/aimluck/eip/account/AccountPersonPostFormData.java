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

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アカウントデータと　部署を表示するためのフォームデータです。
 *
 */
public class AccountPersonPostFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPersonPostFormData.class.getName());

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

  /** 会社名 */
  private ALStringField company_name;

  /** 郵便番号 */
  private ALStringField company_zipcode;

  /** 住所 */
  private ALStringField company_address;

  /** 電話番号 */
  private ALStringField company_telephone;

  /** FAX番号 */
  private ALStringField company_fax_number;

  /** 部署名 */
  private ALStringField post_name;

  /** 郵便番号 */
  private ALStringField post_zipcode;

  /** 住所 */
  private ALStringField post_address;

  /** 電話番号(外線) */
  private ALStringField post_out_telephone;

  /** 電話番号(内線) */
  private ALStringField post_in_telephone;

  /** FAX番号 */
  private ALStringField post_fax_number;

  /** 所属メンバー */
  private List<ALEipUser> memberList;

  /** 所属する部署IDリスト */
  private List<Integer> post_id_list;

  /**
   *
   */
  @Override
  protected void setValidator() {

  }

  /**
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {
    return false;
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      int uid = ALEipUtils.getUserId(rundata);

      // 会社のオブジェクトモデルを取得
      TurbineUser tuser = Database.get(TurbineUser.class, Integer.valueOf(uid));
      EipMCompany companyRecord =
        Database.get(EipMCompany.class, tuser.getCompanyId());

      if (companyRecord == null) {
        return false;
      }

      // 会社名
      company_name.setValue(companyRecord.getCompanyName());
      // 郵便番号
      company_zipcode.setValue(companyRecord.getZipcode());
      // 住所
      company_address.setValue(companyRecord.getAddress());
      // 電話番号
      company_telephone.setValue(companyRecord.getTelephone());
      // FAX番号
      company_fax_number.setValue(companyRecord.getFaxNumber());

      // ユーザーが所属する部署リスト

      int id = ALEipUtils.getUserId(rundata);
      SelectQuery<EipMPost> query =
        Database.query(EipMPost.class).orderAscending(EipMPost.SORT_PROPERTY);
      List<EipMPost> list = query.fetchList();
      List<Integer> idlist = null;
      EipMPost mpost = null;
      for (int n = 0; n < list.size(); n++) {
        mpost = list.get(n);
        idlist = ALEipUtils.getUserIds(mpost.getGroupName());
        if (idlist.contains(id)) {
          post_name_list.add(mpost.getPostName());
          post_id_list.add(mpost.getPostId());
        }
      }
    } catch (Exception ex) {
      logger.error("AccountPersonPostFormData.loadFormData", ex);
      return false;
    }
    return true;

  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   *
   */
  @Override
  public void initField() {
    // ユーザーID
    user_id = new ALNumberField();

    // ユーザー名
    user_name = new ALStringField();
    user_name.setFieldName("ユーザー名");
    // 名前
    name = new ALStringField();
    name.setFieldName("名前");
    // カナ
    name_kana = new ALStringField();
    name_kana.setFieldName("カナ");
    // メールアドレス
    email = new ALStringField();
    email.setFieldName("メールアドレス");
    // 外線
    out_telephone = new ALStringField();
    out_telephone.setFieldName("電話番号(外線)");
    // 内線
    in_telephone = new ALStringField();
    in_telephone.setFieldName("電話番号(内線)");
    // 携帯
    cellular_phone = new ALStringField();
    cellular_phone.setFieldName("電話番号(携帯)");
    // 携帯メール
    cellular_mail = new ALStringField();
    cellular_mail.setFieldName("携帯メールアドレス");
    // 部署のリスト
    post_name_list = new ArrayList<String>();
    // 役職のリスト
    position_name = new ALStringField();
    disabled = new ALStringField();
    // 顔写真
    has_photo = false;
    // 管理者権限
    is_admin = false;

    // 会社名
    company_name = new ALStringField();
    company_name.setFieldName("会社名");
    company_name.setTrim(true);
    // 郵便番号
    company_zipcode = new ALStringField();
    company_zipcode.setFieldName("郵便番号");
    company_zipcode.setTrim(true);
    // 住所
    company_address = new ALStringField();
    company_address.setFieldName("住所");
    company_address.setTrim(true);
    // 電話番号
    company_telephone = new ALStringField();
    company_telephone.setFieldName("電話番号");
    company_telephone.setTrim(true);
    // FAX番号
    company_fax_number = new ALStringField();
    company_fax_number.setFieldName("FAX番号");
    company_fax_number.setTrim(true);

    // 部署名
    post_name = new ALStringField();
    post_name.setFieldName("部署名");
    post_name.setTrim(true);

    // 郵便番号
    post_zipcode = new ALStringField();
    post_zipcode.setFieldName("郵便番号");
    post_zipcode.setTrim(true);

    // 住所
    post_address = new ALStringField();
    post_address.setFieldName("住所");
    post_address.setTrim(true);

    // 電話番号(外線)
    post_out_telephone = new ALStringField();
    post_out_telephone.setFieldName("電話番号（外線）");
    post_out_telephone.setTrim(true);

    // 電話番号(内線)
    post_in_telephone = new ALStringField();
    post_in_telephone.setFieldName("電話番号（内線）");
    post_in_telephone.setTrim(true);

    // FAX番号
    post_fax_number = new ALStringField();
    post_fax_number.setFieldName("FAX番号");
    post_fax_number.setTrim(true);

    memberList = new ArrayList<ALEipUser>();
    post_name_list = new ArrayList<String>();
    post_id_list = new ArrayList<Integer>();
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

  /**
   * 住所を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getCompanyAddress() {
    return company_address;
  }

  /**
   * 会社名を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getCompanyName() {
    return company_name;
  }

  /**
   * FAX番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getCompanyFaxNumber() {
    return company_fax_number;
  }

  /**
   * 電話番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getCompanyTelephone() {
    return company_telephone;
  }

  /**
   * 郵便番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getCompanyZipcode() {
    return company_zipcode;
  }

  /**
   * 部署名を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getPostName() {
    return post_name;
  }

  /**
   * 住所を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getPostAddress() {
    return post_address;
  }

  /**
   * FAX番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getPostFaxNumber() {
    return post_fax_number;
  }

  /**
   * 電話番号（外線）を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getPostOutTelephone() {
    return post_out_telephone;
  }

  /**
   * 電話番号（内線）を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getPostInTelephone() {
    return post_in_telephone;
  }

  /**
   * 郵便番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getPostZipcode() {
    return post_zipcode;
  }

  /**
   * 所属メンバーを取得します。 <BR>
   *
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  /**
   * 部署IDを取得します。 <BR>
   *
   * @return
   */
  public Object getPostID(int i) {
    return post_id_list.get(i);
  }
}
