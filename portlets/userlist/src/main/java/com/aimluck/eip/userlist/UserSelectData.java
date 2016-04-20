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
package com.aimluck.eip.userlist;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.account.AccountResultData;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.userlist.utils.UserListUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーアカウントの検索データを管理するためのクラスです。 <br />
 *
 */
public class UserSelectData extends
    ALAbstractSelectData<TurbineUser, TurbineUser> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(UserSelectData.class.getName());

  /** 現在表示している部署 */
  private String currentPost;

  /** 検索キーワード */
  private ALStringField searchWord;

  private int registeredUserNum = 0;

  private boolean adminFilter;

  /** 部署(Myグループ)一覧 */
  private List<ALEipGroup> postList;

  /** 一覧データ */
  private List<Object> list;

  /** <code>userid</code> ユーザーID */
  private int userid;

  /**
   * 初期化します。
   *
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    searchWord = new ALStringField();
    ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "userposition");

    postList = ALEipUtils.getMyGroups(rundata);

    // ログインユーザの ID を設定する．
    userid = ALEipUtils.getUserId(rundata);

    super.init(action, rundata, context);
  }

  /**
   * アカウント一覧を取得します。 ただし、論理削除されているアカウントは取得しません。
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<TurbineUser> selectList(RunData rundata, Context context) {
    try {
      // 登録済みのユーザ数をデータベースから取得

      SelectQuery<TurbineUser> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);
      ResultList<TurbineUser> list = query.getResultList();

      registeredUserNum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("userlist", ex);
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
  protected SelectQuery<TurbineUser> getSelectQuery(RunData rundata,
      Context context) {

    ObjectId oid =
      new ObjectId("TurbineUser", TurbineUser.USER_ID_PK_COLUMN, 3);
    Expression exp1 =
      ExpressionFactory.matchAllDbExp(
        oid.getIdSnapshot(),
        Expression.GREATER_THAN);
    Expression exp2 =
      ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");

    SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
    query.setQualifier(exp1.andExp(exp2));

    adminFilter = rundata.getParameters().getBoolean("adminfiltered");
    if (adminFilter) {
      try {
        Group group = JetspeedSecurity.getGroup("LoginUser");
        Role adminrole = JetspeedSecurity.getRole("admin");
        List<TurbineUserGroupRole> admins =
          Database
            .query(TurbineUserGroupRole.class)
            .where(
              Operations.eq(
                TurbineUserGroupRole.TURBINE_ROLE_PROPERTY,
                adminrole.getId()),
              Operations.eq(TurbineUserGroupRole.TURBINE_GROUP_PROPERTY, group
                .getId()),
              Operations.ne(TurbineUserGroupRole.TURBINE_USER_PROPERTY, 1))
            .distinct(true)
            .fetchList();
        List<Integer> admin_ids = new ArrayList<Integer>();
        admin_ids.add(Integer.valueOf(1));
        for (TurbineUserGroupRole tugr : admins) {
          admin_ids.add(tugr.getTurbineUser().getUserId());
        }
        query.andQualifier(ExpressionFactory.inDbExp(
          TurbineUser.USER_ID_PK_COLUMN,
          admin_ids));
      } catch (Exception ex) {
        logger.error("userlist", ex);
      }
    }

    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    current_filter = filter;

    if (!(filter == null || "".equals(filter))) {
      query.where(Operations.eq(TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
        + "."
        + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
        + "."
        + TurbineGroup.GROUP_NAME_PROPERTY, filter));
    }

    searchWord.setValue(UserListUtils.getKeyword(rundata, context));
    String searchWordValue = searchWord.getValue();
    if (searchWordValue != null && searchWordValue.length() > 0) {
      String transWord =
        ALStringUtil.convertHiragana2Katakana(ALStringUtil
          .convertH2ZKana(searchWordValue));
      transWord = transWord.replace("　", "").replace(" ", ""); // 全角/半角スペースを削除
      String[] transWords = transWord.split(""); // 1文字ずつに分解

      for (int i = 0; i < transWords.length; i++) {
        Expression exp11 =
          ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_PROPERTY, "%"
            + searchWordValue
            + "%");
        Expression exp12 =
          ExpressionFactory.likeExp(TurbineUser.LAST_NAME_PROPERTY, "%"
            + searchWordValue
            + "%");
        Expression exp13 =
          ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_KANA_PROPERTY, "%"
            + searchWordValue
            + "%");
        Expression exp14 =
          ExpressionFactory.likeExp(TurbineUser.LAST_NAME_KANA_PROPERTY, "%"
            + searchWordValue
            + "%");
        Expression exp15 =
          ExpressionFactory.likeExp(TurbineUser.EMAIL_PROPERTY, "%"
            + searchWordValue
            + "%");
        Expression exp16 =
          ExpressionFactory.likeExp(
            TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
              + "."
              + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
              + "."
              + TurbineGroup.GROUP_ALIAS_NAME_PROPERTY,
            "%" + searchWord + "%");
        Expression exp21 =
          ExpressionFactory.likeExp(TurbineUser.OUT_TELEPHONE_PROPERTY, "%"
            + searchWordValue
            + "%");
        Expression exp22 =
          ExpressionFactory.likeExp(TurbineUser.IN_TELEPHONE_PROPERTY, "%"
            + searchWordValue
            + "%");
        Expression exp23 =
          ExpressionFactory.likeExp(TurbineUser.CELLULAR_PHONE_PROPERTY, "%"
            + searchWordValue
            + "%");
        Expression exp31 =
          ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_PROPERTY, "%"
            + transWords[i]
            + "%");
        Expression exp32 =
          ExpressionFactory.likeExp(TurbineUser.LAST_NAME_PROPERTY, "%"
            + transWords[i]
            + "%");
        Expression exp33 =
          ExpressionFactory.likeExp(TurbineUser.FIRST_NAME_KANA_PROPERTY, "%"
            + transWords[i]
            + "%");
        Expression exp34 =
          ExpressionFactory.likeExp(TurbineUser.LAST_NAME_KANA_PROPERTY, "%"
            + transWords[i]
            + "%");
        Expression exp35 =
          ExpressionFactory.likeExp(
            TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
              + "."
              + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
              + "."
              + TurbineGroup.GROUP_ALIAS_NAME_PROPERTY,
            "%" + transWords[i] + "%");
        Expression exp36 =
          ExpressionFactory.likeExp(TurbineUser.CODE_PROPERTY, "%"
            + searchWordValue
            + "%");

        query.andQualifier(exp11.orExp(exp12).orExp(exp13).orExp(exp14).orExp(
          exp15).orExp(exp16).orExp(exp21).orExp(exp22).orExp(exp23).orExp(
          exp31).orExp(exp32).orExp(exp33).orExp(exp34).orExp(exp35).orExp(
          exp36));

        query.distinct();
      }
    }
    return query;
  }

  /**
   * フィルタ用の <code>Criteria</code> を構築します。
   *
   * @param crt
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected SelectQuery<TurbineUser> buildSelectQueryForFilter(
      SelectQuery<TurbineUser> query, RunData rundata, Context context) {
    // 指定部署IDの取得
    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);

    // 指定部署が存在しているかを確認し、存在していなければ値を削除する
    Map<Integer, ALEipPost> gMap = ALEipManager.getInstance().getPostMap();
    if (filter != null
      && filter.trim().length() != 0
      && !gMap.containsKey(Integer.valueOf(filter))) {
      filter = null;
    }

    String filter_type =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_TYPE_STR);
    String crt_key = null;
    Attributes map = getColumnMap();
    if (filter == null || filter_type == null || filter.equals("")) {
      return query;
    }
    crt_key = map.getValue(filter_type);
    if (crt_key == null) {
      return query;
    }

    Expression exp = ExpressionFactory.matchDbExp(crt_key, filter);
    query.andQualifier(exp);
    current_filter = filter;
    current_filter_type = filter_type;
    return query;
  }

  /**
   *
   * @param id
   * @return
   */
  @SuppressWarnings("unused")
  private String getPostName(int id) {
    if (ALEipManager
      .getInstance()
      .getPostMap()
      .containsKey(Integer.valueOf(id))) {
      return (ALEipManager.getInstance().getPostMap().get(Integer.valueOf(id)))
        .getPostName()
        .getValue();
    }
    return null;
  }

  /**
   *
   * @param id
   * @return
   */
  @SuppressWarnings("unused")
  private String getPositionName(int id) {
    if (ALEipManager.getInstance().getPositionMap().containsKey(
      Integer.valueOf(id))) {
      return (ALEipManager.getInstance().getPositionMap().get(Integer
        .valueOf(id))).getPositionName().getValue();
    }
    return null;
  }

  /**
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected TurbineUser selectDetail(RunData rundata, Context context) {
    String userid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    if (userid == null) {
      return null;
    }
    return Database.get(TurbineUser.class, Integer.parseInt(userid));
  }

  /**
   * @param obj
   * @return
   *
   */
  @Override
  protected Object getResultData(TurbineUser record) {
    AccountResultData rd = new AccountResultData();
    rd.initField();
    setResultData(record, rd);
    return rd;
  }

  /**
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(TurbineUser record) {
    try {
      AccountResultData rd = new AccountResultData();
      rd.initField();
      setResultData(record, rd);
      rd.setIsAdmin(ALEipUtils.isAdmin(Integer.valueOf(record.getUserId())));
      rd.setIsOwner(userid == Integer.valueOf(record.getUserId()));
      return rd;
    } catch (Exception ex) {
      logger.error("userlist", ex);
      return null;
    }
  }

  private void setResultData(TurbineUser model, AccountResultData data) {
    data.setUserId(model.getUserId());
    data.setUserName(model.getLoginName());
    data.setName(getAliasName(model.getFirstName(), model.getLastName()));
    data.setNameKana(getAliasName(model.getFirstNameKana(), model
      .getLastNameKana()));
    data.setEmail(model.getEmail());
    data.setOutTelephone(model.getOutTelephone());
    data.setInTelephone(model.getInTelephone());
    data.setCellularPhone(model.getCellularPhone());
    data.setCellularMail(model.getCellularMail());
    data.setPostNameList(ALEipUtils.getPostNameList(model.getUserId()));
    data.setPositionName(ALEipUtils.getPositionName(model.getPositionId()));
    data.setDisabled(model.getDisabled());
    data.setHasPhoto("T".equals(model.getHasPhoto())
      || "N".equals(model.getHasPhoto()));
    data.setIsNewPhotoSpec("N".equals(model.getHasPhoto()));
    data.setPhotoModified(model.getPhotoModified().getTime());
    data.setCode(model.getCode());
  }

  private String getAliasName(String firstName, String lastName) {
    return new StringBuffer()
      .append(lastName)
      .append(" ")
      .append(firstName)
      .toString();
  }

  /**
   * 一覧表示します。
   *
   * @param action
   * @param rundata
   * @param context
   * @return TRUE 成功 FASLE 失敗
   */
  @Override
  public boolean doSelectList(ALAction action, RunData rundata, Context context) {
    try {
      init(action, rundata, context);
      doCheckAclPermission(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_LIST);
      ResultList<TurbineUser> resultList = selectList(rundata, context);
      if (resultList != null) {
        if (resultList.getTotalCount() > 0) {
          setPageParam(resultList.getTotalCount());
        }

        Map<Integer, Boolean> map = getAdminInfo(resultList);
        list = new ArrayList<Object>();
        for (TurbineUser model : resultList) {
          Object object = getResultData(model);
          setAdminInfo(object, map);
          if (object != null) {
            list.add(object);
          }
        }
      }
      return (list != null);
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    } catch (ALPageNotFoundException e) {
      ALEipUtils.redirectPageNotFound(rundata);
      return false;
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectDBError(rundata);
      return false;
    }

  }

  private Map<Integer, Boolean> getAdminInfo(ResultList<TurbineUser> list) {
    if (list == null || list.isEmpty()) {
      return new HashMap<Integer, Boolean>();
    }

    List<Integer> userIds = new ArrayList<Integer>();
    for (TurbineUser user : list) {
      userIds.add(user.getUserId());
    }

    Role adminrole = null;
    try {
      adminrole = JetspeedSecurity.getRole("admin");
    } catch (JetspeedSecurityException e) {
      logger.error("[UserSelectData]", e);
      return new HashMap<Integer, Boolean>();
    }
    List<TurbineUserGroupRole> roleList =
      Database
        .query(TurbineUserGroupRole.class)
        .where(
          Operations.eq(TurbineUserGroupRole.TURBINE_ROLE_PROPERTY, adminrole
            .getId()),
          Operations.in(TurbineUserGroupRole.TURBINE_USER_PROPERTY, userIds))
        .fetchList();

    if (roleList == null) {
      return new HashMap<Integer, Boolean>();
    }
    Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    for (TurbineUserGroupRole role : roleList) {
      map.put(role.getTurbineUser().getUserId(), Boolean.TRUE);
    }
    return map;

  }

  private void setAdminInfo(Object obj, Map<Integer, Boolean> map) {
    AccountResultData data = (AccountResultData) obj;
    Boolean bool = map.get(Integer.valueOf((int) data.getUserId().getValue()));
    data.setIsAdmin(bool == null ? false : bool);
  }

  /**
   * @return
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("post", "POST_ID");
    map.putValue("login_name", TurbineUser.LOGIN_NAME_PROPERTY);
    map.putValue("name_kana", TurbineUser.LAST_NAME_KANA_PROPERTY);
    map.putValue("userposition", TurbineUser.EIP_MUSER_POSITION_PROPERTY
      + "."
      + EipMUserPosition.POSITION_PROPERTY); // ユーザの順番
    return map;
  }

  /**
   *
   * @return
   */
  public String getCurrentPost() {
    return currentPost;
  }

  /**
   * @return searchWord
   */
  public ALStringField getSearchWord() {
    return searchWord;
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
   *
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * 登録ユーザー数を取得する．
   *
   * @return
   */
  public int getRegisteredUserNum() {
    return registeredUserNum;
  }

  public int getRandomNum() {
    SecureRandom random = new SecureRandom();
    return (random.nextInt() * 100);
  }

  public boolean isAdminFiltered() {
    return adminFilter;
  }

  /**
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_INSIDE;
  }

  public void clearFilter(RunData rundata, Context context) {
    ALEipUtils.removeTemp(rundata, context, LIST_FILTER_STR);
  }
}
