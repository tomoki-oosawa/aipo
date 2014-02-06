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

package com.aimluck.eip.modules.actions.wiki;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.WikiMultiDelete;
import com.aimluck.eip.wiki.WikiSelectData;

/**
 * Wikiのアクションクラスです。 <BR>
 * 
 */
public class WikiAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiAction.class.getName());

  /**
   * 通常表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    // セッション情報のクリア
    clearWikiSession(rundata, context);
    WikiSelectData listData = new WikiSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "wiki");
  }

  /**
   * 最大化表示の際の処理を記述します。 <BR>
   * 
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);

    try {
      if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doWiki_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doWiki_list(rundata, context);
      }
      if (getMode() == null) {
        doWiki_list(rundata, context);
      }
      assert (false);
    } catch (Exception ex) {
      logger.error("todo", ex);
    }
  }

  private void clearWikiSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    ALEipUtils.removeTemp(rundata, context, list);
  }

  /**
   * Wikiを削除します。（複数） <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWiki_multi_delete(RunData rundata, Context context)
      throws Exception {
    WikiMultiDelete delete = new WikiMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doWiki_list(rundata, context);
    // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    // rundata.setRedirectURI(jsLink.getPortletById(
    // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
    // "eventSubmit_doTodo_list", "1").toString());
    // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    // jsLink = null;
  }

  /**
   * Wikiを一覧表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWiki_list(RunData rundata, Context context) throws Exception {
    WikiSelectData listData = new WikiSelectData();
    listData.initField();
    listData.loadCategoryList(rundata);

    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "wiki-list");
  }

  /**
   * Wikiを詳細表示します。 <BR>
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doWiki_detail(RunData rundata, Context context) throws Exception {
    WikiSelectData detailData = new WikiSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "wiki-detail");
    } else {
      doWiki_list(rundata, context);
    }
  }

}