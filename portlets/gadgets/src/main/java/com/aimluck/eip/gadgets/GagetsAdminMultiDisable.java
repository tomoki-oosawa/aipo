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
package com.aimluck.eip.gadgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMInactiveApplication;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.social.ALApplicationService;

/**
 *
 */
public class GagetsAdminMultiDisable extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GagetsAdminMultiDisable.class.getName());

  /**
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      ArrayList<String> arrayList = new ArrayList<String>();
      ArrayList<String> arrayList2 = new ArrayList<String>();

      GadgetsAdminSelectData selectData = new GadgetsAdminSelectData();
      selectData.initField();
      ResultList<PortletEntry> list =
        selectData.selectListPortletEntry(rundata, context);
      for (PortletEntry entry : list) {
        for (String portlet : values) {
          if (portlet.equals(entry.getName())) {
            arrayList.add(portlet);
          }
        }
      }

      for (String portlet : arrayList) {

        SelectQuery<EipMInactiveApplication> query =
          Database.query(EipMInactiveApplication.class);
        Expression exp =
          ExpressionFactory.matchExp(
            EipMInactiveApplication.NAME_PROPERTY,
            portlet);
        query.setQualifier(exp);
        List<EipMInactiveApplication> entrys = query.fetchList();
        if (entrys == null || entrys.size() == 0) {
          EipMInactiveApplication entry =
            Database.create(EipMInactiveApplication.class);
          entry.setName(portlet);
        }
      }

      Database.commit();

      for (String portlet : values) {
        if (!arrayList.contains(portlet)) {
          arrayList2.add(portlet);
        }
      }

      ALApplicationService.disable(arrayList2);
    } catch (Throwable t) {
      logger.error(t, t);
      return false;
    }
    return true;
  }

}
