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
package com.aimluck.eip.webmail;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * WebメールアカウントのDetailResultDataです。 <br />
 */
public class WebMailAccountDetailResultData implements ALData {

  private static final int DEF_SMTP_PORT = 25;

  private static final int DEF_POP3_PORT = 110;

  /** The value for the accountId field */
  private ALNumberField account_id;

  /** The value for the userId field */
  private ALNumberField user_id;

  /** The value for the accountName field */
  private ALStringField account_name;

  /** The value for the accountType field */
  private ALStringField account_type;

  /** The value for the pop3serverName field */
  private ALStringField smtpserver_name;

  /** The value for the pop3serverName field */
  private ALStringField pop3server_name;

  /** The value for the pop3userName field */
  private ALStringField pop3user_name;

  /** The value for the pop3password field */
  private ALStringField pop3_password;

  /** The value for the mailUserName field */
  private ALStringField mail_user_name;

  /** The value for the mailAddress field */
  private ALStringField mail_address;

  /** The value for the smtpPort field */
  private ALNumberField smtp_port;

  /** The value for the pop3Port field */
  private ALNumberField pop3_port;

  /** The value for the authSendFlg field */
  private ALNumberField auth_send_flg;

  /** The value for the auth_send_user_id field */
  private ALStringField auth_send_user_id;

  /** The value for the auth_send_user_password field */
  private ALStringField auth_send_user_password;

  /** The value for the signature field */
  private ALStringField signature;

  /** The value for the pop3EncryptioFlag */
  private ALNumberField pop3_encryption_flag;

  /** The value for the smtpEncryptionFlag field */
  private ALNumberField smtp_encryption_flag;

  /**
   *
   *
   */
  @Override
  public void initField() {
    account_id = new ALNumberField();
    account_id.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_ACCOUNTID"));

    user_id = new ALNumberField();
    user_id.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_USERID"));

    account_name = new ALStringField();
    account_name.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_MAILACCOUNT_NAME"));
    account_name.setTrim(true);

    account_type = new ALStringField();
    account_type.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_ACCOUNT_TYPE"));
    account_type.setTrim(true);

    smtpserver_name = new ALStringField();
    smtpserver_name.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_MAILSERVER_SEND"));
    smtpserver_name.setTrim(true);

    pop3server_name = new ALStringField();
    pop3server_name.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_MAILSERVER_RECEIVE"));
    pop3server_name.setTrim(true);

    pop3user_name = new ALStringField();
    pop3user_name.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_USERID_RECEIVE"));
    pop3user_name.setTrim(true);

    pop3_password = new ALStringField();
    pop3_password.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_USERPASS_RECEIVE"));
    pop3_password.setTrim(true);

    mail_user_name = new ALStringField();
    mail_user_name.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_NAME"));
    mail_user_name.setTrim(true);

    mail_address = new ALStringField();
    mail_address.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_MAILADDRESS"));
    mail_address.setTrim(true);

    smtp_port = new ALNumberField();
    smtp_port.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_PORTNUM_SEND"));
    smtp_port.setValue(DEF_SMTP_PORT);

    pop3_port = new ALNumberField();
    pop3_port.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_PORTNUM_RECEIVE"));
    pop3_port.setValue(DEF_POP3_PORT);

    auth_send_flg = new ALNumberField();
    auth_send_flg.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_SEND_AUTHENTICATION"));
    auth_send_flg.setValue(0);

    pop3_encryption_flag = new ALNumberField();
    pop3_encryption_flag.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_RECEIVE_SSL_ENCRYPTION"));
    pop3_encryption_flag.setValue(0);

    smtp_encryption_flag = new ALNumberField();
    smtp_encryption_flag.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_SEND_SSL_ENCRYPTION"));
    smtp_encryption_flag.setValue(0);

    auth_send_user_id = new ALStringField();
    auth_send_user_id.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_SMTP_ID"));
    auth_send_user_id.setTrim(true);

    auth_send_user_password = new ALStringField();
    auth_send_user_password.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_SMTP_PW"));
    auth_send_user_password.setTrim(true);

    signature = new ALStringField();
    signature.setFieldName(ALLocalizationUtils.getl10n("WEBMAIL_SIGN"));
    signature.setTrim(true);
  }

