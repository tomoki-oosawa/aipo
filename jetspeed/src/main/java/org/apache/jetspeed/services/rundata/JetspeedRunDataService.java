/*
 * Copyright 2000-2002,2004 The Apache Software Foundation.
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

// package
package org.apache.jetspeed.services.rundata;

// Java classes
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Turbine classes
import org.apache.turbine.services.rundata.TurbineRunDataService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.services.TurbineServices;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.jetspeed.services.statemanager.StateManagerService;

/**
* The JetspeedRunDataService extends TurbineRunDataService,
* adding the ability to get the current runData object for the thread
* at any time.  This is accomplished by storing the active runData objects
* in a map, keyed by thread.
* Also done here, because this is so nicely bracketed around each http request
* by Turbine, is the association of the http session for this request / thread
* with the state manager.
*
* @author <a href="mailto:ggolden@umich.edu">Glenn R. Golden</a>
* @version $Revision: 1.5 $
*/
public class JetspeedRunDataService
    extends TurbineRunDataService 
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedRunDataService.class.getName());
    
    /** The collection of active JetspeedRunData objects, keyed by Thread. */
    private Map m_runDataStore = null;

    /*******************************************************************************
    * Service implementation
    *******************************************************************************/

    /**
    * Initializes the service
    *
    * @throws InitializationException if initialization fails.
    */
    public void init()
        throws InitializationException
    {
        super.init();

        // allocate a thread-safe map
        m_runDataStore = Collections.synchronizedMap(new HashMap());

    }   // init

    /*******************************************************************************
    * TurbineRunDataService implementation
    *******************************************************************************/

    /**
    * Gets a RunData instance from a specific configuration.
    *
    * @param key a configuration key.
    * @param req a servlet request.
    * @param res a servlet response.
    * @param config a servlet config.
    * @return a new or recycled RunData object.
    * @throws TurbineException if the operation fails.
    * @throws IllegalArgumentException if any of the parameters are null.
    */
    public RunData getRunData(String key,
                                HttpServletRequest req,
                                HttpServletResponse res,
                                ServletConfig config)
        throws TurbineException, IllegalArgumentException
    {
        // let the super do the work
        JetspeedRunData r = (JetspeedRunData)super.getRunData(key, req, res, config);

        // store the rundata associated with this thread
        m_runDataStore.put(Thread.currentThread(), r);

        // associate this http session with this thread in the state manager
        StateManagerService stateManager = (StateManagerService)TurbineServices
                .getInstance().getService(StateManagerService.SERVICE_NAME);
        if (stateManager != null)
        {
            stateManager.setCurrentContext(req.getSession(true));
        }

        if (logger.isDebugEnabled())
            logger.debug("JetspeedRunDataService: storing rundata " + r
                        + " for thread: " + Thread.currentThread());

        return r;

    }   // getRunData

    /**
    * Puts the used RunData object back to the factory for recycling.
    *
    * @param data the used RunData object.
    * @return true, if pooling is supported and the object was accepted.
    */
    public boolean putRunData(RunData data)
    {
        // un-associate this http session with this thread in the state manager
        StateManagerService stateManager = (StateManagerService)TurbineServices
                .getInstance().getService(StateManagerService.SERVICE_NAME);
        if (stateManager != null)
        {
            stateManager.clearCurrentContext();
        }

        // remove this thread's rundata
        m_runDataStore.remove(Thread.currentThread());

        if (logger.isDebugEnabled())
            logger.debug("JetspeedRunDataService: releasing rundata for thread: "
                + Thread.currentThread());

        // let super do the work
        return super.putRunData(data);

    }   // putRunData

    /**
    * Access the current rundata object - the one associated with the current thread.
    * @return The current JetspeedRunData object associatd with the current thread.
    */
    public JetspeedRunData getCurrentRunData()
    {
        if (logger.isDebugEnabled())
            logger.debug("JetspeedRunDataService: accessing rundata "
                        + m_runDataStore.get(Thread.currentThread())
                        + " for thread: " + Thread.currentThread());

        return (JetspeedRunData) m_runDataStore.get(Thread.currentThread());

    }   // getCurrentRunData

}   // JetspeedRunDataService

/**********************************************************************************
*
* $Header: /home/cvspublic/jakarta-jetspeed/src/java/org/apache/jetspeed/services/rundata/JetspeedRunDataService.java,v 1.5 2004/02/23 03:36:10 jford Exp $
*
**********************************************************************************/

