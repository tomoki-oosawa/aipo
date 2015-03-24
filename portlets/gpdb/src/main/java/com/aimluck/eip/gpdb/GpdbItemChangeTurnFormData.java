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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdb;
import com.aimluck.eip.cayenne.om.portlet.EipTGpdbItem;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.gpdb.util.GpdbUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class GpdbItemChangeTurnFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbItemChangeTurnFormData.class.getName());

  /** 項目定義のリスト　ソート後 */
  private ALStringField positions;

  /** 項目定義ID配列 */
  private String[] gpdbItemIds = null;

  /** 項目定義情報のリスト */
  private List<GpdbItemResultData> gpdbItemList = null;

  /** 項目定義オブジェクトのリスト */
  private List<EipTGpdbItem> rawGpdbItemList = null;

  /** WebデータベースID */
  private String gpdbId = null;

  /** Webデータベースオブジェクト */
  private EipTGpdb gpdb = null;

  /**
   * 初期化します。
   * 
   * @param action
   *          ALAction
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    gpdbItemList = new ArrayList<GpdbItemResultData>();
    rawGpdbItemList = new ArrayList<EipTGpdbItem>();

    // WebデータベースID
    gpdbId = rundata.getParameters().getString("gpdb_id");

    // Webデータベースオブジェクト
    gpdb = GpdbUtils.getEipTGpdb(gpdbId);
  }

  /**
   * 各フィールドを初期化します。
   * 
   * 
   */
  @Override
  public void initField() {
    // 項目定義のリスト
    positions = new ALStringField();
    positions.setFieldName(ALLocalizationUtils
      .getl10n("GPDB_DEFINED_ITEM_LIST"));
    positions.setTrim(true);
  }

  /**
   * データに値を設定します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = true;
    try {
      res = super.setFormData(rundata, context, msgList);
      if (res) {
        if (positions.getValue() == null || positions.getValue().equals("")) {// 初期

          SelectQuery<EipTGpdbItem> query = Database.query(EipTGpdbItem.class);
          query.andQualifier(ExpressionFactory.matchExp(
            EipTGpdbItem.GPDB_PROPERTY,
            gpdb));
          query.orderAscending(EipTGpdbItem.ORDER_NO_PROPERTY);
          gpdbItemList = GpdbUtils.getGpdbItemResultList(query.fetchList());

        } else {// データ送信時
          StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
          gpdbItemIds = new String[st.countTokens()];
          int count = 0;
          while (st.hasMoreTokens()) {
            gpdbItemIds[count] = st.nextToken();
            count++;
          }
          SelectQuery<EipTGpdbItem> query = Database.query(EipTGpdbItem.class);
          List<EipTGpdbItem> list = query.fetchList();

          for (int i = 0; i < gpdbItemIds.length; i++) {
            EipTGpdbItem gpdbItem =
              getEipTGpdbItemFromGpdbItemId(list, gpdbItemIds[i]);
            gpdbItemList.add(GpdbUtils.getGpdbItemResultData(gpdbItem));
            rawGpdbItemList.add(gpdbItem);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return res;
  }

  /**
   * 各フィールドに対する制約条件を設定します。
   */
  @Override
  protected void setValidator() {
  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。
   * 
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    return msgList.isEmpty();
  }

  /**
   * 『項目定義』を読み込みます。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      return true;
    } catch (Exception e) {
      logger.error("Exception", e);
      return false;
    }
  }

  /**
   * 『項目定義』を追加します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 『項目定義』を更新します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = true;
    try {
      int newPosition = 1;
      for (EipTGpdbItem gpdbItem : rawGpdbItemList) {
        gpdbItem.setOrderNo(newPosition);
        newPosition++;
      }
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("Exception", e);
      res = false;
    }
    return res;
  }

  /**
   * 『項目定義』を削除します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 指定したchar型文字が記号であるかを判断します。
   * 
   * @param ch
   *          文字
   * @return TRUE 成功 FALSE 失敗
   */
  protected boolean isSymbol(char ch) {
    byte[] chars;

    try {
      chars = (Character.valueOf(ch).toString()).getBytes("shift_jis");
    } catch (UnsupportedEncodingException ex) {
      return false;
    }

    if (chars == null
      || chars.length == 2
      || Character.isDigit(ch)
      || Character.isLetter(ch)) {
      return false;
    } else {
      return true;
    }

  }

  /**
   * 指定した項目定義IDのオブジェクトを取得する．
   * 
   * @param gpdbItemList
   *          項目定義リスト
   * @param gpdbItemId
   *          項目定義ID
   * @return 項目定義オブジェクト
   */
  private EipTGpdbItem getEipTGpdbItemFromGpdbItemId(
      List<EipTGpdbItem> gpdbItemList, String gpdbItemId) {
    for (int i = 0; i < gpdbItemList.size(); i++) {
      EipTGpdbItem gpdbItem = gpdbItemList.get(i);
      if (gpdbItem.getGpdbItemId().toString().equals(gpdbItemId)) {
        return gpdbItem;
      }
    }
    return null;
  }

  /**
   * 項目定義情報のリストを取得する．
   * 
   * @return 項目定義情報のリスト
   */
  public List<GpdbItemResultData> getGpdbItemList() {
    return gpdbItemList;
  }

  /**
   * WebデータベースIDを返す
   * 
   * @return WebデータベースID
   */
  public String getGpdbId() {
    return gpdbId;
  }
}
