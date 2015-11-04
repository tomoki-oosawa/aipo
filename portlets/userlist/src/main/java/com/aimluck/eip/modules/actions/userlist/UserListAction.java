/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
package com.aimluck.eip.modules.actions.userlist;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.userlist.UserSelectData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーザー名簿のアクションクラスです。
 *
 */
public class UserListAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(UserListAction.class.getName());

  /**
   *
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    // セッションのクリア
    clearUserListSession(rundata, context);
    UserSelectData listData = new UserSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.setStrLength(0);
    listData.doSelectList(this, rundata, context);
    setTemplate(rundata, "userlist.vm");
  }

  /**
   * 最大化表示の際の処理を記述します。
   *
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    // トップ画面の検索キーワードを削除
    ALEipUtils.removeTemp(rundata, context, "sword");
    try {
      UserSelectData listData = new UserSelectData();
      listData.initField();
      listData.setRowsNum(Integer.parseInt(portlet
        .getPortletConfig()
        .getInitParameter("p1b-rows")));
      listData.setStrLength(0);
      listData.doViewList(this, rundata, context);
      setTemplate(rundata, "userlist-list.vm");
    } catch (Exception ex) {
      logger.error("userlist", ex);
    }
  }

  private void clearUserListSession(RunData rundata, Context context) {
    String LIST_FILTER_STR =
      new StringBuffer().append(UserSelectData.class.getSimpleName()).append(
        ALEipConstants.LIST_FILTER).toString();
    List<String> list = new ArrayList<String>();
    list.add(LIST_FILTER_STR);
    list.add("sword");
    ALEipUtils.removeTemp(rundata, context, list);
  }
}
