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
package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.WikiSelectData;
import com.aimluck.eip.wiki.util.WikiUtils;

/**
 *
 */
public class WikiScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiScreen.class.getName());

  /**
   * 出力する
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @throws Exception
   *           例外
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    try {
      ParameterParser parser = rundata.getParameters();
      ALEipUtils.passPSML(rundata, context, "p12f-filters", parser
        .getString(ALEipConstants.LIST_FILTER));
      ALEipUtils.passPSML(rundata, context, "p12g-filtertypes", parser
        .getString(ALEipConstants.LIST_FILTER_TYPE));
      WikiSelectData listData = new WikiSelectData();
      listData.initField();
      listData.loadTopWikiList(rundata, context);
      listData.doViewDetailOne(this, rundata, context);
      String layout_template = "portlets/html/ajax-wiki.vm";
      setTemplate(rundata, context, layout_template);
    } catch (Exception ex) {
      logger.error("WikiScreen.doOutput", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * ポートレット名取得
   * 
   * @return ポートレット名
   */
  @Override
  protected String getPortletName() {
    return WikiUtils.WIKI_PORTLET_NAME;
  }

}
