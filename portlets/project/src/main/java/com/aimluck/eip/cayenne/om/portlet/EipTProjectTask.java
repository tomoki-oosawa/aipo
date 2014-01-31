package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTProjectTask;

public class EipTProjectTask extends _EipTProjectTask {

  int indent = 0;

  public Integer getTaskId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(TASK_ID_PK_COLUMN);
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

  public void setTaskId(String id) {
    setObjectId(new ObjectId("EipTProjectTask", TASK_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

  public int getIndent() {
    return indent;
  }

  public void setIndent(int i) {
    indent = i;
  }
}
