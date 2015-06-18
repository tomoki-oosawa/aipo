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
package com.aimluck.eip.account;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * ユーザーアカウントの順番情報のフォームデータを管理するためのクラスです。 <br />
 */
public class AccountChangeTurnFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountChangeTurnFormData.class.getName());

  // ユーザ名のリスト
  private ALStringField positions;

  private String[] userNames = null;

  /** ユーザ情報のリスト */
  private List<ALEipUser> userList = null;

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

    userList = new ArrayList<ALEipUser>();
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // ユーザ名のリスト
    positions = new ALStringField();
    positions.setFieldName(ALLocalizationUtils
      .getl10nFormat("ACCOUNT_USERNAME_LIST"));
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
        if (positions.getValue() == null || positions.getValue().equals("")) {
          SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
          ObjectId oid =
            new ObjectId("TurbineUser", TurbineUser.USER_ID_PK_COLUMN, 3);
          Expression exp1 =
            ExpressionFactory.matchAllDbExp(
              oid.getIdSnapshot(),
              Expression.GREATER_THAN);
          Expression exp2 =
            ExpressionFactory.matchExp(TurbineUser.COMPANY_ID_PROPERTY, Integer
              .valueOf(1));
          Expression exp3 =
            ExpressionFactory.noMatchExp(TurbineUser.DISABLED_PROPERTY, "T");
          query.setQualifier(exp1);
          query.andQualifier(exp2);
          query.andQualifier(exp3);
          userList = ALEipUtils.getUsersFromSelectQuery(query);
        } else {
          StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
          userNames = new String[st.countTokens()];
          int count = 0;
          while (st.hasMoreTokens()) {
            userNames[count] = st.nextToken();
            count++;
          }
          SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
          Expression exp1 =
            ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, userNames);
          Expression exp2 =
            ExpressionFactory.noMatchExp(TurbineUser.DISABLED_PROPERTY, "T");
          query.setQualifier(exp1);
          query.andQualifier(exp2);

          List<TurbineUser> list = query.fetchList();

          TurbineUser turbineUser = null;
          int length = userNames.length;
          for (int i = 0; i < length; i++) {
            turbineUser = getEipUserRecord(list, userNames[i]);
            ALEipUser user = new ALEipUser();
            user.initField();
            user.setName(turbineUser.getLoginName());
            user.setAliasName(turbineUser.getFirstName(), turbineUser
              .getLastName());
            userList.add(user);
          }
        }
      }
    } catch (RuntimeException ex) {
      logger.error("AccountChangeTurnFormData.setFormData", ex);
      return false;
    } catch (Exception ex) {
      logger.error("AccountChangeTurnFormData.setFormData", ex);
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
    if (positions.getValue() != null && (!positions.getValue().equals(""))) {
      // 受信したユーザ ID の検証
      StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
      ALStringField field = null;
      while (st.hasMoreTokens()) {
        field = new ALStringField();
        field.setTrim(true);
        field.setValue(st.nextToken());
        field.limitMaxLength(30); // ユーザ名の最大文字数が 30 文字．クラス AccountFormData を参照．
        field.setCharacterType(ALStringField.TYPE_ASCII);
        field.validate(msgList);
        String unameValue = field.getValue();
        int length = unameValue.length();
        for (int i1 = 0; i1 < length; i1++) {
          if (isSymbol(unameValue.charAt(i1))) {
            // 使用されているのが妥当な記号であるかの確認
            if (!(unameValue.charAt(i1) == "_".charAt(0)
              || unameValue.charAt(i1) == "-".charAt(0) || unameValue
                .charAt(i1) == ".".charAt(0))) {
              msgList.add(ALLocalizationUtils
                .getl10nFormat("ACCOUNT_ALERT_LOGINNAME_CHAR0"));
              break;
            }
          }
        }
      }
    }
    return (msgList.size() == 0);
  }

  /**
   * 『ユーザー』を読み込みます。 <BR>
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
      logger.error("AccountChangeTurnFormData.loadFormData", e);
      return false;
    }
  }

  /**
   * 『ユーザー』を追加します。 <BR>
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
   * 『ユーザー』を更新します。 <BR>
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
      Expression exp1 =
        ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, userNames);
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class, exp1);
      query.orderAscending(TurbineUser.EIP_MUSER_POSITION_PROPERTY
        + "."
        + EipMUserPosition.POSITION_PROPERTY);
      List<TurbineUser> list = query.fetchList();

      LinkedHashMap<String, TurbineUser> loginnameUserMap =
        new LinkedHashMap<String, TurbineUser>();
      for (TurbineUser user : list) {
        loginnameUserMap.put(user.getLoginName(), user);
      }

      int newPosition = 1;
      for (String name : userNames) {
        TurbineUser user = loginnameUserMap.get(name);
        EipMUserPosition userPosition = user.getEipMUserPosition();
        userPosition.setPosition(newPosition);
        newPosition++;
      }
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("AccountChangeTurnFormData.updateFormData", e);
      res = false;
    }
    return res;
  }

  /**
   * 『ユーザー』を削除します。 <BR>
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
   * 指定したユーザ名のオブジェクトを取得する．
   * 
   * @param userList
   * @param userName
   * @return
   */
  private TurbineUser getEipUserRecord(List<TurbineUser> userList,
      String userName) {
    int size = userList.size();
    for (int i = 0; i < size; i++) {
      TurbineUser record = userList.get(i);
      if (record.getLoginName().equals(userName)) {
        return record;
      }
    }
    return null;
  }

  /**
   * ユーザ情報のリストを取得する．
   * 
   * @return
   */
  public List<ALEipUser> getUserList() {
    return userList;
  }

}
