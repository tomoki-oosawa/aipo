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
public class SystemNetworkFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemNetworkFormData.class.getName());

  /** IP アドレス（グローバル） */
  private ALStringField ipaddress;

  /** ポート番号 */
  private ALNumberField port;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractFormData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * 各フィールドを初期化する．
   * 
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    // IP アドレス（グローバル）
    ipaddress = new ALStringField();
    ipaddress.setFieldName("IPアドレス");
    ipaddress.setTrim(true);

    // ポート番号
    port = new ALNumberField();
    port.setFieldName("ポート番号");
    port.setValue(80);

  }

  /**
   * 各フィールドに対する制約条件を設定する．
   * 
   * @see com.aimluck.eip.common.ALAbstractFormData#setValidator()
   */
  @Override
  protected void setValidator() {
    // IP アドレス（グローバル）
    ipaddress.setNotNull(true);
    // ポート番号
    port.setNotNull(true);
    port.limitValue(1, 65535);
  }

  /**
   * フォームに入力されたデータの妥当性を検証します．
   * 
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#validate(java.util.ArrayList)
   */
  @Override
  protected boolean validate(List<String> msgList) {

    ipaddress.validate(msgList);
    port.validate(msgList);

    return (msgList.size() == 0);
  }

  /**
   * 
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#loadFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
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

      // IP アドレス（グローバル）
      ipaddress.setValue(record.getIpaddress());
      // ポート番号
      port.setValue(record.getPort().longValue());

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#insertFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
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

      // IP アドレス（グローバル）
      record.setIpaddress(ipaddress.getValue());
      // ポート番号
      record.setPort(Integer.valueOf((int) port.getValue()));

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
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @see com.aimluck.eip.common.ALAbstractFormData#deleteFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * IP アドレスを取得する．
   * 
   * @return
   */
  public ALStringField getIpaddress() {
    return ipaddress;
  }

  /**
   * ポート番号を取得する．
   * 
   * @return
   */
  public ALNumberField getPort() {
    return port;
  }

}
