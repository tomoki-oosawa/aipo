package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMProjectKubunValue;

public class EipMProjectKubunValue extends _EipMProjectKubunValue {

  public Integer getProjectKubunValueId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj =
        getObjectId().getIdSnapshot().get(PROJECT_KUBUN_VALUE_ID_PK_COLUMN);
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

  public void setProjectKubunValueId(String id) {
    setObjectId(new ObjectId(
      "EipMProjectKubunValue",
      PROJECT_KUBUN_VALUE_ID_PK_COLUMN,
      Integer.valueOf(id)));
  }

}
