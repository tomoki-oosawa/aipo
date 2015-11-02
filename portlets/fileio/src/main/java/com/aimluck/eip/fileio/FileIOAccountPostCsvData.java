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
package com.aimluck.eip.fileio;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.account.AccountPostResultData;

public class FileIOAccountPostCsvData extends AccountPostResultData {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAccountPostCsvData.class.getName());

  /** データのCSVファイル上での位置(行数) */
  private int line_count;

  /** 同じ部署名がデータベースに存在するかどうか */
  private boolean same_post;

  private boolean is_error;

  /**
   * 初期化 <BR>
   */
  @Override
  public void initField() {
    super.initField();
    setSamePost(false);
    setIsError(false);
  }

  /**
   * データのCSVファイル上での位置(行数)を取得します <BR>
   * 
   * @return
   */
  public int getLineCount() {
    return line_count;
  }

  /**
   * 同じ部署名がデータベースに存在するかどうかを示すフラグを取得します <BR>
   * 
   * @return
   */
  public boolean getSamePost() {
    return same_post;
  }

  /**
   * エラーを含むかどうかを示すフラグを取得します <BR>
   * 
   * @return
   */
  public boolean getIsError() {
    return is_error;
  }

  /**
   * データのCSVファイル上での位置(行数)を入力します <BR>
   * 
   * @param i
   */
  public void setLineCount(int i) {
    line_count = i;
  }

  /**
   * 同じ部署名がデータベースに存在するかどうかを示すフラグを入力します <BR>
   * 
   * @param flg
   */
  public void setSamePost(boolean flg) {
    same_post = flg;
  }

  /**
   * エラーを含むかどうかを示すフラグを入力します <BR>
   * 
   * @param flg
   */
  public void setIsError(boolean flg) {
    is_error = flg;
  }

}
