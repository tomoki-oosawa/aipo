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
package com.aimluck.eip.gpdb;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTGpdb;
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
 * Webデータベース検索データを管理するクラスです。 <BR>
 * 
 */
public class GpdbSelectData extends ALAbstractSelectData<EipTGpdb, EipTGpdb>
    implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbSelectData.class.getName());

  /** Webデータベースの総数 */
  private int gpdbSum;

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

    gpdbAllList = GpdbUtils.getGpdbAllList(); // 全Webデータベース

    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return ResultList
   */
  @Override
  protected ResultList<EipTGpdb> selectList(RunData rundata, Context context) {
    try {
      SelectQuery<EipTGpdb> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTGpdb> list = query.getResultList();
      // 件数をセットする．
      gpdbSum = list.getTotalCount();

      return list;

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return SelectQuery
   */
  private SelectQuery<EipTGpdb> getSelectQuery(RunData rundata, Context context) {
    SelectQuery<EipTGpdb> query = Database.query(EipTGpdb.class);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return Webデータベースオブジェクトモデル
   */
  @Override
  protected EipTGpdb selectDetail(RunData rundata, Context context) {
    // オブジェクトモデルを取得
    return GpdbUtils.getEipTGpdb(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   * 
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultData(EipTGpdb record) {
    return GpdbUtils.getGpdbResultData(record);
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   * 
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultDataDetail(EipTGpdb record) {
    return GpdbUtils.getGpdbResultData(record);
  }

  /**
   * 項目情報を取得する
   * 
   * @return 項目情報
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("gpdb_name", EipTGpdb.GPDB_NAME_PROPERTY);
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
   * 右上メニューのビュータイプを取得する
   * 
   * @return ビュータイプ
   */
  public String getViewtype() {
    return "gpdb";
  }
}
