package com.aimluck.eip.cayenne.om.security;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.security.auto._VTurbineUserLite;

public class VTurbineUserLite extends _VTurbineUserLite {

  public Integer getUserId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(USER_ID_PK_COLUMN);
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

  public void setUserId(String id) {
    setObjectId(new ObjectId("TurbineUser", USER_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }
}
