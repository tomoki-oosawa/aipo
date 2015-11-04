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
package com.aimluck.eip.services.datasync.empty;

import com.aimluck.eip.common.ALBaseUser;
import com.aimluck.eip.services.datasync.ALDataSyncHandler;

public class ALEmptyDataSyncHandler extends ALDataSyncHandler {

  @Override
  public boolean checkConnect() {
    return true;
  }

  @Override
  public boolean addUser(ALBaseUser user) {
    return true;
  }

  @Override
  public boolean updateUser(ALBaseUser user) {
    return true;
  }

  @Override
  public boolean deleteUser(String username) {
    return true;
  }

  @Override
  public boolean enableUser(String username) {
    return true;
  }

  @Override
  public boolean disableUser(String username) {
    return true;
  }

  @Override
  public boolean multiDeleteUser(String[] username_list, int size) {
    return true;
  }

  @Override
  public boolean multiEnableUser(String[] username_list, int size) {
    return true;
  }

  @Override
  public boolean multiDisableUser(String[] username_list, int size) {
    return true;
  }

}
