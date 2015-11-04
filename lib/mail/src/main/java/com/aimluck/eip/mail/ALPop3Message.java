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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.mail.MessagingException;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.mail.util.UnicodeCorrecter;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALCommonUtils;
import com.sk_jp.mail.MailUtility;
import com.sun.mail.pop3.POP3Message;

/**
 * メール受信時のメモリ占有を解除するためのクラスです。 <br />
 * 
 */
public class ALPop3Message extends POP3Message implements ALMailMessage {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALPop3Message.class.getName());

  // private POP3Folder folder;

  // private int hdrSize;

  // private int msgSize;

  String uid;

  /**
   * コンストラクタ
   * 
   * @param msg
   * @param i
   * @throws MessagingException
   */
  public ALPop3Message(POP3Message msg, int i) throws MessagingException {
    super(msg.getFolder(), i);
    // hdrSize = -1;
    // msgSize = -1;
    uid = "UNKNOWN";
    // folder = (POP3Folder) msg.getFolder();
  }

  /**
   * コンテンツを削除する．
   * 
   */
  @Override
  public void clearContents() {
    this.content = null;
  }

  /**
   * 件名を取得する．
   * 
   */
  @Override
  public String getSubject() throws MessagingException {
    String subject =
      UnicodeCorrecter.correctToCP932(MailUtility
        .decodeText(super.getSubject()));

    if (subject == null || subject.equals("")) {
      subject = "無題";
    }
    return subject;
  }

  /**
   * POP3 サーバから受信した受信可能サイズを超えたメールをローカルファイルシステムに保存する． このメールはヘッダ情報のみ POP3
   * サーバから取得し，他の情報は取得しない．
   * 
   * @param filePath
   * @throws MessagingException
   * @throws IOException
   */
  public void saveDefectiveMail(String filePath) throws MessagingException,
      IOException {
    try {
      String charset = System.getProperty("mail.mime.charset", "ISO-2022-JP");
      PrintWriter writer =
        new PrintWriter(new OutputStreamWriter(
          new FileOutputStream(filePath),
          charset));

      String line = null;
      Enumeration<?> enu = getAllHeaderLines();
      while (enu.hasMoreElements()) {
        line = (String) enu.nextElement();
        if (line.startsWith("Content-Type: multipart/mixed")) {
          line = "Content-Type: text/plain; charset=" + charset;
        }
        writer.println(line);
      }
      writer.println();

      writer.println("【重要】『" + ALOrgUtilsService.getAlias() + "』 からのお知らせです。");
      writer.println("メールのサイズが大きすぎたため、このメールの本文を受信できませんでした。");
      writer.println("受信可能なメールサイズは、"
        + ALCommonUtils.getMaxFileSize()
        + "MB までです。");

      writer.flush();
      writer.close();
    } catch (Exception e) {
      logger.error("ALPop3Message.saveDefectiveMail", e);
    }
  }
}
