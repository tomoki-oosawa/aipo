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
package com.aimluck.eip.modules.screens;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALEipConstants;

/**
 * ブラウザにCSVファイルを返すクラスです。 <br />
 * 
 */
public abstract class ALCSVScreen extends RawScreen {

  /** <code>logger</code> loger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALCSVScreen.class.getName());

  /** コンテントタイプ */
  private static final String CONTENT_TYPE = "text/json;charset="
    + ALEipConstants.DEF_CONTENT_ENCODING;

  /** CSV ファイルのエンコーディング名 */
  public static final String DEF_CSV_FILE_ENCODING = "Shift_JIS";

  private String csvFileEncoding = DEF_CSV_FILE_ENCODING;

  /**
   * 
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    ServletOutputStream out = null;

    try {

      String result = getCSVString(rundata);

      String fileName = getFileName();

      HttpServletResponse response = rundata.getResponse();
      // ファイル名の送信
      response.setHeader("Content-disposition", "attachment; filename=\""
        + fileName
        + "\"");
      response.setHeader("Cache-Control", "aipo");
      response.setHeader("Pragma", "aipo");

      // ファイル内容の出力
      out = response.getOutputStream();
      out.write(result.getBytes(csvFileEncoding));
      out.flush();
      out.close();
    } catch (Exception e) {
      logger.error("ALCSVScreen.doOutput", e);
    }

  }

  /**
   * ファイルに'\"'を正しく出力するためのエスケープを加えます。 <br />
   * 
   * @param str
   * @return
   */
  protected String makeOutputItem(String str) {
    StringBuffer buf = new StringBuffer();
    char ch;
    for (int i = 0; i < str.length(); i++) {
      ch = str.charAt(i);
      if (ch == '\"') {
        buf.append(ch);
      }
      buf.append(ch);
    }
    return buf.toString();
  }

  /**
   * 
   * @param rundata
   * @return
   */
  @Override
  protected String getContentType(RunData rundata) {
    return CONTENT_TYPE;
  }

  protected void setCsvEncoding(String encoding) {
    csvFileEncoding = encoding;
  }

  /** ファイルに書き出す内容を取得します */
  protected abstract String getCSVString(RunData rundata) throws Exception;

  /** ファイル名を取得します */
  protected abstract String getFileName();

}
