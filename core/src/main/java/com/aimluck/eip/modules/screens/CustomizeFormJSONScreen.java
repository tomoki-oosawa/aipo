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
package com.aimluck.eip.modules.screens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.IdentityElement;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.Reference;
import org.apache.jetspeed.om.profile.psml.PsmlControl;
import org.apache.jetspeed.om.profile.psml.PsmlController;
import org.apache.jetspeed.om.profile.psml.PsmlEntry;
import org.apache.jetspeed.om.profile.psml.PsmlLayout;
import org.apache.jetspeed.om.profile.psml.PsmlParameter;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.security.portlets.PortletWrapper;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.idgenerator.JetspeedIdGenerator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALApplication;
import com.aimluck.eip.http.HttpServletRequestLocator;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.social.ALApplicationService;
import com.aimluck.eip.services.social.model.ALApplicationGetRequest;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.CustomizeUtils;

/**
 * ToDoをJSONデータとして出力するクラスです。 <br />
 *
 */
public class CustomizeFormJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(CustomizeFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();
    try {
      String mode = rundata.getParameters().get("mode");
      if ("add".equals(mode)) {
        doAdd(rundata, context);
      }
      if ("delete".equals(mode)) {
        doDelete(rundata, context);
      }
      if ("layout".equals(mode)) {
        doLayout(rundata, context);
      }
      if ("page_title".equals(mode)) {
        doPageTitleEdit(rundata, context);
      }
    } catch (Exception e) {
      logger.error("CustomizeFormJSONScreen.getJSONString", e);
    }
    return result;
  }

  public void doPageTitleEdit(RunData rundata, Context context)
      throws Exception {
    // maintainUserSelections(rundata);
    // Map<String, PortletEntry> userSelections =
    // CustomizeUtils.getUserSelections(rundata);
    // String[] pnames = new String[userSelections.size()];
    // userSelections.keySet().toArray(pnames);
    //
    // // Create a ClearPortletControl
    // Control ctrl = new PsmlControl();
    // ctrl.setName("ClearPortletControl");
    //
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    Profile profile = jdata.getProfile();

    String pid = rundata.getParameters().get("portlet_id");
    Portlets portlets = profile.getDocument().getPortletsById(pid);

    setController(rundata, context, portlets);

    editPageTitle(rundata, context);

    // 理由等 ：追加したポートレットを PSML に保存する．
    if (portlets != null) {
      doSaveAddAction(rundata, context, portlets);
    }

    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();
    customizationState.setAttribute("customize-mode", "layout");
  }

  private void editPageTitle(RunData rundata, Context context) {
    String pid = rundata.getParameters().get("portlet_id");
    Portlets[] portletArrays =
      (((JetspeedRunData) rundata).getProfile().getDocument().getPortlets())
        .getPortletsArray();
    String pageTitle = rundata.getParameters().getString("pageTitle");
    if (pageTitle != null
      && pageTitle.length() > 0
      && !"マイページ".equals(pageTitle)) {
      for (Portlets p : portletArrays) {
        if (p.getId().equals(pid)) {
          MetaInfo info = p.getMetaInfo();
          if (info != null) {
            info.setTitle(pageTitle);
          }
          p.setTitle(pageTitle);
        }
      }
    }
  }

  /** Add new portlets in the customized set */
  public void doAdd(RunData rundata, Context context) throws Exception {

    maintainUserSelections(rundata);
    Map<String, PortletEntry> userSelections =
      CustomizeUtils.getUserSelections(rundata);
    String[] pnames = new String[userSelections.size()];
    userSelections.keySet().toArray(pnames);

    // Create a ClearPortletControl
    Control ctrl = new PsmlControl();
    ctrl.setName("ClearPortletControl");

    JetspeedRunData jdata = (JetspeedRunData) rundata;
    Profile profile = jdata.getProfile();

    String pid = rundata.getParameters().get("portlet_id");
    Portlets portlets = profile.getDocument().getPortletsById(pid);

    setController(rundata, context, portlets);

    if (pnames != null) {

      boolean addIt;
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
        // アクセス権限のチェック
        if (ALEipUtils.getHasAuthority(
          rundata,
          context,
          ALAccessControlConstants.VALUE_ACL_INSERT)) {
          if ((entry != null) && (portlets != null)) {
            addIt = true;
            if (addIt) {
              Entry p = new PsmlEntry();
              p.setParent(isGadgets ? "GadgetsTemplate" : pnames[i]);
              p.setId(JetspeedIdGenerator.getNextPeid());

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
                String moduleId =
                  String.valueOf(ALApplicationService.getNextModuleId());
                p3.setValue(moduleId);
                p.addParameter(p3);
                p.setId(moduleId);
              }
              portlets.addEntry(p);
            }
          }
        }
      }
    }
    // --------------------------------------------------------------------------

    editPageTitle(rundata, context);

    // 理由等 ：追加したポートレットを PSML に保存する．
    if (portlets != null) {
      doSaveAddAction(rundata, context, portlets);
    }

    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();
    customizationState.setAttribute("customize-mode", "layout");
  }

  public void doDelete(RunData rundata, Context context) {
    JetspeedRunData jdata = (JetspeedRunData) rundata;

    // アクセス権限のチェック
    ALEipUtils.checkAclPermissionForCustomize(
      rundata,
      context,
      ALAccessControlConstants.VALUE_ACL_DELETE);

    String portletId = rundata.getParameters().get("js_peid");
    Portlets portlets = jdata.getProfile().getDocument().getPortlets();
    Portlets[] portletList = portlets.getPortletsArray();
    if (portletList == null) {
      return;
    }

    Profile profile = jdata.getProfile();
    List<PortletEntry> normalPortlets =
      CustomizeUtils.buildAllPortletList(
        rundata,
        profile.getMediaType(),
        new ArrayList<PortletEntry>());
    List<String> editablePortlets = new ArrayList<String>();
    for (PortletEntry entry : normalPortlets) {
      editablePortlets.add(entry.getName());
    }

    try {
      int length = portletList.length;
      int timelineCount = 0;

      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }
        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          String parent = entries[j].getParent();
          if (parent.equals("Timeline")) {
            timelineCount++;
          }
        }
      }

      search: for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          String parent = entries[j].getParent();
          if (entries[j].getId().equals(portletId)
            && (editablePortlets.contains(entries[j].getParent()) || parent
              .startsWith("GadgetsTemplate"))
            && (!parent.equals("Timeline") || timelineCount > 1)) {
            PortletWrapper wrapper =
              (PortletWrapper) PortletFactory.getPortlet(entries[j]);
            if (wrapper != null) {
              portletList[i].removeEntry(j);
              break search;
            }
          }
        }
      }
      profile.store();
    } catch (Exception e) {
      // probably got wrong coordinates
      logger.error(
        "MultiColumnControllerAction: Probably got wrong coordinates",
        e);
    }

    jdata.getUser().removeTemp("js_peid");
    rundata.setRedirectURI("/aipo/portal");
  }

  public void doLayout(RunData rundata, Context context) {

    JetspeedRunData jdata = (JetspeedRunData) rundata;

    // アクセス権限のチェック
    ALEipUtils.checkAclPermissionForCustomize(
      rundata,
      context,
      ALAccessControlConstants.VALUE_ACL_UPDATE);

    Portlets portlets = jdata.getProfile().getDocument().getPortlets();
    Portlets[] portletList = portlets.getPortletsArray();
    if (portletList == null) {
      return;
    }

    Profile profile = jdata.getProfile();
    List<PortletEntry> normalPortlets =
      CustomizeUtils.buildAllPortletList(
        rundata,
        profile.getMediaType(),
        new ArrayList<PortletEntry>());
    List<String> editablePortlets = new ArrayList<String>();
    for (PortletEntry entry : normalPortlets) {
      editablePortlets.add(entry.getName());
    }

    try {
      int length = portletList.length;

      for (int i = 0; i < length; i++) {
        Entry[] entries = portletList[i].getEntriesArray();
        if (entries == null || entries.length <= 0) {
          continue;
        }

        int ent_length = entries.length;
        for (int j = 0; j < ent_length; j++) {
          String parent = entries[j].getParent();
          if (editablePortlets.contains(parent)
            || parent.startsWith("GadgetsTemplate")) {
            String pid = entries[j].getId();
            int col = rundata.getParameters().getInt(pid + "_col", -1);
            int row = rundata.getParameters().getInt(pid + "_row", -1);
            if (col != -1 && row != -1) {
              setPosition(entries[j], col, row);
            }
          }
        }
      }
      profile.store();
    } catch (Exception e) {
      // probably got wrong coordinates
      logger.error(
        "MultiColumnControllerAction: Probably got wrong coordinates",
        e);
    }
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

  public void doSaveAddAction(RunData data, Context context, Portlets portlets) {
    setPageLayout(data, context, portlets);
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
      ((JetspeedRunData) data).getProfile().store();

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
  private void setPageLayout(RunData rundata, Context context, Portlets portlets) {

    HttpServletRequest request = HttpServletRequestLocator.get();

    List<?>[] columns =
      CustomizeUtils.buildCustomizeColumns(rundata, context, portlets);

    String controllerName = portlets.getController().getName();
    int colNum = 2;
    if (controllerName.startsWith("One")) {
      colNum = 1;
    } else if (controllerName.startsWith("Three")) {
      colNum = 3;
    }
    Portlets set = portlets;

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
        columns = CustomizeUtils.buildColumns(set, colNum);
      }

    } else {
      if (logger.isDebugEnabled()) {
        logger.debug("MultiCol: rebuilding columns ");
      }
      columns = CustomizeUtils.buildColumns(set, colNum);
    }
    if (request != null) {
      request.setAttribute("customize-columns", columns);
    }
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
      }
    }
  }

  /** Updates the customized portlet entry */
  private void setController(RunData rundata, Context context, Portlets portlets) {
    ALEipUtils.checkAclPermissionForCustomize(
      rundata,
      context,
      ALAccessControlConstants.VALUE_ACL_UPDATE);

    try {
      String controller = rundata.getParameters().getString("controller");

      if (controller != null) {
        PortletController pc = PortalToolkit.getController(controller);
        if (pc != null) {

          Controller c = portlets.getController();
          if (c == null) {
            c = new PsmlController();
          }
          c.setName(controller);
          portlets.setController(c);

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
    } catch (Exception e) {
      logger.error("CustomizeFormJSONScreen.setController", e);
    }
  }
}
