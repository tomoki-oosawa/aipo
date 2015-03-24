/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.modules.actions;

import java.util.Locale;

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

/**
 * Just like
 * org.apache.turbine.modules.actions.sessionvalidator.TemplateSessionValidator
 * except:
 * <ul>
 * <li>it doesn't check the session_access_counter
 * <li>it doesn't require you to always logon
 * <li>expects a JetspeedRunData object and put there the additionnal jetspeed
 * properties
 * </ul>
 * 
 * @see org.apache.turbine.modules.actions.sessionvalidator.TemplateSessionValidator
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster </a>
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala </a>
 */
public class JetspeedSessionValidator extends TemplateSessionValidator {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
      .getLogger(JetspeedSessionValidator.class.getName());

  /**
   * Execute the action.
   * 
   * @param data
   *            Turbine information.
   * @exception Exception,
   *                a generic exception.
   */
  public void doPerform(RunData data) throws Exception {
    // first, invoke our superclass action to make sure
    // we follow Turbine evolutions
    // FIXME: if the user is not found (this can happen, for instance,
    // if the anonymous user is not in the DB), it throws a terrible exception
    // in the user's face
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

    JetspeedUser user = (JetspeedUser) data.getUser();

    // if the user is not logged in and auto-login is enable - try and do it.
    if ((user == null || !user.hasLoggedIn())
        && JetspeedResources.getBoolean("automatic.logon.enable", false)) {
      // need to make sure there are cookies - turbine does not handle this
      // currently
      if (data.getRequest().getCookies() != null) {
        // check for user in cookie
        String userName = data.getCookies().getString("username", "");
        String loginCookieValue = data.getCookies()
            .getString("logincookie", "");

        if (userName.length() > 0 && loginCookieValue.length() > 0) {
          try {
            user = JetspeedSecurity.getUser(userName);
            if (user.getPerm("logincookie", "").equals(loginCookieValue)) {
              // cookie is present and correct - log the user in
              data.setUser(user);
              user.setHasLoggedIn(Boolean.TRUE);
              user.updateLastLogin();
              data.save();
            }
          } catch (LoginException noSuchUser) {
            // user not found - ignore it - they will not be logged in
            // automatically
          } catch (org.apache.jetspeed.services.security.UnknownUserException unknownUser) {
            // user not found - ignore it - they will not be logged in
            // automatically
            logger.warn("Username from the cookie was not found: " + userName);
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
              + "please verify the RunData factory settings", e);
      return;
    }
    String language = (String) data.getRequest().getParameter("js_language");

    if (null != language) {
      user.setPerm("language", language);
    }

    // Get the locale store it in the user object
    CustomLocalizationService locService = (CustomLocalizationService) ServiceUtil
        .getServiceByName(LocalizationService.SERVICE_NAME);
    Locale locale = locService.getLocale(data);

    if (locale == null) {
      locale = new Locale(TurbineResources.getString("locale.default.language",
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

  /**
   */
  public boolean requiresNewSession(RunData data) {
    return false;
  }

}
