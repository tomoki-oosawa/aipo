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

}
