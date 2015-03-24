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
import com.aimluck.eip.cayenne.om.portlet.EipMGpdbKubun;
import com.aimluck.eip.cayenne.om.portlet.EipMGpdbKubunValue;
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
public class GpdbKubunChangeTurnFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GpdbKubunChangeTurnFormData.class.getName());

  /** 区分値のリスト文字列　ソート後 */
  private ALStringField positions;

  /** 区分値ID配列 ソート後 */
  private String[] gpdbKubunValueIds = null;

  /** 区分値ResultDataのリスト */
  private List<GpdbKubunValueResultData> gpdbKubunValueList = null;

  /** 区分値オブジェクトのリスト */
  private List<EipMGpdbKubunValue> rawGpdbKubunValueList = null;

  /** 区分ID */
  private Integer kubunId = null;

  /** 区分オブジェクト */
  private EipMGpdbKubun kubun = null;

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

    gpdbKubunValueList = new ArrayList<GpdbKubunValueResultData>();
    rawGpdbKubunValueList = new ArrayList<EipMGpdbKubunValue>();

    // WebデータベースID
    kubunId = rundata.getParameters().getInteger("gpdb_kubun_id");

    // Webデータベースオブジェクト
    kubun = GpdbUtils.getEipMGpdbKubun(kubunId);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // 区分値のリスト
    positions = new ALStringField();
    positions.setFieldName(ALLocalizationUtils.getl10n("GPDB_VALUE_LIST"));
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

          SelectQuery<EipMGpdbKubunValue> query =
            Database.query(EipMGpdbKubunValue.class);
          query.andQualifier(ExpressionFactory.matchExp(
            EipMGpdbKubunValue.GPDB_KUBUN_PROPERTY,
            kubun));
          query.orderAscending(EipMGpdbKubunValue.ORDER_NO_PROPERTY);
          gpdbKubunValueList =
            GpdbUtils.getGpdbKubunValueResultList(query.fetchList());

        } else {// データ送信時
          StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
          gpdbKubunValueIds = new String[st.countTokens()];
          int count = 0;
          while (st.hasMoreTokens()) {
            gpdbKubunValueIds[count] = st.nextToken();
            count++;
          }
          SelectQuery<EipMGpdbKubunValue> query =
            Database.query(EipMGpdbKubunValue.class);
          List<EipMGpdbKubunValue> list = query.fetchList();

          for (int i = 0; i < gpdbKubunValueIds.length; i++) {
            EipMGpdbKubunValue gpdbKubunValue =
              getEipTGpdbKubunValueFromKubunValueId(list, gpdbKubunValueIds[i]);
            gpdbKubunValueList.add(GpdbUtils
              .getGpdbKubunValueResultData(gpdbKubunValue));
            rawGpdbKubunValueList.add(gpdbKubunValue);
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
   * 各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
  }

  /**
   * 区分値のフォームに入力されたデータの妥当性検証を行います。
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
   * 『区分値』を読み込みます。
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
   * 区分値をデータベースに格納します。
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
   * 区分値を更新します。
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
      for (EipMGpdbKubunValue gpdbKubunValue : rawGpdbKubunValueList) {
        gpdbKubunValue.setOrderNo(newPosition);
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
   * 区分値をデータベースから削除します。
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
   * 指定した区分値IDのオブジェクトを取得する．
   * 
   * @param gpdbKubunValueList
   *          区分値オブジェクトリスト
   * @param kubunValueId
   *          区分値ID
   * @return 区分値オブジェクト
   */
  private EipMGpdbKubunValue getEipTGpdbKubunValueFromKubunValueId(
      List<EipMGpdbKubunValue> gpdbKubunValueList, String kubunValueId) {
    for (int i = 0; i < gpdbKubunValueList.size(); i++) {
      EipMGpdbKubunValue gpdbItem = gpdbKubunValueList.get(i);
      if (gpdbItem.getGpdbKubunValueId().toString().equals(kubunValueId)) {
        return gpdbItem;
      }
    }
    return null;
  }

  /**
   * 区分値情報のリストを取得する．
   * 
   * @return 区分値情報のリスト
   */
  public List<GpdbKubunValueResultData> getGpdbKubunValueList() {
    return gpdbKubunValueList;
  }

  /**
   * 区分IDを返す
   * 
   * @return 区分ID
   */
  public Integer getGpdbKubunId() {
    return kubunId;
  }
}
