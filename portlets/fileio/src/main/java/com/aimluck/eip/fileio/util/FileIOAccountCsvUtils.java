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
package com.aimluck.eip.fileio.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーアカウントのCSV読取用ユーティリティクラスです。
 * 
 */
public class FileIOAccountCsvUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAccountCsvUtils.class.getName());

  /** CSVファイルを一時保管するファイル名の指定 */
  public static final String CSV_ACCOUNT_TEMP_FILENAME = "account.csv";

  /** エラーリスト用CSVファイルを一時保管するファイル名の指定 */
  public static final String CSV_ACCOUNT_TEMP_ERROR_FILENAME =
    "account_post_err.csv";

  /** CSVファイルを一時保管するファイル名の指定 */
  public static final String CSV_ACCOUNT_POST_TEMP_FILENAME =
    "account_post.csv";

  /** エラーリスト用CSVファイルを一時保管するファイル名の指定 */
  public static final String CSV_ACCOUNT_POST_TEMP_ERROR_FILENAME =
    "account_post_err.csv";

  /** CSVファイルの列数(ユーザー登録時のみ使用) */
  public static final int CSV_FILE_COL_COUNT = 13;

  /** CSVファイルを一時保管するディレクトリの指定 */
  public static final String CSV_ACCOUNT_TEMP_FOLDER = "user_info";

  /** CSVファイルを一時保管するディレクトリの指定 */
  public static final String CSV_ACCOUNT_POST_TEMP_FOLDER = "account_post";

  /**
   * CSVファイルを字句毎のリストに変換
   * 
   * @param line
   * @return
   */
  public static String[] getCsvSplitStrings(String line) {
    if (line == null || line.equals("")) {
      return null;
    }

    try {
      List<String> list = new ArrayList<String>();
      int count_comma = 0;
      char c;
      StringBuffer token = new StringBuffer("");
      int len = line.length();
      for (int i = 0; i < len; i++) {
        c = line.charAt(i);
        if (c != ',' && i == len - 1) {
          token.append(c);
          list.add(token.toString());
        } else if (c == ',') {
          list.add(token.toString());
          token = new StringBuffer("");
          count_comma++;
          continue;
        } else {
          token.append(c);
        }
        if (count_comma > CSV_FILE_COL_COUNT) {
          break;
        }
      }

      if (line.endsWith(",")) {
        list.add("");
      }

      String[] strings = new String[list.size()];
      strings = list.toArray(strings);
      return strings;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 会社名から会社IDを取得 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMCompany getEipMCompany(RunData rundata, Context context) {
    EipMCompany result = null;
    String id = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (id == null || Integer.valueOf(id) == null) {
        logger.debug("Empty ID...");
        return result;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipMCompany.COMPANY_ID_PK_COLUMN, Integer
          .valueOf(id));
      SelectQuery<EipMCompany> query = Database.query(EipMCompany.class, exp);
      List<EipMCompany> list = query.fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return result;
      }
      result = list.get(0);
    } catch (Exception ex) {
      logger.error("fileio", ex);
    }
    return result;
  }

  /**
   * 部署名から部署IDを取得 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMPost getEipMPost(RunData rundata, Context context) {
    EipMPost result = null;
    String id = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (id == null || Integer.valueOf(id) == null) {
        logger.debug("Empty ID...");
        return result;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipMPost.POST_ID_PK_COLUMN, Integer
          .valueOf(id));
      SelectQuery<EipMPost> query = Database.query(EipMPost.class, exp);
      List<EipMPost> list = query.fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return result;
      }
      result = list.get(0);
    } catch (Exception ex) {
      logger.error("fileio", ex);
    }
    return result;
  }

  /**
   * 一時ファイルの保存先フォルダを取得
   * 
   * @param index
   * @return
   */
  public static String getAccountPostCsvFolderName(String index) {
    return ALStorageService.getDocumentPath(
      ALCsvTokenizer.CSV_TEMP_FOLDER,
      FileIOAccountCsvUtils.CSV_ACCOUNT_POST_TEMP_FOLDER
        + ALStorageService.separator()
        + index);
  }

  /**
   * 一時ファイルの保存先フォルダを取得
   * 
   * @param index
   * @return
   */
  public static String getAccountCsvFolderName(String index) {
    return ALStorageService.getDocumentPath(
      ALCsvTokenizer.CSV_TEMP_FOLDER,
      FileIOAccountCsvUtils.CSV_ACCOUNT_TEMP_FOLDER
        + ALStorageService.separator()
        + index);
  }

}
