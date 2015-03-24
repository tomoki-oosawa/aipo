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
package org.apache.jetspeed.services.customlocalization;

import java.util.Locale;

import org.apache.turbine.services.localization.TurbineLocalizationService;
import org.apache.turbine.util.RunData;

import org.apache.jetspeed.om.security.JetspeedUser;

/**
 * <p>This class is the single point of access to all localization
 * resources.  It caches different ResourceBundles for different
 * Locales.</p>
 * 
 * Work in the same way of turbine except for getLocale(RunData data),
 * turbine read the accept-language header in a http request,
 * instead this method read the user.getPerm("language")
 * from the RunData to obtain the language choice by the user
 * without the browser language rule.
 * If a user not change the language with a ChangeLanguagePortlet,
 * and a user.getPerm("language")are not set,
 * the "Accept-Language" header are read.
 * 
 * @author <a href="mailto:desmax74@yahoo.it">DessMassimiliano</a>
 * @version $Id: JetspeedLocalizationService.java,v 1.8 2004/02/23 03:49:33 jford Exp $
 */
public class JetspeedLocalizationService extends TurbineLocalizationService implements CustomLocalizationService
{

    /**
     * Initialize list of default bundle names.
     * 
     * @param ignored
     *
    protected void initBundleNames(String ignored[])
    {
        bundleNames = TurbineResources.getStringArray("locale.default.bundles");
        String name = TurbineResources.getString("locale.default.bundle");
        if (name != null && name.length() > 0)
        {
            if (bundleNames == null || bundleNames.length <= 0)
            {
                bundleNames = (new String[] {name});
            }
            else
            {
                String array[] = new String[bundleNames.length + 1];
                array[0] = name;
                System.arraycopy(bundleNames, 0, array, 1, bundleNames.length);
                bundleNames = array;
            }
        }
        if (bundleNames == null)
        {
            bundleNames = new String[0];
        }
    }
    */
    
    /**
     * Call getDefaultBundleName() of turbine
     * 
     * @return 
     */
/*    
    public String getDefaultBundleName()
    {
        return bundleNames.length > 0 ? bundleNames[0] : "";
    }
*/

    /**
     * This method read if a user has set getPerm("language")
     * to use another language or not.
     * If not set , accept-language of the request are returned.
     * 
     * @param data
     * @return 
     */
    public final Locale getLocale(RunData data)
    {
        JetspeedUser user = (JetspeedUser) data.getUser();
        if (user == null)
        {
            return getLocale(data.getRequest().getHeader(CustomLocalizationService.ACCEPT_LANGUAGE));
        }
        else
        {
            String lang = "null";

            try
            {
                if (user.getPerm("language") == null)
                {
                    return getLocale(data.getRequest().getHeader(CustomLocalizationService.ACCEPT_LANGUAGE));
                }
                else
                {
                    lang = user.getPerm("language").toString();
                    Locale locale = new Locale(lang, "");
                    return locale;
                }
            }
            catch (Exception use)
            {
                return getLocale(data.getRequest().getHeader(CustomLocalizationService.ACCEPT_LANGUAGE));
            }
        }
    }

    /**
     * Call searchKey(Locale locale, String key) to search the key in the Bundles
     * 
     * @param bundleName
     * @param locale
     * @param key
     * @return 
     *
    public String getString(String bundleName, Locale locale, String key)
    {
        return searchKey(locale,key);
    }
    */

    /**
     * Search the key in the first bundle, if is not found
     * search in the list of bundles
     * 
     * @param locale
     * @param key
     * @return 
     *
    private  String searchKey(Locale locale, String key)
    {
        String keyTemp=null;
        int i=0;
        boolean find=false;
        ResourceBundle rb ;

        while ((null==keyTemp)&&(!find)&&(i<bundleNames.length))
        {
            rb = getBundle(bundleNames[i], locale);
            keyTemp=super.getStringOrNull(rb,key);
            if (keyTemp!=null)
            {
                find=true;
            }
            else i++;
        }
        return keyTemp;
    }


    private String bundleNames[];
    */

}
