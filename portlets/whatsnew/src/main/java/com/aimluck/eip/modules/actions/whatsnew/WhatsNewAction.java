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
package com.aimluck.eip.modules.actions.whatsnew;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.whatsnew.WhatsNewSelectData;

/**
 * 新着情報のアクションクラスです。 <BR>
 * 
 */
public class WhatsNewAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WhatsNewAction.class.getName());

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

    // 選択しているタブ情報の削除
    ALEipUtils.removeTemp(rundata, context, "tab");

    WhatsNewSelectData listData = new WhatsNewSelectData();
    listData.initField();
    listData.setViewSpan(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-span")));
    listData.setViewNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p2a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "whatsnew");
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

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doWhatsnew_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doWhatsnew_list(rundata, context);
      }
      if (getMode() == null) {
        doWhatsnew_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("whatsnew", ex);
    }

  }

  /**
   * WhatsNewを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWhatsnew_list(RunData rundata, Context context)
      throws Exception {
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    WhatsNewSelectData listData = new WhatsNewSelectData();
    listData.initField();
    listData.setViewSpan(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-span")));
    listData.setViewNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p2a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "whatsnew-list");
  }

  /**
   * WhatsNewを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWhatsnew_detail(RunData rundata, Context context)
      throws Exception {
    WhatsNewSelectData detailData = new WhatsNewSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "whatsnew-detail");
    } else {
      doWhatsnew_list(rundata, context);
    }
  }

}
