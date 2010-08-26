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
package org.apache.jetspeed.services.resources;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;

// Turbine stuff
import org.apache.turbine.services.resources.TurbineResourceService;
import org.apache.turbine.services.resources.ResourceService;
import org.apache.turbine.services.InitializationException;

// Commons classes
import org.apache.commons.configuration.Configuration;

import org.apache.jetspeed.services.resources.JetspeedResources;

/**
 * <p>This service subclasses <code>TurbineResourceService</code> and
 * provides functionality for overriding properties in default resource
 * files. This override behavior is extended to non-string properties</p>
 * 
 * <P>To override properties:
 * <ul>
 * <li>Define your own property file containing properties you want to override (for example, my.properties)</li>
 * <li>Add the following property in my.properties file:
 * <code>services.ResourceService.classname = org.apache.jetspeed.services.resources.JetspeedResourceService</code></li>
 * <li>Include TurbineResources.properties at the end of my.properties file</li>
 * <li>Set <code>properties</code> init parameter in web.xml to <code>my.properties</code></li>
 * <li>Set <code>resources</code> init parameter in web.xml to
 * <code>org.apache.jetspeed.services.resources.JetspeedResourceService</code></li>
 * </ul>
 * 
 * <P><B>Important note on overriding services.</B>Order of initializing services may be important.
 * Overriding a service may change this order. It is important that services attempt to initialize
 * dependent services in their early init methods. For example, to make sure that ServletService is
 * running, invoke the following code:
 * <PRE>
 * TurbineServices.getInstance().initService(ServletService.SERVICE_NAME, conf);
 * </PRE>
 * </P>
 * 
 * <P>Also, ${variable} substitution is extended to non-string properties. For example, the following
 * property references are valid:
 * <PRE>
 * confRoot=/WEB-INF/conf
 * 
 * psmlMapFile=${confRoot}/psml-mapping.xml
 * registryMapFile=${confRoot}/registry-mapping.xml
 * 
 * defaultRefresh=60
 * 
 * registryRefresh=${defaultRefresh}
 * psmlRefresh=${defaultRefresh}
 * </PRE>
 * </P>
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * 
 * @version $Id: JetspeedResourceService.java,v 1.8 2004/02/23 03:29:53 jford Exp $
 */
