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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.mail.util.ALStaticObject;
import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Message;

/**
 * メール受信（POP3）を操作する抽象クラスです。 <br />
 * 
 */
public abstract class ALPop3MailReceiver implements ALMailReceiver {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALPop3MailReceiver.class.getName());

  /** <code>AUTH_RECEIVE_NORMAL</code> 受信時の認証方式（標準） */
  public static final int AUTH_RECEIVE_NORMAL = 0;

  /** <code>AUTH_RECEIVE_APOP</code> 受信時の認証方式（APOP） */
  public static final int AUTH_RECEIVE_APOP = 1;

  /** <code>AUTH_SEND_SSL_AUTH</code> 送信時の認証方式（暗号化なし） */
  public static final int ENCRYPTION_SEND_NONE = 0;

  /** <code>AUTH_SEND_SSL_AUTH</code> 送信時の認証方式（SSL暗号化） */
  public static final int ENCRYPTION_SEND_SSL = 1;

  /** メール受信時の処理結果（ロックがかかっていて，受信に失敗した） */
  public static final int RECEIVE_MSG_FAIL_LOCKED = -2;

  /** メール受信時の処理結果（メールサイズが受信可能サイズよりも大きいため，受信に失敗した） */
  public static final int RECEIVE_MSG_FAIL_OVER_MAIL_MAX_SIZE = -3;

  /** メール受信時の処理結果（POP3 サーバと接続できず，受信に失敗した） */
  public static final int RECEIVE_MSG_FAIL_CONNECT = -4;

  /** メール受信時の処理結果（POP3 サーバとの認証に失敗し，受信に失敗した） */
  public static final int RECEIVE_MSG_FAIL_AUTH = -5;

  /** メール受信時の処理結果（Exception のエラーが発生し，受信に失敗した） */
  public static final int RECEIVE_MSG_FAIL_EXCEPTION = -6;

  /** メール受信時の処理結果（OutOfMemory のエラーが発生し，受信に失敗した） */
  public static final int RECEIVE_MSG_FAIL_OUTOFMEMORY = -7;

  /** 接続時のタイムアウト時間 */
  private static final String CONNECTION_TIMEOUT = "60000";

  /** 接続後のタイムアウト時間 */
  private static final String TIMEOUT = "300000";

  /** UIDに追記する件数 */
  private static final int UID_ADD_NUM = 100;

  protected ALPop3MailReceiverContext rcontext;

  /** SSL ファクトリー */
  public static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

  /** 現在の受信メール数 */
  private int receivedMailNum = 0;

  /** 現在の受信可能メール総数 */
  private int mailNumOnServer = 0;

  /**
   * コンストラクタ
   * 
   * @param rcontext
   */
  public ALPop3MailReceiver(ALMailReceiverContext rcontext) {
    this.rcontext = (ALPop3MailReceiverContext) rcontext;
  }

  protected abstract ALFolder getALFolder();

  /**
   * POP3 サーバからメールを受信する．
   * 
   * @param delete
   *          受信したメールを削除するかどうか．削除する場合は，true．
   * @param enableSavingDays
   *          指定日数を超えたメールを削除するかどうか．削除する場合は，true．
   * @param savingDays
   *          メール削除の指定日数
   * @param denyReceivedMail
   *          受信済みメッセージを取り込まない場合は，true．
   * @param authReceiveFlag
   *          受信時の認証方式
   * @throws Exception
   */
  @Override
  public int receive(String orgId) throws Exception {

    // POP3 サーバ上のストア
    Store pop3Store = null;
    // POP3 サーバ上のフォルダ
    POP3Folder pop3Folder = null;
    // ローカルストア
    ALFolder receiveFolder = null;
    // このセッション中のメールの UID の一覧
    List<String> newUIDL = null;
    // このセッションで保存したメールの UID の一覧
    List<String> receivedUIDL = new ArrayList<String>();
    // 今，受信しているメールの UID
    String nowReceivedUID = null;
    boolean overMailMaxSize = false;

    // 一通のメールの受信を完了したかのフラグ
    boolean finishedReceiving = false;

    try {
      // POP3 サーバへの接続
      Session session =
        getSession(rcontext.getAuthReceiveFlag(), rcontext.getEncryptionFlag());
      pop3Store = session.getStore("pop3");
      pop3Store
        .connect(rcontext.getPop3Host(), Integer.parseInt(rcontext
          .getPop3Port()), rcontext.getPop3UserId(), rcontext
          .getPop3UserPasswd());

      if (!pop3Store.isConnected()) {
        // POP3 サーバへの接続失敗時の処理
        close(pop3Store, pop3Folder, receiveFolder);
        return RECEIVE_MSG_FAIL_CONNECT;
      }

      // POP3 サーバ上のメールフォルダを開く
      pop3Folder = (POP3Folder) pop3Store.getFolder("INBOX");
      if (pop3Folder == null) {
        close(pop3Store, null, receiveFolder);
        return RECEIVE_MSG_FAIL;
      }
      pop3Folder.open(Folder.READ_WRITE);

      // ローカルストアに接続
      receiveFolder = getALFolder();
      if (receiveFolder == null) {
        close(pop3Store, pop3Folder, null);
        return RECEIVE_MSG_FAIL;
      }

      // POP3 サーバに保存されているメッセージ数を取得
      int totalMessages = pop3Folder.getMessageCount();
      if (totalMessages == 0) {
        // 新着メール数を保存する．
        receiveFolder.setNewMailNum(0);
        close(pop3Store, pop3Folder, receiveFolder);
        receivedMailNum = 0;
        return receivedMailNum;
      }

      // 保存してある UID の一覧を取得
      List<String> oldUIDL = receiveFolder.loadUID();

      // 受信するメールの UID の一覧を取得
      newUIDL = new ArrayList<String>();
      Message[] messages = pop3Folder.getMessages();

      String uid = null;
      totalMessages = messages.length;
      for (int i = 0; i < totalMessages; i++) {
        uid = pop3Folder.getUID(messages[i]);
        if (uid == null) {
          String[] xuidls = messages[i].getHeader("X-UIDL");
          if (xuidls != null && xuidls.length > 0) {
            uid = xuidls[0];
          } else {
            uid = ((MimeMessage) messages[i]).getMessageID();
          }
        }
        newUIDL.add(uid);
      }

      // UID の差分を取得
      BitSet retrieveFlags = new BitSet();
      if (rcontext.getDenyReceivedMail()) {
        for (int i = 0; i < totalMessages; i++) {
          if (!oldUIDL.contains(newUIDL.get(i))) {
            retrieveFlags.set(i);
            mailNumOnServer++;
          } else {
            receivedUIDL.add(newUIDL.get(i));
          }
        }
      } else {
        for (int i = 0; i < totalMessages; i++) {
          retrieveFlags.set(i);
        }
        mailNumOnServer = totalMessages;
      }
      oldUIDL.clear();
      newUIDL.clear();

      ALStaticObject.getInstance().updateAccountStat(
        rcontext.getAccountId(),
        ALPop3MailReceiveThread.KEY_RECEIVE_MAIL_ALL_NUM,
        Integer.valueOf(mailNumOnServer));

      // 現時点から指定日数を引いた日時を取得．
      Calendar cal = Calendar.getInstance();
      cal.setTime(new Date());
      cal.set(Calendar.DATE, cal.get(Calendar.DATE) - rcontext.getSavingDays());
      Date limitDate = cal.getTime();

      MimeMessage tmpMessage = null;
      ALMailMessage alPop3MailMessage = null;
      for (int i = 0; i < totalMessages; i++) {
        finishedReceiving = false;
        tmpMessage = (MimeMessage) pop3Folder.getMessage(i + 1);
        // 新着メールであるかを確認
        if (retrieveFlags.get(i)) {
          nowReceivedUID = pop3Folder.getUID(tmpMessage);

          if (nowReceivedUID == null) {
            String[] xuidls = tmpMessage.getHeader("X-UIDL");
            if (xuidls != null && xuidls.length > 0) {
              nowReceivedUID = xuidls[0];
            } else {
              nowReceivedUID = tmpMessage.getMessageID();
            }
          }
          // （新着メール）メールを受信し保存する．
          alPop3MailMessage =
            new ALPop3Message((POP3Message) tmpMessage, i + 1);
          if (tmpMessage.getSize() <= ALMailUtils.getMaxMailSize()) {
            // 受信可能なメール容量以下であれば，登録する．
            if (receiveFolder.saveMail(alPop3MailMessage, orgId)) {
              if (rcontext.getDelete()) {
                // 受信したメールを POP3 サーバ上から削除する
                tmpMessage.setFlag(Flags.Flag.DELETED, rcontext.getDelete());
              } else {
                if (rcontext.getEnableSavingDays()) {
                  // 指定日数を過ぎたメールを削除する（ヘッダ Received の日付で判断する）．
                  Date receivedDate = ALMailUtils.getReceivedDate(tmpMessage);
                  if (receivedDate != null && receivedDate.before(limitDate)) {
                    // 受信したメールを POP3 サーバ上から削除する
                    tmpMessage.setFlag(Flags.Flag.DELETED, true);
                  }
                }
              }
              receivedUIDL.add(nowReceivedUID);
              receivedMailNum++;
            }
          } else {
            if (receiveFolder.saveDefectiveMail(alPop3MailMessage, orgId)) {
              receivedUIDL.add(nowReceivedUID);
              receivedMailNum++;
            }
            overMailMaxSize = true;
          }

          ALStaticObject.getInstance().updateAccountStat(
            rcontext.getAccountId(),
            ALPop3MailReceiveThread.KEY_RECEIVE_MAIL_NUM,
            Integer.valueOf(receivedMailNum));

          if (receivedMailNum % UID_ADD_NUM == 0 && receivedMailNum != 0) {
            receiveFolder.saveUID(receivedUIDL);
          }
        }

        finishedReceiving = true;
      }

      // POP3 サーバとの接続を閉じる
      close(pop3Store, pop3Folder, null);
    } catch (AuthenticationFailedException ae) {
      logger.error("ALPop3MailReceiver.receive", ae);
      // 受信済みの最新の UID の一覧を保存する．
      if (!finishedReceiving) {
        receivedUIDL.remove(nowReceivedUID);
      }
      receiveFolder.saveUID(receivedUIDL);
      if (receivedUIDL != null) {
        receivedUIDL.clear();
      }
      // 新着メール数を保存する．
      receiveFolder.setNewMailNum(receivedMailNum);
      // サーバとのコネクションを切断する．
      close(pop3Store, pop3Folder, receiveFolder);
      return RECEIVE_MSG_FAIL_AUTH;
    } catch (MessagingException me) {
      logger.error("ALPop3MailReceiver.receive", me);
      // 受信済みの最新の UID の一覧を保存する．
      if (!finishedReceiving) {
        receivedUIDL.remove(nowReceivedUID);
      }
      receiveFolder.saveUID(receivedUIDL);
      if (receivedUIDL != null) {
        receivedUIDL.clear();
      }
      // 新着メール数を保存する．
      receiveFolder.setNewMailNum(receivedMailNum);
      // サーバとのコネクションを切断する．
      close(pop3Store, pop3Folder, receiveFolder);
      return RECEIVE_MSG_FAIL_CONNECT;
    } catch (Exception e) {
      logger.error("ALPop3MailReceiver.receive", e);
      // 受信済みの最新の UID の一覧を保存する．
      if (!finishedReceiving) {
        receivedUIDL.remove(nowReceivedUID);
      }
      receiveFolder.saveUID(receivedUIDL);
      if (receivedUIDL != null) {
        receivedUIDL.clear();
      }
      // 新着メール数を保存する．
      receiveFolder.setNewMailNum(receivedMailNum);
      // サーバとのコネクションを切断する．
      close(pop3Store, pop3Folder, receiveFolder);
      return RECEIVE_MSG_FAIL_EXCEPTION;
    } catch (Throwable t) {
      logger.error("ALPop3MailReceiver.receive", t);
      // 受信済みの最新の UID の一覧を保存する．
      if (!finishedReceiving) {
        receivedUIDL.remove(nowReceivedUID);
      }
      receiveFolder.saveUID(receivedUIDL);
      if (receivedUIDL != null) {
        receivedUIDL.clear();
      }
      // 新着メール数を保存する．
      receiveFolder.setNewMailNum(receivedMailNum);
      // サーバとのコネクションを切断する．
      close(pop3Store, pop3Folder, receiveFolder);
      return RECEIVE_MSG_FAIL_OUTOFMEMORY;
    }

    try {
      // 受信済みの最新の UID の一覧を保存する．
      receiveFolder.saveUID(receivedUIDL);
      if (receivedUIDL != null) {
        receivedUIDL.clear();
      }
      // 新着メール数を保存する．
      receiveFolder.setNewMailNum(receivedMailNum);

      // ローカルフォルダを閉じる．
      receiveFolder.close();
    } catch (Exception e) {
      logger.error("ALPop3MailReceiver.receive", e);
      return RECEIVE_MSG_FAIL_EXCEPTION;
    } catch (Throwable t) {
      logger.error("ALPop3MailReceiver.receive", t);
      return RECEIVE_MSG_FAIL_OUTOFMEMORY;
    }

    if (overMailMaxSize) {
      return RECEIVE_MSG_FAIL_OVER_MAIL_MAX_SIZE;
    }
    return RECEIVE_MSG_SUCCESS;
  }

  /**
   * POP3 サーバとの接続を終了する．
   */
  public void close(Store pop3Store, Folder pop3Folder, ALFolder receiveFolder) {
    try {
      if (pop3Folder != null && pop3Folder.isOpen()) {
        pop3Folder.close(true);
      }
      if (pop3Store != null) {
        pop3Store.close();
      }
      if (receiveFolder != null) {
        receiveFolder.close();
      }
    } catch (Exception e) {
      logger.error("ALPop3MailReceiver.close", e);
    }
  }

  /**
   * Pop3 before SMTP 用のユーザ認証．
   * 
   * @param pop3Host
   * @param pop3Port
   * @param pop3UserId
   * @param pop3UserPasswd
   * @param pop3EncryptionFlag
   * @return
   */
  public static boolean isAuthenticatedUser(String pop3Host, String pop3Port,
      String pop3UserId, String pop3UserPasswd, int pop3EncryptionFlag) {
    boolean res = false;
    Store pop3Store = null;
    try {
      Properties props = new Properties();
      // POP3 サーバへの接続
      if (pop3EncryptionFlag == ENCRYPTION_SEND_SSL) {
        /** SSL 暗号化 */
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", pop3Port);
        props.setProperty("mail.pop3.socketFactory.port", pop3Port);
      }
      props.setProperty("mail.pop3.connectiontimeout", CONNECTION_TIMEOUT);
      props.setProperty("mail.pop3.timeout", TIMEOUT);
      if (pop3EncryptionFlag == AUTH_RECEIVE_APOP) {
        props.setProperty("mail.pop3.apop.enable", "true");
      }
      Session session = Session.getInstance(props, null);
      pop3Store = session.getStore("pop3");
      pop3Store.connect(
        pop3Host,
        Integer.parseInt(pop3Port),
        pop3UserId,
        pop3UserPasswd);
      res = pop3Store.isConnected();
    } catch (Exception ex) {
      logger.error("ALPop3MailReceiver.isAuthenticatedUser", ex);
      res = false;
    } finally {
      if (pop3Store != null && pop3Store.isConnected()) {
        try {
          pop3Store.close();
        } catch (MessagingException ex) {
          logger.error("ALPop3MailReceiver.isAuthenticatedUser", ex);
          res = false;
        }
      }
    }
    return res;
  }

  /**
   * 新着メール数を取得する．
   * 
   * @param denyReceivedMail
   *          受信済みメッセージを取り込まない場合は，true．
   * @param authReceiveFlag
   *          受信時の認証方式
   * @return
   */
  @Override
  public int getNewMailSum() {
    // POP3 サーバ上のストア
    Store pop3Store = null;
    // POP3 サーバ上のフォルダ
    POP3Folder pop3Folder = null;
    // ローカルストア
    ALFolder receiveFolder = null;

    int newMailSum = -1;
    try {
      // POP3 サーバへの接続
      Session session =
        getSession(rcontext.getAuthReceiveFlag(), rcontext.getEncryptionFlag());
      pop3Store = session.getStore("pop3");
      pop3Store
        .connect(rcontext.getPop3Host(), Integer.parseInt(rcontext
          .getPop3Port()), rcontext.getPop3UserId(), rcontext
          .getPop3UserPasswd());

      // POP3 サーバ上のメールフォルダを開く
      pop3Folder = (POP3Folder) pop3Store.getFolder("INBOX");
      if (pop3Folder == null) {
        // POP3 サーバとの接続を閉じる
        pop3Store.close();
        return -1;
      }
      pop3Folder.open(Folder.READ_WRITE);

      // ローカルストアに接続
      receiveFolder = getALFolder();
      if (receiveFolder == null) {
        // POP3 サーバとの接続を閉じる
        pop3Folder.close(true);
        pop3Store.close();
        return -1;
      }

      // POP3 サーバに保存されているメッセージ数を取得
      int totalMessages = pop3Folder.getMessageCount();
      if (totalMessages == 0) {
        // POP3 サーバとの接続を閉じる
        close(pop3Store, pop3Folder, receiveFolder);
        return 0;
      }

      if (!rcontext.getDenyReceivedMail()) {
        // POP3 サーバとの接続を閉じる
        close(pop3Store, pop3Folder, receiveFolder);

        // 受信済みメッセージを取り込む場合，
        // POP3 サーバ上に存在するメール数を新着メール数として返す．
        return totalMessages;
      }

      // ローカルに保存してある UID の一覧を取得
      List<String> oldUIDL = receiveFolder.loadUID();

      // POP3 サーバから UID を取得
      // 受信するメールの UID の一覧を取得
      List<String> newUIDL = new ArrayList<String>();
      Message[] messages = pop3Folder.getMessages();
      String uid = null;
      totalMessages = messages.length;
      for (int i = 0; i < totalMessages; i++) {
        uid = pop3Folder.getUID(messages[i]);
        if (uid == null) {
          String[] xuidls = messages[i].getHeader("X-UIDL");
          if (xuidls != null && xuidls.length > 0) {
            uid = xuidls[0];
          } else {
            uid = ((MimeMessage) messages[i]).getMessageID();
          }
        }
        newUIDL.add(uid);
      }

      // UID の差分から新着数を取得
      newMailSum = 0;
      for (int i = 0; i < totalMessages; i++) {
        if (!oldUIDL.contains(newUIDL.get(i))) {
          newMailSum++;
        }
      }

      // POP3 サーバとの接続を閉じる
      close(pop3Store, pop3Folder, receiveFolder);
    } catch (MessagingException e) {
      logger.error("ALPop3MailReceiver.getNewMailSum", e);
      // POP3 サーバとの接続を閉じる
      close(pop3Store, pop3Folder, null);
      return -1;
    }
    return newMailSum;
  }

  /**
   * 
   * @param authReceiveFlag
   *          受信時の認証方式
   * @param encryptFlag
   *          暗号化方式
   * @return
   */
  private Session getSession(int authReceiveFlag, int encryptFlag) {
    Properties props = new Properties();
    // POP3 サーバへの接続
    if (encryptFlag == ENCRYPTION_SEND_SSL) {
      /** SSL 暗号化 */
      props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
      props.setProperty("mail.pop3.socketFactory.fallback", "false");
      props.setProperty("mail.pop3.port", rcontext.getPop3Port());
      props.setProperty("mail.pop3.socketFactory.port", rcontext.getPop3Port());
    }
    props.setProperty("mail.pop3.connectiontimeout", CONNECTION_TIMEOUT);
    props.setProperty("mail.pop3.timeout", TIMEOUT);
    if (authReceiveFlag == AUTH_RECEIVE_APOP) {
      props.setProperty("mail.pop3.apop.enable", "true");
    }
    Session session = Session.getInstance(props, null);
    return session;
  }
}
