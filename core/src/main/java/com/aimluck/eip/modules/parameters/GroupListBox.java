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
package com.aimluck.eip.modules.parameters;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * 部署とマイグループをListBoxで返すクラスです。 <br />
 * 
 */
public class GroupListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  private final String DEF_INITIAL_VALUE = ALLocalizationUtils
    .getl10nFormat("GROUPLISTBOX_ALL");

  /**
   * 表示オプションを初期化します。
   * 
   * @param data
   */
  @Override
  protected void init(RunData data) {

    // 部署の取得
    Map<Integer, ALEipPost> postMap = ALEipManager.getInstance().getPostMap();
    Collection<ALEipPost> postCollection = postMap.values();
    try {
      // マイグループの取得
      List<ALEipGroup> mygroupList = ALEipUtils.getMyGroups(data);

      int length = postCollection.size() + mygroupList.size() + 1;
      String[] groupKeys = new String[length];
      String[] groupValues = new String[length];

      groupKeys[0] = "";
      groupValues[0] = (String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE);
      int count = 1;

      // 部署の登録
      ALEipPost post = null;
      Iterator<ALEipPost> iter = postCollection.iterator();
      while (iter.hasNext()) {
        post = iter.next();
        groupKeys[count] = post.getGroupName().toString();
        groupValues[count] = post.getPostName().toString();
        count++;
      }

      // マイグループの登録
      ALEipGroup group = null;
      Iterator<ALEipGroup> iter2 = mygroupList.iterator();
      while (iter2.hasNext()) {
        group = iter2.next();
        groupKeys[count] = group.getName().toString();
        groupValues[count] = group.getAliasName().toString();
        count++;
      }

      this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
      this.items = groupKeys;
      this.values = groupValues;
      this.size = Integer.toString(length);
      this.multiple =
        Boolean
          .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
          .booleanValue();
    } catch (ALDBErrorException e) {
      ALEipUtils.redirectPageNotFound(data);
    }

  }
}
