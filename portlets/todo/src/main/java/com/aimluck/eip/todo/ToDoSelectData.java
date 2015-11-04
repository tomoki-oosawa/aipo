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
package com.aimluck.eip.todo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
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
import com.aimluck.eip.common.ALAbstractMultiFilterSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
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
public class ToDoSelectData extends
    ALAbstractMultiFilterSelectData<EipTTodo, EipTTodo> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoSelectData.class.getName());

  /** 現在選択されているタブ */
  private String currentTab;

  /** カテゴリ一覧 */
  private List<ToDoCategoryResultData> categoryList;

  /** 部署一覧 */
  private List<ALEipGroup> postList;

  /** ToDo の総数 */
  private int todoSum;

  /** ポートレット Schedule への URL */
  private String scheduleUrl;

  private String target_group_name;

  private String target_user_id;

  private List<ALEipGroup> myGroupList;

  private int login_user_id;

  private String aclPortletFeature;

  private boolean hasAclListTodoOther;

  private boolean hasAclDetailTodoOther;

  private boolean hasAclEditTodoOther;

  private boolean hasAclDeleteTodoOther;

  private int table_colum_num;

  /** カテゴリの初期値を取得する */
  private final String filterType = "";

  /** カテゴリ　ID */
  private final String categoryId = "";

  /** ターゲット　 */
  private ALStringField target_keyword;

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

    if (rundata.getParameters().getString("entityid") != null) {
      ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, rundata
        .getParameters()
        .getString("entityid"));
    }

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

    int view_uid = ToDoUtils.getViewId(rundata, context, login_user_id);

    // アクセス権
    if (view_uid == login_user_id) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
    } else {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER;
    }

    // アクセス権(他人のToDo一覧表示)
    hasAclListTodoOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);

    // アクセス権(他人のToDo詳細表示)
    hasAclDetailTodoOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER,
        ALAccessControlConstants.VALUE_ACL_DETAIL);

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

    // My グループの一覧を取得する．
    postList = ALEipUtils.getMyGroups(rundata);
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
        target_keyword.setValue("");
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

      ResultList<EipTTodo> list = query.getResultList();
      setPageParam(list.getTotalCount());
      todoSum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("todo", ex);
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
      Expression exp2 =
        ExpressionFactory.likeExp(EipTTodo.NOTE_PROPERTY, keyword);

      /* フルネーム対応 */
      String first_name = keyword;
      String last_name = keyword;
      char[] c = { '\u3000' };
      String wspace = new String(c);
      String keyword2 = target_keyword.getValue().replaceAll(wspace, " ");
      if (keyword2.contains(" ")) {
        String[] spstr = keyword2.split(" ");
        if (spstr.length == 2) {
          first_name = "%" + spstr[1] + "%";
          last_name = "%" + spstr[0] + "%";
        }
      }

      Expression exp3 =
        ExpressionFactory.likeExp(EipTTodo.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.FIRST_NAME_PROPERTY, first_name);
      Expression exp4 =
        ExpressionFactory.likeExp(EipTTodo.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.FIRST_NAME_KANA_PROPERTY, first_name);
      Expression exp5 =
        ExpressionFactory.likeExp(EipTTodo.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.LAST_NAME_PROPERTY, last_name);
      Expression exp6 =
        ExpressionFactory.likeExp(EipTTodo.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.LAST_NAME_KANA_PROPERTY, last_name);

      query.andQualifier(exp
        .orExp(exp2)
        .orExp(exp3)
        .orExp(exp4)
        .orExp(exp5)
        .orExp(exp6));
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
      && (target_user_id.equals("all") || target_user_id.equals(String
        .valueOf(login_user_id)))) {
      Expression exp6 =
        ExpressionFactory.matchExp(EipTTodo.PUBLIC_FLAG_PROPERTY, "F");
      Expression exp7 =
        ExpressionFactory.matchExp(EipTTodo.USER_ID_PROPERTY, login_user_id);
      query.andQualifier(exp5.orExp(exp6.andExp(exp7)));
    } else {
      query.andQualifier(exp5);
    }

    // 他人のToDoの一覧表示権限がない場合にはユーザーIDが一致していれば閲覧可能
    if (!hasAclListTodoOther) {
      Expression exp8 =
        ExpressionFactory.matchExp(EipTTodo.USER_ID_PROPERTY, login_user_id);
      query.andQualifier(exp8);
    }

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * パラメータをマップに変換します。
   *
   * @param key
   * @param val
   */
  @Override
  protected void parseFilterMap(String key, String val) {
    super.parseFilterMap(key, val);

    Set<String> unUse = new HashSet<String>();

    for (Entry<String, List<String>> pair : current_filterMap.entrySet()) {
      if (pair.getValue().contains("0")) {
        unUse.add(pair.getKey());
      }
    }
    for (String unusekey : unUse) {
      current_filterMap.remove(unusekey);
    }
  }

  // :TODO
  @Override
  protected SelectQuery<EipTTodo> buildSelectQueryForFilter(
      SelectQuery<EipTTodo> query, RunData rundata, Context context) {

    super.buildSelectQueryForFilter(query, rundata, context);

    if (current_filterMap.containsKey("post")) {
      // 部署を含んでいる場合デフォルトとは別にフィルタを用意

      List<String> postIds = current_filterMap.get("post");

      boolean existPost = false;

      for (int i = 0; i < postList.size(); i++) {
        String pid = postList.get(i).getName().toString();
        if (pid.equals(postIds.get(0).toString())) {
          existPost = true;
          break;
        }
      }
      Map<Integer, ALEipPost> map = ALEipManager.getInstance().getPostMap();
      for (Map.Entry<Integer, ALEipPost> item : map.entrySet()) {
        String pid = item.getValue().getGroupName().toString();
        if (pid.equals(postIds.get(0).toString())) {
          existPost = true;
          break;
        }
      }

      if (existPost) {
        HashSet<Integer> userIds = new HashSet<Integer>();
        for (String post : postIds) {
          List<Integer> userId = ALEipUtils.getUserIds(post);
          userIds.addAll(userId);
        }
        if (userIds.isEmpty()) {
          userIds.add(-1);
        }
        Expression exp =
          ExpressionFactory.inExp(EipTTodo.USER_ID_PROPERTY, userIds);
        query.andQualifier(exp);
      } else {
        current_filterMap.remove("post");
      }
    }

    // String search = ALEipUtils.getTemp(rundata, context, LIST_SEARCH_STR);
    // if (search != null && !"".equals(search)) {
    // current_search = search;
    // Expression ex1 =
    // ExpressionFactory.likeExp(EipTTodo.NOTE_PROPERTY, "%" + search + "%");
    // Expression ex2 =
    // ExpressionFactory.likeExp(EipTTodo.TODO_NAME_PROPERTY, "%"
    // + search
    // + "%");
    // SelectQuery<EipTTodo> q = Database.query(EipTTodo.class);
    // q.andQualifier(ex1.orExp(ex2));
    // List<EipTTodo> queryList = q.fetchList();
    // List<Integer> resultid = new ArrayList<Integer>();
    // for (EipTTodo item : queryList) {
    // if (item.getParentId() != 0 && !resultid.contains(item.getParentId())) {
    // resultid.add(item.getParentId());
    // } else if (!resultid.contains(item.getTodoId())) {
    // resultid.add(item.getTodoId());
    // }
    // }
    // if (resultid.size() == 0) {
    // // 検索結果がないことを示すために-1を代入
    // resultid.add(-1);
    // }
    // Expression ex =
    // ExpressionFactory.inDbExp(EipTTodo.TODO_ID_PK_COLUMN, resultid);
    // query.andQualifier(ex);
    // }
    return query;
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
      rd.setUserId(record.getUserId().longValue());
      rd.setUserName(ALEipUtils
        .getALEipUser(record.getUserId())
        .getAliasName()
        .getValue());
      rd.setLoginUserId(login_user_id);
      rd.setTodoName(ALCommonUtils.compressString(
        record.getTodoName(),
        getStrLength()));
      if (!ToDoUtils.isEmptyDate(record.getStartDate())) {
        rd.setStartDate(ALDateUtil
          .format(record.getStartDate(), "yyyy年M月d日(E)"));
      }
      if (!ToDoUtils.isEmptyDate(record.getEndDate())) {
        rd.setEndDate(ALDateUtil.format(record.getEndDate(), "yyyy年M月d日(E)"));
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

      // 自身のToDoかを設定する
      rd.setIsSelfTodo(record.getUserId() == login_user_id);

      rd.setAclListTodoOther(hasAclListTodoOther);
      rd.setAclDetailTodoOther(hasAclDetailTodoOther);
      rd.setAclEditTodoOther(hasAclEditTodoOther);
      rd.setAclDeleteTodoOther(hasAclDeleteTodoOther);
      return rd;
    } catch (Exception ex) {
      logger.error("todo", ex);
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
      rd.setUserId(record.getUserId().longValue());
      rd.setUserName(ALEipUtils
        .getALEipUser(record.getUserId())
        .getAliasName()
        .getValue());
      if (!ToDoUtils.isEmptyDate(record.getStartDate())) {
        rd.setStartDate(ALDateUtil
          .format(record.getStartDate(), "yyyy年M月d日(E)"));
      }
      if (!ToDoUtils.isEmptyDate(record.getEndDate())) {
        rd.setEndDate(ALDateUtil.format(record.getEndDate(), "yyyy年M月d日(E)"));
      }
      rd.setStateString(ToDoUtils.getStateString(record.getState().intValue()));
      rd.setPriorityString(ToDoUtils.getPriorityString(record
        .getPriority()
        .intValue()));
      rd.setNote(record.getNote());
      rd.setCreateUserId(record.getCreateUserId().longValue());
      rd.setCreateUserName(ALEipUtils
        .getALEipUser(record.getCreateUserId())
        .getAliasName()
        .getValue());
      rd.setLoginUserId(login_user_id);

      // 公開/非公開を設定する．
      rd.setPublicFlag("T".equals(record.getPublicFlag()));
      rd.setAddonScheduleFlg("T".equals(record.getAddonScheduleFlg()));
      rd.setCreateDate(ALDateUtil
        .format(record.getCreateDate(), "yyyy年M月d日(E)"));
      rd.setUpdateDate(record.getUpdateDate());

      // 自身のToDoかを設定する
      rd.setIsSelfTodo(record.getUserId() == login_user_id);

      rd.setAclListTodoOther(hasAclListTodoOther);
      rd.setAclDetailTodoOther(hasAclDetailTodoOther);
      rd.setAclEditTodoOther(hasAclEditTodoOther);
      rd.setAclDeleteTodoOther(hasAclDeleteTodoOther);

      // もし自分のToDoではない場合に、他人のToDoの詳細表示権限がないときはエラー
      if (record.getUserId() != login_user_id && !hasAclDetailTodoOther) {
        throw new ALPermissionException();
      }

      return rd;

    } catch (RuntimeException ex) {
      logger.error("todo", ex);
      return null;
    } catch (Exception ex) {
      logger.error("todo", ex);
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

  public void setTargetGroupName(String target_group_name) {
    this.target_group_name = target_group_name;
  }

  /**
   * @return target_user_id
   */
  public String getTargetUserId() {
    return target_user_id;
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
   * アクセス権限をチェックします。
   *
   * @return
   */
  @Override
  protected boolean doCheckAclPermission(RunData rundata, Context context,
      int defineAclType) throws ALPermissionException {

    if (defineAclType == 0) {
      return true;
    }

    String pfeature = getAclPortletFeature();
    if (pfeature == null || "".equals(pfeature)) {
      return true;
    }

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    hasAuthority =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        pfeature,
        defineAclType);

    if (!hasAuthority) {
      throw new ALPermissionException();
    }

    return true;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
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

  /**
   * @return table_colum_num
   */
  public int getTableColumNum() {
    return table_colum_num;
  }

  /**
   * @param table_colum_num
   *          セットする
   */
  public void setTableColumNum(int table_colum_num) {
    this.table_colum_num = table_colum_num;
  }

  /**
   * 部署一覧を取得します
   *
   * @return postList
   */
  public List<ALEipGroup> getPostList() {
    return postList;
  }

  /**
   * 部署の一覧を取得する．
   *
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  public void setFiltersPSML(VelocityPortlet portlet, Context context,
      RunData rundata) {
    ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, portlet
      .getPortletConfig()
      .getInitParameter("p12f-filters"));

    ALEipUtils.setTemp(rundata, context, LIST_FILTER_TYPE_STR, portlet
      .getPortletConfig()
      .getInitParameter("p12g-filtertypes"));
  }

  public String getCategoryId() {
    return categoryId;
  }
}
