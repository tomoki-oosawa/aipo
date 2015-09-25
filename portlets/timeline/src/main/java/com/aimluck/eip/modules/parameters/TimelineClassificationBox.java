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

/**
 * タイムラインポートレット初期選択種別の設定値を処理するクラスです。 <br />
 */
public class TimelineClassificationBox extends ListBox {

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
      this.items = this.getItems(data);
      this.values = this.getValues(data);
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
