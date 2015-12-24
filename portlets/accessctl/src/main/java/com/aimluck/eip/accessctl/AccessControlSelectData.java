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
package com.aimluck.eip.accessctl;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.accessctl.bean.AccessControlFeatureBean;
import com.aimluck.eip.accessctl.util.AccessControlUtils;
import com.aimluck.eip.cayenne.om.account.EipTAclPortletFeature;
import com.aimluck.eip.cayenne.om.account.EipTAclRole;
import com.aimluck.eip.cayenne.om.account.EipTAclUserRoleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class AccessControlSelectData extends
    ALAbstractSelectData<EipTAclRole, EipTAclRole> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccessControlSelectData.class.getName());

  /** アクセスロールの総数 */
  private int aclRoleSum;

  /** 機能一覧 */
  private List<AccessControlFeatureBean> portletFeatureList;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p2a-sort"));
    }

    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadPortletFeatureList(RunData rundata, Context context) {
    portletFeatureList = AccessControlUtils.getPortletFeatureList();
  }

  /**
   * 一覧データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public ResultList<EipTAclRole> selectList(RunData rundata, Context context) {
    try {

      SelectQuery<EipTAclRole> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTAclRole> list = query.getResultList();

      aclRoleSum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("AccessControlSelectData.selectList", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  protected SelectQuery<EipTAclRole> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTAclRole> query = Database.query(EipTAclRole.class);
    List<String> featureIdList = new ArrayList<String>();
    for (AccessControlFeatureBean bean : portletFeatureList) {
      featureIdList.add(String.valueOf(bean.getFeatureId()));
    }

    Expression ex =
      ExpressionFactory.inDbExp(EipTAclRole.EIP_TACL_PORTLET_FEATURE_PROPERTY
        + "."
        + EipTAclPortletFeature.FEATURE_ID_PK_COLUMN, featureIdList);

    query.andQualifier(ex);
    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTAclRole obj) {
    try {
      EipTAclRole record = obj;
      AccessControlResultData rd = new AccessControlResultData();
      rd.initField();
      rd.setAclRoleId(record.getRoleId().longValue());
      rd.setAclRoleName(record.getRoleName());
      rd
        .setFeatureName(record.getEipTAclPortletFeature().getFeatureAliasName());
      rd.setNote(record.getNote());

      // アクセス権限
      int tmpAclType = record.getAclType();
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_LIST, tmpAclType)) {
        rd.setAclList(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_DETAIL, tmpAclType)) {
        rd.setAclDetail(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_INSERT, tmpAclType)) {
        rd.setAclInsert(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_UPDATE, tmpAclType)) {
        rd.setAclUpdate(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_DELETE, tmpAclType)) {
        rd.setAclDelete(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_EXPORT, tmpAclType)) {
        rd.setAclExport(true);
      }

      rd.setCreateDate(ALDateUtil.format(
        record.getCreateDate(),
        ALLocalizationUtils.getl10n("ACCESSCTL_DATE_FORMAT")));
      rd.setUpdateDate(ALDateUtil.format(
        record.getUpdateDate(),
        ALLocalizationUtils.getl10n("ACCESSCTL_DATE_FORMAT")));
      return rd;
    } catch (Exception ex) {
      logger.error("AccessControlSelectData.getResultData", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public EipTAclRole selectDetail(RunData rundata, Context context) {
    return AccessControlUtils.getEipTAclRole(rundata, context);
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   * 
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTAclRole obj) {
    try {
      EipTAclRole record = obj;
      AccessControlResultData rd = new AccessControlResultData();
      rd.initField();
      rd.setAclRoleId(record.getRoleId().longValue());
      rd.setAclRoleName(record.getRoleName());
      rd
        .setFeatureName(record.getEipTAclPortletFeature().getFeatureAliasName());
      rd.setNote(record.getNote());

      // アクセス権限
      int tmpAclType = record.getAclType();
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_LIST, tmpAclType)) {
        rd.setAclList(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_DETAIL, tmpAclType)) {
        rd.setAclDetail(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_INSERT, tmpAclType)) {
        rd.setAclInsert(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_UPDATE, tmpAclType)) {
        rd.setAclUpdate(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_DELETE, tmpAclType)) {
        rd.setAclDelete(true);
      }
      if (hasAcl(ALAccessControlConstants.VALUE_ACL_EXPORT, tmpAclType)) {
        rd.setAclExport(true);
      }

      List<String> unamelist = new ArrayList<String>();
      EipTAclUserRoleMap map = null;
      TurbineUser tuser = null;

      List<EipTAclUserRoleMap> maps =
        AccessControlUtils
          .getEipTAclUserRoleMaps(record.getRoleId().intValue());
      if (maps != null && maps.size() > 0) {
        int size = maps.size();
        for (int i = 0; i < size; i++) {
          map = maps.get(i);
          tuser = map.getTurbineUser();
          if (!JetspeedResources.CONFIRM_VALUE_PENDING.equals(tuser
            .getConfirmValue())) {
            unamelist.add(new StringBuffer()
              .append(tuser.getLastName())
              .append(" ")
              .append(tuser.getFirstName())
              .toString());
          }
        }
        rd.addUserNameList(unamelist);
      }

      rd.setCreateDate(ALDateUtil.format(
        record.getCreateDate(),
        ALLocalizationUtils.getl10n("ACCESSCTL_DATE_FORMAT")));
      rd.setUpdateDate(ALDateUtil.format(
        record.getUpdateDate(),
        ALLocalizationUtils.getl10n("ACCESSCTL_DATE_FORMAT")));
      return rd;
    } catch (Exception ex) {
      logger.error("AccessControlSelectData.getResultDataDetail", ex);
      return null;
    }
  }

  private boolean hasAcl(int defineAclType, int aclType) {
    return ((aclType & defineAclType) == defineAclType);
  }

  /**
   * 
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("acl_role_name", EipTAclRole.ROLE_NAME_PROPERTY);
    map.putValue(
      "feature_alias_name",
      EipTAclRole.EIP_TACL_PORTLET_FEATURE_PROPERTY
        + "."
        + EipTAclPortletFeature.FEATURE_ALIAS_NAME_PROPERTY);
    map.putValue("feature", EipTAclRole.EIP_TACL_PORTLET_FEATURE_PROPERTY
      + "."
      + EipTAclPortletFeature.FEATURE_ID_PK_COLUMN);

    return map;
  }

  /**
   * 
   * @return
   */
  public List<AccessControlFeatureBean> getPortletFeatureList() {
    return portletFeatureList;
  }

  /**
   * ロールの総数を返す． <BR>
   * 
   * @return
   */
  public int getAclRoleSum() {
    return aclRoleSum;
  }

}
