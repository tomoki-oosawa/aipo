package org.apache.jetspeed.services.template;

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



// Java Core Classes
import java.util.ArrayList;
import java.util.Properties;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.io.File;
import javax.servlet.ServletConfig;

// Turbine Utility Classes
import org.apache.turbine.util.ServletUtils;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.modules.NavigationLoader;

// Jetspeed classes
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;

/**
 * <p>This service extends the TurbineTemplateService to modify its behaviour:
 * Not only layout and screen packages, but also the screen templates
 * are searched in the template neames filepath, so that a fallback
 * strategy is provided, that can be used for multi-language, multi-device 
 * and browser-specific support support.</p>
 * <p>E.g: a template name "/html/en/US/IE/mytemplate" would search for 
 * following files (in the given order):
 * <ol>
 * <li>. /html/en/US/IE/mytemplate</li>
 * <li>. /html/en/US/mytemplate</li>
 * <li>. /html/en/mytemplate</li>
 * <li>. /html/mytemplate</li>
 * <li>. /mytemplate</li>
 * </ol>
 * </p>
 * <p>
 * TurbineTemplateService part:
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * JetspeedTemplateService part:
 * @author <a href="mailto:ingo@apache.org">Ingo Schuster</a>
 * @version $Id: JetspeedTemplateService.java,v 1.11 2004/02/23 03:38:54 jford Exp $
 */
