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

import java.util.Calendar;
import java.util.Date;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.common.ALCsvTokenizer;
import com.aimluck.eip.services.storage.ALStorageService;

/**
 * スケジュールのCSV読取用ユーティリティクラスです。
 * 
 */
public class FileIOScheduleCsvUtils {

  /** <code>logger</code> loger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOScheduleCsvUtils.class.getName());

  /** <code>SCHEDULEMAP_TYPE_USER</code> ユーザ */
  public static final String SCHEDULEMAP_TYPE_USER = "U";

  /** アカウントの添付ファイルを一時保管するディレクトリの指定 */
  public static final String FOLDER_TMP_FOR_ATTACHMENT_FILES =
    JetspeedResources.getString("aipo.tmp.account.attachment.directory", "");

  /** CSVファイルを一時保管するファイル名の指定 */
  public static final String FOLDER_TMP_FOR_USERINFO_CSV_FILENAME =
    "schedule_info.csv";

  /** エラーリスト用CSVファイルを一時保管するファイル名の指定 */
  public static final String FOLDER_TMP_FOR_USERINFO_CSV_TEMP_FILENAME =
    "schedule_info_error.csv";

  /** CSVファイルを一時保管するディレクトリの指定 */
  public static final String CSV_SCHEDULE_TEMP_FOLDER = "account_schedule";

  public static final String DEFAULT_TIME_FORMAT = "HH:mm";

  /**
   * 一時ファイルの保存先フォルダを取得
   * 
   * @param index
   * @return
   */
  public static String getScheduleCsvFolderName(String index) {
    return ALStorageService.getDocumentPath(
      ALCsvTokenizer.CSV_TEMP_FOLDER,
      FileIOScheduleCsvUtils.CSV_SCHEDULE_TEMP_FOLDER
        + ALStorageService.separator()
        + index);
  }

  public static int compareToDate(Date date1, Date date2) {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    cal1.setTime(date1);
    cal2.setTime(date2);

    int date1Year = cal1.get(Calendar.YEAR);
    int date1Month = cal1.get(Calendar.MONTH) + 1;
    int date1Day = cal1.get(Calendar.DATE);
    int date2Year = cal2.get(Calendar.YEAR);
    int date2Month = cal2.get(Calendar.MONTH) + 1;
    int date2Day = cal2.get(Calendar.DATE);

    if (date1Year == date2Year
      && date1Month == date2Month
      && date1Day == date2Day) {
      return 0;
    }
    if (cal1.after(cal2)) {
      return 2;
    } else {
      return 1;
    }
  }

  public static boolean checkDateAcross(ALDateTimeField start_date,
      ALDateTimeField start_time, ALDateTimeField end_date,
      ALDateTimeField end_time) {

    boolean result = true;

    if (start_date.toString().equals("") || end_date.toString().equals("")) {
      return true;
    }

    if (start_time.toString().equals("") || end_time.toString().equals("")) {
      return true;
    }

    // in case that start_time = 00:00 end_time = 00:00, Permit
    if (Integer.parseInt(start_time.getHour()) == 0
      && Integer.parseInt(start_time.getMinute()) == 0
      && Integer.parseInt(end_time.getHour()) == 0
      && Integer.parseInt(end_time.getMinute()) == 0) {
      return true;
    }

    if (FileIOScheduleCsvUtils.compareToDate(start_date.getValue(), end_date
      .getValue()) != 0) {
      result = false;
    }

    return result;
  }

  public static boolean checkDateAcross(ALDateTimeField startDateTime,
      ALDateTimeField endDateTime) {

    String _sdate =
      startDateTime.getYear()
        + "/"
        + startDateTime.getMonth()
        + "/"
        + startDateTime.getDay();
    String _stime =
      startDateTime.getYear()
        + "/"
        + startDateTime.getMonth()
        + "/"
        + startDateTime.getDay()
        + " "
        + startDateTime.getHour()
        + ":"
        + startDateTime.getMinute();
    String _edate =
      endDateTime.getYear()
        + "/"
        + endDateTime.getMonth()
        + "/"
        + endDateTime.getDay();
    String _etime =
      endDateTime.getYear()
        + "/"
        + endDateTime.getMonth()
        + "/"
        + endDateTime.getDay()
        + " "
        + endDateTime.getHour()
        + ":"
        + endDateTime.getMinute();

    ALDateTimeField startDate = new ALDateTimeField();
    startDate.setValue(_sdate);
    ALDateTimeField startTime =
      new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_TIME_FORMAT);
    startTime.setValue(_stime);
    ALDateTimeField endDate = new ALDateTimeField();
    endDate.setValue(_edate);
    ALDateTimeField endTime =
      new ALDateTimeField(ALDateTimeField.DEFAULT_DATE_TIME_FORMAT);
    endTime.setValue(_etime);

    return checkDateAcross(startDate, startTime, endDate, endTime);
  }

  public static boolean isSpan(ALDateTimeField startDateTime,
      ALDateTimeField endDateTime) {
    try {
      if (Integer.parseInt(startDateTime.getHour()) == 0
        && Integer.parseInt(startDateTime.getMinute()) == 0
        && Integer.parseInt(endDateTime.getHour()) == 0
        && Integer.parseInt(endDateTime.getMinute()) == 0) {
        return true;
      }
    } catch (NumberFormatException e) {
    }
    return false;
  }

}
