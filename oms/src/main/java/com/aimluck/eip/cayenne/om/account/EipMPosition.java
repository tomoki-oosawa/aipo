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
package com.aimluck.eip.cayenne.om.account;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.account.auto._EipMPosition;

public class EipMPosition extends _EipMPosition {

  public static final String CREATE_DATE_PROPERTY = "createDate";

  public static final String POSITION_NAME_PROPERTY = "positionName";

  public static final String SORT_PROPERTY = "sort";

  public static final String UPDATE_DATE_PROPERTY = "updateDate";

  public static final String POSITION_ID_PK_COLUMN = "POSITION_ID";

  @Override
  public void setCreateDate(java.util.Date createDate) {
    writeProperty("createDate", createDate);
  }

  @Override
  public java.util.Date getCreateDate() {
    return (java.util.Date) readProperty("createDate");
  }

  @Override
  public void setPositionName(String positionName) {
    writeProperty("positionName", positionName);
  }

  @Override
  public String getPositionName() {
    return (String) readProperty("positionName");
  }

  public void setSort(Integer sort) {
    writeProperty("sort", sort);
  }

  public Integer getSort() {
    return (Integer) readProperty("sort");
  }

  @Override
  public void setUpdateDate(java.util.Date updateDate) {
    writeProperty("updateDate", updateDate);
  }

  @Override
  public java.util.Date getUpdateDate() {
    return (java.util.Date) readProperty("updateDate");
  }

  public Integer getPositionId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(POSITION_ID_PK_COLUMN);
      if (obj instanceof Long) {
        Long value = (Long) obj;
        return Integer.valueOf(value.intValue());
      } else {
        return (Integer) obj;
      }
    } else {
      return null;
    }
  }

  public void setPositionId(int id) {
    setObjectId(new ObjectId("EipMPosition", POSITION_ID_PK_COLUMN, id));
  }
}
