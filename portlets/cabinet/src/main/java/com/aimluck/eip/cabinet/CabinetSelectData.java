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
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダのファイル検索データを管理するためのクラスです。 <br />
 */
public class CabinetSelectData extends ALAbstractSelectData<EipTCabinetFile> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetSelectData.class.getName());

  /** 選択されたフォルダ情報 */
  private FolderInfo selected_folderinfo = null;

  private ArrayList folder_hierarchy_list;

  /** ファイル総数 */
  private int fileSum;

  /** フォルダの閲覧権限 */
  private boolean isAccessible = true;

  /** フォルダの編集権限 */
  private boolean isEditable = true;

  /** ノーマル画面の表示かどうか */
  private boolean isNormalContext = false;

  private RunData rundata;

  public void setIsNormalContext(boolean flg) {
    isNormalContext = flg;
  }

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @see com.aimluck.eip.common.ALAbstractSelectData#init(com.aimluck.eip.modules.actions.common.ALAction,
   *      org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
   */
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR,
        ALEipUtils.getPortlet(rundata, context).getPortletConfig()
          .getInitParameter("p1c-sort"));
      logger.debug("[CabinetSelectData] Init Parameter. : "
        + ALEipUtils.getPortlet(rundata, context).getPortletConfig()
          .getInitParameter("p1c-sort"));
    }

    int fid = CabinetUtils.ROOT_FODLER_ID;
    if (isNormalContext) {
      String id = ALEipUtils.getPortlet(rundata, context).getPortletConfig()
        .getInitParameter("p3a-folder");
      fid = Integer.parseInt(id);
    } else {
      // 自ポートレットからのリクエストであれば、パラメータを展開しセッションに保存する。
      if (ALEipUtils.isMatch(rundata, context)) {
        // ENTITY ID
        if (rundata.getParameters().containsKey(CabinetUtils.KEY_FOLDER_ID)) {
          ALEipUtils.setTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID,
            rundata.getParameters().getString(CabinetUtils.KEY_FOLDER_ID));
        }
      }
      String tmpfid = ALEipUtils.getTemp(rundata, context,
        CabinetUtils.KEY_FOLDER_ID);
      if (tmpfid != null && !"".equals(tmpfid)) {
        try {
          fid = Integer.parseInt(tmpfid);
          /** フォルダ権限のチェック */
          isAccessible = CabinetUtils.isAccessibleFolder(fid, rundata);
          isEditable = CabinetUtils.isEditableFolder(fid, rundata);
        } catch (Exception e) {
          fid = CabinetUtils.ROOT_FODLER_ID;
        }
      }
    }

    folder_hierarchy_list = CabinetUtils.getFolderList();
    if (folder_hierarchy_list != null && folder_hierarchy_list.size() > 0) {
      int size = folder_hierarchy_list.size();
      for (int i = 0; i < size; i++) {
        FolderInfo info = (FolderInfo) folder_hierarchy_list.get(i);
        if (info.getFolderId() == fid) {
          selected_folderinfo = info;
          break;
        }
      }
      if (selected_folderinfo == null) {
        selected_folderinfo = (FolderInfo) folder_hierarchy_list.get(0);
      }
    }

    this.rundata = rundata;

    super.init(action, rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectList(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected List<EipTCabinetFile> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      CabinetUtils.setFolderVisible(folder_hierarchy_list, selected_folderinfo,
        rundata);

      SelectQuery<EipTCabinetFile> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      List<EipTCabinetFile> list = query.perform();
      // ファイル総数をセットする．
      if (list == null) {
        return new ArrayList<EipTCabinetFile>();
      } else {
        fileSum = list.size();
      }
      return buildPaginatedList(list);
    } catch (Exception ex) {
      logger.error("Exception", ex);
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
  private SelectQuery<EipTCabinetFile> getSelectQuery(RunData rundata,
      Context context) {
    SelectQuery<EipTCabinetFile> query = new SelectQuery<EipTCabinetFile>(
      EipTCabinetFile.class)
      .select(EipTCabinetFile.FILE_ID_PK_COLUMN,
        EipTCabinetFile.FOLDER_ID_COLUMN, EipTCabinetFile.FILE_TITLE_COLUMN,
        EipTCabinetFile.FILE_NAME_COLUMN, EipTCabinetFile.FILE_SIZE_COLUMN,
        EipTCabinetFile.UPDATE_USER_ID_COLUMN,
        EipTCabinetFile.UPDATE_DATE_COLUMN);
    if (selected_folderinfo != null) {
      Expression exp = ExpressionFactory.matchDbExp(
        EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
        Integer.valueOf(selected_folderinfo.getFolderId()));
      query.setQualifier(exp);
    }
    query.distinct(true);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#selectDetail(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context)
   */
  protected EipTCabinetFile selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return CabinetUtils.getEipTCabinetFile(rundata, context);
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultData(java.lang.Object)
   */
  protected Object getResultData(EipTCabinetFile record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      CabinetFileResultData rd = new CabinetFileResultData();
      rd.initField();

      rd.setFileId(record.getFileId());
      rd.setFileTitle(record.getFileTitle());
      rd.setFileName(record.getFileName());
      rd.setFileSize(record.getFileSize());

      String updateUserName = "";
      ALEipUser updateUser = ALEipUtils.getALEipUser(record.getUpdateUserId());
      if (updateUser != null) {
        updateUserName = updateUser.getAliasName().getValue();
      }
      rd.setUpdateUser(updateUserName);
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getResultDataDetail(java.lang.Object)
   */
  protected Object getResultDataDetail(EipTCabinetFile obj)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      EipTCabinetFile record = (EipTCabinetFile) obj;

      CabinetFileResultData rd = new CabinetFileResultData();
      rd.initField();
      rd.setFileId(record.getFileId().longValue());
      rd.setFileTitle(record.getFileTitle());
      rd.setFileName(record.getFileName());
      rd.setFileSize(record.getFileSize().longValue());
      rd.setPosition(CabinetUtils.getFolderPosition(folder_hierarchy_list,
        record.getFolderId().intValue()));
      rd.setNote(record.getNote());
      rd.setisEditable((CabinetUtils.isEditableFolder(record.getFolderId(),
        rundata)));

      String createUserName = "";
      ALEipUser createUser = ALEipUtils.getALEipUser(record.getCreateUserId()
        .intValue());
      if (createUser != null) {
        createUserName = createUser.getAliasName().getValue();
      }
      rd.setCreateUser(createUserName);
      rd.setCreateDate(ALDateUtil.format(record.getCreateDate(), "yyyy年M月d日"));
      String updateUserName = "";
      ALEipUser updateUser = ALEipUtils.getALEipUser(record.getUpdateUserId()
        .intValue());
      if (updateUser != null) {
        updateUserName = updateUser.getAliasName().getValue();
      }
      rd.setUpdateUser(updateUserName);
      rd.setUpdateDate(ALDateUtil.format(record.getUpdateDate(), "yyyy年M月d日"));
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  public ArrayList getFolderHierarchyList() {
    return folder_hierarchy_list;
  }

  public FolderInfo getSelectedFolderInfo() {
    return selected_folderinfo;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractSelectData#getColumnMap()
   */
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("file_title", EipTCabinetFile.FILE_TITLE_PROPERTY);
    map.putValue("file_name", EipTCabinetFile.FILE_NAME_PROPERTY);
    map.putValue("update_date", EipTCabinetFile.UPDATE_DATE_PROPERTY);
    map.putValue("file_size", EipTCabinetFile.FILE_SIZE_PROPERTY);
    return map;
  }

  /**
   *
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  /**
   * ファイル総数を取得する． <BR>
   *
   * @return
   */
  public int getFileSum() {
    return fileSum;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
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
