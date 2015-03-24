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
package com.aimluck.eip.webmail;

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * WebメールのResultDataです。 <br />
 */
public class WebMailResultData implements ALData {
  /** 総ヘッダ情報 */
  private List<String> headers = null;

  /** 件名 */
  private ALStringField subject = null;

  /** 差出人 */
  private ALStringField from = null;

  /** 受取人 */
  private ALStringField to = null;

  /** 日付 */
  private ALDateTimeField date = null;

  /** ボディ */
  private ALStringField body = null;

  /** 添付ファイル名 */
  private ALStringField[] attachmentFileNames = null;

  /**
   * 
   * 
   */
  @Override
  public void initField() {
    headers = new ArrayList<String>();
    subject = new ALStringField();
    from = new ALStringField();
    to = new ALStringField();
    date = new ALDateTimeField(WebMailUtils.DATE_TIME_FORMAT);
    body = new ALStringField();
  }

  /**
   * @return
   */
  public List<String> getHeaders() {
    return headers;
  }

  /**
   * @param field
   */
  public void setHeaders(String[] fields) {
    if (fields == null) {
      return;
    }

    ALStringField line = null;
    int length = fields.length;
    for (int i = 0; i < length; i++) {
      line = new ALStringField();
      line.setValue(fields[i]);
      line.setTrim(true);

      headers.add(ALCommonUtils.replaceToAutoCR(line.toString()));
    }
  }

  /**
   * @return
   */
  public ALStringField[] getAttachmentFileNames() {
    return attachmentFileNames;
  }

  /**
   * @param strings
   */
  public void setAttachmentFileNames(String[] strings) {
    if (strings == null || strings.length == 0) {
      return;
    }
    int length = strings.length;
    attachmentFileNames = new ALStringField[length];
    for (int i = 0; i < length; i++) {
      attachmentFileNames[i] = new ALStringField();
      attachmentFileNames[i].setValue(strings[i]);
    }
  }

  public String getBody() {
    return ALEipUtils.getMessageList(body.getValue());
  }

  /**
   * @param string
   */
  public void setBody(String string) {
    body.setValue(string);
  }

  /**
   * @return
   */
  public ALDateTimeField getDate() {
    return date;
  }

  /**
   * @return
   */
  public String getFrom() {
    return ALCommonUtils.replaceToAutoCR(from.toString());
  }

  /**
   * @return
   */
  public String getSubject() {
    return ALCommonUtils.replaceToAutoCR(subject.toString());
  }

  /**
   * @return
   */
  public String getTo() {
    return ALCommonUtils.replaceToAutoCR(to.toString());
  }

  /**
   * @param string
   */
  public void setDate(String string) {
    date.setValue(string);
  }

  /**
   * @param string
   */
  public void setFrom(String string) {
    from.setValue(string);
  }

  /**
   * @param string
   */
  public void setSubject(String string) {
    subject.setValue(string);
  }

  /**
   * @param string
   */
  public void setTo(String string) {
    to.setValue(string);
  }

}
