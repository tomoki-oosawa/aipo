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
package com.aimluck.eip.gpdb;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * WebデータベースのResultDataです。 <BR>
 *
 */
public class GpdbResultData implements ALData {

  /** Webデータベース ID */
  protected ALNumberField gpdb_id;

  /** Webデータベース名 */
  protected ALStringField gpdb_name;

  /** 登録者名 */
  protected ALStringField create_user_name;

  /** 登録者ID */
  protected ALNumberField create_user_id;

  /** メール配信フラグ */
  protected ALStringField mail_flg;

  /** 作成日 */
  protected ALDateTimeField create_date;

  /** 更新日 */
  protected ALDateTimeField update_date;

  /** 登録データ件数 */
  protected int record_cnt;

  /**
   * 初期設定を行います
   */
  @Override
  public void initField() {
    gpdb_id = new ALNumberField();
    gpdb_name = new ALStringField();
    create_user_name = new ALStringField();
    create_user_id = new ALNumberField();
    mail_flg = new ALStringField();
    create_date = new ALDateTimeField("M月d日");
    update_date = new ALDateTimeField("M月d日");
    record_cnt = 0;
  }

  /**
   * WebデータベースIDを取得する
   *
   * @return WebデータベースID
   */
  public ALNumberField getGpdbId() {
    return gpdb_id;
  }

  /**
   * WebデータベースIDを設定する
   *
   * @param i
   *          WebデータベースID
   */
  public void setGpdbId(long i) {
    gpdb_id.setValue(i);
  }

  /**
   * Webデータベース名を取得する
   *
   * @return Webデータベース名
   */
  public ALStringField getGpdbName() {
    return gpdb_name;
  }

  /**
   * Webデータベース名を取得する(Wbr挿入)
   *
   * @return Webデータベース名
   */
  public String getWbrGpdbName() {
    return ALCommonUtils.replaceToAutoCR(gpdb_name.toString());
  }

  /**
   * Webデータベース名を設定する
   *
   * @param string
   *          Webデータベース名
   */
  public void setGpdbName(String string) {
    gpdb_name.setValue(string);
  }

  /**
   * 作成者名を取得する
   *
   * @return 作成者名
   */
  public ALStringField getCreateUserName() {
    return create_user_name;
  }

  /**
   * 作成者名を取得する(Wbr挿入)
   *
   * @return 作成者名
   */
  public String getWbrCreateUserName() {
    return ALCommonUtils.replaceToAutoCR(create_user_name.toString());
  }

  /**
   * 作成者名を設定する
   *
   * @param string
   *          作成者名
   */
  public void setCreateUserName(String string) {
    create_user_name.setValue(string);
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
   * 作成者IDを取得する(Wbr挿入)
   *
   * @return 作成者Id
   */
  // public String getWbrCreateUserId() {
  // return ALCommonUtils.replaceToAutoCR(create_user_id.toString());
  // }

  /**
   * 作成者Idを設定する
   *
   * @param string
   *          作成者Id
   */
  public void setCreateUserId(long i) {
    create_user_id.setValue(i);
  }

  /**
   * メール配信フラグを取得する
   *
   * @return メール配信フラグ
   */
  public ALStringField getMailFlg() {
    return mail_flg;
  }

  /**
   * メール配信フラグを設定する
   *
   * @param string
   *          メール配信フラグ
   */
  public void setMailFlg(String string) {
    mail_flg.setValue(string);
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
   * 登録データ件数を取得する
   *
   * @return 登録データ件数
   */
  public int getRecordCnt() {
    return record_cnt;
  }

  /**
   * 登録データ件数を設定する
   *
   * @param i
   *          登録データ件数
   */
  public void setRecordCnt(int i) {
    record_cnt = i;
  }
}
