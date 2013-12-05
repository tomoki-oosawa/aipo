package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipMGpdbKubun;

public class EipMGpdbKubun extends _EipMGpdbKubun {

  public Integer getGpdbKubunId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(GPDB_KUBUN_ID_PK_COLUMN);
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

  public void setGpdbKubunId(String id) {
    setObjectId(new ObjectId("EipMGpdbKubun", GPDB_KUBUN_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

}
