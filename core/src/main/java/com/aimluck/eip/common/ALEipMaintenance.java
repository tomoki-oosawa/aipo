/*
 * This file is part of the com.aipo.saas package.
 * Copyright (C) 2004-2013 Aimluck,Inc.
 * http://www.aipo.com
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.aimluck.eip.common;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALStringField;

public class ALEipMaintenance implements ALData {

  private ALStringField maintenanceId;

  // htmlタグ許可のためString
  private String title;

  // htmlタグ許可のためString
  private String body;

  private ALDateTimeField startDate;

  private ALDateTimeField endDate;

  private boolean isShow;

  @Override
  public void initField() {
    maintenanceId = new ALStringField();
    title = "";
    body = "";
    startDate = new ALDateTimeField();
    endDate = new ALDateTimeField();
    isShow = false;
  }

  public ALStringField getMaintenanceId() {
    return maintenanceId;
  }

  public void setMaintenanceId(ALStringField maintenanceId) {
    this.maintenanceId = maintenanceId;
  }

  public ALDateTimeField getStartDate() {
    return startDate;
  }

  public void setStartDate(ALDateTimeField startDate) {
    this.startDate = startDate;
  }

  public ALDateTimeField getEndDate() {
    return endDate;
  }

  public void setEndDate(ALDateTimeField endDate) {
    this.endDate = endDate;
  }

  public boolean isShow() {
    return isShow;
  }

  public void setShow(boolean isShow) {
    this.isShow = isShow;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

}