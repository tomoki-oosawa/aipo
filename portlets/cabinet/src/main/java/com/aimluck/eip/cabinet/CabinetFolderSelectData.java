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

package com.aimluck.eip.cabinet;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolderMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダのフォルダ検索データを管理するためのクラスです。 <br />
 */
public class CabinetFolderSelectData extends
    ALAbstractSelectData<EipTCabinetFolder, EipTCabinetFolder> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetFolderSelectData.class.getName());

  /** フォルダ情報一覧 */
  private List<FolderInfo> folder_hierarchy_list;

  /** <code>members</code> 共有メンバー */
  private List<ALEipUser> members;

  int folder_id = 0;

  private RunData rundata;

  /** アクセス制限（編集の可否） */
  private boolean editable;

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
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, ALEipUtils
        .getPortlet(rundata, context)
        .getPortletConfig()
        .getInitParameter("p3c-sort"));
      logger.debug("[CabinetSelectData] Init Parameter. : "
        + ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p3c-sort"));
    }

    folder_hierarchy_list = CabinetUtils.getFolderList();
    members = new ArrayList<ALEipUser>();
    this.rundata = rundata;

    // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
    if (ALEipUtils.isMatch(rundata, context)) {
      // ENTITY ID
      if (rundata.getParameters().containsKey(CabinetUtils.KEY_FOLDER_ID)) {
        // entityid=new を指定することによって明示的にセッション変数を削除することができる。
        ALEipUtils.setTemp(
          rundata,
          context,
          CabinetUtils.KEY_FOLDER_ID,
          rundata.getParameters().getString(CabinetUtils.KEY_FOLDER_ID));
      }
    }

    super.init(action, rundata, context);
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<EipTCabinetFolder> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * 
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTCabinetFolder selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    // オブジェクトモデルを取得
    return CabinetUtils.getEipTCabinetFolder(rundata, context);
  }

  /**
   *
   */
  @Override
  protected Object getResultData(EipTCabinetFolder obj)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * 
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTCabinetFolder record)
      throws ALPageNotFoundException, ALDBErrorException {

    try {

      folder_id = record.getFolderId().intValue();

      CabinetFolderResultData rd = new CabinetFolderResultData();
      rd.initField();
      rd.setFolderId(record.getFolderId().longValue());
      rd.setFolderName(record.getFolderName());
      rd.setNote(record.getNote());
      rd.setPosition(CabinetUtils.getFolderPosition(
        folder_hierarchy_list,
        folder_id));
      int tmp_public_flag = Integer.valueOf(record.getPublicFlag());
      int access_control_folder_id;
      if (tmp_public_flag == CabinetUtils.ACCESS_PUBLIC_ALL) {
        /** 上位のフォルダからアクセス権限を継承している */
        access_control_folder_id =
          CabinetUtils.getAccessControlFolderId(record.getParentId());
        rd.setAccessFlag(Integer.valueOf(CabinetUtils.getFolderByPK(
          access_control_folder_id).getPublicFlag()));
      } else {
        rd.setAccessFlag(tmp_public_flag);
        access_control_folder_id = (int) rd.getFolderId().getValue();
      }
      String createUserName = "";
      ALEipUser createUser =
        ALEipUtils.getALEipUser(record.getCreateUserId().intValue());
      if (createUser != null) {
        createUserName = createUser.getAliasName().getValue();
      }

      rd.setCreateUser(createUserName);
      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      String updateUserName = "";
      ALEipUser updateUser =
        ALEipUtils.getALEipUser(record.getUpdateUserId().intValue());
      if (updateUser != null) {
        updateUserName = updateUser.getAliasName().getValue();
      }

      // メンバーのリストを取得
      SelectQuery<EipTCabinetFolderMap> mapquery =
        Database.query(EipTCabinetFolderMap.class);
      Expression mapexp =
        ExpressionFactory.matchDbExp(
          EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY,
          access_control_folder_id);
      mapquery.setQualifier(mapexp);
      List<EipTCabinetFolderMap> list = mapquery.fetchList();

      List<Integer> users = new ArrayList<Integer>();
      for (int i = 0; i < list.size(); i++) {
        EipTCabinetFolderMap map = list.get(i);
        users.add(map.getUserId());
      }
      SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
      Expression exp =
        ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, users);
      query.setQualifier(exp);
      members.addAll(ALEipUtils.getUsersFromSelectQuery(query));

      rd.setUpdateUser(updateUserName);
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));

      int size = folder_hierarchy_list.size();
      for (int i = 0; i < size; i++) {
        FolderInfo info = folder_hierarchy_list.get(i);
        if (info.getFolderId() == record.getFolderId().intValue()) {
          rd.setCanUpdate(info.canUpdate());
          break;
        }
      }
      editable = CabinetUtils.isEditableFolder(record.getFolderId(), rundata);

      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    return map;
  }

  public int getFolderId() {
    return folder_id;
  }

  public List<ALEipUser> getMemberList() {
    return members;
  }

  public boolean isEditable() {
    return editable;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   * 
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_CABINET_FOLDER;
  }
}
