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

import java.util.List;

import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.datasync.ALDataSyncFactoryService;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.system.util.SystemUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * ユーザーアカウントのパスワードのフォームデータを管理するためのクラスです。 <br />
 */
public class SystemPasswdFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemPasswdFormData.class.getName());

  /** 新しいパスワード */
  private ALStringField new_passwd;

  /** 新しいパスワード（確認用） */
  private ALStringField new_passwd_confirm;

  /**
   * 初期化する <BR>
   *
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    if (ALEipUtils.isMatch(rundata, context)) {
      ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, rundata
        .getUser()
        .getUserName());
    }
  }

  /**
   *
   */
  @Override
  public void initField() {
    // 新しいパスワード
    new_passwd = new ALStringField();
    new_passwd.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_NEWPW"));
    new_passwd.setTrim(true);

    // 新しいパスワード（確認用）
    new_passwd_confirm = new ALStringField();
    new_passwd_confirm.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_NEWPW_CONFIRM"));
    new_passwd_confirm.setTrim(true);

  }

  /**
   *
   */
  @Override
  protected void setValidator() {
    // 新しいパスワード
    new_passwd.setNotNull(true);
    new_passwd.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);
    new_passwd.limitMaxLength(16);

    // 新しいパスワード（確認用）
    new_passwd_confirm.setNotNull(true);
    new_passwd_confirm.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);
    new_passwd_confirm.limitMaxLength(16);
  }

  /**
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {
    new_passwd.validate(msgList);
    new_passwd_confirm.validate(msgList);

    if (!new_passwd.toString().equals(new_passwd_confirm.toString())) {
      msgList.add(ALLocalizationUtils.getl10n("SYSTEM_ALERT_NEWPW_ERROR"));
    }
    return (msgList.size() == 0);
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
    boolean res = true;
    try {

      ALBaseUser user = SystemUtils.getBaseUser(rundata, context);
      if (user == null) {
        return false;
      }

      // WebAPIのDBへ接続できるか確認
      if (!ALDataSyncFactoryService
        .getInstance()
        .getDataSyncHandler()
        .checkConnect()) {
        msgList.add(ALLocalizationUtils
          .getl10n("SYSTEM_ALERT_CONNECT_DB_FAILED"));
        return false;
      }

      // 新しいパスワードをセットする
      JetspeedSecurity.forcePassword(user, new_passwd.toString());

      // ユーザを更新する
      JetspeedSecurity.saveUser(user);

      // WebAPIとのDB同期
      if (!ALDataSyncFactoryService
        .getInstance()
        .getDataSyncHandler()
        .updateUser(user)) {
        return false;
      }

      ALBaseUser currentUser = (ALBaseUser) rundata.getUser();
      if (currentUser.getUserName().equals(user.getUserName())) {
        currentUser.setPassword(user.getPassword());
      }

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        Integer.valueOf(user.getUserId()),
        ALEventlogConstants.PORTLET_TYPE_SYSTEM,
        null);

    } catch (Exception e) {
      logger.error("system", e);
      res = false;
    }
    return res;
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
   * @return
   */
  public ALStringField getNewPasswd() {
    return new_passwd;
  }

  /**
   * @return
   */
  public ALStringField getNewPasswdConfirm() {
    return new_passwd_confirm;
  }

  /**
   * @param field
   */
  public void setNewPasswd(String field) {
    new_passwd.setValue(field);
  }

  /**
   * @param field
   */
  public void setNewPasswdConfirm(String field) {
    new_passwd_confirm.setValue(field);
  }

}
