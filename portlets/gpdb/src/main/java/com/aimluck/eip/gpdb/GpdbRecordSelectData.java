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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdb;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbItem;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbRecord;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SQLTemplate;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Webデータベースレコードの検索データを管理するクラスです。 <BR>
 *
 */
public class GpdbRecordSelectData extends
    ALAbstractSelectData<EipTGpdbRecord, EipTGpdbRecord> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbRecordSelectData.class.getName());

  /** 項目定義よりフィールド一覧を保持する */
  private List<GpdbItemResultData> gpdbItemList;

  /** WebデータベースID */
  private String gpdbId;

  /** Webデータベース */
  private GpdbResultData gpdb;

  /** Webデータベース全リスト */
  private List<GpdbResultData> gpdbAllList;

  /** Webデータベースレコードマップ。キー：項目定義ID */
  private Map<String, GpdbRecordResultData> mapGpdbRecord;

  /** 区分値マップ。キー：区分値ID */
  private Map<String, List<GpdbKubunValueResultData>> mapGpdbKubunValue;

  /** ターゲット　 */
  private ALStringField searchWord;

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
    if (sort == null || "".equals(sort.trim())) {
      ALEipUtils
        .setTemp(rundata, context, LIST_SORT_STR, GpdbUtils.SORT_STRING);
    }
    searchWord = new ALStringField();
    super.init(action, rundata, context);

    // super.init()後にLIST_FILTER_STRに格納される
    // gpdbId = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
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
  protected ResultList<EipTGpdbRecord> selectList(RunData rundata,
      Context context) {
    try {

      // 全Webデータベース
      gpdbAllList = GpdbUtils.getGpdbAllList();
      // セッション情報を設定
      setCurrentFilterAndSort(rundata, context);
      if (gpdbAllList == null || gpdbAllList.size() == 0) {
        // データベース未登録時
        return null;
      }

      // -----------------------
      // 項目定義を取得
      // -----------------------
      gpdbItemList = GpdbUtils.getGpdbItemResultList(gpdbId);

      // dbデータを取得
      {
        EipTGpdb etgpdb = GpdbUtils.getEipTGpdb(gpdbId);
        if (etgpdb != null) {
          gpdb = GpdbUtils.getGpdbResultData(etgpdb);
        }
      }

      // -----------------------
      // ソート項目の設定
      // -----------------------

      Integer sort;
      try {
        sort = Integer.valueOf(current_sort); // ソート項目クリックで項目定義IDが選択される
      } catch (Exception e) {
        sort = null;
      }

      if (sort == null) {
        // デフォルトソート項目を取得
        for (GpdbItemResultData item : gpdbItemList) {
          if (GpdbUtils.FLG_ON.equals(item.getDefaultSortFlg().getValue())) {
            sort = (int) item.getGpdbItemId().getValue();
            current_sort_type = item.getAscDesc().getValue();
          }
        }
      }

      String sortWhere;
      String sortValue;
      String nullSort = "";
      if (sort == null) {

        // ソータなしの場合（初期表示時）
        // 新しく更新された順で並べる
        sortWhere =
          " AND r2.gpdb_item_id = ("
            + "       SELECT r3.gpdb_item_id"
            + "         FROM eip_t_gpdb_record r3"
            + "        WHERE r3.record_no = r2.record_no"
            + "        ORDER BY update_date DESC"
            + "        LIMIT 1"
            + "     )";
        sortValue = "r2.update_date";

      } else {
        // ソータが選択された場合
        sortWhere = " AND r2.gpdb_item_id = #bind($sort) ";

        EipTGpdbItem item = GpdbUtils.getEipTGpdbItem("" + sort);
        if (item != null && GpdbUtils.ITEM_TYPE_SEQ.equals(item.getType())) {
          // 自動採番項目の場合は数値として扱う
          sortValue = Database.castToIntRawColumn("r2.value");
          nullSort = " IS NULL ";
        } else if (item != null
          && (GpdbUtils.ITEM_TYPE_CREATE_DATE.equals(item.getType()) || GpdbUtils.ITEM_TYPE_UPDATE_DATE
            .equals(item.getType()))) {
          // 作成日時、更新日時の場合はタイムスタンプとして扱う
          sortValue = Database.castToDateTimeRawColumn("r2.value");
          nullSort = " IS NULL ";
        } else if (item != null
          && GpdbUtils.ITEM_TYPE_DATE.equals(item.getType())) {
          // 日付の場合は日付として扱う
          sortValue = Database.castToDateRawColumn("r2.value");
          nullSort = " IS NULL ";
        } else {
          sortValue = "r2.value";
          nullSort = " = '' ";
        }
      }
      if (GpdbUtils.hasResetKeywordFlag(rundata, context)) {
        GpdbUtils.resetKeyword(rundata, context, this.getClass().getName());
      }
      searchWord.setValue(GpdbUtils.getSearchword(rundata, context));
      String searchValue;
      if (!searchWord.getValue().equals("")) {
        searchValue = "   AND r4.value LIKE #bind($keyword)";
      } else {
        searchValue = "";
      }
      String ascDesc;
      if (current_sort_type == null) {
        ascDesc = "DESC";
      } else {
        ascDesc = current_sort_type;
      }

      // -----------------------
      // レコード情報を取得
      // -----------------------
      StringBuilder sql = new StringBuilder();
      if (searchValue != "") {

        sql
          .append("SELECT r.* FROM eip_t_gpdb_record r")
          .append(" INNER JOIN eip_t_gpdb_item i")
          .append("    ON i.gpdb_item_id = r.gpdb_item_id")
          .append(" INNER JOIN eip_t_gpdb g")
          .append("    ON g.gpdb_id = r.gpdb_id")
          .append(" WHERE r.gpdb_id = #bind($gpdb_id)")
          .append("   AND i.list_flg = #bind($list_flg)")
          .append("   AND r.record_no IN (")
          .append(" SELECT DISTINCT r4.record_no FROM eip_t_gpdb_record r4")
          .append(" INNER JOIN eip_t_gpdb_item i2")
          .append("    ON i2.gpdb_item_id = r4.gpdb_item_id")
          .append(" WHERE r4.gpdb_id = #bind($gpdb_id)")
          .append(searchValue)
          .append(
            "    AND i2.type IN (#bind($type1),#bind($type2),#bind($type3),#bind($type4))")
          .append(" ) ")
          .append(" ORDER BY (SELECT " + sortValue)
          .append("             FROM eip_t_gpdb_record r2")
          .append("            WHERE r2.record_no = r.record_no")
          .append(sortWhere)
          .append("          ) " + nullSort + ascDesc)
          .append("        , (SELECT " + sortValue)
          .append("             FROM eip_t_gpdb_record r2")
          .append("            WHERE r2.record_no = r.record_no")
          .append(sortWhere)
          .append("          ) " + ascDesc)
          .append("        , r.record_no")
          .append("        , i.order_no");

      } else {

        sql
          .append("SELECT r.* FROM eip_t_gpdb_record r")
          .append(" INNER JOIN eip_t_gpdb_item i")
          .append("    ON i.gpdb_item_id = r.gpdb_item_id")
          .append(" INNER JOIN eip_t_gpdb g")
          .append("    ON g.gpdb_id = r.gpdb_id")
          .append(" WHERE r.gpdb_id = #bind($gpdb_id)")
          .append("   AND i.list_flg = #bind($list_flg)")
          .append(" ORDER BY (SELECT " + sortValue)
          .append("             FROM eip_t_gpdb_record r2")
          .append("            WHERE r2.record_no = r.record_no")
          .append(sortWhere)
          .append("          ) " + nullSort + ascDesc)
          .append("        , (SELECT " + sortValue)
          .append("             FROM eip_t_gpdb_record r2")
          .append("            WHERE r2.record_no = r.record_no")
          .append(sortWhere)
          .append("          ) " + ascDesc)
          .append("        , r.record_no")
          .append("        , i.order_no");
      }

      SQLTemplate<EipTGpdbRecord> sqltemp =
        Database.sql(EipTGpdbRecord.class, String.valueOf(sql));
      sqltemp.param("gpdb_id", Integer.valueOf(gpdbId));
      sqltemp.param("list_flg", GpdbUtils.FLG_ON);
      if (sort != null) {
        sqltemp.param("sort", sort);
        sqltemp.param("sort", sort);
      }
      if (searchValue != "") {
        sqltemp.param("keyword", "%" + searchWord + "%");
        sqltemp.param("type1", GpdbUtils.ITEM_TYPE_TEXTAREA);
        sqltemp.param("type2", GpdbUtils.ITEM_TYPE_TEXT);
        sqltemp.param("type3", GpdbUtils.ITEM_TYPE_LINK);
        sqltemp.param("type4", GpdbUtils.ITEM_TYPE_MAIL);
      }

      ResultList<EipTGpdbRecord> list =
        new ResultList<EipTGpdbRecord>(sqltemp.fetchList());

      // ページャ情報反映
      return new ResultList<EipTGpdbRecord>(buildPaginatedList(list));
    } catch (RuntimeException ex) {
      logger.error("RuntimeException", ex);
      return null;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * フィルタ項目、ソート項目を保持する
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  private void setCurrentFilterAndSort(RunData rundata, Context context) {
    // buildSelectQueryForFilterを呼ばないため自前で設定
    current_filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    current_filter_type =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_TYPE_STR);

    // buildSelectQueryForListViewSortを呼ばないため自前で設定
    current_sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    current_sort_type =
      ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
  }

  /**
   * ページング結果のリストを取得します。
   *
   * @param records
   *          検索結果
   */
  @Override
  protected List<EipTGpdbRecord> buildPaginatedList(List<EipTGpdbRecord> records) {
    List<EipTGpdbRecord> list = new ArrayList<EipTGpdbRecord>();

    // 一覧画面の表示項目数を割り出す
    int countDisp = 0;
    for (GpdbItemResultData item : gpdbItemList) {
      if (GpdbUtils.FLG_ON.equals(item.getListFlg().getValue())) {
        countDisp++;
      }
    }
    int size = records.size(); // 全レコード

    // 開始レコードを設定（表示上のレコード数を引数にする）
    setPageParam(size / countDisp);

    int start = getStart() * countDisp; // ページ開始レコード
    int rowNum = getRowsNum() * countDisp; // ページ内表示レコード数

    // 表示する最終レコードを割り出す
    int end = (start + rowNum <= size) ? start + rowNum : size;

    for (int i = start; i < end; i++) {
      list.add(records.get(i));
    }

    return list;
  }

  /**
   * 詳細データを取得します。
   *
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @return 項目定義オブジェクト
   * @throws ALPageNotFoundException
   */
  @Override
  protected EipTGpdbRecord selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {
    // セッション情報を設定
    setCurrentFilterAndSort(rundata, context);

    // 区分値を取得
    mapGpdbKubunValue = GpdbUtils.getKubunMap();

    // レコード情報を取得
    mapGpdbRecord =
      GpdbUtils.getGpdbRecordMap(gpdbId, ALEipUtils.getTemp(
        rundata,
        context,
        ALEipConstants.ENTITY_ID));

    // オブジェクトモデルを取得(ダミーデータ)
    // 実際はgetResultDataで取得
    return new EipTGpdbRecord();
  }

  /**
   * ResultDataを取得します。（一覧データ）
   *
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected Object getResultData(EipTGpdbRecord record) {
    return GpdbUtils.getGpdbRecordResultData(record);
  }

  /**
   * ResultDataを取得します。（詳細データ）
   *
   * @param record
   *          レコード
   * @return ResultData
   */
  @Override
  protected List<GpdbItemResultData> getResultDataDetail(EipTGpdbRecord record) {
    List<GpdbItemResultData> list = GpdbUtils.getGpdbItemResultList(gpdbId);

    // 添付ファイルを設定する
    for (GpdbItemResultData item : list) {

      String type = item.getType().getValue();
      if (!GpdbUtils.ITEM_TYPE_FILE.equals(type)
        && !GpdbUtils.ITEM_TYPE_IMAGE.equals(type)) {
        continue;
      }

      GpdbRecordResultData data =
        mapGpdbRecord.get("" + item.getGpdbItemId().getValue());

      data.setAttachmentFiles(GpdbUtils.getAttachmentFiles((int) data
        .getGpdbRecordId()
        .getValue()));
    }

    return list;
  }

  /**
   * 項目情報を取得する
   *
   * @return 項目情報
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("gpdb_id", EipTGpdb.GPDB_ID_PK_COLUMN);
    return map;
  }

  /**
   * WebデータベースIDを取得する
   *
   * @return WebデータベースID
   */
  public String getGpdbId() {
    return gpdbId;
  }

  /**
   * WebデータベースIDの設定
   *
   */

  public void setGpdbId(Context context, RunData rundata) {
    String gpdbId = rundata.getParameters().getString("filter", "");
    String sesGpdbId = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    if ("".equals(gpdbId)) {
      gpdbId = null == sesGpdbId ? "" : sesGpdbId;
    }
    String filterType = rundata.getParameters().getString("filtertype", "");

    List<GpdbResultData> gpdbAllList = GpdbUtils.getGpdbAllList();
    if (!"".equals(gpdbId)
      && ("gpdb_id".equals(filterType) || filterType == null || ""
        .equals(filterType))) { // 最大化画面でデータベース選択した場合と詳細画面
      if (validateGpdbId(gpdbAllList, gpdbId)) {
        this.gpdbId = gpdbId;
      }
    }

    if (this.gpdbId == null) {
      VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
      String database =
        portlet.getPortletConfig().getInitParameter("p1d-database", "");
      if (!database.isEmpty() && StringUtils.isNumeric(database)) {
        if (validateGpdbId(gpdbAllList, database)) {
          this.gpdbId = database;
        }
      }

      if (this.gpdbId == null && gpdbAllList.size() > 0) {// 初期設定されていない場合リストの一番目を表示
        this.gpdbId = gpdbAllList.get(0).gpdb_id.toString();
      }
    }

    GpdbUtils.passPSML(rundata, context, "p1d-database", this.gpdbId);
    ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, this.gpdbId);
  }

  /**
   * Webデータベース全リストを取得する
   *
   * @return Webデータベース全リスト
   */
  public List<GpdbResultData> getGpdbAllList() {
    return gpdbAllList;
  }

  /**
   * 項目定義リストを取得します。
   *
   * @return 項目定義リスト
   */
  public List<GpdbItemResultData> getGpdbItemList() {
    return gpdbItemList;
  }

  /**
   * 項目定義リストを取得します。
   *
   * @return 項目定義リスト
   */
  public GpdbResultData getGpdb() {
    return gpdb;
  }

  /**
   * 指定した項目定義IDのレコード情報を取得します。
   *
   * @param gpdbItemId
   *          項目定義ID
   * @return レコード情報
   */
  public GpdbRecordResultData getRecordResult(String gpdbItemId) {
    return mapGpdbRecord.get(gpdbItemId);
  }

  /**
   * 指定区分の区分値リストを取得します。
   *
   * @param kubunId
   *          区分マスタID
   * @return 区分値リスト
   */
  public List<GpdbKubunValueResultData> getKubunValueList(String kubunId) {
    return mapGpdbKubunValue.get(kubunId);
  }

  private boolean validateGpdbId(List<GpdbResultData> allList, String gpdbId) {
    for (GpdbResultData gpdb : allList) {
      if (gpdb.getGpdbId().toString().equals(gpdbId)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return target_keyword
   */
  public ALStringField getSearchWord() {
    return searchWord;
  }
}
