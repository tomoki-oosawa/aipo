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
package com.aimluck.eip.category.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.category.beans.CommonCategoryLiteBean;
import com.aimluck.eip.cayenne.om.portlet.EipTCommonCategory;
import com.aimluck.eip.cayenne.om.portlet.EipTScheduleMap;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
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
   * 指定されたIDの共有カテゴリを取得します。
   * 
   * @param category_id
   * @return
   */
  public static EipTCommonCategory getEipTCommonCategory(Long category_id) {
    try {
      EipTCommonCategory result =
        Database.get(EipTCommonCategory.class, category_id);

      if (result == null) {
        logger.debug("[CommonCategoryUtils] Not found ID...");
        return null;
      }
      return result;
    } catch (Exception ex) {
      logger.error("CommonCategoryUtils.getEipTCommonCategory", ex);
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
    String category_id =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (category_id == null || Integer.valueOf(category_id) == null) {
        logger.debug("[CommonCategoryUtils] Empty ID...");
        return null;
      }
      return getEipTCommonCategory(Long.valueOf(category_id));
    } catch (Exception ex) {
      logger.error("CommonCategoryUtils.getEipTCommonCategory", ex);
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
      SelectQuery<EipTCommonCategory> query =
        Database.query(EipTCommonCategory.class);

      Expression exp =
        ExpressionFactory.noMatchDbExp(
          EipTCommonCategory.COMMON_CATEGORY_ID_PK_COLUMN,
          Integer.valueOf(1));
      query.setQualifier(exp).orderAscending(EipTCommonCategory.NAME_PROPERTY);

      List<EipTCommonCategory> commoncategory_list = query.fetchList();

      for (EipTCommonCategory record : commoncategory_list) {
        CommonCategoryLiteBean bean = new CommonCategoryLiteBean();
        bean.initField();
        bean.setCategoryId(record.getCommonCategoryId().longValue());
        bean.setCategoryName(record.getName());
        list.add(bean);
      }
    } catch (Exception ex) {
      logger.error("CommonCategoryUtils.getCommonCategoryLiteBeans", ex);
    }
    return list;
  }

  /**
   * アクセス権限をチェックします。
   * 
   * @return
   */
  public static boolean checkPermission(RunData rundata, Context context,
      int defineAclType, String pfeature) {

    if (defineAclType == 0) {
      return true;
    }

    if (pfeature == null || "".equals(pfeature)) {
      return true;
    }

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    boolean hasAuthority =
      aclhandler.hasAuthority(
        ALEipUtils.getUserId(rundata),
        pfeature,
        defineAclType);

    return hasAuthority;
  }

  /**
   * 指定された共有カテゴリ内の SchaduleMap を「その他」にカテゴリ変更します。
   * 
   * @param category
   */
  public static void setDefaultCommonCategoryToSchedule(
      EipTCommonCategory category) {
    SelectQuery<EipTScheduleMap> query = Database.query(EipTScheduleMap.class);
    Expression exp =
      ExpressionFactory.matchExp(
        EipTScheduleMap.COMMON_CATEGORY_ID_PROPERTY,
        category.getCommonCategoryId());
    List<EipTScheduleMap> schedulemap_list =
      query.andQualifier(exp).fetchList();
    if (schedulemap_list != null) {
      EipTCommonCategory tmpCategory =
        CommonCategoryUtils.getEipTCommonCategory(Long.valueOf(1));
      for (EipTScheduleMap record : schedulemap_list) {
        record.setEipTCommonCategory(tmpCategory);
      }
    }
  }
}
