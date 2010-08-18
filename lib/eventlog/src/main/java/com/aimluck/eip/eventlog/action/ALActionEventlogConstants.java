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
package com.aimluck.eip.eventlog.action;

import com.aimluck.eip.common.ALEipConstants;

/**
 * ログ保存用ライブラリの定数です。
 * 
 */
public class ALActionEventlogConstants {

  /**
   * EVENT_TYPE
   */

  // その他のイベント
  public static final int EVENT_TYPE_NONE = 0;

  public static final int EVENT_TYPE_UPDATE = 1;

  public static final int EVENT_TYPE_DELETE = 2;

  public static final int EVENT_TYPE_MULTI_DELETE = 3;

  public static final int EVENT_TYPE_INSERT = 4;

  public static final int EVENT_TYPE_FORM = 5;

  public static final int EVENT_TYPE_NEW_FORM = 6;

  public static final int EVENT_TYPE_EDIT_FORM = 7;

  public static final int EVENT_TYPE_LIST = 8;

  public static final int EVENT_TYPE_DETAIL = 9;

  public static final int EVENT_TYPE_LOGIN = 10;

  public static final int EVENT_TYPE_LOGOUT = 11;

  public static final int EVENT_TYPE_ACCEPT = 12;

  public static final int EVENT_TYPE_DENIAL = 13;

  public static final int EVENT_TYPE_PUNCHIN = 14;

  public static final int EVENT_TYPE_PUNCHOUT = 15;

  public static final int EVENT_TYPE_CHANGE_STATUS = 16;

  public static final int EVENT_TYPE_XLS_SCREEN = 17;

  /* EVENT_NUMBER */
  public static final String EVENT_MODE_UPDATE = ALEipConstants.MODE_UPDATE;

  public static final String EVENT_MODE_DELETE = ALEipConstants.MODE_DELETE;

  public static final String EVENT_MODE_MULTI_DELETE = ALEipConstants.MODE_MULTI_DELETE;

  public static final String EVENT_MODE_INSERT = ALEipConstants.MODE_INSERT;

  public static final String EVENT_MODE_FORM = ALEipConstants.MODE_FORM;

  public static final String EVENT_MODE_NEW_FORM = ALEipConstants.MODE_NEW_FORM;

  public static final String EVENT_MODE_EDIT_FORM = ALEipConstants.MODE_EDIT_FORM;

  public static final String EVENT_MODE_LIST = ALEipConstants.MODE_LIST;

  public static final String EVENT_MODE_DETAIL = ALEipConstants.MODE_DETAIL;

  public static final String EVENT_MODE_LOGIN = "Login";

  public static final String EVENT_MODE_LOGOUT = "Logout";

  public static final String EVENT_MODE_ACCEPT = "accept";

  public static final String EVENT_MODE_DENIAL = "denial";

  public static final String EVENT_MODE_PUNCHIN = "punchin";

  public static final String EVENT_MODE_PUNCHOUT = "punchout";

  public static final String EVENT_MODE_CHANGE_STATUS = "change_status";

  public static final String EVENT_MODE_XLS_SCREEN = "xls_screen";

  /**
   * EVENT_ALIAS_NAME
   */
  public static final String[] EVENT_ALIAS_NAME = { "その他のイベント", "更新", "削除",
    "削除", "追加", "フォーム", "新規フォーム", "編集フォーム", "一覧", "詳細", "ログイン", "ログアウト", "承認",
    "差し戻し", "出勤", "退勤", "参加・キャンセル", "Excelファイルとしてダウンロード" };

