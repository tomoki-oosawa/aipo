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

package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.schedule.CellAppScheduleSelectData;

public class CellAppScheduleScreen extends ALCSVScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ScheduleScreen.class.getName());

  /** 無効 */
  private static final String DEF_REMOTE_NONE = "none";

  /** 有効（全てのリモートから許可） */
  private static final String DEF_REMOTE_ALL = "all";

  /**
   * 接続元のIPアドレスによる制限（IPアドレス設定） 無効：none 有効（全てのリモートから許可）：all
   * 有効（特定のリモートから許可）；192.168.1 や 192.168.1.10
   * 
   */
  private static final String TRUST_IP_ADDR = JetspeedResources.getString(
    "aipo.httpio.trustip_addr",
    "");

  @Override
  protected void doOutput(RunData rundata) throws Exception {

    String remoteAddr = rundata.getRemoteAddr();
    if (remoteAddr == null
      || "".equals(remoteAddr)
      || "".equals(TRUST_IP_ADDR)
      || DEF_REMOTE_NONE.equals(TRUST_IP_ADDR)) {
      return;
    }

    if (!DEF_REMOTE_ALL.equals(TRUST_IP_ADDR)
      && !remoteAddr.startsWith(TRUST_IP_ADDR)) {
      return;
    }

    setCsvEncoding("UTF-8");
    super.doOutput(rundata);
  }

  @Override
  protected String getCSVString(RunData rundata) throws Exception {

    try {
      CellAppScheduleSelectData listData = new CellAppScheduleSelectData();
      // セッションから情報をセット
      listData.init(rundata);
      //
      if (listData.validate()) {
        // DBから情報を取得し、データを整える
        if (!listData.getViewList(rundata)) {
          // have not data
          return null;
        }
        // CSV用にデータを整える
        listData.doFormatCsv();
        // CSV用に出力する
        return listData.outPutCsv();
      }
      return null;
    } catch (Exception e) {
      logger.error("[ERROR]" + e);
      return null;
    }
  }

  @Override
  protected String getFileName() {
    return "schedule.csv";
  }

}