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
package com.aimluck.eip.services.quota.impl;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.services.quota.ALQuotaHandler;

/**
 *
 */
public class ALEmptyQuotaHandler extends ALQuotaHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALEmptyQuotaHandler.class.getName());

  private static ALQuotaHandler instance;

  public static ALQuotaHandler getInstance() {
    if (instance == null) {
      instance = new ALEmptyQuotaHandler();
    }
    return instance;
  }

  @Override
  public boolean isOverQuota(String orgId) {
    return false;
  }

}
