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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 検索データを管理するための抽象クラスです。 <br />
 * 
 */
public abstract class ALAbstractMultiFilterSelectData<M1, M2> extends
    ALAbstractSelectData<M1, M2> {

  /** logger */
  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALAbstractMultiFilterSelectData.class.getName());

  protected Map<String, List<String>> current_filterMap;

  /**
   * 
   * @param action
   * @param rundata
   * @param context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    parseFilterMap(rundata, context);
  }

  /**
   *
   */
  @Override
  public void initField() {
  }

  /**
   * filter_type =a,b,c,a &filter =1,2 3,4,5と入力した時に デフォルトでは、a=1,5 & b=2,3 & c=4
   * のようにマップにセットします。
   * 
   * @param key
   * @param val
   */
  protected void parseFilterMap(String key, String val) {
    if (current_filterMap == null) {
      current_filterMap = new HashMap<String, List<String>>();
    }
    if (key == null || val == null) {
      return;
    }
    String[] keys = key.split(",");
    String[] vals = val.split(",");
    current_filterMap.clear();
    for (int i = 0, n = Math.min(keys.length, vals.length); i < n; i++) {
      if (keys[i] != null
        && !"".equals(keys[i])
        && vals[i] != null
        && !"".equals(vals[i])) {
        List<String> childs =
          new ArrayList<String>(Arrays.asList(vals[i].trim().split(" ")));
        if (current_filterMap.containsKey(keys[i])) {
          List<String> exists = current_filterMap.get(keys[i]);
          exists.addAll(childs);
          current_filterMap.put(keys[i], exists);
        } else {
          current_filterMap.put(keys[i], childs);
        }
      }
    }
  }

  /**
   * パラメータをマップに変換します。
   * 
   * @param key
   * @param val
   */
  protected void parseFilterMap(RunData rundata, Context context) {
    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    String filter_type =
      ALEipUtils.getTemp(rundata, context, LIST_FILTER_TYPE_STR);
    current_filter = filter;
    current_filter_type = filter_type;
    parseFilterMap(filter_type, filter);
  }

  /**
   * フィルタ用の <code>SelectQuery</code> を構築します。
   * 
   * @param crt
   * @param rundata
   * @param context
   * @return
   */
  @Override
  protected SelectQuery<M1> buildSelectQueryForFilter(SelectQuery<M1> query,
      RunData rundata, Context context) {

    Attributes map = getColumnMap();

    String crt_key = null;
    for (Entry<String, List<String>> pair : current_filterMap.entrySet()) {
      String type = pair.getKey();
      List<String> param = pair.getValue();

      crt_key = map.getValue(type);
      if (crt_key == null || param.size() <= 0) {
        continue;
      }
      Expression exp = ExpressionFactory.inDbExp(crt_key, param);
      query.andQualifier(exp);
    }
    return query;
  }

  public boolean issetFilter() {
    return !(current_filterMap == null || current_filterMap.isEmpty());
  }

  public boolean issetFilter(String key) {
    return issetFilter() && current_filterMap.containsKey(key);
  }

  public boolean issetFilter(String key, Object value) {
    return issetFilter(key)
      && current_filterMap.get(key).contains(value.toString());
  }

  public List<String> getFilters(String key) {
    if (issetFilter()) {
      return current_filterMap.get(key);
    } else {
      return null;
    }
  }

  public String getFilter(String key, int offset) {
    List<String> filters = getFilters(key);
    if (filters != null && filters.size() > offset) {
      return filters.get(offset);
    } else {
      return null;
    }
  }

  public String getFilter(String key) {
    return getFilter(key, 0);
  }

  /**
   * 
   * @return
   */
  @Override
  @Deprecated
  public String getCurrentFilterType() {
    return current_filter_type;
  }

  /**
   * 
   * @return
   */
  @Override
  @Deprecated
  public String getCurrentFilter() {
    return super.getCurrentSortType();
  }

}
