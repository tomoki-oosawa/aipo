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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

import com.aimluck.eip.common.ALActivity;
import com.aimluck.eip.common.ALActivityCount;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALOAuthConsumer;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.social.gadgets.ALGadgetSpec;
import com.aimluck.eip.services.social.gadgets.ALOAuthService;
import com.aimluck.eip.services.social.model.ALActivityGetRequest;
import com.aimluck.eip.services.social.model.ALActivityPutRequest;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationPutRequest;
import com.aimluck.eip.services.social.model.ALOAuthConsumerPutRequest;

/**
 *
 */
public abstract class ALSocialApplicationHandler {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALSocialApplicationHandler.class.getName());

  public abstract ResultList<ALApplication> getApplicationList(
      ALApplicationGetRequest request);

  public abstract ALApplication getApplication(ALApplicationGetRequest request);

  public abstract void createApplication(ALApplicationPutRequest request);

  public abstract void updateApplication(String appId,
      ALApplicationPutRequest request);

  public abstract void deleteApplication(String... appIdList);

  public abstract void deleteApplication(List<String> appIdList);

  public abstract void enableApplication(String... appIdList);

  public abstract void enableApplication(List<String> appIdList);

  public abstract void disableApplication(String... appIdList);

  public abstract void disableApplication(List<String> appIdList);

  public abstract boolean checkApplicationAvailability(String appId);

  public abstract List<ALOAuthConsumer> getOAuthConsumer(String appId);

  public abstract void putOAuthConsumer(ALOAuthConsumerPutRequest request);

  public abstract String getContainerConfig(Property property);

  public abstract void putContainerConfig(Property property, String value);

  public abstract ResultList<ALActivity> getActivityList(
      ALActivityGetRequest request);

  public abstract ALActivity getActivity(ALActivityGetRequest request);

  public abstract ALActivityCount getActivityCount(ALActivityGetRequest request);

  public abstract void setAllReadActivity(String loginName);

  public abstract void setReadActivity(int activityId, String loginName);

  public abstract void createActivity(ALActivityPutRequest request);

  public abstract long getNextModuleId();

  public abstract void deleteUserData(String... loginNameList);

  public abstract void deleteUserData(List<String> loginNameList);

  protected abstract void daysFirstActivate(String uid);

  public static enum Property {

    LOCKED_DOMAIN_REQUIRED("lockedDomainRequired") {

      @Override
      public String defaultValue() {
        return "false";
      }
    },

    LOCKED_DOMAIN_SUFFIX("lockedDomainSuffix") {
      @Override
      public String defaultValue() {
        return "";
      }
    },

    UNLOCKED_DOMAIN("unLockedDomain") {
      @Override
      public String defaultValue() {
        return "";
      }
    },

    CACHE_GADGET_XML("cacheGadgetXml") {
      @Override
      public String defaultValue() {
        return "true";
      }
    },

    ACTIVITY_SAVE_LIMIT("activitySaveLimit") {
      @Override
      public String defaultValue() {
        return "30";
      }
    },

    CONTAINER_URL("containerUrl") {
      @Override
      public String defaultValue() {
        return JetspeedResources.getString("aipo.container.url");
      }

    };

    private final String property;

    private Property(String property) {
      this.property = property;
    }

    @Override
    public String toString() {
      return this.property;
    }

    public abstract String defaultValue();
  }

  protected String generateConsumerKey(String url) {
    return new StringBuilder(Database.getDomainName()).append(":").append(
      new String(Base64.encodeBase64(DigestUtils.sha(new StringBuilder()
        .append(System.nanoTime())
        .append("::")
        .append(url)
        .toString()
        .getBytes()))).replace("=", "")).toString();
  }

  protected String generateConsumerSecret() {
    return DigestUtils.shaHex(new StringBuilder(UUID.randomUUID().toString())
      .append(System.nanoTime())
      .toString());
  }

  public ALGadgetSpec getMetaData(String specUrl) {
    return getMetaData(specUrl, false);
  }

  public ALGadgetSpec getMetaData(String specUrl, boolean isDetail) {
    List<String> specUrls = new ArrayList<String>();
    specUrls.add(specUrl);
    Map<String, ALGadgetSpec> metaData =
      getMetaData(specUrls, "home", isDetail, true);
    return metaData.get(specUrl);
  }

  public Map<String, ALGadgetSpec> getMetaData(List<String> specUrls) {
    return getMetaData(specUrls, "home", false, true);
  }

  protected String getMetaDataUrl() {
    String baseUrl = ALContainerConfigService.get(Property.CONTAINER_URL);
    if (baseUrl == null || baseUrl.length() == 0) {
      HttpServletRequest request = HttpServletRequestLocator.get();
      Integer port = request.getServerPort();
      String scheme = request.getScheme();
      String continerPath = JetspeedResources.getString("aipo.container.path");
      baseUrl =
        scheme
          + "://127.0.0.1:"
          + port
          + (continerPath == null ? "" : continerPath);
    }
    return baseUrl + "/gadgets/metadata";
  }

  public Map<String, ALGadgetSpec> getMetaData(List<String> specUrls,
      String view, boolean isDetail, boolean nocache) {
    Map<String, ALGadgetSpec> maps = new HashMap<String, ALGadgetSpec>();
    if (specUrls == null || specUrls.size() == 0) {
      return maps;
    }
    try {

      HttpClient httpClient = new HttpClient();
      httpClient.getParams().setParameter("http.connection.timeout", 10000);
      httpClient.getParams().setParameter("http.socket.timeout", 10000);
      PostMethod postMethod = new PostMethod(getMetaDataUrl());
      postMethod.addRequestHeader("Content-Type", "application/javascript");
      postMethod.addParameter("st", "default:st");
      postMethod.addParameter("req", "1");
      postMethod.addParameter("callback", "1");
      JSONObject jsonObject = new JSONObject();
      JSONObject context = new JSONObject();
      context.put("country", "JP");
      context.put("language", "ja");
      context.put("view", view == null ? "home" : view);
      context.put("container", "default");
      context.put("nocache", nocache ? 1 : 0);
      if (isDetail) {
        context.put("withDescription", "1");
        context.put("withOAuthService", "1");
      }
      JSONArray gadgets = new JSONArray();
      for (String specUrl : specUrls) {
        JSONObject gadget = new JSONObject();
        gadget.put("url", specUrl);
        gadget.put("moduleId", 1);
        gadgets.add(gadget);
      }
      jsonObject.put("context", context);
      jsonObject.put("gadgets", gadgets);
      postMethod.setRequestEntity(new StringRequestEntity(
        jsonObject.toString(),
        "application/javascript",
        "UTF-8"));
      httpClient.executeMethod(postMethod);
      String result = postMethod.getResponseBodyAsString();
      JSONObject fromObject = JSONObject.fromObject(result);
      JSONArray jsonArray = (JSONArray) fromObject.get("gadgets");
      Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
      classMap.put("oauthService", ALOAuthService.class);
      classMap.put("userPrefs", HashMap.class);

      if (jsonArray == null) {
        return maps;
      }

      int size = jsonArray.size();
      for (int i = 0; i < size; i++) {
        try {
          JSONObject object = jsonArray.getJSONObject(i);
          Object e = object.get("errors");
          if (e == null) {
            ALGadgetSpec spec =
              (ALGadgetSpec) JSONObject.toBean(
                object,
                ALGadgetSpec.class,
                classMap);
            maps.put(spec.getUrl(), spec);
          }
        } catch (Throwable t) {
          logger.warn("[ALSocialApplicationHandler]", t);
        }
      }
    } catch (Throwable t) {
      logger.warn("[ALSocialApplicationHandler]", t);
    }
    return maps;
  }
}