  // /**
  // * PORTLET_TYPE
  // *
  // * 1-99 管理者用ポートレット 100- 一般ポートレット 0 その他ポートレット
  // *
  // * XX1- 一般ポートレット上位2桁:ポートレットごとに振る 10X-
  // * 一般ポートレット上位2桁:ポートレットごとの詳細モード(category,thema etc...)
  // */
  //
  // // その他のポートレット
  // public static final int PORTLET_TYPE_NONE = 0;
  //
  // public static final int PORTLET_TYPE_LOGIN = 1;
  //
  // public static final int PORTLET_TYPE_LOGOUT = 2;
  //
  // public static final int PORTLET_TYPE_ACCOUNT = 3;
  //
  // public static final int PORTLET_TYPE_AJAXSCHEDULEWEEKLY = 100;
  //
  // public static final int PORTLET_TYPE_BLOG_ENTRY = 110;
  //
  // public static final int PORTLET_TYPE_BLOG_THEMA = 111;
  //
  // public static final int PORTLET_TYPE_WORKFLOW = 120;
  //
  // public static final int PORTLET_TYPE_WORKFLOW_CATEGORY = 121;
  //
  // public static final int PORTLET_TYPE_TODO = 130;
  //
  // public static final int PORTLET_TYPE_TODO_CATEGORY = 131;
  //
  // public static final int PORTLET_TYPE_NOTE = 140;
  //
  // public static final int PORTLET_TYPE_TIMECARD = 150;
  //
  // public static final int PORTLET_TYPE_TIMECARD_XLS_SCREEN = 151;
  //
  // public static final int PORTLET_TYPE_ADDRESSBOOK = 160;
  //
  // public static final int PORTLET_TYPE_ADDRESSBOOK_COMPANY = 161;
  //
  // public static final int PORTLET_TYPE_ADDRESSBOOK_GROUP = 162;
  //
  // public static final int PORTLET_TYPE_MEMO = 170;
  //
  // public static final int PORTLET_TYPE_MSGBOARD_TOPIC = 180;
  //
  // public static final int PORTLET_TYPE_MSGBOARD_CATEGORY = 181;
  //
  // public static final int PORTLET_TYPE_EXTERNALSEARCH = 190;
  //
  // public static final int PORTLET_TYPE_MYLINK = 200;
  //
  // public static final int PORTLET_TYPE_WHATSNEW = 210;
  //
  // public static final int PORTLET_TYPE_CABINET_FILE = 220;
  //
  // public static final int PORTLET_TYPE_CABINET_FOLDER = 221;
  //
  // public static final int PORTLET_TYPE_WEBMAIL = 230;
  //
  // public static final int PORTLET_TYPE_WEBMAIL_ACCOUNT = 231;
  //
  // public static final int PORTLET_TYPE_SCHEDULE = 240;
  //
  // public static final int PORTLET_TYPE_MANHOUR = 250;
  //
  // public static final int PORTLET_TYPE_ACCOUNTPERSON = 260;
  //
  // public static final int PORTLET_TYPE_MYGROUP = 270;
  //
  // public static final int PORTLET_TYPE_PAGE = 280;
  //
  // public static final int PORTLET_TYPE_CELLULAR = 290;
  //
  // public static final int PORTLET_TYPE_COMMON_CATEGORY = 300;

  /**
   *
   */
  public static final String PORTLET_TYPE_STR_STR_NONE = "その他の機能";

  public static final String PORTLET_TYPE_STR_LOGIN = "ログイン";

  public static final String PORTLET_TYPE_STR_LOGOUT = "ログアウト";

  public static final String PORTLET_TYPE_STR_ACCOUNT = "アカウント";

  public static final String PORTLET_TYPE_STR_AJAXSCHEDULEWEEKLY = "カレンダー";

  public static final String PORTLET_TYPE_STR_BLOG_ENTRY = "ブログエントリ";

  public static final String PORTLET_TYPE_STR_BLOG_THEMA = "ブログテーマ";

  public static final String PORTLET_TYPE_STR_WORKFLOW = "ワークフロー";

  public static final String PORTLET_TYPE_STR_TODO = "ToDo";

  public static final String PORTLET_TYPE_STR_TODO_CATEGORY = "ToDoカテゴリ";

  public static final String PORTLET_TYPE_STR_NOTE = "伝言メモ";

  public static final String PORTLET_TYPE_STR_TIMECARD = "タイムカード";

  public static final String PORTLET_TYPE_STR_TIMECARD_XLS_SCREEN = "タイムカード出力";

