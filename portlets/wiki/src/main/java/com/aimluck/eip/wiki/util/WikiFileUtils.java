/*
 * Aipo is a groupware program developed by TOWN, Inc.
 * Copyright (C) 2004-2015 TOWN, Inc.
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
package com.aimluck.eip.wiki.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.commons.io.IOUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTWiki;
import com.aimluck.eip.cayenne.om.portlet.EipTWikiFile;
import com.aimluck.eip.common.ALFileNotRemovedException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.fileupload.util.FileuploadUtils;
import com.aimluck.eip.fileupload.util.FileuploadUtils.ShrinkImageSet;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * Wikiのユーティリティクラスです。 <BR>
 * 
 */
public class WikiFileUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(WikiFileUtils.class.getName());

  /** デフォルトエンコーディングを表わすシステムプロパティのキー */
  public static final String FILE_ENCODING = JetspeedResources.getString(
    "content.defaultencoding",
    "UTF-8");

  /** WIKIの添付ファイルを保管するディレクトリの指定 */
  private static final String FOLDER_FILEDIR_WIKI = JetspeedResources
    .getString("aipo.filedir", "");

  /** 掲示板の添付ファイルを保管するディレクトリのカテゴリキーの指定 */
  private static final String CATEGORY_KEY = JetspeedResources.getString(
    "aipo.wiki.categorykey",
    "");

  /**
   * 添付ファイルを取得します。
   * 
   * @param uid
   * @return
   */
  public static ArrayList<FileuploadLiteBean> getFileuploadList(RunData rundata) {
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

    ArrayList<FileuploadLiteBean> fileNameList =
      new ArrayList<FileuploadLiteBean>();
    FileuploadLiteBean filebean = null;

    // 新規にアップロードされたファイルの処理
    if (newfileids.size() > 0) {
      String folderName =
        rundata.getParameters().getString(
          FileuploadUtils.KEY_FILEUPLOAD_FODLER_NAME);
      if (folderName == null || folderName.equals("")) {
        return null;
      }

      for (String newfileid : newfileids) {
        if ("".equals(newfileid)) {
          continue;
        }
        int fileid = 0;
        try {
          fileid = Integer.parseInt(newfileid);
        } catch (Exception e) {
          continue;
        }

        if (fileid == 0) {
          filebean = new FileuploadLiteBean();
          filebean.initField();
          filebean.setFolderName("photo");
          filebean.setFileName(ALLocalizationUtils
            .getl10n("WIKI_PREVIOUS_PICTURE_FILE"));
          fileNameList.add(filebean);
        } else {
          BufferedReader reader = null;
          try {
            reader =
              new BufferedReader(new InputStreamReader(ALStorageService
                .getFile(
                  FileuploadUtils.FOLDER_TMP_FOR_ATTACHMENT_FILES,
                  ALEipUtils.getUserId(rundata)
                    + ALStorageService.separator()
                    + folderName,
                  fileid + FileuploadUtils.EXT_FILENAME), FILE_ENCODING));
            String line = reader.readLine();
            if (line == null || line.length() <= 0) {
              continue;
            }
            filebean = new FileuploadLiteBean();
            filebean.initField();
            filebean.setFolderName(newfileid);
            filebean.setFileId(fileid);
            filebean.setFileName(line);
            fileNameList.add(filebean);
          } catch (Exception e) {
            logger.error("getFileuploadList", e);
          } finally {
            IOUtils.closeQuietly(reader);
          }
        }
      }
    }

    // すでにあるファイルの処理
    if (hadfileids.size() > 0) {
      ArrayList<Integer> hadfileidsValue = new ArrayList<Integer>();
      for (String hadfileid : hadfileids) {
        int fileid = 0;
        try {
          fileid = Integer.parseInt(hadfileid);
          hadfileidsValue.add(fileid);
        } catch (Exception e) {
          continue;
        }
      }

      try {
        SelectQuery<EipTWikiFile> reqquery = Database.query(EipTWikiFile.class);
        Expression reqexp1 =
          ExpressionFactory.inDbExp(
            EipTWikiFile.FILE_ID_PK_COLUMN,
            hadfileidsValue);
        reqquery.setQualifier(reqexp1);
        List<EipTWikiFile> requests = reqquery.fetchList();
        for (EipTWikiFile file : requests) {
          filebean = new FileuploadBean();
          filebean.initField();
          filebean.setFileId(file.getFileId());
          filebean.setFileName(file.getFileName());
          filebean.setFlagNewFile(false);
          fileNameList.add(filebean);
        }
      } catch (Exception e) {
        logger.error("WikiUtils.getFileuploadList", e);
      }
    }
    return fileNameList;
  }

  public static boolean insertFileDataDelegate(RunData rundata,
      Context context, EipTWiki wiki, List<FileuploadLiteBean> fileuploadList,
      String folderName, List<String> msgList) {
    if (fileuploadList == null || fileuploadList.size() <= 0) {
      fileuploadList = new ArrayList<FileuploadLiteBean>();
    }

    int uid = ALEipUtils.getUserId(rundata);
    String orgId = Database.getDomainName();

    List<Integer> hadfileids = new ArrayList<Integer>();
    for (FileuploadLiteBean file : fileuploadList) {
      if (!file.isNewFile()) {
        hadfileids.add(file.getFileId());
      }
    }

    SelectQuery<EipTWikiFile> dbquery = Database.query(EipTWikiFile.class);
    dbquery.andQualifier(ExpressionFactory.matchExp(
      EipTWikiFile.WIKI_ID_PROPERTY,
      wiki.getWikiId()));
    List<EipTWikiFile> existsFiles = dbquery.fetchList();
    List<EipTWikiFile> delFiles = new ArrayList<EipTWikiFile>();
    for (EipTWikiFile file : existsFiles) {
      if (!hadfileids.contains(file.getFileId())) {
        delFiles.add(file);
      }
    }

    // ローカルファイルに保存されているファイルを削除する．
    if (delFiles.size() > 0) {
      int delsize = delFiles.size();
      for (int i = 0; i < delsize; i++) {
        ALStorageService.deleteFile(WikiFileUtils.getSaveDirPath(orgId, uid)
          + (delFiles.get(i)).getFilePath());
      }
      // データベースから添付ファイルのデータ削除
      Database.deleteAll(delFiles);
    }

    // ファイル追加処理
    try {
      for (FileuploadLiteBean filebean : fileuploadList) {
        if (!filebean.isNewFile()) {
          continue;
        }

        // サムネイル処理
        String[] acceptExts = ImageIO.getWriterFormatNames();
        ShrinkImageSet shrinkImageSet =
          FileuploadUtils.getBytesShrinkFilebean(
            orgId,
            folderName,
            uid,
            filebean,
            acceptExts,
            FileuploadUtils.DEF_THUMBNAIL_WIDTH,
            FileuploadUtils.DEF_THUMBNAIL_HEIGHT,
            msgList,
            true);

        String filename = "0_" + String.valueOf(System.nanoTime());

        // 新規オブジェクトモデル
        EipTWikiFile file = Database.create(EipTWikiFile.class);
        // 所有者
        file.setOwnerId(Integer.valueOf(uid));
        // トピックID
        file.setEipTWiki(wiki);
        // ファイル名
        file.setFileName(filebean.getFileName());
        // ファイルパス
        file.setFilePath(WikiFileUtils.getRelativePath(filename));
        // サムネイル画像
        if (shrinkImageSet != null && shrinkImageSet.getShrinkImage() != null) {
          file.setFileThumbnail(shrinkImageSet.getShrinkImage());
        }
        // 作成日
        file.setCreateDate(Calendar.getInstance().getTime());
        // 更新日
        file.setUpdateDate(Calendar.getInstance().getTime());

        if (shrinkImageSet != null && shrinkImageSet.getFixImage() != null) {
          // ファイルの作成
          ALStorageService.createNewFile(new ByteArrayInputStream(
            shrinkImageSet.getFixImage()), FOLDER_FILEDIR_WIKI
            + ALStorageService.separator()
            + Database.getDomainName()
            + ALStorageService.separator()
            + CATEGORY_KEY
            + ALStorageService.separator()
            + uid
            + ALStorageService.separator()
            + filename);
        } else {
          // ファイルの移動
          ALStorageService.copyTmpFile(uid, folderName, String.valueOf(filebean
            .getFileId()), FOLDER_FILEDIR_WIKI, CATEGORY_KEY
            + ALStorageService.separator()
            + uid, filename);
        }
      }

      // 添付ファイル保存先のフォルダを削除
      ALStorageService.deleteTmpFolder(uid, folderName);
    } catch (Exception e) {
      Database.rollback();
      logger.error("WikiFileUtils.insertFileDataDelegate", e);
      return false;
    }
    return true;
  }

  /**
   * ユーザ毎のルート保存先（絶対パス）を取得します。
   * 
   * @param uid
   * @return
   */
  public static String getSaveDirPath(String orgId, int uid) {
    return ALStorageService.getDocumentPath(FOLDER_FILEDIR_WIKI, CATEGORY_KEY
      + ALStorageService.separator()
      + uid);
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

  public static List<FileuploadBean> getAttachmentFiles(Integer wikiId) {
    SelectQuery<EipTWikiFile> query = Database.query(EipTWikiFile.class);
    query.where(Operations.eq(EipTWikiFile.WIKI_ID_PROPERTY, wikiId));
    query.orderAscending(EipTWikiFile.UPDATE_DATE_PROPERTY);
    query.orderAscending(EipTWikiFile.FILE_PATH_PROPERTY);
    List<EipTWikiFile> result = query.fetchList();

    List<FileuploadBean> beanlist = new ArrayList<FileuploadBean>();
    for (EipTWikiFile file : result) {
      FileuploadBean bean = new FileuploadBean();
      bean.initField();
      bean.setFileId(file.getFileId());
      bean.setFileName(file.getFileName());
      bean.setFlagNewFile(false);
      javax.activation.DataHandler hData =
        new javax.activation.DataHandler(new javax.activation.FileDataSource(
          file.getFileName()));
      if (hData != null) {
        bean.setContentType(hData.getContentType());
      }
      bean.setIsImage(FileuploadUtils.isImage(file.getFileName()));
      beanlist.add(bean);
    }
    return beanlist;
  }

  /**
   * ファイルオブジェクトモデルを取得します。 <BR>
   * 
   * @param rundata
   * @param context
   * @return
   */
  public static EipTWikiFile getEipTWikiFile(RunData rundata) {
    int attachmentIndex = rundata.getParameters().getInt("attachmentIndex", -1);
    if (attachmentIndex < 0) {
      return null;
    }
    SelectQuery<EipTWikiFile> query = Database.query(EipTWikiFile.class);
    Expression exp =
      ExpressionFactory.matchDbExp(EipTWikiFile.FILE_ID_PK_COLUMN, Integer
        .valueOf(attachmentIndex));
    query.andQualifier(exp);
    return query.fetchSingle();
  }

  public static void deleteFiles(Integer wikiId)
      throws ALFileNotRemovedException {
    /** eip_t_wiki_file has delete cascade, not delete files from database here */
    if (null == wikiId) {
      return;
    }
    String orgId = Database.getDomainName();
    SelectQuery<EipTWikiFile> query = Database.query(EipTWikiFile.class);
    query.setQualifier(ExpressionFactory.matchExp(
      EipTWikiFile.WIKI_ID_PROPERTY,
      wikiId));
    List<EipTWikiFile> fileList = query.fetchList();
    try {
      for (EipTWikiFile file : fileList) {
        ALStorageService.deleteFile(getSaveDirPath(orgId, file.getOwnerId())
          + file.getFilePath());
      }
    } catch (Exception e) {
      throw new ALFileNotRemovedException(e);
    }
  }
}
