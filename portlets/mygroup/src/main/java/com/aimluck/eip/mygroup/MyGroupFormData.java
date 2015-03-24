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
package com.aimluck.eip.mygroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipFacilityGroup;
import com.aimluck.eip.cayenne.om.portlet.EipMFacility;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.facilities.FacilityResultData;
import com.aimluck.eip.facilities.util.FacilitiesUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.mygroup.util.MyGroupUtils;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * マイグループのフォームデータを管理するクラスです。 <BR>
 * 
 */
public class MyGroupFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(MyGroupFormData.class.getName());

  /** グループ名 */
  private ALStringField group_alias_name;

  /** メンバーリスト */
  private List<ALEipUser> memberList;

  /** 設備リスト */
  private List<FacilityResultData> facilityList;

  /** 全設備リスト */
  private List<FacilityResultData> facilityAllList;

  /** ログインユーザ ID */
  private int userId;

  private String mygroup_name = null;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    userId = ALEipUtils.getUserId(rundata);
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // グループ名
    group_alias_name = new ALStringField();
    group_alias_name.setFieldName("グループ名");
    group_alias_name.setTrim(true);

    memberList = new ArrayList<ALEipUser>();
    facilityList = new ArrayList<FacilityResultData>();
  }

  /**
   * 
   * @param rundata
   * @param context
   */
  public void loadFacilityAllList(RunData rundata, Context context) {
    facilityAllList = new ArrayList<FacilityResultData>();
    facilityAllList.addAll(FacilitiesUtils.getFacilityAllList());
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
          mygroup_name =
            ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
        }

        String str[] =
          getStrList(rundata.getParameters().getStrings("member_to"));
        if (str != null && str.length > 0) {
          SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
          Expression exp =
            ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, str);
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

        String f_id[] =
          getStrList(rundata.getParameters().getStrings("facility_to"));
        if (f_id != null && f_id.length > 0) {
          SelectQuery<EipMFacility> fquery = Database.query(EipMFacility.class);
          Expression exp =
            ExpressionFactory.inDbExp(EipMFacility.FACILITY_ID_PK_COLUMN, f_id);
          fquery.setQualifier(exp);
          List<EipMFacility> f_list = fquery.fetchList();

          int f_size = f_list.size();
          for (int i = 0; i < f_size; i++) {
            EipMFacility f_record = f_list.get(i);
            FacilityResultData rd = new FacilityResultData();
            rd.initField();
            rd.setFacilityId(f_record.getFacilityId().longValue());
            rd.setFacilityName(f_record.getFacilityName());
            facilityList.add(rd);
          }
        }
      } catch (RuntimeException ex) {
        throw ex;
      } catch (Exception ex) {
        logger.error("mygroup", ex);
      }
    }
    return res;
  }

  /**
   * 
   * @param memberIdList
   * @param memberId
   * @return
   */
  private String[] getStrList(String[] strs) {
    if (strs == null || strs.length <= 0) {
      return null;
    }

    ArrayList<String> list = new ArrayList<String>();
    int len = strs.length;
    for (int i = 0; i < len; i++) {
      if (strs[i] != null && !strs[i].equals("")) {
        list.add(strs[i]);
      }
    }
    String[] new_strs = new String[list.size()];
    new_strs = list.toArray(new_strs);

    return new_strs;
  }

  /**
   * 各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // グループ名
    group_alias_name.setNotNull(true);
    group_alias_name.limitMaxLength(50);
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
    try {
      SelectQuery<TurbineGroup> query = Database.query(TurbineGroup.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            TurbineGroup.GROUP_ALIAS_NAME_PROPERTY,
            group_alias_name.getValue());
        Expression exp2 =
          ExpressionFactory.matchExp(TurbineGroup.OWNER_ID_PROPERTY, Integer
            .valueOf(userId));
        query.setQualifier(exp1);
        query.andQualifier(exp2);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            TurbineGroup.GROUP_ALIAS_NAME_PROPERTY,
            group_alias_name.getValue());
        Expression exp2 =
          ExpressionFactory.matchExp(TurbineGroup.OWNER_ID_PROPERTY, Integer
            .valueOf(userId));
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        Expression exp3 =
          ExpressionFactory.noMatchExp(
            TurbineGroup.GROUP_NAME_PROPERTY,
            mygroup_name);
        query.andQualifier(exp3);
      }

      if (query.fetchList().size() != 0) {
        msgList.add("グループ名『 <span class='em'>"
          + group_alias_name.toString()
          + "</span> 』は既に登録されています。");
      }
    } catch (Exception ex) {
      logger.error("mygroup", ex);
      return false;
    }

    group_alias_name.validate(msgList);
    return (msgList.size() == 0);
  }

  /**
   * 『マイグループ』を読み込みます。 <BR>
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
      TurbineGroup record = MyGroupUtils.getGroup(rundata, context);
      if (record == null) {
        return false;
      }
      // グループ名
      group_alias_name.setValue(record.getGroupAliasName());

      memberList.addAll(ALEipUtils.getUsers(record.getName()));

      facilityList.addAll(FacilitiesUtils.getFacilityList(record.getName()));

    } catch (Exception ex) {
      logger.error("mygroup", ex);
      return false;
    }
    return true;
  }

  /**
   * 『マイグループ』を追加します。 <BR>
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
      String name = group_alias_name.getValue();
      // グループ名
      String groupName =
        new StringBuffer().append(new Date().getTime()).append("_").append(
          ALEipUtils.getUserId(rundata)).toString();
      group.setGroupName(groupName);
      // オーナIDの設定、作成者がオーナとなるので、自分自身のUID
      group.setOwnerId(Integer.valueOf(ALEipUtils.getUserId(rundata)));
      // グループ名(アプリケーションレベル)
      group.setGroupAliasName(name);
      // 公開フラグ
      group.setPublicFlag("1");

      // グループを追加
      JetspeedSecurity.addGroup(group);

      // グループにユーザーを追加
      int size = memberList.size();
      for (int i = 0; i < size; i++) {
        JetspeedSecurity.joinGroup(
          memberList.get(i).getName().getValue(),
          group.getGroupName());
      }

      // グループに設備を追加する．
      Group jetspeedgroup = JetspeedSecurity.getGroup(group.getGroupName());
      int f_size = facilityList.size();
      for (int i = 0; i < f_size; i++) {
        int fid = (int) facilityList.get(i).getFacilityId().getValue();
        EipMFacility facility =
          Database.get(EipMFacility.class, Integer.valueOf(fid));

        insertFacilityGroup(facility, (TurbineGroup) jetspeedgroup);
      }

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        Integer.parseInt(group.getId()),
        ALEventlogConstants.PORTLET_TYPE_MYGROUP,
        group.getGroupAliasName());

      ALEipUtils.reloadMygroup(rundata);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("mygroup", ex);
      return false;
    }
    return true;
  }

  private void insertFacilityGroup(EipMFacility facility,
      TurbineGroup turbine_group) {
    try {
      EipFacilityGroup fg = Database.create(EipFacilityGroup.class);
      fg.setEipMFacility(facility);
      fg.setTurbineGroup(turbine_group);
    } catch (Exception e) {
      logger.error("mygroup", e);
    }
  }

  /**
   * 『マイグループ』を更新します。 <BR>
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
      TurbineGroup record = MyGroupUtils.getGroup(rundata, context);
      if (record == null) {
        return false;
      }
      // グループ名
      record.setGroupAliasName(group_alias_name.getValue());

      // グループを更新
      JetspeedSecurity.saveGroup(record);

      // グループからユーザーを削除
      List<ALEipUser> users = ALEipUtils.getUsers(record.getGroupName());
      int size = users.size();
      for (int i = 0; i < size; i++) {
        JetspeedSecurity.unjoinGroup(
          (users.get(i)).getName().getValue(),
          record.getGroupName());
      }

      // グループにユーザーを追加
      size = memberList.size();
      for (int i = 0; i < size; i++) {
        JetspeedSecurity.joinGroup(
          memberList.get(i).getName().getValue(),
          record.getGroupName());
      }

      // グループから設備を削除
      List<Integer> oldFIdList =
        FacilitiesUtils.getFacilityIds(record.getGroupName());
      if (oldFIdList != null && oldFIdList.size() > 0) {

        SelectQuery<EipFacilityGroup> query =
          Database.query(EipFacilityGroup.class);
        Expression exp1 =
          ExpressionFactory.inDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN,
            oldFIdList);
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.matchDbExp(EipFacilityGroup.TURBINE_GROUP_PROPERTY
            + "."
            + TurbineGroup.GROUP_ID_PK_COLUMN, record.getId());
        query.andQualifier(exp2);
        List<EipFacilityGroup> flist = query.fetchList();
        if (flist != null && flist.size() > 0) {
          Database.deleteAll(flist);
        }
      }

      // グループに設備を追加する．
      TurbineGroup tgroup =
        Database.get(TurbineGroup.class, Integer.valueOf(record.getId()));

      int f_size = facilityList.size();
      for (int i = 0; i < f_size; i++) {
        int fid = (int) facilityList.get(i).getFacilityId().getValue();

        Expression fexp =
          ExpressionFactory.matchDbExp(
            EipMFacility.FACILITY_ID_PK_COLUMN,
            Integer.valueOf(fid));
        SelectQuery<EipMFacility> fquery =
          Database.query(EipMFacility.class, fexp);
        List<EipMFacility> list = fquery.fetchList();
        if (list == null || list.size() <= 0) {
          continue;
        }

        EipMFacility facility = list.get(0);
        insertFacilityGroup(facility, tgroup);
      }

      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        Integer.parseInt(record.getId()),
        ALEventlogConstants.PORTLET_TYPE_MYGROUP,
        record.getGroupAliasName());

      ALEipUtils.reloadMygroup(rundata);
    } catch (RuntimeException ex) {
      Database.rollback();
      logger.error("mygroup", ex);
      return false;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("mygroup", ex);
      return false;
    }
    return true;
  }

  /**
   * 『マイグループ』を削除します。 <BR>
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
      TurbineGroup record = MyGroupUtils.getGroup(rundata, context);
      if (record == null) {
        return false;
      }

      Expression exp =
        ExpressionFactory.matchDbExp(TurbineGroup.GROUP_ID_PK_COLUMN, record
          .getId());
      SelectQuery<EipFacilityGroup> query =
        Database.query(EipFacilityGroup.class, exp);
      List<EipFacilityGroup> fglist = query.fetchList();
      if (fglist != null && fglist.size() > 0) {
        Database.deleteAll(fglist);
      }

      // グループからユーザーを削除
      List<ALEipUser> users =
        ALEipUtils.getUsersIncludingN(record.getGroupName());
      int size = users.size();
      for (int i = 0; i < size; i++) {
        JetspeedSecurity.unjoinGroup(
          (users.get(i)).getName().getValue(),
          record.getGroupName());
      }

      // グループを削除(Turbine_GROUP)
      JetspeedSecurity.removeGroup(record.getGroupName());

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        Integer.parseInt(record.getId()),
        ALEventlogConstants.PORTLET_TYPE_MYGROUP,
        record.getGroupAliasName());

      // マイグループの再読み込み（セッションのリフレッシュ）
      ALEipUtils.reloadMygroup(rundata);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("mygroup", ex);
      return false;
    }
    return true;
  }

  /**
   * グループ名を取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getGroupAliasName() {
    return group_alias_name;
  }

  /**
   * グループメンバーを取得します。 <BR>
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
    return ALEipUtils.getUsers(groupname);
  }

  /**
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  public List<FacilityResultData> getFacilityList() {
    return facilityList;
  }

  public List<FacilityResultData> getFacilityAllList() {
    return facilityAllList;
  }
}
