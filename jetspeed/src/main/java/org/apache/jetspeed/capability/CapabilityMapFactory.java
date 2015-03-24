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

package org.apache.jetspeed.capability;

import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.ClientRegistry;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.Registry;

import org.apache.turbine.util.RunData;


/**
This class describes various browsers capabilities and provides the
ability to query them.

FIXME: the implementation should change to be configuration file based and
handle more browsers.

@author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
@version $Id: CapabilityMapFactory.java,v 1.15 2004/02/23 02:46:39 jford Exp $
*/
public class CapabilityMapFactory
{

    public static final String DEFAULT_AGENT = "Mozilla/4.0";

    public static final String AGENT_XML = "agentxml/1.0";

    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(CapabilityMapFactory.class.getName());
    
    /**
    Returns the map corresponding to the given RunData.
    *
    FIXME: the method will be changed to use a Jetspeed specific request
    wrapper

    @param rundata the request RunData
    @return the map correspondin to the user-agent described in the RunData
    */
    public static CapabilityMap getCapabilityMap( RunData rundata )
    {

        if (rundata == null)
        {
            return getCapabilityMap(DEFAULT_AGENT);
        }

        return getCapabilityMap( rundata.getUserAgent() );
    }

    /**
    Returns the map corresponding to the given user-agent

    @param useragent a user-agent string in the HTTP User-agent format
    @return the map corresponding to the user-agent
    */
    public static CapabilityMap getCapabilityMap( String useragent )
    {
        CapabilityMap map = null;

        if (useragent == null)
        {
            useragent = DEFAULT_AGENT;
        }

        ClientRegistry registry = (ClientRegistry)Registry.get(Registry.CLIENT);
        ClientEntry entry = registry.findEntry(useragent);

        if (entry == null)
        {
            if (useragent.equals(DEFAULT_AGENT))
            {
                logger.error("CapabilityMap: Default agent not found in Client Registry !");
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("CapabilityMap: useragent "+ useragent + "unknown, falling back to default");
                }
                map = getDefaultCapabilityMap();
            }
        }
        else
        {
            map = new BaseCapabilityMap(useragent, entry);
        }


        if (logger.isDebugEnabled())
        {
            logger.debug("CapabilityMap: User-agent: "+useragent+" mapped to "+map);
        }

        return map;
    }

    /**
    Returns the map corresponding to the given user-agent

    @param useragent a user-agent string in the HTTP User-agent format
    @return the map corresponding to the user-agent
    */
    public static CapabilityMap getDefaultCapabilityMap()
    {
        return getCapabilityMap(DEFAULT_AGENT);
    }
}