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

package org.apache.jetspeed.services.forward;

import java.util.Collection;
import java.util.Map;

import org.apache.turbine.services.Service;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.DynamicURI;

import org.apache.jetspeed.services.forward.configuration.Forward;
import org.apache.jetspeed.services.forward.configuration.PortletForward;


/**
 * <P>This is the interface to the Jetspeed Forward services.
 *    The interface defines methods for forwarding navigation to 
 *    other pages or panes in the portal. The Forward service
 *    provides an abstraction, by removing the hard-coding of
 *    portal resources in your actions. Instead, all forward targets
 *    are defined in a centralized configuration file. By using the 
 *    forward service, you use logical forward names in your java code.</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: ForwardService.java,v 1.5 2004/02/23 03:51:09 jford Exp $
 */

public interface ForwardService extends Service
{

    /** The name of this service */
    public String SERVICE_NAME = "ForwardService";


    /**
     *  Forward to a specific forward by name.
     *  All parameters are resolved statically (via the forward definition)
     *
     * @param rundata The turbine rundata context for this request.     
     * @param forwardName Forward to this abstract forward name.
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forward(RunData rundata, String forwardName);


    /**
     *  For the given portlet and given action, forward to the target
     *  defined in the forward configuration for the portlet + action.
     *  All parameters are resolved statically (via the forward definition)     
     *
     * @param portlet The name of the portlet for which we are forwarding.
     * @param target A logical target name. Portlets can have 1 or more targets.
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forward(RunData rundata, String portlet, String target);


    /**
     *  Forward to a specific forward by name.
     *  Parameters are resolved both statically and dynamically, with the 
     *  dynamic parameter overriding the static parameter definitions.
     *
     * @param rundata The turbine rundata context for this request.     
     * @param forwardName Forward to this abstract forward name.
     * @param parameters The dynamic Validation Parameters used in creating validation forwards
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forwardDynamic(RunData rundata, String forwardName, Map parameters);


    /**
     *  For the given portlet and given action, forward to the target
     *  defined in the forward configuration for the portlet + action.
     *  Parameters are resolved both statically and dynamically, with the 
     *  dynamic parameter overriding the static parameter definitions.     
     *
     * @param portlet The name of the portlet for which we are forwarding.
     * @param target A logical target name. Portlets can have 1 or more targets.
     * @param parameters The dynamic Validation Parameters used in creating validation forwards     
     * @return DynamicURI the full link to the referenced page
     */
    public DynamicURI forwardDynamic(RunData rundata, 
                                 String portlet, 
                                 String target,
                                 Map parameters);

    
    /**
     * Get a collection of all forwards in the system.
     *
     * @return Collection of all forward definitions
     */
    public Collection getForwards();

    /**
     * Get a collection of all portlet forwards in the system.
     *
     * @return Collection of all portlet forward definitions
     */
    public Collection getPortletForwards();

    /**
     * Lookup a single forward definition by forward name
     *
     * @param  forwardName The name of the Forward to find
     * @return Forward The found forward definition or null if not found
     */
    public Forward getForward(String forwardName);

    /**
     * Lookup a single portlet forward definition by portlet name + target name
     *
     * @param  portlet The name of the portlet in the Portlet Forward to find
     * @param  target The name of the target in the Portlet Forward to find     
     * @return Forward The found portlet forward definition or null if not found
     */
    public PortletForward getPortletForward(String portlet, String target);

}
