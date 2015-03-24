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

package org.apache.jetspeed.services.profiler;

//java.util
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.commons.lang.SerializationUtils;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.CapabilityMapFactory;
import org.apache.jetspeed.om.profile.BasePSMLDocument;
import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileException;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.om.profile.psml.PsmlControl;
import org.apache.jetspeed.om.profile.psml.PsmlController;
import org.apache.jetspeed.om.profile.psml.PsmlPortlets;
import org.apache.jetspeed.om.profile.psml.PsmlSkin;
import org.apache.jetspeed.om.security.Group;
import org.apache.jetspeed.om.security.GroupRole;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.security.Role;
import org.apache.jetspeed.services.JetspeedSecurity;
import org.apache.jetspeed.services.PortalToolkit;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.PsmlManager;
import org.apache.jetspeed.services.ServiceHelper;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

/**
 * <p>
 * This is an implementation of the <code>Profiler</code> interface.
 * 
 * This implementation maps requests to profiles (PSML resources) based on
 * request parameters, requesting deviced capabilities, and the device's
 * language.
 * </p>
 * <p>
 * This service expects these properties to be set for correct operation:
 * <dl>
 * <dt>root</dt>
 * <dd>The webapp rel. path to the root profiling directory</dd>
 * <dt>resource.default</dt>
 * <dd>The default resource filename</dd>
 * <dt>resource.ext</dt>
 * <dd>The default resource filename extension</dd>
 * <dt>security</dt>
 * <dd>Use security flag</dd>
 * <dt>fallback.language</dt>
 * <dd>Use language configuration flag</dd>
 * <dt>fallback.country</dt>
 * <dd>Use country configuration flag</dd>
 * <dt>fallback.to.root</dt>
 * <dd>Continue falling back past media type flag</dd>
 * 
 * </dl>
 * </p>
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id: JetspeedProfilerService.java,v 1.56 2004/02/23 03:35:24 jford
 *          Exp $
 */

