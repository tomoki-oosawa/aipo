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
import java.util.Map;
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
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
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
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 公開ToDoの検索データを管理するためのクラスです。 <br />
 */
public class ToDoPublicSelectData extends
    ALAbstractSelectData<EipTTodo, EipTTodo> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoSelectData.class.getName());

  /** 現在選択されているタブ */
  private String currentTab;

  /** 表示対象の部署名 */
  private String target_group_name;

  /** 表示対象のユーザ ID */
  private String target_user_id;

  /** グループリスト（My グループと部署） */
  private List<ALEipGroup> myGroupList = null;

  /** ToDo の総数 */
  private int publicTodoSum;

  /** ポートレット Schedule への URL */
  private String scheduleUrl;

  /** アクセスコントロール用の変数 */

  private String aclPortletFeature;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p3b-sort"));
      logger.debug("[ToDoPublicSelectData] Init Parameter. : "
        + ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p3b-sort"));
    }
    super.init(action, rundata, context);

    String entityId_Str =
      rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    if (entityId_Str == null || "".equals(entityId_Str)) {
      aclPortletFeature =
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER;
    } else {
      Integer entityId = Integer.parseInt(entityId_Str);
      int aimUserId = getUserId(rundata, context, entityId);
      int uid = ALEipUtils.getUserId(rundata);
      if (aimUserId != uid) {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER;
      } else {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
      }
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<EipTTodo> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String tabParam = rundata.getParameters().getString("publictab");
    currentTab = ALEipUtils.getTemp(rundata, context, "publictab");
    if (tabParam == null && currentTab == null) {
      ALEipUtils.setTemp(rundata, context, "publictab", "list");
      currentTab = "list";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "publictab", tabParam);
      currentTab = tabParam;
    }

    target_group_name = ToDoUtils.getTargetGroupName(rundata, context);
    target_user_id = ToDoUtils.getTargetUserId(rundata, context);

    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    myGroupList = new ArrayList<ALEipGroup>();
    int length = myGroups.size();
    for (int i = 0; i < length; i++) {
      myGroupList.add(myGroups.get(i));
    }

    try {
      SelectQuery<EipTTodo> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTTodo> list = query.getResultList();
      // ToDo の総数をセットする．
      publicTodoSum = list.getTotalCount();
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
   * @param context
   * @return
   */
  private SelectQuery<EipTTodo> getSelectQuery(RunData rundata, Context context) {
    SelectQuery<EipTTodo> query = Database.query(EipTTodo.class);
    Expression exp0 =
      ExpressionFactory.matchExp(EipTTodo.PUBLIC_FLAG_PROPERTY, "T");
    query.setQualifier(exp0);
    if ((target_user_id != null)
      && (!target_user_id.equals(""))
      && (!target_user_id.equals("all"))) {
      Expression exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(target_user_id));
      query.andQualifier(exp1);
    }

    if ((target_group_name != null)
      && (!target_group_name.equals(""))
      && (!target_group_name.equals("all"))) {
      // 選択したグループを指定する．
      Expression exp2 =
        ExpressionFactory.matchExp(EipTTodo.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
          + "."
          + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
          + "."
          + TurbineGroup.GROUP_NAME_PROPERTY, target_group_name);
      query.andQualifier(exp2);
    }

    if ("list".equals(currentTab)) {
      Expression exp3 =
        ExpressionFactory.noMatchExp(EipTTodo.STATE_PROPERTY, Short
          .valueOf((short) 100));
      query.andQualifier(exp3);
    } else if ("complete".equals(currentTab)) {
      Expression exp3 =
        ExpressionFactory.matchExp(EipTTodo.STATE_PROPERTY, Short
          .valueOf((short) 100));
      query.andQualifier(exp3);
    }

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTTodo selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String js_peid = rundata.getParameters().getString("sch");

    if (js_peid != null && !js_peid.equals("")) {
      // ポートレット Scheduleのへのリンクを取得する．
      int prev = rundata.getParameters().getInt("prev");
      if (prev == JetspeedRunData.MAXIMIZE) {
        // 最大化画面のとき
        scheduleUrl = ALEipUtils.getPortletURI(rundata, js_peid);
      } else {
        // ノーマル画面のとき
        scheduleUrl = ALEipUtils.getPortletURItoTopPage(rundata, js_peid);
      }
    }

    return ToDoUtils.getEipTPublicTodo(rundata, context, true);
  }

  /**
   *
   */
  @Override
  protected Object getResultData(EipTTodo record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      // 登録ユーザ名の設定
      ALBaseUser createdUser =
        ALEipUtils.getBaseUser(record.getTurbineUser().getUserId().intValue());
      String createdUserName =
        new StringBuffer()
          .append(createdUser.getLastName())
          .append(" ")
          .append(createdUser.getFirstName())
          .toString();

      ToDoPublicResultData rd = new ToDoPublicResultData();
      rd.initField();
      rd.setTodoId(record.getTodoId().longValue());
      rd.setCategoryName(ALCommonUtils.compressString(record
        .getEipTTodoCategory()
        .getCategoryName(), getStrLength()));

      rd.setCreatedUser(createdUserName);

      rd.setTodoName(ALCommonUtils.compressString(
        record.getTodoName(),
        getStrLength()));

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
      // 期限状態を設定する．
      rd.setLimitState(ToDoUtils.getLimitState(record.getEndDate()));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTTodo record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      // 登録ユーザ名の設定
      ALBaseUser createdUser =
        ALEipUtils.getBaseUser(record.getTurbineUser().getUserId().intValue());
      String createdUserName =
        new StringBuffer()
          .append(createdUser.getLastName())
          .append(" ")
          .append(createdUser.getFirstName())
          .toString();

      ToDoPublicResultData rd = new ToDoPublicResultData();
      rd.initField();
      rd.setTodoName(record.getTodoName());
      rd.setTodoId(record.getTodoId().longValue());
      rd.setCategoryName(record.getEipTTodoCategory().getCategoryName());
      rd.setCreatedUser(createdUserName);
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
      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  private int getUserId(RunData rundata, Context context, Integer entityId) {
    Expression exp =
      ExpressionFactory.matchDbExp(EipTTodo.TODO_ID_PK_COLUMN, entityId);
    SelectQuery<EipTTodo> query = Database.query(EipTTodo.class, exp);
    List<EipTTodo> record = query.fetchList();
    if (record.size() > 0) {
      return record.get(0).getUserId().intValue();
    } else {
      return -1;
    }

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
  public int getPublicTodoSum() {
    return publicTodoSum;
  }

  public String getTargetGroupName() {
    return target_group_name;
  }

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
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * 
   * @return
   */
  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("todo_name", EipTTodo.TODO_NAME_PROPERTY);
    map.putValue("state", EipTTodo.STATE_PROPERTY);
    map.putValue("priority", EipTTodo.PRIORITY_PROPERTY);
    map.putValue("end_date", EipTTodo.END_DATE_PROPERTY);
    map.putValue("category", EipTTodo.EIP_TTODO_CATEGORY_PROPERTY
      + "."
      + EipTTodoCategory.CATEGORY_NAME_PROPERTY);
    map.putValue("user_name", EipTTodo.TURBINE_USER_PROPERTY
      + "."
      + TurbineUser.LAST_NAME_KANA_PROPERTY);
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
    return aclPortletFeature;
  }
}
