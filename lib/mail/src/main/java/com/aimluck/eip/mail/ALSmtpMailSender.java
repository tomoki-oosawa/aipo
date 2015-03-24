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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.services.storage.ALStorageService;
import com.sk_jp.mail.MailUtility;
import com.sun.mail.smtp.SMTPTransport;

/**
 * メール送信（SMTP）を操作する抽象クラスです。 <br />
 * 
 */
public abstract class ALSmtpMailSender implements ALMailSender {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALSmtpMailSender.class.getName());

  /** <code>AUTH_SEND_NONE</code> 送信時の認証方式（認証なし） */
  public static final int AUTH_SEND_NONE = 0;

  /** <code>AUTH_SEND_POP_BEFORE_SMTP</code> 送信時の認証方式（POP before SMTP） */
  public static final int AUTH_SEND_POP_BEFORE_SMTP = 1;

  /** <code>AUTH_SEND_SMTP_AUTH</code> 送信時の認証方式（SMTP 認証） */
  public static final int AUTH_SEND_SMTP_AUTH = 2;

  /** <code>AUTH_SEND_SSL_AUTH</code> 送信時の認証方式（暗号化なし） */
  public static final int ENCRYPTION_SEND_NONE = 0;

  /** <code>AUTH_SEND_SSL_AUTH</code> 送信時の認証方式（SSL暗号化） */
  public static final int ENCRYPTION_SEND_SSL = 1;

  /** メール送信時の処理結果（送信に成功した） */
  public static final int SEND_MSG_SUCCESS = 0;

  /** メール送信時の処理結果（送信に失敗した） */
  public static final int SEND_MSG_FAIL = 1;

  /** メール送信時の処理結果（メールサイズが送信可能サイズよりも大きいため，送信に失敗した） */
  public static final int SEND_MSG_OVER_MAIL_MAX_SIZE = 2;

  /** メール送信時の処理結果（ロックがかかっていて，送信に失敗した） */
  public static final int SEND_MSG_LOCK = 3;

  /** メール送信時の処理結果（Pop before SMTPの認証失敗で送信に失敗した） */
  public static final int SEND_MSG_FAIL_POP_BEFORE_SMTP_AUTH = 4;

  /** メール送信時の処理結果（SMTP認証の認証失敗で送信に失敗した） */
  public static final int SEND_MSG_FAIL_SMTP_AUTH = 5;

  /** メール送信時の処理結果（管理者のメールアカウントが設定されていないために送信に失敗した） */
  public static final int SEND_MSG_FAIL_NO_ACCOUNT = 6;

  /** 接続時のタイムアウト時間 */
  private final String CONNECTION_TIMEOUT = "120000";

  /** 接続後のタイムアウト時間 */
  private final String TIMEOUT = "420000";

  /** SMTP サーバ */
  public static final String MAIL_SMTP_HOST = "mail.smtp.host";

  /** SMTP サーバのポート番号 */
  public static final String MAIL_SMTP_PORT = "mail.smtp.port";

  /** SMTP サーバとの接続時のタイムアウト */
  public static final String MAIL_SMTP_CONNECTION_TIMEOUT =
    "mail.stmp.connectiontimeout";

  /** SMTP サーバとの接続後のタイムアウト */
  public static final String MAIL_SMTP_TIMEOUT = "mail.stmp.timeout";

  /** 文字コード（ISO-2022-JP） */
  public static final String CHARSET_ISO2022JP = "iso-2022-jp";

  /** SMTP サーバへの接続情報 */
  protected Properties smtpServerProp = null;

  /** POP BEFORE SMTP の WAIT 時間 (ms) */
  protected long POP_BEFORE_SMTP_WAIT_TIME = 1000;

  /** SSL ファクトリー */
  public static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

  /** 送信用セッション */
  protected Session session = null;

  protected ALSmtpMailSenderContext scontext = null;

  public ALSmtpMailSender(ALMailSenderContext scontext) {
    this.scontext = (ALSmtpMailSenderContext) scontext;
  }

  /**
   * 新規作成のメールを取得します。
   * 
   * @param mcontext
   * @return
   */
  private ALLocalMailMessage createMessage(ALSmtpMailContext mcontext) {
    System.setProperty("mail.mime.charset", "ISO-2022-JP");
    System.setProperty("mail.mime.decodetext.strict", "false");

    ALLocalMailMessage msg = null;
    smtpServerProp = new Properties();
    smtpServerProp.setProperty(MAIL_SMTP_HOST, scontext.getSmtpHost());
    smtpServerProp.setProperty(MAIL_SMTP_PORT, scontext.getSmtpPort());
    smtpServerProp.put(MAIL_SMTP_CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
    smtpServerProp.put(MAIL_SMTP_TIMEOUT, TIMEOUT);
    smtpServerProp.setProperty("mail.mime.address.strict", "false");
    smtpServerProp.put("mail.smtp.localhost", "localhost");

    if (scontext.getEncryptionFlag() == ENCRYPTION_SEND_SSL) {
      /** SSL 暗号化 */
      smtpServerProp.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
      smtpServerProp.setProperty("mail.smtp.socketFactory.fallback", "false");
      smtpServerProp.setProperty("mail.smtp.socketFactory.port", scontext
        .getSmtpPort());
    }

    // SMTP サーバのアドレスをセット
    if (scontext.getAuthSendFlag() == AUTH_SEND_SMTP_AUTH
      && scontext.getAuthSendUserId() != null
      && !"".equals(scontext.getAuthSendUserId())
      && scontext.getAuthSendUserPassword() != null
      && !"".equals(scontext.getAuthSendUserPassword())) {
      /** SMTP AUTH */
      smtpServerProp.put("mail.smtp.auth", "true");
      session =
        Session.getInstance(smtpServerProp, new ALSmtpAuth(scontext
          .getAuthSendUserId(), scontext.getAuthSendUserPassword()));
    } else {
      session = Session.getInstance(smtpServerProp, null);
    }

    // デバックモード
    // session.setDebug(true);

    try {

      // メッセージを生成
      msg = new ALLocalMailMessage(session);
      // 送信元メールアドレスと送信者名をセット
      msg.setFrom(new InternetAddress(mcontext.getFrom(), ALMailUtils
        .encodeWordJIS(mcontext.getName())));
      // メールのあて先（to）をセット
      if (mcontext.getTo() == null) {
        throw new MessagingException();
      }
      setRecipient(msg, Message.RecipientType.TO, mcontext.getTo());
      // メールのあて先（cc）をセット
      if (mcontext.getCc() != null) {
        setRecipient(msg, Message.RecipientType.CC, mcontext.getCc());
      }
      // メールのあて先（bcc）をセット
      if (mcontext.getBcc() != null) {
        setRecipient(msg, Message.RecipientType.BCC, mcontext.getBcc());
      }

      // メールの件名をセット
      msg.setSubject(ALMailUtils.encodeWordJIS(mcontext.getSubject()));
      // メールの送信日時をセット
      msg.setSentDate(new Date());

      if (mcontext.getFilePaths() == null) {
        // メールの本文をセット
        msg.setText(mcontext.getMsgText() + "\r\n", CHARSET_ISO2022JP);
        setHeader(msg, mcontext);
      } else {
        String[] checkedFilePaths = mcontext.getFilePaths();
        int checkedFilePathsLength = checkedFilePaths.length;
        if (checkedFilePathsLength <= 0) {
          // MultiPart にせず，メールの本文をセット
          msg.setText(mcontext.getMsgText() + "\r\n", CHARSET_ISO2022JP);
          setHeader(msg, mcontext);
        } else {
          setHeader(msg, mcontext);
          // 複数のボディを格納するマルチパートオブジェクトを生成
          Multipart multiPart = new MimeMultipart();

          // テキストのボディパートを作成
          MimeBodyPart mimeText = new MimeBodyPart();
          // メールの内容を指定
          mimeText.setText(mcontext.getMsgText(), CHARSET_ISO2022JP);
          // １つ目のボディパートを追加
          multiPart.addBodyPart(mimeText);

          // 添付ファイルのボディパートを作成
          MimeBodyPart mimeFile = null;
          for (int i = 0; i < checkedFilePathsLength; i++) {
            final String filePath = checkedFilePaths[i];
            final String fileName =
              ALMailUtils.getFileNameFromText(checkedFilePaths[i]);
            mimeFile = new MimeBodyPart();
            mimeFile.setDataHandler(new DataHandler(new DataSource() {

              @Override
              public String getContentType() {
                return FileTypeMap.getDefaultFileTypeMap().getContentType(
                  fileName);
              }

              @Override
              public InputStream getInputStream() throws IOException {
                return ALStorageService.getFile(filePath);
              }

              @Override
              public String getName() {
                return fileName;
              }

              @Override
              public OutputStream getOutputStream() throws IOException {
                throw new UnsupportedOperationException("getOutputStream");
              }

            }));
            MailUtility.setFileName(mimeFile, ALMailUtils
              .getFileNameFromText(checkedFilePaths[i]), "ISO-2022-JP", null);

            // 添付ファイルをボディパートに追加
            multiPart.addBodyPart(mimeFile);
          }

          // マルチパートオブジェクトをメッセージに設定
          msg.setContent(multiPart);
        }
      }

    } catch (Exception e) {
      logger.error("ALSmtpMailSender.createMessage", e);
      return null;
    }

    return msg;
  }

  private void setHeader(ALLocalMailMessage msg, ALSmtpMailContext mcontext)
      throws Exception {

    // メールの形式をセット
    msg.setHeader(
      ALLocalMailMessage.CONTENT_TYPE,
      "text/plain; charset=ISO-2022-JP");
    msg.setHeader(ALLocalMailMessage.CONTENT_TRANSFER_ENCORDING, "7bit");
    msg.setHeader(
      ALLocalMailMessage.X_Mailer,
      ALLocalMailMessage.X_Mailer_Value);

    // 追加ヘッダをセットする
    Map<String, String> headers = mcontext.getAdditionalHeaders();
    if (headers != null && !headers.isEmpty()) {
      synchronized (headers) {
        String key = null;
        String value = null;
        Map.Entry<String, String> entry = null;
        for (Iterator<Map.Entry<String, String>> i =
          headers.entrySet().iterator(); i.hasNext();) {
          entry = i.next();
          key = entry.getKey();
          value = entry.getValue();
          msg.setHeader(key, value);
        }
      }
    }
  }

  /**
   * SMTP サーバへメールを送信する．
   * 
   * @param to
   * @param cc
   * @param bcc
   * @param from
   * @param name
   * @param subject
   * @param msgText
   * @param filePaths
   * @return
   */
  @Override
  public int send(ALMailContext context) {
    try {
      ALSmtpMailContext mcontext = (ALSmtpMailContext) context;

      ALLocalMailMessage msg = createMessage(mcontext);

      if (msg == null) {
        return SEND_MSG_FAIL;
      }

      int mailSize = msg.getSize();
      if (mailSize > ALMailUtils.getMaxMailSize()) {
        // メール容量のチェック
        return SEND_MSG_OVER_MAIL_MAX_SIZE;
      }

      if (scontext.getAuthSendFlag() == AUTH_SEND_NONE) {
        Transport.send(msg);
      } else if (scontext.getAuthSendFlag() == AUTH_SEND_POP_BEFORE_SMTP) {
        // POP before SMTP を実行する．
        // 認証のみ検証する．メールは受信しない．
        boolean success =
          ALPop3MailReceiver.isAuthenticatedUser(
            scontext.getPop3Host(),
            scontext.getPop3Port(),
            scontext.getPop3UserId(),
            scontext.getPop3UserPasswd(),
            scontext.getPop3EncryptionFlag());
        if (!success) {
          return SEND_MSG_FAIL_POP_BEFORE_SMTP_AUTH;
        } else {
          Thread.sleep(POP_BEFORE_SMTP_WAIT_TIME);
        }

        Transport.send(msg);
      } else if (scontext.getAuthSendFlag() == AUTH_SEND_SMTP_AUTH) {
        Transport transport = session.getTransport("smtp");
        SMTPTransport smtpt = (SMTPTransport) transport;
        smtpt.setSASLRealm("localhost"); // [SASLレルム]
        smtpt.connect(
          scontext.getSmtpHost(),
          scontext.getAuthSendUserId(),
          scontext.getAuthSendUserPassword());
        smtpt.sendMessage(msg, msg.getAllRecipients());
        smtpt.close();
      }

      // メッセージの保存
      ALFolder sendFolder = getALFolder();
      sendFolder.saveMail(msg, null);
    } catch (AuthenticationFailedException ex) {
      logger.error("ALSmtpMailSender.send", ex);
      return SEND_MSG_FAIL_SMTP_AUTH;
    } catch (Exception ex) {
      logger.error("ALSmtpMailSender.send", ex);
      return SEND_MSG_FAIL;
    } catch (Throwable e) {
      logger.error("ALSmtpMailSender.send", e);
      return SEND_MSG_FAIL;
    }

    return SEND_MSG_SUCCESS;
  }

  abstract protected ALFolder getALFolder();

  /**
   * メールの宛名をセットする．
   * 
   * @param msg
   * @param recipientType
   * @param addrString
   * @throws AddressException
   * @throws MessagingException
   */
  private void setRecipient(Message msg, Message.RecipientType recipientType,
      String[] addrString) throws AddressException, MessagingException {
    if (addrString == null) {
      return;
    }
    int addrStringLength = addrString.length;
    InternetAddress[] address = new InternetAddress[addrStringLength];
    for (int i = 0; i < addrStringLength; i++) {
      address[i] = ALMailUtils.getInternetAddress(addrString[i]);
    }
    msg.setRecipients(recipientType, address);
  }

}
