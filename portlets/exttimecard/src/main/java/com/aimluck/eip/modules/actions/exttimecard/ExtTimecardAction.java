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
package com.aimluck.eip.modules.actions.exttimecard;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.exttimecard.ExtTimecardSelectData;
import com.aimluck.eip.exttimecard.ExtTimecardSummaryListSelectData;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカードのアクションクラスです。 <BR>
 * 
 */
public class ExtTimecardAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    /** セッション情報をクリアする */
    clearExtTimecardSession(rundata, context);

    ALEipUtils.setTemp(
      rundata,
      context,
      ExtTimecardUtils.TARGET_USER_ID,
      String.valueOf(ALEipUtils.getUserId(rundata)));
    ExtTimecardSelectData detailData = new ExtTimecardSelectData();
    detailData.initField();
    detailData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "exttimecard");
  }

  /**
   * 最大化表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    /** MODEを取得 */
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_LIST.equals(mode)) {
        doExtTimecard_list(rundata, context);
      } else if ("summary".equals(mode)) {
        doExtTimecard_summary_list(rundata, context);
      } else {
        doExtTimecard_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
    }
  }

  /**
   * タイムカードを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doExtTimecard_list(RunData rundata, Context context)
      throws Exception {
    if ("only"
      .equals(ALEipUtils.getTemp(rundata, context, "target_group_name"))) {
      ALEipUtils.setTemp(rundata, context, "target_group_name", "all");
    }
    ExtTimecardSelectData listData = new ExtTimecardSelectData();
    listData.initField();
    listData.setRowsNum(100);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "exttimecard-list");
  }

  /**
   * タイムカードを月毎に集計表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doExtTimecard_summary_list(RunData rundata, Context context)
      throws Exception {
    ExtTimecardSummaryListSelectData listData =
      new ExtTimecardSummaryListSelectData();
    listData.initField();
    listData.setRowsNum(100);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "exttimecard-summary-list");
  }

  @SuppressWarnings("unused")
  private void doViewPage(RunData rundata, Context context, String vm_template) {
    String def_searchengine =
      ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p1a-selects", "");
    if (!"".equals(def_searchengine)) {
      context.put("def_searchengine", def_searchengine);
    }
    setTemplate(rundata, vm_template);
  }

  private void clearExtTimecardSession(RunData rundata, Context context) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;

    jdata.getUser().removeTemp("nulltarget_user_id");
    jdata.getUser().removeTemp("nulltarget_group_name");

  }
}
