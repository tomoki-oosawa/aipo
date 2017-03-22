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
package com.aimluck.eip.services.reminder.impl;

import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.eip.services.reminder.ALReminderHandler;
import com.aimluck.eip.services.reminder.model.ALReminderDefaultItem;
import com.aimluck.eip.services.reminder.model.ALReminderItem;

/**
 *
 */
public class ALDefaultReminderHandler extends ALReminderHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALDefaultReminderHandler.class.getName());

  private static ALReminderHandler instance;

  public static ALReminderHandler getInstance() {
    if (instance == null) {
      instance = new ALDefaultReminderHandler();
    }

    return instance;
  }

  /**
   * @param orgId
   * @param userId
   * @param category
   * @param itemId
   * @return
   */
  @Override
  public ALReminderItem getJob(String orgId, String userId,
      ReminderCategory category, int itemId) {
    return null;
  }

  /**
   * @param item
   */
  @Override
  public void updateJob(ALReminderItem item) {
  }

  /**
   *
   * @param item
   */
  @Override
  public void removeJob(ALReminderItem item) {
  }

  /**
   * @param orgId
   * @param userId
   * @param category
   * @return
   */
  @Override
  public ALReminderDefaultItem getDefault(String orgId, String userId,
      ReminderCategory category) {
    return null;
  }

  /**
   * @param item
   */
  @Override
  public void updateDefault(ALReminderDefaultItem item) {
  }

  /**
   * @param item
   */
  @Override
  public void updateDefaultBatch(List<ALReminderDefaultItem> itemList) {
  }

  /**
   * @param orgId
   * @param userId
   * @param category
   */
  @Override
  public void removeDefault(String orgId, String userId,
      ReminderCategory category) {
  }

  /**
   * @return
   */
  @Override
  public boolean isEnabled() {
    return JetspeedResources.getBoolean("aipo.reminder.enabled", false);
  }

  /**
   * @return
   */
  @Override
  public boolean isViewSetting() {
    return JetspeedResources.getBoolean("aipo.reminder.viewsetting", false);
  }

  /**
   * @return
   */
  @Override
  public boolean isPastEnabled() {
    return JetspeedResources.getBoolean("aipo.reminder.pastEnabled", false);
  }

}
