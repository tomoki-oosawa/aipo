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
package com.aimluck.eip.mail.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.sk_jp.mail.AttachmentsExtractor;
import com.sk_jp.mail.MailUtility;

/**
 * com.sk_jp.mail.AttachmentsExtractor を継承し、<br />
 * text/plain のパートを添付ファイルとして取り扱えるように拡張したクラスです。 <br />
 * 
 */
public class ALAttachmentsExtractor extends AttachmentsExtractor {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALAttachmentsExtractor.class.getName());

  private final int mode;

  private final List<Part> attachmentParts = new ArrayList<Part>();

  /**
   * 添付ファイル一覧を得るための PartHandler を作成する． message/* のパートや text/plain のパート，inline
   * かつファイル名指定ありのパートも 添付ファイルとして扱う．
   */
  public ALAttachmentsExtractor() {
    this(0);
  }

  /**
   * 添付ファイル一覧を得るための PartHandler を作成する．
   * 
   * @param mode
   *          動作モード．MODE_ で始まる識別子を or 指定します。
   */
  public ALAttachmentsExtractor(int mode) {
    this.mode = mode;
  }

  /**
   * MultipartUtility#process() から呼びだされるメソッド．
   * 
   * @param part
   * @param context
   * @return
   * @throws MessagingException
   * @throws IOException
   */
  @Override
  public boolean processPart(Part part, ContentType context)
      throws MessagingException, IOException {
    if (part.isMimeType("message/*")) {
      if ((mode & MODE_IGNORE_MESSAGE) != 0) {
        return true;
      }
      attachmentParts.add(part);
      return true;
    } else if (part.isMimeType("text/html")) {
      attachmentParts.add(part);
      return true;
    }

    if (MailUtility.getFileName(part) == null) {
      return true;
    }

    if ((mode & MODE_IGNORE_INLINE) != 0
      && Part.INLINE.equalsIgnoreCase(part.getDisposition())) {
      return true;
    }
    attachmentParts.add(part);
    return true;
  }

  /**
   * 指定添付ファイルのファイル名を取得する．
   * 
   * @param index
   * @return
   * @throws MessagingException
   */
  @Override
  public String getFileName(int index) throws MessagingException {
    Part part = attachmentParts.get(index);

    try {

      String name = null;
      if (part.getFileName() != null) {
        name = MimeUtility.decodeText(part.getFileName());
      }

      if (name == null) {
        // 添付ファイル名が取得できない場合は、指定されていなかった場合か、
        // あるいはmessage/*のパートの場合です。
        // この場合は仮のファイル名を付けることとします。
        if (part.isMimeType("message/*")) {
          // If part is Message, create temporary filename.
          name = "message" + index + ".eml";
        } else if (part.isMimeType("text/html")) {
          // If part is a HTML Message, create temporary filename.
          name = "message" + index + ".html";
        } else {
          name = "file" + index + ".tmp";
        }
      }
      return name;
    } catch (UnsupportedEncodingException e) {
      logger.error("ALAttachmentsExtractor.getFileName", e);
      return null;
    }
  }

  /**
   * 添付ファイルの個数を取得する．
   */
  @Override
  public int getCount() {
    return attachmentParts.size();
  }

  /**
   * 指定添付ファイルの Content-Type を取得する．
   */
  @Override
  public String getContentType(int index) throws MessagingException {
    return MailUtility.unfold((attachmentParts.get(index)).getContentType());
  }

  /**
   * 指定添付ファイルのサイズを取得する．
   * 
   * @param index
   * @return
   * @throws MessagingException
   */
  @Override
  public int getSize(int index) throws MessagingException {
    return (attachmentParts.get(index)).getSize();
  }

  /**
   * 指定添付ファイルを読み込むストリームを取得する．
   * 
   * @param index
   * @return
   * @throws MessagingException
   * @throws IOException
   */
  @Override
  public InputStream getInputStream(int index) throws MessagingException,
      IOException {
    return (attachmentParts.get(index)).getInputStream();
  }

}
