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

package com.aimluck.eip.cellular.util;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedUserManagement;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALCellularUtils;

/**
 */
public class CellularUtils {
  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellularUtils.class.getName());

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
      logger.error("Exception", ex);
    }
    return result;
  }

  public static String getUrl(String ip, int port, String servername) {
    if (ip == null || ip.length() == 0 || port == -1) {
      return "";
    }

    String protocol =
      JetspeedResources.getString("access.url.protocol", "http");
    StringBuffer url = new StringBuffer();
    if (port == 80) {
      url.append(protocol).append("://").append(ip).append("/").append(
        servername).append("/");
    } else {
      url
        .append(protocol)
        .append("://")
        .append(ip)
        .append(":")
        .append(port)
        .append("/")
        .append(servername)
        .append("/");
    }

    return url.toString();
  }

  public static String getCellularUrl(RunData rundata, Context context) {
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

    String servername = rundata.getServletConfig().getServletName();
    String key =
      baseUser.getUserName()
        + "_"
        + ALCellularUtils.getCheckValueForCellLogin(
          baseUser.getUserName(),
          baseUser.getUserId());
    EipMCompany record = CellularUtils.getEipMCompany(rundata, context);
    String domain =
      CellularUtils.getUrl(
        record.getIpaddress(),
        record.getPort().intValue(),
        servername);
    if (domain != null && domain.length() > 0) {
      String endword;
      String company_id =
        rundata.getParameters().getString(DatabaseOrmService.ORG_PRE, "");
      if (company_id == null || "".equals(company_id)) {
        endword = "";
      } else {
        endword = "portal/org/" + company_id + "/";
      }

      url =
        CellularUtils.getUrl(
          record.getIpaddress(),
          record.getPort().intValue(),
          servername)
          + endword
          + "?key="
          + key;
    } else {
      url = "";
    }
    return url;
  }
}
