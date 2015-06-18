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
package com.aimluck.eip.modules.actions.cabinet;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cabinet.CabinetSelectData;
import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダの取り扱いに関するアクションクラスです。 <br />
 *
 */
public class CabinetAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetAction.class.getName());

  static final String LIST_FILTER_STR = new StringBuffer()
    .append(CabinetSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_FILTER)
    .toString();

  static final String LIST_FILTER_TYPE_STR = new StringBuffer().append(
    CabinetSelectData.class.getSimpleName()).append(
    ALEipConstants.LIST_FILTER_TYPE).toString();

  static final String LIST_SORT_STR = new StringBuffer()
    .append(CabinetSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_SORT)
    .toString();

  static final String LIST_SORT_TYPE_STR = new StringBuffer().append(
    CabinetSelectData.class.getSimpleName()).append(
    ALEipConstants.LIST_SORT_TYPE).toString();

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
    clearCabinetSession(rundata, context);

    // デフォルトソート設定の初期化

    ALEipUtils.removeTemp(rundata, context, LIST_SORT_STR);
    ALEipUtils.removeTemp(rundata, context, LIST_SORT_TYPE_STR);

    CabinetSelectData listData = new CabinetSelectData();
    listData.setIsNormalContext(true);
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setTableColumNum(Integer.parseInt(ALEipUtils.getPortlet(
      rundata,
      context).getPortletConfig().getInitParameter("p1e-rows")));
    if (listData.doViewList(this, rundata, context)) {
      setTemplate(rundata, "cabinet");
    }
  }

  /**
   * 最大化表示の際の処理を記述する． <BR>
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
    // デフォルトソート設定の初期化
    ALEipUtils.removeTemp(rundata, context, LIST_SORT_STR);
    ALEipUtils.removeTemp(rundata, context, LIST_SORT_TYPE_STR);
    try {
      if (ALEipConstants.MODE_LIST.equals(mode)) {
        doCabinet_list(rundata, context);
      }

      if (getMode() == null) {
        doCabinet_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("cabinet", ex);
    }

  }

  /**
   * キャビネットの一覧を表示する． <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doCabinet_list(RunData rundata, Context context) throws Exception {
    CabinetSelectData listData = new CabinetSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "cabinet-list");
  }

  private void clearCabinetSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("folder_id");
    list.add("CabinetFileWord");
    list.add("com.aimluck.eip.cabinet.CabinetFolderSelectDatasort");
    list.add("com.aimluck.eip.cabinet.CabinetFileWordSelectDatasort");
    list.add(CabinetUtils.TARGET_KEYWORD);
    ALEipUtils.removeTemp(rundata, context, list);
  }

}
