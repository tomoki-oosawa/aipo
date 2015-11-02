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
package com.aimluck.eip.message;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.StringUtils;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.message.util.MessageUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class MessageUserListSelectData extends
    ALAbstractSelectData<TurbineUser, TurbineUser> {

  public static final String TARGET_GROUP_NAME = "target_group_name";

  private String target_group_name;

  private List<ALEipGroup> myGroupList = null;

  private int userId;

  private ALStringField keyword = null;

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    myGroupList = ALEipUtils.getMyGroups(rundata);

    userId = ALEipUtils.getUserId(rundata);

  }

  @Override
  public void initField() {
    keyword = new ALStringField();
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
    setupLists(rundata, context);

    return MessageUtils.getUserList("all".equals(target_group_name)
      ? "LoginUser"
      : target_group_name, keyword.getValue());
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
    if (model.getUserId().intValue() != userId) {
      MessageUserResultData rd = new MessageUserResultData();
      rd.initField();
      rd.setUserId(model.getUserId());
      rd.setFirstName(model.getFirstName());
      rd.setLastName(model.getLastName());
      rd.setFirstNameKana(model.getFirstNameKana());
      rd.setLastNameKana(model.getLastNameKana());
      rd.setHasPhoto("T".equals(model.getHasPhoto())
        || "N".equals(model.getHasPhoto()));
      Date photoModified = model.getPhotoModified();
      if (photoModified != null) {
        rd.setPhotoModified(photoModified.getTime());
      }

      return rd;
    } else {
      return null;
    }
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
   * @param rundata
   * @param context
   */
  protected void setupLists(RunData rundata, Context context) {

    target_group_name = getTargetGroupName(rundata, context);
    current_filter = target_group_name;
    if ((!target_group_name.equals("")) && (!target_group_name.equals("all"))) {
      boolean existPost = false;
      for (int i = 0; i < myGroupList.size(); i++) {
        String pid = myGroupList.get(i).getName().toString();
        if (pid.equals(target_group_name)) {
          existPost = true;
          break;
        }
      }
      Map<Integer, ALEipPost> map = ALEipManager.getInstance().getPostMap();
      for (Map.Entry<Integer, ALEipPost> item : map.entrySet()) {
        String pid = item.getValue().getGroupName().toString();
        if (pid.equals(target_group_name)) {
          existPost = true;
          break;
        }
      }
      if (!existPost) {
        target_group_name = "all";
      }
    }
  }

  /**
   *
   * @param rundata
   * @param context
   * @return
   */
  protected String getTargetGroupName(RunData rundata, Context context) {
    return getTargetGroupName(rundata, context, TARGET_GROUP_NAME);
  }

  /**
   *
   * @param rundata
   * @param context
   * @param target_key
   * @return
   */
  protected String getTargetGroupName(RunData rundata, Context context,
      String target_key) {
    String target_group_name = null;
    String idParam = null;
    idParam = rundata.getParameters().getString(target_key);
    target_group_name = ALEipUtils.getTemp(rundata, context, target_key);

    if (idParam == null && target_group_name == null) {
      ALEipUtils.setTemp(rundata, context, target_key, "all");
      target_group_name = "all";
    } else if (idParam != null) {
      ALEipUtils.setTemp(rundata, context, target_key, idParam);
      target_group_name = idParam;
    }
    return target_group_name;
  }

  /**
   *
   * @return
   */
  public String getTargetGroupName() {
    return target_group_name;
  }

  /**
   *
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   *
   * @return
   */
  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   * @param keyword
   *          セットする keyword
   */
  public void setKeyword(String keyword) {
    this.keyword.setValue(keyword);
  }

  /**
   * @return keyword
   */
  public ALStringField getKeyword() {
    return keyword;
  }

  public boolean hasKeyword() {
    return !StringUtils.isEmpty(keyword.getValue());
  }
}
