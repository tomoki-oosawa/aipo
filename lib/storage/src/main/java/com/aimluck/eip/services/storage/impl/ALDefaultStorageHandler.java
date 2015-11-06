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
package com.aimluck.eip.services.storage.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.storage.ALStorageHandler;

/**
 *
 */
public class ALDefaultStorageHandler extends ALStorageHandler {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALDefaultStorageHandler.class.getName());

  private static final String EXT_FILENAME = ".txt";

  private static ALStorageHandler instance;

  public static ALStorageHandler getInstance() {
    if (instance == null) {
      instance = new ALDefaultStorageHandler();
    }
    return instance;
  }

  @Override
  public void saveFile(InputStream is, String folderPath, String filename) {
    File path = new File(getAbsolutePath(folderPath));

    if (!path.exists()) {
      try {
        path.mkdirs();
      } catch (Exception e) {
        logger.error("Can't create directory...:" + path);
      }
    }

    String filepath = path + separator() + filename;
    File file = new File(filepath);
    FileOutputStream os = null;
    try {
      if (!file.exists()) {
        if (!file.createNewFile()) {
          throw new RuntimeException("createNewFile error");
        }
      }
      os = new FileOutputStream(filepath);
      int c;
      while ((c = is.read()) != -1) {
        os.write(c);
      }
    } catch (IOException e) {
      logger.error("ALDefaultStorageHandler.saveFile", e);
    } finally {
      if (os != null) {
        try {
          os.flush();
          os.close();
        } catch (Throwable e) {
          // ignore
        }
      }
    }
  }

  /**
   * @param inputStream
   * @param filepath
   */
  @Override
  public void createNewFile(InputStream is, String filepath) {
    File file = new File(getAbsolutePath(filepath));

    if (!file.exists()) {
      try {
        String parent = file.getParent();
        if (parent != null) {
          File dir = new File(parent);
          if (!dir.exists()) {
            if (!dir.mkdirs()) {
              throw new RuntimeException("mkdir error");
            }
          }
        }
        if (!file.createNewFile()) {
          throw new RuntimeException("createNewFile error");
        }
      } catch (IOException e) {
        logger.error("Can't create file...:" + file, e);
      }
    }

    FileOutputStream os = null;
    try {
      os = new FileOutputStream(getAbsolutePath(filepath));
      int c;
      while ((c = is.read()) != -1) {
        os.write(c);
      }
    } catch (IOException e) {
      logger.error("ALDefaultStorageHandler.createNewFile", e);
    } finally {
      if (os != null) {
        try {
          os.flush();
          os.close();
        } catch (Throwable e) {
          // ignore
        }
      }
    }
  }

  /**
   * @param is
   * @param folderPath
   * @param filename
   */
  @Override
  public void createNewFile(InputStream is, String folderPath, String filename) {
    File path = new File(getAbsolutePath(folderPath));

    if (!path.exists()) {
      try {
        path.mkdirs();
      } catch (Exception e) {
        logger.error("Can't create directory...:" + path);
      }
    }

    String filepath = path + separator() + filename;
    File file = new File(filepath);
    FileOutputStream os = null;
    try {
      if (!file.createNewFile()) {
        throw new RuntimeException("createNewFile error");
      }
      os = new FileOutputStream(filepath);
      int c;
      while ((c = is.read()) != -1) {
        os.write(c);
      }
    } catch (IOException e) {
      logger.error("ALDefaultStorageHandler.createNewFile", e);
    } finally {
      if (os != null) {
        try {
          os.flush();
          os.close();
        } catch (Throwable e) {
          // ignore
        }
      }
    }
  }

  /**
   * @param is
   * @param rootPath
   * @param fileName
   */
  @Override
  public void createNewTmpFile(InputStream is, int uid, String dir,
      String fileName, String realFileName) {

    File path =
      new File(getAbsolutePath(FOLDER_TMP_FOR_ATTACHMENT_FILES)
        + separator()
        + Database.getDomainName()
        + separator()
        + uid
        + separator()
        + dir);

    if (!path.exists()) {
      try {
        path.mkdirs();
      } catch (Exception e) {
        logger.error("Can't create directory...:" + path);
      }
    }

    // バッファリング修正START
    try {
      String filepath = path + separator() + fileName;
      File file = new File(filepath);
      if (!file.createNewFile()) {
        throw new RuntimeException("createNewFile error");
      }
      int c;
      BufferedInputStream bis = null;
      BufferedOutputStream bos = null;
      try {
        bis = new BufferedInputStream(is, 1024 * 1024);
        bos =
          new BufferedOutputStream(new FileOutputStream(filepath), 1024 * 1024);

        while ((c = bis.read()) != -1) {
          bos.write(c);
        }
      } catch (IOException e) {
        logger.error("ALDefaultStorageHandler.createNewTmpFile", e);
      } finally {

        IOUtils.closeQuietly(bis);
        IOUtils.closeQuietly(bos);
      }
      // バッファリング修正END

      PrintWriter w = null;
      try {
        w =
          new PrintWriter(new OutputStreamWriter(new FileOutputStream(filepath
            + EXT_FILENAME), "UTF-8"));
        w.println(realFileName);
      } catch (IOException e) {
        logger.error("ALDefaultStorageHandler.createNewTmpFile", e);
      } finally {
        if (w != null) {
          try {
            w.flush();
            w.close();
          } catch (Throwable e) {
            // ignore
          }
        }
      }
    } catch (FileNotFoundException e) {
      logger.error("ALDefaultStorageHandler.createNewTmpFile", e);
    } catch (IOException e) {
      logger.error("ALDefaultStorageHandler.createNewTmpFile", e);
    }
  }

  @Override
  public boolean copyFile(String srcRootPath, String srcDir,
      String srcFileName, String destRootPath, String destDir,
      String destFileName) {

    File srcPath =
      new File(getAbsolutePath(srcRootPath)
        + separator()
        + Database.getDomainName()
        + separator()
        + srcDir);

    if (!srcPath.exists()) {
      try {
        srcPath.mkdirs();
      } catch (Exception e) {
        logger.error("Can't create directory...:" + srcPath);
        return false;
      }
    }

    File destPath =
      new File(getAbsolutePath(destRootPath)
        + separator()
        + Database.getDomainName()
        + separator()
        + destDir);

    if (!destPath.exists()) {
      try {
        destPath.mkdirs();
      } catch (Exception e) {
        logger.error("Can't create directory...:" + destPath);
        return false;
      }
    }

    File from = new File(srcPath + separator() + srcFileName);
    File to = new File(destPath + separator() + destFileName);

    boolean res = true;
    FileChannel srcChannel = null;
    FileChannel destChannel = null;

    try {
      srcChannel = new FileInputStream(from).getChannel();
      destChannel = new FileOutputStream(to).getChannel();
      destChannel.transferFrom(srcChannel, 0, srcChannel.size());
    } catch (Exception ex) {
      logger.error("ALDefaultStorageHandler.copyFile", ex);
      res = false;
    } finally {
      if (destChannel != null) {
        try {
          destChannel.close();
        } catch (IOException ex) {
          logger.error("ALDefaultStorageHandler.copyFile", ex);
          res = false;
        }
      }
      if (srcChannel != null) {
        try {
          srcChannel.close();
        } catch (IOException ex) {
          logger.error("ALDefaultStorageHandler.copyFile", ex);
          res = false;
        }
      }
    }

    return res;
  }

  /**
   * @param rootPath
   * @param dir
   */
  @Override
  public long getFolderSize(String rootPath, String dir) {
    return getFolderSize(rootPath
      + separator()
      + Database.getDomainName()
      + separator()
      + dir);
  }

  protected long getFolderSize(String folderPath) {
    if (folderPath == null || folderPath.equals("")) {
      return 0;
    }

    File folder = new File(getAbsolutePath(folderPath));
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
      file = new File(getAbsolutePath(folderPath) + separator() + files[i]);
      if (file.isFile()) {
        fileSizeSum += getFileSize(file);
      } else if (file.isDirectory()) {
        fileSizeSum += getFolderSize(file.getAbsolutePath());
      }
    }
    return fileSizeSum;
  }

  @Override
  public long getFileSize(String rootPath, String dir, String filename) {
    return getFileSize(new File(getAbsolutePath(rootPath)
      + separator()
      + Database.getDomainName()
      + separator()
      + dir
      + separator()
      + filename));
  }

  protected int getFileSize(File file) {
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
      size = fileArray.length;
      output.close();
    } catch (FileNotFoundException e) {
      return -1;
    } catch (IOException ioe) {
      return -1;
    }
    return size;
  }

  @Override
  public boolean deleteFolder(String rootPath, String dir) {
    File file =
      new File(getAbsolutePath(rootPath)
        + separator()
        + Database.getDomainName()
        + separator()
        + dir);

    if (!file.exists()) {
      return true;
    }

    return deleteFolder(file);
  }

  protected boolean deleteFolder(File folder) {
    if (folder == null) {
      return true;
    }

    String[] files = folder.list();
    if (files == null) {
      if (!folder.delete()) {
        throw new RuntimeException("delete error");
      }
      return true;
    }

    int length = files.length;
    if (length <= 0) {
      if (!folder.delete()) {
        throw new RuntimeException("delete error");
      }
      return true;
    }

    String folderPath = folder.getAbsolutePath() + separator();
    File tmpfile = null;
    for (int i = 0; i < length; i++) {
      tmpfile = new File(folderPath + files[i]);
      if (tmpfile.exists()) {
        if (tmpfile.isFile()) {
          if (!tmpfile.delete()) {
            throw new RuntimeException("delete error");
          }
        } else if (tmpfile.isDirectory()) {
          deleteFolder(tmpfile);
        }
      }
    }

    if (!folder.delete()) {
      throw new RuntimeException("delete error");
    }
    return true;
  }

  @Override
  public InputStream getFile(String rootPath, String dir, String fileName)
      throws FileNotFoundException {
    return getFile(rootPath
      + separator()
      + Database.getDomainName()
      + separator()
      + dir
      + separator()
      + fileName);
  }

  @Override
  public InputStream getFile(String filePath) throws FileNotFoundException {
    return new FileInputStream(getAbsolutePath(filePath));
  }

  @Override
  public String getDocumentPath(String rootPath, String categoryKey) {
    File rootDir = new File(getAbsolutePath(rootPath));
    String org_name = Database.getDomainName();
    if (!rootDir.exists()) {
      try {
        rootDir.mkdirs();
      } catch (Exception e) {
        logger.error("Can't create directory...:" + rootPath);
        return rootDir.getAbsolutePath();
      }
    }

    if (org_name == null) {
      return rootDir.getAbsolutePath();
    }

    File base = null;

    // パスを作成
    base =
      new File(rootDir.getAbsolutePath()
        + separator()
        + org_name
        + separator()
        + categoryKey);

    if (!base.exists()) {
      try {
        base.mkdirs();
      } catch (Exception e) {
        logger.error("Can't create directory...:" + base);
        return base.getAbsolutePath();
      }
    }
    return base.getAbsolutePath();
  }

  /**
   * @return
   */
  @Override
  public String separator() {
    return File.separator;
  }

  /**
   * @param rootPath
   * @param dir
   * @param filename
   * @return
   */
  @Override
  public boolean deleteFile(String rootPath, String dir, String filename) {

    File file =
      new File(getDocumentPath(rootPath, dir) + separator() + filename);

    if (file != null && file.exists()) {
      if (!file.delete()) {
        throw new RuntimeException("delete error");
      }
    }

    return true;
  }

  @Override
  public boolean deleteFile(String filePath) {

    File file = new File(getAbsolutePath(filePath));

    if (file != null && file.exists()) {
      if (!file.delete()) {
        throw new RuntimeException("delete error");
      }
    }

    return true;
  }

  @Override
  public boolean deleteOldFolder(String folderPath, Calendar cal) {
    Calendar mod = Calendar.getInstance();
    boolean flag = true;
    File parent_folder = new File(getAbsolutePath(folderPath));
    try {
      if (!parent_folder.exists()) {
        return false;
      }
      if (parent_folder.isFile()) {
        return false;
      }
      String folders_path[] = parent_folder.list();
      if (folders_path.length == 0) {
        return true;
      }
      int length = folders_path.length;
      for (int i = 0; i < length; i++) {
        File folder =
          new File(parent_folder.getAbsolutePath()
            + File.separator
            + folders_path[i]);
        mod.setTimeInMillis(folder.lastModified());// ファイルの最終更新日時を格納
        if (folder.isDirectory()) {
          if (!deleteOldFolder(folder.getAbsolutePath(), cal)) {// フォルダの中身が空もしくは全部削除された場合
            flag = false;
          } else if (mod.before(cal)) {// 空のフォルダが古い場合
            if (!folder.delete()) {
              flag = false;
            }
          }
        } else {
          if (mod.before(cal)) {
            // 一つでも消えないファイルがあればフラグを動かす
            if (!folder.delete()) {
              flag = false;
            }
          } else {
            flag = false;
          }
        }

      }
    } catch (Exception e) {
      logger.error("ALDefaultStorageHandler.deleteOldFolder", e);
      return false;
    }
    return flag;
  }

  protected String getAbsolutePath(String folderPath) {
    try {
      Path path = Paths.get(folderPath);
      if (path == null) {
        return folderPath;
      }
      if (path.isAbsolute()) {
        return folderPath;
      }
      String root = System.getProperty("catalina.home");
      if (root == null) {
        return folderPath;
      }
      return root + separator() + folderPath;
    } catch (Throwable ignore) {
      //
    }
    return folderPath;
  }
}
