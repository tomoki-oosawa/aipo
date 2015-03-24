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
package com.aimluck.eip.services.customlocalization;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.services.customlocalization.CustomLocalizationService;
import org.apache.turbine.services.localization.TurbineLocalizationService;
import org.apache.turbine.util.RunData;

/**
 * <p>
 * This class is the single point of access to all localization resources. It
 * caches different ResourceBundles for different Locales.
 * </p>
 * 
 * Work in the same way of turbine except for getLocale(RunData data), turbine
 * read the accept-language header in a http request, instead this method read
 * the user.getPerm("language") from the RunData to obtain the language choice
 * by the user without the browser language rule. If a user not change the
 * language with a ChangeLanguagePortlet, and a user.getPerm("language")are not
 * set, the "Accept-Language" header are read.
 * 
 * @author <a href="mailto:desmax74@yahoo.it">Dess?ス Massimiliano</a>
 * @version $Id: JetspeedLocalizationService.java,v 1.8 2004/02/23 03:49:33
 *          jford Exp $
 */
public class ALLocalizationService extends TurbineLocalizationService implements
    CustomLocalizationService {

  /**
   * This method read if a user has set getPerm("language") to use another
   * language or not. If not set , accept-language of the request are returned.
   * 
   * @param data
   * @return
   */
  @Override
  public final Locale getLocale(RunData data) {
    JetspeedUser user = (JetspeedUser) data.getUser();
    if (user == null) {
      return getLocale(data.getRequest().getHeader(
        CustomLocalizationService.ACCEPT_LANGUAGE));
    } else {
      String lang = "null";

      try {
        if (user.getPerm("language") == null) {
          return getLocale(data.getRequest().getHeader(
            CustomLocalizationService.ACCEPT_LANGUAGE));
        } else {
          lang = user.getPerm("language").toString();
          Locale locale = new Locale(lang, "");
          return locale;
        }
      } catch (Exception use) {
        return getLocale(data.getRequest().getHeader(
          CustomLocalizationService.ACCEPT_LANGUAGE));
      }
    }
  }

  @Override
  protected void initBundleNames(String ignored[]) {
    super.initBundleNames(ignored);
  }

  @Override
  public ResourceBundle getBundle(String bundleName, Locale locale) {
    try {
      return super.getBundle(bundleName, locale);
    } catch (MissingResourceException e) {
      return super.getBundle(bundleName, Locale.JAPANESE);
    }
  }

}
