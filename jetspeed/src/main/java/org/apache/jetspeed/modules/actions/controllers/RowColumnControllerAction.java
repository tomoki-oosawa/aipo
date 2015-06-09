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

package org.apache.jetspeed.modules.actions.controllers;

//Jetspeed Stuff
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
// Java stuff
import java.util.Vector;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.psml.PsmlLayout;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.AutoProfile;
import org.apache.turbine.modules.ActionLoader;
// Turbine stuff
//import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
// Velocity Stuff
import org.apache.velocity.context.Context;

/**
 * This action builds a context suitable for controllers handlings simple sorted
 * lists of portlets
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 */
public class RowColumnControllerAction extends VelocityControllerAction {

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(RowColumnControllerAction.class.getName());

  /**
   * Subclasses must override this method to provide default behavior for the
   * portlet action
   */
  @Override
  protected void buildNormalContext(PortletController controller,
      Context context, RunData rundata) {
    // retrieve the size for each of the element
    String sizes = controller.getConfig().getInitParameter("sizes");
    context.put("sizes", getCellSizes(sizes));
  }

  /**
   * Parses the size config info and returns a list of size values for the
   * current set
   *
   * @param sizeList
   *          java.lang.String a comma separated string a values
   * @return a List of values
   */
  public static List getCellSizes(String sizelist) {
    List list = new Vector();

    if (sizelist != null) {
      StringTokenizer st = new StringTokenizer(sizelist, ",");
      while (st.hasMoreTokens()) {
        list.add(st.nextToken());
      }
    }

    return list;
  }

  /**
   * Adds a "pane" portlet object in the context which represents the currently
   * selected pane
   */
  @Override
  protected void buildCustomizeContext(PortletController controller,
      Context context, RunData rundata) {
    super.buildCustomizeContext(controller, context, rundata);

    JetspeedRunData jdata = (JetspeedRunData) rundata;
    PortletSet set = (PortletSet) jdata.getCustomized();

    Portlets portlets =
      jdata.getCustomizedProfile().getDocument().getPortletsById(set.getID());

    List plist = new ArrayList();
    List work = new ArrayList();
    List filler =
      Collections.nCopies(portlets.getPortletsCount()
        + portlets.getEntryCount(), null);
    plist.addAll(filler);

    for (int i = 0; i < portlets.getPortletsCount(); i++) {
      Portlets p = portlets.getPortlets(i);
      if (logger.isDebugEnabled()) {
        logger.debug("RowColumnControllerAction: processing portlet: "
          + p.getTitle());
      }
      Layout layout = p.getLayout();
      if (layout == null) {
        // Pane should always have a layout with correct position
        if (logger.isDebugEnabled()) {
          logger
            .debug("RowColumnControllerAction: no layout, creating a new one");
        }
        layout = new PsmlLayout();
        layout.setPosition(i);
        p.setLayout(layout);
      }
      if (layout != null) {
        try {
          int pos = (int) layout.getPosition();
          if (logger.isDebugEnabled()) {
            logger.debug("RowColumnControllerAction: layout has position: "
              + pos);
          }
          if (pos >= 0 && pos < plist.size()) {
            plist.set(pos, p);
          } else {
            work.add(p);
          }
        } catch (Exception e) {
          logger.error("Layout error", e);
          layout.setPosition(-1);
          work.add(p);
        }
      } else {
        work.add(p);
      }
    }

    for (int i = 0; i < portlets.getEntryCount(); i++) {
      Entry p = portlets.getEntry(i);
      Layout layout = p.getLayout();
      if (layout != null) {
        try {
          int pos = (int) layout.getPosition();
          if (pos >= 0) {
            plist.set(pos, p);
          } else {
            work.add(p);
          }
        } catch (Exception e) {
          layout.setPosition(-1);
          work.add(p);
        }
      } else {
        work.add(p);
      }
    }

    Iterator i = work.iterator();
    for (int idx = 0; idx < plist.size(); idx++) {
      if (plist.get(idx) == null) {
        if (i.hasNext()) {
          plist.set(idx, i.next());
        } else {
          plist.remove(idx);
        }
      }
    }

    Map titles = new HashMap();
    i = plist.iterator();
    while (i.hasNext()) {
      Object obj = i.next();

      if (obj instanceof Portlets) {
        Portlets entry = (Portlets) obj;
        if ((entry.getMetaInfo() != null)
          && (entry.getMetaInfo().getTitle() != null)) {
          titles.put(entry.getId(), entry.getMetaInfo().getTitle());
        }
      } else {
        Entry entry = (Entry) obj;
        if ((entry.getMetaInfo() != null)
          && (entry.getMetaInfo().getTitle() != null)) {
          titles.put(entry.getId(), entry.getMetaInfo().getTitle());
        } else {
          RegistryEntry pentry =
            Registry.getEntry(Registry.PORTLET, entry.getParent());

          if (pentry != null) {
            titles.put(entry.getId(), pentry.getTitle());
          }
        }
      }
    }

    context.put("portlets", plist);
    context.put("titles", titles);

    /**
     * Make a list of all used portlets available thru the 'runs' reference
     * ------
     * -------------------------------------------------------------------- last
     * modified: 10/31/01 Andreas Kempf, Siemens ICM S CP PE, Munich mailto:
     * A.Kempf@web.de
     */
    context.put("runs", AutoProfile.getPortletList(rundata));
    // --------------------------------------------------------------------------
  }

