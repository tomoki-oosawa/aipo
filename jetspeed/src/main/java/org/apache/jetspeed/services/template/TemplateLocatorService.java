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

import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;

import java.util.Locale;

/**
 * <p>This interface is a facade for all template location related operations.
 * Template location algorithms are different from the Velocity template location,
 * since Jetspeed has a specialized template directory structure.
 * This is a fix to get us through unti the TurbineTemplateService can locate
 * resources by NLS and mediatype. Then it can be removed</p>
 *
 * <p>The directory structure is currently layout out in the following order:
 *    /templateType/mediaType/LanguageCode/CountryCode</p>
 * <p>Example: /screens/html/en/US/resource.vm</p>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:kimptoc_mail@yahoo.com">Chris Kimpton</a>
 * @version $Id: TemplateLocatorService.java,v 1.8 2004/02/23 03:38:54 jford Exp $
 */

public interface TemplateLocatorService extends Service
{
 
    /** The name of this service */
    public String SERVICE_NAME = "TemplateLocator";

    /**
     * Locate a screen template using Jetspeed template location algorithm, searching by
     * mediatype and language criteria extracted from the request state in rundata.
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     *
     * @return The path relative to the screens directory for the requested screen template,
     *          or null if not found.
     */
    public String locateScreenTemplate(RunData data, String template);

    /**
     * Locate a layout template using Jetspeed template location algorithm, searching by
     * mediatype and language criteria extracted from the request state in rundata.
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     *
     * @return The path relative to the layouts directory for the requested layout template,
     *          or null if not found.
     */
    public String locateLayoutTemplate(RunData data, String template);

    /**
     * Locate a portlet template using Jetspeed template location algorithm, searching by
     * mediatype and language criteria extracted from the request state in rundata.
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     *
     * @return The path relative to the portlets directory for the requested portlet template,
     *          or null if not found.
     */
    public String locatePortletTemplate(RunData data, String template);

    /**
     * Locate a control template using Jetspeed template location algorithm, searching by
     * mediatype and language criteria extracted from the request state in rundata.
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     *
     * @return The path relative to the controls directory for the requested control template,
     *          or null if not found.
     */
    public String locateControlTemplate(RunData data, String template);

    /**
     * Locate a controller template using Jetspeed template location algorithm, searching by
     * mediatype and language criteria extracted from the request state in rundata.
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     *
     * @return The path relative to the controllers directory for the requested controller template,
     *          or null if not found.
     */
    public String locateControllerTemplate(RunData data, String template);

    /**
     * Locate a navigation template using Jetspeed template location algorithm, searching by
     * mediatype and language criteria extracted from the request state in rundata.
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     *
     * @return The path relative to the controllers directory for the requested controller template,
     *          or null if not found.
     */
    public String locateNavigationTemplate(RunData data, String template);

    /**
     * Locate an email template using Jetspeed template location algorithm, searching by
     * language criteria extracted from the request state in rundata.
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     *
     * @return The path relative to the emails directory for the requested email template,
     *          or null if not found.
     */
    public String locateEmailTemplate(RunData data, String template);

    /**
     * Locate an email template using Jetspeed template location algorithm
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     * @param locale The locale of the template.
     *
     * @return The path relative to the emails directory for the requested email template,
     *          or null if not found.
     */
    public String locateEmailTemplate(RunData data, String template, Locale locale);

    /**
     * Locate a parameter style template using Jetspeed template location algorithm, searching by
     * mediatype and language criteria extracted from the request state in rundata.
     *
     * @param data The rundata for the request.
     * @param template The name of the template.
     *
     * @return The path relative to the parameters directory for the requested portlet template,
     *          or null if not found.
     */
    public String locateParameterTemplate(RunData data, String template);

}
