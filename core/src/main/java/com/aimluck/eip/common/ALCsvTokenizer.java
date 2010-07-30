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
package com.aimluck.eip.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 * CSVファイルをセルごとに読み取るためのクラスです。 <br />
 *
 */
public class ALCsvTokenizer {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(ALCsvTokenizer.class.getName());

  public static final String CSV_TEMP_FOLDER = JetspeedResources.getString(
      "aipo.tmp.directory", "") + System.getProperty("file.separator") + "csv";

  /** 一度に表示される件数 */
  public static final int CSV_SHOW_SIZE = 1000;

  /** 一度に表示されるエラーの件数 */
  public static final int CSV_SHOW_ERROR_SIZE = 100;

  /** 表示モード 初期入力確認 */
  public static final int CSV_LIST_MODE_READ = 0;

  /** 表示モード 入力確認 */
  public static final int CSV_LIST_MODE_NO_ERROR = 1;

  /** 表示モード エラーデータ確認 */
  public static final int CSV_LIST_MODE_ERROR = 2;

  /** ダブルクウォートの中かどうか */
  private boolean inquote;

  /** ファイルの終わりにきたら'-1'を返す */
  public int eof;

  /** 改行したかどうか */
  public boolean line;

  private InputStreamReader in;

  /** \r\nではなく\rだった場合に、次の行の1文字目を保存します。 */
  public int newline_r_reminder;

  /** 上記変数newline_r_reminderに取得するべき値が入っているか */
  public boolean flag_reminder_csv = false;

  /**
   * コンストラクタ <BR>
   *
   */
  public ALCsvTokenizer() {
    line = false;
    inquote = false;
    eof = 0;
  }

  /**
   * 指定したファイルパスで初期化します。 <br />
   *
   * @param fname
   */
  public boolean init(String fname) {
    line = false;
    inquote = false;
    eof = 0;
    try {
      // in = new FileReader(fname);
      File file = new File(fname);
      if (!file.exists()) {
        file = null;
        return false;
      }
      file = null;
      FileInputStream is = new FileInputStream(fname);
      in = new InputStreamReader(is, "Shift_JIS");
      return true;
    } catch (IOException ie) {
      // System.out.println("File Not Found!! - "+fname);
      eof = -1;
      logger.error("[ERROR]", ie);
      return false;
    }

  }

  /**
   * CSVファイルを１アイテムずつ読み取ります。 <br />
   *
   * @return
   */
  public String nextToken() {
    int ch;
    String str = "";
    line = false;
    inquote = false;
    if (eof == -1) {
      return "";
    }

    try {
      str = "";
      if (flag_reminder_csv) {
        ch = newline_r_reminder;
        flag_reminder_csv = false;
      } else {
        eof = (ch = in.read());
      }
      if (ch == '\"') {
        inquote = true;
      }
      if ((ch == ',') && (!inquote)) {
        return str;
      } else if (((ch == '\n') || (ch == '\r')) && (!inquote)) {
        if (ch == '\r') {
          eof = ch = in.read();
          if (ch != '\n' && eof != -1) {
            flag_reminder_csv = true;
            newline_r_reminder = ch;
          }
        }
        line = true;
        // eof = (ch = in.read());
        return str;
      } else {
        while (eof != -1) {
          if (ch == '\n' || ch == '\r' && (inquote)) {
            str += "\r\n";
          } else if (ch != '\"') {
            str += (char) ch;
          }
          eof = (ch = in.read());
          if (ch == '\r' && (!inquote)) {
            line = true;
            eof = (ch = in.read());
            if (ch != '\n' && eof != -1) {
              flag_reminder_csv = true;
              newline_r_reminder = ch;
              return str;
            }
          }
          if (ch == '\"') {
            eof = (ch = in.read());
            if (ch != '\"') {
              inquote = !inquote;
            } else if (inquote) {
              str += "\"";
            }
          }
          if (eof == -1) {
            return str;
          }
          if (((ch == ',') || (ch == '\n')) && (!inquote)) {
            if (ch == '\n')
              line = true;
            return str;
          }
        }
      }
      return str;
    } catch (IOException ie) {
      eof = -1;
      return "";
    }

  }

  /**
   * CSVファイルを指定した行から読み取ります。 <br />
   *
   * @param fname
   * @param i
   */
  public boolean setStartLine(String fname, int i) {
    if (!init(fname)) {
      return false;
    }
    int line_count = 0;
    while (eof != -1) {
      if (line_count >= i)
        break;

      while (eof != -1) {
        nextToken();
        if (eof == -1)
          break;
        if (line)
          break;
      }
      if (eof == -1)
        break;
      line_count++;
    }
    return true;
  }

}
