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
package com.aimluck.eip.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * マイグループの情報を保持するクラスです。 <br />
 * 
 */
public class ALMyGroups implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -4254383273867227319L;

  transient List<ALEipGroup> list = null;

  public ALMyGroups() {
    init();
  }

  private void init() {
    list = new ArrayList<ALEipGroup>();
  }

  public void add(ALEipGroup group) {
    synchronized (list) {
      list.add(group);
    }
  }

  public ALEipGroup get(int index) {
    synchronized (list) {
      return list.get(index);
    }
  }

  public void addList(List<ALEipGroup> groups) {
    synchronized (list) {
      int length = groups.size();
      for (int i = 0; i < length; i++) {
        list.add(groups.get(i));
      }
    }
  }

  public List<ALEipGroup> getList() {
    List<ALEipGroup> result = new ArrayList<ALEipGroup>();
    synchronized (list) {
      int length = list.size();
      for (int i = 0; i < length; i++) {
        result.add(list.get(i));
      }
    }
    return result;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    synchronized (list) {
      out.defaultWriteObject();
      int length = list.size();
      out.writeInt(length);
      for (int i = 0; i < length; i++) {
        out.writeObject(list.get(i));
      }
      out.flush();
    }
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    init();
    in.defaultReadObject();

    synchronized (list) {
      int length = in.readInt();
      for (int i = 0; i < length; i++) {
        Object obj = in.readObject();
        list.add((ALEipGroup) obj);
      }
    }
  }

}
