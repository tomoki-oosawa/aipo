package com.aimluck.eip.cayenne.om.account;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.account.auto._EipMUserHoliday;

public class EipMUserHoliday extends _EipMUserHoliday {

  public Integer getUserHolidayId() {

    if (getObjectId() != null && !getObjectId().isTemporary()) {

      Object obj = getObjectId().getIdSnapshot().get(HOLIDAY_ID_PK_COLUMN);

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

  public void setUserHolidayId(String id) {

    setObjectId(new ObjectId("EipMUserHoliay", HOLIDAY_ID_PK_COLUMN, Integer
      .valueOf(id)));

  }

}
