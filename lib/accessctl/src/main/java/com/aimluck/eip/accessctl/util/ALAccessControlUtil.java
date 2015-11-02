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
package com.aimluck.eip.accessctl.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.accessctl.bean.AccessControlAclBean;
import com.aimluck.eip.cayenne.om.account.EipTAclPortletFeature;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;

public class ALAccessControlUtil {

  /**
   * 
   * @param rundata
   * @return
   */
  public static List<AccessControlAclBean> getAclLiteBeansFromFeatureId(
      RunData rundata, String featureid) {
    List<AccessControlAclBean> list = new ArrayList<AccessControlAclBean>();

    EipTAclPortletFeature feature =
      Database.get(EipTAclPortletFeature.class, Integer.parseInt(featureid));
    if (feature == null) {
      return null;
    }

    int defineAclType = feature.getAclType().intValue();

    addAcls(
      list,
      defineAclType,
      ALAccessControlConstants.VALUE_ACL_LIST,
      "acllist",
      "一覧表示");
    addAcls(
      list,
      defineAclType,
      ALAccessControlConstants.VALUE_ACL_DETAIL,
      "acldetail",
      "詳細表示");
    addAcls(
      list,
      defineAclType,
      ALAccessControlConstants.VALUE_ACL_INSERT,
      "aclinsert",
      "追加");
    addAcls(
      list,
      defineAclType,
      ALAccessControlConstants.VALUE_ACL_UPDATE,
      "aclupdate",
      "編集");
    addAcls(
      list,
      defineAclType,
      ALAccessControlConstants.VALUE_ACL_DELETE,
      "acldelete",
      "削除");
    addAcls(
      list,
      defineAclType,
      ALAccessControlConstants.VALUE_ACL_EXPORT,
      "aclexport",
      "外部出力");

    return list;
  }

  private static void addAcls(List<AccessControlAclBean> acls,
      int defineAclType, int aclType, String aclTypeId, String aclTypeName) {
    boolean hasAcl = ((defineAclType & aclType) == aclType);

    if (!hasAcl) {
      return;
    }

    AccessControlAclBean acl = new AccessControlAclBean();
    acl.initField();
    acl.setAclId(aclTypeId);
    acl.setAclName(aclTypeName);
    acls.add(acl);
  }
}
