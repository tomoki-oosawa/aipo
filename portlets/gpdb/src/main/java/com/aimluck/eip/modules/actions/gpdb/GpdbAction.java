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
package com.aimluck.eip.modules.actions.gpdb;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.gpdb.GpdbItemSelectData;
import com.aimluck.eip.gpdb.GpdbRecordSelectData;
import com.aimluck.eip.gpdb.GpdbSelectData;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Webデータベースの管理者用アクションクラスです。
 *
 */
public class GpdbAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbAction.class.getName());

  /** ソート値のキー */
  private static final String LIST_SORT_STR = new StringBuffer()
    .append(GpdbRecordSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_SORT)
    .toString();

  /** ソート項目のキー */
  private static final String LIST_SORT_TYPE_STR = new StringBuffer().append(
    GpdbRecordSelectData.class.getSimpleName()).append(
    ALEipConstants.LIST_SORT_TYPE).toString();

  /** フィルタ値のキー */
  private static final String LIST_FILTER_STR = new StringBuffer().append(
    GpdbRecordSelectData.class.getSimpleName()).append(
    ALEipConstants.LIST_FILTER).toString();

  /** フィルタ項目のキー */
  private static final String LIST_FILTER_TYPE_STR = new StringBuffer().append(
    GpdbRecordSelectData.class.getSimpleName()).append(
    ALEipConstants.LIST_FILTER_TYPE).toString();

  /** ソート値のキー */
  private static final String ITEM_LIST_SORT_STR = new StringBuffer()
    .append(GpdbItemSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_SORT)
    .toString();

  /** ソート項目のキー */
  private static final String ITEM_LIST_SORT_TYPE_STR = new StringBuffer()
    .append(GpdbItemSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_SORT_TYPE)
    .toString();

  /**
   * 通常表示の際の処理を記述します。
   *
   * @param portlet
   *          VelocityPortlet
   * @param context
   *          Context
   * @param rundata
   *          RunData
   * @throws Exception
   *           例外
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    // セッション情報のクリア
    clearSession(rundata, context);
    if (ALEipConstants.MODE_LIST.equals(getMode())) {
      doGpdb_list(rundata, context);
    } else if (getMode() == null) {
      doGpdb_list(rundata, context);
    }
  }

  /**
   * Webデータベースを一覧表示します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  public void doGpdb_list(RunData rundata, Context context) throws Exception {
    GpdbRecordSelectData listData = new GpdbRecordSelectData();
    listData.initField();
    listData.setGpdbId(context, rundata);
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "gpdb-top");
  }

  /**
   * 最大化表示の際の処理を記述します。
   *
   * @param portlet
   *          VelocityPortlet
   * @param context
   *          Context
   * @param rundata
   *          RunData
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    try {
      if (getMode() == null) {
        String admintab = rundata.getParameters().getString("admintab");
        if (!StringUtils.isEmpty(admintab)) {
          doGpdb_all_list(rundata, context);
        } else {
          doGpdbRecord_list(rundata, context);
        }
      }
    } catch (Exception e) {
      logger.error("GpdbAction.buildMaximizedContext", e);
    }
  }

  /**
   * @param rundata
   * @param context
   */
  public void doGpdb_all_list(RunData rundata, Context context)
      throws Exception {

    GpdbSelectData listData = new GpdbSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1c-rows")));
    listData.doViewList(this, rundata, context);

    setTemplate(rundata, "gpdb");

  }

  /**
   * Webデータベースレコードを一覧表示します。 <BR>
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  public void doGpdbRecord_list(RunData rundata, Context context)
      throws Exception {
    GpdbRecordSelectData listData = new GpdbRecordSelectData();
    listData.initField();
    listData.setGpdbId(context, rundata);
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "gpdb-record");
  }

  /**
   * セッション情報をクリアする
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  private void clearSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add(LIST_SORT_STR);
    list.add(LIST_SORT_TYPE_STR);
    list.add(ITEM_LIST_SORT_STR);
    list.add(ITEM_LIST_SORT_TYPE_STR);
    // list.add(LIST_FILTER_STR);
    // list.add(LIST_FILTER_TYPE_STR);
    ALEipUtils.removeTemp(rundata, context, list);
  }

}
