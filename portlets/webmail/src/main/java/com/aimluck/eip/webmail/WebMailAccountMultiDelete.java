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

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMMailAccount;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.mail.ALMailFactoryService;
import com.aimluck.eip.mail.ALMailHandler;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.webmail.util.WebMailUtils;

/**
 * 複数のWebメールアカウントを削除するクラスです。 <br />
 */
public class WebMailAccountMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WebMailAccountMultiDelete.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {

    try {
      String orgId = Database.getDomainName();
      int uid = ALEipUtils.getUserId(rundata);

      // アカウントを削除する．
      SelectQuery<EipMMailAccount> query =
        Database.query(EipMMailAccount.class);
      Expression exp1 =
        ExpressionFactory.matchExp(EipMMailAccount.USER_ID_PROPERTY, Integer
          .valueOf(uid));
      Expression exp2 =
        ExpressionFactory.inDbExp(EipMMailAccount.ACCOUNT_ID_PK_COLUMN, values);

      List<EipMMailAccount> accounts =
        query.setQualifier(exp1.andExp(exp2)).fetchList();
      Database.deleteAll(accounts);

      // ローカルフォルダを削除する．
      String accountId =
        ALEipUtils.getTemp(rundata, context, WebMailUtils.ACCOUNT_ID);
      if (accountId == null) {
        return false;
      }

      // セッション変数を削除する
      WebMailUtils.clearWebMailAccountSession(rundata, context);

      ALMailHandler handler =
        ALMailFactoryService.getInstance().getMailHandler();
      handler.removeAccount(orgId, ALEipUtils.getUserId(rundata), Integer
        .parseInt(accountId));

    } catch (Exception ex) {
      logger.error("webmail", ex);
      return false;
    }
    return true;
  }
}
