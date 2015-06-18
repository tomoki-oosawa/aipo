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
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;



/**
 * Custom localization tool.
 *
 * @author <a href="mailto:desmax74@yahoo.it">Dess� Massimiliano</a>
 * @author <a href="mailto:massimiliano.dessi@gruppoatlantis.it">Dess� Massimiliano</a>
 * @author <a href="mailto:massimiliano.dessi@gruppoatlantis.com">Dess� Massimiliano</a>
 * @version $Id: CustomLocalizationTool.java,v 1.5 2004/02/23 03:49:33 jford Exp $
 */
public class CustomLocalizationTool implements ApplicationTool
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(CustomLocalizationTool.class.getName());    
    
    public CustomLocalizationTool()
    {
        refresh();
    }

    public String get(String key)
    {
        try
        {
            String s = CustomLocalization.getString(getBundleName(null), getLocale(), key);
            return s;
        }
        catch (MissingResourceException noKey)
        {
            logger.error("Exception", noKey);
        }
        return null;
    }

    /**
     * Gets localized key from the default bundle and the provided locale.
     * Returns <code>null</code> if the resource is missing.
     *
     * @param key string to translate.
     * @param locale prefered locale for translation.
     * @return localized key.
     */
    public String get(String key, Locale locale)
    {
        if (locale == null)
        {
            locale = getLocale();
        }

        String s = null;
        try
        {
            s = CustomLocalization.getString(getBundleName(null), locale, key);
        }
        catch (MissingResourceException noKey)
        {
            logger.error("Exception", noKey);
        }

        return s;
    }


    public Locale getLocale()
    {
        return locale;
    }

    protected String getBundleName(Object data)
    {
        return CustomLocalization.getDefaultBundleName();
    }

    public final void init(Object data)
    {
        if (data instanceof RunData)
        {
            locale = CustomLocalization.getLocale((RunData)data);
            bundleName = getBundleName(data);
        }
    }

    public void refresh()
    {
        locale = null;
        bundle = null;
        bundleName = null;
    }

    protected Locale locale;
    private ResourceBundle bundle;
    private String bundleName;
    boolean debug;
}
