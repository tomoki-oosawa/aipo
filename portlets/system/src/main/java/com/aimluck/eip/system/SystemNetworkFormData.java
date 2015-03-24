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
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.system.util.SystemUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 『ネットワーク情報』のフォームデータを管理するクラス．
 * 
 */
public class SystemNetworkFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemNetworkFormData.class.getName());

  private ALStringField protocol;

  /** IP アドレス（グローバル） */
  private ALStringField ipaddress;

  /** ポート番号 */
  private ALNumberField port;

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
  @Override
  public void initField() {

    protocol = new ALStringField();
    protocol.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_PROTOCOL"));
    protocol.setTrim(true);

    // IP アドレス（グローバル）
    ipaddress = new ALStringField();
    ipaddress.setFieldName(ALLocalizationUtils
      .getl10n("SYSTEM_SETFIELDNAME_IPADDRESS"));
    ipaddress.setTrim(true);

    // ポート番号
    port = new ALNumberField();
    port.setFieldName(ALLocalizationUtils.getl10n("SYSTEM_SETFIELDNAME_PORT"));
    port.setValue(80);

  }

  /**
   * 各フィールドに対する制約条件を設定する
   */
  @Override
  protected void setValidator() {
    protocol.setNotNull(true);
    // IP アドレス（グローバル）
    ipaddress.setNotNull(true);
    // ポート番号
    port.setNotNull(true);
    port.limitValue(1, 65535);
  }

  /**
   * フォームに入力されたデータの妥当性を検証します
   * 
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {

    protocol.validate(msgList);
    ipaddress.validate(msgList);
    port.validate(msgList);

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

      protocol.setValue(ALConfigService
        .get(Property.ACCESS_GLOBAL_URL_PROTOCOL));

      // IP アドレス（グローバル）
      ipaddress.setValue(record.getIpaddress());
      // ポート番号
      port.setValue(record.getPort().longValue());

    } catch (Exception ex) {
      logger.error("system", ex);
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

      ALConfigService.put(Property.ACCESS_GLOBAL_URL_PROTOCOL, protocol
        .getValue());

      // IP アドレス（グローバル）
      record.setIpaddress(ipaddress.getValue());
      // ポート番号
      record.setPort(Integer.valueOf((int) port.getValue()));

      // 会社を更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        record.getCompanyId(),
        ALEventlogConstants.PORTLET_TYPE_SYSTEM,
        "社外アドレスを"
          + protocol.getValue()
          + "://"
          + ipaddress.getValue()
          + ":"
          + (int) port.getValue()
          + "に更新");

      // singletonの更新
      ALEipManager.getInstance().reloadCompany();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("system", ex);
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
   * 
   * @return
   */
  public ALStringField getProtocol() {
    return protocol;
  }

  /**
   * IP アドレスを取得する
   * 
   * @return
   */
  public ALStringField getIpaddress() {
    return ipaddress;
  }

  /**
   * ポート番号を取得する
   * 
   * @return
   */
  public ALNumberField getPort() {
    return port;
  }

}
