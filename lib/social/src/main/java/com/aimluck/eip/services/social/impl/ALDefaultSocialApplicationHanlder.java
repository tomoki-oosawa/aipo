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

package com.aimluck.eip.services.social.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import com.aimluck.eip.cayenne.om.social.Activity;
import com.aimluck.eip.cayenne.om.social.ActivityMap;
import com.aimluck.eip.cayenne.om.social.Application;
import com.aimluck.eip.cayenne.om.social.ContainerConfig;
import com.aimluck.eip.cayenne.om.social.OAuthConsumer;
import com.aimluck.eip.common.ALActivity;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALOAuthConsumer;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.Operations;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.social.ALSocialApplicationConstants;
import com.aimluck.eip.services.social.ALSocialApplicationHandler;
import com.aimluck.eip.services.social.gadgets.ALGadgetSpec;
import com.aimluck.eip.services.social.gadgets.ALOAuthService;
import com.aimluck.eip.services.social.model.ALActivityGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationPutRequest;
import com.aimluck.eip.services.social.model.ALOAuthConsumerPutRequest;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest.Status;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ALDefaultSocialApplicationHanlder extends
    ALSocialApplicationHandler {

  private static final JetspeedLogger logger =
    JetspeedLogFactoryService.getLogger(ALDefaultSocialApplicationHanlder.class
      .getName());

  private static ALSocialApplicationHandler instance;

  private ALDefaultSocialApplicationHanlder() {
  }

  public static ALSocialApplicationHandler getInstance() {
    if (instance == null) {
      instance = new ALDefaultSocialApplicationHanlder();
    }
    return instance;
  }

  /**
   * @return
   */
  @Override
  public ResultList<ALApplication> getApplicationList(
      ALApplicationGetRequest request) {
    SelectQuery<Application> query = buildApplicationQuery(request);
    ResultList<Application> resultList = query.getResultList();
    List<ALApplication> list = new ArrayList<ALApplication>(resultList.size());
    List<String> specUrls = new ArrayList<String>(list.size());
    for (Application app : resultList) {
      specUrls.add(app.getUrl());
    }
    Map<String, ALGadgetSpec> metaData =
      getMetaData(specUrls, request.isDetail());
    for (Application app : resultList) {
      ALGadgetSpec gadgetSpec = metaData.get(app.getUrl());
      ALApplication model = new ALApplication();
      model.setAppId(app.getAppId());
      model.setTitle(gadgetSpec == null ? "現在利用できません" : gadgetSpec.getTitle());
      model.setConsumerKey(app.getConsumerKey());
      model.setConsumerSecret(app.getConsumerSecret());
      model.setUrl(app.getUrl());
      model.setStatus(app.getStatus());
      model.setUserPrefs(gadgetSpec.getUserPrefs());
      if (request.isDetail()) {
        model.setDescription(gadgetSpec.getDescription());
      }
      list.add(model);
    }
    ResultList<ALApplication> result =
      new ResultList<ALApplication>(list, resultList.getLimit(), resultList
        .getPage(), resultList.getTotalCount());
    return result;
  }

  /**
   * @param appId
   * @return
   */
  @Override
  public ALApplication getApplication(ALApplicationGetRequest request) {
    SelectQuery<Application> query = buildApplicationQuery(request);
    Application app = query.fetchSingle();
    if (app == null) {
      return null;
    }

    ALGadgetSpec gadgetSpec = getMetaData(app.getUrl(), request.isDetail());
    ALApplication model = new ALApplication();
    model.setAppId(app.getAppId());
    model.setTitle(gadgetSpec == null ? "現在利用できません" : gadgetSpec.getTitle());
    model.setConsumerKey(app.getConsumerKey());
    model.setConsumerSecret(app.getConsumerSecret());
    model.setUrl(app.getUrl());
    model.setStatus(app.getStatus());
    if (gadgetSpec != null) {
      model.setUserPrefs(gadgetSpec.getUserPrefs());
    }
    if (gadgetSpec != null && request.isDetail()) {
      model.setDescription(gadgetSpec.getDescription());
      List<ALOAuthConsumer> consumers = new ArrayList<ALOAuthConsumer>();
      List<ALOAuthService> services = gadgetSpec.getOAuthServices();
      @SuppressWarnings("unchecked")
      List<OAuthConsumer> consumerModels = app.getOauthConsumer();
      for (ALOAuthService service : services) {
        ALOAuthConsumer consumer = new ALOAuthConsumer();
        consumer.setAppId(app.getAppId());
        consumer.setName(service.getName());
        consumer.setAuthorizationUrl(service.getAuthorizationUrl());
        consumer.setRequestUrl(service.getRequestUrl());
        consumer.setAccessUrl(service.getAccessUrl());
        for (OAuthConsumer consumerModel : consumerModels) {
          if (service.getName().equals(consumerModel.getName())) {
            consumer.setType(consumerModel.getType());
            consumer.setConsumerKey(consumerModel.getConsumerKey());
            consumer.setConsumerSecret(consumerModel.getConsumerSecret());
          }
        }
        consumers.add(consumer);
      }
      model.addOAuthConsumers(consumers);
    }
    return model;
  }

  @Override
  public List<ALOAuthConsumer> getOAuthConsumer(String appId) {
    ALApplication app =
      getApplication(new ALApplicationGetRequest()
        .withAppId(appId)
        .withIsDetail(true)
        .withStatus(Status.ALL));
    return app.getOAuthConsumers();
  }

  @Override
  public void putOAuthConsumer(ALOAuthConsumerPutRequest request) {
    try {
      Date date = new Date();
      String appId = request.getAppId();
      String name = request.getName();
      Application app = Database.get(Application.class, "APP_ID", appId);
      if (app == null) {
        return;
      }
      @SuppressWarnings("unchecked")
      List<OAuthConsumer> oauthConsumers = app.getOauthConsumer();
      boolean has = false;
      if (oauthConsumers != null) {
        for (OAuthConsumer oauthConsumer : oauthConsumers) {
          if (oauthConsumer.getName().equals(name)) {
            oauthConsumer.setType(request.getType().value());
            oauthConsumer.setConsumerKey(request.getConsumerKey());
            oauthConsumer.setConsumerSecret(request.getConsumerSecret());
            oauthConsumer.setUpdateDate(date);
            has = true;
          }
        }
      }
      if (!has) {
        OAuthConsumer oauthConsumer = Database.create(OAuthConsumer.class);
        oauthConsumer.setApplication(app);
        oauthConsumer.setName(request.getName());
        oauthConsumer.setType(request.getType().value());
        oauthConsumer.setConsumerKey(request.getConsumerKey());
        oauthConsumer.setConsumerSecret(request.getConsumerSecret());
        oauthConsumer.setCreateDate(date);
        oauthConsumer.setUpdateDate(date);
      }

      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      throw new RuntimeException(t);
    }
  }

  /**
   * @param url
   */
  @Override
  public void createApplication(ALApplicationPutRequest request) {
    String url = request.getUrl();
    Date date = new Date();
    try {
      Application app = Database.create(Application.class);
      app.setAppId(url);
      app.setUrl(url);
      app.setTitle(request.getTitle());
      app.setConsumerKey(generateConsumerKey(url));
      app.setConsumerSecret(generateConsumerSecret());
      app.setStatus(ALSocialApplicationConstants.STATUS_ACTIVE);
      app.setDescription(request.getDescription());
      app.setCreateDate(date);
      app.setUpdateDate(date);

      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      throw new RuntimeException(t);
    }
  }

  /**
   * 
   * @param appId
   * @param request
   */
  @Override
  public void updateApplication(String appId, ALApplicationPutRequest request) {
    Date date = new Date();
    try {
      Application app = Database.get(Application.class, "APP_ID", appId);
      app.setTitle(request.getTitle());
      app.setDescription(request.getDescription());
      app.setUpdateDate(date);
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      throw new RuntimeException(t);
    }
  }

  /**
   * 
   * @param appIdList
   */
  @Override
  public void enableApplication(String... appIdList) {
    try {
      for (String appId : appIdList) {
        Application app = Database.get(Application.class, "APP_ID", appId);
        if (app != null) {
          app.setStatus(1);
        }
      }
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      throw new RuntimeException(t);
    }
  }

  /**
   * 
   * @param appIdList
   */
  @Override
  public void enableApplication(List<String> appIdList) {
    enableApplication(appIdList.toArray(new String[appIdList.size()]));
  }

  /**
   * 
   * @param appIdList
   */
  @Override
  public void disableApplication(String... appIdList) {
    try {
      for (String appId : appIdList) {
        Application app = Database.get(Application.class, "APP_ID", appId);
        if (app != null) {
          app.setStatus(0);
        }
      }
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      throw new RuntimeException(t);
    }
  }

  /**
   * 
   * @param appIdList
   */
  @Override
  public void disableApplication(List<String> appIdList) {
    disableApplication(appIdList.toArray(new String[appIdList.size()]));
  }

  /**
   * 
   * @param appIdList
   */
  @Override
  public void deleteApplication(String... appIdList) {
    try {
      for (String appId : appIdList) {
        Database.delete(Database.get(Application.class, "APP_ID", appId));
      }
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      throw new RuntimeException(t);
    }
  }

  /**
   * @param appIdList
   */
  @Override
  public void deleteApplication(List<String> appIdList) {
    deleteApplication(appIdList.toArray(new String[appIdList.size()]));
  }

  @Override
  public boolean checkApplicationAvailability(String appId) {
    try {
      Application app = Database.get(Application.class, "APP_ID", appId);
      if (app == null) {
        return false;
      }
      Integer status = app.getStatus();
      if (status == null) {
        return false;
      }
      return status.intValue() == 1;
    } catch (Throwable t) {
      logger.warn(t);
      return false;
    }
  }

  protected SelectQuery<Application> buildApplicationQuery(
      ALApplicationGetRequest request) {
    SelectQuery<Application> query = Database.query(Application.class);
    int limit = request.getLimit();
    int page = request.getPage();
    Status status = request.getStatus();
    if (limit > 0) {
      query.limit(limit);
    }
    if (page > 0) {
      query.page(page);
    }
    switch (status) {
      case ACTIVE:
        query.where(Operations.eq(Application.STATUS_PROPERTY, 1));
        break;
      case INACTIVE:
        query.where(Operations.eq(Application.STATUS_PROPERTY, 0));
        break;
      default:
        // ignore
    }
    String appId = request.getAppId();
    if (appId != null && appId.length() > 0) {
      query.where(Operations.eq(Application.APP_ID_PROPERTY, appId));
    }
    query.orderAscending(Application.TITLE_PROPERTY);
    return query;
  }

  /**
   * 
   * @param property
   * @return
   */
  @Override
  public String getContainerConfig(Property property) {
    ContainerConfig config =
      Database
        .query(ContainerConfig.class)
        .where(Operations.eq(ContainerConfig.KEY_PROPERTY, property.toString()))
        .fetchSingle();

    if (config == null) {
      return property.defaultValue();
    }

    return config.getValue();
  }

  /**
   * 
   * @param property
   * @param value
   */
  @Override
  public void putContainerConfig(Property property, String value) {
    try {
      ContainerConfig config =
        Database
          .query(ContainerConfig.class)
          .where(
            Operations.eq(ContainerConfig.KEY_PROPERTY, property.toString()))
          .fetchSingle();
      if (config == null) {
        config = Database.create(ContainerConfig.class);
        config.setKey(property.toString());
      }
      config.setValue(value);
      Database.commit();
    } catch (Throwable t) {
      Database.rollback();
      throw new RuntimeException(t);
    }
  }

  @Override
  public ResultList<ALActivity> getActivityList(ALActivityGetRequest request) {

    SelectQuery<Activity> query = buildActivityQuery(request);
    ResultList<Activity> resultList = query.getResultList();
    List<ALActivity> list = new ArrayList<ALActivity>(resultList.size());
    for (Activity model : resultList) {
      ALActivity activity = new ALActivity();
      activity.setId(model.getId());
      activity.setAppId(model.getAppId());
      activity.setTitle(model.getTitle());
      activity.setUpdateDate(model.getUpdateDate());
      activity.setExternalId(model.getExternalId());
      activity.setPortletParams(model.getPortletParams());
      try {
        ALEipUser user = ALEipUtils.getALEipUser(model.getLoginName());
        activity.setDisplayName(user.getAliasName().getValue());
      } catch (ALDBErrorException e) {
        //
      }
      list.add(activity);
    }
    ResultList<ALActivity> result =
      new ResultList<ALActivity>(list, resultList.getLimit(), resultList
        .getPage(), resultList.getTotalCount());
    return result;
  }

  @Override
  public ALActivity getActivity(ALActivityGetRequest request) {
    SelectQuery<Activity> query = buildActivityQuery(request);
    Activity model = query.fetchSingle();
    if (model == null) {
      return null;
    }
    ALActivity activity = new ALActivity();
    activity.setId(model.getId());
    activity.setAppId(model.getAppId());
    activity.setTitle(model.getTitle());
    activity.setUpdateDate(model.getUpdateDate());
    activity.setExternalId(model.getExternalId());
    activity.setPortletParams(model.getPortletParams());
    try {
      ALEipUser user = ALEipUtils.getALEipUser(model.getLoginName());
      activity.setDisplayName(user.getAliasName().getValue());
    } catch (ALDBErrorException e) {
      //
    }
    return activity;
  }

  @Override
  public int getActivityCount(ALActivityGetRequest request) {
    SelectQuery<Activity> query = buildActivityQuery(request);
    return query.getCount();
  }

  public void setReadActivity(int activityId, String loginName) {
    // FIXME:
  }

  protected SelectQuery<Activity> buildActivityQuery(
      ALActivityGetRequest request) {
    SelectQuery<Activity> query = Database.query(Activity.class);
    int limit = request.getLimit();
    if (limit > 0) {
      query.limit(limit);
    }
    int page = request.getPage();
    if (page > 0) {
      query.page(page);
    }
    int isRead = request.isRead();
    if (isRead > 0) {
      query.where(Operations.eq(Activity.ACTIVITY_MAPS_PROPERTY
        + "."
        + ActivityMap.IS_READ_PROPERTY, isRead));
    }
    String loginName = request.getLoginName();
    if (loginName != null && loginName.length() > 0) {
      query.where(Operations.eq(Activity.LOGIN_NAME_PROPERTY, loginName));
    }
    String targetLoginName = request.getTargetLoginName();
    if (targetLoginName != null && targetLoginName.length() > 0) {
      query.where(Operations.eq(Activity.ACTIVITY_MAPS_PROPERTY
        + "."
        + ActivityMap.LOGIN_NAME_PROPERTY, targetLoginName));
    }
    String appId = request.getAppId();
    if (appId != null && appId.length() > 0) {
      query.where(Operations.eq(Activity.APP_ID_PROPERTY, appId));
    }
    query.orderDesending(Activity.UPDATE_DATE_PROPERTY);
    return query;
  }
}
