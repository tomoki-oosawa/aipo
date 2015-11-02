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
package com.aimluck.eip.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.eip.services.storage.ALStorageService;

/**
 * CSVファイルをセルごとに読み取るためのクラスです。 <br />
 * 
 */
public class ALCsvTokenizer {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALCsvTokenizer.class.getName());

  public static final String CSV_TEMP_FOLDER = JetspeedResources.getString(
    "aipo.tmp.directory",
    "")
    + ALStorageService.separator()
    + "csv";

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
      InputStream is = ALStorageService.getFile(fname);
      in = new InputStreamReader(is, "Shift_JIS");
      return true;
    } catch (IOException ie) {
      eof = -1;
      logger.error("ALCsvTokenizer.init", ie);
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
    line = false;
    inquote = false;
    if (eof == -1) {
      return "";
    }

    try {
      StringBuffer buf = new StringBuffer();
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
        return buf.toString();
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
        return buf.toString();
      } else {
        while (eof != -1) {
          if (ch == '\n' || ch == '\r' && (inquote)) {
            buf.append("\r\n");
          } else if (ch != '\"') {
            buf.append((char) ch);
          }
          eof = (ch = in.read());
          if (ch == '\r' && (!inquote)) {
            line = true;
            eof = (ch = in.read());
            if (ch != '\n' && eof != -1) {
              flag_reminder_csv = true;
              newline_r_reminder = ch;
              return buf.toString();
            }
          }
          if (ch == '\"') {
            eof = (ch = in.read());
            if (ch != '\"') {
              inquote = !inquote;
            } else if (inquote) {
              buf.append("\"");
            }
          }
          if (eof == -1) {
            return buf.toString();
          }
          if (((ch == ',') || (ch == '\n')) && (!inquote)) {
            if (ch == '\n') {
              line = true;
            }
            return buf.toString();
          }
        }
      }
      return buf.toString();
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
      if (line_count >= i) {
        break;
      }

      while (eof != -1) {
        nextToken();
        if (eof == -1) {
          break;
        }
        if (line) {
          break;
        }
      }
      if (eof == -1) {
        break;
      }
      line_count++;
    }
    return true;
  }

}
