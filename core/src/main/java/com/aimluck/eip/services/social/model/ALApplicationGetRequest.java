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
public class ALApplicationGetRequest {

  public static enum Status {
    ALL {
      @Override
      public int intValue() {
        return -1;
      }
    },
    ACTIVE {
      @Override
      public int intValue() {
        return 1;
      }
    },
    INACTIVE {
      @Override
      public int intValue() {
        return 0;
      }
    };
    public abstract int intValue();
  }

  private int limit = -1;

  private int page = -1;

  private Status status = Status.ACTIVE;

  private String appId;

  private boolean isDetail = false;

  private boolean isFetchXml = false;

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

  public ALApplicationGetRequest withLimit(int limit) {
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

  public ALApplicationGetRequest withPage(int page) {
    setPage(page);
    return this;
  }

  /**
   * @param status
   *          セットする status
   */
  public void setStatus(Status status) {
    this.status = status;
  }

  public ALApplicationGetRequest withStatus(Status status) {
    setStatus(status);
    return this;
  }

  /**
   * @return status
   */
  public Status getStatus() {
    return status;
  }

  /**
   * @param appId
   *          セットする appId
   */
  public void setAppId(String appId) {
    this.appId = appId;
  }

  public ALApplicationGetRequest withAppId(String appId) {
    setAppId(appId);
    return this;
  }

  /**
   * @return appId
   */
  public String getAppId() {
    return appId;
  }

  /**
   * @param isDetail
   *          セットする isDetail
   */
  public void setDetail(boolean isDetail) {
    this.isDetail = isDetail;
  }

  public ALApplicationGetRequest withIsDetail(boolean isDetail) {
    setDetail(isDetail);
    return this;
  }

  /**
   * @return isDetail
   */
  public boolean isDetail() {
    return isDetail;
  }

  /**
   * @param isFetchXml
   *          セットする isFetchXml
   */
  public void setFetchXml(boolean isFetchXml) {
    this.isFetchXml = isFetchXml;
  }

  public ALApplicationGetRequest withIsFetchXml(boolean isFetchXml) {
    setFetchXml(isFetchXml);
    return this;
  }

  /**
   * @return isFetchXml
   */
  public boolean isFetchXml() {
    return isFetchXml;
  }

}
