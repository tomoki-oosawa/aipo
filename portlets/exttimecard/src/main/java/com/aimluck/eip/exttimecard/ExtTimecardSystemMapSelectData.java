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
package com.aimluck.eip.exttimecard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystemMap;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タイムカード集計の検索データを管理するためのクラスです。 <br />
 * 
 */
public class ExtTimecardSystemMapSelectData extends
    ALAbstractSelectData<EipTExtTimecardSystemMap, EipTExtTimecardSystem> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardSystemMapSelectData.class.getName());

  /** システムの総数 */
  private int mapSum;

  /** <code>viewtype</code> 表示タイプ */
  protected String viewtype = "user";

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    String sorttype = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "userposition");
    }

    if ("name_kana".equals(ALEipUtils.getTemp(rundata, context, LIST_SORT_STR))
      && (sorttype == null || "".equals(sorttype))) {
      ALEipUtils.setTemp(
        rundata,
        context,
        LIST_SORT_TYPE_STR,
        ALEipConstants.LIST_SORT_TYPE_DESC);
    }

    if ("system_name".equals(ALEipUtils
      .getTemp(rundata, context, LIST_SORT_STR))
      && (sorttype == null || "".equals(sorttype))) {
      ALEipUtils.setTemp(
        rundata,
        context,
        LIST_SORT_TYPE_STR,
        ALEipConstants.LIST_SORT_TYPE_DESC);
    }
    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTExtTimecardSystemMap> selectList(RunData rundata,
      Context context) {
    try {

      String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
      String sort_type =
        ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
      SelectQuery<TurbineUser> query = getSelectQueryForUser(rundata, context);
      query.limit(getRowsNum());
      query.page(getCurrentPage());
      /** パラメータにソート文字列が指定されていなければソートを行わない */
      if (sort == null || "".equals(sort)) {
      } else {
        String crt_key = null;

        Attributes map = getColumnMap();
        crt_key = map.getValue(sort);
        if (crt_key != null) {
          if (sort_type != null
            && ALEipConstants.LIST_SORT_TYPE_DESC.equals(sort_type)) {
            query.orderDesending(crt_key);
          } else {
            query.orderAscending(crt_key);
            sort_type = ALEipConstants.LIST_SORT_TYPE_ASC;
          }
          current_sort = sort;
          current_sort_type = sort_type;
        }
      }
      EipTExtTimecardSystem default_system =
        ExtTimecardUtils.getEipTExtTimecardSystemById(1);
      ResultList<TurbineUser> list = query.getResultList();
      List<EipTExtTimecardSystemMap> select_list =
        new ArrayList<EipTExtTimecardSystemMap>();
      mapSum = list.size();
      for (int i = 0; i < mapSum; i++) {
        TurbineUser user = list.get(i);
        SelectQuery<EipTExtTimecardSystemMap> map_query =
          Database.query(EipTExtTimecardSystemMap.class);
        Expression exp =
          ExpressionFactory.matchExp(
            EipTExtTimecardSystemMap.USER_ID_PROPERTY,
            Integer.valueOf(user.getUserId()));
        map_query.setQualifier(exp);
        ResultList<EipTExtTimecardSystemMap> map_list =
          map_query.getResultList();
        if (map_list.size() == 0) {
          EipTExtTimecardSystemMap dummy_map = new EipTExtTimecardSystemMap();
          dummy_map.setUserId(user.getUserId());
          dummy_map.setEipTExtTimecardSystem(default_system);
          select_list.add(dummy_map);
        } else {
          select_list.add(map_list.get(0));
        }
      }
      return new ResultList<EipTExtTimecardSystemMap>(
        select_list,
        getPagesNum(),
        getRowsNum(),
        list.getTotalCount());
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return null;
    }
  }

  private SelectQuery<TurbineUser> getSelectQueryForUser(RunData rundata,
      Context context) {
    SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);

    ObjectId oid =
      new ObjectId("TurbineUser", TurbineUser.USER_ID_PK_COLUMN, 3);
    Expression exp1 =
      ExpressionFactory.matchAllDbExp(
        oid.getIdSnapshot(),
        Expression.GREATER_THAN);
    Expression exp2 =
      ExpressionFactory.matchExp(TurbineUser.COMPANY_ID_PROPERTY, Integer
        .valueOf(1));
    Expression exp3 =
      ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");

    query.setQualifier(exp1);
    query.andQualifier(exp2);
    query.andQualifier(exp3);

    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    String filter_type =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_TYPE_STR);

    Map<Integer, ALEipPost> gMap = ALEipManager.getInstance().getPostMap();
    if (filter == null
      || "".equals(filter)
      || !gMap.containsKey(Integer.valueOf(filter))) {
      return query;
    }

    current_filter = filter;
    current_filter_type = filter_type;

    String groupName =
      (ALEipManager.getInstance().getPostMap().get(Integer.valueOf(filter)))
        .getGroupName()
        .getValue();

    Expression exp4 =
      ExpressionFactory.matchExp(TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
        + "."
        + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
        + "."
        + TurbineGroup.GROUP_NAME_PROPERTY, groupName);
    query.andQualifier(exp4);

    return query;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipTExtTimecardSystem selectDetail(RunData rundata, Context context) {
    return ExtTimecardUtils.getEipTExtTimecardSystem(rundata, context);
  }

  /**
   *
   */
  @Override
  protected Object getResultData(EipTExtTimecardSystemMap record) {
    try {
      ExtTimecardSystemMapResultData rd = new ExtTimecardSystemMapResultData();
      rd.initField();
      int userid = record.getUserId();
      ALEipUser user = ALEipUtils.getALEipUser(userid);
      rd.setUserId(userid);
      rd.setName(user.getAliasName().getValue());
      rd.setLoginName(user.getName().toString());
      if (record.getEipTExtTimecardSystem() != null) {
        rd.setSystemId(record.getEipTExtTimecardSystem().getSystemId());
        rd.setSystemName(record.getEipTExtTimecardSystem().getSystemName());
      }
      rd.setPostNameList(AccountUtils.getPostBeanList(userid));

      return rd;
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return null;
    }
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTExtTimecardSystem record) {
    return null;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("name_kana", TurbineUser.LAST_NAME_KANA_PROPERTY);
    map.putValue("userposition", TurbineUser.EIP_MUSER_POSITION_PROPERTY
      + "."
      + EipMUserPosition.POSITION_PROPERTY); // ユーザの順番
    return map;
  }

  public int getSystemSum() {
    return mapSum;
  }

  /**
   * 表示タイプを取得します。
   * 
   * @return
   */
  public String getViewtype() {
    return viewtype;
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  public List<EipTExtTimecardSystem> getSystemList() {
    return ExtTimecardUtils.getAllEipTExtTimecardSystem();
  }
}
