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

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * ユーザアカウントを複数削除するためのクラス． <BR>
 *
 */
public class AccountUserMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountUserMultiDelete.class.getName());

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

      Expression exp =
        ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, values);
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class, exp);
      List<TurbineUser> ulist = query.fetchList();
      if (ulist == null || ulist.size() == 0) {
        return false;
      }

      return AccountUtils.deleteUserList(rundata, ulist, msgList);
    } catch (RuntimeException e) {
      Database.rollback();
      logger.error("AccountUserMultiDelete.action", e);
      return false;
    } catch (Exception e) {
      Database.rollback();
      logger.error("AccountUserMultiDelete.action", e);
      return false;
    }
  }
}
