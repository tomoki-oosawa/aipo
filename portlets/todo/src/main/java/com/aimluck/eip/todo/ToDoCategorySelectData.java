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
package com.aimluck.eip.todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTTodoCategory;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ToDoカテゴリ検索データを管理するクラスです。 <BR>
 *
 */
public class ToDoCategorySelectData extends
    ALAbstractSelectData<EipTTodoCategory, EipTTodoCategory> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ToDoCategorySelectData.class.getName());

  /** カテゴリの総数 */
  private int categorySum;

  private String target_user_id;

  private String target_group_name;

  private ArrayList<ALEipGroup> myGroupList;

  private ArrayList<ToDoCategoryResultData> categoryList;

  private boolean hasAclShowCategoryOther;

  private boolean hasAclEditCategoryOther;

  private boolean hasAclDeleteCategoryOther;

  private int login_user_id;

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
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "category_name");
      logger.debug("[ToDoCategorySelectData] Init Parameter. : "
        + "category_name");
    }

    login_user_id = ALEipUtils.getUserId(rundata);

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    // アクセス権(他人のカテゴリー閲覧)
    hasAclShowCategoryOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_CATEGORY_OTHER,
        ALAccessControlConstants.VALUE_ACL_LIST);

    // アクセス権(他人のカテゴリー編集)
    hasAclEditCategoryOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_CATEGORY_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

    // アクセス権(他人のカテゴリー削除)
    hasAclDeleteCategoryOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_CATEGORY_OTHER,
        ALAccessControlConstants.VALUE_ACL_DELETE);

    super.init(action, rundata, context);
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTTodoCategory> selectList(RunData rundata,
      Context context) {
    try {
      target_group_name = ToDoUtils.getTargetGroupName(rundata, context);
      target_user_id = ToDoUtils.getTargetUserId(rundata, context);
      setMyGroupList(new ArrayList<ALEipGroup>());
      getMyGroupList().addAll(ALEipUtils.getMyGroups(rundata));

      SelectQuery<EipTTodoCategory> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTTodoCategory> list = query.getResultList();
      // 件数をセットする．
      categorySum = list.getTotalCount();
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
  private SelectQuery<EipTTodoCategory> getSelectQuery(RunData rundata,
      Context context) {
    if (hasAclShowCategoryOther) {
      return Database.query(EipTTodoCategory.class).where(
        Operations.ne(EipTTodoCategory.USER_ID_PROPERTY, 0));
    } else {
      return Database.query(EipTTodoCategory.class).where(
        Operations.eq(EipTTodoCategory.USER_ID_PROPERTY, ALEipUtils
          .getUserId(rundata)));
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
  protected EipTTodoCategory selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // オブジェクトモデルを取得
    return ToDoUtils.getEipTTodoCategory(rundata, context);
  }

  /**
   * ResultDataを取得します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTTodoCategory record) {
    ToDoCategoryResultData rd = new ToDoCategoryResultData();
    rd.initField();
    rd.setCategoryId(record.getCategoryId().longValue());
    rd.setCategoryName(ALCommonUtils.compressString(
      record.getCategoryName(),
      getStrLength()));

    rd.setHasAclDeleteCategoryOther(hasAclDeleteCategoryOther);
    rd.setHasAclEditCategoryOther(hasAclEditCategoryOther);
    rd.setIsSelfCategory(record.getUpdateUserId() == login_user_id);
    try {
      rd.setUpdateUserId(new ALNumberField(record.getUpdateUserId()));
      rd.setUpdateUserName(ALEipUtils
        .getALEipUser(record.getUpdateUserId())
        .getAliasName()
        .getValue());
    } catch (ALDBErrorException ex) {
      logger.error("todo", ex);
    }
    rd.setNote(record.getNote());
    return rd;
  }

  /**
   * ResultDataを取得します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTTodoCategory record) {
    ToDoCategoryResultData rd = new ToDoCategoryResultData();
    rd.initField();
    rd.setCategoryId(record.getCategoryId().longValue());
    rd.setCategoryName(record.getCategoryName());
    rd.setNote(record.getNote());
    try {
      rd.setUserName(ALEipUtils
        .getALEipUser(record.getUserId())
        .getAliasName()
        .getValue());
      rd.setUserId(new ALNumberField(record.getUserId()));
      rd.setUpdateUserName(ALEipUtils
        .getALEipUser(record.getUpdateUserId())
        .getAliasName()
        .getValue());
      rd.setUpdateUserId(new ALNumberField(record.getUpdateUserId()));
    } catch (ALDBErrorException ex) {
      logger.error("todo", ex);
    }
    rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
    rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
    rd.setHasAclDeleteCategoryOther(hasAclDeleteCategoryOther);
    rd.setHasAclEditCategoryOther(hasAclEditCategoryOther);
    rd.setIsSelfCategory(record.getUserId() == login_user_id);
    return rd;
  }

  /**
   * @return
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("category_name", EipTTodoCategory.CATEGORY_NAME_PROPERTY);
    map.putValue("user_name", EipTTodoCategory.TURBINE_USER_PROPERTY
      + "."
      + TurbineUser.LAST_NAME_KANA_PROPERTY);
    return map;
  }

  /**
   *
   * @param rundata
   * @param context
   */
  public void loadCategoryList(RunData rundata) {
    categoryList = ToDoUtils.getCategoryList(rundata);
  }

  public int getCategorySum() {
    return categorySum;
  }

  /**
   * 現在選択されているタブを取得します。 <BR>
   *
   * @return
   */
  public String getCurrentTab() {
    return "category";
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
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_TODO_CATEGORY_SELF;
  }

  public void setMyGroupList(ArrayList<ALEipGroup> myGroupList) {
    this.myGroupList = myGroupList;
  }

  public ArrayList<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   *
   * @return
   */
  public List<ToDoCategoryResultData> getCategoryList() {
    return categoryList;
  }

  public ALNumberField getLoginUserId() {
    return new ALNumberField(login_user_id);
  }
}
