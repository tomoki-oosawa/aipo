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

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.ALPop3MailReceiver;
import com.aimluck.eip.mail.ALSmtpMailSender;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.modules.screens.SystemWebMailAdminFormJSONScreen;
import com.aimluck.eip.modules.screens.SystemWebMailAdminFormScreen;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.system.util.SystemWebMailUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * Webメールアカウントのフォームデータを管理するためのクラスです。 <br />
 */
public class SystemWebMailAccountFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemWebMailAccountFormData.class.getName());

  private final int DEF_SMTP_PORT = 25;

  private final int DEF_POP3_PORT = 110;

  /** The value for the accountId field */
  private ALNumberField account_id;

  /** The value for the userId field */
  private ALNumberField user_id;

  /** The value for the accountName field */
  private ALStringField account_name;

  /** The value for the accountType field */
  private ALStringField account_type;

  /** The value for the smtpserverName field */
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

  /** <code>auth_send_flag</code> 送信時の認証方式 */
  private ALNumberField auth_send_flg;

  /** <code>auth_send_user_id</code> SMTP 認証時のユーザ ID */
  private ALStringField auth_send_user_id;

  /** <code>auth_send_user_password</code> SMTP 認証時のユーザパスワード */
  private ALStringField auth_send_user_password;

  /** <code>auth_receive_flag</code> 受信時の認証方式 */
  private ALNumberField auth_receive_flag;

  /** The value for the delAtPop3Flg field */
  private ALNumberField del_at_pop3_flg;

  /** <code>del_at_pop3_before_days_flg</code> 指定日数の経過後に POP3 サーバからメールを削除する */
  private ALNumberField del_at_pop3_before_days_flg;

  /** <code>del_at_pop3_before_days</code> POP3 サーバからメールを削除する経過日数 */
  private ALNumberField del_at_pop3_before_days;

  /** The value for the nonReceivedFlg field */
  private ALNumberField non_received_flg;

  /** The value for the createDate field */
  private ALDateTimeField create_date;

  /** The value for the updateDate field */
  private ALDateTimeField update_date;

  /** The value for the signature field */
  private ALStringField signature;

  /** The value for the smtpEncryptionFlag field */
  private ALNumberField smtp_encryption_flag;

  /** The value for the pop3EncryptionFlag field */
  private ALNumberField pop3_encryption_flag;

  private String orgId;

  private boolean isAdmin;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
    orgId = Database.getDomainName();
    isAdmin =
      rundata.getScreen().equals(
        SystemWebMailAdminFormScreen.class.getSimpleName())
        || rundata.getScreen().equals(
          SystemWebMailAdminFormJSONScreen.class.getSimpleName());
  }

  @Override
  public void initField() {
    account_id = new ALNumberField();
    account_id.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_ACCOUNTID"));

    user_id = new ALNumberField();
    user_id.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_USERID"));

    account_name = new ALStringField();
    account_name.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_MAILACCOUNTNAME"));
    account_name.setTrim(true);

    account_type = new ALStringField();
    account_type.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_ACCOUNT_TYPE"));
    account_type.setTrim(true);

    smtpserver_name = new ALStringField();
    smtpserver_name.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_SERVER_SEND"));
    smtpserver_name.setTrim(true);

    pop3server_name = new ALStringField();
    pop3server_name.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_SERVER_RECEIVE"));
    pop3server_name.setTrim(true);

    pop3user_name = new ALStringField();
    pop3user_name.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_RECEIVE_ID"));
    pop3user_name.setTrim(true);

    pop3_password = new ALStringField();
    pop3_password.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_RECEIVE_PW"));
    pop3_password.setTrim(true);

    mail_user_name = new ALStringField();
    mail_user_name.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_NAME"));
    mail_user_name.setTrim(true);

    mail_address = new ALStringField();
    mail_address.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_MAILADDRESS"));
    mail_address.setTrim(true);

    smtp_port = new ALNumberField();
    smtp_port.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_PORT_SEND"));
    smtp_port.setValue(DEF_SMTP_PORT);

    pop3_port = new ALNumberField();
    pop3_port.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_PORT_RECEIVE"));
    pop3_port.setValue(DEF_POP3_PORT);

    auth_send_flg = new ALNumberField();
    auth_send_flg.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_SEND_AUTHENTICATION"));
    auth_send_flg.setValue(ALSmtpMailSender.AUTH_SEND_NONE);

    smtp_encryption_flag = new ALNumberField();
    smtp_encryption_flag.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_SEND_ENCRYPTION"));
    smtp_encryption_flag.setValue(0);

    auth_send_user_id = new ALStringField();
    auth_send_user_id.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_SMTP_ID"));
    auth_send_user_id.setTrim(true);

    auth_send_user_password = new ALStringField();
    auth_send_user_password.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_SMTP_PW"));
    auth_send_user_password.setTrim(true);

    auth_receive_flag = new ALNumberField();
    auth_receive_flag.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_RECEIVE_AUTHENTICATION"));
    auth_receive_flag.setValue(ALPop3MailReceiver.AUTH_RECEIVE_NORMAL);

    pop3_encryption_flag = new ALNumberField();
    pop3_encryption_flag.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_RECEIVE_ENCRYPTION"));
    pop3_encryption_flag.setValue(0);

    del_at_pop3_flg = new ALNumberField();
    del_at_pop3_flg.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_DELETE_MAIL_FROM_SERVER_AFTER_RECEIVE"));
    del_at_pop3_flg.setValue(0);

    del_at_pop3_before_days_flg = new ALNumberField();
    del_at_pop3_before_days_flg.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_DELETE_MAIL_DATE_FLUG"));
    del_at_pop3_before_days_flg.setValue(0);

    del_at_pop3_before_days = new ALNumberField();
    del_at_pop3_before_days.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_DELETE_MAIL_DATE"));
    del_at_pop3_before_days.setValue(0);

    non_received_flg = new ALNumberField();
    non_received_flg.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_DONOT_INPORT_RECEIVEDMSG"));
    non_received_flg.setValue(1);

    create_date = new ALDateTimeField(SystemWebMailUtils.CREATED_DATE_FORMAT);
    create_date.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_MAILACCOUNT_CREATE_DATE"));

    update_date = new ALDateTimeField(SystemWebMailUtils.DATE_TIME_FORMAT);
    update_date.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_MAILACCOUNT_LAST_UPDATE_DATE"));

    signature = new ALStringField();
    signature.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_SIGN"));
    signature.setTrim(true);
  }

  /**
   *
   */
  @Override
  protected void setValidator() {
    // アカウント名
    account_name.setNotNull(true);
    account_name.limitMaxLength(50);

    // SMTPサーバ名
    smtpserver_name.setNotNull(true);
    smtpserver_name.setCharacterType(ALStringField.TYPE_ASCII);
    smtpserver_name.limitMaxLength(60);

    // POP3サーバ名
    pop3server_name.setNotNull(true);
    pop3server_name.setCharacterType(ALStringField.TYPE_ASCII);
    pop3server_name.limitMaxLength(60);

    // POP3用ユーザID
    pop3user_name.setNotNull(true);
    pop3user_name.setCharacterType(ALStringField.TYPE_ASCII);
    pop3user_name.limitMaxLength(50);

    // POP3用ユーザパスワード
    pop3_password.setNotNull(true);
    pop3_password.setCharacterType(ALStringField.TYPE_ASCII);
    pop3_password.limitMaxLength(50);

    // メールユーザ名
    mail_user_name.setNotNull(true);
    mail_user_name.limitMaxLength(50);

    // メールアドレス
    mail_address.setNotNull(true);
    mail_address.setCharacterType(ALStringField.TYPE_ASCII);
    mail_address.limitMaxLength(50);

    // SMTPポート番号
    smtp_port.setNotNull(true);
    smtp_port.limitMinValue(0);
    smtp_port.limitMaxValue(65535);

    // POP3ポート番号
    pop3_port.setNotNull(true);
    pop3_port.limitMinValue(0);
    pop3_port.limitMaxValue(65535);

    // 送信時の認証方式
    auth_send_flg.setNotNull(true);
    auth_send_flg.limitMinValue(ALSmtpMailSender.AUTH_SEND_NONE);
    auth_send_flg.limitMaxValue(ALSmtpMailSender.AUTH_SEND_SMTP_AUTH);

    // 送信時の暗号化
    smtp_encryption_flag.setNotNull(true);
    smtp_encryption_flag.limitMaxValue(ALSmtpMailSender.ENCRYPTION_SEND_SSL);
    smtp_encryption_flag.limitMinValue(ALSmtpMailSender.ENCRYPTION_SEND_NONE);

    // SMTP 認証時のユーザ ID
    auth_send_user_id.setNotNull(true);
    auth_send_user_id.setCharacterType(ALStringField.TYPE_ASCII);
    auth_send_user_id.limitMaxLength(50);

    // SMTP 認証時のユーザパスワード
    auth_send_user_password.setNotNull(true);
    auth_send_user_password.setCharacterType(ALStringField.TYPE_ASCII);
    auth_send_user_password.limitMaxLength(50);

    // 受信時の認証方式
    auth_receive_flag.setNotNull(true);
    auth_receive_flag.limitMinValue(ALPop3MailReceiver.AUTH_RECEIVE_NORMAL);
    auth_receive_flag.limitMaxValue(ALPop3MailReceiver.AUTH_RECEIVE_APOP);

    // 受信時の暗号化
    pop3_encryption_flag.setNotNull(true);
    pop3_encryption_flag.limitMinValue(ALPop3MailReceiver.ENCRYPTION_SEND_NONE);
    pop3_encryption_flag.limitMaxValue(ALPop3MailReceiver.ENCRYPTION_SEND_SSL);

    // 受信後、サーバからメールを削除する
    del_at_pop3_flg.setNotNull(true);
    del_at_pop3_flg.limitMinValue(0);
    del_at_pop3_flg.limitMaxValue(1);

    // 指定日数の経過後に POP3 サーバからメールを削除する
    del_at_pop3_before_days_flg.setNotNull(true);
    del_at_pop3_before_days_flg.limitMinValue(0);
    del_at_pop3_before_days_flg.limitMaxValue(1);

    // POP3 サーバからメールを削除する経過日数
    del_at_pop3_before_days.setNotNull(true);
    del_at_pop3_before_days.limitMinValue(0);
    del_at_pop3_before_days.limitMaxValue(100);

    // 受信済みメッセージは取り込まない
    non_received_flg.setNotNull(true);
    non_received_flg.limitMinValue(0);
    non_received_flg.limitMaxValue(1);

    // 署名の最大文字数
    signature.limitMaxLength(1000);
  }

  /**
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // アカウント名
    account_name.validate(msgList);
    // SMTPサーバ名
    smtpserver_name.validate(msgList);

    if (!isAdmin
      || (isAdmin && auth_send_flg.getValue() == ALSmtpMailSender.AUTH_SEND_POP_BEFORE_SMTP)) {
      // POP3サーバ名
      pop3server_name.validate(msgList);
      // POP3用ユーザID
      pop3user_name.validate(msgList);
      // POP3用ユーザパスワード
      pop3_password.validate(msgList);
      // POP3ポート番号
      pop3_port.validate(msgList);
      // 受信時の暗号化
      pop3_encryption_flag.validate(msgList);
      // 受信時の認証方式
      auth_receive_flag.validate(msgList);
    }
    // メールユーザ名
    mail_user_name.validate(msgList);
    // メールアドレス
    if (mail_address.validate(msgList)
      && !ALStringUtil.isMailAddress(mail_address.getValue())) {
      msgList.add(ALLocalizationUtils.getl10n("SYSTEM_ALERT_SET_MAILADDRESS"));
    }

    // SMTPポート番号
    smtp_port.validate(msgList);

    // 送信時の認証方式
    auth_send_flg.validate(msgList);

    if (auth_send_flg.getValue() == ALSmtpMailSender.AUTH_SEND_SMTP_AUTH) {
      // SMTP 認証時のユーザ ID
      auth_send_user_id.validate(msgList);

      // SMTP 認証時のユーザパスワード
      auth_send_user_password.validate(msgList);
    }

    // 送信時の暗号化
    smtp_encryption_flag.validate(msgList);

    // 受信後、サーバからメールを削除する
    del_at_pop3_flg.validate(msgList);

    if (del_at_pop3_flg.getValue() == 0) {
      // 指定日数の経過後に POP3 サーバからメールを削除する
      del_at_pop3_before_days_flg.validate(msgList);

      if (del_at_pop3_before_days_flg.getValue() == 1) {
        // POP3 サーバからメールを削除する経過日数
        del_at_pop3_before_days.validate(msgList);
      }
    }

    // 受信済みメッセージは取り込まない
    non_received_flg.validate(msgList);

    // 署名
    signature.validate(msgList);

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
    try {
      // 全体で使うメールアカウントのオーナーは常にuid=1
      int userId = isAdmin ? 1 : ALEipUtils.getUserId(rundata);
      int accountId = -1;
      try {
        accountId =
          Integer.parseInt(ALEipUtils.getTemp(rundata, context, ALMailUtils
            .getAccountName(userId, accountId)));
      } catch (Exception e) {
        accountId = 0;
      }

      if (accountId == 0) {
        try {
          Expression exp =
            ExpressionFactory
              .matchExp(EipMMailAccount.USER_ID_PROPERTY, userId);
          SelectQuery<EipMMailAccount> query =
            Database.query(EipMMailAccount.class, exp);

          query.select(EipMMailAccount.ACCOUNT_ID_PK_COLUMN);
          List<EipMMailAccount> accounts = query.fetchList();
          if (accounts != null && accounts.size() > 0) {
            EipMMailAccount account = accounts.get(0);
            accountId = account.getAccountId();
            ALEipUtils.setTemp(
              rundata,
              context,
              SystemWebMailUtils.ACCOUNT_ID,
              Integer.toString(accountId));
          } else {
            // アカウントが一つも見つからなかった
            return false;
          }
        } catch (Exception e) {
        }
      }
      // オブジェクトモデルを取得
      EipMMailAccount account = ALMailUtils.getMailAccount(userId, accountId);
      if (account == null) {
        return false;
      }

      account_id.setValue(account.getAccountId().intValue());
      account_name.setValue(account.getAccountName());
      account_type.setValue(account.getAccountType());
      smtpserver_name.setValue(account.getSmtpserverName());
      pop3server_name.setValue(account.getPop3serverName());
      pop3user_name.setValue(account.getPop3userName());
      pop3_password.setValue(new String(ALMailUtils
        .getDecryptedMailAccountPasswd(account.getPop3password())));
      mail_user_name.setValue(account.getMailUserName());
      mail_address.setValue(account.getMailAddress());
      smtp_port.setValue(account.getSmtpPort());
      pop3_port.setValue(account.getPop3Port());
      auth_send_flg.setValue(account.getAuthSendFlg());
      smtp_encryption_flag.setValue(account.getSmtpEncryptionFlg());
      pop3_encryption_flag.setValue(account.getPop3EncryptionFlg());
      auth_send_user_id.setValue(account.getAuthSendUserId());
      signature.setValue(account.getSignature());

      byte[] tmpAuthSendUserPasswd = account.getAuthSendUserPasswd();
      if (tmpAuthSendUserPasswd != null && tmpAuthSendUserPasswd.length > 0) {
        auth_send_user_password.setValue(new String(ALMailUtils
          .getDecryptedMailAccountPasswd(tmpAuthSendUserPasswd)));
      }

      auth_receive_flag.setValue(account.getAuthReceiveFlg().longValue());
      del_at_pop3_flg.setValue(account.getDelAtPop3Flg());
      del_at_pop3_before_days_flg.setValue(account.getDelAtPop3BeforeDaysFlg());
      del_at_pop3_before_days.setValue(account
        .getDelAtPop3BeforeDays()
        .longValue());
      non_received_flg.setValue(account.getNonReceivedFlg());
      create_date.setValue(account.getCreateDate());
      update_date.setValue(account.getUpdateDate());
    } catch (Exception ex) {
      logger.error("system", ex);
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

    // 全体で使うメールアカウントのオーナーは常にuid=1
    int uid = isAdmin ? 1 : ALEipUtils.getUserId(rundata);
    return ALMailUtils.insertMailAccountData(
      rundata,
      msgList,
      uid,
      getAccountName().getValue(),
      ALMailUtils.ACCOUNT_TYPE_NON,
      getMailAddress().getValue(),
      getMailUserName().getValue(),
      getSmtpserverName().getValue(),
      (int) getSmtpPort().getValue(),
      getPop3serverName().getValue(),
      (int) getPop3Port().getValue(),
      getPop3userName().getValue(),
      getPop3Password().getValue(),
      (int) getAuthSendFlg().getValue(),
      getAuthSendUserId().getValue(),
      this.getAuthSendUserPasswd().getValue(),
      (int) getAuthReceiveFlg().getValue(),
      (int) getDelAtPop3Flg().getValue(),
      (int) getDelAtPop3BeforeDaysFlg().getValue(),
      (int) getDelAtPop3BeforeDays().getValue(),
      getNonReceivedFlg().toString(),
      getSignature().getValue(),
      (int) getSmtpEncryptionFlag().getValue(),
      (int) getPop3EncryptionFlag().getValue());
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
      // 全体で使うメールアカウントのオーナーは常にuid=1

      int userId = isAdmin ? 1 : ALEipUtils.getUserId(rundata);

      // int accountId =
      // Integer.parseInt(ALEipUtils.getTemp(
      // rundata,
      // context,
      // ALEipConstants.ENTITY_ID));
      int accountId = -1;
      try {
        accountId =
          Integer.parseInt(ALEipUtils.getTemp(
            rundata,
            context,
            SystemWebMailUtils.ACCOUNT_ID));
      } catch (Exception e) {
        accountId = 0;
      }

      if (accountId == 0) {
        try {
          Expression exp =
            ExpressionFactory
              .matchExp(EipMMailAccount.USER_ID_PROPERTY, userId);
          SelectQuery<EipMMailAccount> query =
            Database.query(EipMMailAccount.class, exp);

          query.select(EipMMailAccount.ACCOUNT_ID_PK_COLUMN);
          List<EipMMailAccount> accounts = query.fetchList();
          if (accounts != null && accounts.size() > 0) {
            EipMMailAccount account = accounts.get(0);
            accountId = account.getAccountId();
            ALEipUtils.setTemp(
              rundata,
              context,
              SystemWebMailUtils.ACCOUNT_ID,
              Integer.toString(accountId));
          } else {
            // アカウントが一つも見つからなかった
            return false;
          }
        } catch (Exception e) {
        }
      }
      // オブジェクトモデルを取得
      EipMMailAccount mailAccount =
        ALMailUtils.getMailAccount(userId, accountId);
      if (mailAccount == null) {
        return false;
      }

      mailAccount.setUserId(Integer.valueOf(userId));
      mailAccount.setAccountName(getAccountName().getValue());
      // mailAccount.setAccountType(getAccountType().getValue());
      mailAccount.setSmtpserverName(getSmtpserverName().getValue());
      mailAccount.setPop3serverName(getPop3serverName().getValue());
      mailAccount.setPop3userName(getPop3userName().getValue());
      mailAccount
        .setPop3password(ALMailUtils
          .getEncryptedMailAccountPasswd(getPop3Password()
            .getValue()
            .getBytes()));
      mailAccount.setMailUserName(getMailUserName().getValue());
      mailAccount.setMailAddress(getMailAddress().getValue());
      mailAccount.setSmtpPort(Integer.toString((int) getSmtpPort().getValue()));
      mailAccount.setPop3Port(Integer.toString((int) getPop3Port().getValue()));
      mailAccount.setAuthSendFlg(Short.valueOf(getAuthSendFlg().toString()));
      mailAccount.setSmtpEncryptionFlg((short) getSmtpEncryptionFlag()
        .getValue());
      mailAccount.setPop3EncryptionFlg((short) getPop3EncryptionFlag()
        .getValue());
      if (getAuthSendFlg().getValue() == ALSmtpMailSender.AUTH_SEND_SMTP_AUTH) {
        mailAccount.setAuthSendUserId(getAuthSendUserId().getValue());
        mailAccount.setAuthSendUserPasswd(ALMailUtils
          .getEncryptedMailAccountPasswd(getAuthSendUserPasswd()
            .getValue()
            .getBytes()));
      }
      mailAccount.setAuthReceiveFlg(Short.valueOf((short) getAuthReceiveFlg()
        .getValue()));
      mailAccount.setDelAtPop3Flg(getDelAtPop3Flg().getValueAsString());
      if (getDelAtPop3Flg().getValue() == 0) {
        mailAccount.setDelAtPop3BeforeDaysFlg(getDelAtPop3BeforeDaysFlg()
          .getValueAsString());
        if (getDelAtPop3BeforeDaysFlg().getValue() == 1) {
          mailAccount.setDelAtPop3BeforeDays(Integer
            .valueOf((int) getDelAtPop3BeforeDays().getValue()));
        }
      }
      mailAccount.setNonReceivedFlg(getNonReceivedFlg().toString());
      mailAccount.setUpdateDate(Calendar.getInstance().getTime());
      mailAccount.setSignature(getSignature().getValue());

      // アカウントを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        mailAccount.getAccountId(),
        ALEventlogConstants.PORTLET_TYPE_WEBMAIL_ACCOUNT,
        mailAccount.getAccountName());

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[SystemWebMailAccountFormData]", t);
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
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      int uid = ALEipUtils.getUserId(rundata);
      // int accountId =
      // rundata.getParameters().getInt(SystemWebMailUtils.ACCOUNT_ID);
      int accountId = -1;
      try {
        accountId =
          Integer.parseInt(ALEipUtils.getTemp(
            rundata,
            context,
            SystemWebMailUtils.ACCOUNT_ID));
      } catch (Exception e) {
        accountId = 0;
      }

      if (accountId == 0) {
        try {
          Expression exp =
            ExpressionFactory.matchExp(EipMMailAccount.USER_ID_PROPERTY, uid);
          SelectQuery<EipMMailAccount> query =
            Database.query(EipMMailAccount.class, exp);

          query.select(EipMMailAccount.ACCOUNT_ID_PK_COLUMN);
          List<EipMMailAccount> accounts = query.fetchList();
          if (accounts != null && accounts.size() > 0) {
            EipMMailAccount account = accounts.get(0);
            accountId = account.getAccountId();
            ALEipUtils.setTemp(
              rundata,
              context,
              SystemWebMailUtils.ACCOUNT_ID,
              Integer.toString(accountId));
          } else {
            // アカウントが一つも見つからなかった
            return false;
          }
        } catch (Exception e) {
        }
      }
      SelectQuery<EipMMailAccount> query =
        Database.query(EipMMailAccount.class);

      Expression exp1 =
        ExpressionFactory.matchExp(EipMMailAccount.USER_ID_PROPERTY, Integer
          .valueOf(uid));
      Expression exp2 =
        ExpressionFactory.matchDbExp(
          EipMMailAccount.ACCOUNT_ID_PK_COLUMN,
          Integer.valueOf(accountId));

      EipMMailAccount account =
        query.setQualifier(exp1.andExp(exp2)).fetchSingle();
      if (account == null) {
        msgList.add(ALLocalizationUtils
          .getl10n("SYSTEM_ALERT_DONOT_EXIST_MAILACCOUNT_DB"));
        return false;
      }

      // ローカルフォルダを削除する．
      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      handler.removeAccount(orgId, ALEipUtils.getUserId(rundata), accountId);

      Database.delete(account);
      Database.commit();

      // セッション変数を削除する
      SystemWebMailUtils.clearWebMailAccountSession(rundata, context);

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        account.getAccountId(),
        ALEventlogConstants.PORTLET_TYPE_WEBMAIL_ACCOUNT,
        account.getAccountName());
    } catch (Throwable t) {
      Database.rollback();
      logger.error("[SystemWebMailAccountFormData]", t);
      return false;
    }
    return true;
  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      if ("new".equals(rundata.getParameters().getString(
        ALEipConstants.ENTITY_ID))) {
        SelectQuery<EipMMailAccount> query =
          Database.query(EipMMailAccount.class);

        Expression exp1 =
          ExpressionFactory.matchExp(EipMMailAccount.USER_ID_PROPERTY, Integer
            .valueOf(ALEipUtils.getUserId(rundata)));
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipMMailAccount.ACCOUNT_TYPE_PROPERTY,
            Integer.valueOf(ALMailUtils.ACCOUNT_TYPE_INIT));
        EipMMailAccount account =
          query.setQualifier(exp1.andExp(exp2)).fetchSingle();

        if (account != null) {
          mail_address.setValue(account.getMailAddress());
        }
      }
      return super.setFormData(rundata, context, msgList);
    } catch (Exception ex) {
      logger.error("system", ex);
      return false;
    }
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
  public ALStringField getAccountType() {
    return account_type;
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALNumberField getDelAtPop3Flg() {
    return del_at_pop3_flg;
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
  public ALNumberField getNonReceivedFlg() {
    return non_received_flg;
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
  public ALDateTimeField getUpdateDate() {
    return update_date;
  }

  /**
   * @return
   */
  public int getAccountId() {
    return (int) account_id.getValue();
  }

  public ALNumberField getAuthSendFlg() {
    return auth_send_flg;
  }

  public ALStringField getAuthSendUserId() {
    return auth_send_user_id;
  }

  public ALStringField getAuthSendUserPasswd() {
    return auth_send_user_password;
  }

  public ALNumberField getAuthReceiveFlg() {
    return auth_receive_flag;
  }

  public ALNumberField getDelAtPop3BeforeDaysFlg() {
    return del_at_pop3_before_days_flg;
  }

  public ALNumberField getDelAtPop3BeforeDays() {
    return del_at_pop3_before_days;
  }

  /**
   * @return
   */
  public ALStringField getSignature() {
    return signature;
  }

  public ALNumberField getSmtpEncryptionFlag() {
    return smtp_encryption_flag;
  }

  public ALNumberField getPop3EncryptionFlag() {
    return pop3_encryption_flag;
  }

}
