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
package com.aimluck.eip.modules.actions.portlets;

// Jetspeed Stuff
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction;
import org.apache.jetspeed.om.BaseSecurityReference;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.om.profile.psml.PsmlSkin;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.base.BaseParameter;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.portal.PortletSkin;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.util.MetaData;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALStringUtil;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.gadgets.ALUserPref;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.util.ALCommonUtils;

/**
 * This action implements the default portlet behavior customizer
 * 
 * <p>
 * Don't call it from the URL, the Portlet and the Action are automatically
 * associated through the registry PortletName
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 */
public class ALCustomizeAction extends VelocityPortletAction {

  public static final String PARAM_NAMESPACE = "_param_";

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALCustomizeAction.class.getName());

  /**
   * Subclasses must override this method to provide default behavior for the
   * portlet action
   * 
   * <table>
   * <tr>
   * <th>Context</th>
   * <th>Description</th>
   * </tr>
   * <!-- --------- ------------------------- -->
   * <tr>
   * <td>action</td>
   * <td>Action to use</td>
   * </tr>
   * <tr>
   * <td>current_skin</td>
   * <td>Current skin for this portlet INSTANCE</td>
   * </tr>
   * <tr>
   * <td>params</td>
   * <td>List of configurable parameters from the REGISTRY entry.</td>
   * </tr>
   * <tr>
   * <td>portlet</td>
   * <td>Portlet, not the Portlet Instance!</td>
   * </tr>
   * <tr>
   * <td>skins</td>
   * <td>List of skins</td>
   * </tr>
   * <tr>
   * <td>security</td>
   * <td>List of security ref</td>
   * </tr>
   * <tr>
   * <td>security_ref</td>
   * <td>Current securityRef for this portlet INSTANCE</td>
   * </tr>
   * </table>
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) {

    // generic context stuff
    context
      .put("skins", ALCustomizeSetAction.buildList(rundata, Registry.SKIN));
    context.put("securitys", ALCustomizeSetAction.buildList(
      rundata,
      Registry.SECURITY));

    // we should first retrieve the portlet to customize
    Portlet p = ((JetspeedRunData) rundata).getCustomized();

    context.put("action", "portlets.ALCustomizeAction");

    PortletInstance instance = PersistenceManager.getInstance(p, rundata);
    context.put("portlet_instance", PersistenceManager.getInstance(p, rundata));

    if (p == null) {
      return;
    }

    // retrieve the portlet parameters
    PortletEntry entry =
      (PortletEntry) Registry.getEntry(Registry.PORTLET, p.getName());
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
          } else if (p.getPortletConfig().getInitParameter(name) != null) {
            value = p.getPortletConfig().getInitParameter(name);
          } else {
            value = param.getValue();
          }
          clone.setValue(ALStringUtil.sanitizing(value));
          params.add(clone);
        }
      }
    }

    PortletConfig pc = p.getPortletConfig();
    if ("GadgetsTemplate".equals(pc.getName())) {
      String appId = pc.getInitParameter("aid");
      ALApplication app =
        ALApplicationService.get(new ALApplicationGetRequest()
          .withAppId(appId)
          .withIsFetchXml(true));
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
        } else if (p.getPortletConfig().getInitParameter(name) != null) {
          value = p.getPortletConfig().getInitParameter(name);
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
    } else {
      // 理由等 ：XREG からパラメータを読み込む際に順不同になっていた
      // 対処方法：パラメータ名でソートするように変更した。
      Collections.sort(params, new Comparator<Parameter>() {
        @Override
        public int compare(Parameter a, Parameter b) {
          Parameter p1 = a;
          Parameter p2 = b;
          return (p1.getName()).compareTo(p2.getName());

        }
      });
    }

    // get the customization state for this page
    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();
    customizationState.setAttribute("customize-parameters", params);

    // populate the customizer context
    context.put("parameters", params);
    context.put("portlet", p);
    context.put("customizer", portlet);
    // 2007.04.04 update
    context.put("utils", new ALCommonUtils());

    if (p.getPortletConfig().getSecurityRef() != null) {
      context.put("security_ref", p
        .getPortletConfig()
        .getSecurityRef()
        .getParent());
    }
    if (p.getPortletConfig().getSkin() != null) {
      context.put("current_skin", p
        .getPortletConfig()
        .getPortletSkin()
        .getName());
    }

    Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
    String currentTitle =
      profile.getDocument().getEntryById(p.getID()).getTitle();
    if (currentTitle == null && p.getPortletConfig().getMetainfo() != null) {
      currentTitle = p.getPortletConfig().getMetainfo().getTitle();
    }

    context.put("current_title", new ALStringField(currentTitle));

  }

  /** Clean up the customization state */
  public void doCancel(RunData rundata, Context context) {
    ((JetspeedRunData) rundata).setCustomized(null);
    if (((JetspeedRunData) rundata).getCustomized() == null) {
      try {
        ActionLoader.getInstance().exec(rundata, "controls.EndCustomize");
      } catch (Exception e) {
        logger.error("Unable to load action controls.EndCustomize ", e);
      }
    }
  }

  /**
   * Resets the portlet settings to default
   * 
   * @param rundata
   * @param context
   */
  public void doDefault(RunData rundata, Context context) {
    // we should first retrieve the portlet to customize and its parameters
    // definition
    Portlet p = ((JetspeedRunData) rundata).getCustomized();

    // Update paramaters
    try {
      // 理由等 ：デフォルトに戻す際に、ポートレット名も元に戻す
      // 対処方法：XREGからタイトルを取り出し、値を設定
      Portlet portlet = ((JetspeedRunData) rundata).getCustomized();
      String defTitle = portlet.getPortletConfig().getMetainfo().getTitle();

      Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
      Entry entry = profile.getDocument().getEntryById(p.getID());
      PortletConfig pc = p.getPortletConfig();
      MetaData md = pc.getMetainfo();
      if (md == null) {
        md = new MetaData();
        pc.setMetainfo(md);
      }

      boolean isGadgets = false;
      String appId = null;
      String url = null;
      String moduleId = null;
      if ("GadgetsTemplate".equals(pc.getName())) {
        appId = pc.getInitParameter("aid");
        url = pc.getInitParameter("url");
        moduleId = pc.getInitParameter("mid");
        ALApplication app =
          ALApplicationService.get(new ALApplicationGetRequest()
            .withAppId(appId));
        isGadgets = true;
        ALStringField title = app.getTitle();
        defTitle = title.getValue();
      }

      md.setTitle(defTitle);
      entry.setTitle(defTitle);

      PortletInstance instance = PersistenceManager.getInstance(p, rundata);
      instance.removeAllAttributes();

      if (isGadgets) {
        instance.setAttribute("aid", appId);
        instance.setAttribute("url", url);
        instance.setAttribute("mid", moduleId);
      }
      // add by Haruo Kaneko
      profile.store();
      p.init();

      // FIXME: this hack is due to the corrupted lifecycle of the portlet in
      // the
      // current API when caching is activated
      try {
        org.apache.jetspeed.util.PortletSessionState.setPortletConfigChanged(
          p,
          rundata);
        p.init();
      } catch (PortletException e) {
        logger.error("Customizer failed to reinitialize the portlet "
          + p.getName(), e);
      }

      // we're done, make sure clean up the
      // session
      doCancel(rundata, context);
    } catch (Exception e) {
      logger.error("ALCustomizeAction.doDefault", e);
    }
  }

  /** Updates the customized portlet entry */
  public void doUpdate(RunData rundata, Context context) {
    // get the customization state for this page
    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();

    // we should first retrieve the portlet to customize and its parameters
    // definition
    Portlet p = ((JetspeedRunData) rundata).getCustomized();
    List<?> params =
      (List<?>) customizationState.getAttribute("customize-parameters");
    String newSecurityParent =
      rundata.getParameters().getString("_security_ref");
    String newSkinName = rundata.getParameters().getString("_skin");
    String newTitle = rundata.getParameters().getString("current_title");

    boolean changeRequested =
      ((params != null) || (newSkinName != null) || (newSecurityParent != null) || (newTitle != null));
    boolean madePsChange = false;
    boolean madePcChange = false;

    if ((p == null) || (changeRequested == false)) {
      doCancel(rundata, context);
      return;
    }
    PortletConfig pc = p.getPortletConfig();
    Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
    Entry entry = profile.getDocument().getEntryById(p.getID());

    // Only update the security ref if the parent changed
    if ((newSecurityParent != null)) {
      boolean securityChanged = false;
      SecurityReference currentSecurityRef = pc.getSecurityRef();
      if (currentSecurityRef != null) {
        securityChanged =
          (newSecurityParent.equals(currentSecurityRef.getParent()) == false);
      } else {
        securityChanged = (newSecurityParent.trim().length() > 0);
      }
      if (securityChanged == true) {
        SecurityReference securityRef = null;
        if ((newSecurityParent.trim().length() > 0)) {
          securityRef = new BaseSecurityReference();
          securityRef.setParent(newSecurityParent);
        }
        // Note: setting the portlet's config may not be a good idea -
        // it might be used as the Portlet for other PSMLDocument Entries that
        // have a different idea of security - and the caching of Portlets does
        // NOT include security -ggolden.
        pc.setSecurityRef(securityRef);
        entry.setSecurityRef(securityRef);
        madePcChange = true;
      }
    }

    // Only update the skin if the name changed
    if (newSkinName != null) {
      boolean skinChanged = false;
      String currentSkinName = null;

      if (pc.getSkin() != null) {
        currentSkinName = pc.getPortletSkin().getName();
      }

      if (currentSkinName != null) {
        skinChanged = (newSkinName.equals(currentSkinName) == false);
      } else {
        skinChanged = (newSkinName.trim().length() > 0);
      }

      if (skinChanged == true) {
        PortletSkin skin = null;
        if ((newSkinName.trim().length() > 0)) {
          skin = PortalToolkit.getSkin(newSkinName);
          if (skin != null) {
            // Note: setting the portlet's config may not be a good idea -
            // it might be used as the Portlet for other PSMLDocument Entries
            // that
            // have a different idea of skin - and the caching of Portlets does
            // NOT include skin -ggolden.
            pc.setPortletSkin(skin);

            Skin psmlSkin = entry.getSkin();
            if (psmlSkin == null) {
              entry.setSkin(new PsmlSkin());
            }
            entry.getSkin().setName(newSkinName);
          } else {
            logger.warn("Unable to update skin for portlet entry "
              + entry.getId()
              + " because skin does not exist.");
          }
        } else {
          // Note: setting the portlet's config may not be a good idea -
          // it might be used as the Portlet for other PSMLDocument Entries that
          // have a different idea of skin - and the caching of Portlets does
          // NOT include skin -ggolden.
          pc.setPortletSkin(null);
          entry.setSkin(null);
        }
        madePcChange = true;
      }
    }

    // Only update the title if the title changed
    if (newTitle != null) {
      boolean titleChanged = false;
      String currentTitle = entry.getTitle();

      MetaData md = pc.getMetainfo();
      if (currentTitle == null && md != null && md.getTitle() != null) {
        currentTitle = md.getTitle();
      }

      if (currentTitle != null) {
        titleChanged = (newTitle.equals(currentTitle) == false);
      } else {
        titleChanged = (newTitle.trim().length() > 0);
      }

      if (titleChanged == true) {

        if ((newTitle.trim().length() > 0)) {
          // Note: setting the portlet's config may not be a good idea -
          // it might be used as the Portlet for other PSMLDocument Entries that
          // have a different idea of title - and the caching of Portlets does
          // NOT include title -ggolden.
          if (md == null) {
            md = new MetaData();
            pc.setMetainfo(md);
          }
          md.setTitle(newTitle);
          entry.setTitle(newTitle);
          madePcChange = true;
        }
      }
    }

    // Update paramaters
    try {
      PortletInstance instance = PersistenceManager.getInstance(p, rundata);
      PortletEntry regEntry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, p.getName());

      Iterator<?> i = params.iterator();

      while (i.hasNext()) {
        Parameter param = (Parameter) i.next();
        String name = param.getName();
        String newValue = null;
        String[] testArray = rundata.getParameters().getStrings(name);
        if (testArray != null && testArray.length > 1) {
          newValue =
            org.apache.jetspeed.util.StringUtils.arrayToString(testArray, ",");
        } else {
          newValue = rundata.getParameters().getString(name);
          if (newValue == null) {
            newValue = "";
          }
        }

        String regValue =
          name.startsWith("pref-") ? "" : regEntry
            .getParameter(name)
            .getValue();
        String psmlValue = instance.getAttribute(name);

        // New value for this parameter exists
        if (newValue != null) {
          if (!regValue.equals(newValue) || !psmlValue.equals(newValue)) {
            instance.setAttribute(name, newValue);
            psmlValue = newValue;
          }
          madePsChange = true;
        }
        // Remove duplicate parameters from psml
        if (psmlValue != null && psmlValue.equals(regValue)) {
          instance.removeAttribute(name);
          madePsChange = true;
        }

      }

      // save all the changes
      if ((madePsChange == true) || (madePcChange == true)) {
        try {
          // JetspeedRunData jdata = (JetspeedRunData) rundata;
          profile.store();
          // FIXME: this hack is due to the corrupted lifecycle of the portlet
          // in the
          // current API when caching is activated
          p.init();
          org.apache.jetspeed.util.PortletSessionState.setPortletConfigChanged(
            p,
            rundata);
        } catch (PortletException e) {
          logger.error("Customizer failed to reinitialize the portlet "
            + p.getName(), e);
        } catch (Exception e) {
          logger.error("Unable to save profile ", e);
        }
      }

      // we're done, make sure clean up the
      // session
      doCancel(rundata, context);
    } catch (Exception e) {
      logger.error("ALCustomizeAction.doUpdate", e);
    }
  }
}
