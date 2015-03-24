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

package org.apache.jetspeed.services.idgenerator;

// Java
import javax.servlet.ServletConfig;

// Jetspeed
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

// Turbine
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;

/**
 * Simple implementation of the IdGeneratorService.
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id: JetspeedIdGeneratorService.java,v 1.5 2004/02/23 03:28:57 jford Exp $
 */
public class JetspeedIdGeneratorService extends TurbineBaseService
    implements IdGeneratorService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedIdGeneratorService.class.getName());
    
    // configuration keys
    private final static String CONFIG_COUNTER_START = "counter.start";
    private final static String CONFIG_PEID_PREFIX = "peid.prefix";
    private final static String CONFIG_PEID_SUFFIX = "peid.suffix";

    // default configuration values
    private final static long DEFAULT_CONFIG_COUNTER_START = 0x10000;
    private final static String DEFAULT_CONFIG_PEID_PREFIX = "P-";
    private final static String DEFAULT_CONFIG_PEID_SUFFIX = "";

    // configuration parameters
    private static String peidPrefix = null;
    private static String peidSuffix = null;

    protected static long idCounter;

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

        initConfiguration();        

        // initialization done
        setInit(true);

     }
    /**
     * This is the lateinitialization method called by the 
     * Turbine <code>Service</code> framework
     *
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    public void init() throws InitializationException {
        logger.info( "Late init for JetspeedIdGeneratorService called" );        
        while( !getInit() ) {
            //Not yet...
            try {
                Thread.sleep( 100 );
                logger.info( "Waiting for init of JetspeedIdGeneratorService..." );
            } catch (InterruptedException ie ) {
                logger.error("Exception",  ie);
            }
        }
    }

    /**
     * This is the shutdown method called by the 
     * Turbine <code>Service</code> framework
     */
    public void shutdown() 
    {
        logger.info( "Shutdown for JetspeedIdGeneratorService called. idCounter = "
             + idCounter + " (" + Long.toHexString(idCounter) + ")" ); 
    }

    /**
     * Loads the configuration parameters for this service from the
     * JetspeedResources.properties file.
     *
     * @exception throws a <code>InitializationException</code> if the service
     * fails to initialize
     */
    private void initConfiguration() throws InitializationException
    {
        // get configuration parameters from Jetspeed Resources
        ResourceService serviceConf = ((TurbineServices)TurbineServices.getInstance())
                                                     .getResources(IdGeneratorService.SERVICE_NAME);

        peidPrefix = serviceConf.getString( CONFIG_PEID_PREFIX, DEFAULT_CONFIG_PEID_PREFIX );
        peidSuffix = serviceConf.getString( CONFIG_PEID_SUFFIX, DEFAULT_CONFIG_PEID_SUFFIX );
        synchronized(JetspeedIdGeneratorService.class)
        {
            idCounter = serviceConf.getLong( CONFIG_COUNTER_START, DEFAULT_CONFIG_COUNTER_START );
        }
        
   }
    /** Creates new JetspeedIdGeneratorService */
    public JetspeedIdGeneratorService() {
    }

    /**
     * Generate a Unique PEID
     * @return Unique PEID
     */
    public String getNextPeid()
    {
        long newid;

        synchronized(JetspeedIdGeneratorService.class)
        {
            newid = idCounter++;
        }
        
        return peidPrefix + Long.toHexString(System.currentTimeMillis()) + "-" 
               + Long.toHexString(newid) + peidSuffix;
    }
    
}
