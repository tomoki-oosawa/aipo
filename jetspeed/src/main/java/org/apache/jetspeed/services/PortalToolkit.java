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

package org.apache.jetspeed.services;

//jetspeed stuff
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.portal.PortletControl;
import org.apache.jetspeed.portal.PortletController;
import org.apache.jetspeed.portal.PortletSet;
import org.apache.jetspeed.portal.PortletSkin;
import org.apache.jetspeed.services.portaltoolkit.PortalToolkitService;
import org.apache.turbine.services.TurbineServices;

/**
 * Commodity static wrapper around the PortalToolit service
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: PortalToolkit.java,v 1.5 2004/02/23 04:00:57 jford Exp $
 */
public class PortalToolkit {

  /**
   * Commodity method for getting a reference to the service singleton
   */
  private static PortalToolkitService getService() {
    return (PortalToolkitService) TurbineServices.getInstance().getService(
      PortalToolkitService.SERVICE_NAME);
  }

  /**
   * Instanciates a PortletControl based on a Registry entry, if available or
   * directly from a classname.
   *
   * @param name
   *          a PortletControl name available in the registry or a classname
   * @return the created PortletControl
   */
  public static PortletControl getControl(String name) {
    return getService().getControl(name);
  }

  /**
   * Instanciates a PortletControl based on a PSML Control object
   *
   * @param control
   *          the PSML control object
   * @return the created PortletControl
   */
  public static PortletControl getControl(Control control) {
    return getService().getControl(control);
  }

  /**
   * Instanciates a PortletController based on a Registry entry, if available or
   * directly from a classname.
   *
   * @param name
   *          a PortletController name available in the registry or a classname
   * @return the created PortletController
   */
  public static PortletController getController(String name) {
    return getService().getController(name);
  }

  /**
   * Instanciates a PortletController based on a PSML Controller object
   *
   * @param control
   *          the PSML controller object
   * @return the created PortletController
   */
  public static PortletController getController(Controller controller) {
    return getService().getController(controller);
  }

  /**
   * Create a PortletSkin object based on a Registry entry if available
   *
   * @param name
   *          the skin name in the Registry
   * @return the new PortletSkin object
   */
  public static PortletSkin getSkin(String name) {
    return getService().getSkin(name);
  }

  /**
   * Create a PortletSkin object based on PSML skin description
   *
   * @param skin
   *          the PSML Skin object
   * @return the new PortletSkin object
   */
  public static PortletSkin getSkin(Skin skin) {
    return getService().getSkin(skin);
  }

  /**
   * Creates a PortletSet from a PSML portlets description
   *
   * @param portlets
   *          the PSML portlet set description
   * @return a new instance of PortletSet
   */
  public static PortletSet getSet(Portlets portlets) {
    return getService().getSet(portlets);
  }

  /**
   * Given a locator String path, returns a Portlets collecton
   *
   * @param locatorPath
   *          ProfileLocator resource path identifier
   * @return a portlets collection from the PSML resource
   */
  public static Portlets getReference(String locatorPath) {
    return getService().getReference(locatorPath);
  }

  /**
   * Gets default security ref based on the profile type (user|role|group).
   * Returns null if no default is defined.
   *
   * @param profile
   * @return default security reference
   */
  public static SecurityReference getDefaultSecurityRef(Profile profile) {
    return getService().getDefaultSecurityRef(profile);
  }

  /**
   * Gets default security ref based on the profile type (user|role|group).
   * Returns null if no default is defined.
   *
   * @param type
   *          of entity to return default security ref for
   * @return default security reference
   */
  public static SecurityReference getDefaultSecurityRef(String type) {
    return getService().getDefaultSecurityRef(type);
  }

}
