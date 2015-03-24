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
package com.aimluck.eip.modules.actions.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.activity.ActivityAllSelectData;
import com.aimluck.eip.activity.util.ActivityUtils;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ActivityAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ActivityAction.class.getName());

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    // セッション情報のクリア
    clearActivitySession(rundata, context);
    ActivityUtils.resetFilter(rundata, context, ActivityAllSelectData.class
      .getName());

    ActivityAllSelectData listData = new ActivityAllSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));

    listData.setTableColumNum(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p2e-rows")));
    listData.setFiltersFromPSML(portlet, context, rundata);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "activity");
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

    try {
      doActivity_list(rundata, context);
    } catch (Exception ex) {
      logger.error("ActivityAction.buildMaximizedContext", ex);
    }

  }

  public void doActivity_list(RunData rundata, Context context)
      throws Exception {
    ActivityAllSelectData listData = new ActivityAllSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "activity-list");
  }

  private void clearActivitySession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    // エンティティIDの初期化
    list.add("entityid");
    // 選択しているタブ情報の削除
    list.add("tab");
    // 選択しているタブ情報の削除2
    list.add("category");

    list.add("com.aimluck.eip.activity.ActivityAllSelectDatafiltertype");
    list.add("com.aimluck.eip.activity.ActivityAllSelectDatafilter");
    ALEipUtils.removeTemp(rundata, context, list);
  }

}
