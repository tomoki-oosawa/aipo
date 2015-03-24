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
package com.aimluck.eip.mail.util;

import java.util.HashMap;

/**
 * メール受信時の排他制御用のクラスです。 <br />
 * 
 */
public class ALStaticObject {

  private final HashMap<Object, Object> map;

  private static ALStaticObject so = new ALStaticObject();

  public static ALStaticObject getInstance() {
    return so;
  }

  private ALStaticObject() {
    map = new HashMap<Object, Object>();
  }

  public void addAccountId(int accountId) {
    synchronized (map) {
      map.put(Integer.valueOf(accountId), new HashMap<Object, Object>());
    }
  }

  public void removeAccountId(int accountId) {
    synchronized (map) {
      map.remove(Integer.valueOf(accountId));
    }
  }

  public void updateAccountStat(int accountId, Object statKey, Object statValue) {
    synchronized (map) {
      Object obj = map.get(Integer.valueOf(accountId));
      if (obj == null) {
        return;
      }

      @SuppressWarnings("unchecked")
      HashMap<Object, Object> stats = (HashMap<Object, Object>) obj;
      stats.put(statKey, statValue);
    }
  }

  public Object getAccountStat(int accountId, Object statKey) {
    synchronized (map) {
      try {
        Object obj = map.get(Integer.valueOf(accountId));
        if (obj == null) {
          return null;
        }

        @SuppressWarnings("unchecked")
        HashMap<Object, Object> stats = (HashMap<Object, Object>) obj;
        return stats.get(statKey);
      } catch (Exception e) {
        return null;
      }
    }
  }

  public boolean receivable(int accountId) {
    synchronized (map) {
      return !(map.containsKey(Integer.valueOf(accountId)));
    }
  }

}
