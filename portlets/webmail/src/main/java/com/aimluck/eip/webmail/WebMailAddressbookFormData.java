/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.webmail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.addressbookuser.util.AddressBookUserUtils;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * メール送信時に使用するアドレス帳のフォームデータを管理するためのクラスです。 <br />
 */
public class WebMailAddressbookFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(WebMailAddressbookFormData.class.getName());

  /** 社内 */
  public static final int TYPE_EXTERNAL = 0;

  /** 社外 */
  public static final int TYPE_INTERNAL = 1;

  /** 社内／社外 */
  private ALStringField type_company = null;

  private List internalGroupList = null;

  private List externalGroupList = null;

  private List internalUserList = null;

  private ALStringField current_internal_group_name = null;

  private ALStringField current_external_group_name = null;

  private ArrayList toRecipientList = null;

  private ArrayList ccRecipientList = null;

  private ArrayList bccRecipientList = null;

  /**  */
  private int userId = -1;

  private DataContext dataContext;

  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    dataContext = DatabaseOrmService.getInstance().getDataContext();

    userId = ALEipUtils.getUserId(rundata);

    List myGroups = ALEipUtils.getMyGroups(rundata);
    internalGroupList = new ArrayList();
    if (myGroups != null) {
      int length = myGroups.size();
      for (int i = 0; i < length; i++) {
        internalGroupList.add(myGroups.get(i));
      }
    }

    externalGroupList = AddressBookUserUtils
        .getAddressBookUserGroupLiteBeans(rundata);

    internalUserList = new ArrayList();

    toRecipientList = new ArrayList();
    ccRecipientList = new ArrayList();
    bccRecipientList = new ArrayList();

    try {
      String[] detail_to_recipients = rundata.getParameters().getStrings(
          "detail_to_recipients");
      if (detail_to_recipients != null) {
        int length = detail_to_recipients.length;
        for (int i = 0; i < length; i++) {
          toRecipientList.add(new ALStringField(new String(
              detail_to_recipients[i].getBytes("8859_1"), "utf-8")));
        }
      }

      String[] detail_cc_recipients = rundata.getParameters().getStrings(
          "detail_cc_recipients");
      if (detail_cc_recipients != null) {
        int length = detail_cc_recipients.length;
        for (int i = 0; i < length; i++) {
          ccRecipientList.add(new ALStringField(new String(
              detail_cc_recipients[i].getBytes("8859_1"), "utf-8")));
        }
      }

      String[] detail_bcc_recipients = rundata.getParameters().getStrings(
          "detail_bcc_recipients");
      if (detail_bcc_recipients != null) {
        int length = detail_bcc_recipients.length;
        for (int i = 0; i < length; i++) {
          bccRecipientList.add(new ALStringField(new String(
              detail_bcc_recipients[i].getBytes("8859_1"), "utf-8")));
        }
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractFormData#setValidator()
   */
  protected void setValidator() {
    // グループ名
    current_internal_group_name.setNotNull(true);
    // グループ名
    current_external_group_name.setNotNull(true);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractFormData#validate(java.util.ArrayList)
   */
  protected boolean validate(List<String> msgList) {
    return (msgList.size() == 0);
  }

  /**
   * 
   * @see com.aimluck.eip.common.ALAbstractFormData#setFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      try {
        String str[] = rundata.getParameters().getStrings("detail_recipients");
        if (str == null)
          return res;

        ALStringField field = null;
        int size = str.length;
        for (int i = 0; i < size; i++) {
          field = new ALStringField();
          field.setValue(ALStringUtil.unsanitizing(new String(str[i]
              .getBytes("8859_1"), "UTF-8")));
          toRecipientList.add(field);
          ccRecipientList.add(field);
          bccRecipientList.add(field);
        }

      } catch (Exception ex) {
        logger.error("Exception", ex);
      }
    }
    return res;
  }

  /**
   * 返信と転送時にグローバル変数に値をセットする． 返信と転送時には，ENTITY_ID がセッションに既にセットされている状態になっている．
   * 
   * @see com.aimluck.eip.common.ALAbstractFormData#loadFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = false;
    try {
      res = setFormData(rundata, context, msgList);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return res;
  }

  /**
   * 未使用
   * 
   * @see com.aimluck.eip.common.ALAbstractFormData#insertFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 未使用
   * 
   * @see com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * 未使用
   * 
   * @see com.aimluck.eip.common.ALAbstractFormData#deleteFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    return false;
  }

  /**
   * @see com.aimluck.eip.common.ALData#initField()
   */
  public void initField() {
    // 社内／社外
    type_company = new ALStringField();
    type_company.setFieldName("社内／社外");
    type_company.setValue(Integer.toString(TYPE_INTERNAL));

    // グループ名
    current_internal_group_name = new ALStringField();
    current_internal_group_name.setFieldName("グループ名");
    current_internal_group_name.setValue("all");

    // グループ名
    current_external_group_name = new ALStringField();
    current_external_group_name.setFieldName("グループ名");
    current_external_group_name.setValue("all");
  }

  /**
   * 
   * @param groupname
   * @return
   */
  public List getInternalUsers() {
    List list = null;
    String groupName = getCurrentInternalGroupName().toString();
    if (groupName == null || groupName.equals("") || groupName.equals("all")) {
      groupName = "LoginUser";
    }
    list = getPostMygroupUsers(groupName);
    return list;
  }

  /**
   * 外部アドレス取得処理を開始します。
   * 
   * @return
   */
  public List getExternalUsers() {
    List list = null;
    String groupId = getCurrentExternalGroupName().toString();
    try {

      list = AddressBookUserUtils.getAddressBookUserEmailLiteBeansFromGroup(
          groupId, Integer.valueOf(userId));

      // if (groupName == null || groupName.equals("") ||
      // groupName.equals("all")) {
      // // 全体を指定された場合
      // list = getAddressgroupAllUsers();
      //
      // // 返却するオブジェクトの作成
      // ArrayList objList = new ArrayList();
      // if (list != null) {
      // int size = list.size();
      // for (int i = 0; i < size; i++) {
      // objList.add(getResultData(list.get(i), groupName));
      // }
      // }
      // return objList;
      // } else {
      // // 特定グループを指定された時
      // SelectQuery mapquery = new SelectQuery(EipMAddressGroup.class);
      // Expression exp01 = ExpressionFactory.matchExp(
      // EipMAddressGroup.OWNER_ID_PROPERTY, Integer.valueOf(userId));
      // mapquery.setQualifier(exp01);
      // Expression exp02 = ExpressionFactory.matchDbExp(
      // EipMAddressGroup.GROUP_ID_PK_COLUMN, Integer.valueOf(groupName));
      // mapquery.andQualifier(exp02);
      // List maplist = dataContext.performQuery(mapquery);
      // List idlist = new ArrayList();
      // if (idlist != null) {
      // int size = maplist.size();
      // for (int i = 0; i < size; i++) {
      // EipMAddressGroup group = (EipMAddressGroup) maplist.get(i);
      // idlist.add(group.getGroupId());
      // }
      // }
      //
      // SelectQuery query = new SelectQuery(EipMAddressbookGroupView.class);
      // Expression exp = ExpressionFactory.inExp(
      // EipMAddressbookGroupView.GROUP_ID_PROPERTY, idlist);
      // query.andQualifier(exp);
      // query.addOrdering(EipMAddressbookGroupView.NAME_KANA_PROPERTY, true);
      // list = dataContext.performQuery(query);
      //
      // // 返却するオブジェクトの作成
      // ArrayList objList = new ArrayList();
      // if (list != null) {
      // int size = list.size();
      // for (int i = 0; i < size; i++) {
      // objList.add(getResultData(list.get(i), groupName));
      // }
      // }
      // return objList;
      // }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
    return list;
  }

  /**
   * 指定した部署や My グループに属するユーザのリストを取得する．
   * 
   * @param groupName
   * @return
   */
  private List getPostMygroupUsers(String groupName) {
    List list = null;

    try {
      SelectQuery query = new SelectQuery(TurbineUser.class);
      Expression exp1 = ExpressionFactory.matchExp(
          TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY + "."
              + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY + "."
              + TurbineGroup.GROUP_NAME_PROPERTY, groupName);
      query.setQualifier(exp1);
      Expression exp2 = ExpressionFactory.matchExp(
          TurbineUser.DISABLED_PROPERTY, "F");
      query.andQualifier(exp2);
      query.addOrdering(TurbineUser.EIP_MUSER_POSITION_PROPERTY + "."
          + EipMUserPosition.POSITION_PROPERTY, true);
      list = dataContext.performQuery(query);
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
    return list;
  }

  // /**
  // * 外部アドレス全体を取得する。
  // *
  // * @return
  // */
  // private List getAddressgroupAllUsers() {
  // List list = null;
  // try {
  // SelectQuery query = new SelectQuery(EipMAddressbookView.class);
  // // 公開アドレス
  // Expression exp1 = ExpressionFactory.matchExp(
  // EipMAddressbookView.PUBLIC_FLAG_PROPERTY, "T");
  // // アドレス所有者
  // Expression exp2 = ExpressionFactory.matchExp(
  // EipMAddressbookView.OWNER_ID_PROPERTY, Integer.valueOf(userId));
  // query.setQualifier(exp1.orExp(exp2));
  // // ソートする（名前の昇順）
  // query.addOrdering(EipMAddressbookView.LAST_NAME_KANA_PROPERTY, true);
  // list = dataContext.performQuery(query);
  // } catch (Exception ex) {
  // logger.error("Exception", ex);
  // }
  // return list;
  // }

  // /**
  // * アドレス情報を返す際の返却オブジェクトを作成する。
  // *
  // * @see
  // com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
  // */
  // private Object getResultData(Object obj, String type) {
  // try {
  // AddressBookResultData rd = new AddressBookResultData();
  // if (type == null || type.trim().length() == 0 || "all".equals(type)) {
  // // 全体時
  // EipMAddressbookView record = (EipMAddressbookView) obj;
  // rd.initField();
  // rd.setAddressId(record.getAddressId().intValue());
  // rd.setName(new StringBuffer().append(record.getLastName()).append(" ")
  // .append(record.getFirstName()).toString());
  // rd.setEmail(record.getEmail());
  // } else {
  // // 特定グループ指定時
  // EipMAddressbookGroupView record = (EipMAddressbookGroupView) obj;
  // rd.initField();
  // rd.setAddressId(record.getAddressId().intValue());
  // rd.setName(new StringBuffer().append(record.getLastName()).append(" ")
  // .append(record.getFirstName()).toString());
  // rd.setEmail(record.getEmail());
  // }
  // return rd;
  // } catch (Exception ex) {
  // logger.error("Exception", ex);
  // return null;
  // }
  // }

  /**
   * 
   * @return
   */
  public Map getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * 宛先のリストを取得する．
   * 
   * @return
   */
  public List getToRecipientList() {
    return toRecipientList;
  }

  public List getCcRecipientList() {
    return ccRecipientList;
  }

  public List getBccRecipientList() {
    return bccRecipientList;
  }

  public ALStringField getTypeCompany() {
    return type_company;
  }

  public void setTypeCompany(String string) {
    type_company.setValue(string);
  }

  /**
   * 
   * @return
   */
  public List getInternalGroupList() {
    return internalGroupList;
  }

  public List getExternalGroupList() {
    return externalGroupList;
  }

  public ALStringField getCurrentInternalGroupName() {
    return current_internal_group_name;
  }

  public void setCurrentInternalGroupName(String string) {
    current_internal_group_name.setValue(string);
  }

  public ALStringField getCurrentExternalGroupName() {
    return current_external_group_name;
  }

  public void setCurrentExternalGroupName(String string) {
    current_external_group_name.setValue(string);
  }
}
