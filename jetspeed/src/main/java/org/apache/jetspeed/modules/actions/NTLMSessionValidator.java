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

// JDK stuff
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.JetspeedSecurityCache;
import org.apache.jetspeed.services.security.LoginException;
import org.apache.jetspeed.services.security.UnknownUserException;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;

/**
 * Just like org.apache.turbine.modules.actions.sessionvalidator.
 * TemplateSessionValidator except:
 * <ul>
 * <li>it doesn't check the session_access_counter
 * <li>it automatically logs the user in based on currently authenticated nt
 * user
 * <li>expects a JetspeedRunData object and put there the additionnal jetspeed
 * properties
 * </ul>
 * <B>Usage of this session validator is limited to Windows NT/2000 and MS
 * Internet Explorer.</B>
 * <P>
 * To activate this session validator, set <CODE>action.sessionvalidator</CODE>
 * in tr.props to <code>NTLMSessionValidator</code>.
 * </P>
 * <P>
 * When this session validator is active, the following algorithm is used to
 * display appropriate psml:
 * 
 * <pre>
 * Check authentication status
 * If user is not authenticated to machine running the portal
 *      Pop the standard login box
 *      If user passes authentication
 *           Attempt to retrieve matching user profile
 *           If matching profile found
 *                Render the profile
 *           Else
 *                Retrieve and render anonymous profile
 *           End
 *      Else If authentication fails
 *           Keep prompting for login information
 *      Else If the user cancels the login box
 *           Retrieve and render anonymous profile
 *      End
 * Else
 *      Attempt to retrieve matching user profile
 *      If matching profile found
 *           Render the profile
 *      Else
 *           Retrieve and render anonymous profile
 *      End
 * End
 * </pre>
 * 
 * </P>
 * <P>
 * Optionally, certain characters may be removed from the username before it's
 * passed to the JetspeedSecurity. These characters may be specified by setting
 * <CODE>NTLMSessionValidator.chars.to.remove</CODE> property. For example, if
 * invalid characters list is '#@$', username '#user1' will be returned as
 * 'user1'.
 * </P>
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: NTLMSessionValidator.java,v 1.6 2004/02/23 02:59:06 jford Exp $
 * @see org.apache.turbine.modules.actions.sessionvalidator.TemplateSessionValidator
 * @see http://www.innovation.ch/java/ntlm.html
 * @since 1.4b5
 */
public class NTLMSessionValidator extends TemplateSessionValidator {

  private static final String INVALID_CHARS_KEY = "NTLMSessionValidator.chars.to.remove";

  private final String invalidChars = org.apache.jetspeed.services.resources.JetspeedResources
    .getString(INVALID_CHARS_KEY, null);

  private static final byte z = 0;

  private static final byte[] msg1 = { (byte) 'N', (byte) 'T', (byte) 'L',
    (byte) 'M', (byte) 'S', (byte) 'S', (byte) 'P', z, (byte) 2, z, z, z, z, z,
    z, z, (byte) 40, z, z, z, (byte) 1, (byte) 130, z, z, z, (byte) 2,
    (byte) 2, (byte) 2, z, z, z, z, z, z, z, z, z, z, z, z };

  private static final String encodedMsg1 = "NTLM "
    +  new String(Base64.encodeBase64(msg1));

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(NTLMSessionValidator.class.getName());

  /**
   * Execute the action.
   * 
   * @param data
   *          Turbine information.
   * @exception Exception
   *              , a generic exception.
   */
  @Override
  public void doPerform(RunData data) throws Exception {
    // first, invoke our superclass action to make sure
    // we follow Turbine evolutions
    // FIXME: if the user is not found (this can happen, for instance,
    // if the anonymous user is not in the DB), it throws a terrible exception
    // in the user's face
    super.doPerform(data);

    JetspeedUser user = (JetspeedUser) data.getUser();

    // get remote user from ntlm
    String userName = this.getRemoteUser(data);

    if ((user == null || !user.hasLoggedIn())) {
      if (userName != null && userName.length() > 0) {
        byte[] temp = userName.getBytes();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < temp.length; i++) {
          if (temp[i] != 0) {
            if (invalidChars == null || invalidChars.indexOf(temp[i]) < 0) {
              buffer.append((char) temp[i]);
            }
          }
        }
        userName = buffer.toString();
        try {
          user = JetspeedSecurity.getUser(userName);
          data.setUser(user);
          user.setHasLoggedIn(new Boolean(true));
          user.updateLastLogin();
          data.save();
          if (JetspeedSecurityCache.getAcl(userName) == null) {
            JetspeedSecurityCache.load(userName);
          }
          logger.info("NTLMSessionValidator: automatic login using ["
            + userName + "]");
        } catch (LoginException noSuchUser) {
          // user not found - ignore it - they will not be logged in
          // automatically
        } catch (UnknownUserException unknownUser) {
          // user not found - ignore it - they will not be logged in
          // automatically
          if (logger.isWarnEnabled()) {
            logger.warn("NTLMSessionValidator: username [" + userName
              + "] does not exist or authentication failed, "
              + "redirecting to anon profile");
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
      logger
        .error("The RunData object does not implement the expected interface, "
          + "please verify the RunData factory settings");
      return;
    }
    String language = data.getRequest().getParameter("js_language");

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
   * This session validator does not require a new session for each request
   * 
   * @param data
   * @return
   */
  @Override
  public boolean requiresNewSession(RunData data) {
    return false;
  }

  /**
   * Extracts user name from http headers
   * 
   * @param data
   * @return
   * @exception Exception
   */
  private String getRemoteUser(RunData data) throws Exception {
    HttpServletRequest request = data.getRequest();
    HttpServletResponse response = data.getResponse();

    if (data.getUser().hasLoggedIn()
      && request.getMethod().equalsIgnoreCase("get")) {
      return data.getUser().getUserName();
    }

    String auth = request.getHeader("Authorization");
    if (auth == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setHeader("WWW-Authenticate", "NTLM");
      response.flushBuffer();

      return null;
    }
    if (auth.startsWith("NTLM ")) {

      byte[] msg = Base64.decodeBase64(auth.substring(5).getBytes());
      int off = 0, length, offset;

      if (msg[8] == 1) {
        response.setHeader("WWW-Authenticate", encodedMsg1);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        return null;
      } else if (msg[8] == 3) {
        if (data.getUser().hasLoggedIn()) {
          return data.getUser().getUserName();
        }

        off = 30;

        // length = msg[off + 17] * 256 + msg[off + 16];
        // offset = msg[off + 19] * 256 + msg[off + 18];
        // String remoteHost = new String(msg, offset, length);

        // length = msg[off + 1] * 256 + msg[off];
        // offset = msg[off + 3] * 256 + msg[off + 2];
        // String domain = new String(msg, offset, length);

        length = msg[off + 9] * 256 + msg[off + 8];
        offset = msg[off + 11] * 256 + msg[off + 10];
        String username = new String(msg, offset, length);

        return username;
      }

    }

    return null;
  }
}
