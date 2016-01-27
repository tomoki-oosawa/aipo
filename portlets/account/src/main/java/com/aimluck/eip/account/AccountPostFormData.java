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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.account.util.AccountUtils;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 部署のフォームデータを管理するクラスです。 <BR>
 *
 */
public class AccountPostFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPostFormData.class.getName());

  /** 部署名 */
  private ALStringField post_name;

  /** 郵便番号 */
  private ALStringField zipcode1;

  /** 郵便番号 */
  private ALStringField zipcode2;

  /** 住所 */
  private ALStringField address;

  /** 電話番号(外線)1 */
  private ALStringField post_out_telephone1;

  /** 電話番号(外線)2 */
  private ALStringField post_out_telephone2;

  /** 電話番号(外線)3 */
  private ALStringField post_out_telephone3;

  /** 電話番号(内線) */
  private ALStringField post_in_telephone;

  /** FAX番号 */
  private ALStringField fax_number1;

  /** FAX番号 */
  private ALStringField fax_number2;

  /** FAX番号 */
  private ALStringField fax_number3;

  /** 所属メンバー */
  private List<ALEipUser> memberList;

  /** */
  private boolean is_join_member = true;

  /** 部署ID */
  private int post_id;

  private ALStringField group_name;

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
  }

  /**
   * 各フィールドを初期化します。 <BR>
   *
   *
   */
  @Override
  public void initField() {

    // 部署名
    post_name = new ALStringField();
    post_name.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_POST_NAME"));
    post_name.setTrim(true);

    // 郵便番号
    zipcode1 = new ALStringField();
    zipcode1.setFieldName(ALLocalizationUtils.getl10n("ADDRESSBOOK_ZIPCODE"));
    zipcode1.setTrim(true);
    zipcode2 = new ALStringField();
    zipcode2.setFieldName(ALLocalizationUtils.getl10n("ADDRESSBOOK_ZIPCODE"));
    zipcode2.setTrim(true);

    // 住所
    address = new ALStringField();
    address.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_POST_ADDRESS"));
    address.setTrim(true);

    // 電話番号（外線）
    post_out_telephone1 = new ALStringField();
    post_out_telephone1.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_OUT_TELEPHONE"));
    post_out_telephone1.setTrim(true);
    post_out_telephone2 = new ALStringField();
    post_out_telephone2.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_OUT_TELEPHONE"));
    post_out_telephone2.setTrim(true);
    post_out_telephone3 = new ALStringField();
    post_out_telephone3.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_OUT_TELEPHONE"));
    post_out_telephone3.setTrim(true);

    // 電話番号（内線）
    post_in_telephone = new ALStringField();
    post_in_telephone.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_IN_TELEPHONE"));
    post_in_telephone.setTrim(true);

    // FAX番号
    fax_number1 = new ALStringField();
    fax_number1.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_FAX_NUMBER"));
    fax_number1.setTrim(true);
    fax_number2 = new ALStringField();
    fax_number2.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_FAX_NUMBER"));
    fax_number2.setTrim(true);
    fax_number3 = new ALStringField();
    fax_number3.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_FAX_NUMBER"));
    fax_number3.setTrim(true);

    if (is_join_member) {
      memberList = new ArrayList<ALEipUser>();
    }

    group_name = new ALStringField();
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
    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      try {
        if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          post_id =
            Integer.parseInt(ALEipUtils.getTemp(
              rundata,
              context,
              ALEipConstants.ENTITY_ID));
        }

        if (is_join_member) {
          String str[] = rundata.getParameters().getStrings("member_to");
          if (str == null) {
            return res;
          }

          Expression exp =
            ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, str);
          SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
          query.setQualifier(exp);
          List<TurbineUser> list = query.fetchList();

          int size = list.size();
          for (int i = 0; i < size; i++) {
            TurbineUser record = list.get(i);
            ALEipUser user = new ALEipUser();
            user.initField();
            user.setName(record.getLoginName());
            user.setAliasName(record.getFirstName(), record.getLastName());
            memberList.add(user);
          }
        }
      } catch (Exception ex) {
        logger.error("AccountPostFormData.setFormData", ex);
      }
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
    post_name.setNotNull(true);
    post_name.limitMaxLength(50);
    zipcode1.setCharacterType(ALStringField.TYPE_NUMBER);
    zipcode1.limitLength(3, 3);
    zipcode2.setCharacterType(ALStringField.TYPE_NUMBER);
    zipcode2.limitLength(4, 4);
    address.limitMaxLength(64);
    post_out_telephone1.setCharacterType(ALStringField.TYPE_NUMBER);
    post_out_telephone1.limitMaxLength(5);
    post_out_telephone2.setCharacterType(ALStringField.TYPE_NUMBER);
    post_out_telephone2.limitMaxLength(4);
    post_out_telephone3.setCharacterType(ALStringField.TYPE_NUMBER);
    post_out_telephone3.limitMaxLength(4);
    post_in_telephone.setCharacterType(ALStringField.TYPE_ASCII);
    post_in_telephone.limitMaxLength(13);
    fax_number1.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number1.limitMaxLength(5);
    fax_number2.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number2.limitMaxLength(4);
    fax_number3.setCharacterType(ALStringField.TYPE_NUMBER);
    fax_number3.limitMaxLength(4);
  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   *
   * @param msgList
   * @return
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {
    List<String> dummy = new ArrayList<String>();
    post_name.validate(msgList);
    address.validate(msgList);

    if (post_name.toString().lastIndexOf("/") != -1) {
      msgList.add(ALLocalizationUtils.getl10n("ACCOUNT_POST_SET_OTHER_NAME"));
    }

    try {
      SelectQuery<EipMPost> query = Database.query(EipMPost.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp =
          ExpressionFactory.matchExp(EipMPost.POST_NAME_PROPERTY, post_name
            .getValue());
        query.setQualifier(exp);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(EipMPost.POST_NAME_PROPERTY, post_name
            .getValue());
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(EipMPost.POST_ID_PK_COLUMN, Integer
            .valueOf(post_id));
        query.andQualifier(exp2);
      }

      if (query.fetchList().size() != 0) {
        msgList.add(ALLocalizationUtils.getl10nFormat(
          "ACCOUNT_POST_EXIST",
          post_name.toString()));
      }
    } catch (Exception ex) {
      logger.error("AccountPostFormData.validate", ex);
      return false;
    }

    if (!zipcode1.getValue().equals("") || !zipcode2.getValue().equals("")) {
      if (!zipcode1.validate(dummy) || !zipcode2.validate(dummy)) {
        msgList.add(ALLocalizationUtils.getl10n("ACCOUNT_POST_WITHIN_SEVEN"));
      }
    }
    if (!post_out_telephone1.getValue().equals("")
      || !post_out_telephone2.getValue().equals("")
      || !post_out_telephone3.getValue().equals("")) {
      if (!post_out_telephone1.validate(dummy)
        || !post_out_telephone2.validate(dummy)
        || !post_out_telephone3.validate(dummy)) {
        msgList.add(ALLocalizationUtils
          .getl10n("ACCOUNT_POST_SET_TELEPHON_EXTERNAL_NOMBER"));
      }
    }

    post_in_telephone.validate(msgList);
    // ハイフン以外の記号とアルファベットの入力をはじきます
    Pattern pattern = Pattern.compile(".*[^-0-9]+.*");
    Matcher matcher = pattern.matcher(post_in_telephone.getValue());
    Boolean ext_validater = matcher.matches();
    if (ext_validater) {
      msgList.add(ALLocalizationUtils.getl10n("ACCOUNT_POST_WITHIN_SIXTEEN"));

    }
    // post_in_telephone.validate(msgList);

    if (!fax_number1.getValue().equals("")
      || !fax_number2.getValue().equals("")
      || !fax_number3.getValue().equals("")) {
      if (!fax_number1.validate(dummy)
        || !fax_number2.validate(dummy)
        || !fax_number3.validate(dummy)) {
        msgList.add(ALLocalizationUtils.getl10n("ACCOUNT_POST_SET_FAX_NOMBER"));
      }
    }
    return (msgList.size() == 0);
  }

  /**
   * 『部署』を読み込みます。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgListF
   * @return
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMPost record = AccountUtils.getEipMPost(rundata, context);
      if (record == null) {
        return false;
      }

      // 部署名
      post_name.setValue(record.getPostName());
      // 郵便番号
      StringTokenizer token;
      if (record.getZipcode() != null) {
        token = new StringTokenizer(record.getZipcode(), "-");
        if (token.countTokens() == 2) {
          zipcode1.setValue(token.nextToken());
          zipcode2.setValue(token.nextToken());
        }
      }
      // 住所
      address.setValue(record.getAddress());
      // 電話番号（外線）
      if (record.getOutTelephone() != null) {
        token = new StringTokenizer(record.getOutTelephone(), "-");
        if (token.countTokens() == 3) {
          post_out_telephone1.setValue(token.nextToken());
          post_out_telephone2.setValue(token.nextToken());
          post_out_telephone3.setValue(token.nextToken());
        }
      }

      post_in_telephone.setValue(record.getInTelephone());

      // FAX番号
      if (record.getFaxNumber() != null) {
        token = new StringTokenizer(record.getFaxNumber(), "-");
        if (token.countTokens() == 3) {
          fax_number1.setValue(token.nextToken());
          fax_number2.setValue(token.nextToken());
          fax_number3.setValue(token.nextToken());
        }
      }

      post_id = record.getPostId().intValue();

      if (is_join_member) {
        SelectQuery<TurbineGroup> query = Database.query(TurbineGroup.class);
        Expression exp =
          ExpressionFactory.matchExp(
            TurbineGroup.GROUP_ALIAS_NAME_PROPERTY,
            post_name);
        query.setQualifier(exp);
        List<TurbineGroup> list = query.fetchList();
        if (!list.isEmpty()) {
          TurbineGroup tg = list.get(0);
          memberList.addAll(ALEipUtils.getUsers(tg.getGroupName()));
        } else {
          post_name.setValue(null);
        }
      }

    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      logger.error("AccountPostFormData.loadFormData", ex);
      return false;
    }
    return true;
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
    try {

      // グループオブジェクトモデルを生成
      TurbineGroup group = Database.create(TurbineGroup.class);
      String name = post_name.getValue();
      // グループ名(時間+ユーザIDで一意となるグループ名を作成)
      String groupName =
        new StringBuffer().append(new Date().getTime()).append("_").append(
          ALEipUtils.getUserId(rundata)).toString();
      group.setGroupName(groupName);
      // オーナID（部署の場合、作成者に依らずuid=1）
      group.setOwnerId(Integer.valueOf(1));
      // グループ名(アプリケーションレベルで付ける名前)
      group.setGroupAliasName(name);
      // 公開フラグ
      group.setPublicFlag("1");
      // グループを追加
      JetspeedSecurity.addGroup(group);

      // 部署オブジェクトモデルを生成
      EipMPost record = Database.create(EipMPost.class);
      // 部署名
      record.setPostName(post_name.getValue());

      // 設備の順番を調整
      int lastnum = 0;
      StringBuffer statement = new StringBuffer();
      statement.append("SELECT MAX(sort) as max_sort FROM eip_m_post");
      String querydata = statement.toString();
      List<DataRow> maxnum =
        Database.sql(EipMPost.class, querydata).fetchListAsDataRow();
      if (maxnum != null && maxnum.size() > 0) {
        Integer maxnum2 = (Integer) maxnum.get(0).get("max_sort");
        if (maxnum2 != null) {
          lastnum = maxnum2;
        }
      }
      // 最大のソートナンバーの後ろに振られていないデータを追加
      Expression exp2 =
        ExpressionFactory.matchExp(EipMPost.SORT_PROPERTY, null);
      SelectQuery<EipMPost> querynotsort = Database.query(EipMPost.class);
      querynotsort.orderAscending(EipMPost.UPDATE_DATE_PROPERTY);
      querynotsort.setQualifier(exp2);
      List<EipMPost> post_notsort_list = querynotsort.fetchList();
      for (EipMPost postdata2 : post_notsort_list) {
        postdata2.setSort(++lastnum);
      }

      record.setSort(++lastnum);

      // 会社ID
      record.setCompanyId(Integer.valueOf(1));
      // 郵便番号
      if (!zipcode1.getValue().equals("") && !zipcode2.getValue().equals("")) {
        record.setZipcode(new StringBuffer()
          .append(zipcode1.getValue())
          .append("-")
          .append(zipcode2.getValue())
          .toString());
      } else {
        record.setZipcode("");
      }
      // 住所
      record.setAddress(address.getValue());
      // 電話番号（外線）
      if (!post_out_telephone1.getValue().equals("")
        && !post_out_telephone2.getValue().equals("")
        && !post_out_telephone3.getValue().equals("")) {
        record.setOutTelephone(new StringBuffer().append(
          post_out_telephone1.getValue()).append("-").append(
          post_out_telephone2.getValue()).append("-").append(
          post_out_telephone3.getValue()).toString());
      } else {
        record.setOutTelephone("");
      }

      // 電話番号（内線）
      record.setInTelephone(post_in_telephone.getValue());

      // FAX番号
      if (!fax_number1.getValue().equals("")
        && !fax_number2.getValue().equals("")
        && !fax_number3.getValue().equals("")) {
        record.setFaxNumber(new StringBuffer()
          .append(fax_number1.getValue())
          .append("-")
          .append(fax_number2.getValue())
          .append("-")
          .append(fax_number3.getValue())
          .toString());
      } else {
        record.setFaxNumber("");
      }
      // グループ名
      record.setGroupName(groupName);
      Date now = new Date();
      // 作成日
      record.setCreateDate(now);
      // 更新日
      record.setUpdateDate(now);
      // 部署を追加
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        record.getPostId(),
        ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
        "部署「" + record.getPostName() + "」を追加");

      // singletonオブジェクトのリフレッシュ
      ALEipManager.getInstance().reloadPost();
      if (is_join_member) {
        int size = memberList.size();
        for (int i = 0; i < size; i++) {
          ALEipUtils.changePost(rundata, (memberList.get(i))
            .getName()
            .getValue(), record.getPostId().intValue());
        }
      }
      post_id = record.getPostId().intValue();
    } catch (RuntimeException ex) {
      Database.rollback();
      logger.error("AccountPostFormData.insertFormData", ex);
      return false;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccountPostFormData.insertFormData", ex);
      return false;
    }
    return true;
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
    try {
      // オブジェクトモデルを取得
      EipMPost record = AccountUtils.getEipMPost(rundata, context);
      if (record == null) {
        return false;
      }
      // 部署名
      record.setPostName(post_name.getValue());
      // 郵便番号
      if (!zipcode1.getValue().equals("") && !zipcode2.getValue().equals("")) {
        record.setZipcode(new StringBuffer()
          .append(zipcode1.getValue())
          .append("-")
          .append(zipcode2.getValue())
          .toString());
      } else {
        record.setZipcode("");
      }
      // 住所
      record.setAddress(address.getValue());
      // 電話番号（外線）
      if (!post_out_telephone1.getValue().equals("")
        && !post_out_telephone2.getValue().equals("")
        && !post_out_telephone3.getValue().equals("")) {
        record.setOutTelephone(new StringBuffer().append(
          post_out_telephone1.getValue()).append("-").append(
          post_out_telephone2.getValue()).append("-").append(
          post_out_telephone3.getValue()).toString());
      } else {
        record.setOutTelephone("");
      }

      // 電話番号（内線）
      record.setInTelephone(post_in_telephone.getValue());

      // FAX番号
      if (!fax_number1.getValue().equals("")
        && !fax_number2.getValue().equals("")
        && !fax_number3.getValue().equals("")) {
        record.setFaxNumber(new StringBuffer()
          .append(fax_number1.getValue())
          .append("-")
          .append(fax_number2.getValue())
          .append("-")
          .append(fax_number3.getValue())
          .toString());
      } else {
        record.setFaxNumber("");
      }
      // 更新日
      record.setUpdateDate(new Date());

      Database.commit();
      post_id = record.getPostId().intValue();

      TurbineGroup group =
        (TurbineGroup) JetspeedSecurity.getGroup(record.getGroupName());
      if (group == null) {
        return false;
      }

      // グループ名
      group.setGroupAliasName(post_name.getValue());

      // グループを更新
      JetspeedSecurity.saveGroup(group);

      // singletonオブジェクトのリフレッシュ
      ALEipManager.getInstance().reloadPost();

      if (is_join_member) {
        // グループからユーザーを削除
        List<ALEipUser> users = ALEipUtils.getUsers(record.getGroupName());
        int size = users.size();
        for (int i = 0; i < size; i++) {
          JetspeedSecurity.unjoinGroup(
            users.get(i).getName().getValue(),
            record.getGroupName());
        }

        size = memberList.size();
        for (int i = 0; i < size; i++) {
          ALEipUtils.changePost(rundata, (memberList.get(i))
            .getName()
            .getValue(), record.getPostId().intValue());
        }
      }

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        record.getPostId(),
        ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
        "部署「" + record.getPostName() + "」を更新");

    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccountPostFormData.updateFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 『部署』を削除します。 <BR>
   * このとき部署に関連づけられているグループも削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipMPost record = AccountUtils.getEipMPost(rundata, context);
      if (record == null) {
        return false;
      }

      // グループからユーザーを削除
      List<ALEipUser> users =
        ALEipUtils.getUsersIncludingN(record.getGroupName());
      int size = users.size();
      for (int i = 0; i < size; i++) {
        JetspeedSecurity.unjoinGroup(users.get(i).getName().getValue(), record
          .getGroupName());
      }

      // グループを削除
      JetspeedSecurity.removeGroup(record.getGroupName());

      // 部署を削除
      Database.delete(record);

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        record.getPostId(),
        ALEventlogConstants.PORTLET_TYPE_ACCOUNT,
        "部署「" + record.getPostName() + "」を削除");

      // singletonオブジェクトのリフレッシュ
      ALEipManager.getInstance().reloadPost();

    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccountPostFormData.deleteFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * 部署名を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getPostName() {
    return post_name;
  }

  /**
   * 住所を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getAddress() {
    return address;
  }

  /**
   * FAX番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getFaxNumber1() {
    return fax_number1;
  }

  /**
   * FAX番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getFaxNumber2() {
    return fax_number2;
  }

  /**
   * FAX番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getFaxNumber3() {
    return fax_number3;
  }

  /**
   * 電話番号（外線）を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getOutTelephone1() {
    return post_out_telephone1;
  }

  /**
   * 電話番号（外線）を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getOutTelephone2() {
    return post_out_telephone2;
  }

  /**
   * 電話番号（外線）を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getOutTelephone3() {
    return post_out_telephone3;
  }

  /**
   * 電話番号（内線）を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getInTelephone() {
    return post_in_telephone;
  }

  /**
   * 郵便番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getZipcode1() {
    return zipcode1;
  }

  /**
   * 郵便番号を取得します。 <BR>
   *
   * @return
   */
  public ALStringField getZipcode2() {
    return zipcode2;
  }

  /**
   * 所属メンバーを取得します。 <BR>
   *
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  public ALStringField getGroupName() {
    return group_name;
  }

  /**
   *
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers(String groupname) {
    try {
      return ALEipUtils.getUsers(groupname);
      // return getUsers2(groupname);
    } catch (Exception e) {
      logger.error("AccountPostFormData.getUsers", e);
      return new ArrayList<ALEipUser>();
    }
  }

  /**
   *
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   *
   * @param bool
   */
  public void setJoinMember(boolean bool) {
    is_join_member = bool;
  }

  /**
   *
   * @return
   */
  public boolean isJoinMember() {
    return is_join_member;
  }

  /**
   *
   * @return
   */
  public int getPostId() {
    return post_id;
  }
}
