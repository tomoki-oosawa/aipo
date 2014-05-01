/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.commons.utils;

import java.util.List;

import org.apache.cayenne.exp.ExpressionFactory;

import com.aimluck.eip.common.ALFileNotRemovedException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.storage.ALStorageService;

public abstract class ALDeleteFileUtil {
  public static <T> void deleteFiles(int id, String property, String base,
      List<String> fpaths, Class<T> clazz) throws ALFileNotRemovedException {
    SelectQuery<T> query = Database.query(clazz);
    query.andQualifier(ExpressionFactory.matchDbExp(property, id));
    List<T> files = query.fetchList();
    Database.deleteAll(files);

    if (fpaths.size() > 0) {
      // ローカルファイルに保存されているファイルを削除する．
      try {
        int fsize = fpaths.size();
        for (int i = 0; i < fsize; i++) {
          ALStorageService.deleteFile(base + fpaths.get(i));
        }
      } catch (Exception e) {
        Database.rollback();
        ALFileNotRemovedException fe = new ALFileNotRemovedException();
        fe.initCause(e);
        throw fe;
      }
    }
    Database.commit();
  }
}
