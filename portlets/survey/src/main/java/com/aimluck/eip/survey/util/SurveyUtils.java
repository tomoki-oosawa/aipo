/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
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

package com.aimluck.eip.survey.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALEipUtils;

/**
 * アンケートのユーティリティクラスです。 <BR>
 * 
 */
public class SurveyUtils {
  /** ポートレット名 */
  public static final String PORTLET_NAME = "Survey";

  public static final String RESPONDENT_TYPE_ALL = "A";

  public static final String RESPONDENT_TYPE_MEMBER = "M";

  public static final String OPEN_FLAG_MANUAL_OPENED = "M";

  public static final String OPEN_FLAG_AUTO_OPENED = "A";

  public static final String OPEN_FLAG_NOT_OPENED = "F";

  public static final String CLOSE_FLAG_MANUAL_CLOSED = "M";

  public static final String CLOSE_FLAG_AUTO_CLOSED = "A";

  public static final String CLOSE_FLAG_NOT_CLOSED = "F";

  public static final String OPTION_TYPE_RADIO = "R";

  public static final String OPTION_TYPE_CHECK = "C";

  public static void clearSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    list.add("entityid");
    list.add("submenu");
    ALEipUtils.removeTemp(rundata, context, list);
  }
}
