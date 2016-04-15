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

package com.aimluck.eip.account;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMPosition;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;

/**
 * 役職の順番情報のフォームデータを管理するためのクラスです。 <br />
 */
public class AccountPositionChangeTurnFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPositionChangeTurnFormData.class.getName());

  // 役職名のリスト　ソート後
  private ALStringField positions;

  private String[] accountPositionIds = null;

  /** 役職情報のリスト */
  private List<AccountPositionResultData> accountPositionList = null;

  private List<EipMPosition> rawAccountPositionList = null;

  /**
   * 初期化します。
   *
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    accountPositionList = new ArrayList<AccountPositionResultData>();
    rawAccountPositionList = new ArrayList<EipMPosition>();
  }

  /**
   * 各フィールドを初期化します。 <BR>
   *
   *
   */
  @Override
  public void initField() {
    // 名のリスト
    positions = new ALStringField();
    positions.setFieldName("役職名リスト");
    positions.setTrim(true);
  }

  /**
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = true;
    try {
      res = super.setFormData(rundata, context, msgList);
      if (res) {
        if (positions.getValue() == null || positions.getValue().equals("")) {// 初期
          SelectQuery<EipMPosition> query = Database.query(EipMPosition.class);
          query.orderAscending(EipMPosition.SORT_PROPERTY);
          accountPositionList =
            AccountUtils.getAccountPositionResultList(query.fetchList());
        } else {// データ送信時
          StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
          accountPositionIds = new String[st.countTokens()];
          int count = 0;
          while (st.hasMoreTokens()) {
            accountPositionIds[count] = st.nextToken();
            count++;
          }
          SelectQuery<EipMPosition> query = Database.query(EipMPosition.class);
          List<EipMPosition> list = query.fetchList();

          for (int i = 0; i < accountPositionIds.length; i++) {
            EipMPosition accountPosition =
              getEipMPositionFromAccountPositionId(list, accountPositionIds[i]);
            accountPositionList.add(AccountUtils
              .getAccountPositionResultData(accountPosition));
            rawAccountPositionList.add(accountPosition);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("accountPosition", ex);
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
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   *
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {
    return (msgList.size() == 0);
  }

  /**
   * 『役職』を読み込みます。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      return true;
    } catch (Exception e) {
      logger.error("accountPosition", e);
      return false;
    }
  }

  /**
   * 『役職』を追加します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 『役職』を更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = true;
    try {
      int newPosition = 1;
      for (EipMPosition accountPosition : rawAccountPositionList) {
        accountPosition.setSort(newPosition);
        newPosition++;
      }
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("accountPosition", e);
      res = false;
    }
    return res;
  }

  /**
   * 『役職』を削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
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
   * @return
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
   * 指定した役職IDのオブジェクトを取得する．
   *
   * @param userList
   * @param userName
   * @return
   */
  private EipMPosition getEipMPositionFromAccountPositionId(
      List<EipMPosition> AccountPositionList, String AccountPositionId) {
    for (int i = 0; i < AccountPositionList.size(); i++) {
      EipMPosition accountPosition = AccountPositionList.get(i);
      if (accountPosition.getPositionId().toString().equals(AccountPositionId)) {
        return accountPosition;
      }
    }
    return null;
  }

  /**
   * 役職情報のリストを取得する．
   *
   * @return
   */
  public List<AccountPositionResultData> getPositionList() {
    return accountPositionList;
  }

}