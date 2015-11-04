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
package com.aimluck.eip.gadgets;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALOAuthConsumer;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.ALOAuthConsumerService;
import com.aimluck.eip.services.social.gadgets.ALGadgetSpec;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest.Status;
import com.aimluck.eip.services.social.model.ALApplicationPutRequest;
import com.aimluck.eip.services.social.model.ALOAuthConsumerPutRequest;
import com.aimluck.eip.services.social.model.ALOAuthConsumerPutRequest.Type;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 *
 */
public class GadgetsAdminFormData extends ALAbstractFormData {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(GadgetsAdminFormData.class.getName());

  private ALStringField url;

  private ALStringField title;

  private ALStringField description;

  private ALStringField icon;

  private List<ALOAuthConsumer> oAuthConsumers;

  private ALGadgetSpec metaData;

  private ALStringField sendActivity;

  /**
   * データの更新時に一時的にappIdを保存しておくための変数
   */
  private String appId;

  /**
   *
   */
  @Override
  public void initField() {
    url = new ALStringField();
    url.setFieldName(ALLocalizationUtils.getl10n("GADGETS_SETFIELDNAME_URL"));
    url.setTrim(true);

    title = new ALStringField();
    title.setFieldName(ALLocalizationUtils
      .getl10n("GADGETS_SETFIELDNAME_TITLE"));
    title.setTrim(true);

    description = new ALStringField();
    description.setFieldName(ALLocalizationUtils
      .getl10n("GADGETS_SETFIELDNAME_DESCRIPTION"));
    description.setTrim(true);

    icon = new ALStringField();
    icon.setFieldName(ALLocalizationUtils.getl10n("GADGETS_SETFIELDNAME_ICON"));
    icon.setTrim(true);

    oAuthConsumers = new ArrayList<ALOAuthConsumer>();

    sendActivity = new ALStringField();

  }

  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    if (this.getMode().equals(ALEipConstants.MODE_UPDATE)) {
      appId = ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
      ALApplication app =
        ALApplicationService.get(new ALApplicationGetRequest()
          .withAppId(appId)
          .withStatus(Status.ALL)
          .withIsDetail(true)
          .withIsFetchXml(true));
      if (app == null) {
        return false;
      }
      url.setValue(app.getUrl().getValue());
      title.setValue(app.getTitle().getValue());
      description.setValue(app.getDescription().getValue());
      icon.setValue(app.getIcon().getValue());
      oAuthConsumers = app.getOAuthConsumers();
      int size = oAuthConsumers.size();
      for (int i = 0; i < size; i++) {
        ALOAuthConsumer service = oAuthConsumers.get(i);

        String key = "consumerKey" + i;
        String keyString = rundata.getParameters().getString(key);
        service.getConsumerKey().setValue(keyString);

        key = "consumerSecret" + i;
        keyString = rundata.getParameters().getString(key);
        service.getConsumerSecret().setValue(keyString);

        key = "type" + i;
        keyString = rundata.getParameters().getString(key);
        service.getType().setValue(keyString);
      }
    }
    return res;
  }

  /**
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected void setValidator() throws ALPageNotFoundException,
      ALDBErrorException {
    url.setNotNull(true);
    url.limitMaxLength(255);
  }

  /**
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean validate(List<String> msgList)
      throws ALPageNotFoundException, ALDBErrorException {
    if (!ALEipConstants.MODE_UPDATE.equals(getMode())) {
      url.validate(msgList);
    }
    if (msgList.size() == 0) {
      metaData = ALApplicationService.getMetaData(url.getValue(), true);
      if (!ALEipConstants.MODE_UPDATE.equals(getMode())) {
        if (metaData == null) {
          msgList.add(ALLocalizationUtils
            .getl10n("GADGETS_ALERT_SET_VALID_GADGET_URL"));
        }
      }
    }
    return (msgList.size() == 0);
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    String appId =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);
    ALApplication app =
      ALApplicationService.get(new ALApplicationGetRequest()
        .withAppId(appId)
        .withStatus(Status.ALL)
        .withIsDetail(true)
        .withIsFetchXml(true));
    if (app == null) {
      return false;
    }
    url.setValue(app.getUrl().getValue());
    title.setValue(app.getTitle().getValue());
    description.setValue(app.getDescription().getValue());
    icon.setValue(app.getIcon().getValue());
    oAuthConsumers = app.getOAuthConsumers();
    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    try {
      String activityLoginName = null;
      // Activity
      if ("true".equals(sendActivity.getValue())) {
        activityLoginName =
          ALEipUtils.getALEipUser(rundata).getName().getValue();
      }

      ALApplicationService.create(new ALApplicationPutRequest()
        .withUrl(url.getValue())
        .withTitle(metaData.getTitle())
        .withActivityuserId(ALEipUtils.getUserId(rundata))
        .withDescription(metaData.getDescription())
        .withIcon(metaData.getIcon())
        .withActivityLoginName(activityLoginName));

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        ALEipUtils.getUserId(rundata),
        ALEventlogConstants.PORTLET_TYPE_GADGET,
        "アプリ「" + metaData.getTitle() + "」を追加");

    } catch (Throwable t) {
      logger.error(t, t);
      throw new ALDBErrorException();
    }
    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    try {

      ALApplicationService.update(appId, new ALApplicationPutRequest()
        .withTitle(metaData.getTitle())
        .withActivityuserId(ALEipUtils.getUserId(rundata))
        .withDescription(metaData.getDescription())
        .withIcon(metaData.getIcon()));

      for (ALOAuthConsumer service : oAuthConsumers) {
        ALOAuthConsumerService.put(new ALOAuthConsumerPutRequest().withAppId(
          appId).withName(service.getName().getValue()).withConsumerKey(
          service.getConsumerKey().getValue()).withConsumerSecret(
          service.getConsumerSecret().getValue()).withType(
          "RSA-SHA1".equalsIgnoreCase(service.getType().getValue())
            ? Type.RSASHA1
            : Type.HMACSHA1));
      }

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        ALEipUtils.getUserId(rundata),
        ALEventlogConstants.PORTLET_TYPE_GADGET,
        "アプリ「" + metaData.getTitle() + "」を更新");

    } catch (Throwable t) {
      logger.error(t, t);
      throw new ALDBErrorException();
    }

    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param msgList
   * @return
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {

    String appId =
      ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

    try {
      // アプリ名を取得
      String deletedAppTitle = "";
      ALApplication app =
        ALApplicationService.get(new ALApplicationGetRequest()
          .withAppId(appId)
          .withStatus(Status.ALL)
          .withIsDetail(true)
          .withIsFetchXml(true));
      if (app != null) {
        deletedAppTitle = app.getTitle().toString();
      }

      ALApplicationService.delete(appId);

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        ALEipUtils.getUserId(rundata),
        ALEventlogConstants.PORTLET_TYPE_GADGET,
        "アプリ「" + deletedAppTitle + "」を削除");

    } catch (Throwable t) {
      logger.error(t, t);
      throw new ALDBErrorException();
    }

    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param object
   */
  public boolean enableFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      if (!doCheckSecurity(rundata, context)) {
        return false;
      }

      String appId =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

      if (appId == null || "".equals(appId)) {
        return false;
      }

      ALApplicationService.enable(appId);

    } catch (Exception e) {
      Database.rollback();
      logger.error("gadgets", e);
      return false;
    }
    return true;
  }

  /**
   * @param rundata
   * @param context
   * @param object
   */
  public boolean disableFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      if (!doCheckSecurity(rundata, context)) {
        return false;
      }

      String appId =
        ALEipUtils.getTemp(rundata, context, ALEipConstants.ENTITY_ID);

      if (appId == null || "".equals(appId)) {
        return false;
      }

      ALApplicationService.disable(appId);

    } catch (Exception e) {
      logger.error("gadgets", e);
      return false;
    }
    return true;
  }

  public ALStringField getUrl() {
    return url;
  }

  public String getUrlCR() {
    return ALCommonUtils.replaceToAutoCR(url.toString());
  }

  /**
   * 
   * @return
   */
  public List<ALOAuthConsumer> getOAuthConsumers() {
    return oAuthConsumers;
  }

  /**
   * @return title
   */
  public ALStringField getTitle() {
    return title;
  }

  /**
   * @param title
   *          セットする title
   */
  public void setTitle(ALStringField title) {
    this.title = title;
  }

  /**
   * @return icon
   */
  public ALStringField getIcon() {
    return icon;
  }

  /**
   * @param icon
   *          セットする icon
   */
  public void setIcon(ALStringField icon) {
    this.icon = icon;
  }

  /**
   * @param url
   *          セットする url
   */
  public void setUrl(ALStringField url) {
    this.url = url;
  }

  /**
   * @param description
   *          セットする description
   */
  public void setDescription(ALStringField description) {
    this.description = description;
  }

  /**
   * @return description
   */
  public ALStringField getDescription() {
    return description;
  }

  /**
   * @return sendActivity
   */
  public ALStringField getSendActivity() {
    return sendActivity;
  }

  /**
   * @param sendActivity
   *          セットする sendActivity
   */
  public void setSendActivity(String sendActivity) {
    this.sendActivity.setValue(sendActivity);
  }

}
