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

import com.aimluck.eip.cayenne.om.portlet.EipMGpdbKubun;
import com.aimluck.eip.cayenne.om.portlet.EipMGpdbKubunValue;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Webデータベース区分値の検索データを管理するクラスです。 <BR>
 * 
 */
public class GpdbKubunSelectData extends
    ALAbstractSelectData<EipMGpdbKubunValue, EipMGpdbKubunValue> implements
    ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbKubunSelectData.class.getName());

  /** 区分の総数 */
  private int gpdbKubunSum;

  /** 全区分の一覧 */
  private List<GpdbKubunResultData> allGpdbKubun;

  /** 全区分値の一覧（サイズ取得用） */
  private List<GpdbKubunValueResultData> allGpdbKubunValue;

  /** 全Webデータベースの一覧 */
  private List<GpdbResultData> gpdbAllList;

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

    allGpdbKubun = GpdbUtils.getGpdbKubunAllList();
    allGpdbKubunValue = GpdbUtils.getGpdbKubunValueAllList();

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
  protected ResultList<EipMGpdbKubunValue> selectList(RunData rundata,
      Context context) {
    try {

      SelectQuery<EipMGpdbKubunValue> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      query.orderAscending(EipMGpdbKubunValue.GPDB_KUBUN_PROPERTY);
      query.orderAscending(EipMGpdbKubunValue.ORDER_NO_PROPERTY);
      query.orderAscending(EipMGpdbKubunValue.GPDB_KUBUN_VALUE_PROPERTY);

      ResultList<EipMGpdbKubunValue> list = query.getResultList();

      // 件数をセットする．
      gpdbKubunSum = list.getTotalCount();

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
  private SelectQuery<EipMGpdbKubunValue> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMGpdbKubunValue> query =
      Database.query(EipMGpdbKubunValue.class);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 検索条件を再設定します。
   * 
   * @param query
   *          SelectQuery
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return SelectQuery
   */
  @Override
  protected SelectQuery<EipMGpdbKubunValue> buildSelectQueryForFilter(
      SelectQuery<EipMGpdbKubunValue> query, RunData rundata, Context context) {

    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    String filter_type =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_TYPE_STR);

    if ("gpdb_kubun_id".equals(filter_type)) {
      if (filter == null
        || "".equals(filter)
        || GpdbUtils.getEipMGpdbKubun(Integer.valueOf(filter)) == null) {
        // 区分のデータがない場合は検索値を削除する
        ALEipUtils.removeTemp(rundata, context, LIST_FILTER_STR);
      }
    }

    return super.buildSelectQueryForFilter(query, rundata, context);
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
  protected EipMGpdbKubunValue selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return GpdbUtils.getEipMGpdbKubunValue(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ）
   * 
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultData(EipMGpdbKubunValue record) {
    return GpdbUtils.getGpdbKubunValueResultData(record);
  }

  /**
   * ResultDataを取得します。（詳細データ）
   * 
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultDataDetail(EipMGpdbKubunValue record) {
    return GpdbUtils.getGpdbKubunValueResultData(record);
  }

  /**
   * 項目情報を取得する
   * 
   * @return 項目情報
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("gpdb_kubun_id", EipMGpdbKubun.GPDB_KUBUN_ID_PK_COLUMN);
    map.putValue("gpdb_kubun_name", EipMGpdbKubun.GPDB_KUBUN_NAME_PROPERTY);
    return map;
  }

  /**
   * 区分値の総数を取得する
   * 
   * @return 区分値総数
   */
  public int getGpdbKubunValueSum() {
    return gpdbKubunSum;
  }

  /**
   * 右上メニューのビュータイプを取得する
   * 
   * @return ビュータイプ
   */
  public String getViewtype() {
    return "gpdb_kubun";
  }

  /**
   * 区分の全リストを返す
   * 
   * @return 区分の全リスト
   */
  public List<GpdbKubunResultData> getAllGpdbKubun() {
    return allGpdbKubun;
  }

  /**
   * 区分値の全件数を返す
   * 
   * @return 区分値の全件数
   */
  public int getAllGpdbKubunValueSum() {
    return allGpdbKubunValue.size();
  }

  /**
   * Webデータベースの全リストを返す
   * 
   * @return Webデータベースの全リスト
   */
  public List<GpdbResultData> getGpdbAllList() {
    return gpdbAllList;
  }
}
