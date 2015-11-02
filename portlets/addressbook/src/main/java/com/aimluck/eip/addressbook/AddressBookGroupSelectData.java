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
package com.aimluck.eip.addressbook;

import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.addressbook.util.AddressBookUtils;
import com.aimluck.eip.cayenne.om.portlet.EipMAddressGroup;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * アドレス帳グループの検索データクラスです。
 * 
 */
public class AddressBookGroupSelectData extends
    ALAbstractSelectData<EipMAddressGroup, EipMAddressGroup> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AddressBookGroupSelectData.class.getName());

  private List<AddressBookGroupResultData> groupList;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "group_name");
    }

    groupList = AddressBookUtils.getMyGroups(rundata);

    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipMAddressGroup> selectList(RunData rundata,
      Context context) {
    try {

      SelectQuery<EipMAddressGroup> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      return query.getResultList();
    } catch (Exception ex) {
      logger.error("AddressBookGroupSelectData.selectList", ex);
      return null;
    }
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected EipMAddressGroup selectDetail(RunData rundata, Context context) {
    return AddressBookUtils.getEipMAddressGroup(rundata, context);
  }

  /**
   * 
   * @param record
   * @return
   */
  @Override
  protected Object getResultData(EipMAddressGroup record) {
    try {
      AddressBookGroupResultData rd = new AddressBookGroupResultData();
      rd.initField();
      rd.setGroupId(record.getGroupId().longValue());
      rd.setGroupName(record.getGroupName());
      rd.setPublicFlag(record.getPublicFlag());
      return rd;
    } catch (Exception ex) {
      logger.error("AddressBookGroupSelectData.getResultData", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipMAddressGroup obj) {
    return getResultData(obj);
  }

  /**
   * 
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("group_name", EipMAddressGroup.GROUP_NAME_PROPERTY);
    return map;
  }

  /**
   * 検索条件を設定した SelectQuery を返します。
   * 
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipMAddressGroup> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipMAddressGroup> query =
      Database.query(EipMAddressGroup.class);

    Expression exp =
      ExpressionFactory.matchExp(EipMAddressGroup.OWNER_ID_PROPERTY, Integer
        .valueOf(ALEipUtils.getUserId(rundata)));
    query.setQualifier(exp);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  public List<ALStringField> getGroupMemberList(String gid) {
    return AddressBookUtils.getGroupMember(gid);
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

  public List<AddressBookGroupResultData> getGroupList() {
    return groupList;
  }
}
