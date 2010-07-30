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

package org.apache.jetspeed.services;

import org.apache.jetspeed.services.template.TemplateLocatorService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

import java.util.Locale;

/**
 * <P>This is a commodity static accessor class around the 
 * <code>TemplateLocatorService</code> interface</P>
 * 
 * @see org.apache.jetspeed.services.template.TemplateLocatorService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:kimptoc_mail@yahoo.com">Chris Kimpton</a>
 * @version $Id: TemplateLocator.java,v 1.5 2004/02/23 04:00:57 jford Exp $
 */
public class TemplateLocator
{
    /** 
     * Commodity method for getting a reference to the service
     * singleton
     */
    private static TemplateLocatorService getService()
    {
        return (TemplateLocatorService)TurbineServices
                .getInstance()
                .getService(TemplateLocatorService.SERVICE_NAME);
    }

    /**
     * @see TemplateLocatorService#locateScreenTemplate
     */
    public static String locateScreenTemplate(RunData data, String template)
    {
        return getService().locateScreenTemplate(data, template);
    }

    /**
     * @see TemplateLocatorService#locateLayoutTemplate
     */
    public static String locateLayoutTemplate(RunData data, String template)
    {
        return getService().locateLayoutTemplate(data, template);
    }

    /**
     * @see TemplateLocatorService#locatePortletTemplate
     */
    public static String locatePortletTemplate(RunData data, String template)
    {
        return getService().locatePortletTemplate(data, template);
    }

    /**
     * @see TemplateLocatorService#locateControlTemplate
     */
    public static String locateControlTemplate(RunData data, String template)
    {
        return getService().locateControlTemplate(data, template);
    }

    /**
     * @see TemplateLocatorService#locateControllerTemplate
     */
    public static String locateControllerTemplate(RunData data, String template)
    {
        return getService().locateControllerTemplate(data, template);
    }

    /**
     * @see TemplateLocatorService#locateNavigationTemplate
     */
    public static String locateNavigationTemplate(RunData data, String template)
    {
        return getService().locateNavigationTemplate(data, template);
    }

    /**
     * @see TemplateLocatorService#locateEmailTemplate
     */
    public static String locateEmailTemplate(RunData data, String template)
    {
        return getService().locateEmailTemplate(data, template);
    }


    /**
     * @see TemplateLocatorService#locateEmailTemplate
     */
    public static String locateEmailTemplate(RunData data, String template, Locale locale)
    {
        return getService().locateEmailTemplate(data, template, locale);
    }

    /**
     * @see TemplateLocatorService#locateParameterTemplate
     */
    public static String locateParameterTemplate(RunData data, String template)
    {
        return getService().locateParameterTemplate(data, template);
    }

}
