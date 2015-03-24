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
package com.aimluck.eip.eventlog;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTEventlog;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * イベントログの複数削除を行うためのクラスです。 <BR>
 * 
 */
public class EventlogMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(EventlogMultiDelete.class.getName());

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

      SelectQuery<EipTEventlog> query = Database.query(EipTEventlog.class);
      Expression exp =
        ExpressionFactory.inDbExp(EipTEventlog.EVENTLOG_ID_PK_COLUMN, values);
      query.setQualifier(exp);

      List<EipTEventlog> logs = query.fetchList();
      if (logs == null || logs.size() == 0) {
        return false;
      }

      // イベントログを削除
      Database.deleteAll(logs);
      Database.commit();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("eventlog", ex);
      return false;
    }
    return true;
  }

  /**
   * @param action
   * @param rundata
   * @param context
   * @return
   */
  public boolean doAllDelete(ALAction action, RunData rundata, Context context) {
    try {
      if (!doCheckSecurity(rundata, context)) {
        return false;
      }
      doCheckAclPermission(rundata, context, getDefineAclType());

      List<String> msgList = new ArrayList<String>();
      boolean res = false;

      res = allDeleteAction(rundata, context, msgList);

      action.setResultData(this);
      action.setErrorMessages(msgList);
      action.putData(rundata, context);
      return res;
    } catch (ALPermissionException e) {
      ALEipUtils.redirectPermissionError(rundata);
      return false;
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  protected boolean allDeleteAction(RunData rundata, Context context,
      List<String> msgList) {
    try {

      String sql = "DELETE FROM eip_t_eventlog WHERE EVENTLOG_ID > 0";
      Database.sql(EipTEventlog.class, sql).execute();

      Database.commit();
    } catch (RuntimeException ex) {
      Database.rollback();
      logger.error("eventlog", ex);
      return false;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("eventlog", ex);
      return false;
    }
    return true;
  }

}
