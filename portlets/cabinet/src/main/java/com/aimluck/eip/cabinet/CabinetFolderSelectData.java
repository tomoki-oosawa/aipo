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
package com.aimluck.eip.cabinet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

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
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 共有フォルダのフォルダ検索データを管理するためのクラスです。 <br />
 */
public class CabinetFolderSelectData extends
    ALAbstractSelectData<EipTCabinetFolder, EipTCabinetFolder> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetFolderSelectData.class.getName());

  /** <code>members</code> 共有メンバー */
  private List<ALEipUser> members;

  int folder_id = 0;

  private RunData rundata;

  /** 選択されたフォルダ情報 */
  private FolderInfo selected_folderinfo = null;

  private List<FolderInfo> folder_hierarchy_list;

  /** フォルダの閲覧権限 */
  private boolean isAccessible = true;

  /** フォルダの編集権限 */
  private boolean isEditable = true;

  /** ノーマル画面の表示かどうか */
  private boolean isNormalContext = false;

  public void setIsNormalContext(boolean flg) {
    isNormalContext = flg;
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

    int fid = CabinetUtils.ROOT_FODLER_ID;
    if (isNormalContext) {
      // フォルダ選択のリクエスト
      // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
      if (ALEipUtils.isMatch(rundata, context)) {
        // ENTITY ID
        if (rundata.getParameters().containsKey(CabinetUtils.KEY_FOLDER_ID)) {
          ALEipUtils.setTemp(
            rundata,
            context,
            CabinetUtils.KEY_FOLDER_ID,
            rundata.getParameters().getString(CabinetUtils.KEY_FOLDER_ID));
        }
      }
      String tmpfid =
        ALEipUtils.getTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID);

      if (tmpfid != null && !"".equals(tmpfid)) {
        try {
          fid = Integer.parseInt(tmpfid);
          /** フォルダ権限のチェック */
          if (fid == 0) {
            isAccessible = true;
            isEditable = true;
          } else {
            isAccessible = CabinetUtils.isAccessibleFolder(fid, rundata);
            isEditable = CabinetUtils.isEditableFolder(fid, rundata);
          }
        } catch (Exception e) {
          fid = CabinetUtils.ROOT_FODLER_ID;
        }
      } else {
        String id =
          ALEipUtils
            .getPortlet(rundata, context)
            .getPortletConfig()
            .getInitParameter("p3a-folder");
        fid = Integer.parseInt(id);
      }
    } else {
      // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
      if (ALEipUtils.isMatch(rundata, context)) {
        // ENTITY ID
        if (rundata.getParameters().containsKey(CabinetUtils.KEY_FOLDER_ID)) {
          ALEipUtils.setTemp(
            rundata,
            context,
            CabinetUtils.KEY_FOLDER_ID,
            rundata.getParameters().getString(CabinetUtils.KEY_FOLDER_ID));
        }
      }
      String tmpfid =
        ALEipUtils.getTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID);
      if (tmpfid != null && !"".equals(tmpfid)) {
        try {
          fid = Integer.parseInt(tmpfid);
          /** フォルダ権限のチェック */
          if (fid == 0) {
            isAccessible = true;
            isEditable = true;
          } else {
            isAccessible = CabinetUtils.isAccessibleFolder(fid, rundata);
            isEditable = CabinetUtils.isEditableFolder(fid, rundata);
          }
        } catch (RuntimeException e) {
          throw e;
        } catch (Exception e) {
          fid = CabinetUtils.ROOT_FODLER_ID;
        }
      }
    }

    folder_hierarchy_list = CabinetUtils.getFolderList();
    if (folder_hierarchy_list != null && folder_hierarchy_list.size() > 0) {
      int size = folder_hierarchy_list.size();
      for (int i = 0; i < size; i++) {
        FolderInfo info = folder_hierarchy_list.get(i);
        if (info.getFolderId() == fid) {
          selected_folderinfo = info;
          break;
        }
      }
      /*
       * if (selected_folderinfo == null) { selected_folderinfo =
       * folder_hierarchy_list.get(0); }
       */
    }
    // メンバーリスト
    members = new ArrayList<ALEipUser>();

    this.rundata = rundata;

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
    CabinetUtils.setFolderVisible(
      folder_hierarchy_list,
      selected_folderinfo,
      rundata);
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
   * @param rundata
   * @param context
   */
  public void loadFolderList() {
    folder_hierarchy_list = CabinetUtils.getFolderList();
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
      rd.setCreateUserId(record.getCreateUserId());
      rd.setCreateDate(new SimpleDateFormat(ALLocalizationUtils
        .getl10n("CABINET_YEAR_MONTH_DAY")).format(record.getCreateDate()));
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
      Expression nonDisabledexp =
        ExpressionFactory.noMatchExp(TurbineUser.DISABLED_PROPERTY, "T");
      query.setQualifier(exp.andExp(nonDisabledexp));
      members.addAll(ALEipUtils.getUsersFromSelectQuery(query));

      rd.setUpdateUser(updateUserName);
      rd.setUpdateUserId(record.getUpdateUserId());
      rd.setUpdateDate(new SimpleDateFormat(ALLocalizationUtils
        .getl10n("CABINET_YEAR_MONTH_DAY_HOUR_MINUTE")).format(record
        .getUpdateDate()));

      int size = folder_hierarchy_list.size();
      for (int i = 0; i < size; i++) {
        FolderInfo info = folder_hierarchy_list.get(i);
        if (info.getFolderId() == record.getFolderId().intValue()) {
          rd.setCanUpdate(info.canUpdate());
          break;
        }
      }
      isEditable = CabinetUtils.isEditableFolder(record.getFolderId(), rundata);

      return rd;
    } catch (RuntimeException ex) {
      logger.error("cabinet", ex);
      return null;
    } catch (Exception ex) {
      logger.error("cabinet", ex);
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

  public List<FolderInfo> getFolderHierarchyList() {
    return folder_hierarchy_list;
  }

  public FolderInfo getSelectedFolderInfo() {
    return selected_folderinfo;
  }

  public List<ALEipUser> getMemberList() {
    return members;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_CABINET_FILE;
  }

  public boolean isAccessible() {
    return isAccessible;
  }

  public boolean isEditable() {
    return isEditable;
  }

  public void setEditable(boolean isEditable) {
    this.isEditable = isEditable;
  }

}
