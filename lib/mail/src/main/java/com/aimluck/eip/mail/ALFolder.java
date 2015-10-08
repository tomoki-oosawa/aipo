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
package com.aimluck.eip.mail;

import java.util.List;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMail;
import com.aimluck.eip.orm.query.ResultList;

/**
 * 送受信したメールを保持するローカルフォルダを表すインターフェイスです。 <br />
 * 
 */
public interface ALFolder {

  /** 受信 */
  public static final int TYPE_RECEIVE = 1;

  /** 送信 */
  public static final int TYPE_SEND = 2;

  /** UIDL を保存するファイルの名前 */
  public static final String FILE_UIDL = "uid.dat";

  /**
   * メールのインデックス情報を取得する。
   * 
   * @return
   */
  abstract public ResultList<EipTMail> getIndexRows(RunData rundata,
      Context context) throws Exception;

  // /**
  // * インデックス情報を変更する。
  // *
  // * @param rowIndex
  // * Index ファイル内の行番号（0 以上の整数）
  // * @param isRead
  // * 既読／未読
  // * @return
  // */
  // abstract public boolean changeReadInfoOfIndexFile(int rowIndex, boolean
  // isRead);

  /**
   * メールを取得する。
   * 
   * @param mailId
   * @return
   */
  abstract public ALMailMessage getMail(int mailId);

  /**
   * メールを保存する。
   * 
   * @param type
   *          送受信フラグ
   * @param localMailMessage
   * @return
   */
  abstract public boolean saveMail(ALMailMessage mail, String orgId);

  /**
   * 受信サーバから受信した受信可能サイズを超えたメールを保存する。<br />
   * このメールはヘッダ情報のみ、受信サーバから取得し、他の情報は取得しない。
   * 
   * @param localMailMessage
   * @return
   */
  abstract public boolean saveDefectiveMail(ALMailMessage mail, String orgId);

  /**
   * 指定されたインデックスのメールを削除する。
   * 
   * @mailId
   * @return
   */
  abstract public boolean deleteMail(int mailId);

  /**
   * 指定されたインデックスのメールを削除する．
   * 
   * @param msgIndexes
   * @return
   */
  abstract public boolean deleteMails(List<String> msgIndexes);

  /**
   * 指定されたインデックスのメールを既読にする．
   * 
   * @param msgIndexes
   * @return
   */
  abstract public boolean readMails(List<String> msgIndexes);

  /**
   * 保存してある UID リストを取得する。
   * 
   * @return
   */
  abstract public List<String> loadUID();

  /**
   * UID の一覧を保存する．
   * 
   * @param oldUIDL
   */
  abstract public void saveUID(List<String> oldUIDL);

  /**
   * ルートフォルダをセットする。
   * 
   * @param str
   */
  abstract public void setRootFolderPath(String str);

  /**
   * 自身のフォルダまでのフルパスを取得する。
   * 
   * @return
   */
  abstract public String getFullName();

  /**
   * 新着メール数を取得する。
   * 
   * @return
   */
  abstract public int getNewMailNum();

  /**
   * 新着メール数を更新する．
   * 
   * @param num
   */
  abstract public void setNewMailNum(int num);

  /**
   * 指定したフォルダ内の未読メール数を取得する．
   * 
   * @return
   */
  abstract public int getUnreadMailNum();

  /**
   * ローカルフォルダを閉じる．
   */
  abstract public void close();

  /**
   * 表示する項目数を設定します。
   * 
   * @param num
   */
  abstract public void setRowsNum(int num);

  /**
   * 表示文字数を取得します。
   * 
   * @return
   */
  abstract public int getStrLength();

  /**
   * 表示する項目数を取得します。
   * 
   * @return
   */
  abstract public int getRowsNum();

  /**
   * 総件数を取得します。
   * 
   * @return
   */
  abstract public int getCount();

  /**
   * 総ページ数を取得します。
   * 
   * @return
   */
  abstract public int getPagesNum();

  /**
   * 現在表示されているページを取得します。
   * 
   * @return
   */
  abstract public int getCurrentPage();

  /**
   * 
   * @return
   */
  abstract String getCurrentSort();

  /**
   * 
   * @return
   */
  abstract String getCurrentSortType();

  /**
   * @return
   */
  abstract int getStart();
}
