package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTWikiHistory;

public class EipTWikiHistory extends _EipTWikiHistory {

  public Integer getFileId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(HISTORY_ID_PK_COLUMN);
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

  public void setFileId(String id) {
    setObjectId(new ObjectId("EipTWikiHistory", HISTORY_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

}
