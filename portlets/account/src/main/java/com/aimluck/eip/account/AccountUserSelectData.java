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
package com.aimluck.eip.account;

import java.security.SecureRandom;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.account.util.AccountUtils.FilterRole;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザーアカウントの検索データを管理するためのクラスです。 <br />
 *
 */
public class AccountUserSelectData extends
    ALAbstractSelectData<TurbineUser, ALBaseUser> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserSelectData.class.getName());

  /** 現在表示している部署 */
  protected String currentPost = "";

  /** 現在表示しているユーザー */
  protected String currentRole = "";

  protected int registeredUserNum = 0;

  protected static final String LIST_FILTER_ROLE = "filter_role";

  protected final String LIST_FILTER_ROLE_STR = new StringBuffer().append(
    this.getClass().getName()).append(LIST_FILTER_ROLE).toString();

  private boolean adminFilter;

  protected ALStringField target_keyword;

  /** <code>userid</code> ユーザーID */
  private int userid;

  /**
   * 初期化します。
   *
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "userposition");
    }

    if (rundata.getParameters().containsKey(LIST_FILTER_ROLE)) {
      ALEipUtils.setTemp(rundata, context, LIST_FILTER_ROLE_STR, rundata
        .getParameters()
        .getString(LIST_FILTER_ROLE));
    }

    target_keyword = new ALStringField();

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
      logger.error("AccountUserSelectData.selectList", ex);
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

    target_keyword.setValue(AccountUtils.getTargetKeyword(rundata, context));
    currentPost = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    currentRole = ALEipUtils.getTemp(rundata, context, LIST_FILTER_ROLE_STR);

    SelectQuery<TurbineUser> query =
      AccountUtils.getSelectQuery(
        target_keyword.getValue(),
        currentPost,
        currentRole);
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
    String filter_role =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_ROLE_STR);

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

    currentPost = filter;
    currentRole = filter_role;
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
  protected ALBaseUser selectDetail(RunData rundata, Context context) {
    return AccountUtils.getBaseUser(rundata, context);
  }

  /**
   * @param obj
   * @return
   *
   */
  @Override
  protected Object getResultData(TurbineUser record) {
    try {

      AccountResultData rd = new AccountResultData();
      rd.initField();
      rd.setUserId(record.getUserId().intValue());
      rd.setUserName(record.getLoginName());
      rd.setName(new StringBuffer()
        .append(record.getLastName())
        .append(" ")
        .append(record.getFirstName())
        .toString());
      rd.setPostNameList(ALEipUtils.getPostNameList(record.getUserId()));
      rd.setPositionName(ALEipUtils.getPositionName(record
        .getPositionId()
        .intValue()));
      rd.setDisabled(record.getDisabled());
      rd.setHasPhoto("T".equals(record.getHasPhoto())
        || "N".equals(record.getHasPhoto()));
      rd.setPhotoModified(record.getPhotoModified().getTime());
      rd.setEmail(record.getEmail());

      return rd;
    } catch (Exception ex) {
      logger.error("AccountUserSelectData.getResultData", ex);
      return null;
    }
  }

  /**
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(ALBaseUser record) {
    try {
      Integer id = Integer.valueOf(record.getUserId());
      AccountResultData rd = new AccountResultData();
      rd.initField();
      rd.setUserId(Integer.valueOf(record.getUserId()).intValue());
      rd.setUserName(record.getUserName());
      rd.setName(new StringBuffer()
        .append(record.getLastName())
        .append(" ")
        .append(record.getFirstName())
        .toString());
      rd.setNameKana(new StringBuffer()
        .append(record.getLastNameKana())
        .append(" ")
        .append(record.getFirstNameKana())
        .toString());
      rd.setEmail(record.getEmail());
      rd.setOutTelephone(record.getOutTelephone());
      rd.setInTelephone(record.getInTelephone());
      rd.setCellularPhone(record.getCellularPhone());
      rd.setCellularMail(record.getCellularMail());
      rd.setPostNameList(ALEipUtils.getPostNameList(id.intValue()));
      rd.setPositionName(ALEipUtils.getPositionName(record.getPositionId()));
      rd.setDisabled(record.getDisabled());
      rd.setIsAdmin(ALEipUtils.isAdmin(Integer.valueOf(record.getUserId())));
      rd.setHasPhoto(record.hasPhoto());
      rd.setIsNewPhotoSpec("N".equals(record.hasPhotoString()));
      rd.setPhotoModified(record.getPhotoModified().getTime());
      rd.setIsOwner(userid == Integer.valueOf(record.getUserId()));
      rd.setCode(record.getCode());

      return rd;
    } catch (Exception ex) {
      logger.error("AccountUserSelectData.getResultDataDetail", ex);
      return null;
    }
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
  public Map<Integer, FilterRole> getRoleMap() {
    return AccountUtils.getRoleMap();
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
   * @return currentRole
   */
  public String getCurrentRole() {
    return currentRole;
  }

  public ALStringField getTargetKeyword() {
    return target_keyword;
  }
}
