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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ALActivityPutRequest {

  private String appId;

  private String title;

  private String externalId;

  private String portletParams;

  private String loginName;
  
  // timelineに表示させる時はuserIdを使う。
  private int userId;
  
  private String body;

  private float priority = 0f;

  private List<String> recipients;

  public ALActivityPutRequest() {
    setRecipients(new ArrayList<String>());
  }

  /**
   * @param appId
   *          セットする appId
   */
  public void setAppId(String appId) {
    this.appId = appId;
  }

  public ALActivityPutRequest withAppId(String appId) {
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

  public ALActivityPutRequest withLoginName(String loginName) {
    setLoginName(loginName);
    return this;
  }

  /**
   * @return externalId
   */
  public String getExternalId() {
    return externalId;
  }

  /**
   * @param externalId
   *          セットする externalId
   */
  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public ALActivityPutRequest withExternalId(String externalId) {
    setExternalId(externalId);
    return this;
  }

  /**
   * @return portletParams
   */
  public String getPortletParams() {
    return portletParams;
  }

  /**
   * @param portletParams
   *          セットする portletParams
   */
  public void setPortletParams(String portletParams) {
    this.portletParams = portletParams;
  }

  public ALActivityPutRequest withPortletParams(String portletParams) {
    setPortletParams(portletParams);
    return this;
  }

  /**
   * @param priority
   *          セットする priority
   */
  public void setPriority(float priority) {
    this.priority = priority;
  }

  public ALActivityPutRequest withPriority(float priority) {
    setPriority(priority);
    return this;
  }

  /**
   * @return priority
   */
  public float getPriority() {
    return priority;
  }

  /**
   * @param recipients
   *          セットする recipients
   */
  public void setRecipients(List<String> recipients) {
    this.recipients = recipients;
  }

  public ALActivityPutRequest withRecipients(List<String> recipients) {
    setRecipients(recipients);
    return this;
  }

  public ALActivityPutRequest withRecipients(String... recipients) {
    setRecipients(Arrays.asList(recipients));
    return this;
  }

  /**
   * @return recipients
   */
  public List<String> getRecipients() {
    return recipients;
  }

  /**
   * @param body
   *          セットする body
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * @return body
   */
  public String getBody() {
    return body;
  }

  public ALActivityPutRequest withBody(String body) {
    setBody(body);
    return this;
  }

  /**
   * @param title
   *          セットする title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return title
   */
  public String getTitle() {
    return title;
  }

  public ALActivityPutRequest withTitle(String title) {
	    setTitle(title);
	    return this;
	  }

	  /**
	   * @param userId
	   *          セットする userId
	   */
	  public void setUserId(int userId) {
	    this.userId = userId;
	  }

	  /**
	   * @return userId
	   */
	  public int getUserId() {
	    return userId;
	  }

	  public ALActivityPutRequest withUserId(int userId) {
	    this.userId=userId;
	    return this;
	  }
}
