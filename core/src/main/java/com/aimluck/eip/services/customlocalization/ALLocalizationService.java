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
package com.aimluck.eip.services.customlocalization;

import java.util.Locale;

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
 * @author <a href="mailto:desmax74@yahoo.it">Dess?ｽ Massimiliano</a>
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

}
