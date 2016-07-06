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
package com.aimluck.eip.fileio;

import java.util.ArrayList;
import java.util.List;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALLocalizationUtils;

public class FileIOAccountCsvResultData implements ALData {

  /** データのCSVファイル上での位置(行数) */
  private int line_count;

  /** 部署名リスト */
  private List<String> post_name_list;

  /** 部署名 */
  private ALStringField post_name;

  /** 役職 */
  private ALStringField position_name;

  /** ユーザー */
  private TurbineUser user;

  /** 部署がデータベースに存在するか否か */
  private boolean post_not_found;

  /** 役職がデータベースに存在するか否か */
  private boolean position_not_found;

  /** 同じユーザー名が存在するか否か */
  private boolean same_user;

  /** 同じ社員コードが存在するか否か */
  private boolean same_code;

  /** エラーが起きたかどうか */
  private boolean is_error;

  /**
   * 各フィールドを初期化 <BR>
   */
  @Override
  public void initField() {
    line_count = 0;
    // 部署名
    post_name_list = new ArrayList<String>();
    post_name = new ALStringField();
    post_name.setFieldName(ALLocalizationUtils.getl10n("FILIIO_UNIT_NAME"));
    post_name.setTrim(true);

    // 役職
    position_name = new ALStringField();
    position_name.setFieldName(ALLocalizationUtils.getl10n("FILEIO_POST"));
    position_name.setTrim(true);
    user = null;

    setPostNotFound(false);
    setPositionNotFound(false);
    setSameCode(false);

  }

  /**
   * データのCSVファイル上での位置(行数)を入力します <BR>
   *
   * @param i
   */
  public void setLineCount(int i) {
    line_count = i;
  }

  /**
   * ユーザーオブジェクトモデルを入力します <BR>
   *
   * @param data
   */
  public void setUser(TurbineUser data) {
    user = data;
  }

  /**
   * データのCSVファイル上での位置(行数)を取得します <BR>
   *
   * @return
   */
  public int getLineCount() {
    return line_count;
  }

  /**
   * ユーザーオブジェクトモデルを取得します <BR>
   *
   * @return
   */
  public TurbineUser getUser() {
    return user;
  }

  /**
   * 部署名を取得します <BR>
   *
   * @return
   */
  public ALStringField getPostName() {
    return post_name;
  }

  public List<String> getPostNameList() {
    return post_name_list;
  }

  /**
   * 役職名を取得します <BR>
   *
   * @return
   */
  public ALStringField getPositionName() {
    return position_name;
  }

  /**
   * 部署がデータベースに存在するかを示すフラグを取得します <BR>
   *
   * @return
   */
  public boolean getPostNotFound() {
    return post_not_found;
  }

  /**
   * 役職がデータベースに存在するかを示すフラグを取得します <BR>
   *
   * @return
   */
  public boolean getPositionNotFound() {
    return position_not_found;
  }

  /**
   * 同じユーザー名が存在するかを示すフラグを取得します <BR>
   *
   * @return
   */
  public boolean getSameUser() {
    return same_user;
  }

  /**
   * 同じ社員コードが存在するかを示すフラグを取得します <BR>
   *
   * @return
   */
  public boolean getSameCode() {
    return same_code;
  }

  /**
   * エラーが起きたかどうかを示すフラグを取得します <BR>
   *
   * @return
   */
  public boolean getIsError() {
    return is_error;
  }

  /**
   * 部署名を入力します <BR>
   *
   * @param str
   */
  public void setPostName(String str) {
    post_name.setValue(str);
  }

  public void setPostNameList(List<String> postnames) {
    post_name_list.addAll(postnames);
  }

  /**
   * 役職名を入力します <BR>
   *
   * @param str
   */
  public void setPositionName(String str) {
    position_name.setValue(str);
  }

  /**
   * 部署がデータベースに存在するかを示すフラグを設定します <BR>
   *
   * @param flg
   */
  public void setPostNotFound(boolean flg) {
    post_not_found = flg;
  }

  /**
   * 役職がデータベースに存在するかを示すフラグを設定します <BR>
   *
   * @param flg
   */
  public void setPositionNotFound(boolean flg) {
    position_not_found = flg;
  }

  /**
   * 同じユーザー名が存在するかを示すフラグを設定します <BR>
   *
   * @param flg
   */
  public void setSameUser(boolean flg) {
    same_user = flg;
  }

  /**
   * 同じ社員コードが存在するかを示すフラグを設定します <BR>
   *
   * @param flg
   */
  public void setSameCode(boolean flg) {
    same_code = flg;
  }

  /**
   * エラーが起きたかどうかを示すフラグを設定します <BR>
   *
   * @param flg
   */
  public void setIsError(boolean flg) {
    is_error = flg;
  }
}
