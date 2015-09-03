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
import java.util.Map;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * スケジュールポートレット初期選択設備の設定値を処理するクラスです。 <br />
 */
public class TimelineClassificationBox extends ListBox {

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

      this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
      String[] keys = new String[3];
      keys[0] = "all";
      keys[1] = "posting";
      keys[2] = "updates";
      this.items = keys;
      String[] values = new String[3];
      values[0] = "すべて";
      values[1] = "投稿のみ";
      values[2] = "更新のみ";
      this.values = values;
      this.size = Integer.toString(3);
      this.multiple =
        Boolean
          .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
          .booleanValue();

    } catch (Exception e) {
      ALEipUtils.redirectPageNotFound(data);
    }

  }
}
