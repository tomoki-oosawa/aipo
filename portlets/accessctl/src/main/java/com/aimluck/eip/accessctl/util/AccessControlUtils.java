/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.accessctl.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.eip.accessctl.bean.AccessControlFeatureBean;
import com.aimluck.eip.cayenne.om.account.EipMInactiveApplication;
import com.aimluck.eip.cayenne.om.account.EipTAclPortletFeature;
import com.aimluck.eip.cayenne.om.account.EipTAclRole;
import com.aimluck.eip.cayenne.om.account.EipTAclUserRoleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ユーティリティクラスです。 <BR>
 * 
 */
public class AccessControlUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccessControlUtils.class.getName());

  public static final String PORTLET_NAME = "AccessControl";

  /**
   * ロールオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTAclRole getEipTAclRole(RunData rundata, Context context) {
    String aclroleid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (aclroleid == null || Integer.valueOf(aclroleid) == null) {
        // IDが空の場合
        logger.debug("[AccessControlUtils] Empty ID...");
        return null;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipTAclRole.ROLE_ID_PK_COLUMN, aclroleid);
      SelectQuery<EipTAclRole> query = Database.query(EipTAclRole.class, exp);
      List<EipTAclRole> aclroles = query.fetchList();
      if (aclroles == null || aclroles.size() == 0) {
        // 指定したIDのレコードが見つからない場合
        logger.debug("[AccessControlUtils] Not found ID...");
        return null;
      }
      return (aclroles.get(0));
    } catch (Exception ex) {
      logger.error("AccessControlUtils.getEipTAclRole", ex);
      return null;
    }
  }

  public static List<EipTAclUserRoleMap> getEipTAclUserRoleMaps(int aclroleid) {
    try {
      SelectQuery<EipTAclUserRoleMap> query =
        Database.query(EipTAclUserRoleMap.class);

      Expression exp1 =
        ExpressionFactory.matchDbExp(EipTAclUserRoleMap.EIP_TACL_ROLE_PROPERTY
          + "."
          + EipTAclRole.ROLE_ID_PK_COLUMN, aclroleid);
      query.setQualifier(exp1);

      Expression exp2 =
        ExpressionFactory.noMatchExp(EipTAclUserRoleMap.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.DISABLED_PROPERTY, "T");
      query.andQualifier(exp2);

      List<EipTAclUserRoleMap> aclroles = query.fetchList();
      if (aclroles == null || aclroles.size() == 0) {
        // 指定したIDのレコードが見つからない場合
        logger.debug("[AccessControlUtils] Not found ID...");
        return null;
      }
      return aclroles;
    } catch (Exception ex) {
      logger.error("AccessControlUtils.getEipTAclUserRoleMaps", ex);
      return null;
    }
  }

  public static List<AccessControlFeatureBean> getPortletFeatureList() {
    SelectQuery<EipTAclPortletFeature> query =
      Database.query(EipTAclPortletFeature.class);

    List<EipMInactiveApplication> inActiveApplicationList =
      Database.query(EipMInactiveApplication.class).fetchList();

    List<String> inActiveList = new ArrayList<String>();
    for (EipMInactiveApplication model : inActiveApplicationList) {
      inActiveList.add(model.getName().toLowerCase());
    }

    for (String portletName : inActiveList) {
      if (!StringUtils.isEmpty(portletName)) {
        Expression ex =
          ExpressionFactory.notLikeExp(
            EipTAclPortletFeature.FEATURE_NAME_PROPERTY,
            portletName + "%");
        query.andQualifier(ex);
      }
    }

    query.orderAscending(EipTAclPortletFeature.FEATURE_ALIAS_NAME_PROPERTY);

    List<EipTAclPortletFeature> features = query.fetchList();
    if (features == null || features.size() == 0) {
      // 指定したIDのレコードが見つからない場合
      logger.debug("[AccessControlUtils] Not found ID...");
      return null;
    }

    List<AccessControlFeatureBean> list =
      new ArrayList<AccessControlFeatureBean>();

    for (EipTAclPortletFeature feature : features) {
      AccessControlFeatureBean featureInfo = new AccessControlFeatureBean();
      featureInfo.initField();
      featureInfo.setFeatureId(feature.getFeatureId().longValue());
      featureInfo.setFeatureName(feature.getFeatureName());
      featureInfo.setFeatureAliasName(feature.getFeatureAliasName());
      list.add(featureInfo);
    }
    return list;
  }

  public static void setupAcl(int defineAclType, int aclType, ALNumberField acl) {
    if ((aclType & defineAclType) == defineAclType) {
      acl.setValue(1);
    }
  }
}
