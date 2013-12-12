package com.aimluck.eip.cayenne.om.portlet;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTSurveyOption;

public class EipTSurveyOption extends _EipTSurveyOption {
  public Integer getOptionId() {
    return (getObjectId() != null && !getObjectId().isTemporary())
      ? (Integer) getObjectId().getIdSnapshot().get(OPTION_ID_PK_COLUMN)
      : null;
  }
}
