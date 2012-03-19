/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.timeline;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * タイムライントピックのResultData <BR>
 * 
 */
public class TimelineLikeResultData implements ALData {

  /** トピック ID */
  private ALNumberField timeline_like_id;

  /** いいね！が押されたトピックID */
  private ALNumberField timeline_id;

  /** いいね！を押したユーザーID */
  private ALNumberField user_id;

  /** 登録者名 */
  private ALStringField user_name;

  /** 顔写真の有無 */
  private boolean has_photo;

  /**
   *
   *
   */
  @Override
  public void initField() {
    timeline_like_id = new ALNumberField();
    timeline_id = new ALNumberField();
    user_id = new ALNumberField();
    user_name = new ALStringField();
  }

  /**
   * @return timeline_like_id
   */
  public ALNumberField getTimelineLikeId() {
    return timeline_like_id;
  }

  /**
   * @param timeline_like_id
   *          セットする timeline_like_id
   */
  public void setTimelineLikeId(long i) {
    timeline_like_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getTimelineId() {
    return timeline_id;
  }

  /**
   * @param i
   */
  public void setTimelineId(long i) {
    timeline_id.setValue(i);
  }

  /**
   * @return user_id
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  public ALStringField getUserName() {
    return user_name;
  }

  public void setUserName(String str) {
    user_name.setValue(str);
  }

  /**
   * @param user_id
   *          セットする user_id
   */
  public void setUserId(long i) {
    user_id.setValue(i);
  }

  /**
   * 
   * @param bool
   */
  public void setHasPhoto(boolean bool) {
    has_photo = bool;
  }

  public boolean hasPhoto() {
    return has_photo;
  }

}
