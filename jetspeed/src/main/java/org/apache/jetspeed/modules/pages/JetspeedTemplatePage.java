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

package org.apache.jetspeed.modules.pages;

// Turbine Modules
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.services.Registry;
import org.apache.jetspeed.services.TemplateLocator;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.util.MimeType;
import org.apache.turbine.modules.pages.DefaultPage;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;

/**
 * When building sites using templates, Screens need only be defined for
 * templates which require dynamic (database or object) data.
 * 
 * <p>
 * 
 * This page can be used on sites where the number of Screens can be much less
 * than the number of templates. The templates can be grouped in directories
 * with common layouts. Screen modules are then expected to be placed in
 * packages corresponding with the templates' directories and follow a specific
 * naming scheme.
 * 
 * <p>
 * 
 * The template parameter is parsed and and a Screen whose package matches the
 * templates path and shares the same name minus any extension and beginning
 * with a capital letter is searched for. If not found, a Screen in a package
 * matching the template's path with name Default is searched for. If still not
 * found, a Screen with name Default is looked for in packages corresponding to
 * parent directories in the template's path until a match is found.
 * 
 * <p>
 * 
 * For example if data.getParameters().getString("template") returns
 * /about_us/directions/driving.wm, the search follows
 * about_us.directions.Driving, about_us.directions.Default, about_us.Default,
 * Default, WebMacroSiteScreen (i.e. the default screen set in
 * TurbineResources).
 * 
 * <p>
 * 
 * Only one Layout module is used, since it is expected that any dynamic content
 * will be placed in navigations and screens. The layout template to be used is
 * found in a similar way to the Screen. For example the following paths will be
 * searched in the layouts subdirectory: /about_us/directions/driving.wm,
 * /about_us/directions/default.wm, /about_us/default.wm, /default.wm, where wm
 * is the value of the template.default.extension property.
 * 
 * <p>
 * 
 * This approach allows a site with largely static content to be updated and
 * added to regularly by those with little Java experience.
 * 
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally </a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson </a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer </a>
 */
public class JetspeedTemplatePage extends DefaultPage {
  private static int httpLifetime = JetspeedResources.getInt(
    "http.lifetime",
    -1);

  /**
   * Static initialization of the logger for this class
   */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(JetspeedTemplatePage.class.getName());

  /**
   * Works with TemplateService to set up default templates and corresponding
   * class modules.
   * 
   * @param data
   *          Turbine information.
   * @exception Exception
   *              , a generic exception.
   */
  @Override
  protected void doBuildBeforeAction(RunData data) throws Exception {
    switch (httpLifetime) {
      case -1:
        break;
      case 0:
        Object isImageRequest =
          data.getRequest().getAttribute(
            "com.aimluck.eip.util.ALSessionUtils.isImageRequest");
        if (isImageRequest == null || isImageRequest.toString().length() == 0) {
          data.getResponse().setHeader("Cache-Control", "no-cache");
          data.getResponse().setHeader("Pragma", "no-cache");
          data.getResponse().setDateHeader("Expires", 0);
          data.getResponse().setDateHeader(
            "Last-Modified",
            System.currentTimeMillis());
        }
        break;
      default:
        data
          .getResponse()
          .setHeader("Cache-Control", "max-age=" + httpLifetime);
        data.getResponse().setDateHeader(
          "Expires",
          System.currentTimeMillis() + (httpLifetime * 1000));
        data.getResponse().setDateHeader(
          "Last-Modified",
          System.currentTimeMillis());
        break;
    }

    // Set the ContentType of the page
    CapabilityMap cm = ((JetspeedRunData) data).getCapability();
    MimeType mime = cm.getPreferredType();
    String characterSet =
      JetspeedResources.getString(
        JetspeedResources.CONTENT_ENCODING_KEY,
        "utf-8");
    if (mime != null) {
      data.setContentType(mime.getContentType());
      // 理由等：同じ Content-Type で異なる CharacterSet を指定できるように，
      // MediaTypeEntry の取得キーとして，cm.getPreferredMediaType() を使用した．
      // MediaTypeEntry media = (MediaTypeEntry) Registry.getEntry(
      // Registry.MEDIA_TYPE, mime.getCode());
      MediaTypeEntry media =
        (MediaTypeEntry) Registry.getEntry(Registry.MEDIA_TYPE, cm
          .getPreferredMediaType());
      if (media != null && media.getCharacterSet() != null) {
        characterSet = media.getCharacterSet();
      }
    }
    data.setCharSet(characterSet);

    if (logger.isDebugEnabled()) {
      logger.debug("JetspeedTemplatePage: Setting type to: "
        + cm.getPreferredType().getContentType()
        + "; charset="
        + JetspeedResources.getString(
          JetspeedResources.CONTENT_ENCODING_KEY,
          "utf-8"));
    }
  }

  /**
   * Works with TemplateService to set up default templates and corresponding
   * class modules.
   * 
   * @param data
   *          Turbine information.
   * @exception Exception
   *              , a generic exception.
   */
  @Override
  protected void doBuildAfterAction(RunData data) throws Exception {
    // Either template or screen should be guaranteed by the SessionValidator
    // It is occasionally better to specify the screen instead of template
    // in cases where multiple Screens map to one template. The template
    // is hardcoded into the Screen in this instance. In this case this
    // action is skipped.
    if (!data.hasScreen()) {
      // if only a screen but no template is specified, then we need to display
      // a legacy ecs screen --> screenTemplate = ECS
      if (data.getTemplateInfo().getScreenTemplate() == null) {
        String screen = TurbineTemplate.getDefaultScreen();
        data.setScreenTemplate(screen);
      }

      String ext = TurbineTemplate.getDefaultExtension();

      String template = data.getTemplateInfo().getScreenTemplate();

      // save the initial requested template before mangling it
      ((JetspeedRunData) data).setRequestedTemplate(template);

      if (template.lastIndexOf('.') < 0) {
        template = template + "." + ext;
      }
      if (logger.isDebugEnabled()) {
        logger.debug("JetspeedTemplatePage: requested template = " + template);
      }

      // get real path now - this is a fix to get us thru 1.3a2
      // when the TurbineTemplateService can locate resources by NLS and
      // mediatype,
      // then it can be removed
      String locatedScreen =
        TemplateLocator.locateScreenTemplate(data, template);
      data.setScreenTemplate(locatedScreen);
      if (logger.isDebugEnabled()) {
        logger.debug("JetspeedTemplatePage: calculated template = "
          + locatedScreen);
      }

      // 理由等：レイアウトテンプレートを動的に変更不可であったため，
      // ユーザーからのリクエストで変更可能にした．
      String layout_template =
        data.getParameters().getString("layout_template");
      if (layout_template != null && (!layout_template.equals(""))) {
        layout_template = "/" + layout_template + ".vm";
      } else {
        layout_template = template;
      }
      String layout =
        TemplateLocator.locateLayoutTemplate(data, layout_template);
      data.setLayoutTemplate(layout);

      if (logger.isDebugEnabled()) {
        logger.debug("JetspeedTemplatePage: layoutTemplate is finally "
          + layout);
      }

      String screen = TurbineTemplate.getScreenName(template);
      if (screen == null) {
        throw new Exception("Screen could not be determined. \n"
          + "No matches were found by TemplateService and the \n"
          + "services.TurbineTemplateService.default.screen \n"
          + "property was not set.");
      }
      data.setScreen(screen);
    }
  }

}
