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
package com.aimluck.eip.webmail;

import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.access.DataContext;
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
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.mail.ALMailReceiverContext;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * Webメールアカウントの検索データを管理するためのクラスです。 <br />
 */
public class WebMailAccountSelectData extends ALAbstractSelectData {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebMailAccountSelectData.class.getName());

  private String org_id;

  private DataContext dataContext;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR,
          ALEipUtils.getPortlet(rundata, context).getPortletConfig()
              .getInitParameter("p2b-sort"));
    }

    org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
    dataContext = DatabaseOrmService.getInstance().getDataContext();

    super.init(action, rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List selectList(RunData rundata, Context context) {
    try {
      SelectQuery query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List list = query.perform();
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
  private SelectQuery getSelectQuery(RunData rundata, Context context) {
    SelectQuery query = new SelectQuery(EipMMailAccount.class);

    Expression exp1 = ExpressionFactory.matchExp(
        EipMMailAccount.USER_ID_PROPERTY,
        Integer.valueOf(ALEipUtils.getUserId(rundata)));
    query.setQualifier(exp1);
    Expression exp2 = ExpressionFactory.noMatchExp(
        EipMMailAccount.ACCOUNT_TYPE_PROPERTY,
        Integer.valueOf(ALMailUtils.ACCOUNT_TYPE_INIT));
    query.andQualifier(exp2);

    return query;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected Object selectDetail(RunData rundata, Context context) {
    int userId = ALEipUtils.getUserId(rundata);

    EipMMailAccount account = null;
    if (userId == 1) {
      // 管理者用のメールアカウントを表示する場合
      account = ALMailUtils.getEipMMailAccountForAdmin();
    } else {
      int accountId = rundata.getParameters().getInt(WebMailUtils.ACCOUNT_ID,
          -1);
      account = ALMailUtils.getMailAccount(null, userId, accountId);
    }
    return account;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(Object obj) {
    try {
      EipMMailAccount record = (EipMMailAccount) obj;
      WebMailAccountResultData rd = new WebMailAccountResultData();
      rd.initField();
      rd.setAccountId(record.getAccountId().intValue());
      rd.setAccountName(record.getAccountName());
      rd.setMailAddress(record.getMailAddress());

      // 未読メール数を取得し，セットする．
      ALMailHandler handler = ALMailFactoryService.getInstance()
          .getMailHandler();
      ALMailReceiverContext rcontext = ALMailUtils
          .getALPop3MailReceiverContext(org_id, record);
      rd.setCountUnRead(handler.getUnReadMailSum(rcontext));

      // 最終更新日を取得し，セットする．
      Date finalAccessDate = record.getLastReceivedDate();
      rd.setFinalAccessDate(ALMailUtils.translateDate(finalAccessDate));

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(Object obj) {
    if (obj == null)
      return null;
    try {
      EipMMailAccount record = (EipMMailAccount) obj;

      WebMailAccountDetailResultData rd = new WebMailAccountDetailResultData();
      rd.initField();
      rd.setAccountId(record.getAccountId().intValue());
      rd.setAccountName(record.getAccountName());
      rd.setSmtpserverName(record.getSmtpserverName());
      rd.setPop3serverName(record.getPop3serverName());
      rd.setPop3userName(record.getPop3userName());
      rd.setPop3Password("******");
      rd.setMailUserName(record.getMailUserName());
      rd.setMailAddress(record.getMailAddress());
      rd.setSmtpPort(Integer.parseInt(record.getSmtpPort()));
      rd.setPop3Port(Integer.parseInt(record.getPop3Port()));
      rd.setAuthSendFlg(Short.valueOf(record.getAuthSendFlg()));
      rd.setAuthSendUserId(record.getAuthSendUserId());
      rd.setAuthSendUserPassword("******");
      rd.setSignature(record.getSignature());
      rd.setPop3EncryptionFlag(record.getPop3EncryptionFlg());
      rd.setSmtpEncryptionFlag(record.getSmtpEncryptionFlg());
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
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("account_name", EipMMailAccount.ACCOUNT_NAME_PROPERTY);
    return map;
  }

}
