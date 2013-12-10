package com.aimluck.eip.cayenne.om.portlet;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTSurvey;

public class EipTSurvey extends _EipTSurvey {
  public Integer getSurveyId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(SURVEY_ID_PK_COLUMN);
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
