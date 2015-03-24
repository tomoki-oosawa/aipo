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

package org.apache.jetspeed.util.template;

// Jetspeed
import org.apache.jetspeed.util.template.JetspeedLink;
import org.apache.jetspeed.services.resources.JetspeedResources;

// Turbine
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.services.factory.FactoryService;
import org.apache.turbine.services.pool.TurbinePool;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

/**
 * Return a JetspeedLink object.  The object may be
 * returned from a pool or instanciated.  The pool
 * is maintained by Turbine's pool service.
 *
 */
public class JetspeedLinkFactory
{
    /**
     * Name of class for JetspeedLink.  The class is the same one used
     * by the tool <code>jslink</code>, so the class name is retrieved from
     * the tool's configuration.
     */
    private static String JETSPEEDLINK_CLASSNAME = JetspeedResources.getString("tool.request.jslink","org.apache.jetspeed.util.template.BaseJetspeedLink");
    private static FactoryService factoryService = (FactoryService) TurbineServices.
            getInstance().getService(FactoryService.SERVICE_NAME);

    
    /**
     * Get an JetspeedLink object.  The object may be retreived
     * from a pool.  If no object is available in the pool, then one will
     * be instanciated.
     *
     * The JetspeedLink's init() should be called to return a valid link.
     *
     * @throws TurbineException by Turbine's pool service
     * @return JetspeedLink
     */
    static JetspeedLink getInstance()
    throws TurbineException
    {
        JetspeedLink jsLink = (JetspeedLink) TurbinePool.getInstance( JETSPEEDLINK_CLASSNAME);
        if (jsLink == null)
            jsLink = (JetspeedLink) factoryService.getInstance(JETSPEEDLINK_CLASSNAME);
        return jsLink;
    }
    
    /**
     * Get an initialized JetspeedLink object.  The object may be retreived
     * from a pool.  If no object is available in the pool, then one will
     * be instanciated.  The object will be initialized with Rundata.
     *
     * @param rundata The request data.
     * @throws TurbineException by Turbine's pool service
     * @return JetspeedLink
     */
    public static JetspeedLink getInstance( RunData rundata)
    throws TurbineException
    {
        JetspeedLink jsLink = getInstance();
        if (jsLink != null)
            jsLink.init(rundata);
        return jsLink;
    }
    
    /**
     * Return an object to the pool
     *
     * @param jetspeedLink object to return to pool
     */
    public static void putInstance(JetspeedLink jetspeedLink)
    {
        if (jetspeedLink != null)
            TurbinePool.putInstance( jetspeedLink);
        return;
    }
}
