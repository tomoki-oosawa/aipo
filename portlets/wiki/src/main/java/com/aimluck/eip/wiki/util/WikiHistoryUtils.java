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

package com.aimluck.eip.wiki.util;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import com.aimluck.eip.cayenne.om.portlet.EipTWikiHistory;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * WikiHistoryのユーティリティクラスです。 <BR>
 * 
 */
public class WikiHistoryUtils {
  /**
   * versionを取得
   * 
   * @param wikiId
   * @return
   */
  public static int getVersion(Integer wikiId) {
    SelectQuery<EipTWikiHistory> query = Database.query(EipTWikiHistory.class);
    Expression exp =
      ExpressionFactory.matchExp(EipTWikiHistory.WIKI_ID_PROPERTY, wikiId);
    query.setQualifier(exp);
    query.orderDesending(EipTWikiHistory.VERSION_PROPERTY);
    EipTWikiHistory history = query.fetchSingle();
    return history.getVersion() + 1;
  }
}
