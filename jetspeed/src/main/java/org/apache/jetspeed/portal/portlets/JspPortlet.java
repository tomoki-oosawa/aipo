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

// Jetspeed stuff
import org.apache.jetspeed.portal.PortletException;

/**
 * A JSP portlet example.
 * 
 * STW: Changed to subclass the GenericMVCPortlet to help
 * unify the handling of all template based portlets.
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>
 */
public class JspPortlet extends GenericMVCPortlet
{

    public static final String TEMPLATE = "template";

    /**
     * STW:  Backward compatibility: set the viewType to "JSP". 
     * By default the data is non cacheable
    */
    public void init() throws PortletException
    {
        setCacheable(false);
        setViewType("JSP");
        super.init();
        
    }


}
