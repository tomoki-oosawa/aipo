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
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダのファイル検索用データ．
 *
 */
public class CabinetFileWordSelectData extends
    ALAbstractSelectData<EipTCabinetFile, EipTCabinetFile> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetFileWordSelectData.class.getName());

  /** 検索ワード */
  private ALStringField searchWord;

  /** 検索結果件数 */
  private int searchSum = 0;

  /** 選択されたフォルダ情報 */
  private FolderInfo selected_folderinfo = null;

  private List<FolderInfo> folder_hierarchy_list;

  private RunData rundata;

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
      ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "folder_name");
    }

    int fid = CabinetUtils.ROOT_FODLER_ID;
    String tmpfid =
      ALEipUtils.getTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID);
    if (tmpfid != null && !"".equals(tmpfid)) {
      try {
        fid = Integer.parseInt(tmpfid);
      } catch (Exception e) {
        fid = CabinetUtils.ROOT_FODLER_ID;
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
      if (selected_folderinfo == null) {
        selected_folderinfo = folder_hierarchy_list.get(0);
      }
    }
    this.rundata = rundata;

    super.init(action, rundata, context);
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected ResultList<EipTCabinetFile> selectList(RunData rundata,
      Context context) {
    // ページャからきた場合に検索ワードをセッションへ格納する
    if (!rundata.getParameters().containsKey(ALEipConstants.LIST_START)
      && !rundata.getParameters().containsKey(ALEipConstants.LIST_SORT)
      && rundata.getParameters().containsKey("sword")) {
      ALEipUtils.setTemp(rundata, context, "CabinetFileWord", rundata
        .getParameters()
        .getString("sword"));
    }

    // 検索ワードの設定
    searchWord = new ALStringField();
    searchWord.setTrim(true);
    // セッションから値を取得する。
    // 検索ワード未指定時は空文字が入力される
    searchWord
      .setValue(ALEipUtils.getTemp(rundata, context, "CabinetFileWord"));

    try {
      CabinetUtils.setFolderVisible(
        folder_hierarchy_list,
        selected_folderinfo,
        rundata);

      SelectQuery<EipTCabinetFile> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTCabinetFile> list = query.getResultList();
      // 総数をセットする．
      if (list != null) {
        searchSum = list.getTotalCount();
        return list;
      } else {
        logger.info("cabinetFile search > result is null");
        return new ResultList<EipTCabinetFile>();
      }
    } catch (Exception ex) {
      logger.error("cabinet", ex);
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
  protected EipTCabinetFile selectDetail(RunData rundata, Context context) {
    return null;
  }

  /**
   *
   */
  @Override
  protected Object getResultData(EipTCabinetFile record) {
    try {

      CabinetFileResultData rd = new CabinetFileResultData();
      rd.initField();
      rd.setFileId(record.getFileId().longValue());
      rd.setFileTitle(record.getFileTitle());
      rd.setFileName(record.getFileName());
      rd.setFileSize(record.getFileSize().longValue());
      rd.setCounter(record.getCounter());

      rd.setFolderId(record.getEipTCabinetFolder().getFolderId().intValue());
      rd.setFolderName((record.getEipTCabinetFolder()).getFolderName());
      rd.setisEditable(CabinetUtils.isEditableFolder(record
        .getEipTCabinetFolder()
        .getFolderId()
        .intValue(), rundata));

      String updateUserName = "";
      ALEipUser updateUser =
        ALEipUtils.getALEipUser(record.getUpdateUserId().intValue());
      if (updateUser != null) {
        updateUserName = updateUser.getAliasName().getValue();
      }
      rd.setUpdateUser(updateUserName);
      rd.setUpdateUserId(record.getUpdateUserId());
      rd.setUpdateDate(record.getUpdateDate());
      return rd;
    } catch (Exception ex) {
      logger.error("cabinet", ex);
      return null;
    }
  }

  /**
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTCabinetFile obj) {
    return null;
  }

  /**
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("folder_name", EipTCabinetFile.EIP_TCABINET_FOLDER_PROPERTY
      + "."
      + EipTCabinetFolder.FOLDER_NAME_PROPERTY);
    map.putValue("file_title", EipTCabinetFile.FILE_TITLE_PROPERTY);
    map.putValue("file_name", EipTCabinetFile.FILE_NAME_PROPERTY);
    // map.putValue("update_user", TurbineUserConstants.LAST_NAME_KANA);
    map.putValue("update_date", EipTCabinetFile.UPDATE_DATE_PROPERTY);
    map.putValue("file_size", EipTCabinetFile.FILE_SIZE_PROPERTY);
    map.putValue("counter", EipTCabinetFile.COUNTER_PROPERTY);
    return map;
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

    String word = searchWord.getValue();

    Expression exp11 =
      ExpressionFactory.likeExp(EipTCabinetFile.FILE_TITLE_PROPERTY, "%"
        + word
        + "%");
    Expression exp12 =
      ExpressionFactory.likeExp(EipTCabinetFile.FILE_NAME_PROPERTY, "%"
        + word
        + "%");
    Expression exp13 =
      ExpressionFactory
        .likeExp(EipTCabinetFile.NOTE_PROPERTY, "%" + word + "%");
    Expression exp14 =
      ExpressionFactory.inExp(EipTCabinetFile.FOLDER_ID_PROPERTY, CabinetUtils
        .getAuthorizedVisibleFolderIds(rundata));

    query.setQualifier(exp11.orExp(exp12).orExp(exp13));
    query.andQualifier(exp14);

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * 検索ワードを取得します。
   *
   * @return
   */
  public ALStringField getSearchWord() {
    return searchWord;
  }

  public int getSearchSum() {
    return searchSum;
  }

  public List<FolderInfo> getFolderHierarchyList() {
    return folder_hierarchy_list;
  }

  public FolderInfo getSelectedFolderInfo() {
    return selected_folderinfo;
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

}
