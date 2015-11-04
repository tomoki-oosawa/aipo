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
package com.aimluck.eip.modules.actions.note;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.note.NoteGroupSelectData;

/**
 * 伝言メモの送信先に指定できるグループの取り扱いに関するアクションクラスです。 
 */
public class NoteGroupAction extends NoteAction {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(NoteGroupAction.class.getName());

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
    NoteGroupSelectData listData = new NoteGroupSelectData();
    listData.initField();
    // PSMLからパラメータをロードする
    // 最大表示件数（通常時）
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "notegroup");
  }

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    super.buildMaximizedContext(portlet, context, rundata);
  }

}
