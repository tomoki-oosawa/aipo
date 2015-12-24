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
package com.aimluck.eip.license;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.AipoLicense;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.license.util.LicenseUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;

/**
 * Licenseのフォームデータを管理するクラスです。
 * 
 */
public class LicenseFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(LicenseFormData.class.getName());

  /** License */
  private ALStringField license_1;

  private ALStringField license_2;

  private ALStringField license_3;

  private ALStringField license_4;

  private ALStringField license_5;

  private ALStringField license_6;

  private String license_key;

  /**
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
   * 各フィールドを初期化します。
   * 
   */
  public void initField() {
    // License
    license_1 = new ALStringField();
    license_1.setFieldName("ライセンスキーの 1 マス目");
    license_1.setTrim(true);

    license_2 = new ALStringField();
    license_2.setFieldName("ライセンスキーの 2 マス目");
    license_2.setTrim(true);

    license_3 = new ALStringField();
    license_3.setFieldName("ライセンスキーの 3 マス目");
    license_3.setTrim(true);

    license_4 = new ALStringField();
    license_4.setFieldName("ライセンスキーの 4 マス目");
    license_4.setTrim(true);

    license_5 = new ALStringField();
    license_5.setFieldName("ライセンスキーの 5 マス目");
    license_5.setTrim(true);

    license_6 = new ALStringField();
    license_6.setFieldName("ライセンスキーの 6 マス目");
    license_6.setTrim(true);

  }

  /**
   * Licenseの各フィールドに対する制約条件を設定します。
   * 
   */
  @Override
  protected void setValidator() {
    // License
    license_1.setNotNull(true);
    license_2.setNotNull(true);
    license_3.setNotNull(true);
    license_4.setNotNull(true);
    license_5.setNotNull(true);
    license_6.setNotNull(true);
    // License名の文字数制限
    license_1.limitMaxLength(4);
    license_2.limitMaxLength(4);
    license_3.limitMaxLength(4);
    license_4.limitMaxLength(4);
    license_5.limitMaxLength(4);
    license_6.limitMaxLength(4);

    license_1.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);
    license_2.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);
    license_3.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);
    license_4.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);
    license_5.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);
    license_6.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);

  }

  /**
   * Licenseのフォームに入力されたデータの妥当性検証を行います。
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // License
    List<String> dummy = new ArrayList<String>();
    license_1.validate(dummy);
    license_2.validate(dummy);
    license_3.validate(dummy);
    license_4.validate(dummy);
    license_5.validate(dummy);
    license_6.validate(dummy);

    StringBuffer sb = new StringBuffer();
    sb.append(license_1.getValue()).append("-");
    sb.append(license_2.getValue()).append("-");
    sb.append(license_3.getValue()).append("-");
    sb.append(license_4.getValue()).append("-");
    sb.append(license_5.getValue()).append("-");
    sb.append(license_6.getValue());

    license_key = sb.toString().trim();
    if (dummy.size() != 0) {
      msgList.add("「ライセンスキーが無効です」");
    }

    return (msgList.size() == 0);
  }

  /**
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    return true;
  }

  /**
   * Licenseをデータベースから削除します。
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * Licenseをデータベースに格納します。
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * データベースに格納されているToDoを更新します。
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      AipoLicense license = LicenseUtils.getAipoLicense(rundata, context);
      if (license == null) {
        // 新規オブジェクトモデル
        license = Database.create(AipoLicense.class);
      }

      StringBuffer sb = new StringBuffer();
      sb.append(license_1.getValue()).append("-");
      sb.append(license_2.getValue()).append("-");
      sb.append(license_3.getValue()).append("-");
      sb.append(license_4.getValue()).append("-");
      sb.append(license_5.getValue()).append("-");
      sb.append(license_6.getValue());

      license_key = sb.toString().trim();

      license.setLicense(license_key);
      Database.commit();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("license", ex);
      return false;
    }
    return true;
  }

}
