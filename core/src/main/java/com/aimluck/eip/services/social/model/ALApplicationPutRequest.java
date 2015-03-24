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
package com.aimluck.eip.services.social.model;

/**
 *
 */
public class ALApplicationPutRequest {

  private String url;

  private String title;

  private String description;

  private String icon;

  private String activityLoginName;

  private int activityUserId;

  /**
   * @param url
   *          セットする url
   */
  public void setUrl(String url) {
    this.url = url;
  }

  public ALApplicationPutRequest withUrl(String url) {
    setUrl(url);
    return this;
  }

  /**
   * @return url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param title
   *          セットする title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  public ALApplicationPutRequest withTitle(String title) {
    setTitle(title);
    return this;
  }

  /**
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param description
   *          セットする description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public ALApplicationPutRequest withDescription(String description) {
    setDescription(description);
    return this;
  }

  /**
   * @return description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param icon
   *          セットする icon
   */
  public void setIcon(String icon) {
    this.icon = icon;
  }

  public ALApplicationPutRequest withIcon(String icon) {
    setIcon(icon);
    return this;
  }

  /**
   * @return icon
   */
  public String getIcon() {
    return icon;
  }

  /**
   * @return activityLoginName
   */
  public String getActivityLoginName() {
    return activityLoginName;
  }

  /**
   * @param activityLoginName
   *          セットする activityLoginName
   */
  public void setActivityLoginName(String activityLoginName) {
    this.activityLoginName = activityLoginName;
  }

  public ALApplicationPutRequest withActivityLoginName(String loginName) {
    setActivityLoginName(loginName);
    return this;
  }

  /**
   * @param activityuserId
   *          セットする activityuserId
   */
  public void setActivityUserId(int activityuserId) {
    this.activityUserId = activityuserId;
  }

  /**
   * @return activityuserId
   */
  public int getActivityUserId() {
    return activityUserId;
  }

  /**
   * @param activityuserId
   *          セットする activityuserId
   */
  public ALApplicationPutRequest withActivityuserId(int activityuserId) {
    setActivityUserId(activityuserId);
    return this;
  }

}
