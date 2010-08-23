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
package com.aimluck.eip.fileupload.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.fileupload.beans.FileuploadLiteBean;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * ファイルアップロードのユーティリティクラスです。 <BR>
 * 
 */
public class FileuploadUtils {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(FileuploadUtils.class.getName());

  /** KEY（アップロードしたファイルID一覧） */
  public static final String KEY_FILEUPLOAD_ID_LIST = "attachments";

  /** KEY（アップロード先のフォルダ名） */
  public static final String KEY_FILEUPLOAD_FODLER_NAME = "folderName";

  /** 添付ファイル名を保存するファイルの拡張子 */
  public static final String EXT_FILENAME = ".txt";

  /** 一時添付ファイル名 */
  public static final String ATTACHMENT_TEMP_FILENAME = "file";

  /** 一時添付ファイル名を記録するファイル名 */
  public static final String ATTACHMENT_TEMP_FILENAME_REMAIND = "file.txt";

  /** 保存用ファイル名フォーマット */
  public static final String DEFAULT_FILENAME_DATE_FORMAT = "yyyyMMddHHmmssSSS";

  /** 画像サムネイルのサイズ（横幅） */
  public static final int DEF_THUMBNAIL_WIDTH = 86;

  /** 画像サムネイルのサイズ（縦幅） */
  public static final int DEF_THUMBNAIL_HEIGTH = 86;

  /** 現在の添付ファイル数 */
  public static final String KEY_NOW_SIZE = "nsize";

  /** 添付可能数 */
  public static final String KEY_MAX_SIZE = "msize";

  /** デフォルトエンコーディングを表わすシステムプロパティのキー */
  public static final String FILE_ENCODING = JetspeedResources.getString(
    "content.defaultencoding",
    "UTF-8");

  /** アカウントの添付ファイルを一時保管するディレクトリの指定 */
  public static final String FOLDER_TMP_FOR_ATTACHMENT_FILES =
    JetspeedResources.getString("aipo.tmp.fileupload.attachment.directory", "");

  // private static int MAP_MAX = 1048576; // = 2^20

  /**
   * 添付ファイル保存先のユーザのルートフォルダ
   * 
   * @param org_id
   * @param userId
   * @return
   */
  public static File getRootFolder(String org_id, int userId) {
    String rootPath =
      FileuploadUtils.FOLDER_TMP_FOR_ATTACHMENT_FILES
        + File.separator
        + org_id
        + File.separator
        + userId
        + File.separator;
    File folder = new File(rootPath);
    if (!folder.exists()) {
      if (!folder.mkdirs()) {
        return null;
      }
    }
    return folder;
  }

  /**
   * 添付ファイル保存先
   * 
   * @param org_id
   * @param userId
   * @return
   */
  public static File getFolder(String org_id, int userId, String folderName) {
    String path =
      FileuploadUtils.FOLDER_TMP_FOR_ATTACHMENT_FILES
        + File.separator
        + org_id
        + File.separator
        + userId
        + File.separator
        + folderName;
    File folder = new File(path);
    if (!folder.exists()) {
      folder.mkdirs();
    }
    return folder;
  }

  /**
   * 保存されている添付ファイルへのフルパス
   * 
   * @param org_id
   * @param userId
   * @return
   */
  public static File getAbsolutePath(String org_id, int userId,
      String folderName, int fileid) {
    File folder = getFolder(org_id, userId, folderName);
    return new File(folder.getAbsolutePath() + File.separator + fileid);
  }

  /**
   * 新しいファイル名を生成する．
   * 
   * @return
   */
  public static String getNewFileName(String folderPath) {
    int count = 0;

    SimpleDateFormat simpleDateFormat =
      new SimpleDateFormat(DEFAULT_FILENAME_DATE_FORMAT);
    Date date = new Date();
    String tmpname = simpleDateFormat.format(date);
    String path = folderPath + File.separator;
    File folderpath = new File(path);
    if (!folderpath.exists()) {
      folderpath.mkdirs();
    }

    File file = null;
    String newFileName = null;
    String newFilePath = null;

    while (true) {
      newFileName = tmpname + count;
      newFilePath = path + newFileName;
      file = new File(newFilePath);
      if (!file.exists()) {
        break;
      }
      count += 1;
    }
    return newFileName;
  }

