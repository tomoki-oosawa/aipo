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
package com.aimluck.eip.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.util.ALEipUtils;

/**
 * タスクのコメントResultDataです。 <BR>
 * 
 */
public class ProjectTaskCommentResultData implements ALData {

  /** コメント ID */
  protected ALNumberField comment_id;

  /** タスク ID */
  protected ALNumberField task_id;

  /** コメント */
  protected ALStringField comment;

  /** 登録者ID */
  private ALNumberField create_user_id;

  /** 登録者名 */
  private ALStringField create_user;

  /** 作成日 */
  protected ALDateTimeField create_date;

  /** 更新日 */
  protected ALDateTimeField update_date;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /** 顔写真フラグ */
  private boolean has_photo;

  /**
   * 初期設定を行います
   */
  @Override
  public void initField() {
    task_id = new ALNumberField();
    comment_id = new ALNumberField();
    comment = new ALStringField();
    create_user_id = new ALNumberField();
    create_user = new ALStringField();
    create_date = new ALDateTimeField("M月d日");
    update_date = new ALDateTimeField("M月d日");

    attachmentFileList = new ArrayList<FileuploadBean>();

    has_photo = false;
  }

  /**
   * コメントIDを取得する
   * 
   * @return コメントID
   */
  public ALNumberField getCommentId() {
    return comment_id;
  }

  /**
   * コメントIDを設定する
   * 
   * @param i
   *          コメントID
   */
  public void setCommentId(long i) {
    comment_id.setValue(i);
  }

  /**
   * タスクIDを取得する
   * 
   * @return タスクID
   */
  public ALNumberField getTaskId() {
    return task_id;
  }

  /**
   * タスクIDを設定する
   * 
   * @param i
   *          タスクID
   */
  public void setTaskId(long i) {
    task_id.setValue(i);
  }

  /**
   * コメントを取得する
   * 
   * @return タスクID
   */
  public String getComment() {
    return ALEipUtils.getMessageList(comment.getValue());
  }

  /**
   * コメントを設定する
   * 
   * @param s
   *          コメント
   */
  public void setComment(String s) {
    comment.setValue(s);
  }

  /**
   * 作成者IDを取得する
   * 
   * @return 作成者ID
   */
  public ALNumberField getCreateUserId() {
    return create_user_id;
  }

  /**
   * 作成者IDを設定する
   * 
   * @param i
   *          作成者ID
   */
  public void setCreateUserId(long i) {
    create_user_id.setValue(i);
  }

  /**
   * 作成者を取得する
   * 
   * return 作成者
   */
  public ALStringField getCreateUser() {
    return create_user;
  }

  /**
   * 作成者を設定する
   * 
   * @param str
   *          作成者
   */
  public void setCreateUser(String str) {
    create_user.setValue(str);
  }

  /**
   * 作成日を取得する
   * 
   * @return 作成日
   */
  public ALDateTimeField getCreateDate() {
    return create_date;
  }

  /**
   * 作成日を設定する
   * 
   * @param dt
   *          作成日
   */
  public void setCreateDate(Date dt) {
    create_date.setValue(dt);
  }

  /**
   * 更新日を取得する
   * 
   * @return 更新日
   */
  public ALDateTimeField getUpdateDate() {
    return update_date;
  }

  /**
   * 更新日を設定する
   * 
   * @param dt
   *          更新日
   */
  public void setUpdateDate(Date dt) {
    update_date.setValue(dt);
  }

  /**
   * 添付ファイルリストを取得します。
   * 
   * @return list 添付ファイルリスト
   */
  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  /**
   * 添付ファイルリストを設定します。
   * 
   * @param list
   *          添付ファイルリスト
   */
  public void setAttachmentFiles(List<FileuploadBean> list) {
    attachmentFileList = list;
  }

  /**
   * 顔写真フラグを設定する
   * 
   * @param b
   *          フラグ
   */
  public void setHasPhoto(boolean b) {
    has_photo = b;
  }

  /**
   * 顔写真フラグを取得する
   * 
   * @return has_photo
   */
  public boolean hasPhoto() {
    return has_photo;
  }
}
