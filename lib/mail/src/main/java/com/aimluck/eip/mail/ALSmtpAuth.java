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
package com.aimluck.eip.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * メール送信（SMTP）用の認証情報を保持するクラスです。 <br />
 * 
 */
public class ALSmtpAuth extends Authenticator {

  /** SMTP 認証時のユーザーID */
  private final String authSendUserId;

  /** SMTP 認証時のユーザーパスワードｌ */
  private final String authSendUserPassword;

  /**
   * コンストラクタ
   * 
   * @param authSendUserId
   * @param authSendUserPassword
   */
  public ALSmtpAuth(String authSendUserId, String authSendUserPassword) {
    this.authSendUserId = authSendUserId;
    this.authSendUserPassword = authSendUserPassword;
  }

  @Override
  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(authSendUserId, authSendUserPassword);
  }
}
