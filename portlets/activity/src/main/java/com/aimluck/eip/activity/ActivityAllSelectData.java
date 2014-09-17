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

package com.aimluck.eip.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.activity.util.ActivityUtils;
import com.aimluck.eip.cayenne.om.social.Activity;
import com.aimluck.eip.common.ALAbstractMultiFilterSelectData;
import com.aimluck.eip.common.ALActivity;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.social.ALActivityService;
import com.aimluck.eip.services.social.model.ALActivityGetRequest;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ActivityAllSelectData extends
    ALAbstractMultiFilterSelectData<ALActivity, ALActivity> {

  /** Activity の総数 */
  private int activitySum;

  private String currentCategory;

  private ALStringField target_keyword;

  /** 部署一覧 */
  private List<ALEipGroup> postList;

  /** グループID */
  private String postId = "";

  /** グループ名 */
  private String postName = "";

  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    String tabParam = rundata.getParameters().getString("category");
    currentCategory = ALEipUtils.getTemp(rundata, context, "category");
    if (tabParam == null && currentCategory == null) {
      ALEipUtils.setTemp(rundata, context, "category", "all");
      currentCategory = "all";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "category", tabParam);
      currentCategory = tabParam;
    }
    target_keyword = new ALStringField();
    postList = ALEipUtils.getMyGroups(rundata);
    super.init(action, rundata, context);
  }

  /**
   * @return
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue(Activity.UPDATE_DATE_PROPERTY, Activity.UPDATE_DATE_PROPERTY);
    return map;
  }

  /**
   * @param model
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ALActivity getResultData(ALActivity model)
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
  protected ALActivity getResultDataDetail(ALActivity model)
      throws ALPageNotFoundException, ALDBErrorException {
    return model;
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ALActivity selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    return null;
  }

  /**
   * @param rundata
   * @param context
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected ResultList<ALActivity> selectList(RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    if (ActivityUtils.hasResetFlag(rundata, context)) {
      target_keyword.setValue("");
      ActivityUtils.resetFilter(rundata, context, ActivityAllSelectData.class
        .getName());
    } else {
      target_keyword.setValue(ActivityUtils.getTargetKeyword(rundata, context));
    }

    if (current_filterMap.containsKey("post")) {

      List<String> postIds = current_filterMap.get("post");
      boolean existPost = false;
      for (int i = 0; i < postList.size(); i++) {
        String pid = postList.get(i).getName().toString();
        if (pid.equals(postIds.get(0).toString())) {
          existPost = true;
          break;
        }
      }
      Map<Integer, ALEipPost> map = ALEipManager.getInstance().getPostMap();
      if (postIds != null && !postIds.isEmpty()) {
        for (Map.Entry<Integer, ALEipPost> item : map.entrySet()) {
          String pid = item.getValue().getGroupName().toString();
          if (pid.equals(postIds.get(0).toString())) {
            existPost = true;
            break;
          }
        }
      }
      if (existPost) {
        postId = postIds.get(0).toString();
        updatePostName();
        List<Integer> userId = ALEipUtils.getUserIds(postId);
        if (userId.isEmpty()) {
          return new ResultList<ALActivity>(new ArrayList<ALActivity>());
        }
      } else {
        current_filterMap.remove("post");
        updatePostName();

      }

    }
    int page = getCurrentPage();
    int limit = getRowsNum();
    String loginName = ALEipUtils.getALEipUser(rundata).getName().getValue();

    ResultList<ALActivity> list =
      ("all".equals(currentCategory)) ? ALActivityService
        .getList(new ALActivityGetRequest()
          .withLimit(limit)
          .withKeyword(target_keyword.getValue())
          .withLoginName(loginName)
          .withPriority(0f)
          .withPage(page)
          .withTargetLoginName(loginName)
          .withPostId(postId)) : ALActivityService
        .getList(new ALActivityGetRequest()
          .withLimit(limit)
          .withAppId(currentCategory)
          .withKeyword(target_keyword.getValue())
          .withLoginName(loginName)
          .withPriority(0f)
          .withPage(page)
          .withTargetLoginName(loginName)
          .withPostId(postId));

    setPageParam(list.getTotalCount());

    return list;

  }

  /**
   * パラメータをマップに変換します。
   * 
   * @param key
   * @param val
   */
  @Override
  protected void parseFilterMap(String key, String val) {
    super.parseFilterMap(key, val);

    Set<String> unUse = new HashSet<String>();

    for (Entry<String, List<String>> pair : current_filterMap.entrySet()) {
      if (pair.getValue().contains("0")) {
        unUse.add(pair.getKey());
      }
    }
    for (String unusekey : unUse) {
      current_filterMap.remove(unusekey);
    }
  }

  /**
   * Activity の総数を返す． <BR>
   * 
   * @return
   */
  public int getActivitySum() {
    return activitySum;
  }

  public String getCurrentCategory() {
    return currentCategory;
  }

  @Override
  public boolean hasAuthority() {
    // TODO: アクセス権限
    return true;
  }

  /**
   * @return target_keyword
   */
  public ALStringField getTargetKeyword() {
    return target_keyword;
  }

  /**
   * 部署一覧を取得します
   * 
   * @return postList
   */
  public List<ALEipGroup> getPostList() {
    return postList;
  }

  /**
   * 部署の一覧を取得する．
   * 
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  private void updatePostName() {
    postName = "";
    for (int i = 0; i < postList.size(); i++) {
      String pid = postList.get(i).getName().toString();
      if (pid.equals(postId.toString())) {
        postName = postList.get(i).getAliasName().toString();
        return;
      }
    }
    Map<Integer, ALEipPost> map = ALEipManager.getInstance().getPostMap();
    for (Map.Entry<Integer, ALEipPost> item : map.entrySet()) {
      String pid = item.getValue().getGroupName().toString();
      if (pid.equals(postId.toString())) {
        postName = item.getValue().getPostName().toString();
        return;
      }
    }
  }

  public String getPostName() {
    return postName;
  }

  public String getPostId() {
    return postId;
  }

}