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

import java.util.Calendar;

import org.apache.cayenne.access.DataContext;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.om.security.User;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.mail.util.ALStaticObject;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * メール受信（POP3）を操作するクラスです。 <br />
 * 
 */
public class ALPop3MailReceiveThread implements Runnable {

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

  /** ユーザー ID */
  private String userId = null;

  /** メールアカウント ID */
  private int mailAccountId = 0;

  /** 処理タイプ */
  private int processType = PROCESS_TYPE_RECEIVEMAIL;

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALPop3MailReceiveThread.class.getName());

  /**
   * コンストラクタ
   * 
   * @param orgId
   * @param userId
   * @param mailAccountId
   */
  public ALPop3MailReceiveThread(String orgId, JetspeedUser user,
      int mailAccountId, int processType) {
    this.orgId = orgId;
    this.mailAccountId = mailAccountId;
    this.processType = processType;

    userId = user.getUserId();

    ALStaticObject ob = ALStaticObject.getInstance();
    ob.addAccountId(mailAccountId);
  }

  /**
   * メール受信処理
   * 
   */
  @Override
  public void run() {
    ALStaticObject ob = ALStaticObject.getInstance();

    try {

      DataContext.bindThreadDataContext(Database.createDataContext(orgId));

      EipMMailAccount account =
        ALMailUtils.getMailAccount(Integer.parseInt(userId), mailAccountId);
      if (processType == PROCESS_TYPE_RECEIVEMAIL) {
        logger
          .info("[ALFilePop3MailReceiveThread] start receivemail (orgId, userId, mailAccountId)=("
            + Database.getDomainName()
            + ","
            + userId
            + ","
            + mailAccountId
            + ")");

        // メール受信
        int res = receiveMail(orgId, account);

        ob.updateAccountStat(mailAccountId, KEY_RECEIVE_MAIL_NUM, Integer
          .valueOf(res));
      } else if (processType == PROCESS_TYPE_GET_NEWMAILNUM) {
        logger
          .info("[ALFilePop3MailReceiveThread] start newmailnum (orgId, userId, mailAccountId)=("
            + orgId
            + ","
            + userId
            + ","
            + mailAccountId
            + ")");

        // 新着メール数確認
        int res = checkNewMailNum(orgId, account);
        if (res >= 0) {
          ob.updateAccountStat(mailAccountId, KEY_NEW_MAIL_NUM, Integer
            .valueOf(res));
        }
      }
    } catch (Exception e) {
      logger.error("ALFilePop3MailReceiveThread.run", e);
    } finally {
      Database.tearDown();
      ob.updateAccountStat(
        mailAccountId,
        KEY_RECEIVE_STAT,
        PROCESS_STAT_FINISHED);
      ALStaticObject.getInstance().removeAccountId(mailAccountId);
    }
  }

  /**
   * 指定されたアカウントのメールを受信する。
   * 
   * @param account
   */
  private int receiveMail(String orgId, EipMMailAccount account) {
    ALStaticObject.getInstance().updateAccountStat(
      mailAccountId,
      KEY_RECEIVE_STAT,
      PROCESS_STAT_PROCESSING);
    int result = ALPop3MailReceiver.RECEIVE_MSG_FAIL;
    if (account == null) {
      return result;
    }

    try {
      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      ALMailReceiverContext rcontext =
        ALMailUtils.getALPop3MailReceiverContext(orgId, account);

      result = handler.receive(rcontext, orgId);

      if (result <= ALPop3MailReceiver.RECEIVE_MSG_FAIL
        && result != ALPop3MailReceiver.RECEIVE_MSG_FAIL_OVER_MAIL_MAX_SIZE) {
        // 受信に失敗した場合の処理
        return result;
      }

      // 最終受信日を保存する．
      DataContext dataContext = account.getDataContext();
      account.setLastReceivedDate(Calendar.getInstance().getTime());
      Database.commit(dataContext);
    } catch (Exception ex) {
      Database.rollback();
      logger.error("ALFilePop3MailReceiveThread.receiveMail", ex);
      result = ALPop3MailReceiver.RECEIVE_MSG_FAIL;
      return result;
    }
    return result;
  }

  /**
   * 指定されたアカウントの新着メール数を取得する。
   * 
   * @param account
   */
  private int checkNewMailNum(String orgId, EipMMailAccount account) {
    int res = -1;
    if (account == null) {
      return res;
    }

    try {
      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      ALMailReceiverContext rcontext =
        ALMailUtils.getALPop3MailReceiverContext(orgId, account);

      res = handler.getNewMailSum(rcontext);

    } catch (Exception ex) {
      logger.error("ALFilePop3MailReceiveThread.checkNewMailNum", ex);
      res = -1;
    }
    return res;
  }

  /**
   * 新着メール数を取得する。
   * 
   * @param user
   * @return
   */
  public static int getNewMailNum(User user, int mailAccountId) {
    if (user == null) {
      return PROCESS_STAT_NONPROCESSING;
    }

    ALStaticObject ob = ALStaticObject.getInstance();
    Object obj = ob.getAccountStat(mailAccountId, KEY_NEW_MAIL_NUM);

    if (obj == null) {
      return PROCESS_STAT_NONPROCESSING;
    }

    return ((Integer) obj).intValue();
  }

  /**
   * 指定したユーザーのaccountIdが存在するかどうかチェック
   * 
   * @param user
   * @return
   */
  public static boolean isProcessing(User user, int mailAccountId) {
    return !(ALStaticObject.getInstance().receivable(mailAccountId));
  }

  /**
   * 指定したユーザーがメールを受信中かどうかチェック
   * 
   * @param user
   * @return
   */
  public static boolean isReceiving(User user, int mailAccountId,
      String mailReceiveThreadStatus) {
    Object objRS =
      ALStaticObject.getInstance().getAccountStat(
        mailAccountId,
        KEY_RECEIVE_STAT);
    // メール受信を開始しているかどうかをチェック
    if ("1".equals(mailReceiveThreadStatus)) {
      return true;
    }
    if (objRS == null || (Integer) objRS != PROCESS_STAT_PROCESSING) {
      return false;
    }
    return true;
  }

  /**
   * 受信結果を取得する。
   * 
   * @param user
   * @return
   */
  public static int getReceiveMailResult(User user, int mailAccountId) {

    ALStaticObject ob = ALStaticObject.getInstance();
    Object obj = ob.getAccountStat(mailAccountId, KEY_RECEIVE_MAIL_NUM);

    if (obj == null) {
      return PROCESS_STAT_NONPROCESSING;
    }

    return ((Integer) obj).intValue();
  }

  public static String getReceiveMailResultStr(User user, int mailAccountId,
      String mailReceiveThreadStatus) {
    String msg = null;

    if (isReceiving(user, mailAccountId, mailReceiveThreadStatus)) {
      StringBuffer sb = new StringBuffer();

      Object objRMAN =
        ALStaticObject.getInstance().getAccountStat(
          mailAccountId,
          KEY_RECEIVE_MAIL_ALL_NUM);
      Object objRMN =
        ALStaticObject.getInstance().getAccountStat(
          mailAccountId,
          KEY_RECEIVE_MAIL_NUM);

      if (objRMAN != null) {
        sb
          .append("メールを受信中です（ ")
          .append(objRMAN)
          .append(" 件中 ")
          .append(objRMN)
          .append(" 件を受信 ）。");
      } else {
        sb.append("メール受信サーバに問い合わせ中です。");
      }
      return sb.toString();
    }

    // 処理が終了している場合
    int res = getReceiveMailResult(user, mailAccountId);

    if (res == PROCESS_STAT_NONPROCESSING) {
      msg = "";
      return msg;
    } else if (res == ALPop3MailReceiver.RECEIVE_MSG_FAIL) {
      msg = "メールを受信できませんでした。メールアカウントの設定をご確認ください。";
    } else if (res == ALPop3MailReceiver.RECEIVE_MSG_FAIL_LOCKED) {
      msg = "メールの受信中、もしくは、メンテナンス中です。しばらくしてから、『メール一覧』ボタンを押してください。";
    } else if (res == ALPop3MailReceiver.RECEIVE_MSG_FAIL_OVER_MAIL_MAX_SIZE) {
      int size = ALCommonUtils.getMaxFileSize();
      msg =
        (size + "MB よりも大きいサイズのメールがありました。" + size + "MBを超えたメールの場合は、送信者などの情報のみ受信し、本文は受信しません。");
    } else if (res == ALPop3MailReceiver.RECEIVE_MSG_FAIL_CONNECT) {
      msg = "設定されている受信サーバ（POP3）と接続できませんでした。";
    } else if (res == ALPop3MailReceiver.RECEIVE_MSG_FAIL_AUTH) {
      msg = "設定されている受信サーバ（POP3）へのログインに失敗しました。";
    } else if (res == ALPop3MailReceiver.RECEIVE_MSG_FAIL_EXCEPTION) {
      msg = "システム上の問題により、メールを受信できませんでした（Exception エラー）。";
    } else if (res == ALPop3MailReceiver.RECEIVE_MSG_FAIL_OUTOFMEMORY) {
      msg = "システム上の問題により、メールを受信できませんでした（OutOfMemory エラー）。";
    } else {
      msg = "メールを受信しました。";
    }

    ALStaticObject.getInstance().removeAccountId(mailAccountId);

    return msg;
  }

}
