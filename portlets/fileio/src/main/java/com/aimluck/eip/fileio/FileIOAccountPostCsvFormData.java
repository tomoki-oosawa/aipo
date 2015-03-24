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
package com.aimluck.eip.fileio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
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
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 『部署』のフォームデータを管理するクラスです。 <BR>
 * 
 */
public class FileIOAccountPostCsvFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileIOAccountPostCsvFormData.class.getName());

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

  /** 郵便番号 */
  private ALStringField zipcode;

  /** 外線番号 */
  private ALStringField post_out_telephone;

  /** FAX番号 */
  private ALStringField fax_number;

  private boolean same_post;

  /**
   * 初期化します。
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
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
    post_name.setValue("");

    // 郵便番号
    zipcode1 = new ALStringField();
    zipcode1.setFieldName(ALLocalizationUtils.getl10n("ADDRESSBOOK_ZIPCODE"));
    zipcode1.setTrim(true);
    zipcode1.setValue("");
    zipcode2 = new ALStringField();
    zipcode2.setFieldName(ALLocalizationUtils.getl10n("ADDRESSBOOK_ZIPCODE"));
    zipcode2.setTrim(true);
    zipcode2.setValue("");

    // 住所
    address = new ALStringField();
    address.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_POST_ADDRESS"));
    address.setTrim(true);
    address.setValue("");

    // 電話番号（外線）
    post_out_telephone1 = new ALStringField();
    post_out_telephone1.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_OUT_TELEPHONE"));
    post_out_telephone1.setTrim(true);
    post_out_telephone1.setValue("");
    post_out_telephone2 = new ALStringField();
    post_out_telephone2.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_OUT_TELEPHONE"));
    post_out_telephone2.setTrim(true);
    post_out_telephone2.setValue("");
    post_out_telephone3 = new ALStringField();
    post_out_telephone3.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_OUT_TELEPHONE"));
    post_out_telephone3.setTrim(true);
    post_out_telephone3.setValue("");

    // 電話番号（内線）
    post_in_telephone = new ALStringField();
    post_in_telephone.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_IN_TELEPHONE"));
    post_in_telephone.setTrim(true);
    post_in_telephone.setValue("");

    // FAX番号
    fax_number1 = new ALStringField();
    fax_number1.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_FAX_NUMBER"));
    fax_number1.setTrim(true);
    fax_number1.setValue("");
    fax_number2 = new ALStringField();
    fax_number2.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_FAX_NUMBER"));
    fax_number2.setTrim(true);
    fax_number2.setValue("");
    fax_number3 = new ALStringField();
    fax_number3.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_FAX_NUMBER"));
    fax_number3.setTrim(true);
    fax_number3.setValue("");

    zipcode = new ALStringField();
    zipcode.setFieldName(ALLocalizationUtils.getl10n("ACCOUNT_ZIPCODE"));
    zipcode.setTrim(true);
    zipcode.setValue("");

    post_out_telephone = new ALStringField();
    post_out_telephone.setFieldName(ALLocalizationUtils
      .getl10n("ACCOUNT_POST_EXTERNAL_NOMBER"));
    post_out_telephone.setTrim(true);
    post_out_telephone.setValue("");

    fax_number = new ALStringField();
    fax_number.setFieldName(ALLocalizationUtils
      .getl10n("ADDRESSBOOK_FAX_NUMBER"));
    fax_number.setTrim(true);
    fax_number.setValue("");

    setSamePost(false);
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
          SelectQuery<TurbineUser> query =
            Database.query(TurbineUser.class, exp);
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
        logger.error("fileio", ex);
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

    zipcode.setCharacterType(ALStringField.TYPE_ASCII);
    zipcode.limitMaxLength(15);
    post_out_telephone.setCharacterType(ALStringField.TYPE_ASCII);
    post_out_telephone.limitMaxLength(15);
    fax_number.setCharacterType(ALStringField.TYPE_ASCII);
    fax_number.limitMaxLength(15);

    zipcode1.setNotNull(true);
    zipcode2.setNotNull(true);

    post_out_telephone1.setNotNull(true);
    post_out_telephone2.setNotNull(true);
    post_out_telephone3.setNotNull(true);

    fax_number1.setNotNull(true);
    fax_number2.setNotNull(true);
    fax_number3.setNotNull(true);

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
    if (!post_name.validate(msgList)) {
      post_name.setValue("");
    }
    if (!post_name.getValue().equals("")) {
      if (getEipMPost() != null) {
        setSamePost(true);
        msgList.add(ALLocalizationUtils.getl10n("ACCOUNT_POST_SAME_NAME"));

      }
    }

    if (!address.validate(msgList)) {
      address.setValue(null);
    }
    if (!post_out_telephone1.getValue().equals("")
      || !post_out_telephone2.getValue().equals("")
      || !post_out_telephone3.getValue().equals("")) {
      if (!post_out_telephone1.validate(dummy)
        || !post_out_telephone2.validate(dummy)
        || !post_out_telephone3.validate(dummy)) {
        msgList.add(ALLocalizationUtils
          .getl10n("ADDRESSBOOK_ALERT_SET_TELEPHONE"));
        post_out_telephone.setValue(null);
      } else {
        post_out_telephone.setValue(new StringBuffer().append(
          post_out_telephone1.getValue()).append("-").append(
          post_out_telephone2.getValue()).append("-").append(
          post_out_telephone3.getValue()).toString());
      }
    }
    if (!post_in_telephone.toString().equals("")) {
      if (!post_in_telephone.validate(msgList)) {
        post_in_telephone.setValue(null);
        msgList.add(ALLocalizationUtils
          .getl10n("ACCOUNT_POST_SET_TELEPHON_ANEXTENSION_NOMBER"));
      }
    }
    if (!fax_number1.getValue().equals("")
      || !fax_number2.getValue().equals("")
      || !fax_number3.getValue().equals("")) {
      if (!fax_number1.validate(dummy)
        || !fax_number2.validate(dummy)
        || !fax_number3.validate(dummy)) {
        msgList.add(ALLocalizationUtils.getl10n("ACCOUNT_POST_SET_FAX_NOMBER"));
        fax_number.setValue(null);
      } else {
        fax_number.setValue(new StringBuffer()
          .append(fax_number1.getValue())
          .append("-")
          .append(fax_number2.getValue())
          .append("-")
          .append(fax_number3.getValue())
          .toString());
      }
    }
    if (!zipcode1.getValue().equals("") || !zipcode2.getValue().equals("")) {
      if (!zipcode1.validate(dummy) || !zipcode2.validate(dummy)) {
        msgList.add(ALLocalizationUtils.getl10n("ACCOUNT_POST_SET_ZIP_CODE"));
        zipcode.setValue(null);
      } else {
        zipcode.setValue(new StringBuffer().append(zipcode1.getValue()).append(
          "-").append(zipcode2.getValue()).toString());
      }
    }
    return msgList.size() == 0;
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
    return false;
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
      if (getEipMPost() != null) {
        return false;
      }

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
      // 会社ID
      record.setCompanyId(Integer.valueOf(1));
      // 郵便番号
      record.setZipcode(zipcode.getValue());
      // 住所
      record.setAddress(address.getValue());
      // 電話番号（外線）
      record.setOutTelephone(post_out_telephone.getValue());

      // 電話番号（内線）
      record.setInTelephone(post_in_telephone.getValue());

      // FAX番号
      record.setFaxNumber(fax_number.getValue());

      // グループ名
      record.setGroupName(groupName);
      Date now = new Date();
      // 作成日
      record.setCreateDate(now);
      // 更新日
      record.setUpdateDate(now);
      // 部署を追加
      Database.commit();
      // singletonオブジェクトのリフレッシュ
      ALEipManager.getInstance().reloadPost();
      post_id = record.getPostId().intValue();
    } catch (Exception ex) {
      Database.rollback();
      logger.error("fileio", ex);
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
    return false;
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
    return false;
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

  /**
   * 
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers(String groupname) {
    try {
      return ALEipUtils.getUsers(groupname);
    } catch (Exception e) {
      logger.error("[ERROR}", e);
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
   * 部署IDを取得します <BR>
   * 
   * @return
   */
  public int getPostId() {
    return post_id;
  }

  /**
   * 郵便番号を取得します <BR>
   * 
   * @return
   */
  public ALStringField getZipcode() {
    return zipcode;
  }

  /**
   * 電話番号を取得します <BR>
   * 
   * @return
   */
  public ALStringField getOutTelephone() {
    return post_out_telephone;
  }

  /**
   * FAX番号を取得します <BR>
   * 
   * @return
   */
  public ALStringField getFaxNumber() {
    return fax_number;
  }

  /**
   * 同じ部署名がデータベースに存在するかどうかを示すフラグを取得します <BR>
   * 
   * @return
   */
  public boolean getSamePost() {
    return same_post;
  }

  /**
   * 同じ部署名がデータベースに存在するかどうかを示すフラグを入力します <BR>
   * 
   * @return
   */
  public void setSamePost(boolean flg) {
    same_post = flg;
  }

  /**
   * 部署名から部署IDを取得します <BR>
   * 
   * @return
   */
  private EipMPost getEipMPost() {

    SelectQuery<EipMPost> query = Database.query(EipMPost.class);
    Expression exp =
      ExpressionFactory.matchExp(EipMPost.POST_NAME_PROPERTY, post_name);

    query.setQualifier(exp);

    List<EipMPost> list = query.fetchList();

    if (list == null || list.size() == 0) {
      return null;
    }

    EipMPost post = list.get(0);
    return post;

  }

  /**
   * 読み取った単語を指定されたフィールドに格納します。 <BR>
   * 
   * @param token
   * @param i
   */
  public void addItemToken(String token, int i) {
    StringTokenizer st;
    switch (i) {
      case -1:
        break;
      case 0:
        this.post_name.setValue(token);
        break;
      case 1:
        st = new StringTokenizer(token, "-");
        if (st.hasMoreTokens()) {
          zipcode1.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          zipcode2.setValue(st.nextToken());
        }
        break;
      case 2:
        this.address.setValue(token);
        break;
      case 3:
        st = new StringTokenizer(token, "-");
        if (st.hasMoreTokens()) {
          post_out_telephone1.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          post_out_telephone2.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          post_out_telephone3.setValue(st.nextToken());
        }
        break;
      case 4:
        this.post_in_telephone.setValue(token);
        break;
      case 5:
        st = new StringTokenizer(token, "-");
        if (st.hasMoreTokens()) {
          fax_number1.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          fax_number2.setValue(st.nextToken());
        }
        if (st.hasMoreTokens()) {
          fax_number3.setValue(st.nextToken());
        }
        break;
      default:
        break;
    }
  }

}
