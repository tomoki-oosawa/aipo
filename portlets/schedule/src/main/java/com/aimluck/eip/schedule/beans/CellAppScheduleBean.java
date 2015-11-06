/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.schedule.beans;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.schedule.ScheduleDetailResultData;

public class CellAppScheduleBean implements ALData, Cloneable {

  private String title;

  private String text;

  private String place;

  private String start_date;

  private String end_date;

  private static final String format = "yyyyMMddHHmm";

  private ALDateTimeField timeField;

  @Override
  public void initField() {
    title = "";
    text = "";
    start_date = "";
    end_date = "";
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getStart_date() {
    return start_date;
  }

  // public void setStart_date(Date start_date) {
  // timeField = new ALDateTimeField(format);
  // timeField.setValue(start_date);
  // this.start_date = timeField.toString();
  // }

  public String getEnd_date() {
    return end_date;
  }

  // public void setEnd_date(Date end_date) {
  // timeField = new ALDateTimeField(format);
  // timeField.setValue(end_date);
  // this.end_date = timeField.toString();
  // }

  public void setResultData(ScheduleDetailResultData rd) {
    title = encloseDoubleQuart(rd.getName().getValue());
    text = encloseDoubleQuart(rd.getNoteStr());
    place = encloseDoubleQuart(rd.getPlace().getValue());
    timeField = new ALDateTimeField(format);
    timeField.setValue(rd.getStartDate().getValue());
    start_date = timeField.toString();
    timeField.setValue(rd.getEndDate().getValue());
    end_date = timeField.toString();
  }

  public String getPlace() {
    return place;
  }

  public void setPlace(String place) {
    this.place = place;
  }

  private String encloseDoubleQuart(String str) {
    StringBuffer sb = new StringBuffer();
    sb.append("\"").append(str).append("\"");
    return sb.toString();
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}