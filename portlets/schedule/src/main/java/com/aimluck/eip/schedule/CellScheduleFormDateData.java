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
package com.aimluck.eip.schedule;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipTSchedule;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.schedule.util.ScheduleUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * スケジュールのフォームデータを管理するクラスです。
 * 
 */
public class CellScheduleFormDateData extends AbstractCellScheduleFormData {

  /** <code>logger</code> logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleFormDateData.class.getName());

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String entityId =
      ALEipUtils.getParameter(rundata, context, ALEipConstants.ENTITY_ID);
    if (entityId == null) {
      ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);
    }

    super.init(action, rundata, context);
  }

  @Override
  protected void loadCustomFormData(EipTSchedule record) {
    loadEditFlag(record);

    loadMemberAndFacility(record);
  }

  private void loadEditFlag(EipTSchedule record) {
    // 共有メンバーによる編集／削除フラグ
    if ("T".equals(record.getEditFlag())) {
      if (isOwner()) {
        form_data.getEditFlag().setValue(record.getEditFlag());
      } else {
        // スケジュールの登録ユーザがすでにメンバーから抜けているかを検証する．
        int createUserId = record.getOwnerId().intValue();
        boolean inculudeCreateUser = false;
        @SuppressWarnings("unchecked")
        List<EipTScheduleMap> scheduleMaps = record.getEipTScheduleMaps();
        for (EipTScheduleMap map : scheduleMaps) {
          if (createUserId == map.getUserId().intValue()
            && !"R".equals(map.getStatus())) {
            inculudeCreateUser = true;
            break;
          }
        }
        if (inculudeCreateUser) {
          form_data.getEditFlag().setValue("F");
        } else {
          form_data.getEditFlag().setValue("T");
        }
      }
    } else {
      form_data.getEditFlag().setValue("F");
    }
  }

  private void loadMemberAndFacility(EipTSchedule record) {
    // このスケジュールを共有しているメンバーを取得
    SelectQuery<EipTScheduleMap> mapquery =
      Database.query(EipTScheduleMap.class);
    Expression mapexp =
      ExpressionFactory.matchExp(EipTScheduleMap.SCHEDULE_ID_PROPERTY, record
        .getScheduleId());
    mapquery.setQualifier(mapexp);
    List<EipTScheduleMap> list = mapquery.fetchList();
    List<Integer> users = new ArrayList<Integer>();
    List<Integer> facilityIds = new ArrayList<Integer>();
    int size = list.size();
    for (int i = 0; i < size; i++) {
      EipTScheduleMap map = list.get(i);
      if (ScheduleUtils.SCHEDULEMAP_TYPE_USER.equals(map.getType())) {
        users.add(map.getUserId());
      } else {
        facilityIds.add(map.getUserId());
      }
    }

    if (users.size() > 0) {
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, users);
      query.setQualifier(exp);
      form_data.getMemberList().addAll(
        ALEipUtils.getUsersFromSelectQuery(query));
    }
    if (facilityIds.size() > 0) {
      SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
      Expression fexp =
        ExpressionFactory.inDbExp(
          EipMFacility.FACILITY_ID_PK_COLUMN,
          facilityIds);
      fquery.setQualifier(fexp);
      form_data.getFacilityMemberList().addAll(
        FacilitiesUtils.getFacilitiesFromSelectQuery(fquery));
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALDBErrorException {
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }
}
