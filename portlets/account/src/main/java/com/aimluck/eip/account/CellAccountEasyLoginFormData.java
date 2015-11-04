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
package com.aimluck.eip.account;

import java.util.List;

import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.JetspeedUserManagement;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cellular.util.CellularUtils;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 */
public class CellAccountEasyLoginFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CellAccountEasyLoginFormData.class.getName());

  /** 携帯電話の固有 ID */
  private ALStringField cellular_uid;

  /** 携帯電話からのアクセス用キー */
  private String key;

  private ALBaseUser baseUser;

  private boolean enableEasyLogin = false;

  private String cellular_url;

  /**
   * 初期化します。
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    try {
      JetspeedRunData jdata = (JetspeedRunData) rundata;
      try {
        // 最新のユーザ情報を取得する．
        baseUser =
          (ALBaseUser) JetspeedUserManagement.getUser(new UserNamePrincipal(
            jdata.getJetspeedUser().getUserName()));
      } catch (JetspeedSecurityException e) {
        baseUser = (ALBaseUser) rundata.getUser();
      }

      cellular_url = CellularUtils.getCellularUrl(rundata, context);
      String uid = baseUser.getCelluarUId();
      enableEasyLogin = (uid != null && uid.length() > 0);
    } catch (Exception e) {
      logger.error("CellAccountEasyLoginFormData.init", e);
      ALEipUtils.redirectPageNotFound(rundata);
      return;
    }

  }

  /**
   *
   */
  @Override
  public void initField() {
    // 携帯電話の固有 ID
    cellular_uid = new ALStringField();
    cellular_uid.setFieldName("携帯電話の固有 ID");
    cellular_uid.setTrim(true);
  }

  /**
   *
   */
  @Override
  protected void setValidator() {
    // 携帯電話の固有 ID
    cellular_uid.setNotNull(true);
    cellular_uid.setCharacterType(ALStringField.TYPE_ALPHABET_NUMBER);
    cellular_uid.limitMaxLength(50);
  }

  /**
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {
    cellular_uid.validate(msgList);
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
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 携帯電話の固有 ID
      baseUser.setCelluarUId(cellular_uid.getValue());

      // ユーザー情報を更新
      JetspeedSecurity.saveUser(baseUser);
    } catch (Exception ex) {
      logger.error("CellAccountEasyLoginFormData.insertFormData", ex);
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
  protected boolean updateFormData(RunData rundata, Context context,
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
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // 携帯電話の固有 ID
      baseUser.setCelluarUId("");

      // ユーザー情報を更新
      JetspeedSecurity.saveUser(baseUser);
    } catch (Exception ex) {
      logger.error("CellAccountEasyLoginFormData.deleteFormData", ex);
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
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);

    if (res) {
      String celluid = ALCellularUtils.getCellularUid(rundata);
      cellular_uid.setValue(celluid);

      key =
        baseUser.getUserName()
          + "_"
          + ALCellularUtils.getCheckValueForCellLogin(
            baseUser.getUserName(),
            baseUser.getUserId());
    }

    return res;
  }

  public ALStringField getCellularUid() {
    return cellular_uid;
  }

  public boolean enableEasyLogin() {
    return enableEasyLogin;
  }

  public String getKey() {
    return key;
  }

  public String getCellularUrl() {
    return cellular_url;
  }

}
