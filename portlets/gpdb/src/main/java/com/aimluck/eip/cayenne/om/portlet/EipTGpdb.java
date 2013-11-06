package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTGpdb;

public class EipTGpdb extends _EipTGpdb {

  public Integer getGpdbId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(GPDB_ID_PK_COLUMN);
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

  public void setGpdbId(String id) {
    setObjectId(new ObjectId("EipTGpdb", GPDB_ID_PK_COLUMN, Integer.valueOf(id)));
  }

}
