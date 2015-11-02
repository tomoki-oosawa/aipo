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
package com.aimluck.eip.mail.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.ALAbstractFolder;
import com.aimluck.eip.mail.ALLocalMailMessage;
import com.aimluck.eip.mail.ALMailMessage;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * ローカルのファイルシステムを利用し、送受信したメールを保持するローカルフォルダのクラスです。 <br />
 *
 */
public class ALFileLocalFolder extends ALAbstractFolder {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALFileLocalFolder.class.getName());

  /** メールのファイル名規則 */
  public static final String DEFAULT_MAIL_FILENAME_DATE_FORMAT =
    "yyyyMMddHHmmssSSS";

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
  @Override
  public ALLocalMailMessage getMail(int mailid) {
    try {
      SelectQuery<EipTMail> query = Database.query(EipTMail.class);

      Expression exp1 =
        ExpressionFactory.matchDbExp(EipTMail.MAIL_ID_PK_COLUMN, Integer
          .valueOf(mailid));
      Expression exp2 =
        ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY, user_id);

      EipTMail email = query.setQualifier(exp1.andExp(exp2)).fetchSingle();
      if (email == null) {
        logger.debug("[Mail] Not found ID...");
        return null;
      }

      ALLocalMailMessage msg =
        readMail(getFullName()
          + ALStorageService.separator()
          + email.getFilePath());

      // 未読→既読に変更
      email.setReadFlg("T");
      Database.commit();

      return msg;
    } catch (Throwable t) {
      Database.rollback();
      logger.error("ALFileLocalFolder.getMail", t);
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
      input = new BufferedInputStream(ALStorageService.getFile(filepath));
      localmsg =
        new ALLocalMailMessage(Session.getDefaultInstance(prop), input);
      input.close();
    } catch (Exception ex) {
      logger.error("ALFileLocalFolder.readMail", ex);
    }
    return localmsg;
  }

  /**
   * メールを保存する。
   *
   * @param messages
   * @return
   */
  @Override
  public boolean saveMail(ALMailMessage mail, String orgId) {
    boolean res = false;

    try {
      String tmpFileName = getNewFileName();
      res = saveMailToFile(mail, tmpFileName, true);

      if (res) {

        res = insertMailToDB((MimeMessage) mail, tmpFileName, false, false);
      }
    } catch (Exception ex) {
      logger.error("ALFileLocalFolder.saveMail", ex);
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
  @Override
  public boolean saveDefectiveMail(ALMailMessage mail, String orgId) {
    boolean res = false;
    try {
      String tmpFileName = getNewFileName();
      res = saveMailToFile(mail, tmpFileName, false);

      if (res) {
        res = insertMailToDB((MimeMessage) mail, tmpFileName, false, false);
      }
    } catch (Exception ex) {
      logger.error("ALFileLocalFolder.saveDefectiveMail", ex);
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
    ByteArrayOutputStream output = null;
    try {
      // String pop3MailPath = getFullName() + File.separator + fileName;

      // メールの保存
      output = new ByteArrayOutputStream();
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
        newMsg.setText("メールのサイズが"
          + ALCommonUtils.getMaxFileSize()
          + "MBを超えていたため、このメールを受信できませんでした。\r\n 誠に恐れ入りますが、別のメーラーで受信してください。");
        newMsg.writeTo(output);
      }
      ALStorageService.createNewFile(new ByteArrayInputStream(output
        .toByteArray()), getFullName(), fileName);

      output.flush();
      output.close();

      mail.clearContents();

      res = true;
    } catch (Exception ex) {
      logger.error("ALFileLocalFolder.saveMailToFile", ex);
      try {
        if (output != null) {
          output.close();
        }
      } catch (IOException ie) {
        logger.error("ALFileLocalFolder.saveMailToFile", ie);
      }
      res = false;
    } finally {
      try {
        if (output != null) {
          output.close();
        }
      } catch (IOException ie) {
        logger.error("ALFileLocalFolder.saveMailToFile", ie);
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
  @Override
  public boolean deleteMail(int mailid) {
    try {
      SelectQuery<EipTMail> query = Database.query(EipTMail.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(EipTMail.MAIL_ID_PK_COLUMN, Integer
          .valueOf(mailid));
      Expression exp2 =
        ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY, user_id);
      Expression exp3 =
        ExpressionFactory.matchExp(EipTMail.ACCOUNT_ID_PROPERTY, account_id);

      List<EipTMail> mail_list =
        query
          .andQualifier(exp1)
          .andQualifier(exp2)
          .andQualifier(exp3)
          .fetchList();
      if (mail_list == null || mail_list.size() == 0) {
        logger.debug("[ALDbLocalFolder] Not found ID...");
        throw new ALPageNotFoundException();
      }

      EipTMail record = mail_list.get(0);
      String filePath = record.getFilePath();

      // ファイル削除
      ALStorageService.deleteFile(getFullName()
        + ALStorageService.separator()
        + filePath);

      // メールを削除する
      Database.delete(record);
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      logger.error("ALFileLocalFolder.deleteMail", t);
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
  @Override
  public boolean deleteMails(List<String> msgIndexes) {
    try {
      SelectQuery<EipTMail> query = Database.query(EipTMail.class);
      Expression exp1 =
        ExpressionFactory.inDbExp(EipTMail.MAIL_ID_PK_COLUMN, msgIndexes);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY, Integer
          .valueOf(user_id));

      List<EipTMail> mail_list =
        query.andQualifier(exp1).andQualifier(exp2).fetchList();
      if (mail_list == null || mail_list.size() == 0) {
        logger.debug("[ALFileLocalFolder] Not found ID...");
        throw new ALPageNotFoundException();
      }

      for (EipTMail record : mail_list) {
        String filePath = record.getFilePath();
        ALStorageService.deleteFile(getFullName()
          + ALStorageService.separator()
          + filePath);
      }

      Database.deleteAll(mail_list);
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      logger.error("ALFileLocalFolder.deleteMails", t);
      return false;
    }
    return true;
  }

  /**
   *
   * @return
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
  @Override
  public int getNewMailNum() {
    return 0;
  }

  /**
   * 新着メール数を更新する．
   *
   * @param num
   */
  @Override
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
  @Override
  public int getUnreadMailNum() {
    return 0;
  }

  /**
   * ローカルフォルダを閉じる．
   */
  @Override
  public void close() {
  }

  /**
   * 新しいファイル名を生成する．
   *
   * @return
   */
  public String getNewFileName() {
    return String.valueOf(System.nanoTime());
  }

  /**
   * @param msgIndexes
   * @return
   */
  @Override
  public boolean readMails(List<String> msgIndexes) {
    try {
      SelectQuery<EipTMail> query = Database.query(EipTMail.class);
      Expression exp1 =
        ExpressionFactory.inDbExp(EipTMail.MAIL_ID_PK_COLUMN, msgIndexes);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY, Integer
          .valueOf(user_id));

      List<EipTMail> mail_list =
        query.andQualifier(exp1).andQualifier(exp2).fetchList();
      if (mail_list == null || mail_list.size() == 0) {
        logger.debug("[ALFileLocalFolder] Not found ID...");
        throw new ALPageNotFoundException();
      }
      for (EipTMail record : mail_list) {
        record.setReadFlg("T");
      }
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      logger.error("ALFileLocalFolder.readMails", t);
      return false;
    }
    return true;
  }

  @Override
  public boolean unreadMails(List<String> msgIndexes) {
    try {
      SelectQuery<EipTMail> query = Database.query(EipTMail.class);
      Expression exp1 =
        ExpressionFactory.inDbExp(EipTMail.MAIL_ID_PK_COLUMN, msgIndexes);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTMail.USER_ID_PROPERTY, Integer
          .valueOf(user_id));

      List<EipTMail> mail_list =
        query.andQualifier(exp1).andQualifier(exp2).fetchList();
      if (mail_list == null || mail_list.size() == 0) {
        logger.debug("[ALFileLocalFolder] Not found ID...");
        throw new ALPageNotFoundException();
      }
      for (EipTMail record : mail_list) {
        record.setReadFlg("F");
      }
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      logger.error("ALFileLocalFolder.unreadMails", t);
      return false;
    }
    return true;
  }
}
