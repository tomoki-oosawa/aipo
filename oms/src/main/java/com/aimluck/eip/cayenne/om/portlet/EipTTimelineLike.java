package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTTimelineLike;

/**
 * WARNING: This template is deprecated in Cayenne 3.0
 */
public class EipTTimelineLike extends _EipTTimelineLike {

  public static final String OWNER_ID_COLUMN = "OWNER_ID";

  public static final String TIMELINE_ID_COLUMN = "TIMELINE_ID";

  public Integer getTimelineLikeId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj =
        getObjectId().getIdSnapshot().get(TIMELINE_LIKE_ID_PK_COLUMN);
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

  public void setTimelineLikeId(String id) {
    setObjectId(new ObjectId(
      "EipTTimelineLike",
      TIMELINE_LIKE_ID_PK_COLUMN,
      Integer.valueOf(id)));
  }

}
