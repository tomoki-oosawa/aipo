package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTWiki;

public class EipTWiki extends _EipTWiki {

  public static final String WIKI_NAME_COLUMN = "WIKI_NAME";

  public Integer getWikiId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(WIKI_ID_PK_COLUMN);
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

  public void setWikiId(String id) {
    setObjectId(new ObjectId("EipTWiki", WIKI_ID_PK_COLUMN, Integer.valueOf(id)));
  }

}
