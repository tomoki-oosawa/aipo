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

package org.apache.jetspeed.services.webpage;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Properties;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/*
 * Mutable Configuration settings for the WebPage Service
 * Provides the singleton interface to the WebPage Service configuration state.
 * The state of the configuration is serialized to persistence store in standard
 * java <code>java.util.Parameter</code> format (name/value/pair)
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: Configuration.java,v 1.2 2004/02/23 03:46:26 jford Exp $ 
 */
public class Configuration
{  
    public static final String WPS_SERVLET = "/wps";

    public static final int STATUS_NOT_CONFIGURED = 0;
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_OFFLINE = -1;

    static Logger log = Logger.getLogger(Configuration.class);

    // Key and default value for SID Query Request parameter
    public static final String KEY_WPS_SID  = "wps.sid";
    public static final String WPS_SID = "wpsid";
    private String sid = WPS_SID; // cached value

    // Key and default value for PATH Query Request parameter
    public static final String KEY_WPS_PATH     = "wps.path";
    public static final String WPS_PATH    = "wpspath";
    private String path = WPS_PATH; // cached value

    // Key and default value for URL Query Request parameter
    public static final String KEY_WPS_URL  = "wps.url";
    public static final String WPS_URL = "wps.url";
    private String url = WPS_URL; // cached value

    // Key and default value for DEBUG setting
    public static final String KEY_WPS_DEBUG     = "wps.debug";    
    private boolean debug = false; // cached value

    // Key and default value for Parser setting
    public static final String KEY_PARSER     = "parser.default";
    public static final String PARSER_SWING     = "swing";
    public static final String PARSER_OPENXML     = "openxml";
    private String parser = PARSER_SWING; // cached value

    // Key and default value for Content Log Enabling/Disabling
    public static final String KEY_LOG_ENABLE     = "log.content.enable";
    private boolean enableContentLog = false;

    // Key and default value for ContentLog Location
    public static final String KEY_LOG_LOCATION        = "log.content.location";
    public static final String WPS_LOG_LOCATION     = "/WEB-INF/logs/wps-content.log";
    private String logLocation = WPS_LOG_LOCATION; // cached value

    // Key and default value for Content Log Reset on Startup
    public static final String KEY_LOG_RESET     = "log.content.reset";
    private boolean resetContentLog = false;
    
    // Key and default value for Webapp name
    public static final String KEY_WEBAPP         = "wps.webapp.name";
    public static final String WPS_WEBAPP         = "/jetspeed";
    private String webapp = WPS_WEBAPP; // cached value

    // Key and default value for Login Path web application
    public static final String KEY_LOGIN   = "ne.webapp.login";
    public static final String WPS_LOGIN = "/jetspeed/loginController.php";
    private String login = WPS_LOGIN; // cached value

    // Key and default value for Logout Path web application
    public static final String KEY_LOGOUT   = "ne.webapp.logout";
    public static final String WPS_LOGOUT = "/jetspeed/logoutController.php";
    private String logout = WPS_LOGOUT; // cached value

    // Key and default value for User Session Key
    public static final String KEY_USER_SESSION    = "user.session.key";
    public static final String WPS_USER_SESSION = "turbine.user";
    private String userSessionKey = WPS_USER_SESSION; // cached value

    // Key and default value for Default User
    public static final String KEY_USER_DEFAULT    = "user.default";
    public static final String WPS_USER_DEFAULT = "joe";
    private String userDefault = WPS_USER_DEFAULT; // cached value

    // Key and default value for Web Interface User Parameter
    public static final String KEY_PARAM_USER = "ne.webapp.param.username";
    public static final String WPS_PARAM_USER = "da_username";
    private String paramUser = WPS_PARAM_USER; // cached value

    // Key and default value for Web Interface Password Parameter
    public static final String KEY_PARAM_PASSWORD = "webapp.param.password";
    public static final String WPS_PARAM_PASSWORD = "da_password";
    private String paramPassword = WPS_PARAM_PASSWORD; // cached value

    // Key and default value for Web Interface Permission Parameter
    public static final String KEY_PARAM_PERMISSIONS = "webapp.param.permissions";
    public static final String WPS_PARAM_PERMISSIONS = "serialized_permissions";
    private String paramPermissions = WPS_PARAM_PERMISSIONS; // cached value

    // Key and default value for Web Interface Login Failure String
    public static final String KEY_LOGIN_FAILURE = "webapp.login.failure";
    public static final String WPS_LOGIN_FAILURE = "LOGIN FAIL";
    private String loginFailureString = WPS_LOGIN_FAILURE; // cached value
    
