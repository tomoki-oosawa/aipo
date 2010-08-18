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
package com.aimluck.eip.system;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.system.util.SystemUtils;

/**
 *
 */
public class SystemNetworkSelectData extends
    ALAbstractSelectData<EipMCompany, EipMCompany> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemNetworkSelectData.class.getName());

  private String servername;

  private String endword;

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected List<EipMCompany> selectList(RunData rundata, Context context) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  @Override
  protected EipMCompany selectDetail(RunData rundata, Context context) {
    servername = rundata.getServletConfig().getServletName();

    String company_id = rundata.getParameters().getString(
      DatabaseOrmService.ORG_PRE, "");
    if (company_id == null || "".equals(company_id)) {
      endword = "";
    } else {
      endword = "portal/org/" + company_id + "/";
    }
    return SystemUtils.getEipMCompany(rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  @Override
  protected Object getResultData(EipMCompany record) {
    return null;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  @Override
  protected Object getResultDataDetail(EipMCompany record) {

    // Aipoサイト情報の取得
    String localurl = "";
    SystemNetworkResultData rd = new SystemNetworkResultData();
    rd.initField();

    try {
      String ipaddress = record.getIpaddressInternal();
      if (null == ipaddress || "".equals(ipaddress)) {
        Enumeration<NetworkInterface> enuIfs = NetworkInterface
          .getNetworkInterfaces();
        if (null != enuIfs) {
          while (enuIfs.hasMoreElements()) {
            NetworkInterface ni = enuIfs.nextElement();
            Enumeration<InetAddress> enuAddrs = ni.getInetAddresses();
            while (enuAddrs.hasMoreElements()) {
              InetAddress in4 = enuAddrs.nextElement();
              if (!in4.isLoopbackAddress()) {
                ipaddress = in4.getHostAddress();
              }
            }
          }
        }
      }

      Integer port_internal = record.getPortInternal();
      if (null == port_internal) {
        // port_internal = SystemUtils.getServerPort();
        port_internal = 80;
      }

      localurl = SystemUtils.getUrl(ipaddress, port_internal, servername)
        + endword;
      // InetAddress.getLocalHost().getHostAddress(), SystemUtils
      // .getServerPort(), servername) + endword;

      String globalurl = SystemUtils.getUrl(record.getIpaddress(), record
        .getPort().intValue(), servername)
        + endword;

      rd.setLocalUrl(localurl);
      rd.setGlobalUrl(globalurl);

      // } catch (UnknownHostException e) {
      // logger.error(e);
    } catch (SocketException e) {
      logger.error(e);
    }
    return rd;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}
