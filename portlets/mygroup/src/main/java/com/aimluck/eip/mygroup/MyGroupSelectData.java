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
package com.aimluck.eip.mygroup;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.mygroup.util.MyGroupUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * マイグループの検索データを管理するためのクラスです。 <br />
 */
public class MyGroupSelectData extends
    ALAbstractSelectData<TurbineGroup, TurbineGroup> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MyGroupSelectData.class.getName());

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
        .getInitParameter("p2a-sort"));
    }

    super.init(action, rundata, context);
  }

  /**
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<TurbineGroup> selectList(RunData rundata, Context context) {
    try {

      SelectQuery<TurbineGroup> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      return query.getResultList();
    } catch (Exception ex) {
      logger.error("mygroup", ex);
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
  private SelectQuery<TurbineGroup> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<TurbineGroup> query = Database.query(TurbineGroup.class);

    Expression exp =
      ExpressionFactory.matchExp(TurbineGroup.OWNER_ID_PROPERTY, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    query.setQualifier(exp);

    return query;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected TurbineGroup selectDetail(RunData rundata, Context context) {
    return MyGroupUtils.getGroup(rundata, context);
  }

  /**
   * @param obj
   * @return
   * 
   */
  @Override
  protected Object getResultData(TurbineGroup record) {
    MyGroupResultData rd = new MyGroupResultData();
    rd.initField();
    rd.setGroupName(record.getGroupName());
    rd.setGroupAliasName(record.getGroupAliasName());
    return rd;
  }

  /**
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(TurbineGroup record) {
    MyGroupResultData rd = new MyGroupResultData();
    rd.initField();
    rd.setGroupName(record.getGroupName());
    rd.setGroupAliasName(record.getGroupAliasName());
    return rd;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("group_alias_name", TurbineGroup.GROUP_ALIAS_NAME_PROPERTY);
    return map;
  }

  /**
   * 
   * @param name
   * @return
   */
  public List<ALEipUser> getMemberList(String name) {
    return ALEipUtils.getUsers(name);
  }

  /**
   * 
   * @param name
   * @return
   */
  public List<FacilityResultData> getFacilityList(String groupname) {
    return FacilitiesUtils.getFacilityList(groupname);
  }
}
