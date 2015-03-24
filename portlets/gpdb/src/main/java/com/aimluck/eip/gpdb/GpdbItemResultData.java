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

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * Webデータベース項目定義のResultDataです。 <BR>
 * 
 */
public class GpdbItemResultData implements ALData {

  /** 項目定義 ID */
  protected ALNumberField gpdb_item_id;

  /** 項目定義名 */
  protected ALStringField gpdb_item_name;

  /** Webデータベース ID */
  protected ALNumberField gpdb_id;

  /** Webデータベース名 */
  protected ALStringField gpdb_name;

  /** タイトルフラグ */
  protected ALStringField title_flg;

  /** 必須フラグ */
  protected ALStringField required_flg;

  /** 形式 */
  protected ALStringField type;

  /** 形式名 */
  protected ALStringField type_name;

  /** 区分 ID */
  protected ALNumberField gpdb_kubun_id;

  /** 区分名 */
  protected ALStringField gpdb_kubun_name;

  /** 表示サイズ（横） */
  protected ALNumberField size_col;

  /** 表示サイズ（縦） */
  protected ALNumberField size_row;

  /** 表示行数 */
  protected ALNumberField line;

  /** 一覧画面表示フラグ */
  protected ALStringField list_flg;

  /** 詳細画面表示フラグ */
  protected ALStringField detail_flg;

  /** デフォルトソートフラグ */
  protected ALStringField default_sort_flg;

  /** ソート順 */
  protected ALStringField asc_desc;

