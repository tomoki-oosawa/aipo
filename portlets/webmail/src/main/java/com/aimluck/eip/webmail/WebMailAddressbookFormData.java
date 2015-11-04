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
package com.aimluck.eip.webmail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.addressbookuser.beans.AddressBookUserEmailLiteBean;
import com.aimluck.eip.addressbookuser.beans.AddressBookUserGroupLiteBean;
import com.aimluck.eip.addressbookuser.util.AddressBookUserUtils;
import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

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

  private List<ALEipGroup> internalGroupList = null;

  private List<AddressBookUserGroupLiteBean> externalGroupList = null;

  private ALStringField current_internal_group_name = null;

  private ALStringField current_external_group_name = null;

  private ArrayList<ALStringField> toRecipientList = null;

  private ArrayList<ALStringField> ccRecipientList = null;

  private ArrayList<ALStringField> bccRecipientList = null;

  /**  */
  private int userId = -1;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    userId = ALEipUtils.getUserId(rundata);

    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    internalGroupList = new ArrayList<ALEipGroup>();
    if (myGroups != null) {
      int length = myGroups.size();
      for (int i = 0; i < length; i++) {
        internalGroupList.add(myGroups.get(i));
      }
    }

    externalGroupList =
      AddressBookUserUtils.getAddressBookUserGroupLiteBeans(rundata);

    toRecipientList = new ArrayList<ALStringField>();
    ccRecipientList = new ArrayList<ALStringField>();
    bccRecipientList = new ArrayList<ALStringField>();

    try {
      String[] detail_to_recipients =
        rundata.getParameters().getStrings("detail_to_recipients");
      if (detail_to_recipients != null) {
        int length = detail_to_recipients.length;
        for (int i = 0; i < length; i++) {
          toRecipientList.add(new ALStringField(new String(
            detail_to_recipients[i].getBytes("8859_1"),
            "utf-8")));
        }
      }

      String[] detail_cc_recipients =
        rundata.getParameters().getStrings("detail_cc_recipients");
      if (detail_cc_recipients != null) {
        int length = detail_cc_recipients.length;
        for (int i = 0; i < length; i++) {
          ccRecipientList.add(new ALStringField(new String(
            detail_cc_recipients[i].getBytes("8859_1"),
            "utf-8")));
        }
      }

      String[] detail_bcc_recipients =
        rundata.getParameters().getStrings("detail_bcc_recipients");
      if (detail_bcc_recipients != null) {
        int length = detail_bcc_recipients.length;
        for (int i = 0; i < length; i++) {
          bccRecipientList.add(new ALStringField(new String(
            detail_bcc_recipients[i].getBytes("8859_1"),
            "utf-8")));
        }
      }

    } catch (Exception ex) {
      logger.error("webmail", ex);
    }
  }

  /**
   *
   */
  @Override
  protected void setValidator() {
    // グループ名
    current_internal_group_name.setNotNull(true);
    // グループ名
    current_external_group_name.setNotNull(true);
  }

  /**
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {
    return (msgList.size() == 0);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    boolean res = super.setFormData(rundata, context, msgList);
    if (res) {
      try {
        String str[] = rundata.getParameters().getStrings("detail_recipients");
        if (str == null) {
          return res;
        }

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
        logger.error("webmail", ex);
      }
    }
    return res;
  }

  /**
   * 返信と転送時にグローバル変数に値をセットする． 返信と転送時には，ENTITY_ID がセッションに既にセットされている状態になっている．
   * 
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = false;
    try {
      res = setFormData(rundata, context, msgList);
    } catch (Exception ex) {
      logger.error("webmail", ex);
      return false;
    }
    return res;
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
  @Override
  public void initField() {
    // 社内／社外
    type_company = new ALStringField();
    type_company.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_IN_OUT_OFFICE"));
    type_company.setValue(Integer.toString(TYPE_INTERNAL));

    // グループ名
    current_internal_group_name = new ALStringField();
    current_internal_group_name.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_GROUP_NAME"));
    current_internal_group_name.setValue("all");

    // グループ名
    current_external_group_name = new ALStringField();
    current_external_group_name.setFieldName(ALLocalizationUtils
      .getl10n("WEBMAIL_GROUP_NAME"));
    current_external_group_name.setValue("all");
  }

  /**
   * 
   * @param groupname
   * @return
   */
  public List<TurbineUser> getInternalUsers() {
    String groupName = getCurrentInternalGroupName().toString();
    if (groupName == null || groupName.equals("") || groupName.equals("all")) {
      groupName = "LoginUser";
    }
    return getPostMygroupUsers(groupName);
  }

  /**
   * 外部アドレス取得処理を開始します。
   * 
   * @return
   */
  public List<AddressBookUserEmailLiteBean> getExternalUsers() {
    String groupId = getCurrentExternalGroupName().toString();
    try {
      return AddressBookUserUtils.getAddressBookUserEmailLiteBeansFromGroup(
        groupId,
        Integer.valueOf(userId));
    } catch (Exception ex) {
      logger.error("webmail", ex);
      return null;
    }
  }

  /**
   * 指定した部署や My グループに属するユーザのリストを取得する．
   * 
   * @param groupName
   * @return
   */
  private List<TurbineUser> getPostMygroupUsers(String groupName) {
    try {
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);

      Expression exp1 =
        ExpressionFactory.matchExp(TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
          + "."
          + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
          + "."
          + TurbineGroup.GROUP_NAME_PROPERTY, groupName);
      Expression exp2 =
        ExpressionFactory.matchExp(TurbineUser.DISABLED_PROPERTY, "F");
      query.setQualifier(exp1.andExp(exp2));
      query.orderAscending(TurbineUser.EIP_MUSER_POSITION_PROPERTY
        + "."
        + EipMUserPosition.POSITION_PROPERTY);

      return query.fetchList();
    } catch (Exception ex) {
      logger.error("webmail", ex);
      return null;
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
   * 宛先のリストを取得する．
   * 
   * @return
   */
  public List<ALStringField> getToRecipientList() {
    return toRecipientList;
  }

  public List<ALStringField> getCcRecipientList() {
    return ccRecipientList;
  }

  public List<ALStringField> getBccRecipientList() {
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
  public List<ALEipGroup> getInternalGroupList() {
    return internalGroupList;
  }

  public List<AddressBookUserGroupLiteBean> getExternalGroupList() {
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
