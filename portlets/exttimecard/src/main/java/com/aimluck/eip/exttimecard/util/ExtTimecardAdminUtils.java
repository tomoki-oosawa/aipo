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

package com.aimluck.eip.exttimecard.util;

import static com.aimluck.eip.util.ALLocalizationUtils.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.services.config.ALConfigService;

/**
 *
 */
public class ExtTimecardAdminUtils {

  /**
   * アクセス制限の有効/無効
   */
  public static final String EXTTIMECARD_IP_ENABLED = "config.exttimecard.ip.enabled";

  /**
   * 有効なIPアドレス（1件目〜10件目）
   */
  public static final String EXTTIMECARD_IP_ALLOWED = "config.exttimecard.ip.allowed";

  /**
   * 有効なIPアドレス（11件目〜20件目）
   */
  public static final String EXTTIMECARD_IP_ALLOWED2 = "config.exttimecard.ip.allowed2";

  /**
   * EipMConfigの1レコードに保存するIPアドレス数（valueの最大値が255文字のため、IPアドレスは17までしか保存できない）
   */
  public static final int IP_MAXIMUM_ALLOWED_LENGTH = 10;

  /**
   * IPv4かどうかを検証する
   *
   * @param ip_addresses
   * @param msgList
   * @return
   * @throws ALDBErrorException
   * @throws ALPageNotFoundException
   */
  public static boolean validateDelegate(List<ALStringField> ip_addresses,
      List<String> msgList) throws ALDBErrorException, ALPageNotFoundException {

    String ip_judge =
      "^(((\\d)|([1-9]\\d)|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d)|([1-9]\\d)|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))$";
    Pattern p = Pattern.compile(ip_judge);

    for (ALStringField ip_address : ip_addresses) {
      String ip = ip_address.toString();
      Matcher m = p.matcher(ip);
      if (!(m.find())) {
        msgList.add(getl10n("SYSTEM_SET_IP_ACCESS_VALIDATE2"));
        return false;
      }
    }
    return (msgList.size() == 0);
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

  /**
   * EipMConfigに有効なIPアドレスを保存する
   *
   * @param ip_addresses
   */
  public static void setIpAddresses(List<ALStringField> ip_addresses) {
    int endIndex1 = ip_addresses.size();
    if (endIndex1 > IP_MAXIMUM_ALLOWED_LENGTH) {
      endIndex1 = IP_MAXIMUM_ALLOWED_LENGTH;
    }
    setIpAddresses(ip_addresses, 0, endIndex1, EXTTIMECARD_IP_ALLOWED);

    int endIndex2 = ip_addresses.size();
    if (endIndex2 > IP_MAXIMUM_ALLOWED_LENGTH) {
      setIpAddresses(
        ip_addresses,
        IP_MAXIMUM_ALLOWED_LENGTH,
        endIndex2,
        EXTTIMECARD_IP_ALLOWED2);
    } else {
      ALConfigService.put(EXTTIMECARD_IP_ALLOWED2, "");
    }
  }

  /**
   * @param ip_addresses
   * @param beginIndex
   * @param endIndex
   * @param key
   */
  private static void setIpAddresses(List<ALStringField> ip_addresses,
      int beginIndex, int endIndex, String key) {
    StringBuffer ip_addresses_str = new StringBuffer();
    for (int i = beginIndex; i < endIndex; i++) {
      ALStringField ip_address = ip_addresses.get(i);
      ip_addresses_str.append(ip_address.getValue());
      ip_addresses_str.append(",");
    }
    ip_addresses_str.deleteCharAt(ip_addresses_str.length() - 1);
    ALConfigService.put(key, ip_addresses_str.toString());
  }
}