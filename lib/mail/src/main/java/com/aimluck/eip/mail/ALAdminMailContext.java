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
package com.aimluck.eip.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ALAdminMailContext implements ALMailContext {

  private String orgId;

  private int srcUserId;

  private List<ALAdminMailMessage> messageList;

  private int destType;

  public ALAdminMailContext() {
    messageList = new ArrayList<ALAdminMailMessage>();
  }

  public ALAdminMailContext(String orgId, int srcUserId,
      List<ALAdminMailMessage> messageList, int destType) {
    this.orgId = orgId;
    this.messageList = messageList;
    this.destType = destType;
  }

  /**
   * @return orgId
   */
  public String getOrgId() {
    return orgId;
  }

  public ALAdminMailContext withOrgId(String orgId) {
    this.orgId = orgId;
    return this;
  }

  /**
   * @param orgId
   *          セットする orgId
   */
  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  /**
   * @return srcUserId
   */
  public int getSrcUserId() {
    return srcUserId;
  }

  /**
   * @param srcUserId
   *          セットする srcUserId
   */
  public void setSrcUserId(int srcUserId) {
    this.srcUserId = srcUserId;
  }

  public ALAdminMailContext withSrcUserId(int srcUserId) {
    this.srcUserId = srcUserId;
    return this;
  }

  /**
   * @return destMemberList
   */
  public List<ALAdminMailMessage> getMessageList() {
    return messageList;
  }

  /**
   * @param destMemberList
   *          セットする destMemberList
   */
  public void setMessageList(List<ALAdminMailMessage> messageList) {
    this.messageList = messageList;
  }

  public ALAdminMailContext withMemberList(List<ALAdminMailMessage> messageList) {
    this.messageList = messageList;
    return this;
  }

  /**
   * @return destType
   */
  public int getDestType() {
    return destType;
  }

  /**
   * @param destType
   *          セットする destType
   */
  public void setDestType(int destType) {
    this.destType = destType;
  }

  public ALAdminMailContext withDestType(int destType) {
    this.destType = destType;
    return this;
  }
}
