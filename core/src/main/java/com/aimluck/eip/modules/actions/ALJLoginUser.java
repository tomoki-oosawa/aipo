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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.Cookie;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.AccountExpiredException;
import org.apache.jetspeed.services.security.CredentialExpiredException;
import org.apache.jetspeed.services.security.FailedLoginException;
import org.apache.jetspeed.services.security.LoginException;
import org.apache.jetspeed.services.security.nosecurity.FakeJetspeedUser;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipInformation;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALCellularUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;
import com.aimluck.eip.util.ALTimelineUtils;

/**
 * ログイン処理用のクラスです。 <br />
 *
 */
public class ALJLoginUser extends ActionEvent {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALJLoginUser.class.getName());

  public static String KEY_MYGROUP = "mygroup";

  @Override
  @SuppressWarnings("deprecation")
  public void doPerform(RunData rundata) throws Exception {
    JetspeedRunData data = (JetspeedRunData) rundata;

    String username = data.getParameters().getString("username", "");
    String password = data.getParameters().getString("password", "");

    // セッションハイジャック対策
    // 携帯の場合、URLからのログインはログイン画面にリダイレクト
    if (ALCellularUtils.isCellularPhone(data)
      && (username != null)
      && (password != null)
      && (data.getRequest().getMethod() == "GET")) {
      String externalLoginUrl =
        ALConfigService.get(Property.EXTERNAL_LOGIN_URL);
      if (!"".equals(externalLoginUrl)) { // ログイン画面へリダイレクト
        data.setRedirectURI(externalLoginUrl);
      }
      data.getResponse().sendRedirect(data.getRedirectURI());

    }
    // Cookieでセッションを管理していなければエラー画面を表示
    else if (!data.getRequest().isRequestedSessionIdFromCookie()) {
      data.setScreenTemplate("CookieError");
      return;
    }

    // 入力されたユーザ名を検証する．
    ALStringField tmpname = new ALStringField();
    tmpname.setTrim(true);
    tmpname.setNotNull(true);
    tmpname.setCharacterType(ALStringField.TYPE_ASCII);
    tmpname.limitMaxLength(16);
    tmpname.setValue(username);
    boolean valid = tmpname.validate(new ArrayList<String>());

    // 携帯の簡単ログインについては、後でusername, passwordを取得
    if (ALCellularUtils.isCellularPhone(data)
      && (data.getParameters().getString("key", "").trim() != null)) {
      valid = true;
    }

    int length = username.length();
    for (int i1 = 0; i1 < length; i1++) {
      if (isSymbol(username.charAt(i1))) {
        // 使用されているのが妥当な記号であるかの確認
        if (!(username.charAt(i1) == "_".charAt(0)
          || username.charAt(i1) == "-".charAt(0) || username.charAt(i1) == "."
          .charAt(0))) {
          valid = false;
          break;
        }
      }
    }
    if (!valid) {
      // username = "";
      data.setUser(JetspeedSecurity.getAnonymousUser());
      data.setMessage(ALLocalizationUtils.getl10n("LOGINACTION_NO_USERID_PW"));
      // data.setScreenTemplate(JetspeedResources.getString("logon.disabled.form"));
      data.getUser().setHasLoggedIn(Boolean.FALSE);

      return;
    }
    // ここから
    if (ALCellularUtils.isSmartPhone(data) && "admin".equals(username)) {
      data.setUser(JetspeedSecurity.getAnonymousUser());
      data.setMessage(ALLocalizationUtils.getl10n("LOGINACTION_LOGIN_ONLY_PC"));
      data.getUser().setHasLoggedIn(Boolean.FALSE);
      return;
    }

    if ("admin".equals(username)
      && "T".equals(ALConfigService.get(Property.FIRST_ADMIN_LOGIN))) {
      // アップデート時には自動投稿しない
      if (!ALTimelineUtils.hasTimelinePost()) {
        // ID=2 をガイドユーザーとする
        ALTimelineUtils.postTimeline(data, 2);
      }
      ALConfigService.put(Property.FIRST_ADMIN_LOGIN, "F");
    }

    boolean newUserApproval =
      JetspeedResources.getBoolean("newuser.approval.enable", false);
    String secretkey = data.getParameters().getString("secretkey", null);
    if (secretkey != null) {
      // its the first logon - we are verifying the secretkey

      // handle the buttons on the ConfirmRegistration page
      String button1 = data.getParameters().getString("submit1", null);
      if (button1 != null && button1.equalsIgnoreCase("Cancel")) {
        data.setScreenTemplate(TurbineTemplate.getDefaultScreen());
        return;
      }

      // check to make sure the user entered the right confirmation key
      // if not, then send them to the ConfirmRegistration screen
      JetspeedUser user = JetspeedSecurity.getUser(username);

      if (user == null) {
        logger.warn("JLogin User: Unexpected condition : user is NULL");
        return;
      }
      String confirm_value = user.getConfirmed();
      if (!secretkey.equals(confirm_value)
        && !confirm_value.equals(JetspeedResources.CONFIRM_VALUE)) {
        if (newUserApproval) {
          data.setMessage(Localization.getString(
            rundata,
            "JLOGINUSER_KEYNOTVALID"));
          // data.setScreenTemplate("NewUserAwaitingAcceptance");
          return;
        } else {
          if (user.getConfirmed().equals(
            JetspeedResources.CONFIRM_VALUE_REJECTED)) {
            data.setMessage(Localization.getString(
              rundata,
              "JLOGINUSER_KEYNOTVALID"));
            // data.setScreenTemplate("NewUserRejected");
            return;
          } else {
            data.setMessage(Localization.getString(
              rundata,
              "JLOGINUSER_KEYNOTVALID"));
            // data.setScreenTemplate("ConfirmRegistration");
            return;
          }
        }
      }
      user.setConfirmed(JetspeedResources.CONFIRM_VALUE);
      data.setMessage(Localization.getString(rundata, "JLOGINUSER_WELCOME"));
      JetspeedSecurity.saveUser(user);
    }

    JetspeedUser user = null;
    try {
      if (ALCellularUtils.isCellularPhone(data)) {
        String key = data.getParameters().getString("key", "").trim();
        if (key != null && key.length() > 0 && key.contains("_")) {
          username = key.substring(0, key.lastIndexOf("_"));
          String base64value = key.substring(key.lastIndexOf("_") + 1);
          ALEipUser eipuser = ALEipUtils.getALEipUser(username);
          if (eipuser != null) {
            if (!(ALCellularUtils.getCheckValueForCellLogin(username, eipuser
              .getUserId()
              .toString())).equals(base64value)) {
              username = "";
            }
          } else {
            username = "";
          }
        }
        String celluid = ALCellularUtils.getCellularUid(rundata);
        if (!"".equals(celluid)) {
          password = ALEipConstants.KEY_CELLULAR_UID + celluid;
        }
      }

      user = JetspeedSecurity.login(username, password);
      rundata.getSession().setAttribute(
        ALEipConstants.LAST_PASSWORD_LOGIN,
        new Date());

      JetspeedSecurity.saveUser(user);

      // 運営からのお知らせ用のクッキ−削除
      if (rundata.getRequest().getCookies() != null) {
        for (Cookie cookie : rundata.getRequest().getCookies()) {
          String cookieName = cookie.getName();
          if (cookieName.startsWith(ALEipInformation.INFORMATION_COOKIE_PREFIX)) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setValue("true");
            data.getResponse().addCookie(cookie);
          }
        }
      }

      int loginUserId = Integer.parseInt(user.getUserId());
      ALEventlogFactoryService.getInstance().getEventlogHandler().logLogin(
        loginUserId);

    } catch (LoginException e) {
      /*
       * data.setScreenTemplate(
       * JetspeedResources.getString(TurbineConstants.TEMPLATE_LOGIN));
       */
      String message = e.getMessage() != null ? e.getMessage() : e.toString();

      data.setMessage(message);
      data.setUser(JetspeedSecurity.getAnonymousUser());
      data.getUser().setHasLoggedIn(Boolean.FALSE);

      if (e instanceof FailedLoginException) {
        if (ALEipConstants.USER_STAT_DISABLED.equals(message)) {
          data.setMessage(Localization.getString(
            rundata,
            "JLOGINUSER_ACCOUNT_DISABLED"));
          return;
        } else if (ALEipConstants.USER_STAT_NUTRAL.equals(message)) {
          data.setMessage(ALLocalizationUtils
            .getl10n("LOGINACTION_INVALIDATION_USER"));
          return;
        }

        if (!disableCheck(data)) {
          logger.info("JLoginUser: Credential Failure on login for user: "
            + username);
          data.setMessage(Localization.getString(
            rundata,
            "PASSWORDFORM_FAILED_MSG"));
        }
      } else if (e instanceof AccountExpiredException) {
        logger.info("JLoginUser: Account Expired for user " + username);
      } else if (e instanceof CredentialExpiredException) {
        logger.info("JLoginUser: Credentials expired for user: " + username);
        /*
         * data.setScreenTemplate( JetspeedResources.getString(
         * JetspeedResources.CHANGE_PASSWORD_TEMPLATE, "ChangePassword"));
         */
        data.setMessage(Localization.getString(
          rundata,
          "PASSWORDFORM_EXPIRED_MSG"));
        data.getParameters().setString("username", username);
      }

      return;
    } catch (Throwable other) {
      // data.setScreenTemplate(
      // JetspeedResources.getString(TurbineConstants.TEMPLATE_ERROR));
      String message =
        other.getMessage() != null ? other.getMessage() : other.toString();
      data.setMessage(message);
      data.setStackTrace(
        org.apache.turbine.util.StringUtils.stackTrace(other),
        other);
      JetspeedUser juser =
        new FakeJetspeedUser(JetspeedSecurity.getAnonymousUserName(), false);
      data.setUser(juser);
      return;
    }

    // check for being confirmed before allowing someone to finish logging in
    if (data.getUser().hasLoggedIn()) {
      if (JetspeedSecurity.isDisableAccountCheckEnabled()) {
        // dst: this needs some refactoring. I don't believe this api is
        // necessary
        JetspeedSecurity.resetDisableAccountCheck(data
          .getParameters()
          .getString("username", ""));
      }

      String confirmed = data.getUser().getConfirmed();
      if (confirmed == null
        || !confirmed.equals(JetspeedResources.CONFIRM_VALUE)) {
        if (confirmed != null
          && confirmed.equals(JetspeedResources.CONFIRM_VALUE_REJECTED)) {
          data.setMessage(Localization.getString(
            rundata,
            "JLOGINUSER_KEYNOTVALID"));
          // data.setScreenTemplate("NewUserRejected");
          data.getUser().setHasLoggedIn(Boolean.FALSE);
          return;
        } else {

          data.setMessage(Localization.getString(
            rundata,
            "JLOGINUSER_CONFIRMFIRST"));

          // data.setScreenTemplate("ConfirmRegistration");

          data.getUser().setHasLoggedIn(Boolean.FALSE);
          return;
        }
      }

      // user has logged in successfully at this point

      boolean automaticLogonEnabled =
        JetspeedResources.getBoolean("automatic.logon.enable", false);
      if (automaticLogonEnabled) {
        // Does the user want to use this facility?
        boolean userRequestsRememberMe =
          data.getParameters().getBoolean("rememberme", false);
        if (userRequestsRememberMe) {
          // save cookies on the users machine.
          int maxage =
            JetspeedResources.getInt("automatic.logon.cookie.maxage", -1);
          String comment =
            JetspeedResources.getString("automatic.logon.cookie.comment", "");
          String domain =
            JetspeedResources.getString("automatic.logon.cookie.domain");
          String path =
            JetspeedResources.getString("automatic.logon.cookie.path", "/");

          if (domain == null || "".equals(domain)) {
            String server = data.getServerName();
            domain = "." + server;
          }

          String loginCookieValue = null;

          if (JetspeedResources.getString(
            "automatic.logon.cookie.generation",
            "everylogon").equals("everylogon")) {
            loginCookieValue = "" + Math.random();
            data.getUser().setPerm("logincookie", loginCookieValue);
            JetspeedSecurity.saveUser(data.getJetspeedUser());
          } else {
            loginCookieValue = (String) data.getUser().getPerm("logincookie");
            if (loginCookieValue == null || loginCookieValue.length() == 0) {
              loginCookieValue = "" + Math.random();
              data.getUser().setPerm("logincookie", loginCookieValue);
              JetspeedSecurity.saveUser(data.getJetspeedUser());
            }
          }

          Cookie userName =
            new Cookie("username", data.getUser().getUserName());
          Cookie loginCookie = new Cookie("logincookie", loginCookieValue);

          userName.setMaxAge(maxage);
          userName.setComment(comment);
          // userName.setDomain(domain);
          userName.setPath(path);

          loginCookie.setMaxAge(maxage);
          loginCookie.setComment(comment);
          // loginCookie.setDomain(domain);
          loginCookie.setPath(path);

          data.getResponse().addCookie(userName);
          data.getResponse().addCookie(loginCookie);

          String lastloginValue =
            ALCommonUtils.encodeBase64(ALCommonUtils.encrypt(JetspeedResources
              .getString("aipo.cookie.encryptKey", "secureKey"), new Date()
              .getTime()
              + ""));
          if (lastloginValue != null) {
            Cookie lastlogin = new Cookie("lastlogin", lastloginValue);
            lastlogin.setMaxAge(maxage);
            lastlogin.setComment(comment);
            // lastlogin.setDomain(domain);
            lastlogin.setPath(path);
            data.getResponse().addCookie(lastlogin);
          }

        }
      }

      JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);

      String redirectUrl = data.getParameters().getString("redirect", "");

      if (redirectUrl != null && !"".equals(redirectUrl)) {
        data.setRedirectURI(redirectUrl);
        data.getResponse().sendRedirect(redirectUrl);
        JetspeedLinkFactory.putInstance(jsLink);
        jsLink = null;
        return;
      }

      if (ALCellularUtils.isCellularPhone(data)) {
        rundata.setRedirectURI(jsLink.getPortletById("").addQueryData(
          JetspeedResources.PATH_ACTION_KEY,
          "controls.Restore").toString());
        rundata.getResponse().sendRedirect(rundata.getRedirectURI());
        JetspeedLinkFactory.putInstance(jsLink);
        jsLink = null;
        return;
      }

      String client = ALEipUtils.getClient(rundata);
      String peid = data.getParameters().getString("js_peid");
      if (peid == null) {
        peid = (String) data.getUser().getTemp("js_peid");
      }
      if (peid == null && "IPHONE".equals(client)) {
        String firstPortletId =
          ALEipUtils.getFirstPortletId(data.getUser().getUserName());
        String url =
          jsLink.getPortletById(firstPortletId).addQueryData(
            "action",
            "controls.Maximize").toString();
        data.setRedirectURI(url);
        data.getResponse().sendRedirect(url);
        JetspeedLinkFactory.putInstance(jsLink);
        jsLink = null;
        return;
      }

    } else {
      disableCheck(data);
    }
  }

  /**
   *
   * 指定したchar型文字が記号であるかを判断します。
   *
   * @param ch
   * @return
   */
  protected boolean isSymbol(char ch) {
    byte[] chars;

    try {
      chars = (Character.valueOf(ch).toString()).getBytes("shift_jis");
    } catch (UnsupportedEncodingException ex) {
      return false;
    }

    if (chars == null
      || chars.length == 2
      || Character.isDigit(ch)
      || Character.isLetter(ch)) {
      return false;
    } else {
      return true;
    }

  }

  @SuppressWarnings("deprecation")
  private boolean disableCheck(JetspeedRunData data) {
    boolean disabled = false;
    // disable user after a configurable number of strikes
    if (JetspeedSecurity.isDisableAccountCheckEnabled()) {
      disabled =
        JetspeedSecurity.checkDisableAccount(data.getParameters().getString(
          "username",
          ""));

      if (disabled) {
        data.setMessage(Localization.getString(
          data,
          "JLOGINUSER_ACCOUNT_DISABLED"));
        data.setScreenTemplate(JetspeedResources
          .getString("logon.disabled.form"));
        data.getUser().setHasLoggedIn(Boolean.FALSE);
      }
    }
    return disabled;
  }
}
