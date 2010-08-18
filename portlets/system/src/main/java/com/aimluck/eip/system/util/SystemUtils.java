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
package com.aimluck.eip.system.util;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.portlet.EipMMybox;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;

/**
 */
public class SystemUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemUtils.class.getName());

  /** Webアプリケーションサーバのポート番号 */
  private static final int WEBAPPSERVER_PORT = JetspeedResources.getInt(
    "aipo.webappserver.port", 80);

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMMybox getEipMMybox(String company_id) {
    EipMMybox result = null;
    try {
      if (company_id == null || Integer.valueOf(company_id) == null) {
        logger.debug("Empty ID...");
        return result;
      }

      Expression exp = ExpressionFactory.matchDbExp(
        EipMMybox.EIP_MCOMPANY_PROPERTY + "."
          + EipMCompany.COMPANY_ID_PK_COLUMN, Integer.valueOf(company_id));

      List<EipMMybox> list = Database.query(EipMMybox.class, exp).perform();
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

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipMCompany getEipMCompany(RunData rundata, Context context) {
    EipMCompany result = null;
    String id = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (id == null || Integer.valueOf(id) == null) {
        logger.debug("Empty ID...");
        return result;
      }

      Expression exp = ExpressionFactory.matchDbExp(
        EipMCompany.COMPANY_ID_PK_COLUMN, Integer.valueOf(id));

      List<EipMCompany> list = Database.query(EipMCompany.class, exp).perform();
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

  /**
   * Webアプリケーションサーバのポート番号を取得する。
   * 
   * @return
   */
  public static int getServerPort() {
    return WEBAPPSERVER_PORT;
  }

  /**
   * 
   * @param ip
   * @param port
   * @param servername
   *          Webアプリケーション名
   * @return
   */
  public static String getUrl(String ip, int port, String servername) {
    if (ip == null || ip.length() == 0 || port == -1) {
      return "";
    }

    String protocol = JetspeedResources
      .getString("access.url.protocol", "http");

    StringBuffer url = new StringBuffer();

    if (port == 80) {
      url.append(protocol).append("://").append(ip).append("/").append(
        servername).append("/");
    } else {
      url.append(protocol).append("://").append(ip).append(":").append(port)
        .append("/").append(servername).append("/");
    }

    return url.toString();
  }
}
