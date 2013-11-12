package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTGpdbItem;

public class EipTGpdbItem extends _EipTGpdbItem {

  public Integer getGpdbItemId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(GPDB_ITEM_ID_PK_COLUMN);
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

  public void setGpdbItemId(String id) {
    setObjectId(new ObjectId("EipTGpdbItem", GPDB_ITEM_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

}
