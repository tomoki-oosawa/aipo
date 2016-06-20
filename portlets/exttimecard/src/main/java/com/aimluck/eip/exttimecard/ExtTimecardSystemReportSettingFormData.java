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

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.exttimecard.util.ExtTimecardUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.services.config.ALConfigHandler;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class ExtTimecardSystemReportSettingFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ExtTimecardSystemReportSettingFormData.class.getName());

  private ALStringField type;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   *
   */
  @Override
  public void initField() {
    type = new ALStringField();
    type.setFieldName(ALLocalizationUtils.getl10n("EXTTIMECARD_REPORT_TYPE"));

  }

  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      String version =
        ALConfigService.get(ALConfigHandler.Property.EXTTIMECARD_VERTION);
      type.setValue(version);

    } catch (Exception ex) {
      logger.error("exttimecard", ex);
      return false;
    }
    return true;
  }

  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    try {
      String version = type.getValue();
      ALConfigService
        .put(ALConfigHandler.Property.EXTTIMECARD_VERTION, version);
    } catch (Exception ex) {
      logger.error("ExtTimecardSystemReportSettingFormData", ex);
      return false;
    }
    return true;
  }

  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    return false;
  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);

    return res;
  }

  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
  }

  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {

    return (msgList.size() == 0);
  }

  /**
   * 詳細データを取得する抽象メソッドです。
   *
   * @param rundata
   * @param context
   * @return
   */
  protected Object selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  public ALStringField getType() {
    return type;
  }

  public boolean isNewRule() {
    return ExtTimecardUtils.isNewRule();
  }

}
