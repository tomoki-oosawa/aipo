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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.mail.util.ALAttachmentsExtractor;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.mail.util.UnicodeCorrecter;
import com.aimluck.eip.services.storage.ALStorageService;
import com.sk_jp.mail.MailUtility;
import com.sk_jp.mail.MultipartUtility;

/**
 * ローカルに保存するメールを表すクラスです。 <br />
 * 
 */
public class ALLocalMailMessage extends MimeMessage implements ALMailMessage {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALLocalMailMessage.class.getName());

  public static final String MESSAGE_ID = "Message-ID";

  public static final String RETURN_PATH = "Return-Path";

  public static final String DELIVERED_TO = "Delivered-To";

  public static final String RECEIVED = "Received";

  public static final String DATE = "Date";

  public static final String FROM = "From";

  public static final String MIME_VERSION = "MIME-Version";

  public static final String TO = "To";

  public static final String CC = "Cc";

  public static final String BCC = "Bcc";

  public static final String SUBJECT = "Subject";

  public static final String CONTENT_TYPE = "Content-Type";

  public static final String CONTENT_TRANSFER_ENCORDING =
    "Content-Transfer-Encoding";

  public static final String X_Mailer = "X-Mailer";

  public static final String X_Mailer_Value = "Groupware Aipo";

  public static final String X_AIPO_ATTACHMENT_FILE = "X-AIPO-Attachment-File";

  /** 自身をファイルに保存するときのファイル名 */
  private String fileName = null;

  /** HTML メールのファイル名につけるカウンタ */
  // private int attachmentHtmlNum = 0;

  /**
   * コンストラクタ
   * 
   * @param source
   *          メール
   * @param fileName
   *          自身の保存先のファイル名
   * @throws MessagingException
   */
  public ALLocalMailMessage(MimeMessage source, String fileName)
      throws MessagingException {
    super(source);
    this.fileName = fileName;
  }

  /**
   * コンストラクタ
   * 
   * @param fileName
   *          自身の保存先のファイル名
   * @throws MessagingException
   */
  public ALLocalMailMessage(String fileName) throws MessagingException {
    super(Session.getDefaultInstance(new Properties()));
    this.fileName = fileName;
  }

  /**
   * コンストラクタ
   * 
   * @param session
   */
  public ALLocalMailMessage(Session session) throws MessagingException {
    super(session);
  }

  public ALLocalMailMessage(Session session, java.io.InputStream is)
      throws MessagingException {
    super(session, is);
  }

  /**
   * ファイル名を返す．
   * 
   * @return
   */
  public String getMailMassageFileName() {
    return fileName;
  }

  /**
   * メールヘッダを追加する．
   * 
   * @param line
   * @throws MessagingException
   */
  @Override
  public void addHeaderLine(String line) throws MessagingException {
    StringTokenizer st = new StringTokenizer(line, ":");
    if (!st.hasMoreTokens()) {
      return;
    }
    String key = st.nextToken();
    if (!st.hasMoreTokens()) {
      return;
    }
    String value = st.nextToken().trim();
    addHeader(key, value);
  }

  /**
   * 指定されたフォルダからメールを読み込む．
   * 
   * @param folderPath
   */
  public void readMail(String folderPath) {
    try {
      parse(ALStorageService.getFile(folderPath
        + ALStorageService.separator()
        + getMailMassageFileName()));

    } catch (Exception e) {
      logger.error("ALLocalMailMessage.readMail", e);
    }
  }

  /**
   * 指定されたフォルダにメールを保存する．
   * 
   * @param folderPath
   */
  public void saveMail(String folderPath) {
    try {
      ALStorageService.createNewFile(
        getInputStream(),
        folderPath,
        getMailMassageFileName());
    } catch (Exception e) {
      logger.error("ALLocalMailMessage.saveMail", e);
    }
  }

  /**
   * 指定されたフォルダに添付ファイルを保存する．
   * 
   * @param filePath
   * @param fileBytes
   */
  public void saveAttachmentFile(String folderPath, String fileName,
      byte[] fileBytes) {
    try {

      ALStorageService.createNewFile(
        new ByteArrayInputStream(fileBytes),
        folderPath,
        fileName);

    } catch (Exception e) {
      logger.error("ALLocalMailMessage.saveAttachmentFile", e);
    }
  }

  /**
   * メールボディ部のテキストを取得する．
   * 
   * @return
   */
  public String getBodyText() {
    String text = null;
    try {
      String contentType = this.getContentType();

      // au iPhone 対策
      this.setHeader("Content-Type", contentType
        .replace("cp932", "Windows-31J"));
      text = MultipartUtility.getFirstPlainText(this);
      this.setHeader("Content-Type", contentType);

    } catch (Exception e) {
      logger.error("ALLocalMailMessage.getBodyText", e);
    }
    return text;
  }

  /**
   * メールの全てのヘッダを取得する． エンコードを変換する．
   * 
   * @return
   */
  public String getHeader() {
    StringBuffer sb = new StringBuffer();
    try {
      Enumeration<?> enu = getAllHeaderLines();
      while (enu.hasMoreElements()) {
        String line = (String) enu.nextElement();
        sb.append(MailUtility.decodeText(line)).append(ALMailUtils.CR);
      }
    } catch (Exception e) {
      logger.error("ALLocalMailMessage.getHeader", e);
      return "";
    }
    return UnicodeCorrecter.correctToCP932(sb.toString());
  }

  /**
   * メールの全てのヘッダ情報を配列として取得する．
   * 
   * @return
   */
  public String[] getHeaderArray() {
    return ALMailUtils.getLines(getHeader());
  }

  /**
   * メールの本文を一行毎に格納した配列を取得する．
   * 
   * @return
   */
  public String[] getBodyTextArray() {
    return ALMailUtils.getLines(getBodyText());
  }

  /**
   * 添付ファイルのファイル名を配列として取得する．
   * 
   * @return
   */
  public String[] getAttachmentFileNameArray() {
    String[] filenames = null;
    ALAttachmentsExtractor h = new ALAttachmentsExtractor();
    try {
      MultipartUtility.process(this, h);
      filenames = h.getFileNames();
    } catch (Exception e) {
      logger.error("ALLocalMailMessage.getAttachmentFileNameArray", e);
      return null;
    }
    return filenames;
  }

  /**
   * メッセージ ID を独自形式にするためのオーバーライドメソッド．
   * 
   * @throws MessagingException
   */
  @Override
  protected void updateHeaders() throws MessagingException {
    super.updateHeaders();

    // メッセージ ID をセット
    setHeader(MESSAGE_ID, "<" + getMessageId() + ">");
  }

  /**
   * メッセージ ID を生成する．
   * 
   * @return
   */
  private String getMessageId() {

    Calendar cal = new GregorianCalendar();
    // 日付を表示形式に変換
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
    sdf.setTimeZone(TimeZone.getDefault());
    String time = sdf.format(cal.getTime());

    Random random = new Random(cal.getTimeInMillis());
    int tmp = random.nextInt();
    int randomNumber =
      (tmp != Integer.MIN_VALUE ? Math.abs(tmp) : Math.abs(tmp + 1));

    String smtpHostName = session.getProperty(ALSmtpMailSender.MAIL_SMTP_HOST);

    StringBuffer messageId = new StringBuffer();
    messageId.append(time).append(".").append(randomNumber).append("@").append(
      smtpHostName);

    return messageId.toString();
  }

  /**
   * 件名を取得する．
   * 
   * @return
   * @throws MessagingException
   */
  @Override
  public String getSubject() throws MessagingException {
    String subject =
      UnicodeCorrecter.correctToCP932(MailUtility
        .decodeText(super.getSubject()));

    if (subject == null || subject.equals("")) {
      subject = "";
    }
    return subject;
  }

  /**
   * このメールが，HTML メールかを検証する． HTML メールの場合は，true．
   * 
   * @return
   */
  public boolean isHtmlMail() {
    try {
      // 添付ファイルの有無
      ALAttachmentsExtractor h = new ALAttachmentsExtractor();
      MultipartUtility.process(this, h);
      boolean hasAttachments = (h.getCount() > 0) ? true : false;
      return hasAttachments;
    } catch (Exception e) {
      logger.error("ALLocalMailMessage.isHtmlMail", e);
      return false;
    }
  }

  /*
   * boolean htmlMail = false; try { htmlMail = isHtmlMailSub(this); } catch
   * (MessagingException me) { htmlMail = false; } catch (IOException e) {
   * htmlMail = false; } return htmlMail; ? }
   * 
   * private boolean isHtmlMailSub(Part part) throws IOException,
   * MessagingException { if (part.isMimeType("text/plain")) { return false; }
   * else if (part.isMimeType("text/html")) { return true; } else if
   * (part.isMimeType("multipart/*")) { Multipart mp = (Multipart)
   * part.getContent(); for (int i = 0; i < mp.getCount(); i++) { if
   * (isHtmlMailSub(mp.getBodyPart(i))) { return true; } } }
   * 
   * return false; }
   * 
   * /** 添付ファイルを含んでいるかを検証する． 含んでいる場合は，true． @return
   */
  public boolean hasAttachments() {
    ALAttachmentsExtractor h = new ALAttachmentsExtractor();
    try {
      MultipartUtility.process(this, h);
      if (h.getCount() > 0) {
        return true;
      }
    } catch (Exception e) {
      logger.error("ALLocalMailMessage.hasAttachments", e);
      return false;
    }
    return false;
  }

  /**
   * 指定したインデックスのコンテンツの InputStream を取得する．
   * 
   * @param attachmentIndex
   * @return
   */
  public InputStream getInputStream(int attachmentIndex) {
    InputStream in = null;
    ALAttachmentsExtractor h = new ALAttachmentsExtractor();
    try {
      MultipartUtility.process(this, h);
      in = h.getInputStream(attachmentIndex);
    } catch (Exception e) {
      logger.error("ALLocalMailMessage.getInputStream", e);
      return null;
    }
    return in;
  }

  /**
   * 指定したインデックスの添付ファイル名を取得する．
   * 
   * @param attachmentIndex
   * @return
   */
  public String getFileName(int attachmentIndex) {
    String filename = null;
    ALAttachmentsExtractor h = new ALAttachmentsExtractor();
    try {
      MultipartUtility.process(this, h);
      filename = h.getFileName(attachmentIndex);
    } catch (Exception e) {
      logger.error("ALLocalMailMessage.getFileName", e);
      return null;
    }
    return filename;
  }

  @Override
  public int getSize() {
    byte[] b = null;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try {
      writeTo(output);
      b = output.toByteArray();
    } catch (Exception e) {
      logger.error("ALLocalMailMessage.getSize", e);
      return -1;
    } finally {
      try {
        output.close();
      } catch (IOException ioee) {
        return -1;
      }
    }
    return b.length;
  }

  /**
   * メールの送信日時を取得する．
   * 
   * @return
   * @throws MessagingException
   */
  @Override
  public Date getSentDate() throws MessagingException {
    return MailUtility.parseDate(getHeader(DATE, null));
  }

  /**
   * 受信したメールの TO，CC，BCC のフォーマットが RFC に則っていない場合には， 独自処理で対処する： ・「
   * <user@domain」や「user@domain>」のように片方のカッコのみ見つかった場合の処理 ・「 < <user@domain>」や「
   * <user@domain>>」のようにカッコの対が揃っていない場合の処理
   * 
   * @param recipienttype
   * @return
   * @throws MessagingException
   */
  @Override
  public Address[] getRecipients(javax.mail.Message.RecipientType recipienttype)
      throws MessagingException {

    // RFC に則っているかを検証する．
    String recipients =
      MailUtility.decodeText(this.getHeader(recipienttype.toString(), null));
    if (recipients == null) {
      return super.getRecipients(recipienttype);
    }

    StringTokenizer st = new StringTokenizer(recipients, ",;");
    String token = null;
    boolean found = false;
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      if ((token.indexOf('<') >= 0 && token.indexOf('>') == -1)
        || (token.indexOf('<') == -1 && token.indexOf('>') >= 0)
        // 「<user@domain」や「user@domain>」のように
        // 片方のカッコのみ見つかった場合の処理
        || (token.indexOf('(') >= 0 && token.indexOf('>') == -1)
        || (token.indexOf('(') == -1 && token.indexOf(')') >= 0)) {
        // 「foo) <user@domain>」や「(foo <user@domain>」のように
        // 片方のカッコのみ見つかった場合の処理
        found = true;
      } else {
        if ((token.indexOf('<') >= 0 && token.indexOf('<') != token
          .lastIndexOf('<'))
          || (token.indexOf('>') >= 0 && token.indexOf('>') != token
            .lastIndexOf('>'))
          // 「<<user@domain>」や「<user@domain>>」のように
          // カッコの対が揃っていない場合の処理
          || (token.indexOf('(') >= 0 && token.indexOf(')') != token
            .lastIndexOf('('))
          || (token.indexOf(')') >= 0 && token.indexOf(')') != token
            .lastIndexOf(')'))) {
          // 「((foo) <user@domain>」や「(foo)) <user@domain>」のように
          // カッコの対が揃っていない場合の処理
          found = true;
        }
      }
    }

    if (found) {
      int index = 0;
      st = new StringTokenizer(recipients, ",;");
      Address[] addresses = new InternetAddress[st.countTokens()];
      while (st.hasMoreTokens()) {
        token = st.nextToken();
        try {
          addresses[index] = new InternetAddress(token, false);
        } catch (AddressException ae) {
          addresses[index] = new InternetAddress();
          if (index > 0 && token.contains("\r\n\t")) {
            token = token.substring(4);
          }
          ((InternetAddress) addresses[index]).setAddress(token);
        }
        index++;
      }
      return addresses;
    } else {
      return super.getRecipients(recipienttype);
    }
  }

  /**
   * TO，CC，BCC のフォーマットをチェックしない場合は strict = false
   * 
   * @param recipienttype
   * @param strict
   * @return
   * @throws MessagingException
   */
  public Address[] getRecipients(
      javax.mail.Message.RecipientType recipienttype, boolean strict)
      throws MessagingException {
    if (strict) {
      return getRecipients(recipienttype);
    } else {
      Address[] addresses;
      try {
        addresses = super.getRecipients(recipienttype);
      } catch (AddressException e) {
        String recipients =
          MailUtility
            .decodeText(this.getHeader(recipienttype.toString(), null));
        if (recipients == null) {
          return super.getRecipients(recipienttype);
        }
        int index = 0;
        String token = null;
        StringTokenizer st = new StringTokenizer(recipients, ",;");
        addresses = new InternetAddress[st.countTokens()];
        while (st.hasMoreTokens()) {
          token = st.nextToken();
          try {
            addresses[index] = new InternetAddress(token, false);
          } catch (AddressException ae) {
            addresses[index] = new InternetAddress();
            ((InternetAddress) addresses[index]).setAddress(token);
          }
          index++;
        }
      }
      return addresses;
    }
  }

  @Override
  public void clearContents() {

  }

}
