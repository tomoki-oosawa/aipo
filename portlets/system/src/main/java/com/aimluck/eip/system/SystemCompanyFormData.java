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
package com.aimluck.eip.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.system.util.SystemUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 会社情報のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class SystemCompanyFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemCompanyFormData.class.getName());

  /** 会社名 */
  private ALStringField company_name;

  /** 郵便番号 */
  private ALStringField zipcode1;

  /** 郵便番号 */
  private ALStringField zipcode2;

  /** 住所 */
  private ALStringField address;

  /** 電話番号 */
  private ALStringField telephone1;

  /** 電話番号 */
  private ALStringField telephone2;

  /** 電話番号 */
  private ALStringField telephone3;

  /** FAX番号 */
  private ALStringField fax_number1;

  /** FAX番号 */
  private ALStringField fax_number2;

  /** FAX番号 */
  private ALStringField fax_number3;

  /**
   * 初期化します。
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // 会社名
    company_name = new ALStringField();
    company_name.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_COMPANY_NAME"));
    company_name.setTrim(true);
    // 郵便番号
    zipcode1 = new ALStringField();
    zipcode1.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_ZIPCODE"));
    zipcode1.setTrim(true);
    zipcode2 = new ALStringField();
    zipcode2.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_ZIPCODE"));
    zipcode2.setTrim(true);
    // 住所
    address = new ALStringField();
    address.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_ADDRESS"));
    address.setTrim(true);
    // 電話番号
    telephone1 = new ALStringField();
    telephone1.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_TELEPHONE"));
    telephone1.setTrim(true);
    telephone2 = new ALStringField();
    telephone2.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_TELEPHONE"));
    telephone2.setTrim(true);
    telephone3 = new ALStringField();
    telephone3.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_TELEPHONE"));
    telephone3.setTrim(true);
    // FAX番号
    fax_number1 = new ALStringField();
    fax_number1.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_FAX_NUMBER"));
    fax_number1.setTrim(true);
    fax_number2 = new ALStringField();
    fax_number2.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_FAX_NUMBER"));
    fax_number2.setTrim(true);
    fax_number3 = new ALStringField();
    fax_number3.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_FAX_NUMBER"));
    fax_number3.setTrim(true);

  }

  /**
   * 各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    company_name.setNotNull(true);
    company_name.limitMaxLength(50);
    address.limitMaxLength(60);
    zipcode1.setCharacterType(ALStringField.TYPE_NUMBER);
    zipcode1.limitLength(3, 3);
    zipcode2.setCharacterType(ALStringField.TYPE_NUMBER);
    zipcode2.limitLength(4, 4);
    telephone1.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone1.limitMaxLength(5);
    telephone2.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone2.limitMaxLength(4);
    telephone3.setCharacterType(ALStringField.TYPE_NUMBER);
    telephone3.limitMaxLength(4);
    fax_number1.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number1.limitMaxLength(5);
    fax_number2.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number2.limitMaxLength(4);
    fax_number3.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number3.limitMaxLength(4);
  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    List<String> dummy = new ArrayList<String>();
    company_name.validate(msgList);
    address.validate(msgList);

    if (!zipcode1.getValue().equals("") || !zipcode2.getValue().equals("")) {
      if (!zipcode1.validate(dummy) || !zipcode2.validate(dummy)) {
        msgList.add(ALLocalizationUtils.getl10n("SYSTEM_ALERT_SET_ZIPCODE"));
      }
    }
    if (!telephone1.getValue().equals("")
      || !telephone2.getValue().equals("")
      || !telephone3.getValue().equals("")) {
      if (!telephone1.validate(dummy)
        || !telephone2.validate(dummy)
        || !telephone3.validate(dummy)) {
        msgList.add(ALLocalizationUtils.getl10n("SYSTEM_ALERT_SET_TELEPHONE"));
      }
    }
    if (!fax_number1.getValue().equals("")
      || !fax_number2.getValue().equals("")
      || !fax_number3.getValue().equals("")) {
      if (!fax_number1.validate(dummy)
        || !fax_number2.validate(dummy)
        || !fax_number3.validate(dummy)) {
        msgList.add(ALLocalizationUtils.getl10n("SYSTEM_ALERT_SET_FAX_NUMBER"));
      }
    }

    return (msgList.size() == 0);
  }

  /**
   * 『会社』を読み込みます。 <BR>
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
      EipMCompany record = SystemUtils.getEipMCompany(rundata, context);
      if (record == null) {
        return false;
      }
      // 会社名
      company_name.setValue(record.getCompanyName());
      // 郵便番号
      StringTokenizer token;
      if (record.getZipcode() != null) {
        token = new StringTokenizer(record.getZipcode(), "-");
        if (token.countTokens() == 2) {
          zipcode1.setValue(token.nextToken());
          zipcode2.setValue(token.nextToken());
        }
      }

      // 住所
      address.setValue(record.getAddress());
      // 電話番号
      if (record.getTelephone() != null) {
        token = new StringTokenizer(record.getTelephone(), "-");
        if (token.countTokens() == 3) {
          telephone1.setValue(token.nextToken());
          telephone2.setValue(token.nextToken());
          telephone3.setValue(token.nextToken());
        }
      }

      // FAX番号
      if (record.getFaxNumber() != null) {
        token = new StringTokenizer(record.getFaxNumber(), "-");
        if (token.countTokens() == 3) {
          fax_number1.setValue(token.nextToken());
          fax_number2.setValue(token.nextToken());
          fax_number3.setValue(token.nextToken());
        }
      }
    } catch (Exception ex) {
      logger.error("system", ex);
      return false;
    }
    return true;
  }

  /**
   * 『会社』を追加します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    //
    // 現行バージョンでは会社の新規追加は行いません。
    //
    return false;
  }

  /**
   * 『会社』を更新します。 <BR>
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
      EipMCompany record = SystemUtils.getEipMCompany(rundata, context);
      if (record == null) {
        return false;
      }
      // 会社名
      record.setCompanyName(company_name.getValue());
      // 郵便番号
      if (!zipcode1.getValue().equals("") && !zipcode2.getValue().equals("")) {
        record.setZipcode(new StringBuffer()
          .append(zipcode1.getValue())
          .append("-")
          .append(zipcode2.getValue())
          .toString());
      } else {
        record.setZipcode("");
      }

      // 住所
      record.setAddress(address.getValue());
      // 電話番号
      if (!telephone1.getValue().equals("")
        && !telephone2.getValue().equals("")
        && !telephone3.getValue().equals("")) {
        record.setTelephone(new StringBuffer()
          .append(telephone1.getValue())
          .append("-")
          .append(telephone2.getValue())
          .append("-")
          .append(telephone3.getValue())
          .toString());
      } else {
        record.setTelephone("");
      }
      // FAX番号
      if (!fax_number1.getValue().equals("")
        && !fax_number2.getValue().equals("")
        && !fax_number3.getValue().equals("")) {
        record.setFaxNumber(new StringBuffer()
          .append(fax_number1.getValue())
          .append("-")
          .append(fax_number2.getValue())
          .append("-")
          .append(fax_number3.getValue())
          .toString());
      } else {
        record.setFaxNumber("");
      }
      // 更新日
      record.setUpdateDate(new Date());

      // 会社を更新
      Database.commit();

      // singletonの更新
      ALEipManager.getInstance().reloadCompany();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("system", ex);
      return false;
    }
    return true;
  }

  /**
   * 『会社』を削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    //
    // 現行バージョンでは会社の削除は行いません。
    //
    return false;
  }

  /**
   * 住所を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getAddress() {
    return address;
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
  public ALStringField getFaxNumber1() {
    return fax_number1;
  }

  /**
   * FAX番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getFaxNumber2() {
    return fax_number2;
  }

  /**
   * FAX番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getFaxNumber3() {
    return fax_number3;
  }

  /**
   * 電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getTelephone1() {
    return telephone1;
  }

  /**
   * 電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getTelephone2() {
    return telephone2;
  }

  /**
   * 電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getTelephone3() {
    return telephone3;
  }

  /**
   * 郵便番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getZipcode1() {
    return zipcode1;
  }

  /**
   * 郵便番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getZipcode2() {
    return zipcode2;
  }

}
