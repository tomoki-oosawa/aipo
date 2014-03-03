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

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTWiki;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.wiki.WikiFormData;
import com.aimluck.eip.wiki.WikiSelectData;
import com.aimluck.eip.wiki.util.WikiUtils;

/**
 * Wikiの内部リンクを処理するクラスです。 <br />
 * 
 */
public class WikiInternalLinkScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiInternalLinkScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    String name = rundata.getParameters().getString("name", "");
    name = WikiUtils.convertEncording(name);

    String parentId = rundata.getParameters().getString("parentId", "");

    if (StringUtils.isEmpty(name) || StringUtils.isEmpty(parentId)) {
      throw new ALPageNotFoundException("some parameter is empty");
    }

    try {
      EipTWiki destWiki = WikiUtils.getEipTWiki(name, parentId);
      EipTWiki parentWiki = WikiUtils.getEipTWiki(Integer.valueOf(parentId));
      if (null == destWiki) {
        ALEipUtils.removeTemp(rundata, context, ALEipConstants.ENTITY_ID);
        WikiFormData formData = new WikiFormData();
        formData.initField();
        formData.setParentWiki(parentWiki);
        formData.setDestWikiName(name);
        formData.loadTopWikiList(rundata, context);
        formData.doViewForm(this, rundata, context);
        String layout_template = "portlets/html/ja/ajax-wiki-form.vm";
        setTemplate(rundata, context, layout_template);
      } else {
        ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, String
          .valueOf(destWiki.getWikiId()));
        WikiSelectData detailData = new WikiSelectData();
        detailData.initField();
        detailData.loadTopWikiList(rundata, context);
        detailData.doViewDetail(this, rundata, context);
        String layout_template = "portlets/html/ja/ajax-wiki.vm";
        setTemplate(rundata, context, layout_template);
      }
    } catch (Exception e) {
      logger.error("WikiInternalLinkScreen.doOutput", e);
      ALEipUtils.redirectDBError(rundata);
    }
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return WikiUtils.WIKI_PORTLET_NAME;
  }

}
