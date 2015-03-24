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
package com.aimluck.eip.cayenne.om.portlet;

/**
 *
 */
public interface IProjectFile {

  public static final String PK_COLUMN = "FILE_ID";

  public void setFileId(String id);

  public Integer getFileId();

  public void setFileName(String fileName);

  public String getFileName();

  public void setCreateDate(java.util.Date createDate);

  // public java.util.Date getCreateDate();

  public void setFilePath(String filePath);

  public String getFilePath();

  public void setFileThumbnail(byte[] fileThumbnail);

  // public byte[] getFileThumbnail();

  public void setOwnerId(Integer ownerId);

  // public Integer getOwnerId();
  //
  // public void setProjectId(Integer projectId);
  //
  // public Integer getProjectId();

  public void setUpdateDate(java.util.Date updateDate);

  // public java.util.Date getUpdateDate();

  public void setEipT(Object eipT);

}
