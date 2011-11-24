/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTTodo;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
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
  private List<ToDoCategoryResultData> categoryList;

  /** ToDo の総数 */
  private int todoSum;

  /** ポートレット Schedule への URL */
  private String scheduleUrl;

  private String target_group_name;

  private String target_user_id;

  private ALStringField target_keyword;

  private List<ALEipGroup> myGroupList;

  private int login_user_id;

  private boolean hasAclEditTodoOther;

  private boolean hasAclDeleteTodoOther;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(
        rundata,
        context,
        LIST_SORT_STR,
        EipTTodo.UPDATE_DATE_PROPERTY);
      ALEipUtils.setTemp(
        rundata,
        context,
        LIST_SORT_TYPE_STR,
        ALEipConstants.LIST_SORT_TYPE_DESC);
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

    login_user_id = ALEipUtils.getUserId(rundata);

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    // アクセス権(他人のToDo編集)
    hasAclEditTodoOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

    // アクセス権(他人のToDo削除)
    hasAclDeleteTodoOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER,
        ALAccessControlConstants.VALUE_ACL_DELETE);
    target_keyword = new ALStringField();
    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata) {
    categoryList = ToDoUtils.getCategoryList(rundata);
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALDBErrorException
   */
  @Override
  public ResultList<EipTTodo> selectList(RunData rundata, Context context) {
    try {
      if (ToDoUtils.hasResetFlag(rundata, context)) {
        ToDoUtils.resetFilter(rundata, context, this.getClass().getName());
      }
      if (ToDoUtils.hasResetKeywordFlag(rundata, context)) {
        ToDoUtils.resetKeyword(rundata, context, this.getClass().getName());
      }
      if (ToDoUtils.hasResetTargetFlag(rundata, context)) {
        ToDoUtils.resetTarget(rundata, context, this.getClass().getName());
      }
      target_group_name = ToDoUtils.getTargetGroupName(rundata, context);
      target_user_id = ToDoUtils.getTargetUserId(rundata, context);
      target_keyword.setValue(ToDoUtils.getTargetKeyword(rundata, context));

      setMyGroupList(new ArrayList<ALEipGroup>());
      getMyGroupList().addAll(ALEipUtils.getMyGroups(rundata));

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
    Expression exp1;
    if ((target_user_id != null)
      && (!target_user_id.equals(""))
      && (!target_user_id.equals("all"))) {
      exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(target_user_id));
      // exp0.andExp(exp1);
      query.andQualifier(exp1);
    }

    if ((target_group_name != null)
      && (!target_group_name.equals(""))
      && (!target_group_name.equals("all"))) {
      // 選択したグループを指定する．
      Expression exp =
        ExpressionFactory.matchExp(EipTTodo.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
          + "."
          + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
          + "."
          + TurbineGroup.GROUP_NAME_PROPERTY, target_group_name);
      query.andQualifier(exp);
    }

    if ((target_keyword != null) && (!target_keyword.getValue().equals(""))) {
      // 選択したキーワードを指定する．
      String keyword = "%" + target_keyword.getValue() + "%";
      Expression exp =
        ExpressionFactory.likeExp(EipTTodo.TODO_NAME_PROPERTY, keyword);
      query.andQualifier(exp.orExp(ExpressionFactory.likeExp(
        EipTTodo.NOTE_PROPERTY,
        keyword)));
    }

    if ("list".equals(currentTab)) {
      Expression exp3 =
        ExpressionFactory.noMatchExp(EipTTodo.STATE_PROPERTY, Short
          .valueOf((short) 100));
      query.andQualifier(exp3);
    } else if ("complete".equals(currentTab)) {
      Expression exp4 =
        ExpressionFactory.matchExp(EipTTodo.STATE_PROPERTY, Short
          .valueOf((short) 100));
      query.andQualifier(exp4);
    }

    // 公開ならば無条件に閲覧
    // 非公開ならuserIDが一致していれば閲覧可能
    Expression exp5 =
      ExpressionFactory.matchExp(EipTTodo.PUBLIC_FLAG_PROPERTY, "T");
    if (target_user_id != null
      && (target_user_id.equals("all") || target_user_id.equals(login_user_id))) {
      Expression exp6 =
        ExpressionFactory.matchExp(EipTTodo.PUBLIC_FLAG_PROPERTY, "F");
      Expression exp7 =
        ExpressionFactory.matchExp(EipTTodo.USER_ID_PROPERTY, login_user_id);
      query.andQualifier(exp5.orExp(exp6.andExp(exp7)));
    } else {
      query.andQualifier(exp5);
    }

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTTodo record) {
    try {
      // For data inconsistencies
      EipTTodoCategory category = record.getEipTTodoCategory();
      if (category == null) {
        return null;
      }

      ToDoResultData rd = new ToDoResultData();
      rd.initField();
      rd.setTodoId(record.getTodoId().intValue());
      rd.setCategoryId((int) category.getCategoryId().longValue());
      rd.setCategoryName(ALCommonUtils.compressString(record
        .getEipTTodoCategory()
        .getCategoryName(), getStrLength()));
      rd.setUserName(ALEipUtils
        .getALEipUser(record.getUserId())
        .getAliasName()
        .getValue());
      rd.setTodoName(ALCommonUtils.compressString(
        record.getTodoName(),
        getStrLength()));
      if (!ToDoUtils.isEmptyDate(record.getStartDate())) {
        rd.setStartDate(ALDateUtil
          .format(record.getStartDate(), "yyyy年M月d日（E）"));
      }
      if (!ToDoUtils.isEmptyDate(record.getEndDate())) {
        rd.setEndDate(ALDateUtil.format(record.getEndDate(), "yyyy年M月d日（E）"));
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
      rd.setUpdateDate(record.getUpdateDate());

      // 公開/非公開を設定する．
      rd.setPublicFlag("T".equals(record.getPublicFlag()));
      // 期限状態を設定する．
      rd.setLimitState(ToDoUtils.getLimitState(record.getEndDate()));

      rd.setAclEditTodoOther(hasAclEditTodoOther);
      rd.setAclDeleteTodoOther(hasAclDeleteTodoOther);
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
      rd.setUserName(ALEipUtils
        .getALEipUser(record.getUserId())
        .getAliasName()
        .getValue());
      if (!ToDoUtils.isEmptyDate(record.getStartDate())) {
        rd.setStartDate(ALDateUtil
          .format(record.getStartDate(), "yyyy年M月d日（E）"));
      }
      if (!ToDoUtils.isEmptyDate(record.getEndDate())) {
        rd.setEndDate(ALDateUtil.format(record.getEndDate(), "yyyy年M月d日（E）"));
      }
      rd.setStateString(ToDoUtils.getStateString(record.getState().intValue()));
      rd.setPriorityString(ToDoUtils.getPriorityString(record
        .getPriority()
        .intValue()));
      rd.setNote(record.getNote());
      rd.setCreateUserName(ALEipUtils
        .getALEipUser(record.getCreateUserId())
        .getAliasName()
        .getValue());
      // 公開/非公開を設定する．
      rd.setPublicFlag("T".equals(record.getPublicFlag()));
      rd.setAddonScheduleFlg("T".equals(record.getAddonScheduleFlg()));
      rd.setCreateDate(ALDateUtil
        .format(record.getCreateDate(), "yyyy年M月d日（E）"));
      rd.setUpdateDate(record.getUpdateDate());

      // 自身のToDoかを設定する
      rd.setIsSelfTodo(record.getUserId() == login_user_id);

      rd.setAclEditTodoOther(hasAclEditTodoOther);
      rd.setAclDeleteTodoOther(hasAclDeleteTodoOther);
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
   * 
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
    map.putValue("user_name", EipTTodoCategory.TURBINE_USER_PROPERTY
      + "."
      + TurbineUser.LAST_NAME_KANA_PROPERTY);
    map.putValue(EipTTodo.UPDATE_DATE_PROPERTY, EipTTodo.UPDATE_DATE_PROPERTY);
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
   * @return target_group_name
   */
  public String getTargetGroupName() {
    return target_group_name;
  }

  /**
   * @return target_user_id
   */
  public String getTargetUserId() {
    return target_user_id;
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * 
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers() {
    if ((target_group_name != null)
      && (!target_group_name.equals(""))
      && (!target_group_name.equals("all"))) {
      return ALEipUtils.getUsers(target_group_name);
    } else {
      return ALEipUtils.getUsers("LoginUser");
    }
  }

  /**
   * @param target_user_id
   *          セットする target_user_id
   */
  public void setTargetUserId(String target_user_id) {
    this.target_user_id = target_user_id;
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

  public void setMyGroupList(List<ALEipGroup> myGroupList) {
    this.myGroupList = myGroupList;
  }

  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   * @return target_keyword
   */
  public ALStringField getTargetKeyword() {
    return target_keyword;
  }
}
