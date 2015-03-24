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

import java.util.List;
import java.util.jar.Attributes;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipTAclMap;
import com.aimluck.eip.cayenne.om.account.VEipMUserGroupList;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.schedule.ScheduleAdminAclUserGroupResultData.Type;
import com.aimluck.eip.userfacility.util.UserAllUtils;

/**
 *
 */
public class ScheduleAdminAclUserGroupListSelectData extends
    ALAbstractSelectData<VEipMUserGroupList, VEipMUserGroupList> {

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<VEipMUserGroupList> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {

    ResultList<VEipMUserGroupList> resultList =
      UserAllUtils.getUserAllList(
        rundata,
        -1,
        null,
        current_page,
        getRowsNum(),
        false);
    setPageParam(resultList.getTotalCount());

    return resultList;
  }

  public String getViewtype() {
    return "acl";
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected VEipMUserGroupList selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(VEipMUserGroupList record)
      throws ALPageNotFoundException, ALDBErrorException {

    ScheduleAdminAclUserGroupResultData rd =
      new ScheduleAdminAclUserGroupResultData();
    rd.initField();

    rd.setId(record.getId());
    rd.setName(record.getName());
    rd.setType(Type.valueOf(record.getType()));

    boolean updated = false;

    List<EipTAclMap> result =
      Database
        .query(EipTAclMap.class)
        .where(Operations.eq(EipTAclMap.TARGET_ID_PROPERTY, record.getId()))
        .where(Operations.eq(EipTAclMap.TARGET_TYPE_PROPERTY, record.getType()))
        .where(Operations.eq(EipTAclMap.FEATURE_PROPERTY, "schedule"))
        .fetchList();

    if (Type.u.equals(Type.valueOf(record.getType()))
      || Type.f.equals(Type.valueOf(record.getType()))) {
      if (result.size() == 1) {
        EipTAclMap map = result.get(0);
        updated =
          map.getId().intValue() != 2
            || !map.getType().equals("ug")
            || map.getLevel().intValue() != 2;
      } else {
        updated = true;
      }
    } else {
      updated = result.size() > 0;
    }

    rd.setUpdated(updated);

    return rd;
  }

  /**
   * @param record
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(VEipMUserGroupList record)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}
