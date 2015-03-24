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

import org.apache.turbine.services.resources.TurbineResources;

/**
 * This class defines the Jetspeed properties keys.
 * All properties can be retrieved using TurbineResources or JetspeedResources
 * directly.
 *
 * This class also overrides the covers for many of the get routines.  It handles the cases
 * where we want a single value, perhaps as a boolean or float, but the configuration may have
 * an array of values.  In these cases, we let the first value override all the others and use it.
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Rapha謖 Luta</a>
 * @author <a href="mailto:tom@PIsoftware.com">Tom Adams</a>
 * @author <a href="mailto:kimptoc_mail@yahoo.com">Chris Kimpton</a>
 * @author <a href="mailto:ggolden@apache.org">Glenn R. Golden</a>
 * @version $Id: JetspeedResources.java,v 1.19 2004/02/23 03:29:53 jford Exp $
 */
public class JetspeedResources extends TurbineResources {

    //Added by sbelt for GenericNavigation.class
    public static final String SITE_HEADER_LOGO_KEY = "site.header.logo";
    public static final String SITE_HEADER_WELCOME_KEY = "site.header.welcome";
    public static final String SITE_FOOTER_LOGO_KEY = "site.footer.logo";

    //Keys for use within external code
    
    public static final String PROFILER_DEFAULT_CLASSNAME_KEY = "profiler.default.classname";
    public static final String PROFILER_CONFIGURATION_KEY = "profiler.configuration";
    public static final String SKIN_DEFAULT_KEY = "skin.default";
    public static final String PORTLETCONTROL_DEFAULT_CLASSNAME_KEY = "portletcontrol.default.classname";
    public static final String PORTLETCONTROL_MAXIMIZED_CLASSNAME_KEY = "portletcontrol.maximized.classname";
    public static final String PORTLETCONTROLLER_DEFAULT_CLASSNAME_KEY = "portletcontroller.default.classname";

    //Disk Cache (URLManager) parameters
    public static final String CACHE_DIRECTORY_KEY = "cache.directory";
    public static final String CACHE_REQUIRE_CACHED_KEY = "cache.require.cached";
    public static final String DEFAULT_DCE_EXPIRATION_TIME_KEY = "cache.default.expiration";

    public static final String DEFAULTPORTLETCONTROL_WIDTH_KEY = "defaultportletcontrol.width";
    public static final String DEFAULTPORTLETCONTROLLER_NUMCOLUMNS_KEY = "defaultportletcontroller.numcolumns";
    public static final String DEFAULTPORTLETCONTROLLER_WIDTH_KEY = "defaultportletcontroller.width";
    public static final String XMLPORTLETCONTROLLER_WIDTH_KEY = "xmlportletcontroller.width";
    public static final String XMLPORTLETCONTROLLER_STYLESHEET_KEY = "xmlportletcontroller.stylesheet";
    public static final String PORTLETMARKUP_ALL_KEY = "portletmarkup.all";
    public static final String CONTENTFEEDS_STYLESHEET_URL_KEY = "contentfeeds.stylesheet.url";
    public static final String CONTENTFEEDS_FETCHALL_KEY = "contentfeeds.fetchall";
    public static final String CONTENTFEEDS_PORTLETBROWSER_PREVIEW_MAXPORTLETS_KEY = "contentfeeds.portletbrowser.preview.maxportlets";
    public static final String CONTENTFEEDS_PORTLETBROWSER_OVERVIEW_MAXPORTLETS_KEY = "contentfeeds.portletbrowser.overview.maxportlets";
    public static final String CONFIGURATION_DIRECTORY_KEY = "configuration.directory";
    public static final String CONTENT_ROOT_URL_KEY = "content.root.url";
    public static final String TEMP_DIRECTORY_KEY = "temp.directory";
    public static final String ADMIN_PORTLET_NAME_KEY = "admin.portlet.name";
    public static final String ADMIN_USERNAME_KEY = "admin.username";
    public static final String ADMIN_PASSWORD_KEY = "admin.password";
    public static final String DAEMON_ENTRY = "daemon.entry";
    public static final String PSML_REGISTRY_URL_KEY = "psml.registry.url";
    public static final String NAVIGATIONS_TOP_KEY = "navigations.top";
    public static final String NAVIGATIONS_BOTTOM_KEY = "navigations.bottom";
    public static final String AUTOCREATE_PORTLETS_KEY = "autocreate.portlets";
    public static final String CONTENT_PROVIDER_LIST_KEY = "content.provider.list";
    public static final String DEFAULT_TITLE_KEY = "metadata.default.title";
    public static final String DEFAULT_DESCRIPTION_KEY = "metadata.default.description";
    public static final String DEFAULT_IMAGE_KEY = "metadata.default.image";
    public static final String JETSPEED_CONFIG_KEY = "jetspeed.config";

    public static final String CONTENT_ENCODING_KEY = "content.defaultencoding";
        
    public static final String CUSTOMIZATION_RESOURCE_KEY = "customization.psml";
    public static final String CUSTOMIZATION_MAX_NUMBER   = "customization.display.maxNumberOfPortlets";

    // glucode customizer
    public static final String HTML_HEX_COLOR_KEY = "html.hex.color.";
    public static final String HTML_COLOR_NAME_KEY = "html.color.name.";
    public static final String MANDATORY_PORTLET = "portlet.mandatory";

    public static final String CHANGE_PASSWORD_TEMPLATE = "template.change.password";

    // Images for the portal
    
