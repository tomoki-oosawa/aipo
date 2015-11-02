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
package com.aimluck.eip.webmail;

import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 *
 *
 */
public class CellWebMailAccountSelectData extends
    ALAbstractSelectData<EipMMailAccount, EipMMailAccount> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellWebMailAccountSelectData.class.getName());

  private int userId;

  private int accountId;

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

    userId = ALEipUtils.getUserId(rundata);
    try {
      accountId =
        Integer.valueOf(ALEipUtils.getTemp(
          rundata,
          context,
          WebMailUtils.ACCOUNT_ID));
    } catch (Exception e) {
      accountId = 0;
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
  protected ResultList<EipMMailAccount> selectList(RunData rundata,
      Context context) {
    try {
      SelectQuery<EipMMailAccount> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      return query.getResultList();
    } catch (Exception ex) {
      logger.error("webmail", ex);
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
  private SelectQuery<EipMMailAccount> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMMailAccount> query = Database.query(EipMMailAccount.class);

    Expression exp1 =
      ExpressionFactory.matchExp(EipMMailAccount.USER_ID_PROPERTY, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    Expression exp2 =
      ExpressionFactory.noMatchExp(
        EipMMailAccount.ACCOUNT_TYPE_PROPERTY,
        Integer.valueOf(ALMailUtils.ACCOUNT_TYPE_INIT));

    query.setQualifier(exp1.andExp(exp2));
    query.orderDesending(EipMMailAccount.ACCOUNT_TYPE_PROPERTY);

    return query;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipMMailAccount selectDetail(RunData rundata, Context context) {
    return ALMailUtils.getMailAccount(userId, accountId);
  }

  /**
   *
   */
  @Override
  protected Object getResultData(EipMMailAccount record) {
    try {
      WebMailAccountResultData rd = new WebMailAccountResultData();
      rd.initField();
      rd.setAccountId(record.getAccountId().intValue());
      rd.setAccountName(record.getAccountName());
      rd.setMailAddress(record.getMailAddress());
      rd.setAccountType(record.getAccountType());
      return rd;
    } catch (Exception ex) {
      logger.error("webmail", ex);
      return null;
    }
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipMMailAccount record) {
    return null;
  }

  /**
   * @return
   * 
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("account_name", EipMMailAccount.ACCOUNT_NAME_PROPERTY);
    return map;
  }

}
