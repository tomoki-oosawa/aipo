/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.webmail.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFilter;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.mail.ALFolder;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.ALMailMessage;
import com.aimluck.eip.mail.ALMailReceiverContext;
import com.aimluck.eip.mail.ALPop3MailReceiveThread;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.WebMailFormData;

/**
 */
public class WebMailUtils {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebMailUtils.class.getName());

  /** セッションの識別子 */
  public static final String FOLDER_ID = ALMailUtils.FOLDER_ID;

  public static final String FILTER_ID = "filterid";

  /** タブ「受信トレイ」 */
  public static final String TAB_RECEIVE = "receive";

  /** タブ「送信トレイ」 */
  public static final String TAB_SENT = "sent";

  public static final String DATE_TIME_FORMAT = ALDateTimeField.DEFAULT_DATE_TIME_FORMAT;

  public static final String CREATED_DATE_FORMAT = ALDateTimeField.DEFAULT_DATE_FORMAT;

  public final static String ACCOUNT_ID = "accountid";

  public final static String ACCOUNT_NAME = "accountname";

  public final static String MAIL_TYPE = "mailtype";

  public final static String CONFIRM_LAST_TIME = "confirmlasttime";

  /** フィルタタイプ */
  public final static String FILTER_TYPE_MAILADDRESS = ALMailUtils.FILTER_TYPE_MAILADDRESS;

  public final static String FILTER_TYPE_DOMAIN = ALMailUtils.FILTER_TYPE_DOMAIN;

  public final static String FILTER_TYPE_SUBJECT = ALMailUtils.FILTER_TYPE_SUBJECT;

  public static final List getMailAccountNameList(int userId) {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query = new SelectQuery(EipMMailAccount.class);
    query.addCustomDbAttribute(EipMMailAccount.ACCOUNT_ID_PK_COLUMN);
    query.addCustomDbAttribute(EipMMailAccount.ACCOUNT_NAME_COLUMN);
    Expression exp = ExpressionFactory.matchExp(
        EipMMailAccount.USER_ID_PROPERTY, Integer.valueOf(userId));
    query.setQualifier(exp);

    return dataContext.performQuery(query);
  }

  /**
   * 選択したメールをローカルファイルシステムから取得する．
   *
   * @param rundata
   * @param context
   * @return
   * @throws Exception
   */
  public static final ALMailMessage getSelectedLocalMailMessage(
      RunData rundata, Context context, int mailType) throws Exception {
    String org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
    int uid = ALEipUtils.getUserId(rundata);
    int accountId = Integer.parseInt(ALEipUtils.getTemp(rundata, context,
        ACCOUNT_ID));

    String mailid = ALEipUtils.getTemp(rundata, context,
        ALEipConstants.ENTITY_ID);
    if (mailid == null || Integer.valueOf(mailid) == null) {
      // Mail IDが空の場合
      logger.debug("[Mail] Empty ID...");
      return null;
    }

    String currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    int type_mail = (WebMailUtils.TAB_RECEIVE.equals(currentTab)) ? ALFolder.TYPE_RECEIVE
        : ALFolder.TYPE_SEND;
    ALMailHandler handler = ALMailFactoryService.getInstance().getMailHandler();
    ALFolder folder = handler.getALFolder(type_mail, org_id, uid, accountId);
    ALMailMessage msg = folder.getMail(Integer.valueOf(mailid));

    if (WebMailFormData.TYPE_REPLY_MAIL == mailType) {
      return ALMailUtils.getReplyMessage(msg);
    } else if (WebMailFormData.TYPE_FORWARD_MAIL == mailType) {
      return ALMailUtils.getForwardMessage(msg);
    } else {
      return msg;
    }
  }

  /**
   * 複数のメールアドレスを含む文字列の中のメールアドレス形式をチェックします。 想定メール形式は address@aimluck.com xxxyyy
   * <address@aimluck.com>
   *
   * @param argstr
   *            複数メールアドレス
   * @param delim
   * @return
   */
  public static boolean checkAddress(String argstr, String delim) {
    String[] addresses;
    if (argstr == null || argstr.trim().length() == 0)
      return false;
    addresses = ALMailUtils.getTokens(argstr, delim);
    for (int i = 0; i < addresses.length; i++) {
      String str = addresses[i].trim();
      if (str.length() == 0)
        continue;
      if (str.charAt(str.length() - 1) == '>') {
        // 氏名付きアドレス指定 sei mei <seimei@xxx.com>
        int idx = str.indexOf("<");
        // "<"がなければエラー
        if (idx == -1) {
          return false;
        }
        String address = str.substring(idx + 1, str.length() - 1);
        if (!ALStringUtil.isCellPhoneMailAddress(address)) {
          return false;
        }
      } else {
        // アドレス指定のみ
        if (!ALStringUtil.isCellPhoneMailAddress(str)) {
          return false;
        }
      }
    }
    return true;
  }

  public static String checkUnusualChar(String str) {
    ArrayList unusualChars = new ArrayList();

    /**
     * 文字化けを起こす特殊記号 【囲み英数字／ローマ数字】①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ
     * 【単位記号】㍉㌔㌢㍍㌘㌧㌃㌶㍑㍗㌍・㌣㌫㍊㌻㎜㎝㎞㎎㎏㏄㎡ 【省略文字／囲み文字／年号】㍻〝〟№㏍℡㊤㊥㊦㊧㊨㈱㈲㈹㍾㍽㍼∮∑∟⊿
     */
    char[] unusualchars = { '①', '②', '③', '④', '⑤', '⑥', '⑦', '⑧', '⑨', '⑩',
        '⑪', '⑫', '⑬', '⑭', '⑮', '⑯', '⑰', '⑱', '⑲', '⑳', 'Ⅰ', 'Ⅱ', 'Ⅲ', 'Ⅳ',
        'Ⅴ', 'Ⅵ', 'Ⅶ', 'Ⅷ', 'Ⅸ', 'Ⅹ', '㍉', '㌔', '㌢', '㍍', '㌘', '㌧', '㌃', '㌶',
        '㍑', '㍗', '㌍', '・', '㌣', '㌫', '㍊', '㌻', '㎜', '㎝', '㎞', '㎎', '㎏', '㏄',
        '㎡', '㍻', '〝', '〟', '№', '㏍', '℡', '㊤', '㊥', '㊦', '㊧', '㊨', '㈱', '㈲',
        '㈹', '㍾', '㍽', '㍼', '∮', '∑', '∟', '⊿' };
    int unusuallen = unusualchars.length;
    int length = str.length();
    Character cha = null;
    for (int i = 0; i < length; i++) {
      for (int j = 0; j < unusuallen; j++) {
        if (str.charAt(i) == unusualchars[j]) {
          cha = Character.valueOf(unusualchars[j]);
          if (!unusualChars.contains(cha)) {
            unusualChars.add(cha);
          }
        }
      }
    }

    StringBuffer sb = new StringBuffer();
    if (unusualChars.size() < 1) {
      return null;
    }
    length = unusualChars.size() - 1;
    for (int i = 0; i < length; i++) {
      sb.append("\"").append(unusualChars.get(i)).append("\"").append(",");
    }
    sb.append("\"").append(unusualChars.get(length)).append("\"");
    return sb.toString();
  }

  /**
   * POP3 サーバからメールを受信する。
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public static void receiveMailsThread(RunData rundata, Context context)
      throws Exception {
    synchronized (ALPop3MailReceiveThread.KEY_SYNCHRONIZED_LOCK) {
      JetspeedRunData jdata = (JetspeedRunData) rundata;
      JetspeedUser user = (JetspeedUser) jdata.getUser();

      // 現在使用中のアカウントIDを取得
      int accountId = 0;
      try {
        accountId = Integer.parseInt(ALEipUtils.getTemp(rundata, context,
            ACCOUNT_ID));
      } catch (Exception ex) {
      }
      if (accountId <= 0) {
        return;
      }

      // アカウントがユーザーのものであるかどうかチェックする
      EipMMailAccount account = ALMailUtils.getMailAccount(null, Integer
          .parseInt(user.getUserId()), accountId);
      if (account == null) {
        return;
      }

      if (!ALPop3MailReceiveThread.isProcessing(user, accountId)) {
        // メールと接続してなければ新規にスレッドを生成
        String orgId = DatabaseOrmService.getInstance().getOrgId(rundata);
        Runnable receiver = new ALPop3MailReceiveThread(orgId, user, accountId,
            ALPop3MailReceiveThread.PROCESS_TYPE_RECEIVEMAIL);
        Thread mailthread = new Thread(receiver);

        // ALStaticObject ob = ALStaticObject.getInstance();
        // ob.updateAccountStat(accountId, ALPop3MailReceiveThread.KEY_THREAD,
        // mailthread);
        mailthread.start();
      }
    }
  }

  /**
   * POP3 サーバから新着メール数を取得する。
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public static int getNewMailNumThread(String orgId, JetspeedUser user,
      int accountId) throws Exception {
    synchronized (ALPop3MailReceiveThread.KEY_SYNCHRONIZED_LOCK) {
      if (!ALPop3MailReceiveThread.isProcessing(user, accountId)) {
        // メールと接続してなければ新規にスレッドを生成
        Thread mailthread = new Thread(new ALPop3MailReceiveThread(orgId, user,
            accountId, ALPop3MailReceiveThread.PROCESS_TYPE_GET_NEWMAILNUM));
        mailthread.start();
      }

      return ALPop3MailReceiveThread.getNewMailNum(user, accountId);
    }
  }

  // /**
  // * 指定されたアカウントのメール受信を中断する。
  // *
  // * @param account
  // */
  // public static void stopReceiveMailThread(int accountId) {
  //
  // Object objRS = ALStaticObject.getInstance().getAccountStat(accountId,
  // ALPop3MailReceiveThread.KEY_THREAD);
  // if (objRS == null){
  // return;
  // }
  //
  // return;
  // }

  /**
   * POP3 サーバと通信後の結果を取得する。
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public static int getStatThread(String orgId, JetspeedUser user, int accountId)
      throws Exception {
    synchronized (ALPop3MailReceiveThread.KEY_SYNCHRONIZED_LOCK) {
      if (ALPop3MailReceiveThread.isProcessing(user, accountId)) {
        return ALPop3MailReceiveThread.PROCESS_STAT_PROCESSING;
      } else {
        return ALPop3MailReceiveThread.getReceiveMailResult(user, accountId);
      }
    }
  }

  /**
   * POP3 サーバと通信後の結果を取得する。
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public static String getStatStrThread(String orgId, JetspeedUser user,
      int accountId) throws Exception {
    synchronized (ALPop3MailReceiveThread.KEY_SYNCHRONIZED_LOCK) {
      return ALPop3MailReceiveThread.getReceiveMailResultStr(user, accountId);
    }
  }

  /**
   * 未読メール総数を取得する。
   *
   * @param rundata
   * @param userId
   * @param accountId
   * @return
   */
  public static int getUnreadMailNumber(RunData rundata, int userId,
      int accountId) {
    String orgId = DatabaseOrmService.getInstance().getOrgId(rundata);
    EipMMailAccount account = ALMailUtils.getMailAccount(orgId, userId,
        accountId);
    ALMailHandler handler = ALMailFactoryService.getInstance().getMailHandler();
    ALMailReceiverContext rcontext = ALMailUtils.getALPop3MailReceiverContext(
        orgId, account);

    return handler.getUnReadMailSum(rcontext);
  }

  /**
   * フォルダ別未読メール数を取得する。
   *
   * @param rundata
   * @param userId
   * @param accountId
   * @return
   */
  public static Map<Integer,Integer> getUnreadMailNumberMap(RunData rundata, int userId,
      int accountId) {
    String orgId = DatabaseOrmService.getInstance().getOrgId(rundata);
    EipMMailAccount account = ALMailUtils.getMailAccount(orgId, userId,
        accountId);
    ALMailHandler handler = ALMailFactoryService.getInstance().getMailHandler();
    ALMailReceiverContext rcontext = ALMailUtils.getALPop3MailReceiverContext(
        orgId, account);

    return handler.getUnReadMailSumMap(rcontext);
  }

  public static boolean isNewMessage(RunData rundata, Context context) {
    String accountId = rundata.getParameters().getString(
        WebMailUtils.ACCOUNT_ID);
    if (accountId == null || "".equals(accountId)) {
      return true;
    }
    EipMMailAccount account = ALMailUtils.getMailAccount(null, ALEipUtils
        .getUserId(rundata), Integer.parseInt(accountId));
    String orgId = DatabaseOrmService.getInstance().getOrgId(rundata);

    ALMailHandler handler = ALMailFactoryService.getInstance().getMailHandler();
    ALMailReceiverContext rcontext = ALMailUtils.getALPop3MailReceiverContext(
        orgId, account);
    int res = -1;
    try {
      res = handler.getNewMailSum(rcontext);
    } catch (Exception e) {
      res = -1;
      e.printStackTrace();
    }
    return (res > 0 ? true : false);
  }

  /**
   * フォルダオブジェクトモデルを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static EipTMailFolder getEipTMailFolder(RunData rundata,
      Context context) {
    try {
      int accountId, folderId;

      try {
        accountId = Integer.parseInt(ALEipUtils.getTemp(rundata, context,
            ACCOUNT_ID));
        folderId = Integer.parseInt(ALEipUtils.getTemp(rundata, context,
            FOLDER_ID));
      } catch (Exception e) {
        logger.debug("[Mail] Empty ID...");
        return null;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTMailFolder.class);
      Expression exp = ExpressionFactory.matchDbExp(
          EipTMailFolder.FOLDER_ID_PK_COLUMN, folderId);
      Expression exp2 = ExpressionFactory.matchDbExp(
          EipTMailFolder.EIP_MMAIL_ACCOUNT_PROPERTY + "."
              + EipMMailAccount.ACCOUNT_ID_PK_COLUMN, accountId);
      query.setQualifier(exp.andExp(exp2));

      List folders = dataContext.performQuery(query);
      if (folders == null || folders.size() == 0) {
        // 指定したフォルダIDのレコードが見つからない場合
        logger.debug("[WebMail Folder] Not found ID...");
        return null;
      }
      return ((EipTMailFolder) folders.get(0));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * フォルダオブジェクトモデルを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static EipTMailFolder getEipTMailFolder(EipMMailAccount account,
      String folderId) {
    try {
      if (account == null) {
        // アカウントが空の場合
        logger.debug("[WebMail Folder] Empty Account...");
        return null;
      }
      if (folderId == null || Integer.valueOf(folderId) == null) {
        // フォルダIDが空の場合、デフォルトのフォルダIDを使う
        folderId = account.getDefaultFolderId().toString();
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTMailFolder.class);
      Expression exp = ExpressionFactory.matchDbExp(
          EipTMailFolder.FOLDER_ID_PK_COLUMN, folderId);
      Expression exp2 = ExpressionFactory.matchDbExp(
          EipTMailFolder.EIP_MMAIL_ACCOUNT_PROPERTY, account);
      query.setQualifier(exp.andExp(exp2));

      List folders = dataContext.performQuery(query);
      if (folders == null || folders.size() == 0) {
        // 指定したフォルダIDのレコードが見つからない場合
        logger.debug("[WebMail Folder] Not found ID...");
        return null;
      }
      return ((EipTMailFolder) folders.get(0));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * フィルタオブジェクトモデルを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static EipTMailFilter getEipTMailFilter(EipMMailAccount account,
      String filterId) {
    try {
      if (account == null) {
        // アカウントが空の場合
        logger.debug("[WebMail Filter] Empty Account...");
        return null;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTMailFilter.class);
      Expression exp = ExpressionFactory.matchDbExp(
          EipTMailFilter.FILTER_ID_PK_COLUMN, filterId);
      Expression exp2 = ExpressionFactory.matchDbExp(
          EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY, account);
      query.setQualifier(exp.andExp(exp2));

      List filters = dataContext.performQuery(query);
      if (filters == null || filters.size() == 0) {
        // 指定したフィルタIDのレコードが見つからない場合
        logger.debug("[WebMail Filter] Not found ID...");
        return null;
      }
      return (EipTMailFilter) filters.get(0);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * フィルタオブジェクトモデルを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static EipTMailFilter getEipTMailFilter(RunData rundata,
      Context context) {
    try {
      int accountId, filterId;

      try {
        accountId = Integer.parseInt(ALEipUtils.getTemp(rundata, context,
            ACCOUNT_ID));
        filterId = Integer.parseInt(ALEipUtils.getTemp(rundata, context,
            FILTER_ID));
      } catch (Exception e) {
        logger.debug("[WebMail Filter] Empty ID...");
        return null;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTMailFilter.class);
      Expression exp = ExpressionFactory.matchDbExp(
          EipTMailFilter.FILTER_ID_PK_COLUMN, filterId);
      Expression exp2 = ExpressionFactory.matchDbExp(
          EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY + "."
              + EipMMailAccount.ACCOUNT_ID_PK_COLUMN, accountId);
      query.setQualifier(exp.andExp(exp2));

      List filters = dataContext.performQuery(query);
      if (filters == null || filters.size() == 0) {
        // 指定したフィルタIDのレコードが見つからない場合
        logger.debug("[WebMail Filter] Not found ID...");
        return null;
      }
      return ((EipTMailFilter) filters.get(0));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }


  /**
   * 指定されたアカウントのフィルタの最後のソート番号を取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static int getMailFilterLastSortOrder(EipMMailAccount account) {
    try {
      if (account == null) {
        // アカウントが空の場合
        logger.debug("[WebMail Filter] Empty Account...");
        return 0;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTMailFilter.class);
      Expression exp = ExpressionFactory.matchDbExp(
          EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY, account);
      query.setQualifier(exp);
      query.addOrdering(EipTMailFilter.SORT_ORDER_PROPERTY, false);

      List filters = dataContext.performQuery(query);
      if (filters == null || filters.size() == 0) {
        // 指定したフィルタIDのレコードが見つからない場合
        logger.debug("[WebMail Filter] Not found ID...");
        return 0;
      }
      return ((EipTMailFilter) filters.get(0)).getSortOrder();
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return 0;
    }
  }

  /**
   * セッションに保存されたString値から メール未読数のHashMap を作りなおします。
   *
   * @param rundata
   * @param context
   * @return
   */
  public static HashMap getUnreadMailSumMapFromString(String str) {

    HashMap unreadSumMap = new HashMap();
    try {
      String[] entries = str.split("\\{")[1].split("\\}")[0].split(",");
      for (String entry : entries) {
        entry = entry.trim();
        String[] keyValue = entry.split("=");
        unreadSumMap.put(Integer.valueOf(keyValue[0]).intValue(),
            Integer.valueOf(keyValue[1]).intValue());
      }
      return unreadSumMap;
    } catch (Exception e) {
      return null;
    }

  }
}
