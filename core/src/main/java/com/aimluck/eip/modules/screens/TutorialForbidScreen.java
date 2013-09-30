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

package com.aimluck.eip.modules.screens;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.utils.ALTutorialUtil;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 各ポートレットでの添付ファイルを表示させるクラスです。 <br />
 * 
 */
public class TutorialForbidScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TutorialForbidScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
    TurbineUser user = ALEipUtils.getTurbineUser(ALEipUtils.getUserId(rundata));
    String id = rundata.getParameters().getString("id");
    int location = ALTutorialUtil.ID_LIST.indexOf(id);

    if (location == -1) {
      return;
    }

    String prevValue = user.getTutorialForbid();

    if (prevValue == null || prevValue.equals("") || prevValue.equals("F")) {
      prevValue =
        StringUtils.repeat("F", ALTutorialUtil.ID_LIST.size())
          + StringUtils.repeat("T", ALTutorialUtil.FORBID_FLAG_LENGTH
            - ALTutorialUtil.ID_LIST.size());
    } else if (prevValue != null && prevValue.equals("T")) {
      prevValue = StringUtils.repeat("T", ALTutorialUtil.FORBID_FLAG_LENGTH);
    }

    StringBuilder sb = new StringBuilder(prevValue);
    sb.setCharAt(location, 'T');
    String value = sb.toString();

    user.setTutorialForbid(value);
    Database.commit();

    // ex) FFFFTTTT -> 0x0F, FTTFTTTT -> 0x09
    int result = 0;
    for (int i = 0; i < ALTutorialUtil.FORBID_FLAG_LENGTH; i++) {
      if (value.charAt(i) == 'F') {
        result += Math.pow(2, i);
      }
    }

    HttpServletResponse response = rundata.getResponse();
    OutputStream out = response.getOutputStream();
    out.write(String.valueOf(result).getBytes());
    out.flush();
    out.close();
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return null;
  }

}