public class JetspeedTemplateService
    extends TurbineBaseService
  //   implements TemplateService
  // removed dst: 2001/06/03, TDK 2.2 integration
{
    /**
     * Static initialization of the logger for this class
     */    
    private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(JetspeedTemplateService.class.getName());
    
    /** The hashtable used to cache Screen names. */
    private Hashtable screenCache = null;

    /** The hashtable used to cache screen template names. */
    private Hashtable templateCache = null;

    /** The hashtable used to cache Navigation names. */
    private Hashtable navCache = null;

    /** The hashtable used to cache layout template names. */
    private Hashtable layoutCache = null;

    /** Flag set if cache is to be used. */
    private boolean useCache = false;

    /** Default extension. */
    private String extension;

    /** Default layout template. */
    private String defaultLayoutTemplate;

    /** Default Navigation module. */
    private String defaultNavigation;

    /** Default Screen module. */
    private String defaultScreen;

    /**
     * The absolute paths where the appropriate template engine will
     * be searching for templates.
     */
    private String[] templateRoot = null;

    /**
     * Called the first time the Service is used.
     *
     * @param config A ServletConfig.
     */
    public void init(ServletConfig config)
        throws InitializationException
    {
        try
        {
            initTemplate(config);
            setInit(true);
            logger.info ("TemplateService init()....finished!");
        }
        catch (Exception e)
        {
            logger.error( "TurbineTemplateService failed to initialize", e );
            throw new InitializationException("TurbineTemplateService failed to initialize", e);
        }
    }

    /**
     * TODO: Document this class.
     *
     * @param config A ServletConfig.
     * @exception Exception, a generic exception.
     */
    private void initTemplate(ServletConfig config)
        throws Exception
    {
        useCache = TurbineResources.getBoolean("modules.cache", true);
        Properties props = getProperties();

        if (useCache)
        {
            int layoutSize = Integer
                .parseInt(props.getProperty("layout.cache.size", "5"));
            int navigationSize = Integer
                .parseInt(props.getProperty("navigation.cache.size", "10"));
            int screenSize = Integer
                .parseInt(props.getProperty("screen.cache.size", "5"));
            int templateSize = Integer
                .parseInt(props.getProperty("screen.cache.size", "50"));
            layoutCache = new Hashtable( (int)(1.25*layoutSize) + 1);
            navCache = new Hashtable( (int)(1.25*navigationSize) + 1);
            screenCache = new Hashtable( (int)(1.25*screenSize) + 1);
            templateCache = new Hashtable( (int)(1.25*templateSize) + 1);
        }
        // relative to the webapp root directory
        String templatePaths = props
            .getProperty("template.path", "/templates");

        // If possible, transform paths to be webapp root relative.
        templatePaths = ServletUtils.expandRelative(config,
                                                     templatePaths);

        // store the converted paths in service properties for 
        // Turbine based providers 
        props.put("template.path", templatePaths);

        // tokenize the template.path property and assign to an array
        String pathSep = System.getProperty("path.separator");
        StringTokenizer st = new StringTokenizer(templatePaths,pathSep);
        templateRoot = new String[st.countTokens()];
        int pos = 0;
        while(st.hasMoreTokens())
        {
            templateRoot[pos++] = st.nextToken();
        }

        // the extension that is added to layout templates (e.g.)
        extension = props.getProperty("default.extension", "html");              

        // the default modules 
        defaultNavigation = props
            .getProperty("default.navigation", "TemplateNavigation");
        defaultScreen = props.getProperty("default.screen", "TemplateScreen");  
        
        // the default layout template
        defaultLayoutTemplate = props
            .getProperty("default.layout.template", "/default." + extension);
        
        if (defaultLayoutTemplate.indexOf('.') == -1)
        {
            defaultLayoutTemplate = defaultLayoutTemplate + "." + extension;
        }
    }

    /**
     *  Adds the object into the hashtable.
     *
     * @param key The String key for the object.
     * @param value The Object.
     * @param h The Hashtable.
     */
    private void addToCache ( String key,
                              Object value,
                              Hashtable h )
    {
        if (useCache && value != null)
        {
            h.put(key, value);
        }
    }

    /**
     * Get the Screen template given in the properties file.
     *
     * @return A String which is the value of the TemplateService 
     * default.screen property.
     */
    public String getDefaultScreen()
    {
        return defaultScreen;
    }
    
    /**
     * Get the default Navigation given in the properties file.
     *
     * @return A String which is the value of the TemplateService 
     * default.navigation property.
     */
    public String getDefaultNavigation()
    {
        return defaultNavigation;
    }

    /**
     * Get the default layout template given in the properties file.
     *
     * @return A String which is the value of the TemplateService 
     * default.layout.template property.
     */
    public String getDefaultLayoutTemplate()
    {
        return defaultLayoutTemplate;
    }

    
    /**
     * Locate and return the name of a screen template.
     *
     *
     * @param name A String which is the key to the template.
     * @return A String with the screen template path.
     * @exception Exception, a generic exception.
     */
    public String getScreenTemplateName(String key)
        throws Exception
    {
        if (name==null)
            throw new Exception ("TurbineTemplateService: " + 
                "getLayoutTemplateName() was passed in a null value.");

        String name = null;

        if (  useCache && templateCache.containsKey(key) )
        {
            name = (String)templateCache.get(key);
        }
        else
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug("JetspeedTemplatePage.getLayoutTemplateName(" + key + ")");
            }
            String[] names = parseScreenTemplate(key);
            name = names[2];
            addToCache( key, names[0], screenCache );
            addToCache( key, names[1], layoutCache );
            addToCache( key, names[2], templateCache );
        }
        return name;
    }
    /**
     * Locate and return the name of a layout template.
     *
     *
     * @param name A String with the name of the template.
     * @return A String with the layout template path.
     * @exception Exception, a generic exception.
     */
    public String getLayoutTemplateName(String name)
        throws Exception
    {
        if (name==null)
            throw new Exception ("TurbineTemplateService: " + 
                "getLayoutTemplateName() was passed in a null value.");

        String layoutName = null;

        if (  useCache && layoutCache.containsKey(name) )
        {
            layoutName = (String)layoutCache.get(name);
        }
        else
        {
            String[] names = parseScreenTemplate(name);
            layoutName = names[1];
            addToCache( name, names[0], screenCache );
            addToCache( name, names[1], layoutCache );
            addToCache( name, names[2], templateCache );
        }
        return layoutName;
    }

    /**
     * Locate and return the name of a Navigation module.
     *
     * @param name A String with the name of the template.
     * @return A String with the name of the navigation.
     * @exception Exception, a generic exception.
     */
    public String getNavigationName(String name)
        throws Exception
    {
        if (name==null)
            throw new Exception ("TurbineTemplateService: " + 
                "getNavigationName() was passed in a null value.");

        String nav_name = null;

        if (  useCache && navCache.containsKey(name) )
        {
            nav_name = (String)navCache.get(name);
        }
        else
        {
            nav_name = parseNavigationTemplate(name);
            addToCache( name, nav_name, navCache );
        }
        return nav_name;
    }

    /**
     * Locate and return the name of a Screen module.
     *
     * @param name A String with the name of the template.
     * @return A String with the name of the screen.
     * @exception Exception, a generic exception.
     */
    public String getScreenName(String name)
        throws Exception
    {

        if (name==null)
            throw new Exception ("TurbineTemplateService: " + 
                "getScreenName() was passed in a null value.");

        String screenName = null;

        if (  useCache && screenCache.containsKey(name) )
        {
            screenName = (String)screenCache.get(name);
        }
        else
        {
            String[] names = parseScreenTemplate(name);
            screenName = names[0];
            addToCache( name, names[0], screenCache );
            addToCache( name, names[1], layoutCache );
            addToCache( name, names[2], templateCache );
        }
        return screenName;
    }

    /**
     * Get the default extension given in the properties file.
     *
     * @return A String with the extension.
     */
    public String getDefaultExtension()
    {
        return extension;
    }

    /**
     * This method takes the template parameter and parses it, so that
     * relevant Screen/Layout-template information can be extracted.
     *
     * @param template A String with the template name.
     * @return A String[] where the first element is the Screen name
     *         and the second element is the layout template.
     */
    protected String[] parseScreenTemplate( String template ) throws Exception
    {
        // check if an extension was included.  if not, add the default
        if ( template.indexOf('.') == -1 )
        {
            template = template + "." + getDefaultExtension(); 
        }

        if ( logger.isDebugEnabled() )
        {
            logger.debug("JetspeedTemplateService.parseScreen: template = " + template);
        }
        
        StringTokenizer st = new StringTokenizer(template, "/");
        List tokens = new ArrayList(st.countTokens());
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (!token.equals(""))
            {
                tokens.add(token);
            }
        }
        if ( logger.isDebugEnabled() )
        {
            logger.debug("JetspeedTemplateService.parseScreen: tokens1: " + tokens);
        }
        String fileName = (String)tokens.get(tokens.size() - 1);
        tokens.remove(tokens.size()-1);
        int dot = fileName.lastIndexOf('.');
        String className = null;
        if (dot>0)
        {
            className = fileName.substring(0, dot);
        }
        else
        {
            className = fileName;
        }
        String firstChar = String.valueOf(className.charAt(0));
        firstChar = firstChar.toUpperCase();
        className = firstChar + className.substring(1);
        if ( logger.isDebugEnabled() )
        {
            logger.debug("JetspeedTemplateService.parseScreen: tokens2: " + tokens);
        }

        // make sure the template exists and determine the correct
        // templateRoot path
        String pathRoot = null;
        String allPaths = "";
        String pathSep = System.getProperty("path.separator");
        for (int i=0; i<templateRoot.length; i++)
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug("JetspeedTemplateService.parseScreen: templateRoot " + i + " " + templateRoot[i]);
            }

            String templatePath = null;
        
            for (int k=tokens.size(); k>=0;  k--)
            {
                StringBuffer path = new StringBuffer();
                for (int j=0; j<k; j++)
                {
                    path.append("/").append((String)tokens.get(j));
                }
                StringBuffer distinctPath = new StringBuffer(path.toString()).append("/").append(fileName);
                templatePath = distinctPath.toString();
                if ( logger.isDebugEnabled() )
                {
                    logger.debug("JetspeedTemplateService.parseScreen: Path: " + templatePath);
                }
         
                if (new File(templateRoot[i] + "/screens" + templatePath).exists())
                {
                    template = templatePath;
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug("JetspeedTemplateService.parseScreen: template found: " + template);
                    }
                    break;
                }
                templatePath = null;
            }
            if (templatePath != null) {
                pathRoot = templateRoot[i];
                if ( logger.isDebugEnabled() )
                {
                    logger.debug("JetspeedTemplateService.parseScreen: pathRoot: " + pathRoot);
                }
                break;
            }
            allPaths += pathSep + templateRoot[i];
        }
        if (pathRoot == null)
        {
            throw new Exception("The screen template: " +
                                template +
                                " does not exist in " +
                                allPaths.substring(pathSep.length()) +
                                ", so the TemplateService could not " +
                                "determine associated templates.");
        }

        /*
        String[] paths = new String[tokens.size() + 2];
        String[] pkgs = new String[tokens.size() + 2];
        int arrayIndex = 0;
        for (int i=tokens.size(); i>=0;  i--)
        {
            StringBuffer path = new StringBuffer();
            StringBuffer pkg = new StringBuffer();
            for (int j=0; j<i; j++)
            {
                path.append("/").append((String)tokens.get(j));
                pkg.append((String)tokens.get(j)).append('.');
            }
            if ( i == tokens.size() )
            {
                StringBuffer distinctPath = new StringBuffer(path.toString());
                StringBuffer distinctPkg = new StringBuffer(pkg.toString());
                paths[arrayIndex] = distinctPath.append('/').append(fileName).toString();
                pkgs[arrayIndex] = distinctPkg.append(className).toString();
                arrayIndex++;
            }
            paths[arrayIndex] = path.append(defaultLayoutTemplate).toString();
            pkgs[arrayIndex] = pkg.append("Default").toString();
            arrayIndex++;
        }
        */        

        String[] paths = new String[2 * tokens.size() +2];
        String[] pkgs  = new String[2 * tokens.size() +2];
        int arrayIndex = 0;
        for (int i=tokens.size(); i>=0;  i--)
        {
            StringBuffer path = new StringBuffer();
            StringBuffer pkg = new StringBuffer();
            for (int j=0; j<i; j++)
            {
                path.append("/").append((String)tokens.get(j));
                pkg.append((String)tokens.get(j)).append('.');
            }
            paths[arrayIndex] = path.append("/").append(fileName).toString();
            pkgs[arrayIndex]  = pkg.append("/").append(className).toString();
            arrayIndex++;
        }
        
        for (int i=tokens.size(); i>=0;  i--)
        {
            StringBuffer path = new StringBuffer();
            StringBuffer pkg = new StringBuffer();
            for (int j=0; j<i; j++)
            {
                path.append("/").append((String)tokens.get(j));
                pkg.append((String)tokens.get(j)).append('.');
            }
            paths[arrayIndex] = path.append(defaultLayoutTemplate).toString();
            pkgs[arrayIndex]  = pkg.append("Default").toString();
            arrayIndex++;
        }

        if ( logger.isDebugEnabled() )
        {
            for (int i=0; i<paths.length; i++)
            {
                logger.debug("JetspeedTemplateService.parseScreen: paths[" + i + "] = " + paths[i]);
            }
        }

        String[] holder = new String[3];
        holder[0] = getScreenName(pkgs);
        holder[1] = getLayoutTemplateName(pathRoot, paths);
        holder[2] = template;
        return holder;
    }

    /**
     * Parse the template name out to a package path to locate the
     * Navigation module.  This is different than the Screen/Layout
     * parser in that it only looks for packages.  Note: If caching is
     * enabled, this is only performed once for each unique template.
     *
     * @param String The template name (i.e folder/headernav.wm).
     * @return A String with the name of the Navigation module to use
     * for the template.
     */
    protected String parseNavigationTemplate( String template )
    {
        StringTokenizer st = new StringTokenizer(template, "/");
        List tokens = new ArrayList(st.countTokens());
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (!token.equals(""))
            {
                tokens.add(token);
            }
        }
        String fileName = (String)tokens.get(tokens.size() - 1);
        tokens.remove(tokens.size() - 1);
        int dot = fileName.lastIndexOf('.');
        String className = null;
        if (dot>0)
        {
            className = fileName.substring(0, dot);
        }
        else
        {
            className = fileName;
        }
        String firstChar = String.valueOf(className.charAt(0));
        firstChar = firstChar.toUpperCase();
        className = firstChar + className.substring(1);

        String[] pkgs = new String[tokens.size() + 2];
        int arrayIndex = 0;
        for (int i=tokens.size(); i>=0;  i--)
        {
            StringBuffer pkg = new StringBuffer();
            for (int j=0; j<i; j++)
            {
                pkg.append((String)tokens.get(j)).append('.');
            }
            if ( i == tokens.size() )
            {
                StringBuffer distinctPkg = new StringBuffer(pkg.toString());
                pkgs[arrayIndex] = distinctPkg.append(className).toString();
                arrayIndex++;
            }
            pkgs[arrayIndex] = pkg.append("Default").toString();
            arrayIndex++;
        }
        return getNavigationName( pkgs);
    }

    /**
     * Extract possible layouts paths.
     *
     * @param possiblePaths A String[] with possible paths to search.
     * @return A String with the name of the layout template.
     */
    private String getLayoutTemplateName(String pathRoot, String[] possiblePaths)
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug("JetspeedTemplatePage.getLayoutTemplateName: pathRoot " + pathRoot);
        
            for (int i=0; i<possiblePaths.length; i++)
            {
                logger.debug("JetspeedTemplatePage.getLayoutTemplateName: possiblePaths[" + i + "]=" + possiblePaths[i]);
            }
        }
        for (int i=0; i<possiblePaths.length; i++)
        {
            if (new File(pathRoot, "layouts" + possiblePaths[i]).exists())
            {
                if ( logger.isDebugEnabled() )
                {
                    logger.debug("JetspeedTemplatePage.getLayoutTemplateName: " + pathRoot + "/layouts" + possiblePaths[i] + " found.");
                }
                return possiblePaths[i];
            } 
            else 
            {
                if ( logger.isDebugEnabled() )
                {
                    logger.debug("JetspeedTemplatePage.getLayoutTemplateName: " + pathRoot + "/layouts" + possiblePaths[i] + " NOT found.");
                }
            }
        }
        return defaultLayoutTemplate;
    }

    /**
     * Extract a possible Screen from the packages.
     *
     * @param possibleScreens A String[] with possible paths to
     * search.
     * @return A String with the name of the Screen class to use.
     */
    private String getScreenName( String[] possibleScreens)
    {
        for (int i=0; i<possibleScreens.length; i++)
        {
            try
            {
                ScreenLoader.getInstance().getInstance(possibleScreens[i]);
                return possibleScreens[i];
            }
            catch (Exception e) 
            {
                logger.error( "Exception in getScreenName", e );
            }
        }
        return defaultScreen;
    }


    /**
     * Seaches for the Navigation class that may match the
     * name of the Navigation template.
     *
     * @param possibleNavigations A String[] with possible navigation
     * packages.
     * @return A String with the name of the Navigation class to use.
     */
    private String getNavigationName( String[] possibleNavigations)
    {
        for (int i=0; i<possibleNavigations.length; i++)
        {
            try
            {
                NavigationLoader.getInstance().getInstance(possibleNavigations[i]);
                return possibleNavigations[i];
            }
            catch (Exception e) 
            {
                logger.error( "Exception in getNavigationName", e );
            }
        }
        return defaultNavigation;
    }
}