    // Key and default value for Web Interface Login Success String
    public static final String KEY_LOGIN_SUCCESS = "webapp.login.success";
    public static final String WPS_LOGIN_SUCCESS = "LOGIN SUCCESS";
    private String loginSuccessString = WPS_LOGIN_SUCCESS; // cached value
    

    // keys that don't need to be cached for performance
    public static final String KEY_CONTENT_INFO = "content.info";
    public static final String KEY_CONTENT_ERROR = "content.error";

    // constants
    public static final String TRUE_VALUE     = "true";
    public static final String FALSE_VALUE      = "false";

    // singleton instance
    private static Configuration instance = null;

    // the properties are contained in memory here
    private Properties properties;

    // path to the properties NVP (name/value/pair) file
    private String ppath;

    // text placed into the first line of the configuration file.
    public static final String HEADER = "Jetspeed Web Page Service Configuration Properties";


    /*
     * Singleton instance factory.     
     *
     * @return the Configuration singleton
     *
     */
    public static Configuration getInstance() 
    {
        return instance;
    }

    /*
     * Singleton instance factory. Call this to get the initial instance, loaded
     * the state from persistent store.
     *
     * @return the Configuration singleton
     * @throws ServletException
     */
    public static Configuration getInitialInstance(String ppath) throws ServletException
    {
        if (instance == null)
        {    
            Properties properties = new Properties();
    
            try
            {
                properties.load(new FileInputStream(ppath));        
                instance = new Configuration(properties, ppath);
                instance.refresh();
            }
            catch (Exception e)
            {
                log.debug(e.toString());            
                instance = null;
            }
        }
        return instance;
    }

    /*
     * Private constructor for singleton configuration.
     *
     * @param     properties    the properties collection for this configuration
     * @param     path         the path to the properties file
     *
     */
    private Configuration(Properties properties, String ppath)
    {
        this.properties = properties;
        this.ppath = ppath;
    }

    /*
     * Used to obtain a configuration property.
     *
     * @param    name - the property name
     * @return    the property value or <code>null</code> if the property isn't found
     *
     */
    public String getProperty(String name)
    {
        return properties.getProperty(name);
    }

    /*
     * Inserts a new property or updates an existing one.
     * @param    name - the property name
     * @param    value - the property value
     *
     */
    public void setProperty(String name, String value) 
    {                            
        properties.setProperty(name, value);
        OutputStream out;

        try
        {
            out = new FileOutputStream(ppath);
            properties.store(out, HEADER);
            out.close();
        }
        catch (FileNotFoundException e)
        {
            String error = "Unable to update configuration file " + ppath + e.toString();
            log.debug(error);
        }
        catch (SecurityException e)
        {
            String error = "Unable to update configuration file " + ppath + e.toString();
            log.debug(error);
        }
        catch (IOException e)
        {
            String error = "Error updating configuration file " + ppath + e.toString();
            log.debug(error);
        }
        out = null;
    }

    /*
     * Refresh the configuration internal state from the properties persistent store.
     *
     */
    public void refresh()
    {
        String result = getInstance().getProperty(Configuration.KEY_WPS_DEBUG);
        if (null == result) 
        {
            debug = false;
        }
        else
        {
            debug = (result.equalsIgnoreCase(TRUE_VALUE));
        }

        sid = getInstance().getProperty(Configuration.KEY_WPS_SID);
        if (null == sid) 
        {
            sid = WPS_SID;
        }

        path = getInstance().getProperty(Configuration.KEY_WPS_PATH);
        if (null == path) 
            path = WPS_PATH;

        url = getInstance().getProperty(Configuration.KEY_WPS_URL);
        if (null == url) 
            url = WPS_URL;

        parser = getInstance().getProperty(Configuration.KEY_PARSER);
        if (null == parser) 
            parser = PARSER_SWING;

        result = getInstance().getProperty(Configuration.KEY_LOG_ENABLE);
        if (null == result) 
            enableContentLog = false;
        else
            enableContentLog = (result.equalsIgnoreCase(TRUE_VALUE));
    
        logLocation = getInstance().getProperty(Configuration.KEY_LOG_LOCATION);
        if (null == logLocation) 
            logLocation = WPS_LOG_LOCATION;
    
        result = getInstance().getProperty(Configuration.KEY_LOG_RESET);
        if (null == result) 
            resetContentLog = false;
        else
            resetContentLog = (result.equalsIgnoreCase(TRUE_VALUE));
    
        webapp = getInstance().getProperty(Configuration.KEY_WEBAPP);
        if (null == webapp) 
            webapp = WPS_WEBAPP;
    
        login = getInstance().getProperty(Configuration.KEY_LOGIN);
        if (null == login) 
             login = WPS_LOGIN;
    
        logout = getInstance().getProperty(Configuration.KEY_LOGOUT);
        if (null == logout) 
             login = WPS_LOGOUT;
    
        userSessionKey = getInstance().getProperty(Configuration.KEY_USER_SESSION);
        if (null == userSessionKey) 
             userSessionKey = WPS_USER_SESSION;

        userDefault = getInstance().getProperty(Configuration.KEY_USER_DEFAULT);
        if (null == userDefault) 
            userDefault = WPS_USER_DEFAULT;

        paramUser = getInstance().getProperty(Configuration.KEY_PARAM_USER);
        if (null == paramUser) 
            paramUser = WPS_PARAM_USER;

        paramPassword = getInstance().getProperty(Configuration.KEY_PARAM_PASSWORD);
        if (null == paramPassword) 
            paramPassword = WPS_PARAM_PASSWORD;

        paramPermissions = getInstance().getProperty(Configuration.KEY_PARAM_PERMISSIONS);
        if (null == paramPermissions) 
            paramPermissions = WPS_PARAM_PERMISSIONS;

        loginFailureString = getInstance().getProperty(Configuration.KEY_LOGIN_FAILURE);
        if (null == loginFailureString) 
            loginFailureString = WPS_LOGIN_FAILURE;

        loginSuccessString = getInstance().getProperty(Configuration.KEY_LOGIN_SUCCESS);
        if (null == loginSuccessString) 
            loginSuccessString = WPS_LOGIN_SUCCESS;

    }

