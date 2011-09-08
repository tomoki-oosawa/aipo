package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMFacilityGroup;

public class EipMFacilityGroup extends _EipMFacilityGroup {

  public static final String GROUP_NAME_COLUMN = "GROUP_NAME";

  public Integer getGroupId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(GROUP_ID_PK_COLUMN);
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

  public void setGroupId(String id) {
    setObjectId(new ObjectId("EipMFacilityGroup", GROUP_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }
}
