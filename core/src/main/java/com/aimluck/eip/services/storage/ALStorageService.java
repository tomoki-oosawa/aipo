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

/**
 *
 */
public class ALStorageService {

  private ALStorageService() {

  }

  public static ALStorageHandler getService() {
    return ALStorageFactoryService.getInstance().getStorageHandler();
  }

  public static void saveFile(InputStream is, String folderPath, String fileName) {
    getService().saveFile(is, folderPath, fileName);
  }

  public static void createNewFile(InputStream is, String folderPath,
      String fileName) {
    getService().createNewFile(is, folderPath, fileName);
  }

  public static void createNewTmpFile(InputStream is, int uid, String dir,
      String fileName, String realFileName) {
    getService().createNewTmpFile(is, uid, dir, fileName, realFileName);
  }

  public static long getTmpFolderSize(int uid, String dir) {
    return getService().getTmpFolderSize(uid, dir);
  }

  public static boolean copyTmpFile(int uid, String srcDir, String srcFileName,
      String destRootPath, String destDir, String destFileName) {
    return getService().copyTmpFile(
      uid,
      srcDir,
      srcFileName,
      destRootPath,
      destDir,
      destFileName);
  }

  public static boolean deleteTmpFolder(int uid, String dir) {
    return getService().deleteTmpFolder(uid, dir);
  }

  public static InputStream getTmpFile(int uid, String folderName,
      String finename) throws FileNotFoundException {
    return getService().getTmpFile(uid, folderName, finename);
  }

  public static long getFolderSize(String rootPath, String dir) {
    return getService().getFolderSize(rootPath, dir);
  }

  public static long getFileSize(String rootPath, String dir, String filename) {
    return getService().getFileSize(rootPath, dir, filename);
  }

  public static boolean copyFile(String srcRootPath, String srcDir,
      String srcFileName, String destRootPath, String destDir,
      String destFileName) {
    return getService().copyFile(
      srcRootPath,
      srcDir,
      srcFileName,
      destRootPath,
      destDir,
      destFileName);
  }

  public static boolean deleteFolder(String rootPath, String dir) {
    return getService().deleteFolder(rootPath, dir);
  }

  public static boolean deleteFile(String rootPath, String dir, String filename) {
    return getService().deleteFile(rootPath, dir, filename);
  }

  public static boolean deleteFile(String filePath) {
    return getService().deleteFile(filePath);
  }

  public static InputStream getFile(String rootPath, String dir, String fineName)
      throws FileNotFoundException {
    return getService().getFile(rootPath, dir, fineName);
  }

  public static InputStream getFile(String filePath)
      throws FileNotFoundException {
    return getService().getFile(filePath);
  }

  public static String getDocumentPath(String rootPath, String categoryKey) {
    return getService().getDocumentPath(rootPath, categoryKey);
  }

  public static boolean deleteOldFolder(String folderPath, Calendar cal) {
    return getService().deleteOldFolder(folderPath, cal);
  }

  public static String separator() {
    return getService().separator();
  }

  /**
   * @param inputStream
   * @param filepath
   */
  public static void createNewFile(InputStream inputStream, String filepath) {
    getService().createNewFile(inputStream, filepath);
  }
}
