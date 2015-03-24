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

package org.apache.jetspeed.services.template;

// java.io
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.services.Profiler;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationService;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.RunData;

/**
 * <p>
 * Implements all template location related operations. Template location
 * algorithms are different from the Velocity template location, since Jetspeed
 * has a specialized template directory structure. This is a fix to get us
 * through unti the TurbineTemplateService can locate resources by NLS and
 * mediatype. Then it can be removed
 * </p>
 * 
 * <p>
 * The directory structure is currently layout out in the following order:
 * /templateType/mediaType/LanguageCode/CountryCode
 * </p>
 * <p>
 * Example: /screens/html/en/US/resource.vm
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:rapahel@apache.org">Raphael Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spener</a>
 * @author <a href="mailto:kimptoc_mail@yahoo.com">Chris Kimpton</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: JetspeedTemplateLocatorService.java,v 1.22 2004/02/23 03:38:54
 *          jford Exp $
 */

public class JetspeedTemplateLocatorService extends TurbineBaseService
    implements TemplateLocatorService {
  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(JetspeedTemplateLocatorService.class.getName());

  private final static String CONFIG_TEMPLATE_ROOT = ".templateRoot";

  private final static String CONFIG_PORTLET_GLOBAL_SEARCH =
    ".portlet.global.search";

  private final static String CONFIG_HOT_DEPLOY = ".hot.deploy";

  private final static String DIR_SCREENS = "/screens";

  private final static String DIR_LAYOUTS = "/layouts";

  private final static String DIR_PORTLETS = "/portlets";

  private final static String DIR_CONTROLS = "/controls";

  private final static String DIR_CONTROLLERS = "/controllers";

  private final static String DIR_NAVIGATIONS = "/navigations";

  private final static String DIR_PARAMETERS = "/parameters";

  private final static String DIR_EMAILS = "/emails";

  private static final String PATH_SEPARATOR = "/";

  // messages
  private final static String MSG_MISSING_PARAMETER =
    "JetspeedTemplateLocatorService initialization failed. Missing parameter:";

  // Template Service Constants
  private static final String TEMPLATE_EXTENSION = "template.extension";

  private static final String DEFAULT_LAYOUT = "default.layout.template";

  // Template services
  private static VelocityService velocityService;

  private static JspService jspService;

  // the template root directories, webapp relative
  private String[] templateRoots;

  // check the file system if template not found in name cache
  private boolean hotDeploy = false;

  // template name cache
  private Map templateMap = null;

  // include screens when searching for portlet template
  private boolean useGlobalPortletSearch = false;

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

    initConfiguration();

    // initialization done
    setInit(true);
  }

  @Override
  public void init() throws InitializationException {
    logger.info("Late init for JetspeedTemplateLocatorService called");
    while (!getInit()) {
      // Not yet...
      try {
        Thread.sleep(100);
        logger.info("Waiting for init of JetspeedTemplateLocatorService...");
      } catch (InterruptedException ie) {
        logger.error("Exception", ie);
      }
    }
  }

  /**
   * This is the shutdown method called by the Turbine <code>Service</code>
   * framework
   */
  @Override
  public void shutdown() {
  }

  /**
   * Locate a screen template using Jetspeed template location algorithm,
   * searching by mediatype and language criteria extracted from the request
   * state in rundata.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * 
   * @return The path relative to the screens directory for the requested screen
   *         template, or null if not found.
   */
  @Override
  public String locateScreenTemplate(RunData data, String template) {
    List templatePaths = localizeTemplateName(data);
    Iterator i = templatePaths.iterator();
    String located = null;

    while (i.hasNext()) {
      String path = (String) i.next();
      located = locateTemplate(data, DIR_SCREENS, path, template);

      if (null != located) {
        return located;
      }
    }

    if (null == located) {
      // we have not found the requested sreen but still need to return
      // something, search for the default screen

      i = templatePaths.iterator();
      template = "/default." + getTemplateExtension(template);
      while (i.hasNext()) {
        String path = (String) i.next();
        located = locateTemplate(data, DIR_SCREENS, path, template);

        if (null != located) {
          return located;
        }
      }
    }

    return located;
  }

  /**
   * Locate a layout template using Jetspeed template location algorithm,
   * searching by mediatype and language criteria extracted from the request
   * state in rundata.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * 
   * @return The path relative to the layouts directory for the requested layout
   *         template, or null if not found.
   */
  @Override
  public String locateLayoutTemplate(RunData data, String template) {
    List templatePaths = localizeTemplateName(data);
    Iterator i = templatePaths.iterator();
    String located = null;

    while (i.hasNext()) {
      String path = (String) i.next();
      located = locateTemplate(data, DIR_LAYOUTS, path, template);

      if (null != located) {
        return located;
      }
    }

    if (null == located) {
      // we have not found the requested layout but still need to return
      // something, search for the default layout

      i = templatePaths.iterator();

      // template = "/default." + getTemplateExtension(template);
      template = getTemplateLayout(getTemplateExtension(template));
      while (i.hasNext()) {
        String path = (String) i.next();
        located = locateTemplate(data, DIR_LAYOUTS, path, template);

        if (null != located) {
          return located;
        }
      }
    }

    return located;
  }

  /**
   * Locate a controller template using Jetspeed template location algorithm,
   * searching by mediatype and language criteria extracted from the request
   * state in rundata.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * 
   * @return The path relative to the controllers directory for the requested
   *         controller template, or null if not found.
   */
  @Override
  public String locateNavigationTemplate(RunData data, String template) {
    List templatePaths = localizeTemplateName(data);
    Iterator i = templatePaths.iterator();

    while (i.hasNext()) {
      String path = (String) i.next();
      String located = locateTemplate(data, DIR_NAVIGATIONS, path, template);

      if (null != located) {
        return located;
      }
    }

    return null;
  }

  /**
   * Locate a portlet template using Jetspeed template location algorithm,
   * searching by mediatype and language criteria extracted from the request
   * state in rundata.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * 
   * @return The path relative to the portlets directory for the requested
   *         portlet template, or null if not found.
   */
  @Override
  public String locatePortletTemplate(RunData data, String template) {
    List templatePaths = localizeTemplateName(data);
    Iterator i = templatePaths.iterator();

    while (i.hasNext()) {
      String path = (String) i.next();
      String located = locateTemplate(data, DIR_PORTLETS, path, template);

      if (null != located) {
        return DIR_PORTLETS + located;
      }
    }

    // Use "wide" search when required
    if (useGlobalPortletSearch == true) {
      String located = locateScreenTemplate(data, template);
      if (located != null) {
        return DIR_SCREENS + located;
      }
    }

    return null;
  }

  /**
   * Locate a control template using Jetspeed template location algorithm,
   * searching by mediatype and language criteria extracted from the request
   * state in rundata.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * 
   * @return The path relative to the controls directory for the requested
   *         control template, or null if not found.
   */
  @Override
  public String locateControlTemplate(RunData data, String template) {
    List templatePaths = localizeTemplateName(data);
    Iterator i = templatePaths.iterator();

    while (i.hasNext()) {
      String path = (String) i.next();
      String located = locateTemplate(data, DIR_CONTROLS, path, template);

      if (null != located) {
        return DIR_CONTROLS + located;
      }
    }

    return null;
  }

  /**
   * Locate a controller template using Jetspeed template location algorithm,
   * searching by mediatype and language criteria extracted from the request
   * state in rundata.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * 
   * @return The path relative to the controllers directory for the requested
   *         controller template, or null if not found.
   */
  @Override
  public String locateControllerTemplate(RunData data, String template) {
    List templatePaths = localizeTemplateName(data);
    Iterator i = templatePaths.iterator();

    while (i.hasNext()) {
      String path = (String) i.next();
      String located = locateTemplate(data, DIR_CONTROLLERS, path, template);

      if (null != located) {
        return DIR_CONTROLLERS + located;
      }
    }

    return null;
  }

  /**
   * Locate an email template using Jetspeed template location algorithm,
   * searching by mediatype and language criteria extracted from the request
   * state in rundata.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * 
   * @return The path relative to the emails directory for the requested email
   *         template, or null if not found.
   */
  @Override
  public String locateEmailTemplate(RunData data, String template) {
    CustomLocalizationService locService =
      (CustomLocalizationService) ServiceUtil
        .getServiceByName(LocalizationService.SERVICE_NAME);
    return locateEmailTemplate(data, template, locService.getLocale(data));
  }

  /**
   * Locate an email template using Jetspeed template location algorithm,
   * searching by mediatype and language.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * @param locale
   *          The name of the language.
   * 
   * @return The path relative to the emails directory for the requested email
   *         template, or null if not found.
   */
  @Override
  public String locateEmailTemplate(RunData data, String template, Locale locale) {
    List templatePaths = localizeTemplateName(data, locale);
    Iterator i = templatePaths.iterator();

    while (i.hasNext()) {
      String path = (String) i.next();
      String located = locateTemplate(data, DIR_EMAILS, path, template);

      if (null != located) {
        return DIR_EMAILS + located;
      }
    }

    return null;
  }

  /**
   * Locate a parameter style template using Jetspeed template location
   * algorithm, searching by mediatype and language criteria extracted from the
   * request state in rundata.
   * 
   * @param data
   *          The rundata for the request.
   * @param template
   *          The name of the template.
   * 
   * @return The path relative to the portlets directory for the requested
   *         portlet template, or null if not found.
   */
  @Override
  public String locateParameterTemplate(RunData data, String template) {
    List templatePaths = localizeTemplateName(data);
    Iterator i = templatePaths.iterator();

    while (i.hasNext()) {
      String path = (String) i.next();
      String located = locateTemplate(data, DIR_PARAMETERS, path, template);

      if (null != located) {
        return DIR_PARAMETERS + located;
      }
    }

    return null;
  }

  /**
   * General template location algorithm. Starts with the most specific
   * resource, including mediatype + nls specification, and fallsback to least
   * specific.
   * 
   * @param data
   *          The rundata for the request.
   * @param resourceType
   *          The path specific to the resource type sought (eg /screens).
   * @param path
   *          The fullest path to the template based on simple NLS/mediatype
   *          directory.
   * @param template
   *          The name of the template.
   * 
   * @return the exact path to the template, or null if not found.
   */
  private String locateTemplate(RunData data, String resourceType, String path,
      String template) {
    String located = null;

    // Iterate through each of the template roots
    for (int i = 0; i < templateRoots.length; i++) {
      located =
        locateTemplate(data, resourceType, path, template, templateRoots[i]);
      if (located != null) {
        break;
      }
    }

    return located;
  }

  /**
   * General template location algorithm. Starts with the most specific
   * resource, including mediatype + nls specification, and fallsback to least
   * specific.
   * 
   * @param data
   *          The rundata for the request.
   * @param resourceType
   *          The path specific to the resource type sought (eg /screens).
   * @param path
   *          The fullest path to the template based on simple NLS/mediatype
   *          directory.
   * @param template
   *          The name of the template.
   * @param templateRoot
   *          The template root to be searched.
   * 
   * @return the exact path to the template, or null if not found.
   */
  private String locateTemplate(RunData data, String resourceType, String path,
      String template, String templateRoot) {
    String finalPath;

    // make sure resourceType doesn't end with "/" but starts with "/"
    if (resourceType.endsWith(PATH_SEPARATOR)) {
      resourceType = resourceType.substring(0, resourceType.length() - 1);
    }
    if (!resourceType.startsWith(PATH_SEPARATOR)) {
      resourceType = PATH_SEPARATOR + resourceType;
    }
    // make sure path doesn't end with "/" but starts with "/"
    if (path.endsWith(PATH_SEPARATOR)) {
      path = path.substring(0, path.length() - 1);
    }
    if (!path.startsWith(PATH_SEPARATOR)) {
      path = PATH_SEPARATOR + path;
    }
    // make sure template starts with "/"
    if (!template.startsWith(PATH_SEPARATOR)) {
      template = PATH_SEPARATOR + template;
    }

    StringBuffer fullPath = new StringBuffer(templateRoot);

    if (!templateRoot.endsWith(PATH_SEPARATOR)) {
      fullPath.append(PATH_SEPARATOR);
    }

    fullPath.append(getTemplateExtension(template));
    fullPath.append(resourceType);

    String basePath = fullPath.toString();
    String realPath = null;
    String workingPath = null;

    do {
      workingPath = path + template;
      realPath = TurbineServlet.getRealPath(basePath + workingPath);

      // the current template exists in cache, return the corresponding path
      if (templateExists(realPath, true)) {
        if (logger.isDebugEnabled()) {
          logger.debug("TemplateLocator: template exists in cache: "
            + realPath
            + " returning "
            + workingPath);
        }

        return workingPath;
      } else if (this.hotDeploy == true) {
        // Try to locate it directly on file system, perhaps it was recently
        // added
        if (templateExists(realPath, false)) {
          if (logger.isDebugEnabled()) {
            logger
              .debug("TemplateLocator: template exists on the file system: "
                + realPath
                + " returning "
                + workingPath);
          }

          // add it to the map
          // templateMap.put(workingPath, null);
          templateMap.put(realPath, null);

          return workingPath;
        }
      }
      // else strip path of one of its components and loop
      int pt = path.lastIndexOf(PATH_SEPARATOR);
      if (pt > -1) {
        path = path.substring(0, pt);
      } else {
        path = null;
      }
    } while (path != null);

    return null;
  }

  /**
   * Helper function for template locator to find a localized (NLS) resource.
   * Considers both language and country resources as well as all the possible
   * media-types for the request
   * 
   * @param data
   *          The rundata for the request.
   * 
   * @return The possible paths to a localized template ordered by descending
   *         preference
   */
  private List localizeTemplateName(RunData data) {
    return localizeTemplateName(data, null);
  }

  /**
   * Helper function for template locator to find a localized (NLS) resource.
   * Considers both language and country resources as well as all the possible
   * media-types for the request
   * 
   * @param data
   *          The rundata for the request.
   * @param locale
   *          The locale for the request.
   * 
   * @return The possible paths to a localized template ordered by descending
   *         preference
   */
  private List localizeTemplateName(RunData data, Locale inLocale) {
    List templates = new ArrayList();
    Locale tmplocale = null;

    if (inLocale != null) {
      tmplocale = inLocale;
    } else {
      CustomLocalizationService locService =
        (CustomLocalizationService) ServiceUtil
          .getServiceByName(LocalizationService.SERVICE_NAME);
      tmplocale = locService.getLocale(data);
    }

    // Get the locale store it in the user object
    if (tmplocale == null) {
      tmplocale =
        new Locale(
          TurbineResources.getString("locale.default.language", "en"),
          TurbineResources.getString("locale.default.country", "US"));
    }

    if (data.getUser() != null) {
      data.getUser().setTemp("locale", tmplocale);
    }

    StringBuffer templatePath = new StringBuffer();

    // retrieve all the possible media types
    String type =
      data.getParameters().getString(Profiler.PARAM_MEDIA_TYPE, null);
    List types = new ArrayList();
    CapabilityMap cm = ((JetspeedRunData) data).getCapability();

    // Grab the Locale from the temporary storage in the User object
    Locale locale =
      data.getUser() != null
        ? (Locale) data.getUser().getTemp("locale")
        : tmplocale;
    String language = locale.getLanguage();
    String country = locale.getCountry();

    if (null != type) {
      types.add(type);
    } else {
      Iterator i = cm.listMediaTypes();
      while (i.hasNext()) {
        types.add(i.next());
      }
    }

    Iterator typeIterator = types.iterator();

    while (typeIterator.hasNext()) {
      type = (String) typeIterator.next();

      if ((type != null) && (type.length() > 0)) {
        templatePath.append(PATH_SEPARATOR).append(type);
      }

      if ((language != null) && (language.length() > 0)) {
        templatePath.append(PATH_SEPARATOR).append(language);
      }

      if ((country != null) && (country.length() > 0)) {
        templatePath.append(PATH_SEPARATOR).append(country);
      }

      templates.add(templatePath.toString());
      templatePath.setLength(0);
    }

    return templates;
  }

  /**
   * Returns the extension for the specified template
   * 
   * @param template
   *          the template name to scan for an extension
   * @return the template extension if it exists or the default template
   *         extension
   */
  private String getTemplateExtension(String template) {
    String ext = TurbineTemplate.getDefaultExtension();

    int idx = template.lastIndexOf(".");

    if (idx > 0) {
      ext = template.substring(idx + 1);
    }

    return ext;
  }

  /**
   * Checks for the existence of a template resource given a key. The key are
   * absolute paths to the templates, and are cached in a template cache for
   * performance.
   * 
   * @param key
   *          The absolute path to the template resource.
   * 
   * @return True when the template is found, otherwise false.
   */
  public boolean templateExists(String templateKey, boolean useNameCache) {
    if (null == templateKey) {
      return false;
    }

    if (useNameCache == true) {
      return templateMap.containsKey(templateKey);
    }

    return (new File(templateKey).exists());
  }

  /**
   * Loads the configuration parameters for this service from the
   * JetspeedResources.properties file.
   * 
   * @exception throws a <code>InitializationException</code> if the service
   *            fails to initialize
   */
  private void initConfiguration() throws InitializationException {

    templateRoots =
      JetspeedResources.getStringArray(TurbineServices.SERVICE_PREFIX
        + TemplateLocatorService.SERVICE_NAME
        + CONFIG_TEMPLATE_ROOT);

    if ((templateRoots == null) || (templateRoots.length == 0)) {
      throw new InitializationException(MSG_MISSING_PARAMETER
        + CONFIG_TEMPLATE_ROOT);
    }

    templateMap = new HashMap();

    for (int i = 0; i < templateRoots.length; i++) {
      String templateRoot = templateRoots[i];

      if (!templateRoot.endsWith(PATH_SEPARATOR)) {
        templateRoot = templateRoot + PATH_SEPARATOR;
      }

      if (logger.isDebugEnabled()) {
        logger.debug("TemplateLocator: Adding templateRoot:" + templateRoot);
      }

      // traverse starting from the root template directory and add resources
      String templateRootPath = TurbineServlet.getRealPath(templateRoot);
      if (null != templateRootPath) {
        loadNameCache(templateRootPath, "");
      }
    }

    velocityService =
      (VelocityService) TurbineServices.getInstance().getService(
        VelocityService.SERVICE_NAME);

    jspService =
      (JspService) TurbineServices.getInstance().getService(
        JspService.SERVICE_NAME);

    useGlobalPortletSearch =
      JetspeedResources.getBoolean(TurbineServices.SERVICE_PREFIX
        + TemplateLocatorService.SERVICE_NAME
        + CONFIG_PORTLET_GLOBAL_SEARCH, false);

    hotDeploy =
      JetspeedResources.getBoolean(TurbineServices.SERVICE_PREFIX
        + TemplateLocatorService.SERVICE_NAME
        + CONFIG_HOT_DEPLOY, true);

  }

  /**
   * Loads the template name cache map to accelerate template searches.
   * 
   * @param path
   *          The template
   * @param name
   *          just the name of the resource
   */
  private void loadNameCache(String path, String name) {
    File file = new File(path);
    if (file.isFile()) {
      // add it to the map
      templateMap.put(path, null);
    } else {
      if (file.isDirectory()) {
        if (!path.endsWith(File.separator)) {
          path += File.separator;
        }

        String list[] = file.list();

        // Process all files recursivly
        for (int ix = 0; list != null && ix < list.length; ix++) {
          loadNameCache(path + list[ix], list[ix]);
        }
      }
    }
  }

  /**
   * Correctly locate the default layout based on the default.layout.template
   * property of the appropriate template service.
   * 
   * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
   */
  private String getTemplateLayout(String extension) {
    String dftLayout = "/default." + extension;

    Configuration velocityCfg = null;
    Configuration jspCfg = null;
    if (velocityService != null) {
      velocityCfg = velocityService.getConfiguration();
    }

    if (jspService != null) {
      jspCfg = jspService.getConfiguration();
    }

    if (velocityCfg != null
      && velocityCfg.getString(TEMPLATE_EXTENSION).indexOf(extension) > -1) {
      return velocityCfg.getString(DEFAULT_LAYOUT, dftLayout);
    } else if (jspCfg != null
      && jspCfg.getString(TEMPLATE_EXTENSION).indexOf(extension) > -1) {
      return jspCfg.getString(DEFAULT_LAYOUT, dftLayout);
    } else {
      return dftLayout;
    }
  }
}
