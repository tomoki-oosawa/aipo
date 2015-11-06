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

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.social.ALContainerConfigService;
import com.aimluck.eip.services.social.ALSocialApplicationHandler;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class GadgetsContainerAdminFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GadgetsContainerAdminFormData.class.getName());

  private ALNumberField lockedDomainRequired;

  private ALStringField lockedDomainSuffix;

  private ALStringField checkActivityInterval;

  private ALNumberField cacheGadgetXml;

  private ALStringField activitySaveLimit;

  /**
   *
   */
  @Override
  public void initField() {
    lockedDomainRequired = new ALNumberField();
    lockedDomainSuffix = new ALStringField();
    lockedDomainSuffix.setTrim(true);
    lockedDomainSuffix.setFieldName(ALLocalizationUtils
      .getl10n("GADGETS_SETFIELDNAME_LOCKED_DOMAIN_SUFFIX"));
    checkActivityInterval = new ALStringField();
    checkActivityInterval.setTrim(true);
    checkActivityInterval.setFieldName(ALLocalizationUtils
      .getl10n("GADGETS_SETFIELDNAME_CHECK_ACTIVITY_INTERVAL"));
    cacheGadgetXml = new ALNumberField();
    activitySaveLimit = new ALStringField();
    checkActivityInterval.setTrim(true);
    checkActivityInterval.setFieldName(ALLocalizationUtils
      .getl10n("GADGETS_SETFIELDNAME_ACTIVITY_SAVE_LIMIT"));

  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);

    return res;
  }

  /**
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
    lockedDomainSuffix.setNotNull(true);
    lockedDomainSuffix.setCharacterType(ALStringField.TYPE_ASCII);
    lockedDomainSuffix.limitMaxLength(255);
    checkActivityInterval.setNotNull(true);
    activitySaveLimit.setNotNull(true);
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
    if (lockedDomainRequired.getValue() > 0) {
      lockedDomainSuffix.validate(msgList);
    }
    return (msgList.size() == 0);
  }

  /**
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

    lockedDomainRequired.setValue(ALContainerConfigService
      .get(ALSocialApplicationHandler.Property.LOCKED_DOMAIN_REQUIRED)
      .equals("true") ? 1 : 0);
    checkActivityInterval.setValue(ALConfigService
      .get(Property.CHECK_ACTIVITY_INTERVAL));
    lockedDomainSuffix.setValue(ALContainerConfigService
      .get(ALSocialApplicationHandler.Property.LOCKED_DOMAIN_SUFFIX));
    cacheGadgetXml.setValue(ALContainerConfigService.get(
      ALSocialApplicationHandler.Property.CACHE_GADGET_XML).equals("true")
      ? 1
      : 0);
    activitySaveLimit.setValue(ALContainerConfigService
      .get(ALSocialApplicationHandler.Property.ACTIVITY_SAVE_LIMIT));
    return true;
  }

  /**
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

    try {
      long value = lockedDomainRequired.getValue();
      long cache = cacheGadgetXml.getValue();

      ALContainerConfigService.put(
        ALSocialApplicationHandler.Property.LOCKED_DOMAIN_REQUIRED,
        value == 1 ? "true" : "false");

      ALContainerConfigService.put(
        ALSocialApplicationHandler.Property.LOCKED_DOMAIN_SUFFIX,
        value == 1 ? lockedDomainSuffix.getValue() : "");

      ALConfigService.put(
        Property.CHECK_ACTIVITY_INTERVAL,
        checkActivityInterval.getValue());

      ALContainerConfigService.put(
        ALSocialApplicationHandler.Property.CACHE_GADGET_XML,
        cache == 1 ? "true" : "false");

      ALContainerConfigService.put(
        ALSocialApplicationHandler.Property.ACTIVITY_SAVE_LIMIT,
        activitySaveLimit.getValue());

    } catch (Throwable t) {
      logger.error(t, t);
      throw new ALDBErrorException();
    }

    return true;
  }

  /**
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

    try {
      long value = lockedDomainRequired.getValue();
      long cache = cacheGadgetXml.getValue();

      ALContainerConfigService.put(
        ALSocialApplicationHandler.Property.LOCKED_DOMAIN_REQUIRED,
        value == 1 ? "true" : "false");

      ALContainerConfigService.put(
        ALSocialApplicationHandler.Property.LOCKED_DOMAIN_SUFFIX,
        value == 1 ? lockedDomainSuffix.getValue() : "");

      ALConfigService.put(
        Property.CHECK_ACTIVITY_INTERVAL,
        checkActivityInterval.getValue());

      ALContainerConfigService.put(
        ALSocialApplicationHandler.Property.CACHE_GADGET_XML,
        cache == 1 ? "true" : "false");

      ALContainerConfigService.put(
        ALSocialApplicationHandler.Property.ACTIVITY_SAVE_LIMIT,
        activitySaveLimit.getValue());

    } catch (Throwable t) {
      logger.error(t, t);
      throw new ALDBErrorException();
    }

    return true;
  }

  /**
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

    return true;
  }

  /**
   * @return lockedDomainRequired
   */
  public ALNumberField getLockedDomainRequired() {
    return lockedDomainRequired;
  }

  /**
   * @return lockedDomainSuffix
   */
  public ALStringField getLockedDomainSuffix() {
    return lockedDomainSuffix;
  }

  /**
   * @return checkActivityInterval
   */
  public ALStringField getCheckActivityInterval() {
    return checkActivityInterval;
  }

  public ALNumberField getCacheGadgetXml() {
    return cacheGadgetXml;
  }

  public ALStringField getActivitySaveLimit() {
    return activitySaveLimit;
  }
}
