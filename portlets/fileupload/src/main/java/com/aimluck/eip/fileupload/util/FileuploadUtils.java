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

package com.aimluck.eip.fileupload.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import com.aimluck.eip.services.storage.ALStorageService;
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
          new BufferedReader(new InputStreamReader(ALStorageService.getFile(
            FOLDER_TMP_FOR_ATTACHMENT_FILES,
            ALEipUtils.getUserId(rundata)
              + ALStorageService.separator()
              + folderName,
            fileids[i] + EXT_FILENAME), FILE_ENCODING));
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

      InputStream is = null;
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      try {
        is =
          ALStorageService.getFile(FOLDER_TMP_FOR_ATTACHMENT_FILES, uid
            + ALStorageService.separator()
            + folderName, file_name);
        byte b[] = new byte[512];
        int len = -1;
        while ((len = is.read(b, 0, b.length)) != -1) {
          output.write(b, 0, len);
        }
        output.flush();
      } finally {
        if (is != null) {
          is.close();
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
    InputStream is = null;

    try {

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

      is =
        ALStorageService.getFile(FOLDER_TMP_FOR_ATTACHMENT_FILES, uid
          + ALStorageService.separator()
          + folderName, String.valueOf(fileBean.getFileId()));

      BufferedImage orgImage = ImageIO.read(is);
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
        if (is != null) {
          is.close();
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
