/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2015 Aimluck,Inc.
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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.base.BaseParameter;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.jetspeed.util.template.JetspeedTool;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.gadgets.ALUserPref;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * カスタマイズの一覧を処理するクラスです。
 * 
 */
public class PortletCustomizeFormScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(PortletCustomizeFormScreen.class.getName());

  /**
   * 
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {
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
        logger.warn("[Customize]", e);
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

      PortletInstance instance = PersistenceManager.getInstance(found, rundata);
      context.put("portlet_instance", PersistenceManager.getInstance(
        found,
        rundata));

      // retrieve the portlet parameters
      PortletEntry entry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, found.getName());
      // save the entry in the session
      List<Parameter> params = new ArrayList<Parameter>();
      Iterator<?> i = entry.getParameterNames();
      while (i.hasNext()) {
        String name = (String) i.next();
        Parameter param = entry.getParameter(name);
        // filter some "system" and hidden parameters
        if ((!param.isHidden()) && (name.charAt(0) != '_')) {
          // check the user role
          if (JetspeedSecurity.checkPermission(
            (JetspeedUser) rundata.getUser(),
            new PortalResource(entry, param),
            JetspeedSecurity.PERMISSION_CUSTOMIZE)) {
            // Implementation of clone() is missing so we have do it "by hand"
            Parameter clone = new BaseParameter();
            clone.setName(param.getName());
            clone.setTitle(param.getTitle());
            clone.setDescription(param.getDescription());
            clone.setType(param.getType());
            String value;
            if (instance.getAttribute(name, null) != null) {
              value = instance.getAttribute(name);
            } else if (found.getPortletConfig().getInitParameter(name) != null) {
              value = found.getPortletConfig().getInitParameter(name);
            } else {
              value = param.getValue();
            }
            clone.setValue(ALStringUtil.sanitizing(value));
            params.add(clone);
          }
        }
      }

      Collections.sort(params, new Comparator<Parameter>() {
        @Override
        public int compare(Parameter a, Parameter b) {
          Parameter p1 = a;
          Parameter p2 = b;
          return (p1.getName()).compareTo(p2.getName());
        }
      });

      // retrieve the app parameters from xml
      PortletConfig pc = found.getPortletConfig();
      if ("GadgetsTemplate".equals(pc.getName())) {
        String appId = pc.getInitParameter("aid");
        ALApplication app =
          ALApplicationService.get(new ALApplicationGetRequest().withAppId(
            appId).withIsFetchXml(true));
        List<List<Map.Entry<String, String>>> enumsList =
          new ArrayList<List<Map.Entry<String, String>>>();
        List<ALUserPref> userPrefs = app.getUserPrefs();
        Collections.sort(userPrefs, new Comparator<ALUserPref>() {
          @Override
          public int compare(ALUserPref a, ALUserPref b) {
            ALUserPref p1 = a;
            ALUserPref p2 = b;
            return (p1.getName()).compareTo(p2.getName());

          }
        });
        for (ALUserPref userPref : userPrefs) {
          String name = "pref-" + userPref.getName();
          Parameter clone = new BaseParameter();
          clone.setName(name);
          clone.setTitle(userPref.getDisplayName());
          clone.setDescription(null);
          boolean hidden = false;
          boolean list = false;
          switch (userPref.getType()) {
            case ENUM:
              List<Map.Entry<String, String>> enums = userPref.getEnums();
              enumsList.add(enums);
              clone.setType("enum");
              break;
            case LIST:
              clone.setType("list");
              list = true;
              break;
            case BOOL:
              clone.setType("boolean");
              break;
            case HIDDEN:
              clone.setType("hidden");
              hidden = true;
              break;
            default:
              clone.setType(null);
          }
          String value;
          if (instance.getAttribute(name, null) != null) {
            value = instance.getAttribute(name);
          } else if (found.getPortletConfig().getInitParameter(name) != null) {
            value = found.getPortletConfig().getInitParameter(name);
          } else {
            value = userPref.getDefault();
            if (list) {
              value = value.replace("|", ",");
            }
          }
          clone.setValue(ALStringUtil.sanitizing(value));
          if (!hidden) {
            params.add(clone);
          }
        }
        context.put("enums", enumsList);
      }

      // get the customization state for this page
      SessionState customizationState =
        ((JetspeedRunData) rundata).getPageSessionState();
      customizationState.setAttribute("customize-parameters", params);

      // populate the customizer context
      context.put("data", rundata);
      context.put("parameters", params);
      context.put("portlet", found);
      context.put("jetspeed", new JetspeedTool(rundata));
      context.put("l10n", ALLocalizationUtils.createLocalization(rundata));
      context.put("utils", new ALCommonUtils());
      context.put("js_peid", peid);

      String currentTitle =
        profile.getDocument().getEntryById(found.getID()).getTitle();
      if (currentTitle == null
        && found.getPortletConfig().getMetainfo() != null) {
        currentTitle = found.getPortletConfig().getMetainfo().getTitle();
      }
      context.put("current_title", new ALStringField(currentTitle));

      String layout_template = "portlets/html/customizer-portlet.vm";
      setTemplate(rundata, context, layout_template);
    }

    // 理由 ：レイアウト設定のカスタマイズ画面を表示するときに，
    // 最大化画面の情報がセッションに残っていた．
    // 対策 ： セッション内の js_peid を削除する．
    jdata.getUser().removeTemp("js_peid");
  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return "Customize";
  }

}
