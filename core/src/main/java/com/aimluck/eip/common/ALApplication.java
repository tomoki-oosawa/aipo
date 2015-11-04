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
import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.services.social.gadgets.ALUserPref;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * 
 */
public class ALApplication implements ALData, Serializable {

  private static final long serialVersionUID = -7371342588788650173L;

  private ALStringField appId;

  private ALStringField title;

  private ALStringField description;

  private ALStringField url;

  private ALStringField icon;

  private ALStringField consumerKey;

  private ALStringField consumerSecret;

  private List<ALOAuthConsumer> OAuthConsumers;

  private List<ALUserPref> userPrefs;

  private int status;

  public ALApplication() {
    initField();
  }

  /**
   * 
   */
  @Override
  public void initField() {
    appId = new ALStringField();
    title = new ALStringField();
    description = new ALStringField();
    url = new ALStringField();
    consumerKey = new ALStringField();
    consumerSecret = new ALStringField();
    OAuthConsumers = new ArrayList<ALOAuthConsumer>();
    userPrefs = new ArrayList<ALUserPref>();
    icon = new ALStringField();
    status = 0;
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
   * @return title
   */
  public ALStringField getTitle() {
    return title;
  }

  /**
   * @param title
   *          セットする title
   */
  public void setTitle(String title) {
    this.title.setValue(title);
  }

  /**
   * @return description
   */
  public ALStringField getDescription() {
    return description;
  }

  /**
   * @param description
   *          セットする description
   */
  public void setDescription(String description) {
    this.description.setValue(description);
  }

  /**
   * @return url
   */
  public ALStringField getUrl() {
    return url;
  }

  public String getUrlCR() {
    return ALCommonUtils.replaceToAutoCR(url.toString());
  }

  /**
   * @param url
   *          セットする url
   */
  public void setUrl(String url) {
    this.url.setValue(url);
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

  /**
   * @param status
   *          セットする status
   */
  public void setStatus(int status) {
    this.status = status;
  }

  /**
   * @return status
   */
  public int getStatus() {
    return status;
  }

  /**
   * 
   * @param oAuthConsumers
   */
  public void addOAuthConsumers(ALOAuthConsumer oAuthConsumers) {
    OAuthConsumers.add(oAuthConsumers);
  }

  public void addOAuthConsumers(List<ALOAuthConsumer> oAuthConsumers) {
    for (ALOAuthConsumer service : oAuthConsumers) {
      OAuthConsumers.add(service);
    }
  }

  /**
   * @return oAuthConsumers
   */
  public List<ALOAuthConsumer> getOAuthConsumers() {
    return OAuthConsumers;
  }

  /**
   * @param userPrefs
   *          セットする userPrefs
   */
  public void setUserPrefs(List<ALUserPref> userPrefs) {
    this.userPrefs = userPrefs;
  }

  public void addUserPref(ALUserPref userPref) {
    userPrefs.add(userPref);
  }

  /**
   * @return userPrefs
   */
  public List<ALUserPref> getUserPrefs() {
    return userPrefs;
  }

  /**
   * @param icon
   *          セットする icon
   */
  public void setIcon(String icon) {
    this.icon.setValue(icon);
  }

  /**
   * @return icon
   */
  public ALStringField getIcon() {
    return icon;
  }

}
