package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMGpdbKubunValue;

public class EipMGpdbKubunValue extends _EipMGpdbKubunValue {

  public Integer getGpdbKubunValueId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj =
        getObjectId().getIdSnapshot().get(GPDB_KUBUN_VALUE_ID_PK_COLUMN);
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

  public void setGpdbKubunValueId(String id) {
    setObjectId(new ObjectId(
      "EipMGpdbKubunValue",
      GPDB_KUBUN_VALUE_ID_PK_COLUMN,
      Integer.valueOf(id)));
  }

}
