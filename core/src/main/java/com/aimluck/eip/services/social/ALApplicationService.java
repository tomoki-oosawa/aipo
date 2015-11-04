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

import java.util.List;
import java.util.Map;

import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.social.gadgets.ALGadgetSpec;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationPutRequest;

/**
 * 
 */
public class ALApplicationService {

  private ALApplicationService() {

  }

  public static ALSocialApplicationHandler getService() {
    return ALSocialApplicationFactoryService
      .getInstance()
      .getSocialApplicationHandler();
  }

  public static ResultList<ALApplication> getList(
      ALApplicationGetRequest request) {
    return getService().getApplicationList(request);
  }

  public static ALApplication get(ALApplicationGetRequest request) {
    return getService().getApplication(request);
  }

  public static void create(ALApplicationPutRequest request) {
    getService().createApplication(request);
  }

  public static void update(String appId, ALApplicationPutRequest request) {
    getService().updateApplication(appId, request);

  }

  public static void delete(String... appIdList) {
    getService().deleteApplication(appIdList);
  }

  public static void delete(List<String> appIdList) {
    getService().deleteApplication(appIdList);
  }

  public static void enable(String... appIdList) {
    getService().enableApplication(appIdList);
  }

  public static void enable(List<String> appIdList) {
    getService().enableApplication(appIdList);
  }

  public static void disable(String... appIdList) {
    getService().disableApplication(appIdList);
  }

  public static void disable(List<String> appIdList) {
    getService().disableApplication(appIdList);
  }

  public static boolean checkAvailability(String appId) {
    return getService().checkApplicationAvailability(appId);
  }

  public static ALGadgetSpec getMetaData(String specUrl) {
    return getService().getMetaData(specUrl);
  }

  public static Map<String, ALGadgetSpec> getMetaData(List<String> specUrls,
      String view, boolean isDetail, boolean nochache) {
    return getService().getMetaData(specUrls, view, isDetail, nochache);
  }

  public static Map<String, ALGadgetSpec> getMetaData(List<String> specUrls) {
    return getService().getMetaData(specUrls, null, false, true);
  }

  public static ALGadgetSpec getMetaData(String specUrl, boolean isDetail) {
    return getService().getMetaData(specUrl, isDetail);
  }

  public static long getNextModuleId() {
    return getService().getNextModuleId();
  }

  public static void deleteUserData(String... loginNameList) {
    getService().deleteUserData(loginNameList);
  }

  public static void deleteUserData(List<String> loginNameList) {
    getService().deleteUserData(loginNameList);
  }

}
