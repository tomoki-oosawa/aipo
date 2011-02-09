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

package com.aimluck.eip.gadgets;

import java.util.jar.Attributes;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest.Status;
import com.aimluck.eip.util.ALEipUtils;

/**
 * 
 */
public class GadgetsAdminSelectData extends
    ALAbstractSelectData<ALApplication, ALApplication> {

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<ALApplication> selectList(RunData rundata,
      Context context) throws ALPageNotFoundException, ALDBErrorException {
    Status status = Status.ALL;
    String filter = ALEipUtils.getTemp(rundata, context, LIST_FILTER_STR);
    if ("1".equals(filter)) {
      status = Status.ACTIVE;
      current_filter = filter;
    } else if ("0".equals(filter)) {
      status = Status.INACTIVE;
      current_filter = filter;
    } else if ("all".equals(filter)) {
      status = Status.ALL;
      current_filter = filter;
    }
    return ALApplicationService.getList(new ALApplicationGetRequest()
      .withLimit(getRowsNum())
      .withPage(current_page)
      .withStatus(status));
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ALApplication selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String appId =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    return ALApplicationService.get(new ALApplicationGetRequest().withAppId(
      appId).withStatus(Status.ALL).withIsDetail(true));
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(ALApplication model)
      throws ALPageNotFoundException, ALDBErrorException {
    return model;
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(ALApplication model)
      throws ALPageNotFoundException, ALDBErrorException {
    return model;
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

}
