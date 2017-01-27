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
package com.aimluck.eip.services.reminder;

import java.util.List;

import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderCategory;
import com.aimluck.eip.services.reminder.model.ALReminderDefaultItem;
import com.aimluck.eip.services.reminder.model.ALReminderItem;

/**
 *
 */
public class ALReminderService {

  private ALReminderService() {

  }

  public static ALReminderHandler getService() {
    return ALReminderFactoryService.getInstance().getReminderHandler();
  }

  public static ALReminderItem getJob(String orgId, String userId,
      ReminderCategory category, int itemId) {
    return getService().getJob(orgId, userId, category, itemId);
  }

  public static void updateJob(ALReminderItem item) {
    getService().updateJob(item);
  }

  public static void removeJob(ALReminderItem item) {
    getService().removeJob(item);
  }

  public static ALReminderDefaultItem getDefault(String orgId, String userId,
      ReminderCategory category) {
    return getService().getDefault(orgId, userId, category);
  }

  public static void updateDefault(ALReminderDefaultItem item) {
    getService().updateDefault(item);
  }

  public static void updateDefaultBatch(List<ALReminderDefaultItem> itemList) {
    getService().updateDefaultBatch(itemList);
  }

  public static void removeDefault(String orgId, String userId,
      ReminderCategory category) {
    getService().removeDefault(orgId, userId, category);
  }

  public static boolean isEnabled() {
    return getService().isEnabled();
  }

  public static boolean isViewSetting() {
    return getService().isViewSetting();
  }

  public static boolean isPastEnabled() {
    return getService().isPastEnabled();
  }
}
