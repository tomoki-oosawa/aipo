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
package com.aimluck.eip.facilities;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroupMap;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 設備の複数削除を行うためのクラスです。 <BR>
 * 
 */
public class FacilityMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FacilityMultiDelete.class.getName());

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

      // delete schedule maps
      SelectQuery<EipTScheduleMap> query1 =
        Database.query(EipTScheduleMap.class);
      Expression exp1 =
        ExpressionFactory.inExp(EipTScheduleMap.USER_ID_PROPERTY, values);
      Expression exp2 =
        ExpressionFactory.matchExp(EipTScheduleMap.TYPE_PROPERTY, "F");
      query1.setQualifier(exp1.andExp(exp2));

      List<EipTScheduleMap> slist = query1.fetchList();
      if (slist != null && slist.size() > 0) {
        Database.deleteAll(slist);
      }

      // delete facilities
      SelectQuery<EipMFacility> query = Database.query(EipMFacility.class);
      Expression exp =
        ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, values);
      query.setQualifier(exp);

      List<EipMFacility> flist = query.fetchList();
      if (flist == null || flist.size() == 0) {
        return false;
      }
      Database.deleteAll(flist);

      // delete maps
      SelectQuery<EipMFacilityGroupMap> fmaps =
        Database.query(EipMFacilityGroupMap.class);
      fmaps.where(Operations.in(
        EipMFacilityGroupMap.FACILITY_ID_PROPERTY,
        values));
      fmaps.deleteAll();

      Database.commit();

      for (EipMFacility facility : flist) {
        // イベントログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          facility.getFacilityId(),
          ALEventlogConstants.PORTLET_TYPE_FACILITY,
          ALLocalizationUtils.getl10nFormat(
            "FACILITIES_DELETED_FACILITY_WITH_NAME",
            facility.getFacilityName()));
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("facilities", ex);
      return false;
    }
    return true;
  }

}
