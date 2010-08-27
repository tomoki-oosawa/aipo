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

/**
 */
public interface CalendarElement {

  /** 平日 */
  public static final int WEEKDAY = 1;

  /** 土曜日 */
  public static final int SATURDAY = 2;

  /** 日曜日 */
  public static final int SUNDAY = 4;

  /** 休日・祝日 */
  public static final int HOLIDAY = 8;

  /** 空日 */
  public static final int EMPTY = 16;

  /** 種別 */
  public int getType();

  /** 表示用ラベル */
  public String getLabel();

  /** 表示折り返し用フラグ */
  public boolean isSaturday();
}