  /**
   * Cancel the current customizations. If this was the last customization on
   * the stack, then return the user to the home page.
   */
  @Override
  public void doCancel(RunData data, Context context) {
    ((JetspeedRunData) data).setCustomized(null);

    if (((JetspeedRunData) data).getCustomized() == null) {
      try {
        ActionLoader.getInstance().exec(data, "controls.EndCustomize");
      } catch (Exception e) {
        logger.error("Unable to load action controls.EndCustomize ", e);
      }
    }
  }

  public void doSave(RunData data, Context context) {
    doApply(data, context);
  }

  public void doApply(RunData data, Context context) {
    // move one level back in customization
    ((JetspeedRunData) data).setCustomized(null);

    // if we are all done customization
    if (((JetspeedRunData) data).getCustomized() == null) {
      // save the edit profile and make it current
      try {
        ((JetspeedRunData) data).getCustomizedProfile().store();
      } catch (Exception e) {
        logger.error("Unable to save profile ", e);
      }

      try {
        ActionLoader.getInstance().exec(data, "controls.EndCustomize");
      } catch (Exception e) {
        logger.error("Unable to load action controls.EndCustomize ", e);
      }
    }
  }

  /**
   * Remove a pane from the current set This method expects the following
   * parameters - paneid: the id a the pane to modify within the current profile
   * - position: the position of the component to delete
   */
  public void doDelete(RunData data, Context context) throws Exception {
    JetspeedRunData jdata = (JetspeedRunData) data;
    PortletSet customizedSet = (PortletSet) jdata.getCustomized();
    int position = data.getParameters().getInt("position", -1);
    Profile profile = jdata.getCustomizedProfile();

    // ADDED for WML
    // boolean isWML = (profile.getMediaType().equalsIgnoreCase("wml"));

    if (customizedSet == null) {
      return;
    }

    if (position > -1) {
      Portlets set =
        profile.getDocument().getPortletsById(customizedSet.getID());
      if (set != null) {
        // first try explicit portlets position
        for (int i = 0; i < set.getPortletsCount(); i++) {
          Portlets p = set.getPortlets(i);
          Layout layout = p.getLayout();
          //
          // if (layout == null)
          // {
          // Layout nl = new Layout ();
          // nl.setPosition (String.valueOf(i));
          //
          // }
          if ((layout != null) && (layout.getPosition() != -1)) {

            int lpos = (int) layout.getPosition();
            if (lpos == position) {
              set.removePortlets(i);

              updateLayoutPositions(set);

              // MODIFIED: Save changes for wml profiles
              // if (isWML)
              // doSave(data, null);

              return;
            }
          }
        }

        // try explicit entry position
        for (int i = 0; i < set.getEntryCount(); i++) {
          Entry p = set.getEntry(i);
          Layout layout = p.getLayout();

          if ((layout != null) && (layout.getPosition() != -1)) {
            int lpos = (int) layout.getPosition();

            if (lpos == position) {
              set.removeEntry(i);

              // MODIFIED: Save changes for wml profiles
              // if (isWML)
              // doSave(data, null);
              return;
            }
          }
        }

        // else use implicit position
        if (position < set.getPortletsCount()) {
          set.removePortlets(position);

          // MODIFIED: Save changes for wml profiles
          // if (isWML)
          // doSave(data, null);
          return;
        }

        if (position < set.getEntryCount()) {
          set.removeEntry(position);

          // MODIFIED: Save changes for wml profiles
          // if (isWML)
          // doSave(data, null);
          return;
        }
      }
    }

  }

  /**
   * Updates the layout position based on physical order within the resorted
   * portlet list. Assures that layout position is always consecutive and within
   * bounds.
   *
   * @param set
   */
  private void updateLayoutPositions(Portlets set) {
    // Load the panes into a list
    List list = new ArrayList();
    for (int i = 0; i < set.getPortletsCount(); i++) {
      Portlets pane = set.getPortlets(i);
      list.add(pane);
    }

    // Sort list using the current layout position
    Collections.sort(list, new Comparator() {
      @Override
      public int compare(Object pane1, Object pane2) {
        Long pos1 = new Long(((Portlets) pane1).getLayout().getPosition());
        Long pos2 = new Long(((Portlets) pane2).getLayout().getPosition());
        return pos1.compareTo(pos2);
      }
    });

    // Update the layout position based on the physical order within the sorted
    // list
    int position = 0;
    for (Iterator iter = list.iterator(); iter.hasNext();) {
      Portlets pane = (Portlets) iter.next();
      Layout layout = pane.getLayout();
      layout.setPosition(position++);
    }
  }

