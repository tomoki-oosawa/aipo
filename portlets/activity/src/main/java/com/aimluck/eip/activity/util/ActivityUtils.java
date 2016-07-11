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
package com.aimluck.eip.activity.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ActivityUtils {

  public static final String ACTIVITY_PORTLET_NAME = "Activity";

  /** 検索キーワード変数の識別子 */
  public static final String TARGET_KEYWORD = "keyword";

  /** パラメータリセットの識別子 */
  private static final String RESET_FLAG = "reset_params";

  public static final List<Integer> notifyTimingList = Arrays.asList(
    0,
    5,
    15,
    30,
    60);

  /**
   * 表示切り替えで指定した検索キーワードを取得する．
   *
   * @param rundata
   * @param context
   * @return
   */
  public static String getTargetKeyword(RunData rundata, Context context) {
    String target_keyword = null;
    String keywordParam = rundata.getParameters().getString(TARGET_KEYWORD);
    target_keyword = ALEipUtils.getTemp(rundata, context, TARGET_KEYWORD);

    if (keywordParam == null && (target_keyword == null)) {
      ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, "");
      target_keyword = "";
    } else if (keywordParam != null) {
      ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, keywordParam.trim());
      target_keyword = keywordParam;
    }
    return target_keyword;
  }

  /**
   * 表示切り替えのリセットフラグがあるかを返す．
   *
   * @param rundata
   * @param context
   * @return
   */
  public static boolean hasResetFlag(RunData rundata, Context context) {
    String resetflag = rundata.getParameters().getString(RESET_FLAG);
    return resetflag != null;
  }

  /**
   * フィルターを初期化する．
   *
   * @param rundata
   * @param context
   * @param className
   */
  public static void resetFilter(RunData rundata, Context context,
      String className) {
    ALEipUtils.setTemp(rundata, context, TARGET_KEYWORD, "");
  }

  public static String getGlobalPortletId(RunData rundata) {

    Portlets portlets =
      ((JetspeedRunData) rundata).getProfile().getDocument().getPortlets();
    if (portlets == null) {
      return null;
    }

    @SuppressWarnings("unchecked")
    Iterator<Entry> iterator = portlets.getEntriesIterator();
    while (iterator.hasNext()) {
      Entry next = iterator.next();
      if ("Activity".equals(next.getParent())) {
        return next.getId();
      }
    }
    return null;
  }

  /**
   *
   * PSMLに設定されているデータと比較して valueが正しい値ならその値を新しくPSMLに保存。
   *
   *
   * @param rundata
   * @param context
   * @param config
   * @return
   */
  public static String passPSML(RunData rundata, Context context, String key,
      String value) {
    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    PortletConfig config = portlet.getPortletConfig();
    if (value == null || "".equals(value)) {
      value = config != null ? config.getInitParameter(key) : "";
    } else {
      ALEipUtils.setPsmlParameters(rundata, context, key, value);
    }
    return value;
  }
}
