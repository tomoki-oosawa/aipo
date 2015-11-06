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
package com.aimluck.eip.services.storage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 *
 */
public abstract class ALStorageHandler {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALStorageHandler.class.getName());

  public static final String FOLDER_TMP_FOR_ATTACHMENT_FILES =
    JetspeedResources.getString("aipo.tmp.fileupload.attachment.directory", "");

  public abstract void createNewTmpFile(InputStream is, int uid, String dir,
      String fileName, String realFileName);

  public abstract void createNewFile(InputStream is, String folderPath,
      String filename);

  public long getTmpFolderSize(int uid, String dir) {
    return getFolderSize(FOLDER_TMP_FOR_ATTACHMENT_FILES, uid
      + ALStorageService.separator()
      + dir);
  }

  public boolean copyTmpFile(int uid, String srcDir, String srcFileName,
      String destRootPath, String destDir, String destFileName) {
    return copyFile(FOLDER_TMP_FOR_ATTACHMENT_FILES, uid
      + ALStorageService.separator()
      + srcDir, srcFileName, destRootPath, destDir, destFileName);
  }

  public boolean deleteTmpFolder(int uid, String dir) {
    return deleteFolder(FOLDER_TMP_FOR_ATTACHMENT_FILES, uid
      + ALStorageService.separator()
      + dir);
  }

  public InputStream getTmpFile(int uid, String folderName, String finename)
      throws FileNotFoundException {
    return getFile(FOLDER_TMP_FOR_ATTACHMENT_FILES, uid
      + ALStorageService.separator()
      + folderName, finename);
  }

  public abstract boolean copyFile(String srcRootPath, String srcDir,
      String srcFileName, String destRootPath, String destDir,
      String destFileName);

  public abstract long getFolderSize(String rootPath, String dir);

  public abstract long getFileSize(String rootPath, String dir, String filename);

  public abstract boolean deleteFolder(String rootPath, String dir);

  public abstract boolean deleteFile(String rootPath, String dir,
      String filename);

  public abstract boolean deleteFile(String filePath);

  public abstract InputStream getFile(String rootPath, String dir,
      String fineName) throws FileNotFoundException;

  public abstract InputStream getFile(String filePath)
      throws FileNotFoundException;

  public abstract String getDocumentPath(String rootPath, String categoryKey);

  public abstract String separator();

  public abstract void saveFile(InputStream is, String folderPath,
      String fileName);

  public abstract boolean deleteOldFolder(String folderPath, Calendar cal);

  public abstract void createNewFile(InputStream inputStream, String filepath);
}
