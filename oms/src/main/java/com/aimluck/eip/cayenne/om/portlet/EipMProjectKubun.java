package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMProjectKubun;

public class EipMProjectKubun extends _EipMProjectKubun {

  public Integer getProjectKubunId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj =
        getObjectId().getIdSnapshot().get(PROJECT_KUBUN_ID_PK_COLUMN);
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

  public void setProjectKubunId(String id) {
    setObjectId(new ObjectId(
      "EipMProjectKubun",
      PROJECT_KUBUN_ID_PK_COLUMN,
      Integer.valueOf(id)));
  }

}
