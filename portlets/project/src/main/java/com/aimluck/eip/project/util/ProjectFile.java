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
package com.aimluck.eip.project.util;

import static com.aimluck.eip.util.ALLocalizationUtils.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.portlet.IProjectFile;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.fileupload.util.FileuploadUtils.ShrinkImageSet;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ファイルアップロード処理を行うクラスです。
 * 
 * @param <T>
 *          ファイルテーブルのクラスを指定します。
 * @param <M>
 *          ファイルが紐づくテーブルのクラスを指定します。
 */
public class ProjectFile<T extends IProjectFile, M> {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProjectFile.class.getName());

  /** デフォルトエンコーディングを表わすシステムプロパティのキー */
  public static final String FILE_ENCODING = JetspeedResources.getString(
    "content.defaultencoding",
    "UTF-8");

  /** プロジェクトの添付ファイルを保管するディレクトリの指定 */
  public static final String FOLDER_FILEDIR = JetspeedResources.getString(
    "aipo.filedir",
    "");

  /** プロジェクトの添付ファイルを保管するディレクトリのカテゴリキーの指定 */
  public static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.project.categorykey",
    "");

  /***/
  private final Class<T> classT;

  /**
   * コンストラクタ
   * 
   * @param classT
   *          ファイルテーブルのクラスオブジェクト
   */
  public ProjectFile(Class<T> classT) {
    super();
    this.classT = classT;
  }

  /**
   * 一時的にアップロードした添付ファイルを取得します。
   * 
   * @param rundata
   *          RunData
   * @return 添付ファイルリスト
   */
  public ArrayList<FileuploadBean> getFileuploadList(RunData rundata) {
    String[] fileids =
      rundata
        .getParameters()
        .getStrings(FileuploadUtils.KEY_FILEUPLOAD_ID_LIST);
    if (fileids == null) {
      return null;
    }

    ArrayList<String> hadfileids = new ArrayList<String>();
    ArrayList<String> newfileids = new ArrayList<String>();

    for (int j = 0; j < fileids.length; j++) {
      if (fileids[j].trim().startsWith("s")) {
        hadfileids.add(fileids[j].trim().substring(1));
      } else {
        newfileids.add(fileids[j].trim());
      }
    }

    ArrayList<FileuploadBean> fileNameList = new ArrayList<FileuploadBean>();
    FileuploadBean filebean = null;
    int fileid = 0;

    // 新規にアップロードされたファイルの処理
    if (newfileids.size() > 0) {
      String folderName =
        rundata.getParameters().getString(
          FileuploadUtils.KEY_FILEUPLOAD_FODLER_NAME);
      if (folderName == null || folderName.equals("")) {
        return null;
      }

      int length = newfileids.size();
      for (int i = 0; i < length; i++) {
        if (newfileids.get(i) == null || newfileids.get(i).equals("")) {
          continue;
        }

        try {
          fileid = Integer.parseInt(newfileids.get(i));
        } catch (Exception e) {
          continue;
        }

        if (fileid == 0) {
          filebean = new FileuploadBean();
          filebean.initField();
          filebean.setFolderName("photo");
          filebean.setFileName(getl10n("PROJECT_PREVIOUS_PICTURE_FILE"));
          fileNameList.add(filebean);
        } else {
          BufferedReader reader = null;
          try {
            reader =
              new BufferedReader(new InputStreamReader(ALStorageService
                .getTmpFile(ALEipUtils.getUserId(rundata), folderName, fileid
                  + FileuploadUtils.EXT_FILENAME), FILE_ENCODING));
            String line = reader.readLine();
            if (line == null || line.length() <= 0) {
              continue;
            }

            filebean = new FileuploadBean();
            filebean.initField();
            filebean.setFolderName(fileids[i]);
            filebean.setFileId(fileid);
            filebean.setFileName(line);
            fileNameList.add(filebean);
          } catch (Exception e) {
            logger.error("Exception", e);
          } finally {
            try {
              reader.close();
            } catch (Exception e) {
              logger.error("Exception", e);
            }
          }
        }

      }
    }

    if (hadfileids.size() > 0) {
      // すでにあるファイルの処理
      ArrayList<Integer> hadfileidsValue = new ArrayList<Integer>();
      for (int k = 0; k < hadfileids.size(); k++) {
        try {
          fileid = Integer.parseInt(hadfileids.get(k));
          hadfileidsValue.add(fileid);
        } catch (Exception e) {
          continue;
        }
      }

      try {
        SelectQuery<T> reqquery = Database.query(classT);
        Expression reqexp1 =
          ExpressionFactory.inDbExp(T.PK_COLUMN, hadfileidsValue);
        reqquery.setQualifier(reqexp1);
        List<T> requests = reqquery.fetchList();
        int requestssize = requests.size();
        for (int i = 0; i < requestssize; i++) {
          T file = requests.get(i);
          filebean = new FileuploadBean();
          filebean.initField();
          filebean.setFileId(file.getFileId());
          filebean.setFileName(file.getFileName());
          filebean.setFlagNewFile(false);
          fileNameList.add(filebean);
        }
      } catch (Exception ex) {
        logger.error("[ProjectFile] Exception.", ex);
      }
    }
    return fileNameList;
  }

  /**
   * ファイルをデータベースに登録します。
   * 
   * @param fileuploadList
   * @param folderName
   * @param uid
   * @param entry
   * @param msgList
   * @return TRUE:成功 FALSE:失敗
   */
  public boolean insertAttachmentFiles(List<FileuploadBean> fileuploadList,
      String folderName, int uid, M entry, List<String> msgList) {

    if (fileuploadList == null || fileuploadList.size() <= 0) {
      return true;
    }

    try {
      int length = fileuploadList.size();
      ArrayList<FileuploadLiteBean> newfilebeans =
        new ArrayList<FileuploadLiteBean>();
      FileuploadLiteBean filebean = null;
      for (int i = 0; i < length; i++) {
        filebean = fileuploadList.get(i);
        if (filebean.isNewFile()) {
          newfilebeans.add(filebean);
        }
      }
      int newfilebeansSize = newfilebeans.size();
      if (newfilebeansSize > 0) {
        FileuploadLiteBean newfilebean = null;
        for (int j = 0; j < length; j++) {
          newfilebean = newfilebeans.get(j);
          // サムネイル処理
          String[] acceptExts = ImageIO.getWriterFormatNames();
          ShrinkImageSet shrinkImageSet =
            FileuploadUtils.getBytesShrinkFilebean(
              Database.getDomainName(),
              folderName,
              uid,
              newfilebean,
              acceptExts,
              FileuploadUtils.DEF_THUMBNAIL_WIDTH,
              FileuploadUtils.DEF_THUMBNAIL_HEIGHT,
              msgList,
              false);

          String filename = j + "_" + String.valueOf(System.nanoTime());

          // 新規オブジェクトモデル
          T file = Database.create(classT);
          file.setOwnerId(Integer.valueOf(uid));
          file.setFileName(newfilebean.getFileName());
          file.setFilePath(getRelativePath(filename));
          if (shrinkImageSet != null) {
            file.setFileThumbnail(shrinkImageSet.getShrinkImage());
          }
          file.setEipT(entry);
          file.setCreateDate(Calendar.getInstance().getTime());
          file.setUpdateDate(Calendar.getInstance().getTime());

          // ファイルの移動
          ALStorageService.copyTmpFile(uid, folderName, String
            .valueOf(newfilebean.getFileId()), FOLDER_FILEDIR, CATEGORY_KEY
            + ALStorageService.separator()
            + uid, filename);
        }

        // 添付ファイル保存先のフォルダを削除
        ALStorageService.deleteTmpFolder(uid, folderName);
      }

    } catch (Exception e) {
      logger.error(e);
    }
    return true;
  }

  /**
   * ファイル検索のクエリを返します
   * 
   * @param requestid
   *          ファイルを検索するリクエストのid
   * @return query
   */
  public SelectQuery<T> getSelectQueryForFiles(String keyName, int id) {
    SelectQuery<T> query = Database.query(classT);
    query.setQualifier(ExpressionFactory.matchDbExp(keyName, id));
    return query;
  }

  /**
   * アップロード済みの添付ファイル一覧を取得します。
   * 
   * @param list
   *          添付ファイルレコードリスト
   * @return 添付ファイル一覧
   */
  public List<FileuploadBean> getFileList(List<T> list) {

    // ファイルリスト
    List<FileuploadBean> attachmentFileList = new ArrayList<FileuploadBean>();
    if (list != null && list.size() > 0) {
      FileuploadBean filebean = null;
      for (T file : list) {
        String realname = file.getFileName();
        javax.activation.DataHandler hData =
          new javax.activation.DataHandler(new javax.activation.FileDataSource(
            realname));

        filebean = new FileuploadBean();
        filebean.setFileId(file.getFileId().intValue());
        filebean.setFileName(realname);
        if (hData != null) {
          filebean.setContentType(hData.getContentType());
        }
        filebean.setIsImage(FileuploadUtils.isImage(realname));
        attachmentFileList.add(filebean);
      }
    }
    return attachmentFileList;
  }

  /**
   * ファイルオブジェクトモデルを取得します。 <BR>
   * ファイルリンク押下時の動作で使用しています。
   * 
   * @param rundata
   *          RunData
   * @return ファイルオブジェクトモデル
   * @exception ALPageNotFoundException
   *              ページ存在例外
   * @exception ALDBErrorException
   *              DB例外
   */
  public T getEipTFile(RunData rundata, String key)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      int attachmentIndex =
        rundata.getParameters().getInt("attachmentIndex", -1);
      if (attachmentIndex < 0) {
        // ID が空の場合
        logger.debug("[ProjectFile] Empty ID...");
        throw new ALPageNotFoundException();

      }

      SelectQuery<T> query = Database.query(classT);
      Expression exp =
        ExpressionFactory.matchDbExp(key, Integer.valueOf(attachmentIndex));
      query.andQualifier(exp);

      List<T> files = query.fetchList();
      if (files == null || files.size() == 0) {
        // 指定した ID のレコードが見つからない場合
        logger.debug("[ProjectFile] Not found ID...");
        throw new ALPageNotFoundException();
      }
      return files.get(0);
    } catch (Exception ex) {
      logger.error("[ProjectFile]", ex);
      throw new ALDBErrorException();
    }
  }

  /**
   * ファイルを削除する
   * 
   * @param rundata
   *          RunData
   * @param keyName
   *          キーフィールド名
   * @param id
   *          キー
   * @return TRUE:成功 FALSE:失敗
   */
  public boolean removeFile(RunData rundata, String keyName, Integer id) {

    try {
      // タスクに紐づく担当者
      SelectQuery<T> query = Database.query(classT);
      query.setQualifier(ExpressionFactory.matchExp(keyName, id));

      List<T> fileList = query.fetchList();

      if (fileList == null || fileList.isEmpty()) {
        return true;
      }

      // キーフィールドに紐づくファイルを削除
      Database.deleteAll(fileList);

      List<String> fpaths = new ArrayList<String>();
      for (T file : fileList) {
        fpaths.add(file.getFilePath());
      }

      if (!fpaths.isEmpty()) {
        // 保存されている添付ファイルを削除する
        for (String path : fpaths) {
          ALStorageService.deleteFile(getSaveDirPath(ALEipUtils
            .getUserId(rundata))
            + path);
        }
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    return true;
  }

  /**
   * 
   * @param attachmentFileNameList
   * @return
   */
  public List<Integer> getRequestedHasFileIdList(
      List<FileuploadBean> attachmentFileNameList) {
    List<Integer> idlist = new ArrayList<Integer>();
    FileuploadBean filebean = null;
    if (attachmentFileNameList != null) {
      int size = attachmentFileNameList.size();
      for (int i = 0; i < size; i++) {
        filebean = attachmentFileNameList.get(i);
        if (!filebean.isNewFile()) {
          int index = filebean.getFileId();
          idlist.add(Integer.valueOf(index));
        }
      }
    }
    return idlist;
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
   * ユーザ毎のルート保存先（絶対パス）を取得します。
   * 
   * @param uid
   *          ユーザーID
   * @return パス
   */
  public static String getSaveDirPath(int uid) {
    return ALStorageService.getDocumentPath(FOLDER_FILEDIR, CATEGORY_KEY
      + ALStorageService.separator()
      + uid);
  }

}
