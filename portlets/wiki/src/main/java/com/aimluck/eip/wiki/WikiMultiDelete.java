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
package com.aimluck.eip.wiki;

import static com.aimluck.eip.util.ALLocalizationUtils.*;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTWiki;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.wiki.util.WikiFileUtils;
import com.aimluck.eip.wiki.util.WikiUtils;

/**
 * 
 */
public class WikiMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiMultiDelete.class.getName());

  /**
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      Expression exp1 =
        ExpressionFactory.inDbExp(EipTWiki.WIKI_ID_PK_COLUMN, values);

      List<EipTWiki> wikiList =
        Database.query(EipTWiki.class, exp1).fetchList();
      if (wikiList == null || wikiList.size() == 0) {
        return false;
      }

      for (EipTWiki wiki : wikiList) {

        // entityIdを取得
        Integer entityId = wiki.getWikiId();

        if (WikiUtils.getChildCount(entityId) > 0) {
          msgList.add(getl10n("WIKI_HAS_CHILDREN"));
          return false;
        }

        // Wiki名を取得
        String wikiName = wiki.getWikiName();
        // 添付ファイルの削除
        WikiFileUtils.deleteFiles(wiki.getWikiId());

        // Wikiを削除
        Database.delete(wiki);
        Database.commit();

        // ログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          entityId,
          ALEventlogConstants.PORTLET_TYPE_WIKI,
          wikiName);
      }
    } catch (Throwable t) {
      Database.rollback();
      logger.error("WikiMultiDelete.action", t);
      return false;
    }
    return true;
  }

}
