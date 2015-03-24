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
 * スケジュールポートレット初期選択設備の設定値を処理するクラスです。 <br />
 */
public class GroupFacilityListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  public static final String FACILITY_VALUE = "Facility";

  private final String DEF_INITIAL_VALUE = ALLocalizationUtils
    .getl10n("SCHEDULE_SELECT_FACILITY_AND_ALL");

  /**
   * Initialize options
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
      List<ALEipGroup> facilitygroupList = ALEipUtils.getALEipGroups();

      int length =
        postCollection.size()
          + mygroupList.size()
          + facilitygroupList.size()
          + 1
          + 1;
      String[] groupKeys = new String[length];
      String[] groupValues = new String[length];

      groupKeys[0] = "";
      groupValues[0] = (String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE);
      int count = 1;

      // 設備一覧の登録
      groupKeys[count] = FACILITY_VALUE;
      groupValues[count] =
        ALLocalizationUtils.getl10n("SCHEDULE_FACILITY_LIST");
      count++;

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

      // 設備グループの登録
      ALEipGroup facilitygroup = null;
      Iterator<ALEipGroup> iter3 = facilitygroupList.iterator();
      while (iter3.hasNext()) {
        facilitygroup = iter3.next();
        groupKeys[count] = "f;" + facilitygroup.getName().toString();
        groupValues[count] = facilitygroup.getAliasName().toString();
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
