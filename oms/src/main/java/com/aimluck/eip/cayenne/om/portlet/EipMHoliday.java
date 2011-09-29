package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMHoliday;

public class EipMHoliday extends _EipMHoliday {

  public Integer getHolidayId() {
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

  public void setHolidayId(String id) {
    setObjectId(new ObjectId("EipMHOLIDAY", HOLIDAY_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }
}
