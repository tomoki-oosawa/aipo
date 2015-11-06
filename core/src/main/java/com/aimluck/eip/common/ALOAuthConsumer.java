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
package com.aimluck.eip.common;

import java.io.Serializable;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * 
 */
public class ALOAuthConsumer implements ALData, Serializable {

  private static final long serialVersionUID = -6421912115761886784L;

  private ALStringField appId;

  private ALStringField name;

  private ALStringField requestUrl;

  private ALStringField authorizationUrl;

  private ALStringField accessUrl;

  private ALStringField type;

  private ALStringField consumerKey;

  private ALStringField consumerSecret;

  public ALOAuthConsumer() {
    initField();
  }

  /**
   * 
   */
  @Override
  public void initField() {
    appId = new ALStringField();
    name = new ALStringField();
    requestUrl = new ALStringField();
    authorizationUrl = new ALStringField();
    accessUrl = new ALStringField();
    type = new ALStringField();
    consumerKey = new ALStringField();
    consumerSecret = new ALStringField();
  }

  /**
   * @return appId
   */
  public ALStringField getAppId() {
    return appId;
  }

  /**
   * @param appId
   *          セットする appId
   */
  public void setAppId(String appId) {
    this.appId.setValue(appId);
  }

  /**
   * @return name
   */
  public ALStringField getName() {
    return name;
  }

  /**
   * @param name
   *          セットする name
   */
  public void setName(String name) {
    this.name.setValue(name);
  }

  /**
   * @return requestUrl
   */
  public ALStringField getRequestUrl() {
    return requestUrl;
  }

  public String getRequestUrlCR() {
    return ALCommonUtils.replaceToAutoCR(requestUrl.toString());
  }

  /**
   * @param requestUrl
   *          セットする requestUrl
   */
  public void setRequestUrl(String requestUrl) {
    this.requestUrl.setValue(requestUrl);
  }

  /**
   * @return authorizationUrl
   */
  public ALStringField getAuthorizationUrl() {
    return authorizationUrl;
  }

  /**
   * @param authorizationUrl
   *          セットする authorizationUrl
   */
  public void setAuthorizationUrl(String authorizationUrl) {
    this.authorizationUrl.setValue(authorizationUrl);
  }

  /**
   * @return accessUrl
   */
  public String getAuthorizationUrlCR() {
    return ALCommonUtils.replaceToAutoCR(authorizationUrl.toString());
  }

  /**
   * @return accessUrl
   */
  public ALStringField getAccessUrl() {
    return accessUrl;
  }

  /**
   * @return accessUrl
   */
  public String getAccessUrlCR() {
    return ALCommonUtils.replaceToAutoCR(accessUrl.toString());
  }

  /**
   * @param accessUrl
   *          セットする accessUrl
   */
  public void setAccessUrl(String accessUrl) {
    this.accessUrl.setValue(accessUrl);
  }

  /**
   * @return type
   */
  public ALStringField getType() {
    return type;
  }

  /**
   * @param type
   *          セットする type
   */
  public void setType(String type) {
    this.type.setValue(type);
  }

  /**
   * @return consumerKey
   */
  public ALStringField getConsumerKey() {
    return consumerKey;
  }

  /**
   * @param consumerKey
   *          セットする consumerKey
   */
  public void setConsumerKey(String consumerKey) {
    this.consumerKey.setValue(consumerKey);
  }

  /**
   * @return consumerSecret
   */
  public ALStringField getConsumerSecret() {
    return consumerSecret;
  }

  /**
   * @param consumerSecret
   *          セットする consumerSecret
   */
  public void setConsumerSecret(String consumerSecret) {
    this.consumerSecret.setValue(consumerSecret);
  }
}
