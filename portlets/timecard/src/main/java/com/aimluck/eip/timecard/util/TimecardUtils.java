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
package com.aimluck.eip.timecard.util;

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

import com.aimluck.eip.cayenne.om.portlet.EipTTimecard;
import com.aimluck.eip.cayenne.om.portlet.EipTTimecardSettings;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードのユーティリティクラスです。
 * 
 */
public class TimecardUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimecardUtils.class.getName());

  /** <code>TARGET_GROUP_NAME</code> グループによる表示切り替え用変数の識別子 */
  public static final String TARGET_GROUP_NAME = "target_group_name";

  /** <code>TARGET_USER_ID</code> ユーザによる表示切り替え用変数の識別子 */
  public static final String TARGET_USER_ID = "target_user_id";

  /** 出退勤フラグ（出勤） */
  public static final String WORK_FLG_ON = "1";

  /** 出退勤フラグ（退勤） */
  public static final String WORK_FLG_OFF = "0";

  /** 出退勤フラグ（ダミー） */
  public static final String WORK_FLG_DUMMY = "-1";

  /** タイムカードファイルを一時保管するディレクトリの指定 */
  public static final String FOLDER_TMP_FOR_TIMECARD_FILES = JetspeedResources
    .getString("aipo.tmp.timecard.directory", "");

  public static final String TIMECARD_PORTLET_NAME = "Timecard";

  public static final String TIMECARD_SETTINGS_PORTLET_NAME =
    "TimecardSettings";

  /**
   * Todo オブジェクトモデルを取得します。
   * 
   * @param rundata
   * @param context
   * @param isJoin
   *          カテゴリテーブルをJOINするかどうか
   * @return
   */
  public static EipTTimecard getEipTTimecard(RunData rundata, Context context) {
    String timecardid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (timecardid == null || Integer.valueOf(timecardid) == null) {
        // Todo IDが空の場合
        logger.debug("[Timecard] Empty ID...");
        return null;
      }

      SelectQuery<EipTTimecard> query = Database.query(EipTTimecard.class);
      Expression exp11 =
        ExpressionFactory.matchDbExp(
          EipTTimecard.TIMECARD_ID_PK_COLUMN,
          timecardid);
      query.setQualifier(exp11);
      Expression exp21 =
        ExpressionFactory.matchExp(EipTTimecard.USER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query.andQualifier(exp21);

      List<EipTTimecard> timecards = query.fetchList();
      if (timecards == null || timecards.size() == 0) {
        // 指定したTimecard IDのレコードが見つからない場合
        logger.debug("[Timecard] Not found ID...");
        return null;
      }
      return timecards.get(0);
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return null;
    }
  }

  /**
   * 
   * @return
   */
  public static EipTTimecardSettings getEipTTimecardSettings(RunData rundata,
      Context context) {
    String settingid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (settingid == null || Integer.valueOf(settingid) == null) {
        // Setting IDが空の場合
        logger.debug("[TimecardUtils] Empty ID...");
        return null;
      }

      SelectQuery<EipTTimecardSettings> query =
        Database.query(EipTTimecardSettings.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTTimecardSettings.TIMECARD_SETTINGS_ID_PK_COLUMN,
          settingid);
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTTimecardSettings.USER_ID_PROPERTY,
          Integer.valueOf(1));

      query.setQualifier(exp1.andExp(exp2));

      List<EipTTimecardSettings> slist = query.fetchList();
      if (slist == null || slist.size() == 0) {
        // 指定したSetting IDのレコードが見つからない場合
        logger.debug("[TimecardUtils] Not found ID...");
        return null;
      }

      return slist.get(0);
    } catch (Exception ex) {
      logger.error("timecard", ex);
      return null;
    }
  }

  /**
   * 指定した2つの日付を比較する．
   * 
   * @param date1
   * @param date2
   * @param checkTime
   *          時間まで比較する場合，true．
   * @return 等しい場合，0. date1>date2の場合, 1. date1 <date2の場合, 2.
   */
  public static boolean sameDay(Date date1, Date date2) {
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
      return true;
    }
    return false;
  }

}
