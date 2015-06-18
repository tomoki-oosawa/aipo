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
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolderMap;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 共有フォルダのフォルダフォームデータを管理するクラス <BR>
 * 
 */
public class CabinetFolderFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetFolderFormData.class.getName());

  /** フォルダ名 */
  private ALStringField folder_name;

  /** 親フォルダ ID */
  private ALNumberField parent_id;

  /** メモ */
  private ALStringField note;

  /** フォルダ情報一覧 */
  private List<FolderInfo> folder_hierarchy_list;

  /** 選択されたフォルダ情報 */
  private FolderInfo selected_folderinfo = null;

  /** 親フォルダ情報 */
  private FolderInfo parent_folderinfo = null;

  /** メンバー選択が必要かどうか */
  private boolean is_member;

  /** <code>memberList</code> メンバーリスト */
  private List<ALEipUser> memberList;

  /** <code>groups</code> グループ */
  private List<ALEipGroup> groups;

  /** アクセス権限フラグ */
  private ALNumberField access_flag;

  /** ログインユーザー */
  private ALEipUser login_user;

  private String folderid = null;

  private String orgId;

  /** アクセス制御のフォームを表示するか */
  private boolean show_acl_form = true;

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

    login_user = ALEipUtils.getALEipUser(rundata);

    String tmpfid =
      ALEipUtils.getTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID);
    int fid = CabinetUtils.ROOT_FODLER_ID;
    if (tmpfid != null && !"".equals(tmpfid)) {
      try {
        fid = Integer.parseInt(tmpfid);
      } catch (Exception e) {
        fid = CabinetUtils.ROOT_FODLER_ID;
      }
    } else {
      fid = CabinetUtils.ROOT_FODLER_ID;
    }
    FolderInfo rootFolderInfo = null;
    folder_hierarchy_list = CabinetUtils.getFolderList();
    if (folder_hierarchy_list != null) {
      if (folder_hierarchy_list.size() > 0) {
        int size = folder_hierarchy_list.size();
        for (int i = 0; i < size; i++) {
          FolderInfo info = folder_hierarchy_list.get(i);
          info.setVisible(true);
          if (info.getFolderId() == CabinetUtils.ROOT_FODLER_ID) {
            rootFolderInfo = info;
          }
          if (info.getFolderId() == fid) {
            selected_folderinfo = info;
          }
        }
      }
      CabinetUtils.setFolderVisibleForForm(folder_hierarchy_list, rundata);

      if (selected_folderinfo == null) {
        selected_folderinfo = rootFolderInfo;
      }
    }

    groups = ALEipUtils.getMyGroups(rundata);
    is_member = false;

    super.init(action, rundata, context);
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
      if (!rundata.getParameters().containsKey("parent_id")) {
        parent_id.setValue(selected_folderinfo.getFolderId());
      }

      if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        folderid =
          ALEipUtils.getTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID);
      }

      /** メンバーリストの処理 */
      int tmp_access_flag = (int) access_flag.getValue();
      if (tmp_access_flag == CabinetUtils.ACCESS_PUBLIC_MEMBER
        || tmp_access_flag == CabinetUtils.ACCESS_SECRET_MEMBER) {
        String member[] = rundata.getParameters().getStrings("member_to");
        SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
        Expression exp =
          ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, member);
        query.setQualifier(exp);
        memberList.addAll(ALEipUtils.getUsersFromSelectQuery(query));
        /** ログインユーザが含まれていなかった場合は追加 */
        boolean login_user_exists = false;
        for (int i = 0; i < memberList.size(); i++) {
          if (member[i].equals(login_user.getName().getValue())) {
            login_user_exists = true;
            break;
          }
        }
        if (!login_user_exists) {
          memberList.add(login_user);
        }
      } else {
        memberList.add(login_user);
      }

    }
    return res;
  }

  /**
   * 各フィールドを初期化します。 <BR>
   * 
   * 
   */
  @Override
  public void initField() {
    // フォルダ名
    folder_name = new ALStringField();
    folder_name
      .setFieldName(ALLocalizationUtils.getl10n("CABINET_FOLDER_NAME"));

    folder_name.setTrim(true);
    // 親フォルダ
    parent_id = new ALNumberField();
    parent_id
      .setFieldName(ALLocalizationUtils.getl10n("CABINET_ADD_POSISHION"));
    parent_id.setValue(0);
    // メモ
    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils.getl10n("CABINET_MEMO"));
    note.setTrim(true);
    // アクセス権限フラグ
    access_flag = new ALNumberField();
    access_flag.setFieldName(ALLocalizationUtils
      .getl10n("CABINET_ACCESS_RESTRICTION"));
    access_flag.setValue(0);
    // メンバーリスト
    memberList = new ArrayList<ALEipUser>();
  }

  /**
   * フォルダの各フィールドに対する制約条件を設定します。 <BR>
   * 
   * 
   */
  @Override
  protected void setValidator() {
    // フォルダ名必須項目
    folder_name.setNotNull(true);
    // フォルダ名の文字数制限
    folder_name.limitMaxLength(128);
    // 追加位置
    parent_id.limitMinValue(0);
    // メモの文字数制限
    note.limitMaxLength(1000);
    // アクセス権限フラグ
    access_flag.limitValue(0, 3);
  }

  /**
   * フォルダのフォームに入力されたデータの妥当性検証を行います。 <BR>
   * 
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   * 
   */
  @Override
  protected boolean validate(List<String> msgList) {
    // フォルダ名
    folder_name.validate(msgList);
    // 追加位置
    parent_id.validate(msgList);
    // メモ
    note.validate(msgList);
    // アクセス権限フラグ
    access_flag.validate(msgList);

    int tmp_acl_folder =
      CabinetUtils.getAccessControlFolderId((int) parent_id.getValue());
    if (tmp_acl_folder != CabinetUtils.ROOT_FODLER_ID
      && access_flag.getValue() != CabinetUtils.ACCESS_PUBLIC_ALL) {
      msgList.add(ALLocalizationUtils
        .getl10n("CABINET_DONNOT_ACCESS_NEED_TOP_LEVEL_AUTHORITY"));
    }

    if (existsFolderName()) {
      msgList.add(ALLocalizationUtils
        .getl10n("CABINET_SAME_NAME_FOLDER_CHANGE_NAME"));
    }

    return (msgList.size() == 0);
  }

  private boolean existsFolderName() {
    String fname = folder_name.getValue();
    if (fname == null || "".equals(fname)) {
      return false;
    }

    try {
      SelectQuery<EipTCabinetFolder> query =
        Database.query(EipTCabinetFolder.class);
      if (ALEipConstants.MODE_INSERT.equals(getMode())) {
        Expression exp =
          ExpressionFactory.matchExp(
            EipTCabinetFolder.PARENT_ID_PROPERTY,
            Integer.valueOf((int) parent_id.getValue()));
        query.setQualifier(exp);
      } else if (ALEipConstants.MODE_UPDATE.equals(getMode())) {
        Expression exp1 =
          ExpressionFactory.matchExp(
            EipTCabinetFolder.PARENT_ID_PROPERTY,
            Integer.valueOf((int) parent_id.getValue()));
        query.setQualifier(exp1);
        Expression exp2 =
          ExpressionFactory.noMatchDbExp(
            EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
            Integer.valueOf(folderid));
        query.andQualifier(exp2);
      }

      List<EipTCabinetFolder> list = query.fetchList();
      if (list != null && list.size() > 0) {
        EipTCabinetFolder tmpfolder = null;
        int size = list.size();
        for (int i = 0; i < size; i++) {
          tmpfolder = list.get(i);
          if (fname.equals(tmpfolder.getFolderName())) {
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
   * フォルダをデータベースから読み出します。 <BR>
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
      EipTCabinetFolder folder =
        CabinetUtils.getEipTCabinetFolder(rundata, context);
      if (folder == null) {
        return false;
      }

      // フォルダ名
      folder_name.setValue(folder.getFolderName());
      // メモ
      note.setValue(folder.getNote());
      // アクセス権限
      access_flag.setValue(folder.getPublicFlag());
      // メンバーリスト
      int int_flag = Integer.valueOf(folder.getPublicFlag());
      if (int_flag == CabinetUtils.ACCESS_PUBLIC_MEMBER
        || int_flag == CabinetUtils.ACCESS_SECRET_MEMBER) {
        is_member = true;
        // メンバーのリストを取得
        SelectQuery<EipTCabinetFolderMap> mapquery =
          Database.query(EipTCabinetFolderMap.class);
        Expression mapexp =
          ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY,
            folder.getFolderId());
        mapquery.setQualifier(mapexp);
        List<EipTCabinetFolderMap> list = mapquery.fetchList();

        List<Integer> users = new ArrayList<Integer>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
          EipTCabinetFolderMap map = list.get(i);
          users.add(map.getUserId());
        }
        SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
        Expression exp =
          ExpressionFactory.inDbExp(TurbineUser.USER_ID_PK_COLUMN, users);
        Expression nonDisabledexp =
          ExpressionFactory.noMatchExp(TurbineUser.DISABLED_PROPERTY, "T");

        query.setQualifier(exp.andExp(nonDisabledexp));

        memberList.addAll(ALEipUtils.getUsersFromSelectQuery(query));
      } else {
        is_member = false;
      }

      // フォルダ階層の表示切替
      int folder_id = selected_folderinfo.getFolderId();
      ArrayList<Integer> disable_list = new ArrayList<Integer>();
      disable_list.add(Integer.valueOf(folder_id));
      int size = folder_hierarchy_list.size();
      for (int i = 0; i < size; i++) {
        FolderInfo info = folder_hierarchy_list.get(i);
        boolean flg = false;
        int len = disable_list.size();
        for (int j = 0; j < len; j++) {
          Integer id = disable_list.get(j);
          if (info.getFolderId() == id.intValue()
            || info.getParentFolderId() == id.intValue()) {
            flg = true;
            break;
          }
        }
        if (flg) {
          info.setVisible(false);
          disable_list.add(Integer.valueOf(info.getFolderId()));
        } else {
          info.setVisible(true);
        }
      }

      CabinetUtils.setFolderVisibleForForm(folder_hierarchy_list, rundata);

      for (int i = 0; i < size; i++) {
        FolderInfo info = folder_hierarchy_list.get(i);
        if (info.getFolderId() == selected_folderinfo.getParentFolderId()) {
          // 現在編集中のフォルダの親フォルダを選択させる．
          selected_folderinfo = info;
        }
      }

      /** アクセス権限のフォームを表示するか */
      if (Integer.valueOf(folder.getPublicFlag()) == CabinetUtils.ACCESS_PUBLIC_ALL) {
        int acl_folder =
          CabinetUtils.getAccessControlFolderId(folder.getParentId());
        if (acl_folder == 1) {
          /** 上位フォルダをルートフォルダまで探しても、アクセス制限されているフォルダが発見されなかった */
          show_acl_form = true;
        } else {
          /** 上位フォルダでアクセス制限されている */
          show_acl_form = false;
        }
      } else {
        /** このフォルダーにアクセス権限が設定されているので、フォームを表示する */
        show_acl_form = true;
      }

    } catch (Exception ex) {
      logger.error("cabinet", ex);
      return false;
    }
    return true;
  }

  /**
   * フォルダをデータベースとファイルシステムから削除します。 <BR>
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
      String folderid =
        ALEipUtils.getTemp(rundata, context, CabinetUtils.KEY_FOLDER_ID);
      int delete_id = Integer.parseInt(folderid);
      if (delete_id <= CabinetUtils.ROOT_FODLER_ID) {
        // フォルダ『ルートフォルダ』は削除不可にする．
        return false;
      }
      int delete_parent_id = 0;

      // フォルダ情報を取得する．
      SelectQuery<EipTCabinetFolder> query =
        Database.query(EipTCabinetFolder.class);
      Expression exp1 =
        ExpressionFactory.matchDbExp(
          EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
          Integer.valueOf(folderid));
      Expression exp2 =
        ExpressionFactory.matchExp(
          EipTCabinetFolder.PARENT_ID_PROPERTY,
          Integer.valueOf(folderid));
      query.setQualifier(exp1.orExp(exp2));
      List<EipTCabinetFolder> list = query.fetchList();
      if (list == null || list.size() <= 0) {
        return false;
      }

      ArrayList<Integer> folderids = new ArrayList<Integer>();
      EipTCabinetFolder folder = null;
      int size = list.size();
      for (int i = 0; i < size; i++) {
        folder = list.get(i);
        folderids.add(folder.getFolderId());
        if (folder.getFolderId().intValue() == delete_id) {
          delete_parent_id = folder.getParentId().intValue();
        }
      }

      // データベースのフォルダ情報を削除する．
      SelectQuery<EipTCabinetFolder> delfolderquery =
        Database.query(EipTCabinetFolder.class);
      Expression delfolderexp =
        ExpressionFactory.inDbExp(
          EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
          folderids);
      delfolderquery.setQualifier(delfolderexp);
      List<EipTCabinetFolder> delFolderList = delfolderquery.fetchList();

      int delFolderListsize = delFolderList.size();

      // フォルダ情報を削除
      for (int i = 0; i < delFolderListsize; i++) {
        EipTCabinetFolder delfolder = delFolderList.get(i);

        // entityIdを取得
        Integer entityId = delfolder.getFolderId();
        // フォルダ名を取得
        String folderName = delfolder.getFolderName();

        // List cabinetfiles = delfolder.getEipTCabinetFiles();
        List<EipTCabinetFile> cabinetfiles =
          CabinetUtils.getEipTCabinetFileList(delfolder
            .getFolderId()
            .intValue());
        List<String> fpaths = new ArrayList<String>();
        if ((cabinetfiles != null) && (cabinetfiles.size() > 0)) {
          int tsize = cabinetfiles.size();
          for (int k = 0; k < tsize; k++) {
            fpaths.add((cabinetfiles.get(k)).getFilePath());
          }
        }

        // フォルダ情報を削除
        Database.delete(delfolder);
        Database.commit();

        // ログに保存
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          entityId,
          ALEventlogConstants.PORTLET_TYPE_CABINET_FOLDER,
          folderName);

        if (fpaths.size() > 0) {
          // ローカルファイルに保存されているファイルを削除する．
          int fsize = fpaths.size();
          for (int k = 0; k < fsize; k++) {
            ALStorageService.deleteFile(CabinetUtils.getSaveDirPath(orgId)
              + fpaths.get(k));
          }
        }
      }

      // 論理フォルダ階層をリロードする．
      folder_hierarchy_list = CabinetUtils.getFolderList();
      CabinetUtils.setFolderVisibleForForm(folder_hierarchy_list, rundata);
      selected_folderinfo =
        CabinetUtils.getSelectedFolderInfo(
          folder_hierarchy_list,
          delete_parent_id);
    } catch (Exception ex) {
      Database.rollback();
      logger.error("cabinet", ex);
      return false;
    }
    return true;
  }

  /**
   * フォルダをデータベースとファイルシステムに格納します。 <BR>
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

      // 新規オブジェクトモデル
      EipTCabinetFolder folder = Database.create(EipTCabinetFolder.class);
      // 親フォルダ ID
      folder.setParentId(Integer.valueOf((int) parent_id.getValue()));
      // フォルダ名
      folder.setFolderName(folder_name.getValue());
      // メモ
      folder.setNote(note.getValue());
      // 作成者
      folder.setCreateUserId(Integer.valueOf(uid));
      // 更新者
      folder.setUpdateUserId(Integer.valueOf(uid));
      // 作成日
      folder.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      folder.setUpdateDate(Calendar.getInstance().getTime());

      // アクセス権限
      int accessFlag = (int) access_flag.getValue();
      folder.setPublicFlag(String.valueOf(accessFlag));

      for (int i = 0; i < memberList.size(); i++) {
        EipTCabinetFolderMap map = Database.create(EipTCabinetFolderMap.class);
        ALEipUser user = memberList.get(i);
        int user_id = (int) user.getUserId().getValue();

        map.setEipTCabinetFolder(folder);
        map.setUserId(Integer.valueOf(user_id));
      }

      // フォルダを登録
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        folder.getFolderId(),
        ALEventlogConstants.PORTLET_TYPE_CABINET_FOLDER,
        folder_name.getValue());

      int size = folder_hierarchy_list.size();
      for (int i = 0; i < size; i++) {
        FolderInfo info = folder_hierarchy_list.get(i);
        if (info.getFolderId() == parent_id.getValue()) {
          parent_folderinfo = info;
          break;
        }
      }

      selected_folderinfo.setFolderId(folder.getFolderId().intValue());
      selected_folderinfo.setFolderName(folder.getFolderName());
      selected_folderinfo.setHierarchyIndex(parent_folderinfo
        .getHierarchyIndex() + 1);
      selected_folderinfo.setParentFolderId(parent_folderinfo.getFolderId());

      res = true;
    } catch (Exception ex) {
      Database.rollback();
      logger.error("cabinet", ex);
      return false;
    }
    return res;
  }

  /**
   * データベースとファイルシステムに格納されているフォルダを更新します。 <BR>
   * 
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTCabinetFolder folder =
        CabinetUtils.getEipTCabinetFolder(rundata, context);
      if (folder == null) {
        return false;
      }

      int uid = ALEipUtils.getUserId(rundata);

      // フォルダ名
      folder.setFolderName(folder_name.getValue());
      // 親フォルダ ID
      folder.setParentId(Integer.valueOf((int) parent_id.getValue()));
      // メモ
      folder.setNote(note.getValue());
      // 更新者
      folder.setUpdateUserId(Integer.valueOf(uid));
      // 更新日
      folder.setUpdateDate(Calendar.getInstance().getTime());

      // アクセス権限
      // 一度、既存のマップを全て削除する
      SelectQuery<EipTCabinetFolderMap> mapquery =
        Database.query(EipTCabinetFolderMap.class);
      Expression mapexp =
        ExpressionFactory.matchExp(
          EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY,
          folder.getFolderId());
      mapquery.setQualifier(mapexp);
      List<EipTCabinetFolderMap> maplist = mapquery.fetchList();
      Database.deleteAll(maplist);

      // マップを登録しなおす
      int accessFlag = (int) access_flag.getValue();
      folder.setPublicFlag(String.valueOf(accessFlag));
      for (int i = 0; i < memberList.size(); i++) {
        EipTCabinetFolderMap map = Database.create(EipTCabinetFolderMap.class);
        ALEipUser user = memberList.get(i);
        int user_id = (int) user.getUserId().getValue();

        map.setEipTCabinetFolder(folder);
        map.setUserId(Integer.valueOf(user_id));
      }

      if (accessFlag != CabinetUtils.ACCESS_PUBLIC_ALL) {
        /** 子フォルダのアクセス権限を全てACCESS_PUBLIC_ALLに設定 */
        List<EipTCabinetFolder> children = CabinetUtils.getChildFolders(folder);
        for (int i = 0; i < children.size(); i++) {
          EipTCabinetFolder child = children.get(i);
          child.setPublicFlag(String.valueOf(CabinetUtils.ACCESS_PUBLIC_ALL));
        }
      }

      // フォルダを更新
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        folder.getFolderId(),
        ALEventlogConstants.PORTLET_TYPE_CABINET_FOLDER,
        folder_name.getValue());
    } catch (Exception ex) {
      Database.rollback();
      logger.error("cabinet", ex);
      return false;
    }
    return true;
  }

  /**
   * フォルダ名を取得する． <BR>
   * 
   * @return
   */
  public ALStringField getFolderName() {
    return folder_name;
  }

  public ALNumberField getParentId() {
    return parent_id;
  }

  public ALNumberField getAccessFlag() {
    return access_flag;
  }

  public List<ALEipGroup> getGroupList() {
    return groups;
  }

  public boolean isMember() {
    return (is_member && memberList.size() > 0);
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

  public boolean showAclForm() {
    return show_acl_form;
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
