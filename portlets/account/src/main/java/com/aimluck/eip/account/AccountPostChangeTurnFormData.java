/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 部署の順番情報のフォームデータを管理するためのクラスです。 <br />
 */
public class AccountPostChangeTurnFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPostChangeTurnFormData.class.getName());

  // ユーザ名のリスト
  private ALStringField positions;

  private String[] postIds = null;

  /** 部署情報のリスト */
  private List<AccountPostResultData> postList = null;

  private List<EipMPost> rawpostList = null;

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

    postList = new ArrayList<AccountPostResultData>();
    rawpostList = new ArrayList<EipMPost>();
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
      .getl10nFormat("ACCOUNT_POSTNAME_LIST"));
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
          SelectQuery<EipMPost> query = Database.query(EipMPost.class);
          query.orderAscending(EipMPost.SORT_PROPERTY);
          postList = AccountUtils.getAccountPostResultList(query.fetchList());
        } else {
          // データ送信時
          StringTokenizer st = new StringTokenizer(positions.getValue(), ",");
          postIds = new String[st.countTokens()];
          int count = 0;
          while (st.hasMoreTokens()) {
            postIds[count] = st.nextToken();
            count++;
          }
          SelectQuery<EipMPost> query = Database.query(EipMPost.class);
          List<EipMPost> list = query.fetchList();

          for (int i = 0; i < postIds.length; i++) {
            EipMPost post = getEipMPostFromPostId(list, postIds[i]);
            postList.add(AccountUtils.getAccountPostResultData(post));
            rawpostList.add(post);
          }
        }
      }
    } catch (Exception ex) {
      logger.error("posts", ex);
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
   * 『部署』を読み込みます。 <BR>
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
      logger.error("posts", e);
      return false;
    }
  }

  /**
   * 『部署』を追加します。 <BR>
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
   * 『部署』を更新します。 <BR>
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
      for (EipMPost post : rawpostList) {
        post.setSort(newPosition);
        newPosition++;
      }
      Database.commit();
    } catch (Exception e) {
      Database.rollback();
      logger.error("AccountPostChangeTurnFormData.updateFormData", e);
      res = false;
    }
    return res;
  }

  /**
   * 『部署』を削除します。 <BR>
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
   * 指定した部署IDのオブジェクトを取得する．
   *
   * @param userList
   * @param userName
   * @return
   */
  private EipMPost getEipMPostFromPostId(List<EipMPost> postList, String postId) {
    for (int i = 0; i < postList.size(); i++) {
      EipMPost post = postList.get(i);
      if (post.getPostId().toString().equals(postId)) {
        return post;
      }
    }
    return null;
  }

  /**
   * 部署情報のリストを取得する．
   *
   * @return
   */
  public List<AccountPostResultData> getPostList() {
    return postList;
  }
}