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
package com.aimluck.eip.accessctl.action;

/**
 * ログ保存用ライブラリの定数です。
 * 
 */
public class ALActionAccessControlConstants {

  /** アクセス権限の機能名（ブログ（自分の記事）） */
  public static final String POERTLET_FEATURE_BLOG_ENTRY_SELF =
    "blog_entry_self";

  /** アクセス権限の機能名（ブログ（他ユーザーの記事）） */
  public static final String POERTLET_FEATURE_BLOG_ENTRY_OTHER =
    "blog_entry_other";

  /** アクセス権限の機能名（ブログ（他ユーザーへのコメント）） */
  public static final String POERTLET_FEATURE_BLOG_ENTRY_REPLY =
    "blog_entry_reply";

  /** アクセス権限の機能名（ブログ（テーマ）） */
  public static final String POERTLET_FEATURE_BLOG_THEME = "blog_theme";

  /** アクセス権限の機能名（掲示板（トピック）） */
  public static final String POERTLET_FEATURE_MSGBOARD_TOPIC = "msgboard_topic";

  /** アクセス権限の機能名（掲示板（トピック返信）） */
  public static final String POERTLET_FEATURE_MSGBOARD_TOPIC_REPLY =
    "msgboard_topic_reply";

  /** アクセス権限の機能名（掲示板（カテゴリ）） */
  public static final String POERTLET_FEATURE_MSGBOARD_CATEGORY =
    "msgboard_category";

  /** アクセス権限の機能名（掲示板（他ユーザーのトピック）） */
  public static final String POERTLET_FEATURE_MSGBOARD_TOPIC_OTHER =
    "msgboard_topic_other";

  /** アクセス権限の機能名（スケジュール（自分の予定）） */
  public static final String POERTLET_FEATURE_SCHEDULE_SELF = "schedule_self";

  /** アクセス権限の機能名（スケジュール（他ユーザーの予定）） */
  public static final String POERTLET_FEATURE_SCHEDULE_OTHER = "schedule_other";

  /** アクセス権限の機能名 (スケジュール 設備の予約操作) */
  public static final String POERTLET_FEATURE_SCHEDULE_FACILITY =
    "schedule_facility";

  /** アクセス権限の機能名（ToDo（自分のToDo）） */
  public static final String POERTLET_FEATURE_TODO_TODO_SELF = "todo_todo_self";

  /** アクセス権限の機能名（ToDo（他ユーザーのToDo）） */
  public static final String POERTLET_FEATURE_TODO_TODO_OTHER =
    "todo_todo_other";

  /** アクセス権限の機能名（ToDo（カテゴリ）） */
  public static final String POERTLET_FEATURE_TODO_CATEGORY_SELF =
    "todo_category_self";

  /** アクセス権限の機能名（ワークフロー（自分の依頼）） */
  public static final String POERTLET_FEATURE_WORKFLOW_REQUEST_SELF =
    "workflow_request_self";

  /** アクセス権限の機能名（ワークフロー（他ユーザーの依頼）） */
  public static final String POERTLET_FEATURE_WORKFLOW_REQUEST_OTHER =
    "workflow_request_other";

  /** アクセス権限の機能名（アドレス帳（社内アドレス）） */
  public static final String POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_INSIDE =
    "addressbook_address_inside";

  /** アクセス権限の機能名（アドレス帳（社外アドレス）） */
  public static final String POERTLET_FEATURE_ADDRESSBOOK_ADDRESS_OUTSIDE =
    "addressbook_address_outside";

  /** アクセス権限の機能名（アドレス帳（会社情報）） */
  public static final String POERTLET_FEATURE_ADDRESSBOOK_COMPANY =
    "addressbook_company";

  /** アクセス権限の機能名（アドレス帳（社外グループ）） */
  public static final String POERTLET_FEATURE_ADDRESSBOOK_COMPANY_GROUP =
    "addressbook_company_group";

  /** アクセス権限の機能名（タイムカード（自分のタイムカード）） */
  public static final String POERTLET_FEATURE_TIMECARD_TIMECARD_SELF =
    "timecard_timecard_self";

  /** アクセス権限の機能名（タイムカード（他人のタイムカード）） */
  public static final String POERTLET_FEATURE_TIMECARD_TIMECARD_OTHER =
    "timecard_timecard_other";

  /** アクセス権限の機能名（共有フォルダ（ファイル）） */
  public static final String POERTLET_FEATURE_CABINET_FILE = "cabinet_file";

  /** アクセス権限の機能名（共有フォルダ（フォルダ）） */
  public static final String POERTLET_FEATURE_CABINET_FOLDER = "cabinet_folder";

  /** アクセス権限の機能名（プロジェクト管理（自分の工数）） */
  public static final String POERTLET_FEATURE_MANHOUR_SUMMARY_SELF =
    "manhour_summary_self";

  /** アクセス権限の機能名（プロジェクト管理（他ユーザの工数）） */
  public static final String POERTLET_FEATURE_MANHOUR_SUMMARY_OTHER =
    "manhour_summary_other";

  /** アクセス権限の機能名（プロジェクト管理（共有カテゴリ）） */
  public static final String POERTLET_FEATURE_MANHOUR_COMMON_CATEGORY =
    "manhour_common_category";

}
