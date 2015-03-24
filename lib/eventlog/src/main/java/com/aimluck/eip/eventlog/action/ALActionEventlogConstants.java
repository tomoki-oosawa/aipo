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

  public static final int EVENT_TYPE_UPDATE_PASSWORD = 18;

  public static final int EVENT_TYPE_DOWNLOAD = 19;

  public static final int EVENT_TYPE_STARTGUIDE = 20;

  public static final int EVENT_TYPE_COMMENT = 21;

  /* EVENT_NUMBER */
  public static final String EVENT_MODE_UPDATE = ALEipConstants.MODE_UPDATE;

  public static final String EVENT_MODE_DELETE = ALEipConstants.MODE_DELETE;

  public static final String EVENT_MODE_DELETE_REPLY = "delete_reply";

  public static final String EVENT_MODE_MULTI_DELETE =
    ALEipConstants.MODE_MULTI_DELETE;

  public static final String EVENT_MODE_INSERT = ALEipConstants.MODE_INSERT;

  public static final String EVENT_MODE_FORM = ALEipConstants.MODE_FORM;

  public static final String EVENT_MODE_NEW_FORM = ALEipConstants.MODE_NEW_FORM;

  public static final String EVENT_MODE_EDIT_FORM =
    ALEipConstants.MODE_EDIT_FORM;

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

  public static final String EVENT_MODE_UPDATE_PASSWORD =
    ALEipConstants.MODE_UPDATE_PASSWD;

  public static final String EVENT_MODE_DOWNLOAD = "download";

  public static final String EVENT_MODE_STARTGUIDE = "startguide";

  public static final String EVENT_MODE_COMMENT = "comment";

  /**
   * EVENT_ALIAS_NAME
   */
  public static final String[] EVENT_ALIAS_NAME = {
    "その他のイベント",
    "更新",
    "削除",
    "削除",
    "追加",
    "フォーム",
    "新規フォーム",
    "編集フォーム",
    "一覧",
    "詳細",
    "ログイン",
    "ログアウト",
    "承認",
    "差し戻し",
    "出勤",
    "退勤",
    "参加・キャンセル",
    "Excelファイルとしてダウンロード",
    "パスワード変更",
    "ダウンロード",
    "スタートガイド",
    "コメント" };

  public static final String PORTLET_TYPE_STR_STR_NONE = "その他の機能";

  public static final String PORTLET_TYPE_STR_LOGIN = "ログイン";

  public static final String PORTLET_TYPE_STR_LOGOUT = "ログアウト";

  public static final String PORTLET_TYPE_STR_ACCOUNT = "アカウント";

  public static final String PORTLET_TYPE_STR_SYSTEM = "システム";

  public static final String PORTLET_TYPE_STR_AJAXSCHEDULEWEEKLY = "カレンダー";

  public static final String PORTLET_TYPE_STR_BLOG_ENTRY = "ブログエントリ";

  public static final String PORTLET_TYPE_STR_BLOG_THEMA = "ブログテーマ";

  public static final String PORTLET_TYPE_STR_WORKFLOW = "ワークフロー";

  public static final String PORTLET_TYPE_STR_WORKFLOW_CATEGORY = "ワークフロー分類";

  public static final String PORTLET_TYPE_STR_WORKFLOW_ROUTE = "ワークフロー申請経路";

  public static final String PORTLET_TYPE_STR_TODO = "ToDo";

  public static final String PORTLET_TYPE_STR_TODO_CATEGORY = "ToDoカテゴリ";

  public static final String PORTLET_TYPE_STR_NOTE = "伝言メモ";

  public static final String PORTLET_TYPE_STR_TIMECARD = "旧タイムカード";

  public static final String PORTLET_TYPE_STR_TIMECARD_XLS_SCREEN = "旧タイムカード出力";

  public static final String PORTLET_TYPE_STR_ADDRESSBOOK = "アドレス帳(アドレス)";

  public static final String PORTLET_TYPE_STR_ADDRESSBOOK_COMPANY =
    "アドレス帳(会社情報)";

  public static final String PORTLET_TYPE_STR_ADDRESSBOOK_GROUP =
    "アドレス帳(社外グループ)";

  public static final String PORTLET_TYPE_STR_MEMO = "メモ帳";

  public static final String PORTLET_TYPE_STR_MSGBOARD = "掲示板";

  public static final String PORTLET_TYPE_STR_MSGBOARD_CATEGORY = "掲示板カテゴリ";

  public static final String PORTLET_TYPE_STR_EXTERNALSEARCH = "検索窓";

  public static final String PORTLET_TYPE_STR_MYLINK = "Myリンク";

  public static final String PORTLET_TYPE_STR_WHATSNEW = "新着情報";

  public static final String PORTLET_TYPE_STR_CABINET_FILE = "フォルダ";

  public static final String PORTLET_TYPE_STR_CABINET_FOLDER = "フォルダ";

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

  public static final String PORTLET_TYPE_STR_EXTTIMECARD = "タイムカード";

  public static final String PORTLET_TYPE_STR_EXTTIMECARD_SYSTEM = "タイムカード管理";

  public static final String PORTLET_TYPE_STR_REPORT = "報告書";

  public static final String PORTLET_TYPE_STR_TIMELINE = "タイムライン";

  public static final String PORTLET_TYPE_STR_GPDB = "Webデータベース";

  public static final String PORTLET_TYPE_STR_ACCESSCTL = "アクセス権限管理";

  public static final String PORTLET_TYPE_STR_FACILITY = "設備管理";

  public static final String PORTLET_TYPE_STR_GADGET = "アプリ管理";

  public static final String PORTLET_TYPE_STR_WIKI = "Wiki";

  public static final String PORTLET_TYPE_STR_PROJECT = "プロジェクト管理";

}
