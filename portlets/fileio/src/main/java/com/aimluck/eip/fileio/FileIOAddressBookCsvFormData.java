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
import com.aimluck.eip.addressbook.AddressBookCompanyResultData;
import com.aimluck.eip.addressbook.AddressBookGroupResultData;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * アドレス帳用入力フォームデータです。
 * 
 */
public class FileIOAddressBookCsvFormData extends ALAbstractFormData {
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAddressBookCsvFormData.class.getName());

  // 所有グループのリスト
  private List<AddressBookGroupResultData> groupList;

  // このアドレスが登録されているグループ(グループオブジェクト格納)
  private List<AddressBookGroupResultData> groups;

  private List<EipMAddressGroup> groupModelList;

  // グループ名表示用フィールド(「、」区切りのグループ名)
  private ALStringField group_names;

  private ALStringField firstname;

  private ALStringField lastname;

  private ALStringField first_name_kana;

  private ALStringField last_name_kana;

  private FileIOStringField email;

  // 電話番号
  private ALStringField telephone1;

  private ALStringField telephone2;

  private ALStringField telephone3;

  // 携帯電話
  private ALStringField cellular_phone1;

  private ALStringField cellular_phone2;

  private ALStringField cellular_phone3;

  private FileIOStringField cellular_mail;

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

  private FileIOStringField post_name;

  private ALStringField comp_zipcode1;

  private ALStringField comp_zipcode2;

  private FileIOStringField comp_address;

  private ALStringField comp_telephone1;

  private ALStringField comp_telephone2;

  private ALStringField comp_telephone3;

  private ALStringField comp_fax_number1;

  private ALStringField comp_fax_number2;

  private ALStringField comp_fax_number3;

  private FileIOStringField comp_url;

  /** 番号関連をそれぞれ一つにまとめたもの */
  private FileIOStringField telephone_full;

  private FileIOStringField cellular_phone_full;

  private FileIOStringField comp_zipcode_full;

  private FileIOStringField comp_telephone_full;

  private FileIOStringField comp_fax_number_full;

  /** ユーザー名 */
  private ALStringField username;

  private boolean same_company;

  /**/
  private boolean is_company_only;

  /** 会社情報のみの入力か否か */
  private boolean is_new_company;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    is_new_company = rundata.getParameters().getBoolean("is_new_company");

  }

  @Override
  public void initField() {
    groups = new ArrayList<AddressBookGroupResultData>();
    groupModelList = new ArrayList<EipMAddressGroup>();

    group_names = new ALStringField();

    lastname = new ALStringField();
    lastname.setFieldName(ALLocalizationUtils.getl10n("FILEIO_FIRST_NAME"));
    lastname.setTrim(true);
    firstname = new ALStringField();
    firstname.setFieldName(ALLocalizationUtils.getl10n("FILEIO_LAST_NAME"));
    firstname.setTrim(true);
    last_name_kana = new ALStringField();
    last_name_kana.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_FIRST_NAME_SPELL"));
    last_name_kana.setTrim(true);
    first_name_kana = new ALStringField();
    first_name_kana.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_LAST_NAME_SPELL"));
    first_name_kana.setTrim(true);
    email = new FileIOStringField();
    email.setFieldName(ALLocalizationUtils.getl10n("FILEIO_MAILADDRESS"));
    email.setTrim(true);

    // 電話番号
    telephone1 = new ALStringField();
    telephone1.setFieldName(ALLocalizationUtils.getl10n("FILEIO_PHONE_NUMBER"));
    telephone1.setTrim(true);
    telephone2 = new ALStringField();
    telephone2.setFieldName(ALLocalizationUtils.getl10n("FILEIO_PHONE_NUMBER"));
    telephone2.setTrim(true);
    telephone3 = new ALStringField();
    telephone3.setFieldName(ALLocalizationUtils.getl10n("FILEIO_PHONE_NUMBER"));
    telephone3.setTrim(true);

    // 携帯番号
    cellular_phone1 = new ALStringField();
    cellular_phone1.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_MOBILE_PHONE"));
    cellular_phone1.setTrim(true);
    cellular_phone2 = new ALStringField();
    cellular_phone2.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_MOBILE_PHONE"));
    cellular_phone2.setTrim(true);
    cellular_phone3 = new ALStringField();
    cellular_phone3.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_MOBILE_PHONE"));
    cellular_phone3.setTrim(true);

    cellular_mail = new FileIOStringField();
    cellular_mail.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_MOBILE_PHONE_ADDRESS"));
    cellular_mail.setTrim(true);
    company_id = new ALNumberField();
    company_id.setFieldName(ALLocalizationUtils.getl10n("FILEIO_COMPANY"));
    position_name = new ALStringField();
    position_name.setFieldName(ALLocalizationUtils.getl10n("FILEIO_POST"));
    position_name.setTrim(true);
    public_flag = new ALStringField();
    public_flag.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_OPEN_DIVISION"));
    public_flag.setTrim(true);
    create_user = new ALStringField();
    create_user.setFieldName(ALLocalizationUtils.getl10n("COMMON_CREATE_USER"));
    update_user = new ALStringField();
    update_user.setFieldName(ALLocalizationUtils.getl10n("COMMON_UPDATE_USER"));
    create_date = new ALDateField();
    create_date.setFieldName(ALLocalizationUtils.getl10n("COMMON_CREATE_DATE"));
    update_date = new ALDateField();
    update_date.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_LAST_MODIFIED"));

    // 会社情報
    company_name = new ALStringField();
    company_name.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_COMPANY_NAME"));
    company_name.setTrim(true);
    company_name_kana = new ALStringField();
    company_name_kana.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_COMPANU_NAME_SPELL"));
    company_name_kana.setTrim(true);
    post_name = new FileIOStringField();
    post_name.setFieldName(ALLocalizationUtils.getl10n("FILIIO_UNIT_NAME"));
    post_name.setTrim(true);
    comp_zipcode1 = new ALStringField();
    comp_zipcode1.setFieldName(ALLocalizationUtils.getl10n("FILEIO_POST_CODE"));
    comp_zipcode1.setTrim(true);
    comp_zipcode2 = new ALStringField();
    comp_zipcode2.setFieldName(ALLocalizationUtils.getl10n("FILEIO_POST_CODE"));
    comp_zipcode2.setTrim(true);
    comp_address = new FileIOStringField();
    comp_address.setFieldName(ALLocalizationUtils.getl10n("FILEIO_ADDRESS"));
    comp_address.setTrim(true);
    comp_telephone1 = new ALStringField();
    comp_telephone1.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_PHONE_NUMBER"));
    comp_telephone1.setTrim(true);
    comp_telephone2 = new ALStringField();
    comp_telephone2.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_PHONE_NUMBER"));
    comp_telephone2.setTrim(true);
    comp_telephone3 = new ALStringField();
    comp_telephone3.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_PHONE_NUMBER"));
    comp_telephone3.setTrim(true);
    comp_fax_number1 = new ALStringField();
    comp_fax_number1.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_FAX_NUMBER"));
    comp_fax_number1.setTrim(true);
    comp_fax_number2 = new ALStringField();
    comp_fax_number2.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_FAX_NUMBER"));
    comp_fax_number2.setTrim(true);
    comp_fax_number3 = new ALStringField();
    comp_fax_number3.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_FAX_NUMBER"));
    comp_fax_number3.setTrim(true);
    comp_url = new FileIOStringField();
    comp_url.setFieldName(ALLocalizationUtils.getl10n("FILEIO_URL"));
    comp_url.setTrim(true);

    telephone_full = new FileIOStringField();
    telephone_full.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_PHONE_NUMBER"));
    telephone_full.setTrim(true);
    cellular_phone_full = new FileIOStringField();
    cellular_phone_full.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_MOBILE_PHONE"));
    cellular_phone_full.setTrim(true);
    comp_zipcode_full = new FileIOStringField();
    comp_zipcode_full.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_POST_CODE"));
    comp_zipcode_full.setTrim(true);
    comp_telephone_full = new FileIOStringField();
    comp_telephone_full.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_PHONE_NUMBER"));
    comp_telephone_full.setTrim(true);
    comp_fax_number_full = new FileIOStringField();
    comp_fax_number_full.setFieldName(ALLocalizationUtils
      .getl10n("FILEIO_FAX_NUMBER"));
    comp_fax_number_full.setTrim(true);

    // ユーザー名
    username = new ALStringField();
    username.setFieldName(ALLocalizationUtils.getl10n("FILEIO_USER_NAME"));
    username.setTrim(true);

    public_flag.setValue("T");
    this.setTelephone("");
    this.setCellularPhone("");
    email.setValue("");
    cellular_mail.setValue("");
    company_name.setValue("");
    company_name_kana.setValue("");
    setCompZipcode("");
    comp_address.setValue("");
    comp_telephone_full.setValue("");
    post_name.setValue("");
    position_name.setValue("");

    telephone1.setValue("");
    telephone2.setValue("");
    telephone3.setValue("");
    telephone_full.setValue("");
    comp_zipcode1.setValue("");
    comp_zipcode2.setValue("");
    comp_zipcode_full.setValue("");
    cellular_phone1.setValue("");
    cellular_phone2.setValue("");
    cellular_phone3.setValue("");
    cellular_phone_full.setValue("");
    comp_telephone1.setValue("");
    comp_telephone2.setValue("");
    comp_telephone3.setValue("");
    comp_telephone_full.setValue("");
    comp_fax_number1.setValue("");
    comp_fax_number2.setValue("");
    comp_fax_number3.setValue("");
    comp_fax_number_full.setValue("");

    // 備考
    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_SETFIELDNAME_NOTE"));
    note.setTrim(true);
    note.setValue("");

    setSameCompany(false);
    setIsCompanyOnly(false);
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
      logger.error("fileio", ex);
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
      logger.error("fileio", ex);
    }
  }

  public void loadCompanyList(RunData rundata, Context context) {
    companyList = new ArrayList<AddressBookCompanyResultData>();
    try {
      SelectQuery<EipMAddressbookCompany> query =
        Database.query(EipMAddressbookCompany.class);
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
      logger.error("fileio", ex);
    }
  }

  @Override
  protected void setValidator() {
    if (!is_company_only) {
      lastname.setNotNull(true);
    }
    lastname.limitMaxLength(50);
    if (!is_company_only) {
      firstname.setNotNull(true);
    }
    firstname.limitMaxLength(50);
    if (!is_company_only) {
      last_name_kana.setNotNull(true);
    }
    last_name_kana.limitMaxLength(50);
    if (!is_company_only) {
      first_name_kana.setNotNull(true);
    }
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

    // 会社名
    if (is_company_only) {
      company_name.setNotNull(true);
    }
    company_name.limitMaxLength(50);
    // 会社名カナ
    if (is_company_only) {
      company_name_kana.setNotNull(true);
    }
    company_name.limitMaxLength(50);
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

    // ユーザーID
    username.limitMaxLength(16);

    telephone_full.setCharacterType(ALStringField.TYPE_ASCII);
    telephone_full.limitMaxLength(15);
    cellular_phone_full.setCharacterType(ALStringField.TYPE_ASCII);
    cellular_phone_full.limitMaxLength(15);
    comp_telephone_full.setCharacterType(ALStringField.TYPE_ASCII);
    comp_telephone_full.limitMaxLength(15);
    comp_fax_number_full.setCharacterType(ALStringField.TYPE_ASCII);
    comp_fax_number_full.limitMaxLength(15);

    telephone1.setNotNull(true);
    telephone2.setNotNull(true);
    telephone3.setNotNull(true);
    comp_zipcode1.setNotNull(true);
    comp_zipcode2.setNotNull(true);
    cellular_phone1.setNotNull(true);
    cellular_phone2.setNotNull(true);
    cellular_phone3.setNotNull(true);
    comp_telephone1.setNotNull(true);
    comp_telephone2.setNotNull(true);
    comp_telephone3.setNotNull(true);
    comp_fax_number1.setNotNull(true);
    comp_fax_number2.setNotNull(true);
    comp_fax_number3.setNotNull(true);

    // 備考
    note.limitMaxLength(1000);
  }

  @Override
  protected boolean validate(List<String> msgList) {
    List<String> dummy = new ArrayList<String>();
    if (!lastname.validate(msgList)) {
      firstname.setValue("");
      lastname.setValue("");
    }
    if (!firstname.validate(msgList)) {
      firstname.setValue("");
      lastname.setValue("");
    }

    // フリガナのカタカナへの変換
    last_name_kana.setValue(ALStringUtil.convertHiragana2Katakana(ALStringUtil
      .convertH2ZKana(last_name_kana.toString())));
    first_name_kana.setValue(ALStringUtil.convertHiragana2Katakana(ALStringUtil
      .convertH2ZKana(first_name_kana.toString())));
    if (!last_name_kana.validate(msgList)) {
      first_name_kana.setValue("");
      last_name_kana.setValue("");
    }
    if (!first_name_kana.validate(msgList)) {
      first_name_kana.setValue("");
      last_name_kana.setValue("");
    }

    if (email.getValue().trim().length() > 0
      && !ALStringUtil.isMailAddress(email.getValue())) {
      msgList.add(ALLocalizationUtils.getl10n("FILEIO_MAILADDRESS_CAUTION"));
      email.setValidate(false);
      email.setValue(null);
    }

    // 電話
    if (!telephone1.getValue().equals("")
      || !telephone2.getValue().equals("")
      || !telephone3.getValue().equals("")) {
      if (!telephone1.validate(dummy)
        || !telephone2.validate(dummy)
        || !telephone3.validate(dummy)) {
        msgList.add(ALLocalizationUtils.getl10n("FILEIO_PHONE_NUMBER_CAUTION"));
        telephone_full.setValidate(false);
        telephone_full.setValue(null);
      } else {
        telephone_full.setValue(new StringBuffer()
          .append(telephone1.getValue())
          .append("-")
          .append(telephone2.getValue())
          .append("-")
          .append(telephone3.getValue())
          .toString());
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
          .getl10n("FILEIO_CELLPHONE_NUMBER_CAUTION2"));
        cellular_phone_full.setValidate(true);
        cellular_phone_full.setValue(null);
      } else {
        cellular_phone_full.setValue(new StringBuffer().append(
          cellular_phone1.getValue()).append("-").append(
          cellular_phone2.getValue()).append("-").append(
          cellular_phone3.getValue()).toString());
      }
    }

    if (cellular_mail.getValue().trim().length() > 0
      && !ALStringUtil.isCellPhoneMailAddress(cellular_mail.getValue())) {
      msgList.add(ALLocalizationUtils
        .getl10n("FILEIO_CELLPHONE_MAILADDRESS_CAUTION"));
      cellular_mail.setValidate(false);
      cellular_mail.setValue(null);
    }

    if (!position_name.validate(msgList)) {
      position_name.setValue(null);
    }

    if (!note.validate(msgList)) {
      note.setValue(null);
    }

    // 会社情報入力時用
    // if (is_new_company) {
    if (!company_name.toString().equals("")) {
      EipMAddressbookCompany ecompany = getEipMCompany();
      if (ecompany != null) {
        company_id.setValue(ecompany.getCompanyId());
        if ((!company_name_kana.validate(msgList))
          || (company_name_kana.toString().equals(""))) {
          company_name_kana.setValue(ecompany.getCompanyNameKana());
        }
        if (is_company_only) {
          setSameCompany(true);
        }
      } else {
        if (!company_name.validate(msgList)) {
          company_name.setValue(null);
          msgList.add(ALLocalizationUtils
            .getl10n("FILEIO_COMPANY_NAME_CAUTION"));
        }
        // 会社名フリガナのカタカナへの変換
        company_name_kana.setValue(ALStringUtil
          .convertHiragana2Katakana(ALStringUtil
            .convertH2ZKana(company_name_kana.toString())));
        if (!company_name_kana.validate(dummy)) {
          company_name_kana.setValue(null);
          msgList.add(ALLocalizationUtils
            .getl10n("FILEIO_COMPANY_NAME_SPELL_CAUTION"));
        }

        if ((company_name.getValue() != null && company_name_kana.getValue() != null)) {
          if (!company_name.getValue().equals("")
            && company_name_kana.getValue().equals("")) {
            msgList.add(ALLocalizationUtils
              .getl10n("FILEIO_COMPANY_NAME_SPELL_CAUTION"));
          }
        }

        if (!post_name.validate(msgList)) {
          post_name.setValue(null);
          post_name.setValidate(false);
          msgList
            .add(ALLocalizationUtils.getl10n("FILEIO_POSTAL_CODE_CAUTION"));
        }

        if (!comp_zipcode1.getValue().equals("")
          || !comp_zipcode2.getValue().equals("")) {
          if (!comp_zipcode1.validate(dummy) || !comp_zipcode2.validate(dummy)) {
            msgList.add(ALLocalizationUtils
              .getl10n("FILEIO_POSTAL_CODE_CAUTION"));
            comp_zipcode_full.setValidate(false);
            comp_zipcode_full.setValue(null);
          } else {
            comp_zipcode_full.setValue(new StringBuffer().append(
              comp_zipcode1.getValue()).append("-").append(
              comp_zipcode2.getValue()).toString());
          }
        }

        if (!comp_address.validate(msgList)) {
          comp_address.setValidate(false);
          comp_address.setValue(null);
          msgList.add(ALLocalizationUtils.getl10n("FILEIO_ADDRESS_CAUTION"));
        }

        if (!comp_telephone1.getValue().equals("")
          || !comp_telephone2.getValue().equals("")
          || !comp_telephone3.getValue().equals("")) {
          if (!comp_telephone1.validate(dummy)
            || !comp_telephone2.validate(dummy)
            || !comp_telephone3.validate(dummy)) {
            msgList.add(ALLocalizationUtils
              .getl10n("FILEIO_PHONE_NUMBER_CAUTION"));
            comp_telephone_full.setValidate(false);
            comp_telephone_full.setValue(null);
          } else {
            comp_telephone_full.setValue(new StringBuffer().append(
              comp_telephone1.getValue()).append("-").append(
              comp_telephone2.getValue()).append("-").append(
              comp_telephone3.getValue()).toString());
          }
        }

        if (!comp_fax_number1.getValue().equals("")
          || !comp_fax_number2.getValue().equals("")
          || !comp_fax_number3.getValue().equals("")) {
          if (!comp_fax_number1.validate(dummy)
            || !comp_fax_number2.validate(dummy)
            || !comp_fax_number3.validate(dummy)) {
            msgList.add(ALLocalizationUtils
              .getl10n("FILEIO_FAX_NUMBER_CAUTION"));
            comp_fax_number_full.setValidate(false);
            comp_fax_number_full.setValue(null);
          } else {
            comp_fax_number_full.setValue(new StringBuffer().append(
              comp_fax_number1.getValue()).append("-").append(
              comp_fax_number2.getValue()).append("-").append(
              comp_fax_number3.getValue()).toString());
          }
        }

        if (!comp_url.validate(msgList)) {
          comp_url.setValidate(false);
          comp_url.setValue(null);
          msgList.add(ALLocalizationUtils.getl10n("FILEIO_URL_CAUTION"));
        }
      }
    }
    if (getTurbineUser() == null) {
      setUserName("");
    }

    return (msgList.size() == 0);
  }

  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    // try {
    // // オブジェクトモデルを取得
    // EipMAddressbook address = AddressbookUtils.getEipMAddressbook(rundata,
    // context);
    // if (address == null)
    // return false;
    //
    // // 登録ユーザ名の設定
    // ALEipUser createdUser = ALEipUtils.getALEipUser(address.getCreateUserId()
    // .intValue());
    // String createdUserName = createdUser.getAliasName().getValue();
    // create_user.setValue(createdUserName);
    //
    // // 更新ユーザ名の設定
    // String updatedUserName;
    // if (address.getCreateUserId() == address.getUpdateUserId()) {
    // updatedUserName = createdUserName;
    // } else {
    // ALEipUser updatedUser = ALEipUtils.getALEipUser(address
    // .getUpdateUserId().intValue());
    // updatedUserName = updatedUser.getAliasName().getValue();
    // }
    // update_user.setValue(updatedUserName);
    //
    // group_names.setValue(AddressbookUtils.getMyGroupNamesAsString(rundata,
    // address.getAddressId().intValue(), ALEipUtils.getUserId(rundata)));
    // company_id.setValue(address.getCompanyId().longValue());
    //
    // firstname.setValue(address.getFirstName());
    // first_name_kana.setValue(address.getFirstNameKana());
    // lastname.setValue(address.getLastName());
    // last_name_kana.setValue(address.getLastNameKana());
    //
    // email.setValue(address.getEmail());
    // // telephone.setValue(address.getTelephone());
    //
    // // 電話番号
    // StringTokenizer token;
    // if (address.getTelephone() != null) {
    // token = new StringTokenizer(address.getTelephone(), "-");
    // if (token.countTokens() == 3) {
    // telephone1.setValue(token.nextToken());
    // telephone2.setValue(token.nextToken());
    // telephone3.setValue(token.nextToken());
    // }
    // }
    //
    // // 電話番号（携帯）
    // if (address.getCellularPhone() != null) {
    // token = new StringTokenizer(address.getCellularPhone(), "-");
    // if (token.countTokens() == 3) {
    // cellular_phone1.setValue(token.nextToken());
    // cellular_phone2.setValue(token.nextToken());
    // cellular_phone3.setValue(token.nextToken());
    // }
    // }
    //
    // cellular_mail.setValue(address.getCellularMail());
    // position_name.setValue(address.getPositionName());
    // public_flag.setValue(address.getPublicFlag());
    //
    // create_date.setValue(address.getCreateDate());
    // // create_user.setValue(address.getCreateUserId()e)
    // update_date.setValue(address.getUpdateDate());
    //
    // } catch (Exception ex) {
    // logger.error("fileio", ex);
    // return false;
    // }
    // return true;
    return false;
  }

  /**
   * アドレス情報の登録を行います。
   * 
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    // 作業ユーザIDの取得
    ALEipUser user;
    int uid;
    boolean fullname, fullnamekana;
    try {
      user = ALEipUtils.getALEipUser(username.getValue());
      if (user != null) {
        uid = (int) user.getUserId().getValue();
      } else {
        uid = 1;
      }
    } catch (ALDBErrorException e) {
      // logger.error(e);
      // return false;
      uid = 1;
      user = null;
    }
    // int uid = ALEipUtils.getUserId(rundata);

    try {
      EipMAddressbookCompany ecompany;
      if (!company_name.toString().equals("")) {
        // 会社情報の登録処理
        ecompany = getEipMCompany();
        if (ecompany == null) {
          if (!insertCompanyData(rundata, context)) {
          }
        } else {
          if (!company_name_kana.toString().equals("")) {
            company_id.setValue(ecompany.getCompanyId());
          }
        }
      } else {
        company_id.setValue(1);
      }
      if ((getFirstName().toString().equals(""))
        && (getLastName().toString().equals(""))) {
        fullname = false;
      } else {
        fullname = true;
      }

      if ((getFirstNameKana().toString().equals(""))
        && (getLastNameKana().toString().equals(""))) {
        fullnamekana = false;
      } else {
        fullnamekana = true;
      }
      if (!((fullname) && (fullnamekana))) {
        return true;
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
      address.setTelephone(telephone_full.getValue());

      // 携帯電話
      address.setCellularPhone(cellular_phone_full.getValue());

      address.setCellularMail(cellular_mail.getValue());
      address.setPositionName(position_name.getValue());

      // 会社の設定
      if (company_id.getValue() > 0) {
        EipMAddressbookCompany company =
          Database.get(EipMAddressbookCompany.class, Integer
            .valueOf((int) company_id.getValue()));
        if (company.getCompanyId().intValue() > 0) {
          // CompanyID が存在する場合
          address.setEipMAddressbookCompany(company);
        }
      }
      // 公開区分の設定
      address.setPublicFlag(public_flag.getValue());

      // 備考
      address.setNote(note.getValue());

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

      for (int i = 0; i < groupModelList.size(); i++) {
        EipTAddressbookGroupMap map =
          Database.create(EipTAddressbookGroupMap.class);
        map.setAddressId(id);
        map.setEipTAddressGroup(groupModelList.get(i));
      }

      Database.commit();
      return true;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("fileio", ex);
      return false;
    }
  }

  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {

    return false;
  }

  /**
   * フォームへデータをセットします。
   * 
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    groupModelList = new ArrayList<EipMAddressGroup>();
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
          groupModelList.add(group);
        }
      } catch (Exception ex) {
        logger.error("fileio", ex);
      }
    }
    return res;
  }

  /**
   * 文字列の配列が全て空白の場合にtrueを返します <BR>
   * 
   * @param str
   * @return
   */
  private boolean isEmpty(String str[]) {
    boolean res = true;
    for (int i = 0; i < str.length; i++) {
      if (str[i] != null && !"".equals(str[i])) {
        res = false;
      }
    }
    return res;
  }

  /**
   * 携帯メールアドレスを取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getCellularMail() {
    return cellular_mail;
  }

  /**
   * 携帯電話番号を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCellularPhone1() {
    return cellular_phone1;
  }

  public ALStringField getCellularPhone2() {
    return cellular_phone2;
  }

  public ALStringField getCellularPhone3() {
    return cellular_phone3;
  }

  /**
   * 会社IDを取得します <BR>
   * 
   * @return
   */
  public ALNumberField getCompanyId() {
    return company_id;
  }

  /**
   * メールアドレスを取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getEmail() {
    return email;
  }

  /**
   * フリガナ(名)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getFirstNameKana() {
    return first_name_kana;
  }

  /**
   * 名前(名)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getFirstName() {
    return firstname;
  }

  /**
   * フリガナ(氏)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getLastNameKana() {
    return last_name_kana;
  }

  /**
   * 名前(氏)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getLastName() {
    return lastname;
  }

  /**
   * 備考を取得します <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  /**
   * 部署名を取得します <BR>
   * 
   * @return
   */
  public ALStringField getPositionName() {
    return position_name;
  }

  /**
   * 公開フラグを取得します <BR>
   * 
   * @return
   */
  public ALStringField getPublicFlag() {
    return public_flag;
  }

  /**
   * 電話番号(フィールド1)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getTelephone1() {
    return telephone1;
  }

  /**
   * 電話番号(フィールド2)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getTelephone2() {
    return telephone2;
  }

  /**
   * 電話番号(フィールド3)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getTelephone3() {
    return telephone3;
  }

  /**
   * 入力日時を取得します <BR>
   * 
   * @return
   */
  public ALDateField getCreateDate() {
    return create_date;
  }

  /**
   * 更新日時を取得します <BR>
   * 
   * @return
   */
  public ALDateField getUpdateDate() {
    return update_date;
  }

  /**
   * 入力ユーザー名を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCreateUser() {
    return create_user;
  }

  /**
   * 所有グループのリストを取得します <BR>
   * 
   * @return
   */
  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
  }

  /**
   * 会社リストを取得します <BR>
   * 
   * @return
   */
  public List<AddressBookCompanyResultData> getCompanyList() {
    return companyList;
  }

  /**
   * 更新ユーザー名を取得します <BR>
   * 
   * @return
   */
  public ALStringField getUpdateUser() {
    return update_user;
  }

  /**
   * グループオブジェクトを取得します <BR>
   * 
   * @return
   */
  public List<AddressBookGroupResultData> getGroups() {
    return groups;
  }

  /**
   * @return
   */
  public ALStringField getGroupNames() {
    return group_names;
  }

  /**
   * 携帯電話アドレスを入力します <BR>
   * 
   * @param field
   */
  public void setCellularMail(FileIOStringField field) {
    cellular_mail = field;
  }

  /**
   * 会社IDを入力します <BR>
   * 
   * @param field
   */
  public void setCompanyId(ALNumberField field) {
    company_id = field;
  }

  /**
   * メールアドレスを入力します <BR>
   * 
   * @param field
   */
  public void setEmail(FileIOStringField field) {
    email = field;
  }

  /**
   * フリガナ(名)を入力します <BR>
   * 
   * @param field
   */
  public void setFirstNameKana(ALStringField field) {
    first_name_kana = field;
  }

  /**
   * 名前(名)を入力します <BR>
   * 
   * @param field
   */
  public void setFirstName(ALStringField field) {
    firstname = field;
  }

  /**
   * フリガナ(氏)を入力します <BR>
   * 
   * @param field
   */
  public void setLastNameKana(ALStringField field) {
    last_name_kana = field;
  }

  /**
   * 名前(氏)を入力します <BR>
   * 
   * @param field
   */
  public void setLastName(ALStringField field) {
    lastname = field;
  }

  /**
   * 備考を入力します <BR>
   * 
   * @param field
   */
  public void setNote(ALStringField field) {
    note = field;
  }

  /**
   * 部署名を入力します <BR>
   * 
   * @param field
   */
  public void setPositionName(ALStringField field) {
    position_name = field;
  }

  /**
   * 公開フラグを入力します <BR>
   * 
   * @param field
   */
  public void setPublicFlag(ALStringField field) {
    public_flag = field;
  }

  /**
   * 入力日時を入力します <BR>
   * 
   * @param field
   */
  public void setCreateDate(ALDateField field) {
    create_date = field;
  }

  /**
   * 更新日時を入力します <BR>
   * 
   * @param field
   */
  public void setUpdateDate(ALDateField field) {
    update_date = field;
  }

  /**
   * @param field
   */
  public void setGroupNames(ALStringField field) {
    group_names = field;
  }

  /** 会社情報 */

  /**
   * 会社名を取得します <BR>
   */
  public ALStringField getCompanyName() {
    return company_name;
  }

  /**
   * フリガナ(会社名)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompanyNameKana() {
    return company_name_kana;
  }

  /**
   * 部署名を取得します <BR>
   * 
   * @return
   */
  public ALStringField getPostName() {
    return post_name;
  }

  /**
   * 郵便番号(フィールド1)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompZipcode1() {
    return comp_zipcode1;
  }

  /**
   * 郵便番号(フィールド2)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompZipcode2() {
    return comp_zipcode2;
  }

  /**
   * 会社住所を取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getCompAddress() {
    return comp_address;
  }

  /**
   * 会社電話番号(フィールド1)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompTelephone1() {
    return comp_telephone1;
  }

  /**
   * 会社電話番号(フィールド2)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompTelephone2() {
    return comp_telephone2;
  }

  /**
   * 会社電話番号(フィールド3)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompTelephone3() {
    return comp_telephone3;
  }

  /**
   * 会社FAX番号(フィールド1)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompFaxNumber1() {
    return comp_fax_number1;
  }

  /**
   * 会社FAX番号(フィールド2)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompFaxNumber2() {
    return comp_fax_number2;
  }

  /**
   * 会社FAX番号(フィールド3)を取得します <BR>
   * 
   * @return
   */
  public ALStringField getCompFaxNumber3() {
    return comp_fax_number3;
  }

  /**
   * 会社URLを取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getCompUrl() {
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

  // ***************************************************************************
  // privateメソッド
  // ***************************************************************************

  /**
   * 会社情報を登録します。
   */
  private boolean insertCompanyData(RunData rundata, Context context) {
    ALEipUser user;
    int uid;
    try {
      user = ALEipUtils.getALEipUser(username.getValue());
      if (user != null) {
        uid = (int) user.getUserId().getValue();
      } else {
        uid = 1;
      }
    } catch (ALDBErrorException e) {
      uid = 1;
      user = null;
    }
    // int uid = ALEipUtils.getUserId(rundata);
    try {
      EipMAddressbookCompany company =
        Database.create(EipMAddressbookCompany.class);
      rundata.getParameters().setProperties(company);
      company.setCompanyName(company_name.getValue());
      company.setCompanyNameKana(company_name_kana.getValue());
      company.setPostName(post_name.getValue());

      // 郵便番号の設定
      company.setZipcode(comp_zipcode_full.getValue());

      // 住所の設定
      company.setAddress(comp_address.getValue());

      // 電話番号の設定
      company.setTelephone(comp_telephone_full.getValue());

      // FAX番号の設定
      company.setFaxNumber(comp_fax_number_full.getValue());

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
      logger.debug("AddressBookFormData insertFormData out / false");
      logger.error("fileio", ex);
      return false;
    }
    return true;
  }

  /**
   * 入力ユーザー名を取得します。(通常は管理者) <BR>
   * 
   * @return
   */
  public ALStringField getUserName() {
    return username;
  }

  /**
   * 電話番号を取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getTelephone() {
    return telephone_full;
  }

  /**
   * 携帯電話番号を取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getCellularPhone() {
    return cellular_phone_full;
  }

  /**
   * 会社の郵便番号を取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getCompZipcode() {
    return comp_zipcode_full;
  }

  /**
   * 会社の電話番号を取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getCompTelephone() {
    return comp_telephone_full;
  }

  /**
   * 会社のFAX番号を取得します <BR>
   * 
   * @return
   */
  public FileIOStringField getCompFaxNumber() {
    return comp_fax_number_full;
  }

  /**
   * 同じ会社名がデータベースに存在するかどうかを取得します <BR>
   * 
   * @return
   */
  public boolean getSameCompany() {
    return same_company;
  }

  /**
   * 電話番号を入力します <BR>
   * 
   * @param str
   */
  public void setTelephone(String str) {
    telephone_full.setValue(str);
  }

  /**
   * 携帯電話番号を入力します <BR>
   * 
   * @param str
   */
  public void setCellularPhone(String str) {
    cellular_phone_full.setValue(str);
  }

  /**
   * 郵便番号を入力します <BR>
   * 
   * @param str
   */
  public void setCompZipcode(String str) {
    comp_zipcode_full.setValue(str);
  }

  /**
   * 電話番号(会社)を入力します <BR>
   * 
   * @param str
   */
  public void setCompTelephone(String str) {
    comp_telephone_full.setValue(str);
  }

  /**
   * FAX番号(会社)を入力します <BR>
   * 
   * @param str
   */
  public void setCompFaxNumber(String str) {
    comp_fax_number_full.setValue(str);
  }

  /**
   * 入力ユーザー名を入力します。(通常は管理者) <BR>
   * 
   * @param str
   */
  public void setUserName(String str) {
    username.setValue(str);
  }

  /**
   * 同じ会社名がデータベースに存在するかどうか示すフラグを入力します <BR>
   * 
   * @param flg
   */
  public void setSameCompany(boolean flg) {
    same_company = flg;
  }

  /**
   * 会社情報のみ入力する場合はtrueを設定します <BR>
   * 
   * @param flag
   */
  public void setIsCompanyOnly(boolean flag) {
    is_company_only = flag;
  }

  /**
   * 読み取った単語を指定されたフィールドに格納します。 <BR>
   * 
   * @param token
   * @param i
   */
  public void addItemToken(String token, int i) {
    StringTokenizer st;
    switch (i) {
      case -1:
        break;
      case 0:
        st = new StringTokenizer(token);
        if (st.hasMoreTokens()) {
          lastname.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          firstname.setValue(st.nextToken());
        }
        break;
      case 1:
        st = new StringTokenizer(token);
        if (st.hasMoreTokens()) {
          last_name_kana.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          first_name_kana.setValue(st.nextToken());
        }
        break;
      case 2:
        st = new StringTokenizer(token, "-");
        if (st.hasMoreTokens()) {
          telephone1.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          telephone2.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          telephone3.setValue(st.nextToken());
        }
        break;
      case 3:
        st = new StringTokenizer(token, "-");
        if (st.hasMoreTokens()) {
          cellular_phone1.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          cellular_phone2.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          cellular_phone3.setValue(st.nextToken());
        }
        break;
      case 4:
        email.setValue(token);
        break;
      case 5:
        cellular_mail.setValue(token);
        break;
      case 6:
        company_name.setValue(token);
        break;
      case 7:
        company_name_kana.setValue(token);
        break;
      case 8:
        st = new StringTokenizer(token, "-");
        if (st.hasMoreTokens()) {
          comp_zipcode1.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          comp_zipcode2.setValue(st.nextToken());
        }
        break;
      case 9:
        comp_address.setValue(token);
        break;
      case 10:
        st = new StringTokenizer(token, "-");
        if (st.hasMoreTokens()) {
          comp_telephone1.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          comp_telephone2.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          comp_telephone3.setValue(st.nextToken());
        }
        break;
      case 11:
        st = new StringTokenizer(token, "-");
        if (st.hasMoreTokens()) {
          comp_fax_number1.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          comp_fax_number2.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          comp_fax_number3.setValue(st.nextToken());
        }
        break;
      case 12:
        post_name.setValue(token);
        break;
      case 13:
        position_name.setValue(token);
        break;
      case 14:
        username.setValue(token);
        break;
      case 15:
        lastname.setValue(token);
        break;
      case 16:
        firstname.setValue(token);
        break;
      case 17:
        last_name_kana.setValue(token);
        break;
      case 18:
        first_name_kana.setValue(token);
        break;
      case 19:
        comp_url.setValue(token);
        break;
      case 20:
        note.setValue(token);
        break;

      default:
        break;
    }
  }

  /**
   * ユーザー名からユーザーIDを取得 <BR>
   * 
   * @return
   */
  private TurbineUser getTurbineUser() {

    SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
    Expression exp =
      ExpressionFactory.matchExp(TurbineUser.LOGIN_NAME_PROPERTY, username);

    query.setQualifier(exp);

    List<TurbineUser> users = query.fetchList();

    if (users == null || users.size() == 0) {
      // 指定したUser IDのレコードが見つからない場合
      logger.debug("[FileIOAddressBookCsvFormData] Not found ID...");
      return null;
    }

    TurbineUser tuser = users.get(0);
    return tuser;

  }

  /**
   * 会社名から会社IDを取得 <BR>
   * 
   * @return
   */
  private EipMAddressbookCompany getEipMCompany() {
    SelectQuery<EipMAddressbookCompany> query =
      Database.query(EipMAddressbookCompany.class);
    Expression exp =
      ExpressionFactory.matchExp(
        EipMAddressbookCompany.COMPANY_NAME_PROPERTY,
        company_name);

    query.setQualifier(exp);

    List<EipMAddressbookCompany> users = query.fetchList();

    if (users == null || users.size() == 0) {
      // 指定したUser IDのレコードが見つからない場合
      logger.debug("[FileIOAddressBookCsvFormData] Not found ID...");
      return null;
    }

    EipMAddressbookCompany com;
    int i;
    for (i = 0; i < users.size(); i++) {
      com = users.get(i);
      if (com.getPostName().equals(post_name.toString())) {
        return com;
      }
    }

    return null;
  }
}
