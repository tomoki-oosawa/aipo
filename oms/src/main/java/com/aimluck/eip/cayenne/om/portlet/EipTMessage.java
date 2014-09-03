package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTMessage;

public class EipTMessage extends _EipTMessage {

  public Integer getMessageId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(MESSAGE_ID_PK_COLUMN);
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

  public void setMessageId(String id) {
    setObjectId(new ObjectId("EipTMessage", MESSAGE_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

}
