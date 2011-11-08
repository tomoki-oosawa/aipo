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
