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
package com.aimluck.eip.system.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFilter;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFolder;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.ALMailReceiverContext;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.system.SystemWebMailFolderResultData;
import com.aimluck.eip.util.ALEipUtils;

/**
 */
public class SystemWebMailUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemWebMailUtils.class.getName());

  /** セッションの識別子 */
  public static final String FOLDER_ID = ALMailUtils.FOLDER_ID;

  public static final String FILTER_ID = "filterid";

  /** タブ「受信トレイ」 */
  public static final String TAB_RECEIVE = "receive";

  /** タブ「送信トレイ」 */
  public static final String TAB_SENT = "sent";

  public static final String DATE_TIME_FORMAT =
    ALDateTimeField.DEFAULT_DATE_TIME_FORMAT;

  public static final String CREATED_DATE_FORMAT =
    ALDateTimeField.DEFAULT_DATE_FORMAT;

  public final static String ACCOUNT_ID = "accountid";

  public final static String ACCOUNT_NAME = "accountname";

  public final static String MAIL_TYPE = "mailtype";

  public final static String CONFIRM_LAST_TIME = "confirmlasttime";

  /** フィルタタイプ */
  public final static String FILTER_TYPE_MAILADDRESS =
    ALMailUtils.FILTER_TYPE_MAILADDRESS;

  public final static String FILTER_TYPE_DOMAIN =
    ALMailUtils.FILTER_TYPE_DOMAIN;

  public final static String FILTER_TYPE_SUBJECT =
    ALMailUtils.FILTER_TYPE_SUBJECT;

  public final static String FILTER_TYPE_TO = ALMailUtils.FILTER_TYPE_TO;

  public static final String WEBMAIL_PORTLET_NAME = "WebMail";

  public static final String WEBMAIL_ADMIN_PORTLET_NAME = "WebMailAdmin";

  public static final String UNREAD_MAIL = "unreadmailsummap";

  public static final List<EipMMailAccount> getMailAccountNameList(int userId) {
    SelectQuery<EipMMailAccount> query = Database.query(EipMMailAccount.class);

    query.select(EipMMailAccount.ACCOUNT_ID_PK_COLUMN);
    query.select(EipMMailAccount.ACCOUNT_NAME_COLUMN);
    Expression exp =
      ExpressionFactory.matchExp(EipMMailAccount.USER_ID_PROPERTY, Integer
        .valueOf(userId));

    return query.setQualifier(exp).fetchList();
  }

  /**
   * 複数のメールアドレスを含む文字列の中のメールアドレス形式をチェックします。 想定メール形式は address@aimluck.com xxxyyy
   * <address@aimluck.com>
   * 
   * @param argstr
   *          複数メールアドレス
   * @param delim
   * @return
   */
  public static boolean checkAddress(String argstr, String delim) {
    String[] addresses;
    if (argstr == null || argstr.trim().length() == 0) {
      return false;
    }
    addresses = ALMailUtils.getTokens(argstr, delim);
    for (int i = 0; i < addresses.length; i++) {
      String str = addresses[i].trim();
      if (str.length() == 0) {
        continue;
      }
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
    List<Character> unusualChars = new ArrayList<Character>();

    /**
     * 文字化けを起こす特殊記号 【囲み英数字／ローマ数字】①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ
     * 【単位記号】㍉㌔㌢㍍㌘㌧㌃㌶㍑㍗㌍・㌣㌫㍊㌻㎜㎝㎞㎎㎏㏄㎡ 【省略文字／囲み文字／年号】㍻〝〟№㏍℡㊤㊥㊦㊧㊨㈱㈲㈹㍾㍽㍼∮∑∟⊿
     */
    char[] unusualchars =
      {
        '①',
        '②',
        '③',
        '④',
        '⑤',
        '⑥',
        '⑦',
        '⑧',
        '⑨',
        '⑩',
        '⑪',
        '⑫',
        '⑬',
        '⑭',
        '⑮',
        '⑯',
        '⑰',
        '⑱',
        '⑲',
        '⑳',
        'Ⅰ',
        'Ⅱ',
        'Ⅲ',
        'Ⅳ',
        'Ⅴ',
        'Ⅵ',
        'Ⅶ',
        'Ⅷ',
        'Ⅸ',
        'Ⅹ',
        '㍉',
        '㌔',
        '㌢',
        '㍍',
        '㌘',
        '㌧',
        '㌃',
        '㌶',
        '㍑',
        '㍗',
        '㌍',
        '・',
        '㌣',
        '㌫',
        '㍊',
        '㌻',
        '㎜',
        '㎝',
        '㎞',
        '㎎',
        '㎏',
        '㏄',
        '㎡',
        '㍻',
        '〝',
        '〟',
        '№',
        '㏍',
        '℡',
        '㊤',
        '㊥',
        '㊦',
        '㊧',
        '㊨',
        '㈱',
        '㈲',
        '㈹',
        '㍾',
        '㍽',
        '㍼',
        '∮',
        '∑',
        '∟',
        '⊿' };
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
   * 未読メール総数を取得する。
   * 
   * @param rundata
   * @param userId
   * @param accountId
   * @return
   */
  public static int getUnreadMailNumber(RunData rundata, int userId,
      int accountId) {
    String orgId = Database.getDomainName();
    EipMMailAccount account = ALMailUtils.getMailAccount(userId, accountId);
    ALMailHandler handler = ALMailFactoryService.getInstance().getMailHandler();
    ALMailReceiverContext rcontext =
      ALMailUtils.getALPop3MailReceiverContext(orgId, account);

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
  public static Map<Integer, Integer> getUnreadMailNumberMap(RunData rundata,
      int userId, int accountId) {
    String orgId = Database.getDomainName();
    EipMMailAccount account = ALMailUtils.getMailAccount(userId, accountId);
    ALMailHandler handler = ALMailFactoryService.getInstance().getMailHandler();
    ALMailReceiverContext rcontext =
      ALMailUtils.getALPop3MailReceiverContext(orgId, account);

    return handler.getUnReadMailSumMap(rcontext);
  }

  public static boolean isNewMessage(RunData rundata, Context context) {
    String accountId =
      rundata.getParameters().getString(SystemWebMailUtils.ACCOUNT_ID);
    if (accountId == null || "".equals(accountId)) {
      return true;
    }
    EipMMailAccount account =
      ALMailUtils.getMailAccount(ALEipUtils.getUserId(rundata), Integer
        .parseInt(accountId));
    String orgId = Database.getDomainName();

    ALMailHandler handler = ALMailFactoryService.getInstance().getMailHandler();
    ALMailReceiverContext rcontext =
      ALMailUtils.getALPop3MailReceiverContext(orgId, account);
    int res = -1;
    try {
      res = handler.getNewMailSum(rcontext);
    } catch (Exception e) {
      res = -1;
      logger.error("[SystemWebMailUtils]", e);
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
        accountId =
          Integer.parseInt(ALEipUtils.getTemp(rundata, context, ACCOUNT_ID));
        folderId =
          Integer.parseInt(ALEipUtils.getTemp(rundata, context, FOLDER_ID));
      } catch (Exception e) {
        logger.debug("[Mail] Empty ID...");
        return null;
      }

      SelectQuery<EipTMailFolder> query = Database.query(EipTMailFolder.class);

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTMailFolder.FOLDER_ID_PK_COLUMN,
          folderId);
      Expression exp2 =
        ExpressionFactory.matchDbExp(EipTMailFolder.EIP_MMAIL_ACCOUNT_PROPERTY
          + "."
          + EipMMailAccount.ACCOUNT_ID_PK_COLUMN, accountId);

      EipTMailFolder folder =
        query.setQualifier(exp.andExp(exp2)).fetchSingle();
      if (folder == null) {
        logger.debug("[WebMail Folder] Not found ID...");
        return null;
      }
      return folder;
    } catch (Exception ex) {
      logger.error("system", ex);
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

      SelectQuery<EipTMailFolder> query = Database.query(EipTMailFolder.class);

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTMailFolder.FOLDER_ID_PK_COLUMN,
          folderId);
      Expression exp2 =
        ExpressionFactory.matchDbExp(
          EipTMailFolder.EIP_MMAIL_ACCOUNT_PROPERTY,
          account);

      EipTMailFolder folder =
        query.setQualifier(exp.andExp(exp2)).fetchSingle();
      if (folder == null) {
        logger.debug("[WebMail Folder] Not found ID...");
        return null;
      }
      return folder;
    } catch (Exception ex) {
      logger.error("system", ex);
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

      SelectQuery<EipTMailFilter> query = Database.query(EipTMailFilter.class);

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTMailFilter.FILTER_ID_PK_COLUMN,
          filterId);
      Expression exp2 =
        ExpressionFactory.matchDbExp(
          EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY,
          account);

      EipTMailFilter filter =
        query.setQualifier(exp.andExp(exp2)).fetchSingle();
      if (filter == null) {
        logger.debug("[WebMail Filter] Not found ID...");
        return null;
      }
      return filter;
    } catch (Exception ex) {
      logger.error("system", ex);
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
        accountId =
          Integer.parseInt(ALEipUtils.getTemp(rundata, context, ACCOUNT_ID));
        filterId =
          Integer.parseInt(ALEipUtils.getTemp(rundata, context, FILTER_ID));
      } catch (Exception e) {
        logger.debug("[WebMail Filter] Empty ID...");
        return null;
      }

      SelectQuery<EipTMailFilter> query = Database.query(EipTMailFilter.class);
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTMailFilter.FILTER_ID_PK_COLUMN,
          filterId);
      Expression exp2 =
        ExpressionFactory.matchDbExp(EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY
          + "."
          + EipMMailAccount.ACCOUNT_ID_PK_COLUMN, accountId);

      EipTMailFilter filter =
        query.setQualifier(exp.andExp(exp2)).fetchSingle();
      if (filter == null) {
        logger.debug("[WebMail Filter] Not found ID...");
        return null;
      }
      return filter;
    } catch (Exception ex) {
      logger.error("system", ex);
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

      SelectQuery<EipTMailFilter> query = Database.query(EipTMailFilter.class);

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTMailFilter.EIP_MMAIL_ACCOUNT_PROPERTY,
          account);
      query
        .setQualifier(exp)
        .orderDesending(EipTMailFilter.SORT_ORDER_PROPERTY);

      EipTMailFilter filter = query.fetchSingle();
      if (filter == null) {
        logger.debug("[WebMail Filter] Not found ID...");
        return 0;
      }
      return filter.getSortOrder();
    } catch (Exception ex) {
      logger.error("system", ex);
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
  public static HashMap<Integer, Integer> getUnreadMailSumMapFromString(
      String str) {

    HashMap<Integer, Integer> unreadSumMap = new HashMap<Integer, Integer>();
    try {
      String[] entries = str.split("\\{")[1].split("\\}")[0].split(",");
      for (String entry : entries) {
        entry = entry.trim();
        String[] keyValue = entry.split("=");
        unreadSumMap.put(Integer.valueOf(keyValue[0]).intValue(), Integer
          .valueOf(keyValue[1])
          .intValue());
      }
      return unreadSumMap;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * メールアカウントのセッション情報を削除します。
   * 
   * @param rundata
   * @param context
   */
  public static void clearWebMailAccountSession(RunData rundata, Context context) {
    ALEipUtils.removeTemp(rundata, context, SystemWebMailUtils.ACCOUNT_ID);
    ALEipUtils.removeTemp(rundata, context, SystemWebMailUtils.FOLDER_ID);
  }

  /**
   * フォルダオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static List<SystemWebMailFolderResultData> getMailFolderAll(
      EipMMailAccount account) {

    List<EipTMailFolder> folderList = ALMailUtils.getEipTMailFolderAll(account);

    // 受信トレイを先頭に配置する
    List<SystemWebMailFolderResultData> list =
      new ArrayList<SystemWebMailFolderResultData>();

    for (EipTMailFolder folder : folderList) {
      SystemWebMailFolderResultData data =
        new SystemWebMailFolderResultData(folder);
      list.add(data);
    }

    return list;
  }
}
