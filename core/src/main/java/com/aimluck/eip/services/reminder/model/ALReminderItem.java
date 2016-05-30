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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderCategory;
import com.aimluck.eip.services.reminder.ALReminderHandler.ReminderNotifyType;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class ALReminderItem {

  private String orgId;

  private String userId;

  private ReminderCategory category;

  private int itemId;

  private String repeatPattern; // 繰り返しパターン

  private Date eventStartDate; // イベントの日付・時間

  private Date limitEndDate; // 繰り返し期間の終了日

  private int notifyTiming; // 分（何分前に通知するか）

  private Date expireDate; // 有効期限

  private final List<ReminderNotifyType> notifyType =
    new ArrayList<ReminderNotifyType>(); // 通知先（メール、メッセージ）

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
   * @return itemId
   */
  public int getItemId() {
    return itemId;
  }

  /**
   * @param itemId
   *          セットする itemId
   */
  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  /**
   * @return repeatPattern
   */
  public String getRepeatPattern() {
    return repeatPattern;
  }

  /**
   * @param repeatPattern
   *          セットする repeatPattern
   */
  public void setRepeatPattern(String repeatPattern) {
    this.repeatPattern = repeatPattern;
  }

  /**
   * @return eventStartDate
   */
  public Date getEventStartDate() {
    return eventStartDate;
  }

  /**
   * @param eventStartDate
   *          セットする eventStartDate
   */
  public void setEventStartDate(Date eventStartDate) {
    this.eventStartDate = eventStartDate;
  }

  /**
   * @return limitEndDate
   */
  public Date getLimitEndDate() {
    return limitEndDate;
  }

  /**
   * @param limitEndDate
   *          セットする limitEndDate
   */
  public void setLimitEndDate(Date limitEndDate) {
    this.limitEndDate = limitEndDate;
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
  public void addNotifyType(ReminderNotifyType notifyType) {
    this.notifyType.add(notifyType);
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

  /**
   *
   * @return
   */
  public String getNotifyTimingText() {
    if (this.notifyTiming == 0) {
      return ALLocalizationUtils.getl10n("SCHEDULE_REMINDER_ONTIME");
    } else if (this.notifyTiming < 60) {
      return this.notifyTiming
        + ALLocalizationUtils.getl10n("SCHEDULE_REMINDER_BEFORE_MIN");
    } else if (this.notifyTiming >= 60) {
      BigDecimal bd = new BigDecimal(this.notifyTiming / 60);
      BigDecimal bd1 = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
      return bd1 + ALLocalizationUtils.getl10n("SCHEDULE_REMINDER_BEFORE_HOUR");
    }
    return "";
  }

  /**
   * @return expireDate
   */
  public Date getExpireDate() {
    return expireDate;
  }

  /**
   * @param expireDate
   *          セットする expireDate
   */
  public void setExpireDate(Date Date) {
    this.expireDate = Date;
  }
}
