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
package com.aimluck.eip.cabinet.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cabinet.FolderInfo;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolderMap;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.orgutils.ALOrgUtilsFactoryService;
import com.aimluck.eip.util.orgutils.ALOrgUtilsHandler;

/**
 * キャビネットのユーティリティクラスです。 <BR>
 *
 */
public class CabinetUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(CabinetUtils.class.getName());

  /** フォルダ『ルートフォルダ』の予約 ID */
  public static final int ROOT_FODLER_ID = 1;

  /** セッションの識別子 */
  public static final String KEY_FOLDER_ID = "folder_id";

  /** 共有ファイルを保管するディレクトリのカテゴリキーの指定 */
  protected static final String CATEGORY_KEY = JetspeedResources.getString(
      "aipo.cabinet.categorykey", "");

  /** 共有ファイルを保管するディレクトリの指定 */
  private static final String FOLDER_FILEDIR_CABINET = JetspeedResources
      .getString("aipo.filedir", "");

  /** 全てのユーザーが閲覧／追加／編集／削除可 */
  public static final int ACCESS_PUBLIC_ALL = 0;

  /** 全てのユーザーが閲覧可。ただし追加／編集／削除できるのは所属メンバーのみ。 */
  public static final int ACCESS_PUBLIC_MEMBER = 1;

  /** 所属メンバーのみ閲覧／追加／編集／削除可 */
  public static final int ACCESS_SECRET_MEMBER = 2;

  /** 自分のみ閲覧／追加／編集／削除可 */
  public static final int ACCESS_SECRET_SELF = 3;

  /** 全てのユーザーが閲覧／追加／編集／削除可 */
  public static final String PUBLIC_FLAG_PUBLIC_ALL = "P";

  /** 全てのユーザーが閲覧可。ただし追加／編集／削除できるのは所属メンバーのみ。 */
  public static final String PUBLIC_FLAG_PUBLIC_MEMBER = "E";

  /** 所属メンバーのみ閲覧／追加／編集／削除可 */
  public static final String PUBLIC_FLAG_SECRET_MEMBER = "S";

  /** 自分のみ閲覧／追加／編集／削除可 */
  public static final String PUBLIC_FLAG_SECRET_SELF = "O";

  /**
   * フォルダオブジェクトモデルを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static EipTCabinetFolder getEipTCabinetFolder(RunData rundata,
      Context context) {
    String folderid = ALEipUtils.getTemp(rundata, context, KEY_FOLDER_ID);

    try {
      if (folderid == null || Integer.valueOf(folderid) == null) {
        // ファイル IDが空の場合
        logger.debug("[Cabinet Folder] Empty ID...");
        return null;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTCabinetFolder.class);
      Expression exp = ExpressionFactory.matchDbExp(
          EipTCabinetFolder.FOLDER_ID_PK_COLUMN, folderid);
      query.setQualifier(exp);
      List folders = dataContext.performQuery(query);
      if (folders == null || folders.size() == 0) {
        // 指定したフォルダ IDのレコードが見つからない場合
        logger.debug("[Cabinet Folder] Not found ID...");
        return null;
      }
      return ((EipTCabinetFolder) folders.get(0));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ファイルオブジェクトモデルを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static EipTCabinetFile getEipTCabinetFile(RunData rundata,
      Context context) {
    String fileid = ALEipUtils.getTemp(rundata, context,
        ALEipConstants.ENTITY_ID);
    try {
      if (fileid == null || Integer.valueOf(fileid) == null) {
        // ファイル IDが空の場合
        logger.debug("[Cabinet File] Empty ID...");
        return null;
      }

      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTCabinetFile.class);
      Expression exp = ExpressionFactory.matchDbExp(
          EipTCabinetFile.FILE_ID_PK_COLUMN, fileid);
      query.setQualifier(exp);
      List files = dataContext.performQuery(query);
      if (files == null || files.size() == 0) {
        // 指定したファイル IDのレコードが見つからない場合
        logger.debug("[Cabinet File] Not found ID...");
        return null;
      }

      return ((EipTCabinetFile) files.get(0));
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  /**
   * ファイルオブジェクトモデルを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static List getEipTCabinetFileList(int folderId) {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTCabinetFile.class);
      Expression exp = ExpressionFactory.matchExp(
          EipTCabinetFile.FOLDER_ID_PROPERTY, Integer.valueOf(folderId));
      query.setQualifier(exp);
      List files = dataContext.performQuery(query);
      if (files == null || files.size() == 0) {
        return null;
      }

      return files;
    } catch (Exception ex) {
      logger.error("Exception", ex);
      return null;
    }
  }

  public static ArrayList getFolderList() {
    try {
      DataContext dataContext = DatabaseOrmService.getInstance()
          .getDataContext();
      SelectQuery query = new SelectQuery(EipTCabinetFolder.class);
      List list = dataContext.performQuery(query);
      if (list == null || list.size() < 0)
        return null;

      ArrayList prerootlist = getEipTCabinetFolderList(list, 0, 0);

      ArrayList result = getFolderList(prerootlist);

      return result;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static ArrayList getFolderList(List list) {
    try {
      if (list == null || list.size() <= 0)
        return null;

      ArrayList res = new ArrayList();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        FolderInfo info = (FolderInfo) list.get(i);
        res.add(info);
        List infos = info.getList();
        List a = getFolderList(infos);
        if (a != null && a.size() > 0) {
          res.addAll(a);
        }
      }
      return res;
    } catch (Exception e) {
      return null;
    }

  }

  private static ArrayList getEipTCabinetFolderList(List dblist, int parent_id,
      int hierarchy_index) {
    ArrayList list = new ArrayList();
    int size = dblist.size();
    for (int i = 0; i < size; i++) {
      EipTCabinetFolder folder = (EipTCabinetFolder) dblist.get(i);
      if (folder.getParentId().intValue() == parent_id) {
        FolderInfo info = new FolderInfo();
        info.setHierarchyIndex(hierarchy_index);
        info.setFolderId(folder.getFolderId().intValue());
        info.setParentFolderId(folder.getParentId().intValue());
        info.setFolderName(folder.getFolderName());
        list.add(info);
      }
    }

    if (list.size() <= 0)
      return null;

    int size2 = list.size();
    for (int i = 0; i < size2; i++) {
      FolderInfo info = (FolderInfo) list.get(i);
      List colist = getEipTCabinetFolderList(dblist, info.getFolderId(), info
          .getHierarchyIndex() + 1);
      if (colist != null && colist.size() > 0) {
        info.setList(colist);
      }
    }
    return list;
  }

  public static void setFolderVisible(ArrayList folder_list,
      FolderInfo selectedinfo, RunData rundata) {
    if (folder_list == null || folder_list.size() <= 0)
      return;

    ArrayList list = new ArrayList();
    FolderInfo info = null;
    int hierarchy_index = 0;
    int parent_id = 0;
    int size = folder_list.size() - 1;
    for (int i = size; i >= 0; i--) {
      if (hierarchy_index < 0) {
        break;
      }
      info = (FolderInfo) folder_list.get(i);
      if (null != selectedinfo
          && info.getFolderId() == selectedinfo.getFolderId()) {
        /** 新しく開かれたフォルダ */
        info.setOpened(true);
        list.add(0, info);
        parent_id = info.getParentFolderId();
        hierarchy_index = info.getHierarchyIndex() - 1;
      } else if (info.getFolderId() == parent_id) {
        // 親フォルダを捜す
        info.setOpened(true);
        list.add(0, info);
        parent_id = info.getParentFolderId();
        hierarchy_index = info.getHierarchyIndex() - 1;
      }
    }

    size = folder_list.size();
    for (int i = 0; i < size; i++) {
      FolderInfo info1 = (FolderInfo) folder_list.get(i);
      boolean containsId = false;
      for (int j = 0; j < list.size(); j++) {
        FolderInfo info2 = (FolderInfo) list.get(j);
        if (info1.getFolderId() == info2.getFolderId()) {
          containsId = true;
          break;
        } else if (info1.getParentFolderId() == info2.getFolderId()) {
          containsId = true;
          break;
        }
      }
      if (containsId) {
        info1.setVisible(true);
      } else {
        info1.setVisible(false);
      }
    }
    /** アクセス権限による可視、不可視の設定 */
    setFolderAuthorizedVisible(folder_list, rundata);
  }

  /**
   * リスト中のフォルダに対し、閲覧権限の有無により、可視、不可視を設定します
   *
   * @param folder_list
   * @param rundata
   */
  public static void setFolderAuthorizedVisible(ArrayList folder_list,
      RunData rundata) {
    FolderInfo info;
    int size = folder_list.size();
    /** アクセス権限による表示の制御 */
    List invisible_children = new ArrayList();
    for (int i = 0; i < size; i++) {
      info = (FolderInfo) folder_list.get(i);
      if (info.getHierarchyIndex() == 0) {
        info.setAuthorizedVisible(true);
        continue;
      }
      if (!invisible_children.contains(i)) {
        /** 既に見えないフォルダの子フォルダとして処理されたフォルダについては、アクセス権限チェックをスキップする */
        info.setAuthorizedVisible(isAccessibleFolder(info.getFolderId(),
            rundata));
      }
      if (!info.isAuthorizedVisible()) {
        /** 子フォルダも見えないようにする */
        for (int j = 0; j < size; j++) {
          FolderInfo info1 = (FolderInfo) folder_list.get(j);
          if (info1.getParentFolderId() == info.getFolderId()) {
            invisible_children.add(j);
            info1.setAuthorizedVisible(false);
          }
        }
      }
    }
  }

  public static String getFolderPosition(ArrayList folder_list, int folder_id) {
    String sepa = "<b> &gt; </b>";
    StringBuffer folderpath = new StringBuffer();
    FolderInfo info = null;
    int parent_id = -1;
    int size = folder_list.size() - 1;
    for (int i = size; i >= 0; i--) {
      info = (FolderInfo) folder_list.get(i);
      if (info.getFolderId() <= 1) {
        // 「ルートフォルダ」は含めない
        folderpath.insert(0, info.getFolderName());
        break;
      }
      if (info.getFolderId() == folder_id) {
        folderpath.append(sepa);
        folderpath.append(info.getFolderName());
        parent_id = info.getParentFolderId();
      } else if (info.getFolderId() == parent_id) {
        // 親フォルダを捜す
        folderpath.insert(0, info.getFolderName());
        folderpath.insert(0, sepa);
        parent_id = info.getParentFolderId();
      }
    }

    return folderpath.toString();
  }

  public static FolderInfo getSelectedFolderInfo(ArrayList list, int folder_id) {
    FolderInfo selected_folderinfo = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      FolderInfo info = (FolderInfo) list.get(i);
      if (info.getFolderId() == folder_id) {
        selected_folderinfo = info;
        break;
      }
    }
    return selected_folderinfo;
  }

  /**
   * ユーザ毎のルート保存先（絶対パス）を取得します。
   *
   * @param uid
   * @return
   */
  public static String getSaveDirPath(String orgId) {
    ALOrgUtilsHandler handler = ALOrgUtilsFactoryService.getInstance()
        .getOrgUtilsHandler();
    File path = handler.getDocumentPath(FOLDER_FILEDIR_CABINET, orgId,
        CATEGORY_KEY);
    if (!path.exists()) {
      path.mkdirs();
    }
    return path.getAbsolutePath();
  }

  /**
   * ユーザ毎の保存先（相対パス）を取得します。
   *
   * @param uid
   * @return
   */
  public static String getRelativePath(String fileName) {
    return new StringBuffer().append("/").append(fileName).toString();
  }

  /**
   * 添付ファイル保存先（絶対パス）を取得します。
   *
   * @param uid
   * @return
   */
  public static String getAbsolutePath(String orgId, String fileName) {
    ALOrgUtilsHandler handler = ALOrgUtilsFactoryService.getInstance()
        .getOrgUtilsHandler();
    StringBuffer sb = new StringBuffer().append(
        handler.getDocumentPath(FOLDER_FILEDIR_CABINET, orgId, CATEGORY_KEY))
        .append(File.separator);
    File f = new File(sb.toString());
    if (!f.exists()) {
      f.mkdirs();
    }
    return sb.append(File.separator).append(fileName).toString();
  }

  /**
   * 現在ログインしているユーザーは指定したIDのフォルダを閲覧する権限を持つかを返します
   *
   * @param folder_id
   * @param rundata
   * @return
   */
  public static boolean isAccessibleFolder(int folder_id, RunData rundata) {
    EipTCabinetFolder folder = getFolderByPK(folder_id);
    int public_flag = Integer.valueOf(folder.getPublicFlag());
    int current_user_id = ALEipUtils.getUserId(rundata);
    if (folder.getParentId() == CabinetUtils.ROOT_FODLER_ID
        || folder.getFolderId() == CabinetUtils.ROOT_FODLER_ID) {
      /** ルートフォルダまたはその直下のフォルダである場合 */
      if (public_flag == CabinetUtils.ACCESS_PUBLIC_ALL
          || public_flag == CabinetUtils.ACCESS_PUBLIC_MEMBER) {
        /** 全員が閲覧可能 */
        return true;
      } else if (public_flag == CabinetUtils.ACCESS_SECRET_SELF) {
        /** 作成者本人のみ閲覧可能 */
        if (folder.getCreateUserId() == current_user_id)
          return true;
      } else {
        /** 閲覧権限があるユーザーか確認する */
        DataContext dataContext = DatabaseOrmService.getInstance()
            .getDataContext();
        SelectQuery query = new SelectQuery(EipTCabinetFolderMap.class);
        Expression exp1 = ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY, folder);
        Expression exp2 = ExpressionFactory.matchExp(
            EipTCabinetFolderMap.USER_ID_PROPERTY, current_user_id);
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        List list = dataContext.performQuery(query);
        if (list != null && list.size() != 0)
          return true;
      }
      return false;
    } else {
      /** 何らかのフォルダの子フォルダである場合 */
      if (public_flag == CabinetUtils.ACCESS_PUBLIC_ALL) {
        /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
        return isAccessibleFolder(folder.getParentId(), rundata);
      } else if (public_flag == CabinetUtils.ACCESS_PUBLIC_MEMBER) {
        /** 上位フォルダでのアクセス制限は無い。 */
        return true;
      } else if (public_flag == CabinetUtils.ACCESS_SECRET_SELF) {
        /** 作成者本人のみ閲覧可能 */
        if (folder.getCreateUserId() == current_user_id)
          return true;
      } else {
        /** 閲覧権限があるユーザーか確認する */
        DataContext dataContext = DatabaseOrmService.getInstance()
            .getDataContext();
        SelectQuery query = new SelectQuery(EipTCabinetFolderMap.class);
        Expression exp1 = ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY, folder);
        Expression exp2 = ExpressionFactory.matchExp(
            EipTCabinetFolderMap.USER_ID_PROPERTY, current_user_id);
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        List list = dataContext.performQuery(query);
        if (list != null && list.size() != 0)
          return true;
      }
      return false;
    }
  }

  /**
   * 現在ログインしているユーザーは指定したIDのフォルダを編集する権限を持つかを返します
   *
   * @param folder_id
   * @param rundata
   * @return
   */
  public static boolean isEditableFolder(int folder_id, RunData rundata) {
    int current_user_id = ALEipUtils.getUserId(rundata);
    EipTCabinetFolder folder = CabinetUtils.getFolderByPK(folder_id);
    int public_flag = Integer.valueOf(folder.getPublicFlag());
    if (folder.getParentId() == CabinetUtils.ROOT_FODLER_ID
        || folder.getFolderId() == CabinetUtils.ROOT_FODLER_ID) {
      /** ルートフォルダまたはその直下のフォルダである場合 */
      if (public_flag == CabinetUtils.ACCESS_PUBLIC_ALL) {
        /** 全員が編集可能 */
        return true;
      } else if (public_flag == CabinetUtils.ACCESS_SECRET_SELF) {
        /** 作成者本人のみ編集可能 */
        if (folder.getCreateUserId() == current_user_id)
          return true;
      } else {
        /** 編集権限があるユーザーか確認する */
        DataContext dataContext = DatabaseOrmService.getInstance()
            .getDataContext();
        SelectQuery query = new SelectQuery(EipTCabinetFolderMap.class);
        Expression exp1 = ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY, folder);
        Expression exp2 = ExpressionFactory.matchExp(
            EipTCabinetFolderMap.USER_ID_PROPERTY, current_user_id);
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        List list = dataContext.performQuery(query);
        if (list != null && list.size() != 0)
          return true;
      }
      return false;
    } else {
      /** 何らかのフォルダの子フォルダの場合 */
      if (public_flag == CabinetUtils.ACCESS_PUBLIC_ALL) {
        /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
        return isEditableFolder(folder.getParentId(), rundata);
      } else if (public_flag == CabinetUtils.ACCESS_SECRET_SELF) {
        /** 作成者本人のみ編集可能 */
        if (folder.getCreateUserId() == current_user_id)
          return true;
      } else {
        /** 編集権限があるユーザーか確認する */
        DataContext dataContext = DatabaseOrmService.getInstance()
            .getDataContext();
        SelectQuery query = new SelectQuery(EipTCabinetFolderMap.class);
        Expression exp1 = ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY, folder);
        Expression exp2 = ExpressionFactory.matchExp(
            EipTCabinetFolderMap.USER_ID_PROPERTY, current_user_id);
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        List list = dataContext.performQuery(query);
        if (list != null && list.size() != 0)
          return true;
      }
      return false;
    }
  }

  /**
   * フォームで使用するフォルダのリストに対し、権限的に不可視なフォルダを設定します
   *
   * @param folder_list
   * @param rundata
   */
  public static void setFolderVisibleForForm(ArrayList folder_list,
      RunData rundata) {
    int size = folder_list.size();
    FolderInfo info;
    List invisible_children = new ArrayList();
    for (int i = 0; i < size; i++) {
      info = (FolderInfo) folder_list.get(i);
      if (info.getHierarchyIndex() == 0) {
        info.setAuthorizedVisible(true);
        continue;
      }
      if (!invisible_children.contains(i)) {
        /** 見えないフォルダの子フォルダとして処理済みのフォルダについては、アクセス権限チェックをスキップする */
        info
            .setAuthorizedVisible(isEditableFolder(info.getFolderId(), rundata));
      }
      if (!info.isAuthorizedVisible()) {
        /** 不可視に設定したフォルダの子フォルダも見えないようにする */
        for (int j = 0; j < size; j++) {
          FolderInfo info1 = (FolderInfo) folder_list.get(j);
          if (info1.getParentFolderId() == info.getFolderId()) {
            invisible_children.add(j);
            info1.setAuthorizedVisible(false);
          }
        }
      }
    }
  }

  /**
   * 権限的に閲覧可能な全フォルダのIDを返します。
   *
   * @param rundata
   * @return
   */
  public static ArrayList getAuthorizedVisibleFolderIds(RunData rundata) {
    ArrayList ids = new ArrayList();
    ArrayList list = CabinetUtils.getFolderList();
    CabinetUtils.setFolderAuthorizedVisible(list, rundata);
    FolderInfo folder;
    for (int i = 0; i < list.size(); i++) {
      folder = (FolderInfo) list.get(i);
      if (folder.isAuthorizedVisible())
        ids.add(folder.getFolderId());
    }
    return ids;
  }

  /**
   * 上位でアクセスコントロールを行っているフォルダを再帰的に検索します
   *
   * @param parentId
   * @return
   */
  public static Integer getAccessControlFolderId(Integer parentId) {
    if (parentId == CabinetUtils.ROOT_FODLER_ID)
      /** ルートまでさかのぼってもアクセスコントロールがされていない場合 */
      return 1;
    EipTCabinetFolder folder = getFolderByPK(parentId);
    if (Integer.valueOf(folder.getPublicFlag()) == CabinetUtils.ACCESS_PUBLIC_ALL) {
      /** さらに上位のフォルダでアクセスコントロールが設定されている可能性がある */
      return CabinetUtils.getAccessControlFolderId(folder.getParentId());
    } else {
      /** このフォルダでアクセスコントロールが設定されている */
      return folder.getFolderId();
    }
  }

  public static List getChildFolders(EipTCabinetFolder folder) {
    List list = new ArrayList();
    List children = CabinetUtils.getChildren(folder.getFolderId());
    List children_tmp = new ArrayList();
    list.addAll(children);
    int add_count = children.size();
    while (add_count > 0) {
      add_count = 0;
      for (int i = 0; i < children.size(); i++) {
        children_tmp.addAll(CabinetUtils
            .getChildren(((EipTCabinetFolder) children.get(i)).getFolderId()));
      }
      add_count = children_tmp.size();
      children.clear();
      children.addAll(children_tmp);
      list.addAll(children_tmp);
      children_tmp.clear();
    }
    return list;
  }

  public static List getChildren(int parent_id) {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query = new SelectQuery(EipTCabinetFolder.class);
    Expression pk_exp = ExpressionFactory.matchExp(
        EipTCabinetFolder.PARENT_ID_PROPERTY, parent_id);
    query.setQualifier(pk_exp);
    List list = dataContext.performQuery(query);
    return list;
  }

  public static EipTCabinetFolder getFolderByPK(Integer folder_id) {
    DataContext dataContext = DatabaseOrmService.getInstance().getDataContext();
    SelectQuery query = new SelectQuery(EipTCabinetFolder.class);
    Expression pk_exp = ExpressionFactory.matchDbExp(
        EipTCabinetFolder.FOLDER_ID_PK_COLUMN, folder_id);
    query.setQualifier(pk_exp);
    List list = dataContext.performQuery(query);
    EipTCabinetFolder folder = (EipTCabinetFolder) list.get(0);
    return folder;
  }

}