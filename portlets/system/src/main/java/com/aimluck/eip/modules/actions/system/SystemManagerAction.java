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
package com.aimluck.eip.modules.actions.system;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.system.SystemNetworkFormData;
import com.aimluck.eip.system.SystemNetworkSelectData;
import com.aimluck.eip.system.SystemVersionSelectData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * システム管理画面を制御するアクションクラスです。
 * 
 */
public class SystemManagerAction extends ALBaseAction {

  /**
   *
   */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(SystemManagerAction.class.getName());

  /**
   * 
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {
    if (getMode() == null) {
      doSystem_info_network(rundata, context);
    }
  }

  /**
   * ネットワーク情報を表示する
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doSystem_info_network(RunData rundata, Context context)
      throws Exception {
    ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, "1");

    SystemNetworkSelectData detailData = new SystemNetworkSelectData();
    detailData.initField();
    detailData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "system");
  }

  /**
   * ネットワーク情報を登録するフォームを表示する
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doSystem_form_network(RunData rundata, Context context)
      throws Exception {
    ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, "1");
    SystemNetworkFormData formData = new SystemNetworkFormData();
    formData.initField();
    if (formData.doViewForm(this, rundata, context)) {
      setTemplate(rundata, "system-form-network");
    } else {
      doSystem_info_network(rundata, context);
    }
  }

  /**
   * ネットワーク情報を更新する
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doSystem_update_network(RunData rundata, Context context)
      throws Exception {
    SystemNetworkFormData formData = new SystemNetworkFormData();
    formData.initField();
    if (formData.doUpdate(this, rundata, context)) {
      doSystem_info_network(rundata, context);
    } else {
      setTemplate(rundata, "system-form-network");
    }
  }

  /**
   * バージョン情報を表示する
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doSystem_version(RunData rundata, Context context)
      throws Exception {
    // ALEipUtils.setTemp(rundata, context, ALEipConstants.ENTITY_ID, "1");
    SystemVersionSelectData detailData = new SystemVersionSelectData();
    detailData.initField();
    detailData.doViewDetail(this, rundata, context);
    setTemplate(rundata, "system-version");
  }
}
