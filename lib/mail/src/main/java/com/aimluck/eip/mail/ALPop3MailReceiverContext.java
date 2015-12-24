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
 * メール受信（POP3）用の情報を保持するクラスです。 <br />
 * 
 */
public class ALPop3MailReceiverContext implements ALMailReceiverContext {

  /** データベース名 */
  private String org_id;

  /** ユーザーID */
  private int user_id;

  /** メールアカウント ID */
  private int account_id;

  /** POP3 サーバのホスト名（IP アドレス） */
  private String pop3Host;

  /** POP3 サーバのポート番号 */
  private String pop3Port;

  /** POP3 接続時の暗号化フラグ */
  private int encryptionFlag = -1;

  /** POP3 サーバの認証用ユーザー ID */
  private String pop3UserId;

  /** POP3 サーバの認証用ユーザーパスワード */
  private String pop3UserPasswd;

  /** POP3 サーバ上のメール削除フラグ */
  private boolean delete;

  /** POP3 サーバ上のメール保存フラグ */
  private boolean enableSavingDays;

  /** POP3 サーバ上のメール保存日数 */
  private int savingDays;

  /** 受信済みメールの受信フラグ（true の場合、受信しない） */
  private boolean denyReceivedMail;

  private int authReceiveFlag;

  public ALPop3MailReceiverContext() {

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

  public boolean getDelete() {
    return delete;
  }

  public boolean getEnableSavingDays() {
    return enableSavingDays;
  }

  public int getSavingDays() {
    return savingDays;
  }

  public boolean getDenyReceivedMail() {
    return denyReceivedMail;
  }

  public int getAuthReceiveFlag() {
    return authReceiveFlag;
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

  public void setDelete(boolean bool) {
    delete = bool;
  }

  public void setEnableSavingDays(boolean bool) {
    enableSavingDays = bool;
  }

  public void setSavingDays(int i) {
    savingDays = i;
  }

  public void setDenyReceivedMail(boolean bool) {
    denyReceivedMail = bool;
  }

  public void setAuthReceiveFlag(int i) {
    authReceiveFlag = i;
  }

  public int getEncryptionFlag() {
    return encryptionFlag;
  }

  public void setEncryptionFlag(int encryptionFlag) {
    this.encryptionFlag = encryptionFlag;
  }

}
