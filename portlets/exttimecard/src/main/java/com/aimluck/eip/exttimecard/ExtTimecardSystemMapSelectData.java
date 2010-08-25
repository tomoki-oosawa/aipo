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
public class ExtTimecardSystemMapSelectData extends ALAbstractSelectData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardSystemMapSelectData.class.getName());

  /** システムの総数 */
  private int mapSum;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    String sorttype = ALEipUtils.getTemp(rundata, context, LIST_SORT_TYPE_STR);
    if (sort == null || sort.equals("")) {
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
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected ResultList<EipTExtTimecardSystemMap> selectList(RunData rundata,
      Context context) {
    try {

      String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
      SelectQuery<TurbineUser> query = getSelectQueryForUser(rundata, context);
      buildSelectQueryForListView(query);
      /** パラメータにソート文字列が指定されていなければソートを行わない */
      if (sort == null || "".equals(sort)) {
      } else {
        buildSelectQueryForListViewSort(query, rundata, context);
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
  private SelectQuery<EipTExtTimecardSystemMap> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTExtTimecardSystemMap> query =
      Database.query(EipTExtTimecardSystemMap.class);
    return buildSelectQueryForFilter(query, rundata, context);
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
      ExpressionFactory.noMatchExp(TurbineUser.DISABLED_PROPERTY, "T");

    query.setQualifier(exp1);
    query.andQualifier(exp2);
    query.andQualifier(exp3);

    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);

    Map<Integer, ALEipPost> gMap = ALEipManager.getInstance().getPostMap();
    if (filter == null
      || "".equals(filter)
      || !gMap.containsKey(Integer.valueOf(filter))) {
      return query;
    }
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
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected Object selectDetail(RunData rundata, Context context) {
    return ExtTimecardUtils.getEipTExtTimecardSystem(rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  @Override
  protected Object getResultData(Object obj) {
    try {
      EipTExtTimecardSystemMap record = (EipTExtTimecardSystemMap) obj;
      ExtTimecardSystemMapResultData rd = new ExtTimecardSystemMapResultData();
      rd.initField();
      int userid = record.getUserId();
      ALEipUser user = ALEipUtils.getALEipUser(userid);
      rd.setUserId(userid);
      rd.setName(user.getAliasName().getValue());
      rd.setLoginName(user.getName().toString());
      rd.setSystemId(record.getEipTExtTimecardSystem().getSystemId());
      rd.setSystemName(record.getEipTExtTimecardSystem().getSystemName());
      rd.setPostNameList(AccountUtils.getPostBeanList(userid));

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(Object obj) {
    try {
      EipTExtTimecardSystemMap record = (EipTExtTimecardSystemMap) obj;
      ExtTimecardSystemMapDetailResultData rd =
        new ExtTimecardSystemMapDetailResultData();
      rd.initField();
      rd.setSystemMapId(record.getSystemMapId());
      int userid = record.getUserId();
      ALEipUser user = ALEipUtils.getALEipUser(userid);
      rd.setUserId(userid);
      rd.setName(user.getAliasName().getValue());
      rd.setLoginName(user.getName().toString());
      rd.setSystemId(record.getEipTExtTimecardSystem().getSystemId());
      rd.setSystemName(record.getEipTExtTimecardSystem().getSystemName());
      rd.setPostNameList(AccountUtils.getPostBeanList(userid));
      rd.setCreateDate(record.getCreateDate().toString());
      rd.setUpdateDate(record.getUpdateDate().toString());

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("name_kana", TurbineUser.LAST_NAME_KANA_PROPERTY);
    return map;
  }

  public int getSystemSum() {
    return mapSum;
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