  /**
   * Move a component up within the pane This method expects the following
   * parameters - paneid: the id a the pane to modify within the current profile
   * - position: move the component which occupies this position
   */
  public void doUp(RunData data, Context context) throws Exception {
    doMove(data, context, true);
  }

  /**
   * Move a component down within the pane This method expects the following
   * parameters - paneid: the id a the pane to modify within the current profile
   * - position: move the component which occupies this position
   */
  public void doDown(RunData data, Context context) throws Exception {
    doMove(data, context, false);
  }

  /**
   * Move a component within the pane This method expects the following
   * parameters - paneid: the id a the pane to modify within the current profile
   * - position: move the component which occupies this position The moveUp
   * boolean determines the direction of the move
   */
  public void doMove(RunData data, Context context, boolean moveUp)
      throws Exception {
    JetspeedRunData jdata = (JetspeedRunData) data;
    PortletSet customizedSet = (PortletSet) jdata.getCustomized();
    int position = data.getParameters().getInt("position", -1);
    Profile profile = jdata.getCustomizedProfile();

    // ADDED for WML
    // boolean isWML = (profile.getMediaType().equalsIgnoreCase("wml"));

    if (customizedSet == null) {
      return;
    }

    if (position > -1) {
      int target = -1;

      Portlets set =
        profile.getDocument().getPortletsById(customizedSet.getID());
      Layout targetLayout = null;
      Layout baseLayout = null;

      if (set != null) {
        // check if we can possibly move as requested and calculate
        // target position
        if (moveUp) {
          if ((position >= set.getPortletsCount())
            && (position >= set.getEntryCount())) {
            return;
          }
          target = position + 1;
        } else {
          if (position == 0) {
            return;
          }
          target = position - 1;
        }

        // first find objects at explicit portlets position
        for (int i = 0; i < set.getPortletsCount(); i++) {
          if ((targetLayout != null) && (baseLayout != null)) {
            break;
          }

          Portlets p = set.getPortlets(i);
          Layout layout = p.getLayout();
          if ((layout != null) && (layout.getPosition() != -1)) {
            int lpos = (int) layout.getPosition();
            if ((baseLayout == null) && (lpos == position)) {
              baseLayout = layout;
            }

            if ((targetLayout == null) && (lpos == target)) {
              targetLayout = layout;
            }
          }
        }

        // try explicit entry position
        for (int i = 0; i < set.getEntryCount(); i++) {
          if ((targetLayout != null) && (baseLayout != null)) {
            break;
          }

          Entry p = set.getEntry(i);
          Layout layout = p.getLayout();
          if ((layout != null) && (layout.getPosition() != -1)) {
            int lpos = (int) layout.getPosition();
            if ((baseLayout == null) && (lpos == position)) {
              baseLayout = layout;
            }

            if ((targetLayout == null) && (lpos == target)) {
              targetLayout = layout;
            }
          }
        }

        // else use implicit position
        if (baseLayout == null) {
          if (position < set.getPortletsCount()) {
            Portlets p = set.getPortlets(position);
            if (p.getLayout() == null) {
              p.setLayout(new PsmlLayout());
            }
            baseLayout = p.getLayout();
          }

          if (position < set.getEntryCount()) {
            Entry p = set.getEntry(position);
            if (p.getLayout() == null) {
              p.setLayout(new PsmlLayout());
            }
            baseLayout = p.getLayout();
          }
        }

        if (targetLayout == null) {
          if (target < set.getPortletsCount()) {
            Portlets p = set.getPortlets(target);
            if (p.getLayout() == null) {
              p.setLayout(new PsmlLayout());
            }
            targetLayout = p.getLayout();
          }

          if (target < set.getEntryCount()) {
            Entry p = set.getEntry(target);
            if (p.getLayout() == null) {
              p.setLayout(new PsmlLayout());
            }
            targetLayout = p.getLayout();
          }
        }

        // we should now have found both baseLayout and targetLayout, swap
        // their positions using explicit positioning

        if ((baseLayout == null) || (targetLayout == null)) {
          return;
        }

        baseLayout.setPosition(target);
        targetLayout.setPosition(position);
      }
    }

    // MODIFIED: Save changes for wml profiles
    // if (isWML)
    // doSave(data, null);
  }
}
