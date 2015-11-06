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
package com.aimluck.eip.webmail;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;

/**
 * ウェブメールのフィルタのResultDataです。 <BR>
 * 
 */
public class WebMailFilterResultData implements ALData {

  /** Folder ID */
  private ALNumberField filter_id;

  /** 処理順 */
  private ALNumberField sort_order;

  /** フィルタ名 */
  private ALStringField filter_name;

  /** フィルタ文字列 */
  private ALStringField filter_string;

  /** フィルタ種別 */
  private ALStringField filter_type;

  /** 振り分け先フォルダ名 */
  private ALStringField dst_folder_name;

  /** 作成日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALStringField update_date;

  /**
   *
   *
   */
  @Override
  public void initField() {
    filter_id = new ALNumberField();
    sort_order = new ALNumberField();
    filter_name = new ALStringField();
    filter_string = new ALStringField();
    filter_type = new ALStringField();
    dst_folder_name = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALStringField();
  }

  /**
   * @return
   */
  public ALNumberField getFilterId() {
    return filter_id;
  }

  /**
   * フィルタ名を返します。
   * 
   * @return
   */
  public ALNumberField getSortOrder() {
    return sort_order;
  }

  /**
   * フィルタ名を返します。
   * 
   * @return
   */
  public ALStringField getFilterName() {
    return filter_name;
  }

  /**
   * フィルタ文字列を返します。
   * 
   * @return
   */
  public ALStringField getFilterString() {
    return filter_string;
  }

  /**
   * フィルタ種別を返します。
   * 
   * @return
   */
  public ALStringField getFilterType() {
    return filter_type;
  }

  /**
   * 振り分け先のフォルダ名を返します。
   * 
   * @return
   */
  public ALStringField getDstFolderName() {
    return dst_folder_name;
  }

  /**
   * フィルタの作成日を返します。
   * 
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * フィルタの更新日を返します。
   * 
   * @return
   */
  public ALStringField getUpdateDate() {
    return update_date;
  }

  /**
   * フィルタIDをセットします。
   * 
   * @param i
   */
  public void setFilterId(long i) {
    filter_id.setValue(i);
  }

  /**
   * 処理順の番号をセットします。
   * 
   * @param i
   */
  public void setSortOrder(long i) {
    sort_order.setValue(i);
  }

  /**
   * フィルタ名をセットします。
   * 
   * @param string
   */
  public void setFilterName(String string) {
    filter_name.setValue(string);
  }

  /**
   * フィルタ文字列をセットします。
   * 
   * @param string
   */
  public void setFilterString(String string) {
    filter_string.setValue(string);
  }

  /**
   * フィルタ種別をセットします。
   * 
   * @param string
   */
  public void setFilterType(String string) {
    filter_type.setValue(string);
  }

  /**
   * 振り分け先のフォルダ名をセットします。
   * 
   * @param string
   */
  public void setDstFolderName(String string) {
    dst_folder_name.setValue(string);
  }

  /**
   * フィルタ作成日をセットします。
   * 
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * フィルタ更新日をセットします。
   * 
   * @param string
   */
  public void setUpdateDate(String string) {
    update_date.setValue(string);
  }
}