  /**
   * 
   * @param folder
   * @return
   */
  public static String getNewAttachmentFolderName(File folder) {
    int maxNum = 1;
    String[] filenames = folder.list();
    File file = null;
    int tmpInt = 1;
    int length = filenames.length;
    for (int i = 0; i < length; i++) {
      file = new File(folder.getAbsolutePath() + File.separator + filenames[i]);
      if (file.isDirectory()) {
        try {
          tmpInt = Integer.parseInt(file.getName());
          if (maxNum <= tmpInt) {
            maxNum = tmpInt + 1;
          }
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      }
    }
    return Integer.toString(maxNum);
  }

  public static String getRealFileName(String name) {
    String filename = null;
    int index = name.lastIndexOf("/");
    if (index < 0) {
      index = name.lastIndexOf("\\");
    }
    if (index > 0) {
      filename = name.substring(index + 1, name.length());
    } else {
      filename = name;
    }
    return filename;
  }

  public static List<FileuploadLiteBean> getFileuploadList(RunData rundata) {
    String[] fileids =
      rundata.getParameters().getStrings(KEY_FILEUPLOAD_ID_LIST);
    if (fileids == null) {
      return null;
    }

    List<FileuploadLiteBean> fileNameList = new ArrayList<FileuploadLiteBean>();
    FileuploadLiteBean filebean = null;
    int fileid = 0;
    int length = fileids.length;

    if (length == 1 && fileids[0] != null && !"".equals(fileids[0])) {
      try {
        fileid = Integer.parseInt(fileids[0]);
      } catch (Exception e) {
        return null;
      }

      if (fileid == 0) {
        filebean = new FileuploadLiteBean();
        filebean.initField();
        filebean.setFolderName("photo");
        filebean.setFileName("以前の写真ファイル");
        fileNameList.add(filebean);
        return fileNameList;
      }
    }

    String folderName =
      rundata.getParameters().getString(
        FileuploadUtils.KEY_FILEUPLOAD_FODLER_NAME);
    if (folderName == null || folderName.equals("")) {
      return null;
    }

    String org_id = DatabaseOrmService.getInstance().getOrgId(rundata);
    File folder = getFolder(org_id, ALEipUtils.getUserId(rundata), folderName);
    String folderpath = folder.getAbsolutePath();

    for (int i = 0; i < length; i++) {
      if (fileids[i] == null || fileids[i].equals("")) {
        continue;
      }

      try {
        fileid = Integer.parseInt(fileids[i]);
      } catch (Exception e) {
        continue;
      }

      BufferedReader reader = null;
      try {
        reader =
          new BufferedReader(new InputStreamReader(
            new FileInputStream(folderpath
              + File.separator
              + fileids[i]
              + EXT_FILENAME),
            FILE_ENCODING));
        String line = reader.readLine();
        if (line == null || line.length() <= 0) {
          continue;
        }

        filebean = new FileuploadLiteBean();
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

    return fileNameList;
  }

  /**
   * ファイルをコピーします。<br />
   * from "c:\\abc.txt"<br />
   * to "c:\\tmp\\abc.txt"
   * 
   * @return
   */
  public static boolean copyFile(File from, File to) {
    boolean res = true;
    FileChannel srcChannel = null;
    FileChannel destChannel = null;

    try {
      srcChannel = new FileInputStream(from).getChannel();
      destChannel = new FileOutputStream(to).getChannel();
      destChannel.transferFrom(srcChannel, 0, srcChannel.size());
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    } finally {
      if (destChannel != null) {
        try {
          destChannel.close();
        } catch (IOException ex) {
          logger.error("Exception", ex);
          res = false;
        }
      }
      if (srcChannel != null) {
        try {
          srcChannel.close();
        } catch (IOException ex) {
          logger.error("Exception", ex);
          res = false;
        }
      }
    }

    return res;
  }

  /**
   * 指定したフォルダの容量を取得します。
   * 
   * @param folderPath
   * @return
   */
  public static long getFolderSize(String folderPath) {
    if (folderPath == null || folderPath.equals("")) {
      return 0;
    }

    File folder = new File(folderPath);
    if (!folder.exists()) {
      return 0;
    }
    if (folder.isFile()) {
      return getFileSize(folder);
    }
    int fileSizeSum = 0;
    File file = null;
    String[] files = folder.list();
    int length = files.length;
    for (int i = 0; i < length; i++) {
      file = new File(folderPath + File.separator + files[i]);
      if (file.isFile()) {
        fileSizeSum += getFileSize(file);
      } else if (file.isDirectory()) {
        fileSizeSum += getFolderSize(file.getAbsolutePath());
      }
    }
    return fileSizeSum;
  }

  /**
   * 指定したファイルのサイズを取得する．
   * 
   * @param file
   * @return
   */
  public static int getFileSize(File file) {
    if (file == null) {
      return -1;
    }

    FileInputStream fileInputStream = null;
    int size = -1;
    try {
      fileInputStream = new FileInputStream(file);
      BufferedInputStream input = new BufferedInputStream(fileInputStream);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      byte[] b = new byte[512];
      int len = -1;
      while ((len = input.read(b)) != -1) {
        output.write(b, 0, len);
        output.flush();
      }
      input.close();
      fileInputStream.close();

      byte[] fileArray = output.toByteArray();
      if (fileArray != null) {
        size = fileArray.length;
      } else {
        size = -1;
      }
      output.close();
    } catch (FileNotFoundException e) {
      return -1;
    } catch (IOException ioe) {
      return -1;
    }
    return size;
  }

  /**
   * 指定したフォルダ以下を全て削除する．
   * 
   * @return
   */
  public static boolean deleteFolder(File folder) {
    if (folder == null) {
      return true;
    }

    String[] files = folder.list();
    if (files == null) {
      folder.delete();
      return true;
    }

    int length = files.length;
    if (length <= 0) {
      folder.delete();
      return true;
    }

    String folderPath = folder.getAbsolutePath() + File.separator;
    File tmpfile = null;
    for (int i = 0; i < length; i++) {
      tmpfile = new File(folderPath + files[i]);
      if (tmpfile.exists()) {
        if (tmpfile.isFile()) {
          tmpfile.delete();
        } else if (tmpfile.isDirectory()) {
          deleteFolder(tmpfile);
        }
      }
    }

    folder.delete();
    return true;
  }

  /**
   * @see com.aimluck.eip.common.ALAbstractFormData#updateFormData(org.apache.turbine.util.RunData,
   *      org.apache.velocity.context.Context, java.util.ArrayList)
   */
  public static boolean deleteAttachments(String org_id, int userId,
      String folderName, List<FileuploadLiteBean> attachmentFileNameList) {
    File folder = null;
    try {
      String folderpath =
        FileuploadUtils.FOLDER_TMP_FOR_ATTACHMENT_FILES
          + File.separator
          + org_id
          + File.separator
          + userId
          + File.separator
          + folderName;
      folder = new File(folderpath);
      if (!folder.exists()) {
        return true;
      }
      String[] uploadedFolders = folder.list();
      int uploadedfolders_length = uploadedFolders.length;
      int length = attachmentFileNameList.size();
      FileuploadLiteBean attachmentFile = null;
      String uploadedFolderName = null;
      File uploadedFolder = null;
      boolean isDelete = true;
      for (int i = 0; i < uploadedfolders_length; i++) {
        isDelete = true;
        uploadedFolder =
          new File(folderpath + File.separator + uploadedFolders[i]);
        if (uploadedFolder.isDirectory()) {
          uploadedFolderName = uploadedFolder.getName();
          for (int j = 0; j < length; j++) {
            attachmentFile = attachmentFileNameList.get(j);
            if (uploadedFolderName.equals(attachmentFile.getFolderName())) {
              isDelete = false;
              break;
            }
          }
          if (isDelete) {
            // 送信されなかったファイルを削除ファイルとして，ローカルフォルダから削除する．
            FileuploadUtils.deleteFolder(uploadedFolder);
          }
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
   * @param org_id
   * @param folderName
   * @param uid
   * @param fileBean
   * @param acceptExts
   *          String[] ext = { ".jpg", ".jpeg" };
   * @param msgList
   * @return
   */
  public static byte[] getBytesFilebean(String org_id, String folderName,
      int uid, FileuploadLiteBean fileBean, String[] acceptExts,
      List<String> msgList) {
    byte[] result = null;
    try {
      File folder = FileuploadUtils.getFolder(org_id, uid, folderName);

      String file_name = fileBean.getFileName();

      if (acceptExts != null && acceptExts.length > 0) {
        // 拡張子をチェックする．
        // ファイルのヘッダで識別するとベスト．
        boolean isAccept = false;
        String tmpExt = null;
        int len = acceptExts.length;
        for (int i = 0; i < len; i++) {
          if (!acceptExts[i].startsWith(".")) {
            tmpExt = "." + acceptExts[i];
          }
          if (file_name.toLowerCase().endsWith(tmpExt)) {
            isAccept = true;
          }
        }
        if (!isAccept) {
          // 期待しない拡張子の場合は，null を返す．
          return null;
        }
      }

      StringBuffer srcfilepath = new StringBuffer(folder.getAbsolutePath());
      srcfilepath.append(File.separator).append(fileBean.getFileId());

      FileInputStream fileinput = null;
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      try {
        fileinput = new FileInputStream(srcfilepath.toString());
        byte b[] = new byte[512];
        int len = -1;
        while ((len = fileinput.read(b, 0, b.length)) != -1) {
          output.write(b, 0, len);
        }
        output.flush();
      } finally {
        if (fileinput != null) {
          fileinput.close();
        }
      }

      result = output.toByteArray();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
    return result;
  }

  /**
   * 縮小した画像のバイナリを返す。
   * 
   * @param org_id
   * @param folderName
   * @param uid
   * @param fileBean
   * @param acceptExts
   * @param msgList
   * @return
   */
  public static byte[] getBytesShrinkFilebean(String org_id, String folderName,
      int uid, FileuploadLiteBean fileBean, String[] acceptExts, int width,
      int height, List<String> msgList) {

    byte[] result = null;
    FileInputStream fileinput = null;

    try {
      File folder = FileuploadUtils.getFolder(org_id, uid, folderName);

      String file_name = fileBean.getFileName();

      if (acceptExts != null && acceptExts.length > 0) {
        // 拡張子をチェックする．
        // ファイルのヘッダで識別するとベスト．
        boolean isAccept = false;
        String tmpExt = null;
        int len = acceptExts.length;
        for (int i = 0; i < len; i++) {
          if (!acceptExts[i].startsWith(".")) {
            tmpExt = "." + acceptExts[i];
          }
          if (file_name.toLowerCase().endsWith(tmpExt)) {
            isAccept = true;
            break;
          }
        }
        if (!isAccept) {
          // 期待しない拡張子の場合は，null を返す．
          return null;
        }
      }

      StringBuffer srcfilepath = new StringBuffer(folder.getAbsolutePath());
      srcfilepath.append(File.separator).append(fileBean.getFileId());

      fileinput = new FileInputStream(srcfilepath.toString());

      BufferedImage orgImage = ImageIO.read(fileinput);
      BufferedImage shrinkImage =
        FileuploadUtils.shrinkImage(orgImage, width, height);
      Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix("jpg");
      ImageWriter writer = writers.next();

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImageOutputStream ios = ImageIO.createImageOutputStream(out);
      writer.setOutput(ios);
      writer.write(shrinkImage);

      result = out.toByteArray();
    } catch (Exception e) {
      logger.error("Exception", e);
      result = null;
    } finally {
      try {
        if (fileinput != null) {
          fileinput.close();
        }
      } catch (Exception e) {
        logger.error("Exception", e);
        result = null;
      }
    }
    return result;
  }

  /**
   * Java1.5：BMP, bmp, jpeg, wbmp, gif, png, JPG, jpg, WBMP, JPEG
   * 
   * @param fileType
   * @return
   */
  public static boolean isImage(String fileName) {
    if (fileName == null || "".equals(fileName)) {
      return false;
    }

    int index = fileName.lastIndexOf(".");
    if (index < 1) {
      return false;
    }

    String fileType = getFileTypeName(fileName);

    String[] format = ImageIO.getWriterFormatNames();
    int len = format.length;
    for (int i = 0; i < len; i++) {
      if (format[i].equals(fileType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * ファイル名からファイルの拡張子を取得する。
   * 
   * @param fileName
   * @return
   */
  public static String getFileTypeName(String fileName) {
    if (fileName == null || "".equals(fileName)) {
      return null;
    }

    int index = fileName.lastIndexOf(".");
    if (index < 1) {
      return null;
    }

    return fileName.substring(index + 1, fileName.length());
  }

  /**
   * 縦横の縮小率で小さい方を縮小率とする。
   * 
   * @param imgfile
   * @param dim
   * @return
   */
  public static BufferedImage shrinkImage(BufferedImage imgfile, int width,
      int height) {

    int iwidth = imgfile.getWidth();
    int iheight = imgfile.getHeight();

    double ratio =
      Math.min((double) width / (double) iwidth, (double) height
        / (double) iheight);
    int shrinkedWidth = (int) (iwidth * ratio);
    int shrinkedHeight = (int) (iheight * ratio);

    // イメージデータを縮小する
    Image targetImage =
      imgfile.getScaledInstance(
        shrinkedWidth,
        shrinkedHeight,
        Image.SCALE_AREA_AVERAGING);
    BufferedImage tmpImage =
      new BufferedImage(
        targetImage.getWidth(null),
        targetImage.getHeight(null),
        BufferedImage.TYPE_INT_RGB);
    Graphics2D g = tmpImage.createGraphics();
    g.drawImage(targetImage, 0, 0, null);

    return tmpImage;
  }
}
