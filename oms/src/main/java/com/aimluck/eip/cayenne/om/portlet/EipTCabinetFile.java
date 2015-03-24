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

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTCabinetFile;

public class EipTCabinetFile extends _EipTCabinetFile {

  public static final String FOLDER_ID_COLUMN = "FOLDER_ID";

  public static final String FILE_TITLE_COLUMN = "FILE_TITLE";

  public static final String FILE_NAME_COLUMN = "FILE_NAME";

  public static final String FILE_SIZE_COLUMN = "FILE_SIZE";

  public static final String COUNTER_COLUMN = "COUNTER";

  public static final String UPDATE_USER_ID_COLUMN = "UPDATE_USER_ID";

  public static final String UPDATE_DATE_COLUMN = "UPDATE_DATE";

  public Integer getFileId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(FILE_ID_PK_COLUMN);
      if (obj instanceof Long) {
        Long value = (Long) obj;
        return Integer.valueOf(value.intValue());
      } else {
        return (Integer) obj;
      }
    } else {
      return null;
    }
  }

  public void setFileId(String id) {
    setObjectId(new ObjectId("EipTCabinetFile", FILE_ID_PK_COLUMN, Integer
      .valueOf(id)));
  }
}
