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
import java.util.Calendar;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cabinet.util.CabinetUtils;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 共有フォルダのファイルフォームデータを管理するクラス <BR>
 * 
 */
public class CabinetFileFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetFileFormData.class.getName());

  /** フォルダ ID */
  private ALNumberField folder_id;

  /** ファイルタイトル */
  private ALStringField file_title;

  /** ファイル名 */
  private ALStringField file_name;

  /** ファイルサイズ */
  private ALNumberField file_size;

  /** メモ */
  private ALStringField note;

  /** フォルダ情報一覧 */
  private List<FolderInfo> folder_hierarchy_list;

  /** 選択されたフォルダ情報 */
  private FolderInfo selected_folderinfo = null;

  /** 添付フォルダ名 */
  private String folderName = null;

  private String fileid = null;

  private String orgId = null;

  /** 添付ファイルリスト */
  private List<FileuploadLiteBean> fileuploadList = null;

  /** Validate用(添付ファイルID) */
  private String[] fileids;

  private RunData rundata;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   * 
   * 
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

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

    orgId = Database.getDomainName();
    this.rundata = rundata;

    String tmpfid =
      ALEipUtils.getTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID);
    int fid = CabinetUtils.ROOT_FODLER_ID;
    if (tmpfid != null && !"".equals(tmpfid)) {
      try {
        fid = Integer.parseInt(tmpfid);
      } catch (Exception e) {
        fid = CabinetUtils.ROOT_FODLER_ID;
      }
    }

    folder_hierarchy_list = CabinetUtils.getFolderList();
    CabinetUtils.setFolderVisibleForForm(folder_hierarchy_list, rundata);

    int size = folder_hierarchy_list.size();
    for (int i = 0; i < size; i++) {
      FolderInfo info = folder_hierarchy_list.get(i);
      if (info.getFolderId() == fid) {
        selected_folderinfo = info;
      }
    }

    folderName = rundata.getParameters().getString("folderName");

    fileids = rundata.getParameters().getStrings("attachments");
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // フォルダ ID
    folder_id = new ALNumberField();
    folder_id.setFieldName("フォルダ");
    folder_id.setNotNull(true);
    folder_id.setValue(0);
    // ファイルタイトル
    file_title = new ALStringField();
    file_title.setFieldName("タイトル");
    file_title.setTrim(true);
    // フォルダ名
    file_name = new ALStringField();
    file_name.setFieldName("ファイル名");
    file_name.setTrim(true);
    // ファイルサイズ
    file_size = new ALNumberField();
    file_size.setFieldName("ファイルサイズ");
    // メモ
    note = new ALStringField();
    note.setFieldName("メモ");
    note.setTrim(true);

    fileuploadList = new ArrayList<FileuploadLiteBean>();
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

    if (res) {
      if (!rundata.getParameters().containsKey(CabinetUtils.KEY_FOLDER_ID)) {
        try {
          folder_id.setValue(selected_folderinfo.getFolderId());
        } catch (Exception e) {
          folder_id.setValue(CabinetUtils.ROOT_FODLER_ID);
        }
      }

      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        fileid = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      }
    }

    try {
      fileuploadList = FileuploadUtils.getFileuploadList(rundata);
      FileuploadLiteBean filebean = null;
      if (fileuploadList != null) {
        // 新規にアップロードしたデータをつめる
        filebean = fileuploadList.get(0);
        file_name.setValue(filebean.getFileName());
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
    }

    return res;
  }

  /**
   * ファイルの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // ファイルタイトル必須項目
    file_title.setNotNull(true);
    // ファイルタイトルの文字数制限
    file_title.limitMaxLength(128);
    // ファイル名必須項目
    file_name.setNotNull(true);
    // ファイル名の文字数制限
    file_name.limitMaxLength(128);
    // メモの文字数制限
    note.limitMaxLength(1000);
  }

  /**
   * ファイルのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // ファイルタイトル
    file_title.validate(msgList);
    // ファイル名
    if (ALEipConstants.MODE_INSERT.equals(getMode())) {
      file_name.validate(msgList);
    } else {
      if (fileuploadList != null) {
        if (fileuploadList.size() > 0) {
          file_name.validate(msgList);
        }
      }
    }

    if (fileids != null) {
      if (fileids[0].equals("")) {
        msgList.add("『 <span class='em'>ファイル名</span> 』を入力してください。");
      }
    }

    // メモ
    note.validate(msgList);
    if (existsFileName()) {
      msgList.add("このファイル名と同じファイルがすでに存在するため、登録できません。ファイル名を変更してください。");
    }

    /** 編集アクセス制限 */
    if (!CabinetUtils.isEditableFolder((int) folder_id.getValue(), rundata)) {
      msgList.add("このフォルダを編集する権限がありません。");
    }
    return (msgList.size() == 0);
  }

  private boolean existsFileName() {
    String fname = file_name.getValue();
    if (fname == null || "".equals(fname)) {
      return false;
    }

    try {
      SelectQuery<EipTCabinetFile> query =
        Database.query(EipTCabinetFile.class);
      query.select(EipTCabinetFile.FILE_NAME_COLUMN);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp =
          ExpressionFactory.matchDbExp(
            EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
            Integer.valueOf((int) folder_id.getValue()));
        query.setQualifier(exp);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchDbExp(
            EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
            Integer.valueOf((int) folder_id.getValue()));
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(
            EipTCabinetFile.FILE_ID_PK_COLUMN,
            Integer.valueOf(fileid));
        query.andQualifier(exp2);
      }

      List<EipTCabinetFile> list = query.fetchList();
      if (list != null && list.size() > 0) {
        for (EipTCabinetFile record : list) {
          if (fname.equals(record.getFileName())) {
            return true;
          }
        }
      }
    } catch (Exception e) {
      return true;
    }

    return false;
  }

  /**
   * ファイルをデータベースから読み出します。 <BR>
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
      EipTCabinetFile file = CabinetUtils.getEipTCabinetFile(rundata, context);
      if (file == null) {
        return false;
      }

      // 親フォルダ
      folder_id.setValue(file.getFolderId().intValue());
      // ファイルタイトル
      file_title.setValue(file.getFileTitle());
      // ファイル名
      file_name.setValue(file.getFileName());
      // ファイルサイズ
      file_size.setValue(file.getFileSize().longValue());
      // メモ
      note.setValue(file.getNote());
      // フォルダ名
      folderName = Integer.toString(file.getFolderId());

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * ファイルをデータベースとファイルシステムから削除します。 <BR>
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
      // オブジェクトモデルを取得
      EipTCabinetFile file = CabinetUtils.getEipTCabinetFile(rundata, context);
      if (file == null) {
        return false;
      }

      // ファイルのパス
      String local_file_pass = file.getFilePath();

      // entityIdの取得
      int entityId = file.getFileId();
      // todo名の取得
      String fileName = file.getFileTitle();

      // ファイルを削除
      Database.delete(file);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_CABINET_FILE,
        fileName);

      if (local_file_pass != null && !"".equals(local_file_pass)) {
        // ローカルファイルに保存されているファイルを削除する．
        ALStorageService.deleteFile(CabinetUtils.getSaveDirPath(orgId)
          + local_file_pass);
      }

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * ファイルをデータベースとファイルシステムに格納します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    boolean res = false;
    try {
      int uid = ALEipUtils.getUserId(rundata);
      FileuploadLiteBean filebean = fileuploadList.get(0);
      // ファイルの移動先
      String filename = "0_" + String.valueOf(System.nanoTime());

      // ファイルの移動
      ALStorageService.copyTmpFile(
        uid,
        folderName,
        String.valueOf(filebean.getFileId()),
        CabinetUtils.FOLDER_FILEDIR_CABINET,
        CabinetUtils.CATEGORY_KEY,
        filename);

      double fileSize =
        ALStorageService.getFileSize(
          CabinetUtils.FOLDER_FILEDIR_CABINET,
          CabinetUtils.CATEGORY_KEY,
          filename);

      EipTCabinetFolder folder =
        Database.get(EipTCabinetFolder.class, Integer.valueOf((int) folder_id
          .getValue()));

      // 新規オブジェクトモデル
      EipTCabinetFile file = Database.create(EipTCabinetFile.class);
      // 親フォルダ
      file.setEipTCabinetFolder(folder);
      // ファイルタイトル
      file.setFileTitle(file_title.getValue());
      // ファイル名
      file.setFileName(file_name.getValue());
      // ファイルサイズ
      file.setFileSize(Long.valueOf((long) fileSize));
      // ファイルパス
      file.setFilePath(CabinetUtils.getRelativePath(filename));
      // ファイル
      // file.setFile(fileData);
      // メモ
      file.setNote(note.getValue());
      // 作成者
      file.setCreateUserId(Integer.valueOf(uid));
      // 更新者
      file.setUpdateUserId(Integer.valueOf(uid));
      // 作成日
      file.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      file.setUpdateDate(Calendar.getInstance().getTime());
      // フォルダを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        file.getFileId(),
        ALEventlogConstants.PORTLET_TYPE_CABINET_FILE,
        file_title.getValue());

      // 添付ファイル保存先のフォルダを削除
      ALStorageService.deleteTmpFolder(uid, folderName);

      res = true;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return res;
  }

  /**
   * データベースとファイルシステムに格納されているファイルを更新します。 <BR>
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
      int uid = ALEipUtils.getUserId(rundata);

      // オブジェクトモデルを取得
      EipTCabinetFile file = CabinetUtils.getEipTCabinetFile(rundata, context);
      if (file == null) {
        return false;
      }

      // アップロードしたファイルは存在するか
      boolean is_upload = true;
      if (fileuploadList == null) {
        is_upload = false;
      }
      if (is_upload) {
        // アップロードが確認できた場合
        FileuploadLiteBean filebean = fileuploadList.get(0);
        String filename = "0_" + String.valueOf(System.nanoTime());

        // ファイルの移動
        ALStorageService.copyTmpFile(
          uid,
          folderName,
          String.valueOf(filebean.getFileId()),
          CabinetUtils.FOLDER_FILEDIR_CABINET,
          CabinetUtils.CATEGORY_KEY,
          filename);

        // ファイル名
        file.setFileName(file_name.getValue());

        // ファイルサイズ
        double fileSize =
          ALStorageService.getFileSize(
            CabinetUtils.FOLDER_FILEDIR_CABINET,
            CabinetUtils.CATEGORY_KEY,
            filename);
        // double fileSize = Math.ceil(destFile.length() / 1024.0);
        file.setFileSize(Long.valueOf((long) fileSize));

        // ファイルパス
        file.setFilePath(CabinetUtils.getRelativePath(filename));
        // 作成者
        file.setCreateUserId(Integer.valueOf(uid));
        // 作成日
        file.setCreateDate(Calendar.getInstance().getTime());
      }

      // 親フォルダ
      EipTCabinetFolder folder =
        Database.get(EipTCabinetFolder.class, Integer.valueOf((int) folder_id
          .getValue()));
      file.setEipTCabinetFolder(folder);

      // ファイルタイトル
      file.setFileTitle(file_title.getValue());
      // メモ
      file.setNote(note.getValue());
      // 更新者
      file.setUpdateUserId(Integer.valueOf(uid));
      // 更新日
      file.setUpdateDate(Calendar.getInstance().getTime());
      // フォルダを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        file.getFileId(),
        ALEventlogConstants.PORTLET_TYPE_CABINET_FILE,
        file_title.getValue());

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  public String getFolderName() {
    return folderName.toString();
  }

  /**
   * 
   * 
   * @return
   */
  public ALNumberField getFolderId() {
    return folder_id;
  }

  /**
   * ファイルタイトルを取得する． <BR>
   * 
   * @return
   */
  public ALStringField getFileTitle() {
    return file_title;
  }

  /**
   * ファイル名を取得する． <BR>
   * 
   * @return
   */
  public ALStringField getFileName() {
    return file_name;
  }

  /**
   * ファイルサイズを取得する． <BR>
   * 
   * @return
   */
  public ALNumberField getFileSize() {
    return file_size;
  }

  /**
   * メモを取得する． <BR>
   * 
   * @return
   */
  public ALStringField getNote() {
    return note;
  }

  public List<FolderInfo> getFolderHierarchyList() {
    return folder_hierarchy_list;
  }

  public FolderInfo getSelectedFolderInfo() {
    return selected_folderinfo;
  }

  /**
   * 
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  public List<FileuploadLiteBean> getAttachmentFileNameList() {
    return fileuploadList;
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
