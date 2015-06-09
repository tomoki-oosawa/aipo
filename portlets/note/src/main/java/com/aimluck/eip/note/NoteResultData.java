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
package com.aimluck.eip.note;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.note.util.NoteUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 伝言メモのResultDataです。
 */
public class NoteResultData implements ALData {

  /** 伝言メモ ID */
  private ALNumberField note_id;

  /** 送信元ユーザ ID（アカウント ID） */
  private ALStringField src_user_id;

  /** 宛先ユーザ ID（アカウント ID） */
  private ALStringField dest_user_id;

  /** 送信元ユーザ名 */
  private ALStringField src_user_fullname;

  /** 宛先ユーザ名 */
  private ALStringField dest_user_fullname;

  /** <code>members</code> 送信先メンバー */
  private List<ALEipUser> dest_users = null;

  /** 依頼者名 */
  private ALStringField client_name;

  /** 依頼者所属 */
  private ALStringField company_name;

  /** 依頼者電話番号 */
  private ALStringField telephone;

  /** 依頼者メールアドレス */
  private ALStringField email_address;

  /** 追加送信先タイプ（パソコンのメールアドレスに送信） */
  private ALStringField add_dest_type_pc;

  /** 追加送信先タイプ（携帯電話のメールアドレスに送信） */
  private ALStringField add_dest_type_cellphone;

  /** 用件タイプ */
  private ALStringField subject_type;

  /** 用件（カスタム） */
  private ALStringField custom_subject;

  /** 新着／未読／既読フラグ */
  private ALStringField note_stat = null;

  /** 送信したメッセージ数 */
  private ALNumberField sent_note;

  /** 新着メッセージ数 */
  private ALNumberField new_note;

  /** 未読メッセージ数 */
  private ALNumberField unread_note;

  /** 既読メッセージ数 */
  private ALNumberField read_note;

  /** メモ */
  private ALStringField message;

  /** 受付日時 */
  private ALDateTimeField accept_date;

  /** 確認日時 */
  private ALDateTimeField confirm_date;

  /** 作成日 */
  private ALDateTimeField create_date;

  /** 更新日時 */
  private ALDateTimeField update_date;

  /** メモの有無 */
  private boolean hasMemo;

  /** 新着／未読／既読の画像へのファイルパス */
  private String note_stat_image_path = null;

  /** 新着／未読／既読の画像に対する説明文 */
  private String note_stat_image_description = null;

  /**
   *
   */
  @Override
  public void initField() {
    note_id = new ALNumberField();
    src_user_id = new ALStringField();
    dest_user_id = new ALStringField();
    src_user_fullname = new ALStringField();
    dest_user_fullname = new ALStringField();
    dest_users = new ArrayList<ALEipUser>();
    client_name = new ALStringField();
    company_name = new ALStringField();
    telephone = new ALStringField();
    email_address = new ALStringField();
    add_dest_type_pc = new ALStringField();
    add_dest_type_cellphone = new ALStringField();
    subject_type = new ALStringField();
    custom_subject = new ALStringField();
    note_stat = new ALStringField();
    sent_note = new ALNumberField();
    new_note = new ALNumberField();
    unread_note = new ALNumberField();
    read_note = new ALNumberField();
    message = new ALStringField();
    message.setTrim(false);
    accept_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    confirm_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    create_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
    update_date = new ALDateTimeField(NoteUtils.DATE_TIME_FORMAT);
  }

  /**
   * @return
   */
  public ALDateTimeField getAcceptDate() {
    return ALEipUtils.getFormattedTime(accept_date);
  }

  public ALDateTimeField getAuiAcceptDate() {
    return ALEipUtils.getFormattedTime(accept_date);
  }

  /**
   * @return
   */
  public ALStringField getAddDestTypePc() {
    return add_dest_type_pc;
  }

  /**
   * @return
   */
  public ALStringField getAddDestTypeCellphone() {
    return add_dest_type_cellphone;
  }

  /**
   * @return
   */
  public String getClientName() {
    return ALCommonUtils.replaceToAutoCR(client_name.toString());
  }

  /**
   * @return
   */
  public String getCompanyName() {
    return ALCommonUtils.replaceToAutoCR(company_name.toString());
  }

  /**
   * @return
   */
  public ALDateTimeField getConfirmDate() {
    return ALEipUtils.getFormattedTime(confirm_date);
  }

  /**
   * @return
   */
  public ALDateTimeField getCreateDate() {
    return ALEipUtils.getFormattedTime(create_date);
  }

  public ALDateTimeField getAuiCreateDate() {
    return ALEipUtils.getFormattedTime(create_date);
  }

  /**
   * @return
   */
  public ALStringField getDestUserId() {
    return dest_user_id;
  }

  /**
   * @return
   */
  public ALStringField getEmailAddress() {
    return email_address;
  }

