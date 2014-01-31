package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTProjectTaskComment;

public class EipTProjectTaskComment extends _EipTProjectTaskComment {

  public Integer getCommentId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(COMMENT_ID_PK_COLUMN);
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

  public void setCommentId(String id) {
    setObjectId(new ObjectId(
      "EipTProjectTaskComment",
      COMMENT_ID_PK_COLUMN,
      Integer.valueOf(id)));
  }

}
