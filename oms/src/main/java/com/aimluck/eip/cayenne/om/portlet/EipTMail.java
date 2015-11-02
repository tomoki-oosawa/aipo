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
package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTMail;

public class EipTMail extends _EipTMail {

  public static final String READ_FLG_COLUMN = "READ_FLG";

  public static final String SUBJECT_COLUMN = "SUBJECT";

  public static final String PERSON_COLUMN = "PERSON";

  public static final String EVENT_DATE_COLUMN = "EVENT_DATE";

  public static final String FILE_VOLUME_COLUMN = "FILE_VOLUME";

  public static final String HAS_FILES_COLUMN = "HAS_FILES";

  public Integer getMailId() {
    if (getObjectId() != null && !getObjectId().isTemporary()) {
      Object obj = getObjectId().getIdSnapshot().get(MAIL_ID_PK_COLUMN);
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

  public void setMailId(String id) {
    setObjectId(new ObjectId("EipTMail", MAIL_ID_PK_COLUMN, Integer.valueOf(id)));
  }
}
