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
 * A Velocity based portlet implementation
 *    <p> 
 *   <strong>NOTE:</strong>This supports the pre-MVC style of template 
 *    based portlet development and is supplied for backward compatibility.
 *    The prefered method is to define template-based portlets is to use    
 *    @see org.apache.jetspeed.portal.portlets.GenericMVCPortlet
 *    or a sub-class there of.  The GenericMVCPortlet javadoc provides
 *    instructions for using using the MVC portlet model.
 *   </p>
 * @author <a href="mailto:re_carrasco@bco011.sonda.cl">Roberto Carrasco</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:sweaver@rippe.com">Scott Weaver</a>u
 */
public class VelocityPortlet extends GenericMVCPortlet

{
   /**
    * STW: Backward compatibility: set the viewType to "Velocity".
    */
    public void init() throws PortletException
    {
        setCacheable(true);
        setViewType("Velocity");
        super.init();        
    }
   


}

