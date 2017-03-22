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

import com.aimluck.eip.mail.util.ALEipUserAddr;

/**
 *
 */
public class ALAdminMailMessage extends ALEipUserAddr {

  private String pcSubject;

  private String cellularSubject;

  private String pcBody;

  private String cellularBody;

  public ALAdminMailMessage() {

  }

  public ALAdminMailMessage(ALEipUserAddr delegate) {
    setUserId(delegate.getUserId());
    setPcMailAddr(delegate.getPcMailAddr());
    setCellMailAddr(delegate.getCellMailAddr());
  }

  /**
   * @return pcSubject
   */
  public String getPcSubject() {
    return pcSubject;
  }

  /**
   * @param pcSubject
   *          セットする pcSubject
   */
  public void setPcSubject(String pcSubject) {
    this.pcSubject = pcSubject;
  }

  public ALAdminMailMessage withPcSubject(String pcSubject) {
    this.pcSubject = pcSubject;
    return this;
  }

  /**
   * @return cellularSubject
   */
  public String getCellularSubject() {
    return cellularSubject;
  }

  /**
   * @param cellularSubject
   *          セットする cellularSubject
   */
  public void setCellularSubject(String cellularSubject) {
    this.cellularSubject = cellularSubject;
  }

  public ALAdminMailMessage withCellularSubject(String cellularSubject) {
    this.cellularSubject = cellularSubject;
    return this;
  }

  /**
   * @return pcBody
   */
  public String getPcBody() {
    return pcBody;
  }

  /**
   * @param pcBody
   *          セットする pcBody
   */
  public void setPcBody(String pcBody) {
    this.pcBody = pcBody;
  }

  public ALAdminMailMessage withPcBody(String pcBody) {
    this.pcBody = pcBody;
    return this;
  }

  /**
   * @return cellularBody
   */
  public String getCellularBody() {
    return cellularBody;
  }

  /**
   * @param cellularBody
   *          セットする cellularBody
   */
  public void setCellularBody(String cellularBody) {
    this.cellularBody = cellularBody;
  }

  public ALAdminMailMessage withCellularBody(String cellularBody) {
    this.cellularBody = cellularBody;
    return this;
  }

}
