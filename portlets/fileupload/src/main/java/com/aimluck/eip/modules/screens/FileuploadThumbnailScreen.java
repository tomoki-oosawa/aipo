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
package com.aimluck.eip.modules.screens;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.modules.screens.RawScreen;
import org.apache.turbine.util.RunData;

/**
 * サムネイル画像を画像データとして出力するクラスです。 <br />
 * 
 */
public class FileuploadThumbnailScreen extends RawScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileuploadThumbnailScreen.class.getName());

  /** ファイル名 */
  private String fileName = null;

  /** サムネイル画像 */
  private byte[] file = null;

  /**
   * @see org.apache.turbine.modules.screens.RawScreen#getContentType(org.apache.turbine.util.RunData)
   */
  @Override
  protected String getContentType(RunData rundata) {
    return "application/octet-stream";
  }

  /**
   * ファイル名
   * 
   * @return
   */
  protected String getFileName() {
    return fileName;
  }

  /**
   * ファイル名
   * 
   * @return
   */
  protected byte[] getFile() {
    return file;
  }

  protected void setFileName(String fileName) {
    this.fileName = fileName;
  }

  protected void setFile(byte[] file) {
    this.file = file;
  }

  /**
   * @see org.apache.turbine.modules.screens.RawScreen#doOutput(org.apache.turbine.util.RunData)
   */
  @Override
  protected void doOutput(RunData rundata) throws Exception {
    ServletOutputStream out = null;
    try {
      String attachmentRealName =
        new String(getFileName().getBytes("Shift_JIS"), "8859_1");

      HttpServletResponse response = rundata.getResponse();
      // ファイル名の送信(attachment部分をinlineに変更すればインライン表示)
      response.setHeader("Content-disposition", "attachment; filename=\""
        + attachmentRealName
        + "\"");
      response.setHeader("Cache-Control", "aipo");
      response.setHeader("Pragma", "aipo");

      // ファイル内容の出力
      out = response.getOutputStream();

      byte[] file = getFile();
      if (file == null) {
        // サムネイル画像がデータベースに保存されていない場合
        throw new FileNotFoundException();
      }

      out.write(file);
      out.flush();
    } catch (Exception e) {
      logger.error("[ERROR]" + e);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException ex) {
        logger.error("[ERROR]" + ex);
      }
    }
  }
}
