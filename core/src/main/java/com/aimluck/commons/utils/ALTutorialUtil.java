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

package com.aimluck.commons.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;

/**
 * チュートリアルに対するユーティリティクラスです。 <br />
 * 
 */
public class ALTutorialUtil {

  public static List<String> ID_LIST = Arrays.asList(
    "tutorial_schedule",
    "tutorial_timeline1",
    "tutorial_timeline2",
    "tutorial_activity",
    "tutorial_user",
    "tutorial_last_image");

  public static final int FORBID_FLAG_LENGTH = 64;

  public static void updateForbid(RunData rundata, String name) {
    try {
      TurbineUser user =
        ALEipUtils.getTurbineUser(ALEipUtils.getUserId(rundata));
      String prevValue = user.getTutorialForbid();

      if (prevValue.charAt(ALTutorialUtil.ID_LIST.indexOf(name)) == 'F') {

        if (prevValue == null || prevValue.equals("") || prevValue.equals("F")) {
          prevValue =
            StringUtils.repeat("F", ALTutorialUtil.ID_LIST.size())
              + StringUtils.repeat("T", ALTutorialUtil.FORBID_FLAG_LENGTH
                - ALTutorialUtil.ID_LIST.size());
        } else if (prevValue != null && prevValue.equals("T")) {
          prevValue =
            StringUtils.repeat("T", ALTutorialUtil.FORBID_FLAG_LENGTH);
        }

        StringBuilder sb = new StringBuilder(prevValue);
        sb.setCharAt(ALTutorialUtil.ID_LIST.indexOf(name), 'T');
        String value = sb.toString();

        user.setTutorialForbid(value);
        Database.commit();
      }
    } catch (Exception e) {

    }
  }

}
