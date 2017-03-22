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
package com.aimluck.eip.modules.actions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.modules.actions.TemplateSessionValidator;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.JetspeedSecurityCache;
import org.apache.jetspeed.services.security.LoginException;
import org.apache.jetspeed.util.Base64;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALConstants;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.filter.ALDigestAuthenticationFilter;
import com.aimluck.eip.http.ServletContextLocator;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.services.preexecute.ALPreExecuteService;
import com.aimluck.eip.services.social.gadgets.ALGadgetContext;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.util.ALSessionUtils;

/**
 * セッションを制御するクラスです。 <br />
 *
 */
public class ALSessionValidator extends TemplateSessionValidator {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALSessionValidator.class.getName());

  /**
   *
   * @param data
   * @throws Exception
   */
  @Override
  public void doPerform(RunData data) throws Exception {
    try {
      // first, invoke our superclass action to make sure
      // we follow Turbine evolutions
      // FIXME: if the user is not found (this can happen, for instance,
      // if the anonymous user is not in the DB), it throws a terrible exception
      // in the user's face
      boolean hasError = false;
      try {
        super.doPerform(data);
      } catch (Throwable other) {
        data.setScreenTemplate(JetspeedResources
          .getString(TurbineConstants.TEMPLATE_ERROR));
        String message =
          other.getMessage() != null ? other.getMessage() : other.toString();
        data.setMessage(message);
        data.setStackTrace(org.apache.turbine.util.StringUtils
          .stackTrace(other), other);
        hasError = true;
      }
      JetspeedUser user = (JetspeedUser) data.getUser();

      if (!hasError) {
        // if the user is not logged in and auto-login is enable - try and do
        // it.
        if ((user == null || !user.hasLoggedIn())
          && JetspeedResources.getBoolean("automatic.logon.enable", false)) {
          // need to make sure there are cookies - turbine does not handle this
          // currently
          if (data.getRequest().getCookies() != null) {
            // check for user in cookie
            String userName = data.getCookies().getString("username", "");
            String loginCookieValue =
              data.getCookies().getString("logincookie", "");

            if (userName.length() > 0 && loginCookieValue.length() > 0) {
              try {
                if (userName.equals(JetspeedSecurity
                  .getAnonymousUser()
                  .getUserName())) {

                }
                user = JetspeedSecurity.getUser(userName);
                if (user != null
                  && !userName.equals(JetspeedSecurity
                    .getAnonymousUser()
                    .getUserName())) {
                  if (user.getPerm("logincookie", "").equals(loginCookieValue)) {
                    // cookie is present and correct - log the user in
                    if (ALEipConstants.USER_STAT_ENABLED.equals(user
                      .getDisabled())) {

                      // IPA#70075625
                      // Sesion Fixation 対策
                      JetspeedRunData automaticloginjdata = null;
                      try {
                        automaticloginjdata = (JetspeedRunData) data;
                      } catch (ClassCastException e) {
                        logger.error(
                          "The RunData object does not implement the expected interface, "
                            + "please verify the RunData factory settings",
                          e);
                        return;
                      }
                      // Session ID を再発行する
                      automaticloginjdata.getSession().invalidate();
                      automaticloginjdata.setSession(automaticloginjdata
                        .getRequest()
                        .getSession(true));
                      data.setUser(user);
                      user.setHasLoggedIn(Boolean.TRUE);
                      user.updateLastLogin();
                      data.save();

                      // イベントログに自動ログイン時にはイベントログを残さない。

                      // for security
                      data.getUser().setTemp(
                        ALEipConstants.SECURE_ID,
                        ALCommonUtils.getSecureRandomString());
                    }
                  }
                }
              } catch (LoginException noSuchUser) {
                // user not found - ignore it - they will not be logged in
                // automatically
                logger.warn(
                  "User denied authentication: " + userName,
                  noSuchUser);
              } catch (org.apache.jetspeed.services.security.UnknownUserException unknownUser) {
                // user not found - ignore it - they will not be logged in
                // automatically
                logger.warn("Username from the cookie was not found: "
                  + userName);
              } catch (Exception other) {
                logger.error(other);
              }
            }
          }
        }

        // now, define Jetspeed specific properties, using the customized
        // RunData properties
        JetspeedRunData jdata = null;

        try {
          jdata = (JetspeedRunData) data;
        } catch (ClassCastException e) {
          logger.error(
            "The RunData object does not implement the expected interface, "
              + "please verify the RunData factory settings",
            e);
          return;
        }
        String language = data.getRequest().getParameter("js_language");

        if (null != language) {
          user.setPerm("language", language);
        }

        // Get the locale store it in the user object
        CustomLocalizationService locService =
          (CustomLocalizationService) ServiceUtil
            .getServiceByName(LocalizationService.SERVICE_NAME);
        Locale locale = locService.getLocale(data);

        if (locale == null) {
          locale =
            new Locale(TurbineResources.getString(
              "locale.default.language",
              "en"), TurbineResources.getString("locale.default.country", "US"));
        }

        data.getUser().setTemp("locale", locale);

        // if a portlet is referenced in the parameters request, store it
        // in the RunData object
        String paramPortlet = jdata.getParameters().getString("js_peid");
        if (paramPortlet != null && paramPortlet.length() > 0) {
          jdata.setJs_peid(paramPortlet);
        }
      }

    } catch (Throwable other) {
      setOrgParametersForError(data);
      data.setScreenTemplate(JetspeedResources
        .getString(TurbineConstants.TEMPLATE_ERROR));
      return;
    }
    // not login and can not connect database
    if (checkDbError(data)) {
      setOrgParametersForError(data);
      data.setScreenTemplate(ALConstants.DB_ERROR_TEMPLATE);
      return;
    }

    // セッションハイジャック対策
    // CookieでセッションIDが渡されていなければエラー画面を表示
    if (data.getRequest().isRequestedSessionIdFromURL()) {
      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(data);
      String url = jsLink.getHomePage().toString().replaceAll(";.*", "");
      data.setRedirectURI(url);
      return;
    }

    JetspeedUser loginuser = (JetspeedUser) data.getUser();

    if (isLogin(loginuser, data)) {
      try {
        JetspeedSecurityCache.load(loginuser.getUserName());
      } catch (Exception e1) {
        // login and can not connect database
        String message = e1.getMessage();
        if (message != null
          && message.indexOf(ALConstants.DB_ERROR_DETECT) != -1) {
          setOrgParametersForError(data);
          String template = data.getParameters().get("template");
          if (template.endsWith("DBError")) {
            data.setScreenTemplate(ALConstants.DB_ERROR_TEMPLATE);
          } else {
            ALEipUtils.redirectDBError(data);
          }
          return;
        }
      }
    }

    if (ALSessionUtils.isImageRequest(data)) {
      if (isLogin(loginuser, data)) {
        return;
      }
    }

    if (ALSessionUtils.isJsonScreen(data)) {
      if (isLogin(loginuser, data)) {
        return;
      }
    }

    if (data.getRequest().getAttribute(
      ALDigestAuthenticationFilter.REQUIRE_DIGEST_AUTH) != null) {
      HttpServletRequest hreq = data.getRequest();
      HttpServletResponse hres = data.getResponse();
      if (!isLogin(loginuser, data)) {
        String auth = hreq.getHeader("Authorization");

        if (auth == null) {
          requireAuth(hres);
          return;

        } else {
          try {
            String decoded = decodeAuthHeader(auth);

            int pos = decoded.indexOf(":");
            String username = decoded.substring(0, pos);
            String password = decoded.substring(pos + 1);

            JetspeedUser juser = JetspeedSecurity.login(username, password);
            data.getUser().setTemp(
              ALEipConstants.LAST_PASSWORD_LOGIN,
              new Date());
            if (juser != null && "F".equals(juser.getDisabled())) {
              JetspeedSecurity.saveUser(juser);
            } else {
              requireAuth(hres);
              return;
            }

          } catch (RuntimeException ex) {
            // RuntimeException
            requireAuth(hres);
            return;
          } catch (Exception ex) {
            requireAuth(hres);
            return;

          }
        }
      }

      if (isICalRequest(data)) {
        data.setScreenTemplate("ScheduleiCalScreen");
        return;
      } else {
        hres.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
      }
    }

    Context context =
      org.apache.turbine.services.velocity.TurbineVelocity.getContext(data);
    // for switching theme org by org
    setOrgParameters(data, context);
    // for preventing XSS on user name
    context.put("utils", new ALCommonUtils());

    context.put("l10n", ALLocalizationUtils.createLocalization(data));

    // Cookie無効エラーを検知している場合、ログインさせない
    if (!isLogin(loginuser, data)
      && !data.getParameters().get("template").equals("CookieError")) {
      String username = data.getParameters().getString("username", "");
      String password = data.getParameters().getString("password", "");
      if (username.length() > 0) {

        if (ALCellularUtils.isSmartPhone(data) && "admin".equals(username)) {
          data.setUser(JetspeedSecurity.getAnonymousUser());
          data.setMessage(ALLocalizationUtils
            .getl10n("LOGINACTION_LOGIN_ONLY_PC"));
          data.getUser().setHasLoggedIn(Boolean.FALSE);
        } else {

          try {
            loginuser = JetspeedSecurity.login(username, password);
            if (loginuser != null && "F".equals(loginuser.getDisabled())) {
              JetspeedSecurity.saveUser(loginuser);
            } else {
              data.setUser(JetspeedSecurity.getAnonymousUser());
              data.setMessage(ALLocalizationUtils
                .getl10n("LOGINACTION_INVALIDATION_USER"));
              data.getUser().setHasLoggedIn(Boolean.FALSE);
            }
          } catch (LoginException e) {
          }
        }
      }
    }

    String externalLoginUrl = ALConfigService.get(Property.EXTERNAL_LOGIN_URL);

    boolean isScreenTimeout = false;
    if (!isLogin(loginuser, data)) {
      // 未ログインの時

      // 理由等 ：セッションが切れた時に、エラーメッセージの表示に不具合あり
      // 対処方法：ログイン画面以外でユーザがログインしていない場合はエラーページへスクリーンを変更
      String uri = data.getRequest().getRequestURI().trim();

      String template = data.getScreenTemplate();

      Class<?> cls = null;
      try {
        @SuppressWarnings("unchecked")
        Vector<String> packages =
          JetspeedResources.getVector("module.packages");
        for (String pk : packages) {
          try {
            cls =
              Class.forName(new StringBuffer()
                .append(pk)
                .append(".screens.")
                .append(template)
                .toString());
          } catch (Throwable ignore) {
            // ignore
          }
        }
      } catch (Exception e) {
        cls = null;
      }
      String newTemplate = null;
      if (cls != null) {
        if (Class
          .forName("com.aimluck.eip.modules.screens.ALJSONScreen")
          .isAssignableFrom(cls)) {
          newTemplate = "ALJSONTimeoutScreen";
        } else if (Class.forName(
          "com.aimluck.eip.modules.screens.ALVelocityScreen").isAssignableFrom(
          cls)) {
          newTemplate = "ALVelocityTimeoutScreen";
        }
      }
      if (newTemplate != null) {
        isScreenTimeout = true;
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
        String contextPath = ServletContextLocator.get().getContextPath();
        if ("/".equals(contextPath)) {
          contextPath = "";
        }
        String portalPath = contextPath + "/portal";
        if (!uri.equals(portalPath + "/") && !uri.equals(portalPath)) {
          data.setScreenTemplate("Timeout");

          if (!"".equals(externalLoginUrl)) {
            // ログイン画面へリダイレクト
            data.setRedirectURI(externalLoginUrl);
          }

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

          if (data.getUser() != null) {
            data.getUser().setTemp(
              "redirect",
              StringEscapeUtils.escapeHtml(sb.toString()));
            context.put("alEipUtils", new ALEipUtils());
            context.put("alEipManager", ALEipManager.getInstance());
          }

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
          + "please verify the RunData factory settings",
        e);
      return;
    }
    String language = data.getRequest().getParameter("js_language");

    if (null != language) {
      loginuser.setPerm("language", language);
    }

    CustomLocalizationService locService =
      (CustomLocalizationService) ServiceUtil
        .getServiceByName(LocalizationService.SERVICE_NAME);
    Locale locale = locService.getLocale(data);

    if (locale == null) {
      locale =
        new Locale(
          TurbineResources.getString("locale.default.language", "en"),
          TurbineResources.getString("locale.default.country", "US"));
    }

    if (loginuser != null) {
      loginuser.setTemp("locale", locale);
    }

    String paramPortlet = jdata.getParameters().getString("js_peid");
    if (paramPortlet != null && paramPortlet.length() > 0) {
      jdata.setJs_peid(paramPortlet);
    }

    // Ajaxリクエストでセッションタイムアウトした場合はリダイレクトしない
    if (!isScreenTimeout && !"".equals(externalLoginUrl)) {
      HttpServletRequest request = data.getRequest();
      if (!isLogin(loginuser, data)) {
        StringBuilder buf = new StringBuilder();
        buf.append(request.getScheme()).append("://").append(
          request.getServerName());
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
          //
        } else {
          buf.append(":").append(request.getServerPort());
        }

        buf.append(request.getRequestURI());
        String queryString = request.getQueryString();
        if (queryString != null && !"".equals(queryString)) {
          buf.append("?").append(queryString);
        }
        String url = buf.toString();
        if (!url.equals(externalLoginUrl)) {
          data.setRedirectURI(externalLoginUrl);
        }
      }
    }

    if (isLogin(loginuser, data)) {

      ALPreExecuteService.migratePsml(data, context);

      boolean hasMessage = false;
      Map<String, Entry> portlets = ALEipUtils.getGlobalPortlets(data);
      Entry entry = portlets.get("Message");
      if (entry != null) {
        if (entry.getId().equals(jdata.getJs_peid())) {
          hasMessage = true;
        }
      }
      String client = ALEipUtils.getClient(data);

      boolean push = (!"IPHONE".equals(client)) || hasMessage;

      HttpServletRequest request = ((JetspeedRunData) data).getRequest();
      String requestUrl = request.getRequestURL().toString();

      String checkActivityUrl =
        ALConfigService.get(Property.CHECK_ACTIVITY_URL);
      String interval = ALConfigService.get(Property.CHECK_ACTIVITY_INTERVAL);

      ALEipUser eipUser = ALEipUtils.getALEipUser(data);
      String orgId = Database.getDomainName();
      String viewer =
        new StringBuilder(orgId).append(":").append(
          eipUser.getName().getValue()).toString();

      ALGadgetContext gadgetContext =
        new ALGadgetContext(data, viewer, "1", "/", 0);

      String relayUrl = ALConfigService.get(Property.CHECK_ACTIVITY_RELAY_URL);
      String rpctoken = String.valueOf(System.nanoTime());
      String checkUrl =
        new StringBuilder("".equals(checkActivityUrl)
          ? "check.html"
          : checkActivityUrl)
          .append("?")
          .append("st=")
          .append(gadgetContext.getSecureToken())
          .append("&parent=")
          .append(URLEncoder.encode(requestUrl, "utf-8"))
          .append("&interval=")
          .append(interval)
          .append("&push=")
          .append(push ? 1 : 0)
          .append("#rpctoken=")
          .append(rpctoken)
          .toString();
      if (data.getSession() != null
        && Boolean.parseBoolean((String) data.getSession().getAttribute(
          "changeToPc"))) { // PC表示切り替え用
        context.put("client", ALEipUtils.getClient(data));

      }

      context.put("requestUrl", requestUrl);
      context.put("relayUrl", relayUrl);
      context.put("rpctoken", rpctoken);
      context.put("checkUrl", checkUrl);
      context.put("st", gadgetContext.getSecureToken());
      context.put("hasAuthorityCustomize", ALEipUtils.getHasAuthority(
        data,
        context,
        ALAccessControlConstants.VALUE_ACL_UPDATE));
    }
  }

  protected void requireAuth(HttpServletResponse hres) throws IOException {
    hres.setHeader("WWW-Authenticate", "BASIC realm=\"Aipo\"");
    hres.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }

  protected String decodeAuthHeader(String header)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    String ret = "";

    try {
      String encStr = header.substring(6);

      byte[] dec = Base64.decodeAsByteArray(encStr);
      ret = new String(dec);

    } catch (Exception ex) {
      ret = "";
    }

    return ret;
  }

  private boolean isLogin(JetspeedUser loginuser, RunData rundata) {
    boolean result = (loginuser != null && loginuser.hasLoggedIn());
    if (result) {
      TurbineUser tuser = null;
      try {
        tuser = ALEipUtils.getTurbineUser(loginuser.getUserName());
      } catch (ALDBErrorException e) {
        return false;
      }
      if (tuser == null) {
        return false;
      }
      // 無効化ユーザーをチェック
      /*-
      String disabled = tuser.getDisabled();
      if (!"F".equals(disabled)) {
        return false;
      }
       */
      // パスワードの有効期限をチェック
      Date lastPasswordLogin = null;
      try {
        lastPasswordLogin =
          (Date) rundata.getSession().getAttribute(
            ALEipConstants.LAST_PASSWORD_LOGIN);
      } catch (Throwable ignore) {

      }
      if (lastPasswordLogin == null) {
        try {
          String lastLoginValue = rundata.getCookies().get("lastlogin");
          if (lastLoginValue != null) {
            String decrypt =
              ALCommonUtils.decrypt(JetspeedResources.getString(
                "aipo.cookie.encryptKey",
                "secureKey"), ALCommonUtils.decodeBase64(lastLoginValue));
            if (decrypt != null) {
              lastPasswordLogin = new Date(Long.valueOf(decrypt).longValue());
            }
          }
        } catch (Throwable ignore) {

        }
      }
      Date passwordChanged = tuser.getPasswordChanged();
      if (passwordChanged != null) {
        if (lastPasswordLogin == null) {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          try {
            String start =
              JetspeedResources.getString(
                "aipo.passwordChanged.logout.start",
                "2016-10-11");
            lastPasswordLogin = sdf.parse(start);
          } catch (ParseException ignore) {
            // ignore
          }
        }
        if (lastPasswordLogin != null) {
          return lastPasswordLogin.after(passwordChanged);
        }
      }
    }
    return result;
  }

  protected boolean isICalRequest(RunData data) {
    String contextPath = ServletContextLocator.get().getContextPath();
    if ("/".equals(contextPath)) {
      contextPath = "";
    }
    HttpServletRequest hreq = data.getRequest();
    String requestURI = hreq.getRequestURI();
    return requestURI.equalsIgnoreCase(contextPath + "/ical/calendar.ics");
  }

  private void setOrgParametersForError(RunData data) {
    Context context =
      org.apache.turbine.services.velocity.TurbineVelocity.getContext(data);
    setOrgParameters(data, context);
    context.put("isError", "true");
  }

  private void setOrgParameters(RunData data, Context context) {
    ALOrgUtilsService.assignCommonContext(context);
  }

  private boolean checkDbError(RunData data) {
    String message = data.getMessage();
    if (null != message && message.indexOf(ALConstants.DB_ERROR_DETECT) != -1) {
      return true;
    }
    return false;
  }

  @Override
  public boolean requiresNewSession(RunData data) {
    return false;
  }
}
