/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2017 TOWN, Inc.
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

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.util.ALLocalizationUtils;

import info.bliki.commons.validator.routines.InetAddressValidator;

/**
 *
 */
public class ExtTimecardAdminFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(
      ExtTimecardAdminFormData.class.getName());

  private String enabledIpFlag;

  private String allowedIp;

  private String allowedIp2;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   *
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    enabledIpFlag = rundata.getParameters().getString("enabled_ip");
    allowedIp = rundata.getParameters().getString("allowed_ip");
    allowedIp2 = rundata.getParameters().getString("allowed_ip2");
  }

  /**
   * 各フィールドを初期化します。
   */
  @Override
  public void initField() {

  }

  /**
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {

  }

  /**
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {

    // 入力されたIPアドレスが有効なものかどうか
    if ((!allowedIp.equals("") && !isValidIpAddress(allowedIp))
      || (!allowedIp2.equals("") && !isValidIpAddress(allowedIp2))) {
      msgList.add(
        ALLocalizationUtils.getl10n("EXTTIMECARD_INVALID_IP_MESSAGE"));
    }

    // IPアドレス制限を設けていて、かつIPアドレスを入力しているかどうか
    if (!enabledIpFlag.equals("T")) {
      if ((allowedIp == null || allowedIp.length() == 0)
        && (allowedIp2 == null || allowedIp2.length() == 0)) {
        msgList.add(
          ALLocalizationUtils.getl10n("EXTTIMECARD_INVALID_IP_MESSAGE"));
      }
    }
    return msgList.size() == 0;
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    ALConfigService.put(Property.EXTTIMECARD_IP_ENABLED, enabledIpFlag);
    ALConfigService.put(Property.EXTTIMECARD_IP_ALLOWED, allowedIp);
    ALConfigService.put(Property.EXTTIMECARD_IP_ALLOWED2, allowedIp2);
    return true;
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
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
}
