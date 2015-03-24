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

import java.util.Calendar;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.EipTEventlog;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.eventlog.util.ALEventlogUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.eventlog.ALEventlogHandler;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ログ保存ハンドラ．
 * 
 */
public class ALActionEventlogHandler extends ALEventlogHandler {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALActionEventlogHandler.class.getName());

  public ALActionEventlogHandler() {
  }

  public static ALEventlogHandler getInstance() {
    return new ALActionEventlogHandler();
  }

  /**
   * ログ
   */
  @Override
  public void log(int entity_id, int portlet_type, String note) {
    logActionEvent(entity_id, portlet_type, note);
  }

  /**
   * ログ
   */
  @Override
  public void log(int entity_id, int portlet_type, String note, String mode) {
    logActionEvent(entity_id, portlet_type, note, mode);
  }

  private void logActionEvent(int entity_id, int portlet_type, String note) {

    // rundataの取得
    RunData rundata = ALEventlogFactoryService.getInstance().getRunData();

    // MODEの取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    if (mode == null || "".equals(mode)) {

      // actionのパラメータを使う
      String action = rundata.getAction();
      if (action == null || "".equals(action)) {
        return;
      }
    } else {
      logActionEvent(entity_id, portlet_type, note, mode);
    }
  }

  private void logActionEvent(int entity_id, int portlet_type, String note,
      String mode) {

    // rundataの取得
    RunData rundata = ALEventlogFactoryService.getInstance().getRunData();

    // EVENTTYPEの取得
    int event_type = ALEventlogUtils.getEventTypeValue(mode);

    // ユーザーIDの取得
    int uid = ALEipUtils.getUserId(rundata);

    // 接続IPアドレスの取得
    String ip_addr = rundata.getRemoteAddr();

    // ログを保存
    saveEvent(event_type, uid, portlet_type, entity_id, ip_addr, note);
  }

  /**
   * Login処理
   * 
   * @param mode
   * @return
   */
  @Override
  public void logLogin(int userid) {
    // rundataの取得
    RunData rundata = ALEventlogFactoryService.getInstance().getRunData();

    int event_type = ALEventlogUtils.getEventTypeValue("Login");
    int p_type = ALEventlogConstants.PORTLET_TYPE_LOGIN;

    // 接続IPアドレスの取得
    String ip_addr = rundata.getRemoteAddr();

    saveEvent(event_type, userid, p_type, 0, ip_addr, null);
  }

  /**
   * Logout処理
   * 
   * @param mode
   * @return
   */
  @Override
  public void logLogout(int userid) {
    // rundataの取得
    RunData rundata = ALEventlogFactoryService.getInstance().getRunData();

    int event_type = ALEventlogUtils.getEventTypeValue("Logout");
    int p_type = ALEventlogConstants.PORTLET_TYPE_LOGOUT;

    // 接続IPアドレスの取得
    String ip_addr = rundata.getRemoteAddr();

    saveEvent(event_type, userid, p_type, 0, ip_addr, null);
  }

  /**
   * XLS出力処理
   * 
   * @param mode
   * @return
   */
  @Override
  public void logXlsScreen(int userid, String Note, int _p_type) {
    // rundataの取得
    RunData rundata = ALEventlogFactoryService.getInstance().getRunData();

    int event_type = ALEventlogUtils.getEventTypeValue("xls_screen");

    // 接続IPアドレスの取得
    String ip_addr = rundata.getRemoteAddr();

    saveEvent(event_type, userid, _p_type, 0, ip_addr, null);
  }

  /**
   * 
   * @param event_type
   *          イベント種別
   * @param uid
   *          ユーザーID
   * @param p_type
   *          ポートレットTYPE
   * @param note
   * @return
   */
  protected boolean saveEvent(int event_type, int uid, int p_type,
      int entity_id, String ip_addr, String note) {
    try {

      // 新規オブジェクトモデル
      EipTEventlog log = Database.create(EipTEventlog.class);

      TurbineUser tuser = Database.get(TurbineUser.class, Integer.valueOf(uid));
      // ユーザーID
      log.setTurbineUser(tuser);
      // イベント発生日
      log.setEventDate(Calendar.getInstance().getTime());
      // イベントTYPE
      log.setEventType(Integer.valueOf(event_type));
      // ポートレットTYPE
      log.setPortletType(Integer.valueOf(p_type));
      // エンティティID
      log.setEntityId(Integer.valueOf(entity_id));
      // 接続IPアドレス
      log.setIpAddr(ip_addr);
      // 作成日
      log.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      log.setUpdateDate(Calendar.getInstance().getTime());
      // note
      log.setNote(note);

      Database.commit();

      return true;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("ALActionEventlogHandler.saveEvent", ex);
      return false;
    }
  }

  /**
   * mode を DB に保存するための数値に変換します。
   * 
   * @param mode
   * @return
   */
  @Override
  public int getEventTypeValue(String mode) {
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
  @Override
  public String getEventAliasName(int eventType) {
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
  @Override
  public String getPortletAliasName(int portletType) {

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
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_GPDB) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_GPDB;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_ACCESSCTL) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_ACCESSCTL;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_FACILITY) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_FACILITY;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_GADGET) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_GADGET;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_WIKI) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_WIKI;
    } else if (portletType == ALEventlogConstants.PORTLET_TYPE_PROJECT) {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_PROJECT;
    } else {
      return ALActionEventlogConstants.PORTLET_TYPE_STR_STR_NONE;
    }
  }
}
