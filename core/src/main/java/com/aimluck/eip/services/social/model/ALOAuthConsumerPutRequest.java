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
package com.aimluck.eip.services.social.model;

/**
 * 
 */
public class ALOAuthConsumerPutRequest {

  public static enum Type {
    HMACSHA1 {
      @Override
      public String value() {
        return "HMAC-SHA1";
      }
    },
    RSASHA1 {
      @Override
      public String value() {
        return "RSA-SHA1";
      }
    };

    public abstract String value();
  }

  private Type type = Type.HMACSHA1;

  private String appId;

  private String name;

  private String consumerKey;

  private String consumerSecret;

  /**
   * @param type
   *          セットする type
   */
  public void setType(Type type) {
    this.type = type;
  }

  public ALOAuthConsumerPutRequest withType(Type type) {
    setType(type);
    return this;
  }

  /**
   * @return type
   */
  public Type getType() {
    return type;
  }

  /**
   * @param appId
   *          セットする appId
   */
  public void setAppId(String appId) {
    this.appId = appId;
  }

  public ALOAuthConsumerPutRequest withAppId(String appId) {
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
   * @param consumerKey
   *          セットする consumerKey
   */
  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  public ALOAuthConsumerPutRequest withConsumerKey(String consumerKey) {
    setConsumerKey(consumerKey);
    return this;
  }

  /**
   * @return consumerKey
   */
  public String getConsumerKey() {
    return consumerKey;
  }

  /**
   * @param consumerSecret
   *          セットする consumerSecret
   */
  public void setConsumerSecret(String consumerSecret) {
    this.consumerSecret = consumerSecret;
  }

  public ALOAuthConsumerPutRequest withConsumerSecret(String consumerSecret) {
    setConsumerSecret(consumerSecret);
    return this;
  }

  /**
   * @return consumerSecret
   */
  public String getConsumerSecret() {
    return consumerSecret;
  }

  /**
   * @param name
   *          セットする name
   */
  public void setName(String name) {
    this.name = name;
  }

  public ALOAuthConsumerPutRequest withName(String name) {
    setName(name);
    return this;
  }

  /**
   * @return name
   */
  public String getName() {
    return name;
  }

}
