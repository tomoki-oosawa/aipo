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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTTimeline;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * タイムライントピックのResultData <BR>
 * 
 */
public class TimelineResultData implements ALData {

  /** トピック ID */
  private ALNumberField timeline_id;

  /** 親トピック ID */
  private ALNumberField parent_id;

  /** 所有者 ID */
  private ALNumberField owner_id;

  /** タイムラインのタイプ */
  private String timeline_type;

  /** アプリケーションID **/
  private String app_id;

  /** 公開/非公開フラグ */
  private boolean is_public;

  /** 新着フラグ */
  private boolean new_topic;

  /** メモ */
  private ALStringField note;

  /** 返信数 */
  private ALNumberField reply_count;

  /** 登録者名 */
  private ALStringField create_user;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** 子トピックオブジェクト */
  private List<TimelineResultData> coTopicList;

  /** 子アクティビティオブジェクト */
  private List<TimelineResultData> coActivityList;

  /** いいねオブジェクト */
  private List<TimelineLikeResultData> likeList;

  /** Urlオブジェクト */
  private List<TimelineUrlResultData> urlList;

  /** いいねをしたか */
  private boolean like;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /** 更新情報の場合のURLのGETparams */
  private ALStringField params;

  /** 更新情報の場合のportletId */
  private String portletId;

  /** いいね！の数 */
  private int likeCount;

  private ALEipUser user = null;

