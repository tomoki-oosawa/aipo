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

import com.aimluck.eip.cayenne.om.portlet.auto._EipTNote;

public class EipTNote extends _EipTNote {

  public static final String CREATE_DATE_COLUMN = "CREATE_DATE";

  public static final String CLIENT_NAME_COLUMN = "CLIENT_NAME";

  public static final String SUBJECT_TYPE_COLUMN = "SUBJECT_TYPE";

  public static final String CUSTOM_SUBJECT_COLUMN = "CUSTOM_SUBJECT";

  public static final String OWNER_ID_COLUMN = "OWNER_ID";

  public Integer getNoteId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(NOTE_ID_PK_COLUMN);
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

  public void setNoteId(String id) {
    setObjectId(new ObjectId("EipTNote", NOTE_ID_PK_COLUMN, Integer.valueOf(id)));
  }
}
