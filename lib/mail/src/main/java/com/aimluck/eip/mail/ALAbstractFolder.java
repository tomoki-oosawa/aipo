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
package com.aimluck.eip.mail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.cayenne.om.portlet.EipTMailFilter;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.mail.util.ALAttachmentsExtractor;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.orgutils.ALOrgUtilsFactoryService;
import com.aimluck.eip.util.orgutils.ALOrgUtilsHandler;
import com.sk_jp.mail.MailUtility;
import com.sk_jp.mail.MultipartUtility;

/**
 * 送受信したメールを保持するローカルフォルダの抽象クラスです。 <br />
 * 
 */
public abstract class ALAbstractFolder implements ALFolder {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(ALAbstractFolder.class.getName());

  /** 受信 or 送信 */
  protected int type_mail = -1;

  protected String org_id;

  protected int user_id;

  protected int account_id;

  /** ルートフォルダのフルパス */
  protected String rootFolderPath = null;

  /** カテゴリキー（mail） */
  protected String categoryKey = null;

  /** 表示行数 */
  private int rows_num = 10;

  /** 表示文字数 */
  private int strlen = 0;

  /** 開始位置 */
  private int start;

  /** 総件数 */
  private int count;

  /** 総ページ数 */
  private int pages_num = 1;

  /** 現在のページ */
  private int current_page = 1;

  /** 現在のソート */
  private String current_sort;

  /** 現在のソートタイプ （asc:昇順、desc:降順） */
  private String current_sort_type;

  protected final String LIST_SORT_STR =
    new StringBuffer().append(this.getClass().getName()).append(
      ALEipConstants.LIST_SORT).toString();

  protected final String LIST_SORT_TYPE_STR =
    new StringBuffer().append(this.getClass().getName()).append(
      ALEipConstants.LIST_SORT_TYPE).toString();

  /**
   * コンストラクタ
   * 
   * @param parentFolder
   *          親フォルダ
   * @param folderName
   *          自身のフォルダ名
   */
  public ALAbstractFolder(int type_mail, String org_id, int user_id,
      int account_id) {
    this.type_mail = type_mail;
    this.org_id = org_id;
    this.user_id = user_id;
    this.account_id = account_id;
  }

