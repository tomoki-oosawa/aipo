/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.blog.util.calendar;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class Month {
  private int year;

  private int month;

  private final List<CalendarElement> list = new ArrayList<CalendarElement>();

  public Month(int year, int month) {
    setYear(year);
    setMonth(month);
  }

  public void add(CalendarElement e) {
    list.add(e);
  }

  public List<CalendarElement> getDayList() {
    return list;
  }

  public CalendarElement get(int i) {
    return list.get(i);
  }

  public int size() {
    return list.size();
  }

  /**
   * @return
   */
  public int getMonth() {
    return month;
  }

  /**
   * @return
   */
  public int getYear() {
    return year;
  }

  /**
   * @param i
   */
  public void setMonth(int i) {
    month = i;
  }

  /**
   * @param i
   */
  public void setYear(int i) {
    year = i;
  }

  public String getMonthLiteral() {
    return String.format("%04d-%02d", year, month);
  }
}
