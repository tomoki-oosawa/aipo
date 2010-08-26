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

package org.apache.jetspeed.services.security;

import org.apache.turbine.util.RunData;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.om.registry.RegistryEntry;

/**
 * <P>Interface defines access control methods specific to Jetspeed for
 * accessing portal resources such as portlets and panes</P>
 * 
 * @see org.apache.jetspeed.services.security.JetspeedSecurityService
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: AccessControl.java,v 1.6 2004/02/23 03:58:11 jford Exp $
 */

public interface AccessControl {

    boolean checkPermission(RunData runData, String permission, Portlet portlet);
    boolean checkPermission(RunData runData, String action, RegistryEntry entry);
    boolean checkPermission(String user, String action, Portlet portlet);

    // TODO: get requirements for controlling access to other OM elements
    // boolean checkPermission(User user, String action, Profile profile);

}
