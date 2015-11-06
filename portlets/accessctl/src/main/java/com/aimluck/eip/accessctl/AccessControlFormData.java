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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.accessctl.bean.AccessControlFeatureBean;
import com.aimluck.eip.accessctl.util.AccessControlUtils;
import com.aimluck.eip.cayenne.om.account.EipTAclPortletFeature;
import com.aimluck.eip.cayenne.om.account.EipTAclRole;
import com.aimluck.eip.cayenne.om.account.EipTAclUserRoleMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * アクセスコントロールのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class AccessControlFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(AccessControlFormData.class.getName());

  /** ロールID */
  private String acl_role_id;

  /** ロール名 */
  private ALStringField acl_role_name;

  /** 機能 */
  private ALNumberField feature_id;

  /** メモ */
  private ALStringField note;

  private ALNumberField acllist;

  private ALNumberField acldetail;

  private ALNumberField aclinsert;

  private ALNumberField aclupdate;

  private ALNumberField acldelete;

  private ALNumberField aclexport;

  /** <code>memberList</code> メンバーリスト */
  private List<ALEipUser> memberList;

  private int defineAclType;

  /** 機能一覧 */
  private List<AccessControlFeatureBean> portletFeatureList;

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadPortletFeatureList(RunData rundata, Context context) {
    portletFeatureList = AccessControlUtils.getPortletFeatureList();
  }

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
    super.init(action, rundata, context);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // ロール名
    acl_role_name = new ALStringField();
    acl_role_name.setFieldName(ALLocalizationUtils
      .getl10n("ACCESSCTL_ROLE_NAME"));
    acl_role_name.setTrim(true);

    // 機能
    feature_id = new ALNumberField();
    feature_id.setFieldName(ALLocalizationUtils.getl10n("ACCESSCTL_ROLE_NAME"));

    // メモ
    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils.getl10n("ACCESSCTL_MEMO"));
    note.setTrim(false);

    acllist = new ALNumberField();
    acldetail = new ALNumberField();
    aclinsert = new ALNumberField();
    aclupdate = new ALNumberField();
    acldelete = new ALNumberField();
    aclexport = new ALNumberField();

    // メンバーリスト
    memberList = new ArrayList<ALEipUser>();
  }

  /**
   * ロールの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // ロール名必須項目
    acl_role_name.setNotNull(true);
    // ロール名の文字数制限
    acl_role_name.limitMaxLength(50);

    // 機能
    feature_id.setNotNull(true);

    // メモの文字数制限
    note.limitMaxLength(1000);

    acllist.limitValue(0, 1);
    acldetail.limitValue(0, 1);
    aclinsert.limitValue(0, 1);
    aclupdate.limitValue(0, 1);
    acldelete.limitValue(0, 1);
    aclexport.limitValue(0, 1);
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
    try {
      if (res) {
        if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          acl_role_id =
            ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
        }

        String[] str = rundata.getParameters().getStrings("member_to");
        if (str != null && str.length > 0) {
          SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
          Expression exp =
            ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, str);
          query.setQualifier(exp);
          memberList.addAll(ALEipUtils.getUsersFromSelectQuery(query));
        }
      }
    } catch (Exception ex) {
      logger.error("AccessControlFormData.setFormData", ex);
      res = false;
    }

    return res;
  }

  /**
   * フォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    String tmp_acl_role_name = acl_role_name.getValue();
    if (tmp_acl_role_name != null && !"".equals(tmp_acl_role_name)) {
      // ロール名の重複をチェックする
      try {
        SelectQuery<EipTAclRole> query = Database.query(EipTAclRole.class);
        if (ALEipConstants.MODE_INSERT.equals(getMode())) {
          Expression exp =
            ExpressionFactory.matchExp(
              EipTAclRole.ROLE_NAME_PROPERTY,
              tmp_acl_role_name);
          query.setQualifier(exp);
        } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          Expression exp1 =
            ExpressionFactory.matchExp(
              EipTAclRole.ROLE_NAME_PROPERTY,
              tmp_acl_role_name);
          query.setQualifier(exp1);
          Expression exp2 =
            ExpressionFactory.noMatchDbExp(
              EipTAclRole.ROLE_ID_PK_COLUMN,
              Integer.valueOf(acl_role_id));
          query.andQualifier(exp2);
        }

        if (query.fetchList().size() != 0) {
          msgList.add(ALLocalizationUtils.getl10nFormat(
            "ACCESSCTL_ALERT_ALREADY_CREATED",
            acl_role_name.toString()));
        }
      } catch (Exception ex) {
        logger.error("AccessControlFormData.validate", ex);
        return false;
      }
    }

    // ロール名
    acl_role_name.validate(msgList);
    // メモ
    note.validate(msgList);

    acllist.validate(msgList);
    acldetail.validate(msgList);
    aclinsert.validate(msgList);
    aclupdate.validate(msgList);
    acldelete.validate(msgList);
    aclexport.validate(msgList);

    // アクセス権限
    if (acllist.getValue() == 0
      && acldetail.getValue() == 0
      && aclinsert.getValue() == 0
      && aclupdate.getValue() == 0
      && acldelete.getValue() == 0
      && aclexport.getValue() == 0) {
      msgList.add(ALLocalizationUtils
        .getl10n("ACCESSCTL_ALERT_NO_FEATURE_SELECTED"));
    }

    // 所属メンバー
    if (memberList.size() == 0) {
      msgList.add(ALLocalizationUtils
        .getl10n("ACCESSCTL_ALERT_NO_MEMBER_SELECTED"));
    } else {
      try {
        // 同一機能の他ロールには所属できないようにする

        List<Integer> uids = new ArrayList<Integer>();
        int msize = memberList.size();
        for (int i = 0; i < msize; i++) {
          ALEipUser user = memberList.get(i);
          uids.add(Integer.valueOf((int) user.getUserId().getValue()));
        }

        SelectQuery<EipTAclRole> rolequery = Database.query(EipTAclRole.class);
        Expression exp11 =
          ExpressionFactory.matchDbExp(
            EipTAclRole.EIP_TACL_PORTLET_FEATURE_PROPERTY
              + "."
              + EipTAclPortletFeature.FEATURE_ID_PK_COLUMN,
            Integer.valueOf((int) feature_id.getValue()));
        Expression exp12 =
          ExpressionFactory.inDbExp(
            EipTAclRole.EIP_TACL_USER_ROLE_MAPS_PROPERTY
              + "."
              + EipTAclUserRoleMap.TURBINE_USER_PROPERTY
              + "."
              + TurbineUser.USER_ID_PK_COLUMN,
            uids);
        rolequery.setQualifier(exp11.andExp(exp12));

        if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
          Expression exp13 =
            ExpressionFactory.noMatchDbExp(
              EipTAclRole.ROLE_ID_PK_COLUMN,
              Integer.valueOf(acl_role_id));
          rolequery.andQualifier(exp13);
        }

        List<EipTAclRole> roleList = rolequery.fetchList();
        if (roleList != null && roleList.size() != 0) {
          msgList
            .add(ALLocalizationUtils.getl10n("ACCESSCTL_ALERT_OTHER_ROLE"));
        }
      } catch (Exception ex) {
        logger.error("AccessControlFormData.validate", ex);
        return false;
      }
    }

    return (msgList.size() == 0);
  }

  /**
   * ロールをデータベースから読み出します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTAclRole aclrole = AccessControlUtils.getEipTAclRole(rundata, context);
      if (aclrole == null) {
        return false;
      }

      // ロール名
      acl_role_name.setValue(aclrole.getRoleName());

      List<EipTAclUserRoleMap> aclUserRoleMaps =
        AccessControlUtils.getEipTAclUserRoleMaps(aclrole
          .getRoleId()
          .intValue());
      if (aclUserRoleMaps != null && aclUserRoleMaps.size() > 0) {
        EipTAclUserRoleMap rolemap = null;
        TurbineUser tuser = null;
        int size = aclUserRoleMaps.size();
        for (int i = 0; i < size; i++) {
          rolemap = aclUserRoleMaps.get(i);
          tuser = rolemap.getTurbineUser();
          ALEipUser user = new ALEipUser();
          user.initField();
          user.setUserId(tuser.getUserId().intValue());
          user.setName(tuser.getLoginName());
          user.setAliasName(tuser.getFirstName(), tuser.getLastName());
          // 招待中ユーザでなければ追加
          if (!JetspeedResources.CONFIRM_VALUE_PENDING.equals(tuser
            .getConfirmValue())) {
            memberList.add(user);
          }
        }
      }

      EipTAclPortletFeature feature = aclrole.getEipTAclPortletFeature();
      feature_id.setValue(feature.getFeatureId().intValue());
      defineAclType = feature.getAclType().intValue();

      // メモ
      note.setValue(aclrole.getNote());

      // アクセス権限
      int tmpAclType = aclrole.getAclType();
      AccessControlUtils.setupAcl(
        ALAccessControlConstants.VALUE_ACL_LIST,
        tmpAclType,
        acllist);
      AccessControlUtils.setupAcl(
        ALAccessControlConstants.VALUE_ACL_DETAIL,
        tmpAclType,
        acldetail);
      AccessControlUtils.setupAcl(
        ALAccessControlConstants.VALUE_ACL_INSERT,
        tmpAclType,
        aclinsert);
      AccessControlUtils.setupAcl(
        ALAccessControlConstants.VALUE_ACL_UPDATE,
        tmpAclType,
        aclupdate);
      AccessControlUtils.setupAcl(
        ALAccessControlConstants.VALUE_ACL_DELETE,
        tmpAclType,
        acldelete);
      AccessControlUtils.setupAcl(
        ALAccessControlConstants.VALUE_ACL_EXPORT,
        tmpAclType,
        aclexport);

    } catch (Exception ex) {
      logger.error("AccessControlFormData.loadFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * ロールをデータベースから削除します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {

      String aclroleid =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      if (aclroleid == null || Integer.valueOf(aclroleid) == null) {
        // IDが空の場合
        logger.debug("[AccessControlUtils] Empty ID...");
        return false;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(EipTAclRole.ROLE_ID_PK_COLUMN, aclroleid);
      SelectQuery<EipTAclRole> query = Database.query(EipTAclRole.class, exp);
      List<EipTAclRole> aclroles = query.fetchList();
      if (aclroles == null || aclroles.size() == 0) {
        // 指定したIDのレコードが見つからない場合
        logger.debug("[AccessControlUtils] Not found ID...");
        return false;
      }
      SelectQuery<EipTAclUserRoleMap> EipTAclUserRoleMapSQL =
        Database.query(EipTAclUserRoleMap.class);
      EipTAclUserRoleMapSQL.andQualifier(ExpressionFactory.matchDbExp(
        EipTAclUserRoleMap.ROLE_ID_COLUMN,
        aclroleid));
      List<EipTAclUserRoleMap> userRoleMaps = EipTAclUserRoleMapSQL.fetchList();

      // オブジェクトを削除
      Database.deleteAll(userRoleMaps);
      Database.delete(aclroles.get(0));

      Database.commit();

      // イベントログに保存
      for (EipTAclRole role : aclroles) {
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          role.getRoleId(),
          ALEventlogConstants.PORTLET_TYPE_ACCESSCTL,
          ALLocalizationUtils.getl10nFormat("ACCESSCTL_EVENTLOG_DELETE", role
            .getRoleName()));
      }

    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccessControlFormData.deleteFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * ロールをデータベースに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      Date now = Calendar.getInstance().getTime();

      // 新規オブジェクトモデル
      EipTAclRole aclrole = Database.create(EipTAclRole.class);
      aclrole.setRoleName(acl_role_name.getValue());
      aclrole.setNote(note.getValue());

      long aclType = getAclTypeValue();
      aclrole.setAclType(Integer.valueOf((int) aclType));

      EipTAclPortletFeature feature =
        Database.get(EipTAclPortletFeature.class, Integer
          .valueOf((int) feature_id.getValue()));
      aclrole.setEipTAclPortletFeature(feature);

      // 作成日
      aclrole.setCreateDate(now);
      // 更新日
      aclrole.setUpdateDate(now);

      // userMapの登録
      insertEipTAclUserRoleMap(aclrole, memberList.get(0));

      // メンバー登録
      int size = memberList.size();
      for (int i = 1; i < size; i++) {
        insertEipTAclUserRoleMap(aclrole, memberList.get(i));
      }

      // ロールを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        aclrole.getRoleId(),
        ALEventlogConstants.PORTLET_TYPE_ACCESSCTL,
        ALLocalizationUtils.getl10nFormat("ACCESSCTL_EVENTLOG_ADD", aclrole
          .getRoleName()));

    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccessControlFormData.insertFormData", ex);
      return false;
    }
    return true;
  }

  /**
   * データベースに格納されているロールを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTAclRole aclrole = AccessControlUtils.getEipTAclRole(rundata, context);
      if (aclrole == null) {
        return false;
      }

      aclrole.setRoleName(acl_role_name.getValue());
      aclrole.setNote(note.getValue());
      // 更新日
      aclrole.setUpdateDate(Calendar.getInstance().getTime());

      long aclType = getAclTypeValue();
      aclrole.setAclType(Integer.valueOf((int) aclType));

      EipTAclPortletFeature feature =
        Database.get(EipTAclPortletFeature.class, Integer
          .valueOf((int) feature_id.getValue()));
      aclrole.setEipTAclPortletFeature(feature);

      // userMapの登録
      int size = memberList.size();
      for (int i = 0; i < size; i++) {
        insertEipTAclUserRoleMap(aclrole, memberList.get(i));
      }

      // 古いEipTAclUserRoleMapの削除
      deleteEipTAclUserRoleMap(rundata, context);

      // ロールを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        aclrole.getRoleId(),
        ALEventlogConstants.PORTLET_TYPE_ACCESSCTL,
        ALLocalizationUtils.getl10nFormat("ACCESSCTL_EVENTLOG_UPDATE", aclrole
          .getRoleName()));
    } catch (Exception ex) {
      Database.rollback();
      logger.error("AccessControlFormData.updateFormData", ex);
      return false;
    }
    return true;
  }

  private void insertEipTAclUserRoleMap(EipTAclRole aclrole, ALEipUser user) {
    EipTAclUserRoleMap map = Database.create(EipTAclUserRoleMap.class);
    int userid = (int) user.getUserId().getValue();
    // ユーザーID
    TurbineUser tuser =
      Database.get(TurbineUser.class, Integer.valueOf(userid));
    map.setEipTAclRole(aclrole);
    map.setTurbineUser(tuser);
  }

  private boolean deleteEipTAclUserRoleMap(RunData rundata, Context context) {
    String aclroleid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    if (aclroleid == null || Integer.valueOf(aclroleid) == null) {
      // IDが空の場合
      logger.debug("[AccessControlFormData] Empty ID...");
      return false;
    }

    SelectQuery<EipTAclUserRoleMap> query =
      Database.query(EipTAclUserRoleMap.class);
    Expression exp =
      ExpressionFactory.matchDbExp(EipTAclUserRoleMap.EIP_TACL_ROLE_PROPERTY
        + "."
        + EipTAclRole.ROLE_ID_PK_COLUMN, aclroleid);
    query.setQualifier(exp);
    List<EipTAclUserRoleMap> maps = query.fetchList();
    if (maps == null || maps.size() == 0) {
      return true;
    }

    Database.deleteAll(maps);
    return true;
  }

  private long getAclTypeValue() {
    long aclType =
      acllist.getValue()
        * ALAccessControlConstants.VALUE_ACL_LIST
        + acldetail.getValue()
        * ALAccessControlConstants.VALUE_ACL_DETAIL
        + aclinsert.getValue()
        * ALAccessControlConstants.VALUE_ACL_INSERT
        + aclupdate.getValue()
        * ALAccessControlConstants.VALUE_ACL_UPDATE
        + acldelete.getValue()
        * ALAccessControlConstants.VALUE_ACL_DELETE
        + aclexport.getValue()
        * ALAccessControlConstants.VALUE_ACL_EXPORT;
    return aclType;
  }

  private boolean hasAcl(int defineAclType, int aclType) {
    return ((defineAclType & aclType) == aclType);
  }

  /**
   * ロール名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getAclRoleName() {
    return acl_role_name;
  }

  public ALNumberField getFeatureId() {
    return feature_id;
  }

  /**
   * メモを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  public ALNumberField getAclList() {
    return acllist;
  }

  public ALNumberField getAclDetail() {
    return acldetail;
  }

  public ALNumberField getAclInsert() {
    return aclinsert;
  }

  public ALNumberField getAclUpdate() {
    return aclupdate;
  }

  public ALNumberField getAclDelete() {
    return acldelete;
  }

  public ALNumberField getAclExport() {
    return aclexport;
  }

  /**
   * グループメンバーを取得します。
   * 
   * @return
   */
  public List<ALEipUser> getMemberList() {
    return memberList;
  }

  /**
   * 
   * @return
   */
  public List<AccessControlFeatureBean> getPortletFeatureList() {
    return portletFeatureList;
  }

  public int getDefineAclType() {
    return defineAclType;
  }

  public boolean hasAclList() {
    return hasAcl(defineAclType, ALAccessControlConstants.VALUE_ACL_LIST);
  }

  public boolean hasAclDetail() {
    return hasAcl(defineAclType, ALAccessControlConstants.VALUE_ACL_DETAIL);
  }

  public boolean hasAclInsert() {
    return hasAcl(defineAclType, ALAccessControlConstants.VALUE_ACL_INSERT);
  }

  public boolean hasAclUpdate() {
    return hasAcl(defineAclType, ALAccessControlConstants.VALUE_ACL_UPDATE);
  }

  public boolean hasAclDelete() {
    return hasAcl(defineAclType, ALAccessControlConstants.VALUE_ACL_DELETE);
  }

  public boolean hasAclExport() {
    return hasAcl(defineAclType, ALAccessControlConstants.VALUE_ACL_EXPORT);
  }
}
