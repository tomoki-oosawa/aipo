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
package com.aimluck.eip.modules.actions.memo;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.memo.MemoSelectData;
import com.aimluck.eip.memo.util.MemoUtils;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * メモ帳のアクションクラスです。 <BR>
 * 
 */
public class MemoAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MemoAction.class.getName());

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

    // セッション情報のクリア
    clearMemoSession(rundata, context);

    ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-memos")
      .trim());
    MemoSelectData listData = new MemoSelectData();
    listData.initField();
    listData.loadMemoIdList(rundata, context);
    listData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "memo");
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
      doMemo_list(rundata, context);
    } catch (Exception ex) {
      logger.error("memo", ex);
    }

  }

  /**
   * Memoを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doMemo_list(RunData rundata, Context context) throws Exception {
    MemoSelectData listData = new MemoSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "memo-list");
  }

  private void clearMemoSession(RunData rundata, Context context) {

    JetspeedRunData jdata = (JetspeedRunData) rundata;
    VelocityPortlet portlet = ((VelocityPortlet) context.get("portlet"));
    String peid = portlet.getID();

    // jdata.getUser().removeTemp(peid + "entityid");
    jdata.getUser().removeTemp(
      new StringBuffer(peid)
        .append("com.aimluck.eip.memo.MemoSelectDatasort")
        .toString());

    MemoUtils.resetFilter(rundata, context, this.getClass().getName());
  }
}
