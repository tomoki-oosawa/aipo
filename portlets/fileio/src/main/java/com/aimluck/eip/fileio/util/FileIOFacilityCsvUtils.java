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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.services.storage.ALStorageService;

/**
 * スケジュールのCSV読取用ユーティリティクラスです。
 * 
 */
public class FileIOFacilityCsvUtils {

  /** <code>logger</code> loger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOFacilityCsvUtils.class.getName());

  /** <code>FACILITYMAP_TYPE_USER</code> ユーザ */
  public static final String FACILITYMAP_TYPE_USER = "U";

  /** アカウントの添付ファイルを一時保管するディレクトリの指定 */
  public static final String FOLDER_TMP_FOR_ATTACHMENT_FILES =
    JetspeedResources.getString("aipo.tmp.account.attachment.directory", "");

  /** CSVファイルを一時保管するファイル名の指定 */
  public static final String FOLDER_TMP_FOR_USERINFO_CSV_FILENAME =
    "facility_info.csv";

  /** エラーリスト用CSVファイルを一時保管するファイル名の指定 */
  public static final String FOLDER_TMP_FOR_USERINFO_CSV_TEMP_FILENAME =
    "facility_info_error.csv";

  /** CSVファイルを一時保管するディレクトリの指定 */
  public static final String CSV_FACILITY_TEMP_FOLDER = "account_facility";

  public static final String DEFAULT_TIME_FORMAT = "HH:mm";

  /**
   * 一時ファイルの保存先フォルダを取得
   * 
   * @param index
   * @return
   */
  public static String getFacilityCsvFolderName(String index) {
    return ALStorageService.getDocumentPath(
      ALCsvTokenizer.CSV_TEMP_FOLDER,
      FileIOFacilityCsvUtils.CSV_FACILITY_TEMP_FOLDER
        + ALStorageService.separator()
        + index);
  }

}
