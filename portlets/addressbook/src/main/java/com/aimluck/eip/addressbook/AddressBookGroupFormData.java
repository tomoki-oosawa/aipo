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
package com.aimluck.eip.addressbook;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressbook;
import com.aimluck.eip.cayenne.om.portlet.EipTAddressbookGroupMap;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳グループの入力用フォームデータです。
 * 
 */
public class AddressBookGroupFormData extends ALAbstractFormData {
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookGroupFormData.class.getName());

  // グループオブジェクトのリスト
  private List<EipMAddressGroup> groupList;

  // アドレスオブジェクトのリスト(全体表示用)
  private List<AddressBookFilterData> allAddressList;

  // アドレスオブジェクトのリスト(グループ別表示用)
  private List<Object> addressList;

  // このグループへ登録されているアドレスのリスト
  private List<AddressBookResultData> addresses;

  private ALStringField group_name;

  private ALStringField public_flag;

  private ALDateField create_date;

  private ALDateField update_date;

  private DataContext dataContext;

  private Integer uid;

  private Integer gid;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    dataContext = DatabaseOrmService.getInstance().getDataContext();

    uid = Integer.valueOf(ALEipUtils.getUserId(rundata));
  }

  /**
   *
   */
  public void initField() {
    groupList = new ArrayList<EipMAddressGroup>();
    allAddressList = new ArrayList<AddressBookFilterData>();
    addressList = new ArrayList<Object>();
    addresses = new ArrayList<AddressBookResultData>();

    group_name = new ALStringField();
    group_name.setFieldName("グループ名");
    group_name.setTrim(true);

    public_flag = new ALStringField();
    public_flag.setFieldName("公開区分");

    create_date = new ALDateField();
    create_date.setFieldName("登録日");

    update_date = new ALDateField();
    update_date.setFieldName("最終更新日");

  }

  /**
   *
   */
  @Override
  protected void setValidator() {
    group_name.setNotNull(true);
    group_name.limitMaxLength(50);
    public_flag.setNotNull(true);
    public_flag.limitMaxLength(1);
  }

  /**
   * 
   * @param msgList
   * @return
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {
      SelectQuery query = new SelectQuery(EipMAddressGroup.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            EipMAddressGroup.GROUP_NAME_PROPERTY,
            group_name.getValue());
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, uid);
        query.andQualifier(exp2);
      } else {

        Expression exp1 =
          ExpressionFactory.matchExp(
            EipMAddressGroup.GROUP_NAME_PROPERTY,
            group_name.getValue());
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, uid);
        query.andQualifier(exp2);
        Expression exp3 =
          ExpressionFactory.noMatchDbExp(
            EipMAddressGroup.GROUP_ID_PK_COLUMN,
            gid);
        query.andQualifier(exp3);
      }
      if (dataContext.performQuery(query).size() != 0) {
        msgList.add("社外グループ名『 <span class='em'>"
          + group_name
          + "</span> 』は既に登録されています。");
      }

      group_name.validate(msgList);
      public_flag.validate(msgList);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return msgList.size() == 0;
  }

  /**
   * フォームへデータをセットします。
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
    // ユーザ登録グループの公開区分は常に非公開(F)とする
    setPublicFlag(new ALStringField("F"));
    boolean res = super.setFormData(rundata, context, msgList);
    addressList = new ArrayList<Object>();
    if (res) {
      try {
        if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          gid =
            Integer.valueOf(ALEipUtils.getTemp(
              rundata,
              context,
              ALEipConstants.ENTITY_ID));
        }

        String str[] = rundata.getParameters().getStrings("address_to");
        if (hasEmpty(str)) {
          return res;
        }

        SelectQuery query = new SelectQuery(EipMAddressbook.class);
        Expression exp =
          ExpressionFactory.inDbExp(EipMAddressbook.ADDRESS_ID_PK_COLUMN, str);
        query.setQualifier(exp);

        @SuppressWarnings("unchecked")
        List<EipMAddressbook> list = dataContext.performQuery(query);
        int size = list.size();
        for (int i = 0; i < size; i++) {
          EipMAddressbook address = list.get(i);
          AddressBookResultData rd = new AddressBookResultData();
          rd.initField();
          rd.setAddressId(address.getAddressId().intValue());
          rd.setName(address.getLastName() + " " + address.getFirstName());
          addresses.add(rd);
        }
      } catch (Exception ex) {
        logger.error("Exception", ex);
        throw new ALDBErrorException();
      }
    }
    return res;
  }

  private boolean hasEmpty(String[] list) {
    if (list == null || list.length == 0) {
      return true;
    }

    String str = null;
    int len = list.length;
    for (int i = 0; i < len; i++) {
      str = list[i];
      if (str == null || "".equals(str)) {
        return true;
      }
    }
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
      // オブジェクトモデルを取得
      EipMAddressGroup group =
        AddressBookUtils.getEipMAddressGroup(rundata, context);
      if (group == null) {
        return false;
      }
      // グループ名
      group_name.setValue(group.getGroupName());
      // 公開区分
      public_flag.setValue(group.getPublicFlag());
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 指定社外グループに属するアドレスを取得する。
   * 
   * @param rundata
   * @param context
   * @return
   */
  public boolean loadAddresses(RunData rundata, Context context) {
    try {
      String gid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      if (gid != null) {
        // この社外グループの所有者が現ユーザと異なる場合はエラー
        SelectQuery query = new SelectQuery(EipMAddressGroup.class);
        Expression exp1 =
          ExpressionFactory.matchDbExp(
            EipMAddressGroup.GROUP_ID_PK_COLUMN,
            Integer.valueOf(gid));
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipMAddressGroup.OWNER_ID_PROPERTY,
            Integer.valueOf(ALEipUtils.getUserId(rundata)));
        query.andQualifier(exp2);

        @SuppressWarnings("unchecked")
        List<EipMAddressGroup> list = dataContext.performQuery(query);
        if (list.size() == 0) {
          return false;
        }
      } else {
        return false;
      }

      // 指定社外グループに属するアドレスのリストを取得
      SelectQuery mapquery = new SelectQuery(EipTAddressbookGroupMap.class);
      Expression mapexp =
        ExpressionFactory.matchDbExp(
          EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY
            + "."
            + EipMAddressGroup.GROUP_ID_PK_COLUMN,
          Integer.valueOf(gid));
      mapquery.setQualifier(mapexp);

      @SuppressWarnings("unchecked")
      List<EipTAddressbookGroupMap> aList = dataContext.performQuery(mapquery);

      int size = aList.size();
      for (int i = 0; i < size; i++) {
        EipTAddressbookGroupMap record = aList.get(i);
        EipMAddressbook addressbook =
          (EipMAddressbook) DataObjectUtils.objectForPK(
            dataContext,
            EipMAddressbook.class,
            record.getAddressId());

        AddressBookResultData rd = new AddressBookResultData();
        rd.initField();
        rd.setAddressId(record.getAddressId().intValue());
        rd
          .setName(addressbook.getLastName() + " " + addressbook.getFirstName());
        addresses.add(rd);
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
    try {
      EipMAddressGroup address =
        (EipMAddressGroup) dataContext
          .createAndRegisterNewObject(EipMAddressGroup.class);
      rundata.getParameters().setProperties(address);
      address.setGroupName(group_name.getValue());
      address.setPublicFlag(public_flag.getValue());

      int uid = ALEipUtils.getUserId(rundata);
      address.setOwnerId(Integer.valueOf(uid));

      Date now = new Date();
      address.setCreateDate(now);
      address.setUpdateDate(now);

      // Address-Groupマッピングテーブルへのデータ追加
      for (int i = 0; i < addresses.size(); i++) {
        EipTAddressbookGroupMap map =
          (EipTAddressbookGroupMap) dataContext
            .createAndRegisterNewObject(EipTAddressbookGroupMap.class);

        int addressid =
          Integer.valueOf((int) (addresses.get(i).getAddressId().getValue()));
        map.setEipMAddressbook((EipMAddressbook) DataObjectUtils.objectForPK(
          dataContext,
          EipMAddressbook.class,
          Integer.valueOf(addressid)));
        map.setEipTAddressGroup(address);
      }

      dataContext.commitChanges();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        address.getGroupId(),
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_GROUP,
        group_name.getValue());

      return true;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
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
    try {
      // オブジェクトモデルを取得
      EipMAddressGroup group =
        AddressBookUtils.getEipMAddressGroup(rundata, context);
      if (group == null) {
        return false;
      }

      group.setGroupName(group_name.getValue());
      group.setPublicFlag(public_flag.getValue());
      group.setUpdateDate(new Date());

      // Address-Groupマッピングテーブルへのデータ追加

      String gid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

      // Address-Group Mapテーブル情報を一旦削除
      SelectQuery query1 = new SelectQuery(EipTAddressbookGroupMap.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY
            + "."
            + EipMAddressGroup.GROUP_ID_PK_COLUMN,
          gid);
      query1.setQualifier(exp1);

      @SuppressWarnings("unchecked")
      List<EipTAddressbookGroupMap> maps = dataContext.performQuery(query1);
      dataContext.deleteObjects(maps);

      // Address-Group Mapテーブルへ指定されたアドレスを追加
      for (int i = 0; i < addresses.size(); i++) {
        EipTAddressbookGroupMap map =
          (EipTAddressbookGroupMap) dataContext
            .createAndRegisterNewObject(EipTAddressbookGroupMap.class);
        int addressid =
          Integer.valueOf((int) (addresses.get(i).getAddressId().getValue()));
        map.setEipMAddressbook((EipMAddressbook) DataObjectUtils.objectForPK(
          dataContext,
          EipMAddressbook.class,
          Integer.valueOf(addressid)));
        map.setEipTAddressGroup(group);
      }

      dataContext.commitChanges();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        group.getGroupId(),
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_GROUP,
        group_name.getValue());

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 社外アドレスの削除。
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
      String groupid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      if (groupid == null || Integer.valueOf(groupid) == null) {
        logger.debug("[AddressBook] Cannot find Ext Group ID .");
        return false;
      }

      // Eip_t_addressbook_group_Mapテーブルから削除
      SelectQuery query1 = new SelectQuery(EipTAddressbookGroupMap.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTAddressbookGroupMap.EIP_TADDRESS_GROUP_PROPERTY
            + "."
            + EipMAddressGroup.GROUP_ID_PK_COLUMN,
          Integer.valueOf(groupid));
      query1.setQualifier(exp1);

      @SuppressWarnings("unchecked")
      List<EipTAddressbookGroupMap> maps = dataContext.performQuery(query1);
      dataContext.deleteObjects(maps);

      // Eip_m_address_groupテーブルから削除
      SelectQuery query2 = new SelectQuery(EipMAddressGroup.class);
      Expression exp2 =
        ExpressionFactory.matchDbExp(
          EipMAddressGroup.GROUP_ID_PK_COLUMN,
          Integer.valueOf(groupid));
      query2.setQualifier(exp2);

      @SuppressWarnings("unchecked")
      List<EipMAddressGroup> groups = dataContext.performQuery(query2);
      EipMAddressGroup delete_group = groups.get(0);
      int entityId = delete_group.getGroupId();
      String groupName = delete_group.getGroupName();
      dataContext.deleteObjects(groups);

      dataContext.commitChanges();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_ADDRESSBOOK_GROUP,
        groupName);

      // 検索画面用フィルタにて設定されているグループフィルタをセッションから削除する。
      String filtername =
        AddressBookFilterdSelectData.class.getName()
          + ALEipConstants.LIST_FILTER;
      ALEipUtils.removeTemp(rundata, context, filtername);
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  public ALStringField getGroupName() {
    return group_name;
  }

  public ALStringField getPublicFlag() {
    return public_flag;
  }

  public void setPublicFlag(ALStringField field) {
    public_flag = field;
  }

  /**
   * @return
   */
  public ALDateField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALDateField getUpdateDate() {
    return update_date;
  }

  /**
   * @param field
   */
  public void setCreateDate(ALDateField field) {
    create_date = field;
  }

  /**
   * @param field
   */
  public void setUpdateDate(ALDateField field) {
    update_date = field;
  }

  public void loadFilter(RunData rundata, Context context) {
    try {
      // 自分がオーナのグループを取得
      SelectQuery query1 = new SelectQuery(EipMAddressGroup.class);
      Expression exp1 =
        ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query1.setQualifier(exp1);

      @SuppressWarnings("unchecked")
      List<EipMAddressGroup> glist = dataContext.performQuery(query1);

      // Mapへ値をセット
      for (int i = 0; i < glist.size(); i++) {
        EipMAddressGroup group = glist.get(i);
        groupList.add(group);
      }

      // アドレス一覧を取得(全体用)
      SelectQuery query2 = new SelectQuery(EipMAddressbook.class);
      Expression exp21 =
        ExpressionFactory.matchExp(EipMAddressbook.PUBLIC_FLAG_PROPERTY, "T");
      Expression exp22 =
        ExpressionFactory.matchExp(EipMAddressbook.OWNER_ID_PROPERTY, Integer
          .valueOf(ALEipUtils.getUserId(rundata)));
      query2.setQualifier(exp21.orExp(exp22));

      @SuppressWarnings("unchecked")
      List<EipMAddressbook> list = dataContext.performQuery(query2);

      int addressNum = list.size();
      AddressBookFilterData address;
      for (int i = 0; i < addressNum; i++) {
        EipMAddressbook rec = list.get(i);
        address = new AddressBookFilterData();
        address.initField();
        address.setAddressId(rec.getAddressId().intValue());
        address.setFullName(rec.getLastName(), rec.getFirstName());
        allAddressList.add(address);
      }

      // アドレス一覧をグループID付で取得(特定グループ用)
      String sql =
        "SELECT EIP_T_ADDRESSBOOK_GROUP_MAP.GROUP_ID, EIP_M_ADDRESSBOOK.ADDRESS_ID, EIP_M_ADDRESSBOOK.LAST_NAME, EIP_M_ADDRESSBOOK.FIRST_NAME FROM EIP_M_ADDRESSBOOK LEFT JOIN EIP_T_ADDRESSBOOK_GROUP_MAP ON EIP_M_ADDRESSBOOK.address_id = EIP_T_ADDRESSBOOK_GROUP_MAP.address_id LEFT JOIN EIP_M_ADDRESS_GROUP ON EIP_T_ADDRESSBOOK_GROUP_MAP.group_id = EIP_M_ADDRESS_GROUP.group_id WHERE EIP_M_ADDRESSBOOK.PUBLIC_FLAG='T' "
          + " OR EIP_M_ADDRESSBOOK.OWNER_ID="
          + ALEipUtils.getUserId(rundata)
          + " ORDER BY EIP_T_ADDRESSBOOK_GROUP_MAP.GROUP_ID ASC";
      @SuppressWarnings("deprecation")
      SQLTemplate rawSelect =
        new SQLTemplate(EipTAddressbookGroupMap.class, sql, true);
      rawSelect.setFetchingDataRows(true);

      @SuppressWarnings("unchecked")
      List<DataRow> list2 = dataContext.performQuery(rawSelect);

      DataRow dataRow = null;
      addressNum = list2.size();
      for (int i = 0; i < addressNum; i++) {
        dataRow = list2.get(i);
        address = new AddressBookFilterData();
        address.initField();
        address.setAddressId(((Integer) ALEipUtils.getObjFromDataRow(
          dataRow,
          EipMAddressbook.ADDRESS_ID_PK_COLUMN)).intValue());
        address.setFullName((String) ALEipUtils.getObjFromDataRow(
          dataRow,
          EipMAddressbook.LAST_NAME_COLUMN), (String) ALEipUtils
          .getObjFromDataRow(dataRow, EipMAddressbook.FIRST_NAME_COLUMN));
        addressList.add(address);
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }
  }

  public List<EipMAddressGroup> getGroupList() {
    return groupList;
  }

  public List<Object> getAddressList() {
    return addressList;
  }

  public List<AddressBookFilterData> getAllAddressList() {
    return allAddressList;
  }

  public List<AddressBookFilterData> getGroupAddressList(int gid) {
    List<AddressBookFilterData> list = new ArrayList<AddressBookFilterData>();
    for (int i = 0; i < addressList.size(); i++) {
      AddressBookFilterData fData = (AddressBookFilterData) addressList.get(i);
      if (fData.getGroupId().getValue() == gid) {
        list.add(fData);
      }
    }
    return list;
  }

  /**
   * @return
   */
  public List<AddressBookResultData> getAddresses() {
    return addresses;
  }

  /**
   * アクセス権限チェック用メソッド。 アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_ADDRESSBOOK_COMPANY_GROUP;
  }
}
