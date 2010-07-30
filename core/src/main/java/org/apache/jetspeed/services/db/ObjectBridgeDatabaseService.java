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

package org.apache.jetspeed.services.db;


import javax.servlet.ServletConfig;

// OJB
import ojb.broker.PersistenceBroker;
import ojb.broker.PersistenceBrokerFactory;

// turbine.services
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;

// Jetspeed
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * <p>This is an implementation of the <code>JetspeedDatabase</code> interface.
 *
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ObjectBridgeDatabaseService.java,v 1.4 2004/02/23 03:28:10 jford Exp $
 */

public class ObjectBridgeDatabaseService  extends TurbineBaseService
    implements JetspeedDatabaseService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ObjectBridgeDatabaseService.class.getName());
    
    PersistenceBroker pm = null;

    /**
     * This is the early initialization method called by the 
     * Turbine <code>Service</code> framework
     * @param conf The <code>ServletConfig</code>
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public synchronized void init(ServletConfig conf) throws InitializationException {

        // already initialized
        if (getInit()) return;

        try
        {
            System.setProperty("OJB.properties", "org/apache/jetspeed/services/db/conf/OJB.properties");
            pm = PersistenceBrokerFactory.createPersistenceBroker("org/apache/jetspeed/services/db/conf/repository.xml");
        }
        catch (Exception e)
        {
            logger.error("Failed to load ObjectBridge Manager: " + e.toString(), e);        
            throw new InitializationException(e.toString());
        }
        // initialization done
        setInit(true);

     }


    public Object getPersistenceManager()
    {
        return pm;
    }

    /**
     * This is the lateinitialization method called by the 
     * Turbine <code>Service</code> framework
     *
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() throws InitializationException {
        logger.info( "Late init for ObjectBridgeDatabaseService called" );        
    }

    /**
     * This is the shutdown method called by the 
     * Turbine <code>Service</code> framework
     */
    public void shutdown() 
    {
    }

}