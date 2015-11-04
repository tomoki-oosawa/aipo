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
package com.aimluck.eip.gpdb;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTGpdbItem;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Webデータベース項目定義情報の検索データを管理するクラスです。
 *
 */
public class GpdbItemSelectData extends
    ALAbstractSelectData<EipTGpdbItem, EipTGpdbItem> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbItemSelectData.class.getName());

  /** Webデータベースの総数 */
  private int gpdbSum;

  /** 全Webデータベースの一覧 */
  private List<GpdbResultData> gpdbAllList;

  /** WebデータベースID */
  private String gpdbId;

  /** フィルタ値のキー */
  private static final String RECORD_LIST_FILTER_STR = new StringBuffer()
    .append(GpdbRecordSelectData.class.getSimpleName())
    .append(ALEipConstants.LIST_FILTER)
    .toString();

  /**
   * 初期設定
   *
   * @param action
   *          ALAction
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils
        .setTemp(rundata, context, LIST_SORT_STR, GpdbUtils.SORT_STRING);
    }

    gpdbAllList = GpdbUtils.getGpdbAllList(); // 全Webデータベース

    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return ResultList
   */
  @Override
  protected ResultList<EipTGpdbItem> selectList(RunData rundata, Context context) {
    try {
      SelectQuery<EipTGpdbItem> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      query.orderAscending(EipTGpdbItem.ORDER_NO_PROPERTY);

      ResultList<EipTGpdbItem> list = query.getResultList();
      // 件数をセットする．
      gpdbSum = list.getTotalCount();

      return list;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return SelectQuery
   */
  private SelectQuery<EipTGpdbItem> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTGpdbItem> query = Database.query(EipTGpdbItem.class);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 詳細データを取得します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return 項目定義オブジェクト
   */
  @Override
  protected EipTGpdbItem selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return GpdbUtils.getEipTGpdbItem(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ）
   *
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultData(EipTGpdbItem record) {
    return GpdbUtils.getGpdbItemResultData(record);
  }

  /**
   * ResultDataを取得します。（詳細データ）
   *
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultDataDetail(EipTGpdbItem record) {
    return GpdbUtils.getGpdbItemResultData(record);
  }

  /**
   * 項目情報を取得する
   *
   * @return 項目情報
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("gpdb_id", EipTGpdbItem.GPDB_PROPERTY);
    return map;
  }

  /**
   * Webデータベースの総数を取得する
   *
   * @return Webデータベース総数
   */
  public int getGpdbSum() {
    return gpdbSum;
  }

  /**
   * Webデータベースの全リストを返す
   *
   * @return Webデータベースの全リスト
   */
  public List<GpdbResultData> getGpdbAllList() {
    return gpdbAllList;
  }

  /**
   * 項目定義の入力項目が出力対象かを判定する
   *
   * @param field
   *          入力項目フィールド名
   * @param type
   *          入力形式
   * @return 出力する：TRUE 出力しない：FALSE
   */
  public boolean displayField(String field, String type) {
    return GpdbUtils.dipslayField(field, type);
  }

  /**
   * WebデータベースIDの設定
   *
   */

  public void setGpdbId(Context context, RunData rundata) {
    String gpdbId = rundata.getParameters().getString("filter", "");
    String reset =
      rundata.getParameters().getString("reset_keyword_params", "");
    if ("true".equals(reset)) {
      ALEipUtils.removeTemp(rundata, context, LIST_FILTER_STR);
    }
    String sesGpdbId =
      ALEipUtils.getTemp(rundata, context, RECORD_LIST_FILTER_STR);
    String itemGpdbId = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    if ("".equals(gpdbId)) {
      gpdbId = null == itemGpdbId ? "" : itemGpdbId;
    }
    if ("".equals(gpdbId)) {
      gpdbId = null == sesGpdbId ? "" : sesGpdbId;
    }
    String filterType = rundata.getParameters().getString("filtertype", "");

    List<GpdbResultData> gpdbAllList = GpdbUtils.getGpdbAllList();
    if ((gpdbId != null && !"".equals(gpdbId))
      && ("gpdb_id".equals(filterType) || filterType == null || ""
        .equals(filterType))) { // 最大化画面でデータベース選択した場合と詳細画面
      for (GpdbResultData gpdb : gpdbAllList) {
        if (gpdb.getGpdbId().toString().equals(gpdbId)) {
          this.gpdbId = gpdbId;
        }
      }
    }

    if (this.gpdbId == null) {
      if (gpdbAllList.size() > 0) {// 初期設定されていない場合リストの一番目を表示
        this.gpdbId = gpdbAllList.get(0).gpdb_id.toString();
      }
    }

    ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, this.gpdbId);
    ALEipUtils.setTemp(rundata, context, LIST_FILTER_TYPE_STR, "gpdb_id");
  }
}
