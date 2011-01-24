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

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.account.EipMCompany;
import com.aimluck.eip.cayenne.om.account.EipMPost;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 　部署を表示するためのフォームデータです。
 * 
 */
public class AccountPersonPostFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccountPersonPostFormData.class.getName());

  /** 会社名 */
  private ALStringField company_name;

  /** 郵便番号 */
  private ALStringField company_zipcode;

  /** 住所 */
  private ALStringField company_address;

  /** 電話番号 */
  private ALStringField company_telephone;

  /** FAX番号 */
  private ALStringField company_fax_number;

  /** 部署名 */
  private ALStringField post_name;

  /** 郵便番号 */
  private ALStringField post_zipcode;

  /** 住所 */
  private ALStringField post_address;

  /** 電話番号(外線) */
  private ALStringField post_out_telephone;

  /** 電話番号(内線) */
  private ALStringField post_in_telephone;

  /** FAX番号 */
  private ALStringField post_fax_number;

  /** 所属メンバー */
  private List<ALEipUser> memberList;

  /** 所属する部署リスト */
  private List<String> post_name_list;

  /** 所属する部署IDリスト */
  private List<Integer> post_id_list;

  /**
   *
   */
  @Override
  protected void setValidator() {

  }

  /**
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {
    return false;
  }

  /**
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
      int uid = ALEipUtils.getUserId(rundata);

      // 会社のオブジェクトモデルを取得
      TurbineUser tuser = Database.get(TurbineUser.class, Integer.valueOf(uid));
      EipMCompany companyRecord =
        Database.get(EipMCompany.class, tuser.getCompanyId());

      if (companyRecord == null) {
        return false;
      }

      // 会社名
      company_name.setValue(companyRecord.getCompanyName());
      // 郵便番号
      company_zipcode.setValue(companyRecord.getZipcode());
      // 住所
      company_address.setValue(companyRecord.getAddress());
      // 電話番号
      company_telephone.setValue(companyRecord.getTelephone());
      // FAX番号
      company_fax_number.setValue(companyRecord.getFaxNumber());

      // ユーザーが所属する部署リスト

      int id = ALEipUtils.getUserId(rundata);
      SelectQuery<EipMPost> query = Database.query(EipMPost.class);
      List<EipMPost> list = query.fetchList();
      List<Integer> idlist = null;
      EipMPost mpost = null;
      for (int n = 0; n < list.size(); n++) {
        mpost = list.get(n);
        idlist = ALEipUtils.getUserIds(mpost.getGroupName());
        if (idlist.contains(id)) {
          post_name_list.add(mpost.getPostName());
          post_id_list.add(mpost.getPostId());
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;

  }

  /**
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
   *
   */
  public void initField() {
    // 会社名
    company_name = new ALStringField();
    company_name.setFieldName("会社名");
    company_name.setTrim(true);
    // 郵便番号
    company_zipcode = new ALStringField();
    company_zipcode.setFieldName("郵便番号");
    company_zipcode.setTrim(true);
    // 住所
    company_address = new ALStringField();
    company_address.setFieldName("住所");
    company_address.setTrim(true);
    // 電話番号
    company_telephone = new ALStringField();
    company_telephone.setFieldName("電話番号");
    company_telephone.setTrim(true);
    // FAX番号
    company_fax_number = new ALStringField();
    company_fax_number.setFieldName("FAX番号");
    company_fax_number.setTrim(true);

    // 部署名
    post_name = new ALStringField();
    post_name.setFieldName("部署名");
    post_name.setTrim(true);

    // 郵便番号
    post_zipcode = new ALStringField();
    post_zipcode.setFieldName("郵便番号");
    post_zipcode.setTrim(true);

    // 住所
    post_address = new ALStringField();
    post_address.setFieldName("住所");
    post_address.setTrim(true);

    // 電話番号(外線)
    post_out_telephone = new ALStringField();
    post_out_telephone.setFieldName("電話番号（外線）");
    post_out_telephone.setTrim(true);

    // 電話番号(内線)
    post_in_telephone = new ALStringField();
    post_in_telephone.setFieldName("電話番号（内線）");
    post_in_telephone.setTrim(true);

    // FAX番号
    post_fax_number = new ALStringField();
    post_fax_number.setFieldName("FAX番号");
    post_fax_number.setTrim(true);

    memberList = new ArrayList<ALEipUser>();
    post_name_list = new ArrayList<String>();
    post_id_list = new ArrayList<Integer>();
  }

  /**
   * 住所を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCompanyAddress() {
    return company_address;
  }

  /**
   * 会社名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCompanyName() {
    return company_name;
  }

  /**
   * FAX番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCompanyFaxNumber() {
    return company_fax_number;
  }

  /**
   * 電話番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCompanyTelephone() {
    return company_telephone;
  }

  /**
   * 郵便番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getCompanyZipcode() {
    return company_zipcode;
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
  public ALStringField getPostAddress() {
    return post_address;
  }

  /**
   * FAX番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getPostFaxNumber() {
    return post_fax_number;
  }

  /**
   * 電話番号（外線）を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getPostOutTelephone() {
    return post_out_telephone;
  }

  /**
   * 電話番号（内線）を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getPostInTelephone() {
    return post_in_telephone;
  }

  /**
   * 郵便番号を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getPostZipcode() {
    return post_zipcode;
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
   * 部署名リストを取得します。 <BR>
   * 
   * @return
   */
  public List<String> getPostNameList() {
    return post_name_list;
  }

  /**
   * 部署IDを取得します。 <BR>
   * 
   * @return
   */
  public Object getPostID(int i) {
    return post_id_list.get(i);
  }
}
