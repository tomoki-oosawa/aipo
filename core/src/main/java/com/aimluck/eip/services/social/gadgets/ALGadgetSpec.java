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
package com.aimluck.eip.services.social.gadgets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.ezmorph.bean.MorphDynaBean;

/**
 * 
 */
public class ALGadgetSpec extends HashMap<String, Object> implements
    Serializable {

  private static final long serialVersionUID = -1189192399765199814L;

  public String getTitle() {
    return (String) get("title");
  }

  public String getUrl() {
    return (String) get("url");
  }

  public String getDescription() {
    return (String) get("description");
  }

  public String getIcon() {
    return (String) get("icon");
  }

  public int getHeight() {
    try {
      return (Integer) get("height");
    } catch (Throwable t) {
      return 200;
    }
  }

  public boolean isScrolling() {
    try {
      return (Boolean) get("scrolling");
    } catch (Throwable t) {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  public List<ALUserPref> getUserPrefs() {
    List<ALUserPref> result = new ArrayList<ALUserPref>();
    Map<String, MorphDynaBean> userPrefs =
      (Map<String, MorphDynaBean>) get("userPrefs");
    if (userPrefs != null) {
      Iterator<Entry<String, MorphDynaBean>> iterator =
        userPrefs.entrySet().iterator();
      while (iterator.hasNext()) {
        ALUserPref userPref = new ALUserPref();
        Entry<String, MorphDynaBean> next = iterator.next();
        String key = next.getKey();
        userPref.put("name", key);
        MorphDynaBean value = next.getValue();

        // displayName
        userPref.put("displayName", value.get("displayName"));
        // default
        userPref.put("default", value.get("default"));
        // orderedEnumValues
        userPref.put("orderedEnumValues", value.get("orderedEnumValues"));
        // type
        userPref.put("type", value.get("type"));

        result.add(userPref);
      }
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public List<ALOAuthService> getOAuthServices() {
    return (List<ALOAuthService>) get("oauthService");
  }

}
