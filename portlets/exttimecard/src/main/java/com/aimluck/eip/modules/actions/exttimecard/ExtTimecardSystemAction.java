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
package com.aimluck.eip.modules.actions.exttimecard;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.exttimecard.ExtTimecardSystemMapSelectData;
import com.aimluck.eip.exttimecard.ExtTimecardSystemSelectData;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカード集計のアクションクラスです。 <BR>
 * 
 */
public class ExtTimecardSystemAction extends ALBaseAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardSystemAction.class.getName());

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
    if (getMode() == null) {
      doExt_timecard_system_user_list(rundata, context);
    }
  }

  /**
   * 勤務形態を一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doExt_timecard_system_list(RunData rundata, Context context)
      throws Exception {
    /*
     * this.setMode("list"); ExtTimecardSystemSelectData listData = new
     * ExtTimecardSystemSelectData(); listData.initField();
     * listData.setRowsNum(Integer.parseInt(ALEipUtils .getPortlet(rundata,
     * context).getPortletConfig().getInitParameter( "p1a-rows")));
     * listData.setStrLength(Integer.parseInt(ALEipUtils.getPortlet(rundata,
     * context).getPortletConfig().getInitParameter("p3a-strlen")));
     * listData.doViewList(this, rundata, context);
     */
    ExtTimecardSystemSelectData listData = new ExtTimecardSystemSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "exttimecardsystem-list");
  }

  /**
   * ユーザーを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doExt_timecard_system_user_list(RunData rundata, Context context)
      throws Exception {
    ExtTimecardSystemMapSelectData listData =
      new ExtTimecardSystemMapSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "exttimecardsystem-user-list");
  }

}
