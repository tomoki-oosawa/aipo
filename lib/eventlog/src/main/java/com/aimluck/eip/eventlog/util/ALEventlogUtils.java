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

package com.aimluck.eip.eventlog.util;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.eventlog.action.ALActionEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;

/**
 *
 */
public class ALEventlogUtils {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEventlogUtils.class.getName());

  /**
   * mode を DB に保存するための数値に変換します。
   * 
   * @param mode
   * @return
   */
  public static int getEventTypeValue(String mode) {
    if (ALActionEventlogConstants.EVENT_MODE_DETAIL.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_DETAIL;
    } else if (ALActionEventlogConstants.EVENT_MODE_INSERT.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_INSERT;
    } else if (ALActionEventlogConstants.EVENT_MODE_LIST.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_LIST;
    } else if (ALActionEventlogConstants.EVENT_MODE_FORM.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_FORM;
    } else if (ALActionEventlogConstants.EVENT_MODE_NEW_FORM.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_NEW_FORM;
    } else if (ALActionEventlogConstants.EVENT_MODE_EDIT_FORM.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_EDIT_FORM;
    } else if (ALActionEventlogConstants.EVENT_MODE_UPDATE.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_UPDATE;
    } else if (ALActionEventlogConstants.EVENT_MODE_MULTI_DELETE.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_MULTI_DELETE;
    } else if (ALActionEventlogConstants.EVENT_MODE_DELETE.equals(mode)
      || ALActionEventlogConstants.EVENT_MODE_DELETE_REPLY.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_DELETE;
    } else if (ALActionEventlogConstants.EVENT_MODE_LOGIN.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_LOGIN;
    } else if (ALActionEventlogConstants.EVENT_MODE_LOGOUT.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_LOGOUT;
    } else if (ALActionEventlogConstants.EVENT_MODE_ACCEPT.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_ACCEPT;
    } else if (ALActionEventlogConstants.EVENT_MODE_DENIAL.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_DENIAL;
    } else if (ALActionEventlogConstants.EVENT_MODE_PUNCHIN.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_PUNCHIN;
    } else if (ALActionEventlogConstants.EVENT_MODE_PUNCHOUT.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_PUNCHOUT;
    } else if (ALActionEventlogConstants.EVENT_MODE_XLS_SCREEN.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_XLS_SCREEN;
    } else if (ALActionEventlogConstants.EVENT_MODE_UPDATE_PASSWORD
      .equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_UPDATE_PASSWORD;
    } else if (ALActionEventlogConstants.EVENT_MODE_DOWNLOAD.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_DOWNLOAD;
    } else if (ALActionEventlogConstants.EVENT_MODE_STARTGUIDE.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_STARTGUIDE;
    } else if (ALActionEventlogConstants.EVENT_MODE_COMMENT.equals(mode)) {
      return ALActionEventlogConstants.EVENT_TYPE_COMMENT;
    }
    return ALActionEventlogConstants.EVENT_TYPE_NONE;
  }

  /**
   * イベントのエイリアス名を取得します。
   * 
   * @param eventType
   * @return
   */
  public static String getEventAliasName(int eventType) {
    int type = ALActionEventlogConstants.EVENT_TYPE_NONE;

    if (eventType > 0
      && eventType < ALActionEventlogConstants.EVENT_ALIAS_NAME.length) {
      type = eventType;
    }

    return ALActionEventlogConstants.EVENT_ALIAS_NAME[type];
  }

  /**
   * ポートレットのエイリアス名を取得します。
   * 
   * @param eventType
   * @return
   */
  public static String getPortletAliasName(int portletType) {

    if (portletType == ALEventlogConstants.PORTLET_TYPE_NONE) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_STR_NONE;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_LOGIN) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_LOGIN;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_LOGOUT) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_LOGOUT;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ACCOUNT) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_ACCOUNT;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_SYSTEM) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_SYSTEM;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_AJAXSCHEDULEWEEKLY) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_AJAXSCHEDULEWEEKLY;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_BLOG_ENTRY) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_BLOG_ENTRY;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_BLOG_THEMA) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_BLOG_THEMA;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WORKFLOW) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_WORKFLOW;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WORKFLOW_CATEGORY) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_WORKFLOW_CATEGORY;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WORKFLOW_ROUTE) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_WORKFLOW_ROUTE;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TODO) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_TODO;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TODO_CATEGORY) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_TODO_CATEGORY;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_NOTE) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_NOTE;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TIMECARD) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_TIMECARD;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TIMECARD_XLS_SCREEN) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_TIMECARD_XLS_SCREEN;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_ADDRESSBOOK;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_COMPANY) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_ADDRESSBOOK_COMPANY;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_GROUP) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_ADDRESSBOOK_GROUP;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MEMO) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_MEMO;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MSGBOARD_TOPIC) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_MSGBOARD;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MSGBOARD_CATEGORY) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_MSGBOARD_CATEGORY;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_EXTERNALSEARCH) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_EXTERNALSEARCH;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MYLINK) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_MYLINK;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WHATSNEW) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_WHATSNEW;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_CABINET_FILE) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_CABINET_FILE;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_CABINET_FOLDER) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_CABINET_FOLDER;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WEBMAIL
      || portletType == ALEventlogConstants.PORTLET_TYPE_WEBMAIL_ACCOUNT) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_WEBMAIL;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WEBMAIL_FOLDER) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_WEBMAIL_FOLDER;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WEBMAIL_FILTER) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_WEBMAIL_FILTER;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_SCHEDULE) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_SCHEDULE;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MANHOUR) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_MANHOUR;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ACCOUNTPERSON) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_ACCOUNTPERSON;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_MYGROUP) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_MYGROUP;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_PAGE) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_PAGE;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_CELLULAR) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_CELLULAR;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_COMMON_CATEGORY) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_COMMON_CATEGORY;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_EXTTIMECARD) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_EXTTIMECARD;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_EXTTIMECARD_SYSTEM) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_EXTTIMECARD_SYSTEM;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_REPORT) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_REPORT;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_REPORT) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_REPORT;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_TIMELINE) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_TIMELINE;
    } else {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_STR_NONE;
    }
  }

  /**
   * ポートレットIDからそのポートレットのPSMLのparentの文字列を取得する
   * 
   * @param rundata
   * @param portletEntryId
   * @return
   */
  public static String getPortletName(RunData rundata, String portletEntryId) {
    try {
      Portlets portlets =
        ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
      if (portlets == null) {
        return null;
      }

      Portlets[] portletList = portlets.getPortletsArray();
      if (portletList == null) {
        return null;
      }

      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          if (entries[j].getId().equals(portletEntryId)) {
            return entries[j].getParent();
          }
        }
      }
    } catch (Exception ex) {
      logger.error("ALEventlogUtils.getPortletName", ex);
      return null;
    }
    return null;
  }
}
