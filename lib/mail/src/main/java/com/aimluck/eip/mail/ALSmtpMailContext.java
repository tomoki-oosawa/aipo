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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * メール送信用の情報を保持するクラスです。 <br />
 * 
 */
public class ALSmtpMailContext implements ALMailContext {

  /** 宛先 */
  private String[] to;

  /** CC */
  private String[] cc;

  /** BCC */
  private String[] bcc;

  /** 差出人のメールアドレス */
  private String from;

  /** 差出人の名前 */
  private String name;

  /** 件名 */
  private String subject;

  /** メール内容 */
  private String msgText;

  /** 添付ファイルのファイルパス */
  private String[] filePaths;

  /** メールヘッダに追記する情報 */
  Map<String, String> additionalHeaders;

  public ALSmtpMailContext() {
    additionalHeaders = new LinkedHashMap<String, String>();
  }

  public String[] getTo() {
    return to;
  }

  public String[] getCc() {
    return cc;
  }

  public String[] getBcc() {
    return bcc;
  }

  public String getFrom() {
    return from;
  }

  public String getName() {
    return name;
  }

  public String getSubject() {
    return subject;
  }

  public String getMsgText() {
    return msgText;
  }

  public String[] getFilePaths() {
    return filePaths;
  }

  public Map<String, String> getAdditionalHeaders() {
    return additionalHeaders;
  }

  public void setTo(String[] strs) {
    to = strs;
  }

  public void setCc(String[] strs) {
    cc = strs;
  }

  public void setBcc(String[] strs) {
    bcc = strs;
  }

  public void seFrom(String str) {
    from = str;
  }

  public void setName(String str) {
    name = str;
  }

  public void setSubject(String str) {
    subject = str;
  }

  public void setMsgText(String str) {
    msgText = str;
  }

  public void setFilePaths(String[] strs) {
    filePaths = strs;
  }

  public void setAdditionalHeaders(Map<String, String> m) {
    if (m == null || m.size() == 0) {
      return;
    }
    additionalHeaders.putAll(m);
  }
}
