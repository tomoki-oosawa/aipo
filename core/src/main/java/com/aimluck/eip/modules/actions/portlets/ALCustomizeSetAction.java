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
package com.aimluck.eip.modules.actions.portlets;

// Jetspeed imports
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.modules.actions.portlets.PortletFilter;
import org.apache.jetspeed.modules.actions.portlets.VelocityPortletAction;
import org.apache.jetspeed.om.BaseSecurityReference;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.IdentityElement;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.Reference;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.om.profile.psml.PsmlControl;
import org.apache.jetspeed.om.profile.psml.PsmlController;
import org.apache.jetspeed.om.profile.psml.PsmlEntry;
import org.apache.jetspeed.om.profile.psml.PsmlLayout;
import org.apache.jetspeed.om.profile.psml.PsmlMetaInfo;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.om.profile.psml.PsmlPortlets;
import org.apache.jetspeed.om.profile.psml.PsmlReference;
import org.apache.jetspeed.om.profile.psml.PsmlSkin;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.om.registry.PortletInfoEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.base.BaseCategory;
import org.apache.jetspeed.om.registry.base.BasePortletEntry;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletSetController;
import org.apache.jetspeed.portal.PortletSkin;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.customlocalization.CustomLocalization;
import org.apache.jetspeed.services.idgenerator.JetspeedIdGenerator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.util.AutoProfile;
import org.apache.jetspeed.util.PortletSessionState;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.portal.ALPortalApplicationService;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.CustomizeUtils;

/**
 * This action implements the default portletset behavior customizer
 *
 * <p>
 * Don't call it from the URL, the Portlet and the Action are automatically
 * associated through the registry PortletName
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 */
public class ALCustomizeSetAction extends VelocityPortletAction {

  private static final String UI_PORTLETS_SELECTED = "portletsSelected";

  // private static final String ALL_PORTLET_LIST = "session.all.portlets.list";

  // private static final String PORTLET_LIST_PAGE_SIZE =
  // "session.portlets.page.size";

  private static final String HIDE_EMPTY_CATEGORIES =
    "customizer.hide.empty.categories";

  public static final String FILTER_FIELDS = "filter_fields";

  public static final String FILTER_VALUES = "filter_values";

