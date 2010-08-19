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
package com.aimluck.eip.account;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 役職の検索用データクラスです。
 * 
 */
public class AccountPositionSelectData extends
    ALAbstractSelectData<EipMPosition, EipMPosition> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPositionSelectData.class.getName());

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
        .getPortlet(rundata, context).getPortletConfig().getInitParameter(
          "p1b-sort"));
    }

    super.init(action, rundata, context);
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected List<EipMPosition> selectList(RunData rundata, Context context) {
    try {
      SelectQuery<EipMPosition> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List<EipMPosition> list = query.fetchList();
      return buildPaginatedList(list);

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
  private SelectQuery<EipMPosition> getSelectQuery(RunData rundata,
      Context context) {
    return new SelectQuery<EipMPosition>(EipMPosition.class);
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected EipMPosition selectDetail(RunData rundata, Context context) {
    return AccountUtils.getEipMPosition(rundata, context);
  }

  /**
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  @Override
  protected Object getResultData(EipMPosition record) {
    AccountPositionResultData rd = new AccountPositionResultData();
    rd.initField();
    rd.setPositionId(record.getPositionId().intValue());
    rd.setPositionName(record.getPositionName());
    return rd;
  }

  /**
   * @param obj
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(EipMPosition record) {
    AccountPositionResultData rd = new AccountPositionResultData();
    rd.initField();
    rd.setPositionId(record.getPositionId().intValue());
    rd.setPositionName(record.getPositionName());
    return rd;
  }

  /**
   * @return
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("position_name", EipMPosition.POSITION_NAME_PROPERTY);
    return map;
  }

}
