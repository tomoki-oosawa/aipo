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

/**
 * メール送信を操作するインターフェイスです。 <br />
 * 
 */
public interface ALMailSender {

  /** メール送信時の処理結果（送信に成功した） */
  public static final int SEND_MSG_SUCCESS = 0;

  /** メール送信時の処理結果（送信に失敗した） */
  public static final int SEND_MSG_FAIL = 1;

  /**
   * SMTP サーバへメールを送信する．
   * 
   * @return
   */
  public int send(ALMailContext context);
}
