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
package com.aimluck.eip.cayenne.om.portlet;

import com.aimluck.eip.cayenne.om.portlet.auto._VEipTScheduleList;

public class VEipTScheduleList extends _VEipTScheduleList {

  private boolean member;

  private int facilityCount;

  private int userCount;

  /**
   * @return member
   */
  public boolean isMember() {
    return member;
  }

  /**
   * @param member
   *          セットする member
   */
  public void setMember(boolean member) {
    this.member = member;
  }

  /**
   * @return userCount
   */
  public int getUserCount() {
    return userCount;
  }

  /**
   * @param userCount
   *          セットする userCount
   */
  public void setUserCount(int userCount) {
    this.userCount = userCount;
  }

  /**
   * @return facilityCount
   */
  public int getFacilityCount() {
    return facilityCount;
  }

  /**
   * @param facilityCount
   *          セットする facilityCount
   */
  public void setFacilityCount(int facilityCount) {
    this.facilityCount = facilityCount;
  }

}
