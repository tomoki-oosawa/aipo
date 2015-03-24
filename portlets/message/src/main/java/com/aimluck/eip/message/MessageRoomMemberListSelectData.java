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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.account.EipMUserPosition;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoom;
import com.aimluck.eip.cayenne.om.portlet.EipTMessageRoomMember;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageRoomMemberListSelectData extends
    ALAbstractSelectData<TurbineUser, TurbineUser> {

  private int targetUserId;

  private int userId;

  private EipTMessageRoom room;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    userId = ALEipUtils.getUserId(rundata);

  }

  @Override
  public void initField() {
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

    if (targetUserId > 0) {
      TurbineUser tuser = ALEipUtils.getTurbineUser(targetUserId);
      if (tuser == null) {
        return new ResultList<TurbineUser>(new ArrayList<TurbineUser>());
      }
      List<TurbineUser> memberList = new ArrayList<TurbineUser>();
      memberList.add(tuser);
      return new ResultList<TurbineUser>(memberList);
    }
    if (!MessageUtils.isJoinRoom(room, userId)) {
      return new ResultList<TurbineUser>(new ArrayList<TurbineUser>());
    }
    @SuppressWarnings("unchecked")
    List<EipTMessageRoomMember> members = room.getEipTMessageRoomMember();
    List<String> memberNames = new ArrayList<String>();
    for (EipTMessageRoomMember member : members) {
      memberNames.add(member.getLoginName());
    }
    SelectQuery<TurbineUser> query = Database.query(TurbineUser.class);
    Expression exp =
      ExpressionFactory.inExp(TurbineUser.LOGIN_NAME_PROPERTY, memberNames);
    query.setQualifier(exp);

    List<TurbineUser> memberList =
      query.orderAscending(
        TurbineUser.EIP_MUSER_POSITION_PROPERTY
          + "."
          + EipMUserPosition.POSITION_PROPERTY).fetchList();

    return new ResultList<TurbineUser>(memberList);
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
    rd.setFirstNameKana(model.getFirstNameKana());
    rd.setLastNameKana(model.getLastNameKana());
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
   *
   * @param room
   */
  public void setRoom(EipTMessageRoom room) {
    this.room = room;
  }

  public int getRoomId() {
    return room.getRoomId();
  }

  public int getUserId() {
    return userId;
  }

  /**
   * @param targetUserId
   *          セットする targetUserId
   */
  public void setTargetUserId(int targetUserId) {
    this.targetUserId = targetUserId;
  }

  /**
   * @return targetUserId
   */
  public int getTargetUserId() {
    return targetUserId;
  }

  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }
}
