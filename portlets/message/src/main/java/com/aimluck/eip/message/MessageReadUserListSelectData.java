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
package com.aimluck.eip.message;

import java.util.Date;
import java.util.jar.Attributes;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;

/**
 *
 */
public class MessageReadUserListSelectData extends
    ALAbstractSelectData<TurbineUser, TurbineUser> {

  private int messageId;

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
  protected ResultList<TurbineUser> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return MessageUtils.getReadUserList(messageId);
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected TurbineUser selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultData(TurbineUser model)
      throws ALPageNotFoundException, ALDBErrorException {
    MessageUserResultData rd = new MessageUserResultData();
    rd.initField();
    rd.setUserId(model.getUserId());
    rd.setFirstName(model.getFirstName());
    rd.setLastName(model.getLastName());
    rd.setHasPhoto("T".equals(model.getHasPhoto()));
    Date photoModified = model.getPhotoModified();
    if (photoModified != null) {
      rd.setPhotoModified(photoModified.getTime());
    }
    return rd;
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected Object getResultDataDetail(TurbineUser model)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    return null;
  }

  /**
   * @param messageId
   *          セットする messageId
   */
  public void setMessageId(int messageId) {
    this.messageId = messageId;
  }

  /**
   * @return messageId
   */
  public int getMessageId() {
    return messageId;
  }
}
