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

import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.orm.Database;

/**
 *
 */
public class ALMailSendThread implements Runnable {

  /** 受信結果のキー（新着メール数） */
  public static final String KEY_NEW_MAIL_NUM = "nmn";

  /** 受信結果のキー（受信メール数） */
  public static final String KEY_RECEIVE_MAIL_NUM = "rmn";

  /** 受信結果のキー（受信メール総数） */
  public static final String KEY_RECEIVE_MAIL_ALL_NUM = "rman";

  /** 受信中のキー */
  public static final String KEY_RECEIVE_STAT = "rs";

  /** 受信中のキー */
  public static final String KEY_THREAD = "thread";

  /** 処理タイプ（メール受信） */
  public static final int PROCESS_TYPE_RECEIVEMAIL = 1;

  /** 処理タイプ（新着メール確認） */
  public static final int PROCESS_TYPE_GET_NEWMAILNUM = 2;

  /** 処理タイプ（メール受信中断） */
  public static final int PROCESS_TYPE_STOP_RECEIVEMAIL = 3;

  /** 処理状態（終了） */
  public static final int PROCESS_STAT_FINISHED = 0;

  /** 処理状態（実行中） */
  public static final int PROCESS_STAT_PROCESSING = -100;

  /** 処理状態（未実行） */
  public static final int PROCESS_STAT_NONPROCESSING = -101;

  /** データベース ID */
  private String orgId = null;

  private ALAdminMailContext adminMailContext = null;

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALPop3MailReceiveThread.class.getName());

  public static ALMailHandler getService() {
    return ALMailFactoryService.getInstance().getMailHandler();
  }

  public ALMailSendThread(String orgId, ALAdminMailContext adminMailContext) {
    this.orgId = orgId;
    this.adminMailContext = adminMailContext;
  }

  @Override
  public void run() {
    try {
      DataContext.bindThreadDataContext(Database.createDataContext(orgId));
      getService().sendAdminMail(adminMailContext);
    } catch (Exception e) {
      logger.error("ALMailSendThread.run", e);
    } finally {
      Database.tearDown();
    }
  }
}
