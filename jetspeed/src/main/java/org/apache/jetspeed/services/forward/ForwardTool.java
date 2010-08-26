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
package org.apache.jetspeed.services.forward;

// Turbine
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.services.pull.ApplicationTool;

// Jetspeed
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.jetspeed.services.rundata.JetspeedRunData;

/**
 * <p>Provides a tool interface to forwards</p>
 *
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ForwardTool.java,v 1.5 2004/02/23 03:51:09 jford Exp $
 */
public class ForwardTool implements ApplicationTool
{
    /**
     *<p>Request to which we refer.</p>
     */
    private JetspeedRunData rundata = null;

    ForwardService service = null;

    /**
     *  Forward to a specific forward by name.
     *
     * @param name Forward to this abstract forward name.
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forward(String forwardName)
    {
        if (null == service)
        {
            service = (ForwardService)ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);
        }
        return service.forward(this.rundata, forwardName);
    }

    /**
     *  For the given portlet and given action, forward to the target
     *  defined in the forward configuration for the portlet + action.
     *
     * @param portlet The name of the portlet for which we are forwarding.
     * @param target A logical target name. Portlets can have 1 or more targets.
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forward(String portlet, String target)
    {
        if (null == service)
        {
            service = (ForwardService)ServiceUtil.getServiceByName(ForwardService.SERVICE_NAME);
        }
        return service.forward(this.rundata, portlet, target);
    }


    /**
     * Methods required by ApplictionTool interface
     *
     */

    /**
     * This will initialise a JetspeedLink object that was
     * constructed with the default constructor (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData object
     */

    public void init(Object data)
    {
        // Keeping init small and fast
        if (data instanceof JetspeedRunData)
        {
            this.rundata = (JetspeedRunData) data;
        }
        else
        {
            this.rundata = null;
        }
        return;
    }
    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }
}