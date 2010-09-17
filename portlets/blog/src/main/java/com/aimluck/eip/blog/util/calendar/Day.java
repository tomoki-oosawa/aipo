/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2010 Aimluck,Inc.
 * http://aipostyle.com/
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

package com.aimluck.eip.blog.util.calendar;

/**
 */
public class Day implements CalendarElement {

  private int day;

  private int dow;

  private boolean has_blog_entry = false;

  private boolean is_holiday = false;

  public String getLabel() {
    return Integer.toString(day);
  }

  public int getType() {
    if (dow == 6) {
      return CalendarElement.SATURDAY;
    }
    if (dow == 0) {
      return CalendarElement.SUNDAY;
    }
    return CalendarElement.WEEKDAY;
  }

  /**
   * @return
   */
  public int getDay() {
    return day;
  }

  /**
   * @return
   */
  public int getDow() {
    return dow;
  }

  /**
   * @param i
   */
  public void setDay(int i) {
    day = i;
  }

  /**
   * @param i
   */
  public void setDow(int i) {
    dow = i;
  }

  public void setHoliday(boolean bool) {
    is_holiday = bool;
  }

  public boolean isHoliday() {
    return is_holiday;
  }

  public boolean isSaturday() {
    return (getType() == CalendarElement.SATURDAY);
  }

  public boolean isSunday() {
    return (getType() == CalendarElement.SUNDAY);
  }

  public void setBlogEntry(boolean bool) {
    has_blog_entry = bool;
  }

  public boolean hasBlogEntry() {
    return has_blog_entry;
  }
}
