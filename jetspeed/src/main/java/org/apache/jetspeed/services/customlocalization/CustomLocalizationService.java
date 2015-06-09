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

import org.apache.turbine.services.localization.LocalizationService;
import org.apache.turbine.util.RunData;

/**
 * Custom Localization allows for the localization properties to changed dynamically,
 * without shutting down the application server. 
 *
 * @author <a href="mailto:massimiliano.dessi@gruppoatlantis.it">DessMassimiliano</a>
 * @version $Id: CustomLocalizationService.java,v 1.5 2004/02/23 03:49:33 jford Exp $
 */
public interface CustomLocalizationService extends LocalizationService
{
    /**
     * Get the locale from the session first, then fallback to normal request headers.
     * 
     * @param data
     * @return Current locale based on state.
     */
    Locale getLocale(RunData data);
    
    /**
     * Get the resource bundle given the state of the current request.
     * 
     * @param data
     * @return The matched resource bundled.
     */
    ResourceBundle getBundle(RunData data);
          
    
    public static final String SERVICE_NAME = "LocalizationService";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
}
