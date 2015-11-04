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
package com.aimluck.eip.modules.actions.timecard;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.timecard.TimecardSelectData;
import com.aimluck.eip.timecard.TimecardSummaryListSelectData;
import com.aimluck.eip.timecard.util.TimecardUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードのアクションクラスです。
 * 
 */
public class TimecardAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TimecardAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    // セッション情報をクリアする
    clearTimecardSession(rundata, context);

    ALEipUtils.setTemp(rundata, context, TimecardUtils.TARGET_USER_ID, String
      .valueOf(ALEipUtils.getUserId(rundata)));
    TimecardSelectData detailData = new TimecardSelectData();
    detailData.initField();
    detailData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "timecard");
  }

  /**
   * 最大化表示の際の処理を記述します。
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_LIST.equals(mode)) {
        doTimecard_list(rundata, context);
      }
      if (getMode() == null) {
        doTimecard_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("timecard", ex);
    }

  }

  /**
   * タイムカードを一覧表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTimecard_list(RunData rundata, Context context)
      throws Exception {
    TimecardSelectData listData = new TimecardSelectData();
    listData.initField();
    listData.setRowsNum(200);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "timecard-list");
  }

  /**
   * 勤務時間集計を表示します。
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTimecard_summary_list(RunData rundata, Context context)
      throws Exception {
    TimecardSummaryListSelectData listData =
      new TimecardSummaryListSelectData();
    listData.initField();
    listData.setRowsNum(200);
    listData.doViewList(this, rundata, context);
    listData.calc();

    setTemplate(rundata, "timecard-summary-list");
  }

  private void clearTimecardSession(RunData rundata, Context context) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;

    jdata.getUser().removeTemp("nulltarget_user_id");
    jdata.getUser().removeTemp("nulltarget_group_name");

  }

}
