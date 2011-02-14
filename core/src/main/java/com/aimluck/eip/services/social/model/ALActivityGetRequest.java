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

package com.aimluck.eip.services.social.model;

/**
 *
 */
public class ALActivityGetRequest {

  private int limit = -1;

  private int page = -1;

  private int isRead = -1;

  private String appId;

  private String loginName;

  private String targetLoginName;

  private float priority = -1f;

  /**
   * @return limit
   */
  public int getLimit() {
    return limit;
  }

  /**
   * @param limit
   *          セットする limit
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }

  public ALActivityGetRequest withLimit(int limit) {
    setLimit(limit);
    return this;
  }

  /**
   * @return page
   */
  public int getPage() {
    return page;
  }

  /**
   * @param page
   *          セットする page
   */
  public void setPage(int page) {
    this.page = page;
  }

  public ALActivityGetRequest withPage(int page) {
    setPage(page);
    return this;
  }

  public int isRead() {
    return isRead;
  }

  /**
   * @param limit
   *          セットする limit
   */
  public void setRead(int isRead) {
    this.isRead = isRead;
  }

  public ALActivityGetRequest withRead(int isRead) {
    setRead(isRead);
    return this;
  }

  /**
   * @param appId
   *          セットする appId
   */
  public void setAppId(String appId) {
    this.appId = appId;
  }

  public ALActivityGetRequest withAppId(String appId) {
    setAppId(appId);
    return this;
  }

  /**
   * @return appId
   */
  public String getAppId() {
    return appId;
  }

  public String getLoginName() {
    return loginName;
  }

  public void setLoginName(String loginName) {
    this.loginName = loginName;
  }

  public ALActivityGetRequest withLoginName(String loginName) {
    setLoginName(loginName);
    return this;
  }

  public String getTargetLoginName() {
    return targetLoginName;
  }

  public void setTargetLoginName(String targetLoginName) {
    this.targetLoginName = targetLoginName;
  }

  public ALActivityGetRequest withTargetLoginName(String targetLoginName) {
    setTargetLoginName(targetLoginName);
    return this;
  }

  /**
   * @param priority
   *          セットする priority
   */
  public void setPriority(float priority) {
    this.priority = priority;
  }

  public ALActivityGetRequest withPriority(float priority) {
    setPriority(priority);
    return this;
  }

  /**
   * @return priority
   */
  public float getPriority() {
    return priority;
  }
}
