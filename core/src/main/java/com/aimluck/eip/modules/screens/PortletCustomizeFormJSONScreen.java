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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import net.sf.json.JSONArray;

import org.apache.jetspeed.om.BaseSecurityReference;
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.om.profile.psml.PsmlSkin;
import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletConfig;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletSkin;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.statemanager.SessionState;
import org.apache.jetspeed.util.MetaData;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * PortletCustomizeをJSONデータとして出力するクラスです。 <br />
 * 
 */
public class PortletCustomizeFormJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(PortletCustomizeFormJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = new JSONArray().toString();

    // get the customization state for this page
    SessionState customizationState =
      ((JetspeedRunData) rundata).getPageSessionState();
    JetspeedRunData jdata = (JetspeedRunData) rundata;
    String peid = jdata.getParameters().getString("js_peid");

    // we should first retrieve the portlet to customize and its parameters
    // definition
    Portlet found = null;
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

    if ((peid == null) || (changeRequested == false)) {
      doCancel(rundata, context);
      return result;
    }

    Profile profile = ((JetspeedRunData) rundata).getCustomizedProfile();
    Entry entry = profile.getDocument().getEntryById(peid);

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

    if (found == null) {
      doCancel(rundata, context);
      return result;
    }

    PortletConfig pc = found.getPortletConfig();
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
      PortletInstance instance = PersistenceManager.getInstance(found, rundata);
      PortletEntry regEntry =
        (PortletEntry) Registry.getEntry(Registry.PORTLET, found.getName());

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
          found.init();
          org.apache.jetspeed.util.PortletSessionState.setPortletConfigChanged(
            found,
            rundata);
        } catch (PortletException e) {
          logger.error("Customizer failed to reinitialize the portlet "
            + found.getName(), e);
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

    return result;
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
}
