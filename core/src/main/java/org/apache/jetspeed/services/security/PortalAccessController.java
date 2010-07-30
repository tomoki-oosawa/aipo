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

// Jetspeed imports
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.security.PortalResource;

// Turbine imports
import org.apache.turbine.services.Service;

/**
 * <p> The <code>PortalAccessController</code> interface defines a contract between 
 * the portal and security provider required for authorization to portal-secure areas.
 * This interface enables an application to be independent of the underlying 
 * authorization technology.
 *
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: PortalAccessController.java,v 1.4 2004/02/23 03:58:11 jford Exp $
 */

public interface PortalAccessController extends Service
{
    public String SERVICE_NAME = "PortalAccessController";

    /** Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given Portlet Instance (<code>Entry</code>) resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param entry the portlet instance resource.
     * @param action the secured action to be performed on the resource by the user.
     * @return boolean true if the user has sufficient privilege.
     * @depracated Use checkpermission(user, entry, action, owner)
     */
    public boolean checkPermission(JetspeedUser user, Entry entry, String action); 

    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given Portlet Instance (<code>Entry</code>) resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param entry the portlet instance resource.
     * @param action the secured action to be performed on the resource by the user.
     * @param owner of the entry, i.e. the username
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedUser user, Entry entry, String action, String owner); 

    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given <code>Portlet</code> resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param portlet the portlet resource.
     * @param action the secured action to be performed on the resource by the user.
     * @return boolean true if the user has sufficient privilege.
     *
     * @depracated Use checkpermission(user, portlet, action, owner)
     */
    public boolean checkPermission(JetspeedUser user, Portlet portlet, String action); 

    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given <code>Portlet</code> resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param portlet the portlet resource.
     * @param action the secured action to be performed on the resource by the user.
     * @param owner of the portlet, i.e. the username
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedUser user, Portlet portlet, String action, String owner); 

    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param resource requesting an action
     * @param action the secured action to be performed on the resource by the user.
     * @return boolean true if the user has sufficient privilege.
     */
    public boolean checkPermission(JetspeedUser user, PortalResource resource, String action); 
}



