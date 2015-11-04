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
package com.aimluck.eip.memo;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTMemo;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.memo.util.MemoUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * メモ帳の検索データを管理するクラスです。 <BR>
 * 
 */
public class MemoSelectData extends ALAbstractSelectData<EipTMemo, EipTMemo>
    implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MemoSelectData.class.getName());

  /** Memo の総数 */
  private int memoSum;

  /** メモ一覧 */
  private List<MemoLiteResultData> memoLiteList;

  /** 検索用 */
  private ALStringField target_keyword;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p2a-sort"));
    }

    target_keyword = new ALStringField();
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadMemoIdList(RunData rundata, Context context) {
    try {
      // メモ一覧
      memoLiteList = new ArrayList<MemoLiteResultData>();

      SelectQuery<EipTMemo> query = getSelectQuery(rundata, context);
      List<EipTMemo> list = query.fetchList();

      for (EipTMemo model : list) {
        MemoLiteResultData rd = new MemoLiteResultData();
        rd.initField();
        rd.setMemoId(model.getMemoId());
        rd.setMemoName(model.getMemoName());
        memoLiteList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("memo", ex);
    }
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTMemo> selectList(RunData rundata, Context context) {
    try {
      if (MemoUtils.hasResetFlag(rundata, context)) {
        MemoUtils.resetFilter(rundata, context, this.getClass().getName());
        target_keyword.setValue("");
      } else {
        target_keyword.setValue(MemoUtils.getTargetKeyword(rundata, context));
      }

      SelectQuery<EipTMemo> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTMemo> list = query.getResultList();
      // Memo の総数をセットする．
      memoSum = list.getTotalCount();

      return list;
    } catch (Exception ex) {
      logger.error("memo", ex);
      return null;
    }
  }

  @Override
  protected SelectQuery<EipTMemo> buildSelectQueryForFilter(
      SelectQuery<EipTMemo> query, RunData rundata, Context context) {
    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    String filter_type =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_TYPE_STR);
    String crt_key = null;
    Attributes map = getColumnMap();
    crt_key = filter_type != null ? map.getValue(filter_type) : null;
    if (filter != null
      && filter_type != null
      && !filter.equals("")
      && crt_key != null) {
      Expression exp = ExpressionFactory.matchDbExp(crt_key, filter);
      query.andQualifier(exp);
      current_filter = filter;
      current_filter_type = filter_type;
    }
    String search = ALEipUtils.getTemp(rundata, context, LIST_SEARCH_STR);
    if (search != null && !search.equals("")) {
      current_search = search;
      Expression ex1 =
        ExpressionFactory.likeExp(EipTMemo.MEMO_NAME_PROPERTY, "%"
          + search
          + "%");
      Expression ex2 =
        ExpressionFactory.likeExp(EipTMemo.NOTE_PROPERTY, "%" + search + "%");
      SelectQuery<EipTMemo> q = Database.query(EipTMemo.class);
      q.andQualifier(ex1.orExp(ex2));
      List<EipTMemo> queryList = q.fetchList();
      List<Integer> resultid = new ArrayList<Integer>();
      for (EipTMemo item : queryList) {
        resultid.add(item.getMemoId());
      }
      if (resultid.size() == 0) {
        // 検索結果がないことを示すために-1を代入
        resultid.add(-1);
      }
      Expression ex =
        ExpressionFactory.inDbExp(EipTMemo.MEMO_ID_PK_COLUMN, resultid);
      query.andQualifier(ex);
    }
    return query;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTMemo> getSelectQuery(RunData rundata, Context context) {
    if ((target_keyword != null) && (!target_keyword.getValue().equals(""))) {
      ALEipUtils.setTemp(rundata, context, LIST_SEARCH_STR, target_keyword
        .getValue());
    } else {
      ALEipUtils.removeTemp(rundata, context, LIST_SEARCH_STR);
    }

    SelectQuery<EipTMemo> query = Database.query(EipTMemo.class);
    Expression exp1 =
      ExpressionFactory.matchExp(EipTMemo.OWNER_ID_PROPERTY, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    query.setQualifier(exp1);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTMemo record) {
    try {
      MemoResultData rd = new MemoResultData();
      rd.initField();
      rd.setMemoId(record.getMemoId());
      rd.setMemoName(record.getMemoName());
      rd.setNote(record.getNote());
      rd.setUpdateDate(record.getUpdateDate());
      rd.setCreateDate(record.getCreateDate());
      return rd;
    } catch (Exception ex) {
      logger.error("memo", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public EipTMemo selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {
    try {
      EipTMemo memo = MemoUtils.getEipTMemo(rundata, context);
      return memo;
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTMemo record) {
    try {
      MemoResultData rd = new MemoResultData();
      rd.initField();
      rd.setMemoId(record.getMemoId().intValue());
      rd.setMemoName(record.getMemoName());
      rd.setNote(record.getNote());
      rd.setCreateDate(record.getCreateDate());
      rd.setUpdateDate(record.getUpdateDate());
      return rd;
    } catch (Exception ex) {
      logger.error("memo", ex);
      return null;
    }
  }

  /**
   * Memo の総数を返す． <BR>
   * 
   * @return
   */
  public int getMemoSum() {
    return memoSum;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("memo_name", EipTMemo.MEMO_NAME_PROPERTY);
    map.putValue("update_date", EipTMemo.UPDATE_DATE_PROPERTY);
    map.putValue("create_date", EipTMemo.CREATE_DATE_PROPERTY);
    return map;
  }

  public List<MemoLiteResultData> getMemoLiteList() {
    return memoLiteList;
  }

  /**
   * @return target_keyword
   */
  public ALStringField getTargetKeyword() {
    return target_keyword;
  }

}
