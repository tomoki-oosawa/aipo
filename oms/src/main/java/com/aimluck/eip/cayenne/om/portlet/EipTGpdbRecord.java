package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTGpdbRecord;

public class EipTGpdbRecord extends _EipTGpdbRecord {

  public Integer getGpdbRecordId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(GPDB_RECORD_ID_PK_COLUMN);
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

  public void setGpdbRecordId(String id) {
    setObjectId(new ObjectId(
      "EipTGpdbRecord",
      GPDB_RECORD_ID_PK_COLUMN,
      Integer.valueOf(id)));
  }

}
