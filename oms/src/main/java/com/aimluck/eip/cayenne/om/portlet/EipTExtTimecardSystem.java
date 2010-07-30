package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTExtTimecardSystem;

public class EipTExtTimecardSystem extends _EipTExtTimecardSystem {

  public static final String SYSTEM_NAME_COLUMN = "SYSTEM_NAME";

  public Integer getSystemId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(SYSTEM_ID_PK_COLUMN);
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

  public void setSystemId(String id) {
    setObjectId(new ObjectId("EipTExtTimecardSystem", SYSTEM_ID_PK_COLUMN,
        Integer.valueOf(id)));
  }
}
