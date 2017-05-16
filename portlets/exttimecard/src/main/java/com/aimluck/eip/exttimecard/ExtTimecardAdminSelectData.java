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
package com.aimluck.eip.exttimecard;

import info.bliki.commons.validator.routines.InetAddressValidator;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.jar.Attributes;

import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.exttimecard.util.ExtTimecardAdminUtils;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.config.ALConfigHandler;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.util.CustomizeUtils;

/**
 *
 */
public class ExtTimecardAdminSelectData extends
    ALAbstractSelectData<PortletEntry, PortletEntry> implements ALData {

  private ALStringField enabled_ip;

  private ALStringField allowed_ip;

  private ALStringField allowed_ip2;

  private String ip = "";

  /**
   *
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    enabled_ip = new ALStringField();
    allowed_ip = new ALStringField();
    allowed_ip2 = new ALStringField();

    enabled_ip.setValue(ALConfigService
      .get(ALConfigHandler.Property.EXTTIMECARD_IP_ENABLED));
    allowed_ip.setValue(ALConfigService
      .get(ALConfigHandler.Property.EXTTIMECARD_IP_ALLOWED));
    allowed_ip2.setValue(ALConfigService
      .get(ALConfigHandler.Property.EXTTIMECARD_IP_ALLOWED2));

    if (isValidIpAddress(rundata.getRemoteAddr())) {
      ip = rundata.getRemoteAddr();
    }
  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<PortletEntry> selectList(RunData rundata, Context context) {
    return null;
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(PortletEntry record) {
    return null;
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(PortletEntry record)
      throws ALPageNotFoundException, ALDBErrorException {
    ExtTimecardAdminDetailResultData rd =
      new ExtTimecardAdminDetailResultData(record);
    rd.initField();
    return rd;
  }

  @Override
  public PortletEntry selectDetail(RunData rundata, Context context)
      throws ALDBErrorException, ALPageNotFoundException {
    return CustomizeUtils.getPortletEntry(
      rundata,
      ExtTimecardUtils.EXTTIMECARD_PORTLET_NAME);
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

  /**
   * @return
   */
  public String getEnabledIpFlag() {
    return enabled_ip.toString();
  }

  /**
   *
   * @return
   */
  public String getAllowedIp() {
    return allowed_ip.toString();
  }

  /**
   *
   * @return
   */
  public String getAllowedIp2() {
    return allowed_ip2.toString();
  }

  /**
   *
   * @return ip
   */
  public String getIp() {
    return ip;
  }

  protected boolean isValidIpAddress(String address) {
    if (address == null || address.length() == 0) {
      return false;
    }
    if (InetAddressValidator.getInstance().isValid(address)) {
      return true;
    }

    try {
      return InetAddress.getByName(address) instanceof Inet6Address;
    } catch (final UnknownHostException ex) {
      return false;
    }
  }

  /**
   * EipMConfigから有効なIPアドレスを取得する
   *
   * @param defaultValue
   * @return
   */
  public static String[] getIpAddresses() {
    String ip_address_values1_str =
      ALConfigService.get(ExtTimecardAdminUtils.EXTTIMECARD_IP_ALLOWED, "");

    String ip_address_values2_str =
      ALConfigService.get(ExtTimecardAdminUtils.EXTTIMECARD_IP_ALLOWED2, "");

    String ip_address_values_str;
    if ("".equals(ip_address_values2_str)) {
      ip_address_values_str = ip_address_values1_str;
    } else {
      ip_address_values_str =
        ip_address_values1_str + "," + ip_address_values2_str;
    }

    return ip_address_values_str.split(",");

  }

}
