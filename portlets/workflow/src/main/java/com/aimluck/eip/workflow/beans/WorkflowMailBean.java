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
package com.aimluck.eip.workflow.beans;

/**
 * ワークフロー検索データを管理するクラスです。 <BR>
 * 
 */
public class WorkflowMailBean {

  String orgId;

  String subject;

  int loginUserId;

  String aipoAlias;

  String globalUrl;

  String localUrl;

  /**
   * @return org_id
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
   * @return subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * @param subject
   *          セットする subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * @return loginUserId
   */
  public int getLoginUserId() {
    return loginUserId;
  }

  /**
   * @param loginUserId
   *          セットする loginUserId
   */
  public void setLoginUserId(int loginUserId) {
    this.loginUserId = loginUserId;
  }

  /**
   * @return aipoAlias
   */
  public String getAipoAlias() {
    return aipoAlias;
  }

  /**
   * @param aipoAlias
   *          セットする aipoAlias
   */
  public void setAipoAlias(String aipoAlias) {
    this.aipoAlias = aipoAlias;
  }

  /**
   * @return globalUrl
   */
  public String getGlobalUrl() {
    return globalUrl;
  }

  /**
   * @param globalUrl
   *          セットする globalUrl
   */
  public void setGlobalUrl(String globalUrl) {
    this.globalUrl = globalUrl;
  }

  /**
   * @return localUrl
   */
  public String getLocalUrl() {
    return localUrl;
  }

  /**
   * @param localUrl
   *          セットする localUrl
   */
  public void setLocalUrl(String localUrl) {
    this.localUrl = localUrl;
  }
}
