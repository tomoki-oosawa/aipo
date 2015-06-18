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
package com.aimluck.eip.gadgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractCheckList;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest.Status;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class GagetsAdminMultiDelete extends ALAbstractCheckList {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GagetsAdminMultiDelete.class.getName());

  /**
   * @param rundata
   * @param context
   * @param values
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean action(RunData rundata, Context context,
      List<String> values, List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      List<String> titles = new ArrayList<String>();
      // アプリ名を取得
      for (String value : values) {
        if (value != null) {
          ALApplication deletedApp =
            ALApplicationService.get(new ALApplicationGetRequest().withAppId(
              value).withStatus(Status.ALL).withIsDetail(true).withIsFetchXml(
              true));
          if (deletedApp != null) {
            titles.add(deletedApp.getTitle().toString());
          }
        }
      }

      // 削除
      ALApplicationService.delete(values);

      // イベントログに保存
      for (String title : titles) {
        ALEventlogFactoryService.getInstance().getEventlogHandler().log(
          ALEipUtils.getUserId(rundata),
          ALEventlogConstants.PORTLET_TYPE_GADGET,
          "アプリ「" + title + "」を削除");
      }

    } catch (Throwable t) {
      logger.error(t, t);
      return false;
    }
    return true;
  }

}
