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
package com.aimluck.eip.timeline;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.util.ALEipUtils;

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

  /** 作成日 */
  private ALDateTimeField create_date;

  private ALEipUser user = null;

  /**
   *
   *
   */
  @Override
  public void initField() {
    timeline_like_id = new ALNumberField();
    timeline_id = new ALNumberField();
    user_id = new ALNumberField();
    create_date = new ALDateTimeField();
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
    ALStringField field = new ALStringField();
    ALEipUser user = getUser();
    if (user != null) {
      field.setValue(user.getAliasName().getValue());
    }
    return field;
  }

  /**
   * @param user_id
   *          セットする user_id
   */
  public void setUserId(long i) {
    user_id.setValue(i);
  }

  public boolean hasPhoto() {
    ALEipUser user = getUser();
    if (user != null) {
      return user.hasPhoto();
    } else {
      return false;
    }
  }

  /**
   * @return photoModified
   */
  public long getPhotoModified() {
    ALEipUser user = getUser();
    if (user != null) {
      return user.getPhotoModified();
    } else {
      return 0;
    }
  }

  /**
   * @return create_date
   */
  public ALDateTimeField getCreateDate() {
    return ALEipUtils.getFormattedTime(create_date);
  }

  /**
   * @param create_date
   *          セットする create_date
   */
  public void setCreateDate(Date date) {
    create_date.setValue(date);
  }

  public ALEipUser getUser() {
    if (user == null) {
      user =
        ALEipManager.getInstance().getUser(
          Integer.valueOf((int) this.user_id.getValue()));
    }
    return user;
  }
}
