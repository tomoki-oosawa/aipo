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
package com.aimluck.eip.util;

import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

/**
 *
 */
public class ALJetspeedLink implements ApplicationTool, JetspeedLink {

  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ALJetspeedLink.class.getName());

  /**
   * <p>
   * Request to which we refer.
   * </p>
   */
  private JetspeedRunData rundata = null;

  /**
   * Profile locator from <code>rundata</code>. This is here for performance
   * reasons.
   */
  private ProfileLocator locator = null;

  /**
   * Profile from <code>rundata</code>. This is here for performance reasons.
   */
  private Profile profile = null;

  /**
   * Has the initialization for the current rundata been performed?. This is
   * here for performance reasons.
   */
  private boolean initDone = false;

  /**
   * <p>
   * The portlet that will be used to build the reference.
   * </p>
   */
  protected Portlet activePortlet = null;

  /**
   * Empty constructor.for introspection
   */
  public ALJetspeedLink() {
  }

  /**
   * Constructor required by ApplicationTool interface
   *
   * @param data
   *          A Jetspeed RunData object.
   */
  public ALJetspeedLink(RunData data) {
    init((Object) data);
  }

  /**
   * This will initialise a JetspeedLink object that was constructed with the
   * default constructor
   *
   * @param rundata
   *          to be a RunData object
   */
  @Override
  public void init(RunData rundata) {
    init((Object) rundata);
  }

  /**
   * Adds a name=value pair to the query string.
   *
   * @param name
   *          A String with the name to add.
   * @param value
   *          An Object with the value to add.
   * @return DynamicURI that to the desired page
   */
  @Override
  public DynamicURI addQueryData(String name, Object value) {
    try {
      return getRoot().addQueryData(name, value);
    } catch (ProfileException e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Adds a name=value pair to the path_info string.
   *
   * @param name
   *          A String with the name to add.
   * @param value
   *          An Object with the value to add.
   * @return DynamicURI that to the desired page
   */
  @Override
  public DynamicURI addPathInfo(String name, Object value) {
    try {
      return getRoot().addPathInfo(name, value);
    } catch (ProfileException e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Return an link to a specific portal element
   *
   * @param peid
   *          of the portal element
   * @return DynamicURI to specific portal element
   *
   * @deprecated Use getPortletById() or getPaneById()
   */
  @Deprecated
  @Override
  public DynamicURI setPortalElement(String peid) {
    if (initDone == false) {
      initLink();
    }
    if (profile.getDocument().getEntryById(peid) != null) {
      return getPortletById(peid);
    } else {
      return setPaneById(peid);
    }
  }

  /**
   * Return an link to a specific portlet using the portet's id
   *
   * @param peid
   *          of the portlet
   * @return DynamicURI to specific portlet
   *
   * @deprecated Use getPortletById()
   */
  @Deprecated
  @Override
  public DynamicURI setPortletById(String peid) {
    return getPortletById(peid);
  }

  /**
   * Return link to the home page without user, page, group, role, template,
   * action, media type, language, or country in link.
   *
   * @return DynamicURI to the home page
   */
  @Override
  public DynamicURI getHomePage() {
    return getLink(
      JetspeedLink.DEFAULT,
      null,
      "",
      JetspeedLink.DEFAULT,
      null,
      "",
      "",
      "",
      "",
      "");
  }

  /**
   * Return a link that includes the template from rundata
   *
   * @return DynamicURI to template
   */
  @Override
  public DynamicURI getTemplate() {
    String template = rundata.getRequestedTemplate();
    return getLink(
      JetspeedLink.CURRENT,
      null,
      null,
      JetspeedLink.DEFAULT,
      null,
      null,
      template,
      null,
      null,
      null);
  }

  /**
   * Return a link to the template.
   *
   * @param template
   *          to add to link
   * @return DynamicURI to specific portlet
   *
   * @deprecated Use getTemplate()
   */
  @Deprecated
  @Override
  public DynamicURI setTemplate(String template) {
    return getTemplate(template);
  }

  /**
   * Return a link that includes an action
   *
   * @param action
   *          Desired action
   * @return DynamicURI that includes the desire action
   *
   * @deprecated Use getAction()
   */
  @Deprecated
  @Override
  public DynamicURI setAction(String action) {
    return getAction(action);
  }

  /**
   * Return a link that includes an action to a specific portlet, as defined by
   * a portlets
   *
   * @param action
   *          Desired action
   * @param portlets
   *          to receive the action
   * @return DynamicURI that includes the desire action
   *
   * @deprecated Use getAction()
   */
  @Deprecated
  @Override
  public DynamicURI setAction(String action, Portlets portlets) {
    return getAction(action, portlets);
  }

  /**
   * Return a link that includes an action to a specific portlet
   *
   * @param action
   *          Desired action
   * @param portlet
   *          to receive the action
   * @return DynamicURI that includes the desire action
   *
   * @deprecated Use getAction()
   */
  @Deprecated
  @Override
  public DynamicURI setAction(String action, Portlet portlet) {
    return getAction(action, portlet);
  }

  /**
   * Return a link that includes an action to a specific portlet, as defined by
   * an entry
   *
   * @param action
   *          Desired action
   * @param entry
   *          to receive the action
   * @return DynamicURI that includes the desire action
   *
   * @deprecated Use getAction()
   */
  @Deprecated
  @Override
  public DynamicURI setAction(String action, Entry entry) {
    return getAction(action, entry);
  }

  /**
   * Return a link that includes an action to a specific portlet, as defined by
   * a PEID
   *
   * @param action
   *          Desired action
   * @param peid
   *          Id of portlet to receive the action
   * @return DynamicURI that includes the desire action
   * @deprecated Use getAction()
   */
  @Deprecated
  public DynamicURI setAction(String action, String peid) {
    return getAction(action, peid);
  }

  /**
   * Return a link to a default page for the group
   *
   * @param group
   *          Desired group
   * @return DynamicURI that to the desired page
   *
   * @deprecated Use getGroup()
   */
  @Deprecated
  @Override
  public DynamicURI setGroup(String group) {
    return getGroup(group);
  }

  /**
   * Return a link to a desired page for the group
   *
   * @param page
   *          Desired page
   * @param group
   *          Desired group
   * @return DynamicURI that to the desired page
   *
   * @deprecated Use getGroup()
   */
  @Deprecated
  @Override
  public DynamicURI setGroup(String group, String page) {
    return getGroup(group, page);
  }

  /**
   * Return a link to a default page for the current user, group, or role.
   *
   * @return DynamicURI that to the desired page
   *
   * @deprecated Use getPage()
   */
  @Deprecated
  @Override
  public DynamicURI setPage() {
    return getPage();
  }

  /**
   * Return a link to a desired page for the current user, group, or role.
   *
   * @param page
   *          Desired page
   * @return DynamicURI that to the desired page
   *
   * @deprecated Use getPage()
   */
  @Deprecated
  @Override
  public DynamicURI setPage(String page) {
    return getPage(page);
  }

  /**
   * Return a link to a desired page and pane for the current user, group, or
   * role.
   *
   * @param page
   *          Desired page
   * @param paneName
   *          Desired pane name
   * @return DynamicURI that to the desired page
   * @deprecated Use getPage()
   */
  @Deprecated
  @Override
  public DynamicURI setPage(String page, String paneName) {
    return getPage(page, paneName);
  }

  /**
   * Return a link to a default page for the role
   *
   * @param role
   *          Desired role
   * @return DynamicURI that to the desired page
   *
   * @deprecated use getRole()
   */
  @Deprecated
  @Override
  public DynamicURI setRole(String role) {
    return getRole(role);
  }

  /**
   * Return a link to a desired page for the role
   *
   * @param role
   *          Desired role
   * @param page
   *          Desired page
   * @return DynamicURI that to the desired page
   *
   * @deprecated use getRole()
   */
  @Deprecated
  @Override
  public DynamicURI setRole(String role, String page) {
    return getRole(role, page);
  }

  /**
   * Return a link to a default page for the user
   *
   * @param user
   *          Desired user
   * @return DynamicURI that to the desired page
   *
   * @deprecated Use getUser()
   */
  @Deprecated
  @Override
  public DynamicURI setUser(String user) {
    return getUser(user);
  }

  /**
   * Return a link to a desired page for the user
   *
   * @param page
   *          Desired page
   * @param user
   *          Desired user
   * @return DynamicURI that to the desired page
   *
   * @deprecated Use getUser()
   */
  @Deprecated
  @Override
  public DynamicURI setUser(String user, String page) {
    return getUser(user, page);
  }

  /**
   * Return a link to a specific pane using the pane's id
   *
   * @param paneId
   *          of the Pane
   * @return URI to specific portlet
   *
   * @deprecated Use getPaneById()
   */
  @Deprecated
  @Override
  public DynamicURI setPaneById(String paneId) {
    return getPaneById(paneId);
  }

  /**
   * Return a link to a specific pane using the pane's id
   *
   * @param paneName
   *          Name of the Pane
   * @return URI to specific portlet
   * @deprecated Use getPaneByName()
   */
  @Deprecated
  public DynamicURI setPaneByName(String paneName) {
    return getPaneByName(paneName);
  }

  /**
   * Return a link to a desired page. This is allows the inclusion of a
   * Group/Role/User, page, template, action, media type, language, and country.
   *
   * @param rootType
   *          Type of root PSML docuument. The should be one of the following:
   *          <dl>
   *          <dt>JetspeedLink.CURRENT</dt>
   *          <dd>The link will retain the current Group/Role/User referance.
   *          rootValue is not used</dd>
   *          <dt>JetspeedLink.DEFAULT</dt>
   *          <dd>Default Group, Role, or User. rootValue is not used</dd>
   *          <dt>JetspeedLink.GROUP</dt>
   *          <dd>Link will be to a Group PSML. rootValue is a Group Name</dd>
   *          <dt>JetspeedLink.ROLE</dt>
   *          <dd>Link will be to a Role PSML. rootValue is a Role Name</dd>
   *          <dt>JetspeedLink.USER</dt>
   *          <dd>Link will be to a User PSML. rootValue is a User Name</dd>
   *          </dl>
   * @param rootValue
   *          See description of rootType
   * @param pageName
   *          Name of page. null = default page
   * @param elementType
   *          <dl>
   *          <dt>JetspeedLink.CURRENT</dt>
   *          <dd>The link will retain the current Pane/Portlet referance.
   *          elementValue is not used</dd>
   *          <dt>JetspeedLink.DEFAULT</dt>
   *          <dd>The link will NOT referance a pane or portlet. elementValue is
   *          not used</dd>
   *          <dt>JetspeedLink.PANE_ID</dt>
   *          <dd>Link will be to a Pane using it's ID. elementValue is a Pane's
   *          ID</dd>
   *          <dt>JetspeedLink.PANE_NAME</dt>
   *          <dd>Link will be to a Pane using it's Name. elementValue is a
   *          Pane's Name</dd>
   *          <dt>JetspeedLink.PORTLET_ID</dt>
   *          <dd>Link will be to a Portlet using it's ID. elementValue is a
   *          Portlet's ID</dd>
   *          <dt>JetspeedLink.PORTLET_NAME</dt>
   *          <dd>Link will be to a Portlet using it's Name. elementValue is a
   *          Portlet's Name</dd>
   *          <dt>JetspeedLink.PORTLET_ID_QUERY</dt>
   *          <dd>Link will be to a Portlet using it's ID based on portlet name
   *          provided. elementValue is a Portlet's name. ID is for the first
   *          portlet with matching name</dd>
   *          </dl>
   * @param elementValue
   *          See description of elementType
   * @param actionName
   *          Name of action. If no action is desired use
   *          JetspeedLink.NO_ACTION.
   * @param templateName
   *          Name of template. If no template is desired use
   *          JetspeedLink.NO_TEMPLATE.
   * @param mediaType
   *          Desired media type. null = default media type
   * @param language
   *          Desired language. null = default language
   * @param country
   *          Desired country. null = default language
   * @return URI to specific portlet
   */
  @Override
  public DynamicURI getLink(int rootType, String rootValue, String pageName,
      int elementType, String elementValue, String actionName,
      String templateName, String mediaType, String language, String country) {
    String uriPathType = null;
    String uriPathElement = null;
    try {
      DynamicURI uri = getRoot();

      // Set Group/Role/User in path
      switch (rootType) {
        case JetspeedLink.DEFAULT:
        case JetspeedLink.CURRENT:
          break;
        case JetspeedLink.GROUP:
          uriPathType = Profiler.PARAM_GROUP;
          break;
        case JetspeedLink.ROLE:
          uriPathType = Profiler.PARAM_ROLE;
          break;
        case JetspeedLink.USER:
          uriPathType = Profiler.PARAM_USER;
          break;
      }

      if (rootType != JetspeedLink.CURRENT) {
        // Cleanup URI
        uri.removePathInfo(Profiler.PARAM_GROUP);
        uri.removePathInfo(Profiler.PARAM_ROLE);
        uri.removePathInfo(Profiler.PARAM_USER);

        if ((rootType != JetspeedLink.DEFAULT)
          && (rootValue != null)
          && (rootValue.trim().length() > 0)) {
          uri.addPathInfo(uriPathType, rootValue);
        }
      }

      // Set Page in path
      if (pageName != null) {
        uri.removePathInfo(Profiler.PARAM_PAGE);
        if (pageName.trim().length() > 0) {
          // uri.addPathInfo(Profiler.PARAM_PAGE, pageName);
        }
      }

      // Set Portlet/Pane in path
      switch (elementType) {
        case JetspeedLink.CURRENT:
        case JetspeedLink.DEFAULT:
          break;
        case JetspeedLink.PANE_ID:
          uriPathElement = JetspeedResources.PATH_PANEID_KEY;
          break;
        case JetspeedLink.PANE_NAME:
          uriPathElement = JetspeedResources.PATH_PANENAME_KEY;
          break;
        case JetspeedLink.PORTLET_ID:
          uriPathElement = JetspeedResources.PATH_PORTLETID_KEY;
          break;
        case JetspeedLink.PORTLET_NAME:
          uriPathElement = JetspeedResources.PATH_PORTLET_KEY;
          break;
        case JetspeedLink.PORTLET_ID_QUERY:
          if (logger.isDebugEnabled()) {
            logger.debug("BaseJetspeedLink: elementValue = " + elementValue);
          }
          uriPathElement = JetspeedResources.PATH_PORTLETID_KEY;
          ProfileLocator baseLocator = Profiler.createLocator();
          Profile baseProfile = null;
          switch (rootType) {
            case JetspeedLink.DEFAULT:
              break;
            case JetspeedLink.CURRENT:
              baseProfile = rundata.getProfile();
              break;
            case JetspeedLink.GROUP:
              baseLocator.setGroupByName(rootValue);
              break;
            case JetspeedLink.ROLE:
              baseLocator.setRoleByName(rootValue);
              break;
            case JetspeedLink.USER:
              try {
                if (logger.isDebugEnabled()) {
                  logger.debug("BaseJetspeedLink: rootValue user = "
                    + rootValue);
                }
                baseLocator
                  .setUser(org.apache.jetspeed.services.JetspeedSecurity
                    .getUser(rootValue));
              } catch (Exception se) {
                logger.error("Exception", se);
                return null;
              }
              break;
          }

          if ((rootType != JetspeedLink.CURRENT)
            && (rootType != JetspeedLink.DEFAULT)) {
            if (mediaType != null && mediaType.length() > 0) {
              baseLocator.setMediaType(mediaType);
            }
            if (language != null && language.length() > 0) {
              baseLocator.setLanguage(language);
            }
            if (country != null && country.length() > 0) {
              baseLocator.setCountry(country);
            }
            if (pageName != null && pageName.length() > 0) {
              baseLocator.setName(pageName);
            }
            baseProfile = Profiler.getProfile(baseLocator);
          }

          if (logger.isDebugEnabled()) {
            logger.debug("BaseJetspeedLink: baseLocator = "
              + baseLocator.getPath());
          }

          if ((baseProfile != null) && (elementValue != null)) {
            if (logger.isDebugEnabled()) {
              logger.debug("BaseJetspeedLink: baseProfile = "
                + baseProfile.toString());
            }
            if (baseProfile.getDocument() != null) {
              if (logger.isDebugEnabled()) {
                logger.debug("BaseJetspeedLink: baseProfile.getDocment() = "
                  + baseProfile.getDocument());
              }
              Entry entry = baseProfile.getDocument().getEntry(elementValue);
              if (entry != null) {
                if (logger.isDebugEnabled()) {
                  logger.debug("BaseJetspeedLink: entry id = " + entry.getId());
                }
                elementValue = entry.getId();
              } else {
                elementValue = null;
              }
            }
          }

          break;
      }

      if (elementType != JetspeedLink.CURRENT) {
        // Remove Group/Role/User in URI
        uri.removePathInfo(JetspeedResources.PATH_PANEID_KEY);
        uri.removePathInfo(JetspeedResources.PATH_PANENAME_KEY);
        uri.removePathInfo(JetspeedResources.PATH_PORTLETID_KEY);
        uri.removePathInfo(JetspeedResources.PATH_PORTLET_KEY);

        if ((elementType != JetspeedLink.DEFAULT)
          && (elementValue != null)
          && (elementValue.length() > 0)) {
          uri.addPathInfo(uriPathElement, elementValue);
        }
      }

      // Set Template in path
      if (templateName != null) {
        uri.removePathInfo(JetspeedResources.PATH_TEMPLATE_KEY);
        if (templateName.length() > 0) {
          uri.addPathInfo(JetspeedResources.PATH_TEMPLATE_KEY, templateName);
        }
      }

      // Set Action in path
      if (actionName != null) {
        uri.removeQueryData(JetspeedResources.PATH_ACTION_KEY);
        if (actionName.length() > 0) {
          uri.addQueryData(JetspeedResources.PATH_ACTION_KEY, actionName);
        }
      }

      // Set MediaType in path
      if (mediaType != null) {
        uri.removePathInfo(Profiler.PARAM_MEDIA_TYPE);
        if (mediaType.length() > 0) {
          uri.addPathInfo(Profiler.PARAM_MEDIA_TYPE, mediaType);
        }
      }

      // Set Language in path
      if (language != null) {
        uri.removePathInfo(Profiler.PARAM_LANGUAGE);
        if (language.length() > 0) {
          uri.addPathInfo(Profiler.PARAM_LANGUAGE, language);
        }
      }

      // Set Country in path
      if (country != null) {
        uri.removePathInfo(Profiler.PARAM_COUNTRY);
        if (country.length() > 0) {
          uri.addPathInfo(Profiler.PARAM_COUNTRY, country);
        }
      }

      return uri;
    } catch (ProfileException e) {
      logger.error("Exception", e);
      return null;
    }
  }

  @Override
  public DynamicURI getLink(int rootType, String rootValue, String pageName,
      int elementType, String elementValue, String actionName,
      String templateName, String mediaType, String language) {
    return getLink(
      rootType,
      rootValue,
      pageName,
      elementType,
      elementValue,
      actionName,
      templateName,
      mediaType,
      language,
      null);
  }

  @Override
  public DynamicURI getLink(int rootType, String rootValue, String pageName,
      int elementType, String elementValue, String actionName,
      String templateName, String mediaType) {
    return getLink(
      rootType,
      rootValue,
      pageName,
      elementType,
      elementValue,
      actionName,
      templateName,
      mediaType,
      null,
      null);
  }

  @Override
  public DynamicURI getLink(int rootType, String rootValue, String pageName,
      int elementType, String elementValue, String actionName,
      String templateName) {
    return getLink(
      rootType,
      rootValue,
      pageName,
      elementType,
      elementValue,
      actionName,
      actionName,
      null,
      null,
      null);
  }

  @Override
  public DynamicURI getLink(int rootType, String rootValue, String pageName,
      int elementType, String elementValue, String actionName) {
    return getLink(
      rootType,
      rootValue,
      pageName,
      elementType,
      elementValue,
      actionName,
      null,
      null,
      null,
      null);
  }

  /**
   */
  @Override
  public DynamicURI getLink(int rootType, String rootValue, String pageName,
      int elementType, String elementValue) {
    return getLink(
      rootType,
      rootValue,
      pageName,
      elementType,
      elementValue,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link that includes an action
   *
   * @param action
   *          action
   * @return DynamicURI that includes the desire action
   */
  @Override
  public DynamicURI getAction(String action) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      null,
      JetspeedLink.CURRENT,
      null,
      action,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link that includes an action to a specific portlet, as defined by
   * an entry
   *
   * @param action
   *          Desired action
   * @param entry
   *          to receive the action
   * @return DynamicURI that includes the desire action
   */
  @Override
  public DynamicURI getAction(String action, Entry entry) {
    if (entry != null) {
      return getLink(
        JetspeedLink.CURRENT,
        null,
        null,
        JetspeedLink.PORTLET_ID,
        entry.getId(),
        null,
        action,
        null,
        null,
        null);
    } else {
      return getLink(
        JetspeedLink.CURRENT,
        null,
        null,
        JetspeedLink.PORTLET_ID,
        null,
        null,
        action,
        null,
        null,
        null);
    }
  }

  /**
   * Return a link that includes an action to a specific portlet
   *
   * @param action
   *          Desired action
   * @param portlet
   *          to receive the action
   * @return DynamicURI that includes the desire action
   */
  @Override
  public DynamicURI getAction(String action, Portlet portlet) {
    if (portlet != null) {
      return getLink(
        JetspeedLink.CURRENT,
        null,
        null,
        JetspeedLink.PORTLET_ID,
        portlet.getID(),
        action,
        null,
        null,
        null,
        null);
    } else {
      return getLink(
        JetspeedLink.CURRENT,
        null,
        null,
        JetspeedLink.PORTLET_ID,
        null,
        action,
        null,
        null,
        null,
        null);
    }
  }

  /**
   * Return a link that includes an action to a specific portlet, as defined by
   * a portlets
   *
   * @param action
   *          Desired action
   * @param portlets
   *          to receive the action
   * @return DynamicURI that includes the desire action
   */
  @Override
  public DynamicURI getAction(String action, Portlets portlets) {
    if (portlets != null) {
      return getLink(
        JetspeedLink.CURRENT,
        null,
        null,
        JetspeedLink.PORTLET_ID,
        portlets.getId(),
        action,
        null,
        null,
        null,
        null);
    } else {
      return getLink(
        JetspeedLink.CURRENT,
        null,
        null,
        JetspeedLink.PORTLET_ID,
        null,
        action,
        null,
        null,
        null,
        null);
    }
  }

  /**
   * Return a link that includes an action to a specific portlet, as defined by
   * a PEID
   *
   * @param action
   *          Desired action
   * @param peid
   *          Id of the portlet to receive the action
   * @return DynamicURI that includes the desire action
   */
  @Override
  public DynamicURI getAction(String action, String peid) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      null,
      JetspeedLink.PORTLET_ID,
      peid,
      action,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a default page for the group
   *
   * @param group
   *          Desired group
   * @return DynamicURI that to the default page for the group
   */
  @Override
  public DynamicURI getGroup(String group) {
    return getLink(
      JetspeedLink.GROUP,
      group,
      "",
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a desired page for the group
   *
   * @param page
   *          Desired page
   * @param group
   *          Desired group
   * @return DynamicURI that to the desired group and page
   */
  @Override
  public DynamicURI getGroup(String group, String page) {
    return getLink(
      JetspeedLink.GROUP,
      group,
      page,
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a default page for the current user, group, or role.
   *
   * @return DynamicURI that to the default page
   */
  @Override
  public DynamicURI getPage() {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      "",
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a desired page for the current user, group, or role.
   *
   * @param page
   *          Desired page
   * @return DynamicURI that to the desired page
   */
  @Override
  public DynamicURI getPage(String page) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      page,
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a desired page and pane for the current user, group, or
   * role.
   *
   * @param page
   *          Desired page
   * @param paneName
   *          Name of the desired pane
   * @return DynamicURI that to the desired page
   */
  @Override
  public DynamicURI getPage(String page, String paneName) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      page,
      JetspeedLink.PANE_NAME,
      paneName,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a specific pane using the pane's id
   *
   * @param paneId
   *          of the Pane
   * @return URI to specific portlet
   */
  @Override
  public DynamicURI getPaneById(String paneId) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      this.getPageName(),
      JetspeedLink.PANE_ID,
      paneId,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a specific pane using the pane's id
   *
   * @param paneName
   *          Name of the Pane
   * @return URI to specific portlet
   */
  @Override
  public DynamicURI getPaneByName(String paneName) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      this.getPageName(),
      JetspeedLink.PANE_NAME,
      paneName,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return an link to a specific portlet using the portet's id
   *
   * @param peid
   *          of the portlet
   * @return DynamicURI to specific portlet
   */
  @Override
  public DynamicURI getPortletById(String peid) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      this.getPageName(),
      JetspeedLink.PORTLET_ID,
      peid,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Add a portlet reference in the link.
   *
   * Note: This must be used with caution, since a portlet may exist may times
   * in a PSML. setPortletById() is the perfered method.
   *
   * @param portletName
   *          the name of the portlet to link to
   * @return a DynamicURI referencing the named portlet
   */
  @Override
  public DynamicURI getPortletByName(String portletName) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      this.getPageName(),
      JetspeedLink.PORTLET_NAME,
      portletName,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a default page for the role
   *
   * @param role
   *          Desired role
   * @return DynamicURI that to the desired page
   */
  @Override
  public DynamicURI getRole(String role) {
    return getLink(
      JetspeedLink.ROLE,
      role,
      "",
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a desired page for the role
   *
   * @param role
   *          Desired role
   * @param page
   *          Desired page
   * @return DynamicURI that to the desired page
   */
  @Override
  public DynamicURI getRole(String role, String page) {
    return getLink(
      JetspeedLink.ROLE,
      role,
      page,
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to the template.
   *
   * @param template
   *          to add to link
   * @return DynamicURI to specific portlet
   */
  @Override
  public DynamicURI getTemplate(String template) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      null,
      JetspeedLink.DEFAULT,
      null,
      null,
      template,
      null,
      null,
      null);
  }

  /**
   * Return a link to a default page for the user
   *
   * @param user
   *          Desired user
   * @return DynamicURI that to the desired page
   */
  @Override
  public DynamicURI getUser(String user) {
    return getLink(
      JetspeedLink.USER,
      user,
      "",
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      null,
      null,
      null);
  }

  /**
   * Return a link to a desired page for the user
   *
   * @param page
   *          Desired page
   * @param user
   *          Desired user
   * @return DynamicURI that to the desired page
   */
  @Override
  public DynamicURI getUser(String user, String page) {
    return getLink(
      JetspeedLink.USER,
      user,
      page,
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      null,
      null,
      null);
  }

  private void initLink() {
    if (initDone == true) {
      return;
    }

    try {
      // get the profile that is set in the rundata
      profile = rundata.getProfile();

      // if there was no profile, try making one from the rundata
      if (profile == null) {
        // this would only happen if the JetspeedAccessController didn't get a
        // chance
        // to setup the rundata...
        profile = Profiler.getProfile(rundata);
        rundata.setProfile(profile);
        logger.warn("BaseJetspeedLink: profile in rundata was null");
      }
    } catch (ProfileException e) {
      logger.error("Exception", e);
    }
    if (profile != null) {
      // Get ProfileLocator for path info.
      if ((profile instanceof ProfileLocator) == true) {
        locator = profile;
      }
    }
    initDone = true;
  }

  /**
   * Return a link to the root portlet or pane
   *
   * @throws ProfileException
   *           if the profile detects an error
   * @return URI to the root portlet/pane
   */
  private DynamicURI getRoot() throws ProfileException {
    DynamicURI uri = null;
    initLink();
    // if (locator != null) {
    // uri = Profiler.makeDynamicURI(rundata, locator);
    // }
    if (uri == null) {
      uri = new DynamicURI(rundata);
    }

    // check if we need to force to a secure (https) link
    if (JetspeedResources.getBoolean("force.ssl", false)) {
      uri.setSecure();
    }

    return uri;
  }

  /**
   * Return a URL, as a string, the the root page or pane.
   *
   * @return a URL, as a string, the the root page or pane.
   */
  @Override
  public String toString() {
    try {
      return getRoot().toString();
    } catch (ProfileException e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Return the action key. Used by velocity templates, i.e. $jlink.ActionKey
   *
   * @return the action parameter name
   */
  @Override
  public String getActionKey() {
    return JetspeedResources.PATH_ACTION_KEY;
  }

  /**
   * Is the PSML for the anonymous user?
   *
   * @return True = PSML is for the anonymous user
   */
  @Override
  public boolean getAnonymous() {
    initLink();
    try {
      return locator.getAnonymous();
    } catch (Exception e) {
      logger.error("Exception", e);
      return true;
    }
  }

  /**
   * Return country of the PSML file
   *
   * @return Country of PSML, or null if no country
   */
  @Override
  public String getCountry() {
    initLink();
    try {
      return locator.getCountry();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Return Group name of the PSML file
   *
   * @return Group name of PSML, or null if no Group name
   */
  @Override
  public String getGroupName() {
    initLink();
    try {
      return locator.getGroupName();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Return Language of the PSML file
   *
   * @return Language of PSML, or null if no Language
   */
  @Override
  public String getLanguage() {
    initLink();
    try {
      return locator.getLanguage();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Return Media Type of the PSML file
   *
   * @return Media Type of PSML, or null if no Media Type
   */
  @Override
  public String getMediaType() {
    initLink();
    try {
      return locator.getMediaType();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Return Page name of the PSML file
   *
   * @return Page name of PSML, or null if no Page name
   */
  @Override
  public String getPageName() {
    initLink();
    try {
      return locator.getName();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Return Role name of the PSML file
   *
   * @return Role name of PSML, or null if no Role name
   */
  @Override
  public String getRoleName() {
    initLink();
    try {
      return locator.getRoleName();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * Return User name of the PSML file
   *
   * @return User name of PSML, or null if no User name
   */
  @Override
  public String getUserName() {
    initLink();
    try {
      return locator.getUserName();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
  }

  /**
   * The following methods used by Velocity to get value of constants
   */
  public static int getCURRENT() {
    return JetspeedLink.CURRENT;
  }

  public static int getDEFAULT() {
    return JetspeedLink.DEFAULT;
  }

  public static int getGROUP() {
    return JetspeedLink.GROUP;
  }

  public static int getPANE_ID() {
    return JetspeedLink.PANE_ID;
  }

  public static int getPANE_NAME() {
    return JetspeedLink.PANE_NAME;
  }

  public static int getPORTLET_ID() {
    return JetspeedLink.PORTLET_ID;
  }

  public static int getPORTLET_NAME() {
    return JetspeedLink.PORTLET_NAME;
  }

  public static int getROLE() {
    return JetspeedLink.ROLE;
  }

  public static int getUSER() {
    return JetspeedLink.USER;
  }

  public static String getDEFAULT_PAGE() {
    return "";
  }

  /**
   * deprecated methods from JetspeedTemplateLink.
   */

  /**
   * <p>
   * Set the portlet giving context to this Link object.
   * </p>
   *
   * This method is from JetspeedTemplateLink and is only here for backward
   * compatibility. This it should not be used for any new development. Also any
   * problems with this method will not be fixed
   *
   * @param portlet
   *          the name of the active portlet
   * @deprecated the name is confusing. Use @see(#forPaneById()) instead.
   */
  @Deprecated
  @Override
  public void setPortlet(Portlet portlet) {
    this.activePortlet = portlet;
  }

  /**
   * Add a portlet reference in the link.
   *
   * Note: This must be used with caution, since a portlet may exist may times
   * in a PSML. setPortletById() is the perfered method.
   *
   * @param portletName
   *          the name of the portlet to link to
   * @return a DynamicURI referencing the named portlet for easy link
   *         construction in template
   */
  @Override
  public DynamicURI setPortletByName(String portletName) {
    DynamicURI uri = null;
    try {
      uri = getRoot();
    } catch (Exception e) {
      logger.error("Exception", e);
      return null;
    }
    if ((portletName != null) && (portletName.length() > 0)) {
      uri.addPathInfo(JetspeedResources.PATH_PORTLET_KEY, portletName);
    }
    return uri;
  }

  /**
   * Methods required by ApplictionTool interface
   *
   */

  /**
   * This will initialise a JetspeedLink object that was constructed with the
   * default constructor (ApplicationTool method).
   *
   * @param data
   *          assumed to be a RunData object
   */

  @Override
  public void init(Object data) {
    // Keeping init small and fast
    if (data instanceof JetspeedRunData) {
      this.rundata = (JetspeedRunData) data;
    } else {
      this.rundata = null;
    }
    profile = null;
    locator = null;
    initDone = false;
    return;
  }

  /**
   * Refresh method - does nothing
   */
  @Override
  public void refresh() {
    // empty
  }

  @Override
  public DynamicURI setMediaType(String mediaType) {
    return getLink(
      JetspeedLink.CURRENT,
      null,
      null,
      JetspeedLink.DEFAULT,
      null,
      null,
      null,
      mediaType);
  }
}
