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

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolderMap;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダのファイル検索データを管理するためのクラスです。 <br />
 */
public class CabinetSelectData extends
    ALAbstractSelectData<EipTCabinetFile, EipTCabinetFile> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetSelectData.class.getName());

  /** 選択されたフォルダ情報 */
  private FolderInfo selected_folderinfo = null;

  private List<FolderInfo> folder_hierarchy_list;

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

  private ALStringField target_keyword;

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
        .getInitParameter("p1c-sort"));
      logger.debug("[CabinetSelectData] Init Parameter. : "
        + ALEipUtils
          .getPortlet(rundata, context)
          .getPortletConfig()
          .getInitParameter("p1c-sort"));
    }

    int fid = 0;
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

    this.rundata = rundata;
    target_keyword = new ALStringField();

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
  protected ResultList<EipTCabinetFile> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    try {
      if (CabinetUtils.hasResetFlag(rundata, context)) {
        CabinetUtils.resetFilter(rundata, context, this.getClass().getName());
        target_keyword.setValue("");
      } else {
        target_keyword
          .setValue(CabinetUtils.getTargetKeyword(rundata, context));
      }
      CabinetUtils.setFolderVisible(
        folder_hierarchy_list,
        selected_folderinfo,
        rundata);

      SelectQuery<EipTCabinetFile> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTCabinetFile> list = query.getResultList();
      // ファイル総数をセットする．
      if (list == null) {
        return new ResultList<EipTCabinetFile>(new ArrayList<EipTCabinetFile>());
      } else {
        fileSum = list.size();
      }
      return list;
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
    SelectQuery<EipTCabinetFile> query = Database.query(EipTCabinetFile.class);
    if (selected_folderinfo != null) {
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
          Integer.valueOf(selected_folderinfo.getFolderId()));

      query.setQualifier(exp);
    } else {
      // アクセス制御
      Expression exp01 =
        ExpressionFactory.matchExp(EipTCabinetFile.EIP_TCABINET_FOLDER_PROPERTY
          + "."
          + EipTCabinetFolder.PUBLIC_FLAG_PROPERTY, Integer
          .valueOf(CabinetUtils.ACCESS_PUBLIC_ALL));

      Expression exp02 =
        ExpressionFactory.matchExp(EipTCabinetFile.EIP_TCABINET_FOLDER_PROPERTY
          + "."
          + EipTCabinetFolder.PUBLIC_FLAG_PROPERTY, Integer
          .valueOf(CabinetUtils.ACCESS_PUBLIC_MEMBER));

      Expression exp11 =
        ExpressionFactory.matchExp(EipTCabinetFile.EIP_TCABINET_FOLDER_PROPERTY
          + "."
          + EipTCabinetFolder.PUBLIC_FLAG_PROPERTY, Integer
          .valueOf(CabinetUtils.ACCESS_SECRET_MEMBER));

      Expression exp12 =
        ExpressionFactory.matchExp(EipTCabinetFile.EIP_TCABINET_FOLDER_PROPERTY
          + "."
          + EipTCabinetFolder.PUBLIC_FLAG_PROPERTY, Integer
          .valueOf(CabinetUtils.ACCESS_SECRET_SELF));

      Expression exp13 =
        ExpressionFactory.matchExp(EipTCabinetFile.EIP_TCABINET_FOLDER_PROPERTY
          + "."
          + EipTCabinetFolder.EIP_TCABINET_FOLDER_MAP_PROPERTY
          + "."
          + EipTCabinetFolderMap.USER_ID_PROPERTY, Integer.valueOf(ALEipUtils
          .getUserId(rundata)));

      Expression publicExp = exp01.orExp(exp02);
      Expression privateExp = (exp11.andExp(exp13)).orExp(exp12.andExp(exp13));
      query.setQualifier(publicExp).orQualifier(privateExp);

    }
    if ((target_keyword != null) && (!target_keyword.getValue().equals(""))) {
      // 選択したキーワードを指定する．
      String keyword = "%" + target_keyword.getValue() + "%";
      Expression target_exp1 =
        ExpressionFactory.likeExp(EipTCabinetFile.FILE_NAME_PROPERTY, keyword);
      Expression target_exp2 =
        ExpressionFactory.likeExp(EipTCabinetFile.FILE_TITLE_PROPERTY, keyword);
      Expression target_exp3 =
        ExpressionFactory.likeExp(EipTCabinetFile.NOTE_PROPERTY, keyword);
      query.andQualifier(target_exp1.orExp(target_exp2.orExp(target_exp3)));
    }

    query.distinct(true);

    return buildSelectQueryForFilter(query, rundata, context);
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
  protected EipTCabinetFile selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return CabinetUtils.getEipTCabinetFile(rundata, context);
  }

  /**
   *
   */
  @Override
  protected Object getResultData(EipTCabinetFile record)
      throws ALPageNotFoundException, ALDBErrorException {
    try {

      CabinetFileResultData rd = new CabinetFileResultData();
      rd.initField();

      rd.setFileId(record.getFileId());
      rd.setFileTitle(record.getFileTitle());
      rd.setFileName(record.getFileName());
      rd.setFileSize(record.getFileSize());
      rd.setFolderName(ALCommonUtils.compressString(record
        .getEipTCabinetFolder()
        .getFolderName(), getStrLength()));
      rd.setCounter(record.getCounter());

      String updateUserName = "";
      ALEipUser updateUser = ALEipUtils.getALEipUser(record.getUpdateUserId());
      if (updateUser != null) {
        updateUserName = updateUser.getAliasName().getValue();
      }
      rd.setUpdateUser(updateUserName);
      rd.setUpdateDate(record.getUpdateDate());
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * 
   * @param obj
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(EipTCabinetFile obj)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      EipTCabinetFile record = obj;

      CabinetFileResultData rd = new CabinetFileResultData();
      rd.initField();
      rd.setFileId(record.getFileId().longValue());
      rd.setFileTitle(record.getFileTitle());
      rd.setFileName(record.getFileName());
      rd.setFileSize(record.getFileSize().longValue());
      rd.setCounter(record.getCounter());
      rd.setPosition(CabinetUtils.getFolderPosition(
        folder_hierarchy_list,
        record.getFolderId().intValue()));
      rd.setNote(record.getNote());
      rd.setisEditable((CabinetUtils.isEditableFolder(
        record.getFolderId(),
        rundata)));

      String createUserName = "";
      ALEipUser createUser =
        ALEipUtils.getALEipUser(record.getCreateUserId().intValue());
      if (createUser != null) {
        createUserName = createUser.getAliasName().getValue();
      }
      rd.setCreateUser(createUserName);
      rd.setCreateDate(new SimpleDateFormat("yyyy年M月d日").format(record
        .getCreateDate()));
      String updateUserName = "";
      ALEipUser updateUser =
        ALEipUtils.getALEipUser(record.getUpdateUserId().intValue());
      if (updateUser != null) {
        updateUserName = updateUser.getAliasName().getValue();
      }
      rd.setUpdateUser(updateUserName);
      rd.setUpdateDate(record.getUpdateDate());
      return rd;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  public List<FolderInfo> getFolderHierarchyList() {
    return folder_hierarchy_list;
  }

  public FolderInfo getSelectedFolderInfo() {
    return selected_folderinfo;
  }

  /**
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("file_title", EipTCabinetFile.FILE_TITLE_PROPERTY);
    map.putValue("file_name", EipTCabinetFile.FILE_NAME_PROPERTY);
    map.putValue("update_date", EipTCabinetFile.UPDATE_DATE_PROPERTY);
    map.putValue("file_size", EipTCabinetFile.FILE_SIZE_PROPERTY);
    map.putValue("counter", EipTCabinetFile.COUNTER_PROPERTY);
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

  public ALStringField getTargetKeyword() {
    return target_keyword;
  }
}
