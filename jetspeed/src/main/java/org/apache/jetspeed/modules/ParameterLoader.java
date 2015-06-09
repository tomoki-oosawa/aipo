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

package org.apache.jetspeed.modules;

// jetspeed stuff
import org.apache.jetspeed.modules.parameters.ParameterPresentationStyle;
import org.apache.jetspeed.modules.parameters.ParameterPresentationStyleFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.resources.JetspeedResources;

// Java Core Classes
import java.util.Vector;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

// Turbine Utility Classes
import org.apache.turbine.modules.GenericLoader;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.util.RunData;

/**
 * The purpose of this class is to allow one to load and execute
 * Parameter modules.
 *
 * @author <a href="mailto:mark_orciuch@ngsltd.com">Mark Orciuch</a>
 * @version $Id: ParameterLoader.java,v 1.5 2004/02/23 03:01:32 jford Exp $
 */
public class ParameterLoader extends GenericLoader
{
    /**
     * The single instance of this class.
     */
    private static ParameterLoader instance = new ParameterLoader(JetspeedResources.getInt("parameter.cache.size", 50));

    /**
     * Static initialization of the logger for this class
     */
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(ParameterLoader.class.getName());
    
    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private ParameterLoader() {

        super();
    }

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     * 
     * @param i
     */
    private ParameterLoader(int i) {
        super(i);
    }

    /**
     * Adds an instance of an object into the hashtable.
     * 
     * @param name   Name of object.
     * @param param
     */
    private void addInstance(String name, ParameterPresentationStyle param) {

        if (cache()) {
            this.put(name, (ParameterPresentationStyle)param );
        }
    }

    /**
     * Attempts to load and render a parameter using custom style. For example, one might define
     * a custom parameter presentation style TextArea which displays current value of the parameter
     * using HTML text area presentation. Assuming that TextArea is rendered using two optional
     * parameters: rows and cols, the map passed to this method could contain the following values:
     * <li>symbols.style.rows = 5
     * <li>symbols.style.cols = 80
     * and the call might look like this:
     *<p>
     * String symbols = eval(data, "TextArea", "symbols", "MSFT,SUNW,EMC,ORCL", parms);
     * 
     * @param data     Turbine information.
     * @param provider Custom parameter class name (without the package)
     * @param name     Name for rendered HTML tag
     * @param value    Current value
     * @param parms    Optional rendition parameters
     * @return 
     * @exception Exception a generic exception.
     */
    public String eval(RunData data, String provider, String name, String value, Map parms) throws Exception {

        // Execute parameter
        ParameterPresentationStyle prm = getInstance(provider);

        // Filter out style params
        Map styleparms = extractStyleParameters(parms, name);
        prm.setParms(styleparms);

        return prm.getContent(data, name, value, styleparms);

    }

    /**
     * This method is not used.
     *
     * @param data Turbine information.
     * @param name Name of object that will execute the screen.
     * @exception Exception a generic exception.
     */
    public void exec(RunData data, String name) throws Exception {

        //this.eval(data, name);
    }

    /**
     * Pulls out an instance of the object by name.  Name is just the
     * single name of the object.
     * 
     * @param provider   Name of object instance.
     * @return A Screen with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public ParameterPresentationStyle getInstance(String provider) throws Exception {

        ParameterPresentationStyle prm = null;

        // Check if the parameter is already in the cache
        if (cache() && this.containsKey(provider)) {

            prm = (ParameterPresentationStyle) this.get(provider);
            if ( logger.isDebugEnabled() ) {
                logger.debug("ParameterLoader: Serving parameter: " + provider + ", prm=" + prm + " from cache");            
            }

        } else {

            // We get the broker service
            AssemblerBrokerService ab =
                (AssemblerBrokerService)TurbineServices.getInstance()
                .getService (AssemblerBrokerService.SERVICE_NAME);

            try {
                // Attempt to load the presentation style
                prm = (ParameterPresentationStyle)ab.getAssembler("parameter", provider);
                if (prm == null) {
                    if ( logger.isDebugEnabled() ) {
                        logger.debug("ParameterLoader: Registering the factory");
                    }
                    ab.registerFactory("parameter", new ParameterPresentationStyleFactory());
                    prm = (ParameterPresentationStyle)ab.getAssembler("parameter", provider);
                }
                if ( logger.isDebugEnabled() ) {
                    logger.debug("ParameterLoader: Loaded parameter: "+provider+", prm="+prm);
                }
            } catch (ClassCastException cce) {
                prm = null;
                logger.error( "Error loading presentation style", cce );
            }

            if (prm == null) {
                // If we did not find a screen we should try and give
                // the user a reason for that...
                // FIX ME: The AssemblerFactories should each add it's own string here...
                Vector packages = JetspeedResources.getVector("module.packages");

                throw new ClassNotFoundException( "\n\n\tRequested Parameter not found: " +
                                                  provider + "\n" +
                                                  "\tTurbine looked in the following modules.packages path: \n\t" +
                                                  packages.toString() + "\n");
            } else if(cache()) {

                addInstance(provider, prm);
            }

        }

        return prm;
    }

    /**
     * The method through which this class is accessed.
     * 
     * @return The single instance of this class.
     */
    public static ParameterLoader getInstance() {

        return instance;
    }

    /**
     * Extracts any parameters to parameter style.
     * 
     * @param parms  portlet parameters
     * @param parm   parameter name
     * @return hashtable of optional parameters for the style
     */
    public static Map extractStyleParameters(Map parms, String parmName) {

        Hashtable result = new Hashtable();

        if (parms != null) {
            String key = parmName.concat(".style.");
            Iterator it = parms.keySet().iterator();
            while (it.hasNext()) {
                String parmkey = (String)it.next();
                if (parmkey.startsWith(key)) {
                    try {
                        String stylekey = parmkey.substring(parmkey.lastIndexOf(".")+1);
                        if ( logger.isDebugEnabled() )
                        {
                            logger.debug("ParameterLoader: parm name [" + parmName + "] - storing option [" + stylekey + 
                                      "] with value [" + parms.get(parmkey) + "]");
                        }
                        result.put(stylekey, parms.get(parmkey));
                    } catch (Exception e) {
                        logger.error("Error extracting params", e);
                    }
                }
            }
        }

        return result;
    }

}
