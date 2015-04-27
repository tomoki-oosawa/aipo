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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.DefaultJetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.statemanager.JetspeedHttpStateManagerService;

// Jetspeed classes

/**
 * This interface extends the RunData interface with methods specific to the
 * needs of a Jetspeed like portal implementation.
 *
 * <note>Several of these properties may be put in the base RunData interface in
 * future releases of Turbine</note>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: DefaultJetspeedRunData.java,v 1.20 2004/02/23 03:36:10 jford
 *          Exp $
 */
public class ALJetspeedRunData extends DefaultJetspeedRunData implements
    JetspeedRunData {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALJetspeedRunData.class.getName());

  /*-
   @Override
   public User getUserFromSession() {

   try {
   User user = (User) getSession().getAttribute("turbine.user");
   if (user != null) {
   return user;
   }
   } catch (Throwable ignore) {
   }
   try {
   User user = (User) getRequest().getAttribute("turbine.user");
   if (user != null) {
   return user;
   }
   } catch (Throwable ignore) {
   }
   String username = null;
   try {
   username = (String) getSession().getAttribute("turbine.username");
   } catch (Throwable ignore) {
   }
   if (username == null) {
   return null;
   }
   try {
   JetspeedUser user =
   JetspeedUserManagement.getUser(new UserNamePrincipal(username));
   if (user != null) {
   user.setHasLoggedIn(Boolean.TRUE);
   getRequest().setAttribute("turbine.user", user);
   // getSession().setAttribute("turbine.user", user);
   return user;
   }
   } catch (JetspeedSecurityException e) {
   }
   return null;
   }

   @Override
   public boolean removeUserFromSession() {
   try {
   getRequest().removeAttribute("turbine.user");
   getSession().removeAttribute("turbine.user");
   getSession().removeAttribute("turbine.username");
   } catch (Exception e) {
   return false;
   }
   return true;
   }
   */

  @Override
  public void save() {
    getRequest().setAttribute("turbine.user", getUser());
    getSession().setAttribute("turbine.username", getUser().getUserName());
    getSession().setAttribute("turbine.user", getUser());

    HttpSession session = this.getSession();
    Enumeration e = session.getAttributeNames();
    System.out.println("============save==============");
    while (e.hasMoreElements()) {
      String key1 = (String) e.nextElement();

      System.out.println(key1 + "：" + session.getAttribute(key1));
      if (session.getAttribute(key1) instanceof JetspeedHttpStateManagerService.StateEntry) {
        JetspeedHttpStateManagerService.StateEntry entry =
          (JetspeedHttpStateManagerService.StateEntry) session
            .getAttribute(key1);
        Map map2 = entry.getMap();
        Iterator iterator = map2.keySet().iterator();
        while (iterator.hasNext()) {
          Object next = iterator.next();
          System.out.println(next + "：" + map2.get(next));
        }
      }
    }

    if (getUser() != null) {
      Hashtable tempStorage = getUser().getTempStorage();
      Enumeration keys = tempStorage.keys();
      System.out.println("============save baseuser==============");
      while (keys.hasMoreElements()) {
        String key2 = (String) keys.nextElement();
        System.out.println(key2 + "：" + tempStorage.get(key2));
      }
    }

  }

}