  /**
   * @return
   */
  public ALStringField getAccountName() {
    return account_name;
  }

  /**
   * @return
   */
  public String getSignature() {
    return ALEipUtils.getMessageList(signature.getValue());
  }

  /**
   * @return
   */
  public ALStringField getAccountType() {
    return account_type;
  }

  /**
   * @return
   */
  public ALStringField getMailAddress() {
    return mail_address;
  }

  /**
   * @return
   */
  public ALStringField getMailUserName() {
    return mail_user_name;
  }

  /**
   * @return
   */
  public ALStringField getPop3Password() {
    return pop3_password;
  }

  /**
   * @return
   */
  public ALNumberField getPop3Port() {
    return pop3_port;
  }

  /**
   * @return
   */
  public ALStringField getPop3serverName() {
    return pop3server_name;
  }

  /**
   * @return
   */
  public ALStringField getPop3userName() {
    return pop3user_name;
  }

  public ALStringField getSmtpserverName() {
    return smtpserver_name;
  }

  /**
   * @return
   */
  public ALNumberField getSmtpPort() {
    return smtp_port;
  }

  /**
   * @return
   */
  public long getAccountId() {
    return account_id.getValue();
  }

  /**
   * @return
   */
  public ALNumberField getAuthSendFlg() {
    return auth_send_flg;
  }

  /**
   * @return
   */
  public ALStringField getAuthSendUserId() {
    return auth_send_user_id;
  }

  /**
   * @return
   */
  public ALStringField getAuthSendUserPassword() {
    return auth_send_user_password;
  }

  /**
   * @return
   */
  public void setAccountId(int i) {
    account_id.setValue(i);
  }

  /**
   * @return
   */
  public void setAccountName(String string) {
    account_name.setValue(string);
  }

  public void setSignature(String string) {
    signature.setValue(string);
  }

  /**
   * @return
   */
  public void setAccountType(String string) {
    account_type.setValue(string);
  }

  /**
   * @return
   */
  public void setMailAddress(String string) {
    mail_address.setValue(string);
  }

  /**
   * @return
   */
  public void setMailUserName(String string) {
    mail_user_name.setValue(string);
  }

  /**
   * @return
   */
  public void setPop3Password(String string) {
    pop3_password.setValue(string);
  }

  /**
   * @return
   */
  public void setPop3serverName(String string) {
    pop3server_name.setValue(string);
  }

  /**
   * @return
   */
  public void setPop3userName(String string) {
    pop3user_name.setValue(string);
  }

  public void setSmtpserverName(String string) {
    smtpserver_name.setValue(string);
  }

  /**
   * @return
   */
  public void setSmtpPort(long i) {
    smtp_port.setValue(i);
  }

  /**
   * @return
   */
  public void setPop3Port(long i) {
    pop3_port.setValue(i);
  }

  /**
   * @return
   */
  public void setAuthSendFlg(long i) {
    auth_send_flg.setValue(i);
  }

  /**
   * @return
   */
  public void setAuthSendUserId(String str) {
    auth_send_user_id.setValue(str);
  }

  /**
   * @return
   */
  public void setAuthSendUserPassword(String str) {
    auth_send_user_password.setValue(str);
  }

  public ALNumberField getPop3EncryptionFlag() {
    return pop3_encryption_flag;
  }

  public void setPop3EncryptionFlag(long i) {
    this.pop3_encryption_flag.setValue(i);
  }

  public ALNumberField getSmtpEncryptionFlag() {
    return smtp_encryption_flag;
  }

  public void setSmtpEncryptionFlag(long i) {
    this.smtp_encryption_flag.setValue(i);
  }
}
