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

package org.apache.jetspeed.portal.portlets;

/**
 * This subclass of VelocityPortlet should be used as base portlet
 * for all VelocityPortlets that implement their own customizer
 * <p>
 *  <strong>NOTE:</strong>This supports the pre-MVC style of template based portlet development.  
 *  The perefered method for defining customization is to set a parameter
 *  named "provides.customization" in the portlet config
 *  to either "true" or "false" depending on
 *  whether or not that portlet will provide it's own customizer.  The portlet 
 *  should also being using @see org.apache.jetspeed.portal.portlets.GenericMVCPortlet
 *  or a sub-class there of.
 * </p>
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 */
public class CustomizerVelocityPortlet extends VelocityPortlet
{
    /**
    * @return true if the portlet does its own customization
    */
    public boolean providesCustomization()
    {
        return true;
    }
}