public class JetspeedProfilerService extends TurbineBaseService implements
    ProfilerService {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(JetspeedProfilerService.class.getName());

  // configuration keys
  private final static String CONFIG_RESOURCE_DEFAULT = "resource.default";

  private final static String CONFIG_RESOURCE_EXT = "resource.ext";

  private final static String CONFIG_SECURITY = "security";

  private final static String CONFIG_ROLE_FALLBACK = "rolefallback";

  private final static String CONFIG_NEWUSER_TEMPLATE = "newuser.template";

  private final static String CONFIG_NEWUSER_MEDIA = "newuser.media_types";

  private final static String CONFIG_FALLBACK_LANGUAGE = "fallback.language";

  private final static String CONFIG_FALLBACK_COUNTRY = "fallback.country";

  private final static String CONFIG_FALLBACK_TO_ROOT = "fallback.to.root";

  private final static String CONFIG_ROLE_MERGE = "rolemerge";

  private final static String CONFIG_ROLE_MERGE_CONTROL = "rolemerge.control";

  private final static String CONFIG_ROLE_MERGE_CONTROLLER =
    "rolemerge.controller";

  // default configuration values
  private final static String DEFAULT_CONFIG_RESOURCE_DEFAULT = "default";

  private final static String DEFAULT_CONFIG_RESOURCE_EXT = ".psml";

  private final static boolean DEFAULT_CONFIG_SECURITY = false;

  private final static boolean DEFAULT_CONFIG_ROLE_FALLBACK = true;

  private final static String DEFAULT_CONFIG_NEWUSER_TEMPLATE = null;

  private final static String[] DEFAULT_CONFIG_NEWUSER_MEDIA =
    { "html", "wml" };

  private final static String DEFAULT_CONFIG_ROLE_MERGE_CONTROL = "TabControl";

  private final static String DEFAULT_CONFIG_ROLE_MERGE_CONTROLLER =
    "TabController";

  private final static String PATH_EXTENSION_DELIMITER = ".";

  // messages
  private final static String MSG_MISSING_PARAMETER =
    "JetspeedProfilerService initialization failed. Missing parameter:";

  // pluggable Locator and Profile classes
  private Class profileClass = null;

  private Class locatorClass = null;

  // configuration parameters
  String root; // the root psml resource directory

  String resourceDefault; // the default name for a resource

  String resourceExt; // the default extension for a resource

  String rolemergeControl; // the default control used with merged role profiles

  String rolemergeController; // the default controller used with merged role
                              // profiles

  // MODIFIED: A. Kempf
  String newUserTemplate = DEFAULT_CONFIG_NEWUSER_TEMPLATE;

  boolean useSecurity = false; // use security features

  boolean useRoleFallback = true;

  boolean useFallbackLanguage = true;

  boolean useFallbackCountry = true;

  boolean useFallbackToRoot = false;

  boolean useRoleMerge = false;

  String mediaTypes[] = null;

  /**
   * This methode creates a wml profile and a html profile for a new user
   * --------------------------------------------------------------------------
   * last modified: 10/31/01 Andreas Kempf, Siemens ICM S CP PE, Munich mailto:
   * A.Kempf@web.de
   */
  @Override
  public Profile createProfile(RunData data, Profile profile)
      throws ProfileException {
    Profile current = null;
    CapabilityMap map;

    if (data == null) {
      map = CapabilityMapFactory.getDefaultCapabilityMap();
    } else {
      map = ((JetspeedRunData) data).getCapability();
    }
    String mediaType = getMediaType(data, map);

    // if template is null then use role-based psml
    if (newUserTemplate == null) {
      return current;
    }

    if (mediaTypes != null) {
      Profile dummy;
      for (int ix = 0; ix < mediaTypes.length; ix++) {
        dummy = createProfile(data, profile, mediaTypes[ix], newUserTemplate);
        if (mediaTypes[ix].equalsIgnoreCase(mediaType)) {
          current = dummy;
        }
      }
    }
    return current;
  }

  // --------------------------------------------------------------------------

  /**
   * This is the early initialization method called by the Turbine
   * <code>Service</code> framework
   * 
   * @param conf
   *          The <code>ServletConfig</code>
   * @exception throws a <code>InitializationException</code> if the service
   *            fails to initialize
   */
  @Override
  public synchronized void init(ServletConfig conf)
      throws InitializationException {

    // already initialized
    if (getInit()) {
      return;
    }

    try {
      initConfiguration();
    } catch (Exception e) {
      logger.error("Profiler: Failed to load Service ", e);
    }

    // initialization done
    setInit(true);

  }

  /**
   * This is the shutdown method called by the Turbine <code>Service</code>
   * framework
   */
  @Override
  public void shutdown() {
  }

  /**
   * get the Profile object using the Rundata state and capability map this is
   * the mapping functionality of the profiler
   * 
   * @param rundata
   *          the rundata object for the current request
   * @param cm
   *          the <code>CapabilityMap</code> of the current requesting device
   * @return a new Profile object
   */
  @Override
  public Profile getProfile(RunData data, CapabilityMap cm)
      throws ProfileException {
    JetspeedRunData rundata = (JetspeedRunData) data;
    Profile profile = fallbackProfile(rundata, cm);
    if (null == profile && useRoleFallback) {
      Vector profiles = new Vector();
      JetspeedUser user = rundata.getJetspeedUser();
      if (user != null) {
        try {
          String paramRole =
            rundata.getParameters().getString(Profiler.PARAM_ROLE);
          Iterator groupRoles = JetspeedSecurity.getRoles(user.getUserName());
          if (groupRoles != null) {
            while (groupRoles.hasNext()) {
              // note: this is an unordered list. will need to change db schema
              // to order it
              GroupRole gr = (GroupRole) groupRoles.next();
              rundata.getParameters().setString(
                Profiler.PARAM_ROLE,
                gr.getRole().getName());
              profile = fallbackProfile(rundata, cm);
              if (profile != null) {
                profiles.add(profile);
              }
              rundata.getParameters().remove(Profiler.PARAM_ROLE);
            }
            profile = mergeRoleProfiles(data, profiles);

            // If something went wrong with merging, attempt another fallback
            if (profile == null) {
              profile = fallbackProfile(rundata, cm);
            }
          }

          rundata.getParameters().setString(Profiler.PARAM_ROLE, paramRole);
        } catch (Exception e) {
          logger.error("Error getting profile", e);
          throw new ProfileException(e.toString());
        }
      }
    }
    return profile;
  }

  /**
   * Merge role profiles to create default profile. Resulting psml will be a set
   * of tabs. If role's psml is a tab control, each tab is placed in the
   * resulting psml as is. Otherwise, a new tab is created and psml is placed
   * there. In this case, tab name will be derived from role's name. For
   * example, if role name is "news", the resulting profile name will be
   * "News Home".
   * 
   * @param data
   * @param profiles
   *          Vector of profiles for all roles user is part of
   * @return Merged profile
   * @exception Exception
   */
  private Profile mergeRoleProfiles(RunData data, Vector profiles)
      throws Exception {
    Profile result = null;
    // If merge feature is not turned on, return
    // profile for the first role (if any)
    if (!this.useRoleMerge) {
      if (profiles.size() > 0) {
        result = (Profile) profiles.get(0);
      }
    }
    // Proceed with merging all profiles
    else if (profiles.size() > 0) {
      try {
        // Create an empty portlet container
        Portlets portlets = new PsmlPortlets();
        Control control = new PsmlControl();
        control.setName(this.rolemergeControl);
        portlets.setControl(control);
        Controller controller = new PsmlController();
        controller.setName(this.rolemergeController);
        portlets.setController(controller);

        // Set the skin
        Skin skin = new PsmlSkin();
        skin.setName(PortalToolkit.getSkin((String) null).getName());
        portlets.setSkin(skin);

        String mediaType = null;

        // Process each role profile
        int paneCount = 0;
        for (Iterator it = profiles.iterator(); it.hasNext();) {
          Profile roleProfile = (Profile) it.next();
          mediaType =
            mediaType == null ? roleProfile.getMediaType() : mediaType;
          Profile tmpProfile = (Profile) roleProfile.clone();
          Portlets tmpPortlets = tmpProfile.getDocument().getPortlets();

          // If topmost control is a tab control, then add each tab to the
          // container
          Control paneControl = tmpPortlets.getControl();
          if (paneControl != null
            && paneControl.getName().equals(this.rolemergeControl)) {
            for (int i = 0; i < tmpPortlets.getPortletsCount(); i++) {
              Portlets pane = tmpPortlets.getPortlets(i);
              pane.setLayout(null);
              portlets.addPortlets(pane);
              paneCount++;
            }
          }
          // Otherwise, add the contents of profile as a pane
          else {
            if (tmpPortlets.getTitle() == null) {
              String title =
                org.apache.turbine.util.StringUtils.firstLetterCaps(roleProfile
                  .getRoleName());
              tmpPortlets.setTitle(title + " Home");
            }
            tmpPortlets.setLayout(null);
            portlets.addPortlets(tmpPortlets);
            paneCount++;
          }

          if (logger.isDebugEnabled()) {
            logger.debug("Profiler: Processing profile for role "
              + roleProfile.getRoleName());
          }
        }

        // Create a new profile for the user
        ProfileLocator locator = createLocator();
        locator.setUser((JetspeedUser) data.getUser());
        locator.setMediaType(mediaType);
        locator.setName(this.resourceDefault + this.resourceExt);

        // Regenerate the portlet ids so they are unique
        org.apache.jetspeed.util.PortletUtils.regenerateIds(portlets);

        // Save the new profile to permament storage
        result = this.createProfile(locator, portlets);

      } catch (Exception e) {
        logger.error("Exception", e);
      }
    }

    return result;
  }

  /**
   * get the Profile object using the Rundata state and capability map this is
   * the mapping functionality of the profiler
   * 
   * @param rundata
   *          the rundata object for the current request
   * @param cm
   *          the <code>CapabilityMap</code> of the current requesting device
   * @return a new Profile object
   */
  protected Profile fallbackProfile(RunData data, CapabilityMap cm)
      throws ProfileException {
    try {
      JetspeedRunData rundata = (JetspeedRunData) data;
      Profile profile = createProfile();
      JetspeedUser user = rundata.getJetspeedUser();
      if (user == null) {
        return profile;
      }

      // get the media type from the capability map or rundata
      profile.setMediaType(getMediaType(rundata, cm));

      // Is it a group, role, or user resource?
      // It can only be one
      String param = rundata.getParameters().getString(Profiler.PARAM_GROUP);

      if (null != param) {
        // GROUP Resource
        profile.setGroup(JetspeedSecurity.getGroup(param));
      } else {
        param = rundata.getParameters().getString(Profiler.PARAM_ROLE);
        if (null != param) {
          // ROLE Resource
          if (user.hasLoggedIn()) // disallow role access for anonymous user
          {
            profile.setRole(JetspeedSecurity.getRole(param));
          } else {
            profile.setAnonymous(true);
            profile.setUser(user);
          }
        } else // it must be a user resource or anonymous resource
        {
          // accessing another user's resource
          param = rundata.getParameters().getString(Profiler.PARAM_USER);
          if (null != param) {

            if (param.equals(JetspeedSecurity.getAnonymousUserName())) {
              profile.setAnonymous(true);
            }
            if (user != null && user.getUserName().equals(param)) {
              profile.setUser(user);
            } else {
              profile.setUser(JetspeedSecurity.getUser(param));
            }
          } else {
            profile.setAnonymous(user.getUserName().equals(
              JetspeedSecurity.getAnonymousUserName()));
            profile.setUser(user);
          }
        }
      }

      // get resource name
      StringBuffer resource = new StringBuffer();
      param = rundata.getParameters().getString(Profiler.PARAM_PAGE);
      if (null == param) {
        // the default resource
        resource.append(resourceDefault);
        resource.append(resourceExt);
      } else { // a specific resource
        resource.append(param);
        if (-1 == param.indexOf(PATH_EXTENSION_DELIMITER)) {
          resource.append(resourceExt);
        }
      }
      profile.setName(resource.toString());

      // LANGUAGE
      getLanguageSettings(profile, rundata);

      PSMLDocument doc = fallback(profile);
      if (null != doc) {
        profile.setDocument(doc);
        return profile;
      }
    } catch (Exception e) {
      logger.error("Exception in fallbackProfile", e);
      throw new ProfileException(e.toString());
    }
    return null;
  }

  /**
   * get the Profile object using the Rundata state and capability map this is
   * the mapping functionality of the profiler
   * 
   * @param rundata
   *          the rundata object for the current request
   * @return a new Profile object
   */
  @Override
  public Profile getProfile(RunData rundata) throws ProfileException {
    CapabilityMap cm = null;

    if (rundata instanceof JetspeedRunData) {
      cm = ((JetspeedRunData) rundata).getCapability();
    } else {
      cm = CapabilityMapFactory.getCapabilityMap(rundata);
    }

    return getProfile(rundata, cm);
  }

  /**
   * get the Profile object using the Rundata state and specific mimetype
   * 
   * @deprecated Do not use a profiler method based on MimeType
   * 
   * @param rundata
   *          the rundata object for the current request
   * @param mt
   *          the <code>MimeType</code> of the current requesting device
   * @return a new Profile object
   */
  @Deprecated
  @Override
  public Profile getProfile(RunData data, MimeType mt) throws ProfileException {
    CapabilityMap cm = CapabilityMapFactory.getCapabilityMap(mt.toString());
    return getProfile(data, cm);
  }

  /**
   * get the Profile object using a profile locator
   * 
   * @param rundata
   *          The rundata object for the current request.
   * @param locator
   *          The locator containing criteria describing the profile.
   * @return a new Profile object
   */
  @Override
  public Profile getProfile(ProfileLocator locator) throws ProfileException {
    PSMLDocument doc = fallback(locator);
    Profile profile = createProfile(locator);
    profile.setDocument(doc);
    return profile;
  }

  /*
   * Gets the language and country parameters from the request using the Turbine
   * locale detector.
   * 
   * @param profile The profile object which is modified with the new language
   * settings.
   * 
   * @param rundata The request specific state.
   */
  protected void getLanguageSettings(Profile profile, RunData rundata) {
    String language =
      rundata.getParameters().getString(Profiler.PARAM_LANGUAGE);

    if (language != null) {
      // dont use locale based fall back
      profile.setLanguage(language);

      if (!language.equals("-1")) {
        String country =
          rundata.getParameters().getString(Profiler.PARAM_COUNTRY);
        if (country != null) {
          profile.setCountry(country);
        }
      }
    } else {
      Locale locale = (Locale) rundata.getUser().getTemp("locale");
      if (locale == null) {
        // Get the locale store it in the user object
        CustomLocalizationService locService =
          (CustomLocalizationService) ServiceUtil
            .getServiceByName(LocalizationService.SERVICE_NAME);
        locale = locService.getLocale(rundata);
        if (locale == null) {
          locale =
            new Locale(TurbineResources.getString(
              "locale.default.language",
              "en"), TurbineResources.getString("locale.default.country", "US"));
        }
        rundata.getUser().setTemp("locale", locale);
      }

      if (useFallbackLanguage) {
        profile.setLanguage(locale.getLanguage());
      }

      if (useFallbackCountry) {
        profile.setCountry(locale.getCountry());
      }
    }
  }

  /*
   * A basic profiler fallback algorithm that starts from the most specific
   * parameters, going to the least specific parameters. The PsmlManager
   * implementation is passed a list of ProfileLocators ordered from most
   * specific to least specific.
   * 
   * @param locator The profile locator criteria used to locate a profile.
   * 
   * @param rundata The request specific state.
   * 
   * @return The found psml document, or null if not found.
   */
  protected PSMLDocument fallbackList(ProfileLocator original, RunData rundata) {
    try {
      List locators = new LinkedList();
      ProfileLocator locator = (ProfileLocator) original.clone();

      locators.add(locator.clone());

      // remove country
      if (null != original.getCountry()) {
        locator.setCountry(null);
        locators.add(locator.clone());
      }

      // remove language
      if (null != original.getLanguage()) {
        locator.setLanguage(null);
        locators.add(locator.clone());
      }

      // fallback mediaType
      if (null != original.getMediaType()) {
        locator.setMediaType(null);
        locators.add(locator.clone());
      }

      if (null != original.getGroup()) {
        locator.setGroup(null);
        locators.add(locator.clone());
      } else if (null != original.getRole()) {
        locator.setRole(null);
        locators.add(locator.clone());
      } else if (null != original.getUser()) {
        locator.setUser(null);
        locators.add(locator.clone());
      }
      PSMLDocument doc = PsmlManager.getDocument(locators);
      return doc;

    } catch (CloneNotSupportedException e) {
      logger.error("Profiler: Could not clone profile locator object", e);
    }
    return null;
  }

  /*
   * A basic profiler fallback algorithm that starts from the most specific
   * parameters, going to the least specific parameters. The PsmlManager
   * implementation is passed a list of ProfileLocators ordered from most
   * specific to least specific.
   * 
   * This is alternate fallback algorithm.
   * 
   * @param locator The profile locator criteria used to locate a profile.
   * 
   * @return PSMLDocument The located document or null.
   */
  protected PSMLDocument fallback(ProfileLocator locator) {
    if (logger.isDebugEnabled()) {
      logger.debug("Profiler: fallback called with: " + locator);
    }

    PSMLDocument doc = PsmlManager.getDocument(locator);
    if (null != doc) {
      return doc;
    }

    // remove country
    if (null != locator.getCountry() && (!locator.getCountry().equals("-1"))) {
      locator.setCountry(null);
      doc = PsmlManager.getDocument(locator);
      if (null != doc) {
        return doc;
      }
    }

    // remove language
    if (null != locator.getLanguage() && (!locator.getLanguage().equals("-1"))) {
      locator.setLanguage(null);
      doc = PsmlManager.getDocument(locator);
      if (null != doc) {
        return doc;
      }
    }

    // fallback mediaType
    if (useFallbackToRoot) {
      if (null != locator.getMediaType()) {
        locator.setMediaType(null);
        doc = PsmlManager.getDocument(locator);
        if (null != doc) {
          return doc;
        }
      }
    }

    if (!useRoleFallback) {
      if (null != locator.getGroup()) {
        locator.setGroup(null);
        doc = PsmlManager.getDocument(locator);
        if (null != doc) {
          return doc;
        }
      } else if (null != locator.getRole()) {
        locator.setRole(null);
        doc = PsmlManager.getDocument(locator);
        if (null != doc) {
          return doc;
        }
      } else if (null != locator.getUser()) {
        locator.setUser(null);
        doc = PsmlManager.getDocument(locator);
        if (null != doc) {
          return doc;
        }
      }
    }
    return doc;

  }

  /**
   * Lookup the media type from the CapabilitMap. First the RunData is checked
   * for an explicit media-type request.
   * 
   * @param cm
   *          The <code>CapabilityMap</code> of the current requesting device.
   * @param rundata
   *          , The <code>RunData</code> turbine request context information.
   * @return a String, the unique name of the media type.
   */
  protected String getMediaType(RunData rundata, CapabilityMap cm) {
    String paramMediaType;
    String media = null;

    if (null != rundata) {
      paramMediaType =
        rundata.getParameters().getString(Profiler.PARAM_MEDIA_TYPE);
      if (null != paramMediaType) {
        return paramMediaType;
      }
    }

    if (cm != null) {
      media = cm.getPreferredMediaType();
    }

    return media;
  }

  /**
   * Loads the configuration parameters for this service from the
   * JetspeedResources.properties file.
   * 
   * @exception throws a <code>InitializationException</code> if the service
   *            fails to initialize
   */
  private void initConfiguration() throws InitializationException {
    profileClass = ServiceHelper.loadModelClass(this, "profile.impl");
    locatorClass = ServiceHelper.loadModelClass(this, "locator.impl");

    // get configuration parameters from Jetspeed Resources
    ResourceService serviceConf =
      ((TurbineServices) TurbineServices.getInstance())
        .getResources(ProfilerService.SERVICE_NAME);

    resourceDefault =
      serviceConf.getString(
        CONFIG_RESOURCE_DEFAULT,
        DEFAULT_CONFIG_RESOURCE_DEFAULT);

    resourceExt =
      serviceConf.getString(CONFIG_RESOURCE_EXT, DEFAULT_CONFIG_RESOURCE_EXT);
    if (-1 == resourceExt.indexOf(PATH_EXTENSION_DELIMITER)) {
      resourceExt = PATH_EXTENSION_DELIMITER + resourceExt;
    }

    useSecurity =
      serviceConf.getBoolean(CONFIG_SECURITY, DEFAULT_CONFIG_SECURITY);

    useRoleFallback =
      serviceConf
        .getBoolean(CONFIG_ROLE_FALLBACK, DEFAULT_CONFIG_ROLE_FALLBACK);

    newUserTemplate =
      serviceConf.getString(
        CONFIG_NEWUSER_TEMPLATE,
        DEFAULT_CONFIG_NEWUSER_TEMPLATE);

    useFallbackToRoot =
      serviceConf.getBoolean(CONFIG_FALLBACK_TO_ROOT, useFallbackToRoot);

    useFallbackLanguage =
      serviceConf.getBoolean(CONFIG_FALLBACK_LANGUAGE, useFallbackLanguage);

    useRoleMerge = serviceConf.getBoolean(CONFIG_ROLE_MERGE, useRoleMerge);

    rolemergeControl =
      serviceConf.getString(
        CONFIG_ROLE_MERGE_CONTROL,
        DEFAULT_CONFIG_ROLE_MERGE_CONTROL);

    rolemergeController =
      serviceConf.getString(
        CONFIG_ROLE_MERGE_CONTROLLER,
        DEFAULT_CONFIG_ROLE_MERGE_CONTROLLER);

    if (useFallbackLanguage == false) {
      useFallbackCountry = false;
    } else {
      useFallbackCountry =
        serviceConf.getBoolean(CONFIG_FALLBACK_COUNTRY, useFallbackCountry);
    }

    try {
      mediaTypes = serviceConf.getStringArray(CONFIG_NEWUSER_MEDIA);
    } catch (Exception e) {
      logger.error("Error getting media types", e);
    }

    if (null == mediaTypes || mediaTypes.length == 0) {
      mediaTypes = DEFAULT_CONFIG_NEWUSER_MEDIA;
    }
  }

  /**
   * Builds a dynamic URI based on the current profiler group/role/page
   * 
   * @param data
   *          The rundata object for the current request.
   * @param locator
   *          The description of the profile.
   * @return A new dynamic URI representing all profile parameters from the
   *         locator.
   */
  @Override
  public DynamicURI makeDynamicURI(RunData data, ProfileLocator locator)
      throws ProfileException {
    DynamicURI uri = new DynamicURI(data);

    // check mediatype to add to the uri
    String mtype = locator.getMediaType();
    if (null != mtype) {
      uri.addPathInfo(Profiler.PARAM_MEDIA_TYPE, mtype);
    }

    // check language to add to the uri
    String language = locator.getLanguage();
    if (null != language) {
      uri.addPathInfo(Profiler.PARAM_LANGUAGE, language);
    }

    // check language to add to the uri
    String country = locator.getCountry();
    if (null != country) {
      uri.addPathInfo(Profiler.PARAM_COUNTRY, country);
    }

    // check User, Group or Role to add to the uri
    JetspeedUser user = locator.getUser();
    if (null != user) {
      if (user.getUserName() != null) {
        uri.addPathInfo(Profiler.PARAM_USER, user.getUserName());
      }
    } else {
      Group group = locator.getGroup();
      if (null != group) {
        uri.addPathInfo(Profiler.PARAM_GROUP, group.getName());
      } else {
        Role role = locator.getRole();
        if (null != role) {
          uri.addPathInfo(Profiler.PARAM_ROLE, role.getName());
        }
      }
    }

    // check Page to add to the uri
    String page = locator.getName();
    if (null != page) {
      uri.addPathInfo(Profiler.PARAM_PAGE, page);
    }

    return uri;
  }

  /**
   * Creates a new Profile object that can be successfully managed by the
   * current Profiler implementation
   * 
   * @return A new Profile object
   */
  @Override
  public Profile createProfile() {
    return (Profile) ServiceHelper.createObject(this.profileClass);
  }

  /**
   * Creates a new Profile object for a specific locator.
   * 
   * @param locator
   *          The description of the profile.
   * @return A new Profile object
   */
  @Override
  public Profile createProfile(ProfileLocator locator) {
    Profile profile = (Profile) ServiceHelper.createObject(this.profileClass);
    profile.init(locator);
    return profile;
  }

  /**
   * Creates a new ProfileLocator object that can be successfully managed by the
   * current Profiler implementation
   * 
   * @return A new ProfileLocator object
   */
  @Override
  public ProfileLocator createLocator() {
    return (ProfileLocator) ServiceHelper.createObject(this.locatorClass);
  }

  /**
   * Create a new profile given a profile locator
   * 
   * This method assumes that you have cloned and regenerated the portlet ids if
   * the portlets come from another profile.
   * 
   * @param locator
   *          The description of the new profile to be created.
   * @param portlets
   *          The PSML tree
   */

  @Override
  public Profile createProfile(ProfileLocator locator, Portlets portlets)
      throws ProfileException {
    if (portlets == null) {
      portlets = new PsmlPortlets();
    }

    Profile profile = createProfile(locator);
    PSMLDocument doc = new BasePSMLDocument(null, portlets);
    profile.setDocument(doc);
    doc = PsmlManager.createDocument(profile);
    profile.setDocument(doc);
    return profile;
  }

  /**
   * Create a new profile. The profile parameter's document will be cloned.
   * 
   * @param rundata
   *          The rundata object for the current request.
   * @param profile
   *          The description of the new profile to be created.
   * @param contentType
   *          create a profile for the specific contentType
   * @param from
   *          create a profile by cloning the profile from the specific user (if
   *          null - turbine is used)
   * @return The newly created profile.
   *         ----------------------------------------------------------- Andreas
   *         Kempf, Siemens ICM S CP PE, Munich
   */

  /**
   * This methode creates a wml profile and a html profile for a new user
   */

  public Profile createProfile(RunData data, Profile profile,
      String contentType, String from) throws ProfileException {
    if ((contentType == null) || (contentType.length() < 2)) {
      contentType = "html";
    }

    if ((from == null) || (from.length() < 2)) {
      from = "turbine";
    }

    if ((null == profile.getDocument())
      || (!profile.getMediaType().equalsIgnoreCase(contentType))) {
      // locate the default resource

      // TODO: make this configurable

      try {
        ProfileLocator locator = createLocator();
        locator.setUser(JetspeedSecurity.getUser(from));

        locator.setMediaType(contentType);
        PSMLDocument doc = fallback(locator);

        if (doc != null) {
          PSMLDocument clonedDoc = (PSMLDocument) SerializationUtils.clone(doc);
          org.apache.jetspeed.util.PortletUtils.regenerateIds(clonedDoc
            .getPortlets());
          profile.setDocument(clonedDoc);
        }

        profile.setName(resourceDefault + resourceExt);

      } catch (Exception e) {
        logger.error("Error creating profile", e);
        throw new ProfileException(e.toString());
      }
    }

    try {
      profile.setMediaType(contentType);

      PSMLDocument doc = PsmlManager.createDocument(profile);
      Profile newProfile = (Profile) profile.clone();
      newProfile.setDocument(doc);

      return newProfile;
    } catch (CloneNotSupportedException e) {
      logger.error("Could not clone profile locator object: ", e);
    }
    return null;

  }

  /**
   * Create a new profile.
   * 
   * @deprecated Should be removed when old customizer is removed.
   * 
   * @param rundata
   *          The rundata object for the current request.
   * @param profile
   *          The description of the new profile to be created.
   * @param mt
   *          The specific mime type, which is converted to a mediatype.
   * @return The newly created profile.
   */
  @Deprecated
  @Override
  public Profile createProfile(RunData data, Profile profile, MimeType mt)
      throws ProfileException {
    CapabilityMap cm =
      CapabilityMapFactory.getCapabilityMap(mt.getContentType());
    profile.setMediaType(getMediaType(data, cm));
    return createProfile(data, profile);
  }

  /**
   * Removes a profile.
   * 
   * @param locator
   *          The profile locator criteria.
   */
  @Override
  public void removeProfile(ProfileLocator locator) {
    PsmlManager.removeDocument(locator);
  }

  /**
   * Query for a collection of profiles given a profile locator criteria.
   * 
   * @param locator
   *          The profile locator criteria.
   * @return The list of profiles matching the locator criteria.
   */
  @Override
  public Iterator query(QueryLocator locator) {
    return PsmlManager.query(locator);
  }

  /**
   * @see org.apache.jetspeed.services.profiler.ProfilerService#useRoleProfileMerging
   */
  @Override
  public boolean useRoleProfileMerging() {
    return this.useRoleFallback && this.useRoleMerge;
  }
}
