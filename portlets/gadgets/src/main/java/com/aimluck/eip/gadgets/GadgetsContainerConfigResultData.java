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
package com.aimluck.eip.gadgets;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * 
 */
public class GadgetsContainerConfigResultData implements ALData {

  private ALStringField lockedDomainRequired;

  private ALStringField lockedDomainSuffix;

  private ALStringField unLockedDomain;

  private ALStringField checkActivityInterval;

  private ALStringField cacheGadgetXml;

  private ALStringField activitySaveLimit;

  @Override
  public void initField() {
    lockedDomainRequired = new ALStringField();
    lockedDomainSuffix = new ALStringField();
    unLockedDomain = new ALStringField();
    checkActivityInterval = new ALStringField();
    cacheGadgetXml = new ALStringField();
    activitySaveLimit = new ALStringField();
  }

  /**
   * @return lockedDomainRequired
   */
  public ALStringField getLockedDomainRequired() {
    return lockedDomainRequired;
  }

  /**
   * @param lockedDomainRequired
   *          セットする lockedDomainRequired
   */
  public void setLockedDomainRequired(String lockedDomainRequired) {
    this.lockedDomainRequired.setValue(lockedDomainRequired);
  }

  /**
   * @return lockedDomainSuffix
   */
  public ALStringField getLockedDomainSuffix() {
    return lockedDomainSuffix;
  }

  /**
   * @param lockedDomainSuffix
   *          セットする lockedDomainSuffix
   */
  public void setLockedDomainSuffix(String lockedDomainSuffix) {
    this.lockedDomainSuffix.setValue(lockedDomainSuffix);
  }

  /**
   * @return unLockedDomain
   */
  public ALStringField getUnLockedDomain() {
    return unLockedDomain;
  }

  /**
   * @param unLockedDomain
   *          セットする unLockedDomain
   */
  public void setUnLockedDomain(String unLockedDomain) {
    this.unLockedDomain.setValue(unLockedDomain);
  }

  /**
   * @return checkActivityInterval
   */
  public ALStringField getCheckActivityInterval() {
    return checkActivityInterval;
  }

  /**
   * @param checkActivityInterval
   *          セットする checkActivityInterval
   */
  public void setCheckActivityInterval(String checkActivityInterval) {
    this.checkActivityInterval.setValue(checkActivityInterval);
  }

  /**
   * @return cacheGadgetXml
   */
  public ALStringField getCacheGadgetXml() {
    return cacheGadgetXml;
  }

  /**
   * @param cacheGadgetXml
   *          セットする cacheGadgetXml
   */
  public void setCacheGadgetXml(String cacheGadgetXml) {
    this.cacheGadgetXml.setValue(cacheGadgetXml);
  }

  /**
   * @param activitySaveLimit
   *          セットする activitySaveLimit
   */
  public void setActivitySaveLimit(String activitySaveLimit) {
    this.activitySaveLimit.setValue(activitySaveLimit);
  }

  /**
   * @return activitySaveLimit
   */
  public ALStringField getActivitySaveLimit() {
    return activitySaveLimit;
  }
}
