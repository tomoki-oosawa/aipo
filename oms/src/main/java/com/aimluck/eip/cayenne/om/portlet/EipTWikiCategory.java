package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTWikiCategory;

public class EipTWikiCategory extends _EipTWikiCategory {

  public Integer getCategoryId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(CATEGORY_ID_PK_COLUMN);
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

  public void setCategoryId(String id) {
    setObjectId(new ObjectId("EipTWikiCategory", CATEGORY_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

  /**
   * @return
   */

}
