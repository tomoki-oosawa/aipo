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

package com.aimluck.eip.schedule.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMUserHoliday;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.ScheduleHolidayResultData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 設備のユーティリティクラスです。 <BR>
 * 
 */
public class ScheduleHolidayUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleHolidayUtils.class.getName());

  public static final String SCHEDULE_HOLIDAY_PORTLET_NAME =
    "Schedule_Holidays";

  /**
   * 設備オブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMUserHoliday getEipMUserHoliday(RunData rundata,
      Context context) {
    String holidayid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (holidayid == null || Integer.valueOf(holidayid) == null) {
        // Facilities IDが空の場合
        logger.debug("[User_Holiday] Empty ID...");
        return null;
      }

      EipMUserHoliday userholiday =
        Database.get(EipMUserHoliday.class, holidayid);

      return userholiday;
    } catch (Exception ex) {
      logger.error("userholidays", ex);
      return null;
    }
  }

  /**
   * @param rundata
   * @param context
   * @return
   */
  public static List<ScheduleHolidayResultData> loadHolidayList(
      RunData rundata, Context context) {
    try {
      List<ScheduleHolidayResultData> holidayList =
        new ArrayList<ScheduleHolidayResultData>();
      SelectQuery<EipMUserHoliday> query =
        Database.query(EipMUserHoliday.class);
      query.orderAscending(EipMUserHoliday.HOLIDAY_TITLE_PROPERTY);
      List<EipMUserHoliday> aList = query.fetchList();

      for (EipMUserHoliday record : aList) {
        ScheduleHolidayResultData rd = new ScheduleHolidayResultData();
        rd.initField();
        rd.setHolidayId(record.getUserHolidayId().longValue());
        rd.setHolidayTitle(record.getHolidayTitle());
        rd.setHolidayDate(record.getHolidayDate());
        holidayList.add(rd);
      }

      return holidayList;
    } catch (Exception ex) {
      logger.error("scheduleholiday", ex);
      return null;
    }
  }

}
