package com.aimluck.eip.cayenne.om.social;

import com.aimluck.eip.cayenne.om.social.auto._ModuleId;

public class ModuleId extends _ModuleId {

  private static final long serialVersionUID = -993697590942134786L;

  public Integer getId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(ID_PK_COLUMN);
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
}