    /*
     * Static accessors for convenient access to configuration state.
     *
     */
    public boolean getDebug()
    {
        return debug;
    }
    
    public String getSID()
    {
        return sid;
    }

    public String getPath()
    {
        return path;
    }

    public String getURL()
    {
        return url;
    }

    public String getParser()
    {
        return parser;
    }

    public boolean getEnableContentLog()
    {
        return enableContentLog;
    }

    public String getLogLocation()
    {
        return logLocation;
    }
    
    public boolean getResetContentLog()
    {
        return resetContentLog;
    }

    public String getWebapp()
    {
         return webapp;
    }

    public String getLogin()
    {
        return login;
    }

    public String getLogout()
    {
        return logout;
    }

    public String getUserSessionKey()
    {
        return userSessionKey;
    }

    public String getDefaultUser()
    {
        return userDefault;
    }

    public String getParamUser()
    {
        return paramUser;
    }

    public String getParamPassword()
    {
        return paramPassword;
    }

    public String getParamPermissions()
    {
        return paramPermissions;
    }

    public String getLoginFailureString()
    {
        return loginFailureString;
    }

    public String getLoginSuccessString()
    {
        return loginSuccessString;
    }

    /*
     * Create a go-between String using the format expected by the WPS.
     *
     * Example:
     *
     *  http://<ProxyHostAddess>/jetProxy?jaid=1.Element?japath=/somestuff/Controller.php
     *
     * @param proxyHost The base URL of the proxy server's host, i.e (http://localhost).
     * @param neid The unique network element id.
     * @param resource The resource to be proxied. This can be a combination of the relative
     *        path + the resource, or just solely the resource.
     * @param relativePath The relative path to the resource. Necessary for request-relative
     *        resources. Specify as null to disable this parameter.
     */
    public static String createProxyString(String proxyHost, 
                                           String neid,
                                           String resource,
                                           String relativePath)
    {
        String base = WebPageHelper.concatURLs(proxyHost, WPS_SERVLET);
        StringBuffer buffer = new StringBuffer(base);                    
        //
        // build the request string in proxy server expected format
        //
        // Example:
        //
        // http://<ProxyHostAddess>/jetProxy?jaid=1.Element?japath=/somestuff/Controller.php
        //
        buffer.append("?");
        buffer.append(getInstance().getSID());
        buffer.append("=");
        buffer.append(neid);  
        buffer.append("&");
        buffer.append(getInstance().getPath());
        buffer.append("=");

        //
        // Is it a request-relative or webapp-relative resource?
        // When the target resource starts with "/", then it is webapp-relative
        // otherwise it is request-relative. When request relative, use the
        // path from the original request to find the resource, otherwise take
        // the resource as is.
        //
        if (null != relativePath && !resource.startsWith("/"))
        {
            // its request-relative, use path from request
            buffer.append(relativePath);          
        }

        buffer.append(resource.replace('&', '@'));
        String proxiedPath = buffer.toString();                
        return proxiedPath;            
    }
}
