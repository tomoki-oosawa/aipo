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
package com.aimluck.eip.exttimecard;

import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystemMap;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * ユーザーの勤務形態を一括変更するためのクラス． <BR>
 * 
 */
public class ExtTimecardSystemMapMultiChange extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardSystemMapMultiChange.class.getName());

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
      DataContext.getThreadDataContext().unregisterObjects(
        DataContext.getThreadDataContext().uncommittedObjects());

      Expression exp =
        ExpressionFactory.inExp(
          EipTExtTimecardSystemMap.USER_ID_PROPERTY,
          values);
      SelectQuery<EipTExtTimecardSystemMap> query =
        Database.query(EipTExtTimecardSystemMap.class, exp);
      List<EipTExtTimecardSystemMap> ulist = query.fetchList();

      String system_id = rundata.getParameters().get("system_id");
      EipTExtTimecardSystem to_system =
        ExtTimecardUtils.getEipTExtTimecardSystemById(Integer
          .valueOf(system_id));
      if (to_system == null) {
        return false;
      }

      int size = ulist.size();
      for (int i = 0; i < size; i++) {
        EipTExtTimecardSystemMap record = ulist.get(i);
        record.setEipTExtTimecardSystem(to_system);
        record.setUpdateDate(Calendar.getInstance().getTime());
        values.remove(record.getUserId().toString());
      }

      size = values.size();
      for (int i = 0; i < size; i++) {
        EipTExtTimecardSystemMap record =
          Database.create(EipTExtTimecardSystemMap.class);
        String user_id = values.get(i);
        record.setUserId(Integer.valueOf(user_id));
        record.setEipTExtTimecardSystem(to_system);
        record.setCreateDate(Calendar.getInstance().getTime());
        record.setUpdateDate(Calendar.getInstance().getTime());
      }
      Database.commit();
      return true;
    } catch (Exception e) {
      Database.rollback();
      logger.error("exttimecard", e);
      return false;
    }
  }
}
