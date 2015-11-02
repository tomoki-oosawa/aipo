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
package com.aimluck.eip.modules.actions.project.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ProjectTestUtil {

  public static final String FS = System.getProperty("file.separator");

  public static String fileSeparatorJoin(String... args) {
    List<String> list = Arrays.asList(args);
    return join(list, FS);
  }

  public static String join(List<String> list, String separator) {
    StringBuilder features = new StringBuilder();
    Iterator<String> it = list.iterator();
    while (it.hasNext()) {
      String word = it.next();
      if (StringUtils.isEmpty(word)) {
        continue;
      }
      features.append(word);
      if (it.hasNext()) {
        features.append(separator);
      }
    }
    return features.toString();
  }

  public static <T> List<List<T>> devide(List<T> origin, int size) {
    if (origin == null || origin.isEmpty() || size <= 0) {
      return Collections.emptyList();
    }

    int block = origin.size() / size + (origin.size() % size > 0 ? 1 : 0);

    List<List<T>> devidedList = new ArrayList<List<T>>(block);
    for (int i = 0; i < block; i++) {
      int start = i * size;
      int end = Math.min(start + size, origin.size());
      devidedList.add(new ArrayList<T>(origin.subList(start, end)));
    }
    return devidedList;
  }

}