  /** アクセス権限の有無 */
  protected boolean hasAuthority;

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALCustomizeSetAction.class.getName());

  /**
   * Subclasses must override this method to provide default behavior for the
   * portlet action
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    JetspeedRunData jdata = (JetspeedRunData) rundata;
    SessionState customizationState = jdata.getPageSessionState();
    Profile profile = jdata.getCustomizedProfile();
    String mediaType = profile.getMediaType();

    // set velocity variable of mediatype (displayed in the customizer menu)
    context.put("mtype", profile.getMediaType());

    // make the list of already used panes/portlets available through the 'runs'
    // reference
    context.put("runs", AutoProfile.getPortletList(rundata));
    ;
    // we should first retrieve the portlet to customize
    PortletSet set = (PortletSet) (jdata).getCustomized();

    // identify the portlet submode and build the appropriate subt-template path
    String mode = rundata.getParameters().getString("mode");
    if (mode == null) {
      mode = (String) customizationState.getAttribute("customize-mode");
      if ((mode == null)
        || (mode.equalsIgnoreCase("addset"))
        || (mode.equalsIgnoreCase("general"))) {
        mode = "layout";
        customizationState.setAttribute("customize-mode", mode);
      }

    } else {
      if ((mediaType.equalsIgnoreCase("wml"))
        && (!mode.equalsIgnoreCase("add"))) {
        mode = "layout";
      }

      customizationState.setAttribute("customize-mode", mode);
    }

    String template = (String) context.get("template");
    if (template != null) {
      int idx = template.lastIndexOf(".");

      if (idx > 0) {
        template = template.substring(0, idx);
      }

      StringBuffer buffer = new StringBuffer(template);
      buffer.append("-").append(mode).append(".vm");
      template =
        TemplateLocator.locatePortletTemplate(rundata, buffer.toString());
      context.put("feature", template);

    }

    if (set == null) {
      return;
    }

    // get the customization state for this page
    String customizedPaneName =
      (String) customizationState.getAttribute("customize-paneName");
    if (customizedPaneName == null) {
      customizedPaneName = "*";
    }

    // generic context stuff
    context.put("panename", customizedPaneName);
    context.put("skin", set.getPortletConfig().getPortletSkin());
    context.put("set", set);
    context.put("action", "portlets.ALCustomizeSetAction");
    context.put("controllers", buildInfoList(
      rundata,
      Registry.PORTLET_CONTROLLER,
      mediaType));
    // context.put("skins", buildList(rundata, Registry.SKIN));
    // context.put("securitys", buildList(rundata, Registry.SECURITY));
    context.put("customizer", portlet);

    String controllerName = set.getController().getConfig().getName();
    context.put("currentController", controllerName);

    context.put("currentSecurityRef", set.getPortletConfig().getSecurityRef());

    // 2007.04.04 update
    context.put("utils", new ALCommonUtils());
    setContexts(rundata, context);
    /**
     * Special handling for wml profiles no skins, no properties menuentry, no
     * panes
     * --------------------------------------------------------------------
     * ------ last modified: 12/10/01 Andreas Kempf, Siemens ICM S CP OP, Munich
     * mailto: A.Kempf@web.de
     */

    if (mediaType.equalsIgnoreCase("wml")) {
      context.put("currentSkin", "Not for wml!");
      context.put("allowproperties", "false");
    } else {
      if (set.getPortletConfig().getSkin() != null) {
        context.put("currentSkin", set
          .getPortletConfig()
          .getPortletSkin()
          .getName());
      }
      context.put("allowproperties", "true");
    }

    // context.put("allowpane", "false");
    context.put("allowpane", "true");

    // do not allow panes for wml profiles
    if ((!mediaType.equalsIgnoreCase("wml"))
      && (set.getController() instanceof PortletSetController)) {
      if (customizedPaneName != null) {
        context.put("allowpane", "true");
      }
    } else {
      context.put("allowportlet", "true");
    }
    // --------------------------------------------------------------------------

    if ("add".equals(mode)) // build context for add mode
    {
      ALEipUtils.checkAclPermissionForCustomize(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_INSERT);

      int start = rundata.getParameters().getInt("start", -1);
      if (start < 0) {
        start = 0;
        HttpServletRequest request = HttpServletRequestLocator.get();
        if (request != null) {
          request.removeAttribute("portlets.list");
          request.removeAttribute("portlets.selections");
        }
      }

      ArrayList<PortletEntry> allPortlets = new ArrayList<PortletEntry>();
      List<PortletEntry> portlets =
        buildPortletList(rundata, set, mediaType, allPortlets);
      Map<String, PortletEntry> userSelections =
        CustomizeUtils.getUserSelections(rundata);
      // Build a list of categories from the available portlets
      List<BaseCategory> categories =
        buildCategoryList(rundata, mediaType, allPortlets);
      context.put("categories", categories);

      context.put("parents", PortletFilter.buildParentList(allPortlets));
      addFiltersToContext(rundata, context);

      int size = getSize(portlet);
      int end = Math.min(start + size, portlets.size());

      if (start > 0) {
        context.put("prev", String.valueOf(Math.max(start - size, 0)));
      }

      if (start + size < portlets.size()) {
        context.put("next", String.valueOf(start + size));
      }

      context.put("browser", portlets.subList(start, end));
      context.put("size", Integer.valueOf(size));
      context.put(UI_PORTLETS_SELECTED, userSelections);

      context.put("portlets", portlets);
    } else if ("addref".equals(mode)) {
      Iterator<?> psmlIterator = null;
      psmlIterator = Profiler.query(new QueryLocator(QueryLocator.QUERY_ALL));

      // Set Start and End
      int start = rundata.getParameters().getInt("start", 0);
      int size = getSize(portlet);

      // Only include entries in compatibale with the
      // Media-type/Country/Language
      List<Profile> psmlList = new LinkedList<Profile>();
      Profile refProfile = null;
      int profileCounter = 0;
      while (psmlIterator.hasNext()) {
        refProfile = (Profile) psmlIterator.next();

        if (refProfile.getMediaType() != null) {
          if (profile.getMediaType().equals(refProfile.getMediaType()) == false) {
            continue;
          }
        }

        if (profile.getLanguage() != null) {
          if (refProfile.getLanguage() != null) {
            if (profile.getLanguage().equals(refProfile.getLanguage()) == true) {
              if (profile.getCountry() != null) {
                if (refProfile.getCountry() != null) {
                  if (profile.getCountry().equals(refProfile.getCountry()) == false) {
                    // Profile and Ref are different countries
                    continue;
                  }
                }
              } else {
                if (refProfile.getCountry() != null) {
                  // Profile has no country and Ref has a country
                  continue;
                }
              }
            } else {
              // Profile and Ref are different languages
              continue;
            }
          }
        } else {
          if (refProfile.getLanguage() != null) {
            // Profile has no Language and Ref has a country
            continue;
          }
        }

        if (profile.getPath().equals(refProfile.getPath()) == true) {
          // Do not allow Profile to reference it self
          continue;
        }

        // Only add profiles to list that will be displayed
        if (profileCounter >= (start + size)) {
          break;
        }
        if (profileCounter >= start) {
          psmlList.add(refProfile);
        }
        profileCounter++;
      }

      // Add Start to context
      if (start > 0) {
        context.put("prev", String.valueOf(Math.max(start - size, 0)));
      }

      // Set end to context
      if ((size == psmlList.size()) && (psmlIterator.hasNext())) {
        context.put("next", String.valueOf(start + size));
      }

      context.put("psml", psmlList.iterator());
    } else // build context for layout mode
    {

      // nothing specific to do
    }

  }

  private void setContexts(RunData rundata, Context context) {
    String pid = rundata.getParameters().get("js_peid");
    if (pid == null) {
      // pid=rundata.get
    }
    context.put("js_peid", pid);
    Portlets[] portletArrays =
      (((JetspeedRunData) rundata).getProfile().getDocument().getPortlets())
        .getPortletsArray();
    ALStringField pageTitle = new ALStringField();
    String mypageId = "";
    for (Portlets p : portletArrays) {
      if ("マイページ".equals(p.getTitle())) {
        mypageId = p.getId();
      }
      if (p.getId().equals(pid)) {
        MetaInfo info = p.getMetaInfo();
        pageTitle.setValue(info.getTitle());
      }
    }
    context.put("isMypage", "マイページ".equals(pageTitle.getValue()));
    context.put("mypageId", mypageId);
    context.put("globalPortlets", ALEipUtils.getGlobalPortlets(rundata));
    context.put("pageTitle", pageTitle);

    context.put(ALEipConstants.SECURE_ID, rundata.getUser().getTemp(
      ALEipConstants.SECURE_ID));
  }

  public int getSize(VelocityPortlet portlet) {
    int size = 30;
    try {
      size =
        Integer.parseInt(portlet.getPortletConfig().getInitParameter("size"));
    } catch (Exception e) {
      logger.debug("CustomizeSetAction: Init param 'size' not parsed");
    }
    return size;
  }

  /** Clean up the customization state */
  public void doCancel(RunData rundata, Context context) {
    // ((JetspeedRunData)rundata).setCustomized(null);
    // rundata.setScreenTemplate("Home");
    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();
    customizationState.setAttribute("customize-mode", "layout");
  }

  /** Save the general informations for this set */
  public void doSave(RunData rundata, Context context) {
    doMetainfo(rundata, context);
    doSkin(rundata, context);
    doLayout(rundata, context);
    doSecurity(rundata, context);

    Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
    try {
      String mtype = rundata.getParameters().getString("mtype");

      if (mtype != null) {
        profile.setMediaType(mtype);
      }
      profile.store();
    } catch (Exception e) {
      logger.error("Exception occured while saving PSML", e);
    }
  }

  /** Save customizations and get out of customization state */
  public void doApply(RunData rundata, Context context) {
    doSave(rundata, context);
  }

  /** Add a new portlets element in the customized set */
  public void doAddset(RunData rundata, Context context) {
    Portlets portlets =
      ((JetspeedRunData) rundata)
        .getCustomizedProfile()
        .getDocument()
        .getPortlets();
    String title = rundata.getParameters().getString("title", "My Pane");

    if (portlets != null) {
      // 最後に配置しているタブポートレットの位置を 1 つ後にずらす
      Portlets[] portletList = portlets.getPortletsArray();
      long position = 0;
      long tmpPosition = 0;
      int index = 0;
      int length = portletList.length;
      for (int i = 0; i < length; i++) {
        tmpPosition = portletList[i].getLayout().getPosition();
        if (position < tmpPosition) {
          position = tmpPosition;
          index = i;
        }
      }
      portletList[index].getLayout().setPosition(position + 1);

      // レイアウトの作成
      Layout newLayout = new PsmlLayout();
      newLayout.setPosition(position);
      newLayout.setSize(-1);

      Portlets p = new PsmlPortlets();
      p.setLayout(newLayout);
      p.setMetaInfo(new PsmlMetaInfo());
      p.getMetaInfo().setTitle(title);
      p.setId(JetspeedIdGenerator.getNextPeid());
      SecurityReference defaultRef =
        PortalToolkit.getDefaultSecurityRef(((JetspeedRunData) rundata)
          .getCustomizedProfile());
      if (defaultRef != null) {
        if (logger.isDebugEnabled()) {
          logger
            .debug("CustomizeSetAction: setting default portlet set security to ["
              + defaultRef.getParent()
              + "]");
        }
        p.setSecurityRef(defaultRef);
      }
      portlets.addPortlets(p);
    }

    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();
    customizationState.setAttribute("customize-mode", "layout");
  }

  @SuppressWarnings("deprecation")
  public void doPrevious(RunData rundata, Context context) throws Exception {
    int queryStart = rundata.getParameters().getInt("previous", 0);
    String mtype = rundata.getParameters().getString("mtype", null);
    maintainUserSelections(rundata);
    JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
    DynamicURI duri = null;
    if (mtype == null) {
      duri =
        link.setTemplate("Customize").addQueryData(
          "start",
          String.valueOf(queryStart));
    } else {
      duri =
        link.setTemplate("Customize").addQueryData(
          "start",
          String.valueOf(queryStart)).addQueryData("mtype", mtype);
    }
    JetspeedLinkFactory.putInstance(link);
    rundata.setRedirectURI(duri.toString());
    return;
  }

  @SuppressWarnings("deprecation")
  public void doNext(RunData rundata, Context context) throws Exception {
    int queryStart = rundata.getParameters().getInt("next", 0);
    String mtype = rundata.getParameters().getString("mtype", null);
    maintainUserSelections(rundata);
    JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
    DynamicURI duri = null;
    if (mtype == null) {
      duri =
        link.setTemplate("Customize").addQueryData(
          "start",
          String.valueOf(queryStart));
    } else {
      duri =
        link.setTemplate("Customize").addQueryData(
          "start",
          String.valueOf(queryStart)).addQueryData("mtype", mtype);
    }
    JetspeedLinkFactory.putInstance(link);
    rundata.setRedirectURI(duri.toString());
    return;
  }

  protected void maintainUserSelections(RunData rundata) throws Exception {
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    Profile profile = jdata.getProfile();
    String mediaType = profile.getMediaType();
    String[] pnames = rundata.getParameters().getStrings("pname");
    Map<String, PortletEntry> userSelections =
      CustomizeUtils.getUserSelections(rundata);
    List<PortletEntry> portlets =
      CustomizeUtils.buildAllPortletList(
        rundata,
        mediaType,
        new ArrayList<PortletEntry>());

    if (portlets == null) {
      throw new Exception("Master Portlet List is null!");
    }

    if (pnames != null) {
      for (String pname : pnames) {
        for (PortletEntry entry : portlets) {
          String name = entry.getName();
          if (name.equals(pname)) {
            userSelections.put(pname, userSelections.get(pname));
            break;
          }
        }
      }
    }

    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute("portlets.selections", userSelections);
    }
  }

  /** Add new portlets in the customized set */
  public void doAdd(RunData rundata, Context context) throws Exception {
    /**
     * Special handling for wml profiles no skins, no properties menuentry, no
     * panes
     * --------------------------------------------------------------------
     * ------ last modified: 10/31/01 Andreas Kempf, Siemens ICM S CP PE, Munich
     * mailto: A.Kempf@web.de
     */
    // boolean isWML = AutoProfile.doIt (rundata,
    // true).getMediaType().equalsIgnoreCase("wml");
    PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();

    maintainUserSelections(rundata);
    Map<String, PortletEntry> userSelections =
      CustomizeUtils.getUserSelections(rundata);
    String[] pnames = new String[userSelections.size()];
    userSelections.keySet().toArray(pnames);

    // String[] pnames = rundata.getParameters().getStrings("pname");

    // Create a ClearPortletControl
    Control ctrl = new PsmlControl();
    ctrl.setName("ClearPortletControl");

    if ((pnames != null) && (set != null)) {
      Portlets portlets =
        ((JetspeedRunData) rundata)
          .getCustomizedProfile()
          .getDocument()
          .getPortletsById(set.getID());

      // List usedPortlets = AutoProfile.getPortletList(rundata);
      boolean addIt;
      // int cc;
      // Entry usedEntry;

      for (int i = 0; i < pnames.length; i++) {
        String pname = pnames[i];
        PortletEntry entry = null;
        boolean isGadgets = false;
        ALApplication app = null;
        if (pname.startsWith("GadgetsTemplate::")) {
          String[] split = pname.split("::");
          String appId = split[1];
          app =
            ALApplicationService.get(new ALApplicationGetRequest()
              .withAppId(appId));
          entry =
            (PortletEntry) Registry.getEntry(
              Registry.PORTLET,
              "GadgetsTemplate");
          isGadgets = true;
        } else {
          entry = (PortletEntry) Registry.getEntry(Registry.PORTLET, pnames[i]);
        }

        // add only new portlets!
        if ((entry != null) && (portlets != null)) {

          addIt = true;
          /*
           * for (cc=0; cc<usedPortlets.size(); cc++) { usedEntry = (Entry)
           * usedPortlets.get(cc); if
           * (entry.getName().equals(usedEntry.getParent())) { addIt = false;
           * break; } }
           */
          if (addIt) {
            Entry p = new PsmlEntry();
            // add the ClearPortletControl to wml entries
            // if (isWML)
            // p.setControl (ctrl);

            p.setParent(isGadgets ? "GadgetsTemplate" : pnames[i]);
            p.setId(JetspeedIdGenerator.getNextPeid());
            // SecurityReference defaultRef =
            // PortalToolkit.getDefaultSecurityRef(
            // ((JetspeedRunData) rundata).getCustomizedProfile());
            // if (defaultRef != null)
            // {
            // if (Log.getLogger().isDebugEnabled())
            // {
            // Log.debug("CustomizeSetAction: setting default portlet security
            // to [" + defaultRef.getParent() + "]");
            // }
            // p.setSecurityRef(defaultRef);
            // }
            if (isGadgets) {
              p.setTitle(app.getTitle().getValue());
              Parameter p1 = new PsmlParameter();
              p1.setName("aid");
              p1.setValue(app.getAppId().getValue());
              p.addParameter(p1);
              Parameter p2 = new PsmlParameter();
              p2.setName("url");
              p2.setValue(app.getUrl().getValue());
              p.addParameter(p2);
              Parameter p3 = new PsmlParameter();
              p3.setName("mid");
              p3.setValue(String
                .valueOf(ALApplicationService.getNextModuleId()));
              p.addParameter(p3);
            }
            portlets.addEntry(p);
          }
        }
      }
    }
    // --------------------------------------------------------------------------

    // 理由等 ：追加したポートレットを PSML に保存する．
    doSaveAddAction(rundata, context);

    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();
    customizationState.setAttribute("customize-mode", "layout");

    // 理由等 ：追加したポートレット情報をセッションに反映させるため，
    // リダイレクトでレイアウト設定画面に移行させた．
    JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    if (set != null) {
      rundata.setRedirectURI(jsLink
        .getTemplate()
        .addPathInfo("js_peid", set.getID())
        .addPathInfo("mode", "layout")
        .addQueryData("mtype", "html")
        .addQueryData("action", "controls.Customize")
        .toString());
      rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      jsLink = null;
    }
    /**
     * fasdfadfa Save changed wml profile
     * --------------------------------------------------
     * ------------------------ last modified: 10/31/01 Andreas Kempf, Siemens
     * ICM S CP PE, Munich mailto: A.Kempf@web.de if (isWML) {
     * ((JetspeedRunData)rundata).getCustomizedProfile().store();
     * //rundata.save(); }
     */

  }

  public void doSaveAddAction(RunData data, Context context) {
    PortletSet set = (PortletSet) ((JetspeedRunData) data).getCustomized();
    Portlets portlets =
      ((JetspeedRunData) data)
        .getCustomizedProfile()
        .getDocument()
        .getPortletsById(set.getID());

    setPageLayout(data, context);
    // String REFERENCES_REMOVED = "references-removed";
    // get the customization state for this page
    // update the changes made here to the profile being edited
    List<?>[] columns =
      CustomizeUtils.buildCustomizeColumns(data, context, portlets);
    for (int col = 0; col < columns.length; col++) {
      for (int row = 0; row < columns[col].size(); row++) {
        setPosition((IdentityElement) columns[col].get(row), col, row);
      }
    }
    // save the edit profile and make it current
    try {
      ((JetspeedRunData) data).getCustomizedProfile().store();

      // // Because of the way references are stored in memory, we have to
      // completely refresh
      // // the profile after a references is removed (otherwise it will
      // continue being displayed)
      // String referencesRemoved =
      // (String) customizationState.getAttribute(REFERENCES_REMOVED);
      // if (referencesRemoved != null && referencesRemoved.equals("true")) {
      // PsmlManager.refresh(((JetspeedRunData) data).getCustomizedProfile());
      // }
    } catch (Exception e) {
      logger.error("Unable to save profile ", e);
    }
  }

  /** Add new Reference in the customized set */
  public void doAddref(RunData rundata, Context context) throws Exception {
    PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
    String[] refNames = rundata.getParameters().getStrings("refname");

    // Create a ClearPortletControl
    Control ctrl = new PsmlControl();
    ctrl.setName("ClearPortletControl");

    if ((refNames != null) && (set != null)) {
      Portlets portlets =
        ((JetspeedRunData) rundata)
          .getCustomizedProfile()
          .getDocument()
          .getPortletsById(set.getID());

      for (int i = 0; i < refNames.length; i++) {
        SecurityReference sref = getSecurityReference(rundata, refNames[i]);
        if (sref != null) {
          Reference ref = new PsmlReference();
          ref.setPath(refNames[i]);
          ref.setSecurityRef(sref);
          portlets.addReference(ref);
        } else {
          String tmpl =
            CustomLocalization.getString("CUSTOMIZER_ADD_REF_ERROR", rundata);
          Object[] args = { refNames[i] };
          String message = MessageFormat.format(tmpl, args);

          rundata.addMessage(message.concat("<br>"));
          if (logger.isWarnEnabled()) {
            logger.warn(message);
          }
        }
      }
    }
    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();
    customizationState.setAttribute("customize-mode", "layout");
  }

  /**
   * Get the security reference from the outer portlet set
   *
   * @param path
   *          the psml locator path
   * @return the security reference of the referenced resource
   */
  protected SecurityReference getSecurityReference(RunData rundata, String path) {
    try {
      ProfileLocator locator = Profiler.createLocator();
      locator.createFromPath(path);
      Profile profile = Profiler.getProfile(locator);
      if (profile != null) {
        PSMLDocument doc = profile.getDocument();
        if (doc != null) {
          Portlets rootSet = doc.getPortlets();
          /*
           * There is no way to do a check on a Portlets element, only a Entry
           * element. This can easily be added, but Im just under a release
           * right now and it could be perceived as too destabilizing -- david
           * 
           * if (JetspeedSecurity.checkPermission((JetspeedUser)
           * rundata.getUser(), rootSet, JetspeedSecurity.PERMISSION_VIEW)) {
           */
          return rootSet.getSecurityRef();
          // }
        }
      }
    } catch (ProfileException e) {
      logger.error("ALCustomizeSetAction.getSecurityReference", e);
    }
    return null;
  }

  /** Sets the metainfo for this entry */
  public void doMetainfo(RunData rundata, Context context) {
    PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();
    String title = rundata.getParameters().getString("title");
    String description = rundata.getParameters().getString("description");

    if (set != null) {
      Portlets portlets =
        ((JetspeedRunData) rundata)
          .getCustomizedProfile()
          .getDocument()
          .getPortletsById(set.getID());

      if (portlets != null) {
        MetaInfo meta = portlets.getMetaInfo();
        if (meta == null) {
          meta = new PsmlMetaInfo();
          portlets.setMetaInfo(meta);
        }

        if (title != null) {
          meta.setTitle(title);
          set.setTitle(title);
        }

        if (description != null) {
          meta.setDescription(description);
          set.setDescription(description);
        }
      }
    }
  }

  /** Updates the customized portlet entry */
  public void doLayout(RunData rundata, Context context) {
    ALEipUtils.checkAclPermissionForCustomize(
      rundata,
      context,
      ALAccessControlConstants.VALUE_ACL_UPDATE);

    // we should first retrieve the portlet to customize and its parameters
    // definition
    PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();

    try {
      String controller = rundata.getParameters().getString("controller");

      if (controller != null) {
        Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
        PortletController pc = PortalToolkit.getController(controller);

        if (pc != null) {
          set.setController(pc);

          Portlets portlets =
            profile.getDocument().getPortletsById(set.getID());

          Controller c = portlets.getController();
          if (c == null) {
            c = new PsmlController();
            portlets.setController(c);
          }
          c.setName(controller);

          String linkedControl = pc.getConfig().getInitParameter("control");

          if (linkedControl != null) {
            Control ctl = new PsmlControl();
            ctl.setName(linkedControl);
            portlets.setControl(ctl);
          } else {
            portlets.setControl(null);
          }
        }
      }
      setContexts(rundata, context);
    } catch (Exception e) {
      logger.error("ALCustomizeSetAction.doLayout", e);
    }
  }

  /**
   * Set the skin in the PSML and the current PortletConfig using the HTML
   * parameter "skin". If the parmeter is missing or 'blank', then the skin is
   * set to null.
   *
   */

  /**
   * Set the skin in the PSML and the current PortletConfig using the HTML
   * parameter "skin". If the parmeter is missing or 'blank', then the skin is
   * set to null.
   *
   */
  @SuppressWarnings("deprecation")
  public void doSkin(RunData rundata, Context context) {
    // we should first retrieve the portlet to customize and its parameters
    // definition
    PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();

    try {
      String skin = rundata.getParameters().getString("skin");
      Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
      Portlets portlets = profile.getDocument().getPortletsById(set.getID());

      // skin is neither null nor zero-length
      if ((skin != null) && (skin.trim().length() > 0)) {
        PortletSkin s = PortalToolkit.getSkin(skin);

        if (s != null) {
          set.getPortletConfig().setPortletSkin(s);

          Skin psmlSkin = portlets.getSkin();
          if (psmlSkin == null) {
            portlets.setSkin(new PsmlSkin());
          }
          portlets.getSkin().setName(skin);
        } else {
          logger.warn("Unable to update skin for portlet set "
            + set.getID()
            + " because skin "
            + skin
            + " does not exist.");
          return;
        }
      } else {
        // skin is either null or zero-length
        String custPortletSetID = portlets.getId();
        String rootPortletSetID = profile.getRootSet().getID();

        // set system default skin for root PSML element
        if (custPortletSetID != null
          && rootPortletSetID != null
          && custPortletSetID.equals(rootPortletSetID)) {
          // get system default skin
          String defaultSkinName =
            JetspeedResources.getString("services.PortalToolkit.default.skin");
          PortletSkin defaultSkin = PortalToolkit.getSkin(defaultSkinName);

          if (defaultSkin != null) {
            set.getPortletConfig().setPortletSkin(defaultSkin);
            Skin psmlSkin = portlets.getSkin();

            if (psmlSkin == null) {
              portlets.setSkin(new PsmlSkin());
            }

            portlets.getSkin().setName(defaultSkin.getName());
          } else {
            logger.warn("Unable to set default skin for root portlet set "
              + set.getID()
              + " because skin "
              + skin
              + " does not exist.");
            return;
          }
        } else {
          // By setting the skin to null, the parent's skin will be used.
          set.getPortletConfig().setPortletSkin((PortletSkin) null);
          portlets.setSkin(null);
        }
      }
    } catch (Exception e) {
      logger.error("ALCustomizeSetAction.doSkin", e);
    }
  }

  /**
   * Set the SecuirtyRef in the PSML and the current PortletConfig using the
   * HTML parameter "securityRef". If the parmeter is missing or 'blank', then
   * the SecuriyReference is set to null.
   *
   */
  public void doSecurity(RunData rundata, Context context) {
    // we should first retrieve the portlet to customize and its parameters
    // definition
    PortletSet set = (PortletSet) ((JetspeedRunData) rundata).getCustomized();

    try {
      String securityRefName = rundata.getParameters().getString("securityRef");
      SecurityReference securityRef = null;
      Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
      Portlets portlets = profile.getDocument().getPortletsById(set.getID());

      if ((securityRefName != null) && (securityRefName.trim().length() > 0)) {
        securityRef = new BaseSecurityReference();
        securityRef.setParent(securityRefName);
      }
      set.getPortletConfig().setSecurityRef(securityRef);
      portlets.setSecurityRef(securityRef);
    } catch (Exception e) {
      logger.error("ALCustomizeSetAction.doSecurity", e);
    }
  }

  // Create a list of all available portlets
  @SuppressWarnings("unchecked")
  public static List<PortletEntry> buildPortletList(RunData data,
      PortletSet set, String mediaType, List<PortletEntry> allPortlets) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    List<PortletEntry> list = null;
    if (request != null) {
      try {
        list = (List<PortletEntry>) request.getAttribute("portlets.list");
      } catch (Throwable ignore) {
        //
      }
    }
    if (list != null) {
      return list;
    }
    list = new ArrayList<PortletEntry>();
    Iterator<?> i = Registry.get(Registry.PORTLET).listEntryNames();

    while (i.hasNext()) {
      PortletEntry entry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, (String) i.next());

      // Iterator medias;
      // Make a master portlet list, we will eventually us this to build a
      // category list
      allPortlets.add(entry);
      // MODIFIED: Selection now takes care of the specified mediatype!
      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_VIEW)
        && ((!entry.isHidden())
          && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT)) && entry
            .hasMediaType(mediaType))
        && !entry.getSecurityRef().getParent().equals("admin-view")
        && CustomizeUtils.isAdminUserView(entry, data)
        && ALPortalApplicationService.isActive(entry.getName())) {
        list.add(entry);
      }
    }

    ResultList<ALApplication> resultList =
      ALApplicationService.getList(new ALApplicationGetRequest()
        .withStatus(ALApplicationGetRequest.Status.ACTIVE));

    for (ALApplication app : resultList) {
      BasePortletEntry entry = new BasePortletEntry();
      entry.setTitle(app.getTitle().getValue());
      entry.setDescription(app.getDescription().getValue());
      entry.setName("GadgetsTemplate::" + app.getAppId().getValue());
      entry.setParent("GadgetsTemplate");
      entry.addParameter("aid", app.getAppId().getValue());
      entry.addParameter("url", app.getUrl().getValue());
      list.add(entry);
    }

    String[] filterFields =
      (String[]) PortletSessionState.getAttribute(data, FILTER_FIELDS);
    String[] filterValues =
      (String[]) PortletSessionState.getAttribute(data, FILTER_VALUES);
    list = PortletFilter.filterPortlets(list, filterFields, filterValues);

    Collections.sort(list, new Comparator<PortletEntry>() {
      @Override
      public int compare(PortletEntry o1, PortletEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle().toLowerCase() : (o1)
            .getName()
            .toLowerCase();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle().toLowerCase() : (o2)
            .getName()
            .toLowerCase();

        return t1.compareTo(t2);
      }
    });
    // this is used only by maintainUserSelection - which does not need the
    // portlet list to be regenrated
    if (request != null) {
      request.setAttribute("portlets.list", list);
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  public static List<PortletEntry> buildPortletList(RunData data,
      String mediaType) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    List<PortletEntry> list = null;
    if (request != null) {
      try {
        list = (List<PortletEntry>) request.getAttribute("portlets.list");
      } catch (Throwable ignore) {
        //
      }
    }
    if (list != null) {
      return list;
    }
    list = new ArrayList<PortletEntry>();
    Iterator<?> i = Registry.get(Registry.PORTLET).listEntryNames();

    while (i.hasNext()) {
      PortletEntry entry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, (String) i.next());

      // Iterator medias;
      // Make a master portlet list, we will eventually us this to build a
      // category list
      // MODIFIED: Selection now takes care of the specified mediatype!
      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_VIEW)
        && ((!entry.isHidden())
          && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT)) && entry
            .hasMediaType(mediaType))
        && !entry.getSecurityRef().getParent().equals("admin-view")
        && CustomizeUtils.isAdminUserView(entry, data)
        && ALPortalApplicationService.isActive(entry.getName())) {
        list.add(entry);
      }
    }

    ResultList<ALApplication> resultList =
      ALApplicationService.getList(new ALApplicationGetRequest()
        .withStatus(ALApplicationGetRequest.Status.ACTIVE));

    for (ALApplication app : resultList) {
      BasePortletEntry entry = new BasePortletEntry();
      entry.setTitle(app.getTitle().getValue());
      entry.setDescription(app.getDescription().getValue());
      entry.setName("GadgetsTemplate::" + app.getAppId().getValue());
      entry.setParent("GadgetsTemplate");
      entry.addParameter("aid", app.getAppId().getValue());
      entry.addParameter("url", app.getUrl().getValue());
      list.add(entry);
    }

    String[] filterFields =
      (String[]) PortletSessionState.getAttribute(data, FILTER_FIELDS);
    String[] filterValues =
      (String[]) PortletSessionState.getAttribute(data, FILTER_VALUES);
    list = PortletFilter.filterPortlets(list, filterFields, filterValues);

    Collections.sort(list, new Comparator<PortletEntry>() {
      @Override
      public int compare(PortletEntry o1, PortletEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle().toLowerCase() : (o1)
            .getName()
            .toLowerCase();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle().toLowerCase() : (o2)
            .getName()
            .toLowerCase();

        return t1.compareTo(t2);
      }
    });
    // this is used only by maintainUserSelection - which does not need the
    // portlet list to be regenrated
    if (request != null) {
      request.setAttribute("portlets.list", list);
    }
    return list;
  }

  public static List<PortletInfoEntry> buildInfoList(RunData data,
      String regName, String mediaType) {
    List<PortletInfoEntry> list = new ArrayList<PortletInfoEntry>();

    // String mime =
    // ((JetspeedRunData) data).getCapability().getPreferredType().toString();

    // Iterator m = Registry.get(Registry.MEDIA_TYPE).listEntryNames();
    // String mediaName = "html";
    //
    // while(m.hasNext())
    // {
    // MediaTypeEntry me = (MediaTypeEntry)
    // Registry.getEntry(Registry.MEDIA_TYPE,(String)m.next());
    //
    // if (me!=null)
    // {
    // if (mime.equals(me.getMimeType()))
    // {
    // mediaName = me.getName();
    // break;
    // }
    // }
    // }

    Iterator<?> i = Registry.get(regName).listEntryNames();

    while (i.hasNext()) {
      PortletInfoEntry entry =
        (PortletInfoEntry) Registry.getEntry(regName, (String) i.next());

      // MODIFIED: Selection now takes care of the specified mediatype!
      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_CUSTOMIZE)
        && ((!entry.isHidden()) && entry.hasMediaType(mediaType))) {
        list.add(entry);
      }
    }

    Collections.sort(list, new Comparator<RegistryEntry>() {
      @Override
      public int compare(RegistryEntry o1, RegistryEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle() : (o1).getName();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle() : (o2).getName();

        return t1.compareTo(t2);
      }
    });

    return list;
  }

  public static List<RegistryEntry> buildList(RunData data, String regName) {
    List<RegistryEntry> list = new ArrayList<RegistryEntry>();

    Iterator<?> i = Registry.get(regName).listEntryNames();
    while (i.hasNext()) {
      RegistryEntry entry = Registry.getEntry(regName, (String) i.next());

      if (JetspeedSecurity.checkPermission(
        (JetspeedUser) data.getUser(),
        new PortalResource(entry),
        JetspeedSecurity.PERMISSION_CUSTOMIZE)
        && (!entry.isHidden())) {
        list.add(entry);
      }
    }

    Collections.sort(list, new Comparator<RegistryEntry>() {
      @Override
      public int compare(RegistryEntry o1, RegistryEntry o2) {
        String t1 =
          ((o1).getTitle() != null) ? (o1).getTitle() : (o1).getName();
        String t2 =
          ((o2).getTitle() != null) ? (o2).getTitle() : (o2).getName();

        return t1.compareTo(t2);
      }
    });

    return list;
  }

  /**
   * Builds a list of all portlet categories
   *
   * @param RunData
   *          current requests RunData object
   * @param List
   *          portlets All available portlets
   */
  public static List<BaseCategory> buildCategoryList(RunData data,
      String mediaType, List<PortletEntry> portlets) {
    boolean hideEmpties =
      JetspeedResources.getBoolean(HIDE_EMPTY_CATEGORIES, true);
    TreeMap<String, BaseCategory> catMap = new TreeMap<String, BaseCategory>();
    Iterator<PortletEntry> pItr = portlets.iterator();
    while (pItr.hasNext()) {
      PortletEntry entry = pItr.next();
      if (hideEmpties) {
        if (JetspeedSecurity.checkPermission(
          (JetspeedUser) data.getUser(),
          new PortalResource(entry),
          JetspeedSecurity.PERMISSION_VIEW)
          && ((!entry.isHidden())
            && (!entry.getType().equals(PortletEntry.TYPE_ABSTRACT)) && entry
              .hasMediaType(mediaType))) {
          Iterator<?> cItr = entry.listCategories();
          while (cItr.hasNext()) {
            BaseCategory cat = (BaseCategory) cItr.next();
            catMap.put(cat.getName(), cat);
          }
        }
      } else {
        Iterator<?> cItr = entry.listCategories();
        while (cItr.hasNext()) {
          BaseCategory cat = (BaseCategory) cItr.next();
          catMap.put(cat.getName(), cat);
        }
      }
    }

    // BaseCategory allCat = new BaseCategory();
    // allCat.setName("All Portlets");
    // catMap.put(allCat.getName(), allCat);
    return new ArrayList<BaseCategory>(catMap.values());

  }

  /**
   * Adds a filter over the available portlets list based on category
   */
  @SuppressWarnings("deprecation")
  public void doFiltercategory(RunData rundata, Context context)
      throws Exception {
    String filterCat =
      rundata.getParameters().getString("filter_category", "All Portlets");
    PortletSessionState.setAttribute(rundata, "filter_category", filterCat);
    maintainUserSelections(rundata);

    String mtype = rundata.getParameters().getString("mtype", null);
    JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
    DynamicURI duri = null;
    if (mtype == null) {
      duri = link.setTemplate("Customize").addQueryData("start", "0");
    } else {
      duri =
        link.setTemplate("Customize").addQueryData("start", "0").addQueryData(
          "mtype",
          mtype);
    }
    JetspeedLinkFactory.putInstance(link);
    rundata.setRedirectURI(duri.toString());
    return;
  }

  /**
   * Adds a filter over the available portlets list based on category
   */
  @SuppressWarnings("deprecation")
  public void doFilter(RunData rundata, Context context) throws Exception {
    String[] filterFields = rundata.getParameters().getStrings("filter_field");
    String[] filterValues = new String[filterFields.length];
    for (int i = 0; i < filterFields.length; i++) {
      String filterField = filterFields[i];
      if (filterField != null) {
        String filterValue =
          rundata.getParameters().getString(filterField + ":filter_value");
        filterValues[i] = filterValue;
      }
    }

    PortletSessionState.setAttribute(rundata, FILTER_FIELDS, filterFields);
    PortletSessionState.setAttribute(rundata, FILTER_VALUES, filterValues);

    maintainUserSelections(rundata);

    String mtype = rundata.getParameters().getString("mtype", null);
    JetspeedLink link = JetspeedLinkFactory.getInstance(rundata);
    DynamicURI duri = null;
    if (mtype == null) {
      duri = link.setTemplate("Customize").addQueryData("start", "0");
    } else {
      duri =
        link.setTemplate("Customize").addQueryData("start", "0").addQueryData(
          "mtype",
          mtype);
    }
    JetspeedLinkFactory.putInstance(link);
    rundata.setRedirectURI(duri.toString());
    return;
  }

  private void addFiltersToContext(RunData data, Context context) {
    String[] filterFields =
      (String[]) PortletSessionState.getAttribute(data, FILTER_FIELDS);
    String[] filterValues =
      (String[]) PortletSessionState.getAttribute(data, FILTER_VALUES);
    if (filterFields != null
      && filterValues != null
      && filterFields.length == filterValues.length) {
      for (int i = 0; i < filterFields.length; i++) {
        String field = filterFields[i];
        String value = filterValues[i];

        context.put(field + "_filter_value", value);
      }
    }
  }

  protected static void setPosition(IdentityElement identityElement, int col,
      int row) {
    boolean colFound = false;
    boolean rowFound = false;

    if (identityElement != null) {
      Layout layout = identityElement.getLayout();

      if (layout == null) {
        layout = new PsmlLayout();
        identityElement.setLayout(layout);
      }

      for (int i = 0; i < layout.getParameterCount(); i++) {
        Parameter p = layout.getParameter(i);

        if (p.getName().equals("column")) {
          p.setValue(String.valueOf(col));
          colFound = true;
        } else if (p.getName().equals("row")) {
          p.setValue(String.valueOf(row));
          rowFound = true;
        }
      }

      if (!colFound) {
        Parameter p = new PsmlParameter();
        p.setName("column");
        p.setValue(String.valueOf(col));
        layout.addParameter(p);
      }

      if (!rowFound) {
        Parameter p = new PsmlParameter();
        p.setName("row");
        p.setValue(String.valueOf(row));
        layout.addParameter(p);
      }
    }
  }

  @SuppressWarnings("deprecation")
  private void setPageLayout(RunData rundata, Context context) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;

    // get the customization state for this page
    SessionState customizationState = jdata.getPageSessionState();

    PortletSet portletSet = (PortletSet) (jdata).getCustomized();
    PortletController controller = portletSet.getController();
    List<?>[] columns = null;
    // retrieve the number of columns
    String cols = controller.getConfig().getInitParameter("cols");
    int colNum = 0;
    try {
      colNum = Integer.parseInt(cols);
    } catch (Exception e) {
      // not an integer or null, default to standarrd value
      colNum = 3;
    }
    // context.put("colNum", String.valueOf(colNum));

    // retrieve the size for each of the columns
    // String sizes = controller.getConfig().getInitParameter("sizes");
    // context.put("sizes", getCellSizes(sizes));

    // retrieve the class for each of the columns
    // String columnClasses =
    // controller.getConfig().getInitParameter("col_classes");
    // context.put("col_classes", getCellClasses(columnClasses));

    PortletSet customizedSet = (PortletSet) jdata.getCustomized();
    Portlets set =
      jdata.getCustomizedProfile().getDocument().getPortletsById(
        customizedSet.getID());
    columns = CustomizeUtils.buildCustomizeColumns(rundata, context, set);

    if (logger.isDebugEnabled()) {
      logger.debug("MultiCol: columns "
        + Arrays.toString(columns)
        + " set "
        + set);
    }
    if ((columns != null) && (columns.length == colNum)) {
      int eCount = 0;
      for (int i = 0; i < columns.length; i++) {
        eCount += columns[i].size();
      }

      if (logger.isDebugEnabled()) {
        logger.debug("MultiCol: eCount "
          + eCount
          + " setCount"
          + set.getEntryCount()
          + set.getPortletsCount());
      }
      if (eCount != set.getEntryCount() + set.getPortletsCount()) {
        if (logger.isDebugEnabled()) {
          logger.debug("MultiCol: rebuilding columns ");
        }
        columns = buildColumns(set, colNum);
      }

    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("MultiCol: rebuilding columns ");
      }
      columns = buildColumns(set, colNum);
    }
    customizationState.setAttribute("customize-columns", columns);
    context.put("portlets", columns);

    Map<String, String> titles = new HashMap<String, String>();
    for (int col = 0; col < columns.length; col++) {
      for (int row = 0; row < columns[col].size(); row++) {
        IdentityElement identityElement =
          (IdentityElement) columns[col].get(row);
        MetaInfo metaInfo = identityElement.getMetaInfo();
        if ((metaInfo != null) && (metaInfo.getTitle() != null)) {
          titles.put(identityElement.getId(), metaInfo.getTitle());
          continue;
        }

        if (identityElement instanceof Entry) {
          Entry entry = (Entry) identityElement;
          PortletEntry pentry =
            (PortletEntry) Registry.getEntry(Registry.PORTLET, entry
              .getParent());
          if ((pentry != null) && (pentry.getTitle() != null)) {
            titles.put(entry.getId(), pentry.getTitle());
            continue;
          }

          titles.put(entry.getId(), entry.getParent());
          continue;
        }

        if (identityElement instanceof Reference) {
          titles.put(identityElement.getId(), Localization.getString(
            rundata,
            "CUSTOMIZER_REF_DEFAULTTITLE"));
          continue;
        }

        // Let's make sure their is a title
        titles.put(identityElement.getId(), Localization.getString(
          rundata,
          "CUSTOMIZER_NOTITLESET"));
      }
    }
    Map<String, String> descriptions = new HashMap<String, String>();
    for (int col = 0; col < columns.length; col++) {
      for (int row = 0; row < columns[col].size(); row++) {
        IdentityElement identityElement =
          (IdentityElement) columns[col].get(row);
        MetaInfo metaInfo = identityElement.getMetaInfo();
        if ((metaInfo != null) && (metaInfo.getDescription() != null)) {
          descriptions.put(identityElement.getId(), metaInfo.getDescription());
          continue;
        }

        if (identityElement instanceof Entry) {
          Entry entry = (Entry) identityElement;
          PortletEntry pentry =
            (PortletEntry) Registry.getEntry(Registry.PORTLET, entry
              .getParent());
          if ((pentry != null) && (pentry.getDescription() != null)) {
            descriptions.put(entry.getId(), pentry.getDescription());
            continue;
          }

          descriptions.put(entry.getId(), entry.getParent());
          continue;
        }

        // if (identityElement instanceof Reference)
        // {
        // description.put(identityElement.getId(),
        // Localization.getString(rundata, "CUSTOMIZER_REF_DEFAULTTITLE"));
        // continue;
        // }

        // Let's make sure their is a title
        // description.put(identityElement.getId(),
        // Localization.getString(rundata, "CUSTOMIZER_NOTITLESET"));
        // descriptions.put(identityElement.getId(), "説明なし");
      }
    }
    // context.put("titles", titles);
    // context.put("descriptions", descriptions);
    // context.put("action", "controllers.MultiColumnControllerAction");
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected static List<?>[] buildColumns(Portlets set, int colNum) {
    // normalize the constraints and calculate max num of rows needed
    Iterator<?> iterator = set.getEntriesIterator();
    int row = 0;
    int col = 0;
    int rowNum = 0;
    while (iterator.hasNext()) {
      IdentityElement identityElement = (IdentityElement) iterator.next();

      Layout layout = identityElement.getLayout();

      if (layout != null) {
        for (int p = 0; p < layout.getParameterCount(); p++) {
          Parameter prop = layout.getParameter(p);

          try {
            if (prop.getName().equals("row")) {
              row = Integer.parseInt(prop.getValue());
              if (row > rowNum) {
                rowNum = row;
              }
            } else if (prop.getName().equals("column")) {
              col = Integer.parseInt(prop.getValue());
              if (col > colNum) {
                prop.setValue(String.valueOf(col % colNum));
              }
            }
          } catch (Exception e) {
            // ignore any malformed layout properties
          }
        }
      }
    }

    int sCount = set.getEntryCount() + set.getPortletsCount();
    row = (sCount / colNum) + 1;
    if (row > rowNum) {
      rowNum = row;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Controller customize colNum: "
        + colNum
        + " rowNum: "
        + rowNum);
    }
    // initialize the result position table and the work list
    List[] table = new List[colNum];
    List filler = Collections.nCopies(rowNum + 1, null);
    for (int i = 0; i < colNum; i++) {
      table[i] = new ArrayList();
      table[i].addAll(filler);
    }

    List<IdentityElement> work = new ArrayList<IdentityElement>();

    // position the constrained elements and keep a reference to the
    // others
    for (int i = 0; i < set.getEntryCount(); i++) {
      addElement(set.getEntry(i), table, work, colNum);
    }

    // Add references
    for (int i = 0; i < set.getReferenceCount(); i++) {
      addElement(set.getReference(i), table, work, colNum);
    }

    // insert the unconstrained elements in the table
    Iterator<IdentityElement> i = work.iterator();
    for (row = 0; row < rowNum; row++) {
      for (col = 0; i.hasNext() && (col < colNum); col++) {
        if (table[col].get(row) == null) {
          if (logger.isDebugEnabled()) {
            logger.debug("Set portlet at col " + col + " row " + row);
          }
          table[col].set(row, i.next());
        }
      }
    }

    // now cleanup any remaining null elements
    for (int j = 0; j < table.length; j++) {
      if (logger.isDebugEnabled()) {
        logger.debug("Column " + j);
      }
      i = table[j].iterator();
      while (i.hasNext()) {
        Object obj = i.next();
        if (logger.isDebugEnabled()) {
          logger.debug("Element " + obj);
        }
        if (obj == null) {
          i.remove();
        }

      }
    }

    return table;
  }

  /**
   * Add an element to the "table" or "work" objects. If the element is
   * unconstrained, and the position is within the number of columns, then the
   * element is added to "table". Othewise the element is added to "work"
   *
   * @param element
   *          to add
   * @param table
   *          of positioned elements
   * @param work
   *          list of un-positioned elements
   * @param columnCount
   *          Number of colum
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected static void addElement(IdentityElement element, List[] table,
      List<IdentityElement> work, int columnCount) {
    Layout layout = element.getLayout();
    int row = -1;
    int col = -1;

    if (layout != null) {
      try {
        for (int p = 0; p < layout.getParameterCount(); p++) {
          Parameter prop = layout.getParameter(p);

          if (prop.getName().equals("row")) {
            row = Integer.parseInt(prop.getValue());
          } else if (prop.getName().equals("column")) {
            col = Integer.parseInt(prop.getValue());
          }
        }
      } catch (Exception e) {
        // ignore any malformed layout properties
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Constraints col " + col + " row " + row);
    }
    if ((row >= 0) && (col >= 0) && (col < columnCount)) {
      table[col].set(row, element);
    } else {
      if (layout != null) {
        // We got here because the column, as defined in the layout,
        // is greater then the numner of columns. This usually
        // happens when the number of column has been decreased.
        // Delete the offending layout. It may be recreated with
        // the correct values.
        element.setLayout(null);
        layout = null;
      }
      work.add(element);
    }
  }
}
