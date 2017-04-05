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

import static com.aimluck.eip.util.ALLocalizationUtils.*;
import info.bliki.commons.validator.routines.InetAddressValidator;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.exttimecard.util.ExtTimecardAdminUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class ExtTimecardAdminFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardAdminFormData.class.getName());

  private ALStringField enabled_ip;

  private ALStringField allowed_ip;

  private ALStringField allowed_ip2;

  /** <code></code> アクセス制限の有効/無効 */
  private ALNumberField is_enabled;

  /** <code>ip_address</code> 有効なIPアドレス */
  private List<ALStringField> ip_addresses;

  /** <code>myip</code> 接続元のIPアドレス */
  private String myip;

  public List<ALStringField> getIpAddresses() {
    return ip_addresses;
  }

  public boolean getIsEnabled() {
    return 1 == is_enabled.getValue();
  }

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
    ip_addresses = new ArrayList<ALStringField>();

    enabled_ip = new ALStringField();
    allowed_ip = new ALStringField();
    allowed_ip2 = new ALStringField();

    myip = rundata.getRemoteAddr();
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
   */
  @Override
  protected boolean validate(List<String> msgList) {

    try {
      // 入力されたIPアドレスが有効なものかどうか
      ExtTimecardAdminUtils.validateDelegate(ip_addresses, msgList);

      // IPアドレス制限を設けていて、かつIPアドレスを入力しているかどうか
      if (enabled_ip.toString().equals("T")) {
        if (ip_addresses.size() == 0) {
          msgList.add(ALLocalizationUtils
            .getl10n("EXTTIMECARD_INVALID_IP_MESSAGE"));
        }
      }

    } catch (RuntimeException ex) {
      logger.error("exttimecard", ex);
      return false;
    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return false;
    }
    return (msgList.size() == 0);
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

    this.is_enabled.setValue("true".equals(ALConfigService.get(
      ExtTimecardAdminUtils.EXTTIMECARD_IP_ALLOWED,
      "false")) ? 1 : 0);

    String[] ip_address_values = ExtTimecardAdminUtils.getIpAddresses();
    if (ip_address_values != null) {
      for (String value : ip_address_values) {
        if (value != null && !"".equals(value)) {
          appendIpAddresses(value);
        }
      }
    }

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

    ALConfigService.put(Property.EXTTIMECARD_IP_ENABLED, "T".equals(enabled_ip
      .toString()) ? "T" : "F");

    ALConfigService.put(Property.EXTTIMECARD_IP_ALLOWED, allowed_ip.toString());
    ALConfigService.put(Property.EXTTIMECARD_IP_ALLOWED2, allowed_ip2
      .toString());

    // 有効なIPアドレスを保存
    ExtTimecardAdminUtils.setIpAddresses(ip_addresses);

    // イベントログに保存
    ALBaseUser user = AccountUtils.getBaseUser(rundata, context);
    ALEventlogFactoryService.getInstance().getEventlogHandler().log(
      Integer.valueOf(user.getUserId()),
      ALEventlogConstants.PORTLET_TYPE_GADGET,
      ALLocalizationUtils
        .getl10nFormat("EXTTIMECARD_ADMIN_RESTRICT_IP_CHANGED"));

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

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      try {
        String ip_address_values[] =
          rundata.getParameters().getStrings("allowed_ip");
        if (ip_address_values != null) {
          for (String value : ip_address_values) {
            if (value != null && !"".equals(value)) {
              appendIpAddresses(value);
            }
          }
        }

        return res;

      } catch (Exception ex) {
        logger.error("ExtTimecardAdminFormData.setFormData", ex);
      }
    }
    return res;
  }

  private void appendIpAddresses(String value) {
    ALStringField ip_address = new ALStringField();
    ip_address.setFieldName(getl10n("SYSTEM_PERMITTED_IP"));
    ip_address.setValue(value);
    ip_addresses.add(ip_address);
  }
}
