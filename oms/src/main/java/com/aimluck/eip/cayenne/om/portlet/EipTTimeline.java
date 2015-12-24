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

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTTimeline;

public class EipTTimeline extends _EipTTimeline {

  public static final String CREATE_DATE_COLUMN = "CREATE_DATE";

  public static final String OWNER_ID_COLUMN = "OWNER_ID";

  public static final String PARENT_ID_COLUMN = "PARENT_ID";

  public static final String TIMELINE_TYPE_ACTIVITY = "A";

  public static final String TIMELINE_TYPE_TIMELINE = "T";// デフォルト

  private boolean like;

  private int likeCount;

  /**
   * @return like
   */
  public boolean isLike() {
    return like;
  }

  /**
   * @param like
   *          セットする like
   */
  public void setLike(boolean like) {
    this.like = like;
  }

  /**
   * @return userCount
   */
  public int getLikeCount() {
    return likeCount;
  }

  /**
   * @param userCount
   *          セットする userCount
   */
  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public Integer getTimelineId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(TIMELINE_ID_PK_COLUMN);
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

  public void setTimelineId(String id) {
    setObjectId(new ObjectId("EipTTimeline", TIMELINE_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }

}
