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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.aimluck.eip.fileio.beans.ExportZipBean;
import com.aimluck.eip.orm.Database;
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

  private static final String HOME_FOLDER = JetspeedResources.getString(
    "aipo.home",
    "");

  private static final String FILE_FOLDER = JetspeedResources.getString(
    "aipo.filedir",
    "");

  private static final String TEMP_FOLDER_BASE = JetspeedResources.getString(
    "aipo.tmp.fileupload.attachment.directory",
    "");

  /** ZIPファイルを保管するディレクトリのカテゴリキーの指定 */
  private static final String ZIP_CATEGORY_KEY = JetspeedResources.getString(
    "aipo.zipfile.categorykey",
    "");

  /** CSVを保管するディレクトリのカテゴリキーの指定 */
  private static final String CSV_CATEGORY_KEY = JetspeedResources.getString(
    "aipo.csv.categorykey",
    "");

  /** サムネイルを保管するディレクトリのカテゴリキーの指定 */
  private static final String THUMBNAIL_CATEGORY_KEY = JetspeedResources
    .getString("aipo.thumbnail.categorykey", "");

  /** ZIPファイルを一時保管するファイル名の指定 */
  private static final String ZIP_FILE_TEMP_FILENAME = "file.zip";

  /** ZIP作成中フラグファイルを一時保管するファイル名の指定 */
  private static final String ZIP_FILE_CREATING_FILENAME = "creating";

  /** ZIPファイルを一時保管するディレクトリの指定 */
  private static final String ZIP_FILE_TEMP_FOLDER = "data";

  /** CSVファイルを一時保管するディレクトリの指定 */
  private static final String CSV_FILE_TEMP_FOLDER = "csv";

  /**
   * テーブル名とカラム名のMAP (name, [key, thumbnail_column1, thumbnail_column2 ...])
   * */
  private static final Map<String, List<String>> TABLE_THUMBNAIL_COLUMN_MAP =
    new HashMap<String, List<String>>() {
      private static final long serialVersionUID = -6186944992193875524L;
      {
        put("eip_m_mail_account", Arrays.asList(
          "account_id",
          "pop3password",
          "auth_send_user_passwd"));
        put("eip_t_blog_file", Arrays.asList("file_id", "file_thumbnail"));
        put("eip_t_msgboard_file", Arrays.asList("file_id", "file_thumbnail"));
        put("eip_t_timeline_url", Arrays.asList("url_id", "thumbnail"));
        put("jetspeed_group_profile", Arrays.asList("psml_id", "profile"));
        put("jetspeed_role_profile", Arrays.asList("psml_id", "profile"));
        put("jetspeed_user_profile", Arrays.asList("psml_id", "profile"));
        put("turbine_user", Arrays.asList(
          "user_id",
          "photo",
          "photo_smartphone"));
      }
    };

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
    int uid = ALEipUtils.getUserId(rundata);
    String folderName = getTempFolderName(uid, ZIP_FILE_TEMP_FOLDER);

    List<File> files = getAllFileList(new File(FILE_FOLDER));
    makeCsvData(rundata, context, msgList);
    files.addAll(getAllFileList(new File(getFolderName(CSV_CATEGORY_KEY))));
    files
      .addAll(getAllFileList(new File(getFolderName(THUMBNAIL_CATEGORY_KEY))));

    File creating =
      new File(getFolderName(ZIP_CATEGORY_KEY), ZIP_FILE_CREATING_FILENAME);
    createNewFile(creating);
    File zipFile = new File(folderName, ZIP_FILE_TEMP_FILENAME);
    createNewFile(zipFile);

    try {
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
        HOME_FOLDER,
        ZIP_CATEGORY_KEY,
        "0_" + String.valueOf(System.nanoTime()));

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
    } finally {
      // 一時ファイル保存先のフォルダを削除
      new File(folderName).delete();
      creating.delete();
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
  private static boolean makeCsvData(RunData rundata, Context context,
      List<String> msgList) {
    InputStream resourceAsStream =
      rundata.getServletContext().getResourceAsStream(
        "WEB-INF/datasource/dbcp-org001.properties");
    Connection conn = null;

    int uid = ALEipUtils.getUserId(rundata);
    File tmpDir = new File(getTempFolderName(uid, CSV_FILE_TEMP_FOLDER));

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
        saveThumbnail(tableName, resultSet);
        resultSet = statement.executeQuery();
        String fileName = tableName + ".csv";
        Csv.save(
          resultSet,
          new File(tmpDir, fileName),
          csvConfig,
          resultSetHandler);
        resultSet.close();
        statement.close();

        // ファイルの移動
        ALStorageService.copyTmpFile(
          uid,
          CSV_FILE_TEMP_FOLDER,
          fileName,
          HOME_FOLDER,
          CSV_CATEGORY_KEY,
          fileName);
      }
      return true;
    } catch (Exception e) {
      logger.error("[ERROR]" + e);
      return false;
    } finally {
      tmpDir.delete();
    }
  }

  public static List<ExportZipBean> getExportZipList() {
    List<ExportZipBean> res = new ArrayList<ExportZipBean>();
    List<File> zips = getAllFileList(new File(getFolderName(ZIP_CATEGORY_KEY)));
    for (File zip : zips) {
      ExportZipBean bean = new ExportZipBean();
      bean.setName(zip.getName());
      res.add(bean);
    }
    return res;
  }

  private static void saveThumbnail(String tableName, ResultSet resultSet) {
    if (TABLE_THUMBNAIL_COLUMN_MAP.get(tableName) != null) {
      List<String> columns = TABLE_THUMBNAIL_COLUMN_MAP.get(tableName);
      String key = columns.get(0);
      List<String> thumbnails = columns.subList(1, columns.size());

      try {
        while (resultSet.next()) {
          for (String thumbnail : thumbnails) {
            File file =
              new File(getFolderName(THUMBNAIL_CATEGORY_KEY
                + ALStorageService.separator()
                + tableName
                + ALStorageService.separator()
                + thumbnail), String.valueOf(resultSet.getInt(key)));
            createNewFile(file);
            FileOutputStream fos = new FileOutputStream(file);
            try {
              fos.write(resultSet.getBytes(thumbnail));
            } catch (IOException e) {
              logger.error("[ERROR]" + e);
            } finally {
              try {
                fos.close();
              } catch (IOException e) {
                logger.error("[ERROR]" + e);
              }
            }
          }
        }
      } catch (FileNotFoundException e) {
        logger.error("[ERROR]" + e);
      } catch (SQLException e) {
        logger.error("[ERROR]" + e);
      }
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

  private static boolean createNewFile(File file) {
    if (!file.exists()) {
      try {
        String parent = file.getParent();
        if (parent != null) {
          File dir = new File(parent);
          if (!dir.exists()) {
            dir.mkdirs();
          }
        }
        file.createNewFile();
      } catch (Exception e) {
        logger.error("Can't create file...:" + file);
        return false;
      }
    }
    return true;
  }

  /**
   * 一時ファイルの保存先フォルダを取得
   * 
   * @param uid
   * @param folder
   * @return
   */
  private static String getTempFolderName(int uid, String folder) {
    return ALStorageService.getDocumentPath(TEMP_FOLDER_BASE, uid
      + ALStorageService.separator()
      + folder);
  }

  /**
   * ファイルの保存先フォルダを取得
   * 
   * @param index
   * @return
   */
  private static String getFolderName(String category) {
    return HOME_FOLDER
      + ALStorageService.separator()
      + Database.getDomainName()
      + ALStorageService.separator()
      + category;
  }

}
