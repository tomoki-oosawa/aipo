package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTMessageRoom;

public class EipTMessageRoom extends _EipTMessageRoom {

  private int unreadCount = 0;

  private String firstName = null;

  private String lastName = null;

  public Integer getRoomId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(ROOM_ID_PK_COLUMN);
      if (obj instanceof Long) {
        Long value = (Long) obj;
        return Integer.valueOf(value.intValue());
      } else {
        return (Integer) obj;
      }
    } else {
      return null;
    }
  }

  public void setRoomId(String id) {
    setObjectId(new ObjectId("EipTMessageRoom", ROOM_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }

  /**
   * @return unreadCount
   */
  public Integer getUnreadCount() {
    return unreadCount;
  }

  /**
   * @param firstName
   *          セットする firstName
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * @return firstName
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * @param lastName
   *          セットする lastName
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * @return lastName
   */
  public String getLastName() {
    return lastName;
  }

}