  /**
   * 初期設定を行います
   */
  @Override
  public void initField() {
    gpdb_item_id = new ALNumberField();
    gpdb_item_name = new ALStringField();
    gpdb_id = new ALNumberField();
    gpdb_name = new ALStringField();
    title_flg = new ALStringField();
    required_flg = new ALStringField();
    type = new ALStringField();
    type_name = new ALStringField();
    gpdb_kubun_id = new ALNumberField();
    gpdb_kubun_name = new ALStringField();
    size_col = new ALNumberField();
    size_row = new ALNumberField();
    line = new ALNumberField();
    list_flg = new ALStringField();
    detail_flg = new ALStringField();
    default_sort_flg = new ALStringField();
    asc_desc = new ALStringField();
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
   * 項目名を取得する
   * 
   * @return 項目名
   */
  public ALStringField getGpdbItemName() {
    return gpdb_item_name;
  }

  /**
   * 項目名を取得する(Wbr挿入)
   * 
   * @return 項目名
   */
  public String getWbrGpdbItemName() {
    return ALCommonUtils.replaceToAutoCR(gpdb_item_name.toString());
  }

  /**
   * 項目名を取得する
   * 
   * @return 項目名
   */
  public String getRawGpdbItemName() {
    return gpdb_item_name.toString();
  }

  /**
   * 項目名を設定する
   * 
   * @param string
   *          項目名
   */
  public void setGpdbItemName(String string) {
    gpdb_item_name.setValue(string);
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
   * Webデータベースを設定する
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
   * タイトルフラグを取得する
   * 
   * @return タイトルフラグ
   */
  public ALStringField getTitleFlg() {
    return title_flg;
  }

  /**
   * タイトルフラグを設定する
   * 
   * @param string
   *          タイトルフラグ
   */
  public void setTitleFlg(String string) {
    title_flg.setValue(string);
  }

  /**
   * 必須フラグを取得する
   * 
   * @return 必須フラグ
   */
  public ALStringField getRequiredFlg() {
    return required_flg;
  }

  /**
   * 必須フラグを設定する
   * 
   * @param string
   *          必須フラグ
   */
  public void setRequiredFlg(String string) {
    required_flg.setValue(string);
  }

  /**
   * 項目形式を取得する
   * 
   * @return 項目形式
   */
  public ALStringField getType() {
    return type;
  }

  /**
   * 項目形式を設定する
   * 
   * @param string
   *          項目形式
   */
  public void setType(String string) {
    type.setValue(string);
  }

  /**
   * 形式名称を取得する
   * 
   * @return 形式名称
   */
  public ALStringField getTypeName() {
    return type_name;
  }

  /**
   * 形式名称を設定する
   * 
   * @param string
   *          形式名称
   */
  public void setTypeName(String string) {
    type_name.setValue(string);
  }

  /**
   * 区分マスタIDを取得する
   * 
   * @return 区分マスタID
   */
  public ALNumberField getGpdbKubunId() {
    return gpdb_kubun_id;
  }

  /**
   * 区分マスタIDを設定する
   * 
   * @param i
   *          区分マスタID
   */
  public void setGpdbKubunId(long i) {
    gpdb_kubun_id.setValue(i);
  }

  /**
   * 区分マスタ名を取得する
   * 
   * @return 区分マスタ名
   */
  public ALStringField getGpdbKubunName() {
    return gpdb_kubun_name;
  }

  /**
   * 区分マスタ名を取得する(Wbr挿入)
   * 
   * @return 区分マスタ名
   */
  public String getWbrGpdbKubunName() {
    return ALCommonUtils.replaceToAutoCRString(gpdb_kubun_name.toString());
  }

  /**
   * 区分マスタ名を設定する
   * 
   * @param string
   *          区分マスタ名
   */
  public void setGpdbKubunName(String string) {
    gpdb_kubun_name.setValue(string);
  }

  /**
   * 表示サイズ（横）を取得する
   * 
   * @return 表示サイズ（横）
   */
  public ALNumberField getSizeCol() {
    return size_col;
  }

  /**
   * 表示サイズ（横）を設定する
   * 
   * @param i
   *          表示サイズ（横）
   */
  public void setSizeCol(long i) {
    size_col.setValue(i);
  }

  /**
   * 表示サイズ（縦）を取得する
   * 
   * @return 表示サイズ（縦）
   */
  public ALNumberField getSizeRow() {
    return size_row;
  }

  /**
   * 表示サイズ（縦）を設定する
   * 
   * @param i
   *          表示サイズ（縦）
   */
  public void setSizeRow(long i) {
    size_row.setValue(i);
  }

  /**
   * 表示行数を取得する
   * 
   * @return 表示行数
   */
  public ALNumberField getLine() {
    return line;
  }

  /**
   * 表示行数を設定する
   * 
   * @param i
   *          表示行数
   */
  public void setLine(long i) {
    line.setValue(i);
  }

  /**
   * 一覧画面表示フラグを取得する
   * 
   * @return 一覧画面表示フラグ
   */
  public ALStringField getListFlg() {
    return list_flg;
  }

  /**
   * 一覧画面表示フラグを設定する
   * 
   * @param string
   *          一覧画面表示フラグ
   */
  public void setListFlg(String string) {
    list_flg.setValue(string);
  }

  /**
   * 詳細画面表示フラグを取得する
   * 
   * @return 詳細画面表示フラグ
   */
  public ALStringField getDetailFlg() {
    return detail_flg;
  }

  /**
   * 詳細画面表示フラグを設定する
   * 
   * @param string
   *          詳細画面表示フラグ
   */
  public void setDetailFlg(String string) {
    detail_flg.setValue(string);
  }

  /**
   * デフォルトソートフラグを取得する
   * 
   * @return デフォルトソートフラグ
   */
  public ALStringField getDefaultSortFlg() {
    return default_sort_flg;
  }

  /**
   * デフォルトソートフラグを設定する
   * 
   * @param string
   *          デフォルトソートフラグ
   */
  public void setDefaultSortFlg(String string) {
    default_sort_flg.setValue(string);
  }

  /**
   * ソート順を取得する
   * 
   * @return ソート順
   */
  public ALStringField getAscDesc() {
    return asc_desc;
  }

  /**
   * ソート順を設定する
   * 
   * @param string
   *          ソート順
   */
  public void setAscDesc(String string) {
    asc_desc.setValue(string);
  }
}
