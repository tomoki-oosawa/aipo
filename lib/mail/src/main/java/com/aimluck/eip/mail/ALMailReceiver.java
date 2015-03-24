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
public interface ALMailReceiver {

  /** メール受信時の処理結果（受信に成功した） */
  public static final int RECEIVE_MSG_SUCCESS = 0;

  /** メール受信時の処理結果（受信に失敗した） */
  public static final int RECEIVE_MSG_FAIL = -1;

  /** メール受信時のロックファイルのタイムアウト時間（20分） */
  public static final long TIMEOUT_SPAN = 20 * 60 * 1000;

  /**
   * POP3 サーバからメールを受信する．
   * 
   * @throws Exception
   */
  abstract public int receive(String orgId) throws Exception;

  /**
   * 新着メール数を取得する．
   * 
   * @return
   */
  abstract public int getNewMailSum();
}
