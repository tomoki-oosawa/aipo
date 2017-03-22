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

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.services.reminder.model.ALReminderDefaultItem;
import com.aimluck.eip.services.reminder.model.ALReminderItem;

/**
 *
 */
public abstract class ALReminderHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALReminderHandler.class.getName());

  public static enum ReminderCategory {
    SCHEDULE("schedule");

    private final String value;

    private ReminderCategory(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }
  }

  public static enum ReminderNotifyType {
    MAIL("mail"), MESSAGE("message");

    private final String value;

    private ReminderNotifyType(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return this.value;
    }
  }

  public abstract ALReminderItem getJob(String orgId, String userId,
      ReminderCategory category, int itemId);

  public abstract void updateJob(ALReminderItem item);

  public abstract void removeJob(ALReminderItem item);

  public abstract ALReminderDefaultItem getDefault(String orgId, String userId,
      ReminderCategory category);

  public abstract void updateDefault(ALReminderDefaultItem item);

  public abstract void updateDefaultBatch(List<ALReminderDefaultItem> itemList);

  public abstract void removeDefault(String orgId, String userId,
      ReminderCategory category);

  public abstract boolean isEnabled();

  public abstract boolean isViewSetting();

  public abstract boolean isPastEnabled();

}
