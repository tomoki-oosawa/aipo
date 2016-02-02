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

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.ActionEvent;
import org.apache.turbine.util.RunData;

import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.http.ServletContextLocator;
import com.aimluck.eip.services.config.ALConfigHandler.Property;
import com.aimluck.eip.services.config.ALConfigService;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * ログアウト処理用のクラスです。 <br />
 *
 */
public class ALJLogoutUser extends ActionEvent {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALJLogoutUser.class.getName());

  @Override
  public void doPerform(RunData data) throws Exception {

    String username = data.getParameters().getString("user");

    if (JetspeedResources.getBoolean("automatic.logon.enable", false)) {
      Cookie userName = new Cookie("username", "");
      Cookie loginCookie = new Cookie("logincookie", "");

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

      userName.setMaxAge(0);
      userName.setComment(comment);
      // userName.setDomain(domain);
      userName.setPath(path);

      loginCookie.setMaxAge(0);
      loginCookie.setComment(comment);
      // loginCookie.setDomain(domain);
      loginCookie.setPath(path);

      data.getResponse().addCookie(userName);
      data.getResponse().addCookie(loginCookie);

      if (data.getRequest().getCookies() != null) {
        data.getCookies().remove("logincookie");
        data.getCookies().remove("username");
      }
    }

    if (JetspeedResources.getBoolean("automatic.logout.save", false)) {
      JetspeedSecurity.saveUser((JetspeedUser) data.getUserFromSession());
    }

    JetspeedSecurity.logout();

    if (username != null && !"".equals(username)) {
      ALEipUser logoutuser = ALEipUtils.getALEipUser(username);
      if (logoutuser != null) {
        int logoutUserId = (int) logoutuser.getUserId().getValue();
        ALEventlogFactoryService.getInstance().getEventlogHandler().logLogout(
          logoutUserId);
      }
    }

    data.setMessage(JetspeedResources
      .getString(TurbineConstants.LOGOUT_MESSAGE));

    JetspeedLink jsLink = null;

    data.setScreen(JetspeedResources
      .getString(TurbineConstants.SCREEN_HOMEPAGE));

    try {
      jsLink = JetspeedLinkFactory.getInstance(data);
    } catch (Exception e) {
      logger.error("Error getting jsLink", e);
    }

    String externalLoginUrl = ALConfigService.get(Property.EXTERNAL_LOGIN_URL);
    if ("".equals(externalLoginUrl)) {
      // 　ガラケーのログアウト時はifの中を通っている。
      if (ALEipUtils.isCellularPhone(data)) {

        // contextPathの取得
        ServletContext servletContext = ServletContextLocator.get();
        String contextPath = servletContext.getContextPath(); // "" "/"
        // contextPath = "" なら contextPath = "/"にする。
        if ("".equals(contextPath)) {
          contextPath = "/";
        }

        // contextPathをsetする。
        data.setRedirectURI(contextPath.concat("?logout=T"));
      } else {
        // contextPathの取得
        ServletContext servletContext = ServletContextLocator.get();
        String contextPath = servletContext.getContextPath(); // "" "/"
        // contextPath = "" なら contextPath = "/"にする。
        if ("".equals(contextPath)) {
          contextPath = "/";
        }
        // contextPathをsetする。
        data.setRedirectURI(contextPath);
      }
    } else {
      data.setRedirectURI(externalLoginUrl);
    }

    JetspeedLinkFactory.putInstance(jsLink);
    jsLink = null;
    // セッションの削除
    if (data.getSession() != null) {
      try {
        data.getSession().invalidate();
      } catch (IllegalStateException ex) {
        logger.debug(ALLocalizationUtils
          .getl10n("LOGOUTACTION_ALREADY_SESSION_DELETE"));
      }
    }
  }
}
