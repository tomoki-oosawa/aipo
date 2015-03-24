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
package com.aimluck.eip.accessctl;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipTAclRole;
import com.aimluck.eip.cayenne.om.account.EipTAclUserRoleMap;
import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * ロールの複数削除を行うためのクラスです。 <BR>
 * 
 */
public class AccessControlMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccessControlMultiDelete.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList) {
    try {

      SelectQuery<EipTAclRole> query = Database.query(EipTAclRole.class);
      Expression exp =
        ExpressionFactory.inDbExp(EipTAclRole.ROLE_ID_PK_COLUMN, values);
      query.setQualifier(exp);
      List<EipTAclRole> roles = query.fetchList();
      if (roles == null || roles.size() == 0) {
        return false;
      }

      SelectQuery<EipTAclUserRoleMap> EipTAclUserRoleMapSQL =
        Database.query(EipTAclUserRoleMap.class);
      EipTAclUserRoleMapSQL.andQualifier(ExpressionFactory.inDbExp(
        EipTAclUserRoleMap.ROLE_ID_COLUMN,
        values));
      List<EipTAclUserRoleMap> userRoleMaps = EipTAclUserRoleMapSQL.fetchList();

      Database.deleteAll(userRoleMaps);
      Database.deleteAll(roles);

      Database.commit();

      // イベントログに保存
      for (EipTAclRole role : roles) {
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          role.getRoleId(),
          ALEventlogConstants.PORTLET_TYPE_ACCESSCTL,
          ALLocalizationUtils.getl10nFormat("ACCESSCTL_EVENTLOG_DELETE", role
            .getRoleName()));
      }
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccessControlMultiDelete.action", ex);
      return false;
    }
    return true;
  }

}
