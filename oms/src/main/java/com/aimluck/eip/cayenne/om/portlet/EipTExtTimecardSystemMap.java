package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTExtTimecardSystemMap;

public class EipTExtTimecardSystemMap extends _EipTExtTimecardSystemMap {

  public Integer getSystemMapId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(SYSTEM_MAP_ID_PK_COLUMN);
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

  public void setSystemMapId(String id) {
    setObjectId(new ObjectId("EipTExtTimecardSystemMap", SYSTEM_MAP_ID_PK_COLUMN,
        Integer.valueOf(id)));
  }
}
