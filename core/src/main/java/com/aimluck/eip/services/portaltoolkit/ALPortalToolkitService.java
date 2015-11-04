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
package com.aimluck.eip.services.portaltoolkit;

import java.util.Iterator;
import java.util.Map;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.registry.PortletEntry;
import org.apache.jetspeed.portal.BasePortletSet;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.services.PortletFactory;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.portaltoolkit.JetspeedPortalToolkitService;
import org.apache.jetspeed.util.JetspeedException;

import com.aimluck.eip.services.portal.ALPortalApplicationService;
import com.aimluck.eip.util.CustomizeUtils;

/**
 *
 */
public class ALPortalToolkitService extends JetspeedPortalToolkitService {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALPortalToolkitService.class.getName());

  @Override
  protected PortletSet getSet(Portlets portlets, VariableInteger theCount) {
    // Create a new BasePortletSet to handle the portlets
    BasePortletSet set = new BasePortletSet();
    PortletController controller = getController(portlets.getController());
    set.setController(controller);
    String name = portlets.getName();
    if (name != null) {
      set.setName(name);
    } else {
      set.setName(String.valueOf(theCount.getValue()));
    }

    set.setID(portlets.getId());

    theCount.setValue(theCount.getValue() + 1);

    // FIXME: this sucks ! we should either associate the portlet set
    // with its portlets peer or set the porpoerties directly on the portlet
    // set object
    // Unfortunately, this would change the API too drastically for now...
    set.setPortletConfig(getPortletConfig(portlets));

    // Add all sub portlet sets in the main set
    // Portlets[] subsets = portlets.getPortlets();
    // for (int i=0; i < subsets.length; i++ )

    for (@SuppressWarnings("unchecked")
    Iterator<Portlets> it = portlets.getPortletsIterator(); it.hasNext();) {
      Portlets subset = it.next();
      // Set this subset's parent Portlets collection.
      subset.setParentPortlets(portlets);

      Map<?, ?> constraints = getParameters(subset.getLayout());
      int position = getPosition(subset.getLayout());
      set.addPortlet(getSet(subset, theCount), controller
        .getConstraints(constraints), position);
    }

    // Populate the PortletSet with Portlets
    // Entry[] entries = portlets.getEntry();
    // for( int i = 0; i < entries.length; ++i )

    for (@SuppressWarnings("unchecked")
    Iterator<Portlets> eit = portlets.getEntriesIterator(); eit.hasNext();) {
      try {

        Entry psmlEntry = (Entry) eit.next();
        PortletEntry entry =
          (PortletEntry) Registry.getEntry(Registry.PORTLET, psmlEntry
            .getParent());

        if (entry != null) {
          Portlet p = PortletFactory.getPortlet(psmlEntry);

          if (p != null) {
            Map<?, ?> constraints = getParameters(psmlEntry.getLayout());
            int position = getPosition(psmlEntry.getLayout());

            PortletControl control = getControl(psmlEntry.getControl(), entry);

            if (ALPortalApplicationService.isActive(p.getName())
              && CustomizeUtils.isAdminUserView(entry)) {
              set.addPortlet(initControl(control, p), controller
                .getConstraints(constraints), position);
            }
          }
        } else {
          if (!"ScheduleAdmin".equals(psmlEntry.getParent())) {
            logger.error(" The portlet "
              + psmlEntry.getParent()
              + " does not exist in the Registry ");
          }
          continue;
        }
      } catch (JetspeedException e) {
        logger.error("ALPortalToolkitService.getSet", e);
        continue;
      }

    }

    // Decorate with a control if required and return
    if (portlets.getControl() != null) {
      PortletControl control = getControl(portlets.getControl());
      return initControl(control, set);
    }

    set.sortPortletSet();
    // Or return the set
    return set;
  }
}