  protected void init(RunData rundata, Context context) {
    if (rundata.getParameters().containsKey(ALEipConstants.LIST_SORT)) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, rundata
        .getParameters()
        .getString(ALEipConstants.LIST_SORT));
    }

    if (rundata.getParameters().containsKey(ALEipConstants.LIST_SORT_TYPE)) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_TYPE_STR, rundata
        .getParameters()
        .getString(ALEipConstants.LIST_SORT_TYPE));
    }

    if (rundata.getParameters().containsKey(ALEipConstants.LIST_START)) {
      current_page = rundata.getParameters().getInt(ALEipConstants.LIST_START);
    }
  }

  protected boolean insertMailToDB(DataContext dataContext,
      MimeMessage mimeMessage, String filePath, boolean saveContents,
      boolean isRead) {
    try {
      EipTMail email = Database.create(dataContext, EipTMail.class);

      String mail = "";
      if (saveContents) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mimeMessage.writeTo(output);
        mail = output.toString();
      } else {
        Session session = Session.getDefaultInstance(new Properties());
        Message newMsg = new MimeMessage(session);
        Enumeration<?> headers = mimeMessage.getAllHeaders();
        while (headers.hasMoreElements()) {
          Header h = (Header) headers.nextElement();
          newMsg.addHeader(h.getName(), h.getValue());
        }
        newMsg
          .setText("メールのサイズが7MBを超えていたため、このメールを受信できませんでした。\r\n 誠に恐れ入りますが、別のメーラーで受信してください。");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        newMsg.writeTo(output);
        mail = output.toString();
      }

      String subject;
      Address[] presonAddress;
      String preson;
      Date sentDate = null;

      // 件名
      subject = mimeMessage.getHeader(ALLocalMailMessage.SUBJECT, null);
      if (subject == null) {
        subject = MailUtility.encodeWordJIS("無題");
      } else {
        subject = subject.replaceAll("\r", "");
        subject = subject.replaceAll("\n", "");
      }

      String type0 = (ALFolder.TYPE_RECEIVE == type_mail) ? "R" : "S";

      // 差出人 or 受取人
      if ("R".equals(type0)) {
        try {
          presonAddress = mimeMessage.getFrom();
        } catch (MessagingException ex) {
          presonAddress = null;
        }
      } else {
        try {
          presonAddress = mimeMessage.getAllRecipients();
        } catch (MessagingException ex) {
          presonAddress = null;
        }
      }

      if (presonAddress != null && presonAddress.length > 0) {
        String personaladdr =
          ALMailUtils.getOneString(ALMailUtils.getTokens(presonAddress[0]
            .toString(), ALMailUtils.CR), "");
        try {
          preson = MailUtility.decodeText(personaladdr);
          if (presonAddress.length > 1) {
            preson += "，...";
          }
        } catch (RuntimeException ex) {
          logger.error("Exception", ex);
          preson = "--";
        }
      } else {
        preson = "--";
      }

      // 日付
      try {
        sentDate = mimeMessage.getSentDate();
      } catch (MessagingException ex) {
      }
      if (sentDate == null) {
        sentDate = Calendar.getInstance().getTime();
      }

      // ファイル容量（KB）
      int fileVolume = 0;
      try {
        fileVolume = (int) Math.ceil(mimeMessage.getSize() / 1024.0);
      } catch (MessagingException ex) {
        fileVolume = 0;
      }

      // 添付ファイルの有無
      String hasAttachments = null;
      if (saveContents) {
        try {
          ALAttachmentsExtractor h = new ALAttachmentsExtractor();
          MultipartUtility.process(mimeMessage, h);
          hasAttachments = (h.getCount() > 0) ? "T" : "F";
        } catch (MessagingException ex) {
          hasAttachments = "F";
        }
      } else {
        hasAttachments = "F";
      }

      // メールタイプ・既読フラグ
      String type = (ALFolder.TYPE_RECEIVE == type_mail) ? "R" : "S";
      String read_flg = isRead ? "T" : "F";

      // アカウントのフォルダに代入
      EipMMailAccount account =
        ALMailUtils.getMailAccount(org_id, user_id, account_id);
      int folder_id = account.getDefaultFolderId();

      // フォルダ振り分け処理
      List<?> filters = ALMailUtils.getEipTMailFilters(account);
      if (filters != null) {
        for (Object filter : filters) {
          EipTMailFilter mailFilter = (EipTMailFilter) filter;
          if (ALMailUtils.isMatchFilter(mailFilter, subject, preson)) {
            folder_id = mailFilter.getEipTMailFolder().getFolderId();
            break;
          }
        }
      }

      email.setUserId(Integer.valueOf(user_id));
      email.setAccountId(Integer.valueOf(account_id));
      email.setType(type);
      email.setReadFlg(read_flg);
      email.setSubject(subject);
      email.setPerson(preson);
      email.setEventDate(sentDate);
      email.setFileVolume(Integer.valueOf(fileVolume));
      email.setHasFiles(hasAttachments);
      email.setFilePath(filePath);
      email.setMail(mail.getBytes());
      email.setFolderId(Integer.valueOf(folder_id));

      // 作成日
      email.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      email.setUpdateDate(Calendar.getInstance().getTime());

      Database.commit(dataContext);
    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }

  /**
   * インデックス情報を取得します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public List<?> getIndexRows(RunData rundata, Context context)
      throws Exception {
    try {
      // // 未読メール総数をセットする．
      // setUnreadMailSum();
      // // 最終更新日を取得し，セットする．
      // setFinalAccessDate(rundata, context);

      String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
      if (sort == null || sort.equals("")) {
        ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p2a-sort"));
      }

      init(rundata, context);

      // ソート対象が日時だった場合、ソート順を逆にする．
      if ("date".equals(ALEipUtils.getTemp(rundata, context, LIST_SORT_STR))) {
        String sort_type =
          ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
        if (sort_type == null || sort_type.equals("")) {
          ALEipUtils.setTemp(
            rundata,
            context,
            LIST_SORT_TYPE_STR,
            ALEipConstants.LIST_SORT_TYPE_DESC);
        }
      }

      DataContext dataContext =
        DatabaseOrmService.getInstance().getDataContext();
      SelectQuery query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List<?> list = dataContext.performQuery(query);

      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery getSelectQuery(RunData rundata, Context context) {
    String type = (type_mail == TYPE_RECEIVE) ? "R" : "S";

    // メールタイプが「受信」の場合、セッションからフォルダIDを取得する
    String folder_id = "";
    if (type_mail == TYPE_RECEIVE) {
      folder_id = ALEipUtils.getTemp(rundata, context, ALMailUtils.FOLDER_ID);
    }

    SelectQuery query = new SelectQuery(EipTMail.class);
    query.addCustomDbAttribute(EipTMail.MAIL_ID_PK_COLUMN);
    query.addCustomDbAttribute(EipTMail.READ_FLG_COLUMN);
    query.addCustomDbAttribute(EipTMail.SUBJECT_COLUMN);
    query.addCustomDbAttribute(EipTMail.PERSON_COLUMN);
    query.addCustomDbAttribute(EipTMail.EVENT_DATE_COLUMN);
    query.addCustomDbAttribute(EipTMail.FILE_VOLUME_COLUMN);
    query.addCustomDbAttribute(EipTMail.HAS_FILES_COLUMN);
    Expression exp1 =
      ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY, Integer
        .valueOf(user_id));
    query.setQualifier(exp1);
    Expression exp2 =
      ExpressionFactory.matchExp(EipTMail.ACCOUNT_ID_PROPERTY, Integer
        .valueOf(account_id));
    query.andQualifier(exp2);
    Expression exp3 = ExpressionFactory.matchExp(EipTMail.TYPE_PROPERTY, type);
    query.andQualifier(exp3);

    // folder_id が空でなければ、フォルダIDで絞り込む
    if (!("".equals(folder_id))) {
      Expression exp4 =
        ExpressionFactory.matchExp(EipTMail.FOLDER_ID_PROPERTY, Integer
          .valueOf(folder_id));
      query.andQualifier(exp4);
    }

    query.setDistinct(true);

    return query;
  }

  /**
   * 保存してある UID リストを取得する．
   * 
   * @return
   */
  public List<String> loadUID() {
    List<String> oldUIDL = new ArrayList<String>();

    BufferedReader reader = null;
    try {
      String indexFilename =
        getFullName() + File.separator + ALFolder.FILE_UIDL;
      File uidlFile = new File(indexFilename);
      if (!uidlFile.exists()) {
        uidlFile.createNewFile();
        uidlFile = new File(indexFilename);
        if (!uidlFile.exists()) {
          return oldUIDL;
        }
      }

      reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(uidlFile)));
      String line = null;
      while ((line = reader.readLine()) != null) {
        oldUIDL.add(line);
      }

    } catch (IOException ioe) {
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ioe) {
        }
      }
    }
    return oldUIDL;
  }

  /**
   * UID の一覧を保存する．
   * 
   * @param oldUIDL
   */
  public void saveUID(List<String> oldUIDL) {
    PrintWriter writer = null;
    try {
      String uidlFilename = getFullName() + File.separator + ALFolder.FILE_UIDL;
      File file = new File(uidlFilename);
      if (!file.exists()) {
        file.createNewFile();
      }
      // UID の一覧を保存
      writer = new PrintWriter(new FileOutputStream(new File(uidlFilename)));
      int length = oldUIDL.size();
      for (int i = 0; i < length; i++) {
        writer.println(oldUIDL.get(i));
      }

      writer.flush();
    } catch (IOException ioe) {
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  public void setRootFolderPath(String str) {
    rootFolderPath = str;
  }

  protected String getRootFolderPath() {
    return (rootFolderPath != null && !"".equals(rootFolderPath))
      ? rootFolderPath
      : ALMailUtils.rootFolderPath;
  }

  protected String getCategoryKey() {
    return (categoryKey != null && !"".equals(categoryKey))
      ? categoryKey
      : ALMailUtils.categoryKey;
  }

  /**
   * 自身のフォルダまでのフルパスを取得する。
   * 
   * @return
   */
  public String getFullName() {
    StringBuffer fullName = null;
    String categoryKeytmp = getCategoryKey();

    if (categoryKeytmp != null && !"".equals(categoryKeytmp)) {
      ALOrgUtilsHandler handler =
        ALOrgUtilsFactoryService.getInstance().getOrgUtilsHandler();
      File docPath =
        handler.getDocumentPath(getRootFolderPath(), org_id, categoryKeytmp);
      String pathStr = null;
      try {
        pathStr = docPath.getCanonicalPath();
      } catch (IOException e) {
        logger.error("ALAbstractFolder: unable to resolve file path for "
          + pathStr);
      }
      fullName = new StringBuffer(pathStr);
      fullName
        .append(File.separator)
        .append(user_id)
        .append(File.separator)
        .append(account_id);
    } else {
      fullName = new StringBuffer(getRootFolderPath());
      fullName
        .append(File.separator)
        .append(org_id)
        .append(File.separator)
        .append(user_id)
        .append(File.separator)
        .append(account_id);
    }

    if (ALFolder.TYPE_RECEIVE == type_mail) {
      fullName.append(File.separator).append("Receive");
    } else {
      fullName.append(File.separator).append("Send");
    }

    String path = fullName.toString();
    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }

    return fullName.toString();
  }

  /**
   * ページング結果のリストを取得します。
   * 
   * @param records
   *          検索結果
   */
  protected void buildSelectQueryForListView(SelectQuery query) {
    query.setPageSize(getRowsNum());
  }

  /**
   * ページング結果のリストを取得します。
   * 
   * @param records
   *          検索結果
   */
  protected List<Object> buildPaginatedList(List<?> records) {
    List<Object> list = new ArrayList<Object>();

    setPageParam(records.size());

    int size = records.size();
    int end = (start + rows_num <= size) ? start + rows_num : size;
    for (int i = start; i < end; i++) {
      list.add(records.get(i));
    }

    return list;
  }

  /**
   * 
   * @param cnt
   */
  protected void setPageParam(int cnt) {
    count = cnt;
    pages_num = ((int) (Math.ceil(count / (double) rows_num)));
    current_page = (current_page <= pages_num) ? current_page : pages_num;
    start = rows_num * ((current_page > 0) ? (current_page - 1) : current_page);
  }

  /**
   * ソート用の <code>SelectQuery</code> を構築します。
   * 
   * @param crt
   * @return
   */
  protected SelectQuery buildSelectQueryForListViewSort(SelectQuery query,
      RunData rundata, Context context) {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    String sort_type = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    String crt_key = null;
    Attributes map = getColumnMap();
    if (sort == null) {
      return query;
    }
    crt_key = map.getValue(sort);
    if (crt_key == null) {
      return query;
    }
    if (sort_type != null
      && ALEipConstants.LIST_SORT_TYPE_DESC.equals(sort_type)) {
      query.addOrdering(crt_key, false);
    } else {
      query.addOrdering(crt_key, true);
      sort_type = ALEipConstants.LIST_SORT_TYPE_ASC;
    }
    current_sort = sort;
    current_sort_type = sort_type;
    return query;
  }

  /**
   * 表示する項目数を設定します。
   * 
   * @param num
   */
  public void setRowsNum(int num) {
    if (num >= 1) {
      rows_num = num;
    }
  }

  /**
   * 表示文字数を設定します。
   * 
   * @param num
   */
  public void setStrLength(int num) {
    if (num >= 0) {
      strlen = num;
    }
  }

  /**
   * 表示文字数を取得します。
   * 
   * @return
   */
  public int getStrLength() {
    return strlen;
  }

  /**
   * 表示する項目数を取得します。
   * 
   * @return
   */
  public int getRowsNum() {
    return rows_num;
  }

  /**
   * 総件数を取得します。
   * 
   * @return
   */
  public int getCount() {
    return count;
  }

  /**
   * 総ページ数を取得します。
   * 
   * @return
   */
  public int getPagesNum() {
    return pages_num;
  }

  /**
   * 現在表示されているページを取得します。
   * 
   * @return
   */
  public int getCurrentPage() {
    return current_page;
  }

  /**
   * 
   * @return
   */
  public String getCurrentSort() {
    return current_sort;
  }

  /**
   * 
   * @return
   */
  public String getCurrentSortType() {
    return current_sort_type;
  }

  /**
   * 
   * @return
   */
  protected abstract Attributes getColumnMap();

  /**
   * @return
   */
  public int getStart() {
    return start;
  }
}
