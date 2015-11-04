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
package com.aimluck.eip.mail;

/**
 * メール送信（SMTP）用の情報を保持するクラスです。 <br />
 * 
 */
public class ALSmtpMailSenderContext implements ALMailSenderContext {

  /** データベース ID */
  protected String org_id;

  /** ユーザー ID */
  protected int user_id;

  /** アカウント ID */
  protected int account_id;

  /** SMTP サーバのドメイン */
  protected String smtpHost = null;

  /** SMTP サーバのポート番号 */
  protected String smtpPort = null;

  /** 送信時の認証方式 */
  protected int authSendFlag = -1;

  /** 送信時の暗号化方式 */
  protected int encryptionFlag = -1;

  /** SMTP 認証時のユーザ− ID */
  protected String authSendUserId = null;

  /** SMTP 認証時のユーザパスワード */
  protected String authSendUserPassword = null;

  /** POP before SMTP 認証用： POP3 サーバのホスト名（IP アドレス） */
  private String pop3Host;

  /** POP before SMTP 認証用： POP3 サーバのポート番号 */
  private String pop3Port;

  /** POP before SMTP 認証用： POP3 サーバの認証用ユーザー ID */
  private String pop3UserId;

  /** POP before SMTP 認証用： POP3 サーバの認証用ユーザーパスワード */
  private String pop3UserPasswd;

  /** POP before SMTP 認証用： POP3 サーバ接続時の暗号化方式 */
  private int pop3EncryptionFlag = -1;

  public ALSmtpMailSenderContext() {

  }

  public String getOrgId() {
    return org_id;
  }

  public int getUserId() {
    return user_id;
  }

  public int getAccountId() {
    return account_id;
  }

  public String getSmtpHost() {
    return smtpHost;
  }

  public String getSmtpPort() {
    return smtpPort;
  }

  public int getAuthSendFlag() {
    return authSendFlag;
  }

  public String getAuthSendUserId() {
    return authSendUserId;
  }

  public String getAuthSendUserPassword() {
    return authSendUserPassword;
  }

  public String getPop3Host() {
    return pop3Host;
  }

  public String getPop3Port() {
    return pop3Port;
  }

  public String getPop3UserId() {
    return pop3UserId;
  }

  public String getPop3UserPasswd() {
    return pop3UserPasswd;
  }

  public void setOrgId(String str) {
    org_id = str;
  }

  public void setUserId(int i) {
    user_id = i;
  }

  public void setAccountId(int i) {
    account_id = i;
  }

  public void setSmtpHost(String str) {
    smtpHost = str;
  }

  public void setSmtpPort(String str) {
    smtpPort = str;
  }

  public void setAuthSendFlag(int i) {
    authSendFlag = i;
  }

  public void setAuthSendUserId(String str) {
    authSendUserId = str;
  }

  public void setAuthSendUserPassword(String str) {
    authSendUserPassword = str;
  }

  public void setPop3Host(String str) {
    pop3Host = str;
  }

  public void setPop3Port(String str) {
    pop3Port = str;
  }

  public void setPop3UserId(String str) {
    pop3UserId = str;
  }

  public void setPop3UserPasswd(String str) {
    pop3UserPasswd = str;
  }

  public int getEncryptionFlag() {
    return encryptionFlag;
  }

  public void setEncryptionFlag(int encryptionFlag) {
    this.encryptionFlag = encryptionFlag;
  }

  public int getPop3EncryptionFlag() {
    return pop3EncryptionFlag;
  }

  public void setPop3EncryptionFlag(int pop3EncryptionFlag) {
    this.pop3EncryptionFlag = pop3EncryptionFlag;
  }
}
