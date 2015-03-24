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

package org.apache.jetspeed.portal;

/**
 * This interface is used as a marker for identifying the portlet
 * classes that can be used as their own customizer
 *
 * This marker is detected by the customization system when rendering
 * the customizer for a given portlet
 *
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @deprecated provide providesCustomization() from Portlet.java insted
 * to indicate that a portlet can do its own customization.
 */
public interface PortletCustomizer
{
    // empty interface
}
