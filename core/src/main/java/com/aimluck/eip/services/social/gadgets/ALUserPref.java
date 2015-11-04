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
package com.aimluck.eip.services.social.gadgets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ezmorph.bean.MorphDynaBean;

/**
 * 
 */
public class ALUserPref extends HashMap<String, Object> implements Serializable {

  private static final long serialVersionUID = 5863053713116484726L;

  public enum Type {
    STRING("string"), BOOL("bool"), ENUM("enum"), LIST("list"), HIDDEN("hidden");

    private final String type;

    Type(String type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return type;
    }
  }

  public String getName() {
    return (String) get("name");
  }

  public String getDefault() {
    return (String) get("default");
  }

  public String getDisplayName() {
    return (String) get("displayName");
  }

  public List<Map.Entry<String, String>> getEnums() {
    @SuppressWarnings("unchecked")
    List<MorphDynaBean> orderedEnumValues =
      (List<MorphDynaBean>) get("orderedEnumValues");
    List<Map.Entry<String, String>> result =
      new ArrayList<Map.Entry<String, String>>();
    for (MorphDynaBean item : orderedEnumValues) {
      String key = (String) item.get("value");
      String value = (String) item.get("displayValue");
      Map<String, String> map = new HashMap<String, String>(1);
      map.put(key, value);
      result.add(map.entrySet().iterator().next());
    }
    return result;
  }

  public Type getType() {
    String type = (String) get("type");
    Type result = null;
    if (type != null) {
      try {
        result = Type.valueOf(type.toUpperCase());
      } catch (Throwable t) {

      }
    }
    return result;
  }
}