    public static final String MAX_IMAGE = getString( CONTENT_ROOT_URL_KEY, "" ) + "images/max.gif";
    public static final String INFO_IMAGE = getString( CONTENT_ROOT_URL_KEY, "" ) + "images/info.gif";
    public static final String EDIT_IMAGE = getString( CONTENT_ROOT_URL_KEY, "" ) + "images/edit.gif";
    public static final String TRANSPARENT_IMAGE = getString( CONTENT_ROOT_URL_KEY, "" ) + "images/dot.gif";
    public static final String RESTORE_IMAGE = getString( CONTENT_ROOT_URL_KEY, "" ) + "images/restore.gif";
    public static final String SITE_HEADER_LOGO = getString( CONTENT_ROOT_URL_KEY, "" ) + getString( SITE_HEADER_LOGO_KEY, "images/jetspeed-logo.gif" );
    public static final String SITE_FOOTER_LOGO = getString( CONTENT_ROOT_URL_KEY, "" ) + getString( SITE_FOOTER_LOGO_KEY, "images/feather.gif");
    public static final String SITE_WELCOME = getString( SITE_HEADER_WELCOME_KEY, "Welcome to Jetspeed" );
    public static final String SITE_STYLESHEET = getString( CONTENT_ROOT_URL_KEY, "" ) + getString( "site.stylesheet" );

    // this is the value that is stored in the database for confirmed users
    public static final String CONFIRM_VALUE = "CONFIRMED";
    public static final String CONFIRM_VALUE_PENDING = "PENDING";
    public static final String CONFIRM_VALUE_REJECTED = "REJECTED";

    // parameter names for the Jetspeed path framework elements    
    public static final String PATH_ACTION_KEY = "action";
    public static final String PATH_PANEID_KEY = "js_pane";        
    public static final String PATH_PANENAME_KEY = "js_panename";
    public static final String PATH_PORTLETID_KEY = "js_peid";
    public static final String PATH_SCREEN_KEY = "screen";
    public static final String PATH_TEMPLATE_KEY = "template";
    public static final String PATH_PORTLET_KEY = "portlet";
    public static final String PATH_SUBPANE_SEPARATOR = ",";

    // product information
    public static final String JETSPEED_NAME_KEY = "jetspeed.name";
    public static final String JETSPEED_VERSION_KEY = "jetspeed.version";
    
    /**
     * Special portlet registry parameter used to override default portlet cache expiration value
     */
    public static final String TIME_TO_LIVE = "_TimeToLive";

    /**
     * @depracated Use PATH_PANEID_KEY or PATH_PANENAME_KEY
     */
    public static final String PATH_PANEL_KEY = "select-panel";        

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a boolean value.
     *
     * @param name The resource name.
     * @return The value of the named resource as a boolean.
     */
    public static boolean getBoolean(String name)
    {
        try
        {
            return TurbineResources.getBoolean (name);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return new Boolean(values[0]).booleanValue();

            // otherwise, just throw the exception
            throw e;
        }
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
    public static boolean getBoolean(String name,
                                     boolean def)
    {
        try
        {
            return TurbineResources.getBoolean(name, def);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return new Boolean(values[0]).booleanValue();

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double.
     *
     * @param name The resoource name.
     * @return The value of the named resource as double.
     */
    public static double getDouble(String name)
    {
        try
        {
            return TurbineResources.getDouble(name);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return Double.parseDouble(values[0]);

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a double, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the named resource as a double.
     */
    public static double getDouble(String name,
                                   double def)
    {
        try
        {
            return TurbineResources.getDouble(name, def);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return Double.parseDouble(values[0]);

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float.
     *
     * @param name The resource name.
     * @return The value of the resource as a float.
     */
    public static float getFloat(String name)
    {
        try
        {
            return TurbineResources.getFloat(name);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return Float.parseFloat(values[0]);

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a float, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a float.
     */
    public static float getFloat(String name,
                                 float def)
    {
        try
        {
            return TurbineResources.getFloat(name, def);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return Float.parseFloat(values[0]);

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer.
     *
     * @param name The resource name.
     * @return The value of the resource as an integer.
     */
    public static int getInt(String name)
    {
        try
        {
            return TurbineResources.getInt(name);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return Integer.parseInt(values[0]);

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as an integer, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as an integer.
     */
    public static int getInt(String name,
                             int def)
    {
        try
        {
            return TurbineResources.getInt(name, def);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return Integer.parseInt(values[0]);

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long.
     *
     * @param name The resource name.
     * @return The value of the resource as a long.
     */
    public static long getLong(String name)
    {
        try
        {
            return TurbineResources.getLong(name);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return Long.parseLong(values[0]);

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a long, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a long.
     */
    public static long getLong(String name,
                               long def)
    {
        try
        {
            return TurbineResources.getLong(name, def);
        }
        catch (ClassCastException e)
        {
            // get the possible list
            String[] values = getStringArray(name);
            
            // try again with the first
            if ((values != null) && (values.length > 0))
                return Long.parseLong(values[0]);

            // otherwise, just throw the exception
            throw e;
        }
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string.
     *
     * @param name The resource name.
     * @return The value of the resource as a string.
     */
    public static String getString(String name)
    {
        // get the possible list
        String[] values = getStringArray(name);
        if ((values != null) && (values.length > 0))
            return values[0];

        return TurbineResources.getString(name);
    }

    /**
     * The purpose of this method is to get the configuration resource
     * with the given name as a string, or a default value.
     *
     * @param name The resource name.
     * @param def The default value of the resource.
     * @return The value of the resource as a string.
     */
    public static String getString(String name,
                                   String def)
    {
        // get the possible list
        String[] values = getStringArray(name);
        if ((values != null) && (values.length > 0))
            return values[0];

        return TurbineResources.getString(name, def);
    }
}

