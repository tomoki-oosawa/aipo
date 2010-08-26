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

package org.apache.jetspeed.services.security.nosecurity;

// Java imports
import javax.servlet.ServletConfig;

// Jetspeed import
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.security.JetspeedUser;
import org.apache.jetspeed.portal.Portlet;
import org.apache.jetspeed.services.security.PortalAccessController;
import org.apache.jetspeed.services.security.PortalResource;

// Turbine imports
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;

/**
 * NoSecurityAccessController
 * 
 * Use this service if you want to disable all authorization checks
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id: NoSecurityAccessController.java,v 1.5 2004/02/23 03:53:24 jford Exp $
 */
public class NoSecurityAccessController extends TurbineBaseService
                                     implements PortalAccessController
{
    
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
     */
    final public boolean checkPermission(JetspeedUser user, Portlet portlet, String action)
    {
        return checkPermission(user, portlet, action, null);
    }

    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given <code>Portlet</code> resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param portlet the portlet resource.
     * @param action the secured action to be performed on the resource by the user.
     * @param owner of the entry, i.e. the username
     * @return boolean true if the user has sufficient privilege.
     */
    final public boolean checkPermission(JetspeedUser user, Portlet portlet, String action, String owner)
    {
        return true;
    }
    
    /**
     * Given a <code>JetspeedUser</code>, authorize that user to perform the secured action on
     * the given Portlet Instance (<code>Entry</code>) resource. If the user does not have
     * sufficient privilege to perform the action on the resource, the check returns false,
     * otherwise when sufficient privilege is present, checkPermission returns true.
     *
     * @param user the user to be checked.
     * @param entry the portlet instance resource.
     * @param action the secured action to be performed on the resource by the user.
     * @return boolean true if the user has sufficient privilege.
     */
    final public boolean checkPermission(JetspeedUser user, Entry entry, String action)
    {
        return checkPermission(user, entry, action, null);
    }

    
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
    final public boolean checkPermission(JetspeedUser user, Entry entry, String action, String owner)
    {
        return true;
    }
    
    
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
    final public boolean checkPermission(JetspeedUser user, PortalResource resource, String action)
    {
        return true;
    }
    
    
    
    /*
     * Turbine Services Interface
     */
    
    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public synchronized void init(ServletConfig conf)
    throws InitializationException
    {
        if (getInit())
        {
            return;
        }
        
        super.init(conf);
        
        setInit(true);
    }
    
}