  /**
   *
   *
   */
  @Override
  public void initField() {
    timeline_id = new ALNumberField();
    parent_id = new ALNumberField();
    owner_id = new ALNumberField();
    params = new ALStringField();

    note = new ALStringField();
    note.setTrim(false);
    reply_count = new ALNumberField();
    create_user = new ALStringField();
    create_date = new ALDateTimeField();
    update_date = new ALDateTimeField();

    is_public = true;
    new_topic = false;
    like = false;
    attachmentFileList = new ArrayList<FileuploadBean>();
    likeCount = 0;
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
   * @param i
   */
  public void setParentId(long i) {
    parent_id.setValue(i);
  }

  /**
   * @param i
   */
  public ALNumberField getParentId() {
    return parent_id;
  }

  /**
   * @param i
   */
  public void setOwnerId(long i) {
    owner_id.setValue(i);
  }

  /**
   * @return
   */
  public ALNumberField getOwnerId() {
    return owner_id;
  }

  public ALStringField getOwnerName() {
    ALStringField field = new ALStringField();
    ALEipUser user = getUser();
    if (user != null) {
      field.setValue(user.getAliasName().getValue());
    }
    return field;
  }

  /**
   * @return String
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  private static final int PRE_NOTE_LENGTH = 500;

  /**
   * 続きを見るを表示するかどうかの判定
   * 
   * @return boolean
   */
  public boolean isLongNote() {
    return note.getValue().length() > PRE_NOTE_LENGTH;
  }

  /**
   * 部分文字列の最後がアドレスのときTrueを返します。
   * 
   * @param 部分文字列sub
   * @return
   */
  private boolean isLastWordAddress(String sub) {
    if (sub.indexOf("http") != -1
      && sub.lastIndexOf("http") > sub.lastIndexOf(" ")
      && sub.lastIndexOf("http") > sub.lastIndexOf("\n")) {
      return true; // httpが最後の行にある
    }

    if (sub.indexOf("h") != -1
      && sub.lastIndexOf("h") > sub.lastIndexOf(" ")
      && sub.lastIndexOf("h") > sub.lastIndexOf("\n")) {
      String nextSub =
        note.getValue().substring(
          sub.lastIndexOf("h"),
          sub.lastIndexOf("h") + 4);
      if (nextSub.equals("http")) {
        return true;// httpのhだけが部分文字列に含まれている時
      }
    }

    return false;
  }

  /**
   * 続きを見るで隠されない部分を返します。
   * 
   * @return String
   */
  public String getPreviewNote() {
    if (isLongNote()) {
      try {

        String subnote =
          ALEipUtils.getMessageList(note.getValue().substring(
            0,
            PRE_NOTE_LENGTH));
        String sub = note.getValue().substring(0, PRE_NOTE_LENGTH);
        if (!isLastWordAddress(sub)) {
          return subnote;
        }

        sub = note.getValue();
        int i;
        for (i = PRE_NOTE_LENGTH; i < sub.length()
          && sub.charAt(i) != ' '
          && sub.charAt(i) != '\n'; i++) {
        }
        subnote = ALEipUtils.getMessageList(note.getValue().substring(0, i));
        return subnote;
      } catch (Exception ex) {
        // 文字数のカウントに失敗した場合は文字を丸めずに返す
        return getNote();
      }
    } else {
      if (EipTTimeline.TIMELINE_TYPE_ACTIVITY.equals(timeline_type)) {
        return note.toString();
      } else {
        return getNote();
      }
    }
  }

  /**
   * 続きを見るで隠される部分を返します。
   * 
   * @return String
   */
  public String getDetailNote() {
    if (isLongNote()) {
      String subnote =
        ALEipUtils.getMessageList(note.getValue().substring(PRE_NOTE_LENGTH));
      String sub = note.getValue().substring(0, PRE_NOTE_LENGTH);
      if (!isLastWordAddress(sub)) {
        return subnote;
      }

      sub = note.getValue();
      int i;
      for (i = PRE_NOTE_LENGTH; i < sub.length()
        && sub.charAt(i) != ' '
        && sub.charAt(i) != '\n'; i++) {
      }
      subnote = ALEipUtils.getMessageList(note.getValue().substring(i));
      return subnote;
    } else {
      return null;
    }
  }

  public boolean isDetailNote() {
    if (getDetailNote() != null && !"".equals(getDetailNote())) {
      return true;
    } else {

      return false;
    }
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return ALEipUtils.getFormattedTime(create_date);
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * @param string
   */
  public void setCreateDate(Date date) {
    create_date.setValue(date);
  }

  /**
   * @param string
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    this.update_date.setValue(date);
  }

  public ALStringField getCreateUser() {
    return create_user;
  }

  public void setCreateUser(String str) {
    create_user.setValue(str);
  }

  public String getReplyCountText() {
    return ALLocalizationUtils.getl10nFormat(
      "TIMELINE_REPLAY_COUNT_TEXT",
      reply_count.toString());
  }

  public String getLikeCountText() {
    return ALLocalizationUtils.getl10nFormat(
      "TIMELINE_LIKE_COUNT_TEXT",
      likeCount);
  }

  /**
   * 公開/非公開フラグ．
   * 
   * @return
   */
  public boolean isPublic() {
    return is_public;
  }

  /**
   * @return
   */
  public void setPublicFlag(boolean bool) {
    is_public = bool;
  }

  public boolean isNew() {
    return new_topic;
  }

  public void setNewTopicFlag(boolean bool) {
    new_topic = bool;
  }

  public void setReplyCount(long count) {
    reply_count.setValue(count);
  }

  public ALNumberField getReplyCount() {
    return reply_count;
  }

  /**
   * @return coTopicList
   */
  public List<TimelineResultData> getCoTopicList() {
    return coTopicList;
  }

  /**
   * @param coTopicList
   *          セットする coTopicList
   */
  public void setCoTopicList(List<TimelineResultData> coTopicList) {
    this.coTopicList =
      coTopicList == null ? new ArrayList<TimelineResultData>(0) : coTopicList;
  }

  /**
   * @return coActivityList
   */
  public List<TimelineResultData> getCoActivityList() {
    return coActivityList;
  }

  /**
   * @param coActivityList
   *          セットする coActivityList
   */
  public void setCoActivityList(List<TimelineResultData> coActivityList) {
    this.coActivityList =
      coActivityList == null
        ? new ArrayList<TimelineResultData>(0)
        : coActivityList;
  }

  /**
   * @return likeList
   */
  public List<TimelineLikeResultData> getLikeList() {
    return likeList;
  }

  /**
   * @param likeList
   *          セットする likeList
   */
  public void setLikeList(List<TimelineLikeResultData> likeList) {
    this.likeList = likeList;
  }

  /**
   * @return urlList
   */
  public List<TimelineUrlResultData> getUrlList() {
    return urlList;
  }

  /**
   * @param urlList
   *          セットする urlList
   */
  public void setUrlList(List<TimelineUrlResultData> urlList) {
    this.urlList = urlList;
  }

  public Integer getReplyCountInteger() {
    return Integer.valueOf((int) reply_count.getValue());
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
   * @return attachmentFileList
   */
  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  /**
   * @param attachmentFileList
   *          セットする attachmentFileList
   */
  public void setAttachmentFileList(List<FileuploadBean> attachmentFileList) {
    this.attachmentFileList =
      attachmentFileList == null
        ? new ArrayList<FileuploadBean>(0)
        : attachmentFileList;
  }

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
   * @param timeline_type
   *          セットする timeline_type
   */
  public void setTimelineType(String timeline_type) {
    this.timeline_type = timeline_type;
  }

  /**
   * @return timeline_type
   */
  public String getTimelineType() {
    return timeline_type;
  }

  /**
   * @param timeline_type
   *          セットする app_id
   */
  public void setAppId(String app_id) {
    this.app_id = app_id;
  }

  /**
   * @return app_id
   */
  public String getAppId() {
    return app_id;
  }

  public boolean isNotActivityParent() {
    if (app_id == null) {
      return true;
    }
    if (app_id.isEmpty()) {
      return true;
    }
    return !(app_id.equals("ACTIVITY_PARENT"));
  }

  /**
   * @param params
   *          セットする params
   */
  public void setParams(String params) {
    this.params.setValue(params);
  }

  /**
   * @return params
   */
  public ALStringField getParams() {
    return params;
  }

  public String getPopupUrl() {
    String portletParams = this.getParams().getValue();

    if (portletParams != null && portletParams.length() > 0) {
      StringBuilder b = new StringBuilder(portletParams);
      // if (portletParams.indexOf("?") > -1) {
      // b.append("&activityId=").append(id);
      // } else {
      // b.append("?activityId=").append(id);
      // }
      return b.toString();
    } else {
      return "";
    }
  }

  public int getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
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

  public ALEipUser getUser() {
    if (user == null) {
      user =
        ALEipManager.getInstance().getUser(
          Integer.valueOf((int) this.owner_id.getValue()));
    }
    return user;
  }

  /**
   * @param portletId
   *          セットする portletId
   */
  public void setPortletId(String portletId) {
    this.portletId = portletId;
  }

  /**
   * @return portletId
   */
  public String getPortletId() {
    return portletId;
  }

}
