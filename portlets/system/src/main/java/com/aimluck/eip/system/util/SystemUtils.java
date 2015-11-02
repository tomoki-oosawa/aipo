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
package com.aimluck.eip.system.util;

import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALServletUtils;

/**
 *
 */
public class SystemUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemUtils.class.getName());
	
  public static final String COMPANY_PORTLET_NAME = "Company";

  /** Webアプリケーションサーバのポート番号 */
  private static final int WEBAPPSERVER_PORT = JetspeedResources.getInt(
    "aipo.webappserver.port",
    80);

  public static final String SYSTEM_PORTLET_NAME = "System";




  /**
   * セッション中のエンティティIDで示されるユーザ情報を取得する。 論理削除されたユーザを取得した場合はnullを返す。
   *
   * @param rundata
   * @param context
   * @return
   */
  public static ALBaseUser getBaseUser(RunData rundata, Context context) {
    String userid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (userid == null) {
        logger.debug("Empty ID...");
        return null;
      }
      ALBaseUser user = (ALBaseUser) JetspeedSecurity.getUser(userid);
      // 削除済みユーザの取得は行わない。
      // By Haruo Kaneko
      if ("T".equals(user.getDisabled())) {
        return null;
      } else {
        return (ALBaseUser) JetspeedSecurity.getUser(userid);
      }
    } catch (UnknownUserException uex) {
      logger.error("UnknownUserException : UserID = " + userid);
      return null;
    } catch (Exception ex) {
      logger.error("system", ex);
      return null;
    }
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

      Expression exp =
        ExpressionFactory.matchDbExp(EipMCompany.COMPANY_ID_PK_COLUMN, Integer
          .valueOf(id));

      List<EipMCompany> list =
        Database.query(EipMCompany.class, exp).fetchList();
      if (list == null || list.size() == 0) {
        logger.debug("Not found ID...");
        return result;
      }
      result = list.get(0);
    } catch (Exception ex) {
      logger.error("system", ex);
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
   * @see ALServletUtils#getAccessUrl(String, int, boolean)
   * @param ip
   * @param port
   * @param servername
   * @param isGlobal
   * @return
   * @deprecated
   */
  @Deprecated
  public static String getUrl(String ip, int port, String servername,
      boolean isGlobal) {
    if (ip == null || ip.length() == 0 || port == -1) {
      return "";
    }

    String protocol =
      isGlobal
        ? ALConfigService.get(Property.ACCESS_GLOBAL_URL_PROTOCOL)
        : ALConfigService.get(Property.ACCESS_LOCAL_URL_PROTOCOL);

    StringBuffer url = new StringBuffer();

    if (port == 80 || port == 443) {
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
}