  /**
   * @return
   */
  public String getMessage() {
    return ALEipUtils.getMessageList(message.getValue());
  }

  /**
   * @return
   */
  public ALNumberField getNoteId() {
    return note_id;
  }

  /**
   * @return
   */
  public ALStringField getSrcUserId() {
    return src_user_id;
  }

  /**
   * @return
   */
  public ALStringField getSubjectType() {
    return subject_type;
  }

  public String getCustomSubject() {
    return ALCommonUtils.replaceToAutoCR(custom_subject.toString());
  }

  /**
   * @return
   */
  public ALStringField getTelephone() {
    return telephone;
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * @return
   */
  public List<ALEipUser> getDestUsers() {
    return dest_users;
  }

  /**
   * @param field
   */
  public void setAcceptDate(Date date) {
    if (date == null) {
      return;
    }
    accept_date.setValue(date);
  }

  /**
   * @param field
   */
  public void setAddDestTypePc(String field) {
    add_dest_type_pc.setValue(field);
  }

  /**
   * @param field
   */
  public void setAddDestTypeCellphone(String field) {
    add_dest_type_cellphone.setValue(field);
  }

  /**
   * @param field
   */
  public void setClientName(String field) {
    client_name.setValue(field);
  }

  /**
   * @param field
   */
  public void setCompanyName(String field) {
    company_name.setValue(field);
  }

  /**
   * @param field
   */
  public void setConfirmDate(Date date) {
    if (date == null) {
      return;
    }
    confirm_date.setValue(date);
  }

  /**
   * @param field
   */
  public void setCreateDate(Date date) {
    if (date == null) {
      return;
    }
    create_date.setValue(date);
  }

  /**
   * @param field
   */
  public void setDestUserId(String field) {
    dest_user_id.setValue(field);
  }

  /**
   * @param field
   */
  public void setEmailAddress(String field) {
    email_address.setValue(field);
  }

  /**
   * @param field
   */
  public void setMessage(String field) {
    message.setValue(field);
  }

  /**
   * @param field
   */
  public void setNoteId(long id) {
    note_id.setValue(id);
  }

  /**
   * @param field
   */
  public void setSrcUserId(String field) {
    src_user_id.setValue(field);
  }

  /**
   * @param field
   */
  public void setSubjectType(String field) {
    subject_type.setValue(field);
  }

  public void setCustomSubject(String field) {
    custom_subject.setValue(field);
  }

  /**
   * @param field
   */
  public void setTelephone(String field) {
    telephone.setValue(field);
  }

  /**
   * @param field
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    update_date.setValue(date);
  }

  public ALStringField getNoteStat() {
    return note_stat;
  }

  public void setNoteStat(String value) {
    note_stat.setValue(value);
  }

  public ALNumberField getSentNote() {
    return sent_note;
  }

  public void setSentNote(long value) {
    sent_note.setValue(value);
  }

  public ALNumberField getNewNote() {
    return new_note;
  }

  public void setNewNote(long value) {
    new_note.setValue(value);
  }

  public ALNumberField getUnreadNote() {
    return unread_note;
  }

  public void setUnreadNote(long value) {
    unread_note.setValue(value);
  }

  public ALNumberField getReadNote() {
    return read_note;
  }

  public void setReadNote(long value) {
    read_note.setValue(value);
  }

  /**
   * 新着／未読／既読の画像ファイルへのパスを返す．
   *
   * @return
   */
  public void setNoteStatImage(String noteStatImagePath) {
    this.note_stat_image_path = noteStatImagePath;
  }

  /**
   * 新着／未読／既読の画像ファイルへのパスを返す．
   *
   * @return
   */
  public String getNoteStatImage() {
    return note_stat_image_path;
  }

  /**
   *
   * @param noteStatImageDescription
   */
  public void setNoteStatImageDescription(String noteStatImageDescription) {
    this.note_stat_image_description = noteStatImageDescription;
  }

  /**
   *
   * @return
   */
  public String getNoteStatImageDescription() {
    return note_stat_image_description;
  }

  /**
   *
   * @return
   */
  public ALStringField getSrcUserFullName() {
    return src_user_fullname;
  }

  /**
   *
   * @return
   */
  public ALStringField getDestUserFullName() {
    return dest_user_fullname;
  }

  /**
   *
   * @param field
   */
  public void setSrcUserFullName(String field) {
    src_user_fullname.setValue(field);
  }

  /**
   *
   * @param field
   */
  public void setDestUserFullName(String field) {
    dest_user_fullname.setValue(field);
  }

  /**
   *
   * @return
   */
  public boolean hasMemo() {
    return hasMemo;
  }

  /**
   *
   * @param hasMemo
   */
  public void setHasMemo(boolean hasMemo) {
    this.hasMemo = hasMemo;
  }

  /**
   *
   * @param
   */
  public void setDestUsers(List<ALEipUser> dest_users) {
    this.dest_users = dest_users;
  }

}
