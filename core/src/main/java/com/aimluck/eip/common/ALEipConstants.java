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
package com.aimluck.eip.common;

import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 * Aimluck EIP の定数です。 <br />
 * 
 */
public class ALEipConstants {

  public static final String ENTITY_ID = "entityid";

  public static final String MODE = "mode";

  public static final String MODE_UPDATE = "update";

  public static final String MODE_DELETE = "delete";

  public static final String MODE_MULTI_DELETE = "multi_delete";

  public static final String MODE_INSERT = "insert";

  public static final String MODE_FORM = "form";

  public static final String MODE_NEW_FORM = "new_form";

  public static final String MODE_EDIT_FORM = "edit_form";

  public static final String MODE_LIST = "list";

  public static final String MODE_DETAIL = "detail";

  public static final String MODE_UPDATE_PASSWD = "update_passwd";

  public static final String PORTLET_ID = "js_peid";

  public static final String MESSAGE_LIST = "msgs";

  public static final String ERROR_MESSAGE_LIST = "errmsgs";

  public static final String RESULT = "result";

  public static final String RESULT_LIST = "results";

  public static final String POST_ENTITY_ID = "_entityid";

  public static final String POST_DATE_YEAR = "_year";

  public static final String POST_DATE_MONTH = "_month";

  public static final String POST_DATE_DAY = "_day";

  public static final String POST_DATE_HOUR = "_hour";

  public static final String POST_DATE_MINUTE = "_minute";

  public static final String LIST_START = "start";

  public static final String LIST_SORT = "sort";

  public static final String LIST_SORT_TYPE = "sorttype";

  public static final String LIST_SORT_TYPE_ASC = "asc";

  public static final String LIST_SORT_TYPE_DESC = "desc";

  public static final String LIST_FILTER = "filter";

  public static final String SEARCH = "search";

  public static final String LIST_FILTER_TYPE = "filtertype";

  public static final String LIST_INDEX = "index";

  public static final String MYGROUP = "mygroup";

  public static final String FACILITYGROUP = "facilitygroup";

  public static final String UTILS = "utils";

  /** 携帯電話の固有 ID のプレフィックス */
  public static final String KEY_CELLULAR_UID = "cellularuid_";

  /** 使用するデータベースサーバ名：PostgreSQL */
  public static final String DB_NAME_POSTGRESQL = "postgresql";

  /** デフォルトエンコーディング */
  public static final String DEF_CONTENT_ENCODING = JetspeedResources
    .getString(JetspeedResources.CONTENT_ENCODING_KEY, "utf-8");

  /** ユーザーの状態：削除 */
  public static final String USER_STAT_DISABLED = "T";

  /** ユーザーの状態：有効 */
  public static final String USER_STAT_ENABLED = "F";

  /** ユーザーの状態：無効 */
  public static final String USER_STAT_NUTRAL = "N";

  /** セキュリティID */
  public static final String SECURE_ID = "secid";

}
