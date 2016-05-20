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

package com.aimluck.eip.services.reminder.model;

import java.util.List;

import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderCategory;
import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderNotifyType;

/**
 *
 */
public class ALReminderDefaultItem {

  private String orgId;

  private String userId;

  private ReminderCategory category;

  private boolean enabled;

  private int notifyTiming; // 分（何分前に通知するか）

  private List<ReminderNotifyType> notifyType; // 通知先（メール、メッセージ）

  /**
   * @return orgId
   */
  public String getOrgId() {
    return orgId;
  }

  /**
   * @param orgId
   *          セットする orgId
   */
  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  /**
   * @return userId
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @param userId
   *          セットする userId
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * @return category
   */
  public ReminderCategory getCategory() {
    return category;
  }

  /**
   * @param category
   *          セットする category
   */
  public void setCategory(ReminderCategory category) {
    this.category = category;
  }

  /**
   * @return notifyTiming
   */
  public int getNotifyTiming() {
    return notifyTiming;
  }

  /**
   * @param notifyTiming
   *          セットする notifyTiming
   */
  public void setNotifyTiming(int notifyTiming) {
    this.notifyTiming = notifyTiming;
  }

  /**
   * @return notifyType
   */
  public List<ReminderNotifyType> getNotifyType() {
    return notifyType;
  }

  /**
   * @param notifyType
   *          セットする notifyType
   */
  public void setNotifyType(List<ReminderNotifyType> notifyType) {
    this.notifyType = notifyType;
  }

  /**
   * @return enabled
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * @param enabled
   *          セットする enabled
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   *
   * @return
   */
  public boolean hasNotifyTypeMail() {
    if (this.notifyType != null && this.notifyType.size() > 0) {
      if (this.notifyType.contains(ReminderNotifyType.MAIL)) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   * @return
   */
  public boolean hasNotifyTypeMessage() {
    if (this.notifyType != null && this.notifyType.size() > 0) {
      if (this.notifyType.contains(ReminderNotifyType.MESSAGE)) {
        return true;
      }
    }
    return false;
  }
}
