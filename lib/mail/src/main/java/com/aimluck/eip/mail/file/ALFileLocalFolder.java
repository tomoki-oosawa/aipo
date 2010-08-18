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
package com.aimluck.eip.mail.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.ALAbstractFolder;
import com.aimluck.eip.mail.ALLocalMailMessage;
import com.aimluck.eip.mail.ALMailMessage;
import com.aimluck.eip.orm.DatabaseOrmService;

/**
 * ローカルのファイルシステムを利用し、送受信したメールを保持するローカルフォルダのクラスです。 <br />
 * 
 */
public class ALFileLocalFolder extends ALAbstractFolder {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALFileLocalFolder.class.getName());

  /** メールのファイル名規則 */
  public static final String DEFAULT_MAIL_FILENAME_DATE_FORMAT = "yyyyMMddHHmmssSSS";

  /**
   * コンストラクタ
   * 
   * @param parentFolder
   *          親フォルダ
   * @param folderName
   *          自身のフォルダ名
   */
  public ALFileLocalFolder(int type_mail, String org_id, int user_id,
      int account_id) {
    super(type_mail, org_id, user_id, account_id);
  }

  /**
   * メールを取得します。
   * 
   * @param index
   * @return
   */
  public ALLocalMailMessage getMail(int mailid) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      SelectQuery query = new SelectQuery(EipTMail.class);
      Expression exp1 = ExpressionFactory.matchDbExp(
        EipTMail.MAIL_ID_PK_COLUMN, Integer.valueOf(mailid));
      query.setQualifier(exp1);
      Expression exp2 = ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY,
        user_id);
      query.andQualifier(exp2);
      List<?> mails = dataContext.performQuery(query);
      if (mails == null || mails.size() == 0) {
        // 指定したMail IDのレコードが見つからない場合
        logger.debug("[Mail] Not found ID...");
        return null;
      }
      EipTMail email = ((EipTMail) mails.get(0));
      ALLocalMailMessage msg = readMail(getFullName() + File.separator
        + email.getFilePath());

      // 未読→既読に変更
      email.setReadFlg("T");
      dataContext.commitChanges();

      return msg;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 指定されたファイルを読み込み，mail メッセージを取得する．
   * 
   * @param fileName
   * @return
   */
  private ALLocalMailMessage readMail(String filepath) {
    System.setProperty("mail.mime.charset", "ISO-2022-JP");
    System.setProperty("mail.mime.decodetext.strict", "false");
    Properties prop = new Properties();
    prop.setProperty("mail.mime.address.strict", "false");
    ALLocalMailMessage localmsg = null;
    BufferedInputStream input = null;
    try {
      input = new BufferedInputStream(new FileInputStream(filepath));
      localmsg = new ALLocalMailMessage(Session.getDefaultInstance(prop), input);
      input.close();
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
    return localmsg;
  }

  /**
   * メールを保存する。
   * 
   * @param messages
   * @return
   */
  public boolean saveMail(ALMailMessage mail, String orgId) {
    boolean res = false;

    try {
      String tmpFileName = getNewFileName();
      res = saveMailToFile(mail, tmpFileName, true);

      if (res) {
        DataContext dataContext = null;
        if (orgId == null || "".equals(orgId)) {
          dataContext = DatabaseOrmService.getInstance().getDataContext();
        } else {
          dataContext = DataContext.createDataContext(orgId);
        }
        res = insertMailToDB(dataContext, (MimeMessage) mail, tmpFileName,
          false, false);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    }
    return res;
  }

  /**
   * 受信サーバから受信した受信可能サイズを超えたメールを保存する。<br />
   * このメールはヘッダ情報のみ、受信サーバから取得し、他の情報は取得しない。
   * 
   * @param localMailMessage
   * @return
   */
  public boolean saveDefectiveMail(ALMailMessage mail, String orgId) {
    boolean res = false;
    try {
      String tmpFileName = getNewFileName();
      res = saveMailToFile(mail, tmpFileName, false);

      if (res) {
        DataContext dataContext = null;
        if (orgId == null || "".equals(orgId)) {
          dataContext = DatabaseOrmService.getInstance().getDataContext();
        } else {
          dataContext = DataContext.createDataContext(orgId);
        }
        res = insertMailToDB(dataContext, (MimeMessage) mail, tmpFileName,
          false, false);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    }
    return res;
  }

  /**
   * メールをファイルに保存します。
   * 
   * @param mail
   * @return
   */
  private boolean saveMailToFile(ALMailMessage mail, String fileName,
      boolean savecontents) {
    boolean res = false;
    BufferedOutputStream output = null;

    try {
      String pop3MailPath = getFullName() + File.separator + fileName;

      // メールの保存
      output = new BufferedOutputStream(new FileOutputStream(pop3MailPath));
      if (savecontents) {
        mail.writeTo(output);
      } else {
        MimeMessage mimeMessage = (MimeMessage) mail;
        Session session = Session.getDefaultInstance(new Properties());
        Message newMsg = new MimeMessage(session);
        Enumeration<?> headers = mimeMessage.getAllHeaders();
        while (headers.hasMoreElements()) {
          Header h = (Header) headers.nextElement();
          newMsg.addHeader(h.getName(), h.getValue());
        }
        newMsg
          .setText("メールのサイズが7MBを超えていたため、このメールを受信できませんでした。\r\n 誠に恐れ入りますが、別のメーラーで受信してください。");
        newMsg.writeTo(output);
      }

      output.flush();
      output.close();

      mail.clearContents();

      res = true;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      try {
        if (output != null) {
          output.close();
        }
      } catch (IOException ie) {
        logger.error("Exception", ie);
      }
      res = false;
    } finally {
      try {
        if (output != null) {
          output.close();
        }
      } catch (IOException ie) {
        logger.error("Exception", ie);
        res = false;
      }
    }
    return res;
  }

  /**
   * 指定されたインデックスのメールを削除する．
   * 
   * @return
   */
  public boolean deleteMail(int mailid) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();

      SelectQuery query = new SelectQuery(EipTMail.class);
      Expression exp1 = ExpressionFactory.matchDbExp(
        EipTMail.MAIL_ID_PK_COLUMN, Integer.valueOf(mailid));
      query.setQualifier(exp1);
      Expression exp2 = ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY,
        user_id);
      query.andQualifier(exp2);
      Expression exp3 = ExpressionFactory.matchExp(
        EipTMail.ACCOUNT_ID_PROPERTY, account_id);
      query.andQualifier(exp3);

      // より厳密にはメールフォルダも指定する。

      List<?> mails = dataContext.performQuery(query);
      if (mails == null || mails.size() == 0) {
        // 指定したMail IDのレコードが見つからない場合
        logger.debug("[ALDbLocalFolder] Not found ID...");
        throw new ALPageNotFoundException();
      }

      EipTMail email = (EipTMail) mails.get(0);

      String filePath = email.getFilePath();

      dataContext.deleteObject(email);
      dataContext.commitChanges();

      // ファイル削除
      File file = new File(getFullName() + File.separator + filePath);
      if (file.exists()) {
        file.delete();
      }

    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
    return true;
  }

  /**
   * 指定されたインデックスのメールを削除する．
   * 
   * @param msgIndexes
   * @return
   */
  public boolean deleteMails(List<String> msgIndexes) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
        .getDataContext();
      SelectQuery query = new SelectQuery(EipTMail.class);
      Expression exp1 = ExpressionFactory.inDbExp(EipTMail.MAIL_ID_PK_COLUMN,
        msgIndexes);
      query.setQualifier(exp1);
      Expression exp2 = ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY,
        Integer.valueOf(user_id));
      query.andQualifier(exp2);
      List<?> mails = dataContext.performQuery(query);
      if (mails == null || mails.size() == 0) {
        // 指定したMail IDのレコードが見つからない場合
        logger.debug("[ALFileLocalFolder] Not found ID...");
        throw new ALPageNotFoundException();
      }

      EipTMail email = null;
      int size = mails.size();
      String[] filePaths = new String[size];
      for (int i = 0; i < size; i++) {
        email = (EipTMail) mails.get(i);
        filePaths[i] = email.getFilePath();
      }

      dataContext.deleteObjects(mails);
      dataContext.commitChanges();

      // ファイル削除
      for (int i = 0; i < size; i++) {
        File file = new File(getFullName() + File.separator + filePaths[i]);
        if (file.exists()) {
          file.delete();
        }
      }

    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
    return true;
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("read_flg", EipTMail.READ_FLG_PROPERTY);
    map.putValue("subject", EipTMail.SUBJECT_PROPERTY);
    map.putValue("person", EipTMail.PERSON_PROPERTY);
    map.putValue("date", EipTMail.EVENT_DATE_PROPERTY);
    map.putValue("volume", EipTMail.FILE_VOLUME_PROPERTY);
    return map;
  }

  /**
   * 新着メール数を取得する。
   * 
   * @return
   */
  public int getNewMailNum() {
    return 0;
  }

  /**
   * 新着メール数を更新する．
   * 
   * @param num
   */
  public void setNewMailNum(int num) {

  }

  /**
   * 指定したフォルダ内のメールの総数を取得する。
   * 
   * @param type
   *          送受信フラグ
   * @return
   */
  public int getMailSum() {
    return 0;
  }

  /**
   * 指定したフォルダ内の未読メール数を取得する．
   * 
   * @return
   */
  public int getUnreadMailNum() {
    return 0;
  }

  /**
   * ローカルフォルダを閉じる．
   */
  public void close() {
  }

  /**
   * 新しいファイル名を生成する．
   * 
   * @return
   */
  public String getNewFileName() {
    int count = 0;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
      DEFAULT_MAIL_FILENAME_DATE_FORMAT);
    Date date = new Date();
    String tmpname = simpleDateFormat.format(date);
    String pop3MailPath = getFullName() + File.separator;
    File file = null;
    String newFileName = null;
    String newFilePath = null;

    while (true) {
      newFileName = tmpname + count;
      newFilePath = pop3MailPath + newFileName;
      file = new File(newFilePath);
      if (!file.exists()) {
        break;
      }
      count += 1;
    }
    return newFileName;
  }
}
