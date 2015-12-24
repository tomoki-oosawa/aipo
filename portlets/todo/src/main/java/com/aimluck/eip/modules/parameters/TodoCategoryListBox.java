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

import java.util.ArrayList;

import org.apache.turbine.util.RunData;

import com.aimluck.eip.todo.ToDoCategoryResultData;
import com.aimluck.eip.todo.util.ToDoUtils;
import com.aimluck.eip.util.ALEipUtils;

public class TodoCategoryListBox extends ListBox {

  public static final String INITIAL_VALUE = "initialvalue";

  public static final String DEF_INITIAL_VALUE = "すべてのカテゴリ";

  /**
   * Initialize options
   * 
   * @param data
   */
  @Override
  protected void init(RunData data) {
    try {
      ArrayList<ToDoCategoryResultData> categoryList =
        ToDoUtils.getCategoryList(data);
      String[] categoryKeys = new String[categoryList.size() + 1];
      String[] categoryValues = new String[categoryList.size() + 1];

      // ヘッダ
      categoryKeys[0] = "";
      categoryValues[0] =
        (String) this.getParm(INITIAL_VALUE, DEF_INITIAL_VALUE);

      int i = 1;
      for (ToDoCategoryResultData category : categoryList) {
        categoryKeys[i] = category.getCategoryId().toString();
        categoryValues[i] = category.getCategoryName().toString();
        i++;
      }

      this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
      this.items = categoryKeys;

      this.values = categoryValues;
      this.size = Integer.toString(categoryList.size() + 2);
      this.multiple =
        Boolean
          .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
          .booleanValue();
    } catch (Exception e) {
      ALEipUtils.redirectPageNotFound(data);
    }
  }
}
