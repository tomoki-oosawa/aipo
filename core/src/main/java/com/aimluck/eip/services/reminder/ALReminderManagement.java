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

import java.util.ArrayList;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderCategory;
import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderNotifyType;
import com.aimluck.eip.services.reminder.model.ALReminderDefaultItem;

/**
 * ユーザーを管理するクラスです。 <br />
 *
 */
public class ALReminderManagement {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALReminderManagement.class.getName());

  /**
   * おすすめの通知設定
   *
   * @param orgId
   * @param userId
   * @param category
   * @return
   */
  public static ALReminderDefaultItem getRecommendDefault(String orgId,
      String userId, ReminderCategory category) {
    ALReminderDefaultItem item = new ALReminderDefaultItem();
    item.setEnabled(true);
    item.setNotifyTiming(0);
    item.setCategory(category);
    item.setOrgId(orgId);
    item.setUserId(userId);
    ArrayList<ReminderNotifyType> list = new ArrayList<ReminderNotifyType>();
    list.add(ReminderNotifyType.MAIL);
    list.add(ReminderNotifyType.MESSAGE);
    item.setNotifyType(list);
    return item;
  }

}
