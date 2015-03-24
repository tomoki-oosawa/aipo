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
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * アドレス帳用入力フォームデータです。
 * 
 */
public class AddressBookFormData extends ALAbstractFormData {
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookFormData.class.getName());

  // 所有グループのリスト
  private List<AddressBookGroupResultData> groupList;

  // このアドレスが登録されているグループ(グループオブジェクト格納)
  private List<Object> groups;

  // グループ名表示用フィールド(「、」区切りのグループ名)
  private ALStringField group_names;

  private ALStringField firstname;

  private ALStringField lastname;

  private ALStringField first_name_kana;

  private ALStringField last_name_kana;

  private ALStringField email;

  // 電話番号
  private ALStringField telephone1;

  private ALStringField telephone2;

  private ALStringField telephone3;

  // 携帯電話
  private ALStringField cellular_phone1;

  private ALStringField cellular_phone2;

  private ALStringField cellular_phone3;

  private ALStringField cellular_mail;

  private List<AddressBookCompanyResultData> companyList;

  private ALNumberField company_id;

  private ALStringField position_name;

  private ALStringField public_flag;

  private ALStringField note;

  private ALStringField create_user;

  private ALStringField update_user;

  private ALDateField create_date;

  private ALDateField update_date;

  // 会社情報
  private ALStringField company_name;

  private ALStringField company_name_kana;

  private ALStringField post_name;

  private ALStringField comp_zipcode1;

  private ALStringField comp_zipcode2;

  private ALStringField comp_address;

  private ALStringField comp_telephone1;

  private ALStringField comp_telephone2;

  private ALStringField comp_telephone3;

  private ALStringField comp_fax_number1;

  private ALStringField comp_fax_number2;

  private ALStringField comp_fax_number3;

  private ALStringField comp_url;

  private boolean is_new_company;

  private int user_id;

  private int owner_id;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    is_new_company = rundata.getParameters().getBoolean("is_new_company");

    user_id = ALEipUtils.getUserId(rundata);
  }

  @Override
  public void initField() {
    groups = new ArrayList<Object>();

    group_names = new ALStringField();

    lastname = new ALStringField();
    lastname.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_LASTNAME"));
    lastname.setTrim(true);
    firstname = new ALStringField();
    firstname.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_FIRSTNAME"));
    firstname.setTrim(true);
    last_name_kana = new ALStringField();
    last_name_kana.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_LASTNAME_KANA"));
    last_name_kana.setTrim(true);
    first_name_kana = new ALStringField();
    first_name_kana.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_FIRSTNAME_KANA"));
    first_name_kana.setTrim(true);
    email = new ALStringField();
    email.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_EMAIL"));
    email.setTrim(true);

    // 電話番号
    telephone1 = new ALStringField();
    telephone1.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_TELEPHONE"));
    telephone1.setTrim(true);
    telephone2 = new ALStringField();
    telephone2.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_TELEPHONE"));
    telephone2.setTrim(true);
    telephone3 = new ALStringField();
    telephone3.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_TELEPHONE"));
    telephone3.setTrim(true);

    // 携帯番号
    cellular_phone1 = new ALStringField();
    cellular_phone1.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_CELLULAR_PHONE"));
    cellular_phone1.setTrim(true);
    cellular_phone2 = new ALStringField();
    cellular_phone2.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_CELLULAR_PHONE"));
    cellular_phone2.setTrim(true);
    cellular_phone3 = new ALStringField();
    cellular_phone3.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_CELLULAR_PHONE"));
    cellular_phone3.setTrim(true);

    cellular_mail = new ALStringField();
    cellular_mail.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_CELLULAR_MAIL"));
    cellular_mail.setTrim(true);
    company_id = new ALNumberField();
    company_id.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_COMPANY"));
    position_name = new ALStringField();
    position_name.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_POSITION"));
    position_name.setTrim(true);

    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_NOTE"));
    note.setTrim(true);

    public_flag = new ALStringField();
    public_flag.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_PUBLIC"));
    public_flag.setTrim(true);
    create_user = new ALStringField();
    create_user.setFieldName(ALLocalizationUtils
      .getl10n("COMMON_CREATE_USER"));
    update_user = new ALStringField();
    update_user.setFieldName(ALLocalizationUtils.getl10n("COMMON_UPDATE_USER"));
    create_date = new ALDateField();
    create_date.setFieldName(ALLocalizationUtils.getl10n("COMMON_CREATE_DATE"));
    update_date = new ALDateField();
    update_date.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_LAST_UPDATE_DATE"));

    // 会社情報
    company_name = new ALStringField();
    company_name.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_COMPANY_NAME"));
    company_name.setTrim(true);
    company_name_kana = new ALStringField();
    company_name_kana.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_COMPANY_NAME_KANA"));
    company_name_kana.setTrim(true);
    post_name = new ALStringField();
    post_name.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_POST_NAME"));
    post_name.setTrim(true);
    comp_zipcode1 = new ALStringField();
    comp_zipcode1.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_ZIPCODE"));
    comp_zipcode1.setTrim(true);
    comp_zipcode2 = new ALStringField();
    comp_zipcode2.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_ZIPCODE"));
    comp_zipcode2.setTrim(true);
    comp_address = new ALStringField();
    comp_address.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_ADDRESS"));
    comp_address.setTrim(true);
    comp_telephone1 = new ALStringField();
    comp_telephone1.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_TELEPHONE"));
    comp_telephone1.setTrim(true);
    comp_telephone2 = new ALStringField();
    comp_telephone2.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_TELEPHONE"));
    comp_telephone2.setTrim(true);
    comp_telephone3 = new ALStringField();
    comp_telephone3.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_TELEPHONE"));
    comp_telephone3.setTrim(true);
    comp_fax_number1 = new ALStringField();
    comp_fax_number1.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_FAX_NUMBER"));
    comp_fax_number1.setTrim(true);
    comp_fax_number2 = new ALStringField();
    comp_fax_number2.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_FAX_NUMBER"));
    comp_fax_number2.setTrim(true);
    comp_fax_number3 = new ALStringField();
    comp_fax_number3.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_FAX_NUMBER"));
    comp_fax_number3.setTrim(true);
    comp_url = new ALStringField();
    comp_url.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_URL"));
    comp_url.setTrim(true);
  }

  /**
   * 自分がオーナーのグループを取得する。
   * 
   * @param rundata
   * @param context
   */
  public void loadGroupList(RunData rundata, Context context) {
    groupList = new ArrayList<AddressBookGroupResultData>();
    try {
      SelectQuery<EipMAddressGroup> query =
        Database.query(EipMAddressGroup.class);
      Expression exp =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp);

      List<EipMAddressGroup> aList = query.fetchList();
      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipMAddressGroup record = aList.get(i);
        AddressBookGroupResultData rd = new AddressBookGroupResultData();
        rd.initField();
        rd.setGroupId(record.getGroupId().intValue());
        rd.setGroupName(record.getGroupName());
        groupList.add(rd);
      }

    } catch (Exception ex) {
      logger.error("AddressBookFormData.loadGroupList", ex);
    }
  }

  /**
   * 指定アドレスのグループを取得する。
   * 
   * @param rundata
   * @param context
   */
  public void loadGroups(RunData rundata, Context context) {
    try {
      String addressid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

      if (addressid == null || "".equals(addressid)) {
        return;
      }

      SelectQuery<EipTAddressbookGroupMap> query =
        Database.query(EipTAddressbookGroupMap.class);
      Expression exp1 =
        ExpressionFactory.matchExp(
          EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY
            + "."
            + EipMAddressGroup.OWNER_ID_PROPERTY,
          Integer.valueOf(ALEipUtils.getUserId(rundata)));
      query.setQualifier(exp1);

      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTAddressbookGroupMap.ADDRESS_ID_PROPERTY,
          Integer.valueOf(addressid));
      query.andQualifier(exp2);

      query.distinct(true);

      List<EipTAddressbookGroupMap> aList = query.fetchList();
      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipTAddressbookGroupMap record = aList.get(i);
        AddressBookGroupResultData rd = new AddressBookGroupResultData();
        rd.initField();
        rd.setGroupId(record.getEipTAddressGroup().getGroupId().intValue());
        rd.setGroupName(record.getEipTAddressGroup().getGroupName());
        groups.add(rd);
      }
    } catch (Exception ex) {
      logger.error("AddressBookFormData.loadGroups", ex);
    }
  }

  public void loadCompanyList(RunData rundata, Context context) {
    companyList = new ArrayList<AddressBookCompanyResultData>();
    try {
      SelectQuery<EipMAddressbookCompany> query =
        Database.query(EipMAddressbookCompany.class);

      // exclude default company
      query.setQualifier(AddressBookUtils.excludeDefaultCompanyCriteria());
      query.orderAscending(EipMAddressbookCompany.COMPANY_NAME_KANA_PROPERTY);

      List<EipMAddressbookCompany> aList = query.fetchList();
      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipMAddressbookCompany record = aList.get(i);
        AddressBookCompanyResultData rd = new AddressBookCompanyResultData();
        rd.initField();
        rd.setCompanyId(record.getCompanyId().intValue());
        rd.setCompanyName(record.getCompanyName() + " " + record.getPostName());
        companyList.add(rd);
      }

    } catch (Exception ex) {
      logger.error("AddressBookFormData.loadCompanyList", ex);
    }
  }

  @Override
  protected void setValidator() {
    lastname.setNotNull(true);
    lastname.limitMaxLength(50);
    firstname.setNotNull(true);
    firstname.limitMaxLength(50);
    last_name_kana.setNotNull(true);
    last_name_kana.limitMaxLength(50);
    first_name_kana.setNotNull(true);
    first_name_kana.limitMaxLength(50);
    email.setCharacterType(ALStringField.TYPE_ASCII);
    email.limitMaxLength(50);
    // telephone.setCharacterType(ALStringField.TYPE_ASCII);
    // telephone.limitMaxLength(13);

    // 電話番号
    telephone1.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone1.limitMaxLength(5);
    telephone2.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone2.limitMaxLength(4);
    telephone3.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone3.limitMaxLength(4);

    // 携帯
    cellular_phone1.setCharacterType(ALStringField.TYPE_NUMBER);
    cellular_phone1.limitMaxLength(5);
    cellular_phone2.setCharacterType(ALStringField.TYPE_NUMBER);
    cellular_phone2.limitMaxLength(4);
    cellular_phone3.setCharacterType(ALStringField.TYPE_NUMBER);
    cellular_phone3.limitMaxLength(4);

    cellular_mail.setCharacterType(ALStringField.TYPE_ASCII);
    cellular_mail.limitMaxLength(50);
    position_name.limitMaxLength(50);

    note.limitMaxLength(1000);

    // 会社情報
    if (is_new_company) {
      // 会社名
      company_name.setNotNull(true);
      company_name.limitMaxLength(50);
      // 会社名カナ
      company_name_kana.setNotNull(true);
      company_name_kana.limitMaxLength(50);
      // 部署名
      post_name.limitMaxLength(50);
      // 会社郵便番号
      comp_zipcode1.setCharacterType(ALStringField.TYPE_NUMBER);
      comp_zipcode1.limitLength(3, 3);
      comp_zipcode2.setCharacterType(ALStringField.TYPE_NUMBER);
      comp_zipcode2.limitLength(4, 4);
      // 会社住所
      comp_address.limitMaxLength(50);
      // 会社電話番号
      comp_telephone1.setCharacterType(ALStringField.TYPE_NUMBER);
      comp_telephone1.limitMaxLength(5);
      comp_telephone2.setCharacterType(ALStringField.TYPE_NUMBER);
      comp_telephone2.limitMaxLength(4);
      comp_telephone3.setCharacterType(ALStringField.TYPE_NUMBER);
      comp_telephone3.limitMaxLength(4);
      // 会社FAX番号
      comp_fax_number1.setCharacterType(ALStringField.TYPE_NUMBER);
      comp_fax_number1.limitMaxLength(5);
      comp_fax_number2.setCharacterType(ALStringField.TYPE_NUMBER);
      comp_fax_number2.limitMaxLength(4);
      comp_fax_number3.setCharacterType(ALStringField.TYPE_NUMBER);
      comp_fax_number3.limitMaxLength(4);
      // 会社URL
      comp_url.setCharacterType(ALStringField.TYPE_ASCII);
      comp_url.limitMaxLength(90);
    }
  }

  @Override
  protected boolean validate(List<String> msgList) {
    List<String> dummy = new ArrayList<String>();
    lastname.validate(msgList);
    firstname.validate(msgList);

    // フリガナのカタカナへの変換
    last_name_kana.setValue(ALStringUtil.convertHiragana2Katakana(ALStringUtil
      .convertH2ZKana(last_name_kana.toString())));
    first_name_kana.setValue(ALStringUtil.convertHiragana2Katakana(ALStringUtil
      .convertH2ZKana(first_name_kana.toString())));
    last_name_kana.validate(msgList);
    first_name_kana.validate(msgList);

    // メールアドレス
    email.validate(msgList);
    if (email.getValue().trim().length() > 0
      && !ALStringUtil.isMailAddress(email.getValue())) {
      msgList.add(ALLocalizationUtils.getl10n("ADDRESSBOOK_ALERT_SET_EMAIL"));
    }

    // 電話
    if (!telephone1.getValue().equals("")
      || !telephone2.getValue().equals("")
      || !telephone3.getValue().equals("")) {
      if (!telephone1.validate(dummy)
        || !telephone2.validate(dummy)
        || !telephone3.validate(dummy)) {
        msgList.add(ALLocalizationUtils
          .getl10n("ADDRESSBOOK_ALERT_SET_TELEPHONE"));
      }
    }
    // 携帯電話
    if (!cellular_phone1.getValue().equals("")
      || !cellular_phone2.getValue().equals("")
      || !cellular_phone3.getValue().equals("")) {
      if (!cellular_phone1.validate(dummy)
        || !cellular_phone2.validate(dummy)
        || !cellular_phone3.validate(dummy)) {
        msgList.add(ALLocalizationUtils
          .getl10n("ADDRESSBOOK_ALERT_SET_CELLPHONE"));
      }
    }

    // 携帯メールアドレス
    cellular_mail.validate(msgList);
    if (cellular_mail.getValue().trim().length() > 0
      && !ALStringUtil.isCellPhoneMailAddress(cellular_mail.getValue())) {
      msgList
        .add(ALLocalizationUtils.getl10n("ADDRESSBOOK_ALERT_SET_CELLMAIL"));
    }

    note.validate(msgList);

    position_name.validate(msgList);

    // 会社情報入力時用
    if (is_new_company) {
      company_name.validate(msgList);
      // 会社名フリガナのカタカナへの変換
      company_name_kana.setValue(ALStringUtil
        .convertHiragana2Katakana(ALStringUtil.convertH2ZKana(company_name_kana
          .toString())));
      company_name_kana.validate(msgList);

      post_name.validate(msgList);
      // 会社郵便番号
      if (!comp_zipcode1.getValue().equals("")
        || !comp_zipcode2.getValue().equals("")) {
        if (!comp_zipcode1.validate(dummy) || !comp_zipcode2.validate(dummy)) {
          msgList.add(ALLocalizationUtils
            .getl10n("ADDRESSBOOK_ALERT_SET_CO_ZIPCODE"));
        }
      }
      comp_address.validate(msgList);
      // 会社電話番号
      if (!comp_telephone1.getValue().equals("")
        || !comp_telephone2.getValue().equals("")
        || !comp_telephone3.getValue().equals("")) {
        if (!comp_telephone1.validate(dummy)
          || !comp_telephone2.validate(dummy)
          || !comp_telephone3.validate(dummy)) {
          msgList.add(ALLocalizationUtils
            .getl10n("ADDRESSBOOK_ALERT_SET_CO_PHONE"));
        }
      }
      // 携帯電話
      if (!comp_fax_number1.getValue().equals("")
        || !comp_fax_number2.getValue().equals("")
        || !comp_fax_number3.getValue().equals("")) {
        if (!comp_fax_number1.validate(dummy)
          || !comp_fax_number2.validate(dummy)
          || !comp_fax_number3.validate(dummy)) {
          msgList.add(ALLocalizationUtils
            .getl10n("ADDRESSBOOK_ALERT_SET_CO_FAX"));
        }
      }
      comp_url.validate(msgList);
    }

    return msgList.size() == 0;
  }

  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMAddressbook address =
        AddressBookUtils.getEipMAddressbook(rundata, context);
      if (address == null) {
        return false;
      }

      // 登録ユーザ名の設定
      ALEipUser createdUser =
        ALEipUtils.getALEipUser(address.getCreateUserId().intValue());
      String createdUserName = createdUser.getAliasName().getValue();
      create_user.setValue(createdUserName);

      // 更新ユーザ名の設定
      String updatedUserName;
      if (address.getCreateUserId().equals(address.getUpdateUserId())) {
        updatedUserName = createdUserName;
      } else {
        ALEipUser updatedUser =
          ALEipUtils.getALEipUser(address.getUpdateUserId().intValue());
        updatedUserName = updatedUser.getAliasName().getValue();
      }
      update_user.setValue(updatedUserName);

      group_names.setValue(AddressBookUtils.getMyGroupNamesAsString(
        rundata,
        address.getAddressId().intValue(),
        ALEipUtils.getUserId(rundata)));

      EipMAddressbookCompany company = address.getEipMAddressbookCompany();
      if (company.getCompanyId().intValue() > 0) {
        company_id.setValue(company.getCompanyId().intValue());
      }

      firstname.setValue(address.getFirstName());
      first_name_kana.setValue(address.getFirstNameKana());
      lastname.setValue(address.getLastName());
      last_name_kana.setValue(address.getLastNameKana());

      email.setValue(address.getEmail());
      // telephone.setValue(address.getTelephone());

      // 電話番号
      StringTokenizer token;
      if (address.getTelephone() != null) {
        token = new StringTokenizer(address.getTelephone(), "-");
        if (token.countTokens() == 3) {
          telephone1.setValue(token.nextToken());
          telephone2.setValue(token.nextToken());
          telephone3.setValue(token.nextToken());
        }
      }

      // 電話番号（携帯）
      if (address.getCellularPhone() != null) {
        token = new StringTokenizer(address.getCellularPhone(), "-");
        if (token.countTokens() == 3) {
          cellular_phone1.setValue(token.nextToken());
          cellular_phone2.setValue(token.nextToken());
          cellular_phone3.setValue(token.nextToken());
        }
      }

      cellular_mail.setValue(address.getCellularMail());
      position_name.setValue(address.getPositionName());
      note.setValue(address.getNote());
      public_flag.setValue(address.getPublicFlag());

      create_date.setValue(address.getCreateDate());
      // create_user.setValue(address.getCreateUserId()e)
      update_date.setValue(address.getUpdateDate());

      owner_id = address.getOwnerId();
    } catch (Exception ex) {
      logger.error("AddressBookFormData.loadFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * アドレス情報の登録を行います。
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    // 作業ユーザIDの取得
    int uid = ALEipUtils.getUserId(rundata);
    try {
      if (is_new_company) {
        // 会社情報の登録処理
        if (!insertCompanyData(rundata, context)) {
        }
      }

      // アドレス情報の登録処理
      EipMAddressbook address = Database.create(EipMAddressbook.class);
      // 個人情報の設定
      address.setLastName(lastname.getValue());
      address.setFirstName(firstname.getValue());
      address.setLastNameKana(last_name_kana.getValue());
      address.setFirstNameKana(first_name_kana.getValue());
      address.setEmail(email.getValue());

      // 電話
      if (!telephone1.getValue().equals("")
        && !telephone2.getValue().equals("")
        && !telephone3.getValue().equals("")) {
        address.setTelephone(new StringBuffer()
          .append(telephone1.getValue())
          .append("-")
          .append(telephone2.getValue())
          .append("-")
          .append(telephone3.getValue())
          .toString());
      } else {
        address.setTelephone("");
      }

      // 携帯電話
      if (!cellular_phone1.getValue().equals("")
        && !cellular_phone2.getValue().equals("")
        && !cellular_phone3.getValue().equals("")) {
        address.setCellularPhone(new StringBuffer().append(
          cellular_phone1.getValue()).append("-").append(
          cellular_phone2.getValue()).append("-").append(
          cellular_phone3.getValue()).toString());
      } else {
        address.setCellularPhone("");
      }

      address.setCellularMail(cellular_mail.getValue());
      address.setPositionName(position_name.getValue());

      // メモの設定
      address.setNote(note.getValue());

      // 会社の設定
      boolean hasCompany = false;
      if (company_id.getValue() > 0) {
        EipMAddressbookCompany company =
          Database.get(EipMAddressbookCompany.class, Integer
            .valueOf((int) company_id.getValue()));
        if (company.getCompanyId().intValue() > 0) {
          // CompanyID が存在する場合
          address.setEipMAddressbookCompany(company);
          hasCompany = true;
        }
      }
      if (!hasCompany) {
        // 会社名でソートできるように、ダミーの会社情報を設定する。
        EipMAddressbookCompany company =
          AddressBookUtils.getDummyEipMAddressbookCompany(rundata, context);
        address.setEipMAddressbookCompany(company);
      }

      // 公開区分の設定
      address.setPublicFlag(public_flag.getValue());

      // オーナIDの設定
      address.setOwnerId(Integer.valueOf(uid));
      address.setCreateUserId(Integer.valueOf(uid));
      address.setUpdateUserId(Integer.valueOf(uid));
      Date now = new Date();
      address.setCreateDate(now);
      address.setUpdateDate(now);

      Database.commit();

      // Address-Groupマッピングテーブルへのデータ追加
      Integer id = address.getAddressId();

      for (int i = 0; i < groups.size(); i++) {
        EipTAddressbookGroupMap map =
          Database.create(EipTAddressbookGroupMap.class);
        map.setEipMAddressbook(Database.get(EipMAddressbook.class, id));
        map.setEipTAddressGroup((EipMAddressGroup) groups.get(i));
      }

      Database.commit();

      String name =
        new StringBuffer().append(lastname.getValue()).append(" ").append(
          firstname.getValue()).toString();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        address.getAddressId(),
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK,
        name);

      return true;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookFormData.insertFormData", ex);
      return false;
    }
  }

  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      String addressid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      if (addressid == null || Integer.valueOf(addressid) == null) {
        logger.debug("[AddressBook] Cannot find Address ID .");
        return false;
      }

      EipMAddressbook addressbook =
        Database.get(EipMAddressbook.class, Integer.valueOf(addressid));

      // entityIdの取得
      int entityId = addressbook.getAddressId();
      // 名前の取得
      String name =
        new StringBuffer()
          .append(addressbook.getLastName())
          .append(" ")
          .append(addressbook.getFirstName())
          .toString();

      Database.delete(addressbook);

      SelectQuery<EipTAddressbookGroupMap> query =
        Database.query(EipTAddressbookGroupMap.class);
      Expression exp =
        ExpressionFactory.matchExp(
          EipTAddressbookGroupMap.ADDRESS_ID_PROPERTY,
          Integer.valueOf(addressid));
      query.setQualifier(exp);

      List<EipTAddressbookGroupMap> maps = query.fetchList();
      Database.deleteAll(maps);

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK,
        name);
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookFormData.deleteFormData", ex);
      return false;
    }
    return true;
  }

  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 作業ユーザIDの取得
      int uid = ALEipUtils.getUserId(rundata);
      if (is_new_company) {
        if (!insertCompanyData(rundata, context)) {
        }
      }
      // 会社情報の登録処理終了

      // オブジェクトモデルを取得
      EipMAddressbook address =
        AddressBookUtils.getEipMAddressbook(rundata, context);
      if (address == null) {
        return false;
      }

      address.setLastName(lastname.getValue());
      address.setFirstName(firstname.getValue());
      address.setLastNameKana(last_name_kana.getValue());
      address.setFirstNameKana(first_name_kana.getValue());
      address.setEmail(email.getValue());
      // address.setTelephone(telephone.getValue());

      // 電話番号(外線)
      if (!telephone1.getValue().equals("")
        && !telephone2.getValue().equals("")
        && !telephone3.getValue().equals("")) {
        address.setTelephone(new StringBuffer()
          .append(telephone1.getValue())
          .append("-")
          .append(telephone2.getValue())
          .append("-")
          .append(telephone3.getValue())
          .toString());
      } else {
        address.setTelephone("");
      }

      // 携帯電話
      if (!cellular_phone1.getValue().equals("")
        && !cellular_phone2.getValue().equals("")
        && !cellular_phone3.getValue().equals("")) {
        address.setCellularPhone(new StringBuffer().append(
          cellular_phone1.getValue()).append("-").append(
          cellular_phone2.getValue()).append("-").append(
          cellular_phone3.getValue()).toString());
      } else {
        address.setCellularPhone("");
      }

      address.setCellularMail(cellular_mail.getValue());

      // 会社の設定
      boolean hasCompany = false;
      if (company_id.getValue() > 0) {
        EipMAddressbookCompany company =
          Database.get(EipMAddressbookCompany.class, Integer
            .valueOf((int) company_id.getValue()));
        if (company.getCompanyId().intValue() > 0) {
          // CompanyID が存在する場合
          address.setEipMAddressbookCompany(company);
          hasCompany = true;
        }
      }
      if (!hasCompany) {
        // 会社名でソートできるように、ダミーの会社情報を設定する。
        EipMAddressbookCompany company =
          AddressBookUtils.getDummyEipMAddressbookCompany(rundata, context);
        address.setEipMAddressbookCompany(company);
      }

      address.setPositionName(position_name.getValue());
      address.setNote(note.getValue());

      if (user_id == address.getOwnerId()) {
        address.setPublicFlag(public_flag.getValue());
      }

      address.setUpdateUserId(Integer.valueOf(uid));
      address.setUpdateDate(new Date());

      // Address-Groupマッピングテーブルへのデータ追加

      String addressid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

      // 所属グループの全取得
      SelectQuery<EipMAddressGroup> query1 =
        Database.query(EipMAddressGroup.class);
      Expression exp1 =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(uid));
      query1.setQualifier(exp1);

      List<EipMAddressGroup> list = query1.fetchList();
      int listsize = list.size();
      Integer[] groupIds = new Integer[listsize];
      for (int i = 0; i < listsize; i++) {
        groupIds[i] = list.get(i).getGroupId();
      }

      // Address-Group Mapテーブル情報を一旦削除
      if (listsize != 0) {
        SelectQuery<EipTAddressbookGroupMap> query2 =
          Database.query(EipTAddressbookGroupMap.class);
        Expression exp2 = ExpressionFactory.inDbExp("group_id", groupIds);
        query2.setQualifier(exp2);
        Expression exp3 =
          ExpressionFactory.matchExp(
            EipTAddressbookGroupMap.ADDRESS_ID_PROPERTY,
            addressid);
        query2.setQualifier(exp3);

        List<EipTAddressbookGroupMap> maps = query2.fetchList();
        Database.deleteAll(maps);
      }

      // Address-Group Mapテーブルへ指定された全グループを登録。
      int groupsize = groups.size();
      for (int i = 0; i < groupsize; i++) {
        EipTAddressbookGroupMap map =
          Database.create(EipTAddressbookGroupMap.class);
        map.setEipMAddressbook(Database.get(EipMAddressbook.class, Integer
          .valueOf(addressid)));
        map.setEipTAddressGroup((EipMAddressGroup) groups.get(i));
      }

      Database.commit();

      String name =
        new StringBuffer().append(lastname.getValue()).append(" ").append(
          firstname.getValue()).toString();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        address.getAddressId(),
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK,
        name);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookFormData.updateFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * フォームへデータをセットします。
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    groups = new ArrayList<Object>();
    if (res) {
      try {
        String str[] = rundata.getParameters().getStrings("group_to");
        if (str == null) {
          return res;
        }
        if (isEmpty(str)) {
          return res;
        }

        SelectQuery<EipMAddressGroup> query =
          Database.query(EipMAddressGroup.class);
        Expression exp =
          ExpressionFactory.inDbExp(EipMAddressGroup.GROUP_ID_PK_COLUMN, str);
        query.setQualifier(exp);

        List<EipMAddressGroup> list = query.fetchList();
        int size = list.size();
        for (int i = 0; i < size; i++) {
          EipMAddressGroup group = list.get(i);
          groups.add(group);
        }
      } catch (Exception ex) {
        logger.error("AddressBookFormData.setFormData", ex);
      }
    }
    return res;
  }

  private boolean isEmpty(String str[]) {
    boolean res = true;
    for (int i = 0; i < str.length; i++) {
      if (str[i] != null && !"".equals(str[i])) {
        res = false;
      }
    }
    return res;
  }

  public ALStringField getCellularMail() {
    return cellular_mail;
  }

  public ALStringField getCellularPhone1() {
    return cellular_phone1;
  }

  public ALStringField getCellularPhone2() {
    return cellular_phone2;
  }

  public ALStringField getCellularPhone3() {
    return cellular_phone3;
  }

  public ALNumberField getCompanyId() {
    return company_id;
  }

  public ALStringField getEmail() {
    return email;
  }

  public ALStringField getFirstNameKana() {
    return first_name_kana;
  }

  public ALStringField getFirstName() {
    return firstname;
  }

  public ALStringField getLastNameKana() {
    return last_name_kana;
  }

  public ALStringField getLastName() {
    return lastname;
  }

  public ALStringField getNote() {
    return note;
  }

  public ALStringField getPositionName() {
    return position_name;
  }

  public ALStringField getPublicFlag() {
    return public_flag;
  }

  // public ALStringField getTelephone() {
  // return telephone;
  // }

  public ALStringField getTelephone1() {
    return telephone1;
  }

  public ALStringField getTelephone2() {
    return telephone2;
  }

  public ALStringField getTelephone3() {
    return telephone3;
  }

  public void setCellularMail(ALStringField field) {
    cellular_mail = field;
  }

  // public void setCellularPhone(ALStringField field) {
  // cellular_phone = field;
  // }

  public void setCompanyId(ALNumberField field) {
    company_id = field;
  }

  public void setEmail(ALStringField field) {
    email = field;
  }

  public void setFirstNameKana(ALStringField field) {
    first_name_kana = field;
  }

  public void setFirstName(ALStringField field) {
    firstname = field;
  }

  public void setLastNameKana(ALStringField field) {
    last_name_kana = field;
  }

  public void setLastName(ALStringField field) {
    lastname = field;
  }

  public void setNote(ALStringField field) {
    note = field;
  }

  public void setPositionName(ALStringField field) {
    position_name = field;
  }

  public void setPublicFlag(ALStringField field) {
    public_flag = field;
  }

  // public void setTelephone(ALStringField field) {
  // telephone = field;
  // }

  public ALDateField getCreateDate() {
    return create_date;
  }

  public ALDateField getUpdateDate() {
    return update_date;
  }

  public void setCreateDate(ALDateField field) {
    create_date = field;
  }

  public ALStringField getCreateUser() {
    return create_user;
  }

  public void setUpdateDate(ALDateField field) {
    update_date = field;
  }

  public ALStringField getUpdateUser() {
    return update_user;
  }

  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
  }

  public List<AddressBookCompanyResultData> getCompanyList() {
    return companyList;
  }

  /**
   * @return
   */
  public ALStringField getGroupNames() {
    return group_names;
  }

  /**
   * @param field
   */
  public void setGroupNames(ALStringField field) {
    group_names = field;
  }

  public List<Object> getGroups() {
    return groups;
  }

  // 会社情報 getter
  public ALStringField getCompanyName() {
    return company_name;
  }

  public ALStringField getCompanyNameKana() {
    return company_name_kana;
  }

  public ALStringField getPostName() {
    return post_name;
  }

  public ALStringField getCompZipcode1() {
    return comp_zipcode1;
  }

  public ALStringField getCompZipcode2() {
    return comp_zipcode2;
  }

  public ALStringField getCompAddress() {
    return comp_address;
  }

  public ALStringField getCompTelephone1() {
    return comp_telephone1;
  }

  public ALStringField getCompTelephone2() {
    return comp_telephone2;
  }

  public ALStringField getCompTelephone3() {
    return comp_telephone3;
  }

  public ALStringField getCompFaxNumber1() {
    return comp_fax_number1;
  }

  public ALStringField getCompFaxNumber2() {
    return comp_fax_number2;
  }

  public ALStringField getCompFaxNumber3() {
    return comp_fax_number3;
  }

  public ALStringField getCompUrl() {
    return comp_url;
  }

  /**
   * 会社情報一括登録を行うかを示す値を返す。
   * 
   * @return
   */
  public boolean isNewCompany() {
    return is_new_company;
  }

  /**
   * 現在ログイン中のユーザIDを返します。
   * 
   * @return
   */
  public int getUserId() {
    return user_id;
  }

  /**
   * オーナーIDを返します。
   * 
   * @return
   */
  public int getOwnerId() {
    return owner_id;
  }

  /**
   * 会社情報を登録します。
   */
  private boolean insertCompanyData(RunData rundata, Context context) {
    int uid = ALEipUtils.getUserId(rundata);
    try {
      EipMAddressbookCompany company =
        Database.create(EipMAddressbookCompany.class);
      rundata.getParameters().setProperties(company);
      company.setCompanyName(company_name.getValue());
      company.setCompanyNameKana(company_name_kana.getValue());
      company.setPostName(post_name.getValue());

      // 郵便番号の設定
      if (!comp_zipcode1.getValue().equals("")
        && !comp_zipcode2.getValue().equals("")) {
        company.setZipcode(new StringBuffer()
          .append(comp_zipcode1.getValue())
          .append("-")
          .append(comp_zipcode2.getValue())
          .toString());
      } else {
        company.setZipcode("");
      }

      company.setAddress(comp_address.getValue());

      // 電話番号の設定
      if (!comp_telephone1.getValue().equals("")
        && !comp_telephone2.getValue().equals("")
        && !comp_telephone3.getValue().equals("")) {
        company.setTelephone(new StringBuffer().append(
          comp_telephone1.getValue()).append("-").append(
          comp_telephone2.getValue()).append("-").append(
          comp_telephone3.getValue()).toString());
      } else {
        company.setTelephone("");
      }

      // FAX番号の設定
      if (!comp_fax_number1.getValue().equals("")
        && !comp_fax_number2.getValue().equals("")
        && !comp_fax_number3.getValue().equals("")) {
        company.setFaxNumber(new StringBuffer().append(
          comp_fax_number1.getValue()).append("-").append(
          comp_fax_number2.getValue()).append("-").append(
          comp_fax_number3.getValue()).toString());
      } else {
        company.setFaxNumber("");
      }

      company.setUrl(comp_url.getValue());
      company.setCreateUserId(Integer.valueOf(uid));
      company.setUpdateUserId(Integer.valueOf(uid));

      Date now = new Date();
      company.setCreateDate(now);
      company.setUpdateDate(now);

      Database.commit();

      // 会社IDの設定
      company_id.setValue(company.getCompanyId().longValue());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookFormData.insertCompanyData", ex);
      return false;
    }
    return true;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_OUTSIDE;
  }

  /**
   * アクセス権限をチェックします。
   * 
   * @return
   */
  @Override
  protected boolean doCheckAclPermission(RunData rundata, Context context,
      int defineAclType) throws ALPermissionException {
    boolean tmp = super.doCheckAclPermission(rundata, context, defineAclType);

    // 詳細表示、追加、削除は一覧表示の権限が必要
    if (defineAclType == ALAccessControlConstants.VALUE_ACL_DETAIL
      || defineAclType == ALAccessControlConstants.VALUE_ACL_INSERT
      || defineAclType == ALAccessControlConstants.VALUE_ACL_DELETE) {
      super.doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_LIST);
      hasAuthority = (hasAuthority && tmp);
    }

    return true;
  }
}
