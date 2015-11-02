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
package com.aimluck.eip.cellular.util;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedUserManagement;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALServletUtils;

/**
 */
public class CellularUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellularUtils.class.getName());

  public static final String CABINET_PORTLET_NAME = "Cellular";

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMCompany getEipMCompany(RunData rundata, Context context) {
    EipMCompany result = null;

    String id = "1";
    try {
      if (id == null || Integer.valueOf(id) == null) {
        logger.debug("Empty ID...");
        return result;
      }

      SelectQuery<EipMCompany> query = Database.query(EipMCompany.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipMCompany.COMPANY_ID_PK_COLUMN, Integer
          .valueOf(id));
      query.setQualifier(exp);
      List<EipMCompany> list = query.fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return result;
      }
      result = list.get(0);
    } catch (Exception ex) {
      logger.error("cellular", ex);
    }
    return result;
  }

  public static String getCellularUrl(RunData rundata, Context context) {

    String loginUrl = ALConfigService.get(Property.EXTERNAL_LOGIN_URL);

    if (loginUrl != null && loginUrl.length() > 0) {
      return loginUrl;
    }

    String url;

    ALBaseUser baseUser;
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    try {
      // 最新のユーザ情報を取得する．
      baseUser =
        (ALBaseUser) JetspeedUserManagement.getUser(new UserNamePrincipal(jdata
          .getJetspeedUser()
          .getUserName()));
    } catch (JetspeedSecurityException e) {
      baseUser = (ALBaseUser) rundata.getUser();
    }

    String key =
      baseUser.getUserName()
        + "_"
        + ALCellularUtils.getCheckValueForCellLogin(
          baseUser.getUserName(),
          baseUser.getUserId());
    EipMCompany record = CellularUtils.getEipMCompany(rundata, context);
    String accessUrl =
      ALServletUtils.getAccessUrl(record.getIpaddress(), record
        .getPort()
        .intValue(), true);
    if (accessUrl != null && accessUrl.length() > 0) {
      url = new StringBuilder(accessUrl).append("?key=").append(key).toString();
    } else {
      url = "社外から『Aipo』 にアクセスするためのアドレスが設定されていません。";
    }
    return url;
  }
}
