/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.services.eventlog;

/**
 * イベントログの定数です。 <br />
 * 
 */
public class ALEventlogConstants {

  /**
   * PORTLET_TYPE
   * 
   * 1-99 管理者用ポートレット 100- 一般ポートレット 0 その他ポートレット
   * 
   * XX1- 一般ポートレット上位2桁:ポートレットごとに振る 10X-
   * 一般ポートレット上位2桁:ポートレットごとの詳細モード(category,thema etc...)
   */

  // その他のポートレット
  public static final int PORTLET_TYPE_NONE = 0;

  public static final int PORTLET_TYPE_LOGIN = 1;

  public static final int PORTLET_TYPE_LOGOUT = 2;

  public static final int PORTLET_TYPE_ACCOUNT = 3;

  public static final int PORTLET_TYPE_SYSTEM = 4;

  public static final int PORTLET_TYPE_FACILITY = 5;

  public static final int PORTLET_TYPE_GADGET = 6;

  public static final int PORTLET_TYPE_AJAXSCHEDULEWEEKLY = 100;

  public static final int PORTLET_TYPE_BLOG_ENTRY = 110;

  public static final int PORTLET_TYPE_BLOG_THEMA = 111;

  public static final int PORTLET_TYPE_WORKFLOW = 120;

  public static final int PORTLET_TYPE_WORKFLOW_CATEGORY = 121;

  public static final int PORTLET_TYPE_WORKFLOW_ROUTE = 122;

  public static final int PORTLET_TYPE_TODO = 130;

  public static final int PORTLET_TYPE_TODO_CATEGORY = 131;

  public static final int PORTLET_TYPE_NOTE = 140;

  public static final int PORTLET_TYPE_TIMECARD = 150;

  public static final int PORTLET_TYPE_TIMECARD_XLS_SCREEN = 151;

  public static final int PORTLET_TYPE_ADDRESSBOOK = 160;

  public static final int PORTLET_TYPE_ADDRESSBOOK_COMPANY = 161;

  public static final int PORTLET_TYPE_ADDRESSBOOK_GROUP = 162;

  public static final int PORTLET_TYPE_MEMO = 170;

  public static final int PORTLET_TYPE_MSGBOARD_TOPIC = 180;

  public static final int PORTLET_TYPE_MSGBOARD_CATEGORY = 181;

  public static final int PORTLET_TYPE_EXTERNALSEARCH = 190;

  public static final int PORTLET_TYPE_MYLINK = 200;

  public static final int PORTLET_TYPE_WHATSNEW = 210;

  public static final int PORTLET_TYPE_CABINET_FILE = 220;

  public static final int PORTLET_TYPE_CABINET_FOLDER = 221;

  public static final int PORTLET_TYPE_WEBMAIL = 230;

  public static final int PORTLET_TYPE_WEBMAIL_ACCOUNT = 231;

  public static final int PORTLET_TYPE_WEBMAIL_FOLDER = 232;

  public static final int PORTLET_TYPE_WEBMAIL_FILTER = 233;

  public static final int PORTLET_TYPE_SCHEDULE = 240;

  public static final int PORTLET_TYPE_MANHOUR = 250;

  public static final int PORTLET_TYPE_ACCOUNTPERSON = 260;

  public static final int PORTLET_TYPE_MYGROUP = 270;

  public static final int PORTLET_TYPE_PAGE = 280;

  public static final int PORTLET_TYPE_CELLULAR = 290;

  public static final int PORTLET_TYPE_COMMON_CATEGORY = 300;

  public static final int PORTLET_TYPE_EXTTIMECARD = 310;

  public static final int PORTLET_TYPE_EXTTIMECARD_SYSTEM = 311;

  public static final int PORTLET_TYPE_REPORT = 320;

  public static final int PORTLET_TYPE_TIMELINE = 330;

  public static final int PORTLET_TYPE_GPDB = 340;

  public static final int PORTLET_TYPE_ACCESSCTL = 350;

  public static final int PORTLET_TYPE_WIKI = 360;

  public static final int PORTLET_TYPE_PROJECT = 370;

}
