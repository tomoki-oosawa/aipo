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
package com.aimluck.eip.cayenne.om.account;

import com.aimluck.eip.cayenne.om.account.auto._EipMUserPosition;
import com.aimluck.eip.cayenne.om.security.TurbineUser;

public class EipMUserPosition extends _EipMUserPosition {

  public Integer getId() {
    return (getObjectId() != null && !getObjectId().isTemporary()) ? (Integer) getObjectId()
        .getIdSnapshot().get(ID_PK_COLUMN)
        : null;
  }

  /** Read-only access to FK */
  public Integer getUserId() {
    TurbineUser user = this.getTurbineUser();

    if (user != null) {
      Object obj = getObjectId().getIdSnapshot().get(
          TurbineUser.USER_ID_PK_COLUMN);
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

}
