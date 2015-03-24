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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletException;
import org.apache.jetspeed.portal.PortletInstance;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.persistence.PersistenceManager;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class UserPrefUpdateJSONScreen extends ALJSONScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(UserPrefUpdateJSONScreen.class.getName());

  @Override
  protected String getJSONString(RunData rundata, Context context)
      throws Exception {
    String result = "";
    JSONObject json;

    try {

      boolean madePsChange = false;
      boolean madePcChange = false;

      String payload = getPayload(rundata);
      JSONObject jsonObject = JSONObject.fromObject(payload);

      if (jsonObject != null) {
        Portlet p = ALEipUtils.getPortlet(rundata, context);
        Profile profile = ((JetspeedRunData) rundata).getProfile();

        PortletInstance instance = PersistenceManager.getInstance(p, rundata);
        PortletEntry regEntry =
          (PortletEntry) Registry.getEntry(Registry.PORTLET, p.getName());

        @SuppressWarnings("unchecked")
        Iterator<Map.Entry<String, Object>> i =
          jsonObject.entrySet().iterator();

        while (i.hasNext()) {
          Map.Entry<String, Object> param = i.next();
          String name = "pref-" + param.getKey();
          String[] testArray = null;
          Object value = param.getValue();
          if (value instanceof JSONArray) {
            List<String> testArrayList = new ArrayList<String>();
            JSONArray array = (JSONArray) value;
            Object[] ojb = array.toArray();
            for (Object o : ojb) {
              testArrayList.add(String.valueOf(o));
            }
            testArray = testArrayList.toArray(new String[] {});
          }
          String newValue = null;
          if (testArray != null && testArray.length > 1) {
            newValue =
              org.apache.jetspeed.util.StringUtils
                .arrayToString(testArray, ",");
          } else {
            newValue = String.valueOf(value);
            if ("null".equals(newValue)) {
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
            profile.setDocument(instance.getDocument());
            profile.store();
            p.init();
            org.apache.jetspeed.util.PortletSessionState
              .setPortletConfigChanged(p, rundata);
          } catch (PortletException e) {
            logger.error("Customizer failed to reinitialize the portlet "
              + p.getName(), e);
          } catch (Exception e) {
            logger.error("Unable to save profile ", e);
          }
        }
      }
      json = new JSONObject();
      result = json.toString();
    } catch (Exception e) {
      logger.error("UserPrefUpdateJSONScreen.getJSONString", e);
    }

    return result;
  }

  @Override
  protected String getPrefix() {
    return "";
  }

  @Override
  protected String getSuffix() {
    return "";
  }
}
