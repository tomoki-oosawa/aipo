/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jp.sf.orangesignal.csv.Csv;
import jp.sf.orangesignal.csv.CsvConfig;
import jp.sf.orangesignal.csv.handlers.ResultSetHandler;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * エクスポート用ユーティリティクラスです。
 * 
 */
public class FileIOExportUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOExportUtils.class.getName());

  private static final String ZIP_FILE_FOLDER = JetspeedResources.getString(
    "aipo.filedir",
    "");

  public static final String FOLDER_TMP_FOR_ATTACHMENT_FILES =
    JetspeedResources.getString("aipo.tmp.fileupload.attachment.directory", "");

  /** ZIPファイルを保管するディレクトリのカテゴリキーの指定 */
  protected static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.zipfile.categorykey",
    "");

  /** ZIPファイルを一時保管するファイル名の指定 */
  public static final String ZIP_FILE_TEMP_FILENAME = "file.zip";

  /** ZIPファイルを一時保管するディレクトリの指定 */
  public static final String ZIP_FILE_TEMP_FOLDER = "data";

  /**
   * 保存された全ファイルをZip化して保存します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  public static boolean zipAllFile(RunData rundata, Context context,
      List<String> msgList) {
    try {
      int uid = ALEipUtils.getUserId(rundata);
      String folderName = getZipFileFolderName(String.valueOf(uid));

      List<File> files = getAllFileList(new File(ZIP_FILE_FOLDER));
      files.addAll(makeCsvData(rundata, context, msgList));

      File zipFile =
        new File(folderName
          + ALStorageService.separator()
          + ZIP_FILE_TEMP_FILENAME);
      if (!zipFile.exists()) {
        try {
          String parent = zipFile.getParent();
          if (parent != null) {
            File dir = new File(parent);
            if (!dir.exists()) {
              dir.mkdirs();
            }
          }
          zipFile.createNewFile();
        } catch (Exception e) {
          logger.error("Can't create file...:" + zipFile);
        }
      }

      ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
      try {
        encode(zos, files);
      } finally {
        zos.close();
      }

      // ファイルの移動
      ALStorageService.copyTmpFile(
        uid,
        ZIP_FILE_TEMP_FOLDER,
        ZIP_FILE_TEMP_FILENAME,
        ZIP_FILE_FOLDER,
        CATEGORY_KEY + ALStorageService.separator() + uid,
        "0_" + String.valueOf(System.nanoTime()));

      // 一時ファイル保存先のフォルダを削除
      new File(folderName).delete();

      return true;
    } catch (FileNotFoundException fe) {
      logger.error("zip", fe);
      return false;
    } catch (IOException ie) {
      logger.error("zip", ie);
      return false;
    } catch (Exception e) {
      logger.error("zip", e);
      return false;
    }
  }

  /**
   * 保存された全データをCsv化して保存します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  private static List<File> makeCsvData(RunData rundata, Context context,
      List<String> msgList) {
    List<File> res = new ArrayList<File>();
    InputStream resourceAsStream =
      rundata.getServletContext().getResourceAsStream(
        "WEB-INF/datasource/dbcp-org001.properties");
    Connection conn = null;

    File tmpDir =
      new File(JetspeedResources.getString("aipo.tmp.directory", "")
        + ALStorageService.separator()
        + "csv");

    if (!tmpDir.isDirectory()) {
      tmpDir.mkdirs();
    }

    try {
      Properties dbcpProp = new Properties();
      dbcpProp.load(resourceAsStream);
      String driverClassName =
        dbcpProp.get("cayenne.dbcp.driverClassName").toString();
      String url = dbcpProp.get("cayenne.dbcp.url").toString();
      String username = dbcpProp.get("cayenne.dbcp.username").toString();
      String password = dbcpProp.get("cayenne.dbcp.password").toString();
      Class.forName(driverClassName);
      conn = DriverManager.getConnection(url, username, password);

      DatabaseMetaData meta = conn.getMetaData();
      ResultSet tables =
        meta.getTables(null, null, null, new String[] { "TABLE" });
      CsvConfig csvConfig = new CsvConfig();
      csvConfig.setQuote('"');
      csvConfig.setQuoteDisabled(false);
      csvConfig.setEscape('"');
      csvConfig.setEscapeDisabled(false);
      ResultSetHandler resultSetHandler = new ResultSetHandler();
      while (tables.next()) {
        String tableName = tables.getString("table_name");
        PreparedStatement statement =
          conn.prepareStatement("SELECT * FROM " + tableName + ";");
        ResultSet resultSet = statement.executeQuery();
        File file = new File(tmpDir, tableName + ".csv");
        Csv.save(resultSet, file, csvConfig, resultSetHandler);
        res.add(file);
        resultSet.close();
        statement.close();
      }
      return res;
    } catch (Exception e) {
      logger.error("[ERROR]" + e);
      return new ArrayList<File>();
    }
  }

  private static void encode(ZipOutputStream zos, List<File> files)
      throws Exception {
    byte[] buf = new byte[1024];
    for (File f : files) {
      ZipEntry ze = new ZipEntry(f.getPath().replace('\\', '/'));
      zos.putNextEntry(ze);
      InputStream is = new BufferedInputStream(new FileInputStream(f));
      for (;;) {
        int len = is.read(buf);
        if (len < 0) {
          break;
        }
        zos.write(buf, 0, len);
      }
      is.close();
    }
  }

  private static List<File> getAllFileList(File dir) {
    List<File> res = new ArrayList<File>();
    File[] files = dir.listFiles();
    if (files != null) {
      for (File file : files) {
        if (!file.exists()) {
          continue;
        } else if (file.isDirectory()) {
          res.addAll(getAllFileList(file));
        } else if (file.isFile()) {
          res.add(file);
        }
      }
    }
    return res;
  }

  /**
   * 一時ファイルの保存先フォルダを取得
   * 
   * @param index
   * @return
   */
  private static String getZipFileFolderName(String index) {
    return ALStorageService.getDocumentPath(
      FOLDER_TMP_FOR_ATTACHMENT_FILES,
      index + ALStorageService.separator() + ZIP_FILE_TEMP_FOLDER);
  }

}
