package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTProject;

public class EipTProject extends _EipTProject {

  public Integer getProjectId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(PROJECT_ID_PK_COLUMN);
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

  public void setProjectId(String id) {
    setObjectId(new ObjectId("EipTProject", PROJECT_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

}
