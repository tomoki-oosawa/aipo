package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMFacilityGroupMap;

public class EipMFacilityGroupMap extends _EipMFacilityGroupMap {
  public Integer getId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(ID_PK_COLUMN);
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

  public void setId(String id) {
    setObjectId(new ObjectId("EipMFacilityGroupMap", ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

}
