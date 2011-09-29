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

package com.aimluck.eip.holiday;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMHoliday;
import com.aimluck.eip.common.ALHoliday;
import com.aimluck.eip.holiday.util.HolidayUtils;
import com.aimluck.eip.orm.Database;

/**
 * Holidayをデフォルトに戻すためのクラスです。 <BR>
 * 
 */
public class HolidayDefaultFormat {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(HolidayDefaultFormat.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  public void action(RunData rundata, Context context, int year) {
    // 指定年の祝日をテキストファイルから読み込む
    List<ALHoliday> defaultHolidays = HolidayUtils.loadDefaultHolidays(year);
    // 指定年の祝日情報をデータベースから削除する
    deleteHolidays(year);
    // 読み込んだ祝日情報をデータベースに登録する
    insertHolidays(defaultHolidays, "T");
    // 祝日情報を更新する
    HolidayUtils.updateHolidays();
  }

  /**
   * 指定年の祝日情報をデータベースから削除する。
   * 
   * @param year
   * @return
   */
  private boolean deleteHolidays(int year) {
    try {
      Expression exp1 =
        ExpressionFactory.greaterOrEqualExp(
          EipMHoliday.HOLIDAY_DATE_PROPERTY,
          HolidayUtils.getJan1st(year));
      Expression exp2 =
        ExpressionFactory.lessOrEqualExp(
          EipMHoliday.HOLIDAY_DATE_PROPERTY,
          HolidayUtils.getDec31st(year));

      List<EipMHoliday> holidayList =
        Database.query(EipMHoliday.class, exp1.andExp(exp2)).fetchList();
      if (holidayList == null || holidayList.size() == 0) {
        return false;
      }

      for (EipMHoliday holiday : holidayList) {
        // Holidayを削除
        Database.delete(holiday);
        Database.commit();
      }
    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }

  /**
   * 与えられた祝日情報をデータベースに登録する。
   * 
   * @param holidayList
   *          登録する祝日情報
   * @param defaultFlag
   *          デフォルトの祝日("T") ユーザ定義の祝日("F")
   */
  private boolean insertHolidays(List<ALHoliday> holidayList, String defaultFlag) {
    try {
      for (ALHoliday h : holidayList) {
        // 新規オブジェクトモデル
        EipMHoliday holiday = Database.create(EipMHoliday.class);
        holiday.setHolidayDate(h.getDay().getValue().getDate());
        holiday.setHolidayName(h.getName().getValue());
        holiday.setDefaultFlag(defaultFlag);

        // Holidayを登録
        Database.commit();
      }
    } catch (Throwable t) {
      Database.rollback();
      logger.error(t);
      return false;
    }
    return true;
  }
}
