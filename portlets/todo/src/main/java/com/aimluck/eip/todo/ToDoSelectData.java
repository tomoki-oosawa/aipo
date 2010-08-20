/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDo検索データを管理するクラスです。 <BR>
 * 
 */
public class ToDoSelectData extends ALAbstractSelectData<EipTTodo, EipTTodo>
    implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoSelectData.class.getName());

  /** 現在選択されているタブ */
  private String currentTab;

  /** カテゴリ一覧 */
  private ArrayList<ToDoCategoryResultData> categoryList;

  /** ToDo の総数 */
  private int todoSum;

  /** ポートレット Schedule への URL */
  private String scheduleUrl;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p2a-sort"));
      logger.debug("[ToDoSelectData] Init Parameter. : "
        + ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p2a-sort"));
    }

    String tabParam = rundata.getParameters().getString("tab");
    currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tabParam == null && currentTab == null) {
      ALEipUtils.setTemp(rundata, context, "tab", "list");
      currentTab = "list";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    }

    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata, Context context) {
    try {
      // カテゴリ一覧
      categoryList = new ArrayList<ToDoCategoryResultData>();

      Expression exp =
        ExpressionFactory.matchExp(EipTTodoCategory.USER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      exp.orExp(ExpressionFactory.matchExp(
        EipTTodoCategory.USER_ID_PROPERTY,
        Integer.valueOf(0)));

      List<EipTTodoCategory> categoryList2 =
        Database.query(EipTTodoCategory.class, exp).orderAscending(
          EipTTodoCategory.CATEGORY_NAME_PROPERTY).fetchList();

      for (EipTTodoCategory record : categoryList2) {
        ToDoCategoryResultData rd = new ToDoCategoryResultData();
        rd.initField();
        rd.setCategoryId(record.getCategoryId().longValue());
        rd.setCategoryName(record.getCategoryName());
        categoryList.add(rd);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractListData#selectData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  public ResultList<EipTTodo> selectList(RunData rundata, Context context) {
    try {

      SelectQuery<EipTTodo> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTTodo> resultList = query.getResultList();
      setPageParam(resultList.getTotalCount());
      todoSum = resultList.getTotalCount();
      return resultList;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTTodo> getSelectQuery(RunData rundata, Context context) {
    SelectQuery<EipTTodo> query = Database.query(EipTTodo.class);

    Expression exp1 =
      ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    query.setQualifier(exp1);

    if ("list".equals(currentTab)) {
      Expression exp2 =
        ExpressionFactory.noMatchExp(EipTTodo.STATE_PROPERTY, Short
          .valueOf((short) 100));
      query.andQualifier(exp2);
    } else if ("complete".equals(currentTab)) {
      Expression exp2 =
        ExpressionFactory.matchExp(EipTTodo.STATE_PROPERTY, Short
          .valueOf((short) 100));
      query.andQualifier(exp2);
    }

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getListData(java.lang.Object)
   */
  @Override
  protected Object getResultData(EipTTodo record) {
    try {
      ToDoResultData rd = new ToDoResultData();
      rd.initField();
      rd.setTodoId(record.getTodoId().intValue());
      rd.setCategoryId((int) record
        .getEipTTodoCategory()
        .getCategoryId()
        .longValue());

      rd.setCategoryName(ALCommonUtils.compressString(record
        .getEipTTodoCategory()
        .getCategoryName(), getStrLength()));

      rd.setTodoName(ALCommonUtils.compressString(
        record.getTodoName(),
        getStrLength()));
      if (!ToDoUtils.isEmptyDate(record.getStartDate())) {
        rd.setStartDate(ALDateUtil.format(record.getStartDate(), "yyyy年M月d日"));
      }
      if (!ToDoUtils.isEmptyDate(record.getEndDate())) {
        rd.setEndDate(ALDateUtil.format(record.getEndDate(), "yyyy年M月d日"));
      }
      rd.setState(record.getState().intValue());
      rd.setStateImage(ToDoUtils.getStateImage(record.getState().intValue()));
      rd.setStateString(ToDoUtils.getStateString(record.getState().intValue()));
      rd.setPriority(record.getPriority().intValue());
      rd.setPriorityImage(ToDoUtils.getPriorityImage(record
        .getPriority()
        .intValue()));
      rd.setPriorityString(ToDoUtils.getPriorityString(record
        .getPriority()
        .intValue()));
      // 公開/非公開を設定する．
      rd.setPublicFlag("T".equals(record.getPublicFlag()));
      // 期限状態を設定する．
      rd.setLimitState(ToDoUtils.getLimitState(record.getEndDate()));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  public EipTTodo selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {
    String js_peid = rundata.getParameters().getString("sch");

    if (js_peid != null && !js_peid.equals("")) {
      // ポートレット Scheduleのへのリンクを取得する．
      scheduleUrl = getPortletURItoSchedule(rundata, js_peid);
    }

    try {
      EipTTodo todo = ToDoUtils.getEipTTodo(rundata, context, true);
      return todo;
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    }
  }

  protected String getPortletURItoSchedule(RunData rundata,
      String schedulePortletId) {
    int prev = rundata.getParameters().getInt("prev");
    if (prev == JetspeedRunData.MAXIMIZE) {
      // 最大化画面のとき
      return ALEipUtils.getPortletURI(rundata, schedulePortletId);
    } else {
      // ノーマル画面のとき
      return ALEipUtils.getPortletURItoTopPage(rundata, schedulePortletId);
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(EipTTodo record) {
    try {
      ToDoResultData rd = new ToDoResultData();
      rd.initField();
      rd.setTodoName(record.getTodoName());
      rd.setTodoId(record.getTodoId().longValue());
      rd
        .setCategoryId(record.getEipTTodoCategory().getCategoryId().longValue());
      rd.setCategoryName(record.getEipTTodoCategory().getCategoryName());
      if (!ToDoUtils.isEmptyDate(record.getStartDate())) {
        rd.setStartDate(ALDateUtil.format(record.getStartDate(), "yyyy年M月d日"));
      }
      if (!ToDoUtils.isEmptyDate(record.getEndDate())) {
        rd.setEndDate(ALDateUtil.format(record.getEndDate(), "yyyy年M月d日"));
      }
      rd.setStateString(ToDoUtils.getStateString(record.getState().intValue()));
      rd.setPriorityString(ToDoUtils.getPriorityString(record
        .getPriority()
        .intValue()));
      rd.setNote(record.getNote());
      // 公開/非公開を設定する．
      rd.setPublicFlag("T".equals(record.getPublicFlag()));
      rd.setAddonScheduleFlg("T".equals(record.getAddonScheduleFlg()));
      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 
   * @return
   */
  public List<ToDoCategoryResultData> getCategoryList() {
    return categoryList;
  }

  /**
   * 現在選択されているタブを取得します。 <BR>
   * 
   * @return
   */
  public String getCurrentTab() {
    return currentTab;
  }

  /**
   * ToDo の総数を返す． <BR>
   * 
   * @return
   */
  public int getTodoSum() {
    return todoSum;
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("todo_name", EipTTodo.TODO_NAME_PROPERTY);
    map.putValue("state", EipTTodo.STATE_PROPERTY);
    map.putValue("priority", EipTTodo.PRIORITY_PROPERTY);
    map.putValue("end_date", EipTTodo.END_DATE_PROPERTY);
    map.putValue("category_name", EipTTodo.EIP_TTODO_CATEGORY_PROPERTY
      + "."
      + EipTTodoCategory.CATEGORY_NAME_PROPERTY);
    map.putValue("category", EipTTodoCategory.CATEGORY_ID_PK_COLUMN);
    return map;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  public String getScheduleUrl() {
    return scheduleUrl;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
  }
}
