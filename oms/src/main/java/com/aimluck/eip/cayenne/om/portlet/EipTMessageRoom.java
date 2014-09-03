package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTMessageRoom;

public class EipTMessageRoom extends _EipTMessageRoom {

  public Integer getRoomId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(ROOM_ID_PK_COLUMN);
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

  public void setRoomId(String id) {
    setObjectId(new ObjectId("EipTMessageRoom", ROOM_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

}
