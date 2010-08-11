/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.news;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.AipoLicense;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.license.util.LicenseUtils;
import com.aimluck.eip.orm.DatabaseOrmService;

/**
 */
public class NewsSelectData extends ALAbstractSelectData<AipoLicense> {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(NewsSelectData.class.getName());

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<AipoLicense> selectList(RunData rundata, Context context) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected AipoLicense selectDetail(RunData rundata, Context context) {
    // ライセンス
    AipoLicense al = LicenseUtils.getAipoLicense(rundata, context);

    // ユーザ数
    SelectQuery query = new SelectQuery(TurbineUser.class);
    DataContext datacontext = DatabaseOrmService.getInstance().getDataContext();
    Expression exp = ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY,
        "F");
    query.setQualifier(exp);

    List<?> list = datacontext.performQuery(query);

    context.put("UserNum", (String.valueOf(list.size() - 2)));

    return al;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(AipoLicense obj) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(AipoLicense obj) {
    return obj.getLicense();
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    return null;
  }

}
