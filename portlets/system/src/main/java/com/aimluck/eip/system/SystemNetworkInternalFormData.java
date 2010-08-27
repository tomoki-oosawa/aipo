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
import java.util.Enumeration;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.system.util.SystemUtils;

/**
 * 『ネットワーク情報』のフォームデータを管理するクラス．
 * 
 */
public class SystemNetworkInternalFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemNetworkFormData.class.getName());

  /** IP アドレス（ローカル） */
  private ALStringField ipaddress_internal;

  /** ポート番号（ローカル） */
  private ALNumberField port_internal;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * 各フィールドを初期化する
   * 
   */
  public void initField() {
    // IP アドレス（ローカル）
    ipaddress_internal = new ALStringField();
    ipaddress_internal.setFieldName("IPアドレス");
    ipaddress_internal.setTrim(true);

    // ポート番号（ローカル）
    port_internal = new ALNumberField();
    port_internal.setFieldName("ポート番号");
    port_internal.setValue(80);
  }

  /**
   * 各フィールドに対する制約条件を設定する
   */
  @Override
  protected void setValidator() {
    // IP アドレス（グローバル）
    ipaddress_internal.setNotNull(true);
    // ポート番号
    port_internal.setNotNull(true);
    port_internal.limitValue(1, 65535);
  }

  /**
   * フォームに入力されたデータの妥当性を検証します
   * 
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {

    ipaddress_internal.validate(msgList);
    port_internal.validate(msgList);

    return (msgList.size() == 0);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMCompany record = SystemUtils.getEipMCompany(rundata, context);
      if (record == null) {
        return false;
      }

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
      // IP アドレス（ローカル）
      ipaddress_internal.setValue(ipaddress);
      // ポート番号（ローカル）
      port_internal.setValue(record.getPortInternal().longValue());

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMCompany record = SystemUtils.getEipMCompany(rundata, context);
      if (record == null) {
        return false;
      }

      // IP アドレス（ローカル）
      record.setIpaddressInternal(ipaddress_internal.getValue());
      // ポート番号（ローカル）
      record.setPortInternal(Integer.valueOf((int) port_internal.getValue()));

      // 会社を更新
      Database.commit();

      // singletonの更新
      ALEipManager.getInstance().reloadCompany();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * IP アドレス（ローカル）を取得する．
   * 
   * @return
   */
  public ALStringField getIpaddressInternal() {
    return ipaddress_internal;
  }

  /**
   * ポート番号（ローカル）を取得する．
   * 
   * @return
   */
  public ALNumberField getPortInternal() {
    return port_internal;
  }

}
