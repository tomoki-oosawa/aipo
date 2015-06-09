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

import org.apache.turbine.services.TurbineServices;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.turbine.util.RunData;

/**
 * Custom Localization allows for the localization properties to changed dynamically,
 * without shutting down the application server. 
 *
 * @author <a href="mailto:massimiliano.dessi@gruppoatlantis.it">Dess� Massimiliano</a>
 * @version $Id: CustomLocalization.java,v 1.4 2004/02/23 03:49:33 jford Exp $
 */
public abstract class CustomLocalization 
{

    public CustomLocalization(){}

    public static String getString(String bundleName, Locale locale, String key)
    {
        return getService().getString(bundleName, locale, key);
    }

    public static String getString(String key, RunData data)
    {
        return getService().getString(null, getLocale(data), key);
    }

    public static ResourceBundle getBundle( RunData data )
    {
        return getService().getBundle(data);
    }

    public static Locale getLocale(RunData data)
    {
        return getService().getLocale(data);
    }

    public static String getDefaultBundleName()
    {
        return getService().getDefaultBundleName();
    }

   protected static final CustomLocalizationService getService()
    {
        return (CustomLocalizationService) TurbineServices.getInstance().getService(CustomLocalizationService.SERVICE_NAME);
    }
}
