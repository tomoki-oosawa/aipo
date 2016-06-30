/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
package com.aimluck.eip.modules.screens;

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.exttimecard.ExtTimecardListResultData;
import com.aimluck.eip.exttimecard.ExtTimecardListResultDataContainer;
import com.aimluck.eip.exttimecard.ExtTimecardSelectData;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードの一覧を処理するクラスです。 <br />
 *
 */
public class ExtTimecardListScreen extends ExtTimecardScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardListScreen.class.getName());

  /**
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    try {
      ExtTimecardSelectData listData = new ExtTimecardSelectData();
      listData.initField();
      listData.setRowsNum(100);
      listData.doViewList(this, rundata, context);

      ExtTimecardListResultDataContainer container =
        ExtTimecardUtils.groupByWeek(listData.getQueryStartDate(), listData
          .getAllList(), null);
      container.calculateWeekOvertime();

      ExtTimecardListResultData tclistrd = null;
      List<ExtTimecardListResultData> daykeys = listData.getDateListKeys();
      int daykeysize = daykeys.size();
      for (int i = 0; i < daykeysize; i++) {
        tclistrd = daykeys.get(i);
        tclistrd.setWeekOvertime(container.getWeekOvertime(tclistrd));
        tclistrd.setStatutoryHoliday(container.isStatutoryOffDay(tclistrd));
        tclistrd.calculateWeekOvertime();
      }

      String layout_template =
        ExtTimecardUtils.isNewRule()
          ? "portlets/html/ajax-exttimecard-new-list.vm"
          : "portlets/html/ajax-exttimecard-list.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[ExtTimecardListScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }
}
