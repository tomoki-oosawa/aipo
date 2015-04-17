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

import javax.servlet.http.HttpSession;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.UserNamePrincipal;
import org.apache.jetspeed.services.JetspeedUserManagement;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.DefaultJetspeedRunData;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.security.JetspeedSecurityException;
import org.apache.turbine.om.security.User;

import com.aimluck.eip.common.ALBaseUser;

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

  @Override
  public User getUserFromSession() {
    return getUserFromSession(this.getSession());
  }

  @Override
  public boolean removeUserFromSession() {
    return removeUserFromSession(this.getSession());
  }

  @Override
  public void populate() {
    System.out.println("============populate==============");
    setUser(getUserFromSession());
    if (getUser() != null) {
      getUser().setLastAccessDate();
      getUser().incrementAccessCounter();
      getUser().incrementAccessCounterForSession();
    }
  }

  @Override
  public boolean userExists() {
    setUser(getUserFromSession());
    return getUser() != null;
  }

  @Override
  public void save() {
    this.getSession().putValue("turbine.user", getUser());
    this.getSession().putValue("turbine.username", getUser().getUserName());

    HttpSession session = this.getSession();
    Enumeration e = session.getAttributeNames();
    System.out.println("============save==============");
    while (e.hasMoreElements()) {
      String key1 = (String) e.nextElement();

      System.out.println(key1 + "：" + session.getAttribute(key1));
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

  public static User getUserFromSession(HttpSession session) {
    try {
      String username = (String) session.getValue("turbine.username");
      JetspeedUser user =
        JetspeedUserManagement.getUser(new UserNamePrincipal(username));

      ALBaseUser baseuser = (ALBaseUser) user;
      User value = (User) session.getValue("turbine.user");

      Enumeration e = session.getAttributeNames();
      System.out.println("============getUserFromSession==============");
      System.out.println(username);
      if (user != null) {
        System.out.println("============JetspeedUser==============");
        System.out.println(user.getUserId() + " " + user.getUserName());
      }
      if (baseuser != null) {
        System.out.println("============ALBaseUser==============");
        System.out.println(baseuser.getUserId() + " " + baseuser.getUserName());
      }
      if (value != null) {
        System.out.println("============User==============");
        System.out.println(value.getUserName());
      }
      while (e.hasMoreElements()) {
        String key1 = (String) e.nextElement();

        System.out.println(key1 + "：" + session.getAttribute(key1));
      }

      if (baseuser != null) {
        Hashtable tempStorage = baseuser.getTempStorage();
        Enumeration keys = tempStorage.keys();
        System.out.println("============baseuser==============");
        while (keys.hasMoreElements()) {
          String key2 = (String) keys.nextElement();
          System.out.println(key2 + "：" + tempStorage.get(key2));
        }
      }

      if (value != null) {
        Hashtable tempStorage1 = value.getTempStorage();
        Enumeration keys1 = tempStorage1.keys();
        System.out.println("============turbine.user==============");
        while (keys1.hasMoreElements()) {
          String key2 = (String) keys1.nextElement();
          System.out.println(key2 + "：" + tempStorage1.get(key2));
        }
      }

      return (User) session.getValue("turbine.user");
      // return user;
    } catch (ClassCastException e) {
    } catch (JetspeedSecurityException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean removeUserFromSession(HttpSession session) {
    try {
      System.out.println("============removeUserFromSession==============");
      session.removeValue("turbine.username");
      session.removeValue("turbine.user");
    } catch (Exception e) {
      return false;
    }
    return true;
  }

}
