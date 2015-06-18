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

// ecs stuff
import java.util.Arrays;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.ecs.html.Option;
import org.apache.ecs.html.Select;
import org.apache.jetspeed.modules.parameters.ParameterPresentationStyle;
import org.apache.turbine.util.RunData;

/**
 * Returns simple list box control.
 * <p>
 * Options:
 * <UL>
 * <LI><code>items</code> - comma-separated list of list box items</LI>
 * <LI><code>layout</code> [<strong>$combo</strong>|$list] - combo box vs list
 * box</LI>
 * <LI><code>size</code> - size of the list box for $list style</LI>
 * <LI><code>sort</code> [<strong>false</strong>|true] - return sorted list of
 * items</LI>
 * <LI><code>multiplechoice</code> [<strong>false</strong>|true] - allow
 * multiple selections</LI>
 * <LI><code>null-if-empty</code> [<strong>false</strong>|true] - do not return
 * a select control if item list is empty</LI>
 * </UL>
 * 
 * @author <a href="mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @author <a href="mailto:solutioncenter@infointegrators.com">Dave Trapp</a>
 */
public class ListBox extends ParameterPresentationStyle {

  public static final String SORT = "sort";

  public static final String ITEMS = "items";

  // 修正 ：パラメータとして value を指定できるように，VALUES を追加した．
  public static final String VALUES = "values";

  public static final String LAYOUT = "layout";

  public static final String LAYOUT_COMBO = "$combo";

  public static final String LAYOUT_LIST = "$list";

  public static final String LIST_SIZE = "listsize";

  public static final String MULTIPLE_CHOICE = "multiplechoice";

  public static final String NULL_IF_EMPTY = "null-if-empty";

  protected String layout = null;

  protected String items[] = null;

  protected String values[] = null;

  protected String size = null;

  protected boolean multiple = false;

  /**
   * Returns presentation control
   * 
   * @param data
   * @param name
   * @param value
   * @param parms
   * @return string
   */
  @SuppressWarnings("rawtypes")
  @Override
  public String getContent(RunData data, String name, String value, Map parms) {

    init(data);

    Select select = null;
    if (layout.equalsIgnoreCase(LAYOUT_LIST)) {
      select = new Select(name, Integer.valueOf(size).intValue());
    } else {
      select = new Select(name);
    }

    if (this.getClass() != ListBox.class) {
      select.setClass("w100");
    }

    if (multiple) {
      select.setMultiple(multiple);
    }

    if (items != null) {

      boolean sort =
        Boolean.valueOf((String) this.getParm(SORT, "false")).booleanValue();
      if (sort) {
        Arrays.sort(items);
      }

      for (int i = 0; i < items.length; i++) {
        Option option = new Option(items[i]).addElement(values[i]);
        if (multiple) {
          option.setSelected(value.indexOf(items[i]) >= 0);
        } else {
          option.setSelected(items[i].equalsIgnoreCase(value));
        }
        select.addElement(option);
      }
    }

    // If no items to display, do not display empty control
    boolean nullIfEmpty =
      Boolean
        .valueOf((String) this.getParm(NULL_IF_EMPTY, "false"))
        .booleanValue();
    if (this.items == null || (nullIfEmpty && items.length == 0)) {
      return null;
    }

    return select.toString();

  }

  /**
   * Initialize options
   * 
   * @param data
   */
  protected void init(RunData data) {

    this.layout = (String) this.getParm(LAYOUT, LAYOUT_COMBO);
    this.items = this.getItems(data);
    this.values = this.getValues(data);
    this.size = (String) this.getParm(LIST_SIZE, "1");
    this.multiple =
      Boolean
        .valueOf((String) this.getParm(MULTIPLE_CHOICE, "false"))
        .booleanValue();

  }

  /**
   * Parse items into an arrary of strings
   * 
   * @param data
   * @return string array
   */
  protected String[] getItems(RunData data) {

    String[] result = null;
    String list = (String) this.getParm(ITEMS, "");

    StringTokenizer it = new StringTokenizer(list, ",");
    int size = it.countTokens();

    if (size > 0) {
      result = new String[size];

      int i = 0;
      while (it.hasMoreTokens()) {
        String item = it.nextToken();
        result[i] = item;
        i++;
      }
    }

    return result;

  }

  // 修正 ：パラメータ value に指定した値を取得できるように，
  // メソッド getValues を追加した．
  /**
   * Parse values into an arrary of strings
   * 
   * @param data
   * @return string array
   */
  protected String[] getValues(RunData data) {

    String[] result = null;
    String list = (String) this.getParm(VALUES, "");

    StringTokenizer it = new StringTokenizer(list, ",");
    int size = it.countTokens();

    if (size > 0) {
      result = new String[size];

      int i = 0;
      while (it.hasMoreTokens()) {
        String item = it.nextToken();
        result[i] = item;
        i++;
      }
    }

    return result;

  }
}
