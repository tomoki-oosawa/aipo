/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2008 Aimluck,Inc.
 * http://aipostyle.com/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aimluck.eip.modules.actions;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.modules.actions.JetspeedSessionValidator;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.LoginException;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.orm.DatabaseOrmService;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.orgutils.ALOrgUtilsFactoryService;
import com.aimluck.eip.util.orgutils.ALOrgUtilsHandler;

/**
 * セッションを制御するクラスです。 <br />
 * 
 */
public class ALSessionValidator extends JetspeedSessionValidator {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALSessionValidator.class.getName());

  /**
   * 
   * @see org.apache.turbine.modules.Action#doPerform(org.apache.turbine.util.RunData)
   */
  @Override
  public void doPerform(RunData data) throws Exception {

    try {
      super.doPerform(data);
    } catch (Throwable other) {
      data.setScreenTemplate(JetspeedResources
        .getString(TurbineConstants.TEMPLATE_ERROR));
      String message = other.getMessage() != null ? other.getMessage() : other
        .toString();
      data.setMessage(message);
      data.setStackTrace(org.apache.turbine.util.StringUtils.stackTrace(other),
        other);
      return;
    }

    // for switching theme org by org
    Context context = org.apache.turbine.services.velocity.TurbineVelocity
      .getContext(data);
    ALOrgUtilsHandler handler = ALOrgUtilsFactoryService.getInstance()
      .getOrgUtilsHandler();
    HashMap<String, String> attribute = handler
      .getParameters(DatabaseOrmService.getInstance().getOrgId(data));
    for (Map.Entry<String, String> e : attribute.entrySet()) {
      context.put(e.getKey(), e.getValue());
    }

    // for preventing XSS on user name
    context.put("utils", new ALCommonUtils());

    JetspeedUser loginuser = (JetspeedUser) data.getUser();
    if (loginuser == null || !loginuser.hasLoggedIn()) {
      String username = data.getParameters().getString("username", "");
      String password = data.getParameters().getString("password", "");
      if (username.length() > 0) {
        try {
          JetspeedUser juser = JetspeedSecurity.login(username, password);
          if (juser != null && "F".equals(juser.getDisabled())) {
            JetspeedSecurity.saveUser(juser);
          } else {
            data.setUser(JetspeedSecurity.getAnonymousUser());
            data.setMessage("このユーザーは現在無効化されています。担当者様にご確認ください。");
            data.getUser().setHasLoggedIn(Boolean.valueOf(false));
          }
        } catch (LoginException e) {
        }
      }
    }

    JetspeedUser user = (JetspeedUser) data.getUser();

    if ((user == null || !user.hasLoggedIn())
      && JetspeedResources.getBoolean("automatic.logon.enable", false)) {

      if (data.getRequest().getCookies() != null) {
        String userName = data.getCookies().getString("username", "");
        String loginCookieValue = data.getCookies()
          .getString("logincookie", "");

        if (userName.length() > 0 && loginCookieValue.length() > 0) {
          try {
            user = JetspeedSecurity.getUser(userName);
            if (user.getPerm("logincookie", "").equals(loginCookieValue)) {
              data.setUser(user);
              user.setHasLoggedIn(Boolean.valueOf(true));
              user.updateLastLogin();
              data.save();
            }
          } catch (LoginException noSuchUser) {
          } catch (org.apache.jetspeed.services.security.UnknownUserException unknownUser) {
            logger.warn("Username from the cookie was not found: " + userName);
          } catch (Exception other) {
            logger.error(other);
          }
        }
      }

    } else if ((user == null || !user.hasLoggedIn())
      && !JetspeedResources.getBoolean("automatic.logon.enable", false)) {

      // 理由等 ：セッションが切れた時に、エラーメッセージの表示に不具合あり
      // 対処方法：ログイン画面以外でユーザがログインしていない場合はエラーページへスクリーンを変更
      String uri = data.getRequest().getRequestURI().trim();
      String servername = data.getServletConfig().getServletName();

      String template = data.getScreenTemplate();

      Class<?> cls = null;
      try {
        cls = Class.forName(new StringBuffer().append(
          "com.aimluck.eip.modules.screens.").append(template).toString());
      } catch (Exception e) {
        cls = null;
      }
      String newTemplate = null;
      if (cls != null) {
        if (Class.forName("com.aimluck.eip.modules.screens.ALJSONScreen")
          .isAssignableFrom(cls)) {
          newTemplate = "ALJSONTimeoutScreen";
        } else if (Class.forName(
          "com.aimluck.eip.modules.screens.ALVelocityScreen").isAssignableFrom(
          cls)) {
          newTemplate = "ALVelocityTimeoutScreen";
        }
      }
      if (newTemplate != null) {
        data.setScreenTemplate(newTemplate);
        // セッションの削除
        if (data.getSession() != null) {
          try {
            data.getSession().invalidate();
          } catch (IllegalStateException ex) {
            logger.debug("セッションは既に削除されています。");
          }
        }

      } else {
        if (!uri.equals("/" + servername + "/portal/")
          && !uri.equals("/" + servername + "/portal")) {
          data.setScreenTemplate("Timeout");

          StringBuffer sb = new StringBuffer(uri);
          int count = 0;
          String key = null;
          Enumeration<?> enu = data.getRequest().getParameterNames();
          if (enu.hasMoreElements()) {
            sb.append("?");
          }
          while (enu.hasMoreElements()) {
            if (count != 0) {
              sb.append("&");
            }
            key = (String) enu.nextElement();
            sb.append(key).append("=").append(
              data.getRequest().getParameter(key));
            count = count + 1;
          }

          data.getUser().setTemp("redirect", sb.toString());

          data.getUser().setTemp("alEipUtils", new ALEipUtils());
          data.getUser().setTemp("alEipManager", ALEipManager.getInstance());

          // セッションの削除
          if (data.getSession() != null) {
            try {
              data.getSession().invalidate();
            } catch (IllegalStateException ex) {
              logger.debug("セッションは既に削除されています。");
            }
          }
        }
      }
    }

    JetspeedRunData jdata = null;
    try {
      jdata = (JetspeedRunData) data;
    } catch (ClassCastException e) {
      logger.error(
        "The RunData object does not implement the expected interface, "
          + "please verify the RunData factory settings", e);
      return;
    }
    String language = data.getRequest().getParameter("js_language");

    if (null != language) {
      user.setPerm("language", language);
    }

    CustomLocalizationService locService = (CustomLocalizationService) ServiceUtil
      .getServiceByName(LocalizationService.SERVICE_NAME);
    Locale locale = locService.getLocale(data);

    if (locale == null) {
      locale = new Locale(TurbineResources.getString("locale.default.language",
        "en"), TurbineResources.getString("locale.default.country", "US"));
    }

    data.getUser().setTemp("locale", locale);

    String paramPortlet = jdata.getParameters().getString("js_peid");
    if (paramPortlet != null && paramPortlet.length() > 0) {
      jdata.setJs_peid(paramPortlet);
    }

  }

}
