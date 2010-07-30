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
package com.aimluck.eip.category.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.category.beans.CommonCategoryLiteBean;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有カテゴリのユーティリティクラスです。 <BR>
 *
 */
public class CommonCategoryUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(CommonCategoryUtils.class.getName());

  /**
   *
   * @param dataContext
   * @param category_id
   * @return
   */
  public static EipTCommonCategory getEipTCommonCategory(
      DataContext dataContext, Long category_id) {
    try {

      SelectQuery query = new SelectQuery(EipTCommonCategory.class);

      Expression exp = ExpressionFactory.matchDbExp(
          EipTCommonCategory.COMMON_CATEGORY_ID_PK_COLUMN, category_id);

      query.setQualifier(exp);

      List<?> result = dataContext.performQuery(query);

      if (result == null || result.size() == 0) {
        logger.debug("[CommonCategoryUtils] Not found ID...");
        return null;
      }

      return ((EipTCommonCategory) result.get(0));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  public static EipTCommonCategory getEipTCommonCategory(RunData rundata,
      Context context) {
    String category_id = ALEipUtils.getTemp(rundata, context,
        ALEipConstants.ENTITY_ID);
    try {
      if (category_id == null || Integer.valueOf(category_id) == null) {
        logger.debug("[CommonCategoryUtils] Empty ID...");
        return null;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();

      return getEipTCommonCategory(dataContext, Long.valueOf(category_id));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   *
   * @param rundata
   * @return
   */
  public static List<CommonCategoryLiteBean> getCommonCategoryLiteBeans(
      RunData rundata) {
    List<CommonCategoryLiteBean> list = new ArrayList<CommonCategoryLiteBean>();

    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTCommonCategory.class);

      Expression exp = ExpressionFactory.noMatchDbExp(
          EipTCommonCategory.COMMON_CATEGORY_ID_PK_COLUMN, Integer.valueOf(1));
      query.setQualifier(exp);

      query.addOrdering(EipTCommonCategory.NAME_PROPERTY, true);

      List<?> aList = dataContext.performQuery(query);

      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipTCommonCategory record = (EipTCommonCategory) aList.get(i);
        CommonCategoryLiteBean bean = new CommonCategoryLiteBean();
        bean.initField();
        bean.setCategoryId(record.getCommonCategoryId().longValue());
        bean.setCategoryName(record.getName());
        list.add(bean);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
    return list;
  }

  /**
   * アクセス権限をチェックします。
   *
   * @return
   */
  public static boolean CheckPermission(RunData rundata, Context context,
      int defineAclType, String pfeature) {

    if (defineAclType == 0) {
      return true;
    }

    if (pfeature == null || "".equals(pfeature)) {
      return true;
    }

    ALAccessControlFactoryService aclservice = (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    boolean hasAuthority = aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata), pfeature, defineAclType);

    return hasAuthority;
  }
}
