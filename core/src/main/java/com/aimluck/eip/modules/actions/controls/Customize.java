/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

package com.aimluck.eip.modules.actions.controls;

// Turbine stuff
import java.util.Enumeration;
import java.util.Stack;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.modules.Action;
import org.apache.turbine.util.RunData;

/**
 * Handle Customization requests for the current portal page
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 */
public class Customize extends Action {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(Customize.class.getName());

  /** アクセス権限の有無 */
  protected boolean hasAuthority;

  /**
   * @param rundata
   *          The RunData object for the current request
   */
  @SuppressWarnings("deprecation")
  @Override
  public void doPerform(RunData rundata) throws Exception {
    JetspeedRunData jdata = (JetspeedRunData) rundata;

    // 修正 ：タブのカスタマイズボタンが押されたときに，
    // 常にレイアウト画面を表示するように変更した．
    jdata.cleanupFromCustomization();

    if (jdata.getUser() == null) {
      return;
    }

    if (jdata.getProfile() == null) {
      return;
    }

    // read some parameters
    String editMediaType = jdata.getParameters().getString("mtype");
    String resetStack = jdata.getParameters().getString("reset");
    String peid = jdata.getParameters().getString("js_peid");

    // get the customization state for this page
    // SessionState customizationState = jdata.getPageSessionState();

    // this will be the profile we are editing
    Profile profile = null;

    // the "reset" parameter's presence signals the start of customization
    if ((resetStack != null)
      && ((resetStack.equalsIgnoreCase("on")) || (resetStack
        .equalsIgnoreCase("1")))) {
      // clear out any prior customization state
      jdata.cleanupFromCustomization();
    }

    // if we have not yet setup for customization, do so now
    if (jdata.getCustomizedProfile() == null) {
      ProfileLocator locator = (ProfileLocator) jdata.getProfile().clone();

      if (editMediaType != null) {
        locator.setMediaType(editMediaType);
      }

      // get a profile to edit
      profile = (Profile) Profiler.getProfile(locator).clone();
      jdata.setCustomizedProfile(profile);
    }

    // we are continuing an on-going customization
    else {
      // get the profile we are working on
      profile = jdata.getCustomizedProfile();
    }

    // Get js_peid parmameter.
    // If it does not exist, we will customize the root of the profile
    if (peid == null) {
      // use the id of the root set of the profile
      peid = profile.getRootSet().getID();
      jdata.setJs_peid(peid);
    }

    // find the portlet within the profile with this peid %%% isn't there a
    // better way to do this? -ggolden
    Portlet found = null;
    Stack<Portlet> sets = new Stack<Portlet>();
    sets.push(profile.getRootSet());

    while ((found == null) && (sets.size() > 0)) {
      PortletSet set = (PortletSet) sets.pop();

      if (set.getID().equals(peid)) {
        found = set;
      } else {
        Enumeration<?> en = set.getPortlets();
        while ((found == null) && en.hasMoreElements()) {
          Portlet p = (Portlet) en.nextElement();

          // unstack the controls to find the real PortletSets
          Portlet real = p;
          while (real instanceof PortletControl) {
            real = ((PortletControl) p).getPortlet();
          }

          if (real instanceof PortletSet) {
            if (real.getID().equals(peid)) {
              found = real;
            } else {
              // we'll explore this set afterwards
              sets.push(real);
            }
          } else if (p.getID().equals(peid)) {
            found = p;
          }
        }
      }
    }

    if (found != null) {
      PortalResource portalResource = new PortalResource(found);
      try {
        JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
        portalResource.setOwner(jsLink.getUserName());
        JetspeedLinkFactory.putInstance(jsLink);
      } catch (Exception e) {
        logger.warn(e.toString());
        portalResource.setOwner(null);
      }

      if (!JetspeedSecurity.checkPermission(
        (JetspeedUser) jdata.getUser(),
        portalResource,
        JetspeedSecurity.PERMISSION_CUSTOMIZE)) {
        logger.warn("User "
          + jdata.getUser().getUserName()
          + " has no customize permission for portlet with id "
          + peid);
        jdata
          .setMessage("Sorry, you have no customize permission for this portlet");
        return;
      }
      jdata.setCustomized(found);
      jdata.setScreenTemplate("Customize");
    }

    // 理由 ：レイアウト設定のカスタマイズ画面を表示するときに，
    // 最大化画面の情報がセッションに残っていた．
    // 対策 ： セッション内の js_peid を削除する．
    jdata.getUser().removeTemp("js_peid");
  }

  /**
   * Save the current customization Used by any other customizer to get this
   * done right!
   * 
   * @deprecated. The customizers should handle the save themselves
   */
  public static void save(RunData data) {
    try {
      Profile profile = ((JetspeedRunData) data).getCustomizedProfile();
      profile.store();
    } catch (Exception e) {
      logger.error("Error while saving profile", e);
    }

  } // save

  /**
   * Exit the customizer.
   * 
   * @deprecated. Exec the controls.EndCustomize action instead
   */
  public static void exit(RunData data) {
    JetspeedLink jsLink = null;
    ((JetspeedRunData) data).cleanupFromCustomization();

    // bring logged on user to homepage via HTTP redirect
    try {
      jsLink = JetspeedLinkFactory.getInstance(data);
      String mtype = data.getParameters().getString("mtype");
      if (mtype != null) {
        jsLink.setMediaType(mtype);
        jsLink.addQueryData("mtype", mtype);
      }

    } catch (Exception e) {
      logger.error("exit error", e);
    }
    data.setRedirectURI(jsLink.toString());
    JetspeedLinkFactory.putInstance(jsLink);
    jsLink = null;

  } // exit
}
