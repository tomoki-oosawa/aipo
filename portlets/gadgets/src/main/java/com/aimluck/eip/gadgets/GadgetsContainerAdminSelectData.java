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
package com.aimluck.eip.gadgets;

import java.util.jar.Attributes;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.social.ALContainerConfigService;
import com.aimluck.eip.services.social.ALSocialApplicationHandler;

/**
 * 
 */
public class GadgetsContainerAdminSelectData
    extends
    ALAbstractSelectData<GadgetsContainerConfigResultData, GadgetsContainerConfigResultData> {

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<GadgetsContainerConfigResultData> selectList(
      RunData rundata, Context context) {
    return null;
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected GadgetsContainerConfigResultData selectDetail(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    GadgetsContainerConfigResultData rd =
      new GadgetsContainerConfigResultData();
    rd.initField();

    rd.setLockedDomainRequired(ALContainerConfigService
      .get(ALSocialApplicationHandler.Property.LOCKED_DOMAIN_REQUIRED));
    rd.setCheckActivityInterval(ALConfigService
      .get(Property.CHECK_ACTIVITY_INTERVAL));
    rd.setLockedDomainSuffix(ALContainerConfigService
      .get(ALSocialApplicationHandler.Property.LOCKED_DOMAIN_SUFFIX));
    rd.setCacheGadgetXml(ALContainerConfigService
      .get(ALSocialApplicationHandler.Property.CACHE_GADGET_XML));
    rd.setActivitySaveLimit(ALContainerConfigService
      .get(ALSocialApplicationHandler.Property.ACTIVITY_SAVE_LIMIT));

    return rd;
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected GadgetsContainerConfigResultData getResultData(
      GadgetsContainerConfigResultData model) throws ALPageNotFoundException,
      ALDBErrorException {
    return model;
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected GadgetsContainerConfigResultData getResultDataDetail(
      GadgetsContainerConfigResultData model) throws ALPageNotFoundException,
      ALDBErrorException {
    return model;
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}
