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
package com.aimluck.eip.services.datasync;

import com.aimluck.eip.common.ALBaseUser;

/**
 * データ同期を管理する抽象クラスです。 <br />
 * 
 */
public abstract class ALDataSyncHandler {

  public abstract boolean addUser(ALBaseUser user);

  public abstract boolean updateUser(ALBaseUser user);

  public abstract boolean deleteUser(String username);

  public abstract boolean enableUser(String username);

  public abstract boolean disableUser(String username);

  public abstract boolean multiDeleteUser(String[] username_list, int size);

  public abstract boolean multiEnableUser(String[] username_list, int size);

  public abstract boolean multiDisableUser(String[] username_list, int size);

  public abstract boolean checkConnect();

}
