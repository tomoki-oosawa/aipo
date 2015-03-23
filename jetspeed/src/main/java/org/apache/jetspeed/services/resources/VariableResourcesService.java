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

package org.apache.jetspeed.services.resources;

// Java Core Classes
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

// Turbine stuff.
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.resources.TurbineResourceService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.InitializationException;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * <p>This implementation of the <code>resourcesService</code> relies
 * on an external properties file for storing the configuration keys
 * and values</p>
 *
 * <P>In order to be compatible with legacy applications, this implementation
 * kept a static method for initializing the service, so it's still possible
 * to write the following code:
 * <p><code>
 * TurbineResourceService.setPropertiesName("d:/conf/Turbine.properties");
 * Vector myVar = TurbineResources.getVector("myvar");
 * </code></p>
 *
 * <p>This implementation allows the use of several pre-defined variables within
 *    the configuration file. The variables are identified by the following
 *    sequence of tokens: ${<varname>}. Varname is always folded to lowercase.</p>
 *  <P>The predefined variables are:
 *    <ul>
 *       <li>webapp.dir: base directory for the web application
 *       <li>jvm.dir: JVM startup directory
 *     </ul>
 *  </p>
 *  <p>The init parameters of the servlet are also imported as default variables.
 *     They may override the previously defined default variables
 *  </p>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: VariableResourcesService.java,v 1.15 2004/02/23 03:29:53 jford Exp $
 */
