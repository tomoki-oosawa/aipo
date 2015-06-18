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
package com.aimluck.eip.manhour.util;

import java.util.Calendar;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALEipUtils;

/**
 * 工数管理のユーティリティクラスです。 <br />
 * 
 */
public class ManHourUtils {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ManHourUtils.class.getName());

  public static final String VIEW_DATE_YEAR = "view_date_year";

  public static final String VIEW_DATE_MONTH = "view_date_month";

  public static final String MANHOUR_PORTLET_NAME = "ManHour";

  /**
   * 表示切り替えで指定した月を取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static int getViewDateMonth(RunData rundata, Context context) {
    String view_date_month = null;
    String idParam = rundata.getParameters().getString(VIEW_DATE_MONTH);
    view_date_month = ALEipUtils.getTemp(rundata, context, VIEW_DATE_MONTH);
    if (idParam == null && view_date_month == null) {
      Calendar cal = Calendar.getInstance();
      view_date_month = Integer.toString(cal.get(Calendar.MONTH) + 1);
      ALEipUtils.setTemp(rundata, context, VIEW_DATE_MONTH, view_date_month);
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, VIEW_DATE_MONTH, idParam);
      view_date_month = idParam;
    }
    return Integer.parseInt(view_date_month);
  }

  /**
   * 表示切り替えで指定した年を取得する．
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static int getViewDateYear(RunData rundata, Context context) {
    String view_date_year = null;
    String idParam = rundata.getParameters().getString(VIEW_DATE_YEAR);
    view_date_year = ALEipUtils.getTemp(rundata, context, VIEW_DATE_YEAR);
    if (idParam == null && view_date_year == null) {
      Calendar cal = Calendar.getInstance();
      view_date_year = Integer.toString(cal.get(Calendar.YEAR));
      ALEipUtils.setTemp(rundata, context, VIEW_DATE_YEAR, view_date_year);
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, VIEW_DATE_YEAR, idParam);
      view_date_year = idParam;
    }
    return Integer.parseInt(view_date_year);
  }
}
