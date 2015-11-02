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
package com.aimluck.eip.schedule;

import java.util.Enumeration;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.EipTAclMap;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.portlet.EipMFacilityGroup;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.schedule.ScheduleAdminAclUserGroupResultData.Type;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ScheduleAdminAclFormData extends ALAbstractFormData {

  /** <code>logger</code> logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleAdminAclFormData.class.getName());

  private ScheduleAdminAclUserGroupResultData currentResultData;

  private ALStringField aclData;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    currentResultData = new ScheduleAdminAclUserGroupResultData();
    currentResultData.initField();
    aclData = new ALStringField();

    String entitiyId =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    Integer tmpId = null;
    if (entitiyId.startsWith("ug")) {
      String id = entitiyId.substring(2);
      tmpId = Integer.valueOf(id);
      TurbineGroup record = Database.get(TurbineGroup.class, tmpId);
      if (record == null) {
        throw new ALPageNotFoundException();
      }
      currentResultData.setId(tmpId);
      currentResultData.setName(record.getGroupAliasName());
      currentResultData.setType(Type.ug);
    } else if (entitiyId.startsWith("fg")) {
      String id = entitiyId.substring(2);
      tmpId = Integer.valueOf(id);
      EipMFacilityGroup record = Database.get(EipMFacilityGroup.class, tmpId);
      if (record == null) {
        throw new ALPageNotFoundException();
      }
      currentResultData.setId(tmpId);
      currentResultData.setName(record.getGroupName());
      currentResultData.setType(Type.fg);
    } else if (entitiyId.startsWith("u")) {
      String id = entitiyId.substring(1);
      tmpId = Integer.valueOf(id);
      ALEipUser user = ALEipUtils.getALEipUser(tmpId);
      if (user == null) {
        throw new ALPageNotFoundException();
      }
      currentResultData.setId(tmpId);
      currentResultData.setName(user.getAliasName().getValue());
      currentResultData.setType(Type.u);
    } else if (entitiyId.startsWith("f")) {
      String id = entitiyId.substring(1);
      tmpId = Integer.valueOf(id);
      EipMFacility record = Database.get(EipMFacility.class, tmpId);
      if (record == null) {
        throw new ALPageNotFoundException();
      }
      currentResultData.setId(tmpId);
      currentResultData.setName(record.getFacilityName());
      currentResultData.setType(Type.f);
    } else {
      throw new ALPageNotFoundException();
    }

    List<EipTAclMap> list =
      Database.query(EipTAclMap.class).where(
        Operations.eq(EipTAclMap.TARGET_ID_PROPERTY, tmpId)).where(
        Operations.eq(EipTAclMap.TARGET_TYPE_PROPERTY, currentResultData
          .getType()
          .toString())).where(
        Operations.eq(EipTAclMap.FEATURE_PROPERTY, "schedule")).fetchList();

    StringBuilder tmpAcl = new StringBuilder();
    boolean isFirst = true;
    for (EipTAclMap map : list) {
      if (!isFirst) {
        tmpAcl.append(",");
      }
      isFirst = false;
      tmpAcl.append(map.getType()).append(map.getId()).append(":").append(
        map.getLevel());
    }
    aclData.setValue(tmpAcl.toString());

  }

  /**
   *
   */
  @Override
  public void initField() {
  }

  /**
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
  }

  /**
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
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

    String sql =
      "delete from eip_t_acl_map where target_id = #bind($target_id) and target_type = #bind($target_type) and feature = #bind($feature)";

    Database
      .sql(EipTAclMap.class, sql)
      .param("target_id", currentResultData.getId().getValue())
      .param("target_type", currentResultData.getType().toString())
      .param("feature", "schedule")
      .execute();

    Enumeration<?> parameterNames = rundata.getRequest().getParameterNames();

    while (parameterNames.hasMoreElements()) {
      String next = (String) parameterNames.nextElement();
      if (next.startsWith("acl")) {
        String tmpId = next.substring(3);
        String value = rundata.getRequest().getParameter(next);
        Integer id = null;
        String type = null;
        Integer level = null;
        if (tmpId.startsWith("ug")) {
          id = Integer.valueOf(tmpId.substring(2));
          type = "ug";
        } else if (tmpId.startsWith("u")) {
          id = Integer.valueOf(tmpId.substring(1));
          type = "u";
        }
        try {
          level = Integer.valueOf(value);
        } catch (Throwable ignore) {
          // ignore
        }
        if (id != null && type != null && level != null) {
          EipTAclMap model = Database.create(EipTAclMap.class);
          model.setFeature("schedule");
          model.setId(id);
          model.setType(type);
          model.setTargetId((int) currentResultData.getId().getValue());
          model.setTargetType(currentResultData.getType().toString());
          model.setLevel(level);
        }
      }
    }

    Database.commit();

    return true;
  }

  /**
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

  public ScheduleAdminAclUserGroupResultData getCurrentData() {
    return currentResultData;
  }

  public ALStringField getAclData() {
    return aclData;
  }

}