public class VariableResourcesService extends TurbineResourceService
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(VariableResourcesService.class.getName());
    
    public static final String WEBAPP_DIR="webapp.dir";
    public static final String WEB_DIR="web.dir";
    public static final String JVM_DIR="jvm.dir";
    public static final String START_TOKEN="${";
    public static final String END_TOKEN="}";
    
    /** The container for the generic resources. */
    private Hashtable variables = null;

    /** The container for the generic resources. */
    private Hashtable strings = null;
    private Hashtable vectors = null;
    private Hashtable arrays = null;


    /**
     * Late init. Don't return control until early init says we're done.
     */
    public void init( )
    {
        while( !getInit() ) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie ) {
                logger.info("VariableResources service: Waiting for init()..." );
            }
        }
    }


    /**
     * This method is called when the Service is initialized
     *
     * @param config a ServletConfig object
     */
    public synchronized void init(ServletConfig config) throws InitializationException
    {        
        if (getInit()) return;
        String props = config.getInitParameter(TurbineServices.PROPERTIES_PATH_KEY);

        variables = new Hashtable();
        strings = new Hashtable();
        vectors = new Hashtable();
        arrays = new Hashtable();
        initVariables(config);

        super.init(config);
    }

    /**
     * Initializer method that sets up the generic resources.
     *
     * @param confs A Configurations object.
     */
    private void initVariables(ServletConfig config)
    {
        ServletContext ctxt = config.getServletContext();
        
        String path = ctxt.getRealPath("/");
        
        // define web app dir
        if (path != null) {
            variables.put(WEBAPP_DIR, normalizePath(path) );
        }
        
        // FIXME. the following code blocks on Tomcat 
        // when loaded on startup
        /*
        path = ctxt.getContext("/").getRealPath("/");
        if (path != null ) {
            variables.put(WEB_DIR, normalizePath(path) );
        }
        */

        // define JVM app dir
        try {
            path = new File(".").getCanonicalPath();
            if (path != null) {
                variables.put(JVM_DIR, normalizePath(path) );
            }
        } catch (IOException e) {
            //very unlikely that the JVM can't 
            //resolve its path
            //But logging it anyway...
            logger.error( "Exception define JVM app dir", e );
        }

        // load servlet init parameters as variables, they may override
        // the previously defined variables. All param names are folded
        // to lower case
        
        Enumeration en = config.getInitParameterNames();
        while( en.hasMoreElements() ) {
            String paramName = (String)en.nextElement();
            String paramValue = config.getInitParameter(paramName);
            variables.put(paramName.toLowerCase(),paramValue);
        }
            
    }

    private static String normalizePath(String path) {
        // change all separators to forward
        // slashes
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar,'/');
        }
        
        // remove any trailing slash
        if (path.endsWith("/")) {
            path = path.substring(0,path.length()-1);
        }
        
        return path;
    }
    
    protected void setVariables(Hashtable vars)
    {
        synchronized (this)
        {
            this.variables = vars;
            this.strings = new Hashtable();
            this.vectors = new Hashtable();
            this.arrays = new Hashtable();
        }
    }
    
    protected String substituteString( String base ) {
        if (base == null) return null;
        
        int begin = -1;
        int end = -1;
        int prec = 0-END_TOKEN.length();
        String var = null;
        StringBuffer result = new StringBuffer();
        
        // FIXME: we should probably allow the escaping of the start token
        while ( ((begin=base.indexOf(START_TOKEN,prec+END_TOKEN.length()))>-1)
                && ((end=base.indexOf(END_TOKEN,begin))>-1) ) {
            
            result.append(base.substring(prec+END_TOKEN.length(),begin));
            var = base.substring(begin+START_TOKEN.length(),end);
            if (variables.get(var)!=null) {
                result.append(variables.get(var));
            }
            prec=end;
        }
        result.append(base.substring(prec+END_TOKEN.length(),base.length()));
        
        return result.toString();
    }
        
        
    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string.
     *
     * @param name The resource name.
     * @return The value of the resource as a string.
     */
    public String getString(String name)
    {
        String std = (String)strings.get(name);
        
        if (std == null) {
            std = substituteString(super.getString(name));
            if (std != null) strings.put(name,std);
        }
        
        return std;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a string.
     */
    public String getString(String name,
                                   String def)
    {
        String std = getString(name);
        
        if (std == null)
            std = substituteString(def);
    
        return std;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string array.
     *
     * @param name The resource name.
     * @return The value of the resource as a string array.
     */
    public String[] getStringArray(String name)
    {
        String[] std = (String[])arrays.get(name);
        if (std==null) {
            std = super.getStringArray(name);
            if (std != null) {
                for(int i=0;i<std.length;i++) {
                    std[i]=substituteString(std[i]);
                }
                arrays.put(name,std);
            }
        }
        
        return std;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector.
     *
     * @param name The resource name.
     * @return The value of the resource as a vector.
     */
    public Vector getVector(String name)
    {
        Vector std = (Vector)vectors.get(name);
        
        if (std==null) {
            std = super.getVector(name);
            if (std != null) {
                Vector newstd = new Vector();
                Enumeration en = std.elements();
                while (en.hasMoreElements()) {
                    newstd.addElement(substituteString((String)en.nextElement()));
                }
                std = newstd;
                vectors.put(name,std);
            }
        }
        
        return std;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a vector, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a vector.
     */
    public Vector getVector(String name,
                                   Vector def)
    {
        Vector std = getVector(name); 
        if ( std == null) {
            if (def != null) {
                std = new Vector();
                Enumeration en = def.elements();
                while (en.hasMoreElements()) {
                    std.addElement(substituteString((String)en.nextElement()));
                }
            }
        }

        return std;
    }

    /**
     * The purpose of this method is to extract a subset of configuraton
     * resources sharing a common name prefix. The prefix is stripped
     * from the names of the resulting resources.
     *
     * @param prefix the common name prefix
     * @return A ResourceService providing the subset of configuration.
     */
    public ResourceService getResources(String prefix)
    {
        Configuration config = getConfiguration().subset(prefix);
        
        if (config == null)
        {
            return null;
        }
        
        VariableResourcesService res = new VariableResourcesService();
        try 
        { 
            res.init(config); 
        } 
        catch (Exception e) 
        {
            logger.error( "Unable to init resources for " + prefix, e );
        }
        res.setVariables(this.variables);
        return (ResourceService)res;
    }


}
