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
package com.aimluck.eip.modules.parameters;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.om.registry.Parameter;
import org.apache.jetspeed.om.registry.PortletInfoEntry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.PortalResource;
import org.apache.turbine.services.localization.Localization;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Returns list box control populated with registry entries from selected
 * registry.
 * <p>
 * Options:
 * <UL>
 * <LI><code>registry</code>
 * [Portlet|Security|PortletControl|PortletController|Skin
 * |security|MediaType|Client] - registry name</LI>
 * <LI><code>sort</code> [<strong>true</strong>|false] - return sorted list of
 * items</LI>
 * <LI><code>select-hidden</code> [<strong>false</strong>|true] - allow multiple
 * selections</LI>
 * <LI><code>null-if-empty</code> [<strong>true</strong>|false] - do not return
 * a select control if item list is empty</LI>
 * <LI><code>set-label</code> [<strong>false</strong>|true] - put label in front
 * of the list box based on the registry name</LI>
 * <LI><code>disabled-if-wml</code> [<strong>false</strong>|true] - set disabled
 * attribute for the list box if using wml</LI>
 * <LI><code>select-if-simple</code> [<strong>false</strong>|true] - select only
 * entries with parameter "simple" set to true</LI>
 * </UL>
 * 
 * @author <a href="morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: RegistryEntryListBox.java,v 1.8 2004/02/23 03:01:20 jford Exp $
 */

public class RegistryEntryListBox extends VelocityParameterPresentationStyle {
  public static final String OPTION_REGISTRY = "registry";

  public static final String OPTION_SORT = "sort";

  public static final String OPTION_SELECT_HIDDEN = "select-hidden";

  public static final String OPTION_NULL_IF_EMPTY = "null-if-empty";

  public static final String OPTION_SET_LABEL = "set-label";

  public static final String OPTION_DISABLED_IF_WML = "disabled-if-wml";

  public static final String OPTION_SELECT_IF_SIMPLE = "select-if-simple";

  /**
   * Put custom objects in the velocity context
   * 
   * @param data
   * @param name
   * @param value
   * @param parms
   * @param context
   */
  @SuppressWarnings({ "deprecation", "rawtypes" })
  @Override
  public void buildContext(RunData data, String name, String value, Map parms,
      Context context) {
    // Initialize options
    JetspeedRunData jdata = (JetspeedRunData) data;
    String mediaType = jdata.getProfile().getMediaType();
    String regName = (String) getParm(OPTION_REGISTRY, Registry.PORTLET);
    boolean sort =
      (Boolean.valueOf((String) getParm(OPTION_SORT, "true"))).booleanValue();
    boolean selectHidden =
      (Boolean.valueOf((String) getParm(OPTION_SELECT_HIDDEN, "false")))
        .booleanValue();
    String nullIfEmpty = (String) getParm(OPTION_NULL_IF_EMPTY, "true");
    boolean setLabel =
      (Boolean.valueOf((String) getParm(OPTION_SET_LABEL, "false")))
        .booleanValue();
    boolean disabledIfWML =
      (Boolean.valueOf((String) getParm(OPTION_DISABLED_IF_WML, "false")))
        .booleanValue();
    boolean selectIfSimple =
      (Boolean.valueOf((String) getParm(OPTION_SELECT_IF_SIMPLE, "false")))
        .booleanValue();
    String defaultEntry = null;

    // Iterate thru entries from selected registry
    List<RegistryEntry> list = new ArrayList<RegistryEntry>();
    for (Iterator<?> i = Registry.get(regName).listEntryNames(); i.hasNext();) {
      RegistryEntry entry = Registry.getEntry(regName, (String) i.next());
      boolean selected = false;
      selected =
        JetspeedSecurity.checkPermission(
          (JetspeedUser) data.getUser(),
          new PortalResource(entry),
          "customize");
      if (selected && !selectHidden) {
        selected = !entry.isHidden();
      }
      if (selected && (entry instanceof PortletInfoEntry)) {
        selected = ((PortletInfoEntry) entry).hasMediaType(mediaType);
      }
      if (selected && selectIfSimple) {
        Parameter simpleParam =
          ((PortletInfoEntry) entry).getParameter("simple");
        if (simpleParam != null) {
          selected = Boolean.valueOf(simpleParam.getValue()).booleanValue();
        } else {
          selected = false;
        }
      }
      if (selected) {
        list.add(entry);
      }
    }

    // Perform optional sort of list box items
    if (sort) {
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
    }

    // Set list box label
    String label = null;
    if (regName.equals(Registry.PORTLET)) {
      label = Localization.getString(data, "CUSTOMIZER_PORTLET");
    } else if (regName.equals(Registry.SECURITY)) {
      label = Localization.getString(data, "CUSTOMIZER_SECURITY_REF");
      // SecurityReference defaultRef = PortalToolkit.getDefaultSecurityRef(
      // ((JetspeedRunData) data).getCustomizedProfile());
      // if (defaultRef != null)
      // {
      // defaultEntry = defaultRef.getParent();
      // }
    } else if (regName.equals(Registry.MEDIA_TYPE)) {
      label = Localization.getString(data, "CUSTOMIZER_MEDIATYPE");
    } else if (regName.equals(Registry.PORTLET_CONTROLLER)) {
      label = Localization.getString(data, "CUSTOMIZER_LAYOUT");
    } else if (regName.equals(Registry.PORTLET_CONTROL)) {
      label = Localization.getString(data, "CUSTOMIZER_DECORATION");
    } else if (regName.equals(Registry.CLIENT)) {
      label = "Client";
    } else {
      label = "";
    }
    context.put("entries", list);
    context.put("nullIfEmpty", nullIfEmpty);
    if (setLabel) {
      context.put("label", label);
    }
    if (disabledIfWML && mediaType.equalsIgnoreCase("wml")) {
      context.put("disabled", "disabled");
    }
    context.put("defaultEntry", defaultEntry);
  }

}