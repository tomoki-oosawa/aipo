/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.blog.util.calendar;

import java.util.GregorianCalendar;

import com.aimluck.eip.common.ALEipHolidaysManager;
import com.aimluck.eip.common.ALHoliday;

/**
 */
public class MonthCalendar {

  public MonthCalendar() {

  }

  public Month createCalendar(int year, int month) {
    Month m = new Month(year, month);
    GregorianCalendar g = new GregorianCalendar(year, month - 1, 1);
    int dow = 0;
    Empty empty = new Empty();

    int weeksize = g.get(GregorianCalendar.DAY_OF_WEEK);
    for (int i = 1; i < weeksize; i++) {
      m.add(empty);
      dow++;
    }

    // 祝日かどうかを検証する．
    ALEipHolidaysManager holiDaysManager = ALEipHolidaysManager.getInstance();
    ALHoliday holiDay = null;

    int monthsize = g.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
    for (int i = 1; i <= monthsize; i++) {
      holiDay = holiDaysManager.isHoliday(g.getTime());
      Day d = new Day();
      d.setDay(i);
      d.setDow(dow % 7);
      d.setHoliday(holiDay != null);
      m.add(d);
      dow++;
      g.add(GregorianCalendar.DATE, 1);
    }

    for (int i = dow % 7; i != 0 && i <= 6; i++) {
      m.add(empty);
      dow++;
    }

    return m;
  }
}
