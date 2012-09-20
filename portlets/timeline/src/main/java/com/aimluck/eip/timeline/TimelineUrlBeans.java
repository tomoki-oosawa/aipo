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

import java.util.List;

import com.aimluck.eip.util.ALCommonUtils;

/**
 * タイムライントピックの検索データを管理するクラスです。 <BR>
 * 
 */
public class TimelineUrlBeans {
  /** 画像 */
  private List<String> images;

  /** タイトル */
  private String title;

  /** URL */
  private String url;

  /** メモ */
  private String body;

  /** YouTube動画Id */
  private String youtubeId;

  /** YouTubeの判断 */
  private boolean youtubeFlag;

  /**
   * @return images
   */
  public List<String> getImages() {
    return images;
  }

  /**
   * @param images
   *          セットする images
   */
  public void setImages(List<String> images) {
    this.images = images;
  }

  /**
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title
   *          セットする title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return url
   */
  public String getUrl() {
    return url;
  }

  public String getAutoCRUrl() {
    return ALCommonUtils.replaceToAutoCRString(url);
  }

  /**
   * @param url
   *          セットする url
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return body
   */
  public String getBody() {
    return body;
  }

  /**
   * @param body
   *          セットする body
   */
  public void setBody(String body) {
    this.body = body;
  }

  /**
   * @return youtubeId
   */
  public String getYoutubeId() {
    return youtubeId;
  }

  /**
   * @param youtubeId
   *          セットする youtubeId
   */
  public void setYoutubeId(String youtubeId) {
    this.youtubeId = youtubeId;
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

}
