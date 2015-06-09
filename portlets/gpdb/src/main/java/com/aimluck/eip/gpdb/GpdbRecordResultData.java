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

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Webデータベースレコード情報のResultDataです。 <BR>
 *
 */
public class GpdbRecordResultData implements ALData {

  /** レコードID */
  protected ALNumberField gpdb_record_id;

  /** WebデータベースID */
  protected ALNumberField gpdb_id;

  /** 項目定義ID */
  protected ALNumberField gpdb_item_id;

  /** レコードNo */
  protected ALNumberField record_no;

  /** 値 */
  protected ALStringField value;

  /** 値(表示用) */
  protected ALStringField disp_value;

  /** ユーザーID */
  protected ALNumberField user_id;

  /** 値リスト */
  protected List<String> value_list;

  /** 添付ファイルリスト */
  private List<FileuploadBean> attachmentFileList = null;

  /**
   * 初期設定を行います
   */
  @Override
  public void initField() {
    gpdb_record_id = new ALNumberField();
    gpdb_id = new ALNumberField();
    gpdb_item_id = new ALNumberField();
    record_no = new ALNumberField();
    value = new ALStringField();
    disp_value = new ALStringField();
    user_id = new ALNumberField();
    value_list = new ArrayList<String>();
    attachmentFileList = new ArrayList<FileuploadBean>();
  }

  /**
   * レコードIDを取得する
   *
   * @return レコードID
   */
  public ALNumberField getGpdbRecordId() {
    return gpdb_record_id;
  }

  /**
   * レコードIDを設定する
   *
   * @param i
   *          レコードID
   */
  public void setGpdbRecordId(long i) {
    gpdb_record_id.setValue(i);
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
   * 項目定義IDを取得する
   *
   * @return 項目定義ID
   */
  public ALNumberField getGpdbItemId() {
    return gpdb_item_id;
  }

  /**
   * 項目定義IDを設定する
   *
   * @param i
   *          項目定義ID
   */
  public void setGpdbItemId(long i) {
    gpdb_item_id.setValue(i);
  }

  /**
   * レコードNoを取得する
   *
   * @return レコードNo
   */
  public ALNumberField getRecordNo() {
    return record_no;
  }

  /**
   * レコードNoを設定する
   *
   * @param i
   *          レコードNo
   */
  public void setRecordNo(long i) {
    record_no.setValue(i);
  }

  /**
   * 値を取得する
   *
   * @return 値
   */
  public ALStringField getValue() {
    return value;
  }

  /**
   * 値を取得する(Wbr挿入)
   *
   * @return 値
   */
  public String getWbrValue() {
    return ALCommonUtils.replaceToAutoCR(value.toString());
  }

  /**
   * 値を設定する
   *
   * @param s
   *          値
   */
  public void setValue(String s) {
    value.setValue(s);
  }

  /**
   * 値（表示用）を取得する
   *
   * @return 値（表示用）
   */
  public ALStringField getDispValue() {
    return disp_value;
  }

  /**
   * 値（表示用）を取得する(Wbr挿入)
   *
   * @return 値（表示用）
   */
  public String getWbrDispValue() {
    return ALCommonUtils.replaceToAutoCR(disp_value.toString());
  }

  /**
   * 値（表示用）を設定する
   *
   * @param s
   *          値（表示用）
   */
  public void setDispValue(String s) {
    disp_value.setValue(s);
  }

  /**
   * ユーザーIDを取得する
   *
   * @return ユーザーID
   */
  public ALNumberField getUserId() {
    return user_id;
  }

  /**
   * ユーザーIDを設定する
   *
   * @param i
   *          ユーザーID
   */
  public void setUserId(long i) {
    user_id.setValue(i);
  }

  /**
   * 値をリスト化したものを設定する
   *
   * @param val
   *          値のリスト
   */
  public void setValueList(List<String> val) {
    value_list = val;
  }

  /**
   * 値をリスト化したものを取得する
   *
   * @return 値のリスト
   */
  public List<String> getValueList() {
    return value_list;
  }

  /**
   * 値を取得する<br/>
   * 改行コードを&lt;br/&gt;に変換する
   *
   * @return 値
   */
  public String getValueBr() {
    return ALEipUtils.getMessageList(value.getValue());
  }

  /**
   * 添付ファイルリストを取得する
   *
   * @return 添付ファイルリスト
   */
  public List<FileuploadBean> getAttachmentFileList() {
    return attachmentFileList;
  }

  /**
   * 添付ファイルリストを設定する
   *
   * @param list
   *          添付ファイルリスト
   */
  public void setAttachmentFiles(List<FileuploadBean> list) {
    attachmentFileList = list;
  }
}
