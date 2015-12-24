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
package com.aimluck.eip.services.social;

import com.aimluck.eip.common.ALActivity;
import com.aimluck.eip.common.ALActivityCount;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.social.model.ALActivityGetRequest;
import com.aimluck.eip.services.social.model.ALActivityPutRequest;

/**
 *
 */
public class ALActivityService {

  private ALActivityService() {

  }

  public static ALSocialApplicationHandler getService() {
    return ALSocialApplicationFactoryService
      .getInstance()
      .getSocialApplicationHandler();
  }

  public static ResultList<ALActivity> getList(ALActivityGetRequest request) {
    return getService().getActivityList(request);
  }

  public static ALActivity get(ALActivityGetRequest request) {
    return getService().getActivity(request);
  }

  public static ALActivityCount count(ALActivityGetRequest request) {
    return getService().getActivityCount(request);
  }

  public static void setAllRead(String loginName) {
    getService().setAllReadActivity(loginName);
  }

  public static void setRead(int activityId, String loginName) {
    getService().setReadActivity(activityId, loginName);
  }

  public static void create(ALActivityPutRequest request) {
    getService().createActivity(request);
  }
}
