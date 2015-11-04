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

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbookCompany;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * アドレス帳の会社情報登録フォームデータクラスです。
 * 
 */
public class AddressBookCompanyFormData extends ALAbstractFormData {
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookCompanyFormData.class.getName());

  private ALStringField company_name;

  private ALStringField company_name_kana;

  private ALStringField post_name;

  private ALStringField zipcode1;

  private ALStringField zipcode2;

  private ALStringField address;

  private ALStringField telephone1;

  private ALStringField telephone2;

  private ALStringField telephone3;

  private ALStringField fax_number1;

  private ALStringField fax_number2;

  private ALStringField fax_number3;

  private ALStringField url;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   *
   */
  @Override
  public void initField() {
    company_name = new ALStringField();
    company_name.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_COMPANY_NAME"));
    company_name.setTrim(true);

    company_name_kana = new ALStringField();
    company_name_kana.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_COMPANY_NAME_KANA"));
    company_name_kana.setTrim(true);

    post_name = new ALStringField();
    post_name
      .setFieldName(ALLocalizationUtils.getl10n("ADDRESSBOOK_POST_NAME"));
    post_name.setTrim(true);

    // 郵便番号
    zipcode1 = new ALStringField();
    zipcode1.setFieldName(ALLocalizationUtils.getl10n("ADDRESSBOOK_ZIPCODE"));
    zipcode1.setTrim(true);
    zipcode2 = new ALStringField();
    zipcode2.setFieldName(ALLocalizationUtils.getl10n("ADDRESSBOOK_ZIPCODE"));
    zipcode2.setTrim(true);

    address = new ALStringField();
    address.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_COMPANY_ADDRESS"));
    address.setTrim(true);

    telephone1 = new ALStringField();
    telephone1.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_TELEPHONE"));
    telephone1.setTrim(true);
    telephone2 = new ALStringField();
    telephone2.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_TELEPHONE"));
    telephone2.setTrim(true);
    telephone3 = new ALStringField();
    telephone3.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_TELEPHONE"));
    telephone3.setTrim(true);

    fax_number1 = new ALStringField();
    fax_number1.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_FAX_NUMBER"));
    fax_number1.setTrim(true);
    fax_number2 = new ALStringField();
    fax_number2.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_FAX_NUMBER"));
    fax_number2.setTrim(true);
    fax_number3 = new ALStringField();
    fax_number3.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_FAX_NUMBER"));
    fax_number3.setTrim(true);

    url = new ALStringField();
    url.setFieldName(ALLocalizationUtils.getl10n("ADDRESSBOOK_URL"));
    url.setTrim(true);

  }

  /**
   *
   */
  @Override
  protected void setValidator() {
    company_name.setNotNull(true);
    company_name.limitMaxLength(50);
    company_name_kana.setNotNull(true);
    company_name_kana.limitMaxLength(50);
    post_name.limitMaxLength(50);
    address.limitMaxLength(50);
    url.setCharacterType(ALStringField.TYPE_ASCII);
    url.limitMaxLength(50);

    // 郵便番号
    zipcode1.setCharacterType(ALStringField.TYPE_NUMBER);
    zipcode1.limitLength(3, 3);
    zipcode2.setCharacterType(ALStringField.TYPE_NUMBER);
    zipcode2.limitLength(4, 4);

    // 電話番号
    telephone1.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone1.limitMaxLength(5);
    telephone2.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone2.limitMaxLength(4);
    telephone3.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone3.limitMaxLength(4);

    // FAX番号
    fax_number1.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number1.limitMaxLength(5);
    fax_number2.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number2.limitMaxLength(4);
    fax_number3.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number3.limitMaxLength(4);
  }

  /**
   * 
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {
    List<String> dummy = new ArrayList<String>();
    company_name.validate(msgList);

    // 会社名フリガナのカタカナへの変換
    company_name_kana.setValue(ALStringUtil
      .convertHiragana2Katakana(ALStringUtil.convertH2ZKana(company_name_kana
        .toString())));
    company_name_kana.validate(msgList);

    post_name.validate(msgList);
    address.validate(msgList);
    url.validate(msgList);

    // 郵便番号
    if (!zipcode1.getValue().equals("") || !zipcode2.getValue().equals("")) {
      if (!zipcode1.validate(dummy) || !zipcode2.validate(dummy)) {
        msgList.add("『 <span class='em'>郵便番号</span> 』は7桁の半角数字で入力してください。");
      }
    }

    // 電話
    if (!telephone1.getValue().equals("")
      || !telephone2.getValue().equals("")
      || !telephone3.getValue().equals("")) {
      if (!telephone1.validate(dummy)
        || !telephone2.validate(dummy)
        || !telephone3.validate(dummy)) {
        msgList.add("『 <span class='em'>電話番号</span> 』を正しく入力してください。");
      }
    }
    // FAX番号
    if (!fax_number1.getValue().equals("")
      || !fax_number2.getValue().equals("")
      || !fax_number3.getValue().equals("")) {
      if (!fax_number1.validate(dummy)
        || !fax_number2.validate(dummy)
        || !fax_number3.validate(dummy)) {
        msgList.add("『 <span class='em'>FAX番号</span> 』を正しく入力してください。");
      }
    }

    return msgList.size() == 0;
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
      // オブジェクトモデルを取得
      EipMAddressbookCompany company =
        AddressBookUtils.getEipMAddressbookCompany(rundata, context);
      if (company == null) {
        return false;
      }
      // 取引先名
      company_name.setValue(company.getCompanyName());
      // 取引先名(フリガナ)
      company_name_kana.setValue(company.getCompanyNameKana());
      // 部署名
      post_name.setValue(company.getPostName());

      // 住所
      address.setValue(company.getAddress());

      StringTokenizer token;
      // 郵便番号
      if (company.getZipcode() != null) {
        token = new StringTokenizer(company.getZipcode(), "-");
        if (token.countTokens() == 3) {
          zipcode1.setValue(token.nextToken());
          zipcode2.setValue(token.nextToken());
        }
      }

      // 電話番号
      if (company.getTelephone() != null) {
        token = new StringTokenizer(company.getTelephone(), "-");
        if (token.countTokens() == 3) {
          telephone1.setValue(token.nextToken());
          telephone2.setValue(token.nextToken());
          telephone3.setValue(token.nextToken());
        }
      }
      // FAX番号
      if (company.getFaxNumber() != null) {
        token = new StringTokenizer(company.getFaxNumber(), "-");
        if (token.countTokens() == 3) {
          fax_number1.setValue(token.nextToken());
          fax_number2.setValue(token.nextToken());
          fax_number3.setValue(token.nextToken());
        }
      }
      // 郵便番号
      if (company.getZipcode() != null) {
        token = new StringTokenizer(company.getZipcode(), "-");
        if (token.countTokens() == 2) {
          zipcode1.setValue(token.nextToken());
          zipcode2.setValue(token.nextToken());
        }
      }
      // URL
      url.setValue(company.getUrl());

    } catch (Exception ex) {
      logger.error("AddressBookCompanyFormData.loadFormData", ex);
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
    try {
      EipMAddressbookCompany company =
        Database.create(EipMAddressbookCompany.class);
      rundata.getParameters().setProperties(company);
      company.setCompanyName(company_name.getValue());
      company.setCompanyNameKana(company_name_kana.getValue());
      company.setPostName(post_name.getValue());

      // 郵便番号の設定
      if (!zipcode1.getValue().equals("") && !zipcode2.getValue().equals("")) {
        company.setZipcode(new StringBuffer()
          .append(zipcode1.getValue())
          .append("-")
          .append(zipcode2.getValue())
          .toString());
      } else {
        company.setZipcode("");
      }

      company.setAddress(address.getValue());

      // 電話番号の設定
      if (!telephone1.getValue().equals("")
        && !telephone2.getValue().equals("")
        && !telephone3.getValue().equals("")) {
        company.setTelephone(new StringBuffer()
          .append(telephone1.getValue())
          .append("-")
          .append(telephone2.getValue())
          .append("-")
          .append(telephone3.getValue())
          .toString());
      } else {
        company.setTelephone("");
      }

      // FAX番号の設定
      if (!fax_number1.getValue().equals("")
        && !fax_number2.getValue().equals("")
        && !fax_number3.getValue().equals("")) {
        company.setFaxNumber(new StringBuffer()
          .append(fax_number1.getValue())
          .append("-")
          .append(fax_number2.getValue())
          .append("-")
          .append(fax_number3.getValue())
          .toString());
      } else {
        company.setFaxNumber("");
      }

      company.setUrl(url.getValue());

      int uid = ALEipUtils.getUserId(rundata);

      company.setCreateUserId(Integer.valueOf(uid));
      company.setUpdateUserId(Integer.valueOf(uid));

      Date now = new Date();
      company.setCreateDate(now);
      company.setUpdateDate(now);
      // orm_company.doInsert(company);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        company.getCompanyId(),
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_COMPANY,
        company_name.getValue());

      return true;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookCompanyFormData.insertFormData", ex);
      return false;
    }
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
    try {
      // オブジェクトモデルを取得
      EipMAddressbookCompany company =
        AddressBookUtils.getEipMAddressbookCompany(rundata, context);
      if (company == null) {
        return false;
      }

      company.setCompanyName(company_name.getValue());
      company.setCompanyNameKana(company_name_kana.getValue());
      company.setPostName(post_name.getValue());
      company.setAddress(address.getValue());

      // 郵便番号
      if (!zipcode1.getValue().equals("") && !zipcode2.getValue().equals("")) {
        company.setZipcode(new StringBuffer()
          .append(zipcode1.getValue())
          .append("-")
          .append(zipcode2.getValue())
          .toString());
      } else {
        company.setZipcode("");
      }

      // 電話番号
      if (!telephone1.getValue().equals("")
        && !telephone2.getValue().equals("")
        && !telephone3.getValue().equals("")) {
        company.setTelephone(new StringBuffer()
          .append(telephone1.getValue())
          .append("-")
          .append(telephone2.getValue())
          .append("-")
          .append(telephone3.getValue())
          .toString());
      } else {
        company.setTelephone("");
      }

      // FAX番号
      if (!fax_number1.getValue().equals("")
        && !fax_number2.getValue().equals("")
        && !fax_number3.getValue().equals("")) {
        company.setFaxNumber(new StringBuffer()
          .append(fax_number1.getValue())
          .append("-")
          .append(fax_number2.getValue())
          .append("-")
          .append(fax_number3.getValue())
          .toString());
      } else {
        company.setFaxNumber("");
      }

      company.setAddress(address.getValue());
      company.setUrl(url.getValue());
      company.setUpdateUserId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      company.setUpdateDate(new Date());
      // 取引先情報を更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        company.getCompanyId(),
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_COMPANY,
        company_name.getValue());

    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookCompanyFormData.updateFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 会社情報を削除します。
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      String companyid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      if (companyid == null || Integer.valueOf(companyid) == null) {
        logger.debug("[AddressBook] Cannot find Address ID .");
        return false;
      }

      // 会社情報の削除
      EipMAddressbookCompany company =
        Database.get(EipMAddressbookCompany.class, Integer.valueOf(companyid));
      // entityIdの取得
      int entityId = company.getCompanyId();
      // 会社名の取得
      String companyName = company.getCompanyName();

      Database.delete(company);

      // アドレス情報の中で削除対象会社に所属しているものの会社IDを（その他）のものとする
      int empty_id =
        AddressBookUtils
          .getDummyEipMAddressbookCompany(rundata, context)
          .getCompanyId()
          .intValue();

      SelectQuery<EipMAddressbook> addrquery =
        Database.query(EipMAddressbook.class);
      Expression addrexp =
        ExpressionFactory.matchDbExp(
          EipMAddressbook.EIP_MADDRESSBOOK_COMPANY_PROPERTY
            + "."
            + EipMAddressbookCompany.COMPANY_ID_PK_COLUMN,
          companyid);
      addrquery.setQualifier(addrexp);

      List<EipMAddressbook> addresses = addrquery.fetchList();

      if (addresses != null && addresses.size() > 0) {
        EipMAddressbook addressbook = null;

        EipMAddressbookCompany dummycompany =
          Database.get(EipMAddressbookCompany.class, Integer.valueOf(empty_id));

        int addrsize = addresses.size();
        for (int i = 0; i < addrsize; i++) {
          addressbook = addresses.get(i);
          addressbook.setEipMAddressbookCompany(dummycompany);
        }
      }

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_COMPANY,
        companyName);

    } catch (Exception ex) {
      Database.rollback();
      logger.error("AddressBookCompanyFormData.deleteFormData", ex);
      return false;
    }
    return true;
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
  public ALStringField getCompanyName() {
    return company_name;
  }

  /**
   * @return
   */
  public ALStringField getCompanyNameKana() {
    return company_name_kana;
  }

  public ALStringField getFaxNumber1() {
    return fax_number1;
  }

  public ALStringField getFaxNumber2() {
    return fax_number2;
  }

  public ALStringField getFaxNumber3() {
    return fax_number3;
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
  public ALStringField getTelephone1() {
    return telephone1;
  }

  public ALStringField getTelephone2() {
    return telephone2;
  }

  public ALStringField getTelephone3() {
    return telephone3;
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
  public ALStringField getZipcode1() {
    return zipcode1;
  }

  public ALStringField getZipcode2() {
    return zipcode2;
  }

  /**
   * @param field
   */
  public void setAddress(ALStringField field) {
    address = field;
  }

  /**
   * @param field
   */
  public void setCompanyName(ALStringField field) {
    company_name = field;
  }

  /**
   * @param field
   */
  public void setCompanyNameKana(ALStringField field) {
    company_name_kana = field;
  }

  /**
   * @param field
   */
  public void setPostName(ALStringField field) {
    post_name = field;
  }

  /**
   * @param field
   */
  public void setUrl(ALStringField field) {
    url = field;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_COMPANY;
  }
}
