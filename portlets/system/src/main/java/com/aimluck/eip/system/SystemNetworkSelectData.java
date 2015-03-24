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
package com.aimluck.eip.system;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.system.util.SystemUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALServletUtils;

/**
 *
 */
public class SystemNetworkSelectData extends
    ALAbstractSelectData<EipMCompany, EipMCompany> {

  private static final String SAMPLE_USER1 = "sample1";

  private static final String SAMPLE_USER2 = "sample2";

  private static final String SAMPLE_USER3 = "sample3";

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemNetworkSelectData.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipMCompany> selectList(RunData rundata, Context context) {
    return null;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipMCompany selectDetail(RunData rundata, Context context) {
    return SystemUtils.getEipMCompany(rundata, context);
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultData(EipMCompany record) {
    return null;
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipMCompany record) {

    // Aipoサイト情報の取得
    SystemNetworkResultData rd = new SystemNetworkResultData();
    rd.initField();

    try {
      String ipaddress = record.getIpaddressInternal();
      if (null == ipaddress || "".equals(ipaddress)) {
        Enumeration<NetworkInterface> enuIfs =
          NetworkInterface.getNetworkInterfaces();
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
        port_internal = 80;
      }

      String localurl =
        ALServletUtils.getAccessUrl(ipaddress, port_internal, false);

      String globalurl =
        ALServletUtils.getAccessUrl(record.getIpaddress(), record
          .getPort()
          .intValue(), true);

      rd.setLocalUrl(localurl);
      rd.setGlobalUrl(globalurl);

      // サンプルデータの有無
      try {
        String[] sampleName = { SAMPLE_USER1, SAMPLE_USER2, SAMPLE_USER3 };
        String[] sampleId = { "4", "5", "6" };

        SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
        Expression exp1 =
          ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, sampleName);
        Expression exp2 =
          ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");
        Expression exp3 =
          ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, sampleId);
        query.setQualifier(exp1.andExp(exp2).andExp(exp3));

        List<ALEipUser> list = ALEipUtils.getUsersFromSelectQuery(query);
        if (list.size() <= 0) {
          rd.setSample("");
        } else {
          rd.setSample(SystemNetworkResultData.EXIST);
        }

      } catch (Exception ex) {
        logger.error("system", ex);
        rd.setSample(SystemNetworkResultData.EXIST);
        return null;
      }

    } catch (SocketException e) {
      logger.error("[SystemNetworkSelectData]", e);
    }
    return rd;
  }

  /**
   * 
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}
