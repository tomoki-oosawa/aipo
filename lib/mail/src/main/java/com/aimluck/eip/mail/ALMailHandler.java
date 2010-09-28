/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * メールの送受信を操作するインターフェイスです。 <br />
 * 
 */
public abstract class ALMailHandler {

  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(ALMailHandler.class.getName());

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
      logger.error("Exception", e);
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
      logger.error("Exception", e);
      result = ALMailSender.SEND_MSG_FAIL;
    }
    return result;
    // }
  }

  abstract protected ALMailSender getALMailSender(ALMailSenderContext scontext);

  abstract public ALFolder getALFolder(int type_mail, String org_id,
      int user_id, int account_id);

  private SelectQuery<EipTMail> getUnReadMailQuery(
      ALMailReceiverContext rcontext) {
    try {
      SelectQuery<EipTMail> query = Database.query(EipTMail.class);

      Expression exp1 =
        ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY, Integer
          .valueOf(rcontext.getUserId()));
      Expression exp2 =
        ExpressionFactory.matchExp(EipTMail.ACCOUNT_ID_PROPERTY, Integer
          .valueOf(rcontext.getAccountId()));
      Expression exp3 = ExpressionFactory.matchExp(EipTMail.TYPE_PROPERTY, "R");
      Expression exp4 =
        ExpressionFactory.matchExp(EipTMail.READ_FLG_PROPERTY, "F");

      return query.setQualifier(exp1.andExp(exp2).andExp(exp3).andExp(exp4));
    } catch (Exception e) {
      logger.error("error", e);
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
      SelectQuery<EipTMail> query = getUnReadMailQuery(rcontext);

      query.select(EipTMail.MAIL_ID_PK_COLUMN);

      List<?> mails = query.fetchList();

      return (mails != null) ? mails.size() : 0;
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
      // アカウントのフォルダ一覧を取得する
      EipMMailAccount account =
        ALMailUtils.getMailAccount(
          rcontext.getOrgId(),
          rcontext.getUserId(),
          rcontext.getAccountId());
      List<EipTMailFolder> folders = ALMailUtils.getEipTMailFolderAll(account);
      List<Integer> folder_ids = new ArrayList<Integer>();

      // folder_idsにフォルダIDの一覧を追加し、mailSumMapのキーを設定する
      int folder_id;
      for (EipTMailFolder folder : folders) {
        folder_id = folder.getFolderId();
        folder_ids.add(folder_id);
        mailSumMap.put(folder_id, 0);
      }

      // 未読メールをフォルダIDでソートして取得する
      SelectQuery<EipTMail> query = getUnReadMailQuery(rcontext);

      List<EipTMail> mail_list =
        query.orderAscending(EipTMail.FOLDER_ID_PROPERTY).fetchList();

      // 取得したメールの数をフォルダごとに数え、mailSumMap に代入する。
      int counting_folder_id = (mail_list.get(0)).getFolderId(), count = 0;
      folder_id = 0;

      for (EipTMail mail : mail_list) {
        folder_id = mail.getFolderId();
        if (folder_id != counting_folder_id) {
          mailSumMap.put(counting_folder_id, count);
          count = 0;
          counting_folder_id = folder_id;
        }
        count++;
      }
      mailSumMap.put(folder_id, count);
    } catch (Exception e) {
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
      logger.error("Exception", e);
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

}