  public static final String PORTLET_TYPE_STR_ADDRESSBOOK = "アドレス帳(アドレス)";

  public static final String PORTLET_TYPE_STR_ADDRESSBOOK_COMPANY = "アドレス帳(会社情報)";

  public static final String PORTLET_TYPE_STR_ADDRESSBOOK_GROUP = "アドレス帳(社外グループ)";

  public static final String PORTLET_TYPE_STR_MEMO = "メモ帳";

  public static final String PORTLET_TYPE_STR_MSGBOARD = "掲示板";

  public static final String PORTLET_TYPE_STR_EXTERNALSEARCH = "検索窓";

  public static final String PORTLET_TYPE_STR_MYLINK = "Myリンク";

  public static final String PORTLET_TYPE_STR_WHATSNEW = "新着情報";

  public static final String PORTLET_TYPE_STR_CABINET_FILE = "共有フォルダ";

  public static final String PORTLET_TYPE_STR_CABINET_FOLDER = "共有フォルダ";

  public static final String PORTLET_TYPE_STR_WEBMAIL = "Webメールアカウント";

  public static final String PORTLET_TYPE_STR_WEBMAIL_FOLDER = "Webメールフォルダ";

  public static final String PORTLET_TYPE_STR_WEBMAIL_FILTER = "Webメールフィルタ";

  public static final String PORTLET_TYPE_STR_SCHEDULE = "スケジュール管理";

  public static final String PORTLET_TYPE_STR_MANHOUR = "プロジェクト管理";

  public static final String PORTLET_TYPE_STR_ACCOUNTPERSON = "ユーザー編集";

  public static final String PORTLET_TYPE_STR_MYGROUP = "マイグループ";

  public static final String PORTLET_TYPE_STR_PAGE = "ページ設定";

  public static final String PORTLET_TYPE_STR_CELLULAR = "携帯電話設定";

  public static final String PORTLET_TYPE_STR_COMMON_CATEGORY = "共有カテゴリ";

  // /**
  // * PORTLET_NAME
  // *
  // */
  // // たぶんいらない
  // public static final String PORTLET_NAME_ACCOUNT = "Account";
  //
  // public static final String PORTLET_NAME_AJAXSCHEDULEWEEKLY =
  // "AjaxScheduleWeekly";
  //
  // public static final String PORTLET_NAME_BLOG_ENTRY = "Blog";
  //
  // public static final String PORTLET_NAME_WORKFLOW = "Workflow";
  //
  // public static final String PORTLET_NAME_TODO = "ToDo";
  //
  // public static final String PORTLET_NAME_TODO_CATEGORY = "ToDoCategory";
  //
  // public static final String PORTLET_NAME_NOTE = "Note";
  //
  // public static final String PORTLET_NAME_TIMECARD = "Timecard";
  //
  // public static final String PORTLET_NAME_ADDRESSBOOK = "AddressBook";
  //
  // public static final String PORTLET_NAME_MEMO = "Memo";
  //
  // public static final String PORTLET_NAME_MSGBOARD = "Msgboard";
  //
  // public static final String PORTLET_NAME_EXTERNALSEARCH = "ExternalSearch";
  //
  // public static final String PORTLET_NAME_MYLINK = "MyLink";
  //
  // public static final String PORTLET_NAME_WHATSNEW = "WhatsNew";
  //
  // public static final String PORTLET_NAME_CABINET = "Cabinet";
  //
  // public static final String PORTLET_NAME_WEBMAIL = "WebMail";
  //
  // public static final String PORTLET_NAME_SCHEDULE = "Schedule";
  //
  // public static final String PORTLET_NAME_MANHOUR = "ManHour";
  //
  // public static final String PORTLET_NAME_ACCOUNTPERSON = "AccountPerson";
  //
  // public static final String PORTLET_NAME_MYGROUP = "MyGroup";
  //
  // public static final String PORTLET_NAME_PAGE = "Page";
  //
  // public static final String PORTLET_NAME_CELLULAR = "Cellular";

}
