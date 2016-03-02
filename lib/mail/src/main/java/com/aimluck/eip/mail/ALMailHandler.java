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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * メールの送受信を操作するインターフェイスです。 <br />
 *
 */
public abstract class ALMailHandler {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALMailHandler.class.getName());

  /**
   * コンストラクタ
   */
  public ALMailHandler() {
  }

  /**
   * メールを受信する．
   *
   * @return
   * @throws Exception
   */
  public int receive(ALMailReceiverContext rcontext, String orgId)
      throws Exception {

    int result = ALMailReceiver.RECEIVE_MSG_FAIL;

    try {
      // 未ロック時の場合，メールの受信処理に移る．
      ALMailReceiver receiver = getALMailReceiver(rcontext);
      result = receiver.receive(orgId);
    } catch (Exception e) {
      result = ALMailReceiver.RECEIVE_MSG_FAIL;
      logger.error("ALMailHandler.receive", e);
    }
    return result;

  }

  abstract protected ALMailReceiver getALMailReceiver(
      ALMailReceiverContext rcontext);

  /**
   * メールを送信する．
   *
   * @param userRootFolderName
   * @param accountName
   * @param smtpHost
   * @param smtpPort
   * @param to
   * @param cc
   * @param bcc
   * @param from
   * @param name
   * @param subject
   * @param msgText
   * @param filePaths
   * @param authSendFlag
   *          送信時の認証方式
   * @param authSendUserId
   *          SMTP認証時のユーザID
   * @param authSendUserPassword
   *          SMTP認証時のユーザパスワード
   * @return
   * @throws Exception
   */
  public int send(ALMailSenderContext scontext, ALMailContext mcontext)
      throws Exception {
    // synchronized (LOCK_SEND) {
    int result = ALMailSender.SEND_MSG_SUCCESS;

    try {
      ALMailSender sender = getALMailSender(scontext);
      // sender.setAuthType(authSendFlag, authSendUserId, authSendUserPassword);
      result = sender.send(mcontext);
    } catch (Exception e) {
      logger.error("ALMailHandler.send", e);
      result = ALMailSender.SEND_MSG_FAIL;
    }
    return result;
    // }
  }

  public int sendAdmin(ALMailSenderContext scontext, ALMailContext mcontext)
      throws Exception {
    // synchronized (LOCK_SEND) {
    int result = ALMailSender.SEND_MSG_SUCCESS;

    try {
      ALMailSender sender = getALAdminMailSender(scontext);
      // sender.setAuthType(authSendFlag, authSendUserId, authSendUserPassword);
      result = sender.send(mcontext);
    } catch (Exception e) {
      logger.error("ALMailHandler.sendAdmin", e);
      result = ALMailSender.SEND_MSG_FAIL;
    }
    return result;
    // }
  }

  abstract protected ALMailSender getALMailSender(ALMailSenderContext scontext);

  abstract protected ALMailSender getALAdminMailSender(
      ALMailSenderContext scontext);

  abstract public ALFolder getALFolder(int type_mail, String org_id,
      int user_id, int account_id);

  private SelectQuery<EipTMail> getUnReadMailQuery(
      ALMailReceiverContext rcontext, List<Integer> foler_ids) {
    try {
      SelectQuery<EipTMail> query = Database.query(EipTMail.class);
      if (rcontext != null) {
        if (Integer.valueOf(rcontext.getAccountId()) != null
          || Integer.valueOf(rcontext.getUserId()) != null) {
          Expression exp1 =
            ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY, Integer
              .valueOf(rcontext.getUserId()));
          Expression exp2 =
            ExpressionFactory.matchExp(EipTMail.ACCOUNT_ID_PROPERTY, Integer
              .valueOf(rcontext.getAccountId()));
          Expression exp3 =
            ExpressionFactory.matchExp(EipTMail.TYPE_PROPERTY, "R");
          Expression exp4 =
            ExpressionFactory.matchExp(EipTMail.READ_FLG_PROPERTY, "F");

          query.setQualifier(exp1.andExp(exp2).andExp(exp3).andExp(exp4));

          if (foler_ids != null && foler_ids.size() > 0) {
            Expression exp5 =
              ExpressionFactory.inExp(EipTMail.FOLDER_ID_PROPERTY, foler_ids);
            query.andQualifier(exp5);
          }

          return query;
        } else {
          return null;
        }
      } else {
        return null;
      }
    } catch (Exception e) {
      logger.error("ALMailHandler.getUnReadMailQuery", e);
      return null;
    }
  }

  /**
   * 未読メールの総数を取得する．
   *
   * @param userRootFolderName
   * @param accountName
   * @param folderName
   * @return
   */
  public int getUnReadMailSum(ALMailReceiverContext rcontext) {
    try {
      SelectQuery<EipTMail> query = getUnReadMailQuery(rcontext, null);
      if (query != null) {
        query.select(EipTMail.MAIL_ID_PK_COLUMN);

        return query.getCount();
      } else {
        return 0;
      }
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * フォルダごとの未読メールの総数を取得する．
   *
   * @param userRootFolderName
   * @param accountName
   * @param folderName
   * @return
   */
  public Map<Integer, Integer> getUnReadMailSumMap(
      ALMailReceiverContext rcontext) {
    Map<Integer, Integer> mailSumMap = new HashMap<Integer, Integer>();

    try {
      if (rcontext != null) {
        // アカウントのフォルダ一覧を取得する
        EipMMailAccount account =
          ALMailUtils.getMailAccount(rcontext.getUserId(), rcontext
            .getAccountId());
        List<EipTMailFolder> folders =
          ALMailUtils.getEipTMailFolderAll(account);
        List<Integer> folder_ids = new ArrayList<Integer>();

        // folder_idsにフォルダIDの一覧を追加し、mailSumMapのキーを設定する
        int folder_id;

        for (EipTMailFolder folder : folders) {
          folder_id = folder.getFolderId();
          folder_ids.add(Integer.valueOf(folder_id));
          mailSumMap.put(folder_id, 0);
        }

        // フォルダーごとに未読メールの数を取得する
        int count;
        for (int _foler_id : folder_ids) {
          List<Integer> list = new ArrayList<Integer>();
          list.add(_foler_id);
          SelectQuery<EipTMail> countquery = getUnReadMailQuery(rcontext, list);
          if (countquery == null) {
            count = 0;
          } else {
            count =
              countquery.orderAscending(EipTMail.FOLDER_ID_PROPERTY).getCount();
          }
          mailSumMap.put(_foler_id, count);
        }
      }
    } catch (Exception e) {
      logger.error("ALMailHandler.getUnReadMailSumMap", e);
    }
    return mailSumMap;
  }

  /**
   * 新着メール数を取得する．
   *
   * @param userRootFolderName
   * @param accountName
   * @param pop3Host
   * @param pop3Port
   * @param pop3UserId
   * @param pop3UserPasswd
   * @param denyReceivedMail
   *          受信済みメッセージを取り込まない場合は，true を設定し，新着メールと見なさない．
   * @param authReceiveFlag
   *          受信時の認証方式
   * @return 新着メール数
   * @throws Exception
   */
  public int getNewMailSum(ALMailReceiverContext rcontext) throws Exception {
    int result = -1;
    // boolean createLock = false;

    try {
      // 未ロック時の場合，新着メール数の取得処理に移る．
      ALMailReceiver receiver = getALMailReceiver(rcontext);
      if (receiver != null) {
        result = receiver.getNewMailSum();
      } else {
        result = -1;
      }
    } catch (Exception e) {
      logger.error("ALMailHandler.getNewMailSum", e);
      result = -1;
    }
    return result;
  }

  /**
   * アカウントフォルダを削除する．
   *
   * @param userRootFolderName
   * @param accountName
   * @return
   */
  abstract public boolean removeAccount(String org_id, int user_id,
      int account_id);

  public List<String> sendAdminMail(ALAdminMailContext adminMailContext)
      throws Exception {
    List<String> msgList = new ArrayList<String>();
    int destType = adminMailContext.getDestType();

    List<ALAdminMailMessage> messageList = adminMailContext.getMessageList();
    String org_id = adminMailContext.getOrgId();
    if (destType < ALMailUtils.VALUE_MSGTYPE_DEST_NONE
      || destType > ALMailUtils.VALUE_MSGTYPE_DEST_PC_CELLULAR) {
      return msgList;
    }

    if (messageList == null || messageList.size() == 0) {
      return msgList;
    }

    // メールの送信
    EipMMailAccount account = ALMailUtils.getEipMMailAccountForAdmin();
    int successSendToPc = ALSmtpMailSender.SEND_MSG_SUCCESS;
    int successSendToCell = ALSmtpMailSender.SEND_MSG_SUCCESS;

    if (account == null) {
      // メールアカウントがない場合
      if (destType == ALMailUtils.VALUE_MSGTYPE_DEST_PC) {
        successSendToPc = ALSmtpMailSender.SEND_MSG_FAIL_NO_ACCOUNT;
      } else if (destType == ALMailUtils.VALUE_MSGTYPE_DEST_CELLULAR) {
        successSendToCell = ALSmtpMailSender.SEND_MSG_FAIL_NO_ACCOUNT;
      } else {
        successSendToPc = ALSmtpMailSender.SEND_MSG_FAIL_NO_ACCOUNT;
        successSendToCell = ALSmtpMailSender.SEND_MSG_FAIL_NO_ACCOUNT;
      }
    } else {

      ALMailHandler mailhandler =
        ALMailFactoryService.getInstance().getMailHandler();
      // 送信サーバ情報
      ALMailSenderContext scontext =
        ALMailUtils.getALSmtpMailSenderContext(org_id, account);

      // パソコンへメールを送信
      if ((destType == ALMailUtils.VALUE_MSGTYPE_DEST_PC || destType == ALMailUtils.VALUE_MSGTYPE_DEST_PC_CELLULAR)) {
        for (ALAdminMailMessage message : messageList) {
          if (!ALEipUtils.isEnabledUser(message.getUserId())) {
            continue;
          }
          String emailAddr = message.getPcMailAddr();
          if (emailAddr == null || emailAddr.equals("")) {
            continue;
          }
          String[] tos = new String[] { emailAddr };

          // 送信メッセージのコンテキスト
          ALSmtpMailContext mailcontext =
            ALMailUtils.getALSmtpMailContext(
              tos,
              null,
              null,
              account.getMailAddress(),
              ALStringUtil.unsanitizing(account.getMailUserName()),
              ALStringUtil.unsanitizing(message.getPcSubject()),
              ALStringUtil.unsanitizing(message.getPcBody()),
              null,
              null);

          successSendToPc = mailhandler.sendAdmin(scontext, mailcontext);
        }
      }

      // 携帯電話へメールを送信
      if ((destType == ALMailUtils.VALUE_MSGTYPE_DEST_CELLULAR || destType == ALMailUtils.VALUE_MSGTYPE_DEST_PC_CELLULAR)) {
        for (ALAdminMailMessage message : messageList) {
          if (!ALEipUtils.isEnabledUser(message.getUserId())) {
            continue;
          }
          String emailAddr = message.getCellMailAddr();
          if (emailAddr == null || emailAddr.equals("")) {
            continue;
          }
          String[] tos = new String[] { emailAddr };

          ALSmtpMailContext mailcontext =
            ALMailUtils.getALSmtpMailContext(tos, null, null, account
              .getMailAddress(), ALStringUtil.unsanitizing(account
              .getMailUserName()), ALStringUtil.unsanitizing(message
              .getCellularSubject()), ALStringUtil.unsanitizing(message
              .getCellularBody()), null, null);

          successSendToCell = mailhandler.sendAdmin(scontext, mailcontext);
        }
      }
    }

    if (successSendToPc != ALSmtpMailSender.SEND_MSG_SUCCESS) {
      if (successSendToPc == ALSmtpMailSender.SEND_MSG_OVER_MAIL_MAX_SIZE) {
        msgList.add("メールサイズが送信可能サイズよりも大きいため、パソコンのメールアドレスにメールを送信できませんでした。");
      } else if (successSendToPc == ALSmtpMailSender.SEND_MSG_LOCK) {
        msgList.add("ロックがかかっていて、パソコンのメールアドレスにメールを送信できませんでした。");
      } else if (successSendToPc == ALSmtpMailSender.SEND_MSG_FAIL_POP_BEFORE_SMTP_AUTH) {
        msgList.add("Pop before SMTPの認証に失敗したため、パソコンのメールアドレスにメールを送信できませんでした。");
      } else if (successSendToPc == ALSmtpMailSender.SEND_MSG_FAIL_SMTP_AUTH) {
        msgList.add("SMTP認証の認証に失敗したため、パソコンのメールアドレスにメールを送信できませんでした。");
      } else if (successSendToPc == ALSmtpMailSender.SEND_MSG_FAIL_NO_ACCOUNT) {
        msgList.add("管理者のメールアカウントが設定されていないため、パソコンのメールアドレスにメールを送信できませんでした。");
      } else {
        msgList.add("送信メールサーバに接続できなかったため、パソコンのメールアドレスにメールを送信できませんでした。");
      }
    }

    if (successSendToCell != ALSmtpMailSender.SEND_MSG_SUCCESS) {
      if (successSendToCell == ALSmtpMailSender.SEND_MSG_OVER_MAIL_MAX_SIZE) {
        msgList.add("メールサイズが送信可能サイズよりも大きいため、携帯のメールアドレスにメールを送信できませんでした。");
      } else if (successSendToCell == ALSmtpMailSender.SEND_MSG_LOCK) {
        msgList.add("ロックがかかっていて、携帯のメールアドレスにメールを送信できませんでした。");
      } else if (successSendToCell == ALSmtpMailSender.SEND_MSG_FAIL_POP_BEFORE_SMTP_AUTH) {
        msgList.add("Pop before SMTPの認証に失敗したため、携帯のメールアドレスにメールを送信できませんでした。");
      } else if (successSendToCell == ALSmtpMailSender.SEND_MSG_FAIL_SMTP_AUTH) {
        msgList.add("SMTP認証の認証に失敗したため、携帯のメールアドレスにメールを送信できませんでした。");
      } else if (successSendToCell == ALSmtpMailSender.SEND_MSG_FAIL_NO_ACCOUNT) {
        msgList.add("管理者のメールアカウントが設定されていないため、携帯のメールアドレスにメールを送信できませんでした。");
      } else {
        msgList.add("送信メールサーバに接続できなかったため、携帯のメールアドレスにメールを送信できませんでした。");
      }
    }

    return msgList;
  }

  /**
   * アカウントフォルダ容量計算
   *
   * @param userRootFolderName
   * @param accountName
   * @return
   */
  abstract public long getFolderSize(String org_id, int user_id, int account_id);

}
