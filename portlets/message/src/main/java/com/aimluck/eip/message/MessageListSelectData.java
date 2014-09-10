/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2014 Aimluck,Inc.
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

import java.util.jar.Attributes;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTMessage;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageListSelectData extends
    ALAbstractSelectData<EipTMessage, EipTMessage> {

  public static final int MESSAGE_LIMIT = 50;

  private int page = 1;

  private int roomId;

  private int userId;

  private int lastMessageId;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    userId = ALEipUtils.getUserId(rundata);
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<EipTMessage> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    try {
      page = rundata.getParameters().getInt("p", 1);
    } catch (Throwable ignore) {
      // ignore
    }

    return MessageUtils.getMessageList(roomId, page, MESSAGE_LIMIT);
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected EipTMessage selectDetail(RunData rundata, Context context)
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
  protected Object getResultData(EipTMessage model)
      throws ALPageNotFoundException, ALDBErrorException {
    MessageResultData rd = new MessageResultData();
    rd.initField();
    rd.setMessageId(model.getMessageId());
    rd.setRoomId(roomId);
    rd.setUserId(model.getUserId());
    rd.setFirstName(model.getFirstName());
    rd.setLastName(model.getLastName());
    rd.setHasPhoto("T".equals(model.getHasPhoto()));
    rd.setMemberCount(model.getMemberCount());
    rd.setUnreadCount(model.getUnreadCount());
    rd.setMessage(model.getMessage());
    rd.setCreateDate(model.getCreateDate());
    rd.setOwner(model.getUserId().intValue() == userId);
    if (model.getMessageId().intValue() > lastMessageId) {
      lastMessageId = model.getMessageId().intValue();
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
  protected Object getResultDataDetail(EipTMessage model)
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

  public int getRoomId() {
    return roomId;
  }

  public void setRoomId(int roomId) {
    this.roomId = roomId;
  }

  public int getUserId() {
    return userId;
  }

  /**
   * @return lastMessageId
   */
  public int getLastMessageId() {
    return lastMessageId;
  }

}
