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
package com.aimluck.eip.schedule;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュール詳細表示の検索結果を管理するクラスです。
 * 
 */
public class CellScheduleSelectByMemberData extends CellScheduleSelectData {

  /** <code>logger</code> logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellScheduleSelectByMemberData.class.getName());

  /** <code>login_user</code> 表示対象ユーザー */
  private ALEipUser targerUser;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    // 表示対象ユーザー取得
    String s = rundata.getParameters().getString("selectedmember");
    if (s != null) {
      targerUser = ALEipUtils.getALEipUser(Integer.parseInt(s));
    }
  }

  public ALEipUser getTargerUser() {
    return targerUser;
  }

  public void setTargerUser(ALEipUser targerUser) {
    this.targerUser = targerUser;
  }

  public String getAliasNameListText() {
    return ALLocalizationUtils.getl10nFormat(
      "SCHEDULE_GO_ONEDAY_LIST",
      targerUser.getAliasName().toString());
  }

  public String getAliasNameDetailText() {
    return ALLocalizationUtils.getl10nFormat("SCHEDULE_GO_DETAIL", targerUser
      .getAliasName()
      .toString());
  }

  public String getAliasNameText() {
    return ALLocalizationUtils.getl10nFormat("SCHEDULE_GO_SCHEDULE", targerUser
      .getAliasName()
      .toString());
  }
}
