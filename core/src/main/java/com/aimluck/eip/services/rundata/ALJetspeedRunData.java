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

package com.aimluck.eip.services.rundata;

// Java classes
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.DefaultJetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.statemanager.SessionState;

import com.aimluck.eip.http.HttpServletRequestLocator;

/**
 *
 */
public class ALJetspeedRunData extends DefaultJetspeedRunData implements
    JetspeedRunData {

  @SuppressWarnings("unused")
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALJetspeedRunData.class.getName());

  @Override
  public Profile getCustomizedProfile() {
    Profile profile = null;
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      try {
        profile = (Profile) request.getAttribute("customize-profile");
      } catch (Throwable ignore) {
        //
      }
    }
    if (profile != null) {
      return profile;
    }

    try {
      ProfileLocator locator = (ProfileLocator) getProfile().clone();

      String editMediaType = getParameters().getString("mtype");
      if (editMediaType != null) {
        locator.setMediaType(editMediaType);
      }

      // get a profile to edit
      profile = (Profile) Profiler.getProfile(locator).clone();
    } catch (Throwable ignore) {
      //
    }
    if (request != null && profile != null) {
      request.setAttribute("customize-profile", profile);
    }

    return profile;

  } // getCustomizedProfile

  /**
   * Set the psml profile being customized.
   *
   * @param profile
   *          The Profile being customized.
   */
  @Override
  public void setCustomizedProfile(Profile profile) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute("customize-profile", profile);
    }
  }

  @Override
  public void cleanupFromCustomization() {
    // get the customization state for this page
    SessionState customizationState = getPageSessionState();

    // customizationState.removeAttribute("customize-stack");
    customizationState.removeAttribute("customize-paneName");
    customizationState.removeAttribute("customize-profile");
    // customizationState.removeAttribute("customize-type");
    customizationState.removeAttribute("customize-columns");
    customizationState.removeAttribute("customize-mode");
    customizationState.removeAttribute("customize-parameters");

    setMode("default");

  } // cleanupFromCustomization

  @Override
  public Portlet getCustomized() {
    Portlet p = null;
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      try {
        p = (Portlet) request.getAttribute("customize-portlet");
      } catch (Throwable ignore) {
        // ignore
      }
    }
    if (p != null) {
      return p;
    }

    SessionState customizationState = getPageSessionState();
    Stack stack = (Stack) customizationState.getAttribute("customize-stack");

    if ((stack != null) && (!stack.empty())) {
      p = (Portlet) stack.peek();
    }

    /**
     * Save the title of this currently selected portlet
     * ------------------------
     * -------------------------------------------------- last modified:
     * 11/06/01 Andreas Kempf, Siemens ICM S CP PE, Munich
     */
    if ((p != null) && (stack.size() > 1)) {
      customizationState.setAttribute("customize-paneName", p.getTitle());
    } else {
      customizationState.setAttribute("customize-paneName", "*");
    }

    if (request != null) {
      request.setAttribute("customize-portlet", p);
    }
    return p;
  }

  @Override
  public void setCustomized(Portlet p) {
    HttpServletRequest request = HttpServletRequestLocator.get();
    if (request != null) {
      request.setAttribute("customize-portlet", p);
    }
    // customization state info is in the page's session state
    SessionState customizationState = getPageSessionState();
    Stack stack = (Stack) customizationState.getAttribute("customize-stack");
    if (stack == null) {
      stack = new Stack();
      customizationState.setAttribute("customize-stack", stack);
    }

    if (p == null) {
      if (!stack.empty()) {
        stack.pop();
      }

      customizationState.setAttribute("customize-paneName", "*");
    } else {
      if (stack.size() > 0) {
        Portlet last = (Portlet) stack.peek();

        if ((last != null)
          && (p.getName().equals(last.getName()))
          && (p.getTitle().equals(last.getTitle()))) {
          // System.out.println ("Portlet already used!!!");
        } else {
          stack.push(p);
        }
      } else {
        stack.push(p);
      }

      /**
       * Save the title of this currently selected portlet
       * ----------------------
       * ---------------------------------------------------- last modified:
       * 11/06/01 Andreas Kempf, Siemens ICM S CP PE, Munich
       */

      customizationState.setAttribute("customize-paneName", p.getTitle());
      customizationState.setAttribute("customize-stack", stack);
    }
  }

}
