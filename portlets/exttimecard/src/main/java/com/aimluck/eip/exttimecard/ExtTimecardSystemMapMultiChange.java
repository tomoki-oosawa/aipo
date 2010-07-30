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
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystem;
import com.aimluck.eip.cayenne.om.portlet.EipTExtTimecardSystemMap;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.orm.DatabaseOrmService;

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
   * @see com.aimluck.eip.common.ALAbstractCheckList#action(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList,
   *      java.util.ArrayList)
   */
  protected boolean action(RunData rundata, Context context, List<String> values,
      List<String> msgList) {

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      dataContext.unregisterObjects(dataContext.uncommittedObjects());

      Expression exp = ExpressionFactory.inExp(
          EipTExtTimecardSystemMap.USER_ID_PROPERTY, values);
      SelectQuery query = new SelectQuery(EipTExtTimecardSystemMap.class, exp);
      List ulist = dataContext.performQuery(query);

      String system_id = rundata.getParameters().get("system_id");
      EipTExtTimecardSystem to_system = ExtTimecardUtils
          .getEipTExtTimecardSystemById(Integer.valueOf(system_id));
      if (to_system == null) {
        return false;
      }

      int size = ulist.size();
      for (int i = 0; i < size; i++) {
        EipTExtTimecardSystemMap record = (EipTExtTimecardSystemMap) ulist
            .get(i);
        record.setEipTExtTimecardSystem(to_system);
        record.setUpdateDate(Calendar.getInstance().getTime());
        values.remove(record.getUserId().toString());
      }

      size = values.size();
      for (int i = 0; i < size; i++) {
        EipTExtTimecardSystemMap record = (EipTExtTimecardSystemMap) dataContext
            .createAndRegisterNewObject(EipTExtTimecardSystemMap.class);
        String user_id = (String) values.get(i);
        record.setUserId(Integer.valueOf(user_id));
        record.setEipTExtTimecardSystem(to_system);
        record.setCreateDate(Calendar.getInstance().getTime());
        record.setUpdateDate(Calendar.getInstance().getTime());
      }
      dataContext.commitChanges();
      return true;
    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
  }
}
