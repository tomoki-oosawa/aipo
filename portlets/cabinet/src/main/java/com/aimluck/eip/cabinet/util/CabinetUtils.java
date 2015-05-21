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
package com.aimluck.eip.cabinet.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.eip.cabinet.FolderInfo;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFile;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolder;
import com.aimluck.eip.cayenne.om.portlet.EipTCabinetFolderMap;
import com.aimluck.eip.common.ALActivity;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.social.ALActivityService;
import com.aimluck.eip.services.social.model.ALActivityPutRequest;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * キャビネットのユーティリティクラスです。 <BR>
 *
 */
public class CabinetUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CabinetUtils.class.getName());

  public static final String DATE_TIME_FORMAT =
    ALDateTimeField.DEFAULT_DATE_TIME_FORMAT;

  /** フォルダ『ルートフォルダ』の予約 ID */
  public static final int ROOT_FODLER_ID = 1;

  /** セッションの識別子 */
  public static final String KEY_FOLDER_ID = "folder_id";

  public static final String KEY_POST_NAME = "post_name";

  /** 共有ファイルを保管するディレクトリのカテゴリキーの指定 */
  public static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.cabinet.categorykey",
    "");

  /** 共有ファイルを保管するディレクトリの指定 */
  public static final String FOLDER_FILEDIR_CABINET = JetspeedResources
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

  public static final String CABINET_PORTLET_NAME = "Cabinet";

  /** パラメータリセットの識別子 */
  private static final String RESET_FLAG = "reset_params";

  /** 検索キーワード変数の識別子 */
  public static final String TARGET_KEYWORD = "keyword";

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

      SelectQuery<EipTCabinetFolder> query =
        Database.query(EipTCabinetFolder.class);
      Expression exp =
        ExpressionFactory.matchDbExp(
          EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
          folderid);
      query.setQualifier(exp);
      List<EipTCabinetFolder> folders = query.fetchList();
      if (folders == null || folders.size() == 0) {
        // 指定したフォルダ IDのレコードが見つからない場合
        logger.debug("[Cabinet Folder] Not found ID...");
        return null;
      }
      return folders.get(0);
    } catch (Exception ex) {
      logger.error("cabinet", ex);
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
    String fileid =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    try {
      if (fileid == null || Integer.valueOf(fileid) == null) {
        // ファイル IDが空の場合
        logger.debug("[Cabinet File] Empty ID...");
        return null;
      }

      SelectQuery<EipTCabinetFile> query =
        Database.query(EipTCabinetFile.class);
      Expression exp =
        ExpressionFactory.matchDbExp(EipTCabinetFile.FILE_ID_PK_COLUMN, fileid);
      query.setQualifier(exp);
      List<EipTCabinetFile> files = query.fetchList();
      if (files == null || files.size() == 0) {
        // 指定したファイル IDのレコードが見つからない場合
        logger.debug("[Cabinet File] Not found ID...");
        ALEipUtils.redirectPageNotFound(rundata);
        return null;
      }

      return files.get(0);
    } catch (Exception ex) {
      logger.error("cabinet", ex);
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
  public static List<EipTCabinetFile> getEipTCabinetFileList(int folderId) {
    try {
      SelectQuery<EipTCabinetFile> query =
        Database.query(EipTCabinetFile.class);
      Expression exp =
        ExpressionFactory.matchExp(EipTCabinetFile.FOLDER_ID_PROPERTY, Integer
          .valueOf(folderId));
      query.setQualifier(exp);
      List<EipTCabinetFile> files = query.fetchList();
      if (files == null || files.size() == 0) {
        return null;
      }

      return files;
    } catch (Exception ex) {
      logger.error("cabinet", ex);
      return null;
    }
  }

  public static List<FolderInfo> getFolderList() {
    try {
      SelectQuery<EipTCabinetFolder> query =
        Database.query(EipTCabinetFolder.class);
      query.orderAscending(EipTCabinetFolder.FOLDER_NAME_PROPERTY);
      List<EipTCabinetFolder> list = query.fetchList();
      if (list == null || list.size() < 0) {
        return null;
      }

      List<FolderInfo> prerootlist = getEipTCabinetFolderList(list, 0, 0);

      List<FolderInfo> result = getFolderList(prerootlist);

      return result;
    } catch (Exception e) {
      logger.error("[CabinetUtils]", e);
      return null;
    }
  }

  public static ArrayList<FolderInfo> getFolderList(List<FolderInfo> list) {
    try {
      if (list == null || list.size() <= 0) {
        return null;
      }

      ArrayList<FolderInfo> res = new ArrayList<FolderInfo>();
      int size = list.size();
      for (int i = 0; i < size; i++) {
        FolderInfo info = list.get(i);
        res.add(info);
        List<FolderInfo> infos = info.getList();
        List<FolderInfo> a = getFolderList(infos);
        if (a != null && a.size() > 0) {
          res.addAll(a);
        }
      }
      return res;
    } catch (Exception e) {
      return null;
    }

  }

  private static ArrayList<FolderInfo> getEipTCabinetFolderList(
      List<EipTCabinetFolder> dblist, int parent_id, int hierarchy_index) {
    ArrayList<FolderInfo> list = new ArrayList<FolderInfo>();
    int size = dblist.size();
    for (int i = 0; i < size; i++) {
      EipTCabinetFolder folder = dblist.get(i);
      if (folder.getParentId().intValue() == parent_id) {
        FolderInfo info = new FolderInfo();
        info.setHierarchyIndex(hierarchy_index);
        info.setFolderId(folder.getFolderId().intValue());
        info.setParentFolderId(folder.getParentId().intValue());
        info.setFolderName(folder.getFolderName());
        info.setUpdateDate(folder.getUpdateDate());
        try {
          info.setUpdateUserId(folder.getUpdateUserId());
          info.setUpdateName(ALEipUtils.getALEipUser(folder
            .getUpdateUserId()
            .intValue()));
        } catch (ALDBErrorException e) {
          e.printStackTrace();
        }
        list.add(info);
      }
    }

    if (list.size() <= 0) {
      return null;
    }

    int size2 = list.size();
    for (int i = 0; i < size2; i++) {
      FolderInfo info = list.get(i);
      List<FolderInfo> colist =
        getEipTCabinetFolderList(dblist, info.getFolderId(), info
          .getHierarchyIndex() + 1);
      if (colist != null && colist.size() > 0) {
        info.setList(colist);
      }
    }
    return list;
  }

  public static void setFolderVisible(List<FolderInfo> folder_list,
      FolderInfo selectedinfo, RunData rundata) {
    if (folder_list == null || folder_list.size() <= 0) {
      return;
    }

    List<FolderInfo> list = new ArrayList<FolderInfo>();
    FolderInfo info = null;
    int hierarchy_index = 0;
    int parent_id = 0;
    int size = folder_list.size() - 1;
    for (int i = size; i >= 0; i--) {
      if (hierarchy_index < 0) {
        break;
      }
      info = folder_list.get(i);
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
      FolderInfo info1 = folder_list.get(i);
      boolean containsId = false;
      for (int j = 0; j < list.size(); j++) {
        FolderInfo info2 = list.get(j);
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
  public static void setFolderAuthorizedVisible(List<FolderInfo> folder_list,
      RunData rundata) {
    FolderInfo info;
    int size = folder_list.size();
    /** アクセス権限による表示の制御 */
    List<Integer> invisible_children = new ArrayList<Integer>();
    for (int i = 0; i < size; i++) {
      info = folder_list.get(i);
      if (info.getHierarchyIndex() == 0) {
        info.setAuthorizedVisible(true);
        continue;
      }
      if (!invisible_children.contains(i)) {
        /** 既に見えないフォルダの子フォルダとして処理されたフォルダについては、アクセス権限チェックをスキップする */
        info.setAuthorizedVisible(isAccessibleFolder(
          info.getFolderId(),
          rundata));
      }
      if (!info.isAuthorizedVisible()) {
        /** 子フォルダも見えないようにする */
        for (int j = 0; j < size; j++) {
          FolderInfo info1 = folder_list.get(j);
          if (info1.getParentFolderId() == info.getFolderId()) {
            invisible_children.add(j);
            info1.setAuthorizedVisible(false);
          }
        }
      }
    }
  }

  public static String getFolderPosition(List<FolderInfo> folder_list,
      int folder_id) {
    String sepa = "<b> &gt; </b>";
    StringBuffer folderpath = new StringBuffer();
    FolderInfo info = null;
    int parent_id = -1;
    int size = folder_list.size() - 1;
    for (int i = size; i >= 0; i--) {
      info = folder_list.get(i);
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

  public static FolderInfo getSelectedFolderInfo(List<FolderInfo> list,
      int folder_id) {
    FolderInfo selected_folderinfo = null;
    int size = list.size();
    for (int i = 0; i < size; i++) {
      FolderInfo info = list.get(i);
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
    return ALStorageService.getDocumentPath(
      FOLDER_FILEDIR_CABINET,
      CATEGORY_KEY);
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
        if (folder.getCreateUserId() == current_user_id) {
          return true;
        }
      } else {
        /** 閲覧権限があるユーザーか確認する */
        SelectQuery<EipTCabinetFolderMap> query =
          Database.query(EipTCabinetFolderMap.class);
        Expression exp1 =
          ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY,
            folder);
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipTCabinetFolderMap.USER_ID_PROPERTY,
            current_user_id);
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        List<EipTCabinetFolderMap> list = query.fetchList();
        if (list != null && list.size() != 0) {
          return true;
        }
      }
      return false;
    } else {
      /** 何らかのフォルダの子フォルダである場合 */
      if (public_flag == CabinetUtils.ACCESS_PUBLIC_ALL) {
        /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
        return isAccessibleFolder(folder.getParentId(), rundata);
      } else if (public_flag == CabinetUtils.ACCESS_PUBLIC_MEMBER) {
        /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
        return isAccessibleFolder(folder.getParentId(), rundata);
      } else if (public_flag == CabinetUtils.ACCESS_SECRET_SELF) {
        /** 作成者本人のみ閲覧可能 */
        if (folder.getCreateUserId() == current_user_id) {
          /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
          return isAccessibleFolder(folder.getParentId(), rundata);
        }
      } else {
        /** 閲覧権限があるユーザーか確認する */
        SelectQuery<EipTCabinetFolderMap> query =
          Database.query(EipTCabinetFolderMap.class);
        Expression exp1 =
          ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY,
            folder);
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipTCabinetFolderMap.USER_ID_PROPERTY,
            current_user_id);
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        List<EipTCabinetFolderMap> list = query.fetchList();
        if (list != null && list.size() != 0) {
          /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
          return isAccessibleFolder(folder.getParentId(), rundata);
        }
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
        if (folder.getCreateUserId() == current_user_id) {
          return true;
        }
      } else {
        /** 編集権限があるユーザーか確認する */
        SelectQuery<EipTCabinetFolderMap> query =
          Database.query(EipTCabinetFolderMap.class);
        Expression exp1 =
          ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY,
            folder);
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipTCabinetFolderMap.USER_ID_PROPERTY,
            current_user_id);
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        List<EipTCabinetFolderMap> list = query.fetchList();
        if (list != null && list.size() != 0) {
          return true;
        }
      }
      return false;
    } else {
      /** 何らかのフォルダの子フォルダの場合 */
      if (public_flag == CabinetUtils.ACCESS_PUBLIC_ALL) {
        /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
        return isEditableFolder(folder.getParentId(), rundata);
      } else if (public_flag == CabinetUtils.ACCESS_SECRET_SELF) {
        /** 作成者本人のみ編集可能 */
        if (folder.getCreateUserId() == current_user_id) {
          /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
          return isEditableFolder(folder.getParentId(), rundata);
        }
      } else {
        /** 編集権限があるユーザーか確認する */
        SelectQuery<EipTCabinetFolderMap> query =
          Database.query(EipTCabinetFolderMap.class);
        Expression exp1 =
          ExpressionFactory.matchDbExp(
            EipTCabinetFolderMap.EIP_TCABINET_FOLDER_PROPERTY,
            folder);
        Expression exp2 =
          ExpressionFactory.matchExp(
            EipTCabinetFolderMap.USER_ID_PROPERTY,
            current_user_id);
        query.setQualifier(exp1);
        query.andQualifier(exp2);
        List<EipTCabinetFolderMap> list = query.fetchList();
        if (list != null && list.size() != 0) {
          /** 上位フォルダでアクセス制限がかかっている可能性あり。再帰的に調査する */
          return isEditableFolder(folder.getParentId(), rundata);
        }
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
  public static void setFolderVisibleForForm(List<FolderInfo> folder_list,
      RunData rundata) {
    int size = folder_list.size();
    FolderInfo info;
    List<Integer> invisible_children = new ArrayList<Integer>();
    for (int i = 0; i < size; i++) {
      info = folder_list.get(i);
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
          FolderInfo info1 = folder_list.get(j);
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
  public static List<Integer> getAuthorizedVisibleFolderIds(RunData rundata) {
    List<Integer> ids = new ArrayList<Integer>();
    List<FolderInfo> list = CabinetUtils.getFolderList();
    CabinetUtils.setFolderAuthorizedVisible(list, rundata);
    FolderInfo folder;
    for (int i = 0; i < list.size(); i++) {
      folder = list.get(i);
      if (folder.isAuthorizedVisible()) {
        ids.add(folder.getFolderId());
      }
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
    if (parentId == CabinetUtils.ROOT_FODLER_ID) {
      /** ルートまでさかのぼってもアクセスコントロールがされていない場合 */
      return 1;
    }
    EipTCabinetFolder folder = getFolderByPK(parentId);
    if (Integer.valueOf(folder.getPublicFlag()) == CabinetUtils.ACCESS_PUBLIC_ALL) {
      /** さらに上位のフォルダでアクセスコントロールが設定されている可能性がある */
      return CabinetUtils.getAccessControlFolderId(folder.getParentId());
    } else {
      /** このフォルダでアクセスコントロールが設定されている */
      return folder.getFolderId();
    }
  }

  public static List<EipTCabinetFolder> getChildFolders(EipTCabinetFolder folder) {
    List<EipTCabinetFolder> list = new ArrayList<EipTCabinetFolder>();
    List<EipTCabinetFolder> children =
      CabinetUtils.getChildren(folder.getFolderId());
    List<EipTCabinetFolder> children_tmp = new ArrayList<EipTCabinetFolder>();
    list.addAll(children);
    int add_count = children.size();
    while (add_count > 0) {
      add_count = 0;
      for (int i = 0; i < children.size(); i++) {
        children_tmp.addAll(CabinetUtils.getChildren(children
          .get(i)
          .getFolderId()));
      }
      add_count = children_tmp.size();
      children.clear();
      children.addAll(children_tmp);
      list.addAll(children_tmp);
      children_tmp.clear();
    }
    return list;
  }

  public static List<EipTCabinetFolder> getChildren(int parent_id) {
    SelectQuery<EipTCabinetFolder> query =
      Database.query(EipTCabinetFolder.class);
    Expression pk_exp =
      ExpressionFactory.matchExp(
        EipTCabinetFolder.PARENT_ID_PROPERTY,
        parent_id);
    query.setQualifier(pk_exp);
    List<EipTCabinetFolder> list = query.fetchList();
    return list;
  }

  public static EipTCabinetFolder getFolderByPK(Integer folder_id) {
    SelectQuery<EipTCabinetFolder> query =
      Database.query(EipTCabinetFolder.class);
    Expression pk_exp =
      ExpressionFactory.matchDbExp(
        EipTCabinetFolder.FOLDER_ID_PK_COLUMN,
        folder_id);
    query.setQualifier(pk_exp);
    List<EipTCabinetFolder> list = query.fetchList();
    EipTCabinetFolder folder = list.get(0);
    return folder;
  }

  /**
   *
   * @param file
   * @param loginName
   */
  public static void createCabinetActivity(EipTCabinetFile file,
      String loginName, List<String> recipients, boolean isNew) {

    ALActivity RecentActivity =
      ALActivity.getRecentActivity("Cabinet", file.getFileId(), 0f);
    boolean isDeletePrev =
      RecentActivity != null && RecentActivity.isReplace(loginName);

    String title =
      new StringBuilder("ファイル「")
        .append(file.getFileTitle())
        .append("」を")
        .append(isNew ? "追加しました。" : "編集しました。")
        .toString();
    String portletParams =
      new StringBuilder("?template=CabinetFileDetailScreen").append(
        "&entityid=").append(file.getFileId()).toString();

    if (recipients != null && recipients.size() > 0) {
      ALActivityService.create(new ALActivityPutRequest()
        .withAppId("Cabinet")
        .withLoginName(loginName)
        .withPortletParams(portletParams)
        .withRecipients(recipients)
        .withTitle(title)
        .withUserId(file.getUpdateUserId())
        .withPriority(0f)
        .withExternalId(String.valueOf(file.getFileId())));
    } else {
      ALActivityService.create(new ALActivityPutRequest()
        .withAppId("Cabinet")
        .withLoginName(loginName)
        .withPortletParams(portletParams)
        .withTitle(title)
        .withUserId(file.getUpdateUserId())
        .withPriority(0f)
        .withExternalId(String.valueOf(file.getFileId())));
    }

    if (isDeletePrev) {
      RecentActivity.delete();
    }
  }

  /**
   * <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  public static List<Integer> getWhatsNewInsertList(RunData rundata,
      int folderid, String is_public) throws ALPageNotFoundException,
      ALDBErrorException {

    int userid = ALEipUtils.getUserId(rundata);
    List<ALEipUser> result = new ArrayList<ALEipUser>();

    // ０，１は全員へ公開
    if ("0".equals(is_public) || "1".equals(is_public)) {

      /* 自分以外の全員に新着ポートレット登録 */
      ALAccessControlFactoryService aclservice =
        (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
          .getInstance())
          .getService(ALAccessControlFactoryService.SERVICE_NAME);
      ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
      List<Integer> userIds =
        aclhandler.getAcceptUserIdsExceptLoginUser(
          ALEipUtils.getUserId(rundata),
          ALAccessControlConstants.POERTLET_FEATURE_MSGBOARD_TOPIC,
          ALAccessControlConstants.VALUE_ACL_DETAIL);
      return userIds;

    }
    // ２はグループ内に公開
    else if ("2".equals(is_public)) {
      try {

        SelectQuery<EipTCabinetFolderMap> query =
          Database.query(EipTCabinetFolderMap.class);
        query.select(EipTCabinetFolderMap.USER_ID_COLUMN);

        Expression exp1 =
          ExpressionFactory.matchExp(
            EipTCabinetFolderMap.FOLDER_ID_PROPERTY,
            Integer.valueOf(folderid));
        query.setQualifier(exp1);

        List<EipTCabinetFolderMap> uids = query.fetchList();
        List<Integer> userIds = new ArrayList<Integer>();
        if (uids != null && uids.size() != 0) {
          int size = uids.size();
          for (int i = 0; i < size; i++) {
            EipTCabinetFolderMap uid = uids.get(i);
            Integer id = uid.getUserId();
            if (id.intValue() != userid) {
              result.add(ALEipUtils.getALEipUser(id.intValue()));
              userIds.add(id.intValue());
            }
          }
        }
        return userIds;

      } catch (Exception ex) {
        logger.error("[CabinetUtils]", ex);
        throw new ALDBErrorException();
      }
    }
    // ３は自分以外に公開しない
    else {
      return null;
    }
  }

  /**
   * 表示切り替えのリセットフラグがあるかを返す．
   *
   * @param rundata
   * @param context
   * @return
   */
  public static boolean hasResetFlag(RunData rundata, Context context) {
    String resetflag = rundata.getParameters().getString(RESET_FLAG);
    return resetflag != null;
  }

  /**
   * フィルターを初期化する．
   *
   * @param rundata
   * @param context
   * @param className
   */
  public static void resetFilter(RunData rundata, Context context,
      String className) {
    ALEipUtils.removeTemp(rundata, context, new StringBuffer()
      .append(className)
      .append(ALEipConstants.LIST_FILTER)
      .toString());
    ALEipUtils.removeTemp(rundata, context, new StringBuffer()
      .append(className)
      .append(ALEipConstants.LIST_FILTER_TYPE)
      .toString());
    ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, "");
  }

  /**
   * 表示切り替えで指定した検索キーワードを取得する．
   *
   * @param rundata
   * @param context
   * @return
   */
  public static String getTargetKeyword(RunData rundata, Context context) {
    String target_keyword = null;
    String keywordParam = rundata.getParameters().getString(TARGET_KEYWORD);
    target_keyword = ALEipUtils.getTemp(rundata, context, TARGET_KEYWORD);

    if (keywordParam == null && (target_keyword == null)) {
      ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, "");
      target_keyword = "";
    } else if (keywordParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, keywordParam.trim());
      target_keyword = keywordParam;
    }
    return target_keyword;
  }
}