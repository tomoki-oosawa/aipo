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
package com.aimluck.eip.portal.controls;

// Turbine stuff
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.PanedPortletController;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletState;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.util.template.JetspeedLinkFactory;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.util.ALEipUtils;

/**
 * A Velocity based portlet control designed for handling a PortletSet child
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 *
 */
public class ALVelocityPortletSetControl extends ALVelocityPortletControl {

  private static final long serialVersionUID = 1054029676869610792L;

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALVelocityPortletSetControl.class.getName());

  /**
   * This method adds the control specific objects to the context
   *
   * @param rundata
   *          the RunData object for this request
   * @param context
   *          the Context used by the template
   */
  @Override
  public void buildContext(RunData rundata, Context context) {
    ALOrgUtilsService.assignCommonContext(context);
    if (Boolean.parseBoolean((String) rundata.getSession().getAttribute(
      "changeToPc"))) { // PC表示切り替え用
      context.put("client", ALEipUtils.getClient(rundata));
    }
    if (getPortlet() instanceof PortletSet) {

      PortletSet set = (PortletSet) getPortlet();

      Collection<PortletTab> tabs = getTabs(set, rundata, context);

      // remove "個人設定"
      for (Iterator<PortletTab> i = tabs.iterator(); i.hasNext();) {
        PortletTab tab = i.next();
        if (tab.getTitle().toString().equals("個人設定")) {
          i.remove();
        }
        if (tab.getTitle().toString().equals("システム管理")) {
          i.remove();
        }

      }

      context.put("tabs", tabs);
      String mypageId = "";
      for (Enumeration<?> en = set.getPortlets(); en.hasMoreElements();) {
        Portlet p = (Portlet) en.nextElement();
        if ("マイページ".equals(p.getTitle())) {
          mypageId = p.getID();
        }
      }
      context.put("mypageId", mypageId);
      context.put("globalPortlets", ALEipUtils.getGlobalPortlets(rundata));
    }
  }

  /**
   * Populate a list of tabs that should be displayed by this control. Each tab
   * represents a child portlet.
   *
   * This method works best if the child of this control is a PortletSet whose
   * controller implements the PanedPortletController interface.
   *
   * @param portlet
   *          the base portlet to explore for children @
   */
  private Collection<PortletTab> getTabs(PortletSet portlets, RunData rundata,
      Context context) {
    TreeSet<PortletTab> tabs =
      new TreeSet<PortletTab>(new PortletTabComparator());
    PanedPortletController controller = null;

    // if portlet is a PortletSet, try to retrieve the Controller
    // we need a PanedPortletController to work properly.
    if (portlets.getController() instanceof PanedPortletController) {
      controller = (PanedPortletController) portlets.getController();
    }

    // アクセス権限
    boolean hasAuthority =
      ALEipUtils.getHasAuthority(
        rundata,
        context,
        ALAccessControlConstants.VALUE_ACL_LIST);
    context.put(ALEipConstants.SECURE_ID, rundata.getUser().getTemp(
      ALEipConstants.SECURE_ID));

    String portletId = portlets.getID();

    if (controller != null) {
      String peid =
        rundata.getParameters().getString(controller.getPaneParameter());

      if (portletId.equals(peid)) {
        SessionState state =
          ((JetspeedRunData) rundata).getPortletSessionState(portletId);
        state.setAttribute(JetspeedResources.PATH_PANEID_KEY, null);
      }
    }

    for (Enumeration<?> en = portlets.getPortlets(); en.hasMoreElements();) {
      Portlet p = (Portlet) en.nextElement();
      PortalResource portalResource = new PortalResource(p);
      if (("Activity".equals(p.getName()) || "Message".equals(p.getName()))
        && !portlets.getController().getConfig().getName().equals(
          "MenuController")) {
        continue;
      }

      // Secure the tabs
      JetspeedLink jsLink = null;
      try {
        jsLink = JetspeedLinkFactory.getInstance(rundata);
        portalResource.setOwner(jsLink.getUserName());
        JetspeedLinkFactory.putInstance(jsLink);
      } catch (Exception e) {
        logger.warn("[ALVelocityPortletSetControl]", e);
        portalResource.setOwner(null);
      }
      JetspeedRunData jdata = (JetspeedRunData) rundata;
      boolean hasView =
        JetspeedSecurity.checkPermission(
          (JetspeedUser) jdata.getUser(),
          portalResource,
          JetspeedSecurity.PERMISSION_VIEW);
      if (!hasView) {
        continue;
      }
      // skip any closed portlet
      if ((p instanceof PortletState) && (((PortletState) p).isClosed(rundata))) {
        continue;
      }

      String mstate = p.getAttribute("_menustate", "open", rundata);
      if (mstate.equals("closed")) {
        continue;
      }

      PortletTab tab = new PortletTab();
      tab.setId(p.getID());

      // Handle the portlet title
      String title = null;
      PortletInstance pi = PersistenceManager.getInstance(p, rundata);
      if (pi != null) {
        title = pi.getTitle();
        if (title == null) {
          title = (p.getTitle() != null) ? p.getTitle() : p.getName();
        }
      }
      tab.setTitle(title);

      tab.setPosition(p.getPortletConfig().getPosition());
      if (tabs.contains(tab)) {
        PortletTab lastTab = tabs.last();
        int nextPos = lastTab.getPosition() + 1;
        tab.setPosition(nextPos);
      }

      if (controller != null) {
        tab.setSelected(controller.isSelected(p, rundata));

        if ("IPHONE".equals(ALEipUtils.getClient(rundata))) {
          tab.setLink(jsLink.getPortletById(p.getID()).addQueryData(
            "action",
            "controls.Maximize").toString());
        } else {
          tab.setLink(controller.getPortletURI(p, rundata).toString()
            + "?action=controls.Restore");
        }
      }

      // 修正 ：最大化時とノーマル時のポートレットの表示を切り替え可能にするため，
      // メソッド buildActionList(RunData rundata,Portlet portlet,Context context)
      // を呼ぶように修正した．
      tab.setActions(buildActionList(rundata, p, context));
      tab.setAuthority(hasAuthority);

      tabs.add(tab);
    }
    return tabs;
  }

  /**
   * Utilty class describing a Tab elemnt in the template Velocity Context
   */
  public static class PortletTab {

    private String id = null;

    private final ALStringField title = new ALStringField();

    private boolean selected = false;

    private String link = null;

    private List<PortletAction> actions = null;

    private int position = -1;

    private boolean authority = true;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public ALStringField getTitle() {
      return this.title;
    }

    public void setTitle(String title) {
      this.title.setValue(title);
    }

    public boolean isSelected() {
      return this.selected;
    }

    public void setSelected(boolean selected) {
      this.selected = selected;
    }

    public String getLink() {
      return this.link;
    }

    public void setLink(String link) {
      this.link = link;
    }

    public List<PortletAction> getActions() {
      return (this.actions == null)
        ? new Vector<PortletAction>()
        : this.actions;
    }

    public void setActions(List<PortletAction> actions) {
      this.actions = actions;
    }

    public int getPosition() {
      return position;
    }

    public void setPosition(int pos) {
      position = pos;
    }

    public boolean getAuthority() {
      return authority;
    }

    public void setAuthority(boolean flg) {
      authority = flg;
    }
  }

  /**
   * Used to correctly order tabs based on the position value that is found each
   * PortletTab's parent Portlet's PortletConfig object.
   */
  public static class PortletTabComparator implements Comparator<PortletTab>,
      Serializable {

    /**
     *
     * @param o1
     * @param o2
     * @return
     */
    @Override
    public int compare(PortletTab o1, PortletTab o2) {
      try {
        PortletTab pt1 = o1;
        PortletTab pt2 = o2;
        int pos1 = pt1.getPosition();
        int pos2 = pt2.getPosition();

        if (pos1 < pos2) {
          return -1;
        } else if (pos1 > pos2) {
          return 1;
        } else {
          return 0;
        }
      } catch (ClassCastException e) {
        logger.error("ALVelocityPortletSetControl.compare", e);
        return 0;
      }
    }
  }

}
