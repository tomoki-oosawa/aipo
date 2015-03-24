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
package com.aimluck.eip.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.storage.ALStorageService;

/**
 * CSVファイルの内容を管理するための抽象クラスです。 <br />
 *
 */
public abstract class ALCsvAbstractSelectData<M1, M2> extends
    ALAbstractSelectData<M1, M2> {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALCsvAbstractSelectData.class.getName());

  /** データを分割表示する際の分割数 */
  protected int page_count;

  /** CSVファイルの行数 */
  protected int line_count;

  /** エラー総数 */
  protected int error_count;

  /** 正しく入力されたデータの総数 */
  protected int not_error_count;

  /** 表示モード(初期入力時,確認表示,エラー表示) */
  protected int stats;

  /** CSVのセルをデータに格納する順序 */
  protected List<?> sequency;

  /** 一時フォルダの番号 */
  protected String folderIndex;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  @Override
  protected M2 selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   * エラーが残った行のみをファイル出力します。 <br />
   *
   * @param rundata
   * @param str
   * @param filepath
   * @throws Exception
   */
  protected void outputErrorData(RunData rundata, String str, String filepath)
      throws Exception {
    InputStream bais = new ByteArrayInputStream(str.getBytes("Shift_JIS"));
    ALStorageService.createNewFile(bais, filepath);
  }

  /**
   * Shift_JISコードで'\"'を正常に出力するための関数です。 <br />
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
        buf.append((ch));
      }
      buf.append((ch));
    }
    return buf.toString();
  }

  /**
   *
   */
  @Override
  protected Object getResultData(M1 obj) {
    return obj;
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(M2 obj) {
    return null;
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    return map;
  }

  /**
   * ページ数を設定します。 <br />
   *
   * @param i
   */
  public void setPageCount(int i) {
    page_count = i;
  }

  /**
   * ページ数を取得します。 <br />
   *
   * @return
   */
  public int getPageCount() {
    return page_count;
  }

  /**
   * ライン総数を設定します。 <br />
   *
   * @param i
   */
  public void setLineCount(int i) {
    line_count = i;
  }

  /**
   * ライン総数を取得します。 <br />
   *
   * @return
   */
  public int getLineCount() {
    return line_count;
  }

  /**
   * 正しく入力されたデータの総数を入力します。 <br />
   *
   * @param i
   */
  public void setNotErrorCount(int i) {
    not_error_count = i;
  }

  /**
   * 正しく入力されたデータの総数を取得します。 <br />
   *
   * @return
   */
  public int getNotErrorCount() {
    return not_error_count;
  }

  /**
   * エラーの数を入力します。 <br />
   *
   * @param i
   */
  public void setErrorCount(int i) {
    error_count = i;
  }

  /**
   * エラーの数を取得します。 <br />
   *
   * @return
   */
  public int getErrorCount() {
    return error_count;
  }

  /**
   * 表示モードを設定します。 <br />
   */
  public void setState(int i) {
    if ((i > -1) && (i < 3)) {
      stats = i;
    }
  }

  /**
   * 表示モードを取得します。 <br />
   *
   * @return
   */
  public int getState() {
    return stats;
  }

  /**
   * データがエラーかどうかを返します。 <br />
   *
   * @return
   */
  public boolean isError() {
    if ((error_count > 0) && (stats == ALCsvTokenizer.CSV_LIST_MODE_ERROR)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * CSVファイルの読み込み順序を設定します。 <br />
   *
   * @param s
   */
  public void setSequency(List<?> s) {
    sequency = s;
  }

  /**
   * CSVファイルの読み込み順序を取得します。 <br />
   *
   * @return
   */
  public List<?> getSequency() {
    return sequency;
  }

  /**
   * 一時フォルダの番号を指定します。 <br />
   *
   * @param folderIndex
   */
  public void setTempFolderIndex(String folderIndex) {
    this.folderIndex = folderIndex;
  }

  /**
   * 一時フォルダの番号を取得します。 <br />
   *
   * @return
   */
  public String getTempFolderIndex() {
    return folderIndex;
  }

}
