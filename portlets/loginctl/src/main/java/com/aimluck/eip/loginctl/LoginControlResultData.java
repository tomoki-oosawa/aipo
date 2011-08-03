/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.loginctl;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.eip.common.ALData;

/**
 *
 */
public class LoginControlResultData implements ALData {

  private ALNumberField password_expiration;

  private ALNumberField expiration_notification;

  @Override
  public void initField() {
    password_expiration = new ALNumberField();
    expiration_notification = new ALNumberField();
  }

  /**
   * @param str
   */
  public void setPasswordExpiration(String str) {
    password_expiration.setValue(str);
  }

  /**
   * @param str
   */
  public void setExpirationNotification(String str) {
    expiration_notification.setValue(str);
  }

  /**
   * @return password_expiration
   */
  public ALNumberField getPasswordExpiration() {
    return password_expiration;
  }

  /**
   * @return expiration_notification
   */
  public ALNumberField getExpirationNotification() {
    return expiration_notification;
  }
}
