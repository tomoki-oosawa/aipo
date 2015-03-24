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
package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTSchedule;

public class EipTSchedule extends _EipTSchedule {

  public static final String NAME_COLUMN = "NAME";

  public static final String CREATE_DATE_COLUMN = "CREATE_DATE";

  public static final String OWNER_ID_COLUMN = "OWNER_ID";

  public static final String UPDATE_USER_ID_COLUMN = "UPDATE_USER_ID";

  public static final String START_DATE_COLUMN = "START_DATE";

  public Integer getScheduleId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(SCHEDULE_ID_PK_COLUMN);
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

  public void setScheduleId(String id) {
    setObjectId(new ObjectId("EipTSchedule", SCHEDULE_ID_PK_COLUMN, Integer
        .valueOf(id)));
  }
}
