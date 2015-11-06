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
package com.aimluck.eip.timeline;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * タイムライントピックのResultData <BR>
 * 
 */
public class TimelineUrlResultData implements ALData {

  /** トピック ID */
  private ALNumberField timelineUrlId;

  /** Urlが貼られたトピックID */
  private ALNumberField timelineId;

  /** サムネイル */
  private byte[] thumbnail;

  /** タイトル */
  private ALStringField title;

  /** Url */
  private ALStringField url;

  /** YouTube動画Id */
  private ALStringField youtubeId;

  /** メモ */
  private ALStringField body;

  /** サムネイルの有無 */
  private boolean thumbnailFlag;

  /** YouTubeの判断 */
  private boolean youtubeFlag;

  /**
   *
   *
   */
  @Override
  public void initField() {
    timelineUrlId = new ALNumberField();
    timelineId = new ALNumberField();
    title = new ALStringField();
    url = new ALStringField();
    body = new ALStringField();
    youtubeId = new ALStringField();
    setThumbnailFlag(false);
    setYoutubeFlag(false);
  }

  /**
   * @return timelineUrlId
   */
  public ALNumberField getTimelineUrlId() {
    return timelineUrlId;
  }

  /**
   * @param timelineUrlId
   *          セットする timelineUrlId
   */
  public void setTimelineUrlId(long i) {
    timelineUrlId.setValue(i);
  }

  /**
   * @return timelineId
   */
  public ALNumberField getTimelineId() {
    return timelineId;
  }

  /**
   * @param timelineId
   *          セットする timelineId
   */
  public void setTimelineId(long i) {
    timelineId.setValue(i);
  }

  /**
   * @return thumbnail
   */
  public byte[] getThumbnail() {
    return thumbnail;
  }

  /**
   * @param thumbnail
   *          セットする thumbnail
   */
  public void setThumbnail(byte[] b) {
    thumbnail = b;
  }

  /**
   * @return title
   */
  public ALStringField getTitle() {
    return title;
  }

  public String getAutoCRTitle() {
    return ALCommonUtils.replaceToAutoCR(title.toString());
  }

  /**
   * @param title
   *          セットする title
   */
  public void setTitle(String str) {
    title.setValue(str);
  }

  /**
   * @return url
   */
  public ALStringField getUrl() {
    return url;
  }

  /**
   * @param url
   *          セットする url
   */
  public void setUrl(String str) {
    url.setValue(str);
  }

  /**
   * @return baseUrl
   */
  public String getBasePath() {
    String u = url.getValue();
    int j = u.indexOf("//") + 2;
    int i = u.indexOf('/', j);
    if (i >= 0) {
      return u.substring(j, i);
    } else {
      return u.substring(j, u.length());
    }
  }

  public String getAutoCRBasePath() {
    return ALCommonUtils.replaceToAutoCR(getBasePath());
  }

  /**
   * @return body
   */
  public ALStringField getBody() {
    return body;
  }

  public String getAutoCRBody() {
    return ALCommonUtils.replaceToAutoCR(body.toString());
  }

  /**
   * @param body
   *          セットする body
   */
  public void setBody(String str) {
    body.setValue(str);
  }

  public boolean isThumbnailFlag() {
    return thumbnailFlag;
  }

  public void setThumbnailFlag(boolean thumbnailFlag) {
    this.thumbnailFlag = thumbnailFlag;
  }

  /**
   * @return youtubeFlag
   */
  public boolean isYoutubeFlag() {
    return youtubeFlag;
  }

  /**
   * @param youtubeFlag
   *          セットする youtubeFlag
   */
  public void setYoutubeFlag(boolean youtubeFlag) {
    this.youtubeFlag = youtubeFlag;
  }

  /**
   * @return youtubeId
   */
  public ALStringField getYoutubeId() {
    return youtubeId;
  }

  /**
   * @param youtubeId
   *          セットする youtubeId
   */
  public void setYoutubeId(String str) {
    youtubeId.setValue(str);
  }

}
