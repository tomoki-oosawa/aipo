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

package com.aimluck.eip.holiday.util;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMHoliday;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipHolidaysManager;
import com.aimluck.eip.common.ALHoliday;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Holidayのユーティリティクラスです。 <BR>
 * 
 */
public class HolidayUtils {
  public static final int YEAR_START = 2004;

  /** デフォルトの祝日が書かれたファイルを持つフォルダへのパス */
  private final static String DIRECTORY_HOLIDAYS_DEFAULT = (JetspeedResources
    .getString("aipo.home", "").equals("")) ? "" : JetspeedResources.getString(
    "aipo.home",
    "")
    + File.separator
    + "conf"
    + File.separator
    + "holidays";

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(HolidayUtils.class.getName());

  /**
   * Holiday オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundatas
   * @param context
   * @param isJoin
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static EipMHoliday getEipMHoliday(RunData rundata, Context context,
      boolean isJoin) throws ALPageNotFoundException {
    String holidayid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (holidayid == null || Integer.valueOf(holidayid) == null) {
        // Holiday IDが空の場合
        logger.debug("[Holiday] Empty ID...");
        return null;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(
          EipMHoliday.HOLIDAY_ID_PK_COLUMN,
          holidayid);

      List<EipMHoliday> holidayList =
        Database.query(EipMHoliday.class, exp).fetchList();

      if (holidayList == null || holidayList.size() == 0) {
        // 指定したHoliday IDのレコードが見つからない場合
        logger.debug("[Holiday] Not found ID...");
        return null;
      }

      // アクセス権の判定
      EipMHoliday holiday = holidayList.get(0);
      return holiday;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ALEipHolidaysManagerインスタンスに保持されている <br>
   * ユーザー定義の祝日を更新します。
   */
  public static void updateHolidays() {
    ALEipHolidaysManager manager = ALEipHolidaysManager.getInstance();
    manager.loadHolidays();
  }

  /**
   * 指定年の祝日情報をテキストファイルから読み込む。<br>
   * 対象ファイルが無ければnullを返す
   * 
   * @param year
   */
  public static List<ALHoliday> loadDefaultHolidays(int year) {
    ALEipHolidaysManager manager = ALEipHolidaysManager.getInstance();
    String filePath =
      DIRECTORY_HOLIDAYS_DEFAULT
        + File.separator
        + "holidays_default_"
        + year
        + ".properties";
    return manager.loadDefaultHolidays(filePath);
  }

  /**
   * 
   * @param date
   * @return
   */
  public static boolean isEmptyDate(Date date) {
    if (date == null) {
      return false;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR) == 9999;
  }

  /**
   * 現在の日時を格納したDate型データを返す。
   * 
   * @return
   */
  public static Date getCurrentDate() {
    Calendar cal = Calendar.getInstance();
    return cal.getTime();
  }

  /**
   * 引数で指定した年の1月1日を<br>
   * 格納したDate型データを返す。
   * 
   * @param year
   * @return
   */
  public static Date getJan1st(int year) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, 1 - 1, 1, 0, 0, 0);
    return calendar.getTime();
  }

  /**
   * 引数で指定した年の12月31日を<br>
   * 格納したDate型データを返す。
   * 
   * @param year
   * @return
   */
  public static Date getDec31st(int year) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, 12 - 1, 31, 23, 59, 59);
    return calendar.getTime();
  }

}