public class JetspeedResourceService
extends TurbineResourceService
{
    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a boolean value.
     *
     * @param name The resource name.
     * @return The value of the named resource as a boolean.
     */
    public boolean getBoolean(String name)
    {

        return new Boolean(interpolate(getConfiguration().getString(name))).booleanValue();
    }

    /**
     * The purppose of this method is to get the configuration
     * resource with the given name as a boolean value, or a default
     * value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the named resource as a boolean.
     */
    public boolean getBoolean(String name, boolean def)
    {

        String temp = interpolate(getConfiguration().getString(name));
        return temp != null ? new Boolean(temp).booleanValue() : def;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double.
     *
     * @param name The resoource name.
     * @return The value of the named resource as double.
     */
    public double getDouble(String name)
    {

        return new Double(interpolate(getConfiguration().getString(name))).doubleValue();
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the named resource as a double.
     */
    public double getDouble(String name, double def)
    {

        String temp = interpolate(getConfiguration().getString(name));
        return temp != null ? new Double(temp).doubleValue() : def;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float.
     *
     * @param name The resource name.
     * @return The value of the resource as a float.
     */
    public float getFloat(String name)
    {

        return new Float(interpolate(getConfiguration().getString(name))).floatValue();
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a float.
     */
    public float getFloat(String name, float def)
    {

        String temp = interpolate(getConfiguration().getString(name));
        return temp != null ? new Float(temp).floatValue() : def;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer.
     *
     * @param name The resource name.
     * @return The value of the resource as an integer.
     */
    public int getInt(String name)
    {

        return new Integer(interpolate(getConfiguration().getString(name))).intValue();
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as an integer.
     */
    public int getInt(String name, int def)
    {

        String temp = interpolate(getConfiguration().getString(name));
        return temp != null ? new Integer(temp).intValue() : def;
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long.
     *
     * @param name The resource name.
     * @return The value of the resource as a long.
     */
    public long getLong(String name)
    {

        return new Long(interpolate(getConfiguration().getString(name))).longValue();
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a long.
     */
    public long getLong(String name, long def)
    {

        String temp = interpolate(getConfiguration().getString(name));
        return temp != null ? new Long(temp).longValue() : def;
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

        JetspeedResourceService res = new JetspeedResourceService();
        try 
        {
            res.init(config);
        }
        catch (Exception e)
        {
            System.err.println("Exception in init of JetspeedResourceService" + e.getMessage());
            e.printStackTrace();
        }

        return (ResourceService) res;
    }

    /**
     * This method is called when the Service is initialized
     * It provides a way to override properties at runtime.
     * To use 'runtime' time properties, define a directory where you are keeping your
     * 'runtime' parameters and pass it in as a System Property named 'jetspeed.conf.dir'
     * 
     * This implementation will take the name of the web application (i.e. 'jetspeed') and use it to 
     * find the name of a properties file. These properties are merged with the TurbineResources/JetspeedResources.properties
     * Similarly, you can override Torque properties by naming a file in your runtime directory as ${webapp.name}_Torque.properties
     * 
     * Examples:
     *    jetspeed.conf.dir = /Users/sysadmin/conf
     *    
     *    This directory contains two files:
     *      1. jetspeed.properties - overrides properties in JetspeedResources.properties and TurbineResources.properties
     *      2. jetspeed_torque.properties - overrides properties in Torque.properties
     * 
     * @param config a ServletConfig object
     */
    public void init()
        throws InitializationException
    {     
        System.out.println("Jetspeed Services: Starting with no parameters");
        super.init();
    }
       
    public synchronized void init(ServletConfig config) throws InitializationException        
    {               
        String propsDir = null;
        String appName = config.getServletName();
        String deployFilename = appName + ".properties";
        String torqueFilename = appName + "_torque.properties";
        super.init(config);
        
        // Display product information
        //System.out.println("Jetspeed Services: Starting servlet: [" + appName +"]");
        String version = getString(JetspeedResources.JETSPEED_VERSION_KEY);
        String name = getString(JetspeedResources.JETSPEED_NAME_KEY);
        if (version != null && name != null)
        {
            System.out.println("");
            System.out.println("Starting " + name + "/" + version);
            System.out.println("");
        }
        
        try
        {
            propsDir = System.getProperty("jetspeed.conf.dir", null);
            if (null == propsDir)
            {
                // no deploy-time directory defined to find properties, return
                return;
            }


            String torqueProps = makeFileNamePath(propsDir, torqueFilename);
            String deployProps = makeFileNamePath(propsDir, deployFilename);

            System.out.println("torque props = " + torqueProps);
            System.out.println("deploy props = " + deployProps);

            File deployFile = new File(deployProps);
            if (deployFile.exists())
            {
                FileInputStream is = new FileInputStream(deployProps);
                Properties props = new Properties();
                props.load(is);

                Iterator it = props.entrySet().iterator();
                while (it.hasNext())
                {
                    Entry entry = (Entry)it.next();
                    //if (entry.getValue() != null && ((String)entry.getValue()).length() > 0)
                    this.setProperty((String)entry.getKey(), (String)entry.getValue());
                    System.out.println("setting key/value: " + entry.getKey() + ":" + entry.getValue());                        
                }
            }
            else
            {
                String msg = "Failed to find Deploy properties: " + deployProps;
                System.err.println(msg);
            }

            File torqueFile = new File(torqueProps);
            if (torqueFile.exists())
            {
                this.setProperty("component.torque.config", torqueProps);
                
                FileInputStream tis = new FileInputStream(torqueProps);
                Properties tprops = new Properties();
                tprops.load(tis);
                
                System.out.println("Connecting to: "+tprops.getProperty("database.default.url"));
                System.out.println("Database Username: "+tprops.getProperty("database.default.username"));
            }
        }
        catch (IOException e)
        {
            StringBuffer msg = new StringBuffer("Error reading properties for appName: ");
            msg.append(appName);
            msg.append(", props Dir: " + propsDir);
            System.err.println("Exception in loading properties: " + propsDir);
            e.printStackTrace();
        }
    }


    protected String makeFileNamePath(String propsDir, String fileName)
    {
        StringBuffer name = new StringBuffer(propsDir);
    
        if (!propsDir.endsWith(File.separator))
        {
            name.append(File.separator);
        }
        name.append(fileName);
        return name.toString();
    }

}
